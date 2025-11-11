package com.platform.system.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.common.core.domain.entity.SysDefaultSettingSystem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统默认设置_系统级Mapper接口
 *
 * @author chenkw
 * @date 2022-04-22
 */
public interface SysDefaultSettingSystemMapper extends BaseMapper<SysDefaultSettingSystem> {

    SysDefaultSettingSystem selectSysDefaultSettingSystemById(String clientId);

    List<SysDefaultSettingSystem> selectSysDefaultSettingSystemList(SysDefaultSettingSystem sysDefaultSettingSystem);

    /**
     * 添加多个
     *
     * @param list List SysDefaultSettingSystem
     * @return int
     */
    int inserts(@Param("list") List<SysDefaultSettingSystem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SysDefaultSettingSystem
     * @return int
     */
    int updateAllById(SysDefaultSettingSystem entity);

    @InterceptorIgnore(tenantLine = "true")
    @Select("select t.* from s_sys_default_setting_system t WHERE t.client_id = #{clientId} LIMIT 1")
    SysDefaultSettingSystem selectAll(SysDefaultSettingSystem sysDefaultSettingSystem);
}

