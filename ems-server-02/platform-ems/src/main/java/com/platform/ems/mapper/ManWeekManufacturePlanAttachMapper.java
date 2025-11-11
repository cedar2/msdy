package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.ManWeekManufacturePlanAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 生产周计划-附件Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-10
 */
public interface ManWeekManufacturePlanAttachMapper extends BaseMapper<ManWeekManufacturePlanAttach> {


    ManWeekManufacturePlanAttach selectManWeekManufacturePlanAttachById(Long manufacturePlanAttachSid);

    List<ManWeekManufacturePlanAttach> selectManWeekManufacturePlanAttachList(ManWeekManufacturePlanAttach manWeekManufacturePlanAttach);

    /**
     * 添加多个
     *
     * @param list List ManWeekManufacturePlanAttach
     * @return int
     */
    int inserts(@Param("list") List<ManWeekManufacturePlanAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManWeekManufacturePlanAttach
     * @return int
     */
    int updateAllById(ManWeekManufacturePlanAttach entity);

    /**
     * 更新多个
     *
     * @param list List ManWeekManufacturePlanAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<ManWeekManufacturePlanAttach> list);


}
