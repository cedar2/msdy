package com.platform.ems.device.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.device.log.domain.ManSchedulingInfoLog;

/**
 * 生产排程信息接口日志Mapper接口
 *
 * @author chenkw
 * @date 2023-05-26
 */
public interface ManSchedulingInfoLogMapper extends BaseMapper<ManSchedulingInfoLog> {

    ManSchedulingInfoLog selectManSchedulingInfoLogById(Long schedulingInfoLogSid);

    List<ManSchedulingInfoLog> selectManSchedulingInfoLogList(ManSchedulingInfoLog manSchedulingInfoLog);

    /**
     * 添加多个
     *
     * @param list List ManSchedulingInfoLog
     * @return int
     */
    int inserts(@Param("list") List<ManSchedulingInfoLog> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManSchedulingInfoLog
     * @return int
     */
    int updateAllById(ManSchedulingInfoLog entity);

    /**
     * 更新多个
     *
     * @param list List ManSchedulingInfoLog
     * @return int
     */
    int updatesAllById(@Param("list") List<ManSchedulingInfoLog> list);

}
