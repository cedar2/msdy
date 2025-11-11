package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.BasMaterialSkuDownMapper;
import com.platform.ems.domain.BasMaterialSkuDown;
import com.platform.ems.service.IBasMaterialSkuDownService;

/**
 * 商品SKU羽绒充绒量Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-03-20
 */
@Service
@SuppressWarnings("all")
public class BasMaterialSkuDownServiceImpl extends ServiceImpl<BasMaterialSkuDownMapper,BasMaterialSkuDown>  implements IBasMaterialSkuDownService {
    @Autowired
    private BasMaterialSkuDownMapper basMaterialSkuDownMapper;

    /**
     * 查询商品SKU羽绒充绒量
     * 
     * @param clientId 商品SKU羽绒充绒量ID
     * @return 商品SKU羽绒充绒量
     */
    @Override
    public BasMaterialSkuDown selectBasMaterialSkuDownById(String clientId) {
        return basMaterialSkuDownMapper.selectBasMaterialSkuDownById(clientId);
    }

    /**
     * 查询商品SKU羽绒充绒量列表
     * 
     * @param basMaterialSkuDown 商品SKU羽绒充绒量
     * @return 商品SKU羽绒充绒量
     */
    @Override
    public List<BasMaterialSkuDown> selectBasMaterialSkuDownList(BasMaterialSkuDown basMaterialSkuDown) {
        return basMaterialSkuDownMapper.selectBasMaterialSkuDownList(basMaterialSkuDown);
    }

    /**
     * 新增商品SKU羽绒充绒量
     * 需要注意编码重复校验
     * @param basMaterialSkuDown 商品SKU羽绒充绒量
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasMaterialSkuDown(BasMaterialSkuDown basMaterialSkuDown) {
        return basMaterialSkuDownMapper.insert(basMaterialSkuDown);
    }

    /**
     * 修改商品SKU羽绒充绒量
     * 
     * @param basMaterialSkuDown 商品SKU羽绒充绒量
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasMaterialSkuDown(BasMaterialSkuDown basMaterialSkuDown) {
        return basMaterialSkuDownMapper.updateById(basMaterialSkuDown);
    }

    /**
     * 批量删除商品SKU羽绒充绒量
     * 
     * @param clientIds 需要删除的商品SKU羽绒充绒量ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasMaterialSkuDownByIds(List<String> clientIds) {
        return basMaterialSkuDownMapper.deleteBatchIds(clientIds);
    }


}
