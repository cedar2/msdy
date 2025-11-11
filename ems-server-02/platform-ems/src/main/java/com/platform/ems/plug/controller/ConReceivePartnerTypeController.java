package com.platform.ems.plug.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
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
import com.platform.ems.plug.domain.ConReceivePartnerType;
import com.platform.ems.plug.service.IConReceivePartnerTypeService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 收货方类型Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/receive/partner/type")
@Api(tags = "收货方类型")
public class ConReceivePartnerTypeController extends BaseController {

    @Autowired
    private IConReceivePartnerTypeService conReceivePartnerTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询收货方类型列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询收货方类型列表", notes = "查询收货方类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConReceivePartnerType.class))
    public TableDataInfo list(@RequestBody ConReceivePartnerType conReceivePartnerType) {
        startPage(conReceivePartnerType);
        List<ConReceivePartnerType> list = conReceivePartnerTypeService.selectConReceivePartnerTypeList(conReceivePartnerType);
        return getDataTable(list);
    }


    @PostMapping("/getList")
    @ApiOperation(value = "查询收货方类型列表", notes = "查询收货方类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConReceivePartnerType.class))
    public AjaxResult getlist() {
        List<ConReceivePartnerType> list = conReceivePartnerTypeService.getList();
        return AjaxResult.success(list);
    }

    /**
     * 导出收货方类型列表
     */
    @Log(title = "收货方类型", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出收货方类型列表", notes = "导出收货方类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConReceivePartnerType conReceivePartnerType) throws IOException {
        List<ConReceivePartnerType> list = conReceivePartnerTypeService.selectConReceivePartnerTypeList(conReceivePartnerType);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConReceivePartnerType> util = new ExcelUtil<>(ConReceivePartnerType.class, dataMap);
        util.exportExcel(response, list, "收货方类型");
    }

    /**
     * 导入收货方类型
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入收货方类型", notes = "导入收货方类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConReceivePartnerType> util = new ExcelUtil<>(ConReceivePartnerType.class);
        List<ConReceivePartnerType> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conReceivePartnerType -> {
                conReceivePartnerTypeService.insertConReceivePartnerType(conReceivePartnerType);
                i++;
            });
        } catch (Exception e) {
            lose = listSize - i;
            msg = StrUtil.format("前{}条数据导入成功，失败{}条,导入成功的数据请勿重复导入", i, lose);
        }
        if (StrUtil.isEmpty(msg)) {
            msg = "导入成功";
        }
        return AjaxResult.success(msg);
    }


    @ApiOperation(value = "下载收货方类型导入模板", notes = "下载收货方类型导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConReceivePartnerType> util = new ExcelUtil<ConReceivePartnerType>(ConReceivePartnerType.class);
        util.importTemplateExcel(response, "收货方类型导入模板");
    }


    /**
     * 获取收货方类型详细信息
     */
    @ApiOperation(value = "获取收货方类型详细信息", notes = "获取收货方类型详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConReceivePartnerType.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conReceivePartnerTypeService.selectConReceivePartnerTypeById(sid));
    }

    /**
     * 新增收货方类型
     */
    @ApiOperation(value = "新增收货方类型", notes = "新增收货方类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "收货方类型", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConReceivePartnerType conReceivePartnerType) {
        return toAjax(conReceivePartnerTypeService.insertConReceivePartnerType(conReceivePartnerType));
    }

    /**
     * 修改收货方类型
     */
    @ApiOperation(value = "修改收货方类型", notes = "修改收货方类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "收货方类型", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConReceivePartnerType conReceivePartnerType) {
        return toAjax(conReceivePartnerTypeService.updateConReceivePartnerType(conReceivePartnerType));
    }

    /**
     * 变更收货方类型
     */
    @ApiOperation(value = "变更收货方类型", notes = "变更收货方类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "收货方类型", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConReceivePartnerType conReceivePartnerType) {
        return toAjax(conReceivePartnerTypeService.changeConReceivePartnerType(conReceivePartnerType));
    }

    /**
     * 删除收货方类型
     */
    @ApiOperation(value = "删除收货方类型", notes = "删除收货方类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "收货方类型", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conReceivePartnerTypeService.deleteConReceivePartnerTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "收货方类型", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConReceivePartnerType conReceivePartnerType) {
        return AjaxResult.success(conReceivePartnerTypeService.changeStatus(conReceivePartnerType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "收货方类型", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConReceivePartnerType conReceivePartnerType) {
        conReceivePartnerType.setConfirmDate(new Date());
        conReceivePartnerType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conReceivePartnerType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conReceivePartnerTypeService.check(conReceivePartnerType));
    }

}
