package com.platform.flowable.mapper;

import com.platform.flowable.domain.dto.FlowTaskDto;

import java.util.List;

/**
 * 流程任务关联单Mapper接口
 *
 * @author c
 */
public interface SysTaskFormMapper
{

    /**
     * 查询待办
     * @param userId
     * @return
     */
    List<FlowTaskDto> todoList(String userId);

}
