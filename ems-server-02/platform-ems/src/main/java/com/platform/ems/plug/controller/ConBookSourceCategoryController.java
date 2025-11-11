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
import com.platform.ems.plug.domain.ConBookSourceCategory;
import com.platform.ems.plug.service.IConBookSourceCategoryService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 流水来源类别_财务Controller
 *
 * @author chenkw
 * @date 2021-08-03
 */
@RestController
@RequestMapping("/con/book/source/category")
@Api(tags = "流水来源类别_财务")
public class ConBookSourceCategoryController extends BaseController {

    @Autowired
    private IConBookSourceCategoryService conBookSourceCategoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询流水来源类别_财务列表
     */
    @PreAuthorize(hasPermi = "ems:con:book:source:category:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询流水来源类别_财务列表", notes = "查询流水来源类别_财务列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBookSourceCategory.class))
    public TableDataInfo list(@RequestBody ConBookSourceCategory conBookSourceCategory) {
        startPage(conBookSourceCategory);
        List<ConBookSourceCategory> list = conBookSourceCategoryService.selectConBookSourceCategoryList(conBookSourceCategory);
        return getDataTable(list);
    }

    /**
     * 导出流水来源类别_财务列表
     */
    @PreAuthorize(hasPermi = "ems:con:book:source:category:export")
    @Log(title = "流水来源类别_财务", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出流水来源类别_财务列表", notes = "导出流水来源类别_财务列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBookSourceCategory conBookSourceCategory) throws IOException {
        List<ConBookSourceCategory> list = conBookSourceCategoryService.selectConBookSourceCategoryList(conBookSourceCategory);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBookSourceCategory> util = new ExcelUtil<>(ConBookSourceCategory.class, dataMap);
        util.exportExcel(response, list, "流水来源类别_财务");
    }


    /**
     * 获取流水来源类别_财务详细信息
     */
    @ApiOperation(value = "获取流水来源类别_财务详细信息", notes = "获取流水来源类别_财务详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBookSourceCategory.class))
    @PreAuthorize(hasPermi = "ems:con:book:source:category:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBookSourceCategoryService.selectConBookSourceCategoryById(sid));
    }

    /**
     * 新增流水来源类别_财务
     */
    @ApiOperation(value = "新增流水来源类别_财务", notes = "新增流水来源类别_财务")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:book:source:category:add")
    @Log(title = "流水来源类别_财务", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBookSourceCategory conBookSourceCategory) {
        return toAjax(conBookSourceCategoryService.insertConBookSourceCategory(conBookSourceCategory));
    }

    /**
     * 修改流水来源类别_财务
     */
    @ApiOperation(value = "修改流水来源类别_财务", notes = "修改流水来源类别_财务")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:book:source:category:edit")
    @Log(title = "流水来源类别_财务", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConBookSourceCategory conBookSourceCategory) {
        return toAjax(conBookSourceCategoryService.updateConBookSourceCategory(conBookSourceCategory));
    }

    /**
     * 变更流水来源类别_财务
     */
    @ApiOperation(value = "变更流水来源类别_财务", notes = "变更流水来源类别_财务")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:book:source:category:change")
    @Log(title = "流水来源类别_财务", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBookSourceCategory conBookSourceCategory) {
        return toAjax(conBookSourceCategoryService.changeConBookSourceCategory(conBookSourceCategory));
    }

    /**
     * 删除流水来源类别_财务
     */
    @ApiOperation(value = "删除流水来源类别_财务", notes = "删除流水来源类别_财务")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:book:source:category:remove")
    @Log(title = "流水来源类别_财务", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBookSourceCategoryService.deleteConBookSourceCategoryByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "流水来源类别_财务", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:con:book:source:category:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBookSourceCategory conBookSourceCategory) {
        return AjaxResult.success(conBookSourceCategoryService.changeStatus(conBookSourceCategory));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:con:book:source:category:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "流水来源类别_财务", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBookSourceCategory conBookSourceCategory) {
        conBookSourceCategory.setConfirmDate(new Date());
        conBookSourceCategory.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBookSourceCategory.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBookSourceCategoryService.check(conBookSourceCategory));
    }

    /**
     * 下拉框列表
     */
    @PostMapping("/getConBookSourceCategoryList")
    @ApiOperation(value = "流水来源类别_财务下拉框列表", notes = "流水来源类别_财务下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBookSourceCategory.class))
    public AjaxResult getConBookSourceCategoryList() {
        return AjaxResult.success(conBookSourceCategoryService.getConBookSourceCategoryList());
    }
}
