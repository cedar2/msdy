package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.ManWeekManufacturePlanItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 生产周计划-明细Mapper接口
 * 
 * @author hjj
 * @date 2021-07-16
 */
public interface ManWeekManufacturePlanItemMapper  extends BaseMapper<ManWeekManufacturePlanItem> {


    ManWeekManufacturePlanItem selectManWeekManufacturePlanItemById(Long weekManufacturePlanItemSid);
    ManWeekManufacturePlanItem getQuantityFenPei(ManWeekManufacturePlanItem manWeekManufacturePlanItem);
    List<ManWeekManufacturePlanItem> selectManWeekManufacturePlanItemList(ManWeekManufacturePlanItem manWeekManufacturePlanItem);

    /**
     * 添加多个
     * @param list List ManWeekManufacturePlanItem
     * @return int
     */
    int inserts(@Param("list") List<ManWeekManufacturePlanItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManWeekManufacturePlanItem
    * @return int
    */
    int updateAllById(ManWeekManufacturePlanItem entity);

    /**
     * 更新多个
     * @param list List ManWeekManufacturePlanItem
     * @return int
     */
    int updatesAllById(@Param("list") List<ManWeekManufacturePlanItem> list);


    void deleteManWeekManufacturePlanItemByIds(@Param("list") List<Long> weekManufacturePlanSids);

    /**
     * 生产周计划明细报表
     */
    List<ManWeekManufacturePlanItem> getItemList(ManWeekManufacturePlanItem manWeekManufacturePlanItem);
}
