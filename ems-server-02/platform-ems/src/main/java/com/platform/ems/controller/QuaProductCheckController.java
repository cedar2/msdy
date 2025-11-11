package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.QuaProductCheck;
import com.platform.ems.service.IQuaProductCheckService;
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
import java.util.List;
import java.util.Map;

/**
 * 成衣检测单-主Controller
 *
 * @author linhongwei
 * @date 2022-04-13
 */
@RestController
@RequestMapping("/product/check")
@Api(tags = "成衣检测单-主")
public class QuaProductCheckController extends BaseController {

    @Autowired
    private IQuaProductCheckService quaProductCheckService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询成衣检测单-主列表
     */
    @PreAuthorize(hasPermi = "ems:product:check:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询成衣检测单-主列表", notes = "查询成衣检测单-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = QuaProductCheck.class))
    public TableDataInfo list(@RequestBody QuaProductCheck quaProductCheck) {
        startPage(quaProductCheck);
        List<QuaProductCheck> list = quaProductCheckService.selectQuaProductCheckList(quaProductCheck);
        return getDataTable(list);
    }

    /**
     * 导出成衣检测单-主列表
     */
    @PreAuthorize(hasPermi = "ems:product:check:export")
    @Log(title = "成衣检测单-主", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出成衣检测单-主列表", notes = "导出成衣检测单-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, QuaProductCheck quaProductCheck) throws IOException {
        List<QuaProductCheck> list = quaProductCheckService.selectQuaProductCheckList(quaProductCheck);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<QuaProductCheck> util = new ExcelUtil<>(QuaProductCheck.class, dataMap);
        util.exportExcel(response, list, "成衣检测单_" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取成衣检测单-主详细信息
     */
    @ApiOperation(value = "获取成衣检测单-主详细信息", notes = "获取成衣检测单-主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = QuaProductCheck.class))
    @PreAuthorize(hasPermi = "ems:product:check:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long productCheckSid) {
        if (productCheckSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(quaProductCheckService.selectQuaProductCheckById(productCheckSid));
    }

    /**
     * 新增成衣检测单-主
     */
    @ApiOperation(value = "新增成衣检测单-主", notes = "新增成衣检测单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:product:check:add")
    @Log(title = "成衣检测单-主", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid QuaProductCheck quaProductCheck) {
        return toAjax(quaProductCheckService.insertQuaProductCheck(quaProductCheck));
    }

    /**
     * 修改成衣检测单-主
     */
    @ApiOperation(value = "修改成衣检测单-主", notes = "修改成衣检测单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:product:check:edit")
    @Log(title = "成衣检测单-主", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid QuaProductCheck quaProductCheck) {
        return toAjax(quaProductCheckService.updateQuaProductCheck(quaProductCheck));
    }

    /**
     * 变更成衣检测单-主
     */
    @ApiOperation(value = "变更成衣检测单-主", notes = "变更成衣检测单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:product:check:change")
    @Log(title = "成衣检测单-主", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid QuaProductCheck quaProductCheck) {
        return toAjax(quaProductCheckService.changeQuaProductCheck(quaProductCheck));
    }

    /**
     * 删除成衣检测单-主
     */
    @ApiOperation(value = "删除成衣检测单-主", notes = "删除成衣检测单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:product:check:remove")
    @Log(title = "成衣检测单-主", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> productCheckSids) {
        if (CollectionUtils.isEmpty(productCheckSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(quaProductCheckService.deleteQuaProductCheckByIds(productCheckSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:product:check:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "成衣检测单-主", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody QuaProductCheck quaProductCheck) {
        return toAjax(quaProductCheckService.check(quaProductCheck));
    }

}
