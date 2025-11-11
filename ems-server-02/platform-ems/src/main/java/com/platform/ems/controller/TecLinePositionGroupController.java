package com.platform.ems.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.TecLinePositionGroup;
import com.platform.ems.domain.TecLinePositionGroupItem;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ITecLinePositionGroupItemService;
import com.platform.ems.service.ITecLinePositionGroupService;
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
import java.util.List;
import java.util.Map;

/**
 * 线部位组档案Controller
 *
 * @author linhongwei
 * @date 2021-08-19
 */
@RestController
@RequestMapping("/line/position/group")
@Api(tags = "线部位组档案")
public class TecLinePositionGroupController extends BaseController {

    @Autowired
    private ITecLinePositionGroupService tecLinePositionGroupService;

    @Autowired
    private ITecLinePositionGroupItemService tecLinePositionGroupItemService;

    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询线部位组档案列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询线部位组档案列表", notes = "查询线部位组档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecLinePositionGroup.class))
    public TableDataInfo list(@RequestBody TecLinePositionGroup tecLinePositionGroup) {
        startPage(tecLinePositionGroup);
        List<TecLinePositionGroup> list = tecLinePositionGroupService.selectTecLinePositionGroupList(tecLinePositionGroup);
        return getDataTable(list);
    }

    /**
     * 导出线部位组档案列表
     */
    @ApiOperation(value = "导出线部位组档案列表", notes = "导出线部位组档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, TecLinePositionGroup tecLinePositionGroup) throws IOException {
        List<TecLinePositionGroup> list = tecLinePositionGroupService.selectTecLinePositionGroupList(tecLinePositionGroup);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<TecLinePositionGroup> util = new ExcelUtil<>(TecLinePositionGroup.class, dataMap);
        util.exportExcel(response, list, "线部位组");
    }


    /**
     * 获取线部位组档案详细信息
     */
    @ApiOperation(value = "获取线部位组档案详细信息", notes = "获取线部位组档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecLinePositionGroup.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long groupSid) {
        if (groupSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(tecLinePositionGroupService.selectTecLinePositionGroupById(groupSid));
    }

    /**
     * 新增线部位组档案
     */
    @ApiOperation(value = "新增线部位组档案", notes = "新增线部位组档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid TecLinePositionGroup tecLinePositionGroup) {
        int row = tecLinePositionGroupService.insertTecLinePositionGroup(tecLinePositionGroup);
        return AjaxResult.success(tecLinePositionGroup);
    }

    /**
     * 修改线部位组档案
     */
    @ApiOperation(value = "修改线部位组档案", notes = "修改线部位组档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid TecLinePositionGroup tecLinePositionGroup) {
        return toAjax(tecLinePositionGroupService.updateTecLinePositionGroup(tecLinePositionGroup));
    }

    /**
     * 变更线部位组档案
     */
    @ApiOperation(value = "变更线部位组档案", notes = "变更线部位组档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid TecLinePositionGroup tecLinePositionGroup) {
        return toAjax(tecLinePositionGroupService.changeTecLinePositionGroup(tecLinePositionGroup));
    }

    /**
     * 删除线部位组档案
     */
    @ApiOperation(value = "删除线部位组档案", notes = "删除线部位组档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> groupSids) {
        if (CollectionUtils.isEmpty(groupSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(tecLinePositionGroupService.deleteTecLinePositionGroupByIds(groupSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody TecLinePositionGroup tecLinePositionGroup) {
        return AjaxResult.success(tecLinePositionGroupService.changeStatus(tecLinePositionGroup));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody TecLinePositionGroup tecLinePositionGroup) {
        return toAjax(tecLinePositionGroupService.check(tecLinePositionGroup));
    }

    /**
     * 线部位组明细报表
     */
    @PostMapping("/item/list")
    @ApiOperation(value = "线部位组明细报表", notes = "线部位组明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecLinePositionGroupItem.class))
    public TableDataInfo getItemList(@RequestBody TecLinePositionGroupItem tecLinePositionGroupItem) {
        startPage(tecLinePositionGroupItem);
        List<TecLinePositionGroupItem> list = tecLinePositionGroupItemService.selectTecLinePositionGroupItemList(tecLinePositionGroupItem);
        return getDataTable(list);
    }

    /**
     * 导出线部位组明细报表
     */
    @ApiOperation(value = "导出线部位组明细报表", notes = "导出线部位组明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/export")
    public void export(HttpServletResponse response, TecLinePositionGroupItem tecLinePositionGroupItem) throws IOException {
        List<TecLinePositionGroupItem> list = tecLinePositionGroupItemService.selectTecLinePositionGroupItemList(tecLinePositionGroupItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<TecLinePositionGroupItem> util = new ExcelUtil<>(TecLinePositionGroupItem.class, dataMap);
        util.exportExcel(response, list, "线部位组明细报表");
    }
}
