package com.platform.ems.plug.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConBuStockBusinessFlag;
import com.platform.ems.plug.service.IConBuStockBusinessFlagService;
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
import java.util.List;
import java.util.Map;

/**
 * 业务标识_其它出入库Controller
 *
 * @author wangp
 * @date 2022-10-09
 */
@RestController
@RequestMapping("/conBuStockBusinessFlag")
@Api(tags = "业务标识_其它出入库")
public class ConBuStockBusinessFlagController extends BaseController {

    @Autowired
    private IConBuStockBusinessFlagService conBuStockBusinessFlagService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询业务标识_其它出入库列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询业务标识_其它出入库列表", notes = "查询业务标识_其它出入库列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuStockBusinessFlag.class))
    public TableDataInfo list(@RequestBody ConBuStockBusinessFlag conBuStockBusinessFlag) {
        startPage(conBuStockBusinessFlag);
        List<ConBuStockBusinessFlag> list = conBuStockBusinessFlagService.selectConBuStockBusinessFlagList(conBuStockBusinessFlag);
        return getDataTable(list);
    }

    /**
     * 导出业务标识_其它出入库列表
     */
    @Log(title = "业务标识_其它出入库", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出业务标识_其它出入库列表", notes = "导出业务标识_其它出入库列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBuStockBusinessFlag conBuStockBusinessFlag) throws IOException {
        List<ConBuStockBusinessFlag> list = conBuStockBusinessFlagService.selectConBuStockBusinessFlagList(conBuStockBusinessFlag);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBuStockBusinessFlag> util = new ExcelUtil<>(ConBuStockBusinessFlag.class, dataMap);
        util.exportExcel(response, list, "业务标识_其它出入库");
    }


    /**
     * 获取业务标识_其它出入库详细信息
     */
    @ApiOperation(value = "获取业务标识_其它出入库详细信息", notes = "获取业务标识_其它出入库详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuStockBusinessFlag.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBuStockBusinessFlagService.selectConBuStockBusinessFlagById(sid));
    }

    /**
     * 新增业务标识_其它出入库
     */
    @ApiOperation(value = "新增业务标识_其它出入库", notes = "新增业务标识_其它出入库")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务标识_其它出入库", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid ConBuStockBusinessFlag conBuStockBusinessFlag) {
        return toAjax(conBuStockBusinessFlagService.insertConBuStockBusinessFlag(conBuStockBusinessFlag));
    }

    @ApiOperation(value = "修改业务标识_其它出入库", notes = "修改业务标识_其它出入库")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务标识_其它出入库", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody ConBuStockBusinessFlag conBuStockBusinessFlag) {
        return toAjax(conBuStockBusinessFlagService.updateConBuStockBusinessFlag(conBuStockBusinessFlag));
    }

    /**
     * 变更业务标识_其它出入库
     */
    @ApiOperation(value = "变更业务标识_其它出入库", notes = "变更业务标识_其它出入库")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务标识_其它出入库", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBuStockBusinessFlag conBuStockBusinessFlag) {
        return toAjax(conBuStockBusinessFlagService.changeConBuStockBusinessFlag(conBuStockBusinessFlag));
    }

    /**
     * 删除业务标识_其它出入库
     */
    @ApiOperation(value = "删除业务标识_其它出入库", notes = "删除业务标识_其它出入库")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务标识_其它出入库", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuStockBusinessFlagService.deleteConBuStockBusinessFlagByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务标识_其它出入库", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBuStockBusinessFlag conBuStockBusinessFlag) {
        return AjaxResult.success(conBuStockBusinessFlagService.changeStatus(conBuStockBusinessFlag));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务标识_其它出入库", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody ConBuStockBusinessFlag conBuStockBusinessFlag) {
        return toAjax(conBuStockBusinessFlagService.check(conBuStockBusinessFlag));
    }

    /**
     * 业务标识_其它出入库下拉框
     */
    @PostMapping("/getList")
    @ApiOperation(value = "业务标识_其它出入库列表下拉框", notes = "业务标识_其它出入库列表下拉框")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuStockBusinessFlag.class))
    public AjaxResult getList(@RequestBody ConBuStockBusinessFlag conBuStockBusinessFlag) {
        conBuStockBusinessFlag.setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS);
        return AjaxResult.success(conBuStockBusinessFlagService.getConBuStockBusinessFlagList(conBuStockBusinessFlag));
    }

}
