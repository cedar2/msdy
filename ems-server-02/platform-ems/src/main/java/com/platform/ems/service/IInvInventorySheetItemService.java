package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvInventorySheetItem;

/**
 * 盘点单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface IInvInventorySheetItemService extends IService<InvInventorySheetItem>{
    /**
     * 查询盘点单-明细
     * 
     * @param inventorySheetItemSid 盘点单-明细ID
     * @return 盘点单-明细
     */
    public List<InvInventorySheetItem> selectInvInventorySheetItemById(Long inventorySheetItemSid);

    /**
     * 查询盘点单-明细列表
     * 
     * @param invInventorySheetItem 盘点单-明细
     * @return 盘点单-明细集合
     */
    public List<InvInventorySheetItem> selectInvInventorySheetItemList(InvInventorySheetItem invInventorySheetItem);

    /**
     * 新增盘点单-明细
     * 
     * @param invInventorySheetItem 盘点单-明细
     * @return 结果
     */
    public int insertInvInventorySheetItem(InvInventorySheetItem invInventorySheetItem);

    /**
     * 修改盘点单-明细
     * 
     * @param invInventorySheetItem 盘点单-明细
     * @return 结果
     */
    public int updateInvInventorySheetItem(InvInventorySheetItem invInventorySheetItem);

    /**
     * 批量删除盘点单-明细
     * 
     * @param inventorySheetItemSids 需要删除的盘点单-明细ID
     * @return 结果
     */
    public int deleteInvInventorySheetItemByIds(List<Long> inventorySheetItemSids);

}
