package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.QuaSpecraftCheckRecordAttach;

/**
 * 特殊工艺检测问题台账-附件Mapper接口
 *
 * @author admin
 * @date 2024-03-06
 */
public interface QuaSpecraftCheckRecordAttachMapper extends BaseMapper<QuaSpecraftCheckRecordAttach> {

    QuaSpecraftCheckRecordAttach selectQuaSpecraftCheckRecordAttachById(Long specraftCheckRecordAttachSid);

    List<QuaSpecraftCheckRecordAttach> selectQuaSpecraftCheckRecordAttachList(QuaSpecraftCheckRecordAttach quaSpecraftCheckRecordAttach);

    /**
     * 添加多个
     */
    int inserts(@Param("list") List<QuaSpecraftCheckRecordAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     */
    int updateAllById(QuaSpecraftCheckRecordAttach entity);

    /**
     * 更新多个
     */
    int updatesAllById(@Param("list") List<QuaSpecraftCheckRecordAttach> list);

}
