package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SysRoleAuthorityFieldValue;

/**
 * 角色信息-权限字段值Service接口
 * 
 * @author chenkw
 * @date 2021-12-28
 */
public interface ISysRoleAuthorityFieldValueService extends IService<SysRoleAuthorityFieldValue>{
    /**
     * 查询角色信息-权限字段值
     * 
     * @param roleAuthorityFieldValueSid 角色信息-权限字段值ID
     * @return 角色信息-权限字段值
     */
    public SysRoleAuthorityFieldValue selectSysRoleAuthorityFieldValueById(Long roleAuthorityFieldValueSid);

    /**
     * 查询角色信息-权限字段值列表
     * 
     * @param sysRoleAuthorityFieldValue 角色信息-权限字段值
     * @return 角色信息-权限字段值集合
     */
    public List<SysRoleAuthorityFieldValue> selectSysRoleAuthorityFieldValueList(SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue);

    /**
     * 查询角色信息-权限字段值列表（有关联其他表的比较完整查询）
     *
     * @param sysRoleAuthorityFieldValue 角色信息-权限字段值
     * @return 角色信息-权限字段值集合
     */
    public List<SysRoleAuthorityFieldValue> selectMoreRoleAuthorityFieldValueList(SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue);

    /**
     * 新增角色信息-权限字段值
     * 
     * @param sysRoleAuthorityFieldValue 角色信息-权限字段值
     * @return 结果
     */
    public int insertSysRoleAuthorityFieldValue(SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue);

    /**
     * 修改角色信息-权限字段值
     * 
     * @param sysRoleAuthorityFieldValue 角色信息-权限字段值
     * @return 结果
     */
    public int updateSysRoleAuthorityFieldValue(SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue);

    /**
     * 变更角色信息-权限字段值
     *
     * @param sysRoleAuthorityFieldValue 角色信息-权限字段值
     * @return 结果
     */
    public int changeSysRoleAuthorityFieldValue(SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue);

    /**
     * 批量删除角色信息-权限字段值
     * 
     * @param roleAuthorityFieldValueSids 需要删除的角色信息-权限字段值ID
     * @return 结果
     */
    public int deleteSysRoleAuthorityFieldValueByIds(List<Long>  roleAuthorityFieldValueSids);

}
