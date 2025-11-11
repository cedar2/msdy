package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasGoodsShelf;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 货架档案Mapper接口
 *
 * @author straw
 * @date 2023-02-02
 */
public interface BasGoodsShelfMapper extends BaseMapper<BasGoodsShelf> {


    BasGoodsShelf selectBasGoodsShelfById(Long goodsShelfSid);

    List<BasGoodsShelf> selectBasGoodsShelfList(BasGoodsShelf basGoodsShelf);

    /**
     * 添加多个
     *
     * @param list List BasGoodsShelf
     * @return int
     */
    int inserts(@Param("list") List<BasGoodsShelf> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasGoodsShelf
     * @return int
     */
    int updateAllById(BasGoodsShelf entity);

    /**
     * 更新多个
     *
     * @param list List BasGoodsShelf
     * @return int
     */
    int updatesAllById(@Param("list") List<BasGoodsShelf> list);


}
