package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasPlantCategory;

/**
 * 工厂-擅长品类信息Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-27
 */
public interface BasPlantCategoryMapper  extends BaseMapper<BasPlantCategory> {


    BasPlantCategory selectBasPlantCategoryById(Long plantCategorySid);

    List<BasPlantCategory> selectBasPlantCategoryList(BasPlantCategory basPlantCategory);

    /**
     * 添加多个
     * @param list List BasPlantCategory
     * @return int
     */
    int inserts(@Param("list") List<BasPlantCategory> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasPlantCategory
    * @return int
    */
    int updateAllById(BasPlantCategory entity);

    /**
     * 更新多个
     * @param list List BasPlantCategory
     * @return int
     */
    int updatesAllById(@Param("list") List<BasPlantCategory> list);


    void deletePlantCategoryByIds(@Param("array")String[] plantSids);
}
