package com.platform.ems.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.constant.HttpStatus;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.annotation.FieldScope;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.PayProcessStepComplete;
import com.platform.ems.domain.dto.response.export.PayProductJijianSettleInforCollect;
import com.platform.ems.mapper.PayProductJijianSettleInforMapper;
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
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.PayProductJijianSettleInfor;
import com.platform.ems.service.IPayProductJijianSettleInforService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品计件结算信息Controller
 *
 * @author chenkw
 * @date 2022-07-14
 */
@RestController
@RequestMapping("/pay/product/jijian/settle/infor")
@Api(tags = "商品计件结算信息")
public class PayProductJijianSettleInforController extends BaseController {

    @Autowired
    private IPayProductJijianSettleInforService payProductJijianSettleInforService;
    @Autowired
    private PayProductJijianSettleInforMapper payProductJijianSettleInforMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioClient client;
    @Autowired
    private MinioConfig minioConfig;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询商品计件结算信息列表
     */
    @PreAuthorize(hasPermi = "ems:pay:product:jijian:settle:infor:list")
    @PostMapping("/list")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all", note = "工厂")
    @ApiOperation(value = "查询商品计件结算信息列表" , notes = "查询商品计件结算信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = PayProductJijianSettleInfor.class))
    public TableDataInfo list(@RequestBody PayProductJijianSettleInfor payProductJijianSettleInfor) {
        TableDataInfo rspData = new TableDataInfo();
        PayProductJijianSettleInfor request = new PayProductJijianSettleInfor();
        BeanCopyUtils.copyProperties(payProductJijianSettleInfor,request);
        request.setPageNum(null).setPageSize(null).setPageBegin(null);
        long total = (long) payProductJijianSettleInforMapper.selectPayProductJijianSettleInforList(request).size();
        rspData.setTotal(total);
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(new ArrayList<>());
        rspData.setMsg("查询成功");
        if (total > 0){
            List<PayProductJijianSettleInfor> list = payProductJijianSettleInforService.selectPayProductJijianSettleInforList(payProductJijianSettleInfor);
            rspData.setRows(list);
        }
        return rspData;
    }

    /**
     * 导出商品计件结算信息列表
     */
    @Log(title = "商品计件结算信息" , businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出商品计件结算信息列表" , notes = "导出商品计件结算信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = void.class))
    @PostMapping("/export")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all", note = "工厂", loc = 1)
    public void export(HttpServletResponse response, PayProductJijianSettleInfor payProductJijianSettleInfor) throws IOException {
        payProductJijianSettleInfor.setPageSize(null).setPageNum(null);
        List<PayProductJijianSettleInfor> list = payProductJijianSettleInforService.selectPayProductJijianSettleInforList(payProductJijianSettleInfor);
        if (CollectionUtils.isNotEmpty(list)){
            DecimalFormat df = new DecimalFormat("########.###");
            DecimalFormat df4 = new DecimalFormat("########.####");
            list.forEach(item->{
                item.setSettleQuantityToString(item.getSettleQuantity()==null?"":df.format(item.getSettleQuantity()));
                item.setTotalPriceBlqToString(item.getTotalPriceBlq()==null?"":df4.format(item.getTotalPriceBlq()));
                item.setTotalPriceBlhToString(item.getTotalPriceBlh()==null?"":df4.format(item.getTotalPriceBlh()));
            });
        }
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PayProductJijianSettleInfor> util = new ExcelUtil<>(PayProductJijianSettleInfor.class, dataMap);
        util.exportExcel(response, list, "商品计件结算信息");
    }

