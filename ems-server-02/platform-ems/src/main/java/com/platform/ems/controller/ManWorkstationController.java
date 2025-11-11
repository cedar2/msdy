package com.platform.ems.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.domain.ManWorkstation;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.impl.ManWorkstationServiceImpl;
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
 * 工位档案Controller
 *
 * @author Straw
 * @date 2023-03-31
 */
@RestController
@RequestMapping("/workstation")
@Api(tags = "工位档案")
public class ManWorkstationController extends BaseController {

    @Autowired
    private ManWorkstationServiceImpl manWorkstationService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询工位档案列表
     */

    @PostMapping("/list")
    @ApiOperation(value = "查询工位档案列表",
                  notes = "查询工位档案列表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = ManWorkstation.class))
    public TableDataInfo list(@RequestBody ManWorkstation manWorkstation) {
        startPage(manWorkstation);
        List<ManWorkstation> list = manWorkstationService.selectManWorkstationList(manWorkstation);
        return getDataTable(list);
    }

    /**
     * 导出工位档案列表
     */
    @Log(title = "工位档案",
         businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出工位档案列表",
                  notes = "导出工位档案列表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManWorkstation manWorkstation) throws IOException {
        List<ManWorkstation> list = manWorkstationService.selectManWorkstationList(manWorkstation);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManWorkstation> util = new ExcelUtil<>(ManWorkstation.class, dataMap);
        util.exportExcel(response, list, "工位档案");
    }


    /**
     * 获取工位档案详细信息
     */
    @ApiOperation(value = "获取工位档案详细信息",
                  notes = "获取工位档案详细信息")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = ManWorkstation.class))

    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long workstationSid) {
        if (workstationSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manWorkstationService.selectManWorkstationById(workstationSid));
    }

    /**
     * 新增工位档案
     */
    @ApiOperation(value = "新增工位档案",
                  notes = "新增工位档案")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))

    @Log(title = "工位档案",
         businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid ManWorkstation manWorkstation) {
        return toAjax(manWorkstationService.insertManWorkstation(manWorkstation));
    }

    @ApiOperation(value = "修改工位档案",
                  notes = "修改工位档案")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))

    @Log(title = "工位档案",
         businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮",
                interval = 3000)
    public AjaxResult edit(@RequestBody ManWorkstation manWorkstation) {
        return toAjax(manWorkstationService.updateManWorkstation(manWorkstation));
    }

    /**
     * 变更工位档案
     */
    @ApiOperation(value = "变更工位档案",
                  notes = "变更工位档案")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))

    @Log(title = "工位档案",
         businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManWorkstation manWorkstation) {
        return toAjax(manWorkstationService.changeManWorkstation(manWorkstation));
    }

    /**
     * 删除工位档案
     */
    @ApiOperation(value = "删除工位档案",
                  notes = "删除工位档案")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))

    @Log(title = "工位档案",
         businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> workstationSids) {
        if (CollectionUtils.isEmpty(workstationSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manWorkstationService.deleteManWorkstationByIds(workstationSids));
    }

    @ApiOperation(value = "启用停用接口",
                  notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "工位档案",
         businessType = BusinessType.UPDATE)

    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ManWorkstation manWorkstation) {
        return AjaxResult.success(manWorkstationService.changeStatus(manWorkstation));
    }

    @ApiOperation(value = "确认",
                  notes = "确认")

    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "工位档案",
         businessType = BusinessType.CHECK)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody ManWorkstation manWorkstation) {
        return toAjax(manWorkstationService.check(manWorkstation));
    }

}
