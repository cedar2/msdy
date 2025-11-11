package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.ems.domain.dto.response.ConInventoryDocumentCategoryResponse;
import com.platform.ems.plug.domain.ConInventoryDocumentCategory;
import com.platform.ems.plug.service.IConInventoryDocumentCategoryService;
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
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 库存凭证类别Controller
 *
 * @author c
 * @date 2021-07-29
 */
@RestController
@RequestMapping("/document/category")
@Api(tags = "库存凭证类别")
public class ConInventoryDocumentCategoryController extends BaseController {

    @Autowired
    private IConInventoryDocumentCategoryService conInventoryDocumentCategoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询库存凭证类别列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询库存凭证类别列表", notes = "查询库存凭证类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInventoryDocumentCategory.class))
    public TableDataInfo list(@RequestBody ConInventoryDocumentCategory conInventoryDocumentCategory) {
        startPage(conInventoryDocumentCategory);
        List<ConInventoryDocumentCategory> list = conInventoryDocumentCategoryService.selectConInventoryDocumentCategoryList(conInventoryDocumentCategory);
        return getDataTable(list);
    }

    /**
     * 查询库存凭证类别列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "查询库存凭证类别下拉列表", notes = "查询库存凭证类别下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInventoryDocumentCategoryResponse.class))
    public TableDataInfo getList() {
        List<ConInventoryDocumentCategory> list = conInventoryDocumentCategoryService.getList();
        return getDataTable(list, ConInventoryDocumentCategoryResponse::new);
    }

    /**
     * 导出库存凭证类别列表
     */
    @Log(title = "库存凭证类别", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出库存凭证类别列表", notes = "导出库存凭证类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConInventoryDocumentCategory conInventoryDocumentCategory) throws IOException {
        List<ConInventoryDocumentCategory> list = conInventoryDocumentCategoryService.selectConInventoryDocumentCategoryList(conInventoryDocumentCategory);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConInventoryDocumentCategory> util = new ExcelUtil<>(ConInventoryDocumentCategory.class, dataMap);
        util.exportExcel(response, list, "库存凭证类别");
    }


    /**
     * 获取库存凭证类别详细信息
     */
    @ApiOperation(value = "获取库存凭证类别详细信息", notes = "获取库存凭证类别详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInventoryDocumentCategory.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conInventoryDocumentCategoryService.selectConInventoryDocumentCategoryById(sid));
    }

    /**
     * 新增库存凭证类别
     */
    @ApiOperation(value = "新增库存凭证类别", notes = "新增库存凭证类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "库存凭证类别", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConInventoryDocumentCategory conInventoryDocumentCategory) {
        return toAjax(conInventoryDocumentCategoryService.insertConInventoryDocumentCategory(conInventoryDocumentCategory));
    }

    /**
     * 修改库存凭证类别
     */
    @ApiOperation(value = "修改库存凭证类别", notes = "修改库存凭证类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "库存凭证类别", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConInventoryDocumentCategory conInventoryDocumentCategory) {
        return toAjax(conInventoryDocumentCategoryService.updateConInventoryDocumentCategory(conInventoryDocumentCategory));
    }

    /**
     * 变更库存凭证类别
     */
    @ApiOperation(value = "变更库存凭证类别", notes = "变更库存凭证类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "库存凭证类别", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConInventoryDocumentCategory conInventoryDocumentCategory) {
        return toAjax(conInventoryDocumentCategoryService.changeConInventoryDocumentCategory(conInventoryDocumentCategory));
    }

    /**
     * 删除库存凭证类别
     */
    @ApiOperation(value = "删除库存凭证类别", notes = "删除库存凭证类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "库存凭证类别", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conInventoryDocumentCategoryService.deleteConInventoryDocumentCategoryByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "库存凭证类别", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConInventoryDocumentCategory conInventoryDocumentCategory) {
        return AjaxResult.success(conInventoryDocumentCategoryService.changeStatus(conInventoryDocumentCategory));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "库存凭证类别", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConInventoryDocumentCategory conInventoryDocumentCategory) {
        conInventoryDocumentCategory.setConfirmDate(new Date());
        conInventoryDocumentCategory.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conInventoryDocumentCategory.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conInventoryDocumentCategoryService.check(conInventoryDocumentCategory));
    }

    @PostMapping("/getDocumentCategoryList")
    @ApiOperation(value = "库存凭证类别下拉", notes = "库存凭证类别下拉")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInventoryDocumentCategory.class))
    public AjaxResult getDocumentCategoryList(@RequestBody ConInventoryDocumentCategory conInventoryDocumentCategory) {
        return AjaxResult.success(conInventoryDocumentCategoryService.selectConInventoryDocumentCategoryList(conInventoryDocumentCategory));
    }
}
