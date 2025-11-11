package com.platform.ems.service;

import java.util.List;
import com.platform.ems.domain.PurPurchasePriceAttachment;
import com.platform.ems.domain.dto.response.PurPurchasePriceAttachmentResponse;

/**
 * 采购价信息-附件Service接口
 * 
 * @author ChenPinzhen
 * @date 2021-02-04
 */
public interface IPurPurchasePriceAttachmentService {
    /**
     * 查询采购价信息-附件
     * 
     * @param purchasePriceAttachmentSid 采购价信息-附件ID
     * @return 采购价信息-附件
     */
    public PurPurchasePriceAttachment selectPurPurchasePriceAttachmentById(String purchasePriceAttachmentSid);

    /**
     * 查询采购价信息-附件列表
     * 
     * @param purPurchasePriceAttachment 采购价信息-附件
     * @return 采购价信息-附件集合
     */
    public List<PurPurchasePriceAttachmentResponse> selectPurPurchasePriceAttachmentList(PurPurchasePriceAttachment purPurchasePriceAttachment);

    /**
     * 新增采购价信息-附件
     * 
     * @param purPurchasePriceAttachment 采购价信息-附件
     * @return 结果
     */
    public int insertPurPurchasePriceAttachment(PurPurchasePriceAttachment purPurchasePriceAttachment);

    /**
     * 修改采购价信息-附件
     * 
     * @param purPurchasePriceAttachment 采购价信息-附件
     * @return 结果
     */
    public int updatePurPurchasePriceAttachment(PurPurchasePriceAttachment purPurchasePriceAttachment);

    /**
     * 批量删除采购价信息-附件
     * 
     * @param purchasePriceAttachmentSids 需要删除的采购价信息-附件ID
     * @return 结果
     */
    public int deletePurPurchasePriceAttachmentByIds(String[] purchasePriceAttachmentSids);

    /**
     * 删除采购价信息-附件信息
     * 
     * @param purchasePriceAttachmentSid 采购价信息-附件ID
     * @return 结果
     */
    public int deletePurPurchasePriceAttachmentById(String purchasePriceAttachmentSid);
}
