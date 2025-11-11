package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.platform.common.annotation.Idempotent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.plug.domain.ConPlantJixinliangEnterMode;
import com.platform.ems.plug.service.IConPlantJixinliangEnterModeService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 工厂计薪量录入方式Controller
 *
 * @author zhuangyz
 * @date 2022-07-14
 */
@RestController
@RequestMapping("/con/plant/jixinliang/enter/mode")
@Api(tags = "工厂计薪量录入方式")
public class ConPlantJixinliangEnterModeController extends BaseController {

    @Autowired
    private IConPlantJixinliangEnterModeService conPlantJixinliangEnterModeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询工厂计薪量录入方式列表
     */
    @PreAuthorize(hasPermi = "ems:con:plant:jixinliang:enter:mode:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询工厂计薪量录入方式列表", notes = "查询工厂计薪量录入方式列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPlantJixinliangEnterMode.class))
    public TableDataInfo list(@RequestBody ConPlantJixinliangEnterMode conPlantJixinliangEnterMode) {
        startPage(conPlantJixinliangEnterMode);
        List<ConPlantJixinliangEnterMode> list = conPlantJixinliangEnterModeService.selectConPlantJixinliangEnterModeList(conPlantJixinliangEnterMode);
        return getDataTable(list);
    }

    /**
     * 导出工厂计薪量录入方式列表
     */
    @PreAuthorize(hasPermi = "ems:con:plant:jixinliang:enter:mode:export")
    @Log(title = "工厂计薪量录入方式", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出工厂计薪量录入方式列表", notes = "导出工厂计薪量录入方式列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConPlantJixinliangEnterMode conPlantJixinliangEnterMode) throws IOException {
        List<ConPlantJixinliangEnterMode> list = conPlantJixinliangEnterModeService.selectConPlantJixinliangEnterModeList(conPlantJixinliangEnterMode);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConPlantJixinliangEnterMode> util = new ExcelUtil<>(ConPlantJixinliangEnterMode.class, dataMap);
        util.exportExcel(response, list, "工厂计薪量录入方式");



    }


    /**
     * 获取工厂计薪量录入方式详细信息
     */
    @ApiOperation(value = "获取工厂计薪量录入方式详细信息", notes = "获取工厂计薪量录入方式详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPlantJixinliangEnterMode.class))
    @PreAuthorize(hasPermi = "ems:con:plant:jixinliang:enter:mode:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conPlantJixinliangEnterModeService.selectConPlantJixinliangEnterModeById(sid));
    }

    /**
     * 新增工厂计薪量录入方式
     */
    @ApiOperation(value = "新增工厂计薪量录入方式", notes = "新增工厂计薪量录入方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:plant:jixinliang:enter:mode:add")
    @Log(title = "工厂计薪量录入方式", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid ConPlantJixinliangEnterMode conPlantJixinliangEnterMode) {
        return toAjax(conPlantJixinliangEnterModeService.insertConPlantJixinliangEnterMode(conPlantJixinliangEnterMode));
    }

    @ApiOperation(value = "修改工厂计薪量录入方式", notes = "修改工厂计薪量录入方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:plant:jixinliang:enter:mode:edit")
    @Log(title = "工厂计薪量录入方式", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody ConPlantJixinliangEnterMode conPlantJixinliangEnterMode) {
        return toAjax(conPlantJixinliangEnterModeService.updateConPlantJixinliangEnterMode(conPlantJixinliangEnterMode));
    }

    /**
     * 变更工厂计薪量录入方式
     */
    @ApiOperation(value = "变更工厂计薪量录入方式", notes = "变更工厂计薪量录入方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:plant:jixinliang:enter:mode:change")
    @Log(title = "工厂计薪量录入方式", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConPlantJixinliangEnterMode conPlantJixinliangEnterMode) {
        return toAjax(conPlantJixinliangEnterModeService.changeConPlantJixinliangEnterMode(conPlantJixinliangEnterMode));
    }

    /**
     * 删除工厂计薪量录入方式
     */
    @ApiOperation(value = "删除工厂计薪量录入方式", notes = "删除工厂计薪量录入方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:plant:jixinliang:enter:mode:remove")
    @Log(title = "工厂计薪量录入方式", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conPlantJixinliangEnterModeService.deleteConPlantJixinliangEnterModeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "工厂计薪量录入方式", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:con:plant:jixinliang:enter:mode:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConPlantJixinliangEnterMode conPlantJixinliangEnterMode) {
        return AjaxResult.success(conPlantJixinliangEnterModeService.changeStatus(conPlantJixinliangEnterMode));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:con:plant:jixinliang:enter:mode:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "工厂计薪量录入方式", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody ConPlantJixinliangEnterMode conPlantJixinliangEnterMode) {
        return toAjax(conPlantJixinliangEnterModeService.check(conPlantJixinliangEnterMode));
    }

}
