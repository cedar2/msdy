package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SysDataRoleAuthorityObject;

/**
 * 数据角色信息-权限对象Mapper接口
 *
 * @author chenkw
 * @date 2023-05-16
 */
public interface SysDataRoleAuthorityObjectMapper extends BaseMapper<SysDataRoleAuthorityObject> {

    SysDataRoleAuthorityObject selectSysDataRoleAuthorityObjectById(Long dataRoleAuthorityObjectSid);

    List<SysDataRoleAuthorityObject> selectSysDataRoleAuthorityObjectList(SysDataRoleAuthorityObject sysDataRoleAuthorityObject);

    /**
     * 添加多个
     *
     * @param list List SysDataRoleAuthorityObject
     * @return int
     */
    int inserts(@Param("list") List<SysDataRoleAuthorityObject> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SysDataRoleAuthorityObject
     * @return int
     */
    int updateAllById(SysDataRoleAuthorityObject entity);

}
