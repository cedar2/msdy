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
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import javax.validation.Valid;
import com.platform.ems.domain.DelDeliveryNoteAttachment;
import com.platform.ems.service.IDelDeliveryNoteAttachmentService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 交货单-附件Controller
 *
 * @author linhongwei
 * @date 2021-04-21
 */
@RestController
@RequestMapping("/note/attachment")
@Api(tags = "交货单-附件")
public class DelDeliveryNoteAttachmentController extends BaseController {

    @Autowired
    private IDelDeliveryNoteAttachmentService delDeliveryNoteAttachmentService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询交货单-附件列表
     */
    @PreAuthorize(hasPermi = "ems:attachment:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询交货单-附件列表", notes = "查询交货单-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelDeliveryNoteAttachment.class))
    public TableDataInfo list(@RequestBody DelDeliveryNoteAttachment delDeliveryNoteAttachment) {
        startPage();
        List<DelDeliveryNoteAttachment> list = delDeliveryNoteAttachmentService.selectDelDeliveryNoteAttachmentList(delDeliveryNoteAttachment);
        return getDataTable(list);
    }

    /**
     * 导出交货单-附件列表
     */
    @PreAuthorize(hasPermi = "ems:attachment:export")
    @Log(title = "交货单-附件", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出交货单-附件列表", notes = "导出交货单-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, DelDeliveryNoteAttachment delDeliveryNoteAttachment) throws IOException {
        List<DelDeliveryNoteAttachment> list = delDeliveryNoteAttachmentService.selectDelDeliveryNoteAttachmentList(delDeliveryNoteAttachment);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<DelDeliveryNoteAttachment> util = new ExcelUtil<DelDeliveryNoteAttachment>(DelDeliveryNoteAttachment.class,dataMap);
        util.exportExcel(response, list, "交货单-附件");
    }

    /**
     * 获取交货单-附件详细信息
     */
    @ApiOperation(value = "获取交货单-附件详细信息", notes = "获取交货单-附件详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelDeliveryNoteAttachment.class))
    @PreAuthorize(hasPermi = "ems:attachment:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long deliveryNoteAttachmentSid) {
                    if(deliveryNoteAttachmentSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(delDeliveryNoteAttachmentService.selectDelDeliveryNoteAttachmentById(deliveryNoteAttachmentSid));
    }

    /**
     * 新增交货单-附件
     */
    @ApiOperation(value = "新增交货单-附件", notes = "新增交货单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:attachment:add")
    @Log(title = "交货单-附件", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid DelDeliveryNoteAttachment delDeliveryNoteAttachment) {
        return toAjax(delDeliveryNoteAttachmentService.insertDelDeliveryNoteAttachment(delDeliveryNoteAttachment));
    }

    /**
     * 修改交货单-附件
     */
    @ApiOperation(value = "修改交货单-附件", notes = "修改交货单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:attachment:edit")
    @Log(title = "交货单-附件", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody DelDeliveryNoteAttachment delDeliveryNoteAttachment) {
        return toAjax(delDeliveryNoteAttachmentService.updateDelDeliveryNoteAttachment(delDeliveryNoteAttachment));
    }

    /**
     * 删除交货单-附件
     */
    @ApiOperation(value = "删除交货单-附件", notes = "删除交货单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:attachment:remove")
    @Log(title = "交货单-附件", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  deliveryNoteAttachmentSids) {
        if(ArrayUtil.isEmpty( deliveryNoteAttachmentSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(delDeliveryNoteAttachmentService.deleteDelDeliveryNoteAttachmentByIds(deliveryNoteAttachmentSids));
    }
}
