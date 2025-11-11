package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.dto.response.external.BasMaterialCertificateExternal;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasMaterialCertificate;

/**
 * 商品合格证洗唛信息Mapper接口
 *
 * @author linhongwei
 * @date 2021-03-19
 */
public interface BasMaterialCertificateMapper  extends BaseMapper<BasMaterialCertificate> {

    BasMaterialCertificateExternal selectForExternalById(Long materialCertificateSid);

    BasMaterialCertificate selectBasMaterialCertificateById(Long materialCertificateSid);

    List<BasMaterialCertificate> selectBasMaterialCertificateList(BasMaterialCertificate basMaterialCertificate);

    List<BasMaterialCertificate> selectBasMaterialCertificateListByMaterialSids(@Param("materialSids") List<String> materialSids);

    /**
     * 添加多个
     * @param list List BasMaterialCertificate
     * @return int
     */
    int inserts(@Param("list") List<BasMaterialCertificate> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasMaterialCertificate
    * @return int
    */
    int updateAllById(BasMaterialCertificate entity);

    /**
     * 更新多个
     * @param list List BasMaterialCertificate
     * @return int
     */
    int updatesAllById(@Param("list") List<BasMaterialCertificate> list);


    List<String> selectBasMaterialSidList();

    int checkMaterialSidUnique(Long materialSid);

    int deleteBasMaterialCertificateByIds(@Param("materialCertificateSidList")Long[] materialCertificateSidList);

    int countByDomain(BasMaterialCertificate params);

    /**
     * 商品合格证洗唛信息确认
     */
    int confirm(BasMaterialCertificate basMaterialCertificate);

    BasMaterialCertificate selectBasMaterialCertificateByMaterialSid(Long materialSid);

    List<BasMaterialCertificate> selectMaterialCertificateListByParams(@Param("materialCertificateSidList") Long[] materialCertificateSidList);
}
