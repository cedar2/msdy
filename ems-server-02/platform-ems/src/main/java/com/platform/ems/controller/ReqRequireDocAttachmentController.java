package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import javax.validation.Valid;
import com.platform.ems.domain.ReqRequireDocAttachment;
import com.platform.ems.service.IReqRequireDocAttachmentService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 需求单附件Controller
 *
 * @author linhongwei
 * @date 2021-04-02
 */
@RestController
@RequestMapping("/doc/attachment")
@Api(tags = "需求单附件")
public class ReqRequireDocAttachmentController extends BaseController {

    @Autowired
    private IReqRequireDocAttachmentService reqRequireDocAttachmentService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询需求单附件列表
     */
    @PreAuthorize(hasPermi = "ems:attachment:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询需求单附件列表", notes = "查询需求单附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ReqRequireDocAttachment.class))
    public TableDataInfo list(@RequestBody ReqRequireDocAttachment reqRequireDocAttachment) {
        startPage();
        List<ReqRequireDocAttachment> list = reqRequireDocAttachmentService.selectReqRequireDocAttachmentList(reqRequireDocAttachment);
        return getDataTable(list);
    }

    /**
     * 导出需求单附件列表
     */
    @PreAuthorize(hasPermi = "ems:attachment:export")
    @Log(title = "需求单附件", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出需求单附件列表", notes = "导出需求单附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ReqRequireDocAttachment reqRequireDocAttachment) throws IOException {
        List<ReqRequireDocAttachment> list = reqRequireDocAttachmentService.selectReqRequireDocAttachmentList(reqRequireDocAttachment);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ReqRequireDocAttachment> util = new ExcelUtil<ReqRequireDocAttachment>(ReqRequireDocAttachment.class,dataMap);
        util.exportExcel(response, list, "需求单附件"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 获取需求单附件详细信息
     */
    @ApiOperation(value = "获取需求单附件详细信息", notes = "获取需求单附件详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ReqRequireDocAttachment.class))
    @PreAuthorize(hasPermi = "ems:attachment:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long requireDocAttachmentSid) {
        return AjaxResult.success(reqRequireDocAttachmentService.selectReqRequireDocAttachmentById(requireDocAttachmentSid));
    }

    /**
     * 新增需求单附件
     */
    @ApiOperation(value = "新增需求单附件", notes = "新增需求单附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:attachment:add")
    @Log(title = "需求单附件", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ReqRequireDocAttachment reqRequireDocAttachment) {
        return toAjax(reqRequireDocAttachmentService.insertReqRequireDocAttachment(reqRequireDocAttachment));
    }

    /**
     * 修改需求单附件
     */
    @ApiOperation(value = "修改需求单附件", notes = "修改需求单附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:attachment:edit")
    @Log(title = "需求单附件", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ReqRequireDocAttachment reqRequireDocAttachment) {
        return toAjax(reqRequireDocAttachmentService.updateReqRequireDocAttachment(reqRequireDocAttachment));
    }

    /**
     * 删除需求单附件
     */
    @ApiOperation(value = "删除需求单附件", notes = "删除需求单附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:attachment:remove")
    @Log(title = "需求单附件", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  requireDocAttachmentSids) {
        return toAjax(reqRequireDocAttachmentService.deleteReqRequireDocAttachmentByIds(requireDocAttachmentSids));
    }
}
