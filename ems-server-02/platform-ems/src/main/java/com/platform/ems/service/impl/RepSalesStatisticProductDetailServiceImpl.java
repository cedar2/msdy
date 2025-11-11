package com.platform.ems.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.RepSalesStatisticProductDetailMapper;
import com.platform.ems.domain.RepSalesStatisticProductDetail;
import com.platform.ems.service.IRepSalesStatisticProductDetailService;

/**
 * 销售统计报-款明细Service业务层处理
 *
 * @author linhongwei
 * @date 2022-02-25
 */
@Service
@SuppressWarnings("all")
public class RepSalesStatisticProductDetailServiceImpl extends ServiceImpl<RepSalesStatisticProductDetailMapper, RepSalesStatisticProductDetail> implements IRepSalesStatisticProductDetailService {
    @Autowired
    private RepSalesStatisticProductDetailMapper repSalesStatisticProductDetailMapper;

    /**
     * 查询销售统计报-款明细
     *
     * @param dataRecordSid 销售统计报-款明细ID
     * @return 销售统计报-款明细
     */
    @Override
    public RepSalesStatisticProductDetail selectRepSalesStatisticProductDetailById(Long dataRecordSid) {
        RepSalesStatisticProductDetail repSalesStatisticProductDetail = repSalesStatisticProductDetailMapper.selectRepSalesStatisticProductDetailById(dataRecordSid);
        return repSalesStatisticProductDetail;
    }

    /**
     * 查询销售统计报-款明细列表
     *
     * @param repSalesStatisticProductDetail 销售统计报-款明细
     * @return 销售统计报-款明细
     */
    @Override
    public List<RepSalesStatisticProductDetail> selectRepSalesStatisticProductDetailList(RepSalesStatisticProductDetail repSalesStatisticProductDetail) {
        return repSalesStatisticProductDetailMapper.selectRepSalesStatisticProductDetailList(repSalesStatisticProductDetail);
    }

    /**
     * 新增销售统计报-款明细
     * 需要注意编码重复校验
     *
     * @param repSalesStatisticProductDetail 销售统计报-款明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepSalesStatisticProductDetail(RepSalesStatisticProductDetail repSalesStatisticProductDetail) {
        int row = repSalesStatisticProductDetailMapper.insert(repSalesStatisticProductDetail);
        return row;
    }

    /**
     * 批量删除销售统计报-款明细
     *
     * @param dataRecordSids 需要删除的销售统计报-款明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepSalesStatisticProductDetailByIds(List<Long> dataRecordSids) {
        return repSalesStatisticProductDetailMapper.deleteBatchIds(dataRecordSids);
    }

}
