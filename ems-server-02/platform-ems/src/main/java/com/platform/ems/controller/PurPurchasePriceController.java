package com.platform.ems.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;
import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;

import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.PurPurchasePrice;
import com.platform.ems.domain.PurPurchasePriceItem;

import com.platform.ems.domain.dto.ManWorkOrderProgressFormProcess;
import com.platform.ems.domain.dto.request.PurPurchasePriceActionRequest;
import com.platform.ems.domain.dto.response.PurPurchasePriceReportResponse;
import com.platform.ems.domain.dto.response.PurPurchasePriceResponse;
import com.platform.ems.domain.dto.response.purPurchaseExResponse;
import com.platform.ems.service.ISystemDictDataService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.service.IPurPurchasePriceService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 采购价信息主Controller
 *
 * @author ChenPinzhen
 * @date 2021-02-04
 */
@RestController
@RequestMapping("/purchasePrice")
@Api(tags = "采购价")
public class PurPurchasePriceController extends BaseController {

    @Autowired
    private IPurPurchasePriceService purPurchasePriceService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template";

    /**
     * 新增采购价信息主
     */
    @Log(title = "采购价信息主", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增采购价信息", notes = "新增采购价信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@Validated @RequestBody PurPurchasePrice purPurchasePrice) {
        return purPurchasePriceService.insertPurPurchasePrice(purPurchasePrice);
    }

    /**
     * 批量新增采购价信息主
     */
    @Log(title = "批量新增采购价信息主", businessType = BusinessType.INSERT)
    @ApiOperation(value = "批量新增采购价信息主", notes = "批量新增采购价信息主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/addImport")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult addImportList(@RequestBody List<PurPurchasePrice> PurPurchasePriceList) {
        return purPurchasePriceService.insertImport(PurPurchasePriceList);
    }

    /**
     * 新增/编辑直接提交采购价信息
     */
    @Log(title = "新增/编辑直接提交采购价信息", businessType = BusinessType.SUBMIT)
    @ApiOperation(value = "新增/编辑直接提交采购价信息", notes = "新增/编辑直接提交采购价信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submit(@Validated @RequestBody PurPurchasePrice purPurchasePrice) {
        return purPurchasePriceService.submit(purPurchasePrice);
    }

