package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
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
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.plug.domain.ConDocTypeInventorySheet;
import com.platform.ems.plug.service.IConDocTypeInventorySheetService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 单据类型(盘点单)Controller
 *
 * @author chenkw
 * @date 2021-08-11
 */
@RestController
@RequestMapping("/doc/type/inventory/sheet")
@Api(tags = "单据类型(盘点单)")
public class ConDocTypeInventorySheetController extends BaseController {

    @Autowired
    private IConDocTypeInventorySheetService conDocTypeInventorySheetService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询单据类型(盘点单)列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型(盘点单)列表", notes = "查询单据类型(盘点单)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeInventorySheet.class))
    public TableDataInfo list(@RequestBody ConDocTypeInventorySheet conDocTypeInventorySheet) {
        startPage(conDocTypeInventorySheet);
        List<ConDocTypeInventorySheet> list = conDocTypeInventorySheetService.selectConDocTypeInventorySheetList(conDocTypeInventorySheet);
        return getDataTable(list);
    }

    /**
     * 导出单据类型(盘点单)列表
     */
    @Log(title = "单据类型(盘点单)", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出单据类型(盘点单)列表", notes = "导出单据类型(盘点单)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypeInventorySheet conDocTypeInventorySheet) throws IOException {
        List<ConDocTypeInventorySheet> list = conDocTypeInventorySheetService.selectConDocTypeInventorySheetList(conDocTypeInventorySheet);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypeInventorySheet> util = new ExcelUtil<>(ConDocTypeInventorySheet.class,dataMap);
        util.exportExcel(response, list, "单据类型(盘点单)");
    }


    /**
     * 获取单据类型(盘点单)详细信息
     */
    @ApiOperation(value = "获取单据类型(盘点单)详细信息", notes = "获取单据类型(盘点单)详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeInventorySheet.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if(sid==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocTypeInventorySheetService.selectConDocTypeInventorySheetById(sid));
    }

    /**
     * 新增单据类型(盘点单)
     */
    @ApiOperation(value = "新增单据类型(盘点单)", notes = "新增单据类型(盘点单)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型(盘点单)", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypeInventorySheet conDocTypeInventorySheet) {
        return toAjax(conDocTypeInventorySheetService.insertConDocTypeInventorySheet(conDocTypeInventorySheet));
    }

    /**
     * 修改单据类型(盘点单)
     */
    @ApiOperation(value = "修改单据类型(盘点单)", notes = "修改单据类型(盘点单)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "单据类型(盘点单)", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConDocTypeInventorySheet conDocTypeInventorySheet) {
        return toAjax(conDocTypeInventorySheetService.updateConDocTypeInventorySheet(conDocTypeInventorySheet));
    }

    /**
     * 变更单据类型(盘点单)
     */
    @ApiOperation(value = "变更单据类型(盘点单)", notes = "变更单据类型(盘点单)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "单据类型(盘点单)", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocTypeInventorySheet conDocTypeInventorySheet) {
        return toAjax(conDocTypeInventorySheetService.changeConDocTypeInventorySheet(conDocTypeInventorySheet));
    }

    /**
     * 删除单据类型(盘点单)
     */
    @ApiOperation(value = "删除单据类型(盘点单)", notes = "删除单据类型(盘点单)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型(盘点单)", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(CollectionUtils.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypeInventorySheetService.deleteConDocTypeInventorySheetByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型(盘点单)", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypeInventorySheet conDocTypeInventorySheet) {
        return AjaxResult.success(conDocTypeInventorySheetService.changeStatus(conDocTypeInventorySheet));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型(盘点单)", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypeInventorySheet conDocTypeInventorySheet) {
        conDocTypeInventorySheet.setConfirmDate(new Date());
        conDocTypeInventorySheet.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypeInventorySheet.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypeInventorySheetService.check(conDocTypeInventorySheet));
    }

}
