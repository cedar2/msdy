package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinPayBillPayMethod;

/**
 * 付款单-支付方式明细Service接口
 *
 * @author chenkw
 * @date 2022-06-23
 */
public interface IFinPayBillPayMethodService extends IService<FinPayBillPayMethod> {
    /**
     * 查询付款单-支付方式明细
     *
     * @param payBillPayMethodSid 付款单-支付方式明细ID
     * @return 付款单-支付方式明细
     */
    public FinPayBillPayMethod selectFinPayBillPayMethodById(Long payBillPayMethodSid);

    /**
     * 查询付款单-支付方式明细列表
     *
     * @param finPayBillPayMethod 付款单-支付方式明细
     * @return 付款单-支付方式明细集合
     */
    public List<FinPayBillPayMethod> selectFinPayBillPayMethodList(FinPayBillPayMethod finPayBillPayMethod);

    /**
     * 新增付款单-支付方式明细
     *
     * @param finPayBillPayMethod 付款单-支付方式明细
     * @return 结果
     */
    public int insertFinPayBillPayMethod(FinPayBillPayMethod finPayBillPayMethod);

    /**
     * 修改付款单-支付方式明细
     *
     * @param finPayBillPayMethod 付款单-支付方式明细
     * @return 结果
     */
    public int updateFinPayBillPayMethod(FinPayBillPayMethod finPayBillPayMethod);

    /**
     * 变更付款单-支付方式明细
     *
     * @param finPayBillPayMethod 付款单-支付方式明细
     * @return 结果
     */
    public int changeFinPayBillPayMethod(FinPayBillPayMethod finPayBillPayMethod);

    /**
     * 批量删除付款单-支付方式明细
     *
     * @param payBillPayMethodSids 需要删除的付款单-支付方式明细ID
     * @return 结果
     */
    public int deleteFinPayBillPayMethodByIds(List<Long> payBillPayMethodSids);

}
