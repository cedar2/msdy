package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SysPdmApproveSettingConfirm;

/**
 * PDM工作流单据组别关联配置Mapper接口
 *
 * @author chenkw
 * @date 2023-05-11
 */
public interface SysPdmApproveSettingConfirmMapper extends BaseMapper<SysPdmApproveSettingConfirm> {

    SysPdmApproveSettingConfirm selectSysPdmApproveSettingConfirmById(Long pdmApproverSettingConfirmSid);

    List<SysPdmApproveSettingConfirm> selectSysPdmApproveSettingConfirmList(SysPdmApproveSettingConfirm sysPdmApproveSettingConfirm);

    /**
     * 添加多个
     *
     * @param list List SysPdmApproveSettingConfirm
     * @return int
     */
    int inserts(@Param("list") List<SysPdmApproveSettingConfirm> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SysPdmApproveSettingConfirm
     * @return int
     */
    int updateAllById(SysPdmApproveSettingConfirm entity);

    /**
     * 更新多个
     *
     * @param list List SysPdmApproveSettingConfirm
     * @return int
     */
    int updatesAllById(@Param("list") List<SysPdmApproveSettingConfirm> list);

}
