package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManManufactureCompleteNoteItem;

/**
 * 生产完工确认单-明细Service接口
 *
 * @author linhongwei
 * @date 2021-06-09
 */
public interface IManManufactureCompleteNoteItemService extends IService<ManManufactureCompleteNoteItem> {
    /**
     * 查询生产完工确认单-明细
     *
     * @param manufactureCompleteNoteItemSid 生产完工确认单-明细ID
     * @return 生产完工确认单-明细
     */
    public ManManufactureCompleteNoteItem selectManManufactureCompleteNoteItemById(Long manufactureCompleteNoteItemSid);

    /**
     * 查询生产完工确认单-明细列表
     *
     * @param manManufactureCompleteNoteItem 生产完工确认单-明细
     * @return 生产完工确认单-明细集合
     */
    public List<ManManufactureCompleteNoteItem> selectManManufactureCompleteNoteItemList(ManManufactureCompleteNoteItem manManufactureCompleteNoteItem);


}
