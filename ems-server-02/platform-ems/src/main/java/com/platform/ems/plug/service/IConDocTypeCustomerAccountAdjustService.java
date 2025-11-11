package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeCustomerAccountAdjust;

/**
 * 单据类型_客户调账单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypeCustomerAccountAdjustService extends IService<ConDocTypeCustomerAccountAdjust>{
    /**
     * 查询单据类型_客户调账单
     * 
     * @param sid 单据类型_客户调账单ID
     * @return 单据类型_客户调账单
     */
    public ConDocTypeCustomerAccountAdjust selectConDocTypeCustomerAccountAdjustById(Long sid);

    /**
     * 查询单据类型_客户调账单列表
     * 
     * @param conDocTypeCustomerAccountAdjust 单据类型_客户调账单
     * @return 单据类型_客户调账单集合
     */
    public List<ConDocTypeCustomerAccountAdjust> selectConDocTypeCustomerAccountAdjustList(ConDocTypeCustomerAccountAdjust conDocTypeCustomerAccountAdjust);

    /**
     * 新增单据类型_客户调账单
     * 
     * @param conDocTypeCustomerAccountAdjust 单据类型_客户调账单
     * @return 结果
     */
    public int insertConDocTypeCustomerAccountAdjust(ConDocTypeCustomerAccountAdjust conDocTypeCustomerAccountAdjust);

    /**
     * 修改单据类型_客户调账单
     * 
     * @param conDocTypeCustomerAccountAdjust 单据类型_客户调账单
     * @return 结果
     */
    public int updateConDocTypeCustomerAccountAdjust(ConDocTypeCustomerAccountAdjust conDocTypeCustomerAccountAdjust);

    /**
     * 变更单据类型_客户调账单
     *
     * @param conDocTypeCustomerAccountAdjust 单据类型_客户调账单
     * @return 结果
     */
    public int changeConDocTypeCustomerAccountAdjust(ConDocTypeCustomerAccountAdjust conDocTypeCustomerAccountAdjust);

    /**
     * 批量删除单据类型_客户调账单
     * 
     * @param sids 需要删除的单据类型_客户调账单ID
     * @return 结果
     */
    public int deleteConDocTypeCustomerAccountAdjustByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypeCustomerAccountAdjust
    * @return
    */
    int changeStatus(ConDocTypeCustomerAccountAdjust conDocTypeCustomerAccountAdjust);

    /**
     * 更改确认状态
     * @param conDocTypeCustomerAccountAdjust
     * @return
     */
    int check(ConDocTypeCustomerAccountAdjust conDocTypeCustomerAccountAdjust);

}
