package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.R;
import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ReqRequireDoc;
import com.platform.ems.domain.ReqRequireDocAttachment;
import com.platform.ems.domain.ReqRequireDocItem;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IReqRequireDocService;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 需求单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-02
 */
@Service
@SuppressWarnings("all")
public class ReqRequireDocServiceImpl extends ServiceImpl<ReqRequireDocMapper, ReqRequireDoc> implements IReqRequireDocService {
    @Autowired
    private ReqRequireDocMapper reqRequireDocMapper;

    @Autowired
    private BasMaterialMapper basMaterialMapper;

    @Autowired
    private ReqRequireDocItemMapper reqRequireDocItemMapper;

    @Autowired
    private ReqRequireDocAttachmentMapper reqRequireDocAttachmentMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    private static final String TITLE = "需求单";

    /**
     * 查询需求单
     *
     * @param requireDocSid 需求单ID
     * @return 需求单
     */
    @Override
    public ReqRequireDoc selectReqRequireDocById(Long requireDocSid) {
        ReqRequireDoc reqRequireDoc = reqRequireDocMapper.selectReqRequireDocById(requireDocSid);
        if (reqRequireDoc == null) {
            return null;
        }
        //需求单明细对象
        ReqRequireDocItem reqRequireDocItem = new ReqRequireDocItem();
        reqRequireDocItem.setRequireDocSid(requireDocSid);
        List<ReqRequireDocItem> reqRequireDocItemList = reqRequireDocItemMapper.selectReqRequireDocItemList(reqRequireDocItem);
        //需求单附件对象
        ReqRequireDocAttachment reqRequireDocAttachment = new ReqRequireDocAttachment();
        reqRequireDocAttachment.setRequireDocSid(requireDocSid);
        List<ReqRequireDocAttachment> reqRequireDocAttachmentList = reqRequireDocAttachmentMapper.selectReqRequireDocAttachmentList(reqRequireDocAttachment);

        reqRequireDoc.setReqRequireDocItemList(reqRequireDocItemList);
        reqRequireDoc.setReqRequireDocAttachmentList(reqRequireDocAttachmentList);
        MongodbUtil.find(reqRequireDoc);
        return reqRequireDoc;
    }

    /**
     * 查询需求单列表
     *
     * @param reqRequireDoc 需求单
     * @return 需求单
     */
    @Override
    public List<ReqRequireDoc> selectReqRequireDocList(ReqRequireDoc reqRequireDoc) {
        return reqRequireDocMapper.selectReqRequireDocList(reqRequireDoc);
    }

    /**
     * 新增需求单
     * 需要注意编码重复校验
     *
     * @param reqRequireDoc 需求单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertReqRequireDoc(ReqRequireDoc reqRequireDoc) {
        setConfirmInfo(reqRequireDoc);
        reqRequireDocMapper.insert(reqRequireDoc);
        //需求单明细对象
        List<ReqRequireDocItem> reqRequireDocItemList = reqRequireDoc.getReqRequireDocItemList();
        if (CollectionUtils.isNotEmpty(reqRequireDocItemList)) {
            addReqRequireDocItem(reqRequireDoc, reqRequireDocItemList);
        }
        //需求单附件对象
        List<ReqRequireDocAttachment> reqRequireDocAttachmentList = reqRequireDoc.getReqRequireDocAttachmentList();
        if (CollectionUtils.isNotEmpty(reqRequireDocAttachmentList)) {
            addReqRequireDocAttachment(reqRequireDoc, reqRequireDocAttachmentList);
        }
        ReqRequireDoc requireDoc = reqRequireDocMapper.selectReqRequireDocById(reqRequireDoc.getRequireDocSid());
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(reqRequireDoc.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsEms.TABLE_REQUIRE_DOC)
                    .setDocumentSid(reqRequireDoc.getRequireDocSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("需求单" + requireDoc.getRequireDocCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(requireDoc.getRequireDocCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        } else {
            //校验是否存在待办
            checkTodoExist(reqRequireDoc);
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbUtil.insertUserLog(reqRequireDoc.getRequireDocSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        return 1;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(ReqRequireDoc reqRequireDoc) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, reqRequireDoc.getRequireDocSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, reqRequireDoc.getRequireDocSid()));
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ReqRequireDoc o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 需求单明细对象
     */
    private void addReqRequireDocItem(ReqRequireDoc reqRequireDoc, List<ReqRequireDocItem> reqRequireDocItemList) {
        deleteItem(reqRequireDoc);
        reqRequireDocItemList.forEach(o -> {
            if (o.getBarcodeSid() == null) {
                throw new BaseException("编码为" + o.getMaterialCode() + "的商品未生成商品条码，请重新添加！");
            }
            o.setRequireDocSid(reqRequireDoc.getRequireDocSid());
            reqRequireDocItemMapper.insert(o);
        });
    }

