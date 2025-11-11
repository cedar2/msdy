package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
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
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import javax.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.plug.domain.ConSampleRequisitionUsage;
import com.platform.ems.plug.service.IConSampleRequisitionUsageService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 样品出库用途Controller
 *
 * @author yangqz
 * @date 2022-04-24
 */
@RestController
@RequestMapping("/usage")
@Api(tags = "样品出库用途")
public class ConSampleRequisitionUsageController extends BaseController {

    @Autowired
    private IConSampleRequisitionUsageService conSampleRequisitionUsageService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询样品出库用途列表
     */
    @PreAuthorize(hasPermi = "ems:usage:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询样品出库用途列表", notes = "查询样品出库用途列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSampleRequisitionUsage.class))
    public TableDataInfo list(@RequestBody ConSampleRequisitionUsage conSampleRequisitionUsage) {
        startPage(conSampleRequisitionUsage);
        List<ConSampleRequisitionUsage> list = conSampleRequisitionUsageService.selectConSampleRequisitionUsageList(conSampleRequisitionUsage);
        return getDataTable(list);
    }

    @PostMapping("/getList")
    @ApiOperation(value = "查询样品出库用途下拉列表", notes = "查询样品出库用途下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSampleRequisitionUsage.class))
    public AjaxResult getList(@RequestBody ConSampleRequisitionUsage conSampleRequisitionUsage) {
        List<ConSampleRequisitionUsage> list = conSampleRequisitionUsageService.getList(conSampleRequisitionUsage);
        return AjaxResult.success(list);
    }
    /**
     * 导出样品出库用途列表
     */
    @PreAuthorize(hasPermi = "ems:usage:export")
    @Log(title = "样品出库用途", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出样品出库用途列表", notes = "导出样品出库用途列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConSampleRequisitionUsage conSampleRequisitionUsage) throws IOException {
        List<ConSampleRequisitionUsage> list = conSampleRequisitionUsageService.selectConSampleRequisitionUsageList(conSampleRequisitionUsage);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConSampleRequisitionUsage> util = new ExcelUtil<ConSampleRequisitionUsage>(ConSampleRequisitionUsage.class,dataMap);
        util.exportExcel(response, list, "样品出库用途"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取样品出库用途详细信息
     */
    @ApiOperation(value = "获取样品出库用途详细信息", notes = "获取样品出库用途详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSampleRequisitionUsage.class))
    @PreAuthorize(hasPermi = "ems:usage:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conSampleRequisitionUsageService.selectConSampleRequisitionUsageById(sid));
    }

    /**
     * 新增样品出库用途
     */
    @ApiOperation(value = "新增样品出库用途", notes = "新增样品出库用途")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:usage:add")
    @Log(title = "样品出库用途", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConSampleRequisitionUsage conSampleRequisitionUsage) {
        return toAjax(conSampleRequisitionUsageService.insertConSampleRequisitionUsage(conSampleRequisitionUsage));
    }

    /**
     * 修改样品出库用途
     */
    @ApiOperation(value = "修改样品出库用途", notes = "修改样品出库用途")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:usage:edit")
    @Log(title = "样品出库用途", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConSampleRequisitionUsage conSampleRequisitionUsage) {
        return toAjax(conSampleRequisitionUsageService.updateConSampleRequisitionUsage(conSampleRequisitionUsage));
    }

    /**
     * 变更样品出库用途
     */
    @ApiOperation(value = "变更样品出库用途", notes = "变更样品出库用途")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:usage:change")
    @Log(title = "样品出库用途", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody ConSampleRequisitionUsage conSampleRequisitionUsage) {
        return toAjax(conSampleRequisitionUsageService.changeConSampleRequisitionUsage(conSampleRequisitionUsage));
    }

    /**
     * 删除样品出库用途
     */
    @ApiOperation(value = "删除样品出库用途", notes = "删除样品出库用途")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:usage:remove")
    @Log(title = "样品出库用途", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(CollectionUtils.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conSampleRequisitionUsageService.deleteConSampleRequisitionUsageByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "样品出库用途", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:usage:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConSampleRequisitionUsage conSampleRequisitionUsage) {
        return AjaxResult.success(conSampleRequisitionUsageService.changeStatus(conSampleRequisitionUsage));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:usage:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "样品出库用途", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConSampleRequisitionUsage conSampleRequisitionUsage) {
        conSampleRequisitionUsage.setConfirmDate(new Date());
        conSampleRequisitionUsage.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conSampleRequisitionUsage.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conSampleRequisitionUsageService.check(conSampleRequisitionUsage));
    }

}
