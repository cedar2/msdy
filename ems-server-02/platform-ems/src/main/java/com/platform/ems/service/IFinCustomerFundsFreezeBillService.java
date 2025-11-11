package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinCustomerFundsFreezeBill;

/**
 * 客户暂押款Service接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface IFinCustomerFundsFreezeBillService extends IService<FinCustomerFundsFreezeBill> {
    /**
     * 查询客户暂押款
     *
     * @param fundsFreezeBillSid 客户暂押款ID
     * @return 客户暂押款
     */
    public FinCustomerFundsFreezeBill selectFinCustomerFundsFreezeBillById(Long fundsFreezeBillSid);

    /**
     * 查询客户暂押款列表
     *
     * @param finCustomerFundsFreezeBill 客户暂押款
     * @return 客户暂押款集合
     */
    public List<FinCustomerFundsFreezeBill> selectFinCustomerFundsFreezeBillList(FinCustomerFundsFreezeBill finCustomerFundsFreezeBill);

    /**
     * 新增客户暂押款
     *
     * @param finCustomerFundsFreezeBill 客户暂押款
     * @return 结果
     */
    public int insertFinCustomerFundsFreezeBill(FinCustomerFundsFreezeBill finCustomerFundsFreezeBill);

    /**
     * 修改客户暂押款
     *
     * @param finCustomerFundsFreezeBill 客户暂押款
     * @return 结果
     */
    public int updateFinCustomerFundsFreezeBill(FinCustomerFundsFreezeBill finCustomerFundsFreezeBill);

    /**
     * 变更客户暂押款
     *
     * @param finCustomerFundsFreezeBill 客户暂押款
     * @return 结果
     */
    public int changeFinCustomerFundsFreezeBill(FinCustomerFundsFreezeBill finCustomerFundsFreezeBill);

    /**
     * 批量删除客户暂押款
     *
     * @param fundsFreezeBillSids 需要删除的客户暂押款ID
     * @return 结果
     */
    public int deleteFinCustomerFundsFreezeBillByIds(List<Long> fundsFreezeBillSids);

    /**
     * 更改确认状态
     *
     * @param finCustomerFundsFreezeBill
     * @return
     */
    int check(FinCustomerFundsFreezeBill finCustomerFundsFreezeBill);

    /**
     * 作废
     *
     * @param fundsFreezeBillSid
     * @return
     */
    int invalid(Long fundsFreezeBillSid);
}
