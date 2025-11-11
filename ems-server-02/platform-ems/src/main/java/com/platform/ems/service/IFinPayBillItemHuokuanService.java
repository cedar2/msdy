package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinPayBill;
import com.platform.ems.domain.FinPayBillItemHuokuan;

/**
 * 付款单-核销货款明细表Service接口
 */
public interface IFinPayBillItemHuokuanService extends IService<FinPayBillItemHuokuan>{

    /**
     * 查询付款单-核销货款明细表
     *
     * @param payBillItemHuokuanSid 付款单-核销货款明细表ID
     * @return 付款单-核销货款明细表
     */
    public FinPayBillItemHuokuan selectFinPayBillItemHuokuanById(Long payBillItemHuokuanSid);

    /**
     * 查询付款单-核销货款明细表列表
     *
     * @param finPayBillItemHuokuan 付款单-核销货款明细表
     * @return 付款单-核销货款明细表集合
     */
    public List<FinPayBillItemHuokuan> selectFinPayBillItemHuokuanList(FinPayBillItemHuokuan finPayBillItemHuokuan);

    /**
     * 新增付款单-核销货款明细表
     *
     * @param finPayBillItemHuokuan 付款单-核销货款明细表
     * @return 结果
     */
    public int insertFinPayBillItemHuokuan(FinPayBillItemHuokuan finPayBillItemHuokuan);

    /**
     * 修改付款单-核销货款明细表
     *
     * @param finPayBillItemHuokuan 付款单-核销货款明细表
     * @return 结果
     */
    public int updateFinPayBillItemHuokuan(FinPayBillItemHuokuan finPayBillItemHuokuan);

    /**
     * 变更付款单-核销货款明细表
     *
     * @param finPayBillItemHuokuan 付款单-核销货款明细表
     * @return 结果
     */
    public int changeFinPayBillItemHuokuan(FinPayBillItemHuokuan finPayBillItemHuokuan);

    /**
     * 批量删除付款单-核销货款明细表
     *
     * @param payBillItemHuokuanSids 需要删除的付款单-核销货款明细表ID
     * @return 结果
     */
    public int deleteFinPayBillItemHuokuanByIds(List<Long>  payBillItemHuokuanSids);

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
    public int deleteByList(List<FinPayBillItemHuokuan> itemList);
}
