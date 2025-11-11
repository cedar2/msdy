package com.platform.ems.plug.controller;

import cn.hutool.core.util.ArrayUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConAccountCategory;
import com.platform.ems.plug.service.IConAccountCategoryService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
 * 款项类别Controller
 *
 * @author linhongwei
 * @date 2021-06-22
 */
@RestController
@RequestMapping("/account/category")
@Api(tags = "款项类别")
public class ConAccountCategoryController extends BaseController {

    @Autowired
    private IConAccountCategoryService conAccountCategoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询款项类别列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询款项类别列表", notes = "查询款项类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConAccountCategory.class))
    public TableDataInfo list(@RequestBody ConAccountCategory conAccountCategory) {
        startPage(conAccountCategory);
        List<ConAccountCategory> list = conAccountCategoryService.selectConAccountCategoryList(conAccountCategory);
        return getDataTable(list);
    }

    /**
     * 导出款项类别列表
     */
    @ApiOperation(value = "导出款项类别列表", notes = "导出款项类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConAccountCategory conAccountCategory) throws IOException {
        List<ConAccountCategory> list = conAccountCategoryService.selectConAccountCategoryList(conAccountCategory);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConAccountCategory> util = new ExcelUtil<>(ConAccountCategory.class, dataMap);
        util.exportExcel(response, list, "款项类别");
    }

    /**
     * 获取款项类别详细信息
     */
    @ApiOperation(value = "获取款项类别详细信息", notes = "获取款项类别详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConAccountCategory.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conAccountCategoryService.selectConAccountCategoryById(sid));
    }

    /**
     * 新增款项类别
     */
    @ApiOperation(value = "新增款项类别", notes = "新增款项类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConAccountCategory conAccountCategory) {
        return toAjax(conAccountCategoryService.insertConAccountCategory(conAccountCategory));
    }

    /**
     * 修改款项类别
     */
    @ApiOperation(value = "修改款项类别", notes = "修改款项类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConAccountCategory conAccountCategory) {
        return toAjax(conAccountCategoryService.updateConAccountCategory(conAccountCategory));
    }

    /**
     * 变更款项类别
     */
    @ApiOperation(value = "变更款项类别", notes = "变更款项类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConAccountCategory conAccountCategory) {
        return toAjax(conAccountCategoryService.changeConAccountCategory(conAccountCategory));
    }

    /**
     * 删除款项类别
     */
    @ApiOperation(value = "删除款项类别", notes = "删除款项类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conAccountCategoryService.deleteConAccountCategoryByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConAccountCategory conAccountCategory) {
        return AjaxResult.success(conAccountCategoryService.changeStatus(conAccountCategory));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConAccountCategory conAccountCategory) {
        conAccountCategory.setConfirmDate(new Date());
        conAccountCategory.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conAccountCategory.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conAccountCategoryService.check(conAccountCategory));
    }

    /**
     * 款项类别下拉框列表
     */
    @PostMapping("/getConAccountCategoryList")
    @ApiOperation(value = "款项类别下拉框列表", notes = "款项类别下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConAccountCategory.class))
    public AjaxResult getConAccountCategoryList() {
        return AjaxResult.success(conAccountCategoryService.getConAccountCategoryList());
    }
}
