package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasMaterialCertificateFieldValue;

/**
 * 商品合格证洗唛自定义字段-值Mapper接口
 *
 * @author linhongwei
 * @date 2021-03-20
 */
public interface BasMaterialCertificateFieldValueMapper  extends BaseMapper<BasMaterialCertificateFieldValue> {


    BasMaterialCertificateFieldValue selectBasMaterialCertificateFieldValueById(String clientId);

    List<BasMaterialCertificateFieldValue> selectBasMaterialCertificateFieldValueList(BasMaterialCertificateFieldValue basMaterialCertificateFieldValue);

    /**
     * 添加多个
     * @param list List BasMaterialCertificateFieldValue
     * @return int
     */
    int inserts(@Param("list") List<BasMaterialCertificateFieldValue> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasMaterialCertificateFieldValue
    * @return int
    */
    int updateAllById(BasMaterialCertificateFieldValue entity);

    /**
     * 更新多个
     * @param list List BasMaterialCertificateFieldValue
     * @return int
     */
    int updatesAllById(@Param("list") List<BasMaterialCertificateFieldValue> list);


    void deleteMaterialCertificateFieldValueById(Long materialSid);

    void deleteBasMaterialCertificateFieldValueByIds(@Param("list")List<Long> materialSids);
}
