package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinVendorDeductionBillItem;
import com.platform.ems.mapper.FinVendorDeductionBillItemMapper;
import com.platform.ems.service.IFinVendorDeductionBillItemService;
import com.platform.ems.util.MongodbUtil;

/**
 * 供应商扣款单明细报表Service业务层处理
 * 
 * @author qhq
 * @date 2021-05-31
 */
@Service
@SuppressWarnings("all")
public class FinVendorDeductionBillItemServiceImpl extends ServiceImpl<FinVendorDeductionBillItemMapper,FinVendorDeductionBillItem>  implements IFinVendorDeductionBillItemService {
    @Autowired
    private FinVendorDeductionBillItemMapper finVendorDeductionBillItemMapper;

    /**
     * 查询供应商扣款单明细报表
     * 
     * @param deductionBillItemSid 供应商扣款单明细报表ID
     * @return 供应商扣款单明细报表
     */
    @Override
    public FinVendorDeductionBillItem selectFinVendorDeductionBillItemById(Long deductionBillItemSid) {
        FinVendorDeductionBillItem finVendorDeductionBillItem = finVendorDeductionBillItemMapper.selectFinVendorDeductionBillItemById(deductionBillItemSid);
        MongodbUtil.find(finVendorDeductionBillItem);
        return  finVendorDeductionBillItem;
    }

    /**
     * 查询供应商扣款单明细报表列表
     * 
     * @param finVendorDeductionBillItem 供应商扣款单明细报表
     * @return 供应商扣款单明细报表
     */
    @Override
    public List<FinVendorDeductionBillItem> selectFinVendorDeductionBillItemList(FinVendorDeductionBillItem finVendorDeductionBillItem) {
        return finVendorDeductionBillItemMapper.selectFinVendorDeductionBillItemList(finVendorDeductionBillItem);
    }




}
