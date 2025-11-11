package com.platform.system.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.system.domain.SysOverdueBusiness;
import org.apache.ibatis.annotations.Select;

/**
 * 已逾期警示列Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-29
 */
public interface SysOverdueBusinessMapper  extends BaseMapper<SysOverdueBusiness> {

    SysOverdueBusiness selectSysOverdueBusinessById(Long overdueBusinessSid);

    List<SysOverdueBusiness> selectSysOverdueBusinessList(SysOverdueBusiness sysOverdueBusiness);

    /**
     * 添加多个
     * @param list List SysOverdueBusiness
     * @return int
     */
    int inserts(@Param("list") List<SysOverdueBusiness> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SysOverdueBusiness
    * @return int
    */
    int updateAllById(SysOverdueBusiness entity);

    /**
     * 更新多个
     * @param list List SysOverdueBusiness
     * @return int
     */
    int updatesAllById(@Param("list") List<SysOverdueBusiness> list);


    /**
     * 自动定时任务
     *
     * 批量删除
     *
     * @param entity SysOverdueBusiness
     * @return int
     */
    @InterceptorIgnore(tenantLine = "true")
    int delete(SysOverdueBusiness entity);

    /**
     * 自动定时任务
     *
     * 添加多个
     * @param list List SysOverdueBusiness
     * @return int
     */
    @InterceptorIgnore(tenantLine = "true")
    int insertAll(@Param("list") List<SysOverdueBusiness> list);

    /**
     * 自动定时任务 不分租户查询全部
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("select t.* from s_sys_overdue_business t" +
            " where t.user_id is not null")
    List<SysOverdueBusiness> selectListAll(SysOverdueBusiness entity);

}
