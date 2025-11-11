package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.ManMonthManufacturePlan;
import com.platform.ems.domain.ManMonthManufacturePlanItem;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.ManMonthManufacturePlanRequest;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.ManMonthManufacturePlanItemResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IManMonthManufacturePlanService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 生产月计划Controller
 *
 * @author linhongwei
 * @date 2021-07-16
 */
@RestController
@RequestMapping("/manufacture/month/plan")
@Api(tags = "生产月计划")
public class ManMonthManufacturePlanController extends BaseController {

    @Autowired
    private IManMonthManufacturePlanService manMonthManufacturePlanService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产月计划列表
     */
    @PreAuthorize(hasPermi = "ems:manufacture:month:plan:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询生产月计划列表", notes = "查询生产月计划列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManMonthManufacturePlan.class))
    public TableDataInfo list(@RequestBody ManMonthManufacturePlan manMonthManufacturePlan) {
        startPage(manMonthManufacturePlan);
        List<ManMonthManufacturePlan> list = manMonthManufacturePlanService.selectManMonthManufacturePlanList(manMonthManufacturePlan);
        return getDataTable(list);
    }

    /**
     * 导出生产月计划列表
     */
    @PreAuthorize(hasPermi = "ems:manufacture:month:plan:export")
    @Log(title = "生产月计划", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产月计划列表", notes = "导出生产月计划列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManMonthManufacturePlan manMonthManufacturePlan) throws IOException {
        List<ManMonthManufacturePlan> list = manMonthManufacturePlanService.selectManMonthManufacturePlanList(manMonthManufacturePlan);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManMonthManufacturePlan> util = new ExcelUtil<>(ManMonthManufacturePlan.class, dataMap);
        util.exportExcel(response, list, "生产月计划");
    }


    /**
     * 获取生产月计划详细信息
     */
    @ApiOperation(value = "获取生产月计划详细信息", notes = "获取生产月计划详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManMonthManufacturePlan.class))
    @PreAuthorize(hasPermi = "ems:manufacture:month:plan:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long monthManufacturePlanSid) {
        if (monthManufacturePlanSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manMonthManufacturePlanService.selectManMonthManufacturePlanById(monthManufacturePlanSid));
    }

    /**
     * 新增生产月计划
     */
    @ApiOperation(value = "新增生产月计划", notes = "新增生产月计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manufacture:month:plan:add")
    @Log(title = "生产月计划", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ManMonthManufacturePlan manMonthManufacturePlan) {
        int row = 0;
        try {
            row = manMonthManufacturePlanService.insertManMonthManufacturePlan(manMonthManufacturePlan);
        } catch (CustomException e) {
            if (e.getCode() != null && 1 == e.getCode()) {
                List<CommonErrMsgResponse> msgList = new ArrayList<>();
                msgList.add(new CommonErrMsgResponse().setMsg(e.getMessage()));
                return AjaxResult.success(EmsResultEntity.warning(msgList));
            }
            else {
                throw e;
            }
        }
        return toAjax(row);
    }

    @ApiOperation(value = "生产月计划添加明细-行转列", notes = "生产月计划添加明细-行转列")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/addItem/exchange")
    public AjaxResult addItem(@RequestBody  ManMonthManufacturePlanRequest manMonthManufacturePlanRequest) {
        return AjaxResult.success(manMonthManufacturePlanService.addItem(manMonthManufacturePlanRequest));
    }
    /**
     * 修改生产月计划
     */
    @ApiOperation(value = "修改生产月计划", notes = "修改生产月计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manufacture:month:plan:edit")
    @Log(title = "生产月计划", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ManMonthManufacturePlan manMonthManufacturePlan) {
        int row = 0;
        try {
            row = manMonthManufacturePlanService.updateManMonthManufacturePlan(manMonthManufacturePlan);
        } catch (CustomException e) {
            if (e.getCode() != null && 1 == e.getCode()) {
                List<CommonErrMsgResponse> msgList = new ArrayList<>();
                msgList.add(new CommonErrMsgResponse().setMsg(e.getMessage()));
                return AjaxResult.success(EmsResultEntity.warning(msgList));
            }
            else {
                throw e;
            }
        }
        return toAjax(row);
    }

    /**
     * 变更生产月计划
     */
    @ApiOperation(value = "变更生产月计划", notes = "变更生产月计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manufacture:month:plan:change")
    @Log(title = "生产月计划", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManMonthManufacturePlan manMonthManufacturePlan) {
        return toAjax(manMonthManufacturePlanService.changeManMonthManufacturePlan(manMonthManufacturePlan));
    }

    /**
     * 删除生产月计划
     */
    @ApiOperation(value = "删除生产月计划", notes = "删除生产月计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manufacture:month:plan:remove")
    @Log(title = "生产月计划", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> monthManufacturePlanSids) {
        if (CollectionUtils.isEmpty(monthManufacturePlanSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manMonthManufacturePlanService.deleteManMonthManufacturePlanByIds(monthManufacturePlanSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:manufacture:month:plan:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产月计划", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ManMonthManufacturePlan manMonthManufacturePlan) {
        manMonthManufacturePlan.setConfirmDate(new Date());
        manMonthManufacturePlan.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        manMonthManufacturePlan.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        int row = 0;
        try {
            row = manMonthManufacturePlanService.check(manMonthManufacturePlan);
        }catch (BaseException e) {
            if (EmsResultEntity.WARN_TAG.equals(e.getModule())) {
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
     * 生产月计划明细报表
     */
    @PostMapping("/getItemList")
    @ApiOperation(value = "生产月计划明细报表", notes = "生产月计划明细报表")
    @PreAuthorize(hasPermi = "ems:manufacture:month:plan:item:list")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManMonthManufacturePlanItemResponse.class))
    public TableDataInfo list(@RequestBody ManMonthManufacturePlanItem manMonthManufacturePlanItem) {
        startPage(manMonthManufacturePlanItem);
        List<ManMonthManufacturePlanItem> list = manMonthManufacturePlanService.getItemList(manMonthManufacturePlanItem);
        return getDataTable(list);
    }

    /**
     * 导出生产月计划明细报表
     */
    @PreAuthorize(hasPermi = "ems:manufacture:month:plan:item:export")
    @Log(title = "生产月计划明细报表", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产月计划明细报表", notes = "生产月计划明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/export")
    public void export(HttpServletResponse response, ManMonthManufacturePlanItem manMonthManufacturePlanItem) throws IOException {
        List<ManMonthManufacturePlanItem> list = manMonthManufacturePlanService.getItemList(manMonthManufacturePlanItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManMonthManufacturePlanItem> util = new ExcelUtil<>(ManMonthManufacturePlanItem.class, dataMap);
        util.exportExcel(response, list, "生产月计划明细报表" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 作废-生产月计划
     */
    @ApiOperation(value = "作废生产月计划", notes = "作废生产月计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manufacture:month:plan:cancellation")
    @Log(title = "生产月计划", businessType = BusinessType.CANCEL)
    @PostMapping("/cancellation")
    public AjaxResult cancellation(Long monthManufacturePlanSid) {
        if (monthManufacturePlanSid == null) {
            throw new BaseException("参数缺失");
        }
        return toAjax(manMonthManufacturePlanService.cancellationMonthManufacturePlanById(monthManufacturePlanSid));
    }

    /**
     * 提交前校验-生产月计划
     */
    @ApiOperation(value = "提交前校验-生产月计划", notes = "提交前校验-生产月计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/verify")
    public AjaxResult verify(Long monthManufacturePlanSid, String handleStatus) {
        if (monthManufacturePlanSid == null || StrUtil.isEmpty(handleStatus)) {
            throw new BaseException("参数缺失");
        }
        return toAjax(manMonthManufacturePlanService.verify(monthManufacturePlanSid, handleStatus));
    }
}
