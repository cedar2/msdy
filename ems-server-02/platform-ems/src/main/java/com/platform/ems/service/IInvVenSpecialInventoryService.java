package com.platform.ems.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvVenSpecialInventory;

/**
 * 供应商特殊库存（寄售/甲供料）Service接口
 * 
 * @author linhongwei
 * @date 2021-06-01
 */
public interface IInvVenSpecialInventoryService extends IService<InvVenSpecialInventory>{
    /**
     * 查询特殊库存明细报表
     *
     */
    public List<InvVenSpecialInventory> report(InvVenSpecialInventory invVenSpecialInventory);
    /**
     * 查询供应商特殊库存（寄售/甲供料）
     * 
     * @param vendorSpecialStockSid 供应商特殊库存（寄售/甲供料）ID
     * @return 供应商特殊库存（寄售/甲供料）
     */
    public InvVenSpecialInventory selectInvVenSpecialInventoryById(Long vendorSpecialStockSid);

    /**
     * 查询供应商特殊库存（寄售/甲供料）列表
     * 
     * @param invVenSpecialInventory 供应商特殊库存（寄售/甲供料）
     * @return 供应商特殊库存（寄售/甲供料）集合
     */
    public List<InvVenSpecialInventory> selectInvVenSpecialInventoryList(InvVenSpecialInventory invVenSpecialInventory);

   //校验能否添加物料
    public int judgeAdd(List<InvVenSpecialInventory> list);
    /**
     * 新增供应商特殊库存（寄售/甲供料）
     * 
     * @param invVenSpecialInventory 供应商特殊库存（寄售/甲供料）
     * @return 结果
     */
    public int insertInvVenSpecialInventory(InvVenSpecialInventory invVenSpecialInventory);

    /**
     * 修改供应商特殊库存（寄售/甲供料）
     * 
     * @param invVenSpecialInventory 供应商特殊库存（寄售/甲供料）
     * @return 结果
     */
    public int updateInvVenSpecialInventory(InvVenSpecialInventory invVenSpecialInventory);

    /**
     * 变更供应商特殊库存（寄售/甲供料）
     *
     * @param invVenSpecialInventory 供应商特殊库存（寄售/甲供料）
     * @return 结果
     */
    public int changeInvVenSpecialInventory(InvVenSpecialInventory invVenSpecialInventory);

    /**
     * 批量删除供应商特殊库存（寄售/甲供料）
     * 
     * @param vendorSpecialStockSids 需要删除的供应商特殊库存（寄售/甲供料）ID
     * @return 结果
     */
    public int deleteInvVenSpecialInventoryByIds(List<Long> vendorSpecialStockSids);

    /**
    * 启用/停用
    * @param invVenSpecialInventory
    * @return
    */
    int changeStatus(InvVenSpecialInventory invVenSpecialInventory);

    /**
     * 更改确认状态
     * @param invVenSpecialInventory
     * @return
     */
    int check(InvVenSpecialInventory invVenSpecialInventory);

}
