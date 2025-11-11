package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinCustomerAccountAdjustBillItem;

/**
 * 客户调账单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-05-26
 */
public interface IFinCustomerAccountAdjustBillItemService extends IService<FinCustomerAccountAdjustBillItem>{
    /**
     * 查询客户调账单-明细
     * 
     * @param adjustBillItemSid 客户调账单-明细ID
     * @return 客户调账单-明细
     */
    public FinCustomerAccountAdjustBillItem selectFinCustomerAccountAdjustBillItemById(Long adjustBillItemSid);

    /**
     * 查询客户调账单-明细列表
     * 
     * @param finCustomerAccountAdjustBillItem 客户调账单-明细
     * @return 客户调账单-明细集合
     */
    public List<FinCustomerAccountAdjustBillItem> selectFinCustomerAccountAdjustBillItemList(FinCustomerAccountAdjustBillItem finCustomerAccountAdjustBillItem);


}
