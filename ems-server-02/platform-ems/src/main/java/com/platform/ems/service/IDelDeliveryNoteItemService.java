package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DelDeliveryNoteItem;

/**
 * 交货单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-04-21
 */
public interface IDelDeliveryNoteItemService extends IService<DelDeliveryNoteItem>{
    /**
     * 查询交货单-明细
     * 
     * @param clientId 交货单-明细ID
     * @return 交货单-明细
     */
    public DelDeliveryNoteItem selectDelDeliveryNoteItemById(String clientId);

    /**
     * 查询交货单-明细列表
     * 
     * @param delDeliveryNoteItem 交货单-明细
     * @return 交货单-明细集合
     */
    public List<DelDeliveryNoteItem> selectDelDeliveryNoteItemList(DelDeliveryNoteItem delDeliveryNoteItem);

    /**
     * 新增交货单-明细
     * 
     * @param delDeliveryNoteItem 交货单-明细
     * @return 结果
     */
    public int insertDelDeliveryNoteItem(DelDeliveryNoteItem delDeliveryNoteItem);

    /**
     * 修改交货单-明细
     * 
     * @param delDeliveryNoteItem 交货单-明细
     * @return 结果
     */
    public int updateDelDeliveryNoteItem(DelDeliveryNoteItem delDeliveryNoteItem);

    /**
     * 批量删除交货单-明细
     * 
     * @param clientIds 需要删除的交货单-明细ID
     * @return 结果
     */
    public int deleteDelDeliveryNoteItemByIds(List<String> clientIds);

    /**
     * 采购交货单明细报表
     */
    List<DelDeliveryNoteItem> getDeliveryItemList(DelDeliveryNoteItem delDeliveryNoteItem);

    /**
     * 销售发货单明细报表
     */
    List<DelDeliveryNoteItem> getShipmentsItemList(DelDeliveryNoteItem delDeliveryNoteItem);
    public void handleInoutStatus(List<DelDeliveryNoteItem> list);
}
