package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinSaleDeductionBillItem;

/**
 * 销售扣款单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-09
 */
public interface FinSaleDeductionBillItemMapper  extends BaseMapper<FinSaleDeductionBillItem> {


    FinSaleDeductionBillItem selectFinSaleDeductionItemById(Long saleDeductionItemSid);

    List<FinSaleDeductionBillItem> selectFinSaleDeductionItemList(FinSaleDeductionBillItem FinSaleDeductionBillItem);

    /**
     * 添加多个
     * @param list List FinSaleDeductionBillItem
     * @return int
     */
    int inserts(@Param("list") List<FinSaleDeductionBillItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinSaleDeductionBillItem
    * @return int
    */
    int updateAllById(FinSaleDeductionBillItem entity);

    /**
     * 更新多个
     * @param list List FinSaleDeductionBillItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinSaleDeductionBillItem> list);


    void deleteFinSaleDeductionItemByIds(@Param("array") Long[] saleDeductionSids);
}
