package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FrmTrialsalePlanCategoryAnalysis;

/**
 * 新品试销计划单-类目分析Mapper接口
 *
 * @author chenkw
 * @date 2022-12-16
 */
public interface FrmTrialsalePlanCategoryAnalysisMapper extends BaseMapper<FrmTrialsalePlanCategoryAnalysis> {

    FrmTrialsalePlanCategoryAnalysis selectFrmTrialsalePlanCategoryAnalysisById(Long trialsalePlanCategoryAnalysisSid);

    List<FrmTrialsalePlanCategoryAnalysis> selectFrmTrialsalePlanCategoryAnalysisList(FrmTrialsalePlanCategoryAnalysis frmTrialsalePlanCategoryAnalysis);

    /**
     * 添加多个
     *
     * @param list List FrmTrialsalePlanCategoryAnalysis
     * @return int
     */
    int inserts(@Param("list") List<FrmTrialsalePlanCategoryAnalysis> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FrmTrialsalePlanCategoryAnalysis
     * @return int
     */
    int updateAllById(FrmTrialsalePlanCategoryAnalysis entity);

    /**
     * 更新多个
     *
     * @param list List FrmTrialsalePlanCategoryAnalysis
     * @return int
     */
    int updatesAllById(@Param("list") List<FrmTrialsalePlanCategoryAnalysis> list);

}
