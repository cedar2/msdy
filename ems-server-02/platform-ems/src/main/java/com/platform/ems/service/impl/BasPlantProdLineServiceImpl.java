package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.BasPlantCapacity;
import com.platform.ems.mapper.BasPlantCapacityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.BasPlantProdLineMapper;
import com.platform.ems.domain.BasPlantProdLine;
import com.platform.ems.service.IBasPlantProdLineService;

/**
 * 工厂-生产线信息Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-03-27
 */
@Service
@SuppressWarnings("all")
public class BasPlantProdLineServiceImpl extends ServiceImpl<BasPlantProdLineMapper,BasPlantProdLine>  implements IBasPlantProdLineService {
    @Autowired
    private BasPlantProdLineMapper basPlantProdLineMapper;

    @Autowired
    private BasPlantCapacityMapper basPlantCapacityMapper;

    /**
     * 查询工厂-生产线信息
     * 
     * @param productLineSid 工厂-生产线信息ID
     * @return 工厂-生产线信息
     */
    @Override
    public BasPlantProdLine selectBasPlantProdLineById(Long productLineSid) {
        BasPlantProdLine basPlantProdLine = basPlantProdLineMapper.selectBasPlantProdLineById(productLineSid);
        //工厂-富余产能明细
        BasPlantCapacity basPlantCapacity = new BasPlantCapacity();
        basPlantProdLine.setProductLineSid(productLineSid);
        List<BasPlantCapacity> basPlantCapacityList = basPlantCapacityMapper.selectBasPlantCapacityList(basPlantCapacity);
        basPlantProdLine.setBasPlantCapacityList(basPlantCapacityList);
        return basPlantProdLine;
    }

    /**
     * 查询工厂-生产线信息列表
     * 
     * @param basPlantProdLine 工厂-生产线信息
     * @return 工厂-生产线信息
     */
    @Override
    public List<BasPlantProdLine> selectBasPlantProdLineList(BasPlantProdLine basPlantProdLine) {
        return basPlantProdLineMapper.selectBasPlantProdLineList(basPlantProdLine);
    }

    /**
     * 新增工厂-生产线信息
     * 需要注意编码重复校验
     * @param basPlantProdLine 工厂-生产线信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasPlantProdLine(BasPlantProdLine basPlantProdLine) {
        return basPlantProdLineMapper.insert(basPlantProdLine);
    }

    /**
     * 修改工厂-生产线信息
     * 
     * @param basPlantProdLine 工厂-生产线信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasPlantProdLine(BasPlantProdLine basPlantProdLine) {
        return basPlantProdLineMapper.updateById(basPlantProdLine);
    }

    /**
     * 批量删除工厂-生产线信息
     * 
     * @param productLineSids 需要删除的工厂-生产线信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasPlantProdLineByIds(List<Long> productLineSids) {
        return basPlantProdLineMapper.deleteBatchIds(productLineSids);
    }


}
