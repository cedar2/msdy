package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeVendorCashPledge;

import java.util.List;

/**
 * 单据类型_供应商押金Service接口
 *
 * @author linhongwei
 * @date 2021-09-25
 */
public interface IConDocTypeVendorCashPledgeService extends IService<ConDocTypeVendorCashPledge> {
    /**
     * 查询单据类型_供应商押金
     *
     * @param sid 单据类型_供应商押金ID
     * @return 单据类型_供应商押金
     */
    public ConDocTypeVendorCashPledge selectConDocTypeVendorCashPledgeById(Long sid);

    /**
     * 查询单据类型_供应商押金列表
     *
     * @param conDocTypeVendorCashPledge 单据类型_供应商押金
     * @return 单据类型_供应商押金集合
     */
    public List<ConDocTypeVendorCashPledge> selectConDocTypeVendorCashPledgeList(ConDocTypeVendorCashPledge conDocTypeVendorCashPledge);

    /**
     * 新增单据类型_供应商押金
     *
     * @param conDocTypeVendorCashPledge 单据类型_供应商押金
     * @return 结果
     */
    public int insertConDocTypeVendorCashPledge(ConDocTypeVendorCashPledge conDocTypeVendorCashPledge);

    /**
     * 修改单据类型_供应商押金
     *
     * @param conDocTypeVendorCashPledge 单据类型_供应商押金
     * @return 结果
     */
    public int updateConDocTypeVendorCashPledge(ConDocTypeVendorCashPledge conDocTypeVendorCashPledge);

    /**
     * 变更单据类型_供应商押金
     *
     * @param conDocTypeVendorCashPledge 单据类型_供应商押金
     * @return 结果
     */
    public int changeConDocTypeVendorCashPledge(ConDocTypeVendorCashPledge conDocTypeVendorCashPledge);

    /**
     * 批量删除单据类型_供应商押金
     *
     * @param sids 需要删除的单据类型_供应商押金ID
     * @return 结果
     */
    public int deleteConDocTypeVendorCashPledgeByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conDocTypeVendorCashPledge
     * @return
     */
    int changeStatus(ConDocTypeVendorCashPledge conDocTypeVendorCashPledge);

    /**
     * 更改确认状态
     *
     * @param conDocTypeVendorCashPledge
     * @return
     */
    int check(ConDocTypeVendorCashPledge conDocTypeVendorCashPledge);

    /**
     * 单据类型_供应商押金下拉框列表
     */
    List<ConDocTypeVendorCashPledge> getList(ConDocTypeVendorCashPledge conDocTypeVendorCashPledge);
}
