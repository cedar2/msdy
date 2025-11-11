package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinPurchaseDeductionBillAttachment;
import com.platform.ems.mapper.FinPurchaseDeductionBillAttachmentMapper;
import com.platform.ems.service.IFinPurchaseDeductionAttachmentService;

/**
 * 采购扣款单-附件Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-10
 */
@Service
@SuppressWarnings("all")
public class FinPurchaseDeductionAttachmentServiceImpl extends ServiceImpl<FinPurchaseDeductionBillAttachmentMapper,FinPurchaseDeductionBillAttachment>  implements IFinPurchaseDeductionAttachmentService {
    @Autowired
    private FinPurchaseDeductionBillAttachmentMapper FinPurchaseDeductionBillAttachmentMapper;

    /**
     * 查询采购扣款单-附件
     * 
     * @param purchaseDeductionAttachmentSid 采购扣款单-附件ID
     * @return 采购扣款单-附件
     */
    @Override
    public FinPurchaseDeductionBillAttachment selectFinPurchaseDeductionAttachmentById(Long purchaseDeductionAttachmentSid) {
        return FinPurchaseDeductionBillAttachmentMapper.selectFinPurchaseDeductionAttachmentById(purchaseDeductionAttachmentSid);
    }

    /**
     * 查询采购扣款单-附件列表
     * 
     * @param FinPurchaseDeductionBillAttachment 采购扣款单-附件
     * @return 采购扣款单-附件
     */
    @Override
    public List<FinPurchaseDeductionBillAttachment> selectFinPurchaseDeductionAttachmentList(FinPurchaseDeductionBillAttachment FinPurchaseDeductionBillAttachment) {
        return FinPurchaseDeductionBillAttachmentMapper.selectFinPurchaseDeductionAttachmentList(FinPurchaseDeductionBillAttachment);
    }

    /**
     * 新增采购扣款单-附件
     * 需要注意编码重复校验
     * @param FinPurchaseDeductionBillAttachment 采购扣款单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinPurchaseDeductionAttachment(FinPurchaseDeductionBillAttachment FinPurchaseDeductionBillAttachment) {
        return FinPurchaseDeductionBillAttachmentMapper.insert(FinPurchaseDeductionBillAttachment);
    }

    /**
     * 修改采购扣款单-附件
     * 
     * @param FinPurchaseDeductionBillAttachment 采购扣款单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinPurchaseDeductionAttachment(FinPurchaseDeductionBillAttachment FinPurchaseDeductionBillAttachment) {
        return FinPurchaseDeductionBillAttachmentMapper.updateById(FinPurchaseDeductionBillAttachment);
    }

    /**
     * 批量删除采购扣款单-附件
     * 
     * @param purchaseDeductionAttachmentSids 需要删除的采购扣款单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinPurchaseDeductionAttachmentByIds(List<Long> purchaseDeductionAttachmentSids) {
        return FinPurchaseDeductionBillAttachmentMapper.deleteBatchIds(purchaseDeductionAttachmentSids);
    }


}
