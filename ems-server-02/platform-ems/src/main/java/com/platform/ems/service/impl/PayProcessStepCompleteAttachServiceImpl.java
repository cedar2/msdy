package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.PayProcessStepCompleteAttach;
import com.platform.ems.mapper.PayProcessStepCompleteAttachMapper;
import com.platform.ems.service.IPayProcessStepCompleteAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 计薪量申报-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-08
 */
@Service
@SuppressWarnings("all")
public class PayProcessStepCompleteAttachServiceImpl extends ServiceImpl<PayProcessStepCompleteAttachMapper, PayProcessStepCompleteAttach> implements IPayProcessStepCompleteAttachService {
    @Autowired
    private PayProcessStepCompleteAttachMapper payProcessStepCompleteAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "计薪量申报-附件";

    /**
     * 查询计薪量申报-附件
     *
     * @param attachmentSid 计薪量申报-附件ID
     * @return 计薪量申报-附件
     */
    @Override
    public PayProcessStepCompleteAttach selectPayProcessStepCompleteAttachById(Long attachmentSid) {
        PayProcessStepCompleteAttach payProcessStepCompleteAttach = payProcessStepCompleteAttachMapper.selectPayProcessStepCompleteAttachById(attachmentSid);
        MongodbUtil.find(payProcessStepCompleteAttach);
        return payProcessStepCompleteAttach;
    }

    /**
     * 查询计薪量申报-附件列表
     *
     * @param payProcessStepCompleteAttach 计薪量申报-附件
     * @return 计薪量申报-附件
     */
    @Override
    public List<PayProcessStepCompleteAttach> selectPayProcessStepCompleteAttachList(PayProcessStepCompleteAttach payProcessStepCompleteAttach) {
        return payProcessStepCompleteAttachMapper.selectPayProcessStepCompleteAttachList(payProcessStepCompleteAttach);
    }

    /**
     * 新增计薪量申报-附件
     * 需要注意编码重复校验
     *
     * @param payProcessStepCompleteAttach 计薪量申报-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPayProcessStepCompleteAttach(PayProcessStepCompleteAttach payProcessStepCompleteAttach) {
        int row = payProcessStepCompleteAttachMapper.insert(payProcessStepCompleteAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(payProcessStepCompleteAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改计薪量申报-附件
     *
     * @param payProcessStepCompleteAttach 计薪量申报-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePayProcessStepCompleteAttach(PayProcessStepCompleteAttach payProcessStepCompleteAttach) {
        PayProcessStepCompleteAttach response = payProcessStepCompleteAttachMapper.selectPayProcessStepCompleteAttachById(payProcessStepCompleteAttach.getAttachmentSid());
        int row = payProcessStepCompleteAttachMapper.updateById(payProcessStepCompleteAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(payProcessStepCompleteAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response, payProcessStepCompleteAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更计薪量申报-附件
     *
     * @param payProcessStepCompleteAttach 计薪量申报-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePayProcessStepCompleteAttach(PayProcessStepCompleteAttach payProcessStepCompleteAttach) {
        PayProcessStepCompleteAttach response = payProcessStepCompleteAttachMapper.selectPayProcessStepCompleteAttachById(payProcessStepCompleteAttach.getAttachmentSid());
        int row = payProcessStepCompleteAttachMapper.updateAllById(payProcessStepCompleteAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(payProcessStepCompleteAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response, payProcessStepCompleteAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除计薪量申报-附件
     *
     * @param attachmentSids 需要删除的计薪量申报-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePayProcessStepCompleteAttachByIds(List<Long> attachmentSids) {
        return payProcessStepCompleteAttachMapper.deleteBatchIds(attachmentSids);
    }
}
