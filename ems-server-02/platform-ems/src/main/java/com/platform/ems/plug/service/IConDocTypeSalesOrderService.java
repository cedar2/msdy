package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeSalesOrder;
import com.platform.ems.plug.domain.ConDocTypeSalesOrder;

/**
 * 单据类型_销售订单Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypeSalesOrderService extends IService<ConDocTypeSalesOrder>{
    /**
     * 查询单据类型_销售订单
     *
     * @param sid 单据类型_销售订单ID
     * @return 单据类型_销售订单
     */
    public ConDocTypeSalesOrder selectConDocTypeSalesOrderById(Long sid);

    /**
     * 查询单据类型_销售订单列表
     *
     * @param conDocTypeSalesOrder 单据类型_销售订单
     * @return 单据类型_销售订单集合
     */
    public List<ConDocTypeSalesOrder> selectConDocTypeSalesOrderList(ConDocTypeSalesOrder conDocTypeSalesOrder);

    /**
     * 新增单据类型_销售订单
     *
     * @param conDocTypeSalesOrder 单据类型_销售订单
     * @return 结果
     */
    public int insertConDocTypeSalesOrder(ConDocTypeSalesOrder conDocTypeSalesOrder);

    /**
     * 修改单据类型_销售订单
     *
     * @param conDocTypeSalesOrder 单据类型_销售订单
     * @return 结果
     */
    public int updateConDocTypeSalesOrder(ConDocTypeSalesOrder conDocTypeSalesOrder);

    /**
     * 变更单据类型_销售订单
     *
     * @param conDocTypeSalesOrder 单据类型_销售订单
     * @return 结果
     */
    public int changeConDocTypeSalesOrder(ConDocTypeSalesOrder conDocTypeSalesOrder);

    /**
     * 批量删除单据类型_销售订单
     *
     * @param sids 需要删除的单据类型_销售订单ID
     * @return 结果
     */
    public int deleteConDocTypeSalesOrderByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypeSalesOrder
    * @return
    */
    int changeStatus(ConDocTypeSalesOrder conDocTypeSalesOrder);

    /**
     * 更改确认状态
     * @param conDocTypeSalesOrder
     * @return
     */
    int check(ConDocTypeSalesOrder conDocTypeSalesOrder);

    /**  获取下拉列表 */
    List<ConDocTypeSalesOrder> getConDocTypeSalesOrderList();

    List<ConDocTypeSalesOrder> getList(ConDocTypeSalesOrder conDocTypeSalesOrder);
}
