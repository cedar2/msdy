package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.plug.domain.ConInOutStockDocCategory;
import com.platform.ems.plug.service.IConInOutStockDocCategoryService;
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

import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 出入库对应的单据类别Controller
 *
 * @author linhongwei
 * @date 2021-06-15
 */
@RestController
@RequestMapping("/inOutStockDocCategory")
@Api(tags = "出入库对应的单据类别")
public class ConInOutStockDocCategoryController extends BaseController {

    @Autowired
    private IConInOutStockDocCategoryService conInOutStockDocCategoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询出入库对应的单据类别列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询出入库对应的单据类别列表", notes = "查询出入库对应的单据类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInOutStockDocCategory.class))
    public TableDataInfo list(@RequestBody ConInOutStockDocCategory conInOutStockDocCategory) {
        startPage(conInOutStockDocCategory);
        List<ConInOutStockDocCategory> list = conInOutStockDocCategoryService.selectConInOutStockDocCategoryList(conInOutStockDocCategory);
        return getDataTable(list);
    }

    /**
     * 出库获取作业类型下拉列表
     */
    @PostMapping("/getlist/moveType")
    @ApiOperation(value = "出库获取作业类型下拉列表", notes = "出库获取作业类型下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInOutStockDocCategory.class))
    public AjaxResult getlist() {

        return AjaxResult.success(conInOutStockDocCategoryService.getList());
    }

    /**
     * 出库获取作业类型下拉单据类型的下拉列表
     */
    @PostMapping("/getlist/category")
    @ApiOperation(value = "出库获取作业类型下拉单据类型的下拉列表", notes = "出库获取作业类型下拉单据类型的下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInOutStockDocCategory.class))
    public AjaxResult getlistCategory(String movementTypeCode) {
        return AjaxResult.success(conInOutStockDocCategoryService.getListCategory(movementTypeCode));
    }

    /**
     * 导出出入库对应的单据类别列表
     */
    @Log(title = "出入库对应的单据类别", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出出入库对应的单据类别列表", notes = "导出出入库对应的单据类别列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConInOutStockDocCategory conInOutStockDocCategory) throws IOException {
        List<ConInOutStockDocCategory> list = conInOutStockDocCategoryService.selectConInOutStockDocCategoryList(conInOutStockDocCategory);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConInOutStockDocCategory> util = new ExcelUtil<>(ConInOutStockDocCategory.class, dataMap);
        util.exportExcel(response, list, "出入库对应的单据类别");
    }

    /**
     * 获取出入库对应的单据类别详细信息
     */
    @ApiOperation(value = "获取出入库对应的单据类别详细信息", notes = "获取出入库对应的单据类别详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInOutStockDocCategory.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conInOutStockDocCategoryService.selectConInOutStockDocCategoryById(sid));
    }

    /**
     * 新增出入库对应的单据类别
     */
    @ApiOperation(value = "新增出入库对应的单据类别", notes = "新增出入库对应的单据类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "出入库对应的单据类别", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConInOutStockDocCategory conInOutStockDocCategory) {
        return toAjax(conInOutStockDocCategoryService.insertConInOutStockDocCategory(conInOutStockDocCategory));
    }

    /**
     * 修改出入库对应的单据类别
     */
    @ApiOperation(value = "修改出入库对应的单据类别", notes = "修改出入库对应的单据类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "出入库对应的单据类别", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConInOutStockDocCategory conInOutStockDocCategory) {
        return toAjax(conInOutStockDocCategoryService.updateConInOutStockDocCategory(conInOutStockDocCategory));
    }

    /**
     * 变更出入库对应的单据类别
     */
    @ApiOperation(value = "变更出入库对应的单据类别", notes = "变更出入库对应的单据类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "出入库对应的单据类别", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConInOutStockDocCategory conInOutStockDocCategory) {
        return toAjax(conInOutStockDocCategoryService.changeConInOutStockDocCategory(conInOutStockDocCategory));
    }

    /**
     * 删除出入库对应的单据类别
     */
    @ApiOperation(value = "删除出入库对应的单据类别", notes = "删除出入库对应的单据类别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "出入库对应的单据类别", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conInOutStockDocCategoryService.deleteConInOutStockDocCategoryByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "出入库对应的单据类别", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConInOutStockDocCategory conInOutStockDocCategory) {
        return AjaxResult.success(conInOutStockDocCategoryService.changeStatus(conInOutStockDocCategory));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "出入库对应的单据类别", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConInOutStockDocCategory conInOutStockDocCategory) {
        conInOutStockDocCategory.setConfirmDate(new Date());
        conInOutStockDocCategory.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conInOutStockDocCategory.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conInOutStockDocCategoryService.check(conInOutStockDocCategory));
    }

    @PostMapping("/getMovementTypeList")
    @ApiOperation(value = "根据库存凭证类别获取关联作业类型", notes = "根据库存凭证类别获取关联作业类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInOutStockDocCategory.class))
    public AjaxResult getMovementTypeList(@RequestBody ConInOutStockDocCategory conInOutStockDocCategory) {
        return AjaxResult.success(conInOutStockDocCategoryService.selectConInOutStockDocCategoryList(conInOutStockDocCategory));
    }
}
