package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepInventoryTopStock;

/**
 * T100库存Mapper接口
 *
 * @author linhongwei
 * @date 2022-02-25
 */
public interface RepInventoryTopStockMapper extends BaseMapper<RepInventoryTopStock> {


    RepInventoryTopStock selectRepInventoryTopStockById(Long dataRecordSid);

    List<RepInventoryTopStock> selectRepInventoryTopStockList(RepInventoryTopStock repInventoryTopStock);

    /**
     * 添加多个
     *
     * @param list List RepInventoryTopStock
     * @return int
     */
    int inserts(@Param("list") List<RepInventoryTopStock> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity RepInventoryTopStock
     * @return int
     */
    int updateAllById(RepInventoryTopStock entity);

    /**
     * 更新多个
     *
     * @param list List RepInventoryTopStock
     * @return int
     */
    int updatesAllById(@Param("list") List<RepInventoryTopStock> list);


}
