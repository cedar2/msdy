package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.HrDimissionCertificateAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 离职证明-附件Mapper接口
 *
 * @author xfzz
 * @date 2024/5/8
 */
public interface HrDimissionCertificateAttachMapper extends BaseMapper<HrDimissionCertificateAttach> {

    HrDimissionCertificateAttach selectHrDimissionCertificateAttachById(Long dimissionCertificateAttachSid);

    List<HrDimissionCertificateAttach> selectHrDimissionCertificateAttachList(HrDimissionCertificateAttach hrDimissionCertificateAttach);

    /**
     * 添加多个
     *
     * @param list List HrDimissionCertificateAttach
     * @return int
     */
    int inserts(@Param("list") List<HrDimissionCertificateAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity HrDimissionCertificateAttach
     * @return int
     */
    int updateAllById(HrDimissionCertificateAttach entity);

    /**
     * 更新多个
     *
     * @param list List HrDimissionCertificateAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<HrDimissionCertificateAttach> list);

}
