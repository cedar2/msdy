package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.InvInventorySheetAttachmentMapper;
import com.platform.ems.domain.InvInventorySheetAttachment;
import com.platform.ems.service.IInvInventorySheetAttachmentService;

/**
 * 盘点单-附件Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
@Service
@SuppressWarnings("all")
public class InvInventorySheetAttachmentServiceImpl extends ServiceImpl<InvInventorySheetAttachmentMapper,InvInventorySheetAttachment>  implements IInvInventorySheetAttachmentService {
    @Autowired
    private InvInventorySheetAttachmentMapper invInventorySheetAttachmentMapper;

    /**
     * 查询盘点单-附件
     * 
     * @param inventorySheetAttachmentSid 盘点单-附件ID
     * @return 盘点单-附件
     */
    @Override
    public InvInventorySheetAttachment selectInvInventorySheetAttachmentById(Long inventorySheetAttachmentSid) {
        return invInventorySheetAttachmentMapper.selectInvInventorySheetAttachmentById(inventorySheetAttachmentSid);
    }

    /**
     * 查询盘点单-附件列表
     * 
     * @param invInventorySheetAttachment 盘点单-附件
     * @return 盘点单-附件
     */
    @Override
    public List<InvInventorySheetAttachment> selectInvInventorySheetAttachmentList(InvInventorySheetAttachment invInventorySheetAttachment) {
        return invInventorySheetAttachmentMapper.selectInvInventorySheetAttachmentList(invInventorySheetAttachment);
    }

    /**
     * 新增盘点单-附件
     * 需要注意编码重复校验
     * @param invInventorySheetAttachment 盘点单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvInventorySheetAttachment(InvInventorySheetAttachment invInventorySheetAttachment) {
        return invInventorySheetAttachmentMapper.insert(invInventorySheetAttachment);
    }

    /**
     * 修改盘点单-附件
     * 
     * @param invInventorySheetAttachment 盘点单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvInventorySheetAttachment(InvInventorySheetAttachment invInventorySheetAttachment) {
        return invInventorySheetAttachmentMapper.updateById(invInventorySheetAttachment);
    }

    /**
     * 批量删除盘点单-附件
     * 
     * @param inventorySheetAttachmentSids 需要删除的盘点单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvInventorySheetAttachmentByIds(List<Long> inventorySheetAttachmentSids) {
        return invInventorySheetAttachmentMapper.deleteBatchIds(inventorySheetAttachmentSids);
    }


}
