package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.QuaShouhouRecordAttach;

/**
 * 售后质量问题台账-附件Mapper接口
 *
 * @author admin
 * @date 2024-03-06
 */
public interface QuaShouhouRecordAttachMapper extends BaseMapper<QuaShouhouRecordAttach> {

    QuaShouhouRecordAttach selectQuaShouhouRecordAttachById(Long shouhouRecordAttachSid);

    List<QuaShouhouRecordAttach> selectQuaShouhouRecordAttachList(QuaShouhouRecordAttach quaShouhouRecordAttach);

    /**
     * 添加多个
     */
    int inserts(@Param("list") List<QuaShouhouRecordAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     */
    int updateAllById(QuaShouhouRecordAttach entity);

    /**
     * 更新多个
     */
    int updatesAllById(@Param("list") List<QuaShouhouRecordAttach> list);

}
