package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.FinVendorMonthAccountBillAttachMapper;
import com.platform.ems.domain.FinVendorMonthAccountBillAttach;
import com.platform.ems.service.IFinVendorMonthAccountBillAttachService;

/**
 * 供应商月对账单-附件Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Service
@SuppressWarnings("all")
public class FinVendorMonthAccountBillAttachServiceImpl extends ServiceImpl<FinVendorMonthAccountBillAttachMapper, FinVendorMonthAccountBillAttach> implements IFinVendorMonthAccountBillAttachService {
    @Autowired
    private FinVendorMonthAccountBillAttachMapper finVendorMonthAccountBillAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商月对账单-附件";

    /**
     * 查询供应商月对账单-附件
     *
     * @param monthAccountBillAttachmentSid 供应商月对账单-附件ID
     * @return 供应商月对账单-附件
     */
    @Override
    public FinVendorMonthAccountBillAttach selectFinVendorMonthAccountBillAttachById(Long monthAccountBillAttachmentSid) {
        FinVendorMonthAccountBillAttach finVendorMonthAccountBillAttach = finVendorMonthAccountBillAttachMapper.selectFinVendorMonthAccountBillAttachById(monthAccountBillAttachmentSid);
        MongodbUtil.find(finVendorMonthAccountBillAttach);
        return finVendorMonthAccountBillAttach;
    }

    /**
     * 查询供应商月对账单-附件列表
     *
     * @param finVendorMonthAccountBillAttach 供应商月对账单-附件
     * @return 供应商月对账单-附件
     */
    @Override
    public List<FinVendorMonthAccountBillAttach> selectFinVendorMonthAccountBillAttachList(FinVendorMonthAccountBillAttach finVendorMonthAccountBillAttach) {
        return finVendorMonthAccountBillAttachMapper.selectFinVendorMonthAccountBillAttachList(finVendorMonthAccountBillAttach);
    }

    /**
     * 新增供应商月对账单-附件
     * 需要注意编码重复校验
     *
     * @param finVendorMonthAccountBillAttach 供应商月对账单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinVendorMonthAccountBillAttach(FinVendorMonthAccountBillAttach finVendorMonthAccountBillAttach) {
        int row = finVendorMonthAccountBillAttachMapper.insert(finVendorMonthAccountBillAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(finVendorMonthAccountBillAttach.getMonthAccountBillAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商月对账单-附件
     *
     * @param finVendorMonthAccountBillAttach 供应商月对账单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinVendorMonthAccountBillAttach(FinVendorMonthAccountBillAttach finVendorMonthAccountBillAttach) {
        FinVendorMonthAccountBillAttach response = finVendorMonthAccountBillAttachMapper.selectFinVendorMonthAccountBillAttachById(finVendorMonthAccountBillAttach.getMonthAccountBillAttachmentSid());
        int row = finVendorMonthAccountBillAttachMapper.updateById(finVendorMonthAccountBillAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finVendorMonthAccountBillAttach.getMonthAccountBillAttachmentSid(), BusinessType.UPDATE.ordinal(), response, finVendorMonthAccountBillAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商月对账单-附件
     *
     * @param finVendorMonthAccountBillAttach 供应商月对账单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinVendorMonthAccountBillAttach(FinVendorMonthAccountBillAttach finVendorMonthAccountBillAttach) {
        FinVendorMonthAccountBillAttach response = finVendorMonthAccountBillAttachMapper.selectFinVendorMonthAccountBillAttachById(finVendorMonthAccountBillAttach.getMonthAccountBillAttachmentSid());
        int row = finVendorMonthAccountBillAttachMapper.updateAllById(finVendorMonthAccountBillAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finVendorMonthAccountBillAttach.getMonthAccountBillAttachmentSid(), BusinessType.CHANGE.ordinal(), response, finVendorMonthAccountBillAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商月对账单-附件
     *
     * @param monthAccountBillAttachmentSids 需要删除的供应商月对账单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinVendorMonthAccountBillAttachByIds(List<Long> monthAccountBillAttachmentSids) {
        return finVendorMonthAccountBillAttachMapper.deleteBatchIds(monthAccountBillAttachmentSids);
    }

}
