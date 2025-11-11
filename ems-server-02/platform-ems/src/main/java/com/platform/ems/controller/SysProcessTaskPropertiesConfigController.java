package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ISysProcessTaskPropertiesConfigService;
import com.platform.system.domain.SysProcessTaskPropertiesConfig;
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
import java.util.List;
import java.util.Map;

/**
 * 流程节点属性配置Controller
 *
 * @author qhq
 * @date 2021-10-11
 */
@RestController
@RequestMapping("/task/properties/config")
@Api(tags = "流程节点属性配置")
public class SysProcessTaskPropertiesConfigController extends BaseController {

    @Autowired
    private ISysProcessTaskPropertiesConfigService sysProcessTaskPropertiesConfigService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询【请填写功能名称】列表
     */
    @PreAuthorize(hasPermi = "ems:config:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询【请填写功能名称】列表", notes = "查询【请填写功能名称】列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysProcessTaskPropertiesConfig.class))
    public TableDataInfo list(@RequestBody SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig) {
        startPage(sysProcessTaskPropertiesConfig);
        List<SysProcessTaskPropertiesConfig> list = sysProcessTaskPropertiesConfigService.selectSysProcessTaskPropertiesConfigList(sysProcessTaskPropertiesConfig);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @PreAuthorize(hasPermi = "ems:config:export")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出【请填写功能名称】列表", notes = "导出【请填写功能名称】列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig) throws IOException {
        List<SysProcessTaskPropertiesConfig> list = sysProcessTaskPropertiesConfigService.selectSysProcessTaskPropertiesConfigList(sysProcessTaskPropertiesConfig);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<SysProcessTaskPropertiesConfig> util = new ExcelUtil<SysProcessTaskPropertiesConfig>(SysProcessTaskPropertiesConfig.class,dataMap);
        util.exportExcel(response, list, "【请填写功能名称】"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取【请填写功能名称】详细信息
     */
    @ApiOperation(value = "获取【请填写功能名称】详细信息", notes = "获取【请填写功能名称】详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysProcessTaskPropertiesConfig.class))
    @PreAuthorize(hasPermi = "ems:config:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long id) {
                    if(id==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(sysProcessTaskPropertiesConfigService.selectSysProcessTaskPropertiesConfigById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
    @ApiOperation(value = "新增【请填写功能名称】", notes = "新增【请填写功能名称】")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:config:add")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig) {
        return toAjax(sysProcessTaskPropertiesConfigService.insertSysProcessTaskPropertiesConfig(sysProcessTaskPropertiesConfig));
    }

    /**
     * 修改【请填写功能名称】
     */
    @ApiOperation(value = "修改【请填写功能名称】", notes = "修改【请填写功能名称】")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:config:edit")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig) {
        return toAjax(sysProcessTaskPropertiesConfigService.updateSysProcessTaskPropertiesConfig(sysProcessTaskPropertiesConfig));
    }

    /**
     * 变更【请填写功能名称】
     */
    @ApiOperation(value = "变更【请填写功能名称】", notes = "变更【请填写功能名称】")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:config:change")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig) {
        return toAjax(sysProcessTaskPropertiesConfigService.changeSysProcessTaskPropertiesConfig(sysProcessTaskPropertiesConfig));
    }

    /**
     * 删除【请填写功能名称】
     */
    @ApiOperation(value = "删除【请填写功能名称】", notes = "删除【请填写功能名称】")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:config:remove")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  ids) {
        if(CollectionUtils.isEmpty( ids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysProcessTaskPropertiesConfigService.deleteSysProcessTaskPropertiesConfigByIds(ids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:config:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig) {
        return AjaxResult.success(sysProcessTaskPropertiesConfigService.changeStatus(sysProcessTaskPropertiesConfig));
    }

}
