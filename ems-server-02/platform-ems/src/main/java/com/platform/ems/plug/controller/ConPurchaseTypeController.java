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
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConPurchaseType;
import com.platform.ems.plug.service.IConPurchaseTypeService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 采购类型Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/purchase/type")
@Api(tags = "采购类型")
public class ConPurchaseTypeController extends BaseController {

    @Autowired
    private IConPurchaseTypeService conPurchaseTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询采购类型列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询采购类型列表", notes = "查询采购类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPurchaseType.class))
    public TableDataInfo list(@RequestBody ConPurchaseType conPurchaseType) {
        startPage(conPurchaseType);
        List<ConPurchaseType> list = conPurchaseTypeService.selectConPurchaseTypeList(conPurchaseType);
        return getDataTable(list);
    }

    /**
     * 导出采购类型列表
     */
    @Log(title = "采购类型", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出采购类型列表", notes = "导出采购类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConPurchaseType conPurchaseType) throws IOException {
        List<ConPurchaseType> list = conPurchaseTypeService.selectConPurchaseTypeList(conPurchaseType);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConPurchaseType> util = new ExcelUtil<>(ConPurchaseType.class, dataMap);
        util.exportExcel(response, list, "采购类型");
    }

    /**
     * 导入采购类型
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入采购类型", notes = "导入采购类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConPurchaseType> util = new ExcelUtil<ConPurchaseType>(ConPurchaseType.class);
        List<ConPurchaseType> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conPurchaseType -> {
                conPurchaseTypeService.insertConPurchaseType(conPurchaseType);
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


    @ApiOperation(value = "下载采购类型导入模板", notes = "下载采购类型导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConPurchaseType> util = new ExcelUtil<ConPurchaseType>(ConPurchaseType.class);
        util.importTemplateExcel(response, "采购类型导入模板");
    }


    /**
     * 获取采购类型详细信息
     */
    @ApiOperation(value = "获取采购类型详细信息", notes = "获取采购类型详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPurchaseType.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conPurchaseTypeService.selectConPurchaseTypeById(sid));
    }

    /**
     * 新增采购类型
     */
    @ApiOperation(value = "新增采购类型", notes = "新增采购类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购类型", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConPurchaseType conPurchaseType) {
        return toAjax(conPurchaseTypeService.insertConPurchaseType(conPurchaseType));
    }

    /**
     * 修改采购类型
     */
    @ApiOperation(value = "修改采购类型", notes = "修改采购类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购类型", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConPurchaseType conPurchaseType) {
        return toAjax(conPurchaseTypeService.updateConPurchaseType(conPurchaseType));
    }

    /**
     * 变更采购类型
     */
    @ApiOperation(value = "变更采购类型", notes = "变更采购类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购类型", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConPurchaseType conPurchaseType) {
        return toAjax(conPurchaseTypeService.changeConPurchaseType(conPurchaseType));
    }

    /**
     * 删除采购类型
     */
    @ApiOperation(value = "删除采购类型", notes = "删除采购类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购类型", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conPurchaseTypeService.deleteConPurchaseTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购类型", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConPurchaseType conPurchaseType) {
        return AjaxResult.success(conPurchaseTypeService.changeStatus(conPurchaseType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购类型", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConPurchaseType conPurchaseType) {
        conPurchaseType.setConfirmDate(new Date());
        conPurchaseType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conPurchaseType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conPurchaseTypeService.check(conPurchaseType));
    }

    @PostMapping("/getConPurchaseTypeList")
    @ApiOperation(value = "采购类型下拉列表", notes = "采购类型下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPurchaseType.class))
    public AjaxResult getConPurchaseTypeList() {
        ConPurchaseType conPurchaseType = new ConPurchaseType();
        conPurchaseType.setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS);
        String clientType = ApiThreadLocalUtil.get().getSysUser().getClientType();
        if (StrUtil.isNotBlank(clientType) && !ConstantsEms.CLIENT_TYPE_GMYT.equals(clientType)) {
            conPurchaseType.setClientType(ApiThreadLocalUtil.get().getSysUser().getClientType());
        }
        return AjaxResult.success(conPurchaseTypeService.getList(conPurchaseType));
    }

    @PostMapping("/getList")
    @ApiOperation(value = "采购类型下拉列表(带参)", notes = "采购类型下拉框列表(带参)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPurchaseType.class))
    public AjaxResult getList(@RequestBody ConPurchaseType conPurchaseType) {
        String clientType = ApiThreadLocalUtil.get().getSysUser().getClientType();
        if (StrUtil.isNotBlank(clientType) && !ConstantsEms.CLIENT_TYPE_GMYT.equals(clientType)) {
            conPurchaseType.setClientType(ApiThreadLocalUtil.get().getSysUser().getClientType());
        }
        return AjaxResult.success(conPurchaseTypeService.getList(conPurchaseType));
    }
}
