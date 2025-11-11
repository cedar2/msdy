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
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.ConCheckMethod;
import com.platform.ems.service.IConCheckMethodService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 检测方法Controller
 *
 * @author qhq
 * @date 2021-11-01
 */
@RestController
@RequestMapping("/check/method")
@Api(tags = "检测方法")
public class ConCheckMethodController extends BaseController {

    @Autowired
    private IConCheckMethodService conCheckMethodService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询检测方法列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询检测方法列表", notes = "查询检测方法列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCheckMethod.class))
    public TableDataInfo list(@RequestBody ConCheckMethod conCheckMethod) {
        startPage(conCheckMethod);
        List<ConCheckMethod> list = conCheckMethodService.selectConCheckMethodList(conCheckMethod);
        return getDataTable(list);
    }

    /**
     * 导出检测方法列表
     */
    @Log(title = "检测方法", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出检测方法列表", notes = "导出检测方法列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConCheckMethod conCheckMethod) throws IOException {
        List<ConCheckMethod> list = conCheckMethodService.selectConCheckMethodList(conCheckMethod);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConCheckMethod> util = new ExcelUtil<>(ConCheckMethod.class, dataMap);
        util.exportExcel(response, list, "检测方法");
    }


    /**
     * 获取检测方法详细信息
     */
    @ApiOperation(value = "获取检测方法详细信息", notes = "获取检测方法详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCheckMethod.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conCheckMethodService.selectConCheckMethodById(sid));
    }

    /**
     * 新增检测方法
     */
    @ApiOperation(value = "新增检测方法", notes = "新增检测方法")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测方法", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConCheckMethod conCheckMethod) {
        return toAjax(conCheckMethodService.insertConCheckMethod(conCheckMethod));
    }

    /**
     * 修改检测方法
     */
    @ApiOperation(value = "修改检测方法", notes = "修改检测方法")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测方法", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConCheckMethod conCheckMethod) {
        return toAjax(conCheckMethodService.updateConCheckMethod(conCheckMethod));
    }

    /**
     * 变更检测方法
     */
    @ApiOperation(value = "变更检测方法", notes = "变更检测方法")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测方法", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody ConCheckMethod conCheckMethod) {
        return toAjax(conCheckMethodService.changeConCheckMethod(conCheckMethod));
    }

    /**
     * 删除检测方法
     */
    @ApiOperation(value = "删除检测方法", notes = "删除检测方法")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测方法", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conCheckMethodService.deleteConCheckMethodByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测方法", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConCheckMethod conCheckMethod) {
        return AjaxResult.success(conCheckMethodService.changeStatus(conCheckMethod));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测方法", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConCheckMethod conCheckMethod) {
        conCheckMethod.setConfirmDate(new Date());
        conCheckMethod.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conCheckMethod.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conCheckMethodService.check(conCheckMethod));
    }

}
