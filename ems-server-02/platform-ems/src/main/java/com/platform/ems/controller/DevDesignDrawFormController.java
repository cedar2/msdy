package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.DevDesignDrawForm;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IDevDesignDrawFormService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 图稿批复单Controller
 *
 * @author qhq
 * @date 2021-11-05
 */
@RestController
@RequestMapping("/design/draw/form")
@Api(tags = "图稿批复单")
public class DevDesignDrawFormController extends BaseController {

    @Autowired
    private IDevDesignDrawFormService devDesignDrawFormService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询图稿批复单列表
     */
//    @PreAuthorize(hasPermi = "ems:form:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询图稿批复单列表", notes = "查询图稿批复单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevDesignDrawForm.class))
    public TableDataInfo list(@RequestBody DevDesignDrawForm devDesignDrawForm) {
        startPage(devDesignDrawForm);
        List<DevDesignDrawForm> list = devDesignDrawFormService.selectDevDesignDrawFormList(devDesignDrawForm);
        return getDataTable(list);
    }

    /**
     * 导出图稿批复单列表
     */
//    @PreAuthorize(hasPermi = "ems:form:export")
    @Log(title = "图稿批复单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出图稿批复单列表", notes = "导出图稿批复单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, DevDesignDrawForm devDesignDrawForm) throws IOException {
        List<DevDesignDrawForm> list = devDesignDrawFormService.selectDevDesignDrawFormList(devDesignDrawForm);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<DevDesignDrawForm> util = new ExcelUtil<DevDesignDrawForm>(DevDesignDrawForm.class, dataMap);
        util.exportExcel(response, list, "图稿批复单");
    }


    /**
     * 获取图稿批复单详细信息
     */
    @ApiOperation(value = "获取图稿批复单详细信息", notes = "获取图稿批复单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevDesignDrawForm.class))
//    @PreAuthorize(hasPermi = "ems:form:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long designDrawFormSid) {
        if (designDrawFormSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(devDesignDrawFormService.selectDevDesignDrawFormById(designDrawFormSid));
    }

    /**
     * 新增图稿批复单
     */
    @ApiOperation(value = "新增图稿批复单", notes = "新增图稿批复单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:form:add")
    @Log(title = "图稿批复单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid DevDesignDrawForm devDesignDrawForm) {
        return devDesignDrawFormService.insertDevDesignDrawForm(devDesignDrawForm);
    }

    /**
     * 修改图稿批复单
     */
    @ApiOperation(value = "修改图稿批复单", notes = "修改图稿批复单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:form:edit")
    @Log(title = "图稿批复单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody DevDesignDrawForm devDesignDrawForm) {
        return toAjax(devDesignDrawFormService.updateDevDesignDrawForm(devDesignDrawForm));
    }

    /**
     * 变更图稿批复单
     */
    @ApiOperation(value = "变更图稿批复单", notes = "变更图稿批复单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:form:change")
    @Log(title = "图稿批复单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody DevDesignDrawForm devDesignDrawForm) {
        return toAjax(devDesignDrawFormService.changeDevDesignDrawForm(devDesignDrawForm));
    }

    /**
     * 删除图稿批复单
     */
    @ApiOperation(value = "删除图稿批复单", notes = "删除图稿批复单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:form:remove")
    @Log(title = "图稿批复单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> designDrawFormSids) {
        if (CollectionUtils.isEmpty(designDrawFormSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(devDesignDrawFormService.deleteDevDesignDrawFormByIds(designDrawFormSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
//    @PreAuthorize(hasPermi = "ems:form:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "图稿批复单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody DevDesignDrawForm devDesignDrawForm) {
        devDesignDrawForm.setConfirmDate(new Date());
        devDesignDrawForm.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        devDesignDrawForm.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(devDesignDrawFormService.check(devDesignDrawForm));
    }

    /**
     * 是否已创建图稿批复
     */
    @ApiOperation(value = "是否创建图稿批复", notes = "是否创建图稿批复")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevDesignDrawForm.class))
    @PostMapping("/verify")
    public AjaxResult verify(Long productSid) {
        if (productSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(devDesignDrawFormService.verify(productSid));
    }

}
