package com.platform.system.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.common.core.domain.entity.SysClient;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 租户信息Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-30
 */
public interface SysClientMapper extends BaseMapper<SysClient> {

    SysClient selectSysClientById(String clientId);

    List<SysClient> selectSysClientList(SysClient sysClient);

    /**
     * 添加多个
     *
     * @param list List SysClient
     * @return int
     */
    int inserts(@Param("list") List<SysClient> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SysClient
     * @return int
     */
    int updateAllById(SysClient entity);

    /**
     * 更新多个
     *
     * @param list List SysClient
     * @return int
     */
    int updatesAllById(@Param("list") List<SysClient> list);


    List<SysClient> getList(SysClient sysClient);


    @InterceptorIgnore(tenantLine = "true")
    @Select("select * from s_sys_client")
    List<SysClient> selectSysClientAll(SysClient sysClient);
}

