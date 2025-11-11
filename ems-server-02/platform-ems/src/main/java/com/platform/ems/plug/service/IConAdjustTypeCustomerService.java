package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConAccountCategory;
import com.platform.ems.plug.domain.ConAdjustTypeCustomer;

/**
 * 调账类型_客户Service接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConAdjustTypeCustomerService extends IService<ConAdjustTypeCustomer>{
    /**
     * 查询调账类型_客户
     *
     * @param sid 调账类型_客户ID
     * @return 调账类型_客户
     */
    public ConAdjustTypeCustomer selectConAdjustTypeCustomerById(Long sid);

    /**
     * 查询调账类型_客户列表
     *
     * @param conAdjustTypeCustomer 调账类型_客户
     * @return 调账类型_客户集合
     */
    public List<ConAdjustTypeCustomer> selectConAdjustTypeCustomerList(ConAdjustTypeCustomer conAdjustTypeCustomer);

    /**
     * 新增调账类型_客户
     *
     * @param conAdjustTypeCustomer 调账类型_客户
     * @return 结果
     */
    public int insertConAdjustTypeCustomer(ConAdjustTypeCustomer conAdjustTypeCustomer);

    /**
     * 修改调账类型_客户
     *
     * @param conAdjustTypeCustomer 调账类型_客户
     * @return 结果
     */
    public int updateConAdjustTypeCustomer(ConAdjustTypeCustomer conAdjustTypeCustomer);

    /**
     * 变更调账类型_客户
     *
     * @param conAdjustTypeCustomer 调账类型_客户
     * @return 结果
     */
    public int changeConAdjustTypeCustomer(ConAdjustTypeCustomer conAdjustTypeCustomer);

    /**
     * 批量删除调账类型_客户
     *
     * @param sids 需要删除的调账类型_客户ID
     * @return 结果
     */
    public int deleteConAdjustTypeCustomerByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conAdjustTypeCustomer
    * @return
    */
    int changeStatus(ConAdjustTypeCustomer conAdjustTypeCustomer);

    /**
     * 更改确认状态
     * @param conAdjustTypeCustomer
     * @return
     */
    int check(ConAdjustTypeCustomer conAdjustTypeCustomer);

    /**
     * 款项类别下拉框列表
     */
    List<ConAdjustTypeCustomer> getConAdjustTypeCustomerList();
}
