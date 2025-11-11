package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeVendorCashPledge;

import java.util.List;

/**
 * 业务类型_供应商押金Service接口
 *
 * @author c
 * @date 2021-09-27
 */
public interface IConBuTypeVendorCashPledgeService extends IService<ConBuTypeVendorCashPledge> {
    /**
     * 查询业务类型_供应商押金
     *
     * @param sid 业务类型_供应商押金ID
     * @return 业务类型_供应商押金
     */
    public ConBuTypeVendorCashPledge selectConBuTypeVendorCashPledgeById(Long sid);

    /**
     * 查询业务类型_供应商押金列表
     *
     * @param conBuTypeVendorCashPledge 业务类型_供应商押金
     * @return 业务类型_供应商押金集合
     */
    public List<ConBuTypeVendorCashPledge> selectConBuTypeVendorCashPledgeList(ConBuTypeVendorCashPledge conBuTypeVendorCashPledge);

    /**
     * 新增业务类型_供应商押金
     *
     * @param conBuTypeVendorCashPledge 业务类型_供应商押金
     * @return 结果
     */
    public int insertConBuTypeVendorCashPledge(ConBuTypeVendorCashPledge conBuTypeVendorCashPledge);

    /**
     * 修改业务类型_供应商押金
     *
     * @param conBuTypeVendorCashPledge 业务类型_供应商押金
     * @return 结果
     */
    public int updateConBuTypeVendorCashPledge(ConBuTypeVendorCashPledge conBuTypeVendorCashPledge);

    /**
     * 变更业务类型_供应商押金
     *
     * @param conBuTypeVendorCashPledge 业务类型_供应商押金
     * @return 结果
     */
    public int changeConBuTypeVendorCashPledge(ConBuTypeVendorCashPledge conBuTypeVendorCashPledge);

    /**
     * 批量删除业务类型_供应商押金
     *
     * @param sids 需要删除的业务类型_供应商押金ID
     * @return 结果
     */
    public int deleteConBuTypeVendorCashPledgeByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conBuTypeVendorCashPledge
     * @return
     */
    int changeStatus(ConBuTypeVendorCashPledge conBuTypeVendorCashPledge);

    /**
     * 更改确认状态
     *
     * @param conBuTypeVendorCashPledge
     * @return
     */
    int check(ConBuTypeVendorCashPledge conBuTypeVendorCashPledge);

    /**
     * 业务类型_供应商押金下拉框列表
     */
    List<ConBuTypeVendorCashPledge> getList(ConBuTypeVendorCashPledge conBuTypeVendorCashPledge);
}
