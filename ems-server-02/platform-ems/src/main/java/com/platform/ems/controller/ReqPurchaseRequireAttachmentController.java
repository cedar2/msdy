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
import com.platform.ems.domain.ReqPurchaseRequireAttachment;
import com.platform.ems.service.IReqPurchaseRequireAttachmentService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 申购单-附件Controller
 *
 * @author linhongwei
 * @date 2021-04-06
 */
@RestController
@RequestMapping("/require/attachment")
@Api(tags = "申购单-附件")
public class ReqPurchaseRequireAttachmentController extends BaseController {

    @Autowired
    private IReqPurchaseRequireAttachmentService reqPurchaseRequireAttachmentService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询申购单-附件列表
     */
    @PreAuthorize(hasPermi = "ems:attachment:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询申购单-附件列表", notes = "查询申购单-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ReqPurchaseRequireAttachment.class))
    public TableDataInfo list(@RequestBody ReqPurchaseRequireAttachment reqPurchaseRequireAttachment) {
        startPage();
        List<ReqPurchaseRequireAttachment> list = reqPurchaseRequireAttachmentService.selectReqPurchaseRequireAttachmentList(reqPurchaseRequireAttachment);
        return getDataTable(list);
    }

    /**
     * 导出申购单-附件列表
     */
    @PreAuthorize(hasPermi = "ems:attachment:export")
    @Log(title = "申购单-附件", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出申购单-附件列表", notes = "导出申购单-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ReqPurchaseRequireAttachment reqPurchaseRequireAttachment) throws IOException {
        List<ReqPurchaseRequireAttachment> list = reqPurchaseRequireAttachmentService.selectReqPurchaseRequireAttachmentList(reqPurchaseRequireAttachment);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ReqPurchaseRequireAttachment> util = new ExcelUtil<ReqPurchaseRequireAttachment>(ReqPurchaseRequireAttachment.class,dataMap);
        util.exportExcel(response, list, "申购单-附件"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 获取申购单-附件详细信息
     */
    @ApiOperation(value = "获取申购单-附件详细信息", notes = "获取申购单-附件详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ReqPurchaseRequireAttachment.class))
    @PreAuthorize(hasPermi = "ems:attachment:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long purchaseRequireAttachmentSid) {
                    if(purchaseRequireAttachmentSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(reqPurchaseRequireAttachmentService.selectReqPurchaseRequireAttachmentById(purchaseRequireAttachmentSid));
    }

    /**
     * 新增申购单-附件
     */
    @ApiOperation(value = "新增申购单-附件", notes = "新增申购单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:attachment:add")
    @Log(title = "申购单-附件", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ReqPurchaseRequireAttachment reqPurchaseRequireAttachment) {
        return toAjax(reqPurchaseRequireAttachmentService.insertReqPurchaseRequireAttachment(reqPurchaseRequireAttachment));
    }

    /**
     * 修改申购单-附件
     */
    @ApiOperation(value = "修改申购单-附件", notes = "修改申购单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:attachment:edit")
    @Log(title = "申购单-附件", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ReqPurchaseRequireAttachment reqPurchaseRequireAttachment) {
        return toAjax(reqPurchaseRequireAttachmentService.updateReqPurchaseRequireAttachment(reqPurchaseRequireAttachment));
    }

    /**
     * 删除申购单-附件
     */
    @ApiOperation(value = "删除申购单-附件", notes = "删除申购单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:attachment:remove")
    @Log(title = "申购单-附件", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  purchaseRequireAttachmentSids) {
        if(ArrayUtil.isEmpty( purchaseRequireAttachmentSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(reqPurchaseRequireAttachmentService.deleteReqPurchaseRequireAttachmentByIds(purchaseRequireAttachmentSids));
    }
}
