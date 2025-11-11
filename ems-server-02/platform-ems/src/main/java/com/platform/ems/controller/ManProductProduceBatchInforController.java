package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.annotation.Idempotent;
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
import com.platform.ems.domain.ManProductProduceBatchInfor;
import com.platform.ems.service.IManProductProduceBatchInforService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 商品生产批次信息Controller
 *
 * @author chenkw
 * @date 2022-09-30
 */
@RestController
@RequestMapping("/man/product/produce/batchInfor")
@Api(tags = "商品生产批次信息")
public class ManProductProduceBatchInforController extends BaseController {

    @Autowired
    private IManProductProduceBatchInforService manProductProduceBatchInforService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询商品生产批次信息列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询商品生产批次信息列表", notes = "查询商品生产批次信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProductProduceBatchInfor.class))
    public TableDataInfo list(@RequestBody ManProductProduceBatchInfor manProductProduceBatchInfor) {
        startPage(manProductProduceBatchInfor);
        List<ManProductProduceBatchInfor> list = manProductProduceBatchInforService.selectManProductProduceBatchInforList(manProductProduceBatchInfor);
        return getDataTable(list);
    }

    /**
     * 导出商品生产批次信息列表
     */
    @Log(title = "商品生产批次信息", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出商品生产批次信息列表", notes = "导出商品生产批次信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManProductProduceBatchInfor manProductProduceBatchInfor) throws IOException {
        List<ManProductProduceBatchInfor> list = manProductProduceBatchInforService.selectManProductProduceBatchInforList(manProductProduceBatchInfor);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManProductProduceBatchInfor> util = new ExcelUtil<>(ManProductProduceBatchInfor.class, dataMap);
        util.exportExcel(response, list, "商品生产批次信息");
    }

    /**
     * 获取商品生产批次信息详细信息
     */
    @ApiOperation(value = "获取商品生产批次信息详细信息", notes = "获取商品生产批次信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProductProduceBatchInfor.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long produceBatchInforSid) {
        if (produceBatchInforSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manProductProduceBatchInforService.selectManProductProduceBatchInforById(produceBatchInforSid));
    }

    /**
     * 新增商品生产批次信息
     */
    @ApiOperation(value = "新增商品生产批次信息", notes = "新增商品生产批次信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品生产批次信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid ManProductProduceBatchInfor manProductProduceBatchInfor) {
        return toAjax(manProductProduceBatchInforService.insertManProductProduceBatchInfor(manProductProduceBatchInfor));
    }

    @ApiOperation(value = "修改商品生产批次信息", notes = "修改商品生产批次信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品生产批次信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody ManProductProduceBatchInfor manProductProduceBatchInfor) {
        return toAjax(manProductProduceBatchInforService.updateManProductProduceBatchInfor(manProductProduceBatchInfor));
    }

    /**
     * 变更商品生产批次信息
     */
    @ApiOperation(value = "变更商品生产批次信息", notes = "变更商品生产批次信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品生产批次信息", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManProductProduceBatchInfor manProductProduceBatchInfor) {
        return toAjax(manProductProduceBatchInforService.changeManProductProduceBatchInfor(manProductProduceBatchInfor));
    }

    /**
     * 删除商品生产批次信息
     */
    @ApiOperation(value = "删除商品生产批次信息", notes = "删除商品生产批次信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品生产批次信息", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> produceBatchInforSids) {
        if (CollectionUtils.isEmpty(produceBatchInforSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manProductProduceBatchInforService.deleteManProductProduceBatchInforByIds(produceBatchInforSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品生产批次信息", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody ManProductProduceBatchInfor manProductProduceBatchInfor) {
        return toAjax(manProductProduceBatchInforService.check(manProductProduceBatchInfor));
    }

    @ApiOperation(value = "维护实裁数", notes = "维护实裁数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "维护实裁数", businessType = BusinessType.UPDATE)
    @PostMapping("/preserve/shicai")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult preserveShicai(@RequestBody ManProductProduceBatchInfor manProductProduceBatchInfor) {
        return toAjax(manProductProduceBatchInforService.preserveShicai(manProductProduceBatchInfor));
    }

}
