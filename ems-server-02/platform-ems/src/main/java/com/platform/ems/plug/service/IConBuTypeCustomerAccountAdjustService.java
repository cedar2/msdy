package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeCustomerAccountAdjust;

/**
 * 业务类型_客户调账单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeCustomerAccountAdjustService extends IService<ConBuTypeCustomerAccountAdjust>{
    /**
     * 查询业务类型_客户调账单
     * 
     * @param sid 业务类型_客户调账单ID
     * @return 业务类型_客户调账单
     */
    public ConBuTypeCustomerAccountAdjust selectConBuTypeCustomerAccountAdjustById(Long sid);

    /**
     * 查询业务类型_客户调账单列表
     * 
     * @param conBuTypeCustomerAccountAdjust 业务类型_客户调账单
     * @return 业务类型_客户调账单集合
     */
    public List<ConBuTypeCustomerAccountAdjust> selectConBuTypeCustomerAccountAdjustList(ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust);

    /**
     * 新增业务类型_客户调账单
     * 
     * @param conBuTypeCustomerAccountAdjust 业务类型_客户调账单
     * @return 结果
     */
    public int insertConBuTypeCustomerAccountAdjust(ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust);

    /**
     * 修改业务类型_客户调账单
     * 
     * @param conBuTypeCustomerAccountAdjust 业务类型_客户调账单
     * @return 结果
     */
    public int updateConBuTypeCustomerAccountAdjust(ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust);

    /**
     * 变更业务类型_客户调账单
     *
     * @param conBuTypeCustomerAccountAdjust 业务类型_客户调账单
     * @return 结果
     */
    public int changeConBuTypeCustomerAccountAdjust(ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust);

    /**
     * 批量删除业务类型_客户调账单
     * 
     * @param sids 需要删除的业务类型_客户调账单ID
     * @return 结果
     */
    public int deleteConBuTypeCustomerAccountAdjustByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeCustomerAccountAdjust
    * @return
    */
    int changeStatus(ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust);

    /**
     * 更改确认状态
     * @param conBuTypeCustomerAccountAdjust
     * @return
     */
    int check(ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust);

}
