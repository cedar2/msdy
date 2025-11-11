package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PayProductProcessStep;
import com.platform.ems.domain.PayProductProcessStepItem;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 商品道序-主Service接口
 *
 * @author linhongwei
 * @date 2021-09-08
 */
public interface IPayProductProcessStepService extends IService<PayProductProcessStep> {
    /**
     * 查询商品道序-主
     *
     * @param productProcessStepSid 商品道序-主ID
     * @return 商品道序-主
     */
    public PayProductProcessStep selectPayProductProcessStepById(Long productProcessStepSid);

    /**
     * 查询商品道序-主列表
     *
     * @param payProductProcessStep 商品道序-主
     * @return 商品道序-主集合
     */
    public List<PayProductProcessStep> selectPayProductProcessStepList(PayProductProcessStep payProductProcessStep);

    /**
     * 新增商品道序-主
     *
     * @param payProductProcessStep 商品道序-主
     * @return 结果
     */
    public PayProductProcessStep insertPayProductProcessStep(PayProductProcessStep payProductProcessStep);

    /**
     * 【优化】【商品道序】同一道序，在不同商品道序中，若工价不一致，给予提醒
     * @param payProductProcessStep 商品道序-主
     * @return 结果
     */
    EmsResultEntity checkPrice(PayProductProcessStep payProductProcessStep);

    /**
     * 修改商品道序-主
     *
     * @param payProductProcessStep 商品道序-主
     * @return 结果
     */
    public PayProductProcessStep updatePayProductProcessStep(PayProductProcessStep payProductProcessStep);

    /**
     * 变更商品道序-主
     *
     * @param payProductProcessStep 商品道序-主
     * @return 结果
     */
    public PayProductProcessStep changePayProductProcessStep(PayProductProcessStep payProductProcessStep);

    /**
     * 变更页面点暂存商品道序-主
     *
     * @param payProductProcessStep 商品道序-主
     * @return 结果
     */
    public PayProductProcessStep newUpdatePayProductProcessStep(PayProductProcessStep payProductProcessStep);

    /**
     * 批量删除商品道序-主
     *
     * @param productProcessStepSids 需要删除的商品道序-主ID
     * @return 结果
     */
    public int deletePayProductProcessStepByIds(List<Long> productProcessStepSids);

    /**
     * 更改确认状态
     *
     * @param payProductProcessStep
     * @return
     */
    int check(PayProductProcessStep payProductProcessStep);

    /**
     * 商品道序下拉框接口
     */
    List<PayProductProcessStep> getList(PayProductProcessStep payProductProcessStep);

    EmsResultEntity verifyPrice(PayProductProcessStep payProductProcessStep);

    /**
     * 添加专用道序时校验名称是否重复
     */
    PayProductProcessStepItem verifyProcess(PayProductProcessStepItem payProductProcessStepItem);

    /**
     * 导入商品道序
     */
    EmsResultEntity importData(MultipartFile file);

    /**
     * 导入商品道序 单款
     */
    EmsResultEntity importDataSingle(MultipartFile file);

    /**
     * 导入需要忽略并继续时直接写入
     */
    int importAddData(List<PayProductProcessStep> stepList);
}
