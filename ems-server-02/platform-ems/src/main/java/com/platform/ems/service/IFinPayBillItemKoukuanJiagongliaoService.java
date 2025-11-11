package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinPayBill;
import com.platform.ems.domain.FinPayBillItemKoukuanJiagongliao;

/**
 * 付款单-核销甲供料扣款明细表Service接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface IFinPayBillItemKoukuanJiagongliaoService extends IService<FinPayBillItemKoukuanJiagongliao> {

    /**
     * 查询付款单-核销甲供料扣款明细表
     *
     * @param payBillItemKoukuanJiagongliaoSid 付款单-核销甲供料扣款明细表ID
     * @return 付款单-核销甲供料扣款明细表
     */
    public FinPayBillItemKoukuanJiagongliao selectFinPayBillItemKoukuanJiagongliaoById(Long payBillItemKoukuanJiagongliaoSid);

    /**
     * 查询付款单-核销甲供料扣款明细表列表
     *
     * @param finPayBillItemKoukuanJiagongliao 付款单-核销甲供料扣款明细表
     * @return 付款单-核销甲供料扣款明细表集合
     */
    public List<FinPayBillItemKoukuanJiagongliao> selectFinPayBillItemKoukuanJiagongliaoList(FinPayBillItemKoukuanJiagongliao finPayBillItemKoukuanJiagongliao);

    /**
     * 新增付款单-核销甲供料扣款明细表
     *
     * @param finPayBillItemKoukuanJiagongliao 付款单-核销甲供料扣款明细表
     * @return 结果
     */
    public int insertFinPayBillItemKoukuanJiagongliao(FinPayBillItemKoukuanJiagongliao finPayBillItemKoukuanJiagongliao);

    /**
     * 修改付款单-核销甲供料扣款明细表
     *
     * @param finPayBillItemKoukuanJiagongliao 付款单-核销甲供料扣款明细表
     * @return 结果
     */
    public int updateFinPayBillItemKoukuanJiagongliao(FinPayBillItemKoukuanJiagongliao finPayBillItemKoukuanJiagongliao);

    /**
     * 变更付款单-核销甲供料扣款明细表
     *
     * @param finPayBillItemKoukuanJiagongliao 付款单-核销甲供料扣款明细表
     * @return 结果
     */
    public int changeFinPayBillItemKoukuanJiagongliao(FinPayBillItemKoukuanJiagongliao finPayBillItemKoukuanJiagongliao);

    /**
     * 批量删除付款单-核销甲供料扣款明细表
     *
     * @param payBillItemKoukuanJiagongliaoSids 需要删除的付款单-核销甲供料扣款明细表ID
     * @return 结果
     */
    public int deleteFinPayBillItemKoukuanJiagongliaoByIds(List<Long> payBillItemKoukuanJiagongliaoSids);

    /**
     * 批量新增
     */
    public int insertByList(FinPayBill bill);

    /**
     * 批量修改
     */
    public int updateByList(FinPayBill bill);

    /**
     * 批量删除
     */
    public int deleteByList(List<FinPayBillItemKoukuanJiagongliao> itemList);
}
