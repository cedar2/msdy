package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinSaleDeductionBillAttachment;
import com.platform.ems.mapper.FinSaleDeductionBillAttachmentMapper;
import com.platform.ems.service.IFinSaleDeductionBillAttachmentService;

/**
 * 销售扣款单-附件Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-09
 */
@Service
@SuppressWarnings("all")
public class FinSaleDeductionBillAttachmentServiceImpl extends ServiceImpl<FinSaleDeductionBillAttachmentMapper,FinSaleDeductionBillAttachment>  implements IFinSaleDeductionBillAttachmentService {
    @Autowired
    private FinSaleDeductionBillAttachmentMapper FinSaleDeductionBillAttachmentMapper;

    /**
     * 查询销售扣款单-附件
     * 
     * @param saleDeductionAttachmentSid 销售扣款单-附件ID
     * @return 销售扣款单-附件
     */
    @Override
    public FinSaleDeductionBillAttachment selectFinSaleDeductionAttachmentById(Long saleDeductionAttachmentSid) {
        return FinSaleDeductionBillAttachmentMapper.selectFinSaleDeductionAttachmentById(saleDeductionAttachmentSid);
    }

    /**
     * 查询销售扣款单-附件列表
     * 
     * @param FinSaleDeductionBillAttachment 销售扣款单-附件
     * @return 销售扣款单-附件
     */
    @Override
    public List<FinSaleDeductionBillAttachment> selectFinSaleDeductionAttachmentList(FinSaleDeductionBillAttachment FinSaleDeductionBillAttachment) {
        return FinSaleDeductionBillAttachmentMapper.selectFinSaleDeductionAttachmentList(FinSaleDeductionBillAttachment);
    }

    /**
     * 新增销售扣款单-附件
     * 需要注意编码重复校验
     * @param FinSaleDeductionBillAttachment 销售扣款单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinSaleDeductionAttachment(FinSaleDeductionBillAttachment FinSaleDeductionBillAttachment) {
        return FinSaleDeductionBillAttachmentMapper.insert(FinSaleDeductionBillAttachment);
    }

    /**
     * 修改销售扣款单-附件
     * 
     * @param FinSaleDeductionBillAttachment 销售扣款单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinSaleDeductionAttachment(FinSaleDeductionBillAttachment FinSaleDeductionBillAttachment) {
        return FinSaleDeductionBillAttachmentMapper.updateById(FinSaleDeductionBillAttachment);
    }

    /**
     * 批量删除销售扣款单-附件
     * 
     * @param saleDeductionAttachmentSids 需要删除的销售扣款单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinSaleDeductionAttachmentByIds(List<Long> saleDeductionAttachmentSids) {
        return FinSaleDeductionBillAttachmentMapper.deleteBatchIds(saleDeductionAttachmentSids);
    }

}
