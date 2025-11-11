package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeSalesOrder;
import com.platform.ems.plug.domain.ConDocTypeSalesOrder;

/**
 * 业务类型_销售订单Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeSalesOrderService extends IService<ConBuTypeSalesOrder>{
    /**
     * 查询业务类型_销售订单
     *
     * @param sid 业务类型_销售订单ID
     * @return 业务类型_销售订单
     */
    public ConBuTypeSalesOrder selectConBuTypeSalesOrderById(Long sid);

    /**
     * 查询业务类型_销售订单列表
     *
     * @param conBuTypeSalesOrder 业务类型_销售订单
     * @return 业务类型_销售订单集合
     */
    public List<ConBuTypeSalesOrder> selectConBuTypeSalesOrderList(ConBuTypeSalesOrder conBuTypeSalesOrder);

    /**
     * 新增业务类型_销售订单
     *
     * @param conBuTypeSalesOrder 业务类型_销售订单
     * @return 结果
     */
    public int insertConBuTypeSalesOrder(ConBuTypeSalesOrder conBuTypeSalesOrder);

    /**
     * 修改业务类型_销售订单
     *
     * @param conBuTypeSalesOrder 业务类型_销售订单
     * @return 结果
     */
    public int updateConBuTypeSalesOrder(ConBuTypeSalesOrder conBuTypeSalesOrder);

    /**
     * 变更业务类型_销售订单
     *
     * @param conBuTypeSalesOrder 业务类型_销售订单
     * @return 结果
     */
    public int changeConBuTypeSalesOrder(ConBuTypeSalesOrder conBuTypeSalesOrder);

    /**
     * 批量删除业务类型_销售订单
     *
     * @param sids 需要删除的业务类型_销售订单ID
     * @return 结果
     */
    public int deleteConBuTypeSalesOrderByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeSalesOrder
    * @return
    */
    int changeStatus(ConBuTypeSalesOrder conBuTypeSalesOrder);

    /**
     * 更改确认状态
     * @param conBuTypeSalesOrder
     * @return
     */
    int check(ConBuTypeSalesOrder conBuTypeSalesOrder);

    /**  获取下拉列表 */
    List<ConBuTypeSalesOrder> getConBuTypeSalesOrderList();

    /**  根据单据类型获取关联业务类型 */
    List<ConBuTypeSalesOrder> getRelevancyBuList(ConDocTypeSalesOrder conDocTypeSalesOrder);
}
