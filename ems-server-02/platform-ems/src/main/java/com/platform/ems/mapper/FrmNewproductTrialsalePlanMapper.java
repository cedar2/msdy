package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmNewproductTrialsalePlan;

/**
 * 新品试销计划单Mapper接口
 *
 * @author chenkw
 * @date 2022-12-16
 */
public interface FrmNewproductTrialsalePlanMapper extends BaseMapper<FrmNewproductTrialsalePlan> {

    FrmNewproductTrialsalePlan selectFrmNewproductTrialsalePlanById(Long newproductTrialsalePlanSid);

    List<FrmNewproductTrialsalePlan> selectFrmNewproductTrialsalePlanList(FrmNewproductTrialsalePlan frmNewproductTrialsalePlan);

    /**
     * 添加多个
     *
     * @param list List FrmNewproductTrialsalePlan
     * @return int
     */
    int inserts(@Param("list") List<FrmNewproductTrialsalePlan> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmNewproductTrialsalePlan
     * @return int
     */
    int updateAllById(FrmNewproductTrialsalePlan entity);

    /**
     * 更新多个
     *
     * @param list List FrmNewproductTrialsalePlan
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmNewproductTrialsalePlan> list);

}
