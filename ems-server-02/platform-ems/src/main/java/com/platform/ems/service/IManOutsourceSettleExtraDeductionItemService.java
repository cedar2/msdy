package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManManufactureOutsourceSettle;
import com.platform.ems.domain.ManOutsourceSettleExtraDeductionItem;

/**
 * 外发加工费结算单-额外扣款明细Service接口
 *
 * @author admin
 * @date 2023-08-10
 */
public interface IManOutsourceSettleExtraDeductionItemService extends IService<ManOutsourceSettleExtraDeductionItem> {
    /**
     * 查询外发加工费结算单-额外扣款明细
     *
     * @param outsourceSettleExtraDeductionItemSid 外发加工费结算单-额外扣款明细ID
     * @return 外发加工费结算单-额外扣款明细
     */
    public ManOutsourceSettleExtraDeductionItem selectManOutsourceSettleExtraDeductionItemById(Long outsourceSettleExtraDeductionItemSid);

    /**
     * 查询外发加工费结算单-额外扣款明细列表
     *
     * @param manOutsourceSettleExtraDeductionItem 外发加工费结算单-额外扣款明细
     * @return 外发加工费结算单-额外扣款明细集合
     */
    public List<ManOutsourceSettleExtraDeductionItem> selectManOutsourceSettleExtraDeductionItemList(ManOutsourceSettleExtraDeductionItem manOutsourceSettleExtraDeductionItem);

    /**
     * 查询外发加工费结算单-额外扣款明细列表 根据主表
     *
     * @param manOutsourceSettleSid 外发加工费结算单-Sid
     * @return 外发加工费结算单-额外扣款明细集合
     */
    public List<ManOutsourceSettleExtraDeductionItem> selectManOutsourceSettleExtraDeductionItemList(Long manOutsourceSettleSid);

    /**
     * 新增外发加工费结算单-额外扣款明细
     *
     * @param manOutsourceSettleExtraDeductionItem 外发加工费结算单-额外扣款明细
     * @return 结果
     */
    public int insertManOutsourceSettleExtraDeductionItem(ManOutsourceSettleExtraDeductionItem manOutsourceSettleExtraDeductionItem);

    /**
     * 批量修改外发加工费结算单-额外扣款明细 根据主表
     *
     * @param manManufactureOutsourceSettle 外发加工费结算单
     * @return 结果
     */
    public int insertManOutsourceSettleExtraDeductionItemListBy(ManManufactureOutsourceSettle manManufactureOutsourceSettle);

    /**
     * 修改外发加工费结算单-额外扣款明细
     *
     * @param manOutsourceSettleExtraDeductionItem 外发加工费结算单-额外扣款明细
     * @return 结果
     */
    public int updateManOutsourceSettleExtraDeductionItem(ManOutsourceSettleExtraDeductionItem manOutsourceSettleExtraDeductionItem);

    /**
     * 批量修改外发加工费结算单-额外扣款明细 根据主表
     *
     * @param manManufactureOutsourceSettle 外发加工费结算单
     * @return 结果
     */
    public int updateManOutsourceSettleExtraDeductionItemListBy(ManManufactureOutsourceSettle manManufactureOutsourceSettle);

    /**
     * 变更外发加工费结算单-额外扣款明细
     *
     * @param manOutsourceSettleExtraDeductionItem 外发加工费结算单-额外扣款明细
     * @return 结果
     */
    public int changeManOutsourceSettleExtraDeductionItem(ManOutsourceSettleExtraDeductionItem manOutsourceSettleExtraDeductionItem);

    /**
     * 批量删除外发加工费结算单-额外扣款明细
     *
     * @param outsourceSettleExtraDeductionItemSids 需要删除的外发加工费结算单-额外扣款明细ID
     * @return 结果
     */
    public int deleteManOutsourceSettleExtraDeductionItemByIds(List<Long> outsourceSettleExtraDeductionItemSids);

    /**
     * 批量删除外发加工费结算单-额外扣款明细
     *
     * @param itemList 需要删除的额外扣款明细
     * @return 结果
     */
    public int deleteListByList(List<ManOutsourceSettleExtraDeductionItem> itemList);

    /**
     * 批量删除外发加工费结算单-额外扣款明细 根据主表sid
     *
     * @param manufactureOutsourceSettleSids 需要删除的外发加工费结算单SID
     * @return 结果
     */
    public int deleteListBySids(List<Long> manufactureOutsourceSettleSids);


}
