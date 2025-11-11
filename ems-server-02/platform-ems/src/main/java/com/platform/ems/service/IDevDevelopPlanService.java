package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DevDevelopPlan;
import com.platform.ems.domain.dto.response.form.DevDevelopPlanForm;

/**
 * 开发计划Service接口
 * 
 * @author chenkw
 * @date 2022-12-08
 */
public interface IDevDevelopPlanService extends IService<DevDevelopPlan>{
    /**
     * 查询开发计划
     * 
     * @param developPlanSid 开发计划ID
     * @return 开发计划
     */
    public DevDevelopPlan selectDevDevelopPlanById(Long developPlanSid);

    /**
     * 复制开发计划
     *
     * @param developPlanSid 开发计划ID
     * @return 开发计划
     */
    public DevDevelopPlan copyDevDevelopPlanById(Long developPlanSid);

    /**
     * 查询开发计划列表
     * 
     * @param devDevelopPlan 开发计划
     * @return 开发计划集合
     */
    public List<DevDevelopPlan> selectDevDevelopPlanList(DevDevelopPlan devDevelopPlan);

    /**
     * 新增开发计划
     * 
     * @param devDevelopPlan 开发计划
     * @return 结果
     */
    public int insertDevDevelopPlan(DevDevelopPlan devDevelopPlan);

    /**
     * 修改开发计划
     * 
     * @param devDevelopPlan 开发计划
     * @return 结果
     */
    public int updateDevDevelopPlan(DevDevelopPlan devDevelopPlan);

    /**
     * 变更开发计划
     *
     * @param devDevelopPlan 开发计划
     * @return 结果
     */
    public int changeDevDevelopPlan(DevDevelopPlan devDevelopPlan);

    /**
     * 批量删除开发计划
     * 
     * @param developPlanSids 需要删除的开发计划ID
     * @return 结果
     */
    public int deleteDevDevelopPlanByIds(List<Long>  developPlanSids);

    /**
     * 更改确认状态
     * @param devDevelopPlan
     * @return
     */
    int check(DevDevelopPlan devDevelopPlan);

    /**
     * 设置开发计划负责人
     * @param devDevelopPlan
     * @return
     */
    public int setLeader(DevDevelopPlan devDevelopPlan);

    /**
     * 查询开发计划报表
     *
     * @param devDevelopPlanForm 开发计划
     * @return 开发计划集合
     */
    public List<DevDevelopPlanForm> selectDevDevelopPlanForm(DevDevelopPlanForm devDevelopPlanForm);

    /**
     * 修改品类规划
     *
     * @param devDevelopPlan 开发计划
     * @return 结果
     */
    public int updateCategoryPlan(DevDevelopPlan devDevelopPlan);

}
