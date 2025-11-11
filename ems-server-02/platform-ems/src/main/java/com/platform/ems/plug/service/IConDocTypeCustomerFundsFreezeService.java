package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeCustomerFundsFreeze;

import java.util.List;

/**
 * 单据类型_客户暂押款Service接口
 *
 * @author linhongwei
 * @date 2021-09-25
 */
public interface IConDocTypeCustomerFundsFreezeService extends IService<ConDocTypeCustomerFundsFreeze> {
    /**
     * 查询单据类型_客户暂押款
     *
     * @param sid 单据类型_客户暂押款ID
     * @return 单据类型_客户暂押款
     */
    public ConDocTypeCustomerFundsFreeze selectConDocTypeCustomerFundsFreezeById(Long sid);

    /**
     * 查询单据类型_客户暂押款列表
     *
     * @param conDocTypeCustomerFundsFreeze 单据类型_客户暂押款
     * @return 单据类型_客户暂押款集合
     */
    public List<ConDocTypeCustomerFundsFreeze> selectConDocTypeCustomerFundsFreezeList(ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze);

    /**
     * 新增单据类型_客户暂押款
     *
     * @param conDocTypeCustomerFundsFreeze 单据类型_客户暂押款
     * @return 结果
     */
    public int insertConDocTypeCustomerFundsFreeze(ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze);

    /**
     * 修改单据类型_客户暂押款
     *
     * @param conDocTypeCustomerFundsFreeze 单据类型_客户暂押款
     * @return 结果
     */
    public int updateConDocTypeCustomerFundsFreeze(ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze);

    /**
     * 变更单据类型_客户暂押款
     *
     * @param conDocTypeCustomerFundsFreeze 单据类型_客户暂押款
     * @return 结果
     */
    public int changeConDocTypeCustomerFundsFreeze(ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze);

    /**
     * 批量删除单据类型_客户暂押款
     *
     * @param sids 需要删除的单据类型_客户暂押款ID
     * @return 结果
     */
    public int deleteConDocTypeCustomerFundsFreezeByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conDocTypeCustomerFundsFreeze
     * @return
     */
    int changeStatus(ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze);

    /**
     * 更改确认状态
     *
     * @param conDocTypeCustomerFundsFreeze
     * @return
     */
    int check(ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze);

    /**
     * 单据类型_客户暂押款下拉框列表
     */
    List<ConDocTypeCustomerFundsFreeze> getList(ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze);
}
