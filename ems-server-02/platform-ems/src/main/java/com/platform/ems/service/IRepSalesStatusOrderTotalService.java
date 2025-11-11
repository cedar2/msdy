package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.RepSalesStatusOrderTotal;
import com.platform.ems.domain.dto.response.RepSalesStatusOrderProResponse;
import com.platform.ems.domain.dto.response.RepSalesStatusOrderTotalResponse;
import com.platform.ems.domain.dto.response.RepSalesStatusOrderTotalTrend;

/**
 * 销售状况-销售占比/销售趋势/销售同比Service接口
 *
 * @author linhongwei
 * @date 2022-02-25
 */
public interface IRepSalesStatusOrderTotalService extends IService<RepSalesStatusOrderTotal> {
    /**
     * 查询销售状况-销售占比/销售趋势/销售同比
     *
     * @param dataRecordSid 销售状况-销售占比/销售趋势/销售同比ID
     * @return 销售状况-销售占比/销售趋势/销售同比
     */
    public RepSalesStatusOrderTotal selectRepSalesStatusOrderTotalById(Long dataRecordSid);

    public RepSalesStatusOrderTotalResponse getReport(String productSeasonCode);
    /**
     * 查询销售趋势报表
     *
     */
    public RepSalesStatusOrderTotalTrend geTrendt(String code);
    //销售同比
    public RepSalesStatusOrderProResponse getPro(String productSeasonCode);
    /**
     * 新增销售状况-销售占比/销售趋势/销售同比
     *
     * @param repSalesStatusOrderTotal 销售状况-销售占比/销售趋势/销售同比
     * @return 结果
     */
    public int insertRepSalesStatusOrderTotal(RepSalesStatusOrderTotal repSalesStatusOrderTotal);

    /**
     * 批量删除销售状况-销售占比/销售趋势/销售同比
     *
     * @param dataRecordSids 需要删除的销售状况-销售占比/销售趋势/销售同比ID
     * @return 结果
     */
    public int deleteRepSalesStatusOrderTotalByIds(List<Long> dataRecordSids);

}
