package com.platform.system.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.system.domain.SysTodoTask;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 待办事项列Mapper接口
 *
 * @author linhongwei
 * @date 2021-08-23
 */
public interface SysTodoTaskMapper  extends BaseMapper<SysTodoTask> {


    SysTodoTask selectSysTodoTaskById(Long todoTaskSid);
    Long getMenuId(String menuName);

    List<SysTodoTask> selectSysTodoTaskList(SysTodoTask sysTodoTask);

    /**
     * 添加多个
     * @param list List SysTodoTask
     * @return int
     */
    int inserts(@Param("list") List<SysTodoTask> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SysTodoTask
    * @return int
    */
    int updateAllById(SysTodoTask entity);

    /**
     * 更新多个
     * @param list List SysTodoTask
     * @return int
     */
    int updatesAllById(@Param("list") List<SysTodoTask> list);

    /**
     * 自动定时任务
     *
     * 批量删除
     *
     * @param entity SysTodoTask
     * @return int
     */
    @InterceptorIgnore(tenantLine = "true")
    int deleteIgnore(SysTodoTask entity);

    /**
     * 自动定时任务 不分租户查询全部
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("select t.* from s_sys_todo_task t" +
            " where t.user_id is not null")
    List<SysTodoTask> selectListAll(SysTodoTask entity);
}
