package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.entity.SysUserDataRole;
import com.platform.common.core.domain.entity.SysUserRole;

import java.util.List;

/**
 * 用户信息Service接口
 *
 * @author qhq
 * @date 2021-09-13
 */
public interface ISystemUserService extends IService<SysUser> {
    /**
     * 查询用户信息
     *
     * @param userId 用户信息ID
     * @return 用户信息
     */
    public SysUser selectSysUserById(Long userId);

    /**
     * 查询用户信息
     *
     * @param userName 用户信息ID
     * @return 用户信息
     */
    public SysUser selectSysUserByName(String userName);

    /**
     * 查询用户信息列表
     *
     * @param SysUser 用户信息
     * @return 用户信息集合
     */
    public List<SysUser> selectSysUserList(SysUser SysUser);

    /**
     * 新增用户
     * @param sysUser 用户
     * @return 结果
     */
    public int insertSysUser(SysUser sysUser);

    /**
     * 根据用户查数据角色对象
     * @param userId 用户id
     * @return 结果
     */
    public List<SysUserDataRole> selectSysUserDataRoleByUserId(Long userId);

    /**
     * 根据用户查操作角色对象
     * @param userId 用户id
     * @return 结果
     */
    public List<SysUserRole> selectSysUserRoleByUserId(Long userId);

    /**
     * 变更用户信息
     * @param sysUser 用户
     * @return 结果
     */
    public int updateSysUser(SysUser sysUser);

    SysUser verifyEmail(SysUser user);

    void verifyCode(SysUser sysUser);

    int resetPwd(SysUser user);

    int setOpenid(SysUser user);
}
