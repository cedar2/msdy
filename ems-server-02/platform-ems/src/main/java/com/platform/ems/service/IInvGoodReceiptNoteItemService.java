package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvGoodReceiptNoteItem;

/**
 * 收货单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-06-01
 */
public interface IInvGoodReceiptNoteItemService extends IService<InvGoodReceiptNoteItem>{
    /**
     * 查询收货单-明细
     * 
     * @param goodReceiptNoteItemSid 收货单-明细ID
     * @return 收货单-明细
     */
    public InvGoodReceiptNoteItem selectInvGoodReceiptNoteItemById(Long goodReceiptNoteItemSid);

    /**
     * 查询收货单-明细列表
     * 
     * @param invGoodReceiptNoteItem 收货单-明细
     * @return 收货单-明细集合
     */
    public List<InvGoodReceiptNoteItem> selectInvGoodReceiptNoteItemList(InvGoodReceiptNoteItem invGoodReceiptNoteItem);

    /**
     * 新增收货单-明细
     * 
     * @param invGoodReceiptNoteItem 收货单-明细
     * @return 结果
     */
    public int insertInvGoodReceiptNoteItem(InvGoodReceiptNoteItem invGoodReceiptNoteItem);

    /**
     * 修改收货单-明细
     * 
     * @param invGoodReceiptNoteItem 收货单-明细
     * @return 结果
     */
    public int updateInvGoodReceiptNoteItem(InvGoodReceiptNoteItem invGoodReceiptNoteItem);

    /**
     * 变更收货单-明细
     *
     * @param invGoodReceiptNoteItem 收货单-明细
     * @return 结果
     */
    public int changeInvGoodReceiptNoteItem(InvGoodReceiptNoteItem invGoodReceiptNoteItem);

    /**
     * 批量删除收货单-明细
     * 
     * @param goodReceiptNoteItemSids 需要删除的收货单-明细ID
     * @return 结果
     */
    public int deleteInvGoodReceiptNoteItemByIds(List<Long> goodReceiptNoteItemSids);

    /**
    * 启用/停用
    * @param invGoodReceiptNoteItem
    * @return
    */
    int changeStatus(InvGoodReceiptNoteItem invGoodReceiptNoteItem);

    /**
     * 更改确认状态
     * @param invGoodReceiptNoteItem
     * @return
     */
    int check(InvGoodReceiptNoteItem invGoodReceiptNoteItem);

}
