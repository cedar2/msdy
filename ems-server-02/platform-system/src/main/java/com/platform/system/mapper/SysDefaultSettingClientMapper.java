package com.platform.system.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.entity.SysDefaultSettingSystem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统默认设置_租户级Mapper接口
 *
 * @author chenkw
 * @date 2022-04-22
 */
public interface SysDefaultSettingClientMapper extends BaseMapper<SysDefaultSettingClient> {

    @InterceptorIgnore(tenantLine = "true")
    SysDefaultSettingClient selectSysDefaultSettingClientById(String clientId);

    List<SysDefaultSettingClient> selectSysDefaultSettingClientList(SysDefaultSettingClient sysDefaultSettingClient);

    @InterceptorIgnore(tenantLine = "true")
    List<SysDefaultSettingClient> selectSysDefaultSettingClientAll(SysDefaultSettingClient sysDefaultSettingClient);

    /**
     * 添加多个
     *
     * @param list List SysDefaultSettingClient
     * @return int
     */
    int inserts(@Param("list") List<SysDefaultSettingClient> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SysDefaultSettingClient
     * @return int
     */
    int updateAllById(SysDefaultSettingClient entity);

    /**
     * 更新多个
     *
     * @param list List SysDefaultSettingClient
     * @return int
     */
    int updatesAllById(@Param("list") List<SysDefaultSettingClient> list);

    @InterceptorIgnore(tenantLine = "true")
    SysDefaultSettingSystem selectSysDefaultSettingSystem();


}
