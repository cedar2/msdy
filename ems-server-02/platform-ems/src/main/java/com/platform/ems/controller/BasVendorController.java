package com.platform.ems.controller;

import java.io.*;
import java.net.URLEncoder;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import cn.hutool.core.bean.BeanUtil;

import com.platform.common.core.domain.R;
import com.platform.common.exception.base.BaseException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.api.service.RemoteFileService;
import com.platform.framework.web.domain.server.SysFile;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.service.IBasVendorService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.multipart.MultipartFile;

/**
 * 供应商档案Controller
 *
 * @author qhq
 * @date 2021-03-12
 */
@RestController
@RequestMapping("/vendor")
@Api(tags = "供应商档案")
public class BasVendorController extends BaseController {

    @Autowired
    private IBasVendorService basVendorService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private RemoteFileService remoteFileService;

    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询供应商档案列表
     */
    @PreAuthorize(hasPermi = "ems:vendor:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商档案列表", notes = "查询供应商档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasVendor.class))
    public TableDataInfo list(@RequestBody BasVendor basVendor) {
        startPage(basVendor);
        List<BasVendor> list = basVendorService.selectBasVendorList(basVendor);
        return getDataTable(list);
    }

    /**
     * 导出供应商档案列表
     */
    @PreAuthorize(hasPermi = "ems:vendor:export")
    @Log(title = "供应商档案", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出供应商档案列表", notes = "导出供应商档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasVendor basVendor) throws IOException {
        List<BasVendor> list = basVendorService.selectBasVendorList(basVendor);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasVendor> util = new ExcelUtil<>(BasVendor.class, dataMap);
        util.exportExcel(response, list, "供应商");
    }

    /**
     * 获取供应商档案详细信息
     */
    @PreAuthorize(hasPermi = "ems:vendor:query")
    @ApiOperation(value = "获取供应商档案详细信息", notes = "获取供应商档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasVendor.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long vendorSid) {
        return AjaxResult.success(basVendorService.selectBasVendorBySid(vendorSid));
    }

    /**
     * 新增供应商档案
     */
    @PreAuthorize(hasPermi = "ems:vendor:add")
    @ApiOperation(value = "新增供应商档案", notes = "新增供应商档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商档案", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasVendor basVendor) {
        int row = basVendorService.insertBasVendor(basVendor);
        return AjaxResult.success(basVendor);
    }

    /**
     * 修改供应商档案
     */
    @PreAuthorize(hasPermi = "ems:vendor:edit")
    @ApiOperation(value = "修改供应商档案", notes = "修改供应商档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商档案", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasVendor basVendor) {
        return toAjax(basVendorService.updateBasVendor(basVendor));
    }

    @PreAuthorize(hasPermi = "ems:vendor:change")
    @ApiOperation(value = "变更供应商档案", notes = "变更供应商档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商档案", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid BasVendor basVendor) {
        return toAjax(basVendorService.updateBasVendor(basVendor));
    }


    /**
     * 删除供应商档案
     */
    @PreAuthorize(hasPermi = "ems:vendor:remove")
    @ApiOperation(value = "删除供应商档案", notes = "删除供应商档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商档案", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> vendorSids) {
        return toAjax(basVendorService.deleteBasVendorByIds(vendorSids));
    }

    /**
     * 查询供应商档案sid、名称及简称，用于下拉框
     */
    @PostMapping("/getVendorList")
    @ApiOperation("查询供应商档案sid、名称及简称，用于下拉框,前端不用传参，默认查询启用和已确认数据")
    public AjaxResult getVendorList() {
        //供应商的账号只能查询到自己的供应商。
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())) {
            if (ApiThreadLocalUtil.get().getSysUser().getVendorSid() != null) {
                BasVendor basVendor = new BasVendor();
                basVendor.setStatus(ConstantsEms.ENABLE_STATUS)
                        .setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setVendorSid(ApiThreadLocalUtil.get().getSysUser().getVendorSid());
                return AjaxResult.success(basVendorService.getVendorList(basVendor));
            }
            return AjaxResult.success();
        } else {
            return AjaxResult.success(basVendorService.getVendorList(new BasVendor()
                    .setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.CHECK_STATUS)));
        }
    }

    /**
     * 查询供应商档案sid、名称及简称，用于下拉框
     */
    @PostMapping("/getList")
    @ApiOperation("查询供应商档案sid、名称及简称，用于下拉框，前端需要按需求传参数过滤数据")
    public AjaxResult getList(@RequestBody BasVendor basVendor) {
        //供应商的账号只能查询到自己的供应商。
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())) {
            if (ApiThreadLocalUtil.get().getSysUser().getVendorSid() != null) {
                basVendor.setVendorSid(ApiThreadLocalUtil.get().getSysUser().getVendorSid());
                return AjaxResult.success(basVendorService.getVendorList(basVendor));
            } else {
                return AjaxResult.success();
            }
        } else {
            return AjaxResult.success(basVendorService.getVendorList(basVendor));
        }
    }


    /**
     * 批量启用停用
     */
    @PreAuthorize(hasPermi = "ems:vendor:enbleordisable")
    @ApiOperation(value = "批量启用停用供应商档案", notes = "批量启用停用供应商档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/status")
    public AjaxResult editStatus(@RequestBody BasVendor basVendor) {
        return toAjax(basVendorService.editStatus(basVendor));
    }

    /**
     * 批量确认
     */
    @PreAuthorize(hasPermi = "ems:vendor:check")
    @ApiOperation(value = "批量确认供应商档案", notes = "批量确认供应商档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/handleStatus")
    public AjaxResult editHandleStatus(@RequestBody BasVendor basVendor) {
        return toAjax(basVendorService.editHandleStatus(basVendor));
    }


    /**
     * 导入供应商档案
     */
    @PreAuthorize(hasPermi = "ems:vendor:import")
    @PostMapping("/import")
    @ApiOperation(value = "导入供应商档案", notes = "导入供应商档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(basVendorService.importData(file));
    }

    /**
     * 上传导入供应商档案模板
     */
    @PreAuthorize(hasPermi = "ems:vendor:uploadTemplate")
    @PostMapping("/uploadTemplate")
    @ApiOperation(value = "上传导入供应商档案模板", notes = "上传导入供应商档案模板")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult uploadTemplate(MultipartFile file) throws Exception {
        R<SysFile> r = remoteFileService.uploadTemplate(file, FILLE_PATH + "/供应商档案导入模板.xlsx");
        if (r.getCode() != R.SUCCESS) {
            return AjaxResult.error("上传失败");
        }
        return AjaxResult.success("上传成功");
    }

    @PreAuthorize(hasPermi = "ems:vendor:import")
    @ApiOperation(value = "下载供应商档案导入模板", notes = "下载供应商档案导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_供应商_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_供应商_V0.1.xlsx", "UTF-8"));
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
     * 设置我方跟单员
     */
    @PreAuthorize(hasPermi = "ems:vendor:setOperator")
    @ApiOperation(value = "设置我方跟单员", notes = "设置我方跟单员")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setOperator")
    public AjaxResult setOperator(@RequestBody BasVendor basVendor) {
        return toAjax(basVendorService.setOperator(basVendor));
    }

    /**
     * 设置供方业务员
     */
    @PreAuthorize(hasPermi = "ems:vendor:setOperatorVendor")
    @ApiOperation(value = "设置供方业务员", notes = "设置供方业务员")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setOperatorVendor")
    public AjaxResult setOperatorVendor(@RequestBody BasVendor basVendor) {
        return toAjax(basVendorService.setOperatorVendor(basVendor));
    }

    /**
     * 设置合作状态
     */
    @ApiOperation(value = "设置合作状态", notes = "设置合作状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setCooperate")
    public AjaxResult setCooperate(@RequestBody BasVendor basVendor) {
        return toAjax(basVendorService.setCooperate(basVendor));
    }

    /**
     * 查询供应商档案联系人列表
     */
    @PostMapping("/addr/list")
    @ApiOperation(value = "查询供应商档案联系人列表", notes = "查询供应商档案联系人列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasVendorAddr.class))
    public TableDataInfo addrList(@RequestBody BasVendor basVendor) {
        BasVendorAddr addr = new BasVendorAddr();
        BeanUtil.copyProperties(basVendor, addr);
        startPage(addr);
        List<BasVendorAddr> list = basVendorService.selectBasVendorAddrList(addr);
        return getDataTable(list);
    }
}
