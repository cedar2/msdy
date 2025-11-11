package com.platform.ems.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.RepManufactureStatisticMapper;
import com.platform.ems.domain.RepManufactureStatistic;
import com.platform.ems.service.IRepManufactureStatisticService;

/**
 * 生产统计报Service业务层处理
 *
 * @author chenkw
 * @date 2022-05-11
 */
@Service
@SuppressWarnings("all")
public class RepManufactureStatisticServiceImpl extends ServiceImpl<RepManufactureStatisticMapper, RepManufactureStatistic> implements IRepManufactureStatisticService {
    @Autowired
    private RepManufactureStatisticMapper repManufactureStatisticMapper;

    /**
     * 查询生产统计报
     *
     * @param dataRecordSid 生产统计报ID
     * @return 生产统计报
     */
    @Override
    public RepManufactureStatistic selectRepManufactureStatisticById(Long dataRecordSid) {
        RepManufactureStatistic repManufactureStatistic = repManufactureStatisticMapper.selectRepManufactureStatisticById(dataRecordSid);
        return repManufactureStatistic;
    }

    /**
     * 查询生产统计报列表
     *
     * @param repManufactureStatistic 生产统计报
     * @return 生产统计报
     */
    @Override
    public List<RepManufactureStatistic> selectRepManufactureStatisticList(RepManufactureStatistic repManufactureStatistic) {
        return repManufactureStatisticMapper.selectRepManufactureStatisticList(repManufactureStatistic);
    }

    /**
     * 新增生产统计报
     * 需要注意编码重复校验
     *
     * @param repManufactureStatistic 生产统计报
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepManufactureStatistic(RepManufactureStatistic repManufactureStatistic) {
        int row = repManufactureStatisticMapper.insert(repManufactureStatistic);
        return row;
    }

    /**
     * 批量删除生产统计报
     *
     * @param dataRecordSids 需要删除的生产统计报ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepManufactureStatisticByIds(List<Long> dataRecordSids) {
        return repManufactureStatisticMapper.deleteBatchIds(dataRecordSids);
    }

}