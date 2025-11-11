package com.platform.ems.mapper;
import java.util.List;

import com.platform.ems.domain.BasPosition;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasMaterialPackage;

/**
 * 常规辅料包-主Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-14
 */
public interface BasMaterialPackageMapper  extends BaseMapper<BasMaterialPackage> {

    BasMaterialPackage selectBasMaterialPackageById(Long materialPackageSid);

    /**
     * 按条件查询
     * @param basMaterialPackage
     * @return List<BasMaterialPackage>
     */
    List<BasMaterialPackage> selectBasMaterialPackageList(BasMaterialPackage basMaterialPackage);

    /**
     * 添加多个
     * @param list List BasMaterialPackage
     * @return int
     */
    int inserts(@Param("list") List<BasMaterialPackage> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasMaterialPackage
    * @return int
    */
    int updateAllById(BasMaterialPackage entity);

    /**
     * 更新多个
     * @param list List BasMaterialPackage
     * @return int
     */
    int updatesAllById(@Param("list") List<BasMaterialPackage> list);
    
    /**
     * 查询所有辅料包sid、name用于下拉框
     * @return
     */
    List<BasMaterialPackage> getMaterialPackageList();


}
