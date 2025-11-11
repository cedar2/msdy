package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinVendorAccountBalanceBill;

/**
 * 供应商账互抵单Mapper接口
 * 
 * @author qhq
 * @date 2021-05-27
 */
public interface FinVendorAccountBalanceBillMapper  extends BaseMapper<FinVendorAccountBalanceBill> {


    FinVendorAccountBalanceBill selectFinVendorAccountBalanceBillById(Long vendorAccountBalanceBillSid);

    List<FinVendorAccountBalanceBill> selectFinVendorAccountBalanceBillList(FinVendorAccountBalanceBill finVendorAccountBalanceBill);

    /**
     * 添加多个
     * @param list List FinVendorAccountBalanceBill
     * @return int
     */
    int inserts(@Param("list") List<FinVendorAccountBalanceBill> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinVendorAccountBalanceBill
    * @return int
    */
    int updateAllById(FinVendorAccountBalanceBill entity);

    /**
     * 更新多个
     * @param list List FinVendorAccountBalanceBill
     * @return int
     */
    int updatesAllById(@Param("list") List<FinVendorAccountBalanceBill> list);


}
