package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinVendorDeductionBillItem;

/**
 * 供应商扣款单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-05-31
 */
public interface IFinVendorDeductionBillItemService extends IService<FinVendorDeductionBillItem>{
    /**
     * 查询供应商扣款单-明细
     * 
     * @param deductionBillItemSid 供应商扣款单-明细ID
     * @return 供应商扣款单-明细
     */
    public FinVendorDeductionBillItem selectFinVendorDeductionBillItemById(Long deductionBillItemSid);

    /**
     * 查询供应商扣款单-明细列表
     * 
     * @param finVendorDeductionBillItem 供应商扣款单-明细
     * @return 供应商扣款单-明细集合
     */
    public List<FinVendorDeductionBillItem> selectFinVendorDeductionBillItemList(FinVendorDeductionBillItem finVendorDeductionBillItem);


}