    /**
     * 获取商品计件结算信息详细信息
     */
    @ApiOperation(value = "获取商品计件结算信息详细信息" , notes = "获取商品计件结算信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = PayProductJijianSettleInfor.class))
    @PreAuthorize(hasPermi = "ems:pay:product:jijian:settle:infor:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long jijianSettleInforSid) {
        if (jijianSettleInforSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(payProductJijianSettleInforService.selectPayProductJijianSettleInforById(jijianSettleInforSid));
    }

    /**
     * 新增商品计件结算信息
     */
    @ApiOperation(value = "新增商品计件结算信息" , notes = "新增商品计件结算信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pay:product:jijian:settle:infor:add")
    @Log(title = "商品计件结算信息" , businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PayProductJijianSettleInfor payProductJijianSettleInfor) {
        return toAjax(payProductJijianSettleInforService.insertPayProductJijianSettleInfor(payProductJijianSettleInfor));
    }

    /**
     * 修改商品计件结算信息
     */
    @ApiOperation(value = "修改商品计件结算信息" , notes = "修改商品计件结算信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pay:product:jijian:settle:infor:edit")
    @Log(title = "商品计件结算信息" , businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid PayProductJijianSettleInfor payProductJijianSettleInfor) {
        return toAjax(payProductJijianSettleInforService.updatePayProductJijianSettleInfor(payProductJijianSettleInfor));
    }

    /**
     * 变更商品计件结算信息
     */
    @ApiOperation(value = "变更商品计件结算信息" , notes = "变更商品计件结算信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pay:product:jijian:settle:infor:change")
    @Log(title = "商品计件结算信息" , businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid PayProductJijianSettleInfor payProductJijianSettleInfor) {
        return toAjax(payProductJijianSettleInforService.changePayProductJijianSettleInfor(payProductJijianSettleInfor));
    }

    /**
     * 删除商品计件结算信息
     */
    @ApiOperation(value = "删除商品计件结算信息" , notes = "删除商品计件结算信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:pay:product:jijian:settle:infor:remove")
    @Log(title = "商品计件结算信息" , businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> jijianSettleInforSids) {
        if (CollectionUtils.isEmpty(jijianSettleInforSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(payProductJijianSettleInforService.deletePayProductJijianSettleInforByIds(jijianSettleInforSids));
    }

    @ApiOperation(value = "确认" , notes = "确认")
    @PreAuthorize(hasPermi = "ems:pay:product:jijian:settle:infor:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = AjaxResult.class))
    @Log(title = "商品计件结算信息" , businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody PayProductJijianSettleInfor payProductJijianSettleInfor) {
        return toAjax(payProductJijianSettleInforService.check(payProductJijianSettleInfor));
    }

    @ApiOperation(value = "下载导入模板", notes = "下载导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/SCM_导入模板_商品计件结算数_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("SCM_导入模板_商品计件结算数_V1.0.xlsx", "UTF-8"));
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

    /**
     * 导入
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入", notes = "导入")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(payProductJijianSettleInforService.importData(file));
    }

    @PostMapping("/listBy")
    @ApiOperation(value = "根据计薪申报量单查询" , notes = "根据计薪申报量单查询")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = PayProductJijianSettleInfor.class))
    public AjaxResult listBy(@RequestBody PayProcessStepComplete payProcessStepComplete) {
        List<PayProductJijianSettleInfor> list = payProductJijianSettleInforService.listBy(payProcessStepComplete);
        return AjaxResult.success(list);
    }

    @PostMapping("/collect")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all", note = "工厂")
    @ApiOperation(value = "计件结算数汇总" , notes = "计件结算数汇总")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = PayProductJijianSettleInfor.class))
    public TableDataInfo collect(@RequestBody PayProductJijianSettleInfor payProductJijianSettleInfor) {
        TableDataInfo rspData = new TableDataInfo();
        PayProductJijianSettleInfor request = new PayProductJijianSettleInfor();
        BeanCopyUtils.copyProperties(payProductJijianSettleInfor,request);
        request.setPageNum(null).setPageSize(null).setPageBegin(null);
        long total = (long) payProductJijianSettleInforMapper.collectPayProductJijianSettleInforList(request).size();
        rspData.setTotal(total);
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(new ArrayList<>());
        rspData.setMsg("查询成功");
        if (total > 0){
            List<PayProductJijianSettleInfor> list = payProductJijianSettleInforService.collect(payProductJijianSettleInfor);
            rspData.setRows(list);
        }
        return rspData;
    }

    /**
     * 导出计件结算数汇总
     */
    @Log(title = "导出计件结算数汇总" , businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出计件结算数汇总" , notes = "导出计件结算数汇总")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = void.class))
    @PostMapping("/collect/export")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all", note = "工厂", loc = 1)
    public void exportCollect(HttpServletResponse response, PayProductJijianSettleInfor payProductJijianSettleInfor) throws IOException {
        payProductJijianSettleInfor.setPageSize(null).setPageNum(null);
        List<PayProductJijianSettleInfor> list = payProductJijianSettleInforService.collect(payProductJijianSettleInfor);
        if (CollectionUtils.isNotEmpty(list)){
            DecimalFormat df = new DecimalFormat("########.###");
            DecimalFormat df4 = new DecimalFormat("########.####");
            list.forEach(item->{
                item.setSettleQuantityCheckToString(item.getSettleQuantityCheck()==null?"":df.format(item.getSettleQuantityCheck()));
                item.setSettleQuantityTotalToString(item.getSettleQuantityTotal()==null?"":df.format(item.getSettleQuantityTotal()));
                item.setShicaiQuantityToString(item.getShicaiQuantity()==null?"":df.format(item.getShicaiQuantity()));
                item.setWeiSettleQuantityCheckToString(item.getWeiSettleQuantityCheck()==null?"":df.format(item.getWeiSettleQuantityCheck()));
                item.setWeiSettleQuantityTotalToString(item.getWeiSettleQuantityTotal()==null?"":df.format(item.getWeiSettleQuantityTotal()));
                item.setTotalPriceBlqToString(item.getTotalPriceBlq()==null?"":df4.format(item.getTotalPriceBlq()));
                item.setTotalPriceBlhToString(item.getTotalPriceBlh()==null?"":df4.format(item.getTotalPriceBlh()));
            });
        }
        List<PayProductJijianSettleInforCollect> collectList = BeanCopyUtils.copyListProperties(list, PayProductJijianSettleInforCollect::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PayProductJijianSettleInforCollect> util = new ExcelUtil<>(PayProductJijianSettleInforCollect.class, dataMap);
        util.exportExcel(response, collectList, "计件结算数汇总");
    }

    /**
     * 查询商品计件结算信息列表
     */
    @PostMapping("/collect/list")
    @ApiOperation(value = "查询商品计件结算信息列表" , notes = "查询商品计件结算信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = PayProductJijianSettleInfor.class))
    public TableDataInfo collectList(@RequestBody PayProductJijianSettleInfor payProductJijianSettleInfor) {
        TableDataInfo rspData = new TableDataInfo();
        PayProductJijianSettleInfor request = new PayProductJijianSettleInfor();
        BeanCopyUtils.copyProperties(payProductJijianSettleInfor,request);
        request.setPageNum(null).setPageSize(null).setPageBegin(null);
        long total = (long) payProductJijianSettleInforMapper.selectPayProductJijianSettleInforListPrecision(request).size();
        rspData.setTotal(total);
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(new ArrayList<>());
        rspData.setMsg("查询成功");
        if (total > 0){
            List<PayProductJijianSettleInfor> list = payProductJijianSettleInforService.selectPayProductJijianSettleInforListPrecision(payProductJijianSettleInfor);
            rspData.setRows(list);
        }
        return rspData;
    }

    @PostMapping("/collect/complete")
    @ApiOperation(value = "计薪量申报结算数累计(计薪量申报明细页面的结算数累计页签)" , notes = "计薪量申报结算数累计(计薪量申报明细页面的结算数累计页签)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功" , response = PayProductJijianSettleInfor.class))
    public AjaxResult collectComplete(@RequestBody PayProcessStepComplete payProcessStepComplete) {
        if (CollectionUtils.isEmpty(payProcessStepComplete.getPayProcessStepCompleteItemList())) {
            return AjaxResult.success(new ArrayList<>());
        }
        return AjaxResult.success(payProductJijianSettleInforService.collectBy(payProcessStepComplete));
    }

}
