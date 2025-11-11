package com.platform.ems.controller;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.ManProcessRoute;
import com.platform.ems.domain.ManProcessRouteItem;
import com.platform.ems.domain.dto.request.ManProcessRouteActionRequest;
import com.platform.ems.mapper.ManProcessRouteItemMapper;
import com.platform.ems.service.IManProcessRouteService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 工艺路线Controller
 *
 * @author linhongwei
 * @date 2021-03-26
 */
@RestController
@RequestMapping("/man/process/route")
@Api(tags = "工艺路线")
public class ManProcessRouteController extends BaseController {

    @Autowired
    private IManProcessRouteService manProcessRouteService;
    @Resource
    private ManProcessRouteItemMapper manProcessRouteItemMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询工艺路线列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询工艺路线列表", notes = "查询工艺路线列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProcessRoute.class))
    public TableDataInfo list(@RequestBody ManProcessRoute manProcessRoute) {
        startPage(manProcessRoute);
        List<ManProcessRoute> list = manProcessRouteService.selectManProcessRouteList(manProcessRoute);
        return getDataTable(list);
    }

    /**
     * 导出工艺路线列表
     */
    @ApiOperation(value = "导出工艺路线列表", notes = "导出工艺路线列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManProcessRoute manProcessRoute) throws IOException {
        List<ManProcessRoute> list = manProcessRouteService.selectManProcessRouteList(manProcessRoute);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManProcessRoute> util = new ExcelUtil<>(ManProcessRoute.class, dataMap);
        util.exportExcel(response, list, "工艺路线");
    }

    /**
     * 获取工艺路线详细信息
     */
    @ApiOperation(value = "获取工艺路线详细信息", notes = "获取工艺路线详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProcessRoute.class))
    @PostMapping("/getInfo")
    public ManProcessRoute getInfo(Long processRouteSid) {
        return manProcessRouteService.selectManProcessRouteById(processRouteSid);
    }

    @ApiOperation(value = "生产月计划-获取工艺路线", notes = "生产月计划-获取工艺路线")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProcessRoute.class))
    @PostMapping("/month/getInfo")
    public AjaxResult monthGetInfo(Long processRouteSid) {
        return AjaxResult.success(manProcessRouteService.monthGetManProcess(processRouteSid));
    }
    /**
     * 新增工艺路线
     */
    @ApiOperation(value = "新增工艺路线", notes = "新增工艺路线")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ManProcessRoute manProcessRoute) {
        int row = manProcessRouteService.insertManProcessRoute(manProcessRoute);
        return AjaxResult.success(manProcessRoute);
    }

    /**
     * 修改工艺路线
     */
    @ApiOperation(value = "修改工艺路线", notes = "修改工艺路线")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ManProcessRoute manProcessRoute) {
        return toAjax(manProcessRouteService.updateManProcessRoute(manProcessRoute));
    }

    /**
     * 删除工艺路线
     */
    @ApiOperation(value = "删除工艺路线", notes = "删除工艺路线")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> processRouteSids) {
        return toAjax(manProcessRouteService.deleteManProcessRouteByIds(processRouteSids));
    }

    /**
     * 变更工艺路线
     */
    @ApiOperation(value = "变更工艺路线", notes = "变更工艺路线")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManProcessRoute manProcessRoute) {
        return AjaxResult.success(manProcessRouteService.change(manProcessRoute));
    }

    /**
     * 确认工艺路线
     */
    @ApiOperation(value = "确认工艺路线", notes = "确认工艺路线")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/confirm")
    public AjaxResult confirm(@RequestBody ManProcessRouteActionRequest action) {
        return toAjax(manProcessRouteService.confirm(action));
    }

    /**
     * 启用/停用 工艺路线
     */
    @ApiOperation(value = "启用/停用工艺路线", notes = "启用/停用工艺路线")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/status")
    public AjaxResult status(@RequestBody ManProcessRouteActionRequest action) {
        return toAjax(manProcessRouteService.status(action));
    }

    /**
     * 工艺路线下拉框列表
     */
    @PostMapping("/getManProcessRouteList")
    @ApiOperation(value = "工艺路线下拉框列表", notes = "工艺路线下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProcessRoute.class))
    public AjaxResult getManProcessRouteList(@RequestBody ManProcessRoute manProcessRoute) {
        return AjaxResult.success(manProcessRouteService.getManProcessRouteList(manProcessRoute));
    }

    /**
     * 工艺路线明细报表
     */
    @PostMapping("/item/list")
    @ApiOperation(value = "工艺路线明细报表", notes = "工艺路线明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProcessRoute.class))
    public TableDataInfo getItemList(@RequestBody ManProcessRouteItem manProcessRouteItem) {
        startPage(manProcessRouteItem);
        List<ManProcessRouteItem> list = manProcessRouteItemMapper.getItemList(manProcessRouteItem);
        return getDataTable(list);
    }

    /**
     * 导出工艺路线明细报表
     */
    @ApiOperation(value = "导出工艺路线明细报表", notes = "导出工艺路线明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/export")
    public void export(HttpServletResponse response, ManProcessRouteItem manProcessRouteItem) throws IOException {
        List<ManProcessRouteItem> list = manProcessRouteItemMapper.getItemList(manProcessRouteItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManProcessRouteItem> util = new ExcelUtil<>(ManProcessRouteItem.class, dataMap);
        util.exportExcel(response, list, "工艺路线明细报表");
    }
}
