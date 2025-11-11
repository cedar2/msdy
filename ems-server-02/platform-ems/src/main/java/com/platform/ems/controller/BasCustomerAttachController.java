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
import com.platform.ems.domain.BasCustomerAttach;
import com.platform.ems.service.IBasCustomerAttachService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 客户档案-附件Controller
 *
 * @author chenkw
 * @date 2021-09-15
 */
@RestController
@RequestMapping("/customer/attach")
@Api(tags = "客户档案-附件")
public class BasCustomerAttachController extends BaseController {

    @Autowired
    private IBasCustomerAttachService basCustomerAttachService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询客户档案-附件列表
     */
    @PreAuthorize(hasPermi = "ems:customer:attach:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询客户档案-附件列表", notes = "查询客户档案-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCustomerAttach.class))
    public TableDataInfo list(@RequestBody BasCustomerAttach basCustomerAttach) {
        startPage(basCustomerAttach);
        List<BasCustomerAttach> list = basCustomerAttachService.selectBasCustomerAttachList(basCustomerAttach);
        return getDataTable(list);
    }

    /**
     * 导出客户档案-附件列表
     */
    @PreAuthorize(hasPermi = "ems:customer:attach:export")
    @Log(title = "客户档案-附件", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出客户档案-附件列表", notes = "导出客户档案-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasCustomerAttach basCustomerAttach) throws IOException {
        List<BasCustomerAttach> list = basCustomerAttachService.selectBasCustomerAttachList(basCustomerAttach);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<BasCustomerAttach> util = new ExcelUtil<>(BasCustomerAttach.class,dataMap);
        util.exportExcel(response, list, "客户档案-附件");
    }


    /**
     * 获取客户档案-附件详细信息
     */
    @ApiOperation(value = "获取客户档案-附件详细信息", notes = "获取客户档案-附件详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCustomerAttach.class))
    @PreAuthorize(hasPermi = "ems:customer:attach:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long attachmentSid) {
        if(attachmentSid==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basCustomerAttachService.selectBasCustomerAttachById(attachmentSid));
    }

    /**
     * 新增客户档案-附件
     */
    @ApiOperation(value = "新增客户档案-附件", notes = "新增客户档案-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:customer:attach:add")
    @Log(title = "客户档案-附件", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasCustomerAttach basCustomerAttach) {
        return toAjax(basCustomerAttachService.insertBasCustomerAttach(basCustomerAttach));
    }

    /**
     * 修改客户档案-附件
     */
    @ApiOperation(value = "修改客户档案-附件", notes = "修改客户档案-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:customer:attach:edit")
    @Log(title = "客户档案-附件", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody BasCustomerAttach basCustomerAttach) {
        return toAjax(basCustomerAttachService.updateBasCustomerAttach(basCustomerAttach));
    }

    /**
     * 变更客户档案-附件
     */
    @ApiOperation(value = "变更客户档案-附件", notes = "变更客户档案-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:customer:attach:change")
    @Log(title = "客户档案-附件", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody BasCustomerAttach basCustomerAttach) {
        return toAjax(basCustomerAttachService.changeBasCustomerAttach(basCustomerAttach));
    }

    /**
     * 删除客户档案-附件
     */
    @ApiOperation(value = "删除客户档案-附件", notes = "删除客户档案-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:customer:attach:remove")
    @Log(title = "客户档案-附件", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  attachmentSids) {
        if(CollectionUtils.isEmpty( attachmentSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(basCustomerAttachService.deleteBasCustomerAttachByIds(attachmentSids));
    }

}
