package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinBookPaymentItem;
import org.apache.ibatis.annotations.Select;

/**
 * 财务流水账-明细-付款Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-07
 */
public interface FinBookPaymentItemMapper  extends BaseMapper<FinBookPaymentItem> {

    FinBookPaymentItem selectFinBookPaymentItemById(Long bookPaymentItemSid);

    List<FinBookPaymentItem> selectFinBookPaymentItemList(FinBookPaymentItem finBookPaymentItem);

    /**
     * 添加多个
     * @param list List FinBookPaymentItem
     * @return int
     */
    int inserts(@Param("list") List<FinBookPaymentItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookPaymentItem
    * @return int
    */
    int updateAllById(FinBookPaymentItem entity);

    /**
     * 付款单提交查询
     * 按照创建日期(从最早的开始)，找到”核销状态“为”未核销“或”部分核销“，，
     * 客户与公司一致
     */
    @Select("SELECT t.*, t1.book_payment_code, (t.currency_amount_tax_fk - t.currency_amount_tax_yhx - SUM(IFNULL(t2.currency_amount_tax,0))) AS currency_amount_tax_dhx " +
            "FROM s_fin_book_payment_item t\n" +
            "LEFT JOIN s_fin_book_payment t1 ON t.book_payment_sid = t1.book_payment_sid\n" +
            "LEFT JOIN s_fin_clear_log_advance_payment t2 ON t2.shengxiao_status = 'WSX' AND t.book_payment_item_sid = t2.book_payment_item_sid\n" +
            "WHERE t1.vendor_sid = #{vendorSid} AND t1.company_sid = #{companySid} AND t1.book_source_category = 'YFK'\n" +
            "GROUP BY t.book_payment_item_sid\n" +
            "ORDER BY t.create_date asc")
    List<FinBookPaymentItem> paySubmitSelect(FinBookPaymentItem entity);
}
