package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManMonthManufacturePlanItem;

/**
 * 生产月计划-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-07-16
 */
public interface ManMonthManufacturePlanItemMapper  extends BaseMapper<ManMonthManufacturePlanItem> {


    ManMonthManufacturePlanItem selectManMonthManufacturePlanItemById(Long monthManufacturePlanItemSid);

    List<ManMonthManufacturePlanItem> selectManMonthManufacturePlanItemList(ManMonthManufacturePlanItem manMonthManufacturePlanItem);
    ManMonthManufacturePlanItem getQuantityFenpei(ManMonthManufacturePlanItem manMonthManufacturePlanItem);
    /**
     * 添加多个
     * @param list List ManMonthManufacturePlanItem
     * @return int
     */
    int inserts(@Param("list") List<ManMonthManufacturePlanItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManMonthManufacturePlanItem
    * @return int
    */
    int updateAllById(ManMonthManufacturePlanItem entity);

    /**
     * 更新多个
     * @param list List ManMonthManufacturePlanItem
     * @return int
     */
    int updatesAllById(@Param("list") List<ManMonthManufacturePlanItem> list);


    void deleteManMonthManufacturePlanItemByIds(@Param("list") List<Long> monthManufacturePlanSids);

    /**
     * 生产月计划明细报表
     */
    List<ManMonthManufacturePlanItem> getItemList(ManMonthManufacturePlanItem manMonthManufacturePlanItem);
}
