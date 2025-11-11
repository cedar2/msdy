package com.platform.ems.plug.controller;

import cn.hutool.core.util.ArrayUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConIntransitType;
import com.platform.ems.plug.service.IConIntransitTypeService;
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
 * 在途类型Controller
 *
 * @author linhongwei
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/intransit/type")
@Api(tags = "在途类型")
public class ConIntransitTypeController extends BaseController {

    @Autowired
    private IConIntransitTypeService conIntransitTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询在途类型列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询在途类型列表", notes = "查询在途类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConIntransitType.class))
    public TableDataInfo list(@RequestBody ConIntransitType conIntransitType) {
        startPage(conIntransitType);
        List<ConIntransitType> list = conIntransitTypeService.selectConIntransitTypeList(conIntransitType);
        return getDataTable(list);
    }

    /**
     * 导出在途类型列表
     */
    @Log(title = "在途类型", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出在途类型列表", notes = "导出在途类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConIntransitType conIntransitType) throws IOException {
        List<ConIntransitType> list = conIntransitTypeService.selectConIntransitTypeList(conIntransitType);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConIntransitType> util = new ExcelUtil<>(ConIntransitType.class, dataMap);
        util.exportExcel(response, list, "在途类型");
    }

    /**
     * 获取在途类型详细信息
     */
    @ApiOperation(value = "获取在途类型详细信息", notes = "获取在途类型详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConIntransitType.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conIntransitTypeService.selectConIntransitTypeById(sid));
    }

    /**
     * 新增在途类型
     */
    @ApiOperation(value = "新增在途类型", notes = "新增在途类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "在途类型", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConIntransitType conIntransitType) {
        return toAjax(conIntransitTypeService.insertConIntransitType(conIntransitType));
    }

    /**
     * 修改在途类型
     */
    @ApiOperation(value = "修改在途类型", notes = "修改在途类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "在途类型", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConIntransitType conIntransitType) {
        return toAjax(conIntransitTypeService.updateConIntransitType(conIntransitType));
    }

    /**
     * 变更在途类型
     */
    @ApiOperation(value = "变更在途类型", notes = "变更在途类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "在途类型", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConIntransitType conIntransitType) {
        return toAjax(conIntransitTypeService.changeConIntransitType(conIntransitType));
    }

    /**
     * 删除在途类型
     */
    @ApiOperation(value = "删除在途类型", notes = "删除在途类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "在途类型", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conIntransitTypeService.deleteConIntransitTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "在途类型", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConIntransitType conIntransitType) {
        return AjaxResult.success(conIntransitTypeService.changeStatus(conIntransitType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "在途类型", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConIntransitType conIntransitType) {
        conIntransitType.setConfirmDate(new Date());
        conIntransitType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conIntransitType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conIntransitTypeService.check(conIntransitType));
    }

}
