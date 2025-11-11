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
import com.platform.ems.plug.domain.ConStorageUnitSizeType;
import com.platform.ems.plug.service.IConStorageUnitSizeTypeService;
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
 * 托盘规格类型Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/storage/type")
@Api(tags = "托盘规格类型")
public class ConStorageUnitSizeTypeController extends BaseController {

    @Autowired
    private IConStorageUnitSizeTypeService conStorageUnitSizeTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询托盘规格类型列表
     */
    @PreAuthorize(hasPermi = "ems:type:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询托盘规格类型列表", notes = "查询托盘规格类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConStorageUnitSizeType.class))
    public TableDataInfo list(@RequestBody ConStorageUnitSizeType conStorageUnitSizeType) {
        startPage(conStorageUnitSizeType);
        List<ConStorageUnitSizeType> list = conStorageUnitSizeTypeService.selectConStorageUnitSizeTypeList(conStorageUnitSizeType);
        return getDataTable(list);
    }

    /**
     * 导出托盘规格类型列表
     */
    @PreAuthorize(hasPermi = "ems:type:export")
    @Log(title = "托盘规格类型", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出托盘规格类型列表", notes = "导出托盘规格类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConStorageUnitSizeType conStorageUnitSizeType) throws IOException {
        List<ConStorageUnitSizeType> list = conStorageUnitSizeTypeService.selectConStorageUnitSizeTypeList(conStorageUnitSizeType);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConStorageUnitSizeType> util = new ExcelUtil<ConStorageUnitSizeType>(ConStorageUnitSizeType.class, dataMap);
        util.exportExcel(response, list, "托盘规格类型" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入托盘规格类型
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入托盘规格类型", notes = "导入托盘规格类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConStorageUnitSizeType> util = new ExcelUtil<ConStorageUnitSizeType>(ConStorageUnitSizeType.class);
        List<ConStorageUnitSizeType> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conStorageUnitSizeType -> {
                conStorageUnitSizeTypeService.insertConStorageUnitSizeType(conStorageUnitSizeType);
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


    @ApiOperation(value = "下载托盘规格类型导入模板", notes = "下载托盘规格类型导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConStorageUnitSizeType> util = new ExcelUtil<ConStorageUnitSizeType>(ConStorageUnitSizeType.class);
        util.importTemplateExcel(response, "托盘规格类型导入模板");
    }


    /**
     * 获取托盘规格类型详细信息
     */
    @ApiOperation(value = "获取托盘规格类型详细信息", notes = "获取托盘规格类型详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConStorageUnitSizeType.class))
    @PreAuthorize(hasPermi = "ems:type:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conStorageUnitSizeTypeService.selectConStorageUnitSizeTypeById(sid));
    }

    /**
     * 新增托盘规格类型
     */
    @ApiOperation(value = "新增托盘规格类型", notes = "新增托盘规格类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:add")
    @Log(title = "托盘规格类型", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConStorageUnitSizeType conStorageUnitSizeType) {
        return toAjax(conStorageUnitSizeTypeService.insertConStorageUnitSizeType(conStorageUnitSizeType));
    }

    /**
     * 修改托盘规格类型
     */
    @ApiOperation(value = "修改托盘规格类型", notes = "修改托盘规格类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:edit")
    @Log(title = "托盘规格类型", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConStorageUnitSizeType conStorageUnitSizeType) {
        return toAjax(conStorageUnitSizeTypeService.updateConStorageUnitSizeType(conStorageUnitSizeType));
    }

    /**
     * 变更托盘规格类型
     */
    @ApiOperation(value = "变更托盘规格类型", notes = "变更托盘规格类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:change")
    @Log(title = "托盘规格类型", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConStorageUnitSizeType conStorageUnitSizeType) {
        return toAjax(conStorageUnitSizeTypeService.changeConStorageUnitSizeType(conStorageUnitSizeType));
    }

    /**
     * 删除托盘规格类型
     */
    @ApiOperation(value = "删除托盘规格类型", notes = "删除托盘规格类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:remove")
    @Log(title = "托盘规格类型", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conStorageUnitSizeTypeService.deleteConStorageUnitSizeTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "托盘规格类型", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:type:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConStorageUnitSizeType conStorageUnitSizeType) {
        return AjaxResult.success(conStorageUnitSizeTypeService.changeStatus(conStorageUnitSizeType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:type:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "托盘规格类型", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConStorageUnitSizeType conStorageUnitSizeType) {
        conStorageUnitSizeType.setConfirmDate(new Date());
        conStorageUnitSizeType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conStorageUnitSizeType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conStorageUnitSizeTypeService.check(conStorageUnitSizeType));
    }

}
