package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.SysAuthorityField;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限字段Mapper接口
 *
 * @author linxq
 * @date 2023-01-12
 */
@InterceptorIgnore(tenantLine = "true")
public interface SysAuthorityFieldMapper extends BaseMapper<SysAuthorityField> {

    SysAuthorityField selectSysAuthorityFieldById(Long authorityFieldSid);

    List<SysAuthorityField> selectSysAuthorityFieldList(SysAuthorityField sysAuthorityField);

    /**
     * 添加多个
     * @param list List SysAuthorityField
     * @return int
     */
    int inserts(@Param("list") List<SysAuthorityField> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity SysAuthorityField
     * @return int
     */
    int updateAllById(SysAuthorityField entity);

}
