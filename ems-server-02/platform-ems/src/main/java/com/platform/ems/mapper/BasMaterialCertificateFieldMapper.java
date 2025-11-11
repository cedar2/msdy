package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasMaterialCertificateField;

/**
 * 商品合格证洗唛自定义字段Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-31
 */
public interface BasMaterialCertificateFieldMapper  extends BaseMapper<BasMaterialCertificateField> {

    BasMaterialCertificateField selectBasMaterialCertificateFieldById(Long materialCertificateFieldSid);

    List<BasMaterialCertificateField> selectBasMaterialCertificateFieldList(BasMaterialCertificateField field);

    /**
     * 添加多个
     * @param list List BasMaterialCertificateField
     * @return int
     */
    int inserts(@Param("list") List<BasMaterialCertificateField> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasMaterialCertificateField
    * @return int
    */
    int updateAllById(BasMaterialCertificateField entity);

    /**
     * 更新多个
     * @param list List BasMaterialCertificateField
     * @return int
     */
    int updatesAllById(@Param("list") List<BasMaterialCertificateField> list);


}
