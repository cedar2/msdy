package com.platform.system.mapper;

import java.util.List;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.common.core.domain.model.SysRoleDataAuthFieldValue;
import org.apache.ibatis.annotations.Param;
import com.platform.common.core.domain.entity.SysUser;

/**
 * 用户表 数据层
 *
 * @author platform
 */
public interface SysUserMapper extends BaseMapper<SysUser>
{
    /**
     * 根据条件分页查询用户列表
     *
     * @param sysUser 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUserList(SysUser sysUser);

    /**
     * 根据条件分页查询已配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectAllocatedList(SysUser user);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUnallocatedList(SysUser user);

    public int updateUserStatus (@Param(value = "userId") Long userId , @Param(value = "status") String status);

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    public SysUser selectUserByUserName(String userName);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    public SysUser selectUserById(Long userId);

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int insertUser(SysUser user);

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    public int updateUser(SysUser user);

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
    public int updateUserAvatar(@Param("userName") String userName, @Param("avatar") String avatar);

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    public int resetUserPwd(@Param("userName") String userName, @Param("password") String password);

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteUserById(Long userId);

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    public int deleteUserByIds(Long[] userIds);

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    public SysUser checkUserNameUnique(String userName);

    /**
     * 校验手机号码是否唯一
     *
     * @param phonenumber 手机号码
     * @return 结果
     */
    public SysUser checkPhoneUnique(String phonenumber);

    /**
     * 校验email是否唯一
     *
     * @param email 用户邮箱
     * @return 结果
     */
    public SysUser checkEmailUnique(String email);

    public SysUser selectUserByNameAndId(@Param("userName") String userName,@Param("clientId") String clientId);

    /**
     * 通过用户ID查询用户数据角色权限字段信息
     * @param userId 用户ID
     * @return 用户数据角色权限字段信息
     */
    public List<SysRoleDataAuthFieldValue> selectRoleDataAuthFiledValueList(@Param("userId") Long userId);

    /**
     * 通过用户名查询用户
     *
     * @param openId
     * @return 用户对象信息
     */
    public SysUser selectUserByOpenid(String openId);

    public List<SysUser> selectUserByqyUserId(@Param("workWechatOpenid") String workWechatOpenid);

    public List<SysUser> selectUserBydingtalk(String dingtalkOpenid);

    SysUser selectUserByGzhOpenId(String gzhOpenId);

    /**
     * 校验email是否唯一
     *
     * @param user 用户
     * @return 结果
     */
    public SysUser checkUserTypeUnique(SysUser user);

    int cnacel(SysUser user);

    int updateUserOpenId(SysUser user);

    public String selectUserClientId(Long userId);

    @InterceptorIgnore(tenantLine = "true")
    List<SysUser> selectSysUserListAll(SysUser SysUser);

    SysUser selectSysUserById(Long userId);

    SysUser selectSysUserByName(String userName);

    List<SysUser> selectSysUserList(SysUser SysUser);

    List<SysUser> selectSysUserRoleList(SysUser SysUser);

    /**
     * 添加一个
     */
    int insertInfomation(SysUser sysUser);

    /**
     * 添加多个
     * @param list List SysUser
     * @return int
     */
    int inserts(@Param("list") List<SysUser> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity SysUser
     * @return int
     */
    int updateAllById(SysUser entity);

    /**
     * 根据详情自定义更新
     * @param entity SysUser
     * @return int
     */
    int updateByInfoId(SysUser entity);

    /**
     * 更新多个
     * @param list List SysUser
     * @return int
     */
    int updatesAllById(@Param("list") List<SysUser> list);

    /**
     * 通过岗位查员工再查用户
     * @param sysUser
     * @return int
     */
    List<SysUser> selectUserByPositionStaff(SysUser sysUser);

    SysUser selectStaffById(Long staffSid);
}
