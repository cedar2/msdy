package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SysRoleAuthorityObject;

/**
 * 角色信息-权限对象Service接口
 * 
 * @author chenkw
 * @date 2021-12-28
 */
public interface ISysRoleAuthorityObjectService extends IService<SysRoleAuthorityObject>{
    /**
     * 查询角色信息-权限对象
     * 
     * @param roleAuthorityObjectSid 角色信息-权限对象ID
     * @return 角色信息-权限对象
     */
    public SysRoleAuthorityObject selectSysRoleAuthorityObjectById(Long roleAuthorityObjectSid);

    /**
     * 查询角色信息-权限对象列表
     * 
     * @param sysRoleAuthorityObject 角色信息-权限对象
     * @return 角色信息-权限对象集合
     */
    public List<SysRoleAuthorityObject> selectSysRoleAuthorityObjectList(SysRoleAuthorityObject sysRoleAuthorityObject);

    /**
     * 新增角色信息-权限对象
     * 
     * @param sysRoleAuthorityObject 角色信息-权限对象
     * @return 结果
     */
    public int insertSysRoleAuthorityObject(SysRoleAuthorityObject sysRoleAuthorityObject);

    /**
     * 修改角色信息-权限对象
     * 
     * @param sysRoleAuthorityObject 角色信息-权限对象
     * @return 结果
     */
    public int updateSysRoleAuthorityObject(SysRoleAuthorityObject sysRoleAuthorityObject);

    /**
     * 变更角色信息-权限对象
     *
     * @param sysRoleAuthorityObject 角色信息-权限对象
     * @return 结果
     */
    public int changeSysRoleAuthorityObject(SysRoleAuthorityObject sysRoleAuthorityObject);

    /**
     * 批量删除角色信息-权限对象
     * 
     * @param roleAuthorityObjectSids 需要删除的角色信息-权限对象ID
     * @return 结果
     */
    public int deleteSysRoleAuthorityObjectByIds(List<Long>  roleAuthorityObjectSids);

}
