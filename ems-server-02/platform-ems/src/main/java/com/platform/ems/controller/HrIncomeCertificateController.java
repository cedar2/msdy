package com.platform.ems.controller;

import cn.hutool.core.util.StrUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.domain.HrIncomeCertificate;
import com.platform.ems.domain.HrIncomeCertificateAttach;
import com.platform.ems.service.IHrIncomeCertificateAttachService;
import com.platform.ems.service.IHrIncomeCertificateService;
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
 * 收入证明ontroller
 *
 * @author xfzz
 * @date 2024/5/8
 */
@RestController
@RequestMapping("/hr/income/certificate/")
@Api(tags = "收入证明")
public class HrIncomeCertificateController extends BaseController {

    @Autowired
    private IHrIncomeCertificateService hrIncomeCertificateService;

    @Autowired
    private IHrIncomeCertificateAttachService hrIncomeCertificateAttachService;

    @Autowired
    private ISysDictDataService sysDictDataService;

    /**
     * 查询收入证明列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询收入证明列表", notes = "查询收入证明列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = HrIncomeCertificate.class))
    public TableDataInfo list(@RequestBody HrIncomeCertificate hrIncomeCertificate) {
        startPage(hrIncomeCertificate);
        List<HrIncomeCertificate> list = hrIncomeCertificateService.selectHrIncomeCertificateList(hrIncomeCertificate);
        return getDataTable(list);
    }

    /**
     * 导出收入证明列表
     */
    @ApiOperation(value = "导出收入证明列表", notes = "导出收入证明列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, HrIncomeCertificate hrIncomeCertificate) throws IOException {
        List<HrIncomeCertificate> list = hrIncomeCertificateService.selectHrIncomeCertificateList(hrIncomeCertificate);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<HrIncomeCertificate> util = new ExcelUtil<>(HrIncomeCertificate.class, dataMap);
        util.exportExcel(response, list, "收入证明");
    }


    /**
     * 获取收入证明详细信息
     */
    @ApiOperation(value = "获取收入证明详细信息", notes = "获取收入证明详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = HrIncomeCertificate.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long incomeCertificateSid) {
        if (incomeCertificateSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(hrIncomeCertificateService.selectHrIncomeCertificateById(incomeCertificateSid));
    }

    /**
     * 新增收入证明
     */
    @ApiOperation(value = "新增收入证明", notes = "新增收入证明")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid HrIncomeCertificate hrIncomeCertificate) {
        int row = hrIncomeCertificateService.insertHrIncomeCertificate(hrIncomeCertificate);
        if(row>0){
            return AjaxResult.success(null, new HrIncomeCertificate().setIncomeCertificateSid(hrIncomeCertificate.getIncomeCertificateSid()));
        }
        return toAjax(row);
    }


    /**
     * 变更收入证明
     */
    @ApiOperation(value = "变更收入证明", notes = "变更收入证明")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid HrIncomeCertificate hrIncomeCertificate) {
        return toAjax(hrIncomeCertificateService.changeHrIncomeCertificate(hrIncomeCertificate));
    }

    /**
     * 删除收入证明
     */
    @ApiOperation(value = "删除收入证明", notes = "删除收入证明")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> incomeCertificateSids) {
        if (CollectionUtils.isEmpty(incomeCertificateSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(hrIncomeCertificateService.deleteHrIncomeCertificateByIds(incomeCertificateSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody HrIncomeCertificate hrIncomeCertificate) {
        return toAjax(hrIncomeCertificateService.check(hrIncomeCertificate));
    }

    /**
     * 签收
     */
    @ApiOperation(value = "签收", notes = "签收")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/sign")
    public AjaxResult sign(@RequestBody HrIncomeCertificate hrIncomeCertificate) {
        if (CollectionUtils.isEmpty(hrIncomeCertificate.getIncomeCertificateSids()) || StrUtil.isBlank(hrIncomeCertificate.getSignInStatus())) {
            throw new BaseException("参数缺失");
        }
        return toAjax(hrIncomeCertificateService.signHrIncomeCertificateById(hrIncomeCertificate));
    }

    /**
     * 查询页面 发起签署前的校验
     */
    @ApiOperation(value = "查询页面-发起签署前的校验", notes = "查询页面-发起签署前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkAttach")
    public AjaxResult checkAttach(@RequestBody HrIncomeCertificateAttach hrIncomeCertificateAttach) {
        return hrIncomeCertificateAttachService.check(hrIncomeCertificateAttach);
    }

}
