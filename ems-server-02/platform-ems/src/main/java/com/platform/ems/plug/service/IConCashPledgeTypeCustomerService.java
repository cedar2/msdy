package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConCashPledgeTypeCustomer;

import java.util.List;

/**
 * 押金类型_客户Service接口
 *
 * @author linhongwei
 * @date 2021-09-25
 */
public interface IConCashPledgeTypeCustomerService extends IService<ConCashPledgeTypeCustomer> {
    /**
     * 查询押金类型_客户
     *
     * @param sid 押金类型_客户ID
     * @return 押金类型_客户
     */
    public ConCashPledgeTypeCustomer selectConCashPledgeTypeCustomerById(Long sid);

    /**
     * 查询押金类型_客户列表
     *
     * @param conCashPledgeTypeCustomer 押金类型_客户
     * @return 押金类型_客户集合
     */
    public List<ConCashPledgeTypeCustomer> selectConCashPledgeTypeCustomerList(ConCashPledgeTypeCustomer conCashPledgeTypeCustomer);

    /**
     * 新增押金类型_客户
     *
     * @param conCashPledgeTypeCustomer 押金类型_客户
     * @return 结果
     */
    public int insertConCashPledgeTypeCustomer(ConCashPledgeTypeCustomer conCashPledgeTypeCustomer);

    /**
     * 修改押金类型_客户
     *
     * @param conCashPledgeTypeCustomer 押金类型_客户
     * @return 结果
     */
    public int updateConCashPledgeTypeCustomer(ConCashPledgeTypeCustomer conCashPledgeTypeCustomer);

    /**
     * 变更押金类型_客户
     *
     * @param conCashPledgeTypeCustomer 押金类型_客户
     * @return 结果
     */
    public int changeConCashPledgeTypeCustomer(ConCashPledgeTypeCustomer conCashPledgeTypeCustomer);

    /**
     * 批量删除押金类型_客户
     *
     * @param sids 需要删除的押金类型_客户ID
     * @return 结果
     */
    public int deleteConCashPledgeTypeCustomerByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conCashPledgeTypeCustomer
     * @return
     */
    int changeStatus(ConCashPledgeTypeCustomer conCashPledgeTypeCustomer);

    /**
     * 更改确认状态
     *
     * @param conCashPledgeTypeCustomer
     * @return
     */
    int check(ConCashPledgeTypeCustomer conCashPledgeTypeCustomer);

    /**
     * 押金类型_客户下拉框列表
     */
    List<ConCashPledgeTypeCustomer> getList(ConCashPledgeTypeCustomer conCashPledgeTypeCustomer);
}
