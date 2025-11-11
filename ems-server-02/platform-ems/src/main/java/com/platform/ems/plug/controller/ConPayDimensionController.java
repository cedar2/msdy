package com.platform.ems.plug.controller;

import cn.hutool.core.util.ArrayUtil;
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
import com.platform.ems.plug.domain.ConPayDimension;
import com.platform.ems.plug.service.IConPayDimensionService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
 * 付款维度Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/pay/dimension")
@Api(tags = "付款维度")
public class ConPayDimensionController extends BaseController {

    @Autowired
    private IConPayDimensionService conPayDimensionService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询付款维度列表
     */
    @PreAuthorize(hasPermi = "ems:pay:dimension:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询付款维度列表", notes = "查询付款维度列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPayDimension.class))
    public TableDataInfo list(@RequestBody ConPayDimension conPayDimension) {
        startPage(conPayDimension);
        List<ConPayDimension> list = conPayDimensionService.selectConPayDimensionList(conPayDimension);
        return getDataTable(list);
    }

    /**
     * 导出付款维度列表
     */
    @PreAuthorize(hasPermi = "ems:pay:dimension:export")
    @Log(title = "付款维度", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出付款维度列表", notes = "导出付款维度列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConPayDimension conPayDimension) throws IOException {
        List<ConPayDimension> list = conPayDimensionService.selectConPayDimensionList(conPayDimension);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConPayDimension> util = new ExcelUtil<>(ConPayDimension.class, dataMap);
        util.exportExcel(response, list, "付款维度");
    }

    /**
     * 获取付款维度详细信息
     */
    @ApiOperation(value = "获取付款维度详细信息", notes = "获取付款维度详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPayDimension.class))
    @PreAuthorize(hasPermi = "ems:pay:dimension:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conPayDimensionService.selectConPayDimensionById(sid));
    }

    /**
     * 新增付款维度
     */
    @ApiOperation(value = "新增付款维度", notes = "新增付款维度")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:payDimension:add")
    @Log(title = "付款维度", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConPayDimension conPayDimension) {
        return toAjax(conPayDimensionService.insertConPayDimension(conPayDimension));
    }

    /**
     * 修改付款维度
     */
    @ApiOperation(value = "修改付款维度", notes = "修改付款维度")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pay:dimension:edit")
    @Log(title = "付款维度", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConPayDimension conPayDimension) {
        return toAjax(conPayDimensionService.updateConPayDimension(conPayDimension));
    }

    /**
     * 变更付款维度
     */
    @ApiOperation(value = "变更付款维度", notes = "变更付款维度")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:payDimension:change")
    @Log(title = "付款维度", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConPayDimension conPayDimension) {
        return toAjax(conPayDimensionService.changeConPayDimension(conPayDimension));
    }

    /**
     * 删除付款维度
     */
    @ApiOperation(value = "删除付款维度", notes = "删除付款维度")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pay:dimension:remove")
    @Log(title = "付款维度", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conPayDimensionService.deleteConPayDimensionByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "付款维度", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:payDimension:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConPayDimension conPayDimension) {
        return AjaxResult.success(conPayDimensionService.changeStatus(conPayDimension));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:payDimension:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "付款维度", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConPayDimension conPayDimension) {
        conPayDimension.setConfirmDate(new Date());
        conPayDimension.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conPayDimension.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conPayDimensionService.check(conPayDimension));
    }

}
