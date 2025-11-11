package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecModelLinePos;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版型-线部位Mapper接口
 *
 * @author linhongwei
 * @date 2021-10-19
 */
public interface TecModelLinePosMapper extends BaseMapper<TecModelLinePos> {


    TecModelLinePos selectTecModelLinePosById(Long modelLinePosSid);

    List<TecModelLinePos> selectTecModelLinePosList(TecModelLinePos tecModelLinePos);

    /**
     * 添加多个
     *
     * @param list List TecModelLinePos
     * @return int
     */
    int inserts(@Param("list") List<TecModelLinePos> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity TecModelLinePos
     * @return int
     */
    int updateAllById(TecModelLinePos entity);

    /**
     * 更新多个
     *
     * @param list List TecModelLinePos
     * @return int
     */
    int updatesAllById(@Param("list") List<TecModelLinePos> list);


}
