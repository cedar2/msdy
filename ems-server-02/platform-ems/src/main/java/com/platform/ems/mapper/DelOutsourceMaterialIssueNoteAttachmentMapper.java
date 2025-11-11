package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DelOutsourceMaterialIssueNoteAttachment;

/**
 * 外发加工发料单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-17
 */
public interface DelOutsourceMaterialIssueNoteAttachmentMapper  extends BaseMapper<DelOutsourceMaterialIssueNoteAttachment> {


    DelOutsourceMaterialIssueNoteAttachment selectDelOutsourceMaterialIssueNoteAttachmentById(Long issueNoteAttachmentSid);

    List<DelOutsourceMaterialIssueNoteAttachment> selectDelOutsourceMaterialIssueNoteAttachmentList(DelOutsourceMaterialIssueNoteAttachment delOutsourceMaterialIssueNoteAttachment);

    /**
     * 添加多个
     * @param list List DelOutsourceMaterialIssueNoteAttachment
     * @return int
     */
    int inserts(@Param("list") List<DelOutsourceMaterialIssueNoteAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity DelOutsourceMaterialIssueNoteAttachment
    * @return int
    */
    int updateAllById(DelOutsourceMaterialIssueNoteAttachment entity);

    /**
     * 更新多个
     * @param list List DelOutsourceMaterialIssueNoteAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<DelOutsourceMaterialIssueNoteAttachment> list);

}
