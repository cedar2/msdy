package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasPlantProdLine;

/**
 * 工厂-生产线信息Service接口
 * 
 * @author linhongwei
 * @date 2021-03-27
 */
public interface IBasPlantProdLineService extends IService<BasPlantProdLine>{
    /**
     * 查询工厂-生产线信息
     * 
     * @param productLineSid 工厂-生产线信息ID
     * @return 工厂-生产线信息
     */
    public BasPlantProdLine selectBasPlantProdLineById(Long productLineSid);

    /**
     * 查询工厂-生产线信息列表
     * 
     * @param basPlantProdLine 工厂-生产线信息
     * @return 工厂-生产线信息集合
     */
    public List<BasPlantProdLine> selectBasPlantProdLineList(BasPlantProdLine basPlantProdLine);

    /**
     * 新增工厂-生产线信息
     * 
     * @param basPlantProdLine 工厂-生产线信息
     * @return 结果
     */
    public int insertBasPlantProdLine(BasPlantProdLine basPlantProdLine);

    /**
     * 修改工厂-生产线信息
     * 
     * @param basPlantProdLine 工厂-生产线信息
     * @return 结果
     */
    public int updateBasPlantProdLine(BasPlantProdLine basPlantProdLine);

    /**
     * 批量删除工厂-生产线信息
     * 
     * @param productLineSids 需要删除的工厂-生产线信息ID
     * @return 结果
     */
    public int deleteBasPlantProdLineByIds(List<Long> productLineSids);

}
