package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.CosProductCostLabor;

/**
 * 商品成本核算-工价成本明细Service接口
 * 
 * @author qhq
 * @date 2021-04-02
 */
public interface ICosProductCostLaborService extends IService<CosProductCostLabor>{
    /**
     * 查询商品成本核算-工价成本明细
     * 
     * @param productCostLaborSid 商品成本核算-工价成本明细ID
     * @return 商品成本核算-工价成本明细
     */
    public CosProductCostLabor selectCosProductCostLaborById(Long productCostLaborSid);

    /**
     * 查询商品成本核算-工价成本明细列表
     * 
     * @param cosProductCostLabor 商品成本核算-工价成本明细
     * @return 商品成本核算-工价成本明细集合
     */
    public List<CosProductCostLabor> selectCosProductCostLaborList(CosProductCostLabor cosProductCostLabor);

    /**
     * 新增商品成本核算-工价成本明细
     * 
     * @param cosProductCostLabor 商品成本核算-工价成本明细
     * @return 结果
     */
    public int insertCosProductCostLabor(CosProductCostLabor cosProductCostLabor);

    /**
     * 修改商品成本核算-工价成本明细
     * 
     * @param cosProductCostLabor 商品成本核算-工价成本明细
     * @return 结果
     */
    public int updateCosProductCostLabor(CosProductCostLabor cosProductCostLabor);

    /**
     * 批量删除商品成本核算-工价成本明细
     * 
     * @param productCostLaborSids 需要删除的商品成本核算-工价成本明细ID
     * @return 结果
     */
    public int deleteCosProductCostLaborByIds(List<Long>  productCostLaborSids);

}
