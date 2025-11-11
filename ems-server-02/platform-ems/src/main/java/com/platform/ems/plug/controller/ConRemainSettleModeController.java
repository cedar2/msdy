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
import com.platform.ems.plug.domain.ConRemainSettleMode;
import com.platform.ems.plug.service.IConRemainSettleModeService;
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
 * 尾款结算方式Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/remain/settle/mode")
@Api(tags = "尾款结算方式")
public class ConRemainSettleModeController extends BaseController {

    @Autowired
    private IConRemainSettleModeService conRemainSettleModeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询尾款结算方式列表
     */
    @PreAuthorize(hasPermi = "ems:remain:settle:mode:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询尾款结算方式列表", notes = "查询尾款结算方式列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConRemainSettleMode.class))
    public TableDataInfo list(@RequestBody ConRemainSettleMode conRemainSettleMode) {
        startPage(conRemainSettleMode);
        List<ConRemainSettleMode> list = conRemainSettleModeService.selectConRemainSettleModeList(conRemainSettleMode);
        return getDataTable(list);
    }

    /**
     * 导出尾款结算方式列表
     */
    @PreAuthorize(hasPermi = "ems:remain:settle:mode:export")
    @Log(title = "尾款结算方式", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出尾款结算方式列表", notes = "导出尾款结算方式列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConRemainSettleMode conRemainSettleMode) throws IOException {
        List<ConRemainSettleMode> list = conRemainSettleModeService.selectConRemainSettleModeList(conRemainSettleMode);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConRemainSettleMode> util = new ExcelUtil<>(ConRemainSettleMode.class, dataMap);
        util.exportExcel(response, list, "尾款结算方式");
    }

    /**
     * 获取尾款结算方式详细信息
     */
    @ApiOperation(value = "获取尾款结算方式详细信息", notes = "获取尾款结算方式详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConRemainSettleMode.class))
    @PreAuthorize(hasPermi = "ems:remain:settle:mode:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conRemainSettleModeService.selectConRemainSettleModeById(sid));
    }

    /**
     * 新增尾款结算方式
     */
    @ApiOperation(value = "新增尾款结算方式", notes = "新增尾款结算方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:remain:settle:mode:add")
    @Log(title = "尾款结算方式", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConRemainSettleMode conRemainSettleMode) {
        return toAjax(conRemainSettleModeService.insertConRemainSettleMode(conRemainSettleMode));
    }

    /**
     * 修改尾款结算方式
     */
    @ApiOperation(value = "修改尾款结算方式", notes = "修改尾款结算方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:remain:settle:mode:edit")
    @Log(title = "尾款结算方式", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConRemainSettleMode conRemainSettleMode) {
        return toAjax(conRemainSettleModeService.updateConRemainSettleMode(conRemainSettleMode));
    }

    /**
     * 变更尾款结算方式
     */
    @ApiOperation(value = "变更尾款结算方式", notes = "变更尾款结算方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:remain:settle:mode:change")
    @Log(title = "尾款结算方式", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConRemainSettleMode conRemainSettleMode) {
        return toAjax(conRemainSettleModeService.changeConRemainSettleMode(conRemainSettleMode));
    }

    /**
     * 删除尾款结算方式
     */
    @ApiOperation(value = "删除尾款结算方式", notes = "删除尾款结算方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:remain:settle:mode:remove")
    @Log(title = "尾款结算方式", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conRemainSettleModeService.deleteConRemainSettleModeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "尾款结算方式", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:remain:settle:mode:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConRemainSettleMode conRemainSettleMode) {
        return AjaxResult.success(conRemainSettleModeService.changeStatus(conRemainSettleMode));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:remain:settle:mode:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "尾款结算方式", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConRemainSettleMode conRemainSettleMode) {
        conRemainSettleMode.setConfirmDate(new Date());
        conRemainSettleMode.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conRemainSettleMode.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conRemainSettleModeService.check(conRemainSettleMode));
    }

    @PostMapping("/getConRemainSettleModeList")
    @ApiOperation(value = "尾款结算方式下拉列表", notes = "尾款结算方式下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConRemainSettleMode.class))
    public AjaxResult getConRemainSettleModeList() {
        return AjaxResult.success(conRemainSettleModeService.getConRemainSettleModeList());
    }
}
