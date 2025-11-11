package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasMaterialCertificate;
import com.platform.ems.domain.dto.response.external.BasMaterialCertificateExternal;

import java.util.List;

/**
 * 商品合格证洗唛信息Service接口
 * 
 * @author linhongwei
 * @date 2021-03-19
 */
public interface IBasMaterialCertificateService extends IService<BasMaterialCertificate>{
    /**
     * 查询商品合格证洗唛信息--外部打印产用
     *
     * @param materialCertificateSid 商品合格证洗唛信息ID
     * @return 商品合格证洗唛信息
     */
    BasMaterialCertificateExternal selectForExternalById(Long materialCertificateSid);
    /**
     * 查询商品合格证洗唛信息
     * 
     * @param materialCertificateSid 商品合格证洗唛信息ID
     * @return 商品合格证洗唛信息
     */
    BasMaterialCertificate selectBasMaterialCertificateById(Long materialCertificateSid);

    /**
     * 查询商品合格证洗唛信息列表
     * 
     * @param basMaterialCertificate 商品合格证洗唛信息
     * @return 商品合格证洗唛信息集合
     */
    List<BasMaterialCertificate> selectBasMaterialCertificateList(BasMaterialCertificate basMaterialCertificate);

    /**
     * 新增商品合格证洗唛信息
     * 
     * @param basMaterialCertificate 商品合格证洗唛信息
     * @return 结果
     */
    int insertBasMaterialCertificate(BasMaterialCertificate basMaterialCertificate);

    /**
     * 修改商品合格证洗唛信息
     * 
     * @param basMaterialCertificate 商品合格证洗唛信息
     * @return 结果
     */
    int updateBasMaterialCertificate(BasMaterialCertificate basMaterialCertificate);

    /**
     * 批量删除商品合格证洗唛信息
     * 
     * @param materialCertificateSidList 需要删除的商品合格证洗唛信息ID
     * @return 结果
     */
    int deleteBasMaterialCertificateByIds(Long[] materialCertificateSidList);

    /**
     * 商品合格证洗唛信息确认
     */
    int confirm(BasMaterialCertificate basMaterialCertificate);

    /**
     * 商品合格证洗唛信息变更
     */
    int change(BasMaterialCertificate basMaterialCertificate);

//    /**
//     * 验证商品状态
//     */
//    BasMaterialCertificate checkPoint(Long materialSid);

}
