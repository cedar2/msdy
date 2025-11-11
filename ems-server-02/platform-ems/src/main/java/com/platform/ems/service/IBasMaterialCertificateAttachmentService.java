package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasMaterialCertificateAttachment;

/**
 * 商品合格证洗唛-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-03-20
 */
public interface IBasMaterialCertificateAttachmentService extends IService<BasMaterialCertificateAttachment>{
    /**
     * 查询商品合格证洗唛-附件
     * 
     * @param materialCertificateAttachmentSid 商品合格证洗唛-附件ID
     * @return 商品合格证洗唛-附件
     */
    public BasMaterialCertificateAttachment selectBasMaterialCertificateAttachmentById(String materialCertificateAttachmentSid);

    /**
     * 查询商品合格证洗唛-附件列表
     * 
     * @param basMaterialCertificateAttachment 商品合格证洗唛-附件
     * @return 商品合格证洗唛-附件集合
     */
    public List<BasMaterialCertificateAttachment> selectBasMaterialCertificateAttachmentList(BasMaterialCertificateAttachment basMaterialCertificateAttachment);

    /**
     * 新增商品合格证洗唛-附件
     * 
     * @param basMaterialCertificateAttachment 商品合格证洗唛-附件
     * @return 结果
     */
    public int insertBasMaterialCertificateAttachment(BasMaterialCertificateAttachment basMaterialCertificateAttachment);

    /**
     * 修改商品合格证洗唛-附件
     * 
     * @param basMaterialCertificateAttachment 商品合格证洗唛-附件
     * @return 结果
     */
    public int updateBasMaterialCertificateAttachment(BasMaterialCertificateAttachment basMaterialCertificateAttachment);

    /**
     * 批量删除商品合格证洗唛-附件
     * 
     * @param materialCertificateAttachmentSids 需要删除的商品合格证洗唛-附件ID
     * @return 结果
     */
    public int deleteBasMaterialCertificateAttachmentByIds(List<String> materialCertificateAttachmentSids);

}
