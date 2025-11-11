package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecLinePosition;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 线部位档案Mapper接口
 *
 * @author hjj
 * @date 2021-08-19
 */
public interface TecLinePositionMapper extends BaseMapper<TecLinePosition> {


    TecLinePosition selectTecLinePositionById(Long linePositionSid);

    List<TecLinePosition> selectTecLinePositionList(TecLinePosition tecLinePosition);

    /**
     * 添加多个
     *
     * @param list List TecLinePosition
     * @return int
     */
    int inserts(@Param("list") List<TecLinePosition> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity TecLinePosition
     * @return int
     */
    int updateAllById(TecLinePosition entity);

    /**
     * 更新多个
     *
     * @param list List TecLinePosition
     * @return int
     */
    int updatesAllById(@Param("list") List<TecLinePosition> list);

}
