package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinPayBill;
import com.platform.ems.domain.FinPayBillItemKoukuan;

/**
 * 付款单-核销扣款明细表Service接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface IFinPayBillItemKoukuanService extends IService<FinPayBillItemKoukuan> {

    /**
     * 查询付款单-核销扣款明细表
     *
     * @param payBillItemKoukuanSid 付款单-核销扣款明细表ID
     * @return 付款单-核销扣款明细表
     */
    public FinPayBillItemKoukuan selectFinPayBillItemKoukuanById(Long payBillItemKoukuanSid);

    /**
     * 查询付款单-核销扣款明细表列表
     *
     * @param finPayBillItemKoukuan 付款单-核销扣款明细表
     * @return 付款单-核销扣款明细表集合
     */
    public List<FinPayBillItemKoukuan> selectFinPayBillItemKoukuanList(FinPayBillItemKoukuan finPayBillItemKoukuan);

    /**
     * 新增付款单-核销扣款明细表
     *
     * @param finPayBillItemKoukuan 付款单-核销扣款明细表
     * @return 结果
     */
    public int insertFinPayBillItemKoukuan(FinPayBillItemKoukuan finPayBillItemKoukuan);

    /**
     * 修改付款单-核销扣款明细表
     *
     * @param finPayBillItemKoukuan 付款单-核销扣款明细表
     * @return 结果
     */
    public int updateFinPayBillItemKoukuan(FinPayBillItemKoukuan finPayBillItemKoukuan);

    /**
     * 变更付款单-核销扣款明细表
     *
     * @param finPayBillItemKoukuan 付款单-核销扣款明细表
     * @return 结果
     */
    public int changeFinPayBillItemKoukuan(FinPayBillItemKoukuan finPayBillItemKoukuan);

    /**
     * 批量删除付款单-核销扣款明细表
     *
     * @param payBillItemKoukuanSids 需要删除的付款单-核销扣款明细表ID
     * @return 结果
     */
    public int deleteFinPayBillItemKoukuanByIds(List<Long> payBillItemKoukuanSids);

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
    public int deleteByList(List<FinPayBillItemKoukuan> itemList);
}
