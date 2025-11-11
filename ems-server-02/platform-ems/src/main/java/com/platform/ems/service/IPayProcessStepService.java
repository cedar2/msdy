package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PayProcessStep;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 通用道序Service接口
 *
 * @author linhongwei
 * @date 2021-09-07
 */
public interface IPayProcessStepService extends IService<PayProcessStep> {
    /**
     * 查询通用道序
     *
     * @param processStepSid 通用道序ID
     * @return 通用道序
     */
    public PayProcessStep selectPayProcessStepById(Long processStepSid);

    /**
     * 查询通用道序列表
     *
     * @param payProcessStep 通用道序
     * @return 通用道序集合
     */
    public List<PayProcessStep> selectPayProcessStepList(PayProcessStep payProcessStep);

    /**
     * 新增通用道序
     *
     * @param payProcessStep 通用道序
     * @return 结果
     */
    public int insertPayProcessStep(PayProcessStep payProcessStep);

    /**
     * 修改通用道序
     *
     * @param payProcessStep 通用道序
     * @return 结果
     */
    public int updatePayProcessStep(PayProcessStep payProcessStep);

    /**
     * 变更通用道序
     *
     * @param payProcessStep 通用道序
     * @return 结果
     */
    public int changePayProcessStep(PayProcessStep payProcessStep);

    /**
     * 批量删除通用道序
     *
     * @param processStepSids 需要删除的通用道序ID
     * @return 结果
     */
    public int deletePayProcessStepByIds(List<Long> processStepSids);

    /**
     * 启用/停用
     *
     * @param payProcessStep
     * @return
     */
    int changeStatus(PayProcessStep payProcessStep);

    /**
     * 更改确认状态
     *
     * @param payProcessStep
     * @return
     */
    int check(PayProcessStep payProcessStep);

    /**
     * 通用道序停用校验
     */
    PayProcessStep disableVerify(PayProcessStep payProcessStep);

    /**
     * 导入通用道序
     */
    EmsResultEntity importData(MultipartFile file);
}
