package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FrmNewproductTrialsalePlan;
import com.platform.ems.domain.FrmTrialsalePlanProfitSimulate;

/**
 * 新品试销计划单-利润模拟Service接口
 *
 * @author chenkw
 * @date 2022-12-16
 */
public interface IFrmTrialsalePlanProfitSimulateService extends IService<FrmTrialsalePlanProfitSimulate> {
    /**
     * 查询新品试销计划单-利润模拟
     *
     * @param trialsalePlanProfitSimulationSid 新品试销计划单-利润模拟ID
     * @return 新品试销计划单-利润模拟
     */
    public FrmTrialsalePlanProfitSimulate selectFrmTrialsalePlanProfitSimulateById(Long trialsalePlanProfitSimulationSid);

    /**
     * 查询新品试销计划单-利润模拟列表
     *
     * @param frmTrialsalePlanProfitSimulate 新品试销计划单-利润模拟
     * @return 新品试销计划单-利润模拟集合
     */
    public List<FrmTrialsalePlanProfitSimulate> selectFrmTrialsalePlanProfitSimulateList(FrmTrialsalePlanProfitSimulate frmTrialsalePlanProfitSimulate);

    /**
     * 新增新品试销计划单-利润模拟
     *
     * @param frmTrialsalePlanProfitSimulate 新品试销计划单-利润模拟
     * @return 结果
     */
    public int insertFrmTrialsalePlanProfitSimulate(FrmTrialsalePlanProfitSimulate frmTrialsalePlanProfitSimulate);

    /**
     * 修改新品试销计划单-利润模拟
     *
     * @param frmTrialsalePlanProfitSimulate 新品试销计划单-利润模拟
     * @return 结果
     */
    public int updateFrmTrialsalePlanProfitSimulate(FrmTrialsalePlanProfitSimulate frmTrialsalePlanProfitSimulate);

    /**
     * 变更新品试销计划单-利润模拟
     *
     * @param frmTrialsalePlanProfitSimulate 新品试销计划单-利润模拟
     * @return 结果
     */
    public int changeFrmTrialsalePlanProfitSimulate(FrmTrialsalePlanProfitSimulate frmTrialsalePlanProfitSimulate);

    /**
     * 批量删除新品试销计划单-利润模拟
     *
     * @param trialsalePlanProfitSimulationSids 需要删除的新品试销计划单-利润模拟ID
     * @return 结果
     */
    public int deleteFrmTrialsalePlanProfitSimulateByIds(List<Long> trialsalePlanProfitSimulationSids);

    /**
     * 查询新品试销计划单-利润模拟
     *
     * @param newproductTrialsalePlanSid 新品试销计划单-主表ID
     * @return 新品试销计划单-利润模拟
     */
    public List<FrmTrialsalePlanProfitSimulate> selectFrmTrialsalePlanProfitSimulateListById(Long newproductTrialsalePlanSid);

    /**
     * 批量新增新品试销计划单-利润模拟
     *
     * @param plan 新品试销计划单
     * @return 结果
     */
    public int insertFrmTrialsalePlanProfitSimulateList(FrmNewproductTrialsalePlan plan);

    /**
     * 批量修改新品试销计划单-利润模拟
     *
     * @param plan 新品试销计划单
     * @return 结果
     */
    public int updateFrmTrialsalePlanProfitSimulateList(FrmNewproductTrialsalePlan plan);

    /**
     * 批量删除新品试销计划单-利润模拟
     *
     * @param profitSimulateList 需要删除的项新品试销计划单-利润模拟
     * @return 结果
     */
    public int deleteFrmTrialsalePlanProfitSimulateByList(List<FrmTrialsalePlanProfitSimulate> profitSimulateList);

    /**
     * 批量删除新品试销计划单-利润模拟 根据主表sids
     *
     * @param newproductTrialsalePlanSidList 需要删除的新品试销计划单sids
     * @return 结果
     */
    public int deleteFrmTrialsalePlanProfitSimulateByPlan(List<Long> newproductTrialsalePlanSidList);
}
