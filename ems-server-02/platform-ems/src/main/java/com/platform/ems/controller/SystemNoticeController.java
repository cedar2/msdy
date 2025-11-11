package com.platform.ems.controller;

import cn.hutool.core.util.ArrayUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.ISystemDictDataService;

import com.platform.system.domain.SysNotice;
import com.platform.system.service.ISysNoticeService;
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
 * 通知公告Controller
 *
 * @author linhongwei
 * @date 2021-06-30
 */
@RestController
@RequestMapping("/sys/notice")
@Api(tags = "通知公告")
@SuppressWarnings("all")
public class SystemNoticeController extends BaseController {

    @Autowired
    private ISysNoticeService sysNoticeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询通知公告列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询通知公告列表", notes = "查询通知公告列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysNotice.class))
    public TableDataInfo list(@RequestBody SysNotice sysNotice) {
        startPage(sysNotice);
        List<SysNotice> list = sysNoticeService.selectSysNoticeList(sysNotice);
        return getDataTable(list);
    }

    @PostMapping("/getList")
    @ApiOperation(value = "获取首页通知公告列表", notes = "获取首页通知公告列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysNotice.class))
    public TableDataInfo getList(@RequestBody SysNotice sysNotice) {
        startPage(sysNotice);
        sysNotice.setStatus(ConstantsEms.ENABLE_STATUS);
        sysNotice.setHandleStatus(ConstantsEms.CHECK_STATUS);
        sysNotice.setToday(new Date());
        List<SysNotice> list = sysNoticeService.selectSysNoticeList(sysNotice);
        return getDataTable(list);
    }


    /**
     * 导出通知公告列表
     */
    @Log(title = "通知公告", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出通知公告列表", notes = "导出通知公告列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysNotice sysNotice) throws IOException {
        List<SysNotice> list = sysNoticeService.selectSysNoticeList(sysNotice);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SysNotice> util = new ExcelUtil<>(SysNotice.class, dataMap);
        util.exportExcel(response, list, "通知公告");
    }


    /**
     * 获取通知公告详细信息
     */
    @ApiOperation(value = "获取通知公告详细信息", notes = "获取通知公告详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysNotice.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long noticeSid) {
        if (noticeSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysNoticeService.selectSysNoticeById(noticeSid));
    }

    /**
     * 新增通知公告
     */
    @ApiOperation(value = "新增通知公告", notes = "新增通知公告")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SysNotice sysNotice) {
        return toAjax(sysNoticeService.insertSysNotice(sysNotice));
    }

    /**
     * 修改通知公告
     */
    @ApiOperation(value = "修改通知公告", notes = "修改通知公告")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid SysNotice sysNotice) {
        return toAjax(sysNoticeService.updateSysNotice(sysNotice));
    }

    /**
     * 变更通知公告
     */
    @ApiOperation(value = "变更通知公告", notes = "变更通知公告")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "通知公告", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid SysNotice sysNotice) {
        return toAjax(sysNoticeService.changeSysNotice(sysNotice));
    }

    /**
     * 删除通知公告
     */
    @ApiOperation(value = "删除通知公告", notes = "删除通知公告")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> noticeSids) {
        if (ArrayUtil.isEmpty(noticeSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysNoticeService.deleteSysNoticeByIds(noticeSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysNotice sysNotice) {
        return AjaxResult.success(sysNoticeService.changeStatus(sysNotice));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "通知公告", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody SysNotice sysNotice) {
        sysNotice.setConfirmDate(new Date());
        sysNotice.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        sysNotice.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(sysNoticeService.check(sysNotice));
    }

}
