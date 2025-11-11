package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.QuaProductCheckItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 成衣检测单-检测项目Mapper接口
 *
 * @author linhongwei
 * @date 2022-04-13
 */
public interface QuaProductCheckItemMapper extends BaseMapper<QuaProductCheckItem> {


    QuaProductCheckItem selectQuaProductCheckItemById(Long productCheckItemSid);

    List<QuaProductCheckItem> selectQuaProductCheckItemList(QuaProductCheckItem quaProductCheckItem);

    /**
     * 添加多个
     *
     * @param list List QuaProductCheckItem
     * @return int
     */
    int inserts(@Param("list") List<QuaProductCheckItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity QuaProductCheckItem
     * @return int
     */
    int updateAllById(QuaProductCheckItem entity);

    /**
     * 更新多个
     *
     * @param list List QuaProductCheckItem
     * @return int
     */
    int updatesAllById(@Param("list") List<QuaProductCheckItem> list);


}
