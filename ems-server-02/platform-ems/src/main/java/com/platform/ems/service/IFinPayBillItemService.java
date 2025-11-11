package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinPayBill;
import com.platform.ems.domain.FinPayBillItem;

/**
 * 付款单-明细Service接口
 *
 * @author linhongwei
 * @date 2021-04-21
 */
public interface IFinPayBillItemService extends IService<FinPayBillItem> {

    /**
     * 查询付款单-明细
     *
     * @param payBillItemSid 付款单-明细ID
     * @return 付款单-明细
     */
    public FinPayBillItem selectFinPayBillItemById(Long payBillItemSid);

    /**
     * 查询付款单-明细列表
     *
     * @param finPayBillItem 付款单-明细
     * @return 付款单-明细集合
     */
    public List<FinPayBillItem> selectFinPayBillItemList(FinPayBillItem finPayBillItem);

    /**
     * 新增付款单-明细
     *
     * @param finPayBillItem 付款单-明细
     * @return 结果
     */
    public int insertFinPayBillItem(FinPayBillItem finPayBillItem);

    /**
     * 修改付款单-明细
     *
     * @param finPayBillItem 付款单-明细
     * @return 结果
     */
    public int updateFinPayBillItem(FinPayBillItem finPayBillItem);

    /**
     * 变更付款单-明细
     *
     * @param finPayBillItem 付款单-明细
     * @return 结果
     */
    public int changeFinPayBillItem(FinPayBillItem finPayBillItem);

    /**
     * 批量删除付款单-明细
     *
     * @param payBillItemSids 需要删除的付款单-明细ID
     * @return 结果
     */
    public int deleteFinPayBillItemByIds(List<Long> payBillItemSids);

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
    public int deleteByList(List<FinPayBillItem> itemList);
}
