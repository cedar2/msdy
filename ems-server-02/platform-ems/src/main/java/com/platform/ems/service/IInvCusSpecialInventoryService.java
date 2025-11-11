package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvCusSpecialInventory;

/**
 * 客户特殊库存（寄售/客供料）Service接口
 * 
 * @author linhongwei
 * @date 2021-06-01
 */
public interface IInvCusSpecialInventoryService extends IService<InvCusSpecialInventory>{
    /**
     * 查询客户特殊库存（寄售/客供料）
     * 
     * @param customerSpecialStockSid 客户特殊库存（寄售/客供料）ID
     * @return 客户特殊库存（寄售/客供料）
     */
    public InvCusSpecialInventory selectInvCusSpecialInventoryById(Long customerSpecialStockSid);

    /**
     * 查询客户特殊库存（寄售/客供料）列表
     * 
     * @param invCusSpecialInventory 客户特殊库存（寄售/客供料）
     * @return 客户特殊库存（寄售/客供料）集合
     */
    public List<InvCusSpecialInventory> selectInvCusSpecialInventoryList(InvCusSpecialInventory invCusSpecialInventory);

    /**
     * 新增客户特殊库存（寄售/客供料）
     * 
     * @param invCusSpecialInventory 客户特殊库存（寄售/客供料）
     * @return 结果
     */
    public int insertInvCusSpecialInventory(InvCusSpecialInventory invCusSpecialInventory);

    /**
     * 修改客户特殊库存（寄售/客供料）
     * 
     * @param invCusSpecialInventory 客户特殊库存（寄售/客供料）
     * @return 结果
     */
    public int updateInvCusSpecialInventory(InvCusSpecialInventory invCusSpecialInventory);

    /**
     * 变更客户特殊库存（寄售/客供料）
     *
     * @param invCusSpecialInventory 客户特殊库存（寄售/客供料）
     * @return 结果
     */
    public int changeInvCusSpecialInventory(InvCusSpecialInventory invCusSpecialInventory);

    /**
     * 批量删除客户特殊库存（寄售/客供料）
     * 
     * @param customerSpecialStockSids 需要删除的客户特殊库存（寄售/客供料）ID
     * @return 结果
     */
    public int deleteInvCusSpecialInventoryByIds(List<Long> customerSpecialStockSids);

    /**
    * 启用/停用
    * @param invCusSpecialInventory
    * @return
    */
    int changeStatus(InvCusSpecialInventory invCusSpecialInventory);

    /**
     * 更改确认状态
     * @param invCusSpecialInventory
     * @return
     */
    int check(InvCusSpecialInventory invCusSpecialInventory);

}
