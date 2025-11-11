package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvGoodIssueNoteAttachment;

/**
 * 发货单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-01
 */
public interface InvGoodIssueNoteAttachmentMapper  extends BaseMapper<InvGoodIssueNoteAttachment> {


    InvGoodIssueNoteAttachment selectInvGoodIssueNoteAttachmentById(Long goodIssueNoteAttachmentSid);

    List<InvGoodIssueNoteAttachment> selectInvGoodIssueNoteAttachmentList(InvGoodIssueNoteAttachment invGoodIssueNoteAttachment);

    /**
     * 添加多个
     * @param list List InvGoodIssueNoteAttachment
     * @return int
     */
    int inserts(@Param("list") List<InvGoodIssueNoteAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvGoodIssueNoteAttachment
    * @return int
    */
    int updateAllById(InvGoodIssueNoteAttachment entity);

    /**
     * 更新多个
     * @param list List InvGoodIssueNoteAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<InvGoodIssueNoteAttachment> list);


}
