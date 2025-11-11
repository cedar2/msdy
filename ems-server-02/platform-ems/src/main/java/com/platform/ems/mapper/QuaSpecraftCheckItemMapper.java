package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.QuaSpecraftCheckItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 特殊工艺检测单-检测项目Mapper接口
 *
 * @author linhongwei
 * @date 2022-04-12
 */
public interface QuaSpecraftCheckItemMapper extends BaseMapper<QuaSpecraftCheckItem> {


    QuaSpecraftCheckItem selectQuaSpecraftCheckItemById(Long specraftCheckItemSid);

    List<QuaSpecraftCheckItem> selectQuaSpecraftCheckItemList(QuaSpecraftCheckItem quaSpecraftCheckItem);

    /**
     * 添加多个
     *
     * @param list List QuaSpecraftCheckItem
     * @return int
     */
    int inserts(@Param("list") List<QuaSpecraftCheckItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity QuaSpecraftCheckItem
     * @return int
     */
    int updateAllById(QuaSpecraftCheckItem entity);

    /**
     * 更新多个
     *
     * @param list List QuaSpecraftCheckItem
     * @return int
     */
    int updatesAllById(@Param("list") List<QuaSpecraftCheckItem> list);


}
