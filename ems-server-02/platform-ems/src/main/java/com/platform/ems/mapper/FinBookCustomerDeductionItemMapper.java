package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.FinBookReceiptEstimationItem;
import org.apache.ibatis.annotations.Param;

import com.platform.ems.domain.FinBookCustomerDeductionItem;
import org.apache.ibatis.annotations.Select;

/**
 * s_fin_book_customer_deduction_itemMapper接口
 *
 * @author linhongwei
 * @date 2021-06-08
 */
public interface FinBookCustomerDeductionItemMapper  extends BaseMapper<FinBookCustomerDeductionItem> {

    FinBookCustomerDeductionItem selectFinBookCustomerDeductionItemById(Long bookDeductionItemSid);

    List<FinBookCustomerDeductionItem> selectFinBookCustomerDeductionItemList(FinBookCustomerDeductionItem finBookCustomerDeductionItem);

    /**
     * 添加多个
     * @param list List FinBookCustomerDeductionItem
     * @return int
     */
    int inserts(@Param("list") List<FinBookCustomerDeductionItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookCustomerDeductionItem
    * @return int
    */
    int updateAllById(FinBookCustomerDeductionItem entity);

    /**
     * 收款单提交查询
     * 按照创建日期(从最早的开始)，找到”核销状态“为”未核销“或”部分核销“，，
     * 客户与公司一致
     */
    @Select("SELECT t.*, t1.book_deduction_code, (t.currency_amount_tax_kk - t.currency_amount_tax_yhx - SUM(IFNULL(t2.currency_amount_tax,0))) AS currency_amount_tax_dhx " +
            "FROM s_fin_book_customer_deduction_item t\n" +
            "LEFT JOIN s_fin_book_customer_deduction t1 ON t.book_deduction_sid = t1.book_deduction_sid\n" +
            "LEFT JOIN s_fin_clear_log_customer_deduction t2 ON t2.shengxiao_status = 'WSX' AND t.book_deduction_item_sid = t2.book_deduction_item_sid\n" +
            "WHERE t1.customer_sid = #{customerSid} AND t1.company_sid = #{companySid}\n" +
            "GROUP BY t.book_deduction_item_sid\n" +
            "ORDER BY t.create_date asc")
    List<FinBookCustomerDeductionItem> receivableSubmitSelect(FinBookCustomerDeductionItem entity);
}
