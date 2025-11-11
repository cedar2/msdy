package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConCashPledgeTypeVendor;

import java.util.List;

/**
 * 押金类型_供应商Service接口
 *
 * @author linhongwei
 * @date 2021-09-25
 */
public interface IConCashPledgeTypeVendorService extends IService<ConCashPledgeTypeVendor> {
    /**
     * 查询押金类型_供应商
     *
     * @param sid 押金类型_供应商ID
     * @return 押金类型_供应商
     */
    public ConCashPledgeTypeVendor selectConCashPledgeTypeVendorById(Long sid);

    /**
     * 查询押金类型_供应商列表
     *
     * @param conCashPledgeTypeVendor 押金类型_供应商
     * @return 押金类型_供应商集合
     */
    public List<ConCashPledgeTypeVendor> selectConCashPledgeTypeVendorList(ConCashPledgeTypeVendor conCashPledgeTypeVendor);

    /**
     * 新增押金类型_供应商
     *
     * @param conCashPledgeTypeVendor 押金类型_供应商
     * @return 结果
     */
    public int insertConCashPledgeTypeVendor(ConCashPledgeTypeVendor conCashPledgeTypeVendor);

    /**
     * 修改押金类型_供应商
     *
     * @param conCashPledgeTypeVendor 押金类型_供应商
     * @return 结果
     */
    public int updateConCashPledgeTypeVendor(ConCashPledgeTypeVendor conCashPledgeTypeVendor);

    /**
     * 变更押金类型_供应商
     *
     * @param conCashPledgeTypeVendor 押金类型_供应商
     * @return 结果
     */
    public int changeConCashPledgeTypeVendor(ConCashPledgeTypeVendor conCashPledgeTypeVendor);

    /**
     * 批量删除押金类型_供应商
     *
     * @param sids 需要删除的押金类型_供应商ID
     * @return 结果
     */
    public int deleteConCashPledgeTypeVendorByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conCashPledgeTypeVendor
     * @return
     */
    int changeStatus(ConCashPledgeTypeVendor conCashPledgeTypeVendor);

    /**
     * 更改确认状态
     *
     * @param conCashPledgeTypeVendor
     * @return
     */
    int check(ConCashPledgeTypeVendor conCashPledgeTypeVendor);

    /**
     * 押金类型_供应商下拉框列表
     */
    List<ConCashPledgeTypeVendor> getList(ConCashPledgeTypeVendor conCashPledgeTypeVendor);
}
