package com.platform.ems.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.constant.HttpStatus;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.annotation.FieldScope;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.PaySalaryBillItemRequest;
import com.platform.ems.domain.dto.response.PaySalaryBillItemExResponse;
import com.platform.ems.domain.dto.response.form.PaySalaryWageFormResponse;
import com.platform.ems.service.*;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工资单-主Controller
 *
 * @author linhongwei
 * @date 2021-09-14
 */
@RestController
@RequestMapping("/salary/bill")
@Api(tags = "工资单-主")
public class PaySalaryBillController extends BaseController {

    @Autowired
    private IPaySalaryBillService paySalaryBillService;
    @Autowired
    private IPaySalaryBillItemService paySalaryBillItemService;
    @Autowired
    private IPayProcessStepCompleteItemService payProcessStepCompleteItemService;
    @Autowired
    private IBasStaffService basStaffService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private MinioClient client;
    @Autowired
    private MinioConfig minioConfig;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询工资单-主列表
     */
    @PostMapping("/list")
    @PreAuthorize(hasPermi = "ems:salary:bill:list")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all")
    @ApiOperation(value = "查询工资单-主列表", notes = "查询工资单-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PaySalaryBill.class))
    public TableDataInfo list(@RequestBody PaySalaryBill paySalaryBill) {
        startPage(paySalaryBill);
        List<PaySalaryBill> list = paySalaryBillService.selectPaySalaryBillList(paySalaryBill);
        return getDataTable(list);
    }

    /**
     * 获取工资单-主详细信息
     */
    @PostMapping("/getInfo")
    @PreAuthorize(hasPermi = "ems:salary:bill:query")
    @ApiOperation(value = "获取工资单-主详细信息", notes = "获取工资单-主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PaySalaryBill.class))
    public AjaxResult getInfo(Long salaryBillSid) {
        if (salaryBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(paySalaryBillService.selectPaySalaryBillById(salaryBillSid));
    }

    /**
     * 新增工资单-主
     */
    @PostMapping("/add")
    @PreAuthorize(hasPermi = "ems:salary:bill:add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "工资单-主", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增工资单-主", notes = "新增工资单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult add(@RequestBody @Valid PaySalaryBill paySalaryBill) {
        return toAjax(paySalaryBillService.insertPaySalaryBill(paySalaryBill));
    }

    /**
     * 修改工资单-主
     */
    @PostMapping("/edit")
    @PreAuthorize(hasPermi = "ems:salary:bill:edit")
    @Log(title = "工资单-主", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改工资单-主", notes = "修改工资单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult edit(@RequestBody @Valid PaySalaryBill paySalaryBill) {
        return toAjax(paySalaryBillService.updatePaySalaryBill(paySalaryBill));
    }

    /**
     * 变更工资单-主
     */
    @PostMapping("/change")
    @PreAuthorize(hasPermi = "ems:salary:bill:change")
    @Log(title = "工资单-主", businessType = BusinessType.CHANGE)
    @ApiOperation(value = "变更工资单-主", notes = "变更工资单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody @Valid PaySalaryBill paySalaryBill) {
        return toAjax(paySalaryBillService.changePaySalaryBill(paySalaryBill));
    }

    /**
     * 删除工资单-主
     */
    @PostMapping("/delete")
    @PreAuthorize(hasPermi = "ems:salary:bill:remove")
    @Log(title = "工资单-主", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除工资单-主", notes = "删除工资单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult remove(@RequestBody List<Long> salaryBillSids) {
        if (CollectionUtils.isEmpty(salaryBillSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(paySalaryBillService.deletePaySalaryBillByIds(salaryBillSids));
    }

    @PostMapping("/check")
    @PreAuthorize(hasPermi = "ems:salary:bill:check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "工资单-主", businessType = BusinessType.CHECK)
    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult check(@RequestBody PaySalaryBill paySalaryBill) {
        return toAjax(paySalaryBillService.check(paySalaryBill));
    }

    // =============================================================== //

    /**
     * 计件工资(自动)
     */
    @PostMapping("/getPieceworkSalary")
    @ApiOperation(value = "计件工资(自动)", notes = "计件工资(自动)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PaySalaryBillItem.class))
    public AjaxResult paySalaryBillItem(@RequestBody PayProcessStepCompleteItem payProcessStepCompleteItem) {
        return AjaxResult.success(paySalaryBillService.getPieceworkSalary(payProcessStepCompleteItem));
    }

    /**
     * 明细查询员工工资单
     */
    @PostMapping("/item/getStaff")
    @ApiOperation(value = "明细查询员工工资单", notes = "明细查询员工工资单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PaySalaryBillItem.class))
    public TableDataInfo getBasStaffList(@RequestBody PaySalaryBillItem paySalaryBillItem) {
        // 先查询员工
        BasStaff basStaff = new BasStaff();
        BeanCopyUtils.copyProperties(paySalaryBillItem, basStaff);
        // 处理员工的分页
        startPage(paySalaryBillItem);
        List<BasStaff> staffList = basStaffService.selectBasStaffList(basStaff);
        TableDataInfo response =  getDataTable(staffList);
        // 得到带有工资等信息的列表
        response.setRows(paySalaryBillItemService.getProcessStepCompleteWage(staffList, paySalaryBillItem));
        return response;
    }

    /**
     * 工资单明细校验
     */
    @PostMapping("/verifyItem")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "工资单明细校验", businessType = BusinessType.DELETE)
    @ApiOperation(value = "工资单明细校验", notes = "工资单明细校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult verifyPrice(@RequestBody PaySalaryBill paySalaryBill) {
        return AjaxResult.success(paySalaryBillService.verifyItem(paySalaryBill));
    }

    // =============================================================== //

    /**
     * 导出某一笔工资单信息清单明细列表
     */
    @PostMapping("/item/export")
    @Log(title = "导出某一笔工资单信息清单明细列表", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出某一笔工资单信息清单明细列表", notes = "导出某一笔工资单信息清单明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public void exportItem(HttpServletResponse response, PaySalaryBill paySalaryBill) {
        paySalaryBillService.exportItemBySalary(response, paySalaryBill);
    }

    /**
     * 导入某一笔工资单的工资清单明细
     */
    @PostMapping("/item/import")
    @ApiOperation(value = "导入某一笔工资单的工资清单", notes = "导入某一笔工资单的工资清单明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importItemData(@RequestParam MultipartFile file, @RequestParam String salaryBillCode) {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        if (StrUtil.isBlank(salaryBillCode)) {
            throw new BaseException("请选择一笔工资单进行导入");
        }
        Object response = paySalaryBillService.importItemData(file, salaryBillCode);
        if (response instanceof Collection) {
            return AjaxResult.error("导入错误", response);
        } else {
            return AjaxResult.success(response);
        }
    }

    // =============================================================== //

    /**
     * 导出工资单-主列表
     */
    @PostMapping("/export")
    @PreAuthorize(hasPermi = "ems:salary:bill:export")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all", loc = 1)
    @Log(title = "工资单-主", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出工资单-主列表", notes = "导出工资单-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public void export(HttpServletResponse response, PaySalaryBill paySalaryBill) throws IOException {
        List<PaySalaryBill> list = paySalaryBillService.selectPaySalaryBillList(paySalaryBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PaySalaryBill> util = new ExcelUtil<>(PaySalaryBill.class, dataMap);
        util.exportExcel(response, list, "工资单");
    }

    @PostMapping("/importTemplate")
    @ApiOperation(value = "下载工资单导入模板", notes = "下载工资单导入模板")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/SCM_导入模板_工资表_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("SCM_导入模板_工资表_V0.1.xlsx", "UTF-8"));
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
     * 导入工资单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入工资单", notes = "导入工资单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = EmsResultEntity.class))
    public AjaxResult importData(@RequestParam MultipartFile file) {
        return AjaxResult.success(paySalaryBillService.importData(file));
    }

    // =============================================================== //

    /**
     * 工资明细报表
     */
    @PostMapping("/item/form")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all")
    @ApiOperation(value = "工资明细报表", notes = "工资明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PaySalaryBill.class))
    public TableDataInfo itemFormList(@RequestBody PaySalaryBillItemRequest paySalaryBillItem) {
        startPage(paySalaryBillItem);
        List<PaySalaryBillItemExResponse> list = paySalaryBillItemService.getReport(paySalaryBillItem);
        return getDataTable(list);
    }

    /**
     * 导出工资明细报表
     */
    @PostMapping("/item/form/export")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all", loc = 1)
    @ApiOperation(value = "导出工资明细报表", notes = "导出工资明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public void itemFormExport(HttpServletResponse response, PaySalaryBillItemRequest paySalaryBillItem) throws IOException {
        List<PaySalaryBillItemExResponse> list = paySalaryBillItemService.getReport(paySalaryBillItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PaySalaryBillItemExResponse> util = new ExcelUtil<>(PaySalaryBillItemExResponse.class, dataMap);
        util.exportExcel(response, list, "工资单明细报表");
    }

    @PostMapping("/item/set/salaryCostAllocateType")
    @ApiOperation(value = "工资单明细报表设置工资成本分摊", notes = "工资单明细报表设置工资成本分摊")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PaySalaryBillItem.class))
    public AjaxResult setSalaryCostAllocateType(@RequestBody PaySalaryBillItem paySalaryBillItem) {
        if (paySalaryBillItem.getBillItemSidList().length == 0){
            throw new BaseException("请选择行！");
        }
        return AjaxResult.success(paySalaryBillItemService.setSalaryCostAllocateType(paySalaryBillItem));
    }

    // =============================================================== //

    /**
     * 计薪量明细报表 查询
     */
    @PostMapping("/item/wageForm")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all")
    @ApiOperation(value = "计件工资明细报表", notes = "计件工资明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PaySalaryWageFormResponse.class))
    public TableDataInfo getWageForm(@RequestBody PaySalaryWageFormResponse paySalaryBillItem) {
        startPage(paySalaryBillItem);
        List<PaySalaryWageFormResponse> list = payProcessStepCompleteItemService.getProcessStepCompleteWage(paySalaryBillItem);
        return getDataTable(list);
    }

    /**
     * 计薪量明细报表 查询导出
     */
    @PostMapping("/item/wageForm/export")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all", loc = 1)
    @ApiOperation(value = "导出计件工资明细报表", notes = "导出计件工资明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public void exportWageForm(HttpServletResponse response, PaySalaryWageFormResponse paySalaryBillItem) throws IOException {
        List<PaySalaryWageFormResponse> list = payProcessStepCompleteItemService.getProcessStepCompleteWage(paySalaryBillItem);
        if (CollectionUtil.isNotEmpty(list)){
            DecimalFormat df1 = new DecimalFormat("########.#");
            DecimalFormat df2 = new DecimalFormat("########.##");
            DecimalFormat df3 = new DecimalFormat("########.###");
            DecimalFormat df4 = new DecimalFormat("########.####");
            list.forEach(item->{
                item.setCompleteQuantityToString(item.getCompleteQuantity() == null ? null : df1.format(item.getCompleteQuantity()));
                item.setPriceRateToString(item.getPriceRate() == null ? null : df3.format(item.getPriceRate()));
                item.setWangongPriceRateToString(item.getWangongPriceRate() == null ? null : df3.format(item.getWangongPriceRate()));
                item.setSortToString(item.getSort() == null ? null : df2.format(item.getSort()));
                item.setPriceToString(item.getPrice() == null ? null : df4.format(item.getPrice()));
                item.setMoneyToString(item.getMoney() == null ? null : df2.format(item.getMoney()));
            });
        }
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PaySalaryWageFormResponse> util = new ExcelUtil<>(PaySalaryWageFormResponse.class, dataMap);
        util.exportExcel(response, list, "计薪量申报明细报表");
    }

    /**
     * 计薪量明细报表汇总打印导出表单
     */
    @PostMapping("/item/wageForm/print")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all")
    @ApiOperation(value = "计件工资明细报表汇总", notes = "计件工资明细报表汇总")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PaySalaryWageFormResponse.class))
    public AjaxResult printWageForm(@RequestBody PaySalaryWageFormResponse paySalaryBillItem) {
        List<PaySalaryWageFormResponse> itemList = payProcessStepCompleteItemService.printProcessStepCompleteWageVo(paySalaryBillItem);
        if ("YG".equals(paySalaryBillItem.getDimension())){
            return AjaxResult.success(payProcessStepCompleteItemService.printProcessStepCompleteWageByStaff(itemList));
        }
        else if ("LSBZ".equals(paySalaryBillItem.getDimension())){
            return AjaxResult.success(payProcessStepCompleteItemService.printProcessStepCompleteWageByWorkCenter(itemList));
        }
        else {
            throw new BaseException("请选择汇总维度");
        }
    }

    /**
     * 计薪量申报明细报表汇总打印导出表单
     */
    @PostMapping("/item/wageForm/print/export")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all", loc = 1)
    @ApiOperation(value = "计件工资明细报表汇总打印导出表单", notes = "计件工资明细报表汇总打印导出表单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public void printExportWageForm(HttpServletResponse response, PaySalaryWageFormResponse paySalaryBillItem) {
        payProcessStepCompleteItemService.printExcelProcessStepCompleteWage(response, paySalaryBillItem);
    }
}
