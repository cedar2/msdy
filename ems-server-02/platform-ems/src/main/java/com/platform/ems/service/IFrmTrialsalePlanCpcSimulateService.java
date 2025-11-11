package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FrmNewproductTrialsalePlan;
import com.platform.ems.domain.FrmTrialsalePlanCpcSimulate;

/**
 * 新品试销计划单-CPC模拟数据Service接口
 *
 * @author chenkw
 * @date 2022-12-16
 */
public interface IFrmTrialsalePlanCpcSimulateService extends IService<FrmTrialsalePlanCpcSimulate> {
    /**
     * 查询新品试销计划单-CPC模拟数据
     *
     * @param trialsalePlanCpcSimulationSid 新品试销计划单-CPC模拟数据ID
     * @return 新品试销计划单-CPC模拟数据
     */
    public FrmTrialsalePlanCpcSimulate selectFrmTrialsalePlanCpcSimulateById(Long trialsalePlanCpcSimulationSid);

    /**
     * 查询新品试销计划单-CPC模拟数据列表
     *
     * @param frmTrialsalePlanCpcSimulate 新品试销计划单-CPC模拟数据
     * @return 新品试销计划单-CPC模拟数据集合
     */
    public List<FrmTrialsalePlanCpcSimulate> selectFrmTrialsalePlanCpcSimulateList(FrmTrialsalePlanCpcSimulate frmTrialsalePlanCpcSimulate);

    /**
     * 新增新品试销计划单-CPC模拟数据
     *
     * @param frmTrialsalePlanCpcSimulate 新品试销计划单-CPC模拟数据
     * @return 结果
     */
    public int insertFrmTrialsalePlanCpcSimulate(FrmTrialsalePlanCpcSimulate frmTrialsalePlanCpcSimulate);

    /**
     * 修改新品试销计划单-CPC模拟数据
     *
     * @param frmTrialsalePlanCpcSimulate 新品试销计划单-CPC模拟数据
     * @return 结果
     */
    public int updateFrmTrialsalePlanCpcSimulate(FrmTrialsalePlanCpcSimulate frmTrialsalePlanCpcSimulate);

    /**
     * 变更新品试销计划单-CPC模拟数据
     *
     * @param frmTrialsalePlanCpcSimulate 新品试销计划单-CPC模拟数据
     * @return 结果
     */
    public int changeFrmTrialsalePlanCpcSimulate(FrmTrialsalePlanCpcSimulate frmTrialsalePlanCpcSimulate);

    /**
     * 批量删除新品试销计划单-CPC模拟数据
     *
     * @param trialsalePlanCpcSimulationSids 需要删除的新品试销计划单-CPC模拟数据ID
     * @return 结果
     */
    public int deleteFrmTrialsalePlanCpcSimulateByIds(List<Long> trialsalePlanCpcSimulationSids);

    /**
     * 查询新品试销计划单-CPC模拟数据
     *
     * @param newproductTrialsalePlanSid 新品试销计划单-主表ID
     * @return 新品试销计划单-CPC模拟数据
     */
    public List<FrmTrialsalePlanCpcSimulate> selectFrmTrialsalePlanCpcSimulateListById(Long newproductTrialsalePlanSid);

    /**
     * 批量新增新品试销计划单-CPC模拟数据
     *
     * @param plan 新品试销计划单
     * @return 结果
     */
    public int insertFrmTrialsalePlanCpcSimulateList(FrmNewproductTrialsalePlan plan);

    /**
     * 批量修改新品试销计划单-CPC模拟数据
     *
     * @param plan 新品试销计划单
     * @return 结果
     */
    public int updateFrmTrialsalePlanCpcSimulateList(FrmNewproductTrialsalePlan plan);

    /**
     * 批量删除新品试销计划单-CPC模拟数据
     *
     * @param cpcSimulateList 需要删除的项新品试销计划单-CPC模拟数据
     * @return 结果
     */
    public int deleteFrmTrialsalePlanCpcSimulateByList(List<FrmTrialsalePlanCpcSimulate> cpcSimulateList);

    /**
     * 批量删除新品试销计划单-CPC模拟数据 根据主表sids
     *
     * @param newproductTrialsalePlanSidList 需要删除的新品试销计划单sids
     * @return 结果
     */
    public int deleteFrmTrialsalePlanCpcSimulateByPlan(List<Long> newproductTrialsalePlanSidList);
}
