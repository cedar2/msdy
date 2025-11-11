package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManDayManufacturePlan;
import com.platform.ems.domain.ManDayManufacturePlanItem;
import com.platform.ems.domain.ManMonthManufacturePlanItem;

/**
 * 生产日计划Service接口
 * 
 * @author linhongwei
 * @date 2021-06-22
 */
public interface IManDayManufacturePlanService extends IService<ManDayManufacturePlan>{
    /**
     * 查询生产日计划
     * 
     * @param dayManufacturePlanSid 生产日计划ID
     * @return 生产日计划
     */
    public ManDayManufacturePlan selectManDayManufacturePlanById(Long dayManufacturePlanSid);

    /**
     * 查询生产日计划列表
     * 
     * @param manDayManufacturePlan 生产日计划
     * @return 生产日计划集合
     */
    public List<ManDayManufacturePlan> selectManDayManufacturePlanList(ManDayManufacturePlan manDayManufacturePlan);

    /**
     * 新增生产日计划
     * 
     * @param manDayManufacturePlan 生产日计划
     * @return 结果
     */
    public int insertManDayManufacturePlan(ManDayManufacturePlan manDayManufacturePlan);

    /**
     * 修改生产日计划
     * 
     * @param manDayManufacturePlan 生产日计划
     * @return 结果
     */
    public int updateManDayManufacturePlan(ManDayManufacturePlan manDayManufacturePlan);

    /**
     * 变更生产日计划
     *
     * @param manDayManufacturePlan 生产日计划
     * @return 结果
     */
    public int changeManDayManufacturePlan(ManDayManufacturePlan manDayManufacturePlan);

    /**
     * 批量删除生产日计划
     * 
     * @param dayManufacturePlanSids 需要删除的生产日计划ID
     * @return 结果
     */
    public int deleteManDayManufacturePlanByIds(List<Long> dayManufacturePlanSids);

    /**
     * 更改确认状态
     * @param manDayManufacturePlan
     * @return
     */
    int check(ManDayManufacturePlan manDayManufacturePlan);

    /**
     * 生产日计划明细报表
     */
    List<ManDayManufacturePlanItem> getItemList(ManDayManufacturePlanItem manDayManufacturePlanItem);

}
