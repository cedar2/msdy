package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.*;
import org.apache.ibatis.annotations.Param;

/**
 * 供应商月对账单Mapper接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface FinVendorMonthAccountBillMapper extends BaseMapper<FinVendorMonthAccountBill> {

    FinVendorMonthAccountBill selectFinVendorMonthAccountBillById(Long vendorMonthAccountBillSid);

    List<FinVendorMonthAccountBill> selectFinVendorMonthAccountBillList(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 添加多个
     *
     * @param list List FinVendorMonthAccountBill
     * @return int
     */
    int inserts(@Param("list") List<FinVendorMonthAccountBill> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinVendorMonthAccountBill
     * @return int
     */
    int updateAllById(FinVendorMonthAccountBill entity);

    /**
     * 本期扣款:显示供应商的付款的“月账单所属期间”等于月账单的”所属年月”的付款中类型是“扣款”的扣款明细
     *
     * @param finVendorMonthAccountBill
     * @return int
     */
    List<FinVendorMonthAccountBillKkInfo> selectDeductionItemListFk(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 本期扣款:显示供应商的采购发票的“月账单所属期间”等于月账单的”所属年月”的折扣明细中类型是“扣款”的扣款明细
     *
     * @param finVendorMonthAccountBill
     * @return int
     */
    List<FinVendorMonthAccountBillKkInfo> selectDeductionItemListFp(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 本期扣款:显示供应商的互抵单的“月账单所属期间”等于月账单的”所属年月”的明细中类型是“扣款”的扣款明细
     *
     * @param finVendorMonthAccountBill
     * @return int
     */
    List<FinVendorMonthAccountBillKkInfo> selectDeductionItemListHd(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 本期调账:显示供应商的采购发票的“月账单所属期间”等于月账单的”所属年月”的折扣明细中类型是“调账”的调账明细
     *
     * @param finVendorMonthAccountBill
     * @return int
     */
    List<FinVendorMonthAccountBillTzInfo> selectAdjustItemListFp(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 本期调账:显示供应商的付款的“月账单所属期间”等于月账单的”所属年月”的付款中类型是“调账”的调账明细
     *
     * @param finVendorMonthAccountBill
     * @return int
     */
    List<FinVendorMonthAccountBillTzInfo> selectAdjustItemListFk(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 本期调账:显示供应商的互抵单的“月账单所属期间”等于月账单的”所属年月”的明细中类型是“调账”的调账明细
     *
     * @param finVendorMonthAccountBill
     * @return int
     */
    List<FinVendorMonthAccountBillTzInfo> selectAdjustItemListHd(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 台账-待付预付
     *
     * @param finVendorMonthAccountBill
     * @return int
     */
    List<FinVendorMonthAccountBillInfo> getRecordAdvancePayment(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 台账-应付暂估
     *
     * @param finVendorMonthAccountBill
     * @return int
     */
    List<FinVendorMonthAccountBillInfo> getPaymentEstimation(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 台账-应付款
     *
     * @param finVendorMonthAccountBill
     * @return int
     */
    List<FinVendorMonthAccountBillInfo> getAccountPayable(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 台账-扣款
     *
     * @param finVendorMonthAccountBill
     * @return int
     */
    List<FinVendorMonthAccountBillInfo> getBookDeduction(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 台账-调账
     *
     * @param finVendorMonthAccountBill
     * @return int
     */
    List<FinVendorMonthAccountBillInfo> getBookAdjust(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 台账-预付款
     *
     * @param finVendorMonthAccountBill
     * @return int
     */
    List<FinVendorMonthAccountBillInfo> getBookYFk(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 台账-特殊付款
     *
     * @param finVendorMonthAccountBill
     * @return int
     */
    List<FinVendorMonthAccountBillInfo> getBookTsFk(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 台账-押金
     *
     * @param finVendorMonthAccountBill
     * @return int
     */
    List<FinVendorMonthAccountBillInfo> getCashPledge(FinVendorMonthAccountBill finVendorMonthAccountBill);

    /**
     * 台账-暂押款
     *
     * @param finVendorMonthAccountBill
     * @return int
     */
    List<FinVendorMonthAccountBillInfo> getFundsFreeze(FinVendorMonthAccountBill finVendorMonthAccountBill);
}
