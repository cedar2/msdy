package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasPlantAttach;

/**
 * 工厂档案-附件Mapper接口
 *
 * @author chenkw
 * @date 2021-09-15
 */
public interface BasPlantAttachMapper  extends BaseMapper<BasPlantAttach> {


    BasPlantAttach selectBasPlantAttachById(Long attachmentSid);

    List<BasPlantAttach> selectBasPlantAttachList(BasPlantAttach basPlantAttach);

    /**
     * 添加多个
     * @param list List BasPlantAttach
     * @return int
     */
    int inserts(@Param("list") List<BasPlantAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity BasPlantAttach
     * @return int
     */
    int updateAllById(BasPlantAttach entity);

    /**
     * 更新多个
     * @param list List BasPlantAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<BasPlantAttach> list);


}