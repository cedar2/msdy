package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.ManDayManufactureProgress;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 生产进度日报Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-09
 */
public interface ManDayManufactureProgressMapper extends BaseMapper<ManDayManufactureProgress> {


    ManDayManufactureProgress selectManDayManufactureProgressById(Long dayManufactureProgressSid);

    @SqlParser(filter=true)
    List<ManDayManufactureProgress> selectManDayManufactureProgressList(ManDayManufactureProgress manDayManufactureProgress);

    int selectManDayManufactureProgressCount(ManDayManufactureProgress manDayManufactureProgress);

    /**
     * 添加多个
     *
     * @param list List ManDayManufactureProgress
     * @return int
     */
    int inserts(@Param("list") List<ManDayManufactureProgress> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManDayManufactureProgress
     * @return int
     */
    int updateAllById(ManDayManufactureProgress entity);

    /**
     * 更新多个
     *
     * @param list List ManDayManufactureProgress
     * @return int
     */
    int updatesAllById(@Param("list") List<ManDayManufactureProgress> list);

}
