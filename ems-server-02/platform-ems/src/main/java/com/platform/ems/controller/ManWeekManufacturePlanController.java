package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.domain.ManWeekManufacturePlan;
import com.platform.ems.domain.ManWeekManufacturePlanItem;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.export.ManWeekManufacturePlanExResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IManWeekManufacturePlanItemService;
import com.platform.ems.service.IManWeekManufacturePlanService;
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
import java.util.*;

/**
 * 生产周计划Controller
 *
 * @author hjj
 * @date 2021-07-16
 */
@RestController
@RequestMapping("/manufacture/week/plan")
@Api(tags = "生产周计划")
public class ManWeekManufacturePlanController extends BaseController {

    @Autowired
    private IManWeekManufacturePlanService manWeekManufacturePlanService;
    @Autowired
    private IManWeekManufacturePlanItemService manWeekManufacturePlanItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产周计划列表
     */
    @PreAuthorize(hasPermi = "ems:manufacture:week:plan:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询生产周计划列表", notes = "查询生产周计划列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManWeekManufacturePlan.class))
    public TableDataInfo list(@RequestBody ManWeekManufacturePlan manWeekManufacturePlan) {
        startPage(manWeekManufacturePlan);
        List<ManWeekManufacturePlan> list = manWeekManufacturePlanService.selectManWeekManufacturePlanList(manWeekManufacturePlan);
        return getDataTable(list);
    }

    /**
     * 导出生产周计划列表
     */
    @PreAuthorize(hasPermi = "ems:manufacture:week:plan:export")
    @Log(title = "生产周计划", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产周计划列表", notes = "导出生产周计划列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManWeekManufacturePlan manWeekManufacturePlan) throws IOException {
        List<ManWeekManufacturePlan> list = manWeekManufacturePlanService.selectManWeekManufacturePlanList(manWeekManufacturePlan);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManWeekManufacturePlanExResponse> util = new ExcelUtil<>(ManWeekManufacturePlanExResponse.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list,ManWeekManufacturePlanExResponse::new) , "生产周计划");
    }


