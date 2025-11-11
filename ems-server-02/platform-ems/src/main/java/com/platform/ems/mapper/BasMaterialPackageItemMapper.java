package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasMaterialPackageItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 常规辅料包-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-14
 */
public interface BasMaterialPackageItemMapper  extends BaseMapper<BasMaterialPackageItem> {


    BasMaterialPackageItem selectBasMaterialPackageItemById(String materialPackItemSid);

    List<BasMaterialPackageItem> selectBasMaterialPackageItemList(BasMaterialPackageItem basMaterialPackageItem);

    /**
     * 添加多个
     * @param list List BasMaterialPackageItem
     * @return int
     */
    int inserts(@Param("list") List<BasMaterialPackageItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasMaterialPackageItem
    * @return int
    */
    int updateAllById(BasMaterialPackageItem entity);

    /**
     * 更新多个
     * @param list List BasMaterialPackageItem
     * @return int
     */
    int updatesAllById(@Param("list") List<BasMaterialPackageItem> list);

    /**
     * 根据主表sid查询辅料包list
     * @param basMaterialPackageItem
     * @return
     */
    List<BasMaterialPackageItem> getMaterialPackageItemList(BasMaterialPackageItem basMaterialPackageItem);

}
