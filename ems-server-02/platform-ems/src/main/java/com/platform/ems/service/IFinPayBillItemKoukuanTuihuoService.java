package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinPayBill;
import com.platform.ems.domain.FinPayBillItemKoukuanTuihuo;

/**
 * 付款单-核销退货扣款明细表Service接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface IFinPayBillItemKoukuanTuihuoService extends IService<FinPayBillItemKoukuanTuihuo>{

    /**
     * 查询付款单-核销退货扣款明细表
     *
     * @param payBillItemKoukuanTuihuoSid 付款单-核销退货扣款明细表ID
     * @return 付款单-核销退货扣款明细表
     */
    public FinPayBillItemKoukuanTuihuo selectFinPayBillItemKoukuanTuihuoById(Long payBillItemKoukuanTuihuoSid);

    /**
     * 查询付款单-核销退货扣款明细表列表
     *
     * @param finPayBillItemKoukuanTuihuo 付款单-核销退货扣款明细表
     * @return 付款单-核销退货扣款明细表集合
     */
    public List<FinPayBillItemKoukuanTuihuo> selectFinPayBillItemKoukuanTuihuoList(FinPayBillItemKoukuanTuihuo finPayBillItemKoukuanTuihuo);

    /**
     * 新增付款单-核销退货扣款明细表
     *
     * @param finPayBillItemKoukuanTuihuo 付款单-核销退货扣款明细表
     * @return 结果
     */
    public int insertFinPayBillItemKoukuanTuihuo(FinPayBillItemKoukuanTuihuo finPayBillItemKoukuanTuihuo);

    /**
     * 修改付款单-核销退货扣款明细表
     *
     * @param finPayBillItemKoukuanTuihuo 付款单-核销退货扣款明细表
     * @return 结果
     */
    public int updateFinPayBillItemKoukuanTuihuo(FinPayBillItemKoukuanTuihuo finPayBillItemKoukuanTuihuo);

    /**
     * 变更付款单-核销退货扣款明细表
     *
     * @param finPayBillItemKoukuanTuihuo 付款单-核销退货扣款明细表
     * @return 结果
     */
    public int changeFinPayBillItemKoukuanTuihuo(FinPayBillItemKoukuanTuihuo finPayBillItemKoukuanTuihuo);

    /**
     * 批量删除付款单-核销退货扣款明细表
     *
     * @param payBillItemKoukuanTuihuoSids 需要删除的付款单-核销退货扣款明细表ID
     * @return 结果
     */
    public int deleteFinPayBillItemKoukuanTuihuoByIds(List<Long>  payBillItemKoukuanTuihuoSids);

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
    public int deleteByList(List<FinPayBillItemKoukuanTuihuo> itemList);
}
