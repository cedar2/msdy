package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PrjTask;

/**
 * 任务节点Mapper接口
 *
 * @author chenkw
 * @date 2022-12-07
 */
public interface PrjTaskMapper extends BaseMapper<PrjTask> {

    PrjTask selectPrjTaskById(Long taskSid);

    List<PrjTask> selectPrjTaskList(PrjTask prjTask);

    /**
     * 添加多个
     *
     * @param list List PrjTask
     * @return int
     */
    int inserts(@Param("list") List<PrjTask> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PrjTask
     * @return int
     */
    int updateAllById(PrjTask entity);

    /**
     * 更新多个
     *
     * @param list List PrjTask
     * @return int
     */
    int updatesAllById(@Param("list") List<PrjTask> list);

}
