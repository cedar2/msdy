package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.SysAuthorityObject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限对象Mapper接口
 *
 * @author straw
 * @date 2023-01-16
 */
@InterceptorIgnore(tenantLine = "true")
public interface SysAuthorityObjectMapper extends BaseMapper<SysAuthorityObject> {


    SysAuthorityObject selectSysAuthorityObjectById(Long authorityObjectSid);

    List<SysAuthorityObject> selectSysAuthorityObjectList(SysAuthorityObject sysAuthorityObject);

    /**
     * 添加多个
     *
     * @param list List SysAuthorityObject
     * @return int
     */
    int inserts(@Param("list") List<SysAuthorityObject> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SysAuthorityObject
     * @return int
     */
    int updateAllById(SysAuthorityObject entity);

    /**
     * 更新多个
     *
     * @param list List SysAuthorityObject
     * @return int
     */
    int updatesAllById(@Param("list") List<SysAuthorityObject> list);


}
