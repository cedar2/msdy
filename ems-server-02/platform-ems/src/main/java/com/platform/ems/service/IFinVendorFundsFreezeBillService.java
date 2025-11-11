package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinVendorFundsFreezeBill;

/**
 * 供应商暂押款Service接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface IFinVendorFundsFreezeBillService extends IService<FinVendorFundsFreezeBill> {
    /**
     * 查询供应商暂押款
     *
     * @param fundsFreezeBillSid 供应商暂押款ID
     * @return 供应商暂押款
     */
    public FinVendorFundsFreezeBill selectFinVendorFundsFreezeBillById(Long fundsFreezeBillSid);

    /**
     * 查询供应商暂押款列表
     *
     * @param finVendorFundsFreezeBill 供应商暂押款
     * @return 供应商暂押款集合
     */
    public List<FinVendorFundsFreezeBill> selectFinVendorFundsFreezeBillList(FinVendorFundsFreezeBill finVendorFundsFreezeBill);

    /**
     * 新增供应商暂押款
     *
     * @param finVendorFundsFreezeBill 供应商暂押款
     * @return 结果
     */
    public int insertFinVendorFundsFreezeBill(FinVendorFundsFreezeBill finVendorFundsFreezeBill);

    /**
     * 修改供应商暂押款
     *
     * @param finVendorFundsFreezeBill 供应商暂押款
     * @return 结果
     */
    public int updateFinVendorFundsFreezeBill(FinVendorFundsFreezeBill finVendorFundsFreezeBill);

    /**
     * 变更供应商暂押款
     *
     * @param finVendorFundsFreezeBill 供应商暂押款
     * @return 结果
     */
    public int changeFinVendorFundsFreezeBill(FinVendorFundsFreezeBill finVendorFundsFreezeBill);

    /**
     * 批量删除供应商暂押款
     *
     * @param fundsFreezeBillSids 需要删除的供应商暂押款ID
     * @return 结果
     */
    public int deleteFinVendorFundsFreezeBillByIds(List<Long> fundsFreezeBillSids);

    /**
     * 更改确认状态
     *
     * @param finVendorFundsFreezeBill
     * @return
     */
    int check(FinVendorFundsFreezeBill finVendorFundsFreezeBill);

    /**
     * 作废
     *
     * @param fundsFreezeBillSid
     * @return
     */
    int invalid(Long fundsFreezeBillSid);
}
