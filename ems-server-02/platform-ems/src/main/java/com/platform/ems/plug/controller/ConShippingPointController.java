package com.platform.ems.plug.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConShippingPoint;
import com.platform.ems.plug.service.IConShippingPointService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 装运点Controller
 *
 * @author linhongwei
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/shipping/point")
@Api(tags = "装运点")
public class ConShippingPointController extends BaseController {

    @Autowired
    private IConShippingPointService conShippingPointService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询装运点列表
     */
    @PreAuthorize(hasPermi = "ems:point:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询装运点列表", notes = "查询装运点列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConShippingPoint.class))
    public TableDataInfo list(@RequestBody ConShippingPoint conShippingPoint) {
        startPage(conShippingPoint);
        List<ConShippingPoint> list = conShippingPointService.selectConShippingPointList(conShippingPoint);
        return getDataTable(list);
    }

    /**
     * 导出装运点列表
     */
    @PreAuthorize(hasPermi = "ems:point:export")
    @Log(title = "装运点", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出装运点列表", notes = "导出装运点列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConShippingPoint conShippingPoint) throws IOException {
        List<ConShippingPoint> list = conShippingPointService.selectConShippingPointList(conShippingPoint);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConShippingPoint> util = new ExcelUtil<ConShippingPoint>(ConShippingPoint.class, dataMap);
        util.exportExcel(response, list, "装运点" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入装运点
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入装运点", notes = "导入装运点")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConShippingPoint> util = new ExcelUtil<ConShippingPoint>(ConShippingPoint.class);
        List<ConShippingPoint> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conShippingPoint -> {
                conShippingPointService.insertConShippingPoint(conShippingPoint);
                i++;
            });
        } catch (Exception e) {
            lose = listSize - i;
            msg = StrUtil.format("前{}条数据导入成功，失败{}条,导入成功的数据请勿重复导入", i, lose);
        }
        if (StrUtil.isEmpty(msg)) {
            msg = "导入成功";
        }
        return AjaxResult.success(msg);
    }


    @ApiOperation(value = "下载装运点导入模板", notes = "下载装运点导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConShippingPoint> util = new ExcelUtil<ConShippingPoint>(ConShippingPoint.class);
        util.importTemplateExcel(response, "装运点导入模板");
    }


    /**
     * 获取装运点详细信息
     */
    @ApiOperation(value = "获取装运点详细信息", notes = "获取装运点详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConShippingPoint.class))
    @PreAuthorize(hasPermi = "ems:point:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conShippingPointService.selectConShippingPointById(sid));
    }

    /**
     * 新增装运点
     */
    @ApiOperation(value = "新增装运点", notes = "新增装运点")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:point:add")
    @Log(title = "装运点", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConShippingPoint conShippingPoint) {
        return toAjax(conShippingPointService.insertConShippingPoint(conShippingPoint));
    }

    /**
     * 修改装运点
     */
    @ApiOperation(value = "修改装运点", notes = "修改装运点")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:point:edit")
    @Log(title = "装运点", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConShippingPoint conShippingPoint) {
        return toAjax(conShippingPointService.updateConShippingPoint(conShippingPoint));
    }

    /**
     * 变更装运点
     */
    @ApiOperation(value = "变更装运点", notes = "变更装运点")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:point:change")
    @Log(title = "装运点", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConShippingPoint conShippingPoint) {
        return toAjax(conShippingPointService.changeConShippingPoint(conShippingPoint));
    }

    /**
     * 删除装运点
     */
    @ApiOperation(value = "删除装运点", notes = "删除装运点")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:point:remove")
    @Log(title = "装运点", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conShippingPointService.deleteConShippingPointByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "装运点", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:point:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConShippingPoint conShippingPoint) {
        return AjaxResult.success(conShippingPointService.changeStatus(conShippingPoint));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:point:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "装运点", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConShippingPoint conShippingPoint) {
        conShippingPoint.setConfirmDate(new Date());
        conShippingPoint.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conShippingPoint.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conShippingPointService.check(conShippingPoint));
    }

}
