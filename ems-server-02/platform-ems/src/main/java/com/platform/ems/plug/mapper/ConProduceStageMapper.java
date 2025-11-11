package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConProduceStage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 所属生产阶段Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-26
 */
public interface ConProduceStageMapper extends BaseMapper<ConProduceStage> {


    ConProduceStage selectConProduceStageById(Long sid);

    List<ConProduceStage> selectConProduceStageList(ConProduceStage conProduceStage);

    /**
     * 添加多个
     *
     * @param list List ConProduceStage
     * @return int
     */
    int inserts(@Param("list") List<ConProduceStage> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConProduceStage
     * @return int
     */
    int updateAllById(ConProduceStage entity);

    /**
     * 更新多个
     *
     * @param list List ConProduceStage
     * @return int
     */
    int updatesAllById(@Param("list") List<ConProduceStage> list);

    /**
     * 所属生产阶段下拉框列表
     */
    List<ConProduceStage> getList(ConProduceStage conProduceStage);
}
