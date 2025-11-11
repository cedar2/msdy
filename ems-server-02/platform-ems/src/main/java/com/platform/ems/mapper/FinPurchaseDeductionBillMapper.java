package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinPurchaseDeductionBill;

/**
 * 采购扣款单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-10
 */
public interface FinPurchaseDeductionBillMapper  extends BaseMapper<FinPurchaseDeductionBill> {


    FinPurchaseDeductionBill selectFinPurchaseDeductionById(Long purchaseDeductionSid);

    List<FinPurchaseDeductionBill> selectFinPurchaseDeductionList(FinPurchaseDeductionBill finPurchaseDeductionBill);

    /**
     * 添加多个
     * @param list List FinPurchaseDeductionBill
     * @return int
     */
    int inserts(@Param("list") List<FinPurchaseDeductionBill> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinPurchaseDeductionBill
    * @return int
    */
    int updateAllById(FinPurchaseDeductionBill entity);

    /**
     * 更新多个
     * @param list List FinPurchaseDeductionBill
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPurchaseDeductionBill> list);


    int countByDomain(FinPurchaseDeductionBill params);

    int deleteFinPurchaseDeductionByIds(@Param("array")Long[] purchaseDeductionSids);

    int confirm(FinPurchaseDeductionBill finPurchaseDeductionBill);
}
