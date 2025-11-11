package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DevSampleReviewFormAttach;

import java.util.List;

/**
 * 样品评审单-附件Service接口
 *
 * @author linhongwei
 * @date 2022-03-23
 */
public interface IDevSampleReviewFormAttachService extends IService<DevSampleReviewFormAttach> {
    /**
     * 查询样品评审单-附件
     *
     * @param attachmentSid 样品评审单-附件ID
     * @return 样品评审单-附件
     */
    public DevSampleReviewFormAttach selectDevSampleReviewFormAttachById(Long attachmentSid);

    /**
     * 查询样品评审单-附件列表
     *
     * @param devSampleReviewFormAttach 样品评审单-附件
     * @return 样品评审单-附件集合
     */
    public List<DevSampleReviewFormAttach> selectDevSampleReviewFormAttachList(DevSampleReviewFormAttach devSampleReviewFormAttach);

    /**
     * 新增样品评审单-附件
     *
     * @param devSampleReviewFormAttach 样品评审单-附件
     * @return 结果
     */
    public int insertDevSampleReviewFormAttach(DevSampleReviewFormAttach devSampleReviewFormAttach);

    /**
     * 修改样品评审单-附件
     *
     * @param devSampleReviewFormAttach 样品评审单-附件
     * @return 结果
     */
    public int updateDevSampleReviewFormAttach(DevSampleReviewFormAttach devSampleReviewFormAttach);

    /**
     * 变更样品评审单-附件
     *
     * @param devSampleReviewFormAttach 样品评审单-附件
     * @return 结果
     */
    public int changeDevSampleReviewFormAttach(DevSampleReviewFormAttach devSampleReviewFormAttach);

    /**
     * 批量删除样品评审单-附件
     *
     * @param attachmentSids 需要删除的样品评审单-附件ID
     * @return 结果
     */
    public int deleteDevSampleReviewFormAttachByIds(List<Long> attachmentSids);

}
