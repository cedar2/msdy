package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinCustomerCashPledgeBillItem;

/**
 * 客户押金-明细Service接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface IFinCustomerCashPledgeBillItemService extends IService<FinCustomerCashPledgeBillItem> {
    /**
     * 查询客户押金-明细
     *
     * @param cashPledgeBillItemSid 客户押金-明细ID
     * @return 客户押金-明细
     */
    public FinCustomerCashPledgeBillItem selectFinCustomerCashPledgeBillItemById(Long cashPledgeBillItemSid);

    /**
     * 查询客户押金-明细列表
     *
     * @param finCustomerCashPledgeBillItem 客户押金-明细
     * @return 客户押金-明细集合
     */
    public List<FinCustomerCashPledgeBillItem> selectFinCustomerCashPledgeBillItemList(FinCustomerCashPledgeBillItem finCustomerCashPledgeBillItem);

    /**
     * 新增客户押金-明细
     *
     * @param finCustomerCashPledgeBillItem 客户押金-明细
     * @return 结果
     */
    public int insertFinCustomerCashPledgeBillItem(FinCustomerCashPledgeBillItem finCustomerCashPledgeBillItem);

    /**
     * 修改客户押金-明细
     *
     * @param finCustomerCashPledgeBillItem 客户押金-明细
     * @return 结果
     */
    public int updateFinCustomerCashPledgeBillItem(FinCustomerCashPledgeBillItem finCustomerCashPledgeBillItem);

    /**
     * 变更客户押金-明细
     *
     * @param finCustomerCashPledgeBillItem 客户押金-明细
     * @return 结果
     */
    public int changeFinCustomerCashPledgeBillItem(FinCustomerCashPledgeBillItem finCustomerCashPledgeBillItem);

    /**
     * 批量删除客户押金-明细
     *
     * @param cashPledgeBillItemSids 需要删除的客户押金-明细ID
     * @return 结果
     */
    public int deleteFinCustomerCashPledgeBillItemByIds(List<Long> cashPledgeBillItemSids);

}
