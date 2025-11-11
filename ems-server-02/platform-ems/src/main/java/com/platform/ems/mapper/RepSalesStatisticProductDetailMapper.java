package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepSalesStatisticProductDetail;

/**
 * 销售统计报-款明细Mapper接口
 *
 * @author linhongwei
 * @date 2022-02-25
 */
public interface RepSalesStatisticProductDetailMapper extends BaseMapper<RepSalesStatisticProductDetail> {


    RepSalesStatisticProductDetail selectRepSalesStatisticProductDetailById(Long dataRecordSid);

    List<RepSalesStatisticProductDetail> selectRepSalesStatisticProductDetailList(RepSalesStatisticProductDetail repSalesStatisticProductDetail);

    /**
     * 添加多个
     *
     * @param list List RepSalesStatisticProductDetail
     * @return int
     */
    int inserts(@Param("list") List<RepSalesStatisticProductDetail> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity RepSalesStatisticProductDetail
     * @return int
     */
    int updateAllById(RepSalesStatisticProductDetail entity);

    /**
     * 更新多个
     *
     * @param list List RepSalesStatisticProductDetail
     * @return int
     */
    int updatesAllById(@Param("list") List<RepSalesStatisticProductDetail> list);


}
