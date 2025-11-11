package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasShop;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 店铺档案Mapper接口
 *
 * @author c
 * @date 2022-03-31
 */
public interface BasShopMapper extends BaseMapper<BasShop> {


    BasShop selectBasShopById(Long shopSid);

    List<BasShop> selectBasShopList(BasShop basShop);

    /**
     * 添加多个
     *
     * @param list List BasShop
     * @return int
     */
    int inserts(@Param("list") List<BasShop> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasShop
     * @return int
     */
    int updateAllById(BasShop entity);

    /**
     * 更新多个
     *
     * @param list List BasShop
     * @return int
     */
    int updatesAllById(@Param("list") List<BasShop> list);


}
