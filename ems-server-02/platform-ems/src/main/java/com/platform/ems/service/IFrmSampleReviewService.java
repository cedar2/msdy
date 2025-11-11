package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FrmSampleReview;
import com.platform.ems.domain.base.EmsResultEntity;

/**
 * 样品评审单Service接口
 *
 * @author chenkw
 * @date 2022-12-12
 */
public interface IFrmSampleReviewService extends IService<FrmSampleReview> {
    /**
     * 查询样品评审单
     *
     * @param sampleReviewSid 样品评审单ID
     * @return 样品评审单
     */
    public FrmSampleReview selectFrmSampleReviewById(Long sampleReviewSid);

    /**
     * 查询样品评审单列表
     *
     * @param frmSampleReview 样品评审单
     * @return 样品评审单集合
     */
    public List<FrmSampleReview> selectFrmSampleReviewList(FrmSampleReview frmSampleReview);

    /**
     * 新增样品评审单
     *
     * @param frmSampleReview 样品评审单
     * @return 结果
     */
    public int insertFrmSampleReview(FrmSampleReview frmSampleReview);

    /**
     * 修改样品评审单
     *
     * @param frmSampleReview 样品评审单
     * @return 结果
     */
    public int updateFrmSampleReview(FrmSampleReview frmSampleReview);

    /**
     * 变更样品评审单
     *
     * @param frmSampleReview 样品评审单
     * @return 结果
     */
    public int changeFrmSampleReview(FrmSampleReview frmSampleReview);

    /**
     * 批量删除样品评审单
     *
     * @param sampleReviewSids 需要删除的样品评审单ID
     * @return 结果
     */
    public int deleteFrmSampleReviewByIds(List<Long> sampleReviewSids);

    /**
     * 提交前校验
     *
     * @param frmSampleReview 样品评审单
     * @return 结果
     */
    public EmsResultEntity submitVerify(FrmSampleReview frmSampleReview);

    /**
     * 更改确认状态
     *
     * @param frmSampleReview
     * @return
     */
    int check(FrmSampleReview frmSampleReview);

}
