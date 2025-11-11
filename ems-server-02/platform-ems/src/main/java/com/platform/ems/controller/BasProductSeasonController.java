package com.platform.ems.controller;

import java.util.Date;
import java.util.List;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.annotation.PreAuthorize;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasSku;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import com.platform.ems.domain.BasProductSeason;
import com.platform.ems.service.IBasProductSeasonService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;

/**
 * 产品季档案Controller
 *
 * @author linhongwei
 * @date 2021-03-22
 */
@RestController
@RequestMapping("/product/season")
@Api(tags = "产品季档案")
public class BasProductSeasonController extends BaseController {

    @Autowired
    private IBasProductSeasonService basProductSeasonService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询产品季档案列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询产品季档案列表", notes = "查询产品季档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasProductSeason.class))
    public TableDataInfo list(@RequestBody BasProductSeason basProductSeason) {
        startPage(basProductSeason);
        List<BasProductSeason> list = basProductSeasonService.selectBasProductSeasonList(basProductSeason);
        return getDataTable(list);
    }

    /**
     * 导出产品季档案列表
     */
    @ApiOperation(value = "导出产品季档案列表", notes = "导出产品季档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasProductSeason basProductSeason) throws IOException {
        List<BasProductSeason> list = basProductSeasonService.selectBasProductSeasonList(basProductSeason);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasProductSeason> util = new ExcelUtil<>(BasProductSeason.class, dataMap);
        util.exportExcel(response, list, "产品季");
    }

    /**
     * 获取产品季档案详细信息
     */
    @ApiOperation(value = "获取产品季档案详细信息", notes = "获取产品季档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasProductSeason.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long productSeasonSid) {
        return AjaxResult.success(basProductSeasonService.selectBasProductSeasonById(productSeasonSid));
    }

    /**
     * 新增产品季档案
     */
    @ApiOperation(value = "新增产品季档案", notes = "新增产品季档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasProductSeason basProductSeason) {
        int row = basProductSeasonService.insertBasProductSeason(basProductSeason);
        return AjaxResult.success(basProductSeason);
    }

    /**
     * 修改产品季档案
     */
    @ApiOperation(value = "修改产品季档案", notes = "修改产品季档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody BasProductSeason basProductSeason) {
        return toAjax(basProductSeasonService.updateBasProductSeason(basProductSeason));
    }

    @ApiOperation(value = "变更产品季档案", notes = "变更产品季档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:season:change")
    @PostMapping("/change")
    public AjaxResult change(@RequestBody BasProductSeason basProductSeason) {
        return toAjax(basProductSeasonService.updateBasProductSeason(basProductSeason));
    }

    /**
     * 删除产品季档案
     */
    @ApiOperation(value = "删除产品季档案", notes = "删除产品季档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String> clientIds) {
        return toAjax(basProductSeasonService.deleteBasProductSeasonByIds(clientIds));
    }

    @ApiOperation(value = "获取产品季下拉列表", notes = "获取产品季下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/getList")
    public AjaxResult getList() {
        return AjaxResult.success(basProductSeasonService.getList(new BasProductSeason()
                .setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS)));
    }

    @ApiOperation(value = "获取产品季下拉列表", notes = "获取产品季下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/getProductSeasonList")
    public AjaxResult getProductSeasonList(@RequestBody BasProductSeason basProductSeason) {
        return AjaxResult.success(basProductSeasonService.getList(basProductSeason));
    }


    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasSku.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody BasProductSeason basProductSeason) {
        return AjaxResult.success(basProductSeasonService.changeStatus(basProductSeason));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasSku.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody BasProductSeason basProductSeason) {
        basProductSeason.setConfirmDate(new Date());
        basProductSeason.setConfirmerAccount(SecurityUtils.getUsername());
        basProductSeason.setHandleStatus(ConstantsEms.CHECK_STATUS);
        return toAjax(basProductSeasonService.check(basProductSeason));
    }


}