    /**
     * 查询采购价信息主列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "查询采购价信息", notes = "查询采购价信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchasePriceResponse.class))
    public TableDataInfo list(@RequestBody  PurPurchasePrice request) {
        startPage(request);
        List<PurPurchasePrice> list = purPurchasePriceService.selectPurPurchasePriceList(request);
        return getDataTable(list);
    }

    /**
     * 采购价报表
     */
    @Log(title = "采购价报表", businessType = BusinessType.UPDATE)
    @PostMapping("/report")
    @ApiOperation(value = "采购价报表", notes = "采购价报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchasePriceReportResponse.class))
    public TableDataInfo report(@RequestBody PurPurchasePriceReportResponse request) {
        startPage(request);
        List<PurPurchasePriceReportResponse> list = purPurchasePriceService.report(request);
        return getDataTable(list);
    }

    /**
     * 导出采购价信息主列表
     */
    @ApiOperation(value = "导出采购价信息", notes = "导出采购价信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchasePrice.class))
    @Log(title = "采购价信息主", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurPurchasePriceReportResponse request) throws IOException {
        List<PurPurchasePriceReportResponse> list = purPurchasePriceService.report(request);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<purPurchaseExResponse> util = new ExcelUtil<>(purPurchaseExResponse.class,dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, purPurchaseExResponse::new), "采购价明细");
    }

    /**
     * 导出采购价信息主列表
     */
    @ApiOperation(value = "导出采购价明细报表", notes = "导出采购价明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchasePrice.class))
    @Log(title = "采购价信息主", businessType = BusinessType.EXPORT)
    @PostMapping("/export/report")
    public void exportRe(HttpServletResponse response, PurPurchasePriceReportResponse purPurchasePriceReportResponse) throws IOException {
        List<PurPurchasePriceReportResponse> list = purPurchasePriceService.report(purPurchasePriceReportResponse);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<PurPurchasePriceReportResponse> util = new ExcelUtil<>(PurPurchasePriceReportResponse.class,dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, PurPurchasePriceReportResponse::new), "采购价明细报表"+ DateUtil.format(new Date(),"yyyyMMddHHmmss"));
    }

    /**
     * 获取采购价信息主详细信息
     */
    @PostMapping("/getInfo")
    @ApiOperation(value = "获取采购价详细信息", notes = "获取采购价详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult getInfo(Long id) {
        if(id==null){
            throw new CustomException("参数缺失");
        }
        return AjaxResult.success(purPurchasePriceService.selectPurPurchasePriceById(id));
    }

    /**
     * 修改采购价信息
     */
    @Log(title = "采购价信息主", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改采购价信息", notes = "修改采购价信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/update")
    public AjaxResult edit(@RequestBody PurPurchasePrice purPurchasePrice){
        return purPurchasePriceService.updatePurPurchasePrice(purPurchasePrice);
    }

    /**
     * 修改采购价信息
     */
    @Log(title = "采购价信息主", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "编辑/变更 修改采购价信息-新", notes = "编辑/变更 修改采购价信息-新")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/update/new")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult editNew(@RequestBody PurPurchasePrice purPurchasePrice){
        return purPurchasePriceService.updatePurPurchasePriceNew(purPurchasePrice);
    }

    /**
     * 查询页面变更有效期
     */
    @Log(title = "查询页面变更有效期", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "查询页面变更有效期", notes = "查询页面变更有效期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/update/item/newTime")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult changeItemTime(@RequestBody PurPurchasePriceReportResponse purPurchasePriceItem){
        return purPurchasePriceService.changeItemTime(purPurchasePriceItem);
    }

    /**
     * 变更采购价信息
     */
    @Log(title = "采购价信息主", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "变更采购价信息", notes = "变更采购价信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult change(@RequestBody PurPurchasePrice purPurchasePrice){
        return purPurchasePriceService.changePurPurchasePrice(purPurchasePrice);
    }

    /**
     * 删除采购价信息主
     */
    @Log(title = "采购价信息主", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除采购价信息", notes = "删除采购价信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> ids) {
        return  purPurchasePriceService.deletePurPurchasePriceByIds(ids);
    }

    /**
     * 删除采购价信息明细行
     */
    @Log(title = "删除采购价信息明细行", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除采购价信息明细行", notes = "删除采购价信息明细行")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete/item")
    public AjaxResult removeItem(@RequestBody List<Long> ids) {
        return  AjaxResult.success(purPurchasePriceService.deleteItem(ids));
    }

    /**
     * 采购价确认
     */
    @Log(title = "客户档案", businessType = BusinessType.UPDATE)
    @PostMapping("/confirm")
    @ApiOperation(value = "确认采购价信息", notes = "确认采购价信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult confirm(@RequestBody PurPurchasePriceActionRequest purPurchasePriceActionRequset) {
        return purPurchasePriceService.confirm(purPurchasePriceActionRequset);
    }

    /**
     * 启用/停用 采购价
     */
    @Log(title = "客户档案", businessType = BusinessType.UPDATE)
    @PostMapping("/status")
    @ApiOperation(value = "启用/停用采购价信息", notes = "启用/停用采购价信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult editValidStatus(@RequestBody PurPurchasePriceActionRequest purPurchasePriceActionRequset) {
        return purPurchasePriceService.status(purPurchasePriceActionRequset);
    }

    /**
     * 判断是否能新增采购价
     */
    @Log(title = "客户档案", businessType = BusinessType.UPDATE)
    @PostMapping("/judgeAdd")
    @ApiOperation(value = "判断是否能新增采购价", notes = "判断是否能新增采购价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult judgeAdd(@RequestBody PurPurchasePrice purPurchasePrice) {
        return purPurchasePriceService.judgeAdd(purPurchasePrice);
    }

    /**
     * 获取采购价
     */
    @ApiOperation(value = "获取采购价", notes = "获取采购价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchasePriceItem.class))
    @PostMapping("/getPurchasePrice")
    public AjaxResult getPurchasePrice(@RequestBody PurPurchasePrice purPurchasePrice) {
        return AjaxResult.success(purPurchasePriceService.getPurchasePrice(purPurchasePrice));
    }

    /**
     * 提交时校验
     */
    @ApiOperation(value = "提交时校验", notes = "提交时校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchasePrice.class))
    @PostMapping("/processCheck")
    public AjaxResult processCheck(@RequestBody List<PurPurchasePriceItem> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            throw new BaseException("请勾选行再进行操作！");
        }
        List<Long> sids = itemList.stream().map(PurPurchasePriceItem::getPurchasePriceSid).distinct().collect(Collectors.toList());
        List<PurPurchasePrice> priceList = purPurchasePriceService.selectPurPurchasePriceList(new PurPurchasePrice().setPurchasePriceSidList(sids.toArray(new Long[sids.size()])));
        Map<Long,PurPurchasePrice> map = priceList.stream().collect(Collectors.toMap(PurPurchasePrice::getPurchasePriceSid, Function.identity(), (t1,t2) -> t1));
        itemList.forEach(item->{
            purPurchasePriceService.judgeTime((PurPurchasePrice)map.get(item.getPurchasePriceSid()), item);
        });
        return AjaxResult.success(purPurchasePriceService.processCheck(sids));
    }

    /**
     * 导入采购价
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入采购价", notes = "导入采购价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataM(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return purPurchasePriceService.importDataPur(file);
    }

    @ApiOperation(value = "下载采购价导入模板", notes = "下载采购价导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_采购价_V0.1(1).xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_采购价_V0.1(1).xlsx", "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            OutputStream out = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        }
    }
}
