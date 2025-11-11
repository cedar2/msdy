package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DelOutsourceDeliveryNoteItem;

/**
 * 外发加工收货单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-05-17
 */
public interface IDelOutsourceDeliveryNoteItemService extends IService<DelOutsourceDeliveryNoteItem>{
    /**
     * 查询外发加工收货单-明细
     * 
     * @param deliveryNoteItemSid 外发加工收货单-明细ID
     * @return 外发加工收货单-明细
     */
    public DelOutsourceDeliveryNoteItem selectDelOutsourceDeliveryNoteItemById(Long deliveryNoteItemSid);

    /**
     * 查询外发加工收货单-明细列表
     * 
     * @param delOutsourceDeliveryNoteItem 外发加工收货单-明细
     * @return 外发加工收货单-明细集合
     */
    public List<DelOutsourceDeliveryNoteItem> selectDelOutsourceDeliveryNoteItemList(DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem);

    /**
     * 新增外发加工收货单-明细
     * 
     * @param delOutsourceDeliveryNoteItem 外发加工收货单-明细
     * @return 结果
     */
    public int insertDelOutsourceDeliveryNoteItem(DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem);

    /**
     * 修改外发加工收货单-明细
     * 
     * @param delOutsourceDeliveryNoteItem 外发加工收货单-明细
     * @return 结果
     */
    public int updateDelOutsourceDeliveryNoteItem(DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem);

    /**
     * 变更外发加工收货单-明细
     *
     * @param delOutsourceDeliveryNoteItem 外发加工收货单-明细
     * @return 结果
     */
    public int changeDelOutsourceDeliveryNoteItem(DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem);

    /**
     * 批量删除外发加工收货单-明细
     * 
     * @param deliveryNoteItemSids 需要删除的外发加工收货单-明细ID
     * @return 结果
     */
    public int deleteDelOutsourceDeliveryNoteItemByIds(List<Long> deliveryNoteItemSids);

    /**
     * 更改确认状态
     * @param delOutsourceDeliveryNoteItem
     * @return
     */
    int check(DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem);

    /**
     * 外发加工收货单明细报表
     */
    List<DelOutsourceDeliveryNoteItem> getItemList(DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem);
}
