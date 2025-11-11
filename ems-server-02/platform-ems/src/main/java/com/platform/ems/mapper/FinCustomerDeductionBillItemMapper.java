package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinCustomerDeductionBillItem;

/**
 * 客户扣款单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-08
 */
public interface FinCustomerDeductionBillItemMapper  extends BaseMapper<FinCustomerDeductionBillItem> {


    FinCustomerDeductionBillItem selectFinCustomerDeductionBillItemById(Long deductionBillItemSid);

    List<FinCustomerDeductionBillItem> selectFinCustomerDeductionBillItemList(FinCustomerDeductionBillItem finCustomerDeductionBillItem);

    /**
     * 添加多个
     * @param list List FinCustomerDeductionBillItem
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerDeductionBillItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinCustomerDeductionBillItem
    * @return int
    */
    int updateAllById(FinCustomerDeductionBillItem entity);

    /**
     * 更新多个
     * @param list List FinCustomerDeductionBillItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinCustomerDeductionBillItem> list);


}
