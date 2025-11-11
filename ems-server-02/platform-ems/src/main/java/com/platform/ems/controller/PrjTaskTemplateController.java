package com.platform.ems.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.form.PrjTaskTemplateFormRequest;
import com.platform.ems.domain.dto.response.form.PrjTaskTemplateFormResponse;
import com.platform.ems.mapper.BasPositionMapper;
import com.platform.ems.service.IPrjTaskService;
import com.platform.ems.service.IPrjTaskTemplateItemService;
import com.platform.ems.service.IPrjTaskTemplateService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 项目任务模板Controller
 *
 * @author chenkw
 * @date 2022-12-07
 */
@RestController
@RequestMapping("/prj/task/template")
@Api(tags = "项目任务模板")
public class PrjTaskTemplateController extends BaseController {

    @Autowired
    private IPrjTaskTemplateService prjTaskTemplateService;
    @Autowired
    private IPrjTaskTemplateItemService prjTaskTemplateItemService;
    @Autowired
    private IPrjTaskService prjTaskService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private BasPositionMapper basPositionMapper;

    /**
     * 查询项目任务模板列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询项目任务模板列表",
                  notes = "查询项目任务模板列表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = PrjTaskTemplate.class))
    public TableDataInfo list(@RequestBody PrjTaskTemplate prjTaskTemplate) {
        startPage(prjTaskTemplate);
        List<PrjTaskTemplate> list = prjTaskTemplateService.selectPrjTaskTemplateList(prjTaskTemplate);
        return getDataTable(list);
    }

    /**
     * 导出项目任务模板列表
     */
    @Log(title = "项目任务模板",
         businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出项目任务模板列表",
                  notes = "导出项目任务模板列表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PrjTaskTemplate prjTaskTemplate) throws IOException {
        List<PrjTaskTemplate> list = prjTaskTemplateService.selectPrjTaskTemplateList(prjTaskTemplate);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PrjTaskTemplate> util = new ExcelUtil<>(PrjTaskTemplate.class, dataMap);
        util.exportExcel(response, list, "项目任务模板");
    }

