package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PurVendorMonthAccountBill;
import com.platform.ems.domain.PurVendorMonthAccountBillInfo;
import com.platform.ems.domain.PurVendorMonthAccountBillKkInfo;
import com.platform.ems.domain.PurVendorMonthAccountBillTzInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供应商对账单Mapper接口
 *
 */
public interface PurVendorMonthAccountBillMapper extends BaseMapper<PurVendorMonthAccountBill> {

    PurVendorMonthAccountBill selectPurVendorMonthAccountBillById(Long vendorMonthAccountBillSid);

    List<PurVendorMonthAccountBill> selectPurVendorMonthAccountBillList(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 添加多个
     *
     * @param list List PurVendorMonthAccountBill
     * @return int
     */
    int inserts(@Param("list") List<PurVendorMonthAccountBill> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PurVendorMonthAccountBill
     * @return int
     */
    int updateAllById(PurVendorMonthAccountBill entity);

    /**
     * 本期扣款:显示供应商的付款的“月账单所属期间”等于月账单的”所属年月”的付款中类型是“扣款”的扣款明细
     *
     * @param purVendorMonthAccountBill
     * @return int
     */
    List<PurVendorMonthAccountBillKkInfo> selectDeductionItemListFk(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 本期扣款:显示供应商的采购发票的“月账单所属期间”等于月账单的”所属年月”的折扣明细中类型是“扣款”的扣款明细
     *
     * @param purVendorMonthAccountBill
     * @return int
     */
    List<PurVendorMonthAccountBillKkInfo> selectDeductionItemListFp(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 本期扣款:显示供应商的互抵单的“月账单所属期间”等于月账单的”所属年月”的明细中类型是“扣款”的扣款明细
     *
     * @param purVendorMonthAccountBill
     * @return int
     */
    List<PurVendorMonthAccountBillKkInfo> selectDeductionItemListHd(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 本期调账:显示供应商的采购发票的“月账单所属期间”等于月账单的”所属年月”的折扣明细中类型是“调账”的调账明细
     *
     * @param purVendorMonthAccountBill
     * @return int
     */
    List<PurVendorMonthAccountBillTzInfo> selectAdjustItemListFp(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 本期调账:显示供应商的付款的“月账单所属期间”等于月账单的”所属年月”的付款中类型是“调账”的调账明细
     *
     * @param purVendorMonthAccountBill
     * @return int
     */
    List<PurVendorMonthAccountBillTzInfo> selectAdjustItemListFk(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 本期调账:显示供应商的互抵单的“月账单所属期间”等于月账单的”所属年月”的明细中类型是“调账”的调账明细
     *
     * @param purVendorMonthAccountBill
     * @return int
     */
    List<PurVendorMonthAccountBillTzInfo> selectAdjustItemListHd(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 台账-待付预付
     *
     * @param purVendorMonthAccountBill
     * @return int
     */
    List<PurVendorMonthAccountBillInfo> getRecordAdvancePayment(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 台账-应付暂估
     *
     * @param purVendorMonthAccountBill
     * @return int
     */
    List<PurVendorMonthAccountBillInfo> getPaymentEstimation(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 台账-应付款
     *
     * @param purVendorMonthAccountBill
     * @return int
     */
    List<PurVendorMonthAccountBillInfo> getAccountPayable(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 台账-扣款
     *
     * @param purVendorMonthAccountBill
     * @return int
     */
    List<PurVendorMonthAccountBillInfo> getBookDeduction(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 台账-调账
     *
     * @param purVendorMonthAccountBill
     * @return int
     */
    List<PurVendorMonthAccountBillInfo> getBookAdjust(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 台账-预付款
     *
     * @param purVendorMonthAccountBill
     * @return int
     */
    List<PurVendorMonthAccountBillInfo> getBookYFk(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 台账-特殊付款
     *
     * @param purVendorMonthAccountBill
     * @return int
     */
    List<PurVendorMonthAccountBillInfo> getBookTsFk(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 台账-押金
     *
     * @param purVendorMonthAccountBill
     * @return int
     */
    List<PurVendorMonthAccountBillInfo> getCashPledge(PurVendorMonthAccountBill purVendorMonthAccountBill);

    /**
     * 台账-暂押款
     *
     * @param purVendorMonthAccountBill
     * @return int
     */
    List<PurVendorMonthAccountBillInfo> getFundsFreeze(PurVendorMonthAccountBill purVendorMonthAccountBill);
}
