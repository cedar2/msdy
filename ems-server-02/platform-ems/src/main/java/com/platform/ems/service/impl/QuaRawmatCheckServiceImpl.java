package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IQuaRawmatCheckService;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 面辅料检测单-主Service业务层处理
 *
 * @author linhongwei
 * @date 2022-04-11
 */
@Service
@SuppressWarnings("all")
public class QuaRawmatCheckServiceImpl extends ServiceImpl<QuaRawmatCheckMapper, QuaRawmatCheck> implements IQuaRawmatCheckService {
    @Autowired
    private QuaRawmatCheckMapper quaRawmatCheckMapper;
    @Autowired
    private QuaRawmatCheckProductsMapper quaRawmatCheckProductsMapper;
    @Autowired
    private QuaRawmatCheckItemMapper quaRawmatCheckItemMapper;
    @Autowired
    private QuaRawmatCheckAttachMapper quaRawmatCheckAttachMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "面辅料检测单-主";

    /**
     * 查询面辅料检测单-主
     *
     * @param rawmatCheckSid 面辅料检测单-主ID
     * @return 面辅料检测单-主
     */
    @Override
    public QuaRawmatCheck selectQuaRawmatCheckById(Long rawmatCheckSid) {
        QuaRawmatCheck quaRawmatCheck = quaRawmatCheckMapper.selectQuaRawmatCheckById(rawmatCheckSid);
        if (quaRawmatCheck == null) {
            return null;
        }
        //面辅料检测单-款明细
        List<QuaRawmatCheckProducts> productList =
                quaRawmatCheckProductsMapper.selectQuaRawmatCheckProductsList(new QuaRawmatCheckProducts().setRawmatCheckSid(rawmatCheckSid));
        //面辅料检测单-检测项目
        List<QuaRawmatCheckItem> itemList =
                quaRawmatCheckItemMapper.selectQuaRawmatCheckItemList(new QuaRawmatCheckItem().setRawmatCheckSid(rawmatCheckSid));
        //面辅料检测单-附件
        List<QuaRawmatCheckAttach> attachList =
                quaRawmatCheckAttachMapper.selectQuaRawmatCheckAttachList(new QuaRawmatCheckAttach().setRawmatCheckSid(rawmatCheckSid));
        quaRawmatCheck.setProductList(productList);
        quaRawmatCheck.setItemList(itemList);
        quaRawmatCheck.setAttachList(attachList);
        MongodbUtil.find(quaRawmatCheck);
        return quaRawmatCheck;
    }

    /**
     * 查询面辅料检测单-主列表
     *
     * @param quaRawmatCheck 面辅料检测单-主
     * @return 面辅料检测单-主
     */
    @Override
    public List<QuaRawmatCheck> selectQuaRawmatCheckList(QuaRawmatCheck quaRawmatCheck) {
        return quaRawmatCheckMapper.selectQuaRawmatCheckList(quaRawmatCheck);
    }

    /**
     * 新增面辅料检测单-主
     * 需要注意编码重复校验
     *
     * @param quaRawmatCheck 面辅料检测单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertQuaRawmatCheck(QuaRawmatCheck quaRawmatCheck) {
        //设置确认信息
        setConfirmInfo(quaRawmatCheck);
        int row = quaRawmatCheckMapper.insert(quaRawmatCheck);
        if (row > 0) {
            //面辅料检测单-款明细
            List<QuaRawmatCheckProducts> productList = quaRawmatCheck.getProductList();
            if (CollUtil.isNotEmpty(productList)) {
                addProduct(quaRawmatCheck, productList);
            }
            //面辅料检测单-检测项目
            List<QuaRawmatCheckItem> itemList = quaRawmatCheck.getItemList();
            if (CollUtil.isNotEmpty(itemList)) {
                addItem(quaRawmatCheck, itemList);
            }
            //面辅料检测单-附件
            List<QuaRawmatCheckAttach> attachList = quaRawmatCheck.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                addAttach(quaRawmatCheck, attachList);
            }

            //待办通知
            QuaRawmatCheck rawmatCheck = new QuaRawmatCheck();
            rawmatCheck = quaRawmatCheckMapper.selectQuaRawmatCheckById(quaRawmatCheck.getRawmatCheckSid());
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(quaRawmatCheck.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.MFLJC)
                        .setDocumentSid(quaRawmatCheck.getRawmatCheckSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("面辅料检测单" + rawmatCheck.getRawmatCheckCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(rawmatCheck.getRawmatCheckCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(quaRawmatCheck.getRawmatCheckSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(QuaRawmatCheck o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 面辅料检测单-款明细
     */
    private void addProduct(QuaRawmatCheck quaRawmatCheck, List<QuaRawmatCheckProducts> productList) {
        deleteProduct(quaRawmatCheck);
        productList.forEach(o -> {
            o.setRawmatCheckSid(quaRawmatCheck.getRawmatCheckSid());
        });
        quaRawmatCheckProductsMapper.inserts(productList);
    }

