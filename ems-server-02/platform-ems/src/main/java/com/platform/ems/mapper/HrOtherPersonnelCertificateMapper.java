package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.HrOtherPersonnelCertificate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * 其它人事证明Mapper接口
 *
 * @author xfzz
 * @date 2024/5/7
 */
public interface HrOtherPersonnelCertificateMapper extends BaseMapper<HrOtherPersonnelCertificate> {


    HrOtherPersonnelCertificate selectHrOtherPersonnelCertificateById(Long otherPersonnelCertificateSid);

    List<HrOtherPersonnelCertificate> selectHrOtherPersonnelCertificateList(HrOtherPersonnelCertificate hrOtherPersonnelCertificate);

    /**
     * 添加多个
     *
     * @param list List HrOtherPersonnelCertificate
     * @return int
     */
    int inserts(@Param("list") List<HrOtherPersonnelCertificate> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity HrOtherPersonnelCertificate
     * @return int
     */
    int updateAllById(HrOtherPersonnelCertificate entity);

    /**
     * 更新多个
     *
     * @param list List HrOtherPersonnelCertificate
     * @return int
     */
    int updatesAllById(@Param("list") List<HrOtherPersonnelCertificate> list);

}
