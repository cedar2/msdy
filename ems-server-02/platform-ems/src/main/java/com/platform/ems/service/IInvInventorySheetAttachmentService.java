package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvInventorySheetAttachment;

/**
 * 盘点单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface IInvInventorySheetAttachmentService extends IService<InvInventorySheetAttachment>{
    /**
     * 查询盘点单-附件
     * 
     * @param inventorySheetAttachmentSid 盘点单-附件ID
     * @return 盘点单-附件
     */
    public InvInventorySheetAttachment selectInvInventorySheetAttachmentById(Long inventorySheetAttachmentSid);

    /**
     * 查询盘点单-附件列表
     * 
     * @param invInventorySheetAttachment 盘点单-附件
     * @return 盘点单-附件集合
     */
    public List<InvInventorySheetAttachment> selectInvInventorySheetAttachmentList(InvInventorySheetAttachment invInventorySheetAttachment);

    /**
     * 新增盘点单-附件
     * 
     * @param invInventorySheetAttachment 盘点单-附件
     * @return 结果
     */
    public int insertInvInventorySheetAttachment(InvInventorySheetAttachment invInventorySheetAttachment);

    /**
     * 修改盘点单-附件
     * 
     * @param invInventorySheetAttachment 盘点单-附件
     * @return 结果
     */
    public int updateInvInventorySheetAttachment(InvInventorySheetAttachment invInventorySheetAttachment);

    /**
     * 批量删除盘点单-附件
     * 
     * @param inventorySheetAttachmentSids 需要删除的盘点单-附件ID
     * @return 结果
     */
    public int deleteInvInventorySheetAttachmentByIds(List<Long> inventorySheetAttachmentSids);

}
