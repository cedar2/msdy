package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManProduceWeekProgressTotal;

/**
 * 生产周进度汇总Mapper接口
 *
 * @author chenkw
 * @date 2022-08-26
 */
public interface ManProduceWeekProgressTotalMapper extends BaseMapper<ManProduceWeekProgressTotal> {


    ManProduceWeekProgressTotal selectManProduceWeekProgressTotalById(Long weekProgressTotalSid);

    List<ManProduceWeekProgressTotal> selectManProduceWeekProgressTotalList(ManProduceWeekProgressTotal manProduceWeekProgressTotal);

    /**
     * 添加多个
     *
     * @param list List ManProduceWeekProgressTotal
     * @return int
     */
    int inserts(@Param("list") List<ManProduceWeekProgressTotal> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManProduceWeekProgressTotal
     * @return int
     */
    int updateAllById(ManProduceWeekProgressTotal entity);

    /**
     * 更新多个
     *
     * @param list List ManProduceWeekProgressTotal
     * @return int
     */
    int updatesAllById(@Param("list") List<ManProduceWeekProgressTotal> list);

    /**
     * 按条件删除
     * 慎用
     *
     * @param entity ManProduceWeekProgressTotal
     * @return int
     */
    @InterceptorIgnore(tenantLine = "true")
    int delete(ManProduceWeekProgressTotal entity);

    /**
     * 创建一个后台定时作业，定时时间“每周一凌晨3点”，运行此汇总作业，通过上一周的周计划及生产进度日报，对上一周的生产进度进行汇总。
     *
     * @param manProduceWeekProgressTotal ManProduceWeekProgressTotal
     * @return int
     */
    List<ManProduceWeekProgressTotal> selectManWeekManufacturePlanList(ManProduceWeekProgressTotal manProduceWeekProgressTotal);
}
