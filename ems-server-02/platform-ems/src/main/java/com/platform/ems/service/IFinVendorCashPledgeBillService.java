package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinVendorCashPledgeBill;

/**
 * 供应商押金Service接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface IFinVendorCashPledgeBillService extends IService<FinVendorCashPledgeBill> {
    /**
     * 查询供应商押金
     *
     * @param cashPledgeBillSid 供应商押金ID
     * @return 供应商押金
     */
    public FinVendorCashPledgeBill selectFinVendorCashPledgeBillById(Long cashPledgeBillSid);

    /**
     * 查询供应商押金列表
     *
     * @param finVendorCashPledgeBill 供应商押金
     * @return 供应商押金集合
     */
    public List<FinVendorCashPledgeBill> selectFinVendorCashPledgeBillList(FinVendorCashPledgeBill finVendorCashPledgeBill);

    /**
     * 新增供应商押金
     *
     * @param finVendorCashPledgeBill 供应商押金
     * @return 结果
     */
    public int insertFinVendorCashPledgeBill(FinVendorCashPledgeBill finVendorCashPledgeBill);

    /**
     * 修改供应商押金
     *
     * @param finVendorCashPledgeBill 供应商押金
     * @return 结果
     */
    public int updateFinVendorCashPledgeBill(FinVendorCashPledgeBill finVendorCashPledgeBill);

    /**
     * 变更供应商押金
     *
     * @param finVendorCashPledgeBill 供应商押金
     * @return 结果
     */
    public int changeFinVendorCashPledgeBill(FinVendorCashPledgeBill finVendorCashPledgeBill);

    /**
     * 批量删除供应商押金
     *
     * @param cashPledgeBillSids 需要删除的供应商押金ID
     * @return 结果
     */
    public int deleteFinVendorCashPledgeBillByIds(List<Long> cashPledgeBillSids);

    /**
     * 更改确认状态
     *
     * @param finVendorCashPledgeBill
     * @return
     */
    int check(FinVendorCashPledgeBill finVendorCashPledgeBill);


    /**
     * 作废
     *
     * @param cashPledgeBillSid
     * @return
     */
    int invalid(Long cashPledgeBillSid);
}
