package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ManManufactureDefective;
import com.platform.ems.domain.ManManufactureDefectiveAttach;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.mapper.ManManufactureDefectiveAttachMapper;
import com.platform.ems.mapper.ManManufactureDefectiveMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.service.IManManufactureDefectiveService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 生产次品台账Service业务层处理
 *
 * @author c
 * @date 2022-03-02
 */
@Service
@SuppressWarnings("all")
public class ManManufactureDefectiveServiceImpl extends ServiceImpl<ManManufactureDefectiveMapper, ManManufactureDefective> implements IManManufactureDefectiveService {
    @Autowired
    private ManManufactureDefectiveMapper manManufactureDefectiveMapper;
    @Autowired
    private ManManufactureDefectiveAttachMapper manManufactureDefectiveAttachMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "生产次品台账";

    /**
     * 查询生产次品台账
     *
     * @param manufactureDefectiveSid 生产次品台账ID
     * @return 生产次品台账
     */
    @Override
    public ManManufactureDefective selectManManufactureDefectiveById(Long manufactureDefectiveSid) {
        ManManufactureDefective manManufactureDefective = manManufactureDefectiveMapper.selectManManufactureDefectiveById(manufactureDefectiveSid);
        if (manManufactureDefective == null) {
            return null;
        }
        List<ManManufactureDefectiveAttach> attachList =
                manManufactureDefectiveAttachMapper.selectManManufactureDefectiveAttachList(new ManManufactureDefectiveAttach().setManufactureDefectiveSid(manufactureDefectiveSid));
        manManufactureDefective.setAttachList(attachList);
        MongodbUtil.find(manManufactureDefective);
        return manManufactureDefective;
    }

    /**
     * 查询生产次品台账列表
     *
     * @param manManufactureDefective 生产次品台账
     * @return 生产次品台账
     */
    @Override
    public List<ManManufactureDefective> selectManManufactureDefectiveList(ManManufactureDefective manManufactureDefective) {
        return manManufactureDefectiveMapper.selectManManufactureDefectiveList(manManufactureDefective);
    }

