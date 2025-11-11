package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinCustomerAccountBalanceBillItem;

/**
 * 客户账互抵单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-05-27
 */
public interface IFinCustomerAccountBalanceBillItemService extends IService<FinCustomerAccountBalanceBillItem>{
    /**
     * 查询客户账互抵单-明细
     * 
     * @param customerAccountBalanceBillItemSid 客户账互抵单-明细ID
     * @return 客户账互抵单-明细
     */
    public FinCustomerAccountBalanceBillItem selectFinCustomerAccountBalanceBillItemById(Long customerAccountBalanceBillItemSid);

    /**
     * 查询客户账互抵单-明细列表
     * 
     * @param finCustomerAccountBalanceBillItem 客户账互抵单-明细
     * @return 客户账互抵单-明细集合
     */
    public List<FinCustomerAccountBalanceBillItem> selectFinCustomerAccountBalanceBillItemList(FinCustomerAccountBalanceBillItem finCustomerAccountBalanceBillItem);


}
