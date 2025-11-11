package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepSalesTopProduct;

/**
 * 销售TOP10款Mapper接口
 *
 * @author linhongwei
 * @date 2022-02-25
 */
public interface RepSalesTopProductMapper extends BaseMapper<RepSalesTopProduct> {


    RepSalesTopProduct selectRepSalesTopProductById(Long dataRecordSid);

    List<RepSalesTopProduct> selectRepSalesTopProductList(RepSalesTopProduct repSalesTopProduct);

    /**
     * 添加多个
     *
     * @param list List RepSalesTopProduct
     * @return int
     */
    int inserts(@Param("list") List<RepSalesTopProduct> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity RepSalesTopProduct
     * @return int
     */
    int updateAllById(RepSalesTopProduct entity);

    /**
     * 更新多个
     *
     * @param list List RepSalesTopProduct
     * @return int
     */
    int updatesAllById(@Param("list") List<RepSalesTopProduct> list);


}
