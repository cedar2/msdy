package com.platform.ems.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.domain.TecProductZipper;
import com.platform.ems.domain.dto.request.TecProductZipperAddListRequest;
import com.platform.ems.domain.dto.request.TecProductZipperRequest;
import com.platform.ems.domain.dto.response.TecProductZipperInfoResponse;
import com.platform.ems.service.ITecProductZipperService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 商品所用拉链Controller
 *
 * @author c
 * @date 2021-08-03
 */
@RestController
@RequestMapping("/tec/product/zipper")
@Api(tags = "商品所用拉链")
public class TecProductZipperController extends BaseController {

    @Autowired
    private ITecProductZipperService tecProductZipperService;



    /**
     * 获取商品所用拉链详细信息
     */
    @ApiOperation(value = "获取商品所用拉链详细信息", notes = "获取商品所用拉链详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecProductZipperInfoResponse.class))
    @PreAuthorize(hasPermi = "ems::tec:product:zipper:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(@RequestBody TecProductZipperRequest request) {
        if (request == null) {
            throw new CheckedException("参数缺失");
        }
        TecProductZipper tecProduct = new TecProductZipper();
        BeanCopyUtils.copyProperties(request,tecProduct);
        TecProductZipper tecProductZipper = tecProductZipperService.selectTecProductZipperById(tecProduct.getBomMaterialCode(), tecProduct.getMaterialSids());
        TecProductZipperInfoResponse resonse = new TecProductZipperInfoResponse();
        BeanCopyUtils.copyProperties(tecProductZipper,resonse);
        return AjaxResult.success(resonse);
    }

    /**
     * 新增商品所用拉链
     */
    @ApiOperation(value = "新增商品所用拉链", notes = "新增商品所用拉链")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems::tec:product:zipper:add")
    @Log(title = "商品所用拉链", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid TecProductZipperAddListRequest request) {
        TecProductZipper tecProduct = new TecProductZipper();
        BeanCopyUtils.copyProperties(request,tecProduct);
        return toAjax(tecProductZipperService.insertTecProductZipper(tecProduct));
    }

}
