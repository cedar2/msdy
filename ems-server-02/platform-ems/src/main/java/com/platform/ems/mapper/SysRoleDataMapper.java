package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SysRoleData;

/**
 * 数据角色Mapper接口
 *
 * @author chenkw
 * @date 2023-05-16
 */
public interface SysRoleDataMapper extends BaseMapper<SysRoleData> {

    SysRoleData selectSysRoleDataById(Long roleDataSid);

    List<SysRoleData> selectSysRoleDataList(SysRoleData sysRoleData);

    /**
     * 添加多个
     *
     * @param list List SysRoleData
     * @return int
     */
    int inserts(@Param("list") List<SysRoleData> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SysRoleData
     * @return int
     */
    int updateAllById(SysRoleData entity);

    /**
     * 更新多个
     *
     * @param list List SysRoleData
     * @return int
     */
    int updatesAllById(@Param("list") List<SysRoleData> list);

}
