package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.HrIncomeCertificateAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收入证明-附件Mapper接口
 *
 * @author xfzz
 * @date 2024/5/8
 */
public interface HrIncomeCertificateAttachMapper extends BaseMapper<HrIncomeCertificateAttach> {

    HrIncomeCertificateAttach selectHrIncomeCertificateAttachById(Long incomeCertificateAttachSid);

    List<HrIncomeCertificateAttach> selectHrIncomeCertificateAttachList(HrIncomeCertificateAttach hrIncomeCertificateAttach);

    /**
     * 添加多个
     *
     * @param list List HrIncomeCertificateAttach
     * @return int
     */
    int inserts(@Param("list") List<HrIncomeCertificateAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity HrIncomeCertificateAttach
     * @return int
     */
    int updateAllById(HrIncomeCertificateAttach entity);

    /**
     * 更新多个
     *
     * @param list List HrIncomeCertificateAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<HrIncomeCertificateAttach> list);

}
