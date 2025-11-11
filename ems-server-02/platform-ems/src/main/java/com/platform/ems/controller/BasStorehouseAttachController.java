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
import com.platform.ems.domain.BasStorehouseAttach;
import com.platform.ems.service.IBasStorehouseAttachService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 仓库档案-附件Controller
 *
 * @author chenkw
 * @date 2021-09-15
 */
@RestController
@RequestMapping("/storehouse/attach")
@Api(tags = "仓库档案-附件")
public class BasStorehouseAttachController extends BaseController {

    @Autowired
    private IBasStorehouseAttachService basStorehouseAttachService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询仓库档案-附件列表
     */
    @PreAuthorize(hasPermi = "ems:storehouse:attach:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询仓库档案-附件列表", notes = "查询仓库档案-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStorehouseAttach.class))
    public TableDataInfo list(@RequestBody BasStorehouseAttach basStorehouseAttach) {
        startPage(basStorehouseAttach);
        List<BasStorehouseAttach> list = basStorehouseAttachService.selectBasStorehouseAttachList(basStorehouseAttach);
        return getDataTable(list);
    }

    /**
     * 导出仓库档案-附件列表
     */
    @PreAuthorize(hasPermi = "ems:storehouse:attach:export")
    @Log(title = "仓库档案-附件", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出仓库档案-附件列表", notes = "导出仓库档案-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasStorehouseAttach basStorehouseAttach) throws IOException {
        List<BasStorehouseAttach> list = basStorehouseAttachService.selectBasStorehouseAttachList(basStorehouseAttach);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<BasStorehouseAttach> util = new ExcelUtil<>(BasStorehouseAttach.class,dataMap);
        util.exportExcel(response, list, "仓库档案-附件");
    }


    /**
     * 获取仓库档案-附件详细信息
     */
    @ApiOperation(value = "获取仓库档案-附件详细信息", notes = "获取仓库档案-附件详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStorehouseAttach.class))
    @PreAuthorize(hasPermi = "ems:storehouse:attach:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long attachmentSid) {
        if(attachmentSid==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basStorehouseAttachService.selectBasStorehouseAttachById(attachmentSid));
    }

    /**
     * 新增仓库档案-附件
     */
    @ApiOperation(value = "新增仓库档案-附件", notes = "新增仓库档案-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:storehouse:attach:add")
    @Log(title = "仓库档案-附件", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasStorehouseAttach basStorehouseAttach) {
        return toAjax(basStorehouseAttachService.insertBasStorehouseAttach(basStorehouseAttach));
    }

    /**
     * 修改仓库档案-附件
     */
    @ApiOperation(value = "修改仓库档案-附件", notes = "修改仓库档案-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:storehouse:attach:edit")
    @Log(title = "仓库档案-附件", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody BasStorehouseAttach basStorehouseAttach) {
        return toAjax(basStorehouseAttachService.updateBasStorehouseAttach(basStorehouseAttach));
    }

    /**
     * 变更仓库档案-附件
     */
    @ApiOperation(value = "变更仓库档案-附件", notes = "变更仓库档案-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:storehouse:attach:change")
    @Log(title = "仓库档案-附件", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody BasStorehouseAttach basStorehouseAttach) {
        return toAjax(basStorehouseAttachService.changeBasStorehouseAttach(basStorehouseAttach));
    }

    /**
     * 删除仓库档案-附件
     */
    @ApiOperation(value = "删除仓库档案-附件", notes = "删除仓库档案-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:storehouse:attach:remove")
    @Log(title = "仓库档案-附件", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  attachmentSids) {
        if(CollectionUtils.isEmpty( attachmentSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(basStorehouseAttachService.deleteBasStorehouseAttachByIds(attachmentSids));
    }
}
