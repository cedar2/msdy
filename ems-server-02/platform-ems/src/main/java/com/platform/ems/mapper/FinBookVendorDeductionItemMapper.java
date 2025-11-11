package com.platform.ems.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinBookVendorDeduction;
import com.platform.ems.domain.FinBookVendorDeductionItem;
import org.apache.ibatis.annotations.Select;

/**
 * 财务流水账-明细-供应商扣款Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-02
 */
public interface FinBookVendorDeductionItemMapper  extends BaseMapper<FinBookVendorDeductionItem> {

    FinBookVendorDeductionItem selectFinBookVendorDeductionItemById(Long bookDeductionItemSid);

    List<FinBookVendorDeductionItem> selectFinBookVendorDeductionItemList(FinBookVendorDeductionItem finBookVendorDeductionItem);

    /**
     * 添加多个
     * @param list List FinBookVendorDeductionItem
     * @return int
     */
    int inserts(@Param("list") List<FinBookVendorDeductionItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookVendorDeductionItem
    * @return int
    */
    int updateAllById(FinBookVendorDeductionItem entity);

    List<FinBookVendorDeductionItem> getItemList(FinBookVendorDeduction entity);

    /**
     * 付款单提交查询
     * 按照创建日期(从最早的开始)，找到”核销状态“为”未核销“或”部分核销“，，
     * 客户与公司一致
     */
    @Select("SELECT t.*, t1.book_deduction_code, (t.currency_amount_tax_kk - t.currency_amount_tax_yhx - SUM(IFNULL(t2.currency_amount_tax,0))) AS currency_amount_tax_dhx " +
            "FROM s_fin_book_vendor_deduction_item t\n" +
            "LEFT JOIN s_fin_book_vendor_deduction t1 ON t.book_deduction_sid = t1.book_deduction_sid\n" +
            "LEFT JOIN s_fin_clear_log_vendor_deduction t2 ON t2.shengxiao_status = 'WSX' AND t.book_deduction_item_sid = t2.book_deduction_item_sid\n" +
            "WHERE t1.vendor_sid = #{vendorSid} AND t1.company_sid = #{companySid}\n" +
            "GROUP BY t.book_deduction_item_sid\n" +
            "ORDER BY t.create_date asc")
    List<FinBookVendorDeductionItem> paySubmitSelect(FinBookVendorDeductionItem entity);
}
