package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvReserveInventory;

/**
 * 预留库存Service接口
 * 
 * @author linhongwei
 * @date 2022-04-01
 */
public interface IInvReserveInventoryService extends IService<InvReserveInventory>{
    /**
     * 查询预留库存
     * 
     * @param reserveStockSid 预留库存ID
     * @return 预留库存
     */
    public InvReserveInventory selectInvReserveInventoryById(Long reserveStockSid);

    /**
     * 查询预留库存列表
     * 
     * @param invReserveInventory 预留库存
     * @return 预留库存集合
     */
    public List<InvReserveInventory> selectInvReserveInventoryList(InvReserveInventory invReserveInventory);

    /**
     * 新增预留库存
     * 
     * @param invReserveInventory 预留库存
     * @return 结果
     */
    public int insertInvReserveInventory(InvReserveInventory invReserveInventory);

    /**
     * 修改预留库存
     * 
     * @param invReserveInventory 预留库存
     * @return 结果
     */
    public int updateInvReserveInventory(InvReserveInventory invReserveInventory);

    /**
     * 变更预留库存
     *
     * @param invReserveInventory 预留库存
     * @return 结果
     */
    public int changeInvReserveInventory(InvReserveInventory invReserveInventory);

    /**
     * 批量删除预留库存
     * 
     * @param reserveStockSids 需要删除的预留库存ID
     * @return 结果
     */
    public int deleteInvReserveInventoryByIds(List<Long> reserveStockSids);

    /**
    * 启用/停用
    * @param invReserveInventory
    * @return
    */
    int changeStatus(InvReserveInventory invReserveInventory);

    /**
     * 更改确认状态
     * @param invReserveInventory
     * @return
     */
    int check(InvReserveInventory invReserveInventory);

}
