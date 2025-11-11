package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepSalesStatusOrderTotal;
import org.apache.ibatis.annotations.Update;

/**
 * 销售状况-销售占比/销售趋势/销售同比Mapper接口
 *
 * @author linhongwei
 * @date 2022-02-25
 */
public interface RepSalesStatusOrderTotalMapper extends BaseMapper<RepSalesStatusOrderTotal> {


    RepSalesStatusOrderTotal selectRepSalesStatusOrderTotalById(Long dataRecordSid);

    //清空指定表
    @Update("truncate table s_rep_sales_status_order_total")
    void deleteAll();

    List<RepSalesStatusOrderTotal> selectRepSalesStatusOrderTotalList(RepSalesStatusOrderTotal repSalesStatusOrderTotal);

    /**
     * 添加多个
     *
     * @param list List RepSalesStatusOrderTotal
     * @return int
     */
    int inserts(@Param("list") List<RepSalesStatusOrderTotal> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity RepSalesStatusOrderTotal
     * @return int
     */
    int updateAllById(RepSalesStatusOrderTotal entity);

    /**
     * 更新多个
     *
     * @param list List RepSalesStatusOrderTotal
     * @return int
     */
    int updatesAllById(@Param("list") List<RepSalesStatusOrderTotal> list);


}
