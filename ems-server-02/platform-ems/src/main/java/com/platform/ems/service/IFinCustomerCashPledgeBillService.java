package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinCustomerCashPledgeBill;

/**
 * 客户押金Service接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface IFinCustomerCashPledgeBillService extends IService<FinCustomerCashPledgeBill> {
    /**
     * 查询客户押金
     *
     * @param cashPledgeBillSid 客户押金ID
     * @return 客户押金
     */
    public FinCustomerCashPledgeBill selectFinCustomerCashPledgeBillById(Long cashPledgeBillSid);

    /**
     * 查询客户押金列表
     *
     * @param finCustomerCashPledgeBill 客户押金
     * @return 客户押金集合
     */
    public List<FinCustomerCashPledgeBill> selectFinCustomerCashPledgeBillList(FinCustomerCashPledgeBill finCustomerCashPledgeBill);

    /**
     * 新增客户押金
     *
     * @param finCustomerCashPledgeBill 客户押金
     * @return 结果
     */
    public int insertFinCustomerCashPledgeBill(FinCustomerCashPledgeBill finCustomerCashPledgeBill);

    /**
     * 修改客户押金
     *
     * @param finCustomerCashPledgeBill 客户押金
     * @return 结果
     */
    public int updateFinCustomerCashPledgeBill(FinCustomerCashPledgeBill finCustomerCashPledgeBill);

    /**
     * 变更客户押金
     *
     * @param finCustomerCashPledgeBill 客户押金
     * @return 结果
     */
    public int changeFinCustomerCashPledgeBill(FinCustomerCashPledgeBill finCustomerCashPledgeBill);

    /**
     * 批量删除客户押金
     *
     * @param cashPledgeBillSids 需要删除的客户押金ID
     * @return 结果
     */
    public int deleteFinCustomerCashPledgeBillByIds(List<Long> cashPledgeBillSids);

    /**
     * 更改确认状态
     *
     * @param finCustomerCashPledgeBill
     * @return
     */
    int check(FinCustomerCashPledgeBill finCustomerCashPledgeBill);


    /**
     * 作废
     *
     * @param cashPledgeBillSid
     * @return
     */
    int invalid(Long cashPledgeBillSid);
}
