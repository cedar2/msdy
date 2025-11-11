package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.HrIncomeCertificate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收入证明Mapper接口
 *
 * @author xfzz
 * @date 2024/5/7
 */
public interface HrIncomeCertificateMapper extends BaseMapper<HrIncomeCertificate> {


    HrIncomeCertificate selectHrIncomeCertificateById(Long incomeCertificateSid);

    List<HrIncomeCertificate> selectHrIncomeCertificateList(HrIncomeCertificate hrIncomeCertificate);

    /**
     * 添加多个
     *
     * @param list List HrIncomeCertificate
     * @return int
     */
    int inserts(@Param("list") List<HrIncomeCertificate> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity HrIncomeCertificate
     * @return int
     */
    int updateAllById(HrIncomeCertificate entity);

    /**
     * 更新多个
     *
     * @param list List HrIncomeCertificate
     * @return int
     */
    int updatesAllById(@Param("list") List<HrIncomeCertificate> list);

}
