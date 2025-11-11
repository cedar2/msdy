package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.ManWorkCenterProcess;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作中心-工序Mapper接口
 *
 * @author linhongwei
 * @date 2021-03-26
 */
public interface ManWorkCenterProcessMapper extends BaseMapper<ManWorkCenterProcess> {


    List<ManWorkCenterProcess> selectManWorkCenterProcessById(Long workCenterProcessSid);

    List<ManWorkCenterProcess> selectManWorkCenterProcessList(ManWorkCenterProcess manWorkCenterProcess);

    /**
     * 添加多个
     *
     * @param list List ManWorkCenterProcess
     * @return int
     */
    int inserts(@Param("list") List<ManWorkCenterProcess> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManWorkCenterProcess
     * @return int
     */
    int updateAllById(ManWorkCenterProcess entity);

    /**
     * 更新多个
     *
     * @param list List ManWorkCenterProcess
     * @return int
     */
    int updatesAllById(@Param("list") List<ManWorkCenterProcess> list);


}
