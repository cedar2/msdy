package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.*;
import org.apache.ibatis.annotations.Param;

/**
 * 客户月对账单Mapper接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface FinCustomerMonthAccountBillMapper extends BaseMapper<FinCustomerMonthAccountBill> {

    FinCustomerMonthAccountBill selectFinCustomerMonthAccountBillById(Long customerMonthAccountBillSid);

    List<FinCustomerMonthAccountBill> selectFinCustomerMonthAccountBillList(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 添加多个
     *
     * @param list List FinCustomerMonthAccountBill
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerMonthAccountBill> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinCustomerMonthAccountBill
     * @return int
     */
    int updateAllById(FinCustomerMonthAccountBill entity);

    /**
     * 本期扣款:显示客户的收款的“月账单所属期间”等于月账单的”所属年月”的收款中类型是“扣款”的扣款明细
     *
     * @param finCustomerMonthAccountBill
     * @return int
     */
    List<FinCustomerMonthAccountBillKkInfo> selectDeductionItemListSk(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 本期扣款:显示客户的销售发票的“月账单所属期间”等于月账单的”所属年月”的发票折扣中类型是“扣款”的扣款明细
     *
     * @param finCustomerMonthAccountBill
     * @return int
     */
    List<FinCustomerMonthAccountBillKkInfo> selectDeductionItemListFp(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 本期扣款:显示客户的互抵单的“月账单所属期间”等于月账单的”所属年月”的明细中类型是“扣款”的扣款明细
     *
     * @param finCustomerMonthAccountBill
     * @return int
     */
    List<FinCustomerMonthAccountBillKkInfo> selectDeductionItemListHd(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 本期调账:显示客户的收款的“月账单所属期间”等于月账单的”所属年月”的收款中类型是“调账”的调账明细
     *
     * @param finCustomerMonthAccountBill
     * @return int
     */
    List<FinCustomerMonthAccountBillTzInfo> selectAdjustItemListSk(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 本期调账:显示客户的销售发票的“月账单所属期间”等于月账单的”所属年月”的发票折扣中类型是“调账”的调账明细
     *
     * @param finCustomerMonthAccountBill
     * @return int
     */
    List<FinCustomerMonthAccountBillTzInfo> selectAdjustItemListFp(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 本期调账:显示客户的互抵单的“月账单所属期间”等于月账单的”所属年月”的明细中类型是“调账”的调账明细
     *
     * @param finCustomerMonthAccountBill
     * @return int
     */
    List<FinCustomerMonthAccountBillTzInfo> selectAdjustItemListHd(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 台账-待收预收
     *
     * @param finCustomerMonthAccountBill
     * @return int
     */
    List< FinCustomerMonthAccountBillInfo> getRecordAdvanceReceipt(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 台账-应收暂估
     *
     * @param finCustomerMonthAccountBill
     * @return int
     */
    List<FinCustomerMonthAccountBillInfo> getReceiptEstimation(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 台账-应收款
     *
     * @param finCustomerMonthAccountBill
     * @return int
     */
    List<FinCustomerMonthAccountBillInfo> getAccountReceivable(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 台账-扣款
     *
     * @param finCustomerMonthAccountBill
     * @return int
     */
    List<FinCustomerMonthAccountBillInfo> getBookDeduction(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 台账-调账
     *
     * @param finCustomerMonthAccountBill
     * @return int
     */
    List<FinCustomerMonthAccountBillInfo> getBookAdjust(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 台账-预收款
     *
     * @param finCustomerMonthAccountBill
     * @return int
     */
    List<FinCustomerMonthAccountBillInfo> getBookYSk(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 台账-特殊收款
     *
     * @param finCustomerMonthAccountBill
     * @return int
     */
    List<FinCustomerMonthAccountBillInfo> getBookTsSk(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 台账-押金
     *
     * @param finCustomerMonthAccountBill
     * @return int
     */
    List<FinCustomerMonthAccountBillInfo> getCashPledge(FinCustomerMonthAccountBill finCustomerMonthAccountBill);

    /**
     * 台账-暂押款
     *
     * @param finCustomerMonthAccountBill
     * @return int
     */
    List<FinCustomerMonthAccountBillInfo> getFundsFreeze(FinCustomerMonthAccountBill finCustomerMonthAccountBill);
}
