package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecRecordFengyangAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 封样记录-附件Mapper接口
 *
 * @author linhongwei
 * @date 2021-10-11
 */
public interface TecRecordFengyangAttachMapper extends BaseMapper<TecRecordFengyangAttach> {


    TecRecordFengyangAttach selectTecRecordFengyangAttachById(Long attachmentSid);

    List<TecRecordFengyangAttach> selectTecRecordFengyangAttachList(TecRecordFengyangAttach tecRecordFengyangAttach);

    /**
     * 添加多个
     *
     * @param list List TecRecordFengyangAttach
     * @return int
     */
    int inserts(@Param("list") List<TecRecordFengyangAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity TecRecordFengyangAttach
     * @return int
     */
    int updateAllById(TecRecordFengyangAttach entity);

    /**
     * 更新多个
     *
     * @param list List TecRecordFengyangAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<TecRecordFengyangAttach> list);


}
