package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.system.domain.SysTodoTask;
import org.apache.ibatis.annotations.Param;

/**
 * 待办事项列Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-29
 */
public interface SysTodoTaskListMapper  extends BaseMapper<SysTodoTask> {


    SysTodoTask selectSysTodoTaskListById(Long todoTaskSid);

    List<SysTodoTask> selectSysTodoTaskListList(SysTodoTask sysTodoTaskList);

    /**
     * 添加多个
     * @param list List SysTodoTaskList
     * @return int
     */
    int inserts(@Param("list") List<SysTodoTask> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SysTodoTaskList
    * @return int
    */
    int updateAllById(SysTodoTask entity);

    /**
     * 更新多个
     * @param list List SysTodoTaskList
     * @return int
     */
    int updatesAllById(@Param("list") List<SysTodoTask> list);


}
