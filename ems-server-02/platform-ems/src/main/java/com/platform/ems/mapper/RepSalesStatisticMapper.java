package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepSalesStatistic;

/**
 * 销售统计报Mapper接口
 *
 * @author linhongwei
 * @date 2022-02-25
 */
public interface RepSalesStatisticMapper extends BaseMapper<RepSalesStatistic> {


    RepSalesStatistic selectRepSalesStatisticById(Long dataRecordSid);

    List<RepSalesStatistic> selectRepSalesStatisticList(RepSalesStatistic repSalesStatistic);

    /**
     * 添加多个
     *
     * @param list List RepSalesStatistic
     * @return int
     */
    int inserts(@Param("list") List<RepSalesStatistic> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity RepSalesStatistic
     * @return int
     */
    int updateAllById(RepSalesStatistic entity);

    /**
     * 更新多个
     *
     * @param list List RepSalesStatistic
     * @return int
     */
    int updatesAllById(@Param("list") List<RepSalesStatistic> list);


}
