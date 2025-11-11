package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypePurchaseOrder;

/**
 * 单据类型_采购订单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypePurchaseOrderService extends IService<ConDocTypePurchaseOrder>{
    /**
     * 查询单据类型_采购订单
     * 
     * @param sid 单据类型_采购订单ID
     * @return 单据类型_采购订单
     */
    public ConDocTypePurchaseOrder selectConDocTypePurchaseOrderById(Long sid);

    /**
     * 查询单据类型_采购订单列表
     * 
     * @param conDocTypePurchaseOrder 单据类型_采购订单
     * @return 单据类型_采购订单集合
     */
    public List<ConDocTypePurchaseOrder> selectConDocTypePurchaseOrderList(ConDocTypePurchaseOrder conDocTypePurchaseOrder);

    /**
     * 新增单据类型_采购订单
     * 
     * @param conDocTypePurchaseOrder 单据类型_采购订单
     * @return 结果
     */
    public int insertConDocTypePurchaseOrder(ConDocTypePurchaseOrder conDocTypePurchaseOrder);

    /**
     * 修改单据类型_采购订单
     * 
     * @param conDocTypePurchaseOrder 单据类型_采购订单
     * @return 结果
     */
    public int updateConDocTypePurchaseOrder(ConDocTypePurchaseOrder conDocTypePurchaseOrder);

    /**
     * 变更单据类型_采购订单
     *
     * @param conDocTypePurchaseOrder 单据类型_采购订单
     * @return 结果
     */
    public int changeConDocTypePurchaseOrder(ConDocTypePurchaseOrder conDocTypePurchaseOrder);

    /**
     * 批量删除单据类型_采购订单
     * 
     * @param sids 需要删除的单据类型_采购订单ID
     * @return 结果
     */
    public int deleteConDocTypePurchaseOrderByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypePurchaseOrder
    * @return
    */
    int changeStatus(ConDocTypePurchaseOrder conDocTypePurchaseOrder);

    /**
     * 更改确认状态
     * @param conDocTypePurchaseOrder
     * @return
     */
    int check(ConDocTypePurchaseOrder conDocTypePurchaseOrder);

    /**
     * 单据类型_采购订单下拉框
     */
    List<ConDocTypePurchaseOrder> getList();

    /**
     * 单据类型_采购订单下拉框 有参数
     */
    List<ConDocTypePurchaseOrder> getDocList(ConDocTypePurchaseOrder conDocTypePurchaseOrder);
}
