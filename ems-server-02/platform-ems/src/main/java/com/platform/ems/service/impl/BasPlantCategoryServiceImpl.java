package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.BasPlantCategoryMapper;
import com.platform.ems.domain.BasPlantCategory;
import com.platform.ems.service.IBasPlantCategoryService;

/**
 * 工厂-擅长品类信息Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-03-27
 */
@Service
@SuppressWarnings("all")
public class BasPlantCategoryServiceImpl extends ServiceImpl<BasPlantCategoryMapper,BasPlantCategory>  implements IBasPlantCategoryService {
    @Autowired
    private BasPlantCategoryMapper basPlantCategoryMapper;

    /**
     * 查询工厂-擅长品类信息
     * 
     * @param plantCategorySid 工厂-擅长品类信息ID
     * @return 工厂-擅长品类信息
     */
    @Override
    public BasPlantCategory selectBasPlantCategoryById(Long plantCategorySid) {
        return basPlantCategoryMapper.selectBasPlantCategoryById(plantCategorySid);
    }

    /**
     * 查询工厂-擅长品类信息列表
     * 
     * @param basPlantCategory 工厂-擅长品类信息
     * @return 工厂-擅长品类信息
     */
    @Override
    public List<BasPlantCategory> selectBasPlantCategoryList(BasPlantCategory basPlantCategory) {
        return basPlantCategoryMapper.selectBasPlantCategoryList(basPlantCategory);
    }

    /**
     * 新增工厂-擅长品类信息
     * 需要注意编码重复校验
     * @param basPlantCategory 工厂-擅长品类信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasPlantCategory(BasPlantCategory basPlantCategory) {
        return basPlantCategoryMapper.insert(basPlantCategory);
    }

    /**
     * 修改工厂-擅长品类信息
     * 
     * @param basPlantCategory 工厂-擅长品类信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasPlantCategory(BasPlantCategory basPlantCategory) {
        return basPlantCategoryMapper.updateById(basPlantCategory);
    }

    /**
     * 批量删除工厂-擅长品类信息
     * 
     * @param plantCategorySids 需要删除的工厂-擅长品类信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasPlantCategoryByIds(List<Long> plantCategorySids) {
        return basPlantCategoryMapper.deleteBatchIds(plantCategorySids);
    }


}
