package com.platform.ems.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.SalePriceActionRequest;
import com.platform.ems.domain.dto.response.SaleReportExResponse;
import com.platform.ems.domain.dto.response.SaleReportResponse;
import com.platform.ems.service.ISalSalePriceService;
import com.platform.ems.service.ISystemDictDataService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 销售价信息Controller
 *
 * @author yangqize
 * @date 2021-03-07
 */
@RestController
@RequestMapping("/salePrice")
@Api(tags = "销售价")
public class SalSalePriceController extends BaseController {

    @Autowired
    private ISalSalePriceService salSalePriceService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;
    private static final String FILLE_PATH = "/template";
    /**
     * 通过条件查询销售价信息
     */
    @ApiOperation(value = "通过条件查询销售价信息", notes = "通过条件查询销售价信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalePrice.class))
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody SalSalePrice salSalePrice) {
        startPage(salSalePrice);
        List<SalSalePrice> list = salSalePriceService.selectSalSalePriceList(salSalePrice);
        return getDataTable(list);
    }

    /**
     * 通过条件查询销售价报表
     */
    @ApiOperation(value = "通过条件查询销售价报表信息", notes = "通过条件查询销售价报表信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SaleReportResponse.class))
    @PostMapping("/report")
    public TableDataInfo report(@RequestBody SaleReportResponse saleReportResponse) {
        startPage(saleReportResponse);
        List<SaleReportResponse> list = salSalePriceService.saleReport(saleReportResponse);
        return getDataTable(list);
}

    /**
     * 导出销售价信息列表
     */
    @ApiOperation(value = "导出销售价信息列表", notes = "导出销售价信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalePrice.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SaleReportResponse saleReportResponse) throws IOException {
        List<SaleReportResponse> list = salSalePriceService.saleReport(saleReportResponse);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<SaleReportExResponse> util = new ExcelUtil<>(SaleReportExResponse.class,dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, SaleReportExResponse::new), "销售价明细_"+ DateUtil.format(new Date(),"yyyyMMddHHmmss" ));
    }

    /**
     * 导出销售价明细报表
     */
    @ApiOperation(value = "导出销售价明细报表", notes = "导出销售价明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalePrice.class))
    @PostMapping("/export/report")
    public void exportRe(HttpServletResponse response, SaleReportResponse saleReportResponse) throws IOException {
        List<SaleReportResponse> list = salSalePriceService.saleReport(saleReportResponse);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<SaleReportResponse> util = new ExcelUtil<>(SaleReportResponse.class,dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, SaleReportResponse::new), "销售价明细报表");
    }

