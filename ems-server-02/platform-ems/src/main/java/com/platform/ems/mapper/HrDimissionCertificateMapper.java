package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.HrDimissionCertificate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 离职证明Mapper接口
 *
 * @author xfzz
 * @date 2024/5/7
 */
public interface HrDimissionCertificateMapper extends BaseMapper<HrDimissionCertificate> {


    HrDimissionCertificate selectHrDimissionCertificateById(Long dimissionCertificateSid);

    List<HrDimissionCertificate> selectHrDimissionCertificateList(HrDimissionCertificate hrDimissionCertificate);

    /**
     * 添加多个
     *
     * @param list List HrDimissionCertificate
     * @return int
     */
    int inserts(@Param("list") List<HrDimissionCertificate> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity HrDimissionCertificate
     * @return int
     */
    int updateAllById(HrDimissionCertificate entity);

    /**
     * 更新多个
     *
     * @param list List HrDimissionCertificate
     * @return int
     */
    int updatesAllById(@Param("list") List<HrDimissionCertificate> list);

}
