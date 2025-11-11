package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DevProductPlan;

/**
 * 品类规划信息Service接口
 * 
 * @author qhq
 * @date 2021-11-08
 */
public interface IDevProductPlanService extends IService<DevProductPlan>{
    /**
     * 查询品类规划信息
     * 
     * @param productPlanSid 品类规划信息ID
     * @return 品类规划信息
     */
    public DevProductPlan selectDevProductPlanById (Long productPlanSid);

    /**
     * 查询品类规划信息列表
     * 
     * @param devProductPlan 品类规划信息
     * @return 品类规划信息集合
     */
    public List<DevProductPlan> selectDevProductPlanList (DevProductPlan devProductPlan);

    /**
     * 新增品类规划信息
     * 
     * @param devProductPlan 品类规划信息
     * @return 结果
     */
    public int insertDevProductPlan (DevProductPlan devProductPlan);

    /**
     * 修改品类规划信息
     * 
     * @param devProductPlan 品类规划信息
     * @return 结果
     */
    public int updateDevProductPlan (DevProductPlan devProductPlan);

    /**
     * 变更品类规划信息
     *
     * @param devProductPlan 品类规划信息
     * @return 结果
     */
    public int changeDevProductPlan (DevProductPlan devProductPlan);

    /**
     * 批量删除品类规划信息
     * 
     * @param productPlanSids 需要删除的品类规划信息ID
     * @return 结果
     */
    public int deleteDevProductPlanByIds (List<Long> productPlanSids);

    /**
    * 启用/停用
    * @param devProductPlan
    * @return
    */
    int changeStatus (DevProductPlan devProductPlan);

    /**
     * 更改确认状态
     * @param devProductPlan
     * @return
     */
    int check (DevProductPlan devProductPlan);

}
