package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SysScmApproveSettingConfirm;

/**
 * SCM工作流确认配置Mapper接口
 *
 * @author chenkw
 * @date 2023-05-11
 */
public interface SysScmApproveSettingConfirmMapper extends BaseMapper<SysScmApproveSettingConfirm> {

    SysScmApproveSettingConfirm selectSysScmApproveSettingConfirmById(Long scmApproverSettingConfirmSid);

    List<SysScmApproveSettingConfirm> selectSysScmApproveSettingConfirmList(SysScmApproveSettingConfirm sysScmApproveSettingConfirm);

    /**
     * 添加多个
     *
     * @param list List SysScmApproveSettingConfirm
     * @return int
     */
    int inserts(@Param("list") List<SysScmApproveSettingConfirm> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SysScmApproveSettingConfirm
     * @return int
     */
    int updateAllById(SysScmApproveSettingConfirm entity);

    /**
     * 更新多个
     *
     * @param list List SysScmApproveSettingConfirm
     * @return int
     */
    int updatesAllById(@Param("list") List<SysScmApproveSettingConfirm> list);

}
