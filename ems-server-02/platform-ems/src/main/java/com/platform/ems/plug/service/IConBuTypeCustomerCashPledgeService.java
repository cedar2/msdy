package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeCustomerCashPledge;

import java.util.List;

/**
 * 业务类型_客户押金Service接口
 *
 * @author linhongwei
 * @date 2021-09-27
 */
public interface IConBuTypeCustomerCashPledgeService extends IService<ConBuTypeCustomerCashPledge> {
    /**
     * 查询业务类型_客户押金
     *
     * @param sid 业务类型_客户押金ID
     * @return 业务类型_客户押金
     */
    public ConBuTypeCustomerCashPledge selectConBuTypeCustomerCashPledgeById(Long sid);

    /**
     * 查询业务类型_客户押金列表
     *
     * @param conBuTypeCustomerCashPledge 业务类型_客户押金
     * @return 业务类型_客户押金集合
     */
    public List<ConBuTypeCustomerCashPledge> selectConBuTypeCustomerCashPledgeList(ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge);

    /**
     * 新增业务类型_客户押金
     *
     * @param conBuTypeCustomerCashPledge 业务类型_客户押金
     * @return 结果
     */
    public int insertConBuTypeCustomerCashPledge(ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge);

    /**
     * 修改业务类型_客户押金
     *
     * @param conBuTypeCustomerCashPledge 业务类型_客户押金
     * @return 结果
     */
    public int updateConBuTypeCustomerCashPledge(ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge);

    /**
     * 变更业务类型_客户押金
     *
     * @param conBuTypeCustomerCashPledge 业务类型_客户押金
     * @return 结果
     */
    public int changeConBuTypeCustomerCashPledge(ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge);

    /**
     * 批量删除业务类型_客户押金
     *
     * @param sids 需要删除的业务类型_客户押金ID
     * @return 结果
     */
    public int deleteConBuTypeCustomerCashPledgeByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conBuTypeCustomerCashPledge
     * @return
     */
    int changeStatus(ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge);

    /**
     * 更改确认状态
     *
     * @param conBuTypeCustomerCashPledge
     * @return
     */
    int check(ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge);

    /**
     * 业务类型_客户押金下拉框列表
     */
    List<ConBuTypeCustomerCashPledge> getList(ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge);
}
