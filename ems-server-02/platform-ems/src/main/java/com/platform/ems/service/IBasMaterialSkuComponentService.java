package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasMaterialSkuComponent;

/**
 * 商品SKU实测成分Service接口
 * 
 * @author linhongwei
 * @date 2021-03-20
 */
public interface IBasMaterialSkuComponentService extends IService<BasMaterialSkuComponent>{
    /**
     * 查询商品SKU实测成分
     * 
     * @param clientId 商品SKU实测成分ID
     * @return 商品SKU实测成分
     */
    public BasMaterialSkuComponent selectBasMaterialSkuComponentById(String clientId);

    /**
     * 查询商品SKU实测成分列表
     * 
     * @param basMaterialSkuComponent 商品SKU实测成分
     * @return 商品SKU实测成分集合
     */
    public List<BasMaterialSkuComponent> selectBasMaterialSkuComponentList(BasMaterialSkuComponent basMaterialSkuComponent);

    /**
     * 新增商品SKU实测成分
     * 
     * @param basMaterialSkuComponent 商品SKU实测成分
     * @return 结果
     */
    public int insertBasMaterialSkuComponent(BasMaterialSkuComponent basMaterialSkuComponent);

    /**
     * 修改商品SKU实测成分
     * 
     * @param basMaterialSkuComponent 商品SKU实测成分
     * @return 结果
     */
    public int updateBasMaterialSkuComponent(BasMaterialSkuComponent basMaterialSkuComponent);

    /**
     * 批量删除商品SKU实测成分
     * 
     * @param clientIds 需要删除的商品SKU实测成分ID
     * @return 结果
     */
    public int deleteBasMaterialSkuComponentByIds(List<String> clientIds);

}
