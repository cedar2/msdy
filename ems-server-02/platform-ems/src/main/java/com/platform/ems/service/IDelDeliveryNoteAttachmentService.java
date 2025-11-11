package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DelDeliveryNoteAttachment;

/**
 * 交货单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-04-21
 */
public interface IDelDeliveryNoteAttachmentService extends IService<DelDeliveryNoteAttachment>{
    /**
     * 查询交货单-附件
     * 
     * @param deliveryNoteAttachmentSid 交货单-附件ID
     * @return 交货单-附件
     */
    public DelDeliveryNoteAttachment selectDelDeliveryNoteAttachmentById(Long deliveryNoteAttachmentSid);

    /**
     * 查询交货单-附件列表
     * 
     * @param delDeliveryNoteAttachment 交货单-附件
     * @return 交货单-附件集合
     */
    public List<DelDeliveryNoteAttachment> selectDelDeliveryNoteAttachmentList(DelDeliveryNoteAttachment delDeliveryNoteAttachment);

    /**
     * 新增交货单-附件
     * 
     * @param delDeliveryNoteAttachment 交货单-附件
     * @return 结果
     */
    public int insertDelDeliveryNoteAttachment(DelDeliveryNoteAttachment delDeliveryNoteAttachment);

    /**
     * 修改交货单-附件
     * 
     * @param delDeliveryNoteAttachment 交货单-附件
     * @return 结果
     */
    public int updateDelDeliveryNoteAttachment(DelDeliveryNoteAttachment delDeliveryNoteAttachment);

    /**
     * 批量删除交货单-附件
     * 
     * @param deliveryNoteAttachmentSids 需要删除的交货单-附件ID
     * @return 结果
     */
    public int deleteDelDeliveryNoteAttachmentByIds(List<Long> deliveryNoteAttachmentSids);

}
