package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasPlantCategory;

/**
 * 工厂-擅长品类信息Service接口
 * 
 * @author linhongwei
 * @date 2021-03-27
 */
public interface IBasPlantCategoryService extends IService<BasPlantCategory>{
    /**
     * 查询工厂-擅长品类信息
     * 
     * @param plantCategorySid 工厂-擅长品类信息ID
     * @return 工厂-擅长品类信息
     */
    public BasPlantCategory selectBasPlantCategoryById(Long plantCategorySid);

    /**
     * 查询工厂-擅长品类信息列表
     * 
     * @param basPlantCategory 工厂-擅长品类信息
     * @return 工厂-擅长品类信息集合
     */
    public List<BasPlantCategory> selectBasPlantCategoryList(BasPlantCategory basPlantCategory);

    /**
     * 新增工厂-擅长品类信息
     * 
     * @param basPlantCategory 工厂-擅长品类信息
     * @return 结果
     */
    public int insertBasPlantCategory(BasPlantCategory basPlantCategory);

    /**
     * 修改工厂-擅长品类信息
     * 
     * @param basPlantCategory 工厂-擅长品类信息
     * @return 结果
     */
    public int updateBasPlantCategory(BasPlantCategory basPlantCategory);

    /**
     * 批量删除工厂-擅长品类信息
     * 
     * @param plantCategorySids 需要删除的工厂-擅长品类信息ID
     * @return 结果
     */
    public int deleteBasPlantCategoryByIds(List<Long> plantCategorySids);

}
