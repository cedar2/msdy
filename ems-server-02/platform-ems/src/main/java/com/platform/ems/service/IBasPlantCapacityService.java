package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasPlantCapacity;

/**
 * 工厂-富余产能明细Service接口
 * 
 * @author linhongwei
 * @date 2021-03-27
 */
public interface IBasPlantCapacityService extends IService<BasPlantCapacity>{
    /**
     * 查询工厂-富余产能明细
     * 
     * @param plantOvercapacitySid 工厂-富余产能明细ID
     * @return 工厂-富余产能明细
     */
    public BasPlantCapacity selectBasPlantCapacityById(Long plantOvercapacitySid);

    /**
     * 查询工厂-富余产能明细列表
     * 
     * @param basPlantCapacity 工厂-富余产能明细
     * @return 工厂-富余产能明细集合
     */
    public List<BasPlantCapacity> selectBasPlantCapacityList(BasPlantCapacity basPlantCapacity);

    /**
     * 新增工厂-富余产能明细
     * 
     * @param basPlantCapacity 工厂-富余产能明细
     * @return 结果
     */
    public int insertBasPlantCapacity(BasPlantCapacity basPlantCapacity);

    /**
     * 修改工厂-富余产能明细
     * 
     * @param basPlantCapacity 工厂-富余产能明细
     * @return 结果
     */
    public int updateBasPlantCapacity(BasPlantCapacity basPlantCapacity);

    /**
     * 批量删除工厂-富余产能明细
     * 
     * @param plantOvercapacitySids 需要删除的工厂-富余产能明细ID
     * @return 结果
     */
    public int deleteBasPlantCapacityByIds(List<Long> plantOvercapacitySids);

}
