package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.BasMaterialSku;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物料&商品-SKU明细Mapper接口
 *
 * @author linhongwei
 * @date 2021-03-12
 */
public interface BasMaterialSkuMapper extends BaseMapper<BasMaterialSku> {


    BasMaterialSku selectBasMaterialSkuById(String materialSkuSid);
    List<BasMaterialSku> getSkuByCode(String materialCode);
    /**
     * 查询物料sku列表
     * @param basMaterialSku
     * @return
     */
    List<BasMaterialSku> selectBasMaterialSkuListByNameSort(BasMaterialSku basMaterialSku);

    /**
     * 查询物料sku列表
     * @param basMaterialSku
     * @return
     */
    List<BasMaterialSku> selectBasMaterialSkuList(BasMaterialSku basMaterialSku);

    /**
     * 按 款色 查询
     *
     * @param basMaterialSku 物料&商品
     * @return 物料&商品-SKU明细集合
     */
    List<BasMaterial> selectBasMaterialSku1List(BasMaterialSku basMaterialSku);

    List<BasMaterialSku> selectBomBasMaterialSkuList(Long materialSid);

    List<BasMaterial> getBasMaterialSkuList(BasMaterial basMaterial);

   List<BasMaterialSku> getskuList(Long materialSid);

    /**
     * 添加多个
     *
     * @param list List BasMaterialSku
     * @return int
     */
    int inserts(@Param("list") List<BasMaterialSku> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasMaterialSku
     * @return int
     */
    int updateAllById(BasMaterialSku entity);

    /**
     * 更新多个
     *
     * @param list List BasMaterialSku
     * @return int
     */
    int updatesAllById(@Param("list") List<BasMaterialSku> list);


    void deleteBasMaterialSkuByIds(@Param("array")Long[] materialSids);

    /**
     * 查询物料sku明细报表
     * @param basMaterialSku
     * @return
     */
    List<BasMaterialSku> getReportForm(BasMaterialSku basMaterialSku);
}
