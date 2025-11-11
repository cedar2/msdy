package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.platform.common.core.domain.entity.FileType;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.plug.domain.ConFileType;
import com.platform.ems.plug.service.IConFileTypeService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.enums.HandleStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.util.ArrayUtil;

import javax.validation.Valid;

import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;

/**
 * 附件类型Controller
 *
 * @author chenkw
 * @date 2021-07-05
 */
@RestController
@RequestMapping("/con/fileType")
@Api(tags = "附件类型")
public class ConFileTypeController extends BaseController {

    @Resource
    private IConFileTypeService conFileTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询附件类型列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询附件类型列表", notes = "查询附件类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConFileType.class))
    public TableDataInfo list(@RequestBody ConFileType conFileType) {
        startPage(conFileType);
        List<ConFileType> list = conFileTypeService.selectConFileTypeList(conFileType);
        return getDataTable(list);
    }

    @PostMapping("/getList")
    @ApiOperation(value = "查询附件类型列表", notes = "查询附件类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConFileType.class))
    public AjaxResult getList(@RequestBody ConFileType conFileType) {
        List<ConFileType> list = conFileTypeService.selectConFileTypeList(conFileType);
        return AjaxResult.success(list);
    }

    /**
     * 导出附件类型列表
     */
    @ApiOperation(value = "导出附件类型列表", notes = "导出附件类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConFileType conFileType) throws IOException {
        List<ConFileType> list = conFileTypeService.selectConFileTypeList(conFileType);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConFileType> util = new ExcelUtil<>(ConFileType.class, dataMap);
        util.exportExcel(response, list, "附件类型");
    }


    /**
     * 获取附件类型详细信息
     */
    @ApiOperation(value = "获取附件类型详细信息", notes = "获取附件类型详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FileType.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        ConFileType  conFileType=conFileTypeService.selectConFileTypeById(sid);
        FileType fileType=new FileType();
        BeanUtil.copyProperties(conFileType, fileType, true);
        return AjaxResult.success(fileType);
    }

    /**
     * 新增附件类型
     */
    @ApiOperation(value = "新增附件类型", notes = "新增附件类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConFileType conFileType) {
        return toAjax(conFileTypeService.insertConFileType(conFileType));
    }

    /**
     * 修改附件类型
     */
    @ApiOperation(value = "修改附件类型", notes = "修改附件类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConFileType conFileType) {
        return toAjax(conFileTypeService.updateConFileType(conFileType));
    }

    /**
     * 变更附件类型
     */
    @ApiOperation(value = "变更附件类型", notes = "变更附件类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConFileType conFileType) {
        return toAjax(conFileTypeService.changeConFileType(conFileType));
    }

    /**
     * 删除附件类型
     */
    @ApiOperation(value = "删除附件类型", notes = "删除附件类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conFileTypeService.deleteConFileTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConFileType conFileType) {
        return AjaxResult.success(conFileTypeService.changeStatus(conFileType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConFileType conFileType) {
        conFileType.setConfirmDate(new Date());
        conFileType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conFileType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conFileTypeService.check(conFileType));
    }

    /**
     * 下拉框列表
     */
    @PostMapping("/getConFileTypeList")
    @ApiOperation(value = "下拉框列表", notes = "下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConFileType.class))
    public AjaxResult getConFileTypeList() {
        return AjaxResult.success(conFileTypeService.getConFileTypeList());
    }

}
