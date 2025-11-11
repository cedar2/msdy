package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.CosProductCostMaterial;
import com.platform.ems.mapper.CosProductCostMaterialMapper;
import com.platform.ems.service.ICosProductCostMaterialService;

/**
 * 商品成本核算-物料成本明细Service业务层处理
 * 
 * @author qhq
 * @date 2021-04-02
 */
@Service
@SuppressWarnings("all")
public class CosProductCostMaterialServiceImpl extends ServiceImpl<CosProductCostMaterialMapper,CosProductCostMaterial>  implements ICosProductCostMaterialService {
    @Autowired
    private CosProductCostMaterialMapper cosProductCostMaterialMapper;

    /**
     * 查询商品成本核算-物料成本明细
     * 
     * @param productCostMaterialSid 商品成本核算-物料成本明细ID
     * @return 商品成本核算-物料成本明细
     */
    @Override
    public CosProductCostMaterial selectCosProductCostMaterialById(Long productCostMaterialSid) {
      return null;
    }

    /**
     * 查询商品成本核算-物料成本明细列表
     * 
     * @param cosProductCostMaterial 商品成本核算-物料成本明细
     * @return 商品成本核算-物料成本明细
     */
    @Override
    public List<CosProductCostMaterial> selectCosProductCostMaterialList(CosProductCostMaterial cosProductCostMaterial) {
        return cosProductCostMaterialMapper.selectCosProductCostMaterialList(cosProductCostMaterial);
    }

    /**
     * 新增商品成本核算-物料成本明细
     * 需要注意编码重复校验
     * @param cosProductCostMaterial 商品成本核算-物料成本明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCosProductCostMaterial(CosProductCostMaterial cosProductCostMaterial) {
        return cosProductCostMaterialMapper.insert(cosProductCostMaterial);
    }

    /**
     * 修改商品成本核算-物料成本明细
     * 
     * @param cosProductCostMaterial 商品成本核算-物料成本明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCosProductCostMaterial(CosProductCostMaterial cosProductCostMaterial) {
        return cosProductCostMaterialMapper.updateById(cosProductCostMaterial);
    }

    /**
     * 批量删除商品成本核算-物料成本明细
     * 
     * @param productCostMaterialSids 需要删除的商品成本核算-物料成本明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCosProductCostMaterialByIds(List<Long> productCostMaterialSids) {
        return cosProductCostMaterialMapper.deleteBatchIds(productCostMaterialSids);
    }


}
