package com.platform.ems.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.domain.ManManufactureOutsourceSettle;
import com.platform.ems.domain.ManManufactureOutsourceSettleItem;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IManManufactureOutsourceSettleService;
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
 * 外发加工费结算单Controller
 *
 * @author linhongwei
 * @date 2021-06-10
 */
@RestController
@RequestMapping("/man/manufacture/outsoure/settle")
@Api(tags = "外发加工费结算单")
public class ManManufactureOutsourceSettleController extends BaseController {

    @Autowired
    private IManManufactureOutsourceSettleService manManufactureOutsourceSettleService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询外发加工费结算单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询外发加工费结算单列表", notes = "查询外发加工费结算单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOutsourceSettle.class))
    public TableDataInfo list(@RequestBody ManManufactureOutsourceSettle manManufactureOutsourceSettle) {
        startPage(manManufactureOutsourceSettle);
        List<ManManufactureOutsourceSettle> list = manManufactureOutsourceSettleService.selectManManufactureOutsourceSettleList(manManufactureOutsourceSettle);
        return getDataTable(list);
    }

    /**
     * 导出外发加工费结算单列表
     */
    @Log(title = "外发加工费结算单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出外发加工费结算单列表", notes = "导出外发加工费结算单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManManufactureOutsourceSettle manManufactureOutsourceSettle) throws IOException {
        List<ManManufactureOutsourceSettle> list = manManufactureOutsourceSettleService.selectManManufactureOutsourceSettleList(manManufactureOutsourceSettle);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureOutsourceSettle> util = new ExcelUtil<>(ManManufactureOutsourceSettle.class, dataMap);
        util.exportExcel(response, list, "外发加工费结算单");
    }

    /**
     * 获取外发加工费结算单详细信息
     */
    @ApiOperation(value = "获取外发加工费结算单详细信息", notes = "获取外发加工费结算单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOutsourceSettle.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long manufactureOutsourceSettleSid) {
        if (manufactureOutsourceSettleSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manManufactureOutsourceSettleService.selectManManufactureOutsourceSettleById(manufactureOutsourceSettleSid));
    }

    /**
     * 新增外发加工费结算单
     */
    @ApiOperation(value = "新增外发加工费结算单", notes = "新增外发加工费结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "外发加工费结算单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ManManufactureOutsourceSettle manManufactureOutsourceSettle) {
        int row = manManufactureOutsourceSettleService.insertManManufactureOutsourceSettle(manManufactureOutsourceSettle);
        if (row > 0) {
            return AjaxResult.success(null, manManufactureOutsourceSettle.getManufactureOutsourceSettleSid().toString());
        }
        return toAjax(row);
    }

    /**
     * 修改外发加工费结算单
     */
    @ApiOperation(value = "修改外发加工费结算单", notes = "修改外发加工费结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "外发加工费结算单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ManManufactureOutsourceSettle manManufactureOutsourceSettle) {
        return toAjax(manManufactureOutsourceSettleService.updateManManufactureOutsourceSettle(manManufactureOutsourceSettle));
    }

    /**
     * 变更外发加工费结算单
     */
    @ApiOperation(value = "变更外发加工费结算单", notes = "变更外发加工费结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "外发加工费结算单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManManufactureOutsourceSettle manManufactureOutsourceSettle) {
        return toAjax(manManufactureOutsourceSettleService.changeManManufactureOutsourceSettle(manManufactureOutsourceSettle));
    }

    /**
     * 删除外发加工费结算单
     */
    @ApiOperation(value = "删除外发加工费结算单", notes = "删除外发加工费结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "外发加工费结算单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> manufactureOutsourceSettleSids) {
        if (ArrayUtil.isEmpty(manufactureOutsourceSettleSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manManufactureOutsourceSettleService.deleteManManufactureOutsourceSettleByIds(manufactureOutsourceSettleSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "外发加工费结算单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ManManufactureOutsourceSettle manManufactureOutsourceSettle) {
        manManufactureOutsourceSettle.setConfirmDate(new Date());
        manManufactureOutsourceSettle.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        manManufactureOutsourceSettle.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(manManufactureOutsourceSettleService.check(manManufactureOutsourceSettle));
    }

    /**
     * 审批
     */
    @ApiOperation(value = "审批", notes = "审批")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "外发加工费结算单", businessType = BusinessType.HANDLE)
    @PostMapping("/approval")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult approval(@RequestBody ManManufactureOutsourceSettle outsourceSettle) {
        if (ArrayUtil.isEmpty(outsourceSettle.getManufactureOutsourceSettleSidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(outsourceSettle.getHandleStatus())
                && StrUtil.isBlank(outsourceSettle.getBusinessType())) {
            throw new BaseException("参数缺失");
        }
        return toAjax(manManufactureOutsourceSettleService.approval(outsourceSettle));
    }

    /**
     * 作废-外发加工费结算单
     */
    @ApiOperation(value = "作废-外发加工费结算单", notes = "作废-外发加工费结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "外发加工费结算单", businessType = BusinessType.CANCEL)
    @PostMapping("/cancellation")
    public AjaxResult cancellation(Long manufactureOutsourceSettleSid) {
        if (manufactureOutsourceSettleSid == null) {
            throw new BaseException("参数缺失");
        }
        return toAjax(manManufactureOutsourceSettleService.cancellationManufactureOutsourceSettleById(manufactureOutsourceSettleSid));
    }

    /**
     * 查询页面提交前校验-外发加工费结算单
     */
    @ApiOperation(value = "查询页面提交前校验-外发加工费结算单", notes = "查询页面提交前校验-外发加工费结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/verify")
    public AjaxResult verify(Long manufactureOutsourceSettleSid, String handleStatus) {
        if (manufactureOutsourceSettleSid == null) {
            throw new BaseException("参数缺失");
        }
        ManManufactureOutsourceSettle settle = manManufactureOutsourceSettleService.selectManManufactureOutsourceSettleById(manufactureOutsourceSettleSid);
        if (settle == null) {
            return AjaxResult.error("数据已被更新，请刷新页面！");
        }
        return AjaxResult.success(manManufactureOutsourceSettleService.verify(settle));
    }

    /**
     * 新建编辑页面提交前校验-外发加工费结算单
     */
    @ApiOperation(value = "新建编辑页面提交前校验-外发加工费结算单", notes = "新建编辑页面提交前校验-外发加工费结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/verifySubmit")
    public AjaxResult verifySubmit(@RequestBody ManManufactureOutsourceSettle settle) {
        return AjaxResult.success(manManufactureOutsourceSettleService.verify(settle));
    }

    /**
     * 外发加工费结算单明细获取加工价格
     */
    @ApiOperation(value = "外发加工费结算单明细获取加工价格", notes = "外发加工费结算单明细获取加工价格")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOutsourceSettleItem.class))
    @PostMapping("/getPrice")
    public AjaxResult itemGetPrice(@RequestBody ManManufactureOutsourceSettle settle) {
        if (settle == null) {
            return AjaxResult.success(new AjaxResult());
        }
        try {
            settle = manManufactureOutsourceSettleService.itemGetPrice(settle, true);
        } catch (Exception e) {
            logger.error("加工费结算单明细获取最新加工采购价报错");
        }
        return AjaxResult.success(settle.getManManufactureOutsourceSettleItemList());
    }
}
