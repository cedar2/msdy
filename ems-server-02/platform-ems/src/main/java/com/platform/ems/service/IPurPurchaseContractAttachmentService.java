package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.PurPurchaseContractAttachment;

/**
 * 采购合同信息-附件Service接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IPurPurchaseContractAttachmentService extends IService<PurPurchaseContractAttachment>{
    /**
     * 查询采购合同信息-附件
     *
     * @param purchaseContractAttachmentSid 采购合同信息-附件ID
     * @return 采购合同信息-附件
     */
    public PurPurchaseContractAttachment selectPurPurchaseContractAttachmentById(Long purchaseContractAttachmentSid);

    /**
     * 查询采购合同信息-附件列表
     *
     * @param purPurchaseContractAttachment 采购合同信息-附件
     * @return 采购合同信息-附件集合
     */
    public List<PurPurchaseContractAttachment> selectPurPurchaseContractAttachmentList(PurPurchaseContractAttachment purPurchaseContractAttachment);

    /**
     * 采购合同查询页面上传附件前的校验
     *
     * @param purPurchaseContractAttachment 采购合同附件信息
     * @return 结果
     */
    public AjaxResult check(PurPurchaseContractAttachment purPurchaseContractAttachment);

    /**
     * 新增采购合同信息-附件
     *
     * @param purPurchaseContractAttachment 采购合同信息-附件
     * @return 结果
     */
    public int insertPurPurchaseContractAttachment(PurPurchaseContractAttachment purPurchaseContractAttachment);

    /**
     * 修改采购合同信息-附件
     *
     * @param purPurchaseContractAttachment 采购合同信息-附件
     * @return 结果
     */
    public int updatePurPurchaseContractAttachment(PurPurchaseContractAttachment purPurchaseContractAttachment);

    /**
     * 变更采购合同信息-附件
     *
     * @param purPurchaseContractAttachment 采购合同信息-附件
     * @return 结果
     */
    public int changePurPurchaseContractAttachment(PurPurchaseContractAttachment purPurchaseContractAttachment);

    /**
     * 批量删除采购合同信息-附件
     *
     * @param purchaseContractAttachmentSids 需要删除的采购合同信息-附件ID
     * @return 结果
     */
    public int deletePurPurchaseContractAttachmentByIds(List<Long> purchaseContractAttachmentSids);

}
