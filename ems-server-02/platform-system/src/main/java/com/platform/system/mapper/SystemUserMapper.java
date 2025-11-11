package com.platform.system.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.common.core.domain.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户信息Mapper接口
 *
 * @author qhq
 * @date 2021-09-13
 */
public interface SystemUserMapper extends BaseMapper<SysUser> {

    @InterceptorIgnore(tenantLine = "true")
    List<SysUser> selectSysUserListAll(SysUser SysUser);

    SysUser selectSysUserById(Long userId);

    SysUser selectSysUserByName(String userName);

    List<SysUser> selectSysUserList(SysUser SysUser);

    List<SysUser> selectSysUserRoleList(SysUser SysUser);

    /**
     * 添加一个
     * @param sysUser
     * @return int
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

    public SysUser selectUserByNameAndId(@Param("userName") String userName,@Param("clientId") String clientId);

    /**
     * 通过岗位查员工再查用户
     * @param sysUser
     * @return int
     */
    List<SysUser> selectUserByPositionStaff(SysUser sysUser);

    SysUser selectStaffById(Long staffSid);
}
