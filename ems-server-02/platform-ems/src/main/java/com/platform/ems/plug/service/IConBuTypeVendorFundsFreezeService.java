package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeVendorFundsFreeze;

import java.util.List;

/**
 * 业务类型_供应商暂押款Service接口
 *
 * @author linhongwei
 * @date 2021-09-27
 */
public interface IConBuTypeVendorFundsFreezeService extends IService<ConBuTypeVendorFundsFreeze> {
    /**
     * 查询业务类型_供应商暂押款
     *
     * @param sid 业务类型_供应商暂押款ID
     * @return 业务类型_供应商暂押款
     */
    public ConBuTypeVendorFundsFreeze selectConBuTypeVendorFundsFreezeById(Long sid);

    /**
     * 查询业务类型_供应商暂押款列表
     *
     * @param conBuTypeVendorFundsFreeze 业务类型_供应商暂押款
     * @return 业务类型_供应商暂押款集合
     */
    public List<ConBuTypeVendorFundsFreeze> selectConBuTypeVendorFundsFreezeList(ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze);

    /**
     * 新增业务类型_供应商暂押款
     *
     * @param conBuTypeVendorFundsFreeze 业务类型_供应商暂押款
     * @return 结果
     */
    public int insertConBuTypeVendorFundsFreeze(ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze);

    /**
     * 修改业务类型_供应商暂押款
     *
     * @param conBuTypeVendorFundsFreeze 业务类型_供应商暂押款
     * @return 结果
     */
    public int updateConBuTypeVendorFundsFreeze(ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze);

    /**
     * 变更业务类型_供应商暂押款
     *
     * @param conBuTypeVendorFundsFreeze 业务类型_供应商暂押款
     * @return 结果
     */
    public int changeConBuTypeVendorFundsFreeze(ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze);

    /**
     * 批量删除业务类型_供应商暂押款
     *
     * @param sids 需要删除的业务类型_供应商暂押款ID
     * @return 结果
     */
    public int deleteConBuTypeVendorFundsFreezeByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conBuTypeVendorFundsFreeze
     * @return
     */
    int changeStatus(ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze);

    /**
     * 更改确认状态
     *
     * @param conBuTypeVendorFundsFreeze
     * @return
     */
    int check(ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze);

    /**
     * 业务类型_供应商暂押款下拉框列表
     */
    List<ConBuTypeVendorFundsFreeze> getList(ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze);
}
