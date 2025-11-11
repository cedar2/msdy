package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManMonthManufacturePlan;
import com.platform.ems.domain.ManMonthManufacturePlanItem;
import com.platform.ems.domain.dto.request.ManMonthManufacturePlanRequest;

import java.util.List;

/**
 * 生产月计划Service接口
 *
 * @author linhongwei
 * @date 2021-07-16
 */
public interface IManMonthManufacturePlanService extends IService<ManMonthManufacturePlan> {
    /**
     * 查询生产月计划
     *
     * @param monthManufacturePlanSid 生产月计划ID
     * @return 生产月计划
     */
    public ManMonthManufacturePlan selectManMonthManufacturePlanById(Long monthManufacturePlanSid);

    /**
     * 查询生产月计划列表
     *
     * @param manMonthManufacturePlan 生产月计划
     * @return 生产月计划集合
     */
    public List<ManMonthManufacturePlan> selectManMonthManufacturePlanList(ManMonthManufacturePlan manMonthManufacturePlan);
    /**
     * 查询生产月计划添加明细-行转列
     *
     */
    public List<ManMonthManufacturePlanItem> addItem(ManMonthManufacturePlanRequest request);
    /**
     * 新增生产月计划
     *
     * @param manMonthManufacturePlan 生产月计划
     * @return 结果
     */
    public int insertManMonthManufacturePlan(ManMonthManufacturePlan manMonthManufacturePlan);

    /**
     * 修改生产月计划
     *
     * @param manMonthManufacturePlan 生产月计划
     * @return 结果
     */
    public int updateManMonthManufacturePlan(ManMonthManufacturePlan manMonthManufacturePlan);

    /**
     * 变更生产月计划
     *
     * @param manMonthManufacturePlan 生产月计划
     * @return 结果
     */
    public int changeManMonthManufacturePlan(ManMonthManufacturePlan manMonthManufacturePlan);

    /**
     * 批量删除生产月计划
     *
     * @param monthManufacturePlanSids 需要删除的生产月计划ID
     * @return 结果
     */
    public int deleteManMonthManufacturePlanByIds(List<Long> monthManufacturePlanSids);

    /**
     * 更改确认状态
     *
     * @param manMonthManufacturePlan
     * @return
     */
    int check(ManMonthManufacturePlan manMonthManufacturePlan);

    /**
     * 生产月计划明细报表
     */
    List<ManMonthManufacturePlanItem> getItemList(ManMonthManufacturePlanItem manMonthManufacturePlanItem);

    /**
     * 作废-生产月计划
     */
    int cancellationMonthManufacturePlanById(Long monthManufacturePlanSid);

    /**
     * 提交前校验-生产月计划
     */
    int verify(Long monthManufacturePlanSid, String handleStatus);
}
