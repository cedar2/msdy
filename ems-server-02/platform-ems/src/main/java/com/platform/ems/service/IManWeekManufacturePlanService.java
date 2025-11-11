package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManManufactureOrderConcernTask;
import com.platform.ems.domain.ManWeekManufacturePlan;
import com.platform.ems.domain.ManWeekManufacturePlanItem;

/**
 * 生产周计划Service接口
 * 
 * @author hjj
 * @date 2021-07-16
 */
public interface IManWeekManufacturePlanService extends IService<ManWeekManufacturePlan>{
    /**
     * 查询生产周计划
     * 
     * @param weekManufacturePlanSid 生产周计划ID
     * @return 生产周计划
     */
    public ManWeekManufacturePlan selectManWeekManufacturePlanById(Long weekManufacturePlanSid);

    public List<ManManufactureOrderConcernTask> getConcernTask(List<ManWeekManufacturePlanItem> manWeekManufacturePlanItemList);
    /**
     * 查询生产周计划列表
     * 
     * @param manWeekManufacturePlan 生产周计划
     * @return 生产周计划集合
     */
    public List<ManWeekManufacturePlan> selectManWeekManufacturePlanList(ManWeekManufacturePlan manWeekManufacturePlan);
    /**
     * 获取分配量
     *
     */
    public List<ManWeekManufacturePlanItem> getQuantityFenpei(List<ManWeekManufacturePlanItem> items);
    /**
     * 新增生产周计划
     * 
     * @param manWeekManufacturePlan 生产周计划
     * @return 结果
     */
    public int insertManWeekManufacturePlan(ManWeekManufacturePlan manWeekManufacturePlan);

    /**
     * 修改生产周计划
     * 
     * @param manWeekManufacturePlan 生产周计划
     * @return 结果
     */
    public int updateManWeekManufacturePlan(ManWeekManufacturePlan manWeekManufacturePlan);

    /**
     * 变更生产周计划
     *
     * @param manWeekManufacturePlan 生产周计划
     * @return 结果
     */
    public int changeManWeekManufacturePlan(ManWeekManufacturePlan manWeekManufacturePlan);

    /**
     * 批量删除生产周计划
     * 
     * @param weekManufacturePlanSids 需要删除的生产周计划ID
     * @return 结果
     */
    public int deleteManWeekManufacturePlanByIds(List<Long> weekManufacturePlanSids);

    /**
     * 更改确认状态
     * @param manWeekManufacturePlan
     * @return
     */
    int check(ManWeekManufacturePlan manWeekManufacturePlan);

    /**
     * 作废-生产周计划
     */
    int cancellationWeekManufacturePlanById(Long weekManufacturePlanSid);

    /**
     * 提交前校验-生产周计划
     */
    int verify(Long weekManufacturePlanSid, String handleStatus);
}
