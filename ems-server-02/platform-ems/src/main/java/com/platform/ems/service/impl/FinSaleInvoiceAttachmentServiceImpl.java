package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FinSaleInvoiceAttachmentMapper;
import com.platform.ems.domain.FinSaleInvoiceAttachment;
import com.platform.ems.service.IFinSaleInvoiceAttachmentService;

/**
 * 销售发票-附件Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
@Service
@SuppressWarnings("all")
public class FinSaleInvoiceAttachmentServiceImpl extends ServiceImpl<FinSaleInvoiceAttachmentMapper,FinSaleInvoiceAttachment>  implements IFinSaleInvoiceAttachmentService {
    @Autowired
    private FinSaleInvoiceAttachmentMapper finSaleInvoiceAttachmentMapper;

    /**
     * 查询销售发票-附件
     * 
     * @param saleInvoiceAttachmentSid 销售发票-附件ID
     * @return 销售发票-附件
     */
    @Override
    public FinSaleInvoiceAttachment selectFinSaleInvoiceAttachmentById(Long saleInvoiceAttachmentSid) {
        return finSaleInvoiceAttachmentMapper.selectFinSaleInvoiceAttachmentById(saleInvoiceAttachmentSid);
    }

    /**
     * 查询销售发票-附件列表
     * 
     * @param finSaleInvoiceAttachment 销售发票-附件
     * @return 销售发票-附件
     */
    @Override
    public List<FinSaleInvoiceAttachment> selectFinSaleInvoiceAttachmentList(FinSaleInvoiceAttachment finSaleInvoiceAttachment) {
        return finSaleInvoiceAttachmentMapper.selectFinSaleInvoiceAttachmentList(finSaleInvoiceAttachment);
    }

    /**
     * 新增销售发票-附件
     * 需要注意编码重复校验
     * @param finSaleInvoiceAttachment 销售发票-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinSaleInvoiceAttachment(FinSaleInvoiceAttachment finSaleInvoiceAttachment) {
        return finSaleInvoiceAttachmentMapper.insert(finSaleInvoiceAttachment);
    }

    /**
     * 修改销售发票-附件
     * 
     * @param finSaleInvoiceAttachment 销售发票-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinSaleInvoiceAttachment(FinSaleInvoiceAttachment finSaleInvoiceAttachment) {
        return finSaleInvoiceAttachmentMapper.updateById(finSaleInvoiceAttachment);
    }

    /**
     * 批量删除销售发票-附件
     * 
     * @param saleInvoiceAttachmentSids 需要删除的销售发票-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinSaleInvoiceAttachmentByIds(List<Long> saleInvoiceAttachmentSids) {
        return finSaleInvoiceAttachmentMapper.deleteBatchIds(saleInvoiceAttachmentSids);
    }


}
