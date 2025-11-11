package com.platform.ems.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinBookPaymentEstimation;
import com.platform.ems.domain.FinBookPaymentEstimationItem;
import org.apache.ibatis.annotations.Select;

/**
 * 财务流水账-明细-应付暂估Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-31
 */
public interface FinBookPaymentEstimationItemMapper  extends BaseMapper<FinBookPaymentEstimationItem> {

    FinBookPaymentEstimationItem selectFinBookPaymentEstimationItemById(Long bookPaymentEstimationItemSid);

    List<FinBookPaymentEstimationItem> selectFinBookPaymentEstimationItemList(FinBookPaymentEstimationItem finBookPaymentEstimationItem);

    /**
     * 添加多个
     * @param list List FinBookPaymentEstimationItem
     * @return int
     */
    int inserts(@Param("list") List<FinBookPaymentEstimationItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookPaymentEstimationItem
    * @return int
    */
    int updateAllById(FinBookPaymentEstimationItem entity);

    /**
     * 根据查询条件直接获取明细表信息
     * @param entity
     * @return
     */
    List<FinBookPaymentEstimationItem> getItemList(FinBookPaymentEstimation entity);

    /**
     * 部分更新
     * @param entity FinBookPaymentEstimationItem
     * @return int
     */
    int updatePart(FinBookPaymentEstimationItem entity);

    /**
     * 付款单提交查询
     * 按照创建日期(从最早的开始)，找到”核销状态“为”未核销“或”部分核销“，
     * 客户与公司一致 且 未被处理状态不是“已确认”的应付暂估调价量单 引用 的 应付暂估流水
     *
     * 4、找到已被付款单引用的“生效状态”为”未生效 “，”是否退货“为”否“，客户与公司一致的核销应付暂估日志，并计算应付暂估的核销金额和
     * 5、在第三步得到的应付暂估流水中，关联显示第四步得到的核销中金额，并计算待核销金额 = 出入库金额 – 已核销金额 – 核销中金额
     *
     */
    @Select("SELECT t.*, t1.book_payment_estimation_code, t1.is_tuihuo, (t.currency_amount_tax - t.currency_amount_tax_yhx - SUM(IFNULL(t2.currency_amount_tax,0))) AS currency_amount_tax_left\n" +
            "FROM s_fin_book_payment_estimation_item t\n" +
            "LEFT JOIN s_fin_book_payment_estimation t1 ON t.book_payment_estimation_sid = t1.book_payment_estimation_sid\n" +
            "LEFT JOIN s_fin_clear_log_payment_estimation t2 ON t2.shengxiao_status = 'WSX' AND t.book_payment_estimation_item_sid = t2.book_payment_estimation_item_sid\n" +
            "WHERE t1.vendor_sid = #{vendorSid} AND t1.company_sid = #{companySid}\n" +
            " AND NOT EXISTS \n" +
            " (\n" +
            "  SELECT a.payment_estimation_adjust_bill_item_sid FROM s_fin_payment_estimation_adjust_bill_item a\n" +
            "  LEFT JOIN s_fin_payment_estimation_adjust_bill a1 ON a.payment_estimation_adjust_bill_sid = a1.payment_estimation_adjust_bill_sid\n" +
            "  WHERE a1.handle_status != '5' AND t.book_payment_estimation_item_sid = a.account_item_sid\n" +
            " )\n" +
            "GROUP BY t.book_payment_estimation_item_sid\n" +
            "ORDER BY t.create_date asc")
    List<FinBookPaymentEstimationItem> paySubmitSelect(FinBookPaymentEstimationItem entity);

}
