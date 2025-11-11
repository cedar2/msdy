package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConMainFabricComposition;

/**
 * 主面料成分Mapper接口
 *
 * @author chenkw
 * @date 2022-06-01
 */
public interface ConMainFabricCompositionMapper extends BaseMapper<ConMainFabricComposition> {


    ConMainFabricComposition selectConMainFabricCompositionById(Long sid);

    List<ConMainFabricComposition> selectConMainFabricCompositionList(ConMainFabricComposition conMainFabricComposition);

    /**
     * 添加多个
     *
     * @param list List ConMainFabricComposition
     * @return int
     */
    int inserts(@Param("list") List<ConMainFabricComposition> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConMainFabricComposition
     * @return int
     */
    int updateAllById(ConMainFabricComposition entity);

    /**
     * 更新多个
     *
     * @param list List ConMainFabricComposition
     * @return int
     */
    int updatesAllById(@Param("list") List<ConMainFabricComposition> list);


}
