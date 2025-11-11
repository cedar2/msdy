package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.ManMonthManufacturePlanAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 生产月计划-附件Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-10
 */
public interface ManMonthManufacturePlanAttachMapper extends BaseMapper<ManMonthManufacturePlanAttach> {


    ManMonthManufacturePlanAttach selectManMonthManufacturePlanAttachById(Long manufacturePlanAttachSid);

    List<ManMonthManufacturePlanAttach> selectManMonthManufacturePlanAttachList(ManMonthManufacturePlanAttach manMonthManufacturePlanAttach);

    /**
     * 添加多个
     *
     * @param list List ManMonthManufacturePlanAttach
     * @return int
     */
    int inserts(@Param("list") List<ManMonthManufacturePlanAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManMonthManufacturePlanAttach
     * @return int
     */
    int updateAllById(ManMonthManufacturePlanAttach entity);

    /**
     * 更新多个
     *
     * @param list List ManMonthManufacturePlanAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<ManMonthManufacturePlanAttach> list);


}
