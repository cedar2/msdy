package com.platform.ems.plug.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import com.platform.common.core.domain.model.DictData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.plug.domain.ConDataobjectHandleStatus;
import com.platform.ems.plug.service.IConDataobjectHandleStatusService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 数据对象类别与处理状态Controller真
 *
 * @author chenkw
 * @date 2022-06-23
 */
@RestController
@RequestMapping("/con/dataobject/hanleStatus")
@Api(tags = "数据对象类别与处理状态")
public class ConDataobjectHandleStatusController extends BaseController {

    @Autowired
    private IConDataobjectHandleStatusService conDataobjectHandleStatusService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询数据对象类别与处理状态列表的
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询数据对象类别与处理状态列表", notes = "查询数据对象类别与处理状态列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDataobjectHandleStatus.class))
    public TableDataInfo list(@RequestBody ConDataobjectHandleStatus conDataobjectHandleStatus) {
        startPage(conDataobjectHandleStatus);
        List<ConDataobjectHandleStatus> list = conDataobjectHandleStatusService.selectConDataobjectHandleStatusList(conDataobjectHandleStatus);
        list.forEach(item -> {
            String[] strs = item.getDataobjectHandleStatus().split(";");
            item.setDataobjectHandleStatusList(strs);
        });
        return getDataTable(list);
    }

    /**
     * 导出数据对象类别与处理状态列表傻
     */
    @Log(title = "数据对象类别与处理状态", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出数据对象类别与处理状态列表", notes = "导出数据对象类别与处理状态列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDataobjectHandleStatus conDataobjectHandleStatus) throws IOException {
        List<ConDataobjectHandleStatus> list = conDataobjectHandleStatusService.selectConDataobjectHandleStatusList(conDataobjectHandleStatus);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDataobjectHandleStatus> util = new ExcelUtil<>(ConDataobjectHandleStatus.class, dataMap);

        List<DictData> handleStatusList = (List<DictData>) dataMap.get("s_handle_status");

        list.forEach(item -> {
            String handleStatusStr = item.getDataobjectHandleStatus();
            String[] strs = handleStatusStr.split(";");
            String excelStr = "";
            for (String str : strs) {
                for (DictData dictData : handleStatusList) {
                    if (StrUtil.equals(str , dictData.getDictValue())) {
                        excelStr += dictData.getDictLabel() + ";";
                        break;
                    }
                }
            }
            item.setDataobjectHandleStatus(excelStr);
        });

        util.exportExcel(response, list, "数据对象类别与处理状态");
    }


    /**
     * 获取数据对象类别与处理状态详细信息比
     */
    @ApiOperation(value = "获取数据对象类别与处理状态详细信息", notes = "获取数据对象类别与处理状态详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDataobjectHandleStatus.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDataobjectHandleStatusService.selectConDataobjectHandleStatusById(sid));
    }

    /**
     * 新增数据对象类别与处理状态
     */
    @ApiOperation(value = "新增数据对象类别与处理状态", notes = "新增数据对象类别与处理状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "数据对象类别与处理状态", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDataobjectHandleStatus conDataobjectHandleStatus) {
        return toAjax(conDataobjectHandleStatusService.insertConDataobjectHandleStatus(conDataobjectHandleStatus));
    }

    /**
     * 修改数据对象类别与处理状态
     */
    @ApiOperation(value = "修改数据对象类别与处理状态", notes = "修改数据对象类别与处理状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "数据对象类别与处理状态", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConDataobjectHandleStatus conDataobjectHandleStatus) {
        return toAjax(conDataobjectHandleStatusService.updateConDataobjectHandleStatus(conDataobjectHandleStatus));
    }

    /**
     * 变更数据对象类别与处理状态
     */
    @ApiOperation(value = "变更数据对象类别与处理状态", notes = "变更数据对象类别与处理状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "数据对象类别与处理状态", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDataobjectHandleStatus conDataobjectHandleStatus) {
        return toAjax(conDataobjectHandleStatusService.changeConDataobjectHandleStatus(conDataobjectHandleStatus));
    }

    /**
     * 删除数据对象类别与处理状态
     */
    @ApiOperation(value = "删除数据对象类别与处理状态", notes = "删除数据对象类别与处理状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "数据对象类别与处理状态", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDataobjectHandleStatusService.deleteConDataobjectHandleStatusByIds(sids));
    }

    /**
     * 获取下拉框
     */
    @ApiOperation(value = "获取下拉框", notes = "获取下拉框")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/getList")
    public AjaxResult getList(@RequestBody ConDataobjectHandleStatus conDataobjectHandleStatus) {
        return AjaxResult.success(conDataobjectHandleStatusService.getList(conDataobjectHandleStatus));
    }

}
