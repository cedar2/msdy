package com.platform.ems.plug.controller;

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
import com.platform.ems.plug.domain.ConBcstUserConfig;
import com.platform.ems.plug.service.IConBcstUserConfigService;
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
 * 通知用户配置Controller
 *
 * @author linhongwei
 * @date 2021-10-12
 */
@RestController
@RequestMapping("/bcst/user/config")
@Api(tags = "通知用户配置")
public class ConBcstUserConfigController extends BaseController {

    @Autowired
    private IConBcstUserConfigService conBcstUserConfigService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询通知用户配置列表
     */
    @PreAuthorize(hasPermi = "ems:bcst:user:config:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询通知用户配置列表", notes = "查询通知用户配置列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBcstUserConfig.class))
    public TableDataInfo list(@RequestBody ConBcstUserConfig conBcstUserConfig) {
        startPage(conBcstUserConfig);
        List<ConBcstUserConfig> list = conBcstUserConfigService.selectConBcstUserConfigList(conBcstUserConfig);
        return getDataTable(list);
    }

    /**
     * 导出通知用户配置列表
     */
    @PreAuthorize(hasPermi = "ems:bcst:user:config:export")
    @Log(title = "通知用户配置", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出通知用户配置列表", notes = "导出通知用户配置列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBcstUserConfig conBcstUserConfig) throws IOException {
        List<ConBcstUserConfig> list = conBcstUserConfigService.selectConBcstUserConfigList(conBcstUserConfig);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBcstUserConfig> util = new ExcelUtil<>(ConBcstUserConfig.class, dataMap);
        util.exportExcel(response, list, "通知用户配置");
    }


    /**
     * 获取通知用户配置详细信息
     */
    @ApiOperation(value = "获取通知用户配置详细信息", notes = "获取通知用户配置详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBcstUserConfig.class))
    @PreAuthorize(hasPermi = "ems:bcst:user:config:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBcstUserConfigService.selectConBcstUserConfigById(sid));
    }

    /**
     * 新增通知用户配置
     */
    @ApiOperation(value = "新增通知用户配置", notes = "新增通知用户配置")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bcst:user:config:add")
    @Log(title = "通知用户配置", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBcstUserConfig conBcstUserConfig) {
        return toAjax(conBcstUserConfigService.insertConBcstUserConfig(conBcstUserConfig));
    }

    /**
     * 修改通知用户配置
     */
    @ApiOperation(value = "修改通知用户配置", notes = "修改通知用户配置")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bcst:user:config:edit")
    @Log(title = "通知用户配置", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConBcstUserConfig conBcstUserConfig) {
        return toAjax(conBcstUserConfigService.updateConBcstUserConfig(conBcstUserConfig));
    }

    /**
     * 变更通知用户配置
     */
    @ApiOperation(value = "变更通知用户配置", notes = "变更通知用户配置")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bcst:user:config:change")
    @Log(title = "通知用户配置", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBcstUserConfig conBcstUserConfig) {
        return toAjax(conBcstUserConfigService.changeConBcstUserConfig(conBcstUserConfig));
    }

    /**
     * 删除通知用户配置
     */
    @ApiOperation(value = "删除通知用户配置", notes = "删除通知用户配置")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bcst:user:config:remove")
    @Log(title = "通知用户配置", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBcstUserConfigService.deleteConBcstUserConfigByIds(sids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:bcst:user:config:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "通知用户配置", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBcstUserConfig conBcstUserConfig) {
        conBcstUserConfig.setConfirmDate(new Date());
        conBcstUserConfig.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBcstUserConfig.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBcstUserConfigService.check(conBcstUserConfig));
    }

}
