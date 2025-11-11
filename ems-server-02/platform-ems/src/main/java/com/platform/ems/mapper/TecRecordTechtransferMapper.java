package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecRecordTechtransfer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 技术转移记录Mapper接口
 *
 * @author linhongwei
 * @date 2021-10-11
 */
public interface TecRecordTechtransferMapper extends BaseMapper<TecRecordTechtransfer> {


    TecRecordTechtransfer selectTecRecordTechtransferById(Long recordTechtransferSid);

    List<TecRecordTechtransfer> selectTecRecordTechtransferList(TecRecordTechtransfer tecRecordTechtransfer);

    /**
     * 添加多个
     *
     * @param list List TecRecordTechtransfer
     * @return int
     */
    int inserts(@Param("list") List<TecRecordTechtransfer> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity TecRecordTechtransfer
     * @return int
     */
    int updateAllById(TecRecordTechtransfer entity);

    /**
     * 更新多个
     *
     * @param list List TecRecordTechtransfer
     * @return int
     */
    int updatesAllById(@Param("list") List<TecRecordTechtransfer> list);


}
