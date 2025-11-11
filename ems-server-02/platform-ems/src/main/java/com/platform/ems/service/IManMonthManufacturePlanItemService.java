package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManMonthManufacturePlanItem;

/**
 * 生产月计划-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-07-16
 */
public interface IManMonthManufacturePlanItemService extends IService<ManMonthManufacturePlanItem>{
    /**
     * 查询生产月计划-明细
     * 
     * @param monthManufacturePlanItemSid 生产月计划-明细ID
     * @return 生产月计划-明细
     */
    public ManMonthManufacturePlanItem selectManMonthManufacturePlanItemById(Long monthManufacturePlanItemSid);

    /**
     * 查询生产月计划-明细列表
     * 
     * @param manMonthManufacturePlanItem 生产月计划-明细
     * @return 生产月计划-明细集合
     */
    public List<ManMonthManufacturePlanItem> selectManMonthManufacturePlanItemList(ManMonthManufacturePlanItem manMonthManufacturePlanItem);

    /**
     * 新增生产月计划-明细
     * 
     * @param manMonthManufacturePlanItem 生产月计划-明细
     * @return 结果
     */
    public int insertManMonthManufacturePlanItem(ManMonthManufacturePlanItem manMonthManufacturePlanItem);

    /**
     * 修改生产月计划-明细
     * 
     * @param manMonthManufacturePlanItem 生产月计划-明细
     * @return 结果
     */
    public int updateManMonthManufacturePlanItem(ManMonthManufacturePlanItem manMonthManufacturePlanItem);

    /**
     * 变更生产月计划-明细
     *
     * @param manMonthManufacturePlanItem 生产月计划-明细
     * @return 结果
     */
    public int changeManMonthManufacturePlanItem(ManMonthManufacturePlanItem manMonthManufacturePlanItem);

    /**
     * 批量删除生产月计划-明细
     * 
     * @param monthManufacturePlanItemSids 需要删除的生产月计划-明细ID
     * @return 结果
     */
    public int deleteManMonthManufacturePlanItemByIds(List<Long> monthManufacturePlanItemSids);

}
