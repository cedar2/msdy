package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.SysUserWorkItemCache;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户工作台提醒缓存Mapper接口
 *
 * @author platform
 * @date 2024-04-28
 */
public interface SysUserWorkItemCacheMapper extends BaseMapper<SysUserWorkItemCache> {

    /**
     * 查询详情
     *
     * @param userWorkItemCacheSid 单据sid
     * @return SysUserWorkItemCache
     */
    SysUserWorkItemCache selectSysUserWorkItemCacheById(Long userWorkItemCacheSid);

    /**
     * 查询列表
     *
     * @param sysUserWorkItemCache SysUserWorkItemCache
     * @return List
     */
    List<SysUserWorkItemCache> selectSysUserWorkItemCacheList(SysUserWorkItemCache sysUserWorkItemCache);

    /**
     * 添加多个
     *
     * @param list List SysUserWorkItemCache
     * @return int
     */
    int inserts(@Param("list") List<SysUserWorkItemCache> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SysUserWorkItemCache
     * @return int
     */
    int updateAllById(SysUserWorkItemCache entity);

    /**
     * 更新多个
     *
     * @param list List SysUserWorkItemCache
     * @return int
     */
    int updatesAllById(@Param("list") List<SysUserWorkItemCache> list);

    /**
     * 自动定时任务 不分租户查询全部
     */
    @InterceptorIgnore(tenantLine = "true")
    @Delete("delete from s_sys_user_work_item_cache")
    int deleteAll(SysUserWorkItemCache entity);

    /**
     * 自动定时任务 不分租户查询全部
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("select t.*, u1.nick_name, u1.work_wechat_openid, u1.dingtalk_openid, u1.feishu_open_id " +
            " from s_sys_user_work_item_cache t" +
            " left join sys_user u1 on t.user_id = u1.user_id")
    List<SysUserWorkItemCache> selectListAll(SysUserWorkItemCache entity);
}
