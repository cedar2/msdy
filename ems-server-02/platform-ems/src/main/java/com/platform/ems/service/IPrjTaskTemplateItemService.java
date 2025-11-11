package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PrjTaskTemplate;
import com.platform.ems.domain.PrjTaskTemplateItem;

/**
 * 项目任务模板-明细Service接口
 *
 * @author chenkw
 * @date 2022-12-07
 */
public interface IPrjTaskTemplateItemService extends IService<PrjTaskTemplateItem> {
    /**
     * 查询项目任务模板-明细
     *
     * @param taskTemplateItemSid 项目任务模板-明细ID
     * @return 项目任务模板-明细
     */
    public PrjTaskTemplateItem selectPrjTaskTemplateItemById(Long taskTemplateItemSid);

    /**
     * 查询项目任务模板-明细列表
     *
     * @param prjTaskTemplateItem 项目任务模板-明细
     * @return 项目任务模板-明细集合
     */
    public List<PrjTaskTemplateItem> selectPrjTaskTemplateItemList(PrjTaskTemplateItem prjTaskTemplateItem);

    /**
     * 新增项目任务模板-明细
     *
     * @param prjTaskTemplateItem 项目任务模板-明细
     * @return 结果
     */
    public int insertPrjTaskTemplateItem(PrjTaskTemplateItem prjTaskTemplateItem);

    /**
     * 修改项目任务模板-明细
     *
     * @param prjTaskTemplateItem 项目任务模板-明细
     * @return 结果
     */
    public int updatePrjTaskTemplateItem(PrjTaskTemplateItem prjTaskTemplateItem);

    /**
     * 变更项目任务模板-明细
     *
     * @param prjTaskTemplateItem 项目任务模板-明细
     * @return 结果
     */
    public int changePrjTaskTemplateItem(PrjTaskTemplateItem prjTaskTemplateItem);

    /**
     * 批量删除项目任务模板-明细
     *
     * @param taskTemplateItemSids 需要删除的项目任务模板-明细ID
     * @return 结果
     */
    public int deletePrjTaskTemplateItemByIds(List<Long> taskTemplateItemSids);

    /**
     * 查询项目任务模板-明细
     *
     * @param taskTemplateSid 项目任务模板-主表ID
     * @return 项目任务模板-明细
     */
    public List<PrjTaskTemplateItem> selectPrjTaskTemplateItemListById(Long taskTemplateSid);

    /**
     * 批量新增项目任务模板-明细
     *
     * @param template 项目任务模板
     * @return 结果
     */
    public int insertPrjTaskTemplateItemList(PrjTaskTemplate template);

    /**
     * 批量修改项目任务模板-明细
     *
     * @param template 项目任务模板
     * @return 结果
     */
    public int updatePrjTaskTemplateItemList(PrjTaskTemplate template);

    /**
     * 批量删除项目任务模板-明细
     *
     * @param itemList 需要删除的项目任务模板-明细列表
     * @return 结果
     */
    public int deletePrjTaskTemplateItemByList(List<PrjTaskTemplateItem> itemList);

    /**
     * 批量删除项目任务模板-明细 根据主表sids
     *
     * @param prjTaskTemplateSidList 需要删除的项目任务模板sids
     * @return 结果
     */
    public int deletePrjTaskTemplateItemByTemplete(List<Long> prjTaskTemplateSidList);

    /**
     * 任务模板明细报表分配任务处理人
     * @param taskTemplateItem 入参
     * @return 出参
     */
    int setTaskHandler(PrjTaskTemplateItem taskTemplateItem);
}
