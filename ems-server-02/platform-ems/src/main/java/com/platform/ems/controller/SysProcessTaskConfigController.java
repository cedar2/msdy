package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ISysProcessTaskConfigService;
import com.platform.system.domain.SysProcessTaskConfig;
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
 * 流程任务节点个性化配置参数Controller
 *
 * @author qhq
 * @date 2021-10-11
 */
@RestController
@RequestMapping("task/config")
@Api(tags = "流程任务节点个性化配置参数")
public class SysProcessTaskConfigController extends BaseController {

    @Autowired
    private ISysProcessTaskConfigService sysProcessTaskConfigService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询流程任务节点个性化配置参数列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询流程任务节点个性化配置参数列表", notes = "查询流程任务节点个性化配置参数列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysProcessTaskConfig.class))
    public TableDataInfo list(@RequestBody SysProcessTaskConfig sysProcessTaskConfig) {
        startPage(sysProcessTaskConfig);
        List<SysProcessTaskConfig> list = sysProcessTaskConfigService.selectSysProcessTaskConfigList(sysProcessTaskConfig);
        return getDataTable(list);
    }

    /**
     * 导出流程任务节点个性化配置参数列表
     */
    @ApiOperation(value = "导出流程任务节点个性化配置参数列表", notes = "导出流程任务节点个性化配置参数列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysProcessTaskConfig sysProcessTaskConfig) throws IOException {
        List<SysProcessTaskConfig> list = sysProcessTaskConfigService.selectSysProcessTaskConfigList(sysProcessTaskConfig);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<SysProcessTaskConfig> util = new ExcelUtil<SysProcessTaskConfig>(SysProcessTaskConfig.class,dataMap);
        util.exportExcel(response, list, "流程任务节点个性化配置参数"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取流程任务节点个性化配置参数详细信息
     */
    @ApiOperation(value = "获取流程任务节点个性化配置参数详细信息", notes = "获取流程任务节点个性化配置参数详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysProcessTaskConfig.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long id) {
                    if(id==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(sysProcessTaskConfigService.selectSysProcessTaskConfigById(id));
    }

    /**
     * 新增流程任务节点个性化配置参数
     */
    @ApiOperation(value = "新增流程任务节点个性化配置参数", notes = "新增流程任务节点个性化配置参数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SysProcessTaskConfig sysProcessTaskConfig) {
        return toAjax(sysProcessTaskConfigService.insertSysProcessTaskConfig(sysProcessTaskConfig));
    }

    /**
     * 修改流程任务节点个性化配置参数
     */
    @ApiOperation(value = "修改流程任务节点个性化配置参数", notes = "修改流程任务节点个性化配置参数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SysProcessTaskConfig sysProcessTaskConfig) {
        return toAjax(sysProcessTaskConfigService.updateSysProcessTaskConfig(sysProcessTaskConfig));
    }

    /**
     * 变更流程任务节点个性化配置参数
     */
    @ApiOperation(value = "变更流程任务节点个性化配置参数", notes = "变更流程任务节点个性化配置参数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody SysProcessTaskConfig sysProcessTaskConfig) {
        return toAjax(sysProcessTaskConfigService.changeSysProcessTaskConfig(sysProcessTaskConfig));
    }

    /**
     * 删除流程任务节点个性化配置参数
     */
    @ApiOperation(value = "删除流程任务节点个性化配置参数", notes = "删除流程任务节点个性化配置参数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  ids) {
        if(CollectionUtils.isEmpty( ids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysProcessTaskConfigService.deleteSysProcessTaskConfigByIds(ids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysProcessTaskConfig sysProcessTaskConfig) {
        return AjaxResult.success(sysProcessTaskConfigService.changeStatus(sysProcessTaskConfig));
    }
}
