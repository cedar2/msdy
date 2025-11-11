package com.platform.ems.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.TecProductLine;
import com.platform.ems.domain.TecProductLineposMat;
import com.platform.ems.domain.TecProductLineposMatColor;
import com.platform.ems.domain.dto.request.EstimateLineReportRequest;
import com.platform.ems.domain.dto.response.export.TecProductLineposMatExport;
import com.platform.ems.domain.excel.TecProductLineposMatColorExcel;
import com.platform.ems.domain.excel.TecProductLineposMatExcel;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ITecProductLineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品线Controller
 *
 * @author linhongwei
 * @date 2021-10-21
 */
@RestController
@RequestMapping("/product/line")
@Api(tags = "商品线")
public class TecProductLineController extends BaseController {

    @Autowired
    private ITecProductLineService tecProductLineService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询商品线列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询商品线列表", notes = "查询商品线列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecProductLine.class))
    public TableDataInfo list(@RequestBody TecProductLine tecProductLine) {
        startPage(tecProductLine);
        List<TecProductLine> list = tecProductLineService.selectTecProductLineList(tecProductLine);
        return getDataTable(list);
    }

    /**
     * 查询商品线列表
     */
    @PostMapping("/est/line")
    @ApiOperation(value = "查询物料需求报表线用量", notes = "查询物料需求报表线用量")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult estLine(@RequestBody List<EstimateLineReportRequest> list) {
        return AjaxResult.success(tecProductLineService.getEstLine(list));
    }

    /**
     * 导出商品线列表
     */
    @Log(title = "商品线", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出商品线列表", notes = "导出商品线列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, TecProductLine tecProductLine) throws IOException {
        List<TecProductLine> list = tecProductLineService.selectTecProductLineList(tecProductLine);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<TecProductLine> util = new ExcelUtil<>(TecProductLine.class, dataMap);
        util.exportExcel(response, list, "商品线" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 根据商品sid导出商品线列表
     */
    @ApiOperation(value = "根据商品sid导出商品线列表", notes = "根据商品sid导出商品线列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/list/export")
    public void ListExport(HttpServletResponse response, TecProductLine tecProductLine) throws IOException {
        if (ArrayUtil.isEmpty(tecProductLine.getProductSidList())) {
            throw new BaseException("请选择行");
        }
        if (tecProductLine.getProductSidList().length > 10) {
            throw new BaseException("最多选择10个商品！");
        }
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 多页签导出
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        // key 页签名
        Map<String, List<TecProductLineposMatExport>> map = new HashMap<>();
        ExcelUtil<TecProductLineposMatExport> util = new ExcelUtil<>(TecProductLineposMatExport.class, dataMap);
        for (int i = 0; i < tecProductLine.getProductSidList().length; i++) {
            TecProductLine line = tecProductLineService.selectTecProductLineById(tecProductLine.getProductSidList()[i]);
            if (line != null && CollectionUtil.isNotEmpty(line.getTecProductLineposMatList())) {
                List<TecProductLineposMatExport> matList = new ArrayList<>();
                line.getTecProductLineposMatList().forEach(mat->{
                    List<TecProductLineposMatColor> matColorList = mat.getTecProductLineposMatColorList();
                    if (CollectionUtil.isNotEmpty(matColorList)) {
                        matColorList.forEach(color->{
                            TecProductLineposMatExport sheetRow = new TecProductLineposMatExport();
                            sheetRow.setProductCode(line.getMaterialCode())
                                    .setProductName(line.getMaterialName())
                                    .setProductUnitBaseName(line.getUnitBaseName());
                            sheetRow.setMaterialCode(mat.getMaterialCode())
                                    .setMaterialName(mat.getMaterialName())
                                    .setLinePositionName(mat.getLinePositionName())
                                    .setLinePositionCategory(mat.getLinePositionCategory())
                                    .setQuantity(mat.getQuantity())
                                    .setQuantityUnitName(mat.getQuantityUnitName())
                                    .setUnitBaseName(mat.getUnitBaseName())
                                    .setMeasureDescription(mat.getMeasureDescription())
                                    .setRemark(mat.getRemark());
                            sheetRow.setProductSkuName(color.getProductSkuName())
                                    .setMaterialSkuName(color.getMaterialSkuName());
                            matList.add(sheetRow);
                        });
                    }
                    else {
                        TecProductLineposMatExport sheetRow = new TecProductLineposMatExport();
                        BeanCopyUtils.copyProperties(mat, sheetRow);
                        sheetRow.setProductCode(line.getMaterialCode()).setProductName(line.getMaterialName())
                                .setProductUnitBaseName(line.getUnitBaseName());
                        matList.add(sheetRow);
                    }
                });
                map.put(String.valueOf(line.getMaterialCode()), matList);
            }
        }
        if (map.size() > 0) {
            util.exportExcelMap(response, map);
        }
        else {
            throw new BaseException("所选商品无线用量数据导出！");
        }
    }

    /**
     * 获取商品线详细信息
     */
    @ApiOperation(value = "获取商品线详细信息", notes = "获取商品线详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecProductLine.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long materialSid) {
        if (materialSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(tecProductLineService.selectTecProductLineById(materialSid));
    }

    /**
     * 导出商品线详细信息 bom详情页面物料清单的查看线用量
     */
    @ApiOperation(value = "导出商品线详细信息", notes = "导出商品线详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/getInfo/export")
    public void getInfoExport(HttpServletResponse response, Long materialSid) throws IOException {
        if (materialSid == null) {
            throw new BaseException("参数缺失！");
        }
        TecProductLine productLine = tecProductLineService.selectTecProductLineById(materialSid);
        if (productLine == null) {
            throw new BaseException("商品线用量不存在！");
        }
        List<TecProductLineposMat> lineposMatList = productLine.getTecProductLineposMatList();
        // 第一个页签 线明细
        List<TecProductLineposMatExcel> firstSheet = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(lineposMatList)) {
            lineposMatList.forEach(mat->{
                List<TecProductLineposMatColor> matColorList = mat.getTecProductLineposMatColorList();
                if (CollectionUtil.isNotEmpty(matColorList)) {
                    matColorList.forEach(color->{
                        TecProductLineposMatExcel firstSheetRow = new TecProductLineposMatExcel();
                        firstSheetRow.setProductCode(productLine.getMaterialCode())
                                .setProductName(productLine.getMaterialName());
                        firstSheetRow.setMaterialCode(mat.getMaterialCode())
                                .setMaterialName(mat.getMaterialName())
                                .setLinePositionCode(mat.getLinePositionCode())
                                .setLinePositionName(mat.getLinePositionName())
                                .setQuantity(mat.getQuantity())
                                .setQuantityUnitName(mat.getQuantityUnitName())
                                .setUnitBaseName(mat.getUnitBaseName())
                                .setRemark(mat.getRemark());
                        firstSheetRow.setProductSkuName(color.getProductSkuName())
                                .setMaterialSkuName(color.getMaterialSkuName());
                        firstSheet.add(firstSheetRow);
                    });
                }
            });
        }
        // 第二个页签 线小计
        List<TecProductLineposMatColorExcel> secondSheet = new ArrayList<>();
        HashMap<String, TecProductLineposMatColorExcel> map = new HashMap<>();
        if (CollectionUtil.isNotEmpty(firstSheet)) {
            firstSheet.forEach(row->{
                String key = row.getProductCode()+row.getMaterialCode()+row.getProductSkuName()+row.getMaterialSkuName()+row.getQuantityUnitName();
                TecProductLineposMatColorExcel colorExcel = new TecProductLineposMatColorExcel();
                if (map.containsKey(key) && map.get(key) != null) {
                    colorExcel = map.get(key);
                    BigDecimal quantity = colorExcel.getQuantity().add(row.getQuantity());
                    colorExcel.setQuantity(quantity);
                }
                else {
                    BeanCopyUtils.copyProperties(row,colorExcel);
                    map.put(key, colorExcel);
                }
                map.put(key, colorExcel);
            });
            if (map.size() > 0) {
                for (String key : map.keySet()) {
                    secondSheet.add(map.get(key));
                }
            }
        }

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");

        // 测试多sheel导出
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
        WriteSheet sheet1 = EasyExcel.writerSheet(0, productLine.getMaterialCode() + "线明细").head(TecProductLineposMatExcel.class).build();
        WriteSheet sheet2 = EasyExcel.writerSheet(1, productLine.getMaterialCode() + "线小计").head(TecProductLineposMatColorExcel.class).build();
        excelWriter.write(firstSheet,sheet1).write(secondSheet,sheet2);
        excelWriter.finish();
    }

    /**
     * 新增商品线
     */
    @ApiOperation(value = "新增商品线", notes = "新增商品线")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品线", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid TecProductLine tecProductLine) {
        return toAjax(tecProductLineService.insertTecProductLine(tecProductLine));
    }

    /**
     * 修改商品线
     */
    @ApiOperation(value = "修改商品线", notes = "修改商品线")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品线", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid TecProductLine tecProductLine) {
        return toAjax(tecProductLineService.updateTecProductLine(tecProductLine));
    }

    /**
     * 变更商品线
     */
    @ApiOperation(value = "变更商品线", notes = "变更商品线")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品线", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid TecProductLine tecProductLine) {
        return toAjax(tecProductLineService.changeTecProductLine(tecProductLine));
    }

    /**
     * 删除商品线
     */
    @ApiOperation(value = "删除商品线", notes = "删除商品线")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品线", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> productLineSids) {
        if (CollectionUtils.isEmpty(productLineSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(tecProductLineService.deleteTecProductLineByIds(productLineSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品线", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody TecProductLine tecProductLine) {
        return toAjax(tecProductLineService.check(tecProductLine));
    }

    /**
     * 添加线部位时校验名称是否重复
     */
    @ApiOperation(value = "添加线部位时校验名称是否重复", notes = "添加线部位时校验名称是否重复")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "添加线部位时校验名称是否重复", businessType = BusinessType.DELETE)
    @PostMapping("/verifyPosition")
    public AjaxResult verifyProcess(@RequestBody TecProductLineposMat tecProductLineposMat) {
        return AjaxResult.success(tecProductLineService.verifyPosition(tecProductLineposMat));
    }
}
