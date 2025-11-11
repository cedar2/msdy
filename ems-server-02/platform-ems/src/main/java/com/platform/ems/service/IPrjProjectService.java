package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.document.UserOperLog;
import com.platform.ems.domain.PrjProject;
import com.platform.ems.domain.dto.response.form.PrjProjectExecuteCondition;

/**
 * 项目档案Service接口
 *
 * @author chenkw
 * @date 2022-12-08
 */
public interface IPrjProjectService extends IService<PrjProject> {
    /**
     * 查询项目档案
     *
     * @param projectSid 项目档案ID
     * @return 项目档案
     */
    public PrjProject selectPrjProjectById(Long projectSid);

    /**
     * 复制项目档案
     *
     * @param projectSid 项目档案ID
     * @return 项目档案
     */
    public PrjProject copyPrjProjectById(Long projectSid);

    /**
     * 查询项目档案列表
     *
     * @param prjProject 项目档案
     * @return 项目档案集合
     */
    public List<PrjProject> selectPrjProjectList(PrjProject prjProject);

    /**
     * 新增项目档案
     *
     * @param prjProject 项目档案
     * @return 结果
     */
    public int insertPrjProject(PrjProject prjProject);

    /**
     * 修改项目档案
     *
     * @param prjProject 项目档案
     * @return 结果
     */
    public int updatePrjProject(PrjProject prjProject);

    /**
     * 变更项目档案
     *
     * @param prjProject 项目档案
     * @return 结果
     */
    public int changePrjProject(PrjProject prjProject);

    /**
     * 批量删除项目档案
     *
     * @param projectSids 需要删除的项目档案ID
     * @return 结果
     */
    public int deletePrjProjectByIds(List<Long> projectSids);

    /**
     * 更改确认状态
     *
     * @param prjProject
     * @return
     */
    int check(PrjProject prjProject);

    /**
     * 设置项目状态
     * @param prjProject
     * @return
     */
    public int setProjectStatus(PrjProject prjProject);

    /**
     * 设置项目优先级
     * @param prjProject
     * @return
     */
    public int setPriority(PrjProject prjProject);

    /**
     * 设置即将到期提醒天数
     * @param prjProject
     * @return
     */
    int setToexpireDays(PrjProject prjProject);

    /**
     * 设置开发计划
     * @param prjProject
     * @return
     */
    int setDevelopPlan(PrjProject prjProject);

    /**
     * 设置商品款号/SPU号
     * @param prjProject
     * @return
     */
    int setProduct(PrjProject prjProject);

    /**
     * 设置商品SKU号
     * @param prjProject
     * @return
     */
    int setMaterialBarcode(PrjProject prjProject);

    /**
     * 查询页面开始执行的按钮
     * @param prjProject
     * @return
     */
    int startTask(PrjProject prjProject);

    /**
     * 跳转其它单据
     * @param prjProject
     * @return
     */
    PrjProject jumpTo(PrjProject prjProject);

    /**
     * 查询项目进度列表
     *
     * @param projectSid 项目档案ID
     * @return 项目档案
     */
    public PrjProject getPrjProjectProcessById(Long projectSid);

    /**
     * 设置状态灯
     *
     * @param prjProject 项目档案
     * @return 项目档案
     */
    public void setLight(PrjProject prjProject);

    /**
     * 试销站点执行状况报表报表
     */
    List<PrjProjectExecuteCondition> selectPrjProjectExecuteCondition(PrjProjectExecuteCondition prjProject);

    /**
     * 按钮设置采购状态
     *
     * @param prjProject
     * @return 结果
     */
    int updatePurchaseFlag(PrjProject prjProject);

    /**
     * 设置计划日期
     * @param project
     * @return
     */
    int setPlanDate(PrjProject project);

    /**
     * 设置实际完成日期
     * @param project
     * @return
     */
    int setActualEndDate(PrjProject project);

    /**
     * 任务单据操作日志
     * @param projectTaskSidList
     * @return
     */
    public List<UserOperLog> getProjectTaskDocumentOperLogList(List<Long> projectTaskSidList);

    /**
     * 将项目设置为已完成新建事项清单的逻辑
     *
     * @param projectSid
     * @return
     */
    public int setProjectYwcInsertMatterById(Long projectSid);
}
