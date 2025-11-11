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
import com.platform.ems.plug.domain.ConShelfType;
import com.platform.ems.plug.service.IConShelfTypeService;
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
 * 货架类型Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/shelf/type")
@Api(tags = "货架类型")
public class ConShelfTypeController extends BaseController {

    @Autowired
    private IConShelfTypeService conShelfTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询货架类型列表
     */
    @PreAuthorize(hasPermi = "ems:shelfType:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询货架类型列表", notes = "查询货架类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConShelfType.class))
    public TableDataInfo list(@RequestBody ConShelfType conShelfType) {
        startPage(conShelfType);
        List<ConShelfType> list = conShelfTypeService.selectConShelfTypeList(conShelfType);
        return getDataTable(list);
    }

    /**
     * 导出货架类型列表
     */
    @PreAuthorize(hasPermi = "ems:shelfType:export")
    @Log(title = "货架类型", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出货架类型列表", notes = "导出货架类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConShelfType conShelfType) throws IOException {
        List<ConShelfType> list = conShelfTypeService.selectConShelfTypeList(conShelfType);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConShelfType> util = new ExcelUtil<ConShelfType>(ConShelfType.class, dataMap);
        util.exportExcel(response, list, "货架类型" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入货架类型
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入货架类型", notes = "导入货架类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConShelfType> util = new ExcelUtil<ConShelfType>(ConShelfType.class);
        List<ConShelfType> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conShelfType -> {
                conShelfTypeService.insertConShelfType(conShelfType);
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


    @ApiOperation(value = "下载货架类型导入模板", notes = "下载货架类型导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConShelfType> util = new ExcelUtil<ConShelfType>(ConShelfType.class);
        util.importTemplateExcel(response, "货架类型导入模板");
    }


    /**
     * 获取货架类型详细信息
     */
    @ApiOperation(value = "获取货架类型详细信息", notes = "获取货架类型详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConShelfType.class))
    @PreAuthorize(hasPermi = "ems:shelfType:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conShelfTypeService.selectConShelfTypeById(sid));
    }

    /**
     * 新增货架类型
     */
    @ApiOperation(value = "新增货架类型", notes = "新增货架类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:shelfType:add")
    @Log(title = "货架类型", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConShelfType conShelfType) {
        return toAjax(conShelfTypeService.insertConShelfType(conShelfType));
    }

    /**
     * 修改货架类型
     */
    @ApiOperation(value = "修改货架类型", notes = "修改货架类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:shelfType:edit")
    @Log(title = "货架类型", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConShelfType conShelfType) {
        return toAjax(conShelfTypeService.updateConShelfType(conShelfType));
    }

    /**
     * 变更货架类型
     */
    @ApiOperation(value = "变更货架类型", notes = "变更货架类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:shelfType:change")
    @Log(title = "货架类型", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConShelfType conShelfType) {
        return toAjax(conShelfTypeService.changeConShelfType(conShelfType));
    }

    /**
     * 删除货架类型
     */
    @ApiOperation(value = "删除货架类型", notes = "删除货架类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:shelfType:remove")
    @Log(title = "货架类型", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conShelfTypeService.deleteConShelfTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "货架类型", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:shelfType:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConShelfType conShelfType) {
        return AjaxResult.success(conShelfTypeService.changeStatus(conShelfType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:shelfType:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "货架类型", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConShelfType conShelfType) {
        conShelfType.setConfirmDate(new Date());
        conShelfType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conShelfType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conShelfTypeService.check(conShelfType));
    }

}