    /**
     * 获取销售价信息详细信息
     */
    @ApiOperation(value = "获取销售价信息详细信息", notes = "获取销售价信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalePrice.class))
    @PostMapping("/getInfo")
    public SalSalePrice getInfo(Long salSalePriceSid) {
        if(salSalePriceSid==null){
            throw new CustomException("参数缺失");
        }
        return salSalePriceService.selectSalSalePriceById(salSalePriceSid);
    }

    /**
     * 新增销售价信息
     */
    @ApiOperation(value = "新增销售价信息", notes = "新增销售价信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@Validated @RequestBody SalSalePrice salSalePrice) {
        //保存
        String confirmHandleStatus=ConstantsEms.CHECK_STATUS;
        //新增销售价主表
        if(confirmHandleStatus.equals(salSalePrice.getHandleStatus())) {
            //注入确认人 确认时间
            salSalePrice.setConfirmDate(new Date());
            salSalePrice.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        return salSalePriceService.insertSalSalePrice(salSalePrice);
    }

    /**
     * 新增/编辑直接提交销售价信息
     */
    @ApiOperation(value = "新增/编辑直接提交销售价信息", notes = "新增/编辑直接提交销售价信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submit(@Validated @RequestBody SalSalePrice salSalePrice) {
        return salSalePriceService.submit(salSalePrice);
    }

    /**
     * 修改销售价信息
     */
    @ApiOperation(value = "修改销售价信息", notes = "修改销售价信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult edit(@RequestBody SalSalePrice salSalePrice) {
        String checkStatus= ConstantsEms.CHECK_STATUS;
        String handleStatus = salSalePrice.getHandleStatus();
        if(checkStatus.equals(handleStatus)){
            salSalePrice.setConfirmDate(new Date());
            salSalePrice.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        return salSalePriceService.updateSalSalePrice(salSalePrice);
    }

    /**
     * 修改销售价信息
     */
    @ApiOperation(value = "修改/变更 销售价信息-新", notes = "修改/变更 销售价信息-新")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit/new")
    public AjaxResult editNew(@RequestBody SalSalePrice salSalePrice) {
        String checkStatus= ConstantsEms.CHECK_STATUS;
        String handleStatus = salSalePrice.getHandleStatus();
        if(checkStatus.equals(handleStatus)){
            salSalePrice.setConfirmDate(new Date());
            salSalePrice.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        return salSalePriceService.updateSalSalePriceNew(salSalePrice);
    }

    /**
     * 查询页面变更有效期
     */
    @ApiOperation(value = "查询页面变更有效期", notes = "查询页面变更有效期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/update/customer")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult updateCustomer(@RequestBody SalSalePrice salSalePrice){
        return AjaxResult.success(salSalePriceService.updateCustomer(salSalePrice));
    }

    /**
     * 查询页面变更有效期
     */
    @ApiOperation(value = "查询页面变更有效期", notes = "查询页面变更有效期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/update/item/newTime")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult changeItemTime(@RequestBody SaleReportResponse salSalePriceItem){
        return salSalePriceService.changeItemTime(salSalePriceItem);
    }

    /**
     * 提交时校验
     */
    @ApiOperation(value = "提交时校验", notes = "提交时校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalePrice.class))
    @PostMapping("/processCheck")
    public AjaxResult processCheck(@RequestBody List<SalSalePriceItem> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            throw new BaseException("请勾选行再进行操作！");
        }
        List<Long> sids = itemList.stream().map(SalSalePriceItem::getSalePriceSid).distinct().collect(Collectors.toList());
        List<SalSalePrice> priceList = salSalePriceService.selectSalSalePriceList(new SalSalePrice().setSalePriceSidList(sids.toArray(new Long[sids.size()])));
        Map<Long,SalSalePrice> map = priceList.stream().collect(Collectors.toMap(SalSalePrice::getSalePriceSid, Function.identity(), (t1,t2) -> t1));
        itemList.forEach(item->{
            salSalePriceService.judgeTime((SalSalePrice)map.get(item.getSalePriceSid()), item);
        });
        return AjaxResult.success(salSalePriceService.processCheck(sids));
    }

    /**
     * 删除销售价信息
     */
    @ApiOperation(value = "删除销售价信息", notes = "删除销售价信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> salSalePriceSids) {
        return salSalePriceService.deleteSalSalePriceById(salSalePriceSids);
    }

    /**
     * 删除销售价信息
     */
    @ApiOperation(value = "销售价信息明细行", notes = "销售价信息明细行")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete/item")
    public AjaxResult removeItem(@RequestBody List<Long> salSalePriceSids) {
        return AjaxResult.success(salSalePriceService.deleteItem(salSalePriceSids));
    }

    /**
     * 批量 修改销售价信息处理状态（确认）
     */
    @ApiOperation(value = "确认销售价信息", notes = "确认销售价信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/confirm")
    public AjaxResult HandleStatusConfirm(@RequestBody SalePriceActionRequest salePriceActionRequest){
        return salSalePriceService.handleStatusConfirm(salePriceActionRequest);
    }

    /**
     * 变更销售价信息
     */
    @PostMapping("/change")
    @ApiOperation(value = "变更销售价信息", notes = "变更销售价信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody SalSalePrice salSalePrice){
        return salSalePriceService.change(salSalePrice);
    }

    /**
     * 批量 启用/停用
     *
     */
    @ApiOperation(value = "启用/停用销售价信息", notes = "启用/停用销售价信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/status")
    public AjaxResult status(@RequestBody SalePriceActionRequest salePriceActionRequest){
        return salSalePriceService.status(salePriceActionRequest);
    }

    /**
     * 根据编码返回相关商品信息
     *
     */
    @ApiOperation(value = "根据编码返回相关商品信息", notes = "根据编码返回相关商品信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterial.class))
    @PostMapping("/getByCodeMaterial")
    public BasMaterial getMaterialSkus(@RequestBody  BasMaterial material){
        return salSalePriceService.getMaterialSkus(material);
    }

    /**
     * 判断是否能新增采购价
     */
    @PostMapping("/judgeAdd")
    @ApiOperation(value = "判断是否能新增销售价", notes = "判断是否能新增销售价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult judgeAdd(@RequestBody SalSalePrice salSalePrice) {
        return salSalePriceService.judgeAdd(salSalePrice);
    }

    /**
     * 获取销售价
     */
    @ApiOperation(value = "获取销售价", notes = "获取销售价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalePriceItem.class))
    @PostMapping("/getSalePrice")
    public AjaxResult getSalePrice(@RequestBody SalSalePrice salSalePrice) {
        return AjaxResult.success(salSalePriceService.getSalePrice(salSalePrice));
    }

    /**
     * 导入销售价
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入销售价", notes = "导入销售价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataM(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return salSalePriceService.importDataPur(file);
    }

    @ApiOperation(value = "下载采购价导入模板", notes = "下载采购价导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_销售价_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_销售价_V0.1.xlsx", "UTF-8"));
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
