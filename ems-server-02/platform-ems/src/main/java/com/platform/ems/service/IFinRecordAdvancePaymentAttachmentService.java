package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinRecordAdvancePaymentAttachment;

/**
 * 供应商业务台账-附件-预付Service接口
 * 
 * @author linhongwei
 * @date 2021-05-29
 */
public interface IFinRecordAdvancePaymentAttachmentService extends IService<FinRecordAdvancePaymentAttachment>{
    /**
     * 查询供应商业务台账-附件-预付
     * 
     * @param recordAdvancePaymentAttachmentSid 供应商业务台账-附件-预付ID
     * @return 供应商业务台账-附件-预付
     */
    public FinRecordAdvancePaymentAttachment selectFinRecordAdvancePaymentAttachmentById(Long recordAdvancePaymentAttachmentSid);

    /**
     * 查询供应商业务台账-附件-预付列表
     * 
     * @param finRecordAdvancePaymentAttachment 供应商业务台账-附件-预付
     * @return 供应商业务台账-附件-预付集合
     */
    public List<FinRecordAdvancePaymentAttachment> selectFinRecordAdvancePaymentAttachmentList(FinRecordAdvancePaymentAttachment finRecordAdvancePaymentAttachment);

    /**
     * 新增供应商业务台账-附件-预付
     * 
     * @param finRecordAdvancePaymentAttachment 供应商业务台账-附件-预付
     * @return 结果
     */
    public int insertFinRecordAdvancePaymentAttachment(FinRecordAdvancePaymentAttachment finRecordAdvancePaymentAttachment);

    /**
     * 修改供应商业务台账-附件-预付
     * 
     * @param finRecordAdvancePaymentAttachment 供应商业务台账-附件-预付
     * @return 结果
     */
    public int updateFinRecordAdvancePaymentAttachment(FinRecordAdvancePaymentAttachment finRecordAdvancePaymentAttachment);

    /**
     * 变更供应商业务台账-附件-预付
     *
     * @param finRecordAdvancePaymentAttachment 供应商业务台账-附件-预付
     * @return 结果
     */
    public int changeFinRecordAdvancePaymentAttachment(FinRecordAdvancePaymentAttachment finRecordAdvancePaymentAttachment);

    /**
     * 批量删除供应商业务台账-附件-预付
     * 
     * @param recordAdvancePaymentAttachmentSids 需要删除的供应商业务台账-附件-预付ID
     * @return 结果
     */
    public int deleteFinRecordAdvancePaymentAttachmentByIds(List<Long>  recordAdvancePaymentAttachmentSids);

    /**
    * 启用/停用
    * @param finRecordAdvancePaymentAttachment
    * @return
    */
    int changeStatus(FinRecordAdvancePaymentAttachment finRecordAdvancePaymentAttachment);

    /**
     * 更改确认状态
     * @param finRecordAdvancePaymentAttachment
     * @return
     */
    int check(FinRecordAdvancePaymentAttachment finRecordAdvancePaymentAttachment);

}
