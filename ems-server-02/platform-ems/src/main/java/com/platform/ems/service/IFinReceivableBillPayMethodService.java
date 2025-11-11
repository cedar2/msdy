package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinReceivableBillPayMethod;

/**
 * 收款单-支付方式明细Service接口
 *
 * @author chenkw
 * @date 2022-06-23
 */
public interface IFinReceivableBillPayMethodService extends IService<FinReceivableBillPayMethod> {
    /**
     * 查询收款单-支付方式明细
     *
     * @param receivableBillPayMethodSid 收款单-支付方式明细ID
     * @return 收款单-支付方式明细
     */
    public FinReceivableBillPayMethod selectFinReceivableBillPayMethodById(Long receivableBillPayMethodSid);

    /**
     * 查询收款单-支付方式明细列表
     *
     * @param finReceivableBillPayMethod 收款单-支付方式明细
     * @return 收款单-支付方式明细集合
     */
    public List<FinReceivableBillPayMethod> selectFinReceivableBillPayMethodList(FinReceivableBillPayMethod finReceivableBillPayMethod);

    /**
     * 新增收款单-支付方式明细
     *
     * @param finReceivableBillPayMethod 收款单-支付方式明细
     * @return 结果
     */
    public int insertFinReceivableBillPayMethod(FinReceivableBillPayMethod finReceivableBillPayMethod);

    /**
     * 修改收款单-支付方式明细
     *
     * @param finReceivableBillPayMethod 收款单-支付方式明细
     * @return 结果
     */
    public int updateFinReceivableBillPayMethod(FinReceivableBillPayMethod finReceivableBillPayMethod);

    /**
     * 变更收款单-支付方式明细
     *
     * @param finReceivableBillPayMethod 收款单-支付方式明细
     * @return 结果
     */
    public int changeFinReceivableBillPayMethod(FinReceivableBillPayMethod finReceivableBillPayMethod);

    /**
     * 批量删除收款单-支付方式明细
     *
     * @param receivableBillPayMethodSids 需要删除的收款单-支付方式明细ID
     * @return 结果
     */
    public int deleteFinReceivableBillPayMethodByIds(List<Long> receivableBillPayMethodSids);

}
