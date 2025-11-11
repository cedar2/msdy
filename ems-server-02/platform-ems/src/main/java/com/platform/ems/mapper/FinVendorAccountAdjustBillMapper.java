package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinVendorAccountAdjustBill;

/**
 * 供应商调账单Mapper接口
 * 
 * @author qhq
 * @date 2021-05-26
 */
public interface FinVendorAccountAdjustBillMapper  extends BaseMapper<FinVendorAccountAdjustBill> {


    FinVendorAccountAdjustBill selectFinVendorAccountAdjustBillById(Long adjustBillSid);

    List<FinVendorAccountAdjustBill> selectFinVendorAccountAdjustBillList(FinVendorAccountAdjustBill finVendorAccountAdjustBill);

    /**
     * 添加多个
     * @param list List FinVendorAccountAdjustBill
     * @return int
     */
    int inserts(@Param("list") List<FinVendorAccountAdjustBill> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinVendorAccountAdjustBill
    * @return int
    */
    int updateAllById(FinVendorAccountAdjustBill entity);

    /**
     * 更新多个
     * @param list List FinVendorAccountAdjustBill
     * @return int
     */
    int updatesAllById(@Param("list") List<FinVendorAccountAdjustBill> list);


}
