package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeManufactureOrder;

/**
 * 业务类型_生产订单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeManufactureOrderService extends IService<ConBuTypeManufactureOrder>{
    /**
     * 查询业务类型_生产订单
     * 
     * @param sid 业务类型_生产订单ID
     * @return 业务类型_生产订单
     */
    public ConBuTypeManufactureOrder selectConBuTypeManufactureOrderById(Long sid);

    /**
     * 查询业务类型_生产订单列表
     * 
     * @param conBuTypeManufactureOrder 业务类型_生产订单
     * @return 业务类型_生产订单集合
     */
    public List<ConBuTypeManufactureOrder> selectConBuTypeManufactureOrderList(ConBuTypeManufactureOrder conBuTypeManufactureOrder);

    /**
     * 新增业务类型_生产订单
     * 
     * @param conBuTypeManufactureOrder 业务类型_生产订单
     * @return 结果
     */
    public int insertConBuTypeManufactureOrder(ConBuTypeManufactureOrder conBuTypeManufactureOrder);

    /**
     * 修改业务类型_生产订单
     * 
     * @param conBuTypeManufactureOrder 业务类型_生产订单
     * @return 结果
     */
    public int updateConBuTypeManufactureOrder(ConBuTypeManufactureOrder conBuTypeManufactureOrder);

    /**
     * 变更业务类型_生产订单
     *
     * @param conBuTypeManufactureOrder 业务类型_生产订单
     * @return 结果
     */
    public int changeConBuTypeManufactureOrder(ConBuTypeManufactureOrder conBuTypeManufactureOrder);

    /**
     * 批量删除业务类型_生产订单
     * 
     * @param sids 需要删除的业务类型_生产订单ID
     * @return 结果
     */
    public int deleteConBuTypeManufactureOrderByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeManufactureOrder
    * @return
    */
    int changeStatus(ConBuTypeManufactureOrder conBuTypeManufactureOrder);

    /**
     * 更改确认状态
     * @param conBuTypeManufactureOrder
     * @return
     */
    int check(ConBuTypeManufactureOrder conBuTypeManufactureOrder);

}
