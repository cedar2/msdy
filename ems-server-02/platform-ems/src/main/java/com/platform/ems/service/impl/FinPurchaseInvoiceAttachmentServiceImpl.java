package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FinPurchaseInvoiceAttachmentMapper;
import com.platform.ems.domain.FinPurchaseInvoiceAttachment;
import com.platform.ems.service.IFinPurchaseInvoiceAttachmentService;

/**
 * 采购发票-附件Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
@Service
@SuppressWarnings("all")
public class FinPurchaseInvoiceAttachmentServiceImpl extends ServiceImpl<FinPurchaseInvoiceAttachmentMapper,FinPurchaseInvoiceAttachment>  implements IFinPurchaseInvoiceAttachmentService {
    @Autowired
    private FinPurchaseInvoiceAttachmentMapper finPurchaseInvoiceAttachmentMapper;

    /**
     * 查询采购发票-附件
     * 
     * @param purchaseInvoiceAttachmentSid 采购发票-附件ID
     * @return 采购发票-附件
     */
    @Override
    public FinPurchaseInvoiceAttachment selectFinPurchaseInvoiceAttachmentById(Long purchaseInvoiceAttachmentSid) {
        return finPurchaseInvoiceAttachmentMapper.selectFinPurchaseInvoiceAttachmentById(purchaseInvoiceAttachmentSid);
    }

    /**
     * 查询采购发票-附件列表
     * 
     * @param finPurchaseInvoiceAttachment 采购发票-附件
     * @return 采购发票-附件
     */
    @Override
    public List<FinPurchaseInvoiceAttachment> selectFinPurchaseInvoiceAttachmentList(FinPurchaseInvoiceAttachment finPurchaseInvoiceAttachment) {
        return finPurchaseInvoiceAttachmentMapper.selectFinPurchaseInvoiceAttachmentList(finPurchaseInvoiceAttachment);
    }

    /**
     * 新增采购发票-附件
     * 需要注意编码重复校验
     * @param finPurchaseInvoiceAttachment 采购发票-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinPurchaseInvoiceAttachment(FinPurchaseInvoiceAttachment finPurchaseInvoiceAttachment) {
        return finPurchaseInvoiceAttachmentMapper.insert(finPurchaseInvoiceAttachment);
    }

    /**
     * 修改采购发票-附件
     * 
     * @param finPurchaseInvoiceAttachment 采购发票-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinPurchaseInvoiceAttachment(FinPurchaseInvoiceAttachment finPurchaseInvoiceAttachment) {
        return finPurchaseInvoiceAttachmentMapper.updateById(finPurchaseInvoiceAttachment);
    }

    /**
     * 批量删除采购发票-附件
     * 
     * @param purchaseInvoiceAttachmentSids 需要删除的采购发票-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinPurchaseInvoiceAttachmentByIds(List<Long> purchaseInvoiceAttachmentSids) {
        return finPurchaseInvoiceAttachmentMapper.deleteBatchIds(purchaseInvoiceAttachmentSids);
    }


}
