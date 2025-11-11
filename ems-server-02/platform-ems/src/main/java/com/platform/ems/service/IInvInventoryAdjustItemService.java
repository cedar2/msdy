package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvInventoryAdjustItem;

/**
 * 库存调整单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-04-19
 */
public interface IInvInventoryAdjustItemService extends IService<InvInventoryAdjustItem>{
    /**
     * 查询库存调整单-明细
     * 
     * @param inventoryAdjustItemSid 库存调整单-明细ID
     * @return 库存调整单-明细
     */
    public List<InvInventoryAdjustItem> selectInvInventoryAdjustItemById(Long inventoryAdjustItemSid);

    /**
     * 查询库存调整单-明细列表
     * 
     * @param invInventoryAdjustItem 库存调整单-明细
     * @return 库存调整单-明细集合
     */
    public List<InvInventoryAdjustItem> selectInvInventoryAdjustItemList(InvInventoryAdjustItem invInventoryAdjustItem);

    /**
     * 新增库存调整单-明细
     * 
     * @param invInventoryAdjustItem 库存调整单-明细
     * @return 结果
     */
    public int insertInvInventoryAdjustItem(InvInventoryAdjustItem invInventoryAdjustItem);

    /**
     * 修改库存调整单-明细
     * 
     * @param invInventoryAdjustItem 库存调整单-明细
     * @return 结果
     */
    public int updateInvInventoryAdjustItem(InvInventoryAdjustItem invInventoryAdjustItem);

    /**
     * 批量删除库存调整单-明细
     * 
     * @param inventoryAdjustItemSids 需要删除的库存调整单-明细ID
     * @return 结果
     */
    public int deleteInvInventoryAdjustItemByIds(List<Long> inventoryAdjustItemSids);

}
