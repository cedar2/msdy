package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeCustomerFundsFreeze;

import java.util.List;

/**
 * 业务类型_客户暂押款Service接口
 *
 * @author linhongwei
 * @date 2021-09-27
 */
public interface IConBuTypeCustomerFundsFreezeService extends IService<ConBuTypeCustomerFundsFreeze> {
    /**
     * 查询业务类型_客户暂押款
     *
     * @param sid 业务类型_客户暂押款ID
     * @return 业务类型_客户暂押款
     */
    public ConBuTypeCustomerFundsFreeze selectConBuTypeCustomerFundsFreezeById(Long sid);

    /**
     * 查询业务类型_客户暂押款列表
     *
     * @param conBuTypeCustomerFundsFreeze 业务类型_客户暂押款
     * @return 业务类型_客户暂押款集合
     */
    public List<ConBuTypeCustomerFundsFreeze> selectConBuTypeCustomerFundsFreezeList(ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze);

    /**
     * 新增业务类型_客户暂押款
     *
     * @param conBuTypeCustomerFundsFreeze 业务类型_客户暂押款
     * @return 结果
     */
    public int insertConBuTypeCustomerFundsFreeze(ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze);

    /**
     * 修改业务类型_客户暂押款
     *
     * @param conBuTypeCustomerFundsFreeze 业务类型_客户暂押款
     * @return 结果
     */
    public int updateConBuTypeCustomerFundsFreeze(ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze);

    /**
     * 变更业务类型_客户暂押款
     *
     * @param conBuTypeCustomerFundsFreeze 业务类型_客户暂押款
     * @return 结果
     */
    public int changeConBuTypeCustomerFundsFreeze(ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze);

    /**
     * 批量删除业务类型_客户暂押款
     *
     * @param sids 需要删除的业务类型_客户暂押款ID
     * @return 结果
     */
    public int deleteConBuTypeCustomerFundsFreezeByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conBuTypeCustomerFundsFreeze
     * @return
     */
    int changeStatus(ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze);

    /**
     * 更改确认状态
     *
     * @param conBuTypeCustomerFundsFreeze
     * @return
     */
    int check(ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze);

    /**
     * 业务类型_客户暂押款下拉框列表
     */
    List<ConBuTypeCustomerFundsFreeze> getList(ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze);
}
