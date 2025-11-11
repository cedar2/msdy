package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinVendorFundsFreezeBillItem;

/**
 * 供应商暂押款-明细Service接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface IFinVendorFundsFreezeBillItemService extends IService<FinVendorFundsFreezeBillItem> {
    /**
     * 查询供应商暂押款-明细
     *
     * @param fundsFreezeBillItemSid 供应商暂押款-明细ID
     * @return 供应商暂押款-明细
     */
    public FinVendorFundsFreezeBillItem selectFinVendorFundsFreezeBillItemById(Long fundsFreezeBillItemSid);

    /**
     * 查询供应商暂押款-明细列表
     *
     * @param finVendorFundsFreezeBillItem 供应商暂押款-明细
     * @return 供应商暂押款-明细集合
     */
    public List<FinVendorFundsFreezeBillItem> selectFinVendorFundsFreezeBillItemList(FinVendorFundsFreezeBillItem finVendorFundsFreezeBillItem);

    /**
     * 新增供应商暂押款-明细
     *
     * @param finVendorFundsFreezeBillItem 供应商暂押款-明细
     * @return 结果
     */
    public int insertFinVendorFundsFreezeBillItem(FinVendorFundsFreezeBillItem finVendorFundsFreezeBillItem);

    /**
     * 修改供应商暂押款-明细
     *
     * @param finVendorFundsFreezeBillItem 供应商暂押款-明细
     * @return 结果
     */
    public int updateFinVendorFundsFreezeBillItem(FinVendorFundsFreezeBillItem finVendorFundsFreezeBillItem);

    /**
     * 变更供应商暂押款-明细
     *
     * @param finVendorFundsFreezeBillItem 供应商暂押款-明细
     * @return 结果
     */
    public int changeFinVendorFundsFreezeBillItem(FinVendorFundsFreezeBillItem finVendorFundsFreezeBillItem);

    /**
     * 批量删除供应商暂押款-明细
     *
     * @param fundsFreezeBillItemSids 需要删除的供应商暂押款-明细ID
     * @return 结果
     */
    public int deleteFinVendorFundsFreezeBillItemByIds(List<Long> fundsFreezeBillItemSids);

}
