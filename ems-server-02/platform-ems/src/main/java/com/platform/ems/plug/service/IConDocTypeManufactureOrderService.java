package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeManufactureOrder;

/**
 * 单据类型_生产订单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypeManufactureOrderService extends IService<ConDocTypeManufactureOrder>{
    /**
     * 查询单据类型_生产订单
     * 
     * @param sid 单据类型_生产订单ID
     * @return 单据类型_生产订单
     */
    public ConDocTypeManufactureOrder selectConDocTypeManufactureOrderById(Long sid);

    /**
     * 查询单据类型_生产订单列表
     * 
     * @param conDocTypeManufactureOrder 单据类型_生产订单
     * @return 单据类型_生产订单集合
     */
    public List<ConDocTypeManufactureOrder> selectConDocTypeManufactureOrderList(ConDocTypeManufactureOrder conDocTypeManufactureOrder);

    /**
     * 新增单据类型_生产订单
     * 
     * @param conDocTypeManufactureOrder 单据类型_生产订单
     * @return 结果
     */
    public int insertConDocTypeManufactureOrder(ConDocTypeManufactureOrder conDocTypeManufactureOrder);

    /**
     * 修改单据类型_生产订单
     * 
     * @param conDocTypeManufactureOrder 单据类型_生产订单
     * @return 结果
     */
    public int updateConDocTypeManufactureOrder(ConDocTypeManufactureOrder conDocTypeManufactureOrder);

    /**
     * 变更单据类型_生产订单
     *
     * @param conDocTypeManufactureOrder 单据类型_生产订单
     * @return 结果
     */
    public int changeConDocTypeManufactureOrder(ConDocTypeManufactureOrder conDocTypeManufactureOrder);

    /**
     * 批量删除单据类型_生产订单
     * 
     * @param sids 需要删除的单据类型_生产订单ID
     * @return 结果
     */
    public int deleteConDocTypeManufactureOrderByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypeManufactureOrder
    * @return
    */
    int changeStatus(ConDocTypeManufactureOrder conDocTypeManufactureOrder);

    /**
     * 更改确认状态
     * @param conDocTypeManufactureOrder
     * @return
     */
    int check(ConDocTypeManufactureOrder conDocTypeManufactureOrder);

    /**
     * 单据类型_生产订单下拉框接口
     */
    List<ConDocTypeManufactureOrder> getList();
}
