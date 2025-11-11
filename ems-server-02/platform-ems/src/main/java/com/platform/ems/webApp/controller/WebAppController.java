package com.platform.ems.webApp.controller;

import com.platform.common.core.domain.AjaxResult;
import com.platform.system.domain.SysNotice;
import com.platform.system.service.ISysNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:移动端
 * @author: Hu JJ
 * @date: 2021-10-08
 */
@RestController
@RequestMapping("/webapp")
@Api(tags = "移动端")
public class WebAppController {
    @Autowired
    private ISysNoticeService sysNoticeService;


    /**
     * 待办、预警消息条数
     */
    @ApiOperation(value = "待办、预警消息条数", notes = "待办、预警消息条数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysNotice.class))
    @PostMapping("/countMessage")
    public AjaxResult countMessage(SysNotice sysNotice) {
        return AjaxResult.success(sysNoticeService.countMessage(sysNotice));
    }
}
