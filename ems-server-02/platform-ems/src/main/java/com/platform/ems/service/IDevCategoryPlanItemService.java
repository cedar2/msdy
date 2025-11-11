package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DevCategoryPlan;
import com.platform.ems.domain.DevCategoryPlanItem;
import com.platform.ems.domain.dto.request.form.DevCategoryPlanItemFormRequest;
import com.platform.ems.domain.dto.response.form.DevCategoryPlanItemFormResponse;

/**
 * 品类规划-明细Service接口
 *
 * @author chenkw
 * @date 2022-12-09
 */
public interface IDevCategoryPlanItemService extends IService<DevCategoryPlanItem> {
    /**
     * 查询品类规划-明细
     *
     * @param categoryPlanItemSid 品类规划-明细ID
     * @return 品类规划-明细
     */
    public DevCategoryPlanItem selectDevCategoryPlanItemById(Long categoryPlanItemSid);

    /**
     * 查询品类规划-明细列表
     *
     * @param devCategoryPlanItem 品类规划-明细
     * @return 品类规划-明细集合
     */
    public List<DevCategoryPlanItem> selectDevCategoryPlanItemList(DevCategoryPlanItem devCategoryPlanItem);

    /**
     * 新增品类规划-明细
     *
     * @param devCategoryPlanItem 品类规划-明细
     * @return 结果
     */
    public int insertDevCategoryPlanItem(DevCategoryPlanItem devCategoryPlanItem);

    /**
     * 修改品类规划-明细
     *
     * @param devCategoryPlanItem 品类规划-明细
     * @return 结果
     */
    public int updateDevCategoryPlanItem(DevCategoryPlanItem devCategoryPlanItem);

    /**
     * 变更品类规划-明细
     *
     * @param devCategoryPlanItem 品类规划-明细
     * @return 结果
     */
    public int changeDevCategoryPlanItem(DevCategoryPlanItem devCategoryPlanItem);

    /**
     * 批量删除品类规划-明细
     *
     * @param categoryPlanItemSids 需要删除的品类规划-明细ID
     * @return 结果
     */
    public int deleteDevCategoryPlanItemByIds(List<Long> categoryPlanItemSids);

    /**
     * 查询品类规划-明细
     *
     * @param categoryPlanSid 品类规划-主表ID
     * @return 品类规划-明细
     */
    public List<DevCategoryPlanItem> selectDevCategoryPlanItemListById(Long categoryPlanSid);

    /**
     * 批量新增品类规划-明细
     *
     * @param plan 品类规划
     * @return 结果
     */
    public int insertDevCategoryPlanItemList(DevCategoryPlan plan);

    /**
     * 批量修改品类规划-明细
     *
     * @param plan 品类规划
     * @return 结果
     */
    public int updateDevCategoryPlanItemList(DevCategoryPlan plan);

    /**
     * 批量删除品类规划-明细
     *
     * @param itemList 需要删除的品类规划-明细列表
     * @return 结果
     */
    public int deleteDevCategoryPlanItemByList(List<DevCategoryPlanItem> itemList);

    /**
     * 批量删除品类规划-明细 根据主表sids
     *
     * @param devCategoryPlanSidList 需要删除的品类规划sids
     * @return 结果
     */
    public int deleteDevCategoryPlanItemByPlan(List<Long> devCategoryPlanSidList);

    /**
     * 查询品类规划-明细报表
     *
     * @param request 品类规划-明细报表请求体
     * @return 品类规划-明细集合
     */
    public List<DevCategoryPlanItemFormResponse> selectDevCategoryPlanItemForm(DevCategoryPlanItemFormRequest request);
}
