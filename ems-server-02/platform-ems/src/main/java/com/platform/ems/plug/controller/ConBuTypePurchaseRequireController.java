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
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConBuTypePurchaseRequire;
import com.platform.ems.plug.service.impl.ConBuTypePurchaseRequireServiceImpl;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 业务类型_采购申请单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/buTyp/purchase/require")
@Api(tags = "业务类型_采购申请单")
public class ConBuTypePurchaseRequireController extends BaseController {

    final ConBuTypePurchaseRequireServiceImpl conBuTypePurchaseRequireService;
    final ISystemDictDataService sysDictDataService;

    public ConBuTypePurchaseRequireController(ConBuTypePurchaseRequireServiceImpl conBuTypePurchaseRequireService,
                                              ISystemDictDataService sysDictDataService) {
        this.conBuTypePurchaseRequireService = conBuTypePurchaseRequireService;
        this.sysDictDataService = sysDictDataService;
    }

    /**
     * 查询业务类型_采购申请单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询业务类型_采购申请单列表",
                  notes = "查询业务类型_采购申请单列表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = ConBuTypePurchaseRequire.class))
    public TableDataInfo list(@RequestBody ConBuTypePurchaseRequire conBuTypePurchaseRequire) {
        startPage(conBuTypePurchaseRequire);
        List<ConBuTypePurchaseRequire> list = conBuTypePurchaseRequireService.selectConBuTypePurchaseRequireList(
                conBuTypePurchaseRequire);
        return getDataTable(list);
    }

    /**
     * 导出业务类型_采购申请单列表
     */
    @Log(title = "业务类型_采购申请单",
         businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出业务类型_采购申请单列表",
                  notes = "导出业务类型_采购申请单列表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response,
                       ConBuTypePurchaseRequire conBuTypePurchaseRequire) throws IOException {
        List<ConBuTypePurchaseRequire> list = conBuTypePurchaseRequireService.selectConBuTypePurchaseRequireList(
                conBuTypePurchaseRequire);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBuTypePurchaseRequire> util = new ExcelUtil<>(ConBuTypePurchaseRequire.class,
                                                                   dataMap);
        util.exportExcel(response, list, "业务类型_采购申请单");
    }

    /**
     * 导入业务类型_采购申请单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入业务类型_采购申请单",
                  notes = "导入业务类型_采购申请单")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConBuTypePurchaseRequire> util = new ExcelUtil<>(ConBuTypePurchaseRequire.class);
        List<ConBuTypePurchaseRequire> list = util.importExcel(file.getInputStream());
        int listSize = list.size();
        AtomicInteger i = new AtomicInteger(0);
        int lose;
        String msg = "";
        try {
            list.forEach(conBuTypePurchaseRequire -> {
                conBuTypePurchaseRequireService.insertConBuTypePurchaseRequire(conBuTypePurchaseRequire);
                i.incrementAndGet();
            });
        } catch (Exception e) {
            lose = listSize - i.get();
            msg = StrUtil.format("前{}条数据导入成功，失败{}条,导入成功的数据请勿重复导入", i, lose);
        }
        if (StrUtil.isEmpty(msg)) {
            msg = "导入成功";
        }
        return AjaxResult.success(msg);
    }


    @ApiOperation(value = "下载业务类型_采购申请单导入模板",
                  notes = "下载业务类型_采购申请单导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConBuTypePurchaseRequire> util = new ExcelUtil<>(ConBuTypePurchaseRequire.class);
        util.importTemplateExcel(response, "业务类型_采购申请单导入模板");
    }


    /**
     * 获取业务类型_采购申请单详细信息
     */
    @ApiOperation(value = "获取业务类型_采购申请单详细信息",
                  notes = "获取业务类型_采购申请单详细信息")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = ConBuTypePurchaseRequire.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBuTypePurchaseRequireService.selectConBuTypePurchaseRequireById(sid));
    }

    /**
     * 新增业务类型_采购申请单
     */
    @ApiOperation(value = "新增业务类型_采购申请单",
                  notes = "新增业务类型_采购申请单")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "业务类型_采购申请单",
         businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBuTypePurchaseRequire conBuTypePurchaseRequire) {
        return toAjax(conBuTypePurchaseRequireService.insertConBuTypePurchaseRequire(conBuTypePurchaseRequire));
    }

    /**
     * 修改业务类型_采购申请单
     */
    @ApiOperation(value = "修改业务类型_采购申请单",
                  notes = "修改业务类型_采购申请单")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "业务类型_采购申请单",
         businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConBuTypePurchaseRequire conBuTypePurchaseRequire) {
        return toAjax(conBuTypePurchaseRequireService.updateConBuTypePurchaseRequire(conBuTypePurchaseRequire));
    }

    /**
     * 变更业务类型_采购申请单
     */
    @ApiOperation(value = "变更业务类型_采购申请单",
                  notes = "变更业务类型_采购申请单")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "业务类型_采购申请单",
         businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBuTypePurchaseRequire conBuTypePurchaseRequire) {
        return toAjax(conBuTypePurchaseRequireService.changeConBuTypePurchaseRequire(conBuTypePurchaseRequire));
    }

    /**
     * 删除业务类型_采购申请单
     */
    @ApiOperation(value = "删除业务类型_采购申请单",
                  notes = "删除业务类型_采购申请单")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "业务类型_采购申请单",
         businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuTypePurchaseRequireService.deleteConBuTypePurchaseRequireByIds(sids));
    }

    @ApiOperation(value = "启用停用接口",
                  notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "业务类型_采购申请单",
         businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBuTypePurchaseRequire conBuTypePurchaseRequire) {
        return AjaxResult.success(conBuTypePurchaseRequireService.changeStatus(conBuTypePurchaseRequire));
    }

    @ApiOperation(value = "确认",
                  notes = "确认")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "业务类型_采购申请单",
         businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBuTypePurchaseRequire conBuTypePurchaseRequire) {
        conBuTypePurchaseRequire.setConfirmDate(new Date());
        conBuTypePurchaseRequire.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBuTypePurchaseRequire.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBuTypePurchaseRequireService.check(conBuTypePurchaseRequire));
    }

    /**
     * 业务类型_采购申请单下拉框列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "业务类型_采购申请单下拉框列表",
                  notes = "业务类型_采购申请单下拉框列表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = ConBuTypePurchaseRequire.class))
    public AjaxResult getList(@RequestBody ConBuTypePurchaseRequire conBuTypePurchaseRequire) {
        return AjaxResult.success(conBuTypePurchaseRequireService.selectConBuTypePurchaseRequireList(
                conBuTypePurchaseRequire));
    }
}
