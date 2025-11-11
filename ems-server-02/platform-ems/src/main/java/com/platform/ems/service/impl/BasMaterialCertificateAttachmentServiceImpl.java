package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.BasMaterialCertificateAttachmentMapper;
import com.platform.ems.domain.BasMaterialCertificateAttachment;
import com.platform.ems.service.IBasMaterialCertificateAttachmentService;

/**
 * 商品合格证洗唛-附件Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-03-20
 */
@Service
@SuppressWarnings("all")
public class BasMaterialCertificateAttachmentServiceImpl extends ServiceImpl<BasMaterialCertificateAttachmentMapper,BasMaterialCertificateAttachment>  implements IBasMaterialCertificateAttachmentService {
    @Autowired
    private BasMaterialCertificateAttachmentMapper basMaterialCertificateAttachmentMapper;

    /**
     * 查询商品合格证洗唛-附件
     * 
     * @param materialCertificateAttachmentSid 商品合格证洗唛-附件ID
     * @return 商品合格证洗唛-附件
     */
    @Override
    public BasMaterialCertificateAttachment selectBasMaterialCertificateAttachmentById(String materialCertificateAttachmentSid) {
        return basMaterialCertificateAttachmentMapper.selectBasMaterialCertificateAttachmentById(materialCertificateAttachmentSid);
    }

    /**
     * 查询商品合格证洗唛-附件列表
     * 
     * @param basMaterialCertificateAttachment 商品合格证洗唛-附件
     * @return 商品合格证洗唛-附件
     */
    @Override
    public List<BasMaterialCertificateAttachment> selectBasMaterialCertificateAttachmentList(BasMaterialCertificateAttachment basMaterialCertificateAttachment) {
        return basMaterialCertificateAttachmentMapper.selectBasMaterialCertificateAttachmentList(basMaterialCertificateAttachment);
    }

    /**
     * 新增商品合格证洗唛-附件
     * 需要注意编码重复校验
     * @param basMaterialCertificateAttachment 商品合格证洗唛-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasMaterialCertificateAttachment(BasMaterialCertificateAttachment basMaterialCertificateAttachment) {
        return basMaterialCertificateAttachmentMapper.insert(basMaterialCertificateAttachment);
    }

    /**
     * 修改商品合格证洗唛-附件
     * 
     * @param basMaterialCertificateAttachment 商品合格证洗唛-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasMaterialCertificateAttachment(BasMaterialCertificateAttachment basMaterialCertificateAttachment) {
        return basMaterialCertificateAttachmentMapper.updateById(basMaterialCertificateAttachment);
    }

    /**
     * 批量删除商品合格证洗唛-附件
     * 
     * @param materialCertificateAttachmentSids 需要删除的商品合格证洗唛-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasMaterialCertificateAttachmentByIds(List<String> materialCertificateAttachmentSids) {
        return basMaterialCertificateAttachmentMapper.deleteBatchIds(materialCertificateAttachmentSids);
    }


}
