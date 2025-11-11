package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManManufactureCompleteNote;

/**
 * 生产完工确认单Service接口
 * 
 * @author linhongwei
 * @date 2021-06-09
 */
public interface IManManufactureCompleteNoteService extends IService<ManManufactureCompleteNote>{
    /**
     * 查询生产完工确认单
     * 
     * @param manufactureCompleteNoteSid 生产完工确认单ID
     * @return 生产完工确认单
     */
    public ManManufactureCompleteNote selectManManufactureCompleteNoteById(Long manufactureCompleteNoteSid);

    /**
     * 查询生产完工确认单列表
     * 
     * @param manManufactureCompleteNote 生产完工确认单
     * @return 生产完工确认单集合
     */
    public List<ManManufactureCompleteNote> selectManManufactureCompleteNoteList(ManManufactureCompleteNote manManufactureCompleteNote);

    /**
     * 新增生产完工确认单
     * 
     * @param manManufactureCompleteNote 生产完工确认单
     * @return 结果
     */
    public int insertManManufactureCompleteNote(ManManufactureCompleteNote manManufactureCompleteNote);

    /**
     * 修改生产完工确认单
     * 
     * @param manManufactureCompleteNote 生产完工确认单
     * @return 结果
     */
    public int updateManManufactureCompleteNote(ManManufactureCompleteNote manManufactureCompleteNote);

    /**
     * 变更生产完工确认单
     *
     * @param manManufactureCompleteNote 生产完工确认单
     * @return 结果
     */
    public int changeManManufactureCompleteNote(ManManufactureCompleteNote manManufactureCompleteNote);

    /**
     * 批量删除生产完工确认单
     * 
     * @param manufactureCompleteNoteSids 需要删除的生产完工确认单ID
     * @return 结果
     */
    public int deleteManManufactureCompleteNoteByIds(List<Long> manufactureCompleteNoteSids);

    /**
     * 更改确认状态
     * @param manManufactureCompleteNote
     * @return
     */
    int check(ManManufactureCompleteNote manManufactureCompleteNote);

    int approval(ManManufactureCompleteNote manManufactureCompleteNote);

    /**
     * 作废-生产完工确认单
     */
    int cancellationManufactureCompleteNoteById(Long manufactureCompleteNoteSid);

    /**
     * 提交前校验-生产完工确认单
     */
    int verify(Long manufactureCompleteNoteSid, String handleStatus);
}
