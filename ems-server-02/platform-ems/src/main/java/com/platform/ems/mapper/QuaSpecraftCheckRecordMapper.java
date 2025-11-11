package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.QuaSpecraftCheckRecord;

/**
 * 特殊工艺检测问题台账Mapper接口
 *
 * @author admin
 * @date 2024-03-06
 */
public interface QuaSpecraftCheckRecordMapper extends BaseMapper<QuaSpecraftCheckRecord> {

    QuaSpecraftCheckRecord selectQuaSpecraftCheckRecordById(Long specraftCheckRecordSid);

    List<QuaSpecraftCheckRecord> selectQuaSpecraftCheckRecordList(QuaSpecraftCheckRecord quaSpecraftCheckRecord);

    /**
     * 添加多个
     */
    int inserts(@Param("list") List<QuaSpecraftCheckRecord> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     */
    int updateAllById(QuaSpecraftCheckRecord entity);

    /**
     * 更新多个
     */
    int updatesAllById(@Param("list") List<QuaSpecraftCheckRecord> list);

}
