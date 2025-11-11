package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.BasMaterialCertificateFieldValueMapper;
import com.platform.ems.domain.BasMaterialCertificateFieldValue;
import com.platform.ems.service.IBasMaterialCertificateFieldValueService;

/**
 * 商品合格证洗唛自定义字段-值Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-03-20
 */
@Service
@SuppressWarnings("all")
public class BasMaterialCertificateFieldValueServiceImpl extends ServiceImpl<BasMaterialCertificateFieldValueMapper,BasMaterialCertificateFieldValue>  implements IBasMaterialCertificateFieldValueService {
    @Autowired
    private BasMaterialCertificateFieldValueMapper basMaterialCertificateFieldValueMapper;

    /**
     * 查询商品合格证洗唛自定义字段-值
     * 
     * @param clientId 商品合格证洗唛自定义字段-值ID
     * @return 商品合格证洗唛自定义字段-值
     */
    @Override
    public BasMaterialCertificateFieldValue selectBasMaterialCertificateFieldValueById(String clientId) {
        return basMaterialCertificateFieldValueMapper.selectBasMaterialCertificateFieldValueById(clientId);
    }

    /**
     * 查询商品合格证洗唛自定义字段-值列表
     * 
     * @param basMaterialCertificateFieldValue 商品合格证洗唛自定义字段-值
     * @return 商品合格证洗唛自定义字段-值
     */
    @Override
    public List<BasMaterialCertificateFieldValue> selectBasMaterialCertificateFieldValueList(BasMaterialCertificateFieldValue basMaterialCertificateFieldValue) {
        return basMaterialCertificateFieldValueMapper.selectBasMaterialCertificateFieldValueList(basMaterialCertificateFieldValue);
    }

    /**
     * 新增商品合格证洗唛自定义字段-值
     * 需要注意编码重复校验
     * @param basMaterialCertificateFieldValue 商品合格证洗唛自定义字段-值
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasMaterialCertificateFieldValue(BasMaterialCertificateFieldValue basMaterialCertificateFieldValue) {
        return basMaterialCertificateFieldValueMapper.insert(basMaterialCertificateFieldValue);
    }

    /**
     * 修改商品合格证洗唛自定义字段-值
     * 
     * @param basMaterialCertificateFieldValue 商品合格证洗唛自定义字段-值
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasMaterialCertificateFieldValue(BasMaterialCertificateFieldValue basMaterialCertificateFieldValue) {
        return basMaterialCertificateFieldValueMapper.updateById(basMaterialCertificateFieldValue);
    }

    /**
     * 批量删除商品合格证洗唛自定义字段-值
     * 
     * @param clientIds 需要删除的商品合格证洗唛自定义字段-值ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasMaterialCertificateFieldValueByIds(List<String> clientIds) {
        return basMaterialCertificateFieldValueMapper.deleteBatchIds(clientIds);
    }


}
