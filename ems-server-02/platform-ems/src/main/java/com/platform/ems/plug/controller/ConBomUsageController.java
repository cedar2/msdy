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
import com.platform.ems.plug.domain.ConBomUsage;
import com.platform.ems.plug.service.IConBomUsageService;
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
 * BOM用途Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/bom/usage")
@Api(tags = "BOM用途")
public class ConBomUsageController extends BaseController {

    @Autowired
    private IConBomUsageService conBomUsageService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询BOM用途列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询BOM用途列表", notes = "查询BOM用途列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBomUsage.class))
    public TableDataInfo list(@RequestBody ConBomUsage conBomUsage) {
        startPage(conBomUsage);
        List<ConBomUsage> list = conBomUsageService.selectConBomUsageList(conBomUsage);
        return getDataTable(list);
    }

    /**
     * 导出BOM用途列表
     */
    @Log(title = "BOM用途", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出BOM用途列表", notes = "导出BOM用途列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBomUsage conBomUsage) throws IOException {
        List<ConBomUsage> list = conBomUsageService.selectConBomUsageList(conBomUsage);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBomUsage> util = new ExcelUtil<>(ConBomUsage.class, dataMap);
        util.exportExcel(response, list, "BOM用途");
    }

    /**
     * 导入BOM用途
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入BOM用途", notes = "导入BOM用途")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConBomUsage> util = new ExcelUtil<>(ConBomUsage.class);
        List<ConBomUsage> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conBomUsage -> {
                conBomUsageService.insertConBomUsage(conBomUsage);
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


    @ApiOperation(value = "下载BOM用途导入模板", notes = "下载BOM用途导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConBomUsage> util = new ExcelUtil<>(ConBomUsage.class);
        util.importTemplateExcel(response, "BOM用途导入模板");
    }


    /**
     * 获取BOM用途详细信息
     */
    @ApiOperation(value = "获取BOM用途详细信息", notes = "获取BOM用途详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBomUsage.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBomUsageService.selectConBomUsageById(sid));
    }

    /**
     * 新增BOM用途
     */
    @ApiOperation(value = "新增BOM用途", notes = "新增BOM用途")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "BOM用途", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBomUsage conBomUsage) {
        return toAjax(conBomUsageService.insertConBomUsage(conBomUsage));
    }

    /**
     * 修改BOM用途
     */
    @ApiOperation(value = "修改BOM用途", notes = "修改BOM用途")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "BOM用途", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConBomUsage conBomUsage) {
        return toAjax(conBomUsageService.updateConBomUsage(conBomUsage));
    }

    /**
     * 变更BOM用途
     */
    @ApiOperation(value = "变更BOM用途", notes = "变更BOM用途")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "BOM用途", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBomUsage conBomUsage) {
        return toAjax(conBomUsageService.changeConBomUsage(conBomUsage));
    }

    /**
     * 删除BOM用途
     */
    @ApiOperation(value = "删除BOM用途", notes = "删除BOM用途")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "BOM用途", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBomUsageService.deleteConBomUsageByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "BOM用途", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBomUsage conBomUsage) {
        return AjaxResult.success(conBomUsageService.changeStatus(conBomUsage));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "BOM用途", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBomUsage conBomUsage) {
        conBomUsage.setConfirmDate(new Date());
        conBomUsage.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBomUsage.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBomUsageService.check(conBomUsage));
    }

    @ApiOperation(value = "获取下拉框接口", notes = "获取下拉框接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/getList")
    public AjaxResult getList(@RequestBody ConBomUsage conBomUsage) {
        return AjaxResult.success(conBomUsageService.selectConBomUsageList(conBomUsage));
    }

}
