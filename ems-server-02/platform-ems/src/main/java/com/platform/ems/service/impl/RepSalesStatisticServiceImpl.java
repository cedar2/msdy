package com.platform.ems.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.RepSalesStatisticMapper;
import com.platform.ems.domain.RepSalesStatistic;
import com.platform.ems.service.IRepSalesStatisticService;

/**
 * 销售统计报Service业务层处理
 *
 * @author linhongwei
 * @date 2022-02-25
 */
@Service
@SuppressWarnings("all")
public class RepSalesStatisticServiceImpl extends ServiceImpl<RepSalesStatisticMapper, RepSalesStatistic> implements IRepSalesStatisticService {
    @Autowired
    private RepSalesStatisticMapper repSalesStatisticMapper;

    /**
     * 查询销售统计报
     *
     * @param dataRecordSid 销售统计报ID
     * @return 销售统计报
     */
    @Override
    public RepSalesStatistic selectRepSalesStatisticById(Long dataRecordSid) {
        RepSalesStatistic repSalesStatistic = repSalesStatisticMapper.selectRepSalesStatisticById(dataRecordSid);
        return repSalesStatistic;
    }

    /**
     * 查询销售统计报列表
     *
     * @param repSalesStatistic 销售统计报
     * @return 销售统计报
     */
    @Override
    public List<RepSalesStatistic> selectRepSalesStatisticList(RepSalesStatistic repSalesStatistic) {
        return repSalesStatisticMapper.selectRepSalesStatisticList(repSalesStatistic);
    }

    /**
     * 新增销售统计报
     * 需要注意编码重复校验
     *
     * @param repSalesStatistic 销售统计报
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepSalesStatistic(RepSalesStatistic repSalesStatistic) {
        int row = repSalesStatisticMapper.insert(repSalesStatistic);
        return row;
    }

    /**
     * 批量删除销售统计报
     *
     * @param dataRecordSids 需要删除的销售统计报ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepSalesStatisticByIds(List<Long> dataRecordSids) {
        return repSalesStatisticMapper.deleteBatchIds(dataRecordSids);
    }

}
