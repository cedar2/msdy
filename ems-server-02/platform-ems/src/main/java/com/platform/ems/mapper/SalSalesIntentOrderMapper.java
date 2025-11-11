package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SalSalesIntentOrder;

/**
 * 销售意向单Mapper接口
 *
 * @author chenkw
 * @date 2022-10-17
 */
public interface SalSalesIntentOrderMapper extends BaseMapper<SalSalesIntentOrder> {

    SalSalesIntentOrder selectSalSalesIntentOrderById(Long salesIntentOrderSid);

    List<SalSalesIntentOrder> selectSalSalesIntentOrderList(SalSalesIntentOrder salSalesIntentOrder);

    /**
     * 添加多个
     *
     * @param list List SalSalesIntentOrder
     * @return int
     */
    int inserts(@Param("list") List<SalSalesIntentOrder> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SalSalesIntentOrder
     * @return int
     */
    int updateAllById(SalSalesIntentOrder entity);

    /**
     * 更新多个
     *
     * @param list List SalSalesIntentOrder
     * @return int
     */
    int updatesAllById(@Param("list") List<SalSalesIntentOrder> list);

    /**
     * 查询出所有处理状态为“已确认”且签收状态(纸质协议)为“未签收”的销售意向单
     * 查询出所有处理状态为“已确认”且上传状态(纸质合同)为“未上传”的销售意向单
     */
    @InterceptorIgnore(tenantLine = "true")
    List<SalSalesIntentOrder> selectSalesIntentOrderList(SalSalesIntentOrder salesIntentOrder);
}
