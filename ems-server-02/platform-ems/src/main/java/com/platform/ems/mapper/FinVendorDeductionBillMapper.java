package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinVendorDeductionBill;

/**
 * 供应商扣款单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-31
 */
public interface FinVendorDeductionBillMapper  extends BaseMapper<FinVendorDeductionBill> {


    FinVendorDeductionBill selectFinVendorDeductionBillById(Long deductionBillSid);

    List<FinVendorDeductionBill> selectFinVendorDeductionBillList(FinVendorDeductionBill finVendorDeductionBill);

    /**
     * 添加多个
     * @param list List FinVendorDeductionBill
     * @return int
     */
    int inserts(@Param("list") List<FinVendorDeductionBill> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinVendorDeductionBill
    * @return int
    */
    int updateAllById(FinVendorDeductionBill entity);

    /**
     * 更新多个
     * @param list List FinVendorDeductionBill
     * @return int
     */
    int updatesAllById(@Param("list") List<FinVendorDeductionBill> list);


}
