package com.platform.ems.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinBookReceiptPaymentItem;
import org.apache.ibatis.annotations.Select;

/**
 * 财务流水账-明细-收款Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-09
 */
public interface FinBookReceiptPaymentItemMapper  extends BaseMapper<FinBookReceiptPaymentItem> {

    FinBookReceiptPaymentItem selectFinBookReceiptPaymentItemById(Long bookReceiptPaymentItemSid);

    List<FinBookReceiptPaymentItem> selectFinBookReceiptPaymentItemList(FinBookReceiptPaymentItem finBookReceiptPaymentItem);

    /**
     * 添加多个
     * @param list List FinBookReceiptPaymentItem
     * @return int
     */
    int inserts(@Param("list") List<FinBookReceiptPaymentItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookReceiptPaymentItem
    * @return int
    */
    int updateAllById(FinBookReceiptPaymentItem entity);

    /**
     * 收款单提交查询
     * 按照创建日期(从最早的开始)，找到”核销状态“为”未核销“或”部分核销“，，
     * 客户与公司一致
     */
    @Select("SELECT t.*, t1.book_receipt_payment_code, (t.currency_amount_tax_sk - t.currency_amount_tax_yhx - SUM(IFNULL(t2.currency_amount_tax,0))) AS currency_amount_tax_dhx " +
            "FROM s_fin_book_receipt_payment_item t\n" +
            "LEFT JOIN s_fin_book_receipt_payment t1 ON t.book_receipt_payment_sid = t1.book_receipt_payment_sid\n" +
            "LEFT JOIN s_fin_clear_log_advance_receipt_payment t2 ON t2.shengxiao_status = 'WSX' AND t.book_receipt_payment_item_sid = t2.book_receipt_payment_item_sid\n" +
            "WHERE t1.customer_sid = #{customerSid} AND t1.company_sid = #{companySid} AND t1.book_source_category = 'YSK'\n" +
            "GROUP BY t.book_receipt_payment_item_sid\n" +
            "ORDER BY t.create_date asc")
    List<FinBookReceiptPaymentItem> receivableSubmitSelect(FinBookReceiptPaymentItem entity);
}
