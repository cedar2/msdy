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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.util.ArrayUtil;

import javax.validation.Valid;

import com.platform.ems.domain.PurPriceInfor;
import com.platform.ems.service.IPurPriceInforService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 采购价格记录主(报价/核价/议价)Controller
 *
 * @author linhongwei
 * @date 2021-04-26
 */
@RestController
@RequestMapping("/infor")
@Api(tags = "采购价格记录主(报价/核价/议价)")
public class PurPriceInforController extends BaseController {

    @Autowired
    private IPurPriceInforService purPriceInforService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询采购价格记录主(报价/核价/议价)列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询采购价格记录主(报价/核价/议价)列表", notes = "查询采购价格记录主(报价/核价/议价)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPriceInfor.class))
    public TableDataInfo list(@RequestBody PurPriceInfor purPriceInfor) {
        startPage();
        List<PurPriceInfor> list = purPriceInforService.selectPurPriceInforList(purPriceInfor);
        return getDataTable(list);
    }

    /**
     * 导出采购价格记录主(报价/核价/议价)列表
     */
    @ApiOperation(value = "导出采购价格记录主(报价/核价/议价)列表", notes = "导出采购价格记录主(报价/核价/议价)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurPriceInfor purPriceInfor) throws IOException {
        List<PurPriceInfor> list = purPriceInforService.selectPurPriceInforList(purPriceInfor);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PurPriceInfor> util = new ExcelUtil<>(PurPriceInfor.class, dataMap);
        util.exportExcel(response, list, "采购价格记录主(报价/核价/议价)");
    }

    /**
     * 获取采购价格记录主(报价/核价/议价)详细信息
     */
    @ApiOperation(value = "获取采购价格记录主(报价/核价/议价)详细信息", notes = "获取采购价格记录主(报价/核价/议价)详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPriceInfor.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long priceInforSid) {
        if (priceInforSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purPriceInforService.selectPurPriceInforById(priceInforSid));
    }

    /**
     * 新增采购价格记录主(报价/核价/议价)
     */
    @ApiOperation(value = "新增采购价格记录主(报价/核价/议价)", notes = "新增采购价格记录主(报价/核价/议价)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurPriceInfor purPriceInfor) {
        return toAjax(purPriceInforService.insertPurPriceInfor(purPriceInfor));
    }

    /**
     * 修改采购价格记录主(报价/核价/议价)
     */
    @ApiOperation(value = "修改采购价格记录主(报价/核价/议价)", notes = "修改采购价格记录主(报价/核价/议价)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody PurPriceInfor purPriceInfor) {
        return toAjax(purPriceInforService.updatePurPriceInfor(purPriceInfor));
    }

    /**
     * 删除采购价格记录主(报价/核价/议价)
     */
    @ApiOperation(value = "删除采购价格记录主(报价/核价/议价)", notes = "删除采购价格记录主(报价/核价/议价)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> priceInforSids) {
        if (ArrayUtil.isEmpty(priceInforSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purPriceInforService.deletePurPriceInforByIds(priceInforSids));
    }
}
