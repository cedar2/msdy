package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.SysAuthorityField;
import com.platform.ems.domain.SysAuthorityObjectField;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限对象-字段明细Mapper接口
 *
 * @author chenkw
 * @date 2021-12-28
 */
@InterceptorIgnore(tenantLine = "true")
public interface SysAuthorityObjectFieldMapper extends BaseMapper<SysAuthorityObjectField> {

    SysAuthorityObjectField selectSysAuthorityObjectFieldById(Long authorityObjectFieldSid);

    List<SysAuthorityField> selectSysAuthorityObjectFieldListById(@Param("sid") Long sids);

    List<SysAuthorityObjectField> selectSysAuthorityObjectFieldList(SysAuthorityObjectField sysAuthorityObjectField);

    /**
     * 添加多个
     *
     * @param list List SysAuthorityObjectField
     * @return int
     */
    int inserts(@Param("list") List<SysAuthorityObjectField> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SysAuthorityObjectField
     * @return int
     */
    int updateAllById(SysAuthorityObjectField entity);

}
