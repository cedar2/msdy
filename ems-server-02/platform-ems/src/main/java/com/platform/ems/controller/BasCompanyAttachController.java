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
import com.platform.ems.domain.BasCompanyAttach;
import com.platform.ems.service.IBasCompanyAttachService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 公司档案-附件Controller
 *
 * @author chenkw
 * @date 2021-09-15
 */
@RestController
@RequestMapping("/company/attach")
@Api(tags = "公司档案-附件")
public class BasCompanyAttachController extends BaseController {

    @Autowired
    private IBasCompanyAttachService basCompanyAttachService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询公司档案-附件列表
     */
    @PreAuthorize(hasPermi = "ems:company:attach:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询公司档案-附件列表", notes = "查询公司档案-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCompanyAttach.class))
    public TableDataInfo list(@RequestBody BasCompanyAttach basCompanyAttach) {
        startPage(basCompanyAttach);
        List<BasCompanyAttach> list = basCompanyAttachService.selectBasCompanyAttachList(basCompanyAttach);
        return getDataTable(list);
    }

    /**
     * 导出公司档案-附件列表
     */
    @PreAuthorize(hasPermi = "ems:company:attach:export")
    @Log(title = "公司档案-附件", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出公司档案-附件列表", notes = "导出公司档案-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasCompanyAttach basCompanyAttach) throws IOException {
        List<BasCompanyAttach> list = basCompanyAttachService.selectBasCompanyAttachList(basCompanyAttach);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<BasCompanyAttach> util = new ExcelUtil<>(BasCompanyAttach.class,dataMap);
        util.exportExcel(response, list, "公司档案-附件");
    }


    /**
     * 获取公司档案-附件详细信息
     */
    @ApiOperation(value = "获取公司档案-附件详细信息", notes = "获取公司档案-附件详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCompanyAttach.class))
    @PreAuthorize(hasPermi = "ems:company:attach:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long attachmentSid) {
        if(attachmentSid==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basCompanyAttachService.selectBasCompanyAttachById(attachmentSid));
    }

    /**
     * 新增公司档案-附件
     */
    @ApiOperation(value = "新增公司档案-附件", notes = "新增公司档案-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:company:attach:add")
    @Log(title = "公司档案-附件", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasCompanyAttach basCompanyAttach) {
        return toAjax(basCompanyAttachService.insertBasCompanyAttach(basCompanyAttach));
    }

    /**
     * 修改公司档案-附件
     */
    @ApiOperation(value = "修改公司档案-附件", notes = "修改公司档案-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:company:attach:edit")
    @Log(title = "公司档案-附件", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody BasCompanyAttach basCompanyAttach) {
        return toAjax(basCompanyAttachService.updateBasCompanyAttach(basCompanyAttach));
    }

    /**
     * 变更公司档案-附件
     */
    @ApiOperation(value = "变更公司档案-附件", notes = "变更公司档案-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:company:attach:change")
    @Log(title = "公司档案-附件", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody BasCompanyAttach basCompanyAttach) {
        return toAjax(basCompanyAttachService.changeBasCompanyAttach(basCompanyAttach));
    }

    /**
     * 删除公司档案-附件
     */
    @ApiOperation(value = "删除公司档案-附件", notes = "删除公司档案-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:company:attach:remove")
    @Log(title = "公司档案-附件", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  attachmentSids) {
        if(CollectionUtils.isEmpty( attachmentSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(basCompanyAttachService.deleteBasCompanyAttachByIds(attachmentSids));
    }

}