    /**
     * 获取项目任务模板详细信息
     */
    @ApiOperation(value = "获取项目任务模板详细信息",
                  notes = "获取项目任务模板详细信息")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = PrjTaskTemplate.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long taskTemplateSid) {
        if (taskTemplateSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(prjTaskTemplateService.selectPrjTaskTemplateById(taskTemplateSid));
    }

    /**
     * 复制项目任务模板详细信息
     */
    @ApiOperation(value = "复制项目任务模板详细信息",
            notes = "复制项目任务模板详细信息")
    @ApiResponses(@ApiResponse(code = 200,
            message = "请求成功",
            response = PrjTaskTemplate.class))
    @PostMapping("/copy")
    public AjaxResult copy(Long taskTemplateSid) {
        if (taskTemplateSid == null) {
            throw new CheckedException("参数缺失");
        }
        PrjTaskTemplate template = prjTaskTemplateService.selectPrjTaskTemplateById(taskTemplateSid);
        PrjTaskTemplate response = new PrjTaskTemplate();
        response.setProjectType(template.getProjectType()).setTemplateTime(template.getTemplateTime())
                .setRemark(template.getRemark()).setCreateDate(new Date()).setStatus(ConstantsEms.ENABLE_STATUS)
                .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                .setCreatorAccountName(ApiThreadLocalUtil.get().getSysUser().getNickName());
        if (CollectionUtil.isNotEmpty(template.getTaskTemplateItemList())) {
            for (PrjTaskTemplateItem item : template.getTaskTemplateItemList()) {
                item.setTaskTemplateItemSid(null).setTaskTemplateSid(null).setTaskTemplateCode(null)
                        .setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                        .setCreatorAccountName(ApiThreadLocalUtil.get().getSysUser().getNickName())
                        .setUpdateDate(null).setUpdaterAccount(null).setUpdaterAccountName(null);
            }
            response.setTaskTemplateItemList(template.getTaskTemplateItemList());
        }
        return AjaxResult.success(response);
    }

    /**
     * 新增项目任务模板
     */
    @ApiOperation(value = "新增项目任务模板",
                  notes = "新增项目任务模板")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "项目任务模板",
         businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid PrjTaskTemplate prjTaskTemplate) {
        int row = prjTaskTemplateService.insertPrjTaskTemplate(prjTaskTemplate);
        if (row > 0) {
            return AjaxResult.success(prjTaskTemplateService.selectPrjTaskTemplateById(prjTaskTemplate.getTaskTemplateSid()));
        } else {
            return toAjax(row);
        }
    }

    /**
     * 编辑项目任务模板
     */
    @ApiOperation(value = "修改项目任务模板",
                  notes = "修改项目任务模板")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "项目任务模板",
         businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮",
                interval = 3000)
    public AjaxResult edit(@RequestBody @Valid PrjTaskTemplate prjTaskTemplate) {
        return toAjax(prjTaskTemplateService.updatePrjTaskTemplate(prjTaskTemplate));
    }

    /**
     * 变更项目任务模板
     */
    @ApiOperation(value = "变更项目任务模板",
                  notes = "变更项目任务模板")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "项目任务模板",
         businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid PrjTaskTemplate prjTaskTemplate) {
        return toAjax(prjTaskTemplateService.changePrjTaskTemplate(prjTaskTemplate));
    }

    /**
     * 删除项目任务模板
     */
    @ApiOperation(value = "删除项目任务模板",
                  notes = "删除项目任务模板")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "项目任务模板",
         businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> taskTemplateSids) {
        if (CollectionUtils.isEmpty(taskTemplateSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(prjTaskTemplateService.deletePrjTaskTemplateByIds(taskTemplateSids));
    }

    /**
     * 启用停用接口
     */
    @ApiOperation(value = "启用停用接口",
                  notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "项目任务模板",
         businessType = BusinessType.ENBLEORDISABLE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody PrjTaskTemplate prjTaskTemplate) {
        if (ArrayUtil.isEmpty(prjTaskTemplate.getTaskTemplateSidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(prjTaskTemplate.getStatus())) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(prjTaskTemplateService.changeStatus(prjTaskTemplate));
    }

    /**
     * 修改项目任务模板处理状态（确认）
     */
    @ApiOperation(value = "查询页面的确认",
                  notes = "查询页面的确认")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "项目任务模板",
         businessType = BusinessType.HANDLE)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody PrjTaskTemplate prjTaskTemplate) {
        if (ArrayUtil.isEmpty(prjTaskTemplate.getTaskTemplateSidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(prjTaskTemplate.getHandleStatus())) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(prjTaskTemplateService.check(prjTaskTemplate));
    }

    /**
     * 查询项目模版明细报表
     */
    @PostMapping("/task/form")
    @ApiOperation(value = "查询项目模版明细报表",
                  notes = "查询项目模版明细报表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = PrjTaskTemplateFormResponse.class))
    public TableDataInfo templateForm(@RequestBody PrjTaskTemplateFormRequest prjTaskTemplate) {
        startPage(prjTaskTemplate);
        List<PrjTaskTemplateFormResponse> list = prjTaskTemplateService.selectPrjTaskTemplateForm(prjTaskTemplate);
        TableDataInfo rsp = getDataTable(list);
        // 前置任务节点的处理
        getPreTaskName(list);
        return rsp;
    }

    private void setPositionName(PrjTaskTemplateFormResponse taskTemplate) {
        // 发起岗位
        if (StrUtil.isNotBlank(taskTemplate.getStartPositionCode())) {
            String[] starts = taskTemplate.getStartPositionCode().split(";");
            List<BasPosition> startList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                    .lambda().in(BasPosition::getPositionCode, starts));
            if (ArrayUtil.isNotEmpty(startList)) {
                String startName = "";
                for (int i = 0; i < startList.size(); i++) {
                    startName = startName + startList.get(i).getPositionName() + ";";
                }
                taskTemplate.setStartPositionName(startName);
            }
        }
        // 负责岗位
        if (StrUtil.isNotBlank(taskTemplate.getChargePositionCode())) {
            String[] charges = taskTemplate.getChargePositionCode().split(";");
            List<BasPosition> chargeList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                    .lambda().in(BasPosition::getPositionCode, charges));
            if (ArrayUtil.isNotEmpty(chargeList)) {
                String chargeName = "";
                for (int i = 0; i < chargeList.size(); i++) {
                    chargeName = chargeName + chargeList.get(i).getPositionName() + ";";
                }
                taskTemplate.setChargePositionName(chargeName);
            }
        }
        // 告知岗位
        if (StrUtil.isNotBlank(taskTemplate.getNoticePositionCode())) {
            String[] notices = taskTemplate.getNoticePositionCode().split(";");
            List<BasPosition> noticeList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                    .lambda().in(BasPosition::getPositionCode, notices));
            if (ArrayUtil.isNotEmpty(noticeList)) {
                String noticeName = "";
                for (int i = 0; i < noticeList.size(); i++) {
                    noticeName = noticeName + noticeList.get(i).getPositionName() + ";";
                }
                taskTemplate.setNoticePositionName(noticeName);
            }
        }
    }


    /**
     * 前置任务节点的处理
     */
    private void getPreTaskName(List<PrjTaskTemplateFormResponse> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item->{
                // 岗位
                setPositionName(item);
                // 前置任务节点
                String preTaskName = "";
                if (StrUtil.isNotEmpty(item.getPreTask())) {
                    String[] preTaskList = item.getPreTask().split(";");
                    if (ArrayUtil.isNotEmpty(preTaskList)) {
                        List<PrjTask> taskList= prjTaskService.selectPrjTaskList(new PrjTask().setTaskCodeList(preTaskList));
                        if (CollectionUtil.isNotEmpty(taskList)) {
                            for (int i = 0; i < taskList.size(); i++) {
                                preTaskName = preTaskName + taskList.get(i).getTaskName() + ";";
                            }
                        }
                    }
                }
                if (StrUtil.isNotBlank(preTaskName)) {
                    item.setPreTask(preTaskName);
                }
            });
        }
    }

    /**
     * 导出项目模版明细报表
     */
    @ApiOperation(value = "导出项目模版明细报表",
                  notes = "导出项目模版明细报表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = void.class))
    @PostMapping("/task/form/export")
    public void export(HttpServletResponse response, PrjTaskTemplateFormRequest prjTaskTemplate) throws IOException {
        List<PrjTaskTemplateFormResponse> list = prjTaskTemplateService.selectPrjTaskTemplateForm(prjTaskTemplate);
        // 前置任务节点的处理
        getPreTaskName(list);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PrjTaskTemplateFormResponse> util = new ExcelUtil<>(PrjTaskTemplateFormResponse.class, dataMap);
        util.exportExcel(response, list, "任务模版明细报表");
    }


    /**
     * 项目任务明细报表分配任务处理人
     */
    @ApiOperation(value = "项目任务明细报表分配任务处理人", notes = "项目任务明细报表分配任务处理人")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/task/setHandler")
    public AjaxResult setTaskHandler(@RequestBody PrjTaskTemplateItem prjTaskTemplateItem) {
        if (ArrayUtil.isEmpty(prjTaskTemplateItem.getTaskTemplateItemSidList())) {
            throw new BaseException("请选择行");
        }
        return AjaxResult.success(prjTaskTemplateItemService.setTaskHandler(prjTaskTemplateItem));
    }
}
