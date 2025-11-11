package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecModelLine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版型线Mapper接口
 *
 * @author linhongwei
 * @date 2021-10-19
 */
public interface TecModelLineMapper extends BaseMapper<TecModelLine> {


    TecModelLine selectTecModelLineById(Long modelLineSid);

    List<TecModelLine> selectTecModelLineList(TecModelLine tecModelLine);

    /**
     * 添加多个
     *
     * @param list List TecModelLine
     * @return int
     */
    int inserts(@Param("list") List<TecModelLine> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity TecModelLine
     * @return int
     */
    int updateAllById(TecModelLine entity);

    /**
     * 更新多个
     *
     * @param list List TecModelLine
     * @return int
     */
    int updatesAllById(@Param("list") List<TecModelLine> list);


}
