package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDataobjectHandleStatus;

/**
 * 数据对象类别与处理状态Mapper接口
 *
 * @author chenkw
 * @date 2022-06-23
 */
public interface ConDataobjectHandleStatusMapper extends BaseMapper<ConDataobjectHandleStatus> {

    ConDataobjectHandleStatus selectConDataobjectHandleStatusById(Long sid);

    List<ConDataobjectHandleStatus> selectConDataobjectHandleStatusList(ConDataobjectHandleStatus conDataobjectHandleStatus);

    /**
     * 添加多个
     *
     * @param list List ConDataobjectHandleStatus
     * @return int
     */
    int inserts(@Param("list") List<ConDataobjectHandleStatus> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConDataobjectHandleStatus
     * @return int
     */
    int updateAllById(ConDataobjectHandleStatus entity);

    /**
     * 更新多个
     *
     * @param list List ConDataobjectHandleStatus
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDataobjectHandleStatus> list);

    /**
     * 按数据对象类别分组
     *
     * @param conDataobjectHandleStatus ConDataobjectHandleStatus
     * @return int
     */
    List<ConDataobjectHandleStatus> selectConDataobjectHandleStatusGroup(ConDataobjectHandleStatus conDataobjectHandleStatus);

}
