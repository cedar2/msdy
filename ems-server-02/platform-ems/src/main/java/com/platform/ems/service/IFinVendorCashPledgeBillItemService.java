package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinVendorCashPledgeBillItem;

/**
 * 供应商押金-明细Service接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface IFinVendorCashPledgeBillItemService extends IService<FinVendorCashPledgeBillItem> {
    /**
     * 查询供应商押金-明细
     *
     * @param cashPledgeBillItemSid 供应商押金-明细ID
     * @return 供应商押金-明细
     */
    public FinVendorCashPledgeBillItem selectFinVendorCashPledgeBillItemById(Long cashPledgeBillItemSid);

    /**
     * 查询供应商押金-明细列表
     *
     * @param finVendorCashPledgeBillItem 供应商押金-明细
     * @return 供应商押金-明细集合
     */
    public List<FinVendorCashPledgeBillItem> selectFinVendorCashPledgeBillItemList(FinVendorCashPledgeBillItem finVendorCashPledgeBillItem);

    /**
     * 新增供应商押金-明细
     *
     * @param finVendorCashPledgeBillItem 供应商押金-明细
     * @return 结果
     */
    public int insertFinVendorCashPledgeBillItem(FinVendorCashPledgeBillItem finVendorCashPledgeBillItem);

    /**
     * 修改供应商押金-明细
     *
     * @param finVendorCashPledgeBillItem 供应商押金-明细
     * @return 结果
     */
    public int updateFinVendorCashPledgeBillItem(FinVendorCashPledgeBillItem finVendorCashPledgeBillItem);

    /**
     * 变更供应商押金-明细
     *
     * @param finVendorCashPledgeBillItem 供应商押金-明细
     * @return 结果
     */
    public int changeFinVendorCashPledgeBillItem(FinVendorCashPledgeBillItem finVendorCashPledgeBillItem);

    /**
     * 批量删除供应商押金-明细
     *
     * @param cashPledgeBillItemSids 需要删除的供应商押金-明细ID
     * @return 结果
     */
    public int deleteFinVendorCashPledgeBillItemByIds(List<Long> cashPledgeBillItemSids);

}