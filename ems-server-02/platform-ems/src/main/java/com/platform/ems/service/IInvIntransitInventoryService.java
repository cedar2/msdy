package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvIntransitInventory;

/**
 * 调拨在途库存Service接口
 * 
 * @author linhongwei
 * @date 2021-06-04
 */
public interface IInvIntransitInventoryService extends IService<InvIntransitInventory>{
    /**
     * 查询调拨在途库存
     * 
     * @param intransitStockSid 调拨在途库存ID
     * @return 调拨在途库存
     */
    public InvIntransitInventory selectInvIntransitInventoryById(Long intransitStockSid);

    /**
     * 查询调拨在途库存列表
     * 
     * @param invIntransitInventory 调拨在途库存
     * @return 调拨在途库存集合
     */
    public List<InvIntransitInventory> selectInvIntransitInventoryList(InvIntransitInventory invIntransitInventory);

    /**
     * 新增调拨在途库存
     * 
     * @param invIntransitInventory 调拨在途库存
     * @return 结果
     */
    public int insertInvIntransitInventory(InvIntransitInventory invIntransitInventory);

    /**
     * 修改调拨在途库存
     * 
     * @param invIntransitInventory 调拨在途库存
     * @return 结果
     */
    public int updateInvIntransitInventory(InvIntransitInventory invIntransitInventory);

    /**
     * 变更调拨在途库存
     *
     * @param invIntransitInventory 调拨在途库存
     * @return 结果
     */
    public int changeInvIntransitInventory(InvIntransitInventory invIntransitInventory);

    /**
     * 批量删除调拨在途库存
     * 
     * @param intransitStockSids 需要删除的调拨在途库存ID
     * @return 结果
     */
    public int deleteInvIntransitInventoryByIds(List<Long> intransitStockSids);

    /**
    * 启用/停用
    * @param invIntransitInventory
    * @return
    */
    int changeStatus(InvIntransitInventory invIntransitInventory);

    /**
     * 更改确认状态
     * @param invIntransitInventory
     * @return
     */
    int check(InvIntransitInventory invIntransitInventory);

}
