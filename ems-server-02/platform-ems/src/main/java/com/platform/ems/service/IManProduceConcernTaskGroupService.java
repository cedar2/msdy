package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManProduceConcernTaskGroup;
import com.platform.ems.domain.ManProduceConcernTaskGroupItem;
import com.platform.ems.domain.dto.request.ManProduceConcernTaskGroupRequest;
import com.platform.ems.domain.dto.response.ManProduceConcernTaskGroupResponse;

/**
 * 生产关注事项组Service接口
 * 
 * @author zhuangyz
 * @date 2022-08-02
 */
public interface IManProduceConcernTaskGroupService extends IService<ManProduceConcernTaskGroup>{
    /**
     * 查询生产关注事项组
     * 
     * @param concernTaskGroupSid 生产关注事项组ID
     * @return 生产关注事项组
     */
    public ManProduceConcernTaskGroup selectManProduceConcernTaskGroupById(Long concernTaskGroupSid);


    public List<ManProduceConcernTaskGroupItem>  monthConcernTaskGroupById(Long concernTaskGroupSid);
    /**
     * 查询生产关注事项组列表
     * 
     * @param manProduceConcernTaskGroup 生产关注事项组
     * @return 生产关注事项组集合
     */
    public List<ManProduceConcernTaskGroup> selectManProduceConcernTaskGroupList(ManProduceConcernTaskGroup manProduceConcernTaskGroup);


    List<ManProduceConcernTaskGroupResponse> getReport(ManProduceConcernTaskGroupRequest manProduceConcernTaskGroupRequest);
    /**
     * 新增生产关注事项组
     * 
     * @param manProduceConcernTaskGroup 生产关注事项组
     * @return 结果
     */
    public int insertManProduceConcernTaskGroup(ManProduceConcernTaskGroup manProduceConcernTaskGroup);

    /**
     * 修改生产关注事项组
     * 
     * @param manProduceConcernTaskGroup 生产关注事项组
     * @return 结果
     */
    public int updateManProduceConcernTaskGroup(ManProduceConcernTaskGroup manProduceConcernTaskGroup);

    /**
     * 变更生产关注事项组
     *
     * @param manProduceConcernTaskGroup 生产关注事项组
     * @return 结果
     */
    public int changeManProduceConcernTaskGroup(ManProduceConcernTaskGroup manProduceConcernTaskGroup);

    /**
     * 批量删除生产关注事项组
     * 
     * @param concernTaskGroupSids 需要删除的生产关注事项组ID
     * @return 结果
     */
    public int deleteManProduceConcernTaskGroupByIds(List<Long> concernTaskGroupSids);

    /**
    * 启用/停用
    * @param manProduceConcernTaskGroup
    * @return
    */
    int changeStatus(ManProduceConcernTaskGroup manProduceConcernTaskGroup);

    /**
     * 更改确认状态
     * @param manProduceConcernTaskGroup
     * @return
     */
    int check(ManProduceConcernTaskGroup manProduceConcernTaskGroup);

}
