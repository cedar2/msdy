package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasMaterialCertificateField;
/**
 * 商品合格证洗唛自定义字段Service接口
 * 
 * @author linhongwei
 * @date 2021-03-31
 */
public interface IBasMaterialCertificateFieldService extends IService<BasMaterialCertificateField>{
    /**
     * 查询商品合格证洗唛自定义字段
     * 
     * @param materialCertificateFieldSid 商品合格证洗唛自定义字段ID
     * @return 商品合格证洗唛自定义字段
     */
    public BasMaterialCertificateField selectBasMaterialCertificateFieldById(Long materialCertificateFieldSid);

    /**
     * 查询商品合格证洗唛自定义字段列表
     * 
     * @param
     * @return 商品合格证洗唛自定义字段集合
     */
    public List<BasMaterialCertificateField> selectBasMaterialCertificateFieldList(BasMaterialCertificateField field);

    /**
     * 新增商品合格证洗唛自定义字段
     * 
     * @param basMaterialCertificateField 商品合格证洗唛自定义字段
     * @return 结果
     */
    public int insertBasMaterialCertificateField(BasMaterialCertificateField basMaterialCertificateField);

    /**
     * 修改商品合格证洗唛自定义字段
     * 
     * @param basMaterialCertificateField 商品合格证洗唛自定义字段
     * @return 结果
     */
    public int updateBasMaterialCertificateField(BasMaterialCertificateField basMaterialCertificateField);

    /**
     * 批量删除商品合格证洗唛自定义字段
     * 
     * @param materialCertificateFieldSids 需要删除的商品合格证洗唛自定义字段ID
     * @return 结果
     */
    public int deleteBasMaterialCertificateFieldByIds(List<Long> materialCertificateFieldSids);

    /**
     * 更改确认状态
     * @param basMaterialCertificateField
     * @return
     */
    int status(BasMaterialCertificateField basMaterialCertificateField);

    /**
     * 更改确认状态
     * @param basMaterialCertificateField
     * @return
     */
    int check(BasMaterialCertificateField basMaterialCertificateField);

}
