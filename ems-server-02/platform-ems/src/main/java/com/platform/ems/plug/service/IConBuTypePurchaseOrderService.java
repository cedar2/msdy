package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypePurchaseOrder;
import com.platform.ems.plug.domain.ConDocTypePurchaseOrder;

/**
 * 业务类型_采购订单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypePurchaseOrderService extends IService<ConBuTypePurchaseOrder>{
    /**
     * 查询业务类型_采购订单
     * 
     * @param sid 业务类型_采购订单ID
     * @return 业务类型_采购订单
     */
    public ConBuTypePurchaseOrder selectConBuTypePurchaseOrderById(Long sid);

    /**
     * 查询业务类型_采购订单列表
     * 
     * @param conBuTypePurchaseOrder 业务类型_采购订单
     * @return 业务类型_采购订单集合
     */
    public List<ConBuTypePurchaseOrder> selectConBuTypePurchaseOrderList(ConBuTypePurchaseOrder conBuTypePurchaseOrder);

    /**
     * 新增业务类型_采购订单
     * 
     * @param conBuTypePurchaseOrder 业务类型_采购订单
     * @return 结果
     */
    public int insertConBuTypePurchaseOrder(ConBuTypePurchaseOrder conBuTypePurchaseOrder);

    /**
     * 修改业务类型_采购订单
     * 
     * @param conBuTypePurchaseOrder 业务类型_采购订单
     * @return 结果
     */
    public int updateConBuTypePurchaseOrder(ConBuTypePurchaseOrder conBuTypePurchaseOrder);

    /**
     * 变更业务类型_采购订单
     *
     * @param conBuTypePurchaseOrder 业务类型_采购订单
     * @return 结果
     */
    public int changeConBuTypePurchaseOrder(ConBuTypePurchaseOrder conBuTypePurchaseOrder);

    /**
     * 批量删除业务类型_采购订单
     * 
     * @param sids 需要删除的业务类型_采购订单ID
     * @return 结果
     */
    public int deleteConBuTypePurchaseOrderByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypePurchaseOrder
    * @return
    */
    int changeStatus(ConBuTypePurchaseOrder conBuTypePurchaseOrder);

    /**
     * 更改确认状态
     * @param conBuTypePurchaseOrder
     * @return
     */
    int check(ConBuTypePurchaseOrder conBuTypePurchaseOrder);

    /**
     * 业务类型_采购订单下拉框
     */
    List<ConBuTypePurchaseOrder> getList();

    List<ConBuTypePurchaseOrder> getRelevancyBuList(ConDocTypePurchaseOrder conDocTypePurchaseOrder);
}
