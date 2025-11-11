package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.ems.annotation.CreatorScope;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.constant.ConstantsAuthorize;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.FrmNewproductTrialsalePlan;
import com.platform.ems.service.IFrmNewproductTrialsalePlanService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 新品试销计划单Controller
 *
 * @author chenkw
 * @date 2022-12-16
 */
@RestController
@RequestMapping("/frm/newproduct/trialsale/plan")
@Api(tags = "新品试销计划单")
public class FrmNewproductTrialsalePlanController extends BaseController {

    @Autowired
    private IFrmNewproductTrialsalePlanService frmNewproductTrialsalePlanService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询新品试销计划单列表
     */
    @PostMapping("/list")
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_NEWPRODUCT_TRIALSALE_ALL)
    @ApiOperation(value = "查询新品试销计划单列表", notes = "查询新品试销计划单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FrmNewproductTrialsalePlan.class))
    public TableDataInfo list(@RequestBody FrmNewproductTrialsalePlan frmNewproductTrialsalePlan) {
        startPage(frmNewproductTrialsalePlan);
        List<FrmNewproductTrialsalePlan> list = frmNewproductTrialsalePlanService.selectFrmNewproductTrialsalePlanList(frmNewproductTrialsalePlan);
        return getDataTable(list);
    }

    /**
     * 导出新品试销计划单列表
     */
    @Log(title = "新品试销计划单", businessType = BusinessType.EXPORT)
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_NEWPRODUCT_TRIALSALE_ALL, loc = 1)
    @ApiOperation(value = "导出新品试销计划单列表", notes = "导出新品试销计划单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FrmNewproductTrialsalePlan frmNewproductTrialsalePlan) throws IOException {
        List<FrmNewproductTrialsalePlan> list = frmNewproductTrialsalePlanService.selectFrmNewproductTrialsalePlanList(frmNewproductTrialsalePlan);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FrmNewproductTrialsalePlan> util = new ExcelUtil<>(FrmNewproductTrialsalePlan.class, dataMap);
        util.exportExcel(response, list, "新品试销计划单");
    }

    /**
     * 获取新品试销计划单详细信息
     */
    @ApiOperation(value = "获取新品试销计划单详细信息", notes = "获取新品试销计划单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FrmNewproductTrialsalePlan.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long newproductTrialsalePlanSid) {
        if (newproductTrialsalePlanSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(frmNewproductTrialsalePlanService.selectFrmNewproductTrialsalePlanById(newproductTrialsalePlanSid));
    }

    /**
     * 获取新品试销计划单详细信息  根据项目档案
     */
    @ApiOperation(value = "根据项目档案获取新品试销计划单详细信息", notes = "根据项目档案获取新品试销计划单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FrmNewproductTrialsalePlan.class))
    @PostMapping("/byProject")
    public AjaxResult getInfoByProject(@RequestBody FrmNewproductTrialsalePlan frmNewproductTrialsalePlan) {
        if (frmNewproductTrialsalePlan.getProjectSid() == null) {
            return AjaxResult.success();
        }
        List<FrmNewproductTrialsalePlan> list = frmNewproductTrialsalePlanService
                .selectFrmNewproductTrialsalePlanList(new FrmNewproductTrialsalePlan().setProjectSid(frmNewproductTrialsalePlan.getProjectSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            if (list.size() > 1) {
                throw new CheckedException("该项目档案存在多笔新品试销计划单！");
            }
            return AjaxResult.success(frmNewproductTrialsalePlanService
                    .selectFrmNewproductTrialsalePlanById(list.get(0).getNewproductTrialsalePlanSid()));
        }
        return AjaxResult.success();
    }

    /**
     * 新增新品试销计划单
     */
    @ApiOperation(value = "新增新品试销计划单", notes = "新增新品试销计划单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "新品试销计划单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid FrmNewproductTrialsalePlan frmNewproductTrialsalePlan) {
        int row = frmNewproductTrialsalePlanService.insertFrmNewproductTrialsalePlan(frmNewproductTrialsalePlan);
        if (row > 0) {
            return AjaxResult.success(frmNewproductTrialsalePlanService.selectFrmNewproductTrialsalePlanById(
                    frmNewproductTrialsalePlan.getNewproductTrialsalePlanSid()));
        } else {
            return toAjax(row);
        }
    }

    @ApiOperation(value = "修改新品试销计划单", notes = "修改新品试销计划单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "新品试销计划单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody @Valid FrmNewproductTrialsalePlan frmNewproductTrialsalePlan) {
        return toAjax(frmNewproductTrialsalePlanService.updateFrmNewproductTrialsalePlan(frmNewproductTrialsalePlan));
    }

    /**
     * 变更新品试销计划单
     */
    @ApiOperation(value = "变更新品试销计划单", notes = "变更新品试销计划单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "新品试销计划单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FrmNewproductTrialsalePlan frmNewproductTrialsalePlan) {
        return toAjax(frmNewproductTrialsalePlanService.changeFrmNewproductTrialsalePlan(frmNewproductTrialsalePlan));
    }

    /**
     * 删除新品试销计划单
     */
    @ApiOperation(value = "删除新品试销计划单", notes = "删除新品试销计划单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "新品试销计划单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> newproductTrialsalePlanSids) {
        if (CollectionUtils.isEmpty(newproductTrialsalePlanSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(frmNewproductTrialsalePlanService.deleteFrmNewproductTrialsalePlanByIds(newproductTrialsalePlanSids));
    }

    /**
     * 提交前的校验
     */
    @ApiOperation(value = "提交前的校验", notes = "提交前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit/verify")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submitVerify(@RequestBody FrmNewproductTrialsalePlan frmNewproductTrialsalePlan) {
        frmNewproductTrialsalePlan.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
        return AjaxResult.success(frmNewproductTrialsalePlanService.submitVerify(frmNewproductTrialsalePlan));
    }

    /**
     * 提交前的校验查询或者详情页面通过sid
     */
    @ApiOperation(value = "提交前的校验查询或者详情页面通过sid", notes = "提交前的校验查询或者详情页面通过sid")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit/verify/byid")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submitVerify(Long newproductTrialsalePlanSid) {
        FrmNewproductTrialsalePlan frmNewproductTrialsalePlan = frmNewproductTrialsalePlanService.selectFrmNewproductTrialsalePlanById(newproductTrialsalePlanSid);
        if (frmNewproductTrialsalePlan != null) {
            frmNewproductTrialsalePlan.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
            return AjaxResult.success(frmNewproductTrialsalePlanService.submitVerify(frmNewproductTrialsalePlan));
        }
        return AjaxResult.success(EmsResultEntity.success());
    }

    @ApiOperation(value = "查询页面点提交/审批通过/审批驳回/确认", notes = "查询页面点提交/审批通过/审批驳回/确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "新品试销计划单", businessType = BusinessType.HANDLE)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody FrmNewproductTrialsalePlan frmNewproductTrialsalePlan) {
        if (ArrayUtil.isEmpty(frmNewproductTrialsalePlan.getNewproductTrialsalePlanSidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(frmNewproductTrialsalePlan.getHandleStatus())
                && StrUtil.isBlank(frmNewproductTrialsalePlan.getBusinessType())) {
            throw new BaseException("参数缺失");
        }
        return toAjax(frmNewproductTrialsalePlanService.check(frmNewproductTrialsalePlan));
    }

}
