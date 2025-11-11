package com.platform.ems.service.impl;

import com.platform.ems.domain.CosProductCostAttachment;
import com.platform.ems.mapper.CosProductCostAttachmentMapper;
import com.platform.ems.service.ICosProductCostAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品成本核算-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-02-26
 */
@Service
public class CosProductCostAttachmentServiceImpl implements ICosProductCostAttachmentService {
    @Autowired
    private CosProductCostAttachmentMapper cosProductCostAttachmentMapper;

    /**
     * 查询商品成本核算-附件
     *
     * @param productCostAttachmentSid 商品成本核算-附件ID
     * @return 商品成本核算-附件
     */
    @Override
    public CosProductCostAttachment selectCosProductCostAttachmentById(String productCostAttachmentSid) {
        return cosProductCostAttachmentMapper.selectCosProductCostAttachmentById(productCostAttachmentSid);
    }

    /**
     * 查询商品成本核算-附件列表
     *
     * @param cosProductCostAttachment 商品成本核算-附件
     * @return 商品成本核算-附件
     */
    @Override
    public List<CosProductCostAttachment> selectCosProductCostAttachmentList(CosProductCostAttachment cosProductCostAttachment) {
        return cosProductCostAttachmentMapper.selectCosProductCostAttachmentList(cosProductCostAttachment);
    }

    /**
     * 新增商品成本核算-附件
     *
     * @param cosProductCostAttachment 商品成本核算-附件
     * @return 结果
     */
    @Override
    public int insertCosProductCostAttachment(CosProductCostAttachment cosProductCostAttachment) {
        return cosProductCostAttachmentMapper.insertCosProductCostAttachment(cosProductCostAttachment);
    }

    /**
     * 修改商品成本核算-附件
     *
     * @param cosProductCostAttachment 商品成本核算-附件
     * @return 结果
     */
    @Override
    public int updateCosProductCostAttachment(CosProductCostAttachment cosProductCostAttachment) {
        return cosProductCostAttachmentMapper.updateCosProductCostAttachment(cosProductCostAttachment);
    }

    /**
     * 批量删除商品成本核算-附件
     *
     * @param productCostAttachmentSids 需要删除的商品成本核算-附件ID
     * @return 结果
     */
    @Override
    public int deleteCosProductCostAttachmentByIds(String[] productCostAttachmentSids) {
        return cosProductCostAttachmentMapper.deleteCosProductCostAttachmentByIds(productCostAttachmentSids);
    }

    /**
     * 删除商品成本核算-附件信息
     *
     * @param productCostAttachmentSid 商品成本核算-附件ID
     * @return 结果
     */
    @Override
    public int deleteCosProductCostAttachmentById(String productCostAttachmentSid) {
        return cosProductCostAttachmentMapper.deleteCosProductCostAttachmentById(productCostAttachmentSid);
    }
}
