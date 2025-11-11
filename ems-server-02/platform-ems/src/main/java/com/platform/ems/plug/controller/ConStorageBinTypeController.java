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
import com.platform.ems.plug.domain.ConStorageBinType;
import com.platform.ems.plug.service.IConStorageBinTypeService;
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
 * 仓位存储类型Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/storage/bin/type")
@Api(tags = "仓位存储类型")
public class ConStorageBinTypeController extends BaseController {

    @Autowired
    private IConStorageBinTypeService conStorageBinTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询仓位存储类型列表
     */
    @PreAuthorize(hasPermi = "ems:storageBinType:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询仓位存储类型列表", notes = "查询仓位存储类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConStorageBinType.class))
    public TableDataInfo list(@RequestBody ConStorageBinType conStorageBinType) {
        startPage(conStorageBinType);
        List<ConStorageBinType> list = conStorageBinTypeService.selectConStorageBinTypeList(conStorageBinType);
        return getDataTable(list);
    }

    /**
     * 导出仓位存储类型列表
     */
    @PreAuthorize(hasPermi = "ems:storageBinType:export")
    @Log(title = "仓位存储类型", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出仓位存储类型列表", notes = "导出仓位存储类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConStorageBinType conStorageBinType) throws IOException {
        List<ConStorageBinType> list = conStorageBinTypeService.selectConStorageBinTypeList(conStorageBinType);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConStorageBinType> util = new ExcelUtil<ConStorageBinType>(ConStorageBinType.class, dataMap);
        util.exportExcel(response, list, "仓位存储类型" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入仓位存储类型
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入仓位存储类型", notes = "导入仓位存储类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConStorageBinType> util = new ExcelUtil<ConStorageBinType>(ConStorageBinType.class);
        List<ConStorageBinType> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conStorageBinType -> {
                conStorageBinTypeService.insertConStorageBinType(conStorageBinType);
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


    @ApiOperation(value = "下载仓位存储类型导入模板", notes = "下载仓位存储类型导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConStorageBinType> util = new ExcelUtil<ConStorageBinType>(ConStorageBinType.class);
        util.importTemplateExcel(response, "仓位存储类型导入模板");
    }


    /**
     * 获取仓位存储类型详细信息
     */
    @ApiOperation(value = "获取仓位存储类型详细信息", notes = "获取仓位存储类型详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConStorageBinType.class))
    @PreAuthorize(hasPermi = "ems:storageBinType:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conStorageBinTypeService.selectConStorageBinTypeById(sid));
    }

    /**
     * 新增仓位存储类型
     */
    @ApiOperation(value = "新增仓位存储类型", notes = "新增仓位存储类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:storageBinType:add")
    @Log(title = "仓位存储类型", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConStorageBinType conStorageBinType) {
        return toAjax(conStorageBinTypeService.insertConStorageBinType(conStorageBinType));
    }

    /**
     * 修改仓位存储类型
     */
    @ApiOperation(value = "修改仓位存储类型", notes = "修改仓位存储类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:storageBinType:edit")
    @Log(title = "仓位存储类型", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConStorageBinType conStorageBinType) {
        return toAjax(conStorageBinTypeService.updateConStorageBinType(conStorageBinType));
    }

    /**
     * 变更仓位存储类型
     */
    @ApiOperation(value = "变更仓位存储类型", notes = "变更仓位存储类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:storageBinType:change")
    @Log(title = "仓位存储类型", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConStorageBinType conStorageBinType) {
        return toAjax(conStorageBinTypeService.changeConStorageBinType(conStorageBinType));
    }

    /**
     * 删除仓位存储类型
     */
    @ApiOperation(value = "删除仓位存储类型", notes = "删除仓位存储类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:storageBinType:remove")
    @Log(title = "仓位存储类型", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conStorageBinTypeService.deleteConStorageBinTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "仓位存储类型", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:storageBinType:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConStorageBinType conStorageBinType) {
        return AjaxResult.success(conStorageBinTypeService.changeStatus(conStorageBinType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:storageBinType:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "仓位存储类型", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConStorageBinType conStorageBinType) {
        conStorageBinType.setConfirmDate(new Date());
        conStorageBinType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conStorageBinType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conStorageBinTypeService.check(conStorageBinType));
    }

}
