package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinVendorAccountBalanceBillItem;

/**
 * 供应商账互抵单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-05-27
 */
public interface IFinVendorAccountBalanceBillItemService extends IService<FinVendorAccountBalanceBillItem>{
    /**
     * 查询供应商账互抵单-明细
     * 
     * @param vendorAccountBalanceBillItemSid 供应商账互抵单-明细ID
     * @return 供应商账互抵单-明细
     */
    public FinVendorAccountBalanceBillItem selectFinVendorAccountBalanceBillItemById(Long vendorAccountBalanceBillItemSid);

    /**
     * 查询供应商账互抵单-明细列表
     * 
     * @param finVendorAccountBalanceBillItem 供应商账互抵单-明细
     * @return 供应商账互抵单-明细集合
     */
    public List<FinVendorAccountBalanceBillItem> selectFinVendorAccountBalanceBillItemList(FinVendorAccountBalanceBillItem finVendorAccountBalanceBillItem);


}
