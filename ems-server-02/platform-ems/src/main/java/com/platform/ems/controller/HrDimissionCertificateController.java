package com.platform.ems.controller;

import cn.hutool.core.util.StrUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.HrDimissionCertificate;
import com.platform.ems.domain.HrDimissionCertificateAttach;
import com.platform.ems.service.IHrDimissionCertificateAttachService;
import com.platform.ems.service.IHrDimissionCertificateService;
import com.platform.system.service.ISysDictDataService;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 离职证明ontroller
 *
 * @author xfzz
 * @date 2024/5/8
 */
@RestController
@RequestMapping("/hr/dimission/certificate/")
@Api(tags = "离职证明")
public class HrDimissionCertificateController extends BaseController {

    @Autowired
    private IHrDimissionCertificateService hrDimissionCertificateService;

    @Autowired
    private IHrDimissionCertificateAttachService hrDimissionCertificateAttachService;

    @Resource
    private ISysDictDataService sysDictDataService;

    /**
     * 查询离职证明列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询离职证明列表", notes = "查询离职证明列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = HrDimissionCertificate.class))
    public TableDataInfo list(@RequestBody HrDimissionCertificate hrDimissionCertificate) {
        startPage(hrDimissionCertificate);
        List<HrDimissionCertificate> list = hrDimissionCertificateService.selectHrDimissionCertificateList(hrDimissionCertificate);
        return getDataTable(list);
    }

    /**
     * 导出离职证明列表
     */
    @ApiOperation(value = "导出离职证明列表", notes = "导出离职证明列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, HrDimissionCertificate hrDimissionCertificate) throws IOException {
        List<HrDimissionCertificate> list = hrDimissionCertificateService.selectHrDimissionCertificateList(hrDimissionCertificate);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<HrDimissionCertificate> util = new ExcelUtil<>(HrDimissionCertificate.class, dataMap);
        util.exportExcel(response, list, "离职证明");
    }


    /**
     * 获取离职证明详细信息
     */
    @ApiOperation(value = "获取离职证明详细信息", notes = "获取离职证明详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = HrDimissionCertificate.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long dimissionCertificateSid) {
        if (dimissionCertificateSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(hrDimissionCertificateService.selectHrDimissionCertificateById(dimissionCertificateSid));
    }

    /**
     * 新增离职证明
     */
    @ApiOperation(value = "新增离职证明", notes = "新增离职证明")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid HrDimissionCertificate hrDimissionCertificate) {
        int row = hrDimissionCertificateService.insertHrDimissionCertificate(hrDimissionCertificate);
        if(row>0){
            return AjaxResult.success(null, new HrDimissionCertificate().setDimissionCertificateSid(hrDimissionCertificate.getDimissionCertificateSid()));
        }
        return toAjax(row);
    }


    /**
     * 变更离职证明
     */
    @ApiOperation(value = "变更离职证明", notes = "变更离职证明")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid HrDimissionCertificate hrDimissionCertificate) {
        return toAjax(hrDimissionCertificateService.changeHrDimissionCertificate(hrDimissionCertificate));
    }

    /**
     * 删除离职证明
     */
    @ApiOperation(value = "删除离职证明", notes = "删除离职证明")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> dimissionCertificateSids) {
        if (CollectionUtils.isEmpty(dimissionCertificateSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(hrDimissionCertificateService.deleteHrDimissionCertificateByIds(dimissionCertificateSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody HrDimissionCertificate hrDimissionCertificate) {
        return toAjax(hrDimissionCertificateService.check(hrDimissionCertificate));
    }

    /**
     * 签收
     */
    @ApiOperation(value = "签收", notes = "签收")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/sign")
    public AjaxResult sign(@RequestBody HrDimissionCertificate hrDimissionCertificate) {
        if (CollectionUtils.isEmpty(hrDimissionCertificate.getDimissionCertificateSids()) || StrUtil.isBlank(hrDimissionCertificate.getSignInStatus())) {
            throw new BaseException("参数缺失");
        }
        return toAjax(hrDimissionCertificateService.signHrDimissionCertificateById(hrDimissionCertificate));
    }

    /**
     * 查询页面 发起签署前的校验
     */
    @ApiOperation(value = "查询页面-发起签署前的校验", notes = "查询页面-发起签署前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkAttach")
    public AjaxResult checkAttach(@RequestBody HrDimissionCertificateAttach hrDimissionCertificateAttach) {
        return hrDimissionCertificateAttachService.check(hrDimissionCertificateAttach);
    }

}
