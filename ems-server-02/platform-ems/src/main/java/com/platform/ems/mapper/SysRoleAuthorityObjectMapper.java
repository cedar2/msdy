package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SysRoleAuthorityObject;

/**
 * 角色信息-权限对象Mapper接口
 *
 * @author chenkw
 * @date 2021-12-28
 */
public interface SysRoleAuthorityObjectMapper extends BaseMapper<SysRoleAuthorityObject> {


    SysRoleAuthorityObject selectSysRoleAuthorityObjectById(Long roleAuthorityObjectSid);

    List<SysRoleAuthorityObject> selectSysRoleAuthorityObjectList(SysRoleAuthorityObject sysRoleAuthorityObject);

    /**
     * 添加多个
     *
     * @param list List SysRoleAuthorityObject
     * @return int
     */
    int inserts(@Param("list") List<SysRoleAuthorityObject> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SysRoleAuthorityObject
     * @return int
     */
    int updateAllById(SysRoleAuthorityObject entity);

    /**
     * 更新多个
     *
     * @param list List SysRoleAuthorityObject
     * @return int
     */
    int updatesAllById(@Param("list") List<SysRoleAuthorityObject> list);


}
