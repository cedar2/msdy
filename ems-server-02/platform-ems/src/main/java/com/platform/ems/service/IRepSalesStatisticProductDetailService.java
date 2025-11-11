package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepSalesStatisticProductDetail;

/**
 * 销售统计报-款明细Service接口
 *
 * @author linhongwei
 * @date 2022-02-25
 */
public interface IRepSalesStatisticProductDetailService extends IService<RepSalesStatisticProductDetail> {
    /**
     * 查询销售统计报-款明细
     *
     * @param dataRecordSid 销售统计报-款明细ID
     * @return 销售统计报-款明细
     */
    public RepSalesStatisticProductDetail selectRepSalesStatisticProductDetailById(Long dataRecordSid);

    /**
     * 查询销售统计报-款明细列表
     *
     * @param repSalesStatisticProductDetail 销售统计报-款明细
     * @return 销售统计报-款明细集合
     */
    public List<RepSalesStatisticProductDetail> selectRepSalesStatisticProductDetailList(RepSalesStatisticProductDetail repSalesStatisticProductDetail);

    /**
     * 新增销售统计报-款明细
     *
     * @param repSalesStatisticProductDetail 销售统计报-款明细
     * @return 结果
     */
    public int insertRepSalesStatisticProductDetail(RepSalesStatisticProductDetail repSalesStatisticProductDetail);

    /**
     * 批量删除销售统计报-款明细
     *
     * @param dataRecordSids 需要删除的销售统计报-款明细ID
     * @return 结果
     */
    public int deleteRepSalesStatisticProductDetailByIds(List<Long> dataRecordSids);

}
