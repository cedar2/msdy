package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasMaterialSkuDown;

/**
 * 商品SKU羽绒充绒量Service接口
 * 
 * @author linhongwei
 * @date 2021-03-20
 */
public interface IBasMaterialSkuDownService extends IService<BasMaterialSkuDown>{
    /**
     * 查询商品SKU羽绒充绒量
     * 
     * @param clientId 商品SKU羽绒充绒量ID
     * @return 商品SKU羽绒充绒量
     */
    public BasMaterialSkuDown selectBasMaterialSkuDownById(String clientId);

    /**
     * 查询商品SKU羽绒充绒量列表
     * 
     * @param basMaterialSkuDown 商品SKU羽绒充绒量
     * @return 商品SKU羽绒充绒量集合
     */
    public List<BasMaterialSkuDown> selectBasMaterialSkuDownList(BasMaterialSkuDown basMaterialSkuDown);

    /**
     * 新增商品SKU羽绒充绒量
     * 
     * @param basMaterialSkuDown 商品SKU羽绒充绒量
     * @return 结果
     */
    public int insertBasMaterialSkuDown(BasMaterialSkuDown basMaterialSkuDown);

    /**
     * 修改商品SKU羽绒充绒量
     * 
     * @param basMaterialSkuDown 商品SKU羽绒充绒量
     * @return 结果
     */
    public int updateBasMaterialSkuDown(BasMaterialSkuDown basMaterialSkuDown);

    /**
     * 批量删除商品SKU羽绒充绒量
     * 
     * @param clientIds 需要删除的商品SKU羽绒充绒量ID
     * @return 结果
     */
    public int deleteBasMaterialSkuDownByIds(List<String> clientIds);

}
