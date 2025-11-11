package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
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
import com.platform.ems.plug.domain.ConDocBuTypeGroupPo;
import com.platform.ems.plug.service.IConDocBuTypeGroupPoService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 采购订单单据类型与业务类型组合关系Controller
 *
 * @author chenkw
 * @date 2021-12-24
 */
@RestController
@RequestMapping("/con/doc/buType/group/po")
@Api(tags = "采购订单单据类型与业务类型组合关系")
public class ConDocBuTypeGroupPoController extends BaseController {

    @Autowired
    private IConDocBuTypeGroupPoService conDocBuTypeGroupPoService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询采购订单单据类型与业务类型组合关系列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询采购订单单据类型与业务类型组合关系列表", notes = "查询采购订单单据类型与业务类型组合关系列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocBuTypeGroupPo.class))
    public TableDataInfo list(@RequestBody ConDocBuTypeGroupPo conDocBuTypeGroupPo) {
        startPage(conDocBuTypeGroupPo);
        List<ConDocBuTypeGroupPo> list = conDocBuTypeGroupPoService.selectConDocBuTypeGroupPoList(conDocBuTypeGroupPo);
        return getDataTable(list);
    }

    /**
     * 导出采购订单单据类型与业务类型组合关系列表
     */
    @Log(title = "采购订单单据类型与业务类型组合关系", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出采购订单单据类型与业务类型组合关系列表", notes = "导出采购订单单据类型与业务类型组合关系列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocBuTypeGroupPo conDocBuTypeGroupPo) throws IOException {
        List<ConDocBuTypeGroupPo> list = conDocBuTypeGroupPoService.selectConDocBuTypeGroupPoList(conDocBuTypeGroupPo);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDocBuTypeGroupPo> util = new ExcelUtil<>(ConDocBuTypeGroupPo.class, dataMap);
        util.exportExcel(response, list, "采购订单单据类型与业务类型组合关系");
    }


    /**
     * 获取采购订单单据类型与业务类型组合关系详细信息
     */
    @ApiOperation(value = "获取采购订单单据类型与业务类型组合关系详细信息", notes = "获取采购订单单据类型与业务类型组合关系详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocBuTypeGroupPo.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocBuTypeGroupPoService.selectConDocBuTypeGroupPoById(sid));
    }

    /**
     * 新增采购订单单据类型与业务类型组合关系
     */
    @ApiOperation(value = "新增采购订单单据类型与业务类型组合关系", notes = "新增采购订单单据类型与业务类型组合关系")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购订单单据类型与业务类型组合关系", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocBuTypeGroupPo conDocBuTypeGroupPo) {
        return toAjax(conDocBuTypeGroupPoService.insertConDocBuTypeGroupPo(conDocBuTypeGroupPo));
    }

    /**
     * 修改采购订单单据类型与业务类型组合关系
     */
    @ApiOperation(value = "修改采购订单单据类型与业务类型组合关系", notes = "修改采购订单单据类型与业务类型组合关系")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购订单单据类型与业务类型组合关系", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConDocBuTypeGroupPo conDocBuTypeGroupPo) {
        return toAjax(conDocBuTypeGroupPoService.updateConDocBuTypeGroupPo(conDocBuTypeGroupPo));
    }

    /**
     * 变更采购订单单据类型与业务类型组合关系
     */
    @ApiOperation(value = "变更采购订单单据类型与业务类型组合关系", notes = "变更采购订单单据类型与业务类型组合关系")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购订单单据类型与业务类型组合关系", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocBuTypeGroupPo conDocBuTypeGroupPo) {
        return toAjax(conDocBuTypeGroupPoService.changeConDocBuTypeGroupPo(conDocBuTypeGroupPo));
    }

    /**
     * 删除采购订单单据类型与业务类型组合关系
     */
    @ApiOperation(value = "删除采购订单单据类型与业务类型组合关系", notes = "删除采购订单单据类型与业务类型组合关系")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购订单单据类型与业务类型组合关系", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocBuTypeGroupPoService.deleteConDocBuTypeGroupPoByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购订单单据类型与业务类型组合关系", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocBuTypeGroupPo conDocBuTypeGroupPo) {
        if (StrUtil.isBlank(conDocBuTypeGroupPo.getStatus())){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocBuTypeGroupPoService.changeStatus(conDocBuTypeGroupPo));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购订单单据类型与业务类型组合关系", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocBuTypeGroupPo conDocBuTypeGroupPo) {
        if (StrUtil.isBlank(conDocBuTypeGroupPo.getHandleStatus())){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocBuTypeGroupPoService.check(conDocBuTypeGroupPo));
    }

}
