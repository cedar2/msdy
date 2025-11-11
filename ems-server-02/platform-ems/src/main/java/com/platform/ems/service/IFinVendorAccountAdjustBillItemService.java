package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinVendorAccountAdjustBillItem;

/**
 * 供应商调账单-明细Service接口
 * 
 * @author qhq
 * @date 2021-05-26
 */
public interface IFinVendorAccountAdjustBillItemService extends IService<FinVendorAccountAdjustBillItem>{
    /**
     * 查询供应商调账单-明细
     * 
     * @param adjustBillItemSid 供应商调账单-明细ID
     * @return 供应商调账单-明细
     */
    public FinVendorAccountAdjustBillItem selectFinVendorAccountAdjustBillItemById(Long adjustBillItemSid);

    /**
     * 查询供应商调账单-明细列表
     * 
     * @param finVendorAccountAdjustBillItem 供应商调账单-明细
     * @return 供应商调账单-明细集合
     */
    public List<FinVendorAccountAdjustBillItem> selectFinVendorAccountAdjustBillItemList(FinVendorAccountAdjustBillItem finVendorAccountAdjustBillItem);


}
