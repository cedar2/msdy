package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManSchedulingInfo;

/**
 * 生产排程信息Mapper接口
 *
 * @author chenkw
 * @date 2023-05-24
 */
public interface ManSchedulingInfoMapper extends BaseMapper<ManSchedulingInfo> {

    ManSchedulingInfo selectManSchedulingInfoById(Long schedulingInfoSid);

    List<ManSchedulingInfo> selectManSchedulingInfoList(ManSchedulingInfo manSchedulingInfo);

    /**
     * 添加多个
     *
     * @param list List ManSchedulingInfo
     * @return int
     */
    int inserts(@Param("list") List<ManSchedulingInfo> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManSchedulingInfo
     * @return int
     */
    int updateAllById(ManSchedulingInfo entity);

    /**
     * 更新多个
     *
     * @param list List ManSchedulingInfo
     * @return int
     */
    int updatesAllById(@Param("list") List<ManSchedulingInfo> list);

}
