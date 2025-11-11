package com.platform.ems.plug.controller;

import cn.hutool.core.util.ArrayUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConPaymentMethod;
import com.platform.ems.plug.service.IConPaymentMethodService;
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
 * 支付方式Controller
 *
 * @author linhongwei
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/payment/method")
@Api(tags = "支付方式")
public class ConPaymentMethodController extends BaseController {

    @Autowired
    private IConPaymentMethodService conPaymentMethodService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询支付方式列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询支付方式列表", notes = "查询支付方式列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPaymentMethod.class))
    public TableDataInfo list(@RequestBody ConPaymentMethod conPaymentMethod) {
        startPage(conPaymentMethod);
        List<ConPaymentMethod> list = conPaymentMethodService.selectConPaymentMethodList(conPaymentMethod);
        return getDataTable(list);
    }

    /**
     * 导出支付方式列表
     */
    @ApiOperation(value = "导出支付方式列表", notes = "导出支付方式列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConPaymentMethod conPaymentMethod) throws IOException {
        List<ConPaymentMethod> list = conPaymentMethodService.selectConPaymentMethodList(conPaymentMethod);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConPaymentMethod> util = new ExcelUtil<>(ConPaymentMethod.class, dataMap);
        util.exportExcel(response, list, "支付方式");
    }

    /**
     * 获取支付方式详细信息
     */
    @ApiOperation(value = "获取支付方式详细信息", notes = "获取支付方式详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPaymentMethod.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conPaymentMethodService.selectConPaymentMethodById(sid));
    }

    /**
     * 新增支付方式
     */
    @ApiOperation(value = "新增支付方式", notes = "新增支付方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConPaymentMethod conPaymentMethod) {
        return toAjax(conPaymentMethodService.insertConPaymentMethod(conPaymentMethod));
    }

    /**
     * 修改支付方式
     */
    @ApiOperation(value = "修改支付方式", notes = "修改支付方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConPaymentMethod conPaymentMethod) {
        return toAjax(conPaymentMethodService.updateConPaymentMethod(conPaymentMethod));
    }

    /**
     * 变更支付方式
     */
    @ApiOperation(value = "变更支付方式", notes = "变更支付方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConPaymentMethod conPaymentMethod) {
        return toAjax(conPaymentMethodService.changeConPaymentMethod(conPaymentMethod));
    }

    /**
     * 删除支付方式
     */
    @ApiOperation(value = "删除支付方式", notes = "删除支付方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conPaymentMethodService.deleteConPaymentMethodByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConPaymentMethod conPaymentMethod) {
        return AjaxResult.success(conPaymentMethodService.changeStatus(conPaymentMethod));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConPaymentMethod conPaymentMethod) {
        conPaymentMethod.setConfirmDate(new Date());
        conPaymentMethod.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conPaymentMethod.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conPaymentMethodService.check(conPaymentMethod));
    }

    @PostMapping("/getConPaymentMethodList")
    @ApiOperation(value = "支付方式下拉框列表", notes = "支付方式下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPaymentMethod.class))
    public AjaxResult getConPaymentMethodList() {
        ConPaymentMethod conPaymentMethod = new ConPaymentMethod();
        conPaymentMethod.setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setStatus(ConstantsEms.ENABLE_STATUS);
        return AjaxResult.success(conPaymentMethodService.getConPaymentMethodList(conPaymentMethod));
    }

    @PostMapping("/getList")
    @ApiOperation(value = "支付方式下拉框列表", notes = "支付方式下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPaymentMethod.class))
    public AjaxResult getList(@RequestBody ConPaymentMethod conPaymentMethod) {
        return AjaxResult.success(conPaymentMethodService.getConPaymentMethodList(conPaymentMethod));
    }
}
