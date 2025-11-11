package com.platform.ems.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.ManDayManufacturePlanItem;
import com.platform.ems.domain.dto.request.ManDayManufacturePlanItemRequest;
import com.platform.ems.domain.dto.response.ManDayManufacturePlanItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;

import javax.validation.Valid;
import com.platform.ems.domain.ManDayManufacturePlan;
import com.platform.ems.service.IManDayManufacturePlanService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 生产日计划Controller
 *
 * @author linhongwei
 * @date 2021-06-22
 */
@RestController
@RequestMapping("/plan")
@Api(tags = "生产日计划")
public class ManDayManufacturePlanController extends BaseController {

    @Autowired
    private IManDayManufacturePlanService manDayManufacturePlanService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询生产日计划列表
     */
//    @PreAuthorize(hasPermi = "ems:plan:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询生产日计划列表", notes = "查询生产日计划列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManDayManufacturePlan.class))
    public TableDataInfo list(@RequestBody ManDayManufacturePlan manDayManufacturePlan) {
        startPage(manDayManufacturePlan);
        List<ManDayManufacturePlan> list = manDayManufacturePlanService.selectManDayManufacturePlanList(manDayManufacturePlan);
        return getDataTable(list);
    }

    /**
     * 导出生产日计划列表
     */
//    @PreAuthorize(hasPermi = "ems:plan:export")
    @Log(title = "生产日计划", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产日计划列表", notes = "导出生产日计划列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManDayManufacturePlan manDayManufacturePlan) throws IOException {
        List<ManDayManufacturePlan> list = manDayManufacturePlanService.selectManDayManufacturePlanList(manDayManufacturePlan);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ManDayManufacturePlan> util = new ExcelUtil<ManDayManufacturePlan>(ManDayManufacturePlan.class,dataMap);
        util.exportExcel(response, list, "生产日计划");
    }



    /**
     * 获取生产日计划详细信息
     */
    @ApiOperation(value = "获取生产日计划详细信息", notes = "获取生产日计划详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManDayManufacturePlan.class))
//    @PreAuthorize(hasPermi = "ems:plan:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long dayManufacturePlanSid) {
        if (dayManufacturePlanSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manDayManufacturePlanService.selectManDayManufacturePlanById(dayManufacturePlanSid));
    }

    /**
     * 新增生产日计划
     */
    @ApiOperation(value = "新增生产日计划", notes = "新增生产日计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:plan:add")
    @Log(title = "生产日计划", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ManDayManufacturePlan manDayManufacturePlan) {
        return toAjax(manDayManufacturePlanService.insertManDayManufacturePlan(manDayManufacturePlan));
    }

    /**
     * 修改生产日计划
     */
    @ApiOperation(value = "修改生产日计划", notes = "修改生产日计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:plan:edit")
    @Log(title = "生产日计划", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ManDayManufacturePlan manDayManufacturePlan) {
        return toAjax(manDayManufacturePlanService.updateManDayManufacturePlan(manDayManufacturePlan));
    }

    /**
     * 变更生产日计划
     */
    @ApiOperation(value = "变更生产日计划", notes = "变更生产日计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:plan:change")
    @Log(title = "生产日计划", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManDayManufacturePlan manDayManufacturePlan) {
        return toAjax(manDayManufacturePlanService.changeManDayManufacturePlan(manDayManufacturePlan));
    }

    /**
     * 删除生产日计划
     */
    @ApiOperation(value = "删除生产日计划", notes = "删除生产日计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:plan:remove")
    @Log(title = "生产日计划", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> dayManufacturePlanSids) {
        if (ArrayUtil.isEmpty( dayManufacturePlanSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(manDayManufacturePlanService.deleteManDayManufacturePlanByIds(dayManufacturePlanSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
//    @PreAuthorize(hasPermi = "ems:plan:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产日计划", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ManDayManufacturePlan manDayManufacturePlan) {
        manDayManufacturePlan.setConfirmDate(new Date());
        manDayManufacturePlan.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        manDayManufacturePlan.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(manDayManufacturePlanService.check(manDayManufacturePlan));
    }

    /**
     * 生产日计划明细报表
     */
    @PostMapping("/getItemList")
    @PreAuthorize(hasPermi = "ems::man:day:manu:plan:report")
    @ApiOperation(value = "生产日计划明细报表", notes = "生产日计划明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManDayManufacturePlanItemResponse.class))
    public TableDataInfo list(@RequestBody ManDayManufacturePlanItemRequest request) {
        ManDayManufacturePlanItem manDayManufacturePlanItem = new ManDayManufacturePlanItem();
        BeanUtil.copyProperties(request, manDayManufacturePlanItem);
        startPage(manDayManufacturePlanItem);
        List<ManDayManufacturePlanItem> list = manDayManufacturePlanService.getItemList(manDayManufacturePlanItem);
        return getDataTable(list, ManDayManufacturePlanItemResponse::new);
    }

}