    private void deleteItem(ReqRequireDoc reqRequireDoc) {
        reqRequireDocItemMapper.delete(
                new UpdateWrapper<ReqRequireDocItem>()
                        .lambda()
                        .eq(ReqRequireDocItem::getRequireDocSid, reqRequireDoc.getRequireDocSid())
        );
    }

    /**
     * 需求单附件对象
     */
    private void addReqRequireDocAttachment(ReqRequireDoc reqRequireDoc, List<ReqRequireDocAttachment> reqRequireDocAttachmentList) {
        deleteAttachment(reqRequireDoc);
        reqRequireDocAttachmentList.forEach(o -> {
            o.setRequireDocSid(reqRequireDoc.getRequireDocSid());
            reqRequireDocAttachmentMapper.insert(o);
        });
    }

    private void deleteAttachment(ReqRequireDoc reqRequireDoc) {
        reqRequireDocAttachmentMapper.delete(
                new UpdateWrapper<ReqRequireDocAttachment>()
                        .lambda()
                        .eq(ReqRequireDocAttachment::getRequireDocSid, reqRequireDoc.getRequireDocSid())
        );
    }

    /**
     * 修改需求单
     *
     * @param reqRequireDoc 需求单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateReqRequireDoc(ReqRequireDoc reqRequireDoc) {
        setConfirmInfo(reqRequireDoc);
        reqRequireDocMapper.updateAllById(reqRequireDoc);
        //需求单明细对象
        List<ReqRequireDocItem> reqRequireDocItemList = reqRequireDoc.getReqRequireDocItemList();
        if (CollectionUtils.isNotEmpty(reqRequireDocItemList)) {
            reqRequireDocItemList.stream().forEach(o -> {
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addReqRequireDocItem(reqRequireDoc, reqRequireDocItemList);
        } else {
            deleteItem(reqRequireDoc);
        }
        //需求单附件对象
        List<ReqRequireDocAttachment> reqRequireDocAttachmentList = reqRequireDoc.getReqRequireDocAttachmentList();
        if (CollectionUtils.isNotEmpty(reqRequireDocAttachmentList)) {
            reqRequireDocAttachmentList.stream().forEach(o -> {
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addReqRequireDocAttachment(reqRequireDoc, reqRequireDocAttachmentList);
        } else {
            deleteAttachment(reqRequireDoc);
        }
        if (!ConstantsEms.SAVA_STATUS.equals(reqRequireDoc.getHandleStatus())) {
            //校验是否存在待办
            checkTodoExist(reqRequireDoc);
        }
        ReqRequireDoc response = reqRequireDocMapper.selectReqRequireDocById(reqRequireDoc.getRequireDocSid());
        //插入日志
        MongodbUtil.insertUserLog(reqRequireDoc.getRequireDocSid(), BusinessType.UPDATE.getValue(), response, reqRequireDoc, TITLE);
        return 1;
    }

    /**
     * 批量删除需求单
     *
     * @param requireDocSids 需要删除的需求单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteReqRequireDocByIds(Long[] requireDocSids) {

        ReqRequireDoc params = new ReqRequireDoc();
        params.setRequireDocSids(requireDocSids);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = reqRequireDocMapper.countByDomain(params);
        if (count != requireDocSids.length) {
            throw new BaseException("仅保存状态才允许删除");
        }
        //删除需求单
        reqRequireDocMapper.deleteReqRequireDocByIds(requireDocSids);
        //删除需求单明细
        reqRequireDocItemMapper.deleteRequireDocItemByIds(requireDocSids);
        //删除需求单附件
        reqRequireDocAttachmentMapper.deleteRequireDocAttachmentByIds(requireDocSids);
        ReqRequireDoc reqRequireDoc = new ReqRequireDoc();
        for (Long requireDocSid : requireDocSids) {
            reqRequireDoc.setRequireDocSid(requireDocSid);
            //校验是否存在待办
            checkTodoExist(reqRequireDoc);
        }
        return requireDocSids.length;
    }

    /**
     * 需求单确认
     */
    @Override
    public int confirm(ReqRequireDoc reqRequireDoc) {
        //需求单sids
        Long[] requireDocSids = reqRequireDoc.getRequireDocSids();
        ReqRequireDoc params = new ReqRequireDoc();
        params.setRequireDocSids(requireDocSids);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = reqRequireDocMapper.countByDomain(params);
        if (count != requireDocSids.length) {
            throw new BaseException("仅保存状态才允许确认");
        }
        for (Long sid : requireDocSids) {
            reqRequireDoc.setRequireDocSid(sid);
            //校验是否存在待办
            checkTodoExist(reqRequireDoc);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(sid, BusinessType.CHECK.getValue(), msgList, TITLE);
        }
        reqRequireDoc.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        reqRequireDoc.setConfirmDate(new Date());
        return reqRequireDocMapper.confirm(reqRequireDoc);
    }

