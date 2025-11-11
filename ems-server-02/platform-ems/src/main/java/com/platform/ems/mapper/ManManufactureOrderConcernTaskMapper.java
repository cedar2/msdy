package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.dto.response.form.ManManuOrderConcernTracking;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManManufactureOrderConcernTask;

/**
 * 生产订单-关注事项Mapper接口
 *
 * @author chenkw
 * @date 2022-08-02
 */
public interface ManManufactureOrderConcernTaskMapper extends BaseMapper<ManManufactureOrderConcernTask> {

    ManManufactureOrderConcernTask selectManManufactureOrderConcernTaskById(Long manufactureOrderConcernTaskSid);

    List<ManManufactureOrderConcernTask> selectManManufactureOrderConcernTaskList(ManManufactureOrderConcernTask manManufactureOrderConcernTask);

    /**
     * 添加多个
     *
     * @param list List ManManufactureOrderConcernTask
     * @return int
     */
    int inserts(@Param("list") List<ManManufactureOrderConcernTask> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManManufactureOrderConcernTask
     * @return int
     */
    int updateAllById(ManManufactureOrderConcernTask entity);

    /**
     * 获取即将逾期生产订单事项明细
     * 即将逾期天数取值顺序 1、主表， 2、租户配置表， 3、系统配置表， 4、默认7天
     * 每天定时获取【生产订单为”已确认“状态  & 生产订单事项明细的“计划完工日期” >= 当前日期 & 生产订单事项明细的“计划完工日期” <= 当前日期 + 即将预警天数 】的所有生产订单事项明细，预警信息：
     * 款号XXX的生产订单XXX即将到期，计划完工日期YYYY/MM/DD
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ManManufactureOrderConcernTask> selectToexpireList(ManManufactureOrderConcernTask entity);

    /**
     * 获取已逾期生产订单事项明细
     * 每天定时获取【生产订单为”已确认“状态  &  生产订单事项明细的”完工状态“不是”已完工“  &  当前日期 > 生产订单事项明细的“计划完工日期” 】的所有生产订单事项明细，预警信息：
     * 款号XXX的生产订单XXX已逾期，计划完工日期YYYY/MM/DD
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ManManufactureOrderConcernTask> selectOverdueList(ManManufactureOrderConcernTask entity);

    /**
     * 生产进度报表 的关注事项明细
     */
    List<ManManufactureOrderConcernTask> selectManManufactureOrderConcernByTaskGroupList(ManManufactureOrderConcernTask manManufactureOrderConcernTask);

    /**
     * 生产进度跟踪报表（事项）
     *
     * @param request
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ManManuOrderConcernTracking> selectManufactureOrderConcernTrackingList(ManManuOrderConcernTracking request);
}
