package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinPayBill;
import com.platform.ems.domain.FinPayBillItemOutsourceSettle;

import java.util.List;

/**
 * 付款单-外发加工费结算单明细Service接口
 *
 * @author platform
 * @date 2024-05-22
 */
public interface IFinPayBillItemOutsourceSettleService extends IService<FinPayBillItemOutsourceSettle> {

    /**
     * 查询付款单-外发加工费结算单明细
     *
     * @param payBillItemOutsourceSettleSid 付款单-外发加工费结算单明细ID
     * @return 付款单-外发加工费结算单明细
     */
    public FinPayBillItemOutsourceSettle selectFinPayBillItemOutsourceSettleById(Long payBillItemOutsourceSettleSid);

    /**
     * 查询付款单-外发加工费结算单明细列表
     *
     * @param finPayBillItemOutsourceSettle 付款单-外发加工费结算单明细
     * @return 付款单-外发加工费结算单明细集合
     */
    public List<FinPayBillItemOutsourceSettle> selectFinPayBillItemOutsourceSettleList(FinPayBillItemOutsourceSettle finPayBillItemOutsourceSettle);

    /**
     * 新增付款单-外发加工费结算单明细
     *
     * @param finPayBillItemOutsourceSettle 付款单-外发加工费结算单明细
     * @return 结果
     */
    public int insertFinPayBillItemOutsourceSettle(FinPayBillItemOutsourceSettle finPayBillItemOutsourceSettle);

    /**
     * 修改付款单-外发加工费结算单明细
     *
     * @param finPayBillItemOutsourceSettle 付款单-外发加工费结算单明细
     * @return 结果
     */
    public int updateFinPayBillItemOutsourceSettle(FinPayBillItemOutsourceSettle finPayBillItemOutsourceSettle);

    /**
     * 变更付款单-外发加工费结算单明细
     *
     * @param finPayBillItemOutsourceSettle 付款单-外发加工费结算单明细
     * @return 结果
     */
    public int changeFinPayBillItemOutsourceSettle(FinPayBillItemOutsourceSettle finPayBillItemOutsourceSettle);

    /**
     * 批量删除付款单-外发加工费结算单明细
     *
     * @param payBillItemOutsourceSettleSids 需要删除的付款单-外发加工费结算单明细ID
     * @return 结果
     */
    public int deleteFinPayBillItemOutsourceSettleByIds(List<Long>  payBillItemOutsourceSettleSids);

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
    public int deleteByList(List<FinPayBillItemOutsourceSettle> itemList);
}
