package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasMaterialCertificateAttachment;

/**
 * 商品合格证洗唛-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-20
 */
public interface BasMaterialCertificateAttachmentMapper  extends BaseMapper<BasMaterialCertificateAttachment> {


    BasMaterialCertificateAttachment selectBasMaterialCertificateAttachmentById(String materialCertificateAttachmentSid);

    List<BasMaterialCertificateAttachment> selectBasMaterialCertificateAttachmentList(BasMaterialCertificateAttachment basMaterialCertificateAttachment);

    /**
     * 添加多个
     * @param list List BasMaterialCertificateAttachment
     * @return int
     */
    int inserts(@Param("list") List<BasMaterialCertificateAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasMaterialCertificateAttachment
    * @return int
    */
    int updateAllById(BasMaterialCertificateAttachment entity);

    /**
     * 更新多个
     * @param list List BasMaterialCertificateAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<BasMaterialCertificateAttachment> list);


    void deleteMaterialCertificateAttachmentById(Long materialCertificateSid);

    void deleteBasMaterialCertificateAttachmentByIds(@Param("list")Long[] materialCertificateSidList);
}
