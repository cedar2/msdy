package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SysAuthorityObject;

import java.util.List;

/**
 * 权限对象Service接口
 *
 * @author straw
 * @date 2023-01-16
 */
public interface ISysAuthorityObjectService extends IService<SysAuthorityObject>, HandleStatusInfoService {
    /**
     * 查询权限对象
     *
     * @param authorityObjectSid 权限对象ID
     * @return 权限对象
     */
    public SysAuthorityObject selectSysAuthorityObjectById(Long authorityObjectSid);

    /**
     * 查询权限对象列表
     *
     * @param sysAuthorityObject 权限对象
     * @return 权限对象集合
     */
    public List<SysAuthorityObject> selectSysAuthorityObjectList(SysAuthorityObject sysAuthorityObject);

    /**
     * 新增权限对象
     *
     * @param sysAuthorityObject 权限对象
     * @return 结果
     */
    public int insertSysAuthorityObject(SysAuthorityObject sysAuthorityObject);

    /**
     * 修改权限对象
     *
     * @param sysAuthorityObject 权限对象
     * @return 结果
     */
    public int updateSysAuthorityObject(SysAuthorityObject sysAuthorityObject);

    /**
     * 变更权限对象
     *
     * @param sysAuthorityObject 权限对象
     * @return 结果
     */
    public int changeSysAuthorityObject(SysAuthorityObject sysAuthorityObject);

    /**
     * 批量删除权限对象
     *
     * @param authorityObjectSids 需要删除的权限对象ID
     * @return 结果
     */
    public int deleteSysAuthorityObjectByIds(List<Long> authorityObjectSids);

    /**
     * 启用/停用
     *
     * @param sysAuthorityObject
     * @return
     */
    int changeStatus(SysAuthorityObject sysAuthorityObject);

    /**
     * 更改确认状态
     *
     * @param sysAuthorityObject
     * @return
     */
    int check(SysAuthorityObject sysAuthorityObject);

}
