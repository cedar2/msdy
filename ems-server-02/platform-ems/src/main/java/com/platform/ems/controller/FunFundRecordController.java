package com.platform.ems.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.exception.base.BaseException;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.FunFundAccount;
import com.platform.system.service.ISysDictDataService;
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
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.FunFundRecord;
import com.platform.ems.service.IFunFundRecordService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 资金流水Controller
 *
 * @author chenkw
 * @date 2022-03-01
 */
@RestController
@RequestMapping("/fun/fund/record")
@Api(tags = "资金流水")
public class FunFundRecordController extends BaseController {

    @Autowired
    private IFunFundRecordService funFundRecordService;
    @Autowired
    private ISysDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询资金流水列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询资金流水列表", notes = "查询资金流水列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FunFundRecord.class))
    public TableDataInfo list(@RequestBody FunFundRecord funFundRecord) {
        startPage(funFundRecord);
        List<FunFundRecord> list = funFundRecordService.selectFunFundRecordList(funFundRecord);
        return getDataTable(list);
    }

    /**
     * 导出资金流水列表
     */
    @ApiOperation(value = "导出资金流水列表", notes = "导出资金流水列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FunFundRecord funFundRecord) throws IOException {
        List<FunFundRecord> list = funFundRecordService.selectFunFundRecordList(funFundRecord);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FunFundRecord> util = new ExcelUtil<>(FunFundRecord.class, dataMap);
        util.exportExcel(response, list, "资金流水");
    }


    /**
     * 获取资金流水详细信息
     */
    @ApiOperation(value = "获取资金流水详细信息", notes = "获取资金流水详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FunFundRecord.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long fundRecordSid) {
        if (fundRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(funFundRecordService.selectFunFundRecordById(fundRecordSid));
    }

    /**
     * 新增资金流水
     */
    @ApiOperation(value = "新增资金流水", notes = "新增资金流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FunFundRecord funFundRecord) {
        int row = funFundRecordService.insertFunFundRecord(funFundRecord);
        if(row>0){
            return AjaxResult.success("操作成功",new FunFundRecord()
                    .setFundRecordSid(funFundRecord.getFundRecordSid()));
        }
        return toAjax(row);
    }

    /**
     * 修改资金流水
     */
    @ApiOperation(value = "修改资金流水", notes = "修改资金流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FunFundRecord funFundRecord) {
        return toAjax(funFundRecordService.updateFunFundRecord(funFundRecord));
    }


    /**
     * 更改资金账户
     */
    @ApiOperation(value = "更改资金账户", notes = "更改资金账户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setAccountName")
    public AjaxResult setAccountName(@RequestBody FunFundRecord funFundRecord) {
        return toAjax(funFundRecordService.setAccountNameById(funFundRecord));
    }

    /**
     * 更改所用汇票
     */
    @ApiOperation(value = "更改所用汇票", notes = "更改所用汇票")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setHuipiaoCode")
    public AjaxResult setHuipiaoCode(@RequestBody FunFundRecord funFundRecord) {
        return toAjax(funFundRecordService.setHuipiaoCodeById(funFundRecord));
    }

    /**
     * 设置其它信息
     */
    @ApiOperation(value = "设置其它信息", notes = "设置其它信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setDateStatus")
    public AjaxResult setDateStatus(@RequestBody FunFundRecord funFundRecord) {
        return toAjax(funFundRecordService.setDateStatus(funFundRecord));
    }

    /**
     * 变更资金流水
     */
    @ApiOperation(value = "变更资金流水", notes = "变更资金流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FunFundRecord funFundRecord) {
        return toAjax(funFundRecordService.changeFunFundRecord(funFundRecord));
    }

    /**
     * 删除资金流水
     */
    @ApiOperation(value = "删除资金流水", notes = "删除资金流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> fundRecordSids) {
        if (CollectionUtils.isEmpty(fundRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(funFundRecordService.deleteFunFundRecordByIds(fundRecordSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FunFundRecord funFundRecord) {
        return toAjax(funFundRecordService.check(funFundRecord));
    }

    /**
     * 导入资金流水
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入资金流水", notes = "导入资金流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        Object response = funFundRecordService.importData(file);
        if (response instanceof Collection){
            return AjaxResult.error("导入错误", response);
        }
        else {
            return AjaxResult.success(response);
        }
    }

    @ApiOperation(value = "下载资金流水导入模板", notes = "下载资金流水导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_资金流水_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_资金流水_V0.1.xlsx", "UTF-8"));
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

}
