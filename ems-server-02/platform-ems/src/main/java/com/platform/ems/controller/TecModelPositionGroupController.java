package com.platform.ems.controller;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.BasSkuGroup;
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
import cn.hutool.core.util.StrUtil;

import javax.validation.Valid;

import com.platform.ems.domain.TecModelPositionGroup;
import com.platform.ems.service.ITecModelPositionGroupService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 版型部位组档案Controller
 *
 * @author linhongwei
 * @date 2021-06-02
 */
@RestController
@RequestMapping("/tecModelPositionGroup")
@Api(tags = "版型部位组档案")
public class TecModelPositionGroupController extends BaseController {

    @Autowired
    private ITecModelPositionGroupService tecModelPositionGroupService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询版型部位组档案列表
     */
    @PreAuthorize(hasPermi = "ems:tec:model:pos:group:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询版型部位组档案列表", notes = "查询版型部位组档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecModelPositionGroup.class))
    public TableDataInfo list(@RequestBody TecModelPositionGroup tecModelPositionGroup) {
        startPage(tecModelPositionGroup);
        List<TecModelPositionGroup> list = tecModelPositionGroupService.selectTecModelPositionGroupList(tecModelPositionGroup);
        return getDataTable(list);
    }

    /**
     * 导出版型部位组档案列表
     */
    @PreAuthorize(hasPermi = "ems:tec:model:pos:group:export")
    @Log(title = "版型部位组档案", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出版型部位组档案列表", notes = "导出版型部位组档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, TecModelPositionGroup tecModelPositionGroup) throws IOException {
        List<TecModelPositionGroup> list = tecModelPositionGroupService.selectTecModelPositionGroupList(tecModelPositionGroup);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<TecModelPositionGroup> util = new ExcelUtil<>(TecModelPositionGroup.class, dataMap);
        util.exportExcel(response, list, "版型部位组");
    }


    /**
     * 获取版型部位组档案详细信息
     */
    @ApiOperation(value = "获取版型部位组档案详细信息", notes = "获取版型部位组档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecModelPositionGroup.class))
    @PreAuthorize(hasPermi = "ems:tec:model:pos:group:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long groupSid) {
        if (groupSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(tecModelPositionGroupService.selectTecModelPositionGroupById(groupSid));
    }

    /**
     * 新增版型部位组档案
     */
    @ApiOperation(value = "新增版型部位组档案", notes = "新增版型部位组档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:tec:model:pos:group:add")
    @Log(title = "版型部位组档案", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid TecModelPositionGroup tecModelPositionGroup) {
        return toAjax(tecModelPositionGroupService.insertTecModelPositionGroup(tecModelPositionGroup));
    }

    /**
     * 修改版型部位组档案
     */
    @ApiOperation(value = "修改版型部位组档案", notes = "修改版型部位组档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:tec:model:pos:group:edit")
    @Log(title = "版型部位组档案", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody TecModelPositionGroup tecModelPositionGroup) {
        return toAjax(tecModelPositionGroupService.updateTecModelPositionGroup(tecModelPositionGroup));
    }

    /**
     * 删除版型部位组档案
     */
    @ApiOperation(value = "删除版型部位组档案", notes = "删除版型部位组档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:tec:model:pos:group:remove")
    @Log(title = "版型部位组档案", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String> groupSids) {
        if (ArrayUtil.isEmpty(groupSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(tecModelPositionGroupService.deleteTecModelPositionGroupByIds(groupSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "版型部位组档案", businessType = BusinessType.ENBLEORDISABLE)
    @PreAuthorize(hasPermi = "ems:tec:model:pos:group:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody TecModelPositionGroup tecModelPositionGroup) {
        if (ArrayUtil.isEmpty(tecModelPositionGroup.getGroupSidList()) || StrUtil.isEmpty(tecModelPositionGroup.getStatus())) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(tecModelPositionGroupService.changeStatus(tecModelPositionGroup));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:tec:model:pos:group:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "版型部位组档案", businessType = BusinessType.HANDLE)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody TecModelPositionGroup tecModelPositionGroup) {
        if (ArrayUtil.isEmpty(tecModelPositionGroup.getGroupSidList())) {
            throw new CheckedException("参数缺失");
        }
        tecModelPositionGroup.setConfirmDate(new Date());
        tecModelPositionGroup.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        tecModelPositionGroup.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(tecModelPositionGroupService.check(tecModelPositionGroup));
    }

    @ApiOperation(value = "获取版型部位组下拉列表", notes = "获取版型部位组下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasSkuGroup.class))
    @PostMapping("/getList")
    public AjaxResult getList() {
        return AjaxResult.success(tecModelPositionGroupService.getList());
    }
}
