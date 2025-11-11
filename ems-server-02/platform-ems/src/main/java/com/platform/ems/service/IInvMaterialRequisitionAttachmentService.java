package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvMaterialRequisitionAttachment;

/**
 * 领退料单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-04-08
 */
public interface IInvMaterialRequisitionAttachmentService extends IService<InvMaterialRequisitionAttachment>{
    /**
     * 查询领退料单-附件
     * 
     * @param materialRequisitionAttachmentSid 领退料单-附件ID
     * @return 领退料单-附件
     */
    public InvMaterialRequisitionAttachment selectInvMaterialRequisitionAttachmentById(Long materialRequisitionAttachmentSid);

    /**
     * 查询领退料单-附件列表
     * 
     * @param invMaterialRequisitionAttachment 领退料单-附件
     * @return 领退料单-附件集合
     */
    public List<InvMaterialRequisitionAttachment> selectInvMaterialRequisitionAttachmentList(InvMaterialRequisitionAttachment invMaterialRequisitionAttachment);

    /**
     * 新增领退料单-附件
     * 
     * @param invMaterialRequisitionAttachment 领退料单-附件
     * @return 结果
     */
    public int insertInvMaterialRequisitionAttachment(InvMaterialRequisitionAttachment invMaterialRequisitionAttachment);

    /**
     * 修改领退料单-附件
     * 
     * @param invMaterialRequisitionAttachment 领退料单-附件
     * @return 结果
     */
    public int updateInvMaterialRequisitionAttachment(InvMaterialRequisitionAttachment invMaterialRequisitionAttachment);

    /**
     * 批量删除领退料单-附件
     * 
     * @param materialRequisitionAttachmentSids 需要删除的领退料单-附件ID
     * @return 结果
     */
    public int deleteInvMaterialRequisitionAttachmentByIds(List<Long> materialRequisitionAttachmentSids);

}
