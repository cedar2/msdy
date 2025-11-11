package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DelOutsourceMaterialIssueNoteAttachment;

/**
 * 外发加工发料单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-05-17
 */
public interface IDelOutsourceMaterialIssueNoteAttachmentService extends IService<DelOutsourceMaterialIssueNoteAttachment>{
    /**
     * 查询外发加工发料单-附件
     * 
     * @param issueNoteAttachmentSid 外发加工发料单-附件ID
     * @return 外发加工发料单-附件
     */
    public DelOutsourceMaterialIssueNoteAttachment selectDelOutsourceMaterialIssueNoteAttachmentById(Long issueNoteAttachmentSid);

    /**
     * 查询外发加工发料单-附件列表
     * 
     * @param delOutsourceMaterialIssueNoteAttachment 外发加工发料单-附件
     * @return 外发加工发料单-附件集合
     */
    public List<DelOutsourceMaterialIssueNoteAttachment> selectDelOutsourceMaterialIssueNoteAttachmentList(DelOutsourceMaterialIssueNoteAttachment delOutsourceMaterialIssueNoteAttachment);

    /**
     * 新增外发加工发料单-附件
     * 
     * @param delOutsourceMaterialIssueNoteAttachment 外发加工发料单-附件
     * @return 结果
     */
    public int insertDelOutsourceMaterialIssueNoteAttachment(DelOutsourceMaterialIssueNoteAttachment delOutsourceMaterialIssueNoteAttachment);

    /**
     * 修改外发加工发料单-附件
     * 
     * @param delOutsourceMaterialIssueNoteAttachment 外发加工发料单-附件
     * @return 结果
     */
    public int updateDelOutsourceMaterialIssueNoteAttachment(DelOutsourceMaterialIssueNoteAttachment delOutsourceMaterialIssueNoteAttachment);

    /**
     * 变更外发加工发料单-附件
     *
     * @param delOutsourceMaterialIssueNoteAttachment 外发加工发料单-附件
     * @return 结果
     */
    public int changeDelOutsourceMaterialIssueNoteAttachment(DelOutsourceMaterialIssueNoteAttachment delOutsourceMaterialIssueNoteAttachment);

    /**
     * 批量删除外发加工发料单-附件
     * 
     * @param issueNoteAttachmentSids 需要删除的外发加工发料单-附件ID
     * @return 结果
     */
    public int deleteDelOutsourceMaterialIssueNoteAttachmentByIds(List<Long> issueNoteAttachmentSids);

    /**
     * 更改确认状态
     * @param delOutsourceMaterialIssueNoteAttachment
     * @return
     */
    int check(DelOutsourceMaterialIssueNoteAttachment delOutsourceMaterialIssueNoteAttachment);

}
