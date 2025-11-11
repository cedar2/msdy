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
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.PurInquiryAttach;
import com.platform.ems.service.IPurInquiryAttachService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 物料询价单-附件Controller
 *
 * @author chenkw
 * @date 2022-01-11
 */
@RestController
@RequestMapping("/pur/inquiry/attach")
@Api(tags = "物料询价单-附件")
public class PurInquiryAttachController extends BaseController {

    @Autowired
    private IPurInquiryAttachService purInquiryAttachService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询物料询价单-附件列表
     */
    @PreAuthorize(hasPermi = "ems:pur:inquiry:attach:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询物料询价单-附件列表", notes = "查询物料询价单-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurInquiryAttach.class))
    public TableDataInfo list(@RequestBody PurInquiryAttach purInquiryAttach) {
        startPage(purInquiryAttach);
        List<PurInquiryAttach> list = purInquiryAttachService.selectPurInquiryAttachList(purInquiryAttach);
        return getDataTable(list);
    }

    /**
     * 导出物料询价单-附件列表
     */
    @PreAuthorize(hasPermi = "ems:pur:inquiry:attach:export")
    @Log(title = "物料询价单-附件", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出物料询价单-附件列表", notes = "导出物料询价单-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurInquiryAttach purInquiryAttach) throws IOException {
        List<PurInquiryAttach> list = purInquiryAttachService.selectPurInquiryAttachList(purInquiryAttach);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PurInquiryAttach> util = new ExcelUtil<>(PurInquiryAttach.class, dataMap);
        util.exportExcel(response, list, "物料询价单-附件");
    }


    /**
     * 获取物料询价单-附件详细信息
     */
    @ApiOperation(value = "获取物料询价单-附件详细信息", notes = "获取物料询价单-附件详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurInquiryAttach.class))
    @PreAuthorize(hasPermi = "ems:pur:inquiry:attach:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long inquiryattachmentSid) {
        if (inquiryattachmentSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purInquiryAttachService.selectPurInquiryAttachById(inquiryattachmentSid));
    }

    /**
     * 新增物料询价单-附件
     */
    @ApiOperation(value = "新增物料询价单-附件", notes = "新增物料询价单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:inquiry:attach:add")
    @Log(title = "物料询价单-附件", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurInquiryAttach purInquiryAttach) {
        return toAjax(purInquiryAttachService.insertPurInquiryAttach(purInquiryAttach));
    }

    /**
     * 修改物料询价单-附件
     */
    @ApiOperation(value = "修改物料询价单-附件", notes = "修改物料询价单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:inquiry:attach:edit")
    @Log(title = "物料询价单-附件", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody PurInquiryAttach purInquiryAttach) {
        return toAjax(purInquiryAttachService.updatePurInquiryAttach(purInquiryAttach));
    }

    /**
     * 变更物料询价单-附件
     */
    @ApiOperation(value = "变更物料询价单-附件", notes = "变更物料询价单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:inquiry:attach:change")
    @Log(title = "物料询价单-附件", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody PurInquiryAttach purInquiryAttach) {
        return toAjax(purInquiryAttachService.changePurInquiryAttach(purInquiryAttach));
    }

    /**
     * 删除物料询价单-附件
     */
    @ApiOperation(value = "删除物料询价单-附件", notes = "删除物料询价单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:inquiry:attach:remove")
    @Log(title = "物料询价单-附件", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> inquiryattachmentSids) {
        if (CollectionUtils.isEmpty(inquiryattachmentSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purInquiryAttachService.deletePurInquiryAttachByIds(inquiryattachmentSids));
    }

}
