package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinPurchaseDeductionBillItem;

/**
 * 采购扣款单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-10
 */
public interface FinPurchaseDeductionBillItemMapper  extends BaseMapper<FinPurchaseDeductionBillItem> {


    FinPurchaseDeductionBillItem selectFinPurchaseDeductionItemById(Long purchaseDeductionItemSid);

    List<FinPurchaseDeductionBillItem> selectFinPurchaseDeductionItemList(FinPurchaseDeductionBillItem FinPurchaseDeductionBillItem);

    /**
     * 添加多个
     * @param list List FinPurchaseDeductionBillItem
     * @return int
     */
    int inserts(@Param("list") List<FinPurchaseDeductionBillItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinPurchaseDeductionBillItem
    * @return int
    */
    int updateAllById(FinPurchaseDeductionBillItem entity);

    /**
     * 更新多个
     * @param list List FinPurchaseDeductionBillItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPurchaseDeductionBillItem> list);


    void deleteFinPurchaseDeductionItemByIds(@Param("array")Long[] purchaseDeductionSids);
}
