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
import com.platform.ems.plug.domain.ConReasonTypeStorage;
import com.platform.ems.plug.service.IConReasonTypeStorageService;
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
 * 原因类型(库存管理)Controller
 *
 * @author linhongwei
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/reason/type/storage")
@Api(tags = "原因类型(库存管理)")
public class ConReasonTypeStorageController extends BaseController {

    @Autowired
    private IConReasonTypeStorageService conReasonTypeStorageService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询原因类型(库存管理)列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询原因类型(库存管理)列表", notes = "查询原因类型(库存管理)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConReasonTypeStorage.class))
    public TableDataInfo list(@RequestBody ConReasonTypeStorage conReasonTypeStorage) {
        startPage(conReasonTypeStorage);
        List<ConReasonTypeStorage> list = conReasonTypeStorageService.selectConReasonTypeStorageList(conReasonTypeStorage);
        return getDataTable(list);
    }

    /**
     * 导出原因类型(库存管理)列表
     */
    @Log(title = "原因类型(库存管理)", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出原因类型(库存管理)列表", notes = "导出原因类型(库存管理)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConReasonTypeStorage conReasonTypeStorage) throws IOException {
        List<ConReasonTypeStorage> list = conReasonTypeStorageService.selectConReasonTypeStorageList(conReasonTypeStorage);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConReasonTypeStorage> util = new ExcelUtil<>(ConReasonTypeStorage.class, dataMap);
        util.exportExcel(response, list, "原因类型(库存管理)");
    }

    /**
     * 导入原因类型(库存管理)
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入原因类型(库存管理)", notes = "导入原因类型(库存管理)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConReasonTypeStorage> util = new ExcelUtil<>(ConReasonTypeStorage.class);
        List<ConReasonTypeStorage> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conReasonTypeStorage -> {
                conReasonTypeStorageService.insertConReasonTypeStorage(conReasonTypeStorage);
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


    @ApiOperation(value = "下载原因类型(库存管理)导入模板", notes = "下载原因类型(库存管理)导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConReasonTypeStorage> util = new ExcelUtil<ConReasonTypeStorage>(ConReasonTypeStorage.class);
        util.importTemplateExcel(response, "原因类型(库存管理)导入模板");
    }


    /**
     * 获取原因类型(库存管理)详细信息
     */
    @ApiOperation(value = "获取原因类型(库存管理)详细信息", notes = "获取原因类型(库存管理)详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConReasonTypeStorage.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conReasonTypeStorageService.selectConReasonTypeStorageById(sid));
    }

    /**
     * 新增原因类型(库存管理)
     */
    @ApiOperation(value = "新增原因类型(库存管理)", notes = "新增原因类型(库存管理)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "原因类型(库存管理)", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConReasonTypeStorage conReasonTypeStorage) {
        return toAjax(conReasonTypeStorageService.insertConReasonTypeStorage(conReasonTypeStorage));
    }

    /**
     * 修改原因类型(库存管理)
     */
    @ApiOperation(value = "修改原因类型(库存管理)", notes = "修改原因类型(库存管理)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "原因类型(库存管理)", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConReasonTypeStorage conReasonTypeStorage) {
        return toAjax(conReasonTypeStorageService.updateConReasonTypeStorage(conReasonTypeStorage));
    }

    /**
     * 变更原因类型(库存管理)
     */
    @ApiOperation(value = "变更原因类型(库存管理)", notes = "变更原因类型(库存管理)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "原因类型(库存管理)", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConReasonTypeStorage conReasonTypeStorage) {
        return toAjax(conReasonTypeStorageService.changeConReasonTypeStorage(conReasonTypeStorage));
    }

    /**
     * 删除原因类型(库存管理)
     */
    @ApiOperation(value = "删除原因类型(库存管理)", notes = "删除原因类型(库存管理)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "原因类型(库存管理)", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conReasonTypeStorageService.deleteConReasonTypeStorageByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "原因类型(库存管理)", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConReasonTypeStorage conReasonTypeStorage) {
        return AjaxResult.success(conReasonTypeStorageService.changeStatus(conReasonTypeStorage));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "原因类型(库存管理)", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConReasonTypeStorage conReasonTypeStorage) {
        conReasonTypeStorage.setConfirmDate(new Date());
        conReasonTypeStorage.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conReasonTypeStorage.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conReasonTypeStorageService.check(conReasonTypeStorage));
    }

    /**
     * 获取原因类型列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "查询原因类型列表", notes = "查询原因类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConReasonTypeStorage.class))
    public AjaxResult getList() {
        return AjaxResult.success(conReasonTypeStorageService.getList());
    }


}
