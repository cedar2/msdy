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
import com.platform.ems.plug.domain.ConDocBuTypeGroupSo;
import com.platform.ems.plug.service.IConDocBuTypeGroupSoService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 销售订单单据类型与业务类型组合关系Controller
 *
 * @author chenkw
 * @date 2021-12-24
 */
@RestController
@RequestMapping("/con/doc/buType/group/so")
@Api(tags = "销售订单单据类型与业务类型组合关系")
public class ConDocBuTypeGroupSoController extends BaseController {

    @Autowired
    private IConDocBuTypeGroupSoService conDocBuTypeGroupSoService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询销售订单单据类型与业务类型组合关系列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询销售订单单据类型与业务类型组合关系列表", notes = "查询销售订单单据类型与业务类型组合关系列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocBuTypeGroupSo.class))
    public TableDataInfo list(@RequestBody ConDocBuTypeGroupSo conDocBuTypeGroupSo) {
        startPage(conDocBuTypeGroupSo);
        List<ConDocBuTypeGroupSo> list = conDocBuTypeGroupSoService.selectConDocBuTypeGroupSoList(conDocBuTypeGroupSo);
        return getDataTable(list);
    }

    /**
     * 导出销售订单单据类型与业务类型组合关系列表
     */
    @Log(title = "销售订单单据类型与业务类型组合关系", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出销售订单单据类型与业务类型组合关系列表", notes = "导出销售订单单据类型与业务类型组合关系列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocBuTypeGroupSo conDocBuTypeGroupSo) throws IOException {
        List<ConDocBuTypeGroupSo> list = conDocBuTypeGroupSoService.selectConDocBuTypeGroupSoList(conDocBuTypeGroupSo);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDocBuTypeGroupSo> util = new ExcelUtil<>(ConDocBuTypeGroupSo.class, dataMap);
        util.exportExcel(response, list, "销售订单单据类型与业务类型组合关系");
    }


    /**
     * 获取销售订单单据类型与业务类型组合关系详细信息
     */
    @ApiOperation(value = "获取销售订单单据类型与业务类型组合关系详细信息", notes = "获取销售订单单据类型与业务类型组合关系详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocBuTypeGroupSo.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocBuTypeGroupSoService.selectConDocBuTypeGroupSoById(sid));
    }

    /**
     * 新增销售订单单据类型与业务类型组合关系
     */
    @ApiOperation(value = "新增销售订单单据类型与业务类型组合关系", notes = "新增销售订单单据类型与业务类型组合关系")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售订单单据类型与业务类型组合关系", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocBuTypeGroupSo conDocBuTypeGroupSo) {
        return toAjax(conDocBuTypeGroupSoService.insertConDocBuTypeGroupSo(conDocBuTypeGroupSo));
    }

    /**
     * 修改销售订单单据类型与业务类型组合关系
     */
    @ApiOperation(value = "修改销售订单单据类型与业务类型组合关系", notes = "修改销售订单单据类型与业务类型组合关系")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售订单单据类型与业务类型组合关系", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConDocBuTypeGroupSo conDocBuTypeGroupSo) {
        return toAjax(conDocBuTypeGroupSoService.updateConDocBuTypeGroupSo(conDocBuTypeGroupSo));
    }

    /**
     * 变更销售订单单据类型与业务类型组合关系
     */
    @ApiOperation(value = "变更销售订单单据类型与业务类型组合关系", notes = "变更销售订单单据类型与业务类型组合关系")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售订单单据类型与业务类型组合关系", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocBuTypeGroupSo conDocBuTypeGroupSo) {
        return toAjax(conDocBuTypeGroupSoService.changeConDocBuTypeGroupSo(conDocBuTypeGroupSo));
    }

    /**
     * 删除销售订单单据类型与业务类型组合关系
     */
    @ApiOperation(value = "删除销售订单单据类型与业务类型组合关系", notes = "删除销售订单单据类型与业务类型组合关系")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售订单单据类型与业务类型组合关系", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocBuTypeGroupSoService.deleteConDocBuTypeGroupSoByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售订单单据类型与业务类型组合关系", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocBuTypeGroupSo conDocBuTypeGroupSo) {
        if (StrUtil.isBlank(conDocBuTypeGroupSo.getStatus())){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocBuTypeGroupSoService.changeStatus(conDocBuTypeGroupSo));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售订单单据类型与业务类型组合关系", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocBuTypeGroupSo conDocBuTypeGroupSo) {
        if (StrUtil.isBlank(conDocBuTypeGroupSo.getHandleStatus())){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocBuTypeGroupSoService.check(conDocBuTypeGroupSo));
    }

}
