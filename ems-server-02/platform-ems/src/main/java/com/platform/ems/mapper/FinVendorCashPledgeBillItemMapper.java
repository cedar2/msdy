package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinVendorCashPledgeBillItem;

/**
 * 供应商押金-明细Mapper接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface FinVendorCashPledgeBillItemMapper extends BaseMapper<FinVendorCashPledgeBillItem> {


    FinVendorCashPledgeBillItem selectFinVendorCashPledgeBillItemById(Long cashPledgeBillItemSid);

    List<FinVendorCashPledgeBillItem> selectFinVendorCashPledgeBillItemList(FinVendorCashPledgeBillItem finVendorCashPledgeBillItem);

    /**
     * 添加多个
     *
     * @param list List FinVendorCashPledgeBillItem
     * @return int
     */
    int inserts(@Param("list") List<FinVendorCashPledgeBillItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinVendorCashPledgeBillItem
     * @return int
     */
    int updateAllById(FinVendorCashPledgeBillItem entity);

    /**
     * 更新多个
     *
     * @param list List FinVendorCashPledgeBillItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinVendorCashPledgeBillItem> list);


}