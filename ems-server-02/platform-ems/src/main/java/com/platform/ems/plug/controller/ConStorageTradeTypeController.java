package com.platform.ems.plug.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
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
import com.platform.ems.plug.domain.ConStorageTradeType;
import com.platform.ems.plug.service.IConStorageTradeTypeService;
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
 * 交易类型Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/storage/trade/type")
@Api(tags = "交易类型")
public class ConStorageTradeTypeController extends BaseController {

    @Autowired
    private IConStorageTradeTypeService conStorageTradeTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询交易类型列表
     */
    @PreAuthorize(hasPermi = "ems:storageTradeType:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询交易类型列表", notes = "查询交易类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConStorageTradeType.class))
    public TableDataInfo list(@RequestBody ConStorageTradeType conStorageTradeType) {
        startPage(conStorageTradeType);
        List<ConStorageTradeType> list = conStorageTradeTypeService.selectConStorageTradeTypeList(conStorageTradeType);
        return getDataTable(list);
    }

    /**
     * 导出交易类型列表
     */
    @PreAuthorize(hasPermi = "ems:storageTradeType:export")
    @Log(title = "交易类型", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出交易类型列表", notes = "导出交易类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConStorageTradeType conStorageTradeType) throws IOException {
        List<ConStorageTradeType> list = conStorageTradeTypeService.selectConStorageTradeTypeList(conStorageTradeType);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConStorageTradeType> util = new ExcelUtil<ConStorageTradeType>(ConStorageTradeType.class, dataMap);
        util.exportExcel(response, list, "交易类型" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入交易类型
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入交易类型", notes = "导入交易类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConStorageTradeType> util = new ExcelUtil<ConStorageTradeType>(ConStorageTradeType.class);
        List<ConStorageTradeType> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conStorageTradeType -> {
                conStorageTradeTypeService.insertConStorageTradeType(conStorageTradeType);
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


    @ApiOperation(value = "下载交易类型导入模板", notes = "下载交易类型导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConStorageTradeType> util = new ExcelUtil<ConStorageTradeType>(ConStorageTradeType.class);
        util.importTemplateExcel(response, "交易类型导入模板");
    }


    /**
     * 获取交易类型详细信息
     */
    @ApiOperation(value = "获取交易类型详细信息", notes = "获取交易类型详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConStorageTradeType.class))
    @PreAuthorize(hasPermi = "ems:storageTradeType:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conStorageTradeTypeService.selectConStorageTradeTypeById(sid));
    }

    /**
     * 新增交易类型
     */
    @ApiOperation(value = "新增交易类型", notes = "新增交易类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:storageTradeType:add")
    @Log(title = "交易类型", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConStorageTradeType conStorageTradeType) {
        return toAjax(conStorageTradeTypeService.insertConStorageTradeType(conStorageTradeType));
    }

    /**
     * 修改交易类型
     */
    @ApiOperation(value = "修改交易类型", notes = "修改交易类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:storageTradeType:edit")
    @Log(title = "交易类型", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConStorageTradeType conStorageTradeType) {
        return toAjax(conStorageTradeTypeService.updateConStorageTradeType(conStorageTradeType));
    }

    /**
     * 变更交易类型
     */
    @ApiOperation(value = "变更交易类型", notes = "变更交易类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:storageTradeType:change")
    @Log(title = "交易类型", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConStorageTradeType conStorageTradeType) {
        return toAjax(conStorageTradeTypeService.changeConStorageTradeType(conStorageTradeType));
    }

    /**
     * 删除交易类型
     */
    @ApiOperation(value = "删除交易类型", notes = "删除交易类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:storageTradeType:remove")
    @Log(title = "交易类型", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conStorageTradeTypeService.deleteConStorageTradeTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "交易类型", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:storageTradeType:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConStorageTradeType conStorageTradeType) {
        return AjaxResult.success(conStorageTradeTypeService.changeStatus(conStorageTradeType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:storageTradeType:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "交易类型", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConStorageTradeType conStorageTradeType) {
        conStorageTradeType.setConfirmDate(new Date());
        conStorageTradeType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conStorageTradeType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conStorageTradeTypeService.check(conStorageTradeType));
    }

}
