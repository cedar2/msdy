package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManProcessStepCompleteRecord;
import com.platform.ems.domain.ManProcessStepCompleteRecordItem;
import com.platform.ems.domain.dto.request.ManProcessStepCompleteRecordTableRequest;
import com.platform.ems.domain.dto.response.ManProcessStepCompleteRecordTableResponse;

/**
 * 商品道序完成量台账-明细Service接口
 *
 * @author chenkw
 * @date 2022-10-20
 */
public interface IManProcessStepCompleteRecordItemService extends IService<ManProcessStepCompleteRecordItem> {
    /**
     * 查询商品道序完成量台账-明细
     *
     * @param stepCompleteRecordItemSid 商品道序完成量台账-明细ID
     * @return 商品道序完成量台账-明细
     */
    public ManProcessStepCompleteRecordItem selectManProcessStepCompleteRecordItemById(Long stepCompleteRecordItemSid);

    /**
     * 查询商品道序完成量台账-明细  根据主表sid
     *
     * @param stepCompleteRecordSid 商品道序完成量台账ID
     * @return 商品道序完成量台账-明细
     */
    public List<ManProcessStepCompleteRecordItem> selectManProcessStepCompleteRecordItemListById(Long stepCompleteRecordSid);

    /**
     * 查询商品道序完成量台账-明细列表
     *
     * @param manProcessStepCompleteRecordItem 商品道序完成量台账-明细
     * @return 商品道序完成量台账-明细集合
     */
    public List<ManProcessStepCompleteRecordItem> selectManProcessStepCompleteRecordItemList(ManProcessStepCompleteRecordItem manProcessStepCompleteRecordItem);

    /**
     * 新增商品道序完成量台账-明细
     *
     * @param manProcessStepCompleteRecordItem 商品道序完成量台账-明细
     * @return 结果
     */
    public int insertManProcessStepCompleteRecordItem(ManProcessStepCompleteRecordItem manProcessStepCompleteRecordItem);

    /**
     * 批量新增商品道序完成量台账-明细
     *
     * @param manProcessStepCompleteRecord 商品道序完成量台账
     * @return 结果
     */
    public int insertManProcessStepCompleteRecordItemList(ManProcessStepCompleteRecord manProcessStepCompleteRecord);

    /**
     * 修改商品道序完成量台账-明细
     *
     * @param manProcessStepCompleteRecordItem 商品道序完成量台账-明细
     * @return 结果
     */
    public int updateManProcessStepCompleteRecordItem(ManProcessStepCompleteRecordItem manProcessStepCompleteRecordItem);

    /**
     * 批量修改商品道序完成量台账-明细
     *
     * @param manProcessStepCompleteRecord 商品道序完成量台账
     * @return 结果
     */
    public int updateManProcessStepCompleteRecordItemList(ManProcessStepCompleteRecord manProcessStepCompleteRecord);

    /**
     * 变更商品道序完成量台账-明细
     *
     * @param manProcessStepCompleteRecordItem 商品道序完成量台账-明细
     * @return 结果
     */
    public int changeManProcessStepCompleteRecordItem(ManProcessStepCompleteRecordItem manProcessStepCompleteRecordItem);

    /**
     * 批量删除商品道序完成量台账-明细
     *
     * @param stepCompleteRecordItemSids 需要删除的商品道序完成量台账-明细ID
     * @return 结果
     */
    public int deleteManProcessStepCompleteRecordItemByIds(List<Long> stepCompleteRecordItemSids);

    /**
     * 批量删除商品道序完成量台账-明细
     *
     * @param stepCompleteRecordItemList 需要删除的商品道序完成量台账
     * @return 结果
     */
    public int deleteManProcessStepCompleteRecordItem(List<ManProcessStepCompleteRecordItem> stepCompleteRecordItemList);

    /**
     * 批量删除商品道序完成量台账-明细
     *
     * @param stepCompleteRecordSids 需要删除的商品道序完成量台账ID
     * @return 结果
     */
    public int deleteManProcessStepCompleteRecordItemByRecordIds(List<Long> stepCompleteRecordSids);

    /**
     * 明细按款显示
     */
    public ManProcessStepCompleteRecordTableResponse itemTable(ManProcessStepCompleteRecordTableRequest request);
}
