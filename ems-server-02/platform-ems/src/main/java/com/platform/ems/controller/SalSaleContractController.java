package com.platform.ems.controller;

import cn.hutool.core.util.StrUtil;
import com.platform.common.core.domain.R;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.SalSaleContract;
import com.platform.ems.domain.SalSaleContractAttachment;
import com.platform.ems.domain.SalSalesOrder;
import com.platform.ems.domain.SalSalesOrderItem;
import com.platform.ems.domain.dto.response.form.SalSaleContractFormResponse;
import com.platform.ems.service.ISalSaleContractAttachmentService;
import com.platform.ems.service.ISalSaleContractPayMethodService;
import com.platform.ems.service.ISalSaleContractService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.task.ContractWarningTask;
import com.platform.ems.task.OrderTask;
import com.platform.ems.util.CommonUtil;
import com.platform.api.service.RemoteFileService;
import com.platform.framework.web.domain.server.SysFile;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.Collator;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

/**
 * 销售合同信息Controller
 *
 * @author linhongwei
 * @date 2021-05-18
 */
@RestController
@RequestMapping("/sale/contract")
@Api(tags = "销售合同信息")
public class SalSaleContractController extends BaseController {

    @Autowired
    private ISalSaleContractService salSaleContractService;
    @Autowired
    private ISalSaleContractPayMethodService salSaleContractPayMethodService;
    @Autowired
    private ISalSaleContractAttachmentService salSaleContractAttachmentService;


    @Autowired
    private ContractWarningTask contractWarningTask;
    @Autowired
    private OrderTask orderTask;

    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private RemoteFileService remoteFileService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;
    @Autowired
    RedissonClient redissonClient;

    private static final String LOCK_KEY = "SALE_CONTRACT_LOCK";

    private static final String FILLE_PATH = "/template";

    /**
     * 查询销售合同信息列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询销售合同信息列表", notes = "查询销售合同信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSaleContract.class))
    public TableDataInfo list(@RequestBody SalSaleContract salSaleContract) {
        startPage(salSaleContract);
        List<SalSaleContract> list = salSaleContractService.selectSalSaleContractList(salSaleContract);
        return getDataTable(list);
    }

    /**
     * 导出销售合同信息列表
     */
    @Log(title = "销售合同信息", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出销售合同信息列表", notes = "导出销售合同信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SalSaleContract salSaleContract) throws IOException {
        List<SalSaleContract> list = salSaleContractService.selectSalSaleContractList(salSaleContract);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SalSaleContract> util = new ExcelUtil<>(SalSaleContract.class, dataMap);
        util.exportExcel(response, list, "销售合同信息");
    }


    /**
     * 测试自动接口
     */
    @ApiOperation(value = "测试自动接口", notes = "测试自动接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/task/test")
    public AjaxResult taskTest() {
        orderTask.salesOrderContractPaper();
        return AjaxResult.success();
    }

