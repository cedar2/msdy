package com.platform.ems.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.ConCheckStandard;
import com.platform.ems.domain.ConCheckStandardItem;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IConCheckStandardService;
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
 * 检测标准Controller
 *
 * @author qhq
 * @date 2021-11-01
 */
@RestController
@RequestMapping("/check/standard")
@Api(tags = "检测标准")
public class ConCheckStandardController extends BaseController {

    @Autowired
    private IConCheckStandardService conCheckStandardService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询检测标准列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询检测标准列表", notes = "查询检测标准列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCheckStandard.class))
    public TableDataInfo list(@RequestBody ConCheckStandard conCheckStandard) {
        startPage(conCheckStandard);
        List<ConCheckStandard> list = conCheckStandardService.selectConCheckStandardList(conCheckStandard);
        return getDataTable(list);
    }

    /**
     * 导出检测标准列表
     */
    @Log(title = "检测标准", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出检测标准列表", notes = "导出检测标准列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConCheckStandard conCheckStandard) throws IOException {
        List<ConCheckStandard> list = conCheckStandardService.selectConCheckStandardList(conCheckStandard);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConCheckStandard> util = new ExcelUtil<>(ConCheckStandard.class, dataMap);
        util.exportExcel(response, list, "检测标准");
    }


    /**
     * 获取检测标准详细信息
     */
    @ApiOperation(value = "获取检测标准详细信息", notes = "获取检测标准详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCheckStandard.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conCheckStandardService.selectConCheckStandardById(sid));
    }

    /**
     * 新增检测标准
     */
    @ApiOperation(value = "新增检测标准", notes = "新增检测标准")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测标准", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConCheckStandard conCheckStandard) {
        return toAjax(conCheckStandardService.insertConCheckStandard(conCheckStandard));
    }

    /**
     * 修改检测标准
     */
    @ApiOperation(value = "修改检测标准", notes = "修改检测标准")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测标准", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConCheckStandard conCheckStandard) {
        return toAjax(conCheckStandardService.updateConCheckStandard(conCheckStandard));
    }

    /**
     * 变更检测标准
     */
    @ApiOperation(value = "变更检测标准", notes = "变更检测标准")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测标准", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody ConCheckStandard conCheckStandard) {
        return toAjax(conCheckStandardService.changeConCheckStandard(conCheckStandard));
    }

    /**
     * 删除检测标准
     */
    @ApiOperation(value = "删除检测标准", notes = "删除检测标准")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测标准", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conCheckStandardService.deleteConCheckStandardByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测标准", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConCheckStandard conCheckStandard) {
        return AjaxResult.success(conCheckStandardService.changeStatus(conCheckStandard));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测标准", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConCheckStandard conCheckStandard) {
        conCheckStandard.setConfirmDate(new Date());
        conCheckStandard.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conCheckStandard.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conCheckStandardService.check(conCheckStandard));
    }

    @ApiOperation(value = "分配项目", notes = "分配项目")
    @PostMapping("/addStandardItem")
    public AjaxResult addStandardItem(ConCheckStandard conCheckStandard) {
        return toAjax(conCheckStandardService.addStandardItem(conCheckStandard));
    }

    @ApiOperation(value = "分配方法", notes = "分配方法")
    @PostMapping("/addStandardItemMethod")
    public AjaxResult addStandardItemMethod(ConCheckStandardItem conCheckStandardItem) {
        return toAjax(conCheckStandardService.addStandardItemMethod(conCheckStandardItem));
    }
}
