package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManManufactureDefectiveAttach;

import java.util.List;

/**
 * 生产次品台账-附件Service接口
 *
 * @author c
 * @date 2022-03-02
 */
public interface IManManufactureDefectiveAttachService extends IService<ManManufactureDefectiveAttach> {
    /**
     * 查询生产次品台账-附件
     *
     * @param attachmentSid 生产次品台账-附件ID
     * @return 生产次品台账-附件
     */
    public ManManufactureDefectiveAttach selectManManufactureDefectiveAttachById(Long attachmentSid);

    /**
     * 查询生产次品台账-附件列表
     *
     * @param manManufactureDefectiveAttach 生产次品台账-附件
     * @return 生产次品台账-附件集合
     */
    public List<ManManufactureDefectiveAttach> selectManManufactureDefectiveAttachList(ManManufactureDefectiveAttach manManufactureDefectiveAttach);

    /**
     * 新增生产次品台账-附件
     *
     * @param manManufactureDefectiveAttach 生产次品台账-附件
     * @return 结果
     */
    public int insertManManufactureDefectiveAttach(ManManufactureDefectiveAttach manManufactureDefectiveAttach);

    /**
     * 修改生产次品台账-附件
     *
     * @param manManufactureDefectiveAttach 生产次品台账-附件
     * @return 结果
     */
    public int updateManManufactureDefectiveAttach(ManManufactureDefectiveAttach manManufactureDefectiveAttach);

    /**
     * 变更生产次品台账-附件
     *
     * @param manManufactureDefectiveAttach 生产次品台账-附件
     * @return 结果
     */
    public int changeManManufactureDefectiveAttach(ManManufactureDefectiveAttach manManufactureDefectiveAttach);

    /**
     * 批量删除生产次品台账-附件
     *
     * @param attachmentSids 需要删除的生产次品台账-附件ID
     * @return 结果
     */
    public int deleteManManufactureDefectiveAttachByIds(List<Long> attachmentSids);

}
