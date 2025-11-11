package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;

import java.util.List;

/**
 * 系统默认设置_租户级Service接口
 *
 * @author chenkw
 * @date 2022-04-22
 */
public interface ISystemDefaultSettingClientService extends IService<SysDefaultSettingClient> {
    /**
     * 查询系统默认设置_租户级
     *
     * @param clientId 系统默认设置_租户级ID
     * @return 系统默认设置_租户级
     */
    public SysDefaultSettingClient selectSysDefaultSettingClientById(String clientId);

    /**
     * 查询系统默认设置_租户级列表
     *
     * @param sysDefaultSettingClient 系统默认设置_租户级
     * @return 系统默认设置_租户级集合
     */
    public List<SysDefaultSettingClient> selectSysDefaultSettingClientList(SysDefaultSettingClient sysDefaultSettingClient);

    /**
     * 新增系统默认设置_租户级
     *
     * @param sysDefaultSettingClient 系统默认设置_租户级
     * @return 结果
     */
    public int insertSysDefaultSettingClient(SysDefaultSettingClient sysDefaultSettingClient);

    /**
     * 修改系统默认设置_租户级
     *
     * @param sysDefaultSettingClient 系统默认设置_租户级
     * @return 结果
     */
    public int updateSysDefaultSettingClient(SysDefaultSettingClient sysDefaultSettingClient);

    /**
     * 变更系统默认设置_租户级
     *
     * @param sysDefaultSettingClient 系统默认设置_租户级
     * @return 结果
     */
    public int changeSysDefaultSettingClient(SysDefaultSettingClient sysDefaultSettingClient);

    /**
     * 批量删除系统默认设置_租户级
     *
     * @param clientIds 需要删除的系统默认设置_租户级ID
     * @return 结果
     */
    public int deleteSysDefaultSettingClientByIds(List<String> clientIds);

    /**
     * 更改确认状态
     *
     * @param sysDefaultSettingClient
     * @return
     */
    int check(SysDefaultSettingClient sysDefaultSettingClient);

}
