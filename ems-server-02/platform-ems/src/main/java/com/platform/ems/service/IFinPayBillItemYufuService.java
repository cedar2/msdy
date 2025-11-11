package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinPayBill;
import com.platform.ems.domain.FinPayBillItemYufu;

/**
 * 付款单-核销预付明细表Service接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface IFinPayBillItemYufuService extends IService<FinPayBillItemYufu>{

    /**
     * 查询付款单-核销预付明细表
     *
     * @param payBillItemYufuSid 付款单-核销预付明细表ID
     * @return 付款单-核销预付明细表
     */
    public FinPayBillItemYufu selectFinPayBillItemYufuById(Long payBillItemYufuSid);

    /**
     * 查询付款单-核销预付明细表列表
     *
     * @param finPayBillItemYufu 付款单-核销预付明细表
     * @return 付款单-核销预付明细表集合
     */
    public List<FinPayBillItemYufu> selectFinPayBillItemYufuList(FinPayBillItemYufu finPayBillItemYufu);

    /**
     * 新增付款单-核销预付明细表
     *
     * @param finPayBillItemYufu 付款单-核销预付明细表
     * @return 结果
     */
    public int insertFinPayBillItemYufu(FinPayBillItemYufu finPayBillItemYufu);

    /**
     * 修改付款单-核销预付明细表
     *
     * @param finPayBillItemYufu 付款单-核销预付明细表
     * @return 结果
     */
    public int updateFinPayBillItemYufu(FinPayBillItemYufu finPayBillItemYufu);

    /**
     * 变更付款单-核销预付明细表
     *
     * @param finPayBillItemYufu 付款单-核销预付明细表
     * @return 结果
     */
    public int changeFinPayBillItemYufu(FinPayBillItemYufu finPayBillItemYufu);

    /**
     * 批量删除付款单-核销预付明细表
     *
     * @param payBillItemYufuSids 需要删除的付款单-核销预付明细表ID
     * @return 结果
     */
    public int deleteFinPayBillItemYufuByIds(List<Long>  payBillItemYufuSids);

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
    public int deleteByList(List<FinPayBillItemYufu> itemList);
}
