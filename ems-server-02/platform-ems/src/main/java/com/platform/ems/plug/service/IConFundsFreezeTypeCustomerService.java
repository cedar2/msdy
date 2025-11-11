package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConFundsFreezeTypeCustomer;

import java.util.List;

/**
 * 暂押款类型_客户Service接口
 *
 * @author linhongwei
 * @date 2021-09-25
 */
public interface IConFundsFreezeTypeCustomerService extends IService<ConFundsFreezeTypeCustomer> {
    /**
     * 查询暂押款类型_客户
     *
     * @param sid 暂押款类型_客户ID
     * @return 暂押款类型_客户
     */
    public ConFundsFreezeTypeCustomer selectConFundsFreezeTypeCustomerById(Long sid);

    /**
     * 查询暂押款类型_客户列表
     *
     * @param conFundsFreezeTypeCustomer 暂押款类型_客户
     * @return 暂押款类型_客户集合
     */
    public List<ConFundsFreezeTypeCustomer> selectConFundsFreezeTypeCustomerList(ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer);

    /**
     * 新增暂押款类型_客户
     *
     * @param conFundsFreezeTypeCustomer 暂押款类型_客户
     * @return 结果
     */
    public int insertConFundsFreezeTypeCustomer(ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer);

    /**
     * 修改暂押款类型_客户
     *
     * @param conFundsFreezeTypeCustomer 暂押款类型_客户
     * @return 结果
     */
    public int updateConFundsFreezeTypeCustomer(ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer);

    /**
     * 变更暂押款类型_客户
     *
     * @param conFundsFreezeTypeCustomer 暂押款类型_客户
     * @return 结果
     */
    public int changeConFundsFreezeTypeCustomer(ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer);

    /**
     * 批量删除暂押款类型_客户
     *
     * @param sids 需要删除的暂押款类型_客户ID
     * @return 结果
     */
    public int deleteConFundsFreezeTypeCustomerByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conFundsFreezeTypeCustomer
     * @return
     */
    int changeStatus(ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer);

    /**
     * 更改确认状态
     *
     * @param conFundsFreezeTypeCustomer
     * @return
     */
    int check(ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer);

    /**
     * 暂押款类型_客户下拉框列表
     */
    List<ConFundsFreezeTypeCustomer> getList(ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer);
}
