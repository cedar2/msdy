package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.BasPlantCapacityMapper;
import com.platform.ems.domain.BasPlantCapacity;
import com.platform.ems.service.IBasPlantCapacityService;

/**
 * 工厂-富余产能明细Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-03-27
 */
@Service
@SuppressWarnings("all")
public class BasPlantCapacityServiceImpl extends ServiceImpl<BasPlantCapacityMapper,BasPlantCapacity>  implements IBasPlantCapacityService {
    @Autowired
    private BasPlantCapacityMapper basPlantCapacityMapper;

    /**
     * 查询工厂-富余产能明细
     * 
     * @param plantOvercapacitySid 工厂-富余产能明细ID
     * @return 工厂-富余产能明细
     */
    @Override
    public BasPlantCapacity selectBasPlantCapacityById(Long plantOvercapacitySid) {
        return basPlantCapacityMapper.selectBasPlantCapacityById(plantOvercapacitySid);
    }

    /**
     * 查询工厂-富余产能明细列表
     * 
     * @param basPlantCapacity 工厂-富余产能明细
     * @return 工厂-富余产能明细
     */
    @Override
    public List<BasPlantCapacity> selectBasPlantCapacityList(BasPlantCapacity basPlantCapacity) {
        return basPlantCapacityMapper.selectBasPlantCapacityList(basPlantCapacity);
    }

    /**
     * 新增工厂-富余产能明细
     * 需要注意编码重复校验
     * @param basPlantCapacity 工厂-富余产能明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasPlantCapacity(BasPlantCapacity basPlantCapacity) {
        return basPlantCapacityMapper.insert(basPlantCapacity);
    }

    /**
     * 修改工厂-富余产能明细
     * 
     * @param basPlantCapacity 工厂-富余产能明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasPlantCapacity(BasPlantCapacity basPlantCapacity) {
        return basPlantCapacityMapper.updateById(basPlantCapacity);
    }

    /**
     * 批量删除工厂-富余产能明细
     * 
     * @param plantOvercapacitySids 需要删除的工厂-富余产能明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasPlantCapacityByIds(List<Long> plantOvercapacitySids) {
        return basPlantCapacityMapper.deleteBatchIds(plantOvercapacitySids);
    }


}
