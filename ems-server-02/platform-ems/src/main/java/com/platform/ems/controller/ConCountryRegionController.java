package com.platform.ems.controller;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.core.domain.TreeSelect;
import com.platform.common.core.domain.entity.ConCountryRegion;
import com.platform.ems.util.BuildTreeService;
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
import com.platform.ems.service.IConCountryRegionService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;

/**
 * 国家区域Controller
 *
 * @author linhongwei
 * @date 2021-06-25
 */
@RestController
@RequestMapping("/con/country/region")
@Api(tags = "国家区域")
public class ConCountryRegionController extends BaseController {

    @Autowired
    private IConCountryRegionService conCountryRegionService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询国家区域列表
     */
    @PreAuthorize(hasPermi = "ems:con:country:region:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询国家区域列表", notes = "查询国家区域列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCountryRegion.class))
    public AjaxResult list(@RequestBody ConCountryRegion conCountryRegion) {
        startPage(conCountryRegion);
        List<ConCountryRegion> list = conCountryRegionService.selectConCountryRegionList(conCountryRegion);
        BuildTreeService<ConCountryRegion> buildTreeService=new BuildTreeService<>("countryRegionSid", "parentCodeId", "children");
        return AjaxResult.success(buildTreeService.buildTree(list));
    }

    @PostMapping("/treeselect")
    @ApiOperation(value = "获取国家区域下拉树列表", notes = "获取国家区域下拉树列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TreeSelect.class))
    public AjaxResult getList() {
        List<ConCountryRegion> list = conCountryRegionService.selectConCountryRegionList(new ConCountryRegion());
        BuildTreeService<ConCountryRegion> buildTreeService=new BuildTreeService<>("countryRegionSid", "parentCodeId", "children");
        List<ConCountryRegion> trees=buildTreeService.buildTree(list);
        return AjaxResult.success(trees.stream().map(TreeSelect::new).collect(Collectors.toList()));
    }

    /**
     * 导出国家区域列表
     */
    @PreAuthorize(hasPermi = "ems:con:country:region:export")
    @Log(title = "国家区域", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出国家区域列表", notes = "导出国家区域列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConCountryRegion conCountryRegion) throws IOException {
        List<ConCountryRegion> list = conCountryRegionService.selectConCountryRegionList(conCountryRegion);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConCountryRegion> util = new ExcelUtil<>(ConCountryRegion.class,dataMap);
        util.exportExcel(response, list, "国家区域");
    }


    /**
     * 获取国家区域详细信息
     */
    @ApiOperation(value = "获取国家区域详细信息", notes = "获取国家区域详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCountryRegion.class))
    @PreAuthorize(hasPermi = "ems:con:country:region:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long countryRegionSid) {
        if(countryRegionSid==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conCountryRegionService.selectConCountryRegionById(countryRegionSid));
    }

    /**
     * 新增国家区域
     */
    @ApiOperation(value = "新增国家区域", notes = "新增国家区域")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:country:region:add")
    @Log(title = "国家区域", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConCountryRegion conCountryRegion) {
        return toAjax(conCountryRegionService.insertConCountryRegion(conCountryRegion));
    }

    /**
     * 修改国家区域
     */
    @ApiOperation(value = "修改国家区域", notes = "修改国家区域")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:country:region:edit")
    @Log(title = "国家区域", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConCountryRegion conCountryRegion) {
        return toAjax(conCountryRegionService.updateConCountryRegion(conCountryRegion));
    }

    /**
     * 变更国家区域
     */
    @ApiOperation(value = "变更国家区域", notes = "变更国家区域")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:country:region:change")
    @Log(title = "国家区域", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody ConCountryRegion conCountryRegion) {
        return toAjax(conCountryRegionService.changeConCountryRegion(conCountryRegion));
    }

    /**
     * 删除国家区域
     */
    @ApiOperation(value = "删除国家区域", notes = "删除国家区域")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:country:region:remove")
    @Log(title = "国家区域", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  countryRegionSids) {
        if(ArrayUtil.isEmpty( countryRegionSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conCountryRegionService.deleteConCountryRegionByIds(countryRegionSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "国家区域", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:con:country:region:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConCountryRegion conCountryRegion) {
        return AjaxResult.success(conCountryRegionService.changeStatus(conCountryRegion));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:con:country:region:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "国家区域", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConCountryRegion conCountryRegion) {
        conCountryRegion.setConfirmDate(new Date());
        conCountryRegion.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conCountryRegion.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conCountryRegionService.check(conCountryRegion));
    }

}
