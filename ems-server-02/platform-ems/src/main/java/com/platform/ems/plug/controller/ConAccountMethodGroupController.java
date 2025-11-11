package com.platform.ems.plug.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConAccountMethodGroup;
import com.platform.ems.plug.service.IConAccountMethodGroupService;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 收付款方式组合Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/account/method/group")
@Api(tags = "收付款方式组合")
public class ConAccountMethodGroupController extends BaseController {

    @Autowired
    private IConAccountMethodGroupService conAccountMethodGroupService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询收付款方式组合列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询收付款方式组合列表", notes = "查询收付款方式组合列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConAccountMethodGroup.class))
    public TableDataInfo list(@RequestBody ConAccountMethodGroup conAccountMethodGroup) {
        startPage(conAccountMethodGroup);
        List<ConAccountMethodGroup> list = conAccountMethodGroupService.selectConAccountMethodGroupList(conAccountMethodGroup);
        return getDataTable(list);
    }

    @ApiOperation(value = "获取收付款方式组合列表", notes = "获取收付款方式组合列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConAccountMethodGroup.class))
    @PostMapping("/getList")
    public AjaxResult getList() {
        ConAccountMethodGroup request = new ConAccountMethodGroup().setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS);
        List<ConAccountMethodGroup> list = conAccountMethodGroupService.selectConAccountMethodGroupList(request);
        return AjaxResult.success(list);
    }


    /**
     * 导出收付款方式组合列表
     */
    @ApiOperation(value = "导出收付款方式组合列表", notes = "导出收付款方式组合列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConAccountMethodGroup conAccountMethodGroup) throws IOException {
        List<ConAccountMethodGroup> list = conAccountMethodGroupService.selectConAccountMethodGroupList(conAccountMethodGroup);
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(o -> {
                o.setAdvanceRate(new BigDecimal(o.getAdvanceRate()).multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP).toString());
                o.setMiddleRate(new BigDecimal(o.getMiddleRate()).multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP).toString());
                o.setRemainRate(new BigDecimal(o.getRemainRate()).multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP).toString());
            });
        }
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConAccountMethodGroup> util = new ExcelUtil<>(ConAccountMethodGroup.class, dataMap);
        util.exportExcel(response, list, "收付款方式组合");
    }


    /**
     * 获取收付款方式组合详细信息
     */
    @ApiOperation(value = "获取收付款方式组合详细信息", notes = "获取收付款方式组合详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConAccountMethodGroup.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conAccountMethodGroupService.selectConAccountMethodGroupById(sid));
    }

    /**
     * 新增收付款方式组合
     */
    @ApiOperation(value = "新增收付款方式组合", notes = "新增收付款方式组合")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConAccountMethodGroup conAccountMethodGroup) {
        return toAjax(conAccountMethodGroupService.insertConAccountMethodGroup(conAccountMethodGroup));
    }

    /**
     * 修改收付款方式组合
     */
    @ApiOperation(value = "修改收付款方式组合", notes = "修改收付款方式组合")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConAccountMethodGroup conAccountMethodGroup) {
        return toAjax(conAccountMethodGroupService.updateConAccountMethodGroup(conAccountMethodGroup));
    }

    /**
     * 变更收付款方式组合
     */
    @ApiOperation(value = "变更收付款方式组合", notes = "变更收付款方式组合")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConAccountMethodGroup conAccountMethodGroup) {
        return toAjax(conAccountMethodGroupService.changeConAccountMethodGroup(conAccountMethodGroup));
    }

    /**
     * 删除收付款方式组合
     */
    @ApiOperation(value = "删除收付款方式组合", notes = "删除收付款方式组合")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conAccountMethodGroupService.deleteConAccountMethodGroupByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConAccountMethodGroup conAccountMethodGroup) {
        return AjaxResult.success(conAccountMethodGroupService.changeStatus(conAccountMethodGroup));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConAccountMethodGroup conAccountMethodGroup) {
        conAccountMethodGroup.setConfirmDate(new Date());
        conAccountMethodGroup.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conAccountMethodGroup.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conAccountMethodGroupService.check(conAccountMethodGroup));
    }

}
