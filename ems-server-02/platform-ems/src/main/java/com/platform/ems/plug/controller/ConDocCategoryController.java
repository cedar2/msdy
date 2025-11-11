package com.platform.ems.plug.controller;

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
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.plug.domain.ConDocCategory;
import com.platform.ems.plug.service.IConDocCategoryService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 单据类别Controller
 *
 * @author chenkw
 * @date 2021-08-02
 */
@RestController
@RequestMapping("/con/doc/category")
@Api(tags = "单据类别")
public class ConDocCategoryController extends BaseController {

    @Autowired
    private IConDocCategoryService conDocCategoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询单据类别列表
     */
    @PreAuthorize(hasPermi = "ems:con:doc:category:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类别列表", notes = "查询单据类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocCategory.class))
    public TableDataInfo list(@RequestBody ConDocCategory conDocCategory) {
        startPage(conDocCategory);
        List<ConDocCategory> list = conDocCategoryService.selectConDocCategoryList(conDocCategory);
        return getDataTable(list);
    }

    /**
     * 导出单据类别列表
     */
    @PreAuthorize(hasPermi = "ems:con:doc:category:export")
    @Log(title = "单据类别", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出单据类别列表", notes = "导出单据类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocCategory conDocCategory) throws IOException {
        List<ConDocCategory> list = conDocCategoryService.selectConDocCategoryList(conDocCategory);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDocCategory> util = new ExcelUtil<>(ConDocCategory.class, dataMap);
        util.exportExcel(response, list, "单据类别");
    }


    /**
     * 获取单据类别详细信息
     */
    @ApiOperation(value = "获取单据类别详细信息", notes = "获取单据类别详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocCategory.class))
    @PreAuthorize(hasPermi = "ems:con:doc:category:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocCategoryService.selectConDocCategoryById(sid));
    }

    /**
     * 新增单据类别
     */
    @ApiOperation(value = "新增单据类别", notes = "新增单据类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:doc:category:add")
    @Log(title = "单据类别", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocCategory conDocCategory) {
        return toAjax(conDocCategoryService.insertConDocCategory(conDocCategory));
    }

    /**
     * 修改单据类别
     */
    @ApiOperation(value = "修改单据类别", notes = "修改单据类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:doc:category:edit")
    @Log(title = "单据类别", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDocCategory conDocCategory) {
        return toAjax(conDocCategoryService.updateConDocCategory(conDocCategory));
    }

    /**
     * 变更单据类别
     */
    @ApiOperation(value = "变更单据类别", notes = "变更单据类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:doc:category:change")
    @Log(title = "单据类别", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocCategory conDocCategory) {
        return toAjax(conDocCategoryService.changeConDocCategory(conDocCategory));
    }

    /**
     * 删除单据类别
     */
    @ApiOperation(value = "删除单据类别", notes = "删除单据类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:doc:category:remove")
    @Log(title = "单据类别", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocCategoryService.deleteConDocCategoryByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类别", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:con:doc:category:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocCategory conDocCategory) {
        return AjaxResult.success(conDocCategoryService.changeStatus(conDocCategory));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:con:doc:category:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类别", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocCategory conDocCategory) {
        conDocCategory.setConfirmDate(new Date());
        conDocCategory.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocCategory.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocCategoryService.check(conDocCategory));
    }

    @PostMapping("/getConDocCategoryList")
    @ApiOperation(value = "单据类别下拉列表", notes = "单据类别下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocCategory.class))
    public AjaxResult getConDocCategoryList() {
        return AjaxResult.success(conDocCategoryService.getConDocCategoryList());
    }

}
