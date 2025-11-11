package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DevCategoryPlan;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 品类规划Service接口
 *
 * @author chenkw
 * @date 2022-12-09
 */
public interface IDevCategoryPlanService extends IService<DevCategoryPlan> {
    /**
     * 查询品类规划
     *
     * @param categoryPlanSid 品类规划ID
     * @return 品类规划
     */
    public DevCategoryPlan selectDevCategoryPlanById(Long categoryPlanSid);

    /**
     * 复制品类规划
     *
     * @param categoryPlanSid 品类规划ID
     * @return 品类规划
     */
    public DevCategoryPlan copyDevCategoryPlanById(Long categoryPlanSid);

    /**
     * 查询品类规划列表
     *
     * @param devCategoryPlan 品类规划
     * @return 品类规划集合
     */
    public List<DevCategoryPlan> selectDevCategoryPlanList(DevCategoryPlan devCategoryPlan);

    /**
     * 校验明细唯一性
     *
     * @param devCategoryPlan 品类规划
     * @return EmsResultEntity
     */
    public EmsResultEntity judgeItemUnique(DevCategoryPlan devCategoryPlan);

    /**
     * 新增品类规划
     *
     * @param devCategoryPlan 品类规划
     * @return 结果
     */
    public int insertDevCategoryPlan(DevCategoryPlan devCategoryPlan);

    /**
     * 修改品类规划
     *
     * @param devCategoryPlan 品类规划
     * @return 结果
     */
    public int updateDevCategoryPlan(DevCategoryPlan devCategoryPlan);

    /**
     * 变更品类规划
     *
     * @param devCategoryPlan 品类规划
     * @return 结果
     */
    public int changeDevCategoryPlan(DevCategoryPlan devCategoryPlan);

    /**
     * 批量删除品类规划
     *
     * @param categoryPlanSids 需要删除的品类规划ID
     * @return 结果
     */
    public int deleteDevCategoryPlanByIds(List<Long> categoryPlanSids);

    /**
     * 更改确认状态
     *
     * @param devCategoryPlan
     * @return
     */
    public int check(DevCategoryPlan devCategoryPlan);

    /**
     * 作废品类规划
     */
    public int cancellationDevCategoryPlanById(DevCategoryPlan devCategoryPlan);

    /**
     * 删除品类规划明细前的校验
     *
     * @param categoryPlanItemSids 需要删除的品类规划明细SID
     * @return 结果
     */
    void deleteDevCategoryPlanItemByIdsJudge(List<Long> categoryPlanItemSids);

    /**
     * 导入品类规划
     * @param file 文件
     * @return 返回
     */
    EmsResultEntity importCategory(MultipartFile file);
}
