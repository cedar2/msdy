package com.platform.ems.controller;

import cn.hutool.core.util.StrUtil;
import com.platform.common.annotation.Log;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.HrLaborContract;
import com.platform.ems.domain.HrLaborContractAttach;
import com.platform.ems.service.IHrLaborContractAttachService;
import com.platform.ems.service.IHrLaborContractService;
import com.platform.system.service.ISysDictDataService;
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
import java.util.List;
import java.util.Map;

/**
 * 劳动合同Controller
 *
 * @author xfzz
 * @date 2024/5/8
 */
@RestController
@RequestMapping("/hr/labor/contract/")
@Api(tags = "劳动合同")
public class HrLaborContractController extends BaseController {

    @Autowired
    private IHrLaborContractService hrLaborContractService;

    @Autowired
    private IHrLaborContractAttachService hrLaborContractAttachService;

    @Autowired
    private ISysDictDataService sysDictDataService;

    /**
     * 查询劳动合同列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询劳动合同列表", notes = "查询劳动合同列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = HrLaborContract.class))
    public TableDataInfo list(@RequestBody HrLaborContract hrLaborContract) {
        startPage(hrLaborContract);
        List<HrLaborContract> list = hrLaborContractService.selectHrLaborContractList(hrLaborContract);
        return getDataTable(list);
    }

    /**
     * 导出劳动合同列表
     */
    @ApiOperation(value = "导出劳动合同列表", notes = "导出劳动合同列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, HrLaborContract hrLaborContract) throws IOException {
        List<HrLaborContract> list = hrLaborContractService.selectHrLaborContractList(hrLaborContract);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<HrLaborContract> util = new ExcelUtil<>(HrLaborContract.class, dataMap);
        util.exportExcel(response, list, "劳动合同");
    }


    /**
     * 获取劳动合同详细信息
     */
    @ApiOperation(value = "获取劳动合同详细信息", notes = "获取劳动合同详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = HrLaborContract.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long laborContractSid) {
        if (laborContractSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(hrLaborContractService.selectHrLaborContractById(laborContractSid));
    }

    /**
     * 新增劳动合同
     */
    @ApiOperation(value = "新增劳动合同", notes = "新增劳动合同")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid HrLaborContract hrLaborContract) {
        int row = hrLaborContractService.insertHrLaborContract(hrLaborContract);
        if(row>0){
            return AjaxResult.success(null, new HrLaborContract().setLaborContractSid(hrLaborContract.getLaborContractSid()));
        }
        return toAjax(row);
    }


    /**
     * 变更劳动合同
     */
    @ApiOperation(value = "变更劳动合同", notes = "变更劳动合同")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid HrLaborContract hrLaborContract) {
        return toAjax(hrLaborContractService.changeHrLaborContract(hrLaborContract));
    }


    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody HrLaborContract hrLaborContract) {
        return toAjax(hrLaborContractService.check(hrLaborContract));
    }

    /**
     * 签收
     */
    @ApiOperation(value = "签收", notes = "签收")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/sign")
    public AjaxResult sign(@RequestBody HrLaborContract hrLaborContract) {
        if (CollectionUtils.isEmpty(hrLaborContract.getLaborContractSids()) || StrUtil.isBlank(hrLaborContract.getSignInStatus())) {
            throw new BaseException("参数缺失");
        }
        return toAjax(hrLaborContractService.signHrLaborContractById(hrLaborContract));
    }

    /**
     * 设置履约状态
     */
    @ApiOperation(value = "设置履约状态", notes = "设置履约状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "劳动合同信息", businessType = BusinessType.HANDLE)
    @PostMapping("/setLvyueStatus")
    public AjaxResult setLvyueStatus(@RequestBody HrLaborContract hrLaborContract) {
        if (CollectionUtils.isEmpty(hrLaborContract.getLaborContractSids()) || StrUtil.isBlank(hrLaborContract.getLvyueStatus())) {
            throw new BaseException("参数缺失");
        }
        return toAjax(hrLaborContractService.setLvyueStatusById(hrLaborContract));
    }

    /**
     * 终止履约状态
     */
    @ApiOperation(value = "终止履约状态", notes = "终止履约状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "劳动合同信息", businessType = BusinessType.HANDLE)
    @PostMapping("/endLvyueStatus")
    public AjaxResult endLvyueStatus(@RequestBody HrLaborContract hrLaborContract) {
        if (CollectionUtils.isEmpty(hrLaborContract.getLaborContractSids())) {
            throw new BaseException("参数缺失");
        }
        return toAjax(hrLaborContractService.endLvyueStatusById(hrLaborContract));
    }

    /**
     * 查询页面 发起签署前的校验
     */
    @ApiOperation(value = "查询页面-发起签署前的校验", notes = "查询页面-发起签署前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkAttach")
    public AjaxResult checkAttach(@RequestBody HrLaborContractAttach hrLaborContractAttach) {
        return hrLaborContractAttachService.check(hrLaborContractAttach);
    }

    /**
     * 导入
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入", notes = "导入")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importCategory(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(hrLaborContractService.importRecord(file));
    }

    @Autowired
    private MinioClient client;
    @Autowired
    private MinioConfig minioConfig;

    private static final String FILLE_PATH = "/template";

    /**
     * 下载导入模板
     */
    @ApiOperation(value = "下载导入模板", notes = "下载导入模板")
    @PostMapping("/import/template")
    public void importKaifTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String fileName = FILLE_PATH + "/" + "SCM_导入模板_劳动合同_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            InputStream inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" +
                    URLEncoder.encode("SCM_导入模板_劳动合同_V1.0.xlsx", "UTF-8"));
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
