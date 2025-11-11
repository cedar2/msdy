package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManDayManufactureProgressDetail;

/**
 * 生产进度日报-完工明细Mapper接口
 *
 * @author chenkw
 * @date 2022-06-10
 */
public interface ManDayManufactureProgressDetailMapper extends BaseMapper<ManDayManufactureProgressDetail> {


    ManDayManufactureProgressDetail selectManDayManufactureProgressDetailById(Long dayManufactureProgressDetailSid);

    List<ManDayManufactureProgressDetail> selectManDayManufactureProgressDetailList(ManDayManufactureProgressDetail manDayManufactureProgressDetail);

    /**
     * 添加多个
     *
     * @param list List ManDayManufactureProgressDetail
     * @return int
     */
    int inserts(@Param("list") List<ManDayManufactureProgressDetail> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManDayManufactureProgressDetail
     * @return int
     */
    int updateAllById(ManDayManufactureProgressDetail entity);

    /**
     * 更新多个
     *
     * @param list List ManDayManufactureProgressDetail
     * @return int
     */
    int updatesAllById(@Param("list") List<ManDayManufactureProgressDetail> list);


}
