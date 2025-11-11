package com.platform.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.common.core.domain.entity.SysUserDataRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户-数据角色Mapper接口
 *
 * @author chenkw
 * @date 2023-05-17
 */
public interface SysUserDataRoleMapper extends BaseMapper<SysUserDataRole> {

    SysUserDataRole selectSysUserDataRoleById(Long userDataRoleSid);

    List<SysUserDataRole> selectSysUserDataRoleList(SysUserDataRole sysUserDataRole);

    /**
     * 添加多个
     *
     * @param list List SysUserDataRole
     * @return int
     */
    int inserts(@Param("list") List<SysUserDataRole> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SysUserDataRole
     * @return int
     */
    int updateAllById(SysUserDataRole entity);

    /**
     * 更新多个
     *
     * @param list List SysUserDataRole
     * @return int
     */
    int updatesAllById(@Param("list") List<SysUserDataRole> list);

}
