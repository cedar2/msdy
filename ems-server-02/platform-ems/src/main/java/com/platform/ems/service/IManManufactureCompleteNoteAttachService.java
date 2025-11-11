package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManManufactureCompleteNoteAttach;

/**
 * 生产完工确认单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-06-09
 */
public interface IManManufactureCompleteNoteAttachService extends IService<ManManufactureCompleteNoteAttach>{
    /**
     * 查询生产完工确认单-附件
     * 
     * @param manufactureCompleteNoteAttachSid 生产完工确认单-附件ID
     * @return 生产完工确认单-附件
     */
    public ManManufactureCompleteNoteAttach selectManManufactureCompleteNoteAttachById(Long manufactureCompleteNoteAttachSid);

    /**
     * 查询生产完工确认单-附件列表
     * 
     * @param manManufactureCompleteNoteAttach 生产完工确认单-附件
     * @return 生产完工确认单-附件集合
     */
    public List<ManManufactureCompleteNoteAttach> selectManManufactureCompleteNoteAttachList(ManManufactureCompleteNoteAttach manManufactureCompleteNoteAttach);

    /**
     * 新增生产完工确认单-附件
     * 
     * @param manManufactureCompleteNoteAttach 生产完工确认单-附件
     * @return 结果
     */
    public int insertManManufactureCompleteNoteAttach(ManManufactureCompleteNoteAttach manManufactureCompleteNoteAttach);

    /**
     * 修改生产完工确认单-附件
     * 
     * @param manManufactureCompleteNoteAttach 生产完工确认单-附件
     * @return 结果
     */
    public int updateManManufactureCompleteNoteAttach(ManManufactureCompleteNoteAttach manManufactureCompleteNoteAttach);

    /**
     * 变更生产完工确认单-附件
     *
     * @param manManufactureCompleteNoteAttach 生产完工确认单-附件
     * @return 结果
     */
    public int changeManManufactureCompleteNoteAttach(ManManufactureCompleteNoteAttach manManufactureCompleteNoteAttach);

    /**
     * 批量删除生产完工确认单-附件
     * 
     * @param manufactureCompleteNoteAttachSids 需要删除的生产完工确认单-附件ID
     * @return 结果
     */
    public int deleteManManufactureCompleteNoteAttachByIds(List<Long> manufactureCompleteNoteAttachSids);

    /**
     * 更改确认状态
     * @param manManufactureCompleteNoteAttach
     * @return
     */
    int check(ManManufactureCompleteNoteAttach manManufactureCompleteNoteAttach);

}
