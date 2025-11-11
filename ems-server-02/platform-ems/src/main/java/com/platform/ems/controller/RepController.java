package com.platform.ems.controller;

import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.RepBusinessRemind;
import com.platform.ems.service.IRepService;
import com.platform.ems.task.*;
import com.platform.ems.task.report.RepFinanceStatusTask;
import com.platform.ems.task.report.RepManufactureStatisticTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 看板数据接口
 *
 * @author chenkw
 * @date 2022-04-26
 */
@RestController
@RequestMapping("/report")
@Api(tags = "看板数据/工作台自动任务接口")
public class RepController extends BaseController {

    @Autowired
    private IRepService repService;
    @Autowired
    private RepManufactureStatisticTask repManufactureStatisticTask;
    @Autowired
    private RepFinanceStatusTask repFinanceStatusTask;
    @Autowired
    private ContractWarningTask contractWarningTask;
    @Autowired
    private FinBookWarningTask finBookWarningTask;
    @Autowired
    private BasMaterialWarningTask basMaterialWarningTask;
    @Autowired
    private ManOrderWarningTask manOrderWarningTask;


    @PostMapping("/business/remind")
    @ApiOperation(value = "已逾期与即将逾期看板数据接口", notes = "已逾期与即将逾期看板数据接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemind.class))
    public AjaxResult remind() {
        return AjaxResult.success(repService.getBusinessRemind());
    }

    @PostMapping("/finance/status")
    @ApiOperation(value = "财务状况看板数据接口", notes = "财务状况看板数据接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemind.class))
    public AjaxResult finance() {
        return AjaxResult.success(repService.getFinanceStatus());
    }

    @PostMapping("/auto/manufacture/update/status")
    @ApiOperation(value = "非允许不可执行（刷新）生产状况数据表更新", notes = "非允许不可执行（刷新）生产状况数据表更新")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemind.class))
    public AjaxResult autoManufactureStatus() {
        repManufactureStatisticTask.setRepManufactureStatistic();
        return AjaxResult.success();
    }

    @PostMapping("/auto/manufacture/update/toOver")
    @ApiOperation(value = "非允许不可执行（刷新并发送）生产订单的即将逾期与已到期表数据更新", notes = "非允许不可执行（刷新并发送）生产订单的即将逾期与已到期表数据更新")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemind.class))
    public AjaxResult autoManufactureToOver() {
        manOrderWarningTask.earlyWarningOrder();
        manOrderWarningTask.earlyWarningProduct();
        manOrderWarningTask.earlyWarningProcess();
        manOrderWarningTask.earlyWarningConcernTask();
        return AjaxResult.success();
    }

    @PostMapping("/auto/finance/update/status")
    @ApiOperation(value = "非允许不可执行（刷新）财务状况数据表更新", notes = "非允许不可执行（刷新）财务状况数据表更新")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemind.class))
    public AjaxResult autoFinance() {
        repFinanceStatusTask.setRepFinanceStatus();
        return AjaxResult.success();
    }

    @PostMapping("/auto/contract/update/toOver")
    @ApiOperation(value = "非允许不可执行（刷新）合同的即将逾期与已到期表数据更新", notes = "非允许不可执行（刷新）合同的即将逾期与已到期表数据更新")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemind.class))
    public AjaxResult autoContract() {
        contractWarningTask.contractWarning();
        return AjaxResult.success();
    }

    @PostMapping("/auto/contract/sentNotice")
    @ApiOperation(value = "非允许不可执行（测试）合同的即将逾期与已到期邮件企微通知", notes = "非允许不可执行（刷新）合同的即将逾期与已到期邮件企微通知")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemind.class))
    public AjaxResult autoContractSent() {
        contractWarningTask.sentNotice();
        return AjaxResult.success();
    }

    @PostMapping("/auto/material/update/gyd")
    @ApiOperation(value = "非允许不可执行（刷新并发送邮件）未上传工艺单的商品", notes = "非允许不可执行（刷新并发送邮件）未上传工艺单的商品")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepBusinessRemind.class))
    public AjaxResult autoMaterialGyd() {
        basMaterialWarningTask.gydWarning();
        return AjaxResult.success();
    }
}
