package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManManufactureOrderProcess;
import com.platform.ems.domain.dto.request.ManManufactureOrderProcessSetRequest;
import com.platform.ems.domain.dto.response.form.ManManuOrderProcessTracking;

/**
 * 生产订单-工序Service接口
 *
 * @author qhq
 * @date 2021-04-13
 */
public interface IManManufactureOrderProcessService extends IService<ManManufactureOrderProcess>{


    /**
     * 即将到期报表-按工序
     * @param manManufactureOrderProcess
     * @return
     */
    List<ManManufactureOrderProcess> selectExpiringProcessForm(ManManufactureOrderProcess manManufactureOrderProcess);


    /**
     * 已逾期生产报表-按工序
     * @param manManufactureOrderProcess
     * @return
     */
    List<ManManufactureOrderProcess> selectOverdueProcessForm(ManManufactureOrderProcess manManufactureOrderProcess);

    /**
     * 查询生产订单-工序
     *
     * @param manufactureOrderProcessSid 生产订单-工序ID
     * @return 生产订单-工序
     */
    ManManufactureOrderProcess selectManManufactureOrderProcessById(Long manufactureOrderProcessSid);

    /**
     * 查询生产订单-工序列表
     *
     * @param manManufactureOrderProcess 生产订单-工序
     * @return 生产订单-工序集合
     */
    List<ManManufactureOrderProcess> selectManManufactureOrderProcessList(ManManufactureOrderProcess manManufactureOrderProcess);

    /**
     * 新增生产订单-工序
     *
     * @param manManufactureOrderProcess 生产订单-工序
     * @return 结果
     */
    int insertManManufactureOrderProcess(ManManufactureOrderProcess manManufactureOrderProcess);

    /**
     * 修改生产订单-工序
     *
     * @param manManufactureOrderProcess 生产订单-工序
     * @return 结果
     */
    int updateManManufactureOrderProcess(ManManufactureOrderProcess manManufactureOrderProcess);

    /**
     * 批量删除生产订单-工序
     *
     * @param manufactureOrderProcessSids 需要删除的生产订单-工序ID
     * @return 结果
     */
    int deleteManManufactureOrderProcessByIds(List<String>  manufactureOrderProcessSids);

    /**
     * 生产订单工序明细报表
     */
    List<ManManufactureOrderProcess> getItemList(ManManufactureOrderProcess manManufactureOrderProcess);

    /**
     * 设置计划信息和进度信息
     */
    int concernSet(ManManufactureOrderProcessSetRequest request);

    /**
     * 设置完工量校验参考工序
     *
     * @param manManufactureOrderProcess 完工量校验参考工序 ; 参考工序所引用数量类型
     * @return 结果
     */
    int setReferProcess(ManManufactureOrderProcess  manManufactureOrderProcess);

    /*
     * 设置计划投产日期
     */
    int setPlanStart(ManManufactureOrderProcess manManufactureOrderProcess);

    /*
     * 设置计划完工日期
     */
    int setPlanEnd(ManManufactureOrderProcess manManufactureOrderProcess);

    /**
     * 设置即将到期提醒天数
     */
    int setToexpireDays(ManManufactureOrderProcess manManufactureOrderProcess);

    /**
     * 生产进度跟踪报表（工序）
     *
     * @param request
     * @return 生产进度跟踪报表（商品）集合
     */
    List<ManManuOrderProcessTracking> selectManufactureOrderProcessTrackingList(ManManuOrderProcessTracking request);
}
