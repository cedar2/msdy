package com.platform.ems.controller;

import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.HrOtherPersonnelCertificate;
import com.platform.ems.domain.HrOtherPersonnelCertificateAttach;
import com.platform.ems.service.IHrOtherPersonnelCertificateAttachService;
import com.platform.ems.service.IHrOtherPersonnelCertificateService;
import com.platform.system.service.ISysDictDataService;
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
import java.util.List;
import java.util.Map;

/**
 * 其它人事证明controller
 *
 * @author xfzz
 * @date 2024/5/8
 */
@RestController
@RequestMapping("/hr/other/personnel/certificate/")
@Api(tags = "其它人事证明")
public class HrOtherPersonnelCertificateController extends BaseController {

    @Autowired
    private IHrOtherPersonnelCertificateService hrOtherPersonnelCertificateService;

    @Autowired
    private IHrOtherPersonnelCertificateAttachService hrOtherPersonnelCertificateAttachService;

    @Autowired
    private ISysDictDataService sysDictDataService;

    /**
     * 查询其它人事证明列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询其它人事证明列表", notes = "查询其它人事证明列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = HrOtherPersonnelCertificate.class))
    public TableDataInfo list(@RequestBody HrOtherPersonnelCertificate hrOtherPersonnelCertificate) {
        startPage(hrOtherPersonnelCertificate);
        List<HrOtherPersonnelCertificate> list = hrOtherPersonnelCertificateService.selectHrOtherPersonnelCertificateList(hrOtherPersonnelCertificate);
        return getDataTable(list);
    }

    /**
     * 导出其它人事证明列表
     */
    @Log(title = "其它人事证明", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出其它人事证明列表", notes = "导出其它人事证明列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, HrOtherPersonnelCertificate hrOtherPersonnelCertificate) throws IOException {
        List<HrOtherPersonnelCertificate> list = hrOtherPersonnelCertificateService.selectHrOtherPersonnelCertificateList(hrOtherPersonnelCertificate);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<HrOtherPersonnelCertificate> util = new ExcelUtil<>(HrOtherPersonnelCertificate.class, dataMap);
        util.exportExcel(response, list, "其它人事证明");
    }


    /**
     * 获取其它人事证明详细信息
     */
    @ApiOperation(value = "获取其它人事证明详细信息", notes = "获取其它人事证明详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = HrOtherPersonnelCertificate.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long otherPersonnelCertificateSid) {
        if (otherPersonnelCertificateSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(hrOtherPersonnelCertificateService.selectHrOtherPersonnelCertificateById(otherPersonnelCertificateSid));
    }

    /**
     * 新增其它人事证明
     */
    @ApiOperation(value = "新增其它人事证明", notes = "新增其它人事证明")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "其它人事证明", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid HrOtherPersonnelCertificate hrOtherPersonnelCertificate) {
        int row = hrOtherPersonnelCertificateService.insertHrOtherPersonnelCertificate(hrOtherPersonnelCertificate);
        if(row>0){
            return AjaxResult.success(null, new HrOtherPersonnelCertificate().setOtherPersonnelCertificateSid(hrOtherPersonnelCertificate.getOtherPersonnelCertificateSid()));
        }
        return toAjax(row);
    }


    /**
     * 变更其它人事证明
     */
    @ApiOperation(value = "变更其它人事证明", notes = "变更其它人事证明")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "其它人事证明", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid HrOtherPersonnelCertificate hrOtherPersonnelCertificate) {
        return toAjax(hrOtherPersonnelCertificateService.changeHrOtherPersonnelCertificate(hrOtherPersonnelCertificate));
    }

    /**
     * 删除其它人事证明
     */
    @ApiOperation(value = "删除其它人事证明", notes = "删除其它人事证明")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "其它人事证明", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> incomeCertificateSids) {
        if (CollectionUtils.isEmpty(incomeCertificateSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(hrOtherPersonnelCertificateService.deleteHrOtherPersonnelCertificateByIds(incomeCertificateSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "其它人事证明", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody HrOtherPersonnelCertificate hrOtherPersonnelCertificate) {
        return toAjax(hrOtherPersonnelCertificateService.check(hrOtherPersonnelCertificate));
    }

    /**
     * 签收
     */
    @ApiOperation(value = "签收", notes = "签收")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "其它人事证明信息", businessType = BusinessType.HANDLE)
    @PostMapping("/sign")
    public AjaxResult sign(@RequestBody HrOtherPersonnelCertificate hrOtherPersonnelCertificate) {
        if (CollectionUtils.isEmpty(hrOtherPersonnelCertificate.getOtherPersonnelCertificateSids()) || StrUtil.isBlank(hrOtherPersonnelCertificate.getSignInStatus())) {
            throw new BaseException("参数缺失");
        }
        return toAjax(hrOtherPersonnelCertificateService.signHrOtherPersonnelCertificateById(hrOtherPersonnelCertificate));
    }

    /**
     * 查询页面 发起签署前的校验
     */
    @ApiOperation(value = "查询页面-发起签署前的校验", notes = "查询页面-发起签署前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkAttach")
    public AjaxResult checkAttach(@RequestBody HrOtherPersonnelCertificateAttach hrOtherPersonnelCertificateAttach) {
        return hrOtherPersonnelCertificateAttachService.check(hrOtherPersonnelCertificateAttach);
    }

}
