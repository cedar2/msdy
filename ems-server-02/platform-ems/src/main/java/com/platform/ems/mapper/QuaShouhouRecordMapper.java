package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.QuaShouhouRecord;

/**
 * 售后质量问题台账Mapper接口
 *
 * @author admin
 * @date 2024-03-06
 */
public interface QuaShouhouRecordMapper extends BaseMapper<QuaShouhouRecord> {

    QuaShouhouRecord selectQuaShouhouRecordById(Long shouhouRecordSid);

    List<QuaShouhouRecord> selectQuaShouhouRecordList(QuaShouhouRecord quaShouhouRecord);

    /**
     * 添加多个
     */
    int inserts(@Param("list") List<QuaShouhouRecord> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     */
    int updateAllById(QuaShouhouRecord entity);

    /**
     * 更新多个
     */
    int updatesAllById(@Param("list") List<QuaShouhouRecord> list);

}
