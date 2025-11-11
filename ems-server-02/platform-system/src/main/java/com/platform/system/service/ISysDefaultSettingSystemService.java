package com.platform.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.entity.SysDefaultSettingSystem;

/**
 * 系统默认设置_系统级Service接口
 *
 * @author chenkw
 * @date 2022-04-22
 */
public interface ISysDefaultSettingSystemService extends IService<SysDefaultSettingSystem> {
    /**
     * 查询系统默认设置_系统级
     *
     * @param clientId 系统默认设置_系统级ID
     * @return 系统默认设置_系统级
     */
    public SysDefaultSettingSystem selectSysDefaultSettingSystemById(String clientId);

    /**
     * 查询系统默认设置_系统级列表
     *
     * @param sysDefaultSettingSystem 系统默认设置_系统级
     * @return 系统默认设置_系统级集合
     */
    public List<SysDefaultSettingSystem> selectSysDefaultSettingSystemList(SysDefaultSettingSystem sysDefaultSettingSystem);

    /**
     * 新增系统默认设置_系统级
     *
     * @param sysDefaultSettingSystem 系统默认设置_系统级
     * @return 结果
     */
    public int insertSysDefaultSettingSystem(SysDefaultSettingSystem sysDefaultSettingSystem);

    /**
     * 修改系统默认设置_系统级
     *
     * @param sysDefaultSettingSystem 系统默认设置_系统级
     * @return 结果
     */
    public int updateSysDefaultSettingSystem(SysDefaultSettingSystem sysDefaultSettingSystem);

    /**
     * 变更系统默认设置_系统级
     *
     * @param sysDefaultSettingSystem 系统默认设置_系统级
     * @return 结果
     */
    public int changeSysDefaultSettingSystem(SysDefaultSettingSystem sysDefaultSettingSystem);

    /**
     * 批量删除系统默认设置_系统级
     *
     * @param clientIds 需要删除的系统默认设置_系统级ID
     * @return 结果
     */
    public int deleteSysDefaultSettingSystemByIds(List<String> clientIds);

    /**
     * 更改确认状态
     *
     * @param sysDefaultSettingSystem
     * @return
     */
    int check(SysDefaultSettingSystem sysDefaultSettingSystem);

}

