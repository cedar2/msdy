package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FrmNewproductTrialsalePlan;
import com.platform.ems.domain.FrmTrialsalePlanCategoryAnalysis;

/**
 * 新品试销计划单-类目分析Service接口
 *
 * @author chenkw
 * @date 2022-12-16
 */
public interface IFrmTrialsalePlanCategoryAnalysisService extends IService<FrmTrialsalePlanCategoryAnalysis> {
    /**
     * 查询新品试销计划单-类目分析
     *
     * @param trialsalePlanCategoryAnalysisSid 新品试销计划单-类目分析ID
     * @return 新品试销计划单-类目分析
     */
    public FrmTrialsalePlanCategoryAnalysis selectFrmTrialsalePlanCategoryAnalysisById(Long trialsalePlanCategoryAnalysisSid);

    /**
     * 查询新品试销计划单-类目分析列表
     *
     * @param frmTrialsalePlanCategoryAnalysis 新品试销计划单-类目分析
     * @return 新品试销计划单-类目分析集合
     */
    public List<FrmTrialsalePlanCategoryAnalysis> selectFrmTrialsalePlanCategoryAnalysisList(FrmTrialsalePlanCategoryAnalysis frmTrialsalePlanCategoryAnalysis);

    /**
     * 新增新品试销计划单-类目分析
     *
     * @param frmTrialsalePlanCategoryAnalysis 新品试销计划单-类目分析
     * @return 结果
     */
    public int insertFrmTrialsalePlanCategoryAnalysis(FrmTrialsalePlanCategoryAnalysis frmTrialsalePlanCategoryAnalysis);

    /**
     * 修改新品试销计划单-类目分析
     *
     * @param frmTrialsalePlanCategoryAnalysis 新品试销计划单-类目分析
     * @return 结果
     */
    public int updateFrmTrialsalePlanCategoryAnalysis(FrmTrialsalePlanCategoryAnalysis frmTrialsalePlanCategoryAnalysis);

    /**
     * 变更新品试销计划单-类目分析
     *
     * @param frmTrialsalePlanCategoryAnalysis 新品试销计划单-类目分析
     * @return 结果
     */
    public int changeFrmTrialsalePlanCategoryAnalysis(FrmTrialsalePlanCategoryAnalysis frmTrialsalePlanCategoryAnalysis);

    /**
     * 批量删除新品试销计划单-类目分析
     *
     * @param trialsalePlanCategoryAnalysisSids 需要删除的新品试销计划单-类目分析ID
     * @return 结果
     */
    public int deleteFrmTrialsalePlanCategoryAnalysisByIds(List<Long> trialsalePlanCategoryAnalysisSids);

    /**
     * 查询新品试销计划单-类目分析
     *
     * @param newproductTrialsalePlanSid 新品试销计划单-主表ID
     * @return 新品试销计划单-类目分析
     */
    public List<FrmTrialsalePlanCategoryAnalysis> selectFrmTrialsalePlanCategoryAnalysisListById(Long newproductTrialsalePlanSid);

    /**
     * 批量新增新品试销计划单-类目分析
     *
     * @param plan 新品试销计划单
     * @return 结果
     */
    public int insertFrmTrialsalePlanCategoryAnalysisList(FrmNewproductTrialsalePlan plan);

    /**
     * 批量修改新品试销计划单-类目分析
     *
     * @param plan 新品试销计划单
     * @return 结果
     */
    public int updateFrmTrialsalePlanCategoryAnalysisList(FrmNewproductTrialsalePlan plan);

    /**
     * 批量删除新品试销计划单-类目分析
     *
     * @param analysisList 需要删除的项新品试销计划单-类目分析
     * @return 结果
     */
    public int deleteFrmTrialsalePlanCategoryAnalysisByList(List<FrmTrialsalePlanCategoryAnalysis> analysisList);

    /**
     * 批量删除新品试销计划单-类目分析 根据主表sids
     *
     * @param newproductTrialsalePlanSidList 需要删除的新品试销计划单sids
     * @return 结果
     */
    public int deleteFrmTrialsalePlanCategoryAnalysisByPlan(List<Long> newproductTrialsalePlanSidList);
}
