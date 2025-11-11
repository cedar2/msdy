package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DelOutsourceMaterialIssueNoteItem;

/**
 * 外发加工发料单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-17
 */
public interface DelOutsourceMaterialIssueNoteItemMapper  extends BaseMapper<DelOutsourceMaterialIssueNoteItem> {


    DelOutsourceMaterialIssueNoteItem selectDelOutsourceMaterialIssueNoteItemById(Long issueNoteItemSid);

    List<DelOutsourceMaterialIssueNoteItem> selectDelOutsourceMaterialIssueNoteItemList(DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem);

    /**
     * 添加多个
     * @param list List DelOutsourceMaterialIssueNoteItem
     * @return int
     */
    int inserts(@Param("list") List<DelOutsourceMaterialIssueNoteItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity DelOutsourceMaterialIssueNoteItem
    * @return int
    */
    int updateAllById(DelOutsourceMaterialIssueNoteItem entity);

    /**
     * 更新多个
     * @param list List DelOutsourceMaterialIssueNoteItem
     * @return int
     */
    int updatesAllById(@Param("list") List<DelOutsourceMaterialIssueNoteItem> list);

    /**
     * 外发加工发料单明细报表
     */
    List<DelOutsourceMaterialIssueNoteItem> getItemList(DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem);
}
