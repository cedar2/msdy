package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.PurPurchaseOrderAttachment;

/**
 * 采购订单-附件Service接口
 *
 * @author linhongwei
 * @date 2021-04-08
 */
public interface IPurPurchaseOrderAttachmentService extends IService<PurPurchaseOrderAttachment>{
    /**
     * 查询采购订单-附件
     *
     * @param purchaseOrderAttachmentSid 采购订单-附件ID
     * @return 采购订单-附件
     */
    public PurPurchaseOrderAttachment selectPurPurchaseOrderAttachmentById(Long purchaseOrderAttachmentSid);

    /**
     * 查询采购订单-附件列表
     *
     * @param purPurchaseOrderAttachment 采购订单-附件
     * @return 采购订单-附件集合
     */
    public List<PurPurchaseOrderAttachment> selectPurPurchaseOrderAttachmentList(PurPurchaseOrderAttachment purPurchaseOrderAttachment);

    /**
     * 新增采购订单-附件
     *
     * @param purPurchaseOrderAttachment 采购订单-附件
     * @return 结果
     */
    public int insertPurPurchaseOrderAttachment(PurPurchaseOrderAttachment purPurchaseOrderAttachment);

    /**
     * 修改采购订单-附件
     *
     * @param purPurchaseOrderAttachment 采购订单-附件
     * @return 结果
     */
    public int updatePurPurchaseOrderAttachment(PurPurchaseOrderAttachment purPurchaseOrderAttachment);

    /**
     * 批量删除采购订单-附件
     *
     * @param purchaseOrderAttachmentSids 需要删除的采购订单-附件ID
     * @return 结果
     */
    public int deletePurPurchaseOrderAttachmentByIds(List<Long> purchaseOrderAttachmentSids);

    /**
     * 查询页面上传附件前的校验
     *
     * @param purPurchaseOrderAttachment
     * @return
     */
    AjaxResult check(PurPurchaseOrderAttachment purPurchaseOrderAttachment);
}
