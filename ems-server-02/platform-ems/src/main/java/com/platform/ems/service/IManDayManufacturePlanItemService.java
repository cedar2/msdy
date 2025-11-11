package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManDayManufacturePlanItem;

/**
 * 生产日计划-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-06-22
 */
public interface IManDayManufacturePlanItemService extends IService<ManDayManufacturePlanItem>{
    /**
     * 查询生产日计划-明细

     * 
     * @param dayManufacturePlanItemSid 生产日计划-明细ID
     * @return 生产日计划-明细

     */
    public ManDayManufacturePlanItem selectManDayManufacturePlanItemById(Long dayManufacturePlanItemSid);

    /**
     * 查询生产日计划-明细列表
     * 
     * @param manDayManufacturePlanItem 生产日计划-明细

     * @return 生产日计划-明细集合
     */
    public List<ManDayManufacturePlanItem> selectManDayManufacturePlanItemList(ManDayManufacturePlanItem manDayManufacturePlanItem);

    /**
     * 新增生产日计划-明细

     * 
     * @param manDayManufacturePlanItem 生产日计划-明细

     * @return 结果
     */
    public int insertManDayManufacturePlanItem(ManDayManufacturePlanItem manDayManufacturePlanItem);

    /**
     * 修改生产日计划-明细

     * 
     * @param manDayManufacturePlanItem 生产日计划-明细

     * @return 结果
     */
    public int updateManDayManufacturePlanItem(ManDayManufacturePlanItem manDayManufacturePlanItem);

    /**
     * 变更生产日计划-明细

     *
     * @param manDayManufacturePlanItem 生产日计划-明细

     * @return 结果
     */
    public int changeManDayManufacturePlanItem(ManDayManufacturePlanItem manDayManufacturePlanItem);

    /**
     * 批量删除生产日计划-明细

     * 
     * @param dayManufacturePlanItemSids 需要删除的生产日计划-明细ID
     * @return 结果
     */
    public int deleteManDayManufacturePlanItemByIds(List<Long> dayManufacturePlanItemSids);

    /**
     * 更改确认状态
     * @param manDayManufacturePlanItem
     * @return
     */
    int check(ManDayManufacturePlanItem manDayManufacturePlanItem);

}
