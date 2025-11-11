package com.platform.ems.plug.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDeductionTypeCustomer;
import com.platform.ems.plug.domain.ConDeductionTypeVendor;

/**
 * 扣款类型_销售Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDeductionTypeCustomerService extends IService<ConDeductionTypeCustomer> {
    /**
     * 查询扣款类型_销售
     *
     * @param sid 扣款类型_销售ID
     * @return 扣款类型_销售
     */
    ConDeductionTypeCustomer selectConDeductionTypeCustomerById(Long sid);

    /**
     * 查询扣款类型_销售列表
     *
     * @param conDeductionTypeCustomer 扣款类型_销售
     * @return 扣款类型_销售集合
     */
    List<ConDeductionTypeCustomer> selectConDeductionTypeCustomerList(ConDeductionTypeCustomer conDeductionTypeCustomer);

    /**
     * 新增扣款类型_销售
     *
     * @param conDeductionTypeCustomer 扣款类型_销售
     * @return 结果
     */
    int insertConDeductionTypeCustomer(ConDeductionTypeCustomer conDeductionTypeCustomer);

    /**
     * 修改扣款类型_销售
     *
     * @param conDeductionTypeCustomer 扣款类型_销售
     * @return 结果
     */
    int updateConDeductionTypeCustomer(ConDeductionTypeCustomer conDeductionTypeCustomer);

    /**
     * 变更扣款类型_销售
     *
     * @param conDeductionTypeCustomer 扣款类型_销售
     * @return 结果
     */
    int changeConDeductionTypeCustomer(ConDeductionTypeCustomer conDeductionTypeCustomer);

    /**
     * 批量删除扣款类型_销售
     *
     * @param sids 需要删除的扣款类型_销售ID
     * @return 结果
     */
    int deleteConDeductionTypeCustomerByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conDeductionTypeCustomer
     * @return
     */
    int changeStatus(ConDeductionTypeCustomer conDeductionTypeCustomer);

    /**
     * 更改确认状态
     *
     * @param conDeductionTypeCustomer
     * @return
     */
    int check(ConDeductionTypeCustomer conDeductionTypeCustomer);


    /**
     * 获取下拉列表
     */
    List<ConDeductionTypeCustomer> getConDeductionTypeCustomerList();
}
