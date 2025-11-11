package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinCustomerAccountAdjustBill;

/**
 * 客户调账单Mapper接口
 * 
 * @author qhq
 * @date 2021-05-26
 */
public interface FinCustomerAccountAdjustBillMapper  extends BaseMapper<FinCustomerAccountAdjustBill> {


    FinCustomerAccountAdjustBill selectFinCustomerAccountAdjustBillById(Long adjustBillSid);

    List<FinCustomerAccountAdjustBill> selectFinCustomerAccountAdjustBillList(FinCustomerAccountAdjustBill finCustomerAccountAdjustBill);

    /**
     * 添加多个
     * @param list List FinCustomerAccountAdjustBill
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerAccountAdjustBill> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinCustomerAccountAdjustBill
    * @return int
    */
    int updateAllById(FinCustomerAccountAdjustBill entity);

    /**
     * 更新多个
     * @param list List FinCustomerAccountAdjustBill
     * @return int
     */
    int updatesAllById(@Param("list") List<FinCustomerAccountAdjustBill> list);


}
