package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurOutsourcePurchaseOrderAttachment;

/**
 * 外发加工单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-05-17
 */
public interface IPurOutsourcePurchaseOrderAttachmentService extends IService<PurOutsourcePurchaseOrderAttachment>{
    /**
     * 查询外发加工单-附件
     * 
     * @param outsourcePurchaseOrderAttachmentSid 外发加工单-附件ID
     * @return 外发加工单-附件
     */
    public PurOutsourcePurchaseOrderAttachment selectPurOutsourcePurchaseOrderAttachmentById(Long outsourcePurchaseOrderAttachmentSid);

    /**
     * 查询外发加工单-附件列表
     * 
     * @param purOutsourcePurchaseOrderAttachment 外发加工单-附件
     * @return 外发加工单-附件集合
     */
    public List<PurOutsourcePurchaseOrderAttachment> selectPurOutsourcePurchaseOrderAttachmentList(PurOutsourcePurchaseOrderAttachment purOutsourcePurchaseOrderAttachment);

    /**
     * 新增外发加工单-附件
     * 
     * @param purOutsourcePurchaseOrderAttachment 外发加工单-附件
     * @return 结果
     */
    public int insertPurOutsourcePurchaseOrderAttachment(PurOutsourcePurchaseOrderAttachment purOutsourcePurchaseOrderAttachment);

    /**
     * 修改外发加工单-附件
     * 
     * @param purOutsourcePurchaseOrderAttachment 外发加工单-附件
     * @return 结果
     */
    public int updatePurOutsourcePurchaseOrderAttachment(PurOutsourcePurchaseOrderAttachment purOutsourcePurchaseOrderAttachment);

    /**
     * 变更外发加工单-附件
     *
     * @param purOutsourcePurchaseOrderAttachment 外发加工单-附件
     * @return 结果
     */
    public int changePurOutsourcePurchaseOrderAttachment(PurOutsourcePurchaseOrderAttachment purOutsourcePurchaseOrderAttachment);

    /**
     * 批量删除外发加工单-附件
     * 
     * @param outsourcePurchaseOrderAttachmentSids 需要删除的外发加工单-附件ID
     * @return 结果
     */
    public int deletePurOutsourcePurchaseOrderAttachmentByIds(List<Long> outsourcePurchaseOrderAttachmentSids);

    /**
     * 更改确认状态
     * @param purOutsourcePurchaseOrderAttachment
     * @return
     */
    int check(PurOutsourcePurchaseOrderAttachment purOutsourcePurchaseOrderAttachment);

}