    /**
     * 需求单变更
     */
    @Override
    public int change(ReqRequireDoc reqRequireDoc) {
        Long requireDocSid = reqRequireDoc.getRequireDocSid();
        ReqRequireDoc requireDoc = reqRequireDocMapper.selectReqRequireDocById(requireDocSid);
        //验证是否确认状态
        if (!HandleStatus.CONFIRMED.getCode().equals(requireDoc.getHandleStatus())) {
            throw new BaseException("仅确认状态才允许变更");
        }
        reqRequireDocMapper.updateAllById(reqRequireDoc);
        //需求单明细对象
        List<ReqRequireDocItem> reqRequireDocItemList = reqRequireDoc.getReqRequireDocItemList();
        if (CollectionUtils.isNotEmpty(reqRequireDocItemList)) {
            reqRequireDocItemList.stream().forEach(o -> {
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addReqRequireDocItem(reqRequireDoc, reqRequireDocItemList);
        } else {
            deleteItem(reqRequireDoc);
        }
        //需求单附件对象
        List<ReqRequireDocAttachment> reqRequireDocAttachmentList = reqRequireDoc.getReqRequireDocAttachmentList();
        if (CollectionUtils.isNotEmpty(reqRequireDocAttachmentList)) {
            reqRequireDocAttachmentList.stream().forEach(o -> {
                o.setUpdateDate(new Date());
                o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            });
            addReqRequireDocAttachment(reqRequireDoc, reqRequireDocAttachmentList);
        } else {
            deleteAttachment(reqRequireDoc);
        }
        ReqRequireDoc response = reqRequireDocMapper.selectReqRequireDocById(reqRequireDoc.getRequireDocSid());
        //插入日志
        MongodbUtil.insertUserLog(reqRequireDoc.getRequireDocSid(), BusinessType.CHANGE.getValue(), response, reqRequireDoc, TITLE);
        return 1;
    }
}
