package com.platform.ems.plug.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConDocTypeManufactureOutsourceSettle;
import com.platform.ems.plug.service.IConDocTypeManufactureOutsourceSettleService;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 单据类型_外发加工费结算单Controller
 *
 * @author c
 * @date 2021-11-25
 */
@RestController
@RequestMapping("/doc/type/settle")
@Api(tags = "单据类型_外发加工费结算单")
public class ConDocTypeManufactureOutsourceSettleController extends BaseController {

    @Autowired
    private IConDocTypeManufactureOutsourceSettleService conDocTypeManOutsourceSettleService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询单据类型_外发加工费结算单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型_外发加工费结算单列表", notes = "查询单据类型_外发加工费结算单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeManufactureOutsourceSettle.class))
    public TableDataInfo list(@RequestBody ConDocTypeManufactureOutsourceSettle outsourceSettle) {
        startPage(outsourceSettle);
        List<ConDocTypeManufactureOutsourceSettle> list = conDocTypeManOutsourceSettleService.selectConDocTypeManOutsourceSettleList(outsourceSettle);
        return getDataTable(list);
    }

    /**
     * 导出单据类型_外发加工费结算单列表
     */
    @ApiOperation(value = "导出单据类型_外发加工费结算单列表", notes = "导出单据类型_外发加工费结算单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypeManufactureOutsourceSettle outsourceSettle) throws IOException {
        List<ConDocTypeManufactureOutsourceSettle> list = conDocTypeManOutsourceSettleService.selectConDocTypeManOutsourceSettleList(outsourceSettle);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypeManufactureOutsourceSettle> util = new ExcelUtil<>(ConDocTypeManufactureOutsourceSettle.class, dataMap);
        util.exportExcel(response, list, "单据类型(外发加工费结算单)");
    }

    /**
     * 获取单据类型_外发加工费结算单详细信息
     */
    @ApiOperation(value = "获取单据类型_外发加工费结算单详细信息", notes = "获取单据类型_外发加工费结算单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeManufactureOutsourceSettle.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocTypeManOutsourceSettleService.selectConDocTypeManOutsourceSettleById(sid));
    }

    /**
     * 新增单据类型_外发加工费结算单
     */
    @ApiOperation(value = "新增单据类型_外发加工费结算单", notes = "新增单据类型_外发加工费结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypeManufactureOutsourceSettle outsourceSettle) {
        return toAjax(conDocTypeManOutsourceSettleService.insertConDocTypeManOutsourceSettle(outsourceSettle));
    }

    /**
     * 修改单据类型_外发加工费结算单
     */
    @ApiOperation(value = "修改单据类型_外发加工费结算单", notes = "修改单据类型_外发加工费结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDocTypeManufactureOutsourceSettle outsourceSettle) {
        return toAjax(conDocTypeManOutsourceSettleService.updateConDocTypeManOutsourceSettle(outsourceSettle));
    }

    /**
     * 变更单据类型_外发加工费结算单
     */
    @ApiOperation(value = "变更单据类型_外发加工费结算单", notes = "变更单据类型_外发加工费结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody ConDocTypeManufactureOutsourceSettle outsourceSettle) {
        return toAjax(conDocTypeManOutsourceSettleService.changeConDocTypeManOutsourceSettle(outsourceSettle));
    }

    /**
     * 删除单据类型_外发加工费结算单
     */
    @ApiOperation(value = "删除单据类型_外发加工费结算单", notes = "删除单据类型_外发加工费结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypeManOutsourceSettleService.deleteConDocTypeManOutsourceSettleByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypeManufactureOutsourceSettle outsourceSettle) {
        return AjaxResult.success(conDocTypeManOutsourceSettleService.changeStatus(outsourceSettle));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypeManufactureOutsourceSettle outsourceSettle) {
        outsourceSettle.setConfirmDate(new Date());
        outsourceSettle.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        outsourceSettle.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypeManOutsourceSettleService.check(outsourceSettle));
    }

    /**
     * 单据类型_外发加工费结算单下拉框列表
     */
    @PostMapping("/getDocOutsourceSettleList")
    @ApiOperation(value = "外发加工费结算下拉框列表", notes = "外发加工费结算下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeManufactureOutsourceSettle.class))
    public AjaxResult getDocOutsourceSettleList(@RequestBody ConDocTypeManufactureOutsourceSettle outsourceSettle) {
        return AjaxResult.success(conDocTypeManOutsourceSettleService.getDocOutsourceSettleList(outsourceSettle));
    }

}
