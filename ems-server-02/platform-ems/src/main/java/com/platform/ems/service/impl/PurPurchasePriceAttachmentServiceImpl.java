package com.platform.ems.service.impl;

import java.util.List;

import com.platform.ems.domain.dto.response.PurPurchasePriceAttachmentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.platform.ems.mapper.PurPurchasePriceAttachmentMapper;
import com.platform.ems.domain.PurPurchasePriceAttachment;
import com.platform.ems.service.IPurPurchasePriceAttachmentService;

/**
 * 采购价信息-附件Service业务层处理
 * 
 * @author ChenPinzhen
 * @date 2021-02-04
 */
@Service
public class PurPurchasePriceAttachmentServiceImpl implements IPurPurchasePriceAttachmentService {
    @Autowired
    private PurPurchasePriceAttachmentMapper purPurchasePriceAttachmentMapper;

    /**
     * 查询采购价信息-附件
     * 
     * @param purchasePriceAttachmentSid 采购价信息-附件ID
     * @return 采购价信息-附件
     */
    @Override
    public PurPurchasePriceAttachment selectPurPurchasePriceAttachmentById(String purchasePriceAttachmentSid) {
        return purPurchasePriceAttachmentMapper.selectPurPurchasePriceAttachmentById(purchasePriceAttachmentSid);
    }

    /**
     * 查询采购价信息-附件列表
     * 
     * @param purPurchasePriceAttachment 采购价信息-附件
     * @return 采购价信息-附件
     */
    @Override
    public List<PurPurchasePriceAttachmentResponse> selectPurPurchasePriceAttachmentList(PurPurchasePriceAttachment purPurchasePriceAttachment) {
        return purPurchasePriceAttachmentMapper.selectPurPurchasePriceAttachmentList(purPurchasePriceAttachment);
    }

    /**
     * 新增采购价信息-附件
     * 
     * @param purPurchasePriceAttachment 采购价信息-附件
     * @return 结果
     */
    @Override
    public int insertPurPurchasePriceAttachment(PurPurchasePriceAttachment purPurchasePriceAttachment) {
        return purPurchasePriceAttachmentMapper.insertPurPurchasePriceAttachment(purPurchasePriceAttachment);
    }

    /**
     * 修改采购价信息-附件
     * 
     * @param purPurchasePriceAttachment 采购价信息-附件
     * @return 结果
     */
    @Override
    public int updatePurPurchasePriceAttachment(PurPurchasePriceAttachment purPurchasePriceAttachment) {
        return purPurchasePriceAttachmentMapper.updatePurPurchasePriceAttachment(purPurchasePriceAttachment);
    }

    /**
     * 批量删除采购价信息-附件
     * 
     * @param purchasePriceAttachmentSids 需要删除的采购价信息-附件ID
     * @return 结果
     */
    @Override
    public int deletePurPurchasePriceAttachmentByIds(String[] purchasePriceAttachmentSids) {
        return purPurchasePriceAttachmentMapper.deletePurPurchasePriceAttachmentByIds(purchasePriceAttachmentSids);
    }

    /**
     * 删除采购价信息-附件信息
     * 
     * @param purchasePriceAttachmentSid 采购价信息-附件ID
     * @return 结果
     */
    @Override
    public int deletePurPurchasePriceAttachmentById(String purchasePriceAttachmentSid) {
        return purPurchasePriceAttachmentMapper.deletePurPurchasePriceAttachmentById(purchasePriceAttachmentSid);
    }
}
