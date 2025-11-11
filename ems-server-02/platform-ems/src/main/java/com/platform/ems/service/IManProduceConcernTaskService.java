package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManProduceConcernTask;

/**
 * 生产关注事项Service接口
 * 
 * @author zhuangyz
 * @date 2022-08-01
 */
public interface IManProduceConcernTaskService extends IService<ManProduceConcernTask>{
    /**
     * 查询生产关注事项
     * 
     * @param concernTaskSid 生产关注事项ID
     * @return 生产关注事项
     */
    public ManProduceConcernTask selectManProduceConcernTaskById(Long concernTaskSid);

    /**
     * 查询生产关注事项列表
     * 
     * @param manProduceConcernTask 生产关注事项
     * @return 生产关注事项集合
     */
    public List<ManProduceConcernTask> selectManProduceConcernTaskList(ManProduceConcernTask manProduceConcernTask);

    /**
     * 新增生产关注事项
     * 
     * @param manProduceConcernTask 生产关注事项
     * @return 结果
     */
    public int insertManProduceConcernTask(ManProduceConcernTask manProduceConcernTask);

    /**
     * 修改生产关注事项
     * 
     * @param manProduceConcernTask 生产关注事项
     * @return 结果
     */
    public int updateManProduceConcernTask(ManProduceConcernTask manProduceConcernTask);

    /**
     * 变更生产关注事项
     *
     * @param manProduceConcernTask 生产关注事项
     * @return 结果
     */
    public int changeManProduceConcernTask(ManProduceConcernTask manProduceConcernTask);

    /**
     * 批量删除生产关注事项
     * 
     * @param concernTaskSids 需要删除的生产关注事项ID
     * @return 结果
     */
    public int deleteManProduceConcernTaskByIds(List<Long> concernTaskSids);

    /**
    * 启用/停用
    * @param manProduceConcernTask
    * @return
    */
    int changeStatus(ManProduceConcernTask manProduceConcernTask);

    /**
     * 更改确认状态
     * @param manProduceConcernTask
     * @return
     */
    int check(ManProduceConcernTask manProduceConcernTask);

}
