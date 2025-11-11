package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasMaterialCertificateFieldValue;

/**
 * 商品合格证洗唛自定义字段-值Service接口
 * 
 * @author linhongwei
 * @date 2021-03-20
 */
public interface IBasMaterialCertificateFieldValueService extends IService<BasMaterialCertificateFieldValue>{
    /**
     * 查询商品合格证洗唛自定义字段-值
     * 
     * @param clientId 商品合格证洗唛自定义字段-值ID
     * @return 商品合格证洗唛自定义字段-值
     */
    public BasMaterialCertificateFieldValue selectBasMaterialCertificateFieldValueById(String clientId);

    /**
     * 查询商品合格证洗唛自定义字段-值列表
     * 
     * @param basMaterialCertificateFieldValue 商品合格证洗唛自定义字段-值
     * @return 商品合格证洗唛自定义字段-值集合
     */
    public List<BasMaterialCertificateFieldValue> selectBasMaterialCertificateFieldValueList(BasMaterialCertificateFieldValue basMaterialCertificateFieldValue);

    /**
     * 新增商品合格证洗唛自定义字段-值
     * 
     * @param basMaterialCertificateFieldValue 商品合格证洗唛自定义字段-值
     * @return 结果
     */
    public int insertBasMaterialCertificateFieldValue(BasMaterialCertificateFieldValue basMaterialCertificateFieldValue);

    /**
     * 修改商品合格证洗唛自定义字段-值
     * 
     * @param basMaterialCertificateFieldValue 商品合格证洗唛自定义字段-值
     * @return 结果
     */
    public int updateBasMaterialCertificateFieldValue(BasMaterialCertificateFieldValue basMaterialCertificateFieldValue);

    /**
     * 批量删除商品合格证洗唛自定义字段-值
     * 
     * @param clientIds 需要删除的商品合格证洗唛自定义字段-值ID
     * @return 结果
     */
    public int deleteBasMaterialCertificateFieldValueByIds(List<String> clientIds);

}
