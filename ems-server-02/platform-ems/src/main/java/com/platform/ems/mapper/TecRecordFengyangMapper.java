package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecRecordFengyang;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 封样记录(标准封样、产前封样)Mapper接口
 *
 * @author linhongwei
 * @date 2021-10-11
 */
public interface TecRecordFengyangMapper extends BaseMapper<TecRecordFengyang> {


    TecRecordFengyang selectTecRecordFengyangById(Long recordFengyangSid);

    List<TecRecordFengyang> selectTecRecordFengyangList(TecRecordFengyang tecRecordFengyang);

    /**
     * 添加多个
     *
     * @param list List TecRecordFengyang
     * @return int
     */
    int inserts(@Param("list") List<TecRecordFengyang> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity TecRecordFengyang
     * @return int
     */
    int updateAllById(TecRecordFengyang entity);

    /**
     * 更新多个
     *
     * @param list List TecRecordFengyang
     * @return int
     */
    int updatesAllById(@Param("list") List<TecRecordFengyang> list);


}
