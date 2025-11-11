package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvInventoryAdjustAttachment;

/**
 * 库存调整单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-04-19
 */
public interface IInvInventoryAdjustAttachmentService extends IService<InvInventoryAdjustAttachment>{
    /**
     * 查询库存调整单-附件
     * 
     * @param inventoryAdjustAttachmentSid 库存调整单-附件ID
     * @return 库存调整单-附件
     */
    public InvInventoryAdjustAttachment selectInvInventoryAdjustAttachmentById(Long inventoryAdjustAttachmentSid);

    /**
     * 查询库存调整单-附件列表
     * 
     * @param invInventoryAdjustAttachment 库存调整单-附件
     * @return 库存调整单-附件集合
     */
    public List<InvInventoryAdjustAttachment> selectInvInventoryAdjustAttachmentList(InvInventoryAdjustAttachment invInventoryAdjustAttachment);

    /**
     * 新增库存调整单-附件
     * 
     * @param invInventoryAdjustAttachment 库存调整单-附件
     * @return 结果
     */
    public int insertInvInventoryAdjustAttachment(InvInventoryAdjustAttachment invInventoryAdjustAttachment);

    /**
     * 修改库存调整单-附件
     * 
     * @param invInventoryAdjustAttachment 库存调整单-附件
     * @return 结果
     */
    public int updateInvInventoryAdjustAttachment(InvInventoryAdjustAttachment invInventoryAdjustAttachment);

    /**
     * 批量删除库存调整单-附件
     * 
     * @param inventoryAdjustAttachmentSids 需要删除的库存调整单-附件ID
     * @return 结果
     */
    public int deleteInvInventoryAdjustAttachmentByIds(List<Long> inventoryAdjustAttachmentSids);

}
