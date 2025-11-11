package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PayProductProcessStep;
import com.platform.ems.domain.PayUpdateProductProcessStep;
import com.platform.ems.domain.base.EmsResultEntity;

/**
 * 商品道序变更-主Service接口
 *
 * @author chenkw
 * @date 2022-11-08
 */
public interface IPayUpdateProductProcessStepService extends IService<PayUpdateProductProcessStep> {
    /**
     * 查询商品道序变更-主
     *
     * @param updateProductProcessStepSid 商品道序变更-主ID
     * @return 商品道序变更-主
     */
    public PayUpdateProductProcessStep selectPayUpdateProductProcessStepById(Long updateProductProcessStepSid);

    /**
     * 查询商品道序变更-主列表
     *
     * @param payUpdateProductProcessStep 商品道序变更-主
     * @return 商品道序变更-主集合
     */
    public List<PayUpdateProductProcessStep> selectPayUpdateProductProcessStepList(PayUpdateProductProcessStep payUpdateProductProcessStep);

    /**
     * 新增商品道序变更-主
     *
     * @param payUpdateProductProcessStep 商品道序变更-主
     * @return 结果
     */
    public int insertPayUpdateProductProcessStep(PayUpdateProductProcessStep payUpdateProductProcessStep);

    /**
     * 【优化】【商品道序】同一道序，在不同商品道序中，若工价不一致，给予提醒
     * @param payUpdateProductProcessStep 商品道序-主
     * @return 结果
     */
    EmsResultEntity checkPrice(PayUpdateProductProcessStep payUpdateProductProcessStep);

    EmsResultEntity verifyPrice(PayUpdateProductProcessStep payUpdateProductProcessStep);

    /**
     * 修改商品道序变更-主
     *
     * @param payUpdateProductProcessStep 商品道序变更-主
     * @return 结果
     */
    public int updatePayUpdateProductProcessStep(PayUpdateProductProcessStep payUpdateProductProcessStep);

    /**
     * 商品道序变更提交更新
     *
     * @param payUpdateProductProcessStep 商品道序变更提交更新
     * @return 结果
     */
    public int updateStatus(PayUpdateProductProcessStep payUpdateProductProcessStep);

    /**
     * 变更商品道序变更-主
     *
     * @param payUpdateProductProcessStep 商品道序变更-主
     * @return 结果
     */
    public int changePayUpdateProductProcessStep(PayUpdateProductProcessStep payUpdateProductProcessStep);

    /**
     * 批量删除商品道序变更-主
     *
     * @param updateProductProcessStepSids 需要删除的商品道序变更-主ID
     * @return 结果
     */
    public int deletePayUpdateProductProcessStepByIds(List<Long> updateProductProcessStepSids);

    /**
     * 更改确认状态
     *
     * @param payUpdateProductProcessStep
     * @return
     */
    int check(PayUpdateProductProcessStep payUpdateProductProcessStep);

}
