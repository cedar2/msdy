package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.PayWorkattendRecordAttach;
import com.platform.ems.mapper.PayWorkattendRecordAttachMapper;
import com.platform.ems.service.IPayWorkattendRecordAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 考勤信息-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-14
 */
@Service
@SuppressWarnings("all")
public class PayWorkattendRecordAttachServiceImpl extends ServiceImpl<PayWorkattendRecordAttachMapper, PayWorkattendRecordAttach> implements IPayWorkattendRecordAttachService {
    @Autowired
    private PayWorkattendRecordAttachMapper payWorkattendRecordAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "考勤信息-附件";

    /**
     * 查询考勤信息-附件
     *
     * @param attachmentSid 考勤信息-附件ID
     * @return 考勤信息-附件
     */
    @Override
    public PayWorkattendRecordAttach selectPayWorkattendRecordAttachById(Long attachmentSid) {
        PayWorkattendRecordAttach payWorkattendRecordAttach = payWorkattendRecordAttachMapper.selectPayWorkattendRecordAttachById(attachmentSid);
        MongodbUtil.find(payWorkattendRecordAttach);
        return payWorkattendRecordAttach;
    }

    /**
     * 查询考勤信息-附件列表
     *
     * @param payWorkattendRecordAttach 考勤信息-附件
     * @return 考勤信息-附件
     */
    @Override
    public List<PayWorkattendRecordAttach> selectPayWorkattendRecordAttachList(PayWorkattendRecordAttach payWorkattendRecordAttach) {
        return payWorkattendRecordAttachMapper.selectPayWorkattendRecordAttachList(payWorkattendRecordAttach);
    }

    /**
     * 新增考勤信息-附件
     * 需要注意编码重复校验
     *
     * @param payWorkattendRecordAttach 考勤信息-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPayWorkattendRecordAttach(PayWorkattendRecordAttach payWorkattendRecordAttach) {
        int row = payWorkattendRecordAttachMapper.insert(payWorkattendRecordAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(payWorkattendRecordAttach.getAttachmentSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改考勤信息-附件
     *
     * @param payWorkattendRecordAttach 考勤信息-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePayWorkattendRecordAttach(PayWorkattendRecordAttach payWorkattendRecordAttach) {
        PayWorkattendRecordAttach response = payWorkattendRecordAttachMapper.selectPayWorkattendRecordAttachById(payWorkattendRecordAttach.getAttachmentSid());
        int row = payWorkattendRecordAttachMapper.updateById(payWorkattendRecordAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(payWorkattendRecordAttach.getAttachmentSid(), BusinessType.UPDATE.getValue(), response, payWorkattendRecordAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更考勤信息-附件
     *
     * @param payWorkattendRecordAttach 考勤信息-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePayWorkattendRecordAttach(PayWorkattendRecordAttach payWorkattendRecordAttach) {
        PayWorkattendRecordAttach response = payWorkattendRecordAttachMapper.selectPayWorkattendRecordAttachById(payWorkattendRecordAttach.getAttachmentSid());
        int row = payWorkattendRecordAttachMapper.updateAllById(payWorkattendRecordAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(payWorkattendRecordAttach.getAttachmentSid(), BusinessType.CHANGE.getValue(), response, payWorkattendRecordAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除考勤信息-附件
     *
     * @param attachmentSids 需要删除的考勤信息-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePayWorkattendRecordAttachByIds(List<Long> attachmentSids) {
        return payWorkattendRecordAttachMapper.deleteBatchIds(attachmentSids);
    }
}
