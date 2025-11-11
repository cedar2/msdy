package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.ManMonthManufacturePlanItem;
import com.platform.ems.domain.ManWeekManufacturePlanItem;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManMonthManufacturePlan;

/**
 * 生产月计划Mapper接口
 * 
 * @author linhongwei
 * @date 2021-07-16
 */
public interface ManMonthManufacturePlanMapper  extends BaseMapper<ManMonthManufacturePlan> {


    ManMonthManufacturePlan selectManMonthManufacturePlanById(Long monthManufacturePlanSid);

    List<ManMonthManufacturePlan> selectManMonthManufacturePlanList(ManMonthManufacturePlan manMonthManufacturePlan);


    /**
     * 添加多个
     * @param list List ManMonthManufacturePlan
     * @return int
     */
    int inserts(@Param("list") List<ManMonthManufacturePlan> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManMonthManufacturePlan
    * @return int
    */
    int updateAllById(ManMonthManufacturePlan entity);

    /**
     * 更新多个
     * @param list List ManMonthManufacturePlan
     * @return int
     */
    int updatesAllById(@Param("list") List<ManMonthManufacturePlan> list);

}
