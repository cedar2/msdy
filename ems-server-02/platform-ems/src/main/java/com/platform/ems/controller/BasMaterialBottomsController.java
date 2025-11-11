package com.platform.ems.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasMaterialBottoms;
import com.platform.ems.domain.dto.request.MaterialBottomsActionRequest;
import com.platform.ems.service.IBasMaterialBottomsService;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 商品-上下装尺码对照Controller
 *
 * @author shakeflags
 * @date 2021-03-9
 */
@RestController
@RequestMapping("/bottoms")
@Api(tags = "商品-上下装尺码对照")
public class BasMaterialBottomsController extends BaseController {

    @Autowired
    private IBasMaterialBottomsService basMaterialBottomsService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询商品-上下装尺码对照列表
     */
    @PreAuthorize(hasPermi = "ems:material:bottoms:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询商品-上下装尺码对照列表", notes = "查询商品-上下装尺码对照列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialBottoms.class))
    public TableDataInfo list(@RequestBody BasMaterialBottoms request) {
        startPage(request);
        List<BasMaterialBottoms> list = basMaterialBottomsService.selectBasMaterialBottomsList(request);
        return getDataTable(list);
    }

    /**
     * 导出商品-上下装尺码对照列表
     */
    @PreAuthorize(hasPermi = "ems:material:bottoms:export")
    @Log(title = "商品-上下装尺码对照", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出商品-上下装尺码对照列表", notes = "导出商品-上下装尺码对照列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasMaterialBottoms basMaterialBottoms) throws IOException {
        List<BasMaterialBottoms> list =  basMaterialBottomsService.selectBasMaterialBottomsList(basMaterialBottoms);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<BasMaterialBottoms> util = new ExcelUtil<>(BasMaterialBottoms.class,dataMap);
        util.exportExcel(response, list, "上下装对照组档案");
    }

    /**
     * 获取商品-上下装尺码对照详细信息
     */
    @ApiOperation(value = "获取商品-上下装尺码对照详细信息", notes = "获取商品-上下装尺码对照详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialBottoms.class))
    @PreAuthorize(hasPermi = "ems:material:bottoms:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long bottomsSkuSid) {
        return AjaxResult.success(basMaterialBottomsService.selectBasMaterialBottomsById(bottomsSkuSid));
    }

    /**
     * 新增商品-上下装尺码对照
     */
    @ApiOperation(value = "新增商品-上下装尺码对照", notes = "新增商品-上下装尺码对照")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:material:bottoms:add")
    @Log(title = "商品-上下装尺码对照", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody BasMaterialBottoms basMaterialBottoms) {
        return toAjax(basMaterialBottomsService.insertBasMaterialBottoms(basMaterialBottoms));
    }

    /**
     * 修改商品-上下装尺码对照
     */
    @ApiOperation(value = "修改商品-上下装尺码对照", notes = "修改商品-上下装尺码对照")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:material:bottoms:edit")
    @Log(title = "商品-上下装尺码对照", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasMaterialBottoms basMaterialBottoms) {
        String checkStatus= ConstantsEms.CHECK_STATUS;
        String handleStatus = basMaterialBottoms.getHandleStatus();
        if(checkStatus.equals(handleStatus)){
            basMaterialBottoms.setConfirmDate(new Date());
            basMaterialBottoms.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        return basMaterialBottomsService.updateBasMaterialBottoms(basMaterialBottoms);
    }
    /**
     * 修改商品-上下装尺码对照
     */
    @ApiOperation(value = "修改商品-上下装尺码对照", notes = "修改商品-上下装尺码对照")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:material:bottoms:change")
    @Log(title = "商品-上下装尺码对照", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid BasMaterialBottoms basMaterialBottoms) {
        return basMaterialBottomsService.changeBasMaterialBottoms(basMaterialBottoms);
    }

    /**
     * 删除商品-上下装尺码对照
     */
    @ApiOperation(value = "删除商品-上下装尺码对照", notes = "删除商品-上下装尺码对照")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:material:bottoms:remove")
    @Log(title = "商品-上下装尺码对照", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  bottomsSkuSids) {
        return basMaterialBottomsService.deleteBasMaterialBottomsByIds(bottomsSkuSids);
    }
    /**
     * 启用/停用状态
     */
    @PostMapping("/status")
    @Log(title = "商品-上下装尺码对照", businessType = BusinessType.ENBLEORDISABLE)
    @PreAuthorize(hasPermi = "ems:material:bottoms:enableordisable")
    @ApiOperation(value = "批量编辑启用/停用状态", notes = "批量编辑启用/停用状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult editValidStatus(@RequestBody MaterialBottomsActionRequest materialBottomsActionRequest){
        return basMaterialBottomsService.updateValidStatus(materialBottomsActionRequest);
    }
    /**
     * 确认状态
     */
    @PostMapping("/confirm")
    @Log(title = "商品-上下装尺码对照", businessType = BusinessType.CHECK)
    @PreAuthorize(hasPermi = "ems:material:bottoms:check")
    @ApiOperation(value = "批量编辑确认状态", notes = "批量编辑确认状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult confirm(@RequestBody MaterialBottomsActionRequest materialBottomsActionRequest){
        return basMaterialBottomsService.confirm(materialBottomsActionRequest);
    }
}
