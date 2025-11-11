package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.CosProductCostMaterial;

/**
 * 商品成本核算-物料成本明细Service接口
 * 
 * @author qhq
 * @date 2021-04-02
 */
public interface ICosProductCostMaterialService extends IService<CosProductCostMaterial>{
    /**
     * 查询商品成本核算-物料成本明细
     * 
     * @param productCostMaterialSid 商品成本核算-物料成本明细ID
     * @return 商品成本核算-物料成本明细
     */
    public CosProductCostMaterial selectCosProductCostMaterialById(Long productCostMaterialSid);

    /**
     * 查询商品成本核算-物料成本明细列表
     * 
     * @param cosProductCostMaterial 商品成本核算-物料成本明细
     * @return 商品成本核算-物料成本明细集合
     */
    public List<CosProductCostMaterial> selectCosProductCostMaterialList(CosProductCostMaterial cosProductCostMaterial);

    /**
     * 新增商品成本核算-物料成本明细
     * 
     * @param cosProductCostMaterial 商品成本核算-物料成本明细
     * @return 结果
     */
    public int insertCosProductCostMaterial(CosProductCostMaterial cosProductCostMaterial);

    /**
     * 修改商品成本核算-物料成本明细
     * 
     * @param cosProductCostMaterial 商品成本核算-物料成本明细
     * @return 结果
     */
    public int updateCosProductCostMaterial(CosProductCostMaterial cosProductCostMaterial);

    /**
     * 批量删除商品成本核算-物料成本明细
     * 
     * @param productCostMaterialSids 需要删除的商品成本核算-物料成本明细ID
     * @return 结果
     */
    public int deleteCosProductCostMaterialByIds(List<Long>  productCostMaterialSids);

}
