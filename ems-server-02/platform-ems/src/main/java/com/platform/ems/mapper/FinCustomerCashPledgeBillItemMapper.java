package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinCustomerCashPledgeBillItem;

/**
 * 客户押金-明细Mapper接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface FinCustomerCashPledgeBillItemMapper extends BaseMapper<FinCustomerCashPledgeBillItem> {


    FinCustomerCashPledgeBillItem selectFinCustomerCashPledgeBillItemById(Long cashPledgeBillItemSid);

    List<FinCustomerCashPledgeBillItem> selectFinCustomerCashPledgeBillItemList(FinCustomerCashPledgeBillItem finCustomerCashPledgeBillItem);

    /**
     * 添加多个
     *
     * @param list List FinCustomerCashPledgeBillItem
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerCashPledgeBillItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinCustomerCashPledgeBillItem
     * @return int
     */
    int updateAllById(FinCustomerCashPledgeBillItem entity);

    /**
     * 更新多个
     *
     * @param list List FinCustomerCashPledgeBillItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinCustomerCashPledgeBillItem> list);


}
