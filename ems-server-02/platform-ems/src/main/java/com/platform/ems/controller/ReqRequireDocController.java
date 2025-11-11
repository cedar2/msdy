package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.ReqRequireDoc;
import com.platform.ems.service.IReqRequireDocService;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 需求单Controller
 *
 * @author linhongwei
 * @date 2021-04-02
 */
@RestController
@RequestMapping("/doc")
@Api(tags = "需求单")
public class ReqRequireDocController extends BaseController {

    @Autowired
    private IReqRequireDocService reqRequireDocService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询需求单列表
     */
    @PreAuthorize(hasPermi = "ems:doc:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询需求单列表", notes = "查询需求单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ReqRequireDoc.class))
    public TableDataInfo list(@RequestBody ReqRequireDoc reqRequireDoc) {
        startPage(reqRequireDoc);
        List<ReqRequireDoc> list = reqRequireDocService.selectReqRequireDocList(reqRequireDoc);
        return getDataTable(list);
    }

    /**
     * 导出需求单列表
     */
    @PreAuthorize(hasPermi = "ems:doc:export")
    @Log(title = "需求单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出需求单列表", notes = "导出需求单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ReqRequireDoc reqRequireDoc) throws IOException {
        List<ReqRequireDoc> list = reqRequireDocService.selectReqRequireDocList(reqRequireDoc);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ReqRequireDoc> util = new ExcelUtil<ReqRequireDoc>(ReqRequireDoc.class, dataMap);
        util.exportExcel(response, list, "需求单" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 获取需求单详细信息
     */
    @ApiOperation(value = "获取需求单详细信息", notes = "获取需求单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ReqRequireDoc.class))
    @PreAuthorize(hasPermi = "ems:doc:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long requireDocSid) {
        return AjaxResult.success(reqRequireDocService.selectReqRequireDocById(requireDocSid));
    }

    /**
     * 新增需求单
     */
    @ApiOperation(value = "新增需求单", notes = "新增需求单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:doc:add")
    @Log(title = "需求单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ReqRequireDoc reqRequireDoc) {
        return toAjax(reqRequireDocService.insertReqRequireDoc(reqRequireDoc));
    }

    /**
     * 修改需求单
     */
    @ApiOperation(value = "修改需求单", notes = "修改需求单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:doc:edit")
    @Log(title = "需求单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ReqRequireDoc reqRequireDoc) {
        return toAjax(reqRequireDocService.updateReqRequireDoc(reqRequireDoc));
    }

    /**
     * 删除需求单
     */
    @ApiOperation(value = "删除需求单", notes = "删除需求单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:doc:remove")
    @Log(title = "需求单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody Long[] requireDocSids) {
        return toAjax(reqRequireDocService.deleteReqRequireDocByIds(requireDocSids));
    }

    /**
     * 需求单确认
     */
    @PreAuthorize(hasPermi = "ems:doc:check")
    @Log(title = "需求单", businessType = BusinessType.UPDATE)
    @PostMapping("/confirm")
    @ApiOperation(value = "需求单确认", notes = "需求单确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult confirm(@RequestBody ReqRequireDoc reqRequireDoc) {
        return AjaxResult.success(reqRequireDocService.confirm(reqRequireDoc));
    }

    /**
     * 需求单变更
     */
    @PreAuthorize(hasPermi = "ems:doc:change")
    @Log(title = "需求单", businessType = BusinessType.UPDATE)
    @PostMapping("/change")
    @ApiOperation(value = "需求单变更", notes = "需求单变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody @Valid ReqRequireDoc reqRequireDoc) {
        return AjaxResult.success(reqRequireDocService.change(reqRequireDoc));
    }
}
