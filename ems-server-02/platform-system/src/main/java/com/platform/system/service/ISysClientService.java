package com.platform.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.entity.SysClient;

import java.util.List;

/**
 * 租户信息Service接口
 *
 * @author linhongwei
 * @date 2021-09-30
 */
public interface ISysClientService extends IService<SysClient> {
    /**
     * 查询租户信息
     *
     * @param clientId 租户信息ID
     * @return 租户信息
     */
    public SysClient selectSysClientById(String clientId);

    /**
     * 查询租户信息列表
     *
     * @param sysClient 租户信息
     * @return 租户信息集合
     */
    public List<SysClient> selectSysClientList(SysClient sysClient);

    /**
     * 新增租户信息
     *
     * @param sysClient 租户信息
     * @return 结果
     */
    public int insertSysClient(SysClient sysClient);

    /**
     * 修改租户信息
     *
     * @param sysClient 租户信息
     * @return 结果
     */
    public int updateSysClient(SysClient sysClient);

    /**
     * 变更租户信息
     *
     * @param sysClient 租户信息
     * @return 结果
     */
    public int changeSysClient(SysClient sysClient);

    /**
     * 批量删除租户信息
     *
     * @param clientIds 需要删除的租户信息ID
     * @return 结果
     */
    public int deleteSysClientByIds(List<String> clientIds);

    /**
     * 启用/停用
     *
     * @param sysClient
     * @return
     */
    int changeStatus(SysClient sysClient);

    /**
     * 更改确认状态
     *
     * @param sysClient
     * @return
     */
    int check(SysClient sysClient);

    List<SysClient> getList(SysClient sysClient);

    int setDianqian(SysClient sysClient);
}

