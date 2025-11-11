package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManDayManufacturePlanItem;

/**
 * 生产日计划-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-22
 */
public interface ManDayManufacturePlanItemMapper  extends BaseMapper<ManDayManufacturePlanItem> {


    ManDayManufacturePlanItem selectManDayManufacturePlanItemById(Long dayManufacturePlanItemSid);

    List<ManDayManufacturePlanItem> selectManDayManufacturePlanItemList(ManDayManufacturePlanItem manDayManufacturePlanItem);

    /**
     * 添加多个
     * @param list List ManDayManufacturePlanItem
     * @return int
     */
    int inserts(@Param("list") List<ManDayManufacturePlanItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManDayManufacturePlanItem
    * @return int
    */
    int updateAllById(ManDayManufacturePlanItem entity);

    /**
     * 更新多个
     * @param list List ManDayManufacturePlanItem
     * @return int
     */
    int updatesAllById(@Param("list") List<ManDayManufacturePlanItem> list);


    void deleteManDayManufacturePlanItemByIds(@Param("list") List<Long> dayManufacturePlanSids);

    /**
     * 生产日计划明细报表
     */
    List<ManDayManufacturePlanItem> getItemList(ManDayManufacturePlanItem manDayManufacturePlanItem);

    /**
     * 查询当天计划完成量
     */
    ManDayManufacturePlanItem selectQuantityBy(ManDayManufacturePlanItem manDayManufacturePlanItem);
}