    /**
     * 面辅料检测单-检测项目
     */
    private void addItem(QuaRawmatCheck quaRawmatCheck, List<QuaRawmatCheckItem> itemList) {
        deleteItem(quaRawmatCheck);
        itemList.forEach(o -> {
            o.setRawmatCheckSid(quaRawmatCheck.getRawmatCheckSid());
        });
        quaRawmatCheckItemMapper.inserts(itemList);
    }

    /**
     * 面辅料检测单-附件
     */
    private void addAttach(QuaRawmatCheck quaRawmatCheck, List<QuaRawmatCheckAttach> attachList) {
        deleteAttach(quaRawmatCheck);
        attachList.forEach(o -> {
            o.setRawmatCheckSid(quaRawmatCheck.getRawmatCheckSid());
        });
        quaRawmatCheckAttachMapper.inserts(attachList);
    }

    /**
     * 删除款明细
     */
    private void deleteProduct(QuaRawmatCheck quaRawmatCheck) {
        quaRawmatCheckProductsMapper.delete(
                new UpdateWrapper<QuaRawmatCheckProducts>()
                        .lambda()
                        .eq(QuaRawmatCheckProducts::getRawmatCheckSid, quaRawmatCheck.getRawmatCheckSid())
        );
    }

    /**
     * 删除检测项目
     */
    private void deleteItem(QuaRawmatCheck quaRawmatCheck) {
        quaRawmatCheckItemMapper.delete(
                new UpdateWrapper<QuaRawmatCheckItem>()
                        .lambda()
                        .eq(QuaRawmatCheckItem::getRawmatCheckSid, quaRawmatCheck.getRawmatCheckSid())
        );
    }

    /**
     * 删除附件
     */
    private void deleteAttach(QuaRawmatCheck quaRawmatCheck) {
        quaRawmatCheckAttachMapper.delete(
                new UpdateWrapper<QuaRawmatCheckAttach>()
                        .lambda()
                        .eq(QuaRawmatCheckAttach::getRawmatCheckSid, quaRawmatCheck.getRawmatCheckSid())
        );
    }

