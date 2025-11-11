package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManProduceWeekProgressTotal;

/**
 * 生产周进度汇总Service接口
 *
 * @author chenkw
 * @date 2022-08-26
 */
public interface IManProduceWeekProgressTotalService extends IService<ManProduceWeekProgressTotal> {
    /**
     * 查询生产周进度汇总
     *
     * @param weekProgressTotalSid 生产周进度汇总ID
     * @return 生产周进度汇总
     */
    ManProduceWeekProgressTotal selectManProduceWeekProgressTotalById(Long weekProgressTotalSid);

    /**
     * 查询生产周进度汇总列表
     *
     * @param manProduceWeekProgressTotal 生产周进度汇总
     * @return 生产周进度汇总集合
     */
    List<ManProduceWeekProgressTotal> selectManProduceWeekProgressTotalList(ManProduceWeekProgressTotal manProduceWeekProgressTotal);

    /**
     * 新增生产周进度汇总
     *
     * @param manProduceWeekProgressTotal 生产周进度汇总
     * @return 结果
     */
    int insertManProduceWeekProgressTotal(ManProduceWeekProgressTotal manProduceWeekProgressTotal);

    /**
     * 修改生产周进度汇总
     *
     * @param manProduceWeekProgressTotal 生产周进度汇总
     * @return 结果
     */
    int updateManProduceWeekProgressTotal(ManProduceWeekProgressTotal manProduceWeekProgressTotal);

    /**
     * 变更生产周进度汇总
     *
     * @param manProduceWeekProgressTotal 生产周进度汇总
     * @return 结果
     */
    int changeManProduceWeekProgressTotal(ManProduceWeekProgressTotal manProduceWeekProgressTotal);

    /**
     * 批量删除生产周进度汇总
     *
     * @param weekProgressTotalSids 需要删除的生产周进度汇总ID
     * @return 结果
     */
    int deleteManProduceWeekProgressTotalByIds(List<Long> weekProgressTotalSids);

    /**
     * 更新汇总生产周进度汇总
     *
     * @param weekProgressTotalSids 需要删除的生产周进度汇总ID
     * @return 结果
     */
    int refreshManProduceWeekProgressTotalByIds(List<Long> weekProgressTotalSids);

}
