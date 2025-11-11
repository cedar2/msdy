package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.CosProductCostBom;

/**
 * 商品成本核算-BOM主Service接口
 * 
 * @author qhq
 * @date 2021-04-25
 */
public interface ICosProductCostBomService extends IService<CosProductCostBom>{
    /**
     * 查询商品成本核算-BOM主
     * 
     * @param productCostBomSid 商品成本核算-BOM主ID
     * @return 商品成本核算-BOM主
     */
    public CosProductCostBom selectCosProductCostBomById(Long productCostBomSid);

    /**
     * 查询商品成本核算-BOM主列表
     * 
     * @param cosProductCostBom 商品成本核算-BOM主
     * @return 商品成本核算-BOM主集合
     */
    public List<CosProductCostBom> selectCosProductCostBomList(CosProductCostBom cosProductCostBom);

    /**
     * 新增商品成本核算-BOM主
     * 
     * @param cosProductCostBom 商品成本核算-BOM主
     * @return 结果
     */
    public int insertCosProductCostBom(CosProductCostBom cosProductCostBom);

    /**
     * 修改商品成本核算-BOM主
     * 
     * @param cosProductCostBom 商品成本核算-BOM主
     * @return 结果
     */
    public int updateCosProductCostBom(CosProductCostBom cosProductCostBom);

    /**
     * 批量删除商品成本核算-BOM主
     * 
     * @param productCostBomSids 需要删除的商品成本核算-BOM主ID
     * @return 结果
     */
    public int deleteCosProductCostBomByIds(List<Long>  productCostBomSids);

}