package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PrjTaskTemplate;
import com.platform.ems.domain.dto.request.form.PrjTaskTemplateFormRequest;
import com.platform.ems.domain.dto.response.form.PrjTaskTemplateFormResponse;

import java.util.List;

/**
 * 项目任务模板Service接口
 *
 * @author chenkw
 * @date 2022-12-07
 */
public interface IPrjTaskTemplateService extends IService<PrjTaskTemplate> {
    /**
     * 查询项目任务模板
     *
     * @param taskTemplateSid 项目任务模板ID
     * @return 项目任务模板
     */
    public PrjTaskTemplate selectPrjTaskTemplateById(Long taskTemplateSid);

    /**
     * 查询项目任务模板列表
     *
     * @param prjTaskTemplate 项目任务模板
     * @return 项目任务模板集合
     */
    public List<PrjTaskTemplate> selectPrjTaskTemplateList(PrjTaskTemplate prjTaskTemplate);

    /**
     * 新增项目任务模板
     *
     * @param prjTaskTemplate 项目任务模板
     * @return 结果
     */
    public int insertPrjTaskTemplate(PrjTaskTemplate prjTaskTemplate);

    /**
     * 修改项目任务模板
     *
     * @param prjTaskTemplate 项目任务模板
     * @return 结果
     */
    public int updatePrjTaskTemplate(PrjTaskTemplate prjTaskTemplate);

    /**
     * 变更项目任务模板
     *
     * @param prjTaskTemplate 项目任务模板
     * @return 结果
     */
    public int changePrjTaskTemplate(PrjTaskTemplate prjTaskTemplate);

    /**
     * 批量删除项目任务模板
     *
     * @param taskTemplateSids 需要删除的项目任务模板ID
     * @return 结果
     */
    public int deletePrjTaskTemplateByIds(List<Long> taskTemplateSids);

    /**
     * 启用/停用
     *
     * @param prjTaskTemplate
     * @return
     */
    int changeStatus(PrjTaskTemplate prjTaskTemplate);

    /**
     * 更改确认状态
     *
     * @param prjTaskTemplate
     * @return
     */
    int check(PrjTaskTemplate prjTaskTemplate);

    List<PrjTaskTemplateFormResponse> selectPrjTaskTemplateForm(PrjTaskTemplateFormRequest prjTaskTemplate);

}
