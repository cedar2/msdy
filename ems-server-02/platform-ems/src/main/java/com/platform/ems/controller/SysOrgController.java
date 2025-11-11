package com.platform.ems.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import cn.hutool.core.util.ArrayUtil;
import com.platform.common.core.domain.TreeSelect;
import com.platform.common.core.domain.entity.SysOrg;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.service.ISysOrgService;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 组织架构信息Controller
 *
 * @author qhq
 * @date 2021-03-18
 */
@RestController
@RequestMapping("/organization")
@Api(tags = "组织架构信息")
public class SysOrgController extends BaseController {

    @Autowired
    private ISysOrgService SysOrgService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询组织架构信息列表
     */
    @PreAuthorize(hasPermi = "ems:organization:infor:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询组织架构信息列表", notes = "查询组织架构信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysOrg.class))
    public AjaxResult list(@RequestBody SysOrg sysOrg) {
        startPage(sysOrg);
        List<SysOrg> list = SysOrgService.selectSysOrgList(sysOrg);
        List<SysOrg> treeSelects= SysOrgService.buildTreeSelect(list);
        return AjaxResult.success(treeSelects);
    }

    @PostMapping("/treeselect")
    @ApiOperation(value = "获取组织架构下拉树列表", notes = "获取组织架构下拉树列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TreeSelect.class))
    public AjaxResult getList() {
        List<SysOrg> list = SysOrgService.selectSysOrgList(new SysOrg());
        List<SysOrg> treeSelects= SysOrgService.buildTreeSelect(list);
        return AjaxResult.success(treeSelects.stream().map(TreeSelect::new).collect(Collectors.toList()));
    }

    /**
     * 导出组织架构信息列表
     */
    @PreAuthorize(hasPermi = "ems:organization:infor:export")
    @Log(title = "组织架构信息", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出组织架构信息列表", notes = "导出组织架构信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysOrg sysOrg) throws IOException {
        List<SysOrg> list = SysOrgService.selectSysOrgList(sysOrg);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SysOrg> util = new ExcelUtil<>(SysOrg.class, dataMap);
        util.exportExcel(response, list, "组织架构信息" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取组织架构信息详细信息
     */
    @ApiOperation(value = "获取组织架构信息详细信息", notes = "获取组织架构信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysOrg.class))
    @PreAuthorize(hasPermi = "ems:organization:infor:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long nodeSid) {
        if (nodeSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(SysOrgService.selectSysOrgById(nodeSid));
    }

    /**
     * 新增组织架构信息
     */
    @ApiOperation(value = "新增组织架构信息", notes = "新增组织架构信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:organization:infor:add")
    @Log(title = "组织架构信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SysOrg sysOrg) {
        return toAjax(SysOrgService.insertSysOrg(sysOrg));
    }

    /**
     * 修改组织架构信息
     */
    @ApiOperation(value = "修改组织架构信息", notes = "修改组织架构信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:organization:infor:edit")
    @Log(title = "组织架构信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid SysOrg sysOrg) {
        return toAjax(SysOrgService.updateSysOrg(sysOrg));
    }

    /**
     * 变更组织架构信息
     */
    @ApiOperation(value = "变更组织架构信息", notes = "变更组织架构信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:organization:infor:change")
    @Log(title = "组织架构信息", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid  SysOrg sysOrg) {
        return toAjax(SysOrgService.changeSysOrg(sysOrg));
    }

    /**
     * 删除组织架构信息
     */
    @ApiOperation(value = "删除组织架构信息", notes = "删除组织架构信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:organization:infor:remove")
    @Log(title = "组织架构信息", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> nodeSids) {
        if (ArrayUtil.isEmpty(nodeSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(SysOrgService.deleteSysOrgByIds(nodeSids));
    }

    /**
     * 提示员工在其他地方已存在是否继续创建
     */
    @ApiOperation(value = "提示员工在其他地方已存在是否继续创建", notes = "提示员工在其他地方已存在是否继续创建")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "提示员工在其他地方已存在是否继续创建", businessType = BusinessType.QUERY)
    @PostMapping("/checkStaff")
    public AjaxResult checkStaff(@RequestBody SysOrg sysOrg) {
        return AjaxResult.success(SysOrgService.checkStaff(sysOrg));
    }

}
