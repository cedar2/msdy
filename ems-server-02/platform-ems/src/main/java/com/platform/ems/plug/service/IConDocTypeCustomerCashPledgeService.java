package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeCustomerCashPledge;

import java.util.List;

/**
 * 单据类型_客户押金Service接口
 *
 * @author linhongwei
 * @date 2021-09-25
 */
public interface IConDocTypeCustomerCashPledgeService extends IService<ConDocTypeCustomerCashPledge> {
    /**
     * 查询单据类型_客户押金
     *
     * @param sid 单据类型_客户押金ID
     * @return 单据类型_客户押金
     */
    public ConDocTypeCustomerCashPledge selectConDocTypeCustomerCashPledgeById(Long sid);

    /**
     * 查询单据类型_客户押金列表
     *
     * @param conDocTypeCustomerCashPledge 单据类型_客户押金
     * @return 单据类型_客户押金集合
     */
    public List<ConDocTypeCustomerCashPledge> selectConDocTypeCustomerCashPledgeList(ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge);

    /**
     * 新增单据类型_客户押金
     *
     * @param conDocTypeCustomerCashPledge 单据类型_客户押金
     * @return 结果
     */
    public int insertConDocTypeCustomerCashPledge(ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge);

    /**
     * 修改单据类型_客户押金
     *
     * @param conDocTypeCustomerCashPledge 单据类型_客户押金
     * @return 结果
     */
    public int updateConDocTypeCustomerCashPledge(ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge);

    /**
     * 变更单据类型_客户押金
     *
     * @param conDocTypeCustomerCashPledge 单据类型_客户押金
     * @return 结果
     */
    public int changeConDocTypeCustomerCashPledge(ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge);

    /**
     * 批量删除单据类型_客户押金
     *
     * @param sids 需要删除的单据类型_客户押金ID
     * @return 结果
     */
    public int deleteConDocTypeCustomerCashPledgeByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conDocTypeCustomerCashPledge
     * @return
     */
    int changeStatus(ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge);

    /**
     * 更改确认状态
     *
     * @param conDocTypeCustomerCashPledge
     * @return
     */
    int check(ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge);

    /**
     * 单据类型_客户押金下拉框列表
     */
    List<ConDocTypeCustomerCashPledge> getList(ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge);
}
