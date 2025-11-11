package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PayProcessStepCompleteAttach;

import java.util.List;

/**
 * 计薪量申报-附件Service接口
 *
 * @author linhongwei
 * @date 2021-09-08
 */
public interface IPayProcessStepCompleteAttachService extends IService<PayProcessStepCompleteAttach> {
    /**
     * 查询计薪量申报-附件
     *
     * @param attachmentSid 计薪量申报-附件ID
     * @return 计薪量申报-附件
     */
    public PayProcessStepCompleteAttach selectPayProcessStepCompleteAttachById(Long attachmentSid);

    /**
     * 查询计薪量申报-附件列表
     *
     * @param payProcessStepCompleteAttach 计薪量申报-附件
     * @return 计薪量申报-附件集合
     */
    public List<PayProcessStepCompleteAttach> selectPayProcessStepCompleteAttachList(PayProcessStepCompleteAttach payProcessStepCompleteAttach);

    /**
     * 新增计薪量申报-附件
     *
     * @param payProcessStepCompleteAttach 计薪量申报-附件
     * @return 结果
     */
    public int insertPayProcessStepCompleteAttach(PayProcessStepCompleteAttach payProcessStepCompleteAttach);

    /**
     * 修改计薪量申报-附件
     *
     * @param payProcessStepCompleteAttach 计薪量申报-附件
     * @return 结果
     */
    public int updatePayProcessStepCompleteAttach(PayProcessStepCompleteAttach payProcessStepCompleteAttach);

    /**
     * 变更计薪量申报-附件
     *
     * @param payProcessStepCompleteAttach 计薪量申报-附件
     * @return 结果
     */
    public int changePayProcessStepCompleteAttach(PayProcessStepCompleteAttach payProcessStepCompleteAttach);

    /**
     * 批量删除计薪量申报-附件
     *
     * @param attachmentSids 需要删除的计薪量申报-附件ID
     * @return 结果
     */
    public int deletePayProcessStepCompleteAttachByIds(List<Long> attachmentSids);

}
