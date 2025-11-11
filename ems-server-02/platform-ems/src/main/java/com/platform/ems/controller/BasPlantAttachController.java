package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import javax.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.BasPlantAttach;
import com.platform.ems.service.IBasPlantAttachService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 工厂档案-附件Controller
 *
 * @author chenkw
 * @date 2021-09-15
 */
@RestController
@RequestMapping("/plant/attach")
@Api(tags = "工厂档案-附件")
public class BasPlantAttachController extends BaseController {

    @Autowired
    private IBasPlantAttachService basPlantAttachService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询工厂档案-附件列表
     */
    @PreAuthorize(hasPermi = "ems:plant:attach:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询工厂档案-附件列表", notes = "查询工厂档案-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPlantAttach.class))
    public TableDataInfo list(@RequestBody BasPlantAttach basPlantAttach) {
        startPage(basPlantAttach);
        List<BasPlantAttach> list = basPlantAttachService.selectBasPlantAttachList(basPlantAttach);
        return getDataTable(list);
    }

    /**
     * 导出工厂档案-附件列表
     */
    @PreAuthorize(hasPermi = "ems:plant:attach:export")
    @Log(title = "工厂档案-附件", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出工厂档案-附件列表", notes = "导出工厂档案-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasPlantAttach basPlantAttach) throws IOException {
        List<BasPlantAttach> list = basPlantAttachService.selectBasPlantAttachList(basPlantAttach);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<BasPlantAttach> util = new ExcelUtil<>(BasPlantAttach.class,dataMap);
        util.exportExcel(response, list, "工厂档案-附件");
    }


    /**
     * 获取工厂档案-附件详细信息
     */
    @ApiOperation(value = "获取工厂档案-附件详细信息", notes = "获取工厂档案-附件详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPlantAttach.class))
    @PreAuthorize(hasPermi = "ems:plant:attach:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long attachmentSid) {
        if(attachmentSid==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basPlantAttachService.selectBasPlantAttachById(attachmentSid));
    }

    /**
     * 新增工厂档案-附件
     */
    @ApiOperation(value = "新增工厂档案-附件", notes = "新增工厂档案-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:plant:attach:add")
    @Log(title = "工厂档案-附件", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasPlantAttach basPlantAttach) {
        return toAjax(basPlantAttachService.insertBasPlantAttach(basPlantAttach));
    }

    /**
     * 修改工厂档案-附件
     */
    @ApiOperation(value = "修改工厂档案-附件", notes = "修改工厂档案-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:plant:attach:edit")
    @Log(title = "工厂档案-附件", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody BasPlantAttach basPlantAttach) {
        return toAjax(basPlantAttachService.updateBasPlantAttach(basPlantAttach));
    }

    /**
     * 变更工厂档案-附件
     */
    @ApiOperation(value = "变更工厂档案-附件", notes = "变更工厂档案-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:plant:attach:change")
    @Log(title = "工厂档案-附件", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody BasPlantAttach basPlantAttach) {
        return toAjax(basPlantAttachService.changeBasPlantAttach(basPlantAttach));
    }

    /**
     * 删除工厂档案-附件
     */
    @ApiOperation(value = "删除工厂档案-附件", notes = "删除工厂档案-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:plant:attach:remove")
    @Log(title = "工厂档案-附件", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  attachmentSids) {
        if(CollectionUtils.isEmpty( attachmentSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(basPlantAttachService.deleteBasPlantAttachByIds(attachmentSids));
    }

}
