package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.QuaRawmatCheckRecordAttach;

/**
 * 物料检测问题台账-附件Mapper接口
 *
 * @author admin
 * @date 2024-03-06
 */
public interface QuaRawmatCheckRecordAttachMapper extends BaseMapper<QuaRawmatCheckRecordAttach> {

    QuaRawmatCheckRecordAttach selectQuaRawmatCheckRecordAttachById(Long rawmatCheckRecordAttachSid);

    List<QuaRawmatCheckRecordAttach> selectQuaRawmatCheckRecordAttachList(QuaRawmatCheckRecordAttach quaRawmatCheckRecordAttach);

    /**
     * 添加多个
     */
    int inserts(@Param("list") List<QuaRawmatCheckRecordAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     */
    int updateAllById(QuaRawmatCheckRecordAttach entity);

    /**
     * 更新多个
     */
    int updatesAllById(@Param("list") List<QuaRawmatCheckRecordAttach> list);

}
