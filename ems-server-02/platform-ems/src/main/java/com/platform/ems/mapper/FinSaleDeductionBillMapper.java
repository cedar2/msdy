package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinSaleDeductionBill;
import com.platform.ems.domain.SalServiceAcceptance;

/**
 * 销售扣款单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-09
 */
public interface FinSaleDeductionBillMapper  extends BaseMapper<FinSaleDeductionBill> {


    FinSaleDeductionBill selectFinSaleDeductionById(Long saleDeductionSid);

    List<FinSaleDeductionBill> selectFinSaleDeductionList(FinSaleDeductionBill FinSaleDeductionBill);

    /**
     * 添加多个
     * @param list List FinSaleDeductionBill
     * @return int
     */
    int inserts(@Param("list") List<FinSaleDeductionBill> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinSaleDeductionBill
    * @return int
    */
    int updateAllById(FinSaleDeductionBill entity);

    /**
     * 更新多个
     * @param list List FinSaleDeductionBill
     * @return int
     */
    int updatesAllById(@Param("list") List<FinSaleDeductionBill> list);


    int countByDomain(SalServiceAcceptance params);

    int deleteFinSaleDeductionByIds(@Param("array") Long[] saleDeductionSids);

    int confirm(FinSaleDeductionBill FinSaleDeductionBill);
}
