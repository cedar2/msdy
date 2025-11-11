package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.dto.response.form.PrjProjectExecuteCondition;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PrjProject;

/**
 * 项目档案Mapper接口
 *
 * @author chenkw
 * @date 2022-12-08
 */
public interface PrjProjectMapper extends BaseMapper<PrjProject> {

    PrjProject selectPrjProjectById(Long projectSid);

    List<PrjProject> selectPrjProjectList(PrjProject prjProject);

    @InterceptorIgnore(tenantLine = "true")
    List<PrjProject> selectPrjProjectListAll(PrjProject prjProject);

    /**
     * 添加多个
     *
     * @param list List PrjProject
     * @return int
     */
    int inserts(@Param("list") List<PrjProject> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PrjProject
     * @return int
     */
    int updateAllById(PrjProject entity);

    /**
     * 更新多个
     *
     * @param list List PrjProject
     * @return int
     */
    int updatesAllById(@Param("list") List<PrjProject> list);

    /**
     * 查询即将到期的项目档案
     * offset : 设置到期前的天数才算即将到期
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PrjProject> getToexpireBusiness(@Param("entity") PrjProject project, @Param("offset") int offset);

    /**
     * 查询已逾期的项目档案
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PrjProject> getOverdueBusiness(PrjProject project);

    /**
     * 查询即将到期的项目档案
     * offset : 设置到期前的天数才算即将到期
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PrjProject> getToexpireBusinessNoUser(@Param("entity") PrjProject project, @Param("offset") int offset);

    /**
     * 查询已逾期的项目档案
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PrjProject> getOverdueBusinessNoUser(PrjProject project);

    /**
     * 试销站点执行状况报表报表
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PrjProjectExecuteCondition> selectPrjProjectExecuteCondition(PrjProjectExecuteCondition prjProject);

    /**
     * 定时任务获取 项目状态是否自动设置已完成
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PrjProject> selectPrjProjectNeedComplete(PrjProject prjProject);

    /**
     * 定时任务获取 项目状态是否自动设置已完成
     */
    @InterceptorIgnore(tenantLine = "true")
    int updatePrjProject(PrjProject prjProject);
}
