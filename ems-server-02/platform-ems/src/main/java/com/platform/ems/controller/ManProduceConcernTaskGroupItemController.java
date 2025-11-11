package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.annotation.Idempotent;
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

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.ManProduceConcernTaskGroupItem;
import com.platform.ems.service.IManProduceConcernTaskGroupItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 生产关注事项组-明细Controller
 *
 * @author zhuangyz
 * @date 2022-08-02
 */
@RestController
@RequestMapping("/manProduceConcernTaskGroupItem" )
@Api(tags = "生产关注事项组-明细" )
public class ManProduceConcernTaskGroupItemController extends BaseController {

    @Autowired
    private IManProduceConcernTaskGroupItemService manProduceConcernTaskGroupItemService;

    @Autowired
    private ISystemDictDataService sysDictDataService;

/**
 * 查询生产关注事项组-明细列表
 */
@PreAuthorize(hasPermi = "ems:manProduceConcernTaskGroupItem:list" )
@PostMapping("/list" )
@ApiOperation(value = "查询生产关注事项组-明细列表" , notes = "查询生产关注事项组-明细列表" )
@ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = ManProduceConcernTaskGroupItem.class))
        public TableDataInfo list(@RequestBody ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem) {
        startPage(manProduceConcernTaskGroupItem);
        List<ManProduceConcernTaskGroupItem> list = manProduceConcernTaskGroupItemService.selectManProduceConcernTaskGroupItemList(manProduceConcernTaskGroupItem);
        return getDataTable(list);
    }

    /**
     * 导出生产关注事项组-明细列表
     */
    @PreAuthorize(hasPermi = "ems:manProduceConcernTaskGroupItem:export" )
    @Log(title = "生产关注事项组-明细" , businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产关注事项组-明细列表" , notes = "导出生产关注事项组-明细列表" )
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = void.class))
    @PostMapping("/export" )
    public void export(HttpServletResponse response, ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem) throws IOException {
        List<ManProduceConcernTaskGroupItem> list = manProduceConcernTaskGroupItemService.selectManProduceConcernTaskGroupItemList(manProduceConcernTaskGroupItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManProduceConcernTaskGroupItem> util = new ExcelUtil<>(ManProduceConcernTaskGroupItem. class,dataMap);
        util.exportExcel(response, list, "生产关注事项组-明细");
    }


    /**
     * 获取生产关注事项组-明细详细信息
     */
    @ApiOperation(value = "获取生产关注事项组-明细详细信息" , notes = "获取生产关注事项组-明细详细信息" )
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = ManProduceConcernTaskGroupItem.class))
    @PreAuthorize(hasPermi = "ems:manProduceConcernTaskGroupItem:query" )
    @PostMapping("/getInfo" )
    public AjaxResult getInfo(Long concernTaskGroupItemSid) {
                    if (concernTaskGroupItemSid==null){
            throw new CheckedException("参数缺失" );
        }
                return AjaxResult.success(manProduceConcernTaskGroupItemService.selectManProduceConcernTaskGroupItemById(concernTaskGroupItemSid));
    }

    /**
     * 新增生产关注事项组-明细
     */
    @ApiOperation(value = "新增生产关注事项组-明细", notes = "新增生产关注事项组-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manProduceConcernTaskGroupItem:add")
    @Log(title = "生产关注事项组-明细", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem) {
        return toAjax(manProduceConcernTaskGroupItemService.insertManProduceConcernTaskGroupItem(manProduceConcernTaskGroupItem));
    }


    /**
     * 删除生产关注事项组-明细
     */
    @ApiOperation(value = "删除生产关注事项组-明细" , notes = "删除生产关注事项组-明细" )
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manProduceConcernTaskGroupItem:remove" )
    @Log(title = "生产关注事项组-明细" , businessType = BusinessType.DELETE)
    @PostMapping("/delete" )
    public AjaxResult remove(@RequestBody List<Long>  concernTaskGroupItemSids) {
        if (CollectionUtils.isEmpty(concernTaskGroupItemSids)) {
            throw new CheckedException("参数缺失" );
        }
        return toAjax(manProduceConcernTaskGroupItemService.deleteManProduceConcernTaskGroupItemByIds(concernTaskGroupItemSids));
    }

    //    /**
//     * 变更生产关注事项组-明细
//     */
//    @ApiOperation(value = "变更生产关注事项组-明细" , notes = "变更生产关注事项组-明细" )
//    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:manProduceConcernTaskGroupItem:change" )
//    @Log(title = "生产关注事项组-明细" , businessType = BusinessType.CHANGE)
//    @PostMapping("/change" )
//    public AjaxResult change(@RequestBody @Valid ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem) {
//        return toAjax(manProduceConcernTaskGroupItemService.changeManProduceConcernTaskGroupItem(manProduceConcernTaskGroupItem));
//    }


    //    @ApiOperation(value = "修改生产关注事项组-明细", notes = "修改生产关注事项组-明细")
//    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:manProduceConcernTaskGroupItem:edit")
//    @Log(title = "生产关注事项组-明细", businessType = BusinessType.UPDATE)
//    @PostMapping("/edit")
//    @Idempotent(message = "系统处理中，请勿重复点击按钮",interval=3000)
//    public AjaxResult edit(@RequestBody ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem) {
//        return toAjax(manProduceConcernTaskGroupItemService.updateManProduceConcernTaskGroupItem(manProduceConcernTaskGroupItem));
//    }


//    @ApiOperation(value = "启用停用接口" , notes = "启用停用接口" )
//    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = AjaxResult.class))
//    @Log(title = "生产关注事项组-明细" , businessType = BusinessType.UPDATE)
//    @PreAuthorize(hasPermi = "ems:manProduceConcernTaskGroupItem:enbleordisable" )
//    @PostMapping("/changeStatus" )
//    public AjaxResult changeStatus(@RequestBody ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem) {
//        return AjaxResult.success(manProduceConcernTaskGroupItemService.changeStatus(manProduceConcernTaskGroupItem));
//    }

//    @ApiOperation(value = "确认", notes = "确认")
//    @PreAuthorize(hasPermi = "ems:manProduceConcernTaskGroupItem:check")
//    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @Log(title = "生产关注事项组-明细", businessType = BusinessType.CHECK)
//    @PostMapping("/check")
//    @Idempotent(message = "系统处理中，请勿重复点击按钮")
//    public AjaxResult check(@RequestBody ManProduceConcernTaskGroupItem manProduceConcernTaskGroupItem) {
//        return toAjax(manProduceConcernTaskGroupItemService.check(manProduceConcernTaskGroupItem));
//    }

}
