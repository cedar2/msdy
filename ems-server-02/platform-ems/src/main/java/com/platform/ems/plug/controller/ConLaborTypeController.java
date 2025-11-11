package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.ems.constant.ConstantsEms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.util.ArrayUtil;

import javax.validation.Valid;

import com.platform.ems.plug.domain.ConLaborType;
import com.platform.ems.plug.service.IConLaborTypeService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 工价类型Controller
 *
 * @author c
 * @date 2021-06-10
 */
@RestController
@RequestMapping("/con/laborType")
@Api(tags = "工价类型")
public class ConLaborTypeController extends BaseController {

    @Autowired
    private IConLaborTypeService conLaborTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询工价类型列表
     */
    @PreAuthorize(hasPermi = "ems:con:laborType:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询工价类型列表", notes = "查询工价类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConLaborType.class))
    public TableDataInfo list(@RequestBody ConLaborType conLaborType) {
        startPage(conLaborType);
        List<ConLaborType> list = conLaborTypeService.selectConLaborTypeList(conLaborType);
        return getDataTable(list);
    }

    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConLaborType.class))
    @ApiOperation(value = "获取工价类型下拉列表", notes = "获取工价类型下拉列表")
    @PostMapping("/getList")
    public AjaxResult getList() {
        List<ConLaborType> list = conLaborTypeService.selectConLaborTypeList(null);
        return AjaxResult.success(list);
    }


    /**
     * 导出工价类型列表
     */
    @PreAuthorize(hasPermi = "ems:con:laborType:export")
    @Log(title = "工价类型", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出工价类型列表", notes = "导出工价类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConLaborType conLaborType) throws IOException {
        List<ConLaborType> list = conLaborTypeService.selectConLaborTypeList(conLaborType);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConLaborType> util = new ExcelUtil<>(ConLaborType.class, dataMap);
        util.exportExcel(response, list, "工价类型");
    }


    /**
     * 获取工价类型详细信息
     */
    @ApiOperation(value = "获取工价类型详细信息", notes = "获取工价类型详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConLaborType.class))
    @PreAuthorize(hasPermi = "ems:con:laborType:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long laborTypeSid) {
        if (laborTypeSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conLaborTypeService.selectConLaborTypeById(laborTypeSid));
    }

    /**
     * 新增工价类型
     */
    @ApiOperation(value = "新增工价类型", notes = "新增工价类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:laborType:add")
    @Log(title = "工价类型", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConLaborType conLaborType) {
        return toAjax(conLaborTypeService.insertConLaborType(conLaborType));
    }

    /**
     * 修改工价类型
     */
    @ApiOperation(value = "修改工价类型", notes = "修改工价类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:laborType:edit")
    @Log(title = "工价类型", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConLaborType conLaborType) {
        if(ConstantsEms.CHECK_STATUS.equals(conLaborType.getHandleStatus())){
            return AjaxResult.error("已确认不可编辑");
        }
        return toAjax(conLaborTypeService.updateConLaborType(conLaborType));
    }

    /**
     * 变更工价类型
     */
    @ApiOperation(value = "变更工价类型", notes = "变更工价类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:laborType:change")
    @Log(title = "工价类型", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConLaborType conLaborType) {
        if(ConstantsEms.SAVA_STATUS.equals(conLaborType.getHandleStatus())){
            return AjaxResult.error("未确认不可变更");
        }
        return toAjax(conLaborTypeService.changeConLaborType(conLaborType));
    }

    /**
     * 删除工价类型
     */
    @ApiOperation(value = "删除工价类型", notes = "删除工价类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:laborType:remove")
    @Log(title = "工价类型", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> laborTypeSids) {
        if (ArrayUtil.isEmpty(laborTypeSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conLaborTypeService.deleteConLaborTypeByIds(laborTypeSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "工价类型", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:con:laborType:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConLaborType conLaborType) {
        return AjaxResult.success(conLaborTypeService.changeStatus(conLaborType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:con:laborType:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "工价类型", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConLaborType conLaborType) {
        conLaborType.setConfirmDate(new Date());
        conLaborType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conLaborType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conLaborTypeService.check(conLaborType));
    }

    @PostMapping("/getConLaborTypeList")
    @ApiOperation(value = "工价类型下拉列表", notes = "工价类型下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConLaborType.class))
    public AjaxResult getConLaborTypeList(){
        return AjaxResult.success(conLaborTypeService.getConLaborTypeList());
    }


}
