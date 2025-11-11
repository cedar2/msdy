package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasPosition;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 岗位Mapper接口
 * 
 * @author qhq
 * @date 2021-03-18
 */
public interface BasPositionMapper  extends BaseMapper<BasPosition> {


    BasPosition selectBasPositionById(Long positionSid);

    List<BasPosition> selectBasPositionList(BasPosition basPosition);

    /**
     * 添加多个
     * @param list List BasPosition
     * @return int
     */
    int inserts(@Param("list") List<BasPosition> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasPosition
    * @return int
    */
    int updateAllById(BasPosition entity);

    /**
     * 更新多个
     * @param list List BasPosition
     * @return int
     */
    int updatesAllById(@Param("list") List<BasPosition> list);


}
