package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManWeekManufacturePlan;

/**
 * 生产周计划Mapper接口
 * 
 * @author hjj
 * @date 2021-07-16
 */
public interface ManWeekManufacturePlanMapper  extends BaseMapper<ManWeekManufacturePlan> {


    ManWeekManufacturePlan selectManWeekManufacturePlanById(Long weekManufacturePlanSid);

    List<ManWeekManufacturePlan> selectManWeekManufacturePlanList(ManWeekManufacturePlan manWeekManufacturePlan);

    /**
     * 添加多个
     * @param list List ManWeekManufacturePlan
     * @return int
     */
    int inserts(@Param("list") List<ManWeekManufacturePlan> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManWeekManufacturePlan
    * @return int
    */
    int updateAllById(ManWeekManufacturePlan entity);

    /**
     * 更新多个
     * @param list List ManWeekManufacturePlan
     * @return int
     */
    int updatesAllById(@Param("list") List<ManWeekManufacturePlan> list);


    int countByDomain(ManWeekManufacturePlan params);
}
