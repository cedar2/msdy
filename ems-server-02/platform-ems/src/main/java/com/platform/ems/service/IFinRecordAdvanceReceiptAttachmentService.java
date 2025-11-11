package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinRecordAdvanceReceiptAttachment;

/**
 * 客户业务台账-附件-预收Service接口
 * 
 * @author linhongwei
 * @date 2021-06-16
 */
public interface IFinRecordAdvanceReceiptAttachmentService extends IService<FinRecordAdvanceReceiptAttachment>{
    /**
     * 查询客户业务台账-附件-预收
     * 
     * @param recordAdvanceReceiptAttachmentSid 客户业务台账-附件-预收ID
     * @return 客户业务台账-附件-预收
     */
    public FinRecordAdvanceReceiptAttachment selectFinRecordAdvanceReceiptAttachmentById(Long recordAdvanceReceiptAttachmentSid);

    /**
     * 查询客户业务台账-附件-预收列表
     * 
     * @param finRecordAdvanceReceiptAttachment 客户业务台账-附件-预收
     * @return 客户业务台账-附件-预收集合
     */
    public List<FinRecordAdvanceReceiptAttachment> selectFinRecordAdvanceReceiptAttachmentList(FinRecordAdvanceReceiptAttachment finRecordAdvanceReceiptAttachment);

    /**
     * 新增客户业务台账-附件-预收
     * 
     * @param finRecordAdvanceReceiptAttachment 客户业务台账-附件-预收
     * @return 结果
     */
    public int insertFinRecordAdvanceReceiptAttachment(FinRecordAdvanceReceiptAttachment finRecordAdvanceReceiptAttachment);

    /**
     * 修改客户业务台账-附件-预收
     * 
     * @param finRecordAdvanceReceiptAttachment 客户业务台账-附件-预收
     * @return 结果
     */
    public int updateFinRecordAdvanceReceiptAttachment(FinRecordAdvanceReceiptAttachment finRecordAdvanceReceiptAttachment);

    /**
     * 变更客户业务台账-附件-预收
     *
     * @param finRecordAdvanceReceiptAttachment 客户业务台账-附件-预收
     * @return 结果
     */
    public int changeFinRecordAdvanceReceiptAttachment(FinRecordAdvanceReceiptAttachment finRecordAdvanceReceiptAttachment);

    /**
     * 批量删除客户业务台账-附件-预收
     * 
     * @param recordAdvanceReceiptAttachmentSids 需要删除的客户业务台账-附件-预收ID
     * @return 结果
     */
    public int deleteFinRecordAdvanceReceiptAttachmentByIds(List<Long>  recordAdvanceReceiptAttachmentSids);

    /**
    * 启用/停用
    * @param finRecordAdvanceReceiptAttachment
    * @return
    */
    int changeStatus(FinRecordAdvanceReceiptAttachment finRecordAdvanceReceiptAttachment);

    /**
     * 更改确认状态
     * @param finRecordAdvanceReceiptAttachment
     * @return
     */
    int check(FinRecordAdvanceReceiptAttachment finRecordAdvanceReceiptAttachment);

}
