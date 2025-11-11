package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DevMakeSampleForm;
import com.platform.ems.domain.DevSampleReviewForm;

import java.util.List;

/**
 * 样品评审单Service接口
 *
 * @author linhongwei
 * @date 2022-03-23
 */
public interface IDevSampleReviewFormService extends IService<DevSampleReviewForm> {
    /**
     * 查询样品评审单
     *
     * @param sampleReviewFormSid 样品评审单ID
     * @return 样品评审单
     */
    public DevSampleReviewForm selectDevSampleReviewFormById(Long sampleReviewFormSid);

    /**
     * 查询样品评审单列表
     *
     * @param devSampleReviewForm 样品评审单
     * @return 样品评审单集合
     */
    public List<DevSampleReviewForm> selectDevSampleReviewFormList(DevSampleReviewForm devSampleReviewForm);

    /**
     * 新增样品评审单
     *
     * @param devSampleReviewForm 样品评审单
     * @return 结果
     */
    public DevSampleReviewForm insertDevSampleReviewForm(DevSampleReviewForm devSampleReviewForm);

    /**
     * 修改样品评审单
     *
     * @param devSampleReviewForm 样品评审单
     * @return 结果
     */
    public DevSampleReviewForm updateDevSampleReviewForm(DevSampleReviewForm devSampleReviewForm);

    /**
     * 变更样品评审单
     *
     * @param devSampleReviewForm 样品评审单
     * @return 结果
     */
    public int changeDevSampleReviewForm(DevSampleReviewForm devSampleReviewForm);

    /**
     * 批量删除样品评审单
     *
     * @param sampleReviewFormSids 需要删除的样品评审单ID
     * @return 结果
     */
    public int deleteDevSampleReviewFormByIds(List<Long> sampleReviewFormSids);

    /**
     * 更改确认状态
     *
     * @param devSampleReviewForm
     * @return
     */
    int check(DevSampleReviewForm devSampleReviewForm);

    int updateHandleStatus(DevSampleReviewForm devSampleReviewForm);

}