    /**
     * 获取生产周计划详细信息
     */
    @ApiOperation(value = "获取生产周计划详细信息", notes = "获取生产周计划详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManWeekManufacturePlan.class))
    @PreAuthorize(hasPermi = "ems:manufacture:week:plan:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long weekManufacturePlanSid) {
        if (weekManufacturePlanSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manWeekManufacturePlanService.selectManWeekManufacturePlanById(weekManufacturePlanSid));
    }

    /**
     * 新增生产周计划
     */
    @ApiOperation(value = "新增生产周计划", notes = "新增生产周计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manufacture:week:plan:add")
    @Log(title = "生产周计划", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent
    public AjaxResult add(@RequestBody @Valid ManWeekManufacturePlan manWeekManufacturePlan) {
        return toAjax(manWeekManufacturePlanService.insertManWeekManufacturePlan(manWeekManufacturePlan));
    }


    @ApiOperation(value = "生产周计划-获取事项进度", notes = "生产周计划-获取事项进度")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manufacture:week:plan:add")
    @Log(title = "生产周计划", businessType = BusinessType.INSERT)
    @PostMapping("/get/concernTask")
    @Idempotent
    public AjaxResult add(@RequestBody List<ManWeekManufacturePlanItem> manWeekManufacturePlanItemList) {
        return AjaxResult.success(manWeekManufacturePlanService.getConcernTask(manWeekManufacturePlanItemList));
    }

    @ApiOperation(value = "生产周计划明细获取分配量", notes = "生产周计划明细获取分配量")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/get/quantityFenpei")
    @Idempotent
    public AjaxResult getQuantityFenpei(@RequestBody List<ManWeekManufacturePlanItem> items) {
        return AjaxResult.success(manWeekManufacturePlanService.getQuantityFenpei(items));
    }
    /**
     * 修改生产周计划
     */
    @Idempotent
    @ApiOperation(value = "修改生产周计划", notes = "修改生产周计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manufacture:week:plan:edit")
    @Log(title = "生产周计划", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ManWeekManufacturePlan manWeekManufacturePlan) {
        int row = 0;
        try {
            row = manWeekManufacturePlanService.updateManWeekManufacturePlan(manWeekManufacturePlan);
        } catch (BaseException e) {
            if (e.getModule() != null && EmsResultEntity.WARN_TAG.equals(e.getModule())) {
                List<CommonErrMsgResponse> msgList = new ArrayList<>();
                msgList.add(new CommonErrMsgResponse().setMsg(e.getDefaultMessage()));
                return AjaxResult.success(EmsResultEntity.warning(msgList));
            }
            else {
                throw e;
            }
        }
        return toAjax(row);
    }

    /**
     * 变更生产周计划
     */
    @ApiOperation(value = "变更生产周计划", notes = "变更生产周计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manufacture:week:plan:change")
    @Log(title = "生产周计划", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManWeekManufacturePlan manWeekManufacturePlan) {
        return toAjax(manWeekManufacturePlanService.changeManWeekManufacturePlan(manWeekManufacturePlan));
    }

    /**
     * 删除生产周计划
     */
    @ApiOperation(value = "删除生产周计划", notes = "删除生产周计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manufacture:week:plan:remove")
    @Log(title = "生产周计划", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> weekManufacturePlanSids) {
        if (CollectionUtils.isEmpty(weekManufacturePlanSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manWeekManufacturePlanService.deleteManWeekManufacturePlanByIds(weekManufacturePlanSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:manufacture:week:plan:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产周计划", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ManWeekManufacturePlan manWeekManufacturePlan) {
        manWeekManufacturePlan.setConfirmDate(new Date());
        manWeekManufacturePlan.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        manWeekManufacturePlan.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        int row = 0;
        try {
            row = manWeekManufacturePlanService.check(manWeekManufacturePlan);
        } catch (BaseException e) {
            if (e.getModule() != null && EmsResultEntity.WARN_TAG.equals(e.getModule())) {
                List<CommonErrMsgResponse> msgList = new ArrayList<>();
                for (Object arg : e.getArgs()) {
                    msgList.add(new CommonErrMsgResponse().setMsg((String) arg));
                }
                return AjaxResult.success(EmsResultEntity.warning(msgList));
            }
            else {
                throw e;
            }
        }
        return toAjax(row);
    }

    /**
     * 生产周计划明细报表
     */
    @PostMapping("/getItemList")
    @PreAuthorize(hasPermi = "ems:manufacture:week:plan:item:list")
    @ApiOperation(value = "生产周计划明细报表", notes = "生产周计划明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManWeekManufacturePlanItem.class))
    public TableDataInfo list(@RequestBody ManWeekManufacturePlanItem manWeekManufacturePlanItem) {
        startPage(manWeekManufacturePlanItem);
        List<ManWeekManufacturePlanItem> list = manWeekManufacturePlanItemService.getItemList(manWeekManufacturePlanItem);
        return getDataTable(list);
    }

    /**
     * 导出生产周计划明细报表
     */
    @PreAuthorize(hasPermi = "ems:manufacture:week:plan:item:export")
    @Log(title = "生产周计划明细报表", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产周计划明细报表", notes = "导出生产周计划明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/export")
    public void export(HttpServletResponse response, ManWeekManufacturePlanItem manWeekManufacturePlanItem) throws IOException {
        List<ManWeekManufacturePlanItem> list = manWeekManufacturePlanItemService.getItemList(manWeekManufacturePlanItem);
        manWeekManufacturePlanItemService.exportReport( response, list , manWeekManufacturePlanItem.getDateStart());
    }

    /**
     * 作废-生产周计划
     */
    @ApiOperation(value = "作废生产周计划", notes = "作废生产周计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manufacture:week:plan:cancellation")
    @Log(title = "生产周计划", businessType = BusinessType.CANCEL)
    @PostMapping("/cancellation")
    public AjaxResult cancellation(Long weekManufacturePlanSid) {
        if (weekManufacturePlanSid == null) {
            throw new BaseException("参数缺失");
        }
        return toAjax(manWeekManufacturePlanService.cancellationWeekManufacturePlanById(weekManufacturePlanSid));
    }

    /**
     * 提交前校验-生产周计划
     */
    @ApiOperation(value = "提交前校验-生产周计划", notes = "提交前校验-生产周计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/verify")
    public AjaxResult verify(Long weekManufacturePlanSid, String handleStatus) {
        if (weekManufacturePlanSid == null || StrUtil.isEmpty(handleStatus)) {
            throw new BaseException("参数缺失");
        }
        return toAjax(manWeekManufacturePlanService.verify(weekManufacturePlanSid, handleStatus));
    }
}
