package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinCustomerFundsFreezeBillItem;

/**
 * 客户暂押款-明细Mapper接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface FinCustomerFundsFreezeBillItemMapper extends BaseMapper<FinCustomerFundsFreezeBillItem> {


    FinCustomerFundsFreezeBillItem selectFinCustomerFundsFreezeBillItemById(Long fundsFreezeBillItemSid);

    List<FinCustomerFundsFreezeBillItem> selectFinCustomerFundsFreezeBillItemList(FinCustomerFundsFreezeBillItem finCustomerFundsFreezeBillItem);

    /**
     * 添加多个
     *
     * @param list List FinCustomerFundsFreezeBillItem
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerFundsFreezeBillItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinCustomerFundsFreezeBillItem
     * @return int
     */
    int updateAllById(FinCustomerFundsFreezeBillItem entity);

    /**
     * 更新多个
     *
     * @param list List FinCustomerFundsFreezeBillItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinCustomerFundsFreezeBillItem> list);


}
