package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepManufactureStatistic;

/**
 * 生产统计报Service接口
 *
 * @author chenkw
 * @date 2022-05-11
 */
public interface IRepManufactureStatisticService extends IService<RepManufactureStatistic>{
    /**
     * 查询生产统计报
     *
     * @param dataRecordSid 生产统计报ID
     * @return 生产统计报
     */
    public RepManufactureStatistic selectRepManufactureStatisticById(Long dataRecordSid);

    /**
     * 查询生产统计报列表
     *
     * @param repManufactureStatistic 生产统计报
     * @return 生产统计报集合
     */
    public List<RepManufactureStatistic> selectRepManufactureStatisticList(RepManufactureStatistic repManufactureStatistic);

    /**
     * 新增生产统计报
     *
     * @param repManufactureStatistic 生产统计报
     * @return 结果
     */
    public int insertRepManufactureStatistic(RepManufactureStatistic repManufactureStatistic);

    /**
     * 批量删除生产统计报
     *
     * @param dataRecordSids 需要删除的生产统计报ID
     * @return 结果
     */
    public int deleteRepManufactureStatisticByIds(List<Long>  dataRecordSids);


}