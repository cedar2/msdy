package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.FinPurchaseInvoiceDiscount;
import com.platform.ems.domain.dto.response.financial.FinPurchaseInvoiceDiscountListResponse;
import com.platform.ems.domain.dto.response.form.FinPurchaseInvoiceItemFormResponse;
import com.platform.ems.mapper.FinPurchaseInvoiceDiscountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinPurchaseInvoiceItem;
import com.platform.ems.mapper.FinPurchaseInvoiceItemMapper;
import com.platform.ems.service.IFinPurchaseInvoiceItemService;

/**
 * 采购发票-明细Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
@Service
@SuppressWarnings("all")
public class FinPurchaseInvoiceItemServiceImpl extends ServiceImpl<FinPurchaseInvoiceItemMapper,FinPurchaseInvoiceItem>  implements IFinPurchaseInvoiceItemService {
    @Autowired
    private FinPurchaseInvoiceItemMapper finPurchaseInvoiceItemMapper;
    @Autowired
    private FinPurchaseInvoiceDiscountMapper finPurchaseInvoiceDiscountMapper;

    /**
     * 查询采购发票-明细
     * 
     * @param purchaseInvoiceItemSid 采购发票-明细ID
     * @return 采购发票-明细
     */
    @Override
    public FinPurchaseInvoiceItem selectFinPurchaseInvoiceItemById(Long purchaseInvoiceItemSid) {
        return finPurchaseInvoiceItemMapper.selectFinPurchaseInvoiceItemById(purchaseInvoiceItemSid);
    }

    /**
     * 查询采购发票-明细列表
     * 
     * @param finPurchaseInvoiceItem 采购发票-明细
     * @return 采购发票-明细
     */
    @Override
    public List<FinPurchaseInvoiceItem> selectFinPurchaseInvoiceItemList(FinPurchaseInvoiceItem finPurchaseInvoiceItem) {
        return finPurchaseInvoiceItemMapper.selectFinPurchaseInvoiceItemList(finPurchaseInvoiceItem);
    }

    /**
     * 查询采购发票-明细报表
     *
     * @param finPurchaseInvoiceItem 采购发票-明细
     * @return 采购发票-明细
     */
    @Override
    public List<FinPurchaseInvoiceItem> getReportForm(FinPurchaseInvoiceItem finPurchaseInvoiceItem) {
        List<FinPurchaseInvoiceItem> itemList = finPurchaseInvoiceItemMapper.getReportForm(finPurchaseInvoiceItem);
        return itemList;
    }



}
