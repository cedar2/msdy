package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasLaboratory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实验室档案Mapper接口
 *
 * @author c
 * @date 2022-03-31
 */
public interface BasLaboratoryMapper extends BaseMapper<BasLaboratory> {


    BasLaboratory selectBasLaboratoryById(Long laboratorySid);

    List<BasLaboratory> selectBasLaboratoryList(BasLaboratory basLaboratory);

    /**
     * 添加多个
     *
     * @param list List BasLaboratory
     * @return int
     */
    int inserts(@Param("list") List<BasLaboratory> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasLaboratory
     * @return int
     */
    int updateAllById(BasLaboratory entity);

    /**
     * 更新多个
     *
     * @param list List BasLaboratory
     * @return int
     */
    int updatesAllById(@Param("list") List<BasLaboratory> list);

    /**
     * 下拉框
     *
     * @param basLaboratory
     * @return
     */
    @SqlParser(filter=true)
    List<BasLaboratory> getList(BasLaboratory basLaboratory);
}
