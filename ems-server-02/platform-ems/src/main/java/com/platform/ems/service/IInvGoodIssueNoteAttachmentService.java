package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvGoodIssueNoteAttachment;

/**
 * 发货单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-06-01
 */
public interface IInvGoodIssueNoteAttachmentService extends IService<InvGoodIssueNoteAttachment>{
    /**
     * 查询发货单-附件
     * 
     * @param goodIssueNoteAttachmentSid 发货单-附件ID
     * @return 发货单-附件
     */
    public InvGoodIssueNoteAttachment selectInvGoodIssueNoteAttachmentById(Long goodIssueNoteAttachmentSid);

    /**
     * 查询发货单-附件列表
     * 
     * @param invGoodIssueNoteAttachment 发货单-附件
     * @return 发货单-附件集合
     */
    public List<InvGoodIssueNoteAttachment> selectInvGoodIssueNoteAttachmentList(InvGoodIssueNoteAttachment invGoodIssueNoteAttachment);

    /**
     * 新增发货单-附件
     * 
     * @param invGoodIssueNoteAttachment 发货单-附件
     * @return 结果
     */
    public int insertInvGoodIssueNoteAttachment(InvGoodIssueNoteAttachment invGoodIssueNoteAttachment);

    /**
     * 修改发货单-附件
     * 
     * @param invGoodIssueNoteAttachment 发货单-附件
     * @return 结果
     */
    public int updateInvGoodIssueNoteAttachment(InvGoodIssueNoteAttachment invGoodIssueNoteAttachment);

    /**
     * 变更发货单-附件
     *
     * @param invGoodIssueNoteAttachment 发货单-附件
     * @return 结果
     */
    public int changeInvGoodIssueNoteAttachment(InvGoodIssueNoteAttachment invGoodIssueNoteAttachment);

    /**
     * 批量删除发货单-附件
     * 
     * @param goodIssueNoteAttachmentSids 需要删除的发货单-附件ID
     * @return 结果
     */
    public int deleteInvGoodIssueNoteAttachmentByIds(List<Long> goodIssueNoteAttachmentSids);

    /**
    * 启用/停用
    * @param invGoodIssueNoteAttachment
    * @return
    */
    int changeStatus(InvGoodIssueNoteAttachment invGoodIssueNoteAttachment);

    /**
     * 更改确认状态
     * @param invGoodIssueNoteAttachment
     * @return
     */
    int check(InvGoodIssueNoteAttachment invGoodIssueNoteAttachment);

}
