package com.platform.ems.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.domain.BasGoodsShelf;
import com.platform.ems.domain.InvInventoryDocumentItem;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.impl.BasGoodsShelfServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 货架档案Controller
 *
 * @author straw
 * @date 2023-02-02
 */
@RestController
@RequestMapping("/shelf")
@Api(tags = "货架档案")
public class BasGoodsShelfController extends BaseController {

    private final BasGoodsShelfServiceImpl basGoodsShelfService;
    private final ISystemDictDataService sysDictDataService;

    public BasGoodsShelfController(BasGoodsShelfServiceImpl basGoodsShelfService,
                                   ISystemDictDataService sysDictDataService) {
        this.basGoodsShelfService = basGoodsShelfService;
        this.sysDictDataService = sysDictDataService;
    }

    /**
     * 查询货架档案列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询货架档案列表",
                  notes = "查询货架档案列表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = BasGoodsShelf.class))
    public TableDataInfo list(@RequestBody BasGoodsShelf basGoodsShelf) {
        startPage(basGoodsShelf);
        List<BasGoodsShelf> list = basGoodsShelfService.selectBasGoodsShelfList(basGoodsShelf);
        return getDataTable(list);
    }

    /**
     * 导出货架档案列表
     */
    @Log(title = "货架档案",
         businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出货架档案列表",
                  notes = "导出货架档案列表")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasGoodsShelf basGoodsShelf) throws IOException {
        List<BasGoodsShelf> list = basGoodsShelfService.selectBasGoodsShelfList(basGoodsShelf);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasGoodsShelf> util = new ExcelUtil<>(BasGoodsShelf.class, dataMap);
        util.exportExcel(response, list, "货架档案");
    }


    /**
     * 获取货架档案详细信息
     */
    @ApiOperation(value = "获取货架档案详细信息",
                  notes = "获取货架档案详细信息")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = BasGoodsShelf.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long goodsShelfSid) {
        if (goodsShelfSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basGoodsShelfService.selectBasGoodsShelfById(goodsShelfSid));
    }

    /**
     * 新增货架档案
     */
    @ApiOperation(value = "新增货架档案",
                  notes = "新增货架档案")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "货架档案",
         businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid BasGoodsShelf basGoodsShelf) {
        int row = basGoodsShelfService.insertBasGoodsShelf(basGoodsShelf);
        if (row > 0) {
            return AjaxResult.success(basGoodsShelfService.selectBasGoodsShelfById(basGoodsShelf.getGoodsShelfSid()));
        } else {
            return toAjax(row);
        }
    }

    @ApiOperation(value = "修改货架档案",
                  notes = "修改货架档案")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "货架档案",
         businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮",
                interval = 3000)
    public AjaxResult edit(@RequestBody BasGoodsShelf basGoodsShelf) {
        return toAjax(basGoodsShelfService.updateBasGoodsShelf(basGoodsShelf));
    }

    /**
     * 变更货架档案
     */
    @ApiOperation(value = "变更货架档案",
                  notes = "变更货架档案")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "货架档案",
         businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid BasGoodsShelf basGoodsShelf) {
        return toAjax(basGoodsShelfService.changeBasGoodsShelf(basGoodsShelf));
    }

    /**
     * 删除货架档案
     */
    @ApiOperation(value = "删除货架档案",
                  notes = "删除货架档案")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "货架档案",
         businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> goodsShelfSids) {
        if (CollectionUtils.isEmpty(goodsShelfSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basGoodsShelfService.deleteBasGoodsShelfByIds(goodsShelfSids));
    }

    @ApiOperation(value = "启用停用接口",
                  notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "货架档案",
         businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody BasGoodsShelf basGoodsShelf) {
        return AjaxResult.success(basGoodsShelfService.changeStatus(basGoodsShelf));
    }

    @ApiOperation(value = "确认",
                  notes = "确认")
    @ApiResponses(@ApiResponse(code = 200,
                               message = "请求成功",
                               response = AjaxResult.class))
    @Log(title = "货架档案",
         businessType = BusinessType.CHECK)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody BasGoodsShelf basGoodsShelf) {
        return toAjax(basGoodsShelfService.check(basGoodsShelf));
    }

    @ApiOperation(value = "根据物料分类和仓库和库位获取货架编号多值用分号隔开", notes = "根据物料分类和仓库和库位获取货架编号多值用分号隔开")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/getCodes")
    public AjaxResult getCodes(@RequestBody List<InvInventoryDocumentItem> request) {
        return AjaxResult.success(basGoodsShelfService.getCodes(request));
    }
}