    /**
     * 修改面辅料检测单-主
     *
     * @param quaRawmatCheck 面辅料检测单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuaRawmatCheck(QuaRawmatCheck quaRawmatCheck) {
        //设置确认信息
        setConfirmInfo(quaRawmatCheck);
        quaRawmatCheck.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        QuaRawmatCheck response = quaRawmatCheckMapper.selectQuaRawmatCheckById(quaRawmatCheck.getRawmatCheckSid());
        int row = quaRawmatCheckMapper.updateAllById(quaRawmatCheck);
        if (row > 0) {
            //面辅料检测单-款明细
            List<QuaRawmatCheckProducts> productList = quaRawmatCheck.getProductList();
            if (CollUtil.isNotEmpty(productList)) {
                addProduct(quaRawmatCheck, productList);
            } else {
                deleteProduct(quaRawmatCheck);
            }
            //面辅料检测单-检测项目
            List<QuaRawmatCheckItem> itemList = quaRawmatCheck.getItemList();
            if (CollUtil.isNotEmpty(itemList)) {
                addItem(quaRawmatCheck, itemList);
            } else {
                deleteItem(quaRawmatCheck);
            }
            //面辅料检测单-附件
            List<QuaRawmatCheckAttach> attachList = quaRawmatCheck.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                addAttach(quaRawmatCheck, attachList);
            } else {
                deleteAttach(quaRawmatCheck);
            }

            if (!ConstantsEms.SAVA_STATUS.equals(quaRawmatCheck.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(quaRawmatCheck);
            }
            //插入日志
            MongodbUtil.insertUserLog(quaRawmatCheck.getRawmatCheckSid(), BusinessType.UPDATE.getValue(), response, quaRawmatCheck, TITLE);
        }
        return row;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(QuaRawmatCheck quaRawmatCheck) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, quaRawmatCheck.getRawmatCheckSid()));
        if (CollUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, quaRawmatCheck.getRawmatCheckSid()));
        }
    }

    /**
     * 变更面辅料检测单-主
     *
     * @param quaRawmatCheck 面辅料检测单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeQuaRawmatCheck(QuaRawmatCheck quaRawmatCheck) {
        //设置确认信息
        setConfirmInfo(quaRawmatCheck);
        quaRawmatCheck.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        QuaRawmatCheck response = quaRawmatCheckMapper.selectQuaRawmatCheckById(quaRawmatCheck.getRawmatCheckSid());
        int row = quaRawmatCheckMapper.updateAllById(quaRawmatCheck);
        if (row > 0) {
            //面辅料检测单-款明细
            List<QuaRawmatCheckProducts> productList = quaRawmatCheck.getProductList();
            if (CollUtil.isNotEmpty(productList)) {
                addProduct(quaRawmatCheck, productList);
            } else {
                deleteProduct(quaRawmatCheck);
            }
            //面辅料检测单-检测项目
            List<QuaRawmatCheckItem> itemList = quaRawmatCheck.getItemList();
            if (CollUtil.isNotEmpty(itemList)) {
                addItem(quaRawmatCheck, itemList);
            } else {
                deleteItem(quaRawmatCheck);
            }
            //面辅料检测单-附件
            List<QuaRawmatCheckAttach> attachList = quaRawmatCheck.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                addAttach(quaRawmatCheck, attachList);
            } else {
                deleteAttach(quaRawmatCheck);
            }
            //插入日志
            MongodbUtil.insertUserLog(quaRawmatCheck.getRawmatCheckSid(), BusinessType.CHANGE.getValue(), response, quaRawmatCheck, TITLE);
        }
        return row;
    }

    /**
     * 批量删除面辅料检测单-主
     *
     * @param rawmatCheckSids 需要删除的面辅料检测单-主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteQuaRawmatCheckByIds(List<Long> rawmatCheckSids) {
        //删除校验
        Integer count = quaRawmatCheckMapper.selectCount(new QueryWrapper<QuaRawmatCheck>().lambda()
                .eq(QuaRawmatCheck::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(QuaRawmatCheck::getRawmatCheckSid, rawmatCheckSids));
        if (count != rawmatCheckSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        QuaRawmatCheck quaRawmatCheck = new QuaRawmatCheck();
        rawmatCheckSids.forEach(rawmatCheckSid -> {
            quaRawmatCheck.setRawmatCheckSid(rawmatCheckSid);
            //校验是否存在待办
            checkTodoExist(quaRawmatCheck);
        });
        //删除-款明细
        quaRawmatCheckProductsMapper.delete(new UpdateWrapper<QuaRawmatCheckProducts>().lambda()
                .in(QuaRawmatCheckProducts::getRawmatCheckSid, rawmatCheckSids));
        //删除-检测项目
        quaRawmatCheckItemMapper.delete(new UpdateWrapper<QuaRawmatCheckItem>().lambda()
                .in(QuaRawmatCheckItem::getRawmatCheckSid, rawmatCheckSids));
        //删除-附件
        quaRawmatCheckAttachMapper.delete(new UpdateWrapper<QuaRawmatCheckAttach>().lambda()
                .in(QuaRawmatCheckAttach::getRawmatCheckSid, rawmatCheckSids));
        return quaRawmatCheckMapper.deleteBatchIds(rawmatCheckSids);
    }

    /**
     * 更改确认状态
     *
     * @param quaRawmatCheck
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(QuaRawmatCheck quaRawmatCheck) {
        int row = 0;
        Long[] sids = quaRawmatCheck.getRawmatCheckSidList();
        if (sids != null && sids.length > 0) {
            row = quaRawmatCheckMapper.update(new QuaRawmatCheck(), new UpdateWrapper<QuaRawmatCheck>().lambda()
                    .set(QuaRawmatCheck::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(QuaRawmatCheck::getConfirmDate, new Date())
                    .set(QuaRawmatCheck::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .in(QuaRawmatCheck::getRawmatCheckSid, sids));
            for (Long id : sids) {
                //校验是否存在待办
                quaRawmatCheck.setRawmatCheckSid(id);
                checkTodoExist(quaRawmatCheck);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


}
