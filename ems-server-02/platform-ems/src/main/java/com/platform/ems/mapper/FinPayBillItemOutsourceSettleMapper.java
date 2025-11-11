package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinPayBillItemOutsourceSettle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 付款单-外发加工费结算单明细Mapper接口
 *
 * @author platform
 * @date 2024-05-22
 */
public interface FinPayBillItemOutsourceSettleMapper  extends BaseMapper<FinPayBillItemOutsourceSettle> {

    /**
     * 查询详情
     * @param payBillItemOutsourceSettleSid 单据sid
     * @return FinPayBillItemOutsourceSettle
     */
    FinPayBillItemOutsourceSettle selectFinPayBillItemOutsourceSettleById(Long payBillItemOutsourceSettleSid);

    /**
     * 查询列表
     * @param finPayBillItemOutsourceSettle FinPayBillItemOutsourceSettle
     * @return List
     */
    List<FinPayBillItemOutsourceSettle> selectFinPayBillItemOutsourceSettleList(FinPayBillItemOutsourceSettle finPayBillItemOutsourceSettle);

    /**
     * 添加多个
     * @param list List FinPayBillItemOutsourceSettle
     * @return int
     */
    int inserts(@Param("list") List<FinPayBillItemOutsourceSettle> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity FinPayBillItemOutsourceSettle
     * @return int
     */
    int updateAllById(FinPayBillItemOutsourceSettle entity);

    /**
     * 更新多个
     * @param list List FinPayBillItemOutsourceSettle
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPayBillItemOutsourceSettle> list);

}
