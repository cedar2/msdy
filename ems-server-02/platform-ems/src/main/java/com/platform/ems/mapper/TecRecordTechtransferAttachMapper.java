package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecRecordTechtransferAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 技术转移记录-附件Mapper接口
 *
 * @author linhongwei
 * @date 2021-10-11
 */
public interface TecRecordTechtransferAttachMapper extends BaseMapper<TecRecordTechtransferAttach> {


    TecRecordTechtransferAttach selectTecRecordTechtransferAttachById(Long attachmentSid);

    List<TecRecordTechtransferAttach> selectTecRecordTechtransferAttachList(TecRecordTechtransferAttach tecRecordTechtransferAttach);

    /**
     * 添加多个
     *
     * @param list List TecRecordTechtransferAttach
     * @return int
     */
    int inserts(@Param("list") List<TecRecordTechtransferAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity TecRecordTechtransferAttach
     * @return int
     */
    int updateAllById(TecRecordTechtransferAttach entity);

    /**
     * 更新多个
     *
     * @param list List TecRecordTechtransferAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<TecRecordTechtransferAttach> list);


}
