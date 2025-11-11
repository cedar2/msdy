package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinBookCustomerAccountAdjust;
import com.platform.ems.domain.FinBookCustomerAccountAdjustItem;

/**
 * 财务流水账-明细-客户调账Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-08
 */
public interface FinBookCustomerAccountAdjustItemMapper  extends BaseMapper<FinBookCustomerAccountAdjustItem> {


    FinBookCustomerAccountAdjustItem selectFinBookCustomerAccountAdjustItemById(Long bookAccountAdjustItemSid);

    List<FinBookCustomerAccountAdjustItem> selectFinBookCustomerAccountAdjustItemList(FinBookCustomerAccountAdjustItem finBookCustomerAccountAdjustItem);

    /**
     * 添加多个
     * @param list List FinBookCustomerAccountAdjustItem
     * @return int
     */
    int inserts(@Param("list") List<FinBookCustomerAccountAdjustItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookCustomerAccountAdjustItem
    * @return int
    */
    int updateAllById(FinBookCustomerAccountAdjustItem entity);

    /**
     * 更新多个
     * @param list List FinBookCustomerAccountAdjustItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookCustomerAccountAdjustItem> list);

    /**
     * 通过主表条件获取明细list
     * @param entity
     * @return
     */
    List<FinBookCustomerAccountAdjustItem> getItemList(FinBookCustomerAccountAdjust entity);
}
