package com.platform.ems.plug.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
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
import com.platform.ems.plug.domain.ConShipmentMode;
import com.platform.ems.plug.service.IConShipmentModeService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 配送方式Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/shipment/mode")
@Api(tags = "配送方式")
public class ConShipmentModeController extends BaseController {

    @Autowired
    private IConShipmentModeService conShipmentModeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询配送方式列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询配送方式列表", notes = "查询配送方式列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConShipmentMode.class))
    public TableDataInfo list(@RequestBody ConShipmentMode conShipmentMode) {
        startPage(conShipmentMode);
        List<ConShipmentMode> list = conShipmentModeService.selectConShipmentModeList(conShipmentMode);
        return getDataTable(list);
    }

    /**
     * 导出配送方式列表
     */
    @Log(title = "配送方式", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出配送方式列表", notes = "导出配送方式列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConShipmentMode conShipmentMode) throws IOException {
        List<ConShipmentMode> list = conShipmentModeService.selectConShipmentModeList(conShipmentMode);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConShipmentMode> util = new ExcelUtil<>(ConShipmentMode.class, dataMap);
        util.exportExcel(response, list, "配送方式");
    }

    /**
     * 导入配送方式
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入配送方式", notes = "导入配送方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConShipmentMode> util = new ExcelUtil<>(ConShipmentMode.class);
        List<ConShipmentMode> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conShipmentMode -> {
                conShipmentModeService.insertConShipmentMode(conShipmentMode);
                i++;
            });
        } catch (Exception e) {
            lose = listSize - i;
            msg = StrUtil.format("前{}条数据导入成功，失败{}条,导入成功的数据请勿重复导入", i, lose);
        }
        if (StrUtil.isEmpty(msg)) {
            msg = "导入成功";
        }
        return AjaxResult.success(msg);
    }


    @ApiOperation(value = "下载配送方式导入模板", notes = "下载配送方式导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConShipmentMode> util = new ExcelUtil<>(ConShipmentMode.class);
        util.importTemplateExcel(response, "配送方式导入模板");
    }


    /**
     * 获取配送方式详细信息
     */
    @ApiOperation(value = "获取配送方式详细信息", notes = "获取配送方式详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConShipmentMode.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conShipmentModeService.selectConShipmentModeById(sid));
    }

    /**
     * 新增配送方式
     */
    @ApiOperation(value = "新增配送方式", notes = "新增配送方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "配送方式", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConShipmentMode conShipmentMode) {
        return toAjax(conShipmentModeService.insertConShipmentMode(conShipmentMode));
    }

    /**
     * 修改配送方式
     */
    @ApiOperation(value = "修改配送方式", notes = "修改配送方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "配送方式", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConShipmentMode conShipmentMode) {
        return toAjax(conShipmentModeService.updateConShipmentMode(conShipmentMode));
    }

    /**
     * 变更配送方式
     */
    @ApiOperation(value = "变更配送方式", notes = "变更配送方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "配送方式", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConShipmentMode conShipmentMode) {
        return toAjax(conShipmentModeService.changeConShipmentMode(conShipmentMode));
    }

    /**
     * 删除配送方式
     */
    @ApiOperation(value = "删除配送方式", notes = "删除配送方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "配送方式", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conShipmentModeService.deleteConShipmentModeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "配送方式", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConShipmentMode conShipmentMode) {
        return AjaxResult.success(conShipmentModeService.changeStatus(conShipmentMode));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "配送方式", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConShipmentMode conShipmentMode) {
        conShipmentMode.setConfirmDate(new Date());
        conShipmentMode.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conShipmentMode.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conShipmentModeService.check(conShipmentMode));
    }

    /**
     * 配送方式下拉框
     */
    @PostMapping("/getList")
    @ApiOperation(value = "配送方式下拉框", notes = "配送方式下拉框")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConShipmentMode.class))
    public AjaxResult getList() {
        return AjaxResult.success(conShipmentModeService.getList());
    }
}
