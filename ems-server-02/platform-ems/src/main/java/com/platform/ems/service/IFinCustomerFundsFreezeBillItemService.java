package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinCustomerFundsFreezeBillItem;

/**
 * 客户暂押款-明细Service接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface IFinCustomerFundsFreezeBillItemService extends IService<FinCustomerFundsFreezeBillItem> {
    /**
     * 查询客户暂押款-明细
     *
     * @param fundsFreezeBillItemSid 客户暂押款-明细ID
     * @return 客户暂押款-明细
     */
    public FinCustomerFundsFreezeBillItem selectFinCustomerFundsFreezeBillItemById(Long fundsFreezeBillItemSid);

    /**
     * 查询客户暂押款-明细列表
     *
     * @param finCustomerFundsFreezeBillItem 客户暂押款-明细
     * @return 客户暂押款-明细集合
     */
    public List<FinCustomerFundsFreezeBillItem> selectFinCustomerFundsFreezeBillItemList(FinCustomerFundsFreezeBillItem finCustomerFundsFreezeBillItem);

    /**
     * 新增客户暂押款-明细
     *
     * @param finCustomerFundsFreezeBillItem 客户暂押款-明细
     * @return 结果
     */
    public int insertFinCustomerFundsFreezeBillItem(FinCustomerFundsFreezeBillItem finCustomerFundsFreezeBillItem);

    /**
     * 修改客户暂押款-明细
     *
     * @param finCustomerFundsFreezeBillItem 客户暂押款-明细
     * @return 结果
     */
    public int updateFinCustomerFundsFreezeBillItem(FinCustomerFundsFreezeBillItem finCustomerFundsFreezeBillItem);

    /**
     * 变更客户暂押款-明细
     *
     * @param finCustomerFundsFreezeBillItem 客户暂押款-明细
     * @return 结果
     */
    public int changeFinCustomerFundsFreezeBillItem(FinCustomerFundsFreezeBillItem finCustomerFundsFreezeBillItem);

    /**
     * 批量删除客户暂押款-明细
     *
     * @param fundsFreezeBillItemSids 需要删除的客户暂押款-明细ID
     * @return 结果
     */
    public int deleteFinCustomerFundsFreezeBillItemByIds(List<Long> fundsFreezeBillItemSids);

}
