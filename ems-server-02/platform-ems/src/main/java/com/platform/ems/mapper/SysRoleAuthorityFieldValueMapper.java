package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SysRoleAuthorityFieldValue;

/**
 * 角色信息-权限字段值Mapper接口
 *
 * @author chenkw
 * @date 2021-12-28
 */
public interface SysRoleAuthorityFieldValueMapper extends BaseMapper<SysRoleAuthorityFieldValue> {


    SysRoleAuthorityFieldValue selectSysRoleAuthorityFieldValueById(Long roleAuthorityFieldValueSid);

    List<SysRoleAuthorityFieldValue> selectSysRoleAuthorityFieldValueList(SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue);

    /**
     * 有关联其他表的比较完整查询
     *
     * @param sysRoleAuthorityFieldValue List SysRoleAuthorityFieldValue
     * @return int
     */
    List<SysRoleAuthorityFieldValue> selectMoreRoleAuthorityFieldValueList(SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue);

    /**
     * 添加多个
     *
     * @param list List SysRoleAuthorityFieldValue
     * @return int
     */
    int inserts(@Param("list") List<SysRoleAuthorityFieldValue> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SysRoleAuthorityFieldValue
     * @return int
     */
    int updateAllById(SysRoleAuthorityFieldValue entity);

    /**
     * 更新多个
     *
     * @param list List SysRoleAuthorityFieldValue
     * @return int
     */
    int updatesAllById(@Param("list") List<SysRoleAuthorityFieldValue> list);


}
