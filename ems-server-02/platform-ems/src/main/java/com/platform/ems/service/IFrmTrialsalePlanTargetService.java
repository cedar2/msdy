package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FrmNewproductTrialsalePlan;
import com.platform.ems.domain.FrmTrialsalePlanTarget;

/**
 * 新品试销计划单-目标预定Service接口
 *
 * @author chenkw
 * @date 2022-12-16
 */
public interface IFrmTrialsalePlanTargetService extends IService<FrmTrialsalePlanTarget> {
    /**
     * 查询新品试销计划单-目标预定
     *
     * @param trialsalePlanTargetSid 新品试销计划单-目标预定ID
     * @return 新品试销计划单-目标预定
     */
    public FrmTrialsalePlanTarget selectFrmTrialsalePlanTargetById(Long trialsalePlanTargetSid);

    /**
     * 查询新品试销计划单-目标预定列表
     *
     * @param frmTrialsalePlanTarget 新品试销计划单-目标预定
     * @return 新品试销计划单-目标预定集合
     */
    public List<FrmTrialsalePlanTarget> selectFrmTrialsalePlanTargetList(FrmTrialsalePlanTarget frmTrialsalePlanTarget);

    /**
     * 新增新品试销计划单-目标预定
     *
     * @param frmTrialsalePlanTarget 新品试销计划单-目标预定
     * @return 结果
     */
    public int insertFrmTrialsalePlanTarget(FrmTrialsalePlanTarget frmTrialsalePlanTarget);

    /**
     * 修改新品试销计划单-目标预定
     *
     * @param frmTrialsalePlanTarget 新品试销计划单-目标预定
     * @return 结果
     */
    public int updateFrmTrialsalePlanTarget(FrmTrialsalePlanTarget frmTrialsalePlanTarget);

    /**
     * 变更新品试销计划单-目标预定
     *
     * @param frmTrialsalePlanTarget 新品试销计划单-目标预定
     * @return 结果
     */
    public int changeFrmTrialsalePlanTarget(FrmTrialsalePlanTarget frmTrialsalePlanTarget);

    /**
     * 批量删除新品试销计划单-目标预定
     *
     * @param trialsalePlanTargetSids 需要删除的新品试销计划单-目标预定ID
     * @return 结果
     */
    public int deleteFrmTrialsalePlanTargetByIds(List<Long> trialsalePlanTargetSids);

    /**
     * 查询新品试销计划单-目标预定
     *
     * @param newproductTrialsalePlanSid 新品试销计划单-主表ID
     * @return 新品试销计划单-目标预定
     */
    public List<FrmTrialsalePlanTarget> selectFrmTrialsalePlanTargetListById(Long newproductTrialsalePlanSid);

    /**
     * 批量新增新品试销计划单-目标预定
     *
     * @param plan 新品试销计划单
     * @return 结果
     */
    public int insertFrmTrialsalePlanTargetList(FrmNewproductTrialsalePlan plan);

    /**
     * 批量修改新品试销计划单-目标预定
     *
     * @param plan 新品试销计划单
     * @return 结果
     */
    public int updateFrmTrialsalePlanTargetList(FrmNewproductTrialsalePlan plan);

    /**
     * 批量删除新品试销计划单-目标预定
     *
     * @param targetList 需要删除的项新品试销计划单-目标预定
     * @return 结果
     */
    public int deleteFrmTrialsalePlanTargetByList(List<FrmTrialsalePlanTarget> targetList);

    /**
     * 批量删除新品试销计划单-目标预定 根据主表sids
     *
     * @param newproductTrialsalePlanSidList 需要删除的新品试销计划单sids
     * @return 结果
     */
    public int deleteFrmTrialsalePlanTargetByPlan(List<Long> newproductTrialsalePlanSidList);
}
