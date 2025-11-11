package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.HrOtherPersonnelCertificateAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * 其它人事证明-附件Mapper接口
 *
 * @author xfzz
 * @date 2024/5/8
 */
public interface HrOtherPersonnelCertificateAttachMapper extends BaseMapper<HrOtherPersonnelCertificateAttach> {

    HrOtherPersonnelCertificateAttach selectHrOtherPersonnelCertificateAttachById(Long otherPersonnelCertificateAttachSid);

    List<HrOtherPersonnelCertificateAttach> selectHrOtherPersonnelCertificateAttachList(HrOtherPersonnelCertificateAttach hrOtherPersonnelCertificateAttach);

    /**
     * 添加多个
     *
     * @param list List HrOtherPersonnelCertificateAttach
     * @return int
     */
    int inserts(@Param("list") List<HrOtherPersonnelCertificateAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity HrOtherPersonnelCertificateAttach
     * @return int
     */
    int updateAllById(HrOtherPersonnelCertificateAttach entity);

    /**
     * 更新多个
     *
     * @param list List HrOtherPersonnelCertificateAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<HrOtherPersonnelCertificateAttach> list);

}
