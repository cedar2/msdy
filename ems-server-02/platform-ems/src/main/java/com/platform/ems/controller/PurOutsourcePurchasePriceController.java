package com.platform.ems.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.PurPurchasePrice;
import com.platform.ems.domain.dto.response.PurOutsourcePurchasePriceResponse;
import com.platform.ems.domain.dto.response.PurOutsourceReportResponse;
import com.platform.ems.domain.dto.response.purOutPurchaseExResponse;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.util.ArrayUtil;

import javax.validation.Valid;

import com.platform.ems.domain.PurOutsourcePurchasePrice;
import com.platform.ems.service.IPurOutsourcePurchasePriceService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 加工采购价主Controller
 *
 * @author linhongwei
 * @date 2021-05-12
 */
@RestController
@RequestMapping("/outsource/purchase/price")
@Api(tags = "加工采购价")
public class PurOutsourcePurchasePriceController extends BaseController {

    @Autowired
    private IPurOutsourcePurchasePriceService purOutsourcePurchasePriceService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;
    private static final String FILLE_PATH = "/template";


    /**
     * 查询加工采购价主列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询加工采购价主列表", notes = "查询加工采购价主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourcePurchasePrice.class))
    public TableDataInfo list(@RequestBody PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        startPage(purOutsourcePurchasePrice);
        List<PurOutsourcePurchasePrice> list = purOutsourcePurchasePriceService.selectPurOutsourcePurchasePriceList(purOutsourcePurchasePrice);
        return getDataTable(list);
    }

    /**
     * 查询加工采购价报表
     */
    @PostMapping("/report")
    @ApiOperation(value = "查询加工采购价报表", notes = "查询加工采购价报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourceReportResponse.class))
    public TableDataInfo report(@RequestBody PurOutsourceReportResponse request) {
        startPage(request);
        List<PurOutsourceReportResponse> list = purOutsourcePurchasePriceService.report(request);
        return getDataTable(list);
    }

    /**
     * 导出加工采购价主列表
     */
    @Log(title = "加工采购价", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出加工采购价明细报表", notes = "导出加工采购价明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/report")
    public void exportRe(HttpServletResponse response, PurOutsourceReportResponse purOutsourceReportResponse) throws IOException {
        List<PurOutsourceReportResponse> list = purOutsourcePurchasePriceService.report(purOutsourceReportResponse);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PurOutsourceReportResponse> util = new ExcelUtil<>(PurOutsourceReportResponse.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, PurOutsourceReportResponse::new), "加工采购价明细报表");
    }

    /**
     * 导出加工采购价主列表
     */
    @Log(title = "加工采购价", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出加工采购价主列表", notes = "导出加工采购价主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurOutsourceReportResponse purOutsourceReportResponse) throws IOException {
        List<PurOutsourceReportResponse> list = purOutsourcePurchasePriceService.report(purOutsourceReportResponse);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<purOutPurchaseExResponse> util = new ExcelUtil<>(purOutPurchaseExResponse.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, purOutPurchaseExResponse::new), "加工采购价明细");
    }

    /**
     * 获取加工采购价主详细信息
     */
    @ApiOperation(value = "获取加工采购价主详细信息", notes = "获取加工采购价主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourcePurchasePrice.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long outsourcePurchasePriceSid) {
        if (outsourcePurchasePriceSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purOutsourcePurchasePriceService.selectPurOutsourcePurchasePriceById(outsourcePurchasePriceSid));
    }

    /**
     * 提交时校验
     */
    @ApiOperation(value = "提交时校验", notes = "提交时校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchasePrice.class))
    @PostMapping("/processCheck")
    public AjaxResult processCheck(@RequestBody List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purOutsourcePurchasePriceService.processCheck(ids));
    }
    /**
     * 获取加工采购价价格
     */
    @ApiOperation(value = "获取加工采购价价格", notes = "获取加工采购价价格")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterial.class))
    @PostMapping("/getPrice")
    public AjaxResult getPrice(@RequestBody @Valid PurOutsourcePurchasePriceResponse response) {
        return AjaxResult.success(purOutsourcePurchasePriceService.getPrice(response));
    }

    /**
     * 新增加工采购价主
     */
    @ApiOperation(value = "新增加工采购价主", notes = "新增加工采购价主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "加工采购价", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        int row = purOutsourcePurchasePriceService.insertPurOutsourcePurchasePrice(purOutsourcePurchasePrice);
        if (row > 0) {
            return AjaxResult.success("操作成功", new PurOutsourcePurchasePrice()
                    .setOutsourcePurchasePriceSid(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid()));
        }
        return toAjax(row);
    }

    /**
     * 校验新增加工采购价
     */
    @ApiOperation(value = "校验新增加工采购价", notes = "校验新增加工采购价主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "加工采购价", businessType = BusinessType.INSERT)
    @PostMapping("/judgeAdd")
    public AjaxResult judgeAdd(@RequestBody PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        return AjaxResult.success(purOutsourcePurchasePriceService.judgeAdd(purOutsourcePurchasePrice));
    }

    /**
     * 修改加工采购价主
     */
    @ApiOperation(value = "修改加工采购价主", notes = "修改加工采购价主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "加工采购价", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        return toAjax(purOutsourcePurchasePriceService.updatePurOutsourcePurchasePrice(purOutsourcePurchasePrice));
    }

    /**
     * 修改加工采购价主
     */
    @ApiOperation(value = "修改/变更 加工采购价主-新", notes = "修改/变更 加工采购价主-新")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "加工采购价", businessType = BusinessType.UPDATE)
    @PostMapping("/edit/new")
    public AjaxResult editNew(@RequestBody PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        return toAjax(purOutsourcePurchasePriceService.updatePurOutsourcePurchasePriceNew(purOutsourcePurchasePrice));
    }

    /**
     * 变更加工采购价主
     */
    @ApiOperation(value = "变更加工采购价主", notes = "变更加工采购价主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "加工采购价", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        return toAjax(purOutsourcePurchasePriceService.changePurOutsourcePurchasePrice(purOutsourcePurchasePrice));
    }

    /**
     * 删除加工采购价主
     */
    @ApiOperation(value = "删除加工采购价主", notes = "删除加工采购价主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "加工采购价", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> outsourcePurchasePriceSids) {
        if (ArrayUtil.isEmpty(outsourcePurchasePriceSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purOutsourcePurchasePriceService.deletePurOutsourcePurchasePriceByIds(outsourcePurchasePriceSids));
    }

    /**
     * 删除加工采购价明细行
     */
    @ApiOperation(value = "删除加工采购价明细行", notes = "删除加工采购价明细行")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "加工采购价", businessType = BusinessType.DELETE)
    @PostMapping("/delete/item")
    public AjaxResult removeItem(@RequestBody List<Long> outsourcePurchasePriceSids) {
        if (ArrayUtil.isEmpty(outsourcePurchasePriceSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purOutsourcePurchasePriceService.deleteItems(outsourcePurchasePriceSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "加工采购价", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        return AjaxResult.success(purOutsourcePurchasePriceService.changeStatus(purOutsourcePurchasePrice));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "加工采购价", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        return toAjax(purOutsourcePurchasePriceService.check(purOutsourcePurchasePrice));
    }

    /**
     * 导入加工采购价
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入加工采购价", notes = "导入加工采购价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataM(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return purOutsourcePurchasePriceService.importDataOutPur(file);
    }

    @ApiOperation(value = "下载外发加工采购价导入模板", notes = "下载外发加工采购价导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_外发加工采购价_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_外发加工采购价_V0.1.xlsx", "UTF-8"));
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
