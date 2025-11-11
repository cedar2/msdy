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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.plug.domain.ConBuTypeAccountCategory;
import com.platform.ems.plug.service.IConBuTypeAccountCategoryService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 业务类型对应款项类别Controller
 *
 * @author chenkw
 * @date 2022-06-22
 */
@RestController
@RequestMapping("/con/bu/type/account/category")
@Api(tags = "业务类型对应款项类别")
public class ConBuTypeAccountCategoryController extends BaseController {

    @Autowired
    private IConBuTypeAccountCategoryService conBuTypeAccountCategoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询业务类型对应款项类别列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询业务类型对应款项类别列表", notes = "查询业务类型对应款项类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeAccountCategory.class))
    public TableDataInfo list(@RequestBody ConBuTypeAccountCategory conBuTypeAccountCategory) {
        startPage(conBuTypeAccountCategory);
        List<ConBuTypeAccountCategory> list = conBuTypeAccountCategoryService.selectConBuTypeAccountCategoryList(conBuTypeAccountCategory);
        return getDataTable(list);
    }

    /**
     * 导出业务类型对应款项类别列表
     */
    @Log(title = "业务类型对应款项类别", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出业务类型对应款项类别列表", notes = "导出业务类型对应款项类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBuTypeAccountCategory conBuTypeAccountCategory) throws IOException {
        List<ConBuTypeAccountCategory> list = conBuTypeAccountCategoryService.selectConBuTypeAccountCategoryList(conBuTypeAccountCategory);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBuTypeAccountCategory> util = new ExcelUtil<>(ConBuTypeAccountCategory.class, dataMap);
        util.exportExcel(response, list, "业务类型对应款项类别");
    }


    /**
     * 获取业务类型对应款项类别详细信息
     */
    @ApiOperation(value = "获取业务类型对应款项类别详细信息", notes = "获取业务类型对应款项类别详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeAccountCategory.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBuTypeAccountCategoryService.selectConBuTypeAccountCategoryById(sid));
    }

    /**
     * 新增业务类型对应款项类别
     */
    @ApiOperation(value = "新增业务类型对应款项类别", notes = "新增业务类型对应款项类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型对应款项类别", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBuTypeAccountCategory conBuTypeAccountCategory) {
        return toAjax(conBuTypeAccountCategoryService.insertConBuTypeAccountCategory(conBuTypeAccountCategory));
    }

    /**
     * 修改业务类型对应款项类别
     */
    @ApiOperation(value = "修改业务类型对应款项类别", notes = "修改业务类型对应款项类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型对应款项类别", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConBuTypeAccountCategory conBuTypeAccountCategory) {
        return toAjax(conBuTypeAccountCategoryService.updateConBuTypeAccountCategory(conBuTypeAccountCategory));
    }

    /**
     * 变更业务类型对应款项类别
     */
    @ApiOperation(value = "变更业务类型对应款项类别", notes = "变更业务类型对应款项类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型对应款项类别", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBuTypeAccountCategory conBuTypeAccountCategory) {
        return toAjax(conBuTypeAccountCategoryService.changeConBuTypeAccountCategory(conBuTypeAccountCategory));
    }

    /**
     * 删除业务类型对应款项类别
     */
    @ApiOperation(value = "删除业务类型对应款项类别", notes = "删除业务类型对应款项类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型对应款项类别", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuTypeAccountCategoryService.deleteConBuTypeAccountCategoryByIds(sids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型对应款项类别", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBuTypeAccountCategory conBuTypeAccountCategory) {
        conBuTypeAccountCategory.setConfirmDate(new Date());
        conBuTypeAccountCategory.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBuTypeAccountCategory.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBuTypeAccountCategoryService.check(conBuTypeAccountCategory));
    }

    /**
     * 款项类别下拉接口
     */
    @PostMapping("/getAccountCategoryList")
    @ApiOperation(value = "获取款项类别下拉框接口", notes = "获取款项类别下拉框接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeAccountCategory.class))
    public AjaxResult getAccountCategoryList(@RequestBody ConBuTypeAccountCategory conBuTypeAccountCategory) {
        return AjaxResult.success(conBuTypeAccountCategoryService.getAccountCategoryList(conBuTypeAccountCategory));
    }

    /**
     * 流水类型下拉接口
     */
    @PostMapping("/getBookTypeList")
    @ApiOperation(value = "获取下拉框接口", notes = "获取下拉框接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeAccountCategory.class))
    public AjaxResult getBookTypeList(@RequestBody ConBuTypeAccountCategory conBuTypeAccountCategory) {
        return AjaxResult.success(conBuTypeAccountCategoryService.getBookTypeList(conBuTypeAccountCategory));
    }

    /**
     * 下拉接口
     */
    @PostMapping("/getList")
    @ApiOperation(value = "获取下拉框接口", notes = "获取下拉框接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeAccountCategory.class))
    public AjaxResult getList(@RequestBody ConBuTypeAccountCategory conBuTypeAccountCategory) {
        return AjaxResult.success(conBuTypeAccountCategoryService.getConBuTypeAccountCategoryList(conBuTypeAccountCategory));
    }

}
