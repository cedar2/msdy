package com.platform.ems.plug.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConBuTypeInout;
import com.platform.ems.plug.service.IConBuTypeInoutService;
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
import java.util.List;
import java.util.Map;

/**
 * 业务类型-出入库Controller
 *
 * @author linhongwei
 * @date 2022-10-09
 */
@RestController
@RequestMapping("/conBuTypeInout")
@Api(tags = "业务类型-出入库")
public class ConBuTypeInoutController extends BaseController {

    @Autowired
    private IConBuTypeInoutService conBuTypeInoutService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询业务类型-出入库列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询业务类型-出入库列表", notes = "查询业务类型-出入库列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeInout.class))
    public TableDataInfo list(@RequestBody ConBuTypeInout conBuTypeInout) {
        startPage(conBuTypeInout);
        List<ConBuTypeInout> list = conBuTypeInoutService.selectConBuTypeInoutList(conBuTypeInout);
        return getDataTable(list);
    }

    /**
     * 下拉框接口
     */
    @PostMapping("/getList")
    @ApiOperation(value = "查询业务类型-出入库列表下拉框接口", notes = "查询业务类型-出入库列表下拉框接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeInout.class))
    public AjaxResult getList(@RequestBody ConBuTypeInout conBuTypeInout) {
        conBuTypeInout.setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS);
        return AjaxResult.success(conBuTypeInoutService.getConBuTypeInoutList(conBuTypeInout));
    }

    /**
     * 导出业务类型-出入库列表
     */
    @Log(title = "业务类型-出入库", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出业务类型-出入库列表", notes = "导出业务类型-出入库列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBuTypeInout conBuTypeInout) throws IOException {
        List<ConBuTypeInout> list = conBuTypeInoutService.selectConBuTypeInoutList(conBuTypeInout);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBuTypeInout> util = new ExcelUtil<>(ConBuTypeInout.class, dataMap);
        util.exportExcel(response, list, "所属业务类型-出入库");
    }


    /**
     * 获取业务类型-出入库详细信息
     */
    @ApiOperation(value = "获取业务类型-出入库详细信息", notes = "获取业务类型-出入库详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeInout.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBuTypeInoutService.selectConBuTypeInoutById(sid));
    }

    /**
     * 新增 业务类型-出入库
     */
    @ApiOperation(value = "新增业务类型-出入库", notes = "新增业务类型-出入库")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型-出入库", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid ConBuTypeInout conBuTypeInout) {
        return toAjax(conBuTypeInoutService.insertConBuTypeInout(conBuTypeInout));
    }

    @ApiOperation(value = "修改业务类型-出入库", notes = "修改业务类型-出入库")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型-出入库", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody ConBuTypeInout conBuTypeInout) {
        return toAjax(conBuTypeInoutService.updateConBuTypeInout(conBuTypeInout));
    }

    /**
     * 变更业务类型-出入库
     */
    @ApiOperation(value = "变更业务类型-出入库", notes = "变更业务类型-出入库")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型-出入库", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBuTypeInout conBuTypeInout) {
        return toAjax(conBuTypeInoutService.changeConBuTypeInout(conBuTypeInout));
    }

    /**
     * 删除业务类型-出入库
     */
    @ApiOperation(value = "删除业务类型-出入库", notes = "删除业务类型-出入库")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型-出入库", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuTypeInoutService.deleteConBuTypeInoutByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型-出入库", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBuTypeInout conBuTypeInout) {
        return AjaxResult.success(conBuTypeInoutService.changeStatus(conBuTypeInout));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型-出入库", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody ConBuTypeInout conBuTypeInout) {
        return toAjax(conBuTypeInoutService.check(conBuTypeInout));
    }

}
