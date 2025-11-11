package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.CosProductCostLabor;
import com.platform.ems.mapper.CosProductCostLaborMapper;
import com.platform.ems.service.ICosProductCostLaborService;

/**
 * 商品成本核算-工价成本明细Service业务层处理
 * 
 * @author qhq
 * @date 2021-04-02
 */
@Service
@SuppressWarnings("all")
public class CosProductCostLaborServiceImpl extends ServiceImpl<CosProductCostLaborMapper,CosProductCostLabor>  implements ICosProductCostLaborService {
    @Autowired
    private CosProductCostLaborMapper cosProductCostLaborMapper;

    /**
     * 查询商品成本核算-工价成本明细
     * 
     * @param productCostLaborSid 商品成本核算-工价成本明细ID
     * @return 商品成本核算-工价成本明细
     */
    @Override
    public CosProductCostLabor selectCosProductCostLaborById(Long productCostLaborSid) {
        return cosProductCostLaborMapper.selectCosProductCostLaborById(productCostLaborSid);
    }

    /**
     * 查询商品成本核算-工价成本明细列表
     * 
     * @param cosProductCostLabor 商品成本核算-工价成本明细
     * @return 商品成本核算-工价成本明细
     */
    @Override
    public List<CosProductCostLabor> selectCosProductCostLaborList(CosProductCostLabor cosProductCostLabor) {
        return cosProductCostLaborMapper.selectCosProductCostLaborList(cosProductCostLabor);
    }

    /**
     * 新增商品成本核算-工价成本明细
     * 需要注意编码重复校验
     * @param cosProductCostLabor 商品成本核算-工价成本明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCosProductCostLabor(CosProductCostLabor cosProductCostLabor) {
        return cosProductCostLaborMapper.insert(cosProductCostLabor);
    }

    /**
     * 修改商品成本核算-工价成本明细
     * 
     * @param cosProductCostLabor 商品成本核算-工价成本明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCosProductCostLabor(CosProductCostLabor cosProductCostLabor) {
        return cosProductCostLaborMapper.updateById(cosProductCostLabor);
    }

    /**
     * 批量删除商品成本核算-工价成本明细
     * 
     * @param productCostLaborSids 需要删除的商品成本核算-工价成本明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCosProductCostLaborByIds(List<Long> productCostLaborSids) {
        return cosProductCostLaborMapper.deleteBatchIds(productCostLaborSids);
    }


}
