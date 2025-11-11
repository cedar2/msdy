package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasPlantCapacity;

/**
 * 工厂-富余产能明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-27
 */
public interface BasPlantCapacityMapper  extends BaseMapper<BasPlantCapacity> {


    BasPlantCapacity selectBasPlantCapacityById(Long plantOvercapacitySid);

    List<BasPlantCapacity> selectBasPlantCapacityList(BasPlantCapacity basPlantCapacity);

    /**
     * 添加多个
     * @param list List BasPlantCapacity
     * @return int
     */
    int inserts(@Param("list") List<BasPlantCapacity> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasPlantCapacity
    * @return int
    */
    int updateAllById(BasPlantCapacity entity);

    /**
     * 更新多个
     * @param list List BasPlantCapacity
     * @return int
     */
    int updatesAllById(@Param("list") List<BasPlantCapacity> list);


}
