package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SamOsbSampleReimburseAttach;

import java.util.List;


/**
 * 外采样报销单-附件Service接口
 *
 * @author qhq
 * @date 2021-12-28
 */
public interface ISamOsbSampleReimburseAttachService extends IService<SamOsbSampleReimburseAttach> {
    /**
     * 查询外采样报销单-附件
     *
     * @param attachmentSid 外采样报销单-附件ID
     * @return 外采样报销单-附件
     */
    public SamOsbSampleReimburseAttach selectSamOsbSampleReimburseAttachById(Long attachmentSid);

    /**
     * 查询外采样报销单-附件列表
     *
     * @param samOsbSampleReimburseAttach 外采样报销单-附件
     * @return 外采样报销单-附件集合
     */
    public List<SamOsbSampleReimburseAttach> selectSamOsbSampleReimburseAttachList(SamOsbSampleReimburseAttach samOsbSampleReimburseAttach);

    /**
     * 新增外采样报销单-附件
     *
     * @param samOsbSampleReimburseAttach 外采样报销单-附件
     * @return 结果
     */
    public int insertSamOsbSampleReimburseAttach(SamOsbSampleReimburseAttach samOsbSampleReimburseAttach);

    /**
     * 修改外采样报销单-附件
     *
     * @param samOsbSampleReimburseAttach 外采样报销单-附件
     * @return 结果
     */
    public int updateSamOsbSampleReimburseAttach(SamOsbSampleReimburseAttach samOsbSampleReimburseAttach);

    /**
     * 变更外采样报销单-附件
     *
     * @param samOsbSampleReimburseAttach 外采样报销单-附件
     * @return 结果
     */
    public int changeSamOsbSampleReimburseAttach(SamOsbSampleReimburseAttach samOsbSampleReimburseAttach);

    /**
     * 批量删除外采样报销单-附件
     *
     * @param attachmentSids 需要删除的外采样报销单-附件ID
     * @return 结果
     */
    public int deleteSamOsbSampleReimburseAttachByIds(List<Long> attachmentSids);

}
