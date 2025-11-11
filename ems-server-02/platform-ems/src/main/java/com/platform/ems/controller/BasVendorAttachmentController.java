package com.platform.ems.controller;

import java.util.List;
import java.util.Date;
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
import javax.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.BasVendorAttachment;
import com.platform.ems.service.IBasVendorAttachmentService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 供应商-附件Controller
 *
 * @author chenkw
 * @date 2021-09-13
 */
@RestController
@RequestMapping("/vendorAttachment")
@Api(tags = "供应商-附件")
public class BasVendorAttachmentController extends BaseController {

    @Autowired
    private IBasVendorAttachmentService basVendorAttachmentService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商-附件列表
     */
    @PreAuthorize(hasPermi = "ems:vendorAttachment:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商-附件列表", notes = "查询供应商-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasVendorAttachment.class))
    public TableDataInfo list(@RequestBody BasVendorAttachment basVendorAttachment) {
        startPage(basVendorAttachment);
        List<BasVendorAttachment> list = basVendorAttachmentService.selectBasVendorAttachmentList(basVendorAttachment);
        return getDataTable(list);
    }

    /**
     * 导出供应商-附件列表
     */
    @PreAuthorize(hasPermi = "ems:vendorAttachment:export")
    @Log(title = "供应商-附件", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出供应商-附件列表", notes = "导出供应商-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasVendorAttachment basVendorAttachment) throws IOException {
        List<BasVendorAttachment> list = basVendorAttachmentService.selectBasVendorAttachmentList(basVendorAttachment);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<BasVendorAttachment> util = new ExcelUtil<BasVendorAttachment>(BasVendorAttachment.class,dataMap);
        util.exportExcel(response, list, "供应商-附件");
    }


    /**
     * 获取供应商-附件详细信息
     */
    @ApiOperation(value = "获取供应商-附件详细信息", notes = "获取供应商-附件详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasVendorAttachment.class))
    @PreAuthorize(hasPermi = "ems:vendorAttachment:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long vendorAttachmentSid) {
        if(vendorAttachmentSid==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basVendorAttachmentService.selectBasVendorAttachmentById(vendorAttachmentSid));
    }

    /**
     * 新增供应商-附件
     */
    @ApiOperation(value = "新增供应商-附件", notes = "新增供应商-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:vendorAttachment:add")
    @Log(title = "供应商-附件", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasVendorAttachment basVendorAttachment) {
        return toAjax(basVendorAttachmentService.insertBasVendorAttachment(basVendorAttachment));
    }

    /**
     * 修改供应商-附件
     */
    @ApiOperation(value = "修改供应商-附件", notes = "修改供应商-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:vendorAttachment:edit")
    @Log(title = "供应商-附件", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody BasVendorAttachment basVendorAttachment) {
        return toAjax(basVendorAttachmentService.updateBasVendorAttachment(basVendorAttachment));
    }

    /**
     * 变更供应商-附件
     */
    @ApiOperation(value = "变更供应商-附件", notes = "变更供应商-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:vendorAttachment:change")
    @Log(title = "供应商-附件", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody BasVendorAttachment basVendorAttachment) {
        return toAjax(basVendorAttachmentService.changeBasVendorAttachment(basVendorAttachment));
    }

    /**
     * 删除供应商-附件
     */
    @ApiOperation(value = "删除供应商-附件", notes = "删除供应商-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:vendorAttachment:remove")
    @Log(title = "供应商-附件", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  vendorAttachmentSids) {
        if(CollectionUtils.isEmpty( vendorAttachmentSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(basVendorAttachmentService.deleteBasVendorAttachmentByIds(vendorAttachmentSids));
    }

}
