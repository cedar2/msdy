package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManDayManufacturePlan;

/**
 * 生产日计划Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-22
 */
public interface ManDayManufacturePlanMapper  extends BaseMapper<ManDayManufacturePlan> {


    ManDayManufacturePlan selectManDayManufacturePlanById(Long dayManufacturePlanSid);

    List<ManDayManufacturePlan> selectManDayManufacturePlanList(ManDayManufacturePlan manDayManufacturePlan);

    /**
     * 添加多个
     * @param list List ManDayManufacturePlan
     * @return int
     */
    int inserts(@Param("list") List<ManDayManufacturePlan> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManDayManufacturePlan
    * @return int
    */
    int updateAllById(ManDayManufacturePlan entity);

    /**
     * 更新多个
     * @param list List ManDayManufacturePlan
     * @return int
     */
    int updatesAllById(@Param("list") List<ManDayManufacturePlan> list);


    int countByDomain(ManDayManufacturePlan params);
}
