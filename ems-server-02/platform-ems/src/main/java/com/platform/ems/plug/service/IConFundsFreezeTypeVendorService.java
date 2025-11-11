package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConFundsFreezeTypeVendor;

import java.util.List;

/**
 * 暂押款类型_供应商Service接口
 *
 * @author linhongwei
 * @date 2021-09-25
 */
public interface IConFundsFreezeTypeVendorService extends IService<ConFundsFreezeTypeVendor> {
    /**
     * 查询暂押款类型_供应商
     *
     * @param sid 暂押款类型_供应商ID
     * @return 暂押款类型_供应商
     */
    public ConFundsFreezeTypeVendor selectConFundsFreezeTypeVendorById(Long sid);

    /**
     * 查询暂押款类型_供应商列表
     *
     * @param conFundsFreezeTypeVendor 暂押款类型_供应商
     * @return 暂押款类型_供应商集合
     */
    public List<ConFundsFreezeTypeVendor> selectConFundsFreezeTypeVendorList(ConFundsFreezeTypeVendor conFundsFreezeTypeVendor);

    /**
     * 新增暂押款类型_供应商
     *
     * @param conFundsFreezeTypeVendor 暂押款类型_供应商
     * @return 结果
     */
    public int insertConFundsFreezeTypeVendor(ConFundsFreezeTypeVendor conFundsFreezeTypeVendor);

    /**
     * 修改暂押款类型_供应商
     *
     * @param conFundsFreezeTypeVendor 暂押款类型_供应商
     * @return 结果
     */
    public int updateConFundsFreezeTypeVendor(ConFundsFreezeTypeVendor conFundsFreezeTypeVendor);

    /**
     * 变更暂押款类型_供应商
     *
     * @param conFundsFreezeTypeVendor 暂押款类型_供应商
     * @return 结果
     */
    public int changeConFundsFreezeTypeVendor(ConFundsFreezeTypeVendor conFundsFreezeTypeVendor);

    /**
     * 批量删除暂押款类型_供应商
     *
     * @param sids 需要删除的暂押款类型_供应商ID
     * @return 结果
     */
    public int deleteConFundsFreezeTypeVendorByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conFundsFreezeTypeVendor
     * @return
     */
    int changeStatus(ConFundsFreezeTypeVendor conFundsFreezeTypeVendor);

    /**
     * 更改确认状态
     *
     * @param conFundsFreezeTypeVendor
     * @return
     */
    int check(ConFundsFreezeTypeVendor conFundsFreezeTypeVendor);

    /**
     * 暂押款类型_供应商下拉框列表
     */
    List<ConFundsFreezeTypeVendor> getList(ConFundsFreezeTypeVendor conFundsFreezeTypeVendor);
}
