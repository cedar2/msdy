package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.SalCustomerMonthAccountBillAttach;
import com.platform.ems.mapper.SalCustomerMonthAccountBillAttachMapper;
import com.platform.ems.service.ISalCustomerMonthAccountBillAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户对账单-附件Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Service
@SuppressWarnings("all")
public class SalCustomerMonthAccountBillAttachServiceImpl extends ServiceImpl<SalCustomerMonthAccountBillAttachMapper, SalCustomerMonthAccountBillAttach> implements ISalCustomerMonthAccountBillAttachService {
    @Autowired
    private SalCustomerMonthAccountBillAttachMapper salCustomerMonthAccountBillAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "客户对账单-附件";

    /**
     * 查询客户对账单-附件
     *
     * @param monthAccountBillAttachmentSid 客户对账单-附件ID
     * @return 客户对账单-附件
     */
    @Override
    public SalCustomerMonthAccountBillAttach selectSalCustomerMonthAccountBillAttachById(Long monthAccountBillAttachmentSid) {
        SalCustomerMonthAccountBillAttach salCustomerMonthAccountBillAttach = salCustomerMonthAccountBillAttachMapper.selectSalCustomerMonthAccountBillAttachById(monthAccountBillAttachmentSid);
        MongodbUtil.find(salCustomerMonthAccountBillAttach);
        return salCustomerMonthAccountBillAttach;
    }

    /**
     * 查询客户对账单-附件列表
     *
     * @param salCustomerMonthAccountBillAttach 客户对账单-附件
     * @return 客户对账单-附件
     */
    @Override
    public List<SalCustomerMonthAccountBillAttach> selectSalCustomerMonthAccountBillAttachList(SalCustomerMonthAccountBillAttach salCustomerMonthAccountBillAttach) {
        return salCustomerMonthAccountBillAttachMapper.selectSalCustomerMonthAccountBillAttachList(salCustomerMonthAccountBillAttach);
    }

    /**
     * 新增客户对账单-附件
     * 需要注意编码重复校验
     *
     * @param salCustomerMonthAccountBillAttach 客户对账单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalCustomerMonthAccountBillAttach(SalCustomerMonthAccountBillAttach salCustomerMonthAccountBillAttach) {
        int row = salCustomerMonthAccountBillAttachMapper.insert(salCustomerMonthAccountBillAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(salCustomerMonthAccountBillAttach.getMonthAccountBillAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改客户对账单-附件
     *
     * @param salCustomerMonthAccountBillAttach 客户对账单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalCustomerMonthAccountBillAttach(SalCustomerMonthAccountBillAttach salCustomerMonthAccountBillAttach) {
        SalCustomerMonthAccountBillAttach response = salCustomerMonthAccountBillAttachMapper.selectSalCustomerMonthAccountBillAttachById(salCustomerMonthAccountBillAttach.getMonthAccountBillAttachmentSid());
        int row = salCustomerMonthAccountBillAttachMapper.updateById(salCustomerMonthAccountBillAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(salCustomerMonthAccountBillAttach.getMonthAccountBillAttachmentSid(), BusinessType.UPDATE.ordinal(), response, salCustomerMonthAccountBillAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更客户对账单-附件
     *
     * @param salCustomerMonthAccountBillAttach 客户对账单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSalCustomerMonthAccountBillAttach(SalCustomerMonthAccountBillAttach salCustomerMonthAccountBillAttach) {
        SalCustomerMonthAccountBillAttach response = salCustomerMonthAccountBillAttachMapper.selectSalCustomerMonthAccountBillAttachById(salCustomerMonthAccountBillAttach.getMonthAccountBillAttachmentSid());
        int row = salCustomerMonthAccountBillAttachMapper.updateAllById(salCustomerMonthAccountBillAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(salCustomerMonthAccountBillAttach.getMonthAccountBillAttachmentSid(), BusinessType.CHANGE.ordinal(), response, salCustomerMonthAccountBillAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除客户对账单-附件
     *
     * @param monthAccountBillAttachmentSids 需要删除的客户对账单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalCustomerMonthAccountBillAttachByIds(List<Long> monthAccountBillAttachmentSids) {
        return salCustomerMonthAccountBillAttachMapper.deleteBatchIds(monthAccountBillAttachmentSids);
    }

}
