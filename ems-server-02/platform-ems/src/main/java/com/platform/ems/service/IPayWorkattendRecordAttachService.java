package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PayWorkattendRecordAttach;

import java.util.List;

/**
 * 考勤信息-附件Service接口
 *
 * @author linhongwei
 * @date 2021-09-14
 */
public interface IPayWorkattendRecordAttachService extends IService<PayWorkattendRecordAttach> {
    /**
     * 查询考勤信息-附件
     *
     * @param attachmentSid 考勤信息-附件ID
     * @return 考勤信息-附件
     */
    public PayWorkattendRecordAttach selectPayWorkattendRecordAttachById(Long attachmentSid);

    /**
     * 查询考勤信息-附件列表
     *
     * @param payWorkattendRecordAttach 考勤信息-附件
     * @return 考勤信息-附件集合
     */
    public List<PayWorkattendRecordAttach> selectPayWorkattendRecordAttachList(PayWorkattendRecordAttach payWorkattendRecordAttach);

    /**
     * 新增考勤信息-附件
     *
     * @param payWorkattendRecordAttach 考勤信息-附件
     * @return 结果
     */
    public int insertPayWorkattendRecordAttach(PayWorkattendRecordAttach payWorkattendRecordAttach);

    /**
     * 修改考勤信息-附件
     *
     * @param payWorkattendRecordAttach 考勤信息-附件
     * @return 结果
     */
    public int updatePayWorkattendRecordAttach(PayWorkattendRecordAttach payWorkattendRecordAttach);

    /**
     * 变更考勤信息-附件
     *
     * @param payWorkattendRecordAttach 考勤信息-附件
     * @return 结果
     */
    public int changePayWorkattendRecordAttach(PayWorkattendRecordAttach payWorkattendRecordAttach);

    /**
     * 批量删除考勤信息-附件
     *
     * @param attachmentSids 需要删除的考勤信息-附件ID
     * @return 结果
     */
    public int deletePayWorkattendRecordAttachByIds(List<Long> attachmentSids);

}
