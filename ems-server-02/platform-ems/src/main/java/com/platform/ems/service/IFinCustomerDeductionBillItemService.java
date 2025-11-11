package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinCustomerDeductionBillItem;

/**
 * 客户扣款单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-06-08
 */
public interface IFinCustomerDeductionBillItemService extends IService<FinCustomerDeductionBillItem>{
    /**
     * 查询客户扣款单-明细
     * 
     * @param deductionBillItemSid 客户扣款单-明细ID
     * @return 客户扣款单-明细
     */
    public FinCustomerDeductionBillItem selectFinCustomerDeductionBillItemById(Long deductionBillItemSid);

    /**
     * 查询客户扣款单-明细列表
     * 
     * @param finCustomerDeductionBillItem 客户扣款单-明细
     * @return 客户扣款单-明细集合
     */
    public List<FinCustomerDeductionBillItem> selectFinCustomerDeductionBillItemList(FinCustomerDeductionBillItem finCustomerDeductionBillItem);



}
