package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManDayManufactureKuanProgress;

/**
 * 生产进度日报-款生产进度Mapper接口
 *
 * @author chenkw
 * @date 2022-08-03
 */
public interface ManDayManufactureKuanProgressMapper extends BaseMapper<ManDayManufactureKuanProgress> {

    ManDayManufactureKuanProgress selectManDayManufactureKuanProgressById(Long dayManufactureKuanProgressSid);

    @SqlParser(filter=true)
    List<ManDayManufactureKuanProgress> selectManDayManufactureKuanProgressList(ManDayManufactureKuanProgress manDayManufactureKuanProgress);

    /**
     * 添加多个
     *
     * @param list List ManDayManufactureKuanProgress
     * @return int
     */
    int inserts(@Param("list") List<ManDayManufactureKuanProgress> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManDayManufactureKuanProgress
     * @return int
     */
    int updateAllById(ManDayManufactureKuanProgress entity);

    /**
     * 更新多个
     *
     * @param list List ManDayManufactureKuanProgress
     * @return int
     */
    int updatesAllById(@Param("list") List<ManDayManufactureKuanProgress> list);


}
