package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DelOutsourceMaterialIssueNoteItem;

/**
 * 外发加工发料单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-05-17
 */
public interface IDelOutsourceMaterialIssueNoteItemService extends IService<DelOutsourceMaterialIssueNoteItem>{
    /**
     * 查询外发加工发料单-明细
     * 
     * @param issueNoteItemSid 外发加工发料单-明细ID
     * @return 外发加工发料单-明细
     */
    public DelOutsourceMaterialIssueNoteItem selectDelOutsourceMaterialIssueNoteItemById(Long issueNoteItemSid);

    /**
     * 查询外发加工发料单-明细列表
     * 
     * @param delOutsourceMaterialIssueNoteItem 外发加工发料单-明细
     * @return 外发加工发料单-明细集合
     */
    public List<DelOutsourceMaterialIssueNoteItem> selectDelOutsourceMaterialIssueNoteItemList(DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem);

    /**
     * 新增外发加工发料单-明细
     * 
     * @param delOutsourceMaterialIssueNoteItem 外发加工发料单-明细
     * @return 结果
     */
    public int insertDelOutsourceMaterialIssueNoteItem(DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem);

    /**
     * 修改外发加工发料单-明细
     * 
     * @param delOutsourceMaterialIssueNoteItem 外发加工发料单-明细
     * @return 结果
     */
    public int updateDelOutsourceMaterialIssueNoteItem(DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem);

    /**
     * 变更外发加工发料单-明细
     *
     * @param delOutsourceMaterialIssueNoteItem 外发加工发料单-明细
     * @return 结果
     */
    public int changeDelOutsourceMaterialIssueNoteItem(DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem);

    /**
     * 批量删除外发加工发料单-明细
     * 
     * @param issueNoteItemSids 需要删除的外发加工发料单-明细ID
     * @return 结果
     */
    public int deleteDelOutsourceMaterialIssueNoteItemByIds(List<Long> issueNoteItemSids);

    /**
     * 更改确认状态
     * @param delOutsourceMaterialIssueNoteItem
     * @return
     */
    int check(DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem);

    /**
     * 外发加工发料单明细报表
     */
    List<DelOutsourceMaterialIssueNoteItem> getItemList(DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem);
}
