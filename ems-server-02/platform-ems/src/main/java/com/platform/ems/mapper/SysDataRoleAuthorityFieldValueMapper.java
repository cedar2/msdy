package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SysDataRoleAuthorityFieldValue;

/**
 * 数据角色信息-权限字段值Mapper接口
 *
 * @author chenkw
 * @date 2023-05-16
 */
public interface SysDataRoleAuthorityFieldValueMapper extends BaseMapper<SysDataRoleAuthorityFieldValue> {

    SysDataRoleAuthorityFieldValue selectSysDataRoleAuthorityFieldValueById(Long dataRoleAuthorityFieldValueSid);

    List<SysDataRoleAuthorityFieldValue> selectSysDataRoleAuthorityFieldValueList(SysDataRoleAuthorityFieldValue sysDataRoleAuthorityFieldValue);

    /**
     * 添加多个
     *
     * @param list List SysDataRoleAuthorityFieldValue
     * @return int
     */
    int inserts(@Param("list") List<SysDataRoleAuthorityFieldValue> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SysDataRoleAuthorityFieldValue
     * @return int
     */
    int updateAllById(SysDataRoleAuthorityFieldValue entity);

}
