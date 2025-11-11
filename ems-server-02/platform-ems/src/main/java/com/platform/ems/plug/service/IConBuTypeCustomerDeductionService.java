package com.platform.ems.plug.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeCustomerDeduction;

/**
 * 业务类型_客户扣款单Service接口
 *
 * @author chenkw
 * @date 2021-08-03
 */
public interface IConBuTypeCustomerDeductionService extends IService<ConBuTypeCustomerDeduction> {
    /**
     * 查询业务类型_客户扣款单
     *
     * @param sid 业务类型_客户扣款单ID
     * @return 业务类型_客户扣款单
     */
    public ConBuTypeCustomerDeduction selectConBuTypeCustomerDeductionById(Long sid);

    /**
     * 查询业务类型_客户扣款单列表
     *
     * @param conBuTypeCustomerDeduction 业务类型_客户扣款单
     * @return 业务类型_客户扣款单集合
     */
    public List<ConBuTypeCustomerDeduction> selectConBuTypeCustomerDeductionList(ConBuTypeCustomerDeduction conBuTypeCustomerDeduction);

    /**
     * 新增业务类型_客户扣款单
     *
     * @param conBuTypeCustomerDeduction 业务类型_客户扣款单
     * @return 结果
     */
    public int insertConBuTypeCustomerDeduction(ConBuTypeCustomerDeduction conBuTypeCustomerDeduction);

    /**
     * 修改业务类型_客户扣款单
     *
     * @param conBuTypeCustomerDeduction 业务类型_客户扣款单
     * @return 结果
     */
    public int updateConBuTypeCustomerDeduction(ConBuTypeCustomerDeduction conBuTypeCustomerDeduction);

    /**
     * 变更业务类型_客户扣款单
     *
     * @param conBuTypeCustomerDeduction 业务类型_客户扣款单
     * @return 结果
     */
    public int changeConBuTypeCustomerDeduction(ConBuTypeCustomerDeduction conBuTypeCustomerDeduction);

    /**
     * 批量删除业务类型_客户扣款单
     *
     * @param sids 需要删除的业务类型_客户扣款单ID
     * @return 结果
     */
    public int deleteConBuTypeCustomerDeductionByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conBuTypeCustomerDeduction
     * @return
     */
    int changeStatus(ConBuTypeCustomerDeduction conBuTypeCustomerDeduction);

    /**
     * 更改确认状态
     *
     * @param conBuTypeCustomerDeduction
     * @return
     */
    int check(ConBuTypeCustomerDeduction conBuTypeCustomerDeduction);

    /**
     * 下拉框列表
     */
    List<ConBuTypeCustomerDeduction> getConBuTypeCustomerDeductionList();
}
