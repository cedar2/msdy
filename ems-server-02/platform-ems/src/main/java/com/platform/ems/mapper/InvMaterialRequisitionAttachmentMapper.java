package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvMaterialRequisitionAttachment;

/**
 * 领退料单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-08
 */
public interface InvMaterialRequisitionAttachmentMapper  extends BaseMapper<InvMaterialRequisitionAttachment> {


    InvMaterialRequisitionAttachment selectInvMaterialRequisitionAttachmentById(Long materialRequisitionAttachmentSid);

    List<InvMaterialRequisitionAttachment> selectInvMaterialRequisitionAttachmentList(InvMaterialRequisitionAttachment invMaterialRequisitionAttachment);

    /**
     * 添加多个
     * @param list List InvMaterialRequisitionAttachment
     * @return int
     */
    int inserts(@Param("list") List<InvMaterialRequisitionAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvMaterialRequisitionAttachment
    * @return int
    */
    int updateAllById(InvMaterialRequisitionAttachment entity);

    /**
     * 更新多个
     * @param list List InvMaterialRequisitionAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<InvMaterialRequisitionAttachment> list);


    void deleteInvMaterialRequisitionAttachmentByIds(@Param("array")Long[] materialRequisitionSids);
}
