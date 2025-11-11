package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvGoodIssueNoteItem;

/**
 * 发货单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-06-01
 */
public interface IInvGoodIssueNoteItemService extends IService<InvGoodIssueNoteItem>{
    /**
     * 查询发货单-明细
     * 
     * @param goodIssueNoteItemSid 发货单-明细ID
     * @return 发货单-明细
     */
    public InvGoodIssueNoteItem selectInvGoodIssueNoteItemById(Long goodIssueNoteItemSid);

    /**
     * 查询发货单-明细列表
     * 
     * @param invGoodIssueNoteItem 发货单-明细
     * @return 发货单-明细集合
     */
    public List<InvGoodIssueNoteItem> selectInvGoodIssueNoteItemList(InvGoodIssueNoteItem invGoodIssueNoteItem);

    /**
     * 新增发货单-明细
     * 
     * @param invGoodIssueNoteItem 发货单-明细
     * @return 结果
     */
    public int insertInvGoodIssueNoteItem(InvGoodIssueNoteItem invGoodIssueNoteItem);

    /**
     * 修改发货单-明细
     * 
     * @param invGoodIssueNoteItem 发货单-明细
     * @return 结果
     */
    public int updateInvGoodIssueNoteItem(InvGoodIssueNoteItem invGoodIssueNoteItem);

    /**
     * 变更发货单-明细
     *
     * @param invGoodIssueNoteItem 发货单-明细
     * @return 结果
     */
    public int changeInvGoodIssueNoteItem(InvGoodIssueNoteItem invGoodIssueNoteItem);

    /**
     * 批量删除发货单-明细
     * 
     * @param goodIssueNoteItemSids 需要删除的发货单-明细ID
     * @return 结果
     */
    public int deleteInvGoodIssueNoteItemByIds(List<Long> goodIssueNoteItemSids);

    /**
    * 启用/停用
    * @param invGoodIssueNoteItem
    * @return
    */
    int changeStatus(InvGoodIssueNoteItem invGoodIssueNoteItem);

    /**
     * 更改确认状态
     * @param invGoodIssueNoteItem
     * @return
     */
    int check(InvGoodIssueNoteItem invGoodIssueNoteItem);

}
