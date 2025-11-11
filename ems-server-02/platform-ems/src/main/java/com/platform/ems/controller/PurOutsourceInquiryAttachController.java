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
import com.platform.ems.domain.PurOutsourceInquiryAttach;
import com.platform.ems.service.IPurOutsourceInquiryAttachService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 加工询价单-附件Controller
 *
 * @author chenkw
 * @date 2022-01-11
 */
@RestController
@RequestMapping("/pur/outsource/inquiry/attach")
@Api(tags = "加工询价单-附件")
public class PurOutsourceInquiryAttachController extends BaseController {

    @Autowired
    private IPurOutsourceInquiryAttachService purOutsourceInquiryAttachService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询加工询价单-附件列表
     */
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:attach:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询加工询价单-附件列表", notes = "查询加工询价单-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourceInquiryAttach.class))
    public TableDataInfo list(@RequestBody PurOutsourceInquiryAttach purOutsourceInquiryAttach) {
        startPage(purOutsourceInquiryAttach);
        List<PurOutsourceInquiryAttach> list = purOutsourceInquiryAttachService.selectPurOutsourceInquiryAttachList(purOutsourceInquiryAttach);
        return getDataTable(list);
    }

    /**
     * 导出加工询价单-附件列表
     */
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:attach:export")
    @Log(title = "加工询价单-附件", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出加工询价单-附件列表", notes = "导出加工询价单-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurOutsourceInquiryAttach purOutsourceInquiryAttach) throws IOException {
        List<PurOutsourceInquiryAttach> list = purOutsourceInquiryAttachService.selectPurOutsourceInquiryAttachList(purOutsourceInquiryAttach);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PurOutsourceInquiryAttach> util = new ExcelUtil<PurOutsourceInquiryAttach>(PurOutsourceInquiryAttach.class, dataMap);
        util.exportExcel(response, list, "加工询价单-附件");
    }


    /**
     * 获取加工询价单-附件详细信息
     */
    @ApiOperation(value = "获取加工询价单-附件详细信息", notes = "获取加工询价单-附件详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourceInquiryAttach.class))
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:attach:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long outsourceInquiryattachmentSid) {
        if (outsourceInquiryattachmentSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purOutsourceInquiryAttachService.selectPurOutsourceInquiryAttachById(outsourceInquiryattachmentSid));
    }

    /**
     * 新增加工询价单-附件
     */
    @ApiOperation(value = "新增加工询价单-附件", notes = "新增加工询价单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:attach:add")
    @Log(title = "加工询价单-附件", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurOutsourceInquiryAttach purOutsourceInquiryAttach) {
        return toAjax(purOutsourceInquiryAttachService.insertPurOutsourceInquiryAttach(purOutsourceInquiryAttach));
    }

    /**
     * 修改加工询价单-附件
     */
    @ApiOperation(value = "修改加工询价单-附件", notes = "修改加工询价单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:attach:edit")
    @Log(title = "加工询价单-附件", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody PurOutsourceInquiryAttach purOutsourceInquiryAttach) {
        return toAjax(purOutsourceInquiryAttachService.updatePurOutsourceInquiryAttach(purOutsourceInquiryAttach));
    }

    /**
     * 变更加工询价单-附件
     */
    @ApiOperation(value = "变更加工询价单-附件", notes = "变更加工询价单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:attach:change")
    @Log(title = "加工询价单-附件", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody PurOutsourceInquiryAttach purOutsourceInquiryAttach) {
        return toAjax(purOutsourceInquiryAttachService.changePurOutsourceInquiryAttach(purOutsourceInquiryAttach));
    }

    /**
     * 删除加工询价单-附件
     */
    @ApiOperation(value = "删除加工询价单-附件", notes = "删除加工询价单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pur:outsource:inquiry:attach:remove")
    @Log(title = "加工询价单-附件", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> outsourceInquiryattachmentSids) {
        if (CollectionUtils.isEmpty(outsourceInquiryattachmentSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purOutsourceInquiryAttachService.deletePurOutsourceInquiryAttachByIds(outsourceInquiryattachmentSids));
    }

}
