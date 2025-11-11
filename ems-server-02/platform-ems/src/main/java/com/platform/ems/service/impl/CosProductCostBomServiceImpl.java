package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.CosProductCostBom;
import com.platform.ems.mapper.CosProductCostBomMapper;
import com.platform.ems.service.ICosProductCostBomService;

/**
 * 商品成本核算-BOM主Service业务层处理
 * 
 * @author qhq
 * @date 2021-04-25
 */
@Service
@SuppressWarnings("all")
public class CosProductCostBomServiceImpl extends ServiceImpl<CosProductCostBomMapper,CosProductCostBom>  implements ICosProductCostBomService {
    @Autowired
    private CosProductCostBomMapper cosProductCostBomMapper;

    /**
     * 查询商品成本核算-BOM主
     * 
     * @param productCostBomSid 商品成本核算-BOM主ID
     * @return 商品成本核算-BOM主
     */
    @Override
    public CosProductCostBom selectCosProductCostBomById(Long productCostBomSid) {
        return cosProductCostBomMapper.selectCosProductCostBomById(productCostBomSid);
    }

    /**
     * 查询商品成本核算-BOM主列表
     * 
     * @param cosProductCostBom 商品成本核算-BOM主
     * @return 商品成本核算-BOM主
     */
    @Override
    public List<CosProductCostBom> selectCosProductCostBomList(CosProductCostBom cosProductCostBom) {
        return cosProductCostBomMapper.selectCosProductCostBomList(cosProductCostBom);
    }

    /**
     * 新增商品成本核算-BOM主
     * 需要注意编码重复校验
     * @param cosProductCostBom 商品成本核算-BOM主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCosProductCostBom(CosProductCostBom cosProductCostBom) {
        return cosProductCostBomMapper.insert(cosProductCostBom);
    }

    /**
     * 修改商品成本核算-BOM主
     * 
     * @param cosProductCostBom 商品成本核算-BOM主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCosProductCostBom(CosProductCostBom cosProductCostBom) {
        return cosProductCostBomMapper.updateById(cosProductCostBom);
    }

    /**
     * 批量删除商品成本核算-BOM主
     * 
     * @param productCostBomSids 需要删除的商品成本核算-BOM主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCosProductCostBomByIds(List<Long> productCostBomSids) {
        return cosProductCostBomMapper.deleteBatchIds(productCostBomSids);
    }


}
