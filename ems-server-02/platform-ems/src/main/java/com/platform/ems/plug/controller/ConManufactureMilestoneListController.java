package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.exception.CheckedException;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.plug.domain.ConManufactureMilestoneList;
import com.platform.ems.plug.service.IConManufactureMilestoneListService;
import com.platform.common.utils.poi.ExcelUtil;

/**
 * 生产里程碑清单Controller
 *
 * @author platform
 * @date 2024-03-14
 */
@Api(tags = "生产里程碑清单")
@RestController
@RequestMapping("/con/manufacture/milestone/list")
public class ConManufactureMilestoneListController extends BaseController {

    @Autowired
    private IConManufactureMilestoneListService conManufactureMilestoneListService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产里程碑清单列表
     */
    @ApiOperation(value = "查询生产里程碑清单列表", notes = "查询生产里程碑清单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConManufactureMilestoneList.class))
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody ConManufactureMilestoneList conManufactureMilestoneList) {
        startPage(conManufactureMilestoneList);
        List<ConManufactureMilestoneList> list = conManufactureMilestoneListService.selectConManufactureMilestoneListList(conManufactureMilestoneList);
        return getDataTable(list);
    }

    /**
     * 导出生产里程碑清单列表
     */
    @ApiOperation(value = "导出生产里程碑清单列表", notes = "导出生产里程碑清单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConManufactureMilestoneList conManufactureMilestoneList) throws IOException {
        List<ConManufactureMilestoneList> list = conManufactureMilestoneListService.selectConManufactureMilestoneListList(conManufactureMilestoneList);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConManufactureMilestoneList> util = new ExcelUtil<>(ConManufactureMilestoneList.class, dataMap);
        util.exportExcel(response, list, "生产里程碑清单");
    }

    /**
     * 获取生产里程碑清单详细信息
     */
    @ApiOperation(value = "获取生产里程碑清单详细信息", notes = "获取生产里程碑清单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConManufactureMilestoneList.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long manufactureMilestoneListSid) {
        if (manufactureMilestoneListSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conManufactureMilestoneListService.selectConManufactureMilestoneListById(manufactureMilestoneListSid));
    }

    /**
     * 新增生产里程碑清单
     */
    @ApiOperation(value = "新增生产里程碑清单", notes = "新增生产里程碑清单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConManufactureMilestoneList conManufactureMilestoneList) {
        return toAjax(conManufactureMilestoneListService.insertConManufactureMilestoneList(conManufactureMilestoneList));
    }

    @ApiOperation(value = "修改生产里程碑清单", notes = "修改生产里程碑清单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConManufactureMilestoneList conManufactureMilestoneList) {
        return toAjax(conManufactureMilestoneListService.updateConManufactureMilestoneList(conManufactureMilestoneList));
    }

    /**
     * 变更生产里程碑清单
     */
    @ApiOperation(value = "变更生产里程碑清单", notes = "变更生产里程碑清单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConManufactureMilestoneList conManufactureMilestoneList) {
        return toAjax(conManufactureMilestoneListService.changeConManufactureMilestoneList(conManufactureMilestoneList));
    }

    /**
     * 删除生产里程碑清单
     */
    @ApiOperation(value = "删除生产里程碑清单", notes = "删除生产里程碑清单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> manufactureMilestoneListSids) {
        if (CollectionUtils.isEmpty(manufactureMilestoneListSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conManufactureMilestoneListService.deleteConManufactureMilestoneListByIds(manufactureMilestoneListSids));
    }

    @ApiOperation(value = "修改处理状态接口", notes = "修改处理状态接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConManufactureMilestoneList conManufactureMilestoneList) {
        return toAjax(conManufactureMilestoneListService.check(conManufactureMilestoneList));
    }

}
