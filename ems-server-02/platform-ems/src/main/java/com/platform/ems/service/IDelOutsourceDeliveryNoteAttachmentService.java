package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DelOutsourceDeliveryNoteAttachment;

/**
 * 外发加工交货单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-05-17
 */
public interface IDelOutsourceDeliveryNoteAttachmentService extends IService<DelOutsourceDeliveryNoteAttachment>{
    /**
     * 查询外发加工交货单-附件
     * 
     * @param deliveryNoteAttachmentSid 外发加工交货单-附件ID
     * @return 外发加工交货单-附件
     */
    public DelOutsourceDeliveryNoteAttachment selectDelOutsourceDeliveryNoteAttachmentById(Long deliveryNoteAttachmentSid);

    /**
     * 查询外发加工交货单-附件列表
     * 
     * @param delOutsourceDeliveryNoteAttachment 外发加工交货单-附件
     * @return 外发加工交货单-附件集合
     */
    public List<DelOutsourceDeliveryNoteAttachment> selectDelOutsourceDeliveryNoteAttachmentList(DelOutsourceDeliveryNoteAttachment delOutsourceDeliveryNoteAttachment);

    /**
     * 新增外发加工交货单-附件
     * 
     * @param delOutsourceDeliveryNoteAttachment 外发加工交货单-附件
     * @return 结果
     */
    public int insertDelOutsourceDeliveryNoteAttachment(DelOutsourceDeliveryNoteAttachment delOutsourceDeliveryNoteAttachment);

    /**
     * 修改外发加工交货单-附件
     * 
     * @param delOutsourceDeliveryNoteAttachment 外发加工交货单-附件
     * @return 结果
     */
    public int updateDelOutsourceDeliveryNoteAttachment(DelOutsourceDeliveryNoteAttachment delOutsourceDeliveryNoteAttachment);

    /**
     * 变更外发加工交货单-附件
     *
     * @param delOutsourceDeliveryNoteAttachment 外发加工交货单-附件
     * @return 结果
     */
    public int changeDelOutsourceDeliveryNoteAttachment(DelOutsourceDeliveryNoteAttachment delOutsourceDeliveryNoteAttachment);

    /**
     * 批量删除外发加工交货单-附件
     * 
     * @param deliveryNoteAttachmentSids 需要删除的外发加工交货单-附件ID
     * @return 结果
     */
    public int deleteDelOutsourceDeliveryNoteAttachmentByIds(List<Long> deliveryNoteAttachmentSids);

    /**
     * 更改确认状态
     * @param delOutsourceDeliveryNoteAttachment
     * @return
     */
    int check(DelOutsourceDeliveryNoteAttachment delOutsourceDeliveryNoteAttachment);

}
