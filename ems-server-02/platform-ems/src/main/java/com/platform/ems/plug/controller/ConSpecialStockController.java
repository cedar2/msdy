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
import com.platform.ems.plug.domain.ConSpecialStock;
import com.platform.ems.plug.service.IConSpecialStockService;
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
 * 特殊库存Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/special/stock")
@Api(tags = "特殊库存")
public class ConSpecialStockController extends BaseController {

    @Autowired
    private IConSpecialStockService conSpecialStockService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询特殊库存列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询特殊库存列表", notes = "查询特殊库存列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSpecialStock.class))
    public TableDataInfo list(@RequestBody ConSpecialStock conSpecialStock) {
        startPage(conSpecialStock);
        List<ConSpecialStock> list = conSpecialStockService.selectConSpecialStockList(conSpecialStock);
        return getDataTable(list);
    }

    /**
     * 导出特殊库存列表
     */
    @Log(title = "特殊库存", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出特殊库存列表", notes = "导出特殊库存列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConSpecialStock conSpecialStock) throws IOException {
        List<ConSpecialStock> list = conSpecialStockService.selectConSpecialStockList(conSpecialStock);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConSpecialStock> util = new ExcelUtil<>(ConSpecialStock.class, dataMap);
        util.exportExcel(response, list, "特殊库存");
    }

    /**
     * 获取特殊库存详细信息
     */
    @ApiOperation(value = "获取特殊库存详细信息", notes = "获取特殊库存详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSpecialStock.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conSpecialStockService.selectConSpecialStockById(sid));
    }

    /**
     * 新增特殊库存
     */
    @ApiOperation(value = "新增特殊库存", notes = "新增特殊库存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "特殊库存", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConSpecialStock conSpecialStock) {
        return toAjax(conSpecialStockService.insertConSpecialStock(conSpecialStock));
    }

    /**
     * 修改特殊库存
     */
    @ApiOperation(value = "修改特殊库存", notes = "修改特殊库存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "特殊库存", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConSpecialStock conSpecialStock) {
        return toAjax(conSpecialStockService.updateConSpecialStock(conSpecialStock));
    }

    /**
     * 变更特殊库存
     */
    @ApiOperation(value = "变更特殊库存", notes = "变更特殊库存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "特殊库存", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConSpecialStock conSpecialStock) {
        return toAjax(conSpecialStockService.changeConSpecialStock(conSpecialStock));
    }

    /**
     * 删除特殊库存
     */
    @ApiOperation(value = "删除特殊库存", notes = "删除特殊库存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "特殊库存", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conSpecialStockService.deleteConSpecialStockByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "特殊库存", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConSpecialStock conSpecialStock) {
        return AjaxResult.success(conSpecialStockService.changeStatus(conSpecialStock));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "特殊库存", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConSpecialStock conSpecialStock) {
        conSpecialStock.setConfirmDate(new Date());
        conSpecialStock.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conSpecialStock.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conSpecialStockService.check(conSpecialStock));
    }

    /**
     * 获取特殊库存列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "查询特殊库存列表", notes = "查询特殊库存列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSpecialStock.class))
    public AjaxResult getList() {
        return AjaxResult.success(conSpecialStockService.getList());
    }

}
