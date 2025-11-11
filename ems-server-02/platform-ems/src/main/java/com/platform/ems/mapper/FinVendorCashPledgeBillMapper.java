package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinVendorCashPledgeBill;

/**
 * 供应商押金Mapper接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface FinVendorCashPledgeBillMapper extends BaseMapper<FinVendorCashPledgeBill> {

    FinVendorCashPledgeBill selectFinVendorCashPledgeBillById(Long cashPledgeBillSid);

    List<FinVendorCashPledgeBill> selectFinVendorCashPledgeBillList(FinVendorCashPledgeBill finVendorCashPledgeBill);

    /**
     * 添加多个
     *
     * @param list List FinVendorCashPledgeBill
     * @return int
     */
    int inserts(@Param("list") List<FinVendorCashPledgeBill> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinVendorCashPledgeBill
     * @return int
     */
    int updateAllById(FinVendorCashPledgeBill entity);

}
