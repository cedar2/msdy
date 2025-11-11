package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.InvInventoryAdjustAttachmentMapper;
import com.platform.ems.domain.InvInventoryAdjustAttachment;
import com.platform.ems.service.IInvInventoryAdjustAttachmentService;

/**
 * 库存调整单-附件Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-19
 */
@Service
@SuppressWarnings("all")
public class InvInventoryAdjustAttachmentServiceImpl extends ServiceImpl<InvInventoryAdjustAttachmentMapper,InvInventoryAdjustAttachment>  implements IInvInventoryAdjustAttachmentService {
    @Autowired
    private InvInventoryAdjustAttachmentMapper invInventoryAdjustAttachmentMapper;

    /**
     * 查询库存调整单-附件
     * 
     * @param inventoryAdjustAttachmentSid 库存调整单-附件ID
     * @return 库存调整单-附件
     */
    @Override
    public InvInventoryAdjustAttachment selectInvInventoryAdjustAttachmentById(Long inventoryAdjustAttachmentSid) {
        return invInventoryAdjustAttachmentMapper.selectInvInventoryAdjustAttachmentById(inventoryAdjustAttachmentSid);
    }

    /**
     * 查询库存调整单-附件列表
     * 
     * @param invInventoryAdjustAttachment 库存调整单-附件
     * @return 库存调整单-附件
     */
    @Override
    public List<InvInventoryAdjustAttachment> selectInvInventoryAdjustAttachmentList(InvInventoryAdjustAttachment invInventoryAdjustAttachment) {
        return invInventoryAdjustAttachmentMapper.selectInvInventoryAdjustAttachmentList(invInventoryAdjustAttachment);
    }

    /**
     * 新增库存调整单-附件
     * 需要注意编码重复校验
     * @param invInventoryAdjustAttachment 库存调整单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvInventoryAdjustAttachment(InvInventoryAdjustAttachment invInventoryAdjustAttachment) {
        return invInventoryAdjustAttachmentMapper.insert(invInventoryAdjustAttachment);
    }

    /**
     * 修改库存调整单-附件
     * 
     * @param invInventoryAdjustAttachment 库存调整单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvInventoryAdjustAttachment(InvInventoryAdjustAttachment invInventoryAdjustAttachment) {
        return invInventoryAdjustAttachmentMapper.updateById(invInventoryAdjustAttachment);
    }

    /**
     * 批量删除库存调整单-附件
     * 
     * @param inventoryAdjustAttachmentSids 需要删除的库存调整单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvInventoryAdjustAttachmentByIds(List<Long> inventoryAdjustAttachmentSids) {
        return invInventoryAdjustAttachmentMapper.deleteBatchIds(inventoryAdjustAttachmentSids);
    }


}