    /**
     * 导入销售合同信息
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入销售合同信息", notes = "导入销售合同信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        Object response = salSaleContractService.importData(file);
        if (response instanceof Collection){
            return AjaxResult.error("导入错误", response);
        }
        else {
            return AjaxResult.success(response);
        }
    }

    @ApiOperation(value = "下载销售合同信息导入模板", notes = "下载销售合同信息导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        InputStream inputStream = null;
        OutputStream out =null;
        String fileName = FILLE_PATH + "/销售合同导入模板.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("销售合同导入模板.xlsx", "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            out  = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        }finally {
            if(inputStream!=null){
                inputStream.close();
            }
            if(out!=null){
                out.close();
            }
        }
    }

    /**
     * 获取销售合同信息详细信息
     */
    @ApiOperation(value = "获取销售合同信息详细信息", notes = "获取销售合同信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSaleContract.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long saleContractSid) {
        if (saleContractSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(salSaleContractService.selectSalSaleContractById(saleContractSid));
    }

    /**
     * 按“商品编码+合同交期”汇总“订单明细”页签的数据
     */
    @ApiOperation(value = "订单明细合计", notes = "订单明细合计")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrderItem.class))
    @PostMapping("/order/group")
    public AjaxResult orderGroup(@RequestBody SalSaleContract salSaleContract) {
        return AjaxResult.success(salSaleContractService.groupSaleOrderItemList(salSaleContract));
    }

    /**
     * 合同的订单明细页签调用接口
     */
    @ApiOperation(value = "合同的订单明细页签调用接口", notes = "合同的订单明细页签调用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrderItem.class))
    @PostMapping("/order/list")
    public AjaxResult orderItem(@RequestBody SalSaleContract salSaleContract) {
        return AjaxResult.success(salSaleContractService.saleOrderItemList(salSaleContract));
    }

    /**
     * “订单明细”页签，新增按钮：分配订单明细 点查询的接口
     */
    @ApiOperation(value = "“订单明细”页签，新增按钮：分配订单明细 点查询的接口", notes = "“订单明细”页签，新增按钮：分配订单明细 点查询的接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrderItem.class))
    @PostMapping("/order/item/group")
    public TableDataInfo orderItemGroup(@RequestBody SalSaleContract salSaleContract) {
        startPage(salSaleContract);
        List<SalSalesOrder> list = salSaleContractService.selectGroupSaleOrderItemList(salSaleContract);
        return getDataTable(list);
    }

    /**
     * 添加后，将选中销售订单的合同号改成此合同号，并刷新“订单明细”页签。
     */
    @ApiOperation(value = "将选中销售订单的合同号改成此合同号", notes = "将选中销售订单的合同号改成此合同号")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrderItem.class))
    @PostMapping("/order/changeContract")
    public AjaxResult orderChangeContract(@RequestBody SalSaleContract salSaleContract, String isContinue) {
        return AjaxResult.success(salSaleContractService.changeSaleOrderContract(salSaleContract, isContinue));
    }

    /**
     * 复制销售合同信息详细信息
     */
    @ApiOperation(value = "复制销售合同信息详细信息", notes = "复制销售合同信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSaleContract.class))
    @PostMapping("/copy")
    public AjaxResult copy(Long saleContractSid) {
        if (saleContractSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(salSaleContractService.copySalSaleContractById(saleContractSid));
    }

    /**
     * 新增/提交销售合同前的校验
     */
    @PostMapping("/check/code")
    public AjaxResult checkCode(@RequestBody SalSaleContract salSaleContract) {
        if (StrUtil.isBlank(salSaleContract.getSaleContractCode())){
            throw new BaseException("合同号不能为空");
        }
        try {
            salSaleContractService.checkCode(salSaleContract);
        }catch (CustomException e){
            return AjaxResult.success(e.getMessage(),false);
        }
        return AjaxResult.success(true);
    }

    /**
     * 变更/编辑/新建页面提交销售合同前的校验支付方式
     */
    @PostMapping("/check/pay")
    public AjaxResult checkPay(@RequestBody SalSaleContract salSaleContract) {
        String msg = "";
        String err = "";
        if (StrUtil.isBlank(salSaleContract.getContractSigner())){
            err = err + "合同签约人,";
        }
        if (salSaleContract.getContractSignDate() == null){
            err = err + "合同签约日期,";
        }
        if (StrUtil.isNotBlank(err) && err.endsWith(",")){
            err = err.substring(0, err.length() - 1);
            err = err + "不能为空";
        }
        if (ConstantsEms.CONTRACT_TYPE_BC.equals(salSaleContract.getContractType()) && salSaleContract.getOriginalSaleContractSid() == null){
            if (StrUtil.isNotBlank(err)){
                err = err + "，且补充协议的原合同号为必填";
            }
            else {
                err = "补充协议的原合同号为必填";
            }
        }
        if (StrUtil.isNotBlank(err)){
            err = "提交时，" + err + "，请填写后再操作！";
            return AjaxResult.error(err);
        }
        if (CollectionUtils.isNotEmpty(salSaleContract.getPayMethodListYusf())){
            msg = msg + salSaleContractPayMethodService.submitVerify(salSaleContract.getPayMethodListYusf(), ConstantsFinance.ACCOUNT_CAT_YSFK);
        }
        if (CollectionUtils.isNotEmpty(salSaleContract.getPayMethodListZq())){
            msg = msg + salSaleContractPayMethodService.submitVerify(salSaleContract.getPayMethodListZq(), ConstantsFinance.ACCOUNT_CAT_ZQK);
        }
        if (CollectionUtils.isNotEmpty(salSaleContract.getPayMethodListWq())){
            msg = msg + salSaleContractPayMethodService.submitVerify(salSaleContract.getPayMethodListWq(), ConstantsFinance.ACCOUNT_CAT_WK);
        }
        try {
            if (StrUtil.isNotBlank(msg) && msg.endsWith(",")) {
                msg = msg.substring(0, msg.length() - 1);
                msg = msg + "支付方式的明细和不为100%，是否确认提交？";
                throw new CustomException(msg);
            }
        }catch (CustomException e){
            return AjaxResult.success(e.getMessage(),false);
        }
        return AjaxResult.success(true);
    }

    /**
     * 新增销售合同信息
     */
    @ApiOperation(value = "新增销售合同信息", notes = "新增销售合同信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售合同信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SalSaleContract salSaleContract) {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        if (lock.isLocked()) {
            throw new CustomException("请勿重复操作");
        }
        lock.lock(15L, TimeUnit.SECONDS);
        Long contractSid = null;
        try {
            contractSid = salSaleContractService.insertSalSaleContract(salSaleContract);
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return AjaxResult.success("操作成功",contractSid.toString());
    }

    /**
     * 修改销售合同信息
     */
    @ApiOperation(value = "修改销售合同信息", notes = "修改销售合同信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售合同信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid SalSaleContract salSaleContract) {
        return toAjax(salSaleContractService.updateSalSaleContract(salSaleContract));
    }

    /**
     * 变更销售合同信息
     */
    @ApiOperation(value = "变更销售合同信息", notes = "变更销售合同信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售合同信息", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid SalSaleContract salSaleContract) {
        int row = salSaleContractService.changeSalSaleContract(salSaleContract);
        if (row == 100) {
            return AjaxResult.success("请按需更新后续相关数据！", 1);
        }
        return toAjax(row);
    }

    /**
     * 删除销售合同信息
     */
    @ApiOperation(value = "删除销售合同信息", notes = "删除销售合同信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售合同信息", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> saleContractSids) {
        if (CollectionUtils.isEmpty(saleContractSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(salSaleContractService.deleteSalSaleContractByIds(saleContractSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售合同信息", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody SalSaleContract salSaleContract) {
        return toAjax(salSaleContractService.check(salSaleContract));
    }

    /**
     * 销售合同下拉框列表
     */
    @PostMapping("/getSalSaleContractList")
    @ApiOperation(value = "销售合同下拉框列表", notes = "销售合同下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSaleContract.class))
    public List<SalSaleContract> getSalSaleContractList() {
        return salSaleContractService.getSalSaleContractList();
    }

    /**
     * 销售合同下拉框列表（带参数）
     */
    @PostMapping("/getSaleContractList")
    @ApiOperation(value = "销售合同下拉框列表（带参数）", notes = "销售合同下拉框列表（带参数）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSaleContract.class))
    public List<SalSaleContract> getSaleContractList(@RequestBody SalSaleContract salSaleContract) {
        return salSaleContractService.getSaleContractList(salSaleContract);
    }

    /**
     * 原合同号下拉框接口
     */
    @PostMapping("/getOriginalContractList")
    @ApiOperation(value = "原合同号下拉框接口", notes = "原合同号下拉框接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSaleContract.class))
    public AjaxResult getList(@RequestBody SalSaleContract salSaleContractr) {
        return AjaxResult.success(salSaleContractService.getOriginalContractList(salSaleContractr));
    }

    /**
     * 作废-销售合同信息
     */
    @ApiOperation(value = "作废销售合同信息", notes = "作废销售合同信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售合同信息", businessType = BusinessType.CANCEL)
    @PostMapping("/cancellation")
    public AjaxResult cancellation(@RequestBody SalSaleContract salSaleContractr) {
        if (salSaleContractr.getSaleContractSid() == null) {
            throw new BaseException("参数缺失");
        }
        if (StrUtil.isBlank(salSaleContractr.getCancelRemark())) {
            throw new BaseException("请填写作废说明再操作");
        }
        return toAjax(salSaleContractService.cancellationSalSaleContractById(salSaleContractr));
    }

    /**
     * 结案-销售合同信息
     */
    @ApiOperation(value = "结案销售合同信息", notes = "结案销售合同信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售合同信息", businessType = BusinessType.HANDLE)
    @PostMapping("/closing")
    public AjaxResult closing(Long saleContractSid) {
        if (saleContractSid == null) {
            throw new BaseException("参数缺失");
        }
        return toAjax(salSaleContractService.closingSalSaleContractById(saleContractSid));
    }

    /**
     * 纸质合同签收
     */
    @ApiOperation(value = "纸质合同签收", notes = "纸质合同签收")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售合同信息", businessType = BusinessType.HANDLE)
    @PostMapping("/sign")
    public AjaxResult sign(@RequestBody SalSaleContract salSaleContract) {
        if (CollectionUtils.isEmpty(salSaleContract.getSaleContractSids()) || StrUtil.isBlank(salSaleContract.getSignInStatus())) {
            throw new BaseException("参数缺失");
        }
        return toAjax(salSaleContractService.signSalSaleContractById(salSaleContract));
    }

    /**
     * 自动生成电子合同
     */
    @ApiOperation(value = "自动生成电子合同", notes = "自动生成电子合同")
    @PostMapping("/auto")
    @Transactional(rollbackFor = Exception.class)
    public void createPdf(HttpServletRequest request, HttpServletResponse response, Long saleContractSid){
        if (saleContractSid == null){
            throw new BaseException("请选择一份合同");
        }
        String fileName = FILLE_PATH + "/contract/销售合同电子版(空白模板).pdf";
        File pdf ;
        GetObjectResponse pdfResponse = null;
        GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
        try {
            pdfResponse= client.getObject(args);
            // 获取minio上的空白模板作为文件
            pdf = CommonUtil.asFile(pdfResponse,"销售合同电子版",".pdf");
            // 自动生成一份电子合同
            MultipartFile multipartFile = salSaleContractService.autoGenContract(pdf.getPath(), saleContractSid);
            // 删除临时文件
            pdf.deleteOnExit();
            if (multipartFile != null){
                // 将生成的电子合同上传到minio上
                R<SysFile> resFile = remoteFileService.upload(multipartFile);
                // 上传后的文件地址
                String filePath = resFile.getData().getUrl();
                if (resFile != null){
                    // 保存电子合同信息到附件
                    SalSaleContractAttachment salSaleContractAttachment = new SalSaleContractAttachment();
                    salSaleContractAttachment.setSaleContractSid(saleContractSid).setFileName(resFile.getData().getName())
                            .setFilePath(filePath).setFileType(ConstantsEms.FILE_TYPE_XSHT);
                    int row = salSaleContractAttachmentService.insertSalSaleContractAttachment(salSaleContractAttachment);
                    // 请求下载
                    if (row == 1) {
                        int size = FILLE_PATH.length();
                        filePath = filePath.substring(filePath.indexOf("/" + minioConfig.getBucketName()) + size);
                        InputStream inputStream = client.getObject(GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(filePath).build());
                        OutputStream outputStream = null;
                        try {
                            byte[] buffer = new byte[1024];
                            int len = 0;
                            response.reset();
                            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(filePath, "UTF-8"));
                            response.setContentType("application/octet-stream");
                            response.setCharacterEncoding("UTF-8");
                            outputStream = response.getOutputStream();
                            //输出文件
                            while((len = inputStream.read(buffer)) > 0){
                                outputStream.write(buffer, 0, len);
                            }
                        } catch (Exception e) {
                            throw new BaseException("读取文件异常:" + e.getMessage());
                        }finally {
                            if(inputStream!=null){
                                inputStream.close();
                            }
                            if(outputStream!=null){
                                outputStream.close();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ;
    }

    /**
     * 查询页面 上传附件前的校验
     */
    @ApiOperation(value = "查询页面-上传附件前的校验", notes = "查询页面-上传附件前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkAttach")
    public AjaxResult checkAttach(@RequestBody SalSaleContractAttachment salSaleContractAttachment) {
        return salSaleContractAttachmentService.check(salSaleContractAttachment);
    }

    @ApiOperation(value = "新增销售合同查询页面上传附件", notes = "新增销售合同查询页面上传附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售合同 - 上传附件", businessType = BusinessType.INSERT)
    @PostMapping("/addAttach")
    public AjaxResult addAttachment(@RequestBody @Valid SalSaleContractAttachment salSaleContractAttachment) {
        return AjaxResult.success(salSaleContractAttachmentService.insertSalSaleContractAttachment(salSaleContractAttachment));
    }

    @ApiOperation(value = "销售合同生成流水", notes = "销售合同生成流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售合同生成流水", businessType = BusinessType.INSERT)
    @PostMapping("/advance")
    public AjaxResult advanceReceipt(Long saleContractSid) {
        SalSaleContract salSaleContract = salSaleContractService.selectSalSaleContractById(saleContractSid);
        try {
            salSaleContractService.advanceReceipt(salSaleContract);
        }catch (Exception e){
            throw new BaseException("系统未知错误，请联系管理员");
        }
        return AjaxResult.success();
    }

    /**
     * 设置即将到期提醒天数
     */
    @ApiOperation(value = "设置即将到期提醒天数", notes = "设置即将到期提醒天数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setToexpireDays")
    public AjaxResult setToexpireDays(@RequestBody SalSaleContract salSaleContract) {
        return toAjax(salSaleContractService.setToexpireDays(salSaleContract));
    }

    /**
     * 查询销售合同统计报表
     */
    @PostMapping("/count")
    @ApiOperation(value = "查询销售合同统计报表", notes = "查询销售合同统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSaleContractFormResponse.class))
    public TableDataInfo count(@RequestBody SalSaleContract salSaleContract) {
        startPage(salSaleContract);
        List<SalSaleContractFormResponse> list = salSaleContractService.getCountForm(salSaleContract);
        TableDataInfo tableDataInfo = getDataTable(list);
        if (CollectionUtils.isNotEmpty(list)){
            list = list.stream().sorted(Comparator.comparing(SalSaleContractFormResponse::getYear, Comparator.reverseOrder())
                    .thenComparing(SalSaleContractFormResponse::getCustomerShortName, Collator.getInstance(Locale.CHINA))
                    .thenComparing(SalSaleContractFormResponse::getContractType)).collect(toList());
            tableDataInfo.setRows(list);
        }
        return tableDataInfo;
    }

    /**
     * 查询销售合同统计报表明细
     */
    @PostMapping("/count/item")
    @ApiOperation(value = "查询销售合同统计报表明细", notes = "查询销售合同统计报表明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSaleContractFormResponse.class))
    public TableDataInfo countItem(@RequestBody SalSaleContract salSaleContract) {
        startPage(salSaleContract);
        List<SalSaleContractFormResponse> list = salSaleContractService.getCountFormItem(salSaleContract);
        TableDataInfo tableDataInfo = getDataTable(list);
        if (CollectionUtils.isNotEmpty(list)){
            list = list.stream().sorted(Comparator.comparing(SalSaleContractFormResponse::getCompanyShortName, Collator.getInstance(Locale.CHINA))
                    .thenComparing(SalSaleContractFormResponse::getProductSeasonName, Collator.getInstance(Locale.CHINA))).collect(toList());
            tableDataInfo.setRows(list);
        }
        return tableDataInfo;
    }

    /**
     * 导出销售合同统计报表明细
     */
    @Log(title = "导出销售合同统计报表明细", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出销售合同统计报表明细", notes = "导出销售合同统计报表明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/count/export")
    public void exportCountItem(HttpServletResponse response, SalSaleContract salSaleContract) throws IOException {
        List<SalSaleContractFormResponse> list = salSaleContractService.getCountForm(salSaleContract);
        list = list.stream().sorted(Comparator.comparing(SalSaleContractFormResponse::getYear, Comparator.reverseOrder())
                .thenComparing(SalSaleContractFormResponse::getCustomerShortName, Collator.getInstance(Locale.CHINA))
                .thenComparing(SalSaleContractFormResponse::getContractType)).collect(toList());
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SalSaleContractFormResponse> util = new ExcelUtil<>(SalSaleContractFormResponse.class, dataMap);
        util.exportExcel(response, list, "销售合同统计报表");
    }
}
