package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecLinePositionGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 线部位组档案Mapper接口
 *
 * @author hjj
 * @date 2021-08-19
 */
public interface TecLinePositionGroupMapper extends BaseMapper<TecLinePositionGroup> {


    TecLinePositionGroup selectTecLinePositionGroupById(Long groupSid);

    List<TecLinePositionGroup> selectTecLinePositionGroupList(TecLinePositionGroup tecLinePositionGroup);

    /**
     * 添加多个
     *
     * @param list List TecLinePositionGroup
     * @return int
     */
    int inserts(@Param("list") List<TecLinePositionGroup> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity TecLinePositionGroup
     * @return int
     */
    int updateAllById(TecLinePositionGroup entity);

    /**
     * 更新多个
     *
     * @param list List TecLinePositionGroup
     * @return int
     */
    int updatesAllById(@Param("list") List<TecLinePositionGroup> list);


}
