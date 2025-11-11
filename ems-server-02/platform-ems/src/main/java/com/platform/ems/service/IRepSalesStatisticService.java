package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepSalesStatistic;

/**
 * 销售统计报Service接口
 *
 * @author linhongwei
 * @date 2022-02-25
 */
public interface IRepSalesStatisticService extends IService<RepSalesStatistic> {
    /**
     * 查询销售统计报
     *
     * @param dataRecordSid 销售统计报ID
     * @return 销售统计报
     */
    public RepSalesStatistic selectRepSalesStatisticById(Long dataRecordSid);

    /**
     * 查询销售统计报列表
     *
     * @param repSalesStatistic 销售统计报
     * @return 销售统计报集合
     */
    public List<RepSalesStatistic> selectRepSalesStatisticList(RepSalesStatistic repSalesStatistic);

    /**
     * 新增销售统计报
     *
     * @param repSalesStatistic 销售统计报
     * @return 结果
     */
    public int insertRepSalesStatistic(RepSalesStatistic repSalesStatistic);

    /**
     * 批量删除销售统计报
     *
     * @param dataRecordSids 需要删除的销售统计报ID
     * @return 结果
     */
    public int deleteRepSalesStatisticByIds(List<Long> dataRecordSids);

}
