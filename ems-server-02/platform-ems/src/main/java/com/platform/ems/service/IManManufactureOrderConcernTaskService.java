package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManManufactureOrderConcernTask;
import com.platform.ems.domain.dto.request.ManManufactureOrderConcernTaskSetRequest;
import com.platform.ems.domain.dto.response.form.ManManuOrderConcernTracking;

/**
 * 生产订单-关注事项Service接口
 *
 * @author chenkw
 * @date 2022-08-02
 */
public interface IManManufactureOrderConcernTaskService extends IService<ManManufactureOrderConcernTask> {

    /**
     * 即将到期报表-按事项
     * @param manManufactureOrderConcernTask
     * @return
     */
    List<ManManufactureOrderConcernTask> selectExpiringTaskForm(ManManufactureOrderConcernTask manManufactureOrderConcernTask);

    /**
     * 已逾期生产报表-按事项
     * @param manManufactureOrderConcernTask
     * @return
     */
    List<ManManufactureOrderConcernTask> selectOverdueTaskForm(ManManufactureOrderConcernTask manManufactureOrderConcernTask);

    /**
     * 查询生产订单-关注事项
     *
     * @param manufactureOrderConcernTaskSid 生产订单-关注事项ID
     * @return 生产订单-关注事项
     */
    ManManufactureOrderConcernTask selectManManufactureOrderConcernTaskById(Long manufactureOrderConcernTaskSid);

    /**
     * 查询生产订单-关注事项列表
     *
     * @param manManufactureOrderConcernTask 生产订单-关注事项
     * @return 生产订单-关注事项集合
     */
    List<ManManufactureOrderConcernTask> selectManManufactureOrderConcernTaskList(ManManufactureOrderConcernTask manManufactureOrderConcernTask);

    /**
     * 新增生产订单-关注事项
     *
     * @param manManufactureOrderConcernTask 生产订单-关注事项
     * @return 结果
     */
    int insertManManufactureOrderConcernTask(ManManufactureOrderConcernTask manManufactureOrderConcernTask);

    /**
     * 修改生产订单-关注事项
     *
     * @param manManufactureOrderConcernTask 生产订单-关注事项
     * @return 结果
     */
    int updateManManufactureOrderConcernTask(ManManufactureOrderConcernTask manManufactureOrderConcernTask);

    /**
     * 变更生产订单-关注事项
     *
     * @param manManufactureOrderConcernTask 生产订单-关注事项
     * @return 结果
     */
    int changeManManufactureOrderConcernTask(ManManufactureOrderConcernTask manManufactureOrderConcernTask);

    /**
     * 批量删除生产订单-关注事项
     *
     * @param manufactureOrderConcernTaskSids 需要删除的生产订单-关注事项ID
     * @return 结果
     */
    int deleteManManufactureOrderConcernTaskByIds(List<Long> manufactureOrderConcernTaskSids);

    /**
     * 查询生产订单-关注事项报表
     *
     * @param manManufactureOrderConcernTask 生产订单-关注事项
     * @return 生产订单-关注事项集合
     */
    List<ManManufactureOrderConcernTask> selectManManufactureOrderConcernTaskForm(ManManufactureOrderConcernTask manManufactureOrderConcernTask);

    /**
     * 设置计划信息和进度信息
     * @param request
     * @return
     */
    int concernSet(ManManufactureOrderConcernTaskSetRequest request);

    /**
     * 设置即将到期提醒天数
     * @param request
     * @return
     */
    int setToexpireDays(ManManufactureOrderConcernTask request);

    /**
     * 进度反馈按钮
     */
    int setProcessStatus(ManManufactureOrderConcernTask request);

    /**
     * 生产进度跟踪报表（事项）
     *
     * @param request
     * @return
     */
    List<ManManuOrderConcernTracking> selectManufactureOrderConcernTrackingList(ManManuOrderConcernTracking request);
}
