package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinVendorDeductionBillItem;

/**
 * 供应商扣款单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-31
 */
public interface FinVendorDeductionBillItemMapper  extends BaseMapper<FinVendorDeductionBillItem> {


    FinVendorDeductionBillItem selectFinVendorDeductionBillItemById(Long deductionBillItemSid);

    List<FinVendorDeductionBillItem> selectFinVendorDeductionBillItemList(FinVendorDeductionBillItem finVendorDeductionBillItem);

    /**
     * 添加多个
     * @param list List FinVendorDeductionBillItem
     * @return int
     */
    int inserts(@Param("list") List<FinVendorDeductionBillItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinVendorDeductionBillItem
    * @return int
    */
    int updateAllById(FinVendorDeductionBillItem entity);

    /**
     * 更新多个
     * @param list List FinVendorDeductionBillItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinVendorDeductionBillItem> list);


}
