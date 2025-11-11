package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FinPurchaseInvoiceDiscountMapper;
import com.platform.ems.domain.FinPurchaseInvoiceDiscount;
import com.platform.ems.service.IFinPurchaseInvoiceDiscountService;

/**
 * 采购发票-折扣Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
@Service
@SuppressWarnings("all")
public class FinPurchaseInvoiceDiscountServiceImpl extends ServiceImpl<FinPurchaseInvoiceDiscountMapper,FinPurchaseInvoiceDiscount>  implements IFinPurchaseInvoiceDiscountService {
    @Autowired
    private FinPurchaseInvoiceDiscountMapper finPurchaseInvoiceDiscountMapper;

    /**
     * 查询采购发票-折扣
     * 
     * @param purchaseInvoiceDiscountSid 采购发票-折扣ID
     * @return 采购发票-折扣
     */
    @Override
    public FinPurchaseInvoiceDiscount selectFinPurchaseInvoiceDiscountById(Long purchaseInvoiceDiscountSid) {
        return finPurchaseInvoiceDiscountMapper.selectFinPurchaseInvoiceDiscountById(purchaseInvoiceDiscountSid);
    }

    /**
     * 查询采购发票-折扣列表
     * 
     * @param finPurchaseInvoiceDiscount 采购发票-折扣
     * @return 采购发票-折扣
     */
    @Override
    public List<FinPurchaseInvoiceDiscount> selectFinPurchaseInvoiceDiscountList(FinPurchaseInvoiceDiscount finPurchaseInvoiceDiscount) {
        return finPurchaseInvoiceDiscountMapper.selectFinPurchaseInvoiceDiscountList(finPurchaseInvoiceDiscount);
    }

    /**
     * 新增采购发票-折扣
     * 需要注意编码重复校验
     * @param finPurchaseInvoiceDiscount 采购发票-折扣
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinPurchaseInvoiceDiscount(FinPurchaseInvoiceDiscount finPurchaseInvoiceDiscount) {
        return finPurchaseInvoiceDiscountMapper.insert(finPurchaseInvoiceDiscount);
    }

    /**
     * 修改采购发票-折扣
     * 
     * @param finPurchaseInvoiceDiscount 采购发票-折扣
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinPurchaseInvoiceDiscount(FinPurchaseInvoiceDiscount finPurchaseInvoiceDiscount) {
        return finPurchaseInvoiceDiscountMapper.updateById(finPurchaseInvoiceDiscount);
    }

    /**
     * 批量删除采购发票-折扣
     * 
     * @param purchaseInvoiceDiscountSids 需要删除的采购发票-折扣ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinPurchaseInvoiceDiscountByIds(List<Long> purchaseInvoiceDiscountSids) {
        return finPurchaseInvoiceDiscountMapper.deleteBatchIds(purchaseInvoiceDiscountSids);
    }


}
