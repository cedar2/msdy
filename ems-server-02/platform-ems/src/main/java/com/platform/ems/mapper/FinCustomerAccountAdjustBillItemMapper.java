package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinCustomerAccountAdjustBillItem;

/**
 * 客户调账单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-26
 */
public interface FinCustomerAccountAdjustBillItemMapper  extends BaseMapper<FinCustomerAccountAdjustBillItem> {


    FinCustomerAccountAdjustBillItem selectFinCustomerAccountAdjustBillItemById(Long adjustBillItemSid);

    List<FinCustomerAccountAdjustBillItem> selectFinCustomerAccountAdjustBillItemList(FinCustomerAccountAdjustBillItem finCustomerAccountAdjustBillItem);

    /**
     * 添加多个
     * @param list List FinCustomerAccountAdjustBillItem
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerAccountAdjustBillItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinCustomerAccountAdjustBillItem
    * @return int
    */
    int updateAllById(FinCustomerAccountAdjustBillItem entity);

    /**
     * 更新多个
     * @param list List FinCustomerAccountAdjustBillItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinCustomerAccountAdjustBillItem> list);


}
