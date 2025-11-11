package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PayProcessStepComplete;
import com.platform.ems.domain.PayProcessStepCompleteItem;
import com.platform.ems.domain.base.EmsResultEntity;

import java.util.List;

/**
 * 计薪量申报-主Service接口
 *
 * @author linhongwei
 * @date 2021-09-08
 */
public interface IPayProcessStepCompleteService extends IService<PayProcessStepComplete> {
    /**
     * 查询计薪量申报-主
     *
     * @param stepCompleteSid 计薪量申报-主ID
     * @return 计薪量申报-主
     */
    PayProcessStepComplete selectPayProcessStepCompleteById(Long stepCompleteSid);

    /**
     * 查询计薪量申报-主列表
     *
     * @param payProcessStepComplete 计薪量申报-主
     * @return 计薪量申报-主集合
     */
    List<PayProcessStepComplete> selectPayProcessStepCompleteList(PayProcessStepComplete payProcessStepComplete);

    /**
     * 新增计薪量申报-主
     *
     * @param payProcessStepComplete 计薪量申报-主
     * @return 结果
     */
    String insertPayProcessStepComplete(PayProcessStepComplete payProcessStepComplete);

    /**
     * 修改计薪量申报-主
     *
     * @param payProcessStepComplete 计薪量申报-主
     * @return 结果
     */
    int updatePayProcessStepComplete(PayProcessStepComplete payProcessStepComplete);

    /**
     * 变更计薪量申报-主
     *
     * @param payProcessStepComplete 计薪量申报-主
     * @return 结果
     */
    int changePayProcessStepComplete(PayProcessStepComplete payProcessStepComplete);

    /**
     * 批量删除计薪量申报-主
     *
     * @param stepCompleteSids 需要删除的计薪量申报-主ID
     * @return 结果
     */
    int deletePayProcessStepCompleteByIds(List<Long> stepCompleteSids);

    /**
     * 更改确认状态
     *
     * @param payProcessStepComplete
     * @return
     */
    int check(PayProcessStepComplete payProcessStepComplete);

    /**
     * 确认操作
     *
     * @param payProcessStepComplete
     * @return
     */
    EmsResultEntity confirm(PayProcessStepComplete payProcessStepComplete);

    /**
     * 累计计薪量申报
     */
    PayProcessStepCompleteItem getQuantity(PayProcessStepCompleteItem payProcessStepCompleteItem);

    /**
     * 单据提交校验
     */
    int verify(PayProcessStepComplete payProcessStepComplete);

    /**
     * 单据确认校验
     */
    EmsResultEntity checkVerify(PayProcessStepComplete payProcessStepComplete);

    /**
     * 查找是否已存在相同维度的计薪量
     */
    String verifyUnique(PayProcessStepComplete payProcessStepComplete);

}
