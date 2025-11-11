package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.AssAssetRecordAttach;

/**
 * 资产台账-附件Service接口
 *
 * @author chenkw
 * @date 2022-03-01
 */
public interface IAssAssetRecordAttachService extends IService<AssAssetRecordAttach> {
    /**
     * 查询资产台账-附件
     *
     * @param assetAttachmentSid 资产台账-附件ID
     * @return 资产台账-附件
     */
    public AssAssetRecordAttach selectAssAssetRecordAttachById(Long assetAttachmentSid);

    /**
     * 查询资产台账-附件列表
     *
     * @param assAssetRecordAttach 资产台账-附件
     * @return 资产台账-附件集合
     */
    public List<AssAssetRecordAttach> selectAssAssetRecordAttachList(AssAssetRecordAttach assAssetRecordAttach);

    /**
     * 新增资产台账-附件
     *
     * @param assAssetRecordAttach 资产台账-附件
     * @return 结果
     */
    public int insertAssAssetRecordAttach(AssAssetRecordAttach assAssetRecordAttach);

    /**
     * 修改资产台账-附件
     *
     * @param assAssetRecordAttach 资产台账-附件
     * @return 结果
     */
    public int updateAssAssetRecordAttach(AssAssetRecordAttach assAssetRecordAttach);

    /**
     * 变更资产台账-附件
     *
     * @param assAssetRecordAttach 资产台账-附件
     * @return 结果
     */
    public int changeAssAssetRecordAttach(AssAssetRecordAttach assAssetRecordAttach);

    /**
     * 批量删除资产台账-附件
     *
     * @param assetAttachmentSids 需要删除的资产台账-附件ID
     * @return 结果
     */
    public int deleteAssAssetRecordAttachByIds(List<Long> assetAttachmentSids);

}
