package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.BasMaterialSkuComponentMapper;
import com.platform.ems.domain.BasMaterialSkuComponent;
import com.platform.ems.service.IBasMaterialSkuComponentService;

/**
 * 商品SKU实测成分Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-03-20
 */
@Service
@SuppressWarnings("all")
public class BasMaterialSkuComponentServiceImpl extends ServiceImpl<BasMaterialSkuComponentMapper,BasMaterialSkuComponent>  implements IBasMaterialSkuComponentService {
    @Autowired
    private BasMaterialSkuComponentMapper basMaterialSkuComponentMapper;

    /**
     * 查询商品SKU实测成分
     * 
     * @param clientId 商品SKU实测成分ID
     * @return 商品SKU实测成分
     */
    @Override
    public BasMaterialSkuComponent selectBasMaterialSkuComponentById(String clientId) {
        return basMaterialSkuComponentMapper.selectBasMaterialSkuComponentById(clientId);
    }

    /**
     * 查询商品SKU实测成分列表
     * 
     * @param basMaterialSkuComponent 商品SKU实测成分
     * @return 商品SKU实测成分
     */
    @Override
    public List<BasMaterialSkuComponent> selectBasMaterialSkuComponentList(BasMaterialSkuComponent basMaterialSkuComponent) {
        return basMaterialSkuComponentMapper.selectBasMaterialSkuComponentList(basMaterialSkuComponent);
    }

    /**
     * 新增商品SKU实测成分
     * 需要注意编码重复校验
     * @param basMaterialSkuComponent 商品SKU实测成分
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasMaterialSkuComponent(BasMaterialSkuComponent basMaterialSkuComponent) {
        return basMaterialSkuComponentMapper.insert(basMaterialSkuComponent);
    }

    /**
     * 修改商品SKU实测成分
     * 
     * @param basMaterialSkuComponent 商品SKU实测成分
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasMaterialSkuComponent(BasMaterialSkuComponent basMaterialSkuComponent) {
        return basMaterialSkuComponentMapper.updateById(basMaterialSkuComponent);
    }

    /**
     * 批量删除商品SKU实测成分
     * 
     * @param clientIds 需要删除的商品SKU实测成分ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasMaterialSkuComponentByIds(List<String> clientIds) {
        return basMaterialSkuComponentMapper.deleteBatchIds(clientIds);
    }


}
