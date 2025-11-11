package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmTrialsalePlanTarget;

/**
 * 新品试销计划单-目标预定Mapper接口
 *
 * @author chenkw
 * @date 2022-12-16
 */
public interface FrmTrialsalePlanTargetMapper extends BaseMapper<FrmTrialsalePlanTarget> {

    FrmTrialsalePlanTarget selectFrmTrialsalePlanTargetById(Long trialsalePlanTargetSid);

    List<FrmTrialsalePlanTarget> selectFrmTrialsalePlanTargetList(FrmTrialsalePlanTarget frmTrialsalePlanTarget);

    /**
     * 添加多个
     *
     * @param list List FrmTrialsalePlanTarget
     * @return int
     */
    int inserts(@Param("list") List<FrmTrialsalePlanTarget> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmTrialsalePlanTarget
     * @return int
     */
    int updateAllById(FrmTrialsalePlanTarget entity);

    /**
     * 更新多个
     *
     * @param list List FrmTrialsalePlanTarget
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmTrialsalePlanTarget> list);

}