    /**
     * 新增生产次品台账
     * 需要注意编码重复校验
     *
     * @param manManufactureDefective 生产次品台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManManufactureDefective(ManManufactureDefective manManufactureDefective) {
        //设置确认信息
        setConfirmInfo(manManufactureDefective);
        int row = manManufactureDefectiveMapper.insert(manManufactureDefective);
        if (row > 0) {
            //生产次品台账-附件对象
            List<ManManufactureDefectiveAttach> attachList = manManufactureDefective.getAttachList();
            if (CollectionUtil.isNotEmpty(attachList)) {
                addAttach(manManufactureDefective, attachList);
            }
            ManManufactureDefective manufactureDefective =
                    manManufactureDefectiveMapper.selectManManufactureDefectiveById(manManufactureDefective.getManufactureDefectiveSid());
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(manManufactureDefective.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.SCCPTZ)
                        .setDocumentSid(manManufactureDefective.getManufactureDefectiveSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("生产次品台账" + manufactureDefective.getManufactureDefectiveCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(manufactureDefective.getManufactureDefectiveCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(manManufactureDefective);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(manManufactureDefective.getManufactureDefectiveSid(), manManufactureDefective.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ManManufactureDefective o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 生产次品台账-附件对象
     */
    private void addAttach(ManManufactureDefective manManufactureDefective, List<ManManufactureDefectiveAttach> attachList) {
        attachList.forEach(o -> {
            o.setManufactureDefectiveSid(manManufactureDefective.getManufactureDefectiveSid());
        });
        manManufactureDefectiveAttachMapper.inserts(attachList);
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(ManManufactureDefective manManufactureDefective) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, manManufactureDefective.getManufactureDefectiveSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, manManufactureDefective.getManufactureDefectiveSid()));
        }
    }

    /**
     * 修改生产次品台账
     *
     * @param manManufactureDefective 生产次品台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManManufactureDefective(ManManufactureDefective manManufactureDefective) {
        ManManufactureDefective response = manManufactureDefectiveMapper.selectManManufactureDefectiveById(manManufactureDefective.getManufactureDefectiveSid());
        //设置确认信息
        setConfirmInfo(manManufactureDefective);
        manManufactureDefective.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = manManufactureDefectiveMapper.updateById(manManufactureDefective);
        if (row > 0) {
            //生产次品台账-附件对象
            List<ManManufactureDefectiveAttach> attachList = manManufactureDefective.getAttachList();
            operateAttach(manManufactureDefective, attachList);
            if (!ConstantsEms.SAVA_STATUS.equals(manManufactureDefective.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(manManufactureDefective);
            }
            //插入日志
            List<OperMsg> msgList = BeanUtils.eq(response, manManufactureDefective);
            MongodbDeal.update(manManufactureDefective.getManufactureDefectiveSid(), response.getHandleStatus(), manManufactureDefective.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 生产次品台账-附件
     */
    private void operateAttach(ManManufactureDefective manManufactureDefective, List<ManManufactureDefectiveAttach> attachList) {
        if (CollectionUtil.isNotEmpty(attachList)) {
            //新增
            List<ManManufactureDefectiveAttach> addList = attachList.stream().filter(o -> o.getAttachmentSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addAttach(manManufactureDefective, addList);
            }
            //编辑
            List<ManManufactureDefectiveAttach> editList = attachList.stream().filter(o -> o.getAttachmentSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manManufactureDefectiveAttachMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ManManufactureDefectiveAttach> itemList =
                    manManufactureDefectiveAttachMapper.selectList(new QueryWrapper<ManManufactureDefectiveAttach>().lambda()
                            .eq(ManManufactureDefectiveAttach::getManufactureDefectiveSid, manManufactureDefective.getManufactureDefectiveSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManManufactureDefectiveAttach::getAttachmentSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = attachList.stream().map(ManManufactureDefectiveAttach::getAttachmentSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manManufactureDefectiveAttachMapper.deleteBatchIds(result);
            }
        } else {
            deleteAttach(manManufactureDefective);
        }
    }

    /**
     * 删除生产次品台账-附件
     */
    private void deleteAttach(ManManufactureDefective manManufactureDefective) {
        manManufactureDefectiveAttachMapper.delete(
                new UpdateWrapper<ManManufactureDefectiveAttach>()
                        .lambda()
                        .eq(ManManufactureDefectiveAttach::getManufactureDefectiveSid, manManufactureDefective.getManufactureDefectiveSid())
        );
    }

    /**
     * 变更生产次品台账
     *
     * @param manManufactureDefective 生产次品台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManManufactureDefective(ManManufactureDefective manManufactureDefective) {
        ManManufactureDefective response = manManufactureDefectiveMapper.selectManManufactureDefectiveById(manManufactureDefective.getManufactureDefectiveSid());
        //设置确认信息
        setConfirmInfo(manManufactureDefective);
        int row = manManufactureDefectiveMapper.updateAllById(manManufactureDefective);
        if (row > 0) {
            //生产次品台账-附件对象
            List<ManManufactureDefectiveAttach> attachList = manManufactureDefective.getAttachList();
            operateAttach(manManufactureDefective, attachList);
            //插入日志
            List<OperMsg> msgList = BeanUtils.eq(response, manManufactureDefective);
            MongodbDeal.update(manManufactureDefective.getManufactureDefectiveSid(), response.getHandleStatus(), manManufactureDefective.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量删除生产次品台账
     *
     * @param manufactureDefectiveSids 需要删除的生产次品台账ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManManufactureDefectiveByIds(List<Long> manufactureDefectiveSids) {
        Integer count = manManufactureDefectiveMapper.selectCount(new UpdateWrapper<ManManufactureDefective>().lambda()
                .eq(ManManufactureDefective::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(ManManufactureDefective::getManufactureDefectiveSid, manufactureDefectiveSids));
        if (count != manufactureDefectiveSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        ///生产次品台账-附件对象
        manManufactureDefectiveAttachMapper.delete(new UpdateWrapper<ManManufactureDefectiveAttach>().lambda()
                .in(ManManufactureDefectiveAttach::getManufactureDefectiveSid, manufactureDefectiveSids));
        return manManufactureDefectiveMapper.deleteBatchIds(manufactureDefectiveSids);
    }

    /**
     * 更改确认状态
     *
     * @param manManufactureDefective
     * @return
     */
    @Override
    public int check(ManManufactureDefective manManufactureDefective) {
        int row = 0;
        Long[] sids = manManufactureDefective.getManufactureDefectiveSidList();
        if (ArrayUtil.isNotEmpty(sids)) {
            row = manManufactureDefectiveMapper.update(null, new UpdateWrapper<ManManufactureDefective>().lambda()
                    .set(ManManufactureDefective::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(ManManufactureDefective::getConfirmDate, new Date())
                    .set(ManManufactureDefective::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .in(ManManufactureDefective::getManufactureDefectiveSid, sids));
            for (Long id : sids) {
                manManufactureDefective.setManufactureDefectiveSid(id);
                //校验是否存在待办
                checkTodoExist(manManufactureDefective);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.check(id, manManufactureDefective.getHandleStatus(), msgList, TITLE, null);
            }
        }
        return row;
    }
}
