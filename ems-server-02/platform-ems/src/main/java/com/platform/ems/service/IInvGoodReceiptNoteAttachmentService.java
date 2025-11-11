package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvGoodReceiptNoteAttachment;

/**
 * 收货单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-06-01
 */
public interface IInvGoodReceiptNoteAttachmentService extends IService<InvGoodReceiptNoteAttachment>{
    /**
     * 查询收货单-附件
     * 
     * @param goodReceiptNoteAttachmentSid 收货单-附件ID
     * @return 收货单-附件
     */
    public InvGoodReceiptNoteAttachment selectInvGoodReceiptNoteAttachmentById(Long goodReceiptNoteAttachmentSid);

    /**
     * 查询收货单-附件列表
     * 
     * @param invGoodReceiptNoteAttachment 收货单-附件
     * @return 收货单-附件集合
     */
    public List<InvGoodReceiptNoteAttachment> selectInvGoodReceiptNoteAttachmentList(InvGoodReceiptNoteAttachment invGoodReceiptNoteAttachment);

    /**
     * 新增收货单-附件
     * 
     * @param invGoodReceiptNoteAttachment 收货单-附件
     * @return 结果
     */
    public int insertInvGoodReceiptNoteAttachment(InvGoodReceiptNoteAttachment invGoodReceiptNoteAttachment);

    /**
     * 修改收货单-附件
     * 
     * @param invGoodReceiptNoteAttachment 收货单-附件
     * @return 结果
     */
    public int updateInvGoodReceiptNoteAttachment(InvGoodReceiptNoteAttachment invGoodReceiptNoteAttachment);

    /**
     * 变更收货单-附件
     *
     * @param invGoodReceiptNoteAttachment 收货单-附件
     * @return 结果
     */
    public int changeInvGoodReceiptNoteAttachment(InvGoodReceiptNoteAttachment invGoodReceiptNoteAttachment);

    /**
     * 批量删除收货单-附件
     * 
     * @param goodReceiptNoteAttachmentSids 需要删除的收货单-附件ID
     * @return 结果
     */
    public int deleteInvGoodReceiptNoteAttachmentByIds(List<Long> goodReceiptNoteAttachmentSids);

    /**
    * 启用/停用
    * @param invGoodReceiptNoteAttachment
    * @return
    */
    int changeStatus(InvGoodReceiptNoteAttachment invGoodReceiptNoteAttachment);

    /**
     * 更改确认状态
     * @param invGoodReceiptNoteAttachment
     * @return
     */
    int check(InvGoodReceiptNoteAttachment invGoodReceiptNoteAttachment);

}
