package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeVendorFundsFreeze;

import java.util.List;

/**
 * 单据类型_供应商暂押款Service接口
 *
 * @author linhongwei
 * @date 2021-09-25
 */
public interface IConDocTypeVendorFundsFreezeService extends IService<ConDocTypeVendorFundsFreeze> {
    /**
     * 查询单据类型_供应商暂押款
     *
     * @param sid 单据类型_供应商暂押款ID
     * @return 单据类型_供应商暂押款
     */
    public ConDocTypeVendorFundsFreeze selectConDocTypeVendorFundsFreezeById(Long sid);

    /**
     * 查询单据类型_供应商暂押款列表
     *
     * @param conDocTypeVendorFundsFreeze 单据类型_供应商暂押款
     * @return 单据类型_供应商暂押款集合
     */
    public List<ConDocTypeVendorFundsFreeze> selectConDocTypeVendorFundsFreezeList(ConDocTypeVendorFundsFreeze conDocTypeVendorFundsFreeze);

    /**
     * 新增单据类型_供应商暂押款
     *
     * @param conDocTypeVendorFundsFreeze 单据类型_供应商暂押款
     * @return 结果
     */
    public int insertConDocTypeVendorFundsFreeze(ConDocTypeVendorFundsFreeze conDocTypeVendorFundsFreeze);

    /**
     * 修改单据类型_供应商暂押款
     *
     * @param conDocTypeVendorFundsFreeze 单据类型_供应商暂押款
     * @return 结果
     */
    public int updateConDocTypeVendorFundsFreeze(ConDocTypeVendorFundsFreeze conDocTypeVendorFundsFreeze);

    /**
     * 变更单据类型_供应商暂押款
     *
     * @param conDocTypeVendorFundsFreeze 单据类型_供应商暂押款
     * @return 结果
     */
    public int changeConDocTypeVendorFundsFreeze(ConDocTypeVendorFundsFreeze conDocTypeVendorFundsFreeze);

    /**
     * 批量删除单据类型_供应商暂押款
     *
     * @param sids 需要删除的单据类型_供应商暂押款ID
     * @return 结果
     */
    public int deleteConDocTypeVendorFundsFreezeByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conDocTypeVendorFundsFreeze
     * @return
     */
    int changeStatus(ConDocTypeVendorFundsFreeze conDocTypeVendorFundsFreeze);

    /**
     * 更改确认状态
     *
     * @param conDocTypeVendorFundsFreeze
     * @return
     */
    int check(ConDocTypeVendorFundsFreeze conDocTypeVendorFundsFreeze);

    /**
     * 单据类型_供应商暂押款下拉框列表
     */
    List<ConDocTypeVendorFundsFreeze> getList(ConDocTypeVendorFundsFreeze conDocTypeVendorFundsFreeze);
}
