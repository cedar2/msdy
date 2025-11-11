package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.PaySalaryBillAttach;
import com.platform.ems.mapper.PaySalaryBillAttachMapper;
import com.platform.ems.service.IPaySalaryBillAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 工资单-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-14
 */
@Service
@SuppressWarnings("all")
public class PaySalaryBillAttachServiceImpl extends ServiceImpl<PaySalaryBillAttachMapper, PaySalaryBillAttach> implements IPaySalaryBillAttachService {
    @Autowired
    private PaySalaryBillAttachMapper paySalaryBillAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "工资单-附件";

    /**
     * 查询工资单-附件
     *
     * @param attachmentSid 工资单-附件ID
     * @return 工资单-附件
     */
    @Override
    public PaySalaryBillAttach selectPaySalaryBillAttachById(Long attachmentSid) {
        PaySalaryBillAttach paySalaryBillAttach = paySalaryBillAttachMapper.selectPaySalaryBillAttachById(attachmentSid);
        MongodbUtil.find(paySalaryBillAttach);
        return paySalaryBillAttach;
    }

    /**
     * 查询工资单-附件列表
     *
     * @param paySalaryBillAttach 工资单-附件
     * @return 工资单-附件
     */
    @Override
    public List<PaySalaryBillAttach> selectPaySalaryBillAttachList(PaySalaryBillAttach paySalaryBillAttach) {
        return paySalaryBillAttachMapper.selectPaySalaryBillAttachList(paySalaryBillAttach);
    }

    /**
     * 新增工资单-附件
     * 需要注意编码重复校验
     *
     * @param paySalaryBillAttach 工资单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPaySalaryBillAttach(PaySalaryBillAttach paySalaryBillAttach) {
        int row = paySalaryBillAttachMapper.insert(paySalaryBillAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(paySalaryBillAttach.getAttachmentSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改工资单-附件
     *
     * @param paySalaryBillAttach 工资单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePaySalaryBillAttach(PaySalaryBillAttach paySalaryBillAttach) {
        PaySalaryBillAttach response = paySalaryBillAttachMapper.selectPaySalaryBillAttachById(paySalaryBillAttach.getAttachmentSid());
        int row = paySalaryBillAttachMapper.updateById(paySalaryBillAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(paySalaryBillAttach.getAttachmentSid(), BusinessType.UPDATE.getValue(), response, paySalaryBillAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更工资单-附件
     *
     * @param paySalaryBillAttach 工资单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePaySalaryBillAttach(PaySalaryBillAttach paySalaryBillAttach) {
        PaySalaryBillAttach response = paySalaryBillAttachMapper.selectPaySalaryBillAttachById(paySalaryBillAttach.getAttachmentSid());
        int row = paySalaryBillAttachMapper.updateAllById(paySalaryBillAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(paySalaryBillAttach.getAttachmentSid(), BusinessType.CHANGE.getValue(), response, paySalaryBillAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除工资单-附件
     *
     * @param attachmentSids 需要删除的工资单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePaySalaryBillAttachByIds(List<Long> attachmentSids) {
        return paySalaryBillAttachMapper.deleteBatchIds(attachmentSids);
    }
}
