package com.platform.ems.plug.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConBuTypeManufactureOutsourceSettle;
import com.platform.ems.plug.service.IConBuTypeManufactureOutsourceSettleService;
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
 * 业务类型_外发加工费结算单Controller
 *
 * @author c
 * @date 2021-11-25
 */
@RestController
@RequestMapping("/bu/type/settle")
@Api(tags = "业务类型_外发加工费结算单")
public class ConBuTypeManufactureOutsourceSettleController extends BaseController {

    @Autowired
    private IConBuTypeManufactureOutsourceSettleService conBuTypeManufactureOutsourceSettleService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询业务类型_外发加工费结算单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询业务类型_外发加工费结算单列表", notes = "查询业务类型_外发加工费结算单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeManufactureOutsourceSettle.class))
    public TableDataInfo list(@RequestBody ConBuTypeManufactureOutsourceSettle outsourceSettle) {
        startPage(outsourceSettle);
        List<ConBuTypeManufactureOutsourceSettle> list = conBuTypeManufactureOutsourceSettleService.selectConBuTypeManOutsourceSettleList(outsourceSettle);
        return getDataTable(list);
    }

    /**
     * 导出业务类型_外发加工费结算单列表
     */
    @ApiOperation(value = "导出业务类型_外发加工费结算单列表", notes = "导出业务类型_外发加工费结算单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBuTypeManufactureOutsourceSettle outsourceSettle) throws IOException {
        List<ConBuTypeManufactureOutsourceSettle> list = conBuTypeManufactureOutsourceSettleService.selectConBuTypeManOutsourceSettleList(outsourceSettle);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBuTypeManufactureOutsourceSettle> util = new ExcelUtil<>(ConBuTypeManufactureOutsourceSettle.class, dataMap);
        util.exportExcel(response, list, "业务类型(外发加工费结算单)");
    }

    /**
     * 获取业务类型_外发加工费结算单详细信息
     */
    @ApiOperation(value = "获取业务类型_外发加工费结算单详细信息", notes = "获取业务类型_外发加工费结算单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeManufactureOutsourceSettle.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBuTypeManufactureOutsourceSettleService.selectConBuTypeManOutsourceSettleById(sid));
    }

    /**
     * 新增业务类型_外发加工费结算单
     */
    @ApiOperation(value = "新增业务类型_外发加工费结算单", notes = "新增业务类型_外发加工费结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBuTypeManufactureOutsourceSettle outsourceSettle) {
        return toAjax(conBuTypeManufactureOutsourceSettleService.insertConBuTypeManOutsourceSettle(outsourceSettle));
    }

    /**
     * 修改业务类型_外发加工费结算单
     */
    @ApiOperation(value = "修改业务类型_外发加工费结算单", notes = "修改业务类型_外发加工费结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConBuTypeManufactureOutsourceSettle outsourceSettle) {
        return toAjax(conBuTypeManufactureOutsourceSettleService.updateConBuTypeManOutsourceSettle(outsourceSettle));
    }

    /**
     * 变更业务类型_外发加工费结算单
     */
    @ApiOperation(value = "变更业务类型_外发加工费结算单", notes = "变更业务类型_外发加工费结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody ConBuTypeManufactureOutsourceSettle outsourceSettle) {
        return toAjax(conBuTypeManufactureOutsourceSettleService.changeConBuTypeManOutsourceSettle(outsourceSettle));
    }

    /**
     * 删除业务类型_外发加工费结算单
     */
    @ApiOperation(value = "删除业务类型_外发加工费结算单", notes = "删除业务类型_外发加工费结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuTypeManufactureOutsourceSettleService.deleteConBuTypeManOutsourceSettleByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBuTypeManufactureOutsourceSettle outsourceSettle) {
        return AjaxResult.success(conBuTypeManufactureOutsourceSettleService.changeStatus(outsourceSettle));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBuTypeManufactureOutsourceSettle outsourceSettle) {
        outsourceSettle.setConfirmDate(new Date());
        outsourceSettle.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        outsourceSettle.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBuTypeManufactureOutsourceSettleService.check(outsourceSettle));
    }

    /**
     * 业务类型_外发加工费结算单下拉框列表
     */
    @PostMapping("/getBuOutsourceSettleList")
    @ApiOperation(value = "外发加工费结算下拉框列表", notes = "外发加工费结算下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeManufactureOutsourceSettle.class))
    public AjaxResult getOutsourceSettleList(@RequestBody ConBuTypeManufactureOutsourceSettle outsourceSettle) {
        return AjaxResult.success(conBuTypeManufactureOutsourceSettleService.getOutsourceSettleList(outsourceSettle));
    }
}
