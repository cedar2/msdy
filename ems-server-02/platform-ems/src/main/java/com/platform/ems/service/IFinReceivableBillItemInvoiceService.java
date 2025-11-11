package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinReceivableBill;
import com.platform.ems.domain.FinReceivableBillItemInvoice;

/**
 * 收款单-发票台账明细表Service接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface IFinReceivableBillItemInvoiceService extends IService<FinReceivableBillItemInvoice> {

    /**
     * 查询收款单-发票台账明细表
     *
     * @param receivableBillItemInvoiceSid 收款单-发票台账明细表ID
     * @return 收款单-发票台账明细表
     */
    public FinReceivableBillItemInvoice selectFinReceivableBillItemInvoiceById(Long receivableBillItemInvoiceSid);

    /**
     * 查询收款单-发票台账明细表列表
     *
     * @param finReceivableBillItemInvoice 收款单-发票台账明细表
     * @return 收款单-发票台账明细表集合
     */
    public List<FinReceivableBillItemInvoice> selectFinReceivableBillItemInvoiceList(FinReceivableBillItemInvoice finReceivableBillItemInvoice);

    /**
     * 新增收款单-发票台账明细表
     *
     * @param finReceivableBillItemInvoice 收款单-发票台账明细表
     * @return 结果
     */
    public int insertFinReceivableBillItemInvoice(FinReceivableBillItemInvoice finReceivableBillItemInvoice);

    /**
     * 修改收款单-发票台账明细表
     *
     * @param finReceivableBillItemInvoice 收款单-发票台账明细表
     * @return 结果
     */
    public int updateFinReceivableBillItemInvoice(FinReceivableBillItemInvoice finReceivableBillItemInvoice);

    /**
     * 变更收款单-发票台账明细表
     *
     * @param finReceivableBillItemInvoice 收款单-发票台账明细表
     * @return 结果
     */
    public int changeFinReceivableBillItemInvoice(FinReceivableBillItemInvoice finReceivableBillItemInvoice);

    /**
     * 批量删除收款单-发票台账明细表
     *
     * @param receivableBillItemInvoiceSids 需要删除的收款单-发票台账明细表ID
     * @return 结果
     */
    public int deleteFinReceivableBillItemInvoiceByIds(List<Long> receivableBillItemInvoiceSids);

    /**
     * 批量新增
     */
    public int insertByList(FinReceivableBill bill);

    /**
     * 批量修改
     */
    public int updateByList(FinReceivableBill bill);

    /**
     * 批量删除
     */
    public int deleteByList(List<FinReceivableBillItemInvoice> itemList);
}
