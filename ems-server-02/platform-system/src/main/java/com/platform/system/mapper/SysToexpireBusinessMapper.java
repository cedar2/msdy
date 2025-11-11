package com.platform.system.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.system.domain.SysToexpireBusiness;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 即将到期预警列Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-29
 */
public interface SysToexpireBusinessMapper  extends BaseMapper<SysToexpireBusiness> {

    SysToexpireBusiness selectSysToexpireBusinessById(Long toexpireBusinessSid);

    List<SysToexpireBusiness> selectSysToexpireBusinessList(SysToexpireBusiness sysToexpireBusiness);

    /**
     * 添加多个
     * @param list List SysToexpireBusiness
     * @return int
     */
    int inserts(@Param("list") List<SysToexpireBusiness> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SysToexpireBusiness
    * @return int
    */
    int updateAllById(SysToexpireBusiness entity);

    /**
     * 更新多个
     * @param list List SysToexpireBusiness
     * @return int
     */
    int updatesAllById(@Param("list") List<SysToexpireBusiness> list);

    /**
     * 自动定时任务
     *
     * 批量删除
     *
     * @param entity SysToexpireBusiness
     * @return int
     */
    @InterceptorIgnore(tenantLine = "true")
    int delete(SysToexpireBusiness entity);

    /**
     * 自动定时任务
     *
     * 添加多个
     * @param list List SysToexpireBusiness
     * @return int
     */
    @InterceptorIgnore(tenantLine = "true")
    int insertAll(@Param("list") List<SysToexpireBusiness> list);

    /**
     * 自动定时任务 不分租户查询全部
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("select t.* from s_sys_toexpire_business t" +
            " where t.user_id is not null")
    List<SysToexpireBusiness> selectListAll(SysToexpireBusiness entity);
}
