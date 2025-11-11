package com.platform.ems.controller;

import cn.hutool.core.util.ArrayUtil;
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
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.FunFundAccount;
import com.platform.ems.domain.FunFundAccountExport;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IFunFundAccountService;
import com.platform.ems.service.ISystemDictDataService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 资金账户信息Controller
 *
 * @author chenkw
 * @date 2022-03-01
 */
@RestController
@RequestMapping("/fun/fund/account")
@Api(tags = "资金账户信息")
public class FunFundAccountController extends BaseController {

    @Autowired
    private IFunFundAccountService funFundAccountService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;
    private static final String FILLE_PATH = "/template";

    /**
     * 查询资金账户信息列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询资金账户信息列表", notes = "查询资金账户信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FunFundAccount.class))
    public TableDataInfo list(@RequestBody FunFundAccount funFundAccount) {
        startPage(funFundAccount);
        List<FunFundAccount> list = funFundAccountService.selectFunFundAccountList(funFundAccount);
        return getDataTable(list);
    }

    /**
     * 导出资金账户信息列表
     */
    @ApiOperation(value = "导出资金账户信息列表", notes = "导出资金账户信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FunFundAccount funFundAccount) throws IOException {
        List<FunFundAccount> list = funFundAccountService.selectFunFundAccountList(funFundAccount);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FunFundAccount> util = new ExcelUtil<>(FunFundAccount.class, dataMap);
        util.exportExcel(response, list, "资金账户信息");
    }

    /**
     * 导入资金账户
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入资金账户信息", notes = "导入资金账户信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        Object response = funFundAccountService.importData(file);
        if (response instanceof Collection) {
            return AjaxResult.error("导入错误", response);
        } else {
            return AjaxResult.success(response);
        }
    }

    @ApiOperation(value = "下载资金账户导入模板", notes = "下载资金账户导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_资金账户_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_资金账户_V0.1.xlsx", "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            out = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 获取资金账户信息详细信息
     */
    @ApiOperation(value = "获取资金账户信息详细信息", notes = "获取资金账户信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FunFundAccount.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long fundAccountSid) {
        if (fundAccountSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(funFundAccountService.selectFunFundAccountById(fundAccountSid));
    }

    /**
     * 新增资金账户信息
     */
    @ApiOperation(value = "新增资金账户信息", notes = "新增资金账户信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FunFundAccount funFundAccount) {
        int row = funFundAccountService.insertFunFundAccount(funFundAccount);
        if (row > 0) {
            return AjaxResult.success("操作成功", new FunFundAccount()
                    .setFundAccountSid(funFundAccount.getFundAccountSid()));
        }
        return toAjax(row);
    }

    /**
     * 修改资金账户信息
     */
    @ApiOperation(value = "修改资金账户信息", notes = "修改资金账户信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FunFundAccount funFundAccount) {
        return toAjax(funFundAccountService.updateFunFundAccount(funFundAccount));
    }

    /**
     * 更新余额
     */
    @ApiOperation(value = "更新余额", notes = "更新余额")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/update/amount")
    public AjaxResult updateCurrencyAmount(@RequestBody FunFundAccount funFundAccount) {
        return toAjax(funFundAccountService.updateAmount(funFundAccount));
    }

    /**
     * 变更资金账户信息
     */
    @ApiOperation(value = "变更资金账户信息", notes = "变更资金账户信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FunFundAccount funFundAccount) {
        return toAjax(funFundAccountService.changeFunFundAccount(funFundAccount));
    }

    /**
     * 删除资金账户信息
     */
    @ApiOperation(value = "删除资金账户信息", notes = "删除资金账户信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> fundAccountSids) {
        if (CollectionUtils.isEmpty(fundAccountSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(funFundAccountService.deleteFunFundAccountByIds(fundAccountSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FunFundAccount funFundAccount) {
        if (ArrayUtil.isEmpty(funFundAccount.getFundAccountSidList())) {
            throw new CheckedException("请选择行");
        }
        if (!HandleStatus.CONFIRMED.getCode().equals(funFundAccount.getHandleStatus())) {
            return toAjax(0);
        }
        return toAjax(funFundAccountService.check(funFundAccount));
    }

    @ApiOperation(value = "作废前校验", notes = "作废前校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("invalid/check")
    public AjaxResult checkInvalid(@RequestBody FunFundAccount funFundAccount) {
        if (funFundAccount.getFundAccountSidList() == null || funFundAccount.getFundAccountSidList().length == 0) {
            throw new CheckedException("请选择行");
        }
        if (!HandleStatus.INVALID.getCode().equals(funFundAccount.getHandleStatus())) {
            return toAjax(0);
        }
        try {
            funFundAccountService.checkInvalid(funFundAccount);
        } catch (Exception e) {
            return AjaxResult.success(e.getMessage(), true);
        }
        return AjaxResult.success();
    }

    @ApiOperation(value = "作废", notes = "作废")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/invalid")
    public AjaxResult invalid(@RequestBody FunFundAccount funFundAccount) {
        if (funFundAccount.getFundAccountSidList() == null || funFundAccount.getFundAccountSidList().length == 0) {
            throw new CheckedException("请选择行");
        }
        if (!HandleStatus.INVALID.getCode().equals(funFundAccount.getHandleStatus())) {
            return toAjax(0);
        }
        return toAjax(funFundAccountService.invalid(funFundAccount));
    }

    /**
     * 查询资金 统计 信息列表
     */
    @PostMapping("/statisticalList")
    @ApiOperation(value = "查询资金统计信息列表", notes = "查询资金统计信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FunFundAccount.class))
    public TableDataInfo statisticalList(@RequestBody FunFundAccount funFundAccount) {
        startPage(funFundAccount);
        List<FunFundAccount> list = funFundAccountService.selectStatisticalFunFundAccountList(funFundAccount);
        return getDataTable(list);
    }

    /**
     * 查询资金 统计 信息明细
     */
    @PostMapping("/statisticalDetail")
    @ApiOperation(value = "查询资金统计信息明细", notes = "查询资金统计信息明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FunFundAccount.class))
    public TableDataInfo statisticalDetail(@RequestBody FunFundAccount funFundAccount) {
        startPage(funFundAccount);
        List<FunFundAccount> list = funFundAccountService.selectStatisticalFunFundAccountDetail(funFundAccount);
        return getDataTable(list);
    }

    /**
     * 导出资金统计信息列表
     */
    @ApiOperation(value = "导出资金统计信息列表", notes = "导出资金统计信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/statisticalListExport")
    public void statisticalListExport(HttpServletResponse response, FunFundAccount funFundAccount) throws IOException {
        List<FunFundAccount> list = funFundAccountService.selectStatisticalFunFundAccountList(funFundAccount);
        FunFundAccountExport funFundAccountExport = new FunFundAccountExport();
        List<FunFundAccountExport> statisticalList = BeanCopyUtils.copyListProperties(list, FunFundAccountExport::new);

        Map<String, Object> dataMap = sysDictDataService.getDictDataList();

        ExcelUtil<FunFundAccountExport> util = new ExcelUtil<>(FunFundAccountExport.class, dataMap);
        util.exportExcel(response, statisticalList, "资金统计报表");
    }

    /**
     * 查询资金账户信息列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "查询资金账户信息列表", notes = "查询资金账户信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FunFundAccount.class))
    public AjaxResult getList(@RequestBody FunFundAccount funFundAccount) {
        return AjaxResult.success(funFundAccountService.getList(funFundAccount));
    }
}
