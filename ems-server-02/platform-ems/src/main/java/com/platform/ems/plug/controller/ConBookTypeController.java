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

import com.platform.ems.plug.domain.ConBookType;
import com.platform.ems.plug.service.IConBookTypeService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 流水类型_财务Controller
 *
 * @author linhongwei
 * @date 2021-06-29
 */
@RestController
@RequestMapping("/con/book/type")
@Api(tags = "流水类型_财务")
public class ConBookTypeController extends BaseController {

    @Autowired
    private IConBookTypeService conBookTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询流水类型_财务列表
     */
    @PreAuthorize(hasPermi = "ems:con:book:type:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询流水类型_财务列表", notes = "查询流水类型_财务列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBookType.class))
    public TableDataInfo list(@RequestBody ConBookType conBookType) {
        startPage(conBookType);
        List<ConBookType> list = conBookTypeService.selectConBookTypeList(conBookType);
        return getDataTable(list);
    }

    /**
     * 导出流水类型_财务列表
     */
    @PreAuthorize(hasPermi = "ems:con:book:type:type:export")
    @Log(title = "流水类型_财务", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出流水类型_财务列表", notes = "导出流水类型_财务列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBookType conBookType) throws IOException {
        List<ConBookType> list = conBookTypeService.selectConBookTypeList(conBookType);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBookType> util = new ExcelUtil<>(ConBookType.class, dataMap);
        util.exportExcel(response, list, "流水类型_财务");
    }


    /**
     * 获取流水类型_财务详细信息
     */
    @ApiOperation(value = "获取流水类型_财务详细信息", notes = "获取流水类型_财务详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBookType.class))
    @PreAuthorize(hasPermi = "ems:con:book:type:type:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBookTypeService.selectConBookTypeById(sid));
    }

    /**
     * 新增流水类型_财务
     */
    @ApiOperation(value = "新增流水类型_财务", notes = "新增流水类型_财务")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:book:type:type:add")
    @Log(title = "流水类型_财务", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBookType conBookType) {
        return toAjax(conBookTypeService.insertConBookType(conBookType));
    }

    /**
     * 修改流水类型_财务
     */
    @ApiOperation(value = "修改流水类型_财务", notes = "修改流水类型_财务")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:book:type:type:edit")
    @Log(title = "流水类型_财务", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConBookType conBookType) {
        return toAjax(conBookTypeService.updateConBookType(conBookType));
    }

    /**
     * 变更流水类型_财务
     */
    @ApiOperation(value = "变更流水类型_财务", notes = "变更流水类型_财务")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:book:type:type:change")
    @Log(title = "流水类型_财务", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBookType conBookType) {
        return toAjax(conBookTypeService.changeConBookType(conBookType));
    }

    /**
     * 删除流水类型_财务
     */
    @ApiOperation(value = "删除流水类型_财务", notes = "删除流水类型_财务")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:book:type:type:remove")
    @Log(title = "流水类型_财务", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBookTypeService.deleteConBookTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "流水类型_财务", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:con:book:type:type:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBookType conBookType) {
        return AjaxResult.success(conBookTypeService.changeStatus(conBookType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:con:book:type:type:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "流水类型_财务", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBookType conBookType) {
        conBookType.setConfirmDate(new Date());
        conBookType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBookType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBookTypeService.check(conBookType));
    }

    /**
     * 款项类别下拉框列表
     */
    @PostMapping("/getConBookTypeList")
    @ApiOperation(value = "流水类型财务下拉框列表", notes = "流水类型财务下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBookType.class))
    public AjaxResult getConBookTypeList(@RequestBody ConBookType conBookType) {
        conBookType.setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.CHECK_STATUS);
        return AjaxResult.success(conBookTypeService.getConBookTypeList(conBookType));
    }

}
