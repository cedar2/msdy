package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.InvMaterialRequisitionAttachmentMapper;
import com.platform.ems.domain.InvMaterialRequisitionAttachment;
import com.platform.ems.service.IInvMaterialRequisitionAttachmentService;

/**
 * 领退料单-附件Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-08
 */
@Service
@SuppressWarnings("all")
public class InvMaterialRequisitionAttachmentServiceImpl extends ServiceImpl<InvMaterialRequisitionAttachmentMapper,InvMaterialRequisitionAttachment>  implements IInvMaterialRequisitionAttachmentService {
    @Autowired
    private InvMaterialRequisitionAttachmentMapper invMaterialRequisitionAttachmentMapper;

    /**
     * 查询领退料单-附件
     * 
     * @param materialRequisitionAttachmentSid 领退料单-附件ID
     * @return 领退料单-附件
     */
    @Override
    public InvMaterialRequisitionAttachment selectInvMaterialRequisitionAttachmentById(Long materialRequisitionAttachmentSid) {
        return invMaterialRequisitionAttachmentMapper.selectInvMaterialRequisitionAttachmentById(materialRequisitionAttachmentSid);
    }

    /**
     * 查询领退料单-附件列表
     * 
     * @param invMaterialRequisitionAttachment 领退料单-附件
     * @return 领退料单-附件
     */
    @Override
    public List<InvMaterialRequisitionAttachment> selectInvMaterialRequisitionAttachmentList(InvMaterialRequisitionAttachment invMaterialRequisitionAttachment) {
        return invMaterialRequisitionAttachmentMapper.selectInvMaterialRequisitionAttachmentList(invMaterialRequisitionAttachment);
    }

    /**
     * 新增领退料单-附件
     * 需要注意编码重复校验
     * @param invMaterialRequisitionAttachment 领退料单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvMaterialRequisitionAttachment(InvMaterialRequisitionAttachment invMaterialRequisitionAttachment) {
        return invMaterialRequisitionAttachmentMapper.insert(invMaterialRequisitionAttachment);
    }

    /**
     * 修改领退料单-附件
     * 
     * @param invMaterialRequisitionAttachment 领退料单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvMaterialRequisitionAttachment(InvMaterialRequisitionAttachment invMaterialRequisitionAttachment) {
        return invMaterialRequisitionAttachmentMapper.updateById(invMaterialRequisitionAttachment);
    }

    /**
     * 批量删除领退料单-附件
     * 
     * @param materialRequisitionAttachmentSids 需要删除的领退料单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvMaterialRequisitionAttachmentByIds(List<Long> materialRequisitionAttachmentSids) {
        return invMaterialRequisitionAttachmentMapper.deleteBatchIds(materialRequisitionAttachmentSids);
    }


}
