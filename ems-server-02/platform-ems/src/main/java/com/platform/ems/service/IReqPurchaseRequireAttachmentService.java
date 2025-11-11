package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ReqPurchaseRequireAttachment;

/**
 * 申购单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-04-06
 */
public interface IReqPurchaseRequireAttachmentService extends IService<ReqPurchaseRequireAttachment>{
    /**
     * 查询申购单-附件
     * 
     * @param purchaseRequireAttachmentSid 申购单-附件ID
     * @return 申购单-附件
     */
    public ReqPurchaseRequireAttachment selectReqPurchaseRequireAttachmentById(Long purchaseRequireAttachmentSid);

    /**
     * 查询申购单-附件列表
     * 
     * @param reqPurchaseRequireAttachment 申购单-附件
     * @return 申购单-附件集合
     */
    public List<ReqPurchaseRequireAttachment> selectReqPurchaseRequireAttachmentList(ReqPurchaseRequireAttachment reqPurchaseRequireAttachment);

    /**
     * 新增申购单-附件
     * 
     * @param reqPurchaseRequireAttachment 申购单-附件
     * @return 结果
     */
    public int insertReqPurchaseRequireAttachment(ReqPurchaseRequireAttachment reqPurchaseRequireAttachment);

    /**
     * 修改申购单-附件
     * 
     * @param reqPurchaseRequireAttachment 申购单-附件
     * @return 结果
     */
    public int updateReqPurchaseRequireAttachment(ReqPurchaseRequireAttachment reqPurchaseRequireAttachment);

    /**
     * 批量删除申购单-附件
     * 
     * @param purchaseRequireAttachmentSids 需要删除的申购单-附件ID
     * @return 结果
     */
    public int deleteReqPurchaseRequireAttachmentByIds(List<Long> purchaseRequireAttachmentSids);

}
