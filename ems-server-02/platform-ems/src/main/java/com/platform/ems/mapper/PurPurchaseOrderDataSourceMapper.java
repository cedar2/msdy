package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurPurchaseOrderDataSource;

/**
 * 采购订单-数据来源明细Mapper接口
 *
 * @author chenkw
 * @date 2023-05-09
 */
public interface PurPurchaseOrderDataSourceMapper extends BaseMapper<PurPurchaseOrderDataSource> {

    PurPurchaseOrderDataSource selectPurPurchaseOrderDataSourceById(Long purchaseOrderDataSourceSid);

    List<PurPurchaseOrderDataSource> selectPurPurchaseOrderDataSourceList(PurPurchaseOrderDataSource purPurchaseOrderDataSource);

    /**
     * 添加多个
     *
     * @param list List PurPurchaseOrderDataSource
     * @return int
     */
    int inserts(@Param("list") List<PurPurchaseOrderDataSource> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PurPurchaseOrderDataSource
     * @return int
     */
    int updateAllById(PurPurchaseOrderDataSource entity);

    /**
     * 更新多个
     *
     * @param list List PurPurchaseOrderDataSource
     * @return int
     */
    int updatesAllById(@Param("list") List<PurPurchaseOrderDataSource> list);

}
