package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinPayBill;
import com.platform.ems.domain.FinPayBillItemInvoice;

/**
 * 付款单-发票台账明细表Service接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface IFinPayBillItemInvoiceService extends IService<FinPayBillItemInvoice> {

    /**
     * 查询付款单-发票台账明细表
     *
     * @param payBillItemInvoiceSid 付款单-发票台账明细表ID
     * @return 付款单-发票台账明细表
     */
    public FinPayBillItemInvoice selectFinPayBillItemInvoiceById(Long payBillItemInvoiceSid);

    /**
     * 查询付款单-发票台账明细表列表
     *
     * @param finPayBillItemInvoice 付款单-发票台账明细表
     * @return 付款单-发票台账明细表集合
     */
    public List<FinPayBillItemInvoice> selectFinPayBillItemInvoiceList(FinPayBillItemInvoice finPayBillItemInvoice);

    /**
     * 新增付款单-发票台账明细表
     *
     * @param finPayBillItemInvoice 付款单-发票台账明细表
     * @return 结果
     */
    public int insertFinPayBillItemInvoice(FinPayBillItemInvoice finPayBillItemInvoice);

    /**
     * 修改付款单-发票台账明细表
     *
     * @param finPayBillItemInvoice 付款单-发票台账明细表
     * @return 结果
     */
    public int updateFinPayBillItemInvoice(FinPayBillItemInvoice finPayBillItemInvoice);

    /**
     * 变更付款单-发票台账明细表
     *
     * @param finPayBillItemInvoice 付款单-发票台账明细表
     * @return 结果
     */
    public int changeFinPayBillItemInvoice(FinPayBillItemInvoice finPayBillItemInvoice);

    /**
     * 批量删除付款单-发票台账明细表
     *
     * @param payBillItemInvoiceSids 需要删除的付款单-发票台账明细表ID
     * @return 结果
     */
    public int deleteFinPayBillItemInvoiceByIds(List<Long> payBillItemInvoiceSids);

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
    public int deleteByList(List<FinPayBillItemInvoice> itemList);
}
