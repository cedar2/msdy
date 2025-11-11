package com.platform.ems.mapper;
import java.util.List;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.platform.ems.domain.ManManufactureOrder;
import com.platform.ems.domain.dto.response.form.ManManuOrderProcessTracking;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.ManManufactureOrderProcess;

/**
 * 生产订单-工序Mapper接口
 * 
 * @author qhq
 * @date 2021-04-13
 */
public interface ManManufactureOrderProcessMapper  extends BaseMapper<ManManufactureOrderProcess> {


    ManManufactureOrderProcess selectManManufactureOrderProcessById(Long manufactureOrderProcessSid);

    List<ManManufactureOrderProcess> selectManManufactureOrderProcessList(ManManufactureOrderProcess manManufactureOrderProcess);

    /**
     * 添加多个
     * @param list List ManManufactureOrderProcess
     * @return int
     */
    int inserts(@Param("list") List<ManManufactureOrderProcess> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManManufactureOrderProcess
    * @return int
    */
    int updateAllById(ManManufactureOrderProcess entity);

    /**
     * 更新多个
     * @param list List ManManufactureOrderProcess
     * @return int
     */
    int updatesAllById(@Param("list") List<ManManufactureOrderProcess> list);

    /**
     * 生产订单工序明细报表
     */
    List<ManManufactureOrderProcess> getItemList(ManManufactureOrderProcess manManufactureOrderProcess);

    /**
     * 获取即将逾期生产订单工序明细
     * 即将逾期天数取值顺序 1、主表， 2、租户配置表， 3、系统配置表， 4、默认7天
     * 每天定时获取【生产订单为”已确认“状态  & 生产订单工序明细的“计划完工日期” >= 当前日期 & 生产订单工序明细的“计划完工日期” <= 当前日期 + 即将预警天数 】的所有生产订单工序明细，预警信息：
     * 款号XXX的生产订单XXX即将到期，计划完工日期YYYY/MM/DD
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ManManufactureOrderProcess> selectToexpireList(ManManufactureOrderProcess manManufactureOrderProcess);

    /**
     * 获取已逾期生产订单工序明细
     * 每天定时获取【生产订单为”已确认“状态  &  生产订单工序明细的”完工状态“不是”已完工“  &  当前日期 > 生产订单工序明细的“计划完工日期” 】的所有生产订单工序明细，预警信息：
     * 款号XXX的生产订单XXX已逾期，计划完工日期YYYY/MM/DD
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ManManufactureOrderProcess> selectOverdueList(ManManufactureOrderProcess manManufactureOrderProcess);

    /**
     * 生产进度报表 的工序明细
     */
    List<ManManufactureOrderProcess> selectManManufactureOrderProcessByProcessRouteList(ManManufactureOrderProcess manManufactureOrderProcess);

    /**
     * 班组生产进度报表 的行数据
     */
    List<ManManufactureOrderProcess> selectByProcessRouteListGroupByWork(ManManufactureOrder manManufactureOrder);

    /**
     * 班组生产进度报表 的行数据 的工序明细
     */
    List<ManManufactureOrderProcess> selectByProcessRouteItemListGroupByWork(ManManufactureOrderProcess manManufactureOrderProcess);

    /**
     * 生产进度跟踪报表（工序）
     *
     * @param request
     * @return 生产进度跟踪报表（商品）集合
     */
    @InterceptorIgnore(tenantLine = "true")
    List<ManManuOrderProcessTracking> selectManufactureOrderProcessTrackingList(ManManuOrderProcessTracking request);
}
