package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.ems.constant.ConstantsEms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.util.ArrayUtil;

import javax.validation.Valid;

import com.platform.ems.plug.domain.ConBusinessChannel;
import com.platform.ems.plug.service.IConBusinessChannelService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 销售渠道/业务渠道Controller
 *
 * @author linhongwei
 * @date 2021-06-30
 */
@RestController
@RequestMapping("/ConBusinessChannel")
@Api(tags = "销售渠道/业务渠道")
public class ConBusinessChannelController extends BaseController {

    @Autowired
    private IConBusinessChannelService conBusinessChannelService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询销售渠道/业务渠道列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询销售渠道/业务渠道列表", notes = "查询销售渠道/业务渠道列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBusinessChannel.class))
    public TableDataInfo list(@RequestBody ConBusinessChannel conBusinessChannel) {
        startPage(conBusinessChannel);
        List<ConBusinessChannel> list = conBusinessChannelService.selectConBusinessChannelList(conBusinessChannel);
        return getDataTable(list);
    }

    /**
     * 导出销售渠道/业务渠道列表
     */
    @ApiOperation(value = "导出销售渠道/业务渠道列表", notes = "导出销售渠道/业务渠道列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBusinessChannel conBusinessChannel) throws IOException {
        List<ConBusinessChannel> list = conBusinessChannelService.selectConBusinessChannelList(conBusinessChannel);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBusinessChannel> util = new ExcelUtil<>(ConBusinessChannel.class, dataMap);
        util.exportExcel(response, list, "销售渠道(业务渠道)");
    }


    /**
     * 获取销售渠道/业务渠道详细信息
     */
    @ApiOperation(value = "获取销售渠道/业务渠道详细信息", notes = "获取销售渠道/业务渠道详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBusinessChannel.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBusinessChannelService.selectConBusinessChannelById(sid));
    }

    /**
     * 新增销售渠道/业务渠道
     */
    @ApiOperation(value = "新增销售渠道/业务渠道", notes = "新增销售渠道/业务渠道")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBusinessChannel conBusinessChannel) {
        return toAjax(conBusinessChannelService.insertConBusinessChannel(conBusinessChannel));
    }

    /**
     * 修改销售渠道/业务渠道
     */
    @ApiOperation(value = "修改销售渠道/业务渠道", notes = "修改销售渠道/业务渠道")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConBusinessChannel conBusinessChannel) {
        return toAjax(conBusinessChannelService.updateConBusinessChannel(conBusinessChannel));
    }

    /**
     * 变更销售渠道/业务渠道
     */
    @ApiOperation(value = "变更销售渠道/业务渠道", notes = "变更销售渠道/业务渠道")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBusinessChannel conBusinessChannel) {
        return toAjax(conBusinessChannelService.changeConBusinessChannel(conBusinessChannel));
    }

    /**
     * 删除销售渠道/业务渠道
     */
    @ApiOperation(value = "删除销售渠道/业务渠道", notes = "删除销售渠道/业务渠道")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBusinessChannelService.deleteConBusinessChannelByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBusinessChannel conBusinessChannel) {
        return AjaxResult.success(conBusinessChannelService.changeStatus(conBusinessChannel));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBusinessChannel conBusinessChannel) {
        conBusinessChannel.setConfirmDate(new Date());
        conBusinessChannel.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBusinessChannel.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBusinessChannelService.check(conBusinessChannel));
    }

    /**
     * 款项类别下拉框列表
     */
    @PostMapping("/getConBusinessChannelList")
    @ApiOperation(value = "下拉框列表不带参数", notes = "下拉框列表不带参数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBusinessChannel.class))
    public AjaxResult getConBusinessChannelList() {
        return AjaxResult.success(conBusinessChannelService.getConBusinessChannelList(new ConBusinessChannel()
                .setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS)));
    }

    /**
     * 款项类别下拉框列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "下拉框列表带参数", notes = "下拉框列表带参数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBusinessChannel.class))
    public AjaxResult getList(@RequestBody ConBusinessChannel conBusinessChannel) {
        return AjaxResult.success(conBusinessChannelService.getConBusinessChannelList(conBusinessChannel));
    }

}
