package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FrmTrialsaleResult;
import com.platform.ems.domain.FrmTrialsaleResultPlanItem;

/**
 * 试销结果单-计划项Service接口
 *
 * @author chenkw
 * @date 2022-12-19
 */
public interface IFrmTrialsaleResultPlanItemService extends IService<FrmTrialsaleResultPlanItem> {
    /**
     * 查询试销结果单-计划项
     *
     * @param trialsaleResultPlanItemSid 试销结果单-计划项ID
     * @return 试销结果单-计划项
     */
    public FrmTrialsaleResultPlanItem selectFrmTrialsaleResultPlanItemById(Long trialsaleResultPlanItemSid);

    /**
     * 查询试销结果单-计划项列表
     *
     * @param frmTrialsaleResultPlanItem 试销结果单-计划项
     * @return 试销结果单-计划项集合
     */
    public List<FrmTrialsaleResultPlanItem> selectFrmTrialsaleResultPlanItemList(FrmTrialsaleResultPlanItem frmTrialsaleResultPlanItem);

    /**
     * 新增试销结果单-计划项
     *
     * @param frmTrialsaleResultPlanItem 试销结果单-计划项
     * @return 结果
     */
    public int insertFrmTrialsaleResultPlanItem(FrmTrialsaleResultPlanItem frmTrialsaleResultPlanItem);

    /**
     * 修改试销结果单-计划项
     *
     * @param frmTrialsaleResultPlanItem 试销结果单-计划项
     * @return 结果
     */
    public int updateFrmTrialsaleResultPlanItem(FrmTrialsaleResultPlanItem frmTrialsaleResultPlanItem);

    /**
     * 变更试销结果单-计划项
     *
     * @param frmTrialsaleResultPlanItem 试销结果单-计划项
     * @return 结果
     */
    public int changeFrmTrialsaleResultPlanItem(FrmTrialsaleResultPlanItem frmTrialsaleResultPlanItem);

    /**
     * 批量删除试销结果单-计划项
     *
     * @param trialsaleResultPlanItemSids 需要删除的试销结果单-计划项ID
     * @return 结果
     */
    public int deleteFrmTrialsaleResultPlanItemByIds(List<Long> trialsaleResultPlanItemSids);

    /**
     * 查询新品试销结果单-计划项
     *
     * @param trialsaleResultSid 新品试销结果单-主表ID
     * @return 新品试销结果单-计划项
     */
    public List<FrmTrialsaleResultPlanItem> selectFrmTrialsaleResultPlanItemListById(Long trialsaleResultSid);

    /**
     * 批量新增新品试销结果单-计划项
     *
     * @param result 新品试销结果单
     * @return 结果
     */
    public int insertFrmTrialsaleResultPlanItemList(FrmTrialsaleResult result);

    /**
     * 批量修改新品试销结果单-计划项
     *
     * @param result 新品试销结果单
     * @return 结果
     */
    public int updateFrmTrialsaleResultPlanItemList(FrmTrialsaleResult result);

    /**
     * 批量删除新品试销结果单-计划项
     *
     * @param planItemList 需要删除的项新品试销结果单-计划项
     * @return 结果
     */
    public int deleteFrmTrialsaleResultPlanItemByList(List<FrmTrialsaleResultPlanItem> planItemList);

    /**
     * 批量删除新品试销结果单-计划项 根据主表sids
     *
     * @param trialsaleResultSidList 需要删除的新品试销结果单sids
     * @return 结果
     */
    public int deleteFrmTrialsaleResultPlanItemByPlan(List<Long> trialsaleResultSidList);
}
