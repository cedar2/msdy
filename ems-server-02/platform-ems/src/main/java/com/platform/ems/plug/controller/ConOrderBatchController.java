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
import com.platform.ems.plug.domain.ConOrderBatch;
import com.platform.ems.plug.service.IConOrderBatchService;
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
 * 下单批次Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/order/batch")
@Api(tags = "下单批次")
public class ConOrderBatchController extends BaseController {

    @Autowired
    private IConOrderBatchService conOrderBatchService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询下单批次列表
     */
    @PreAuthorize(hasPermi = "ems:batch:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询下单批次列表", notes = "查询下单批次列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConOrderBatch.class))
    public TableDataInfo list(@RequestBody ConOrderBatch conOrderBatch) {
        startPage(conOrderBatch);
        List<ConOrderBatch> list = conOrderBatchService.selectConOrderBatchList(conOrderBatch);
        return getDataTable(list);
    }

    /**
     * 导出下单批次列表
     */
    @PreAuthorize(hasPermi = "ems:batch:export")
    @Log(title = "下单批次", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出下单批次列表", notes = "导出下单批次列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConOrderBatch conOrderBatch) throws IOException {
        List<ConOrderBatch> list = conOrderBatchService.selectConOrderBatchList(conOrderBatch);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConOrderBatch> util = new ExcelUtil<ConOrderBatch>(ConOrderBatch.class, dataMap);
        util.exportExcel(response, list, "下单批次" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入下单批次
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入下单批次", notes = "导入下单批次")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConOrderBatch> util = new ExcelUtil<ConOrderBatch>(ConOrderBatch.class);
        List<ConOrderBatch> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conOrderBatch -> {
                conOrderBatchService.insertConOrderBatch(conOrderBatch);
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


    @ApiOperation(value = "下载下单批次导入模板", notes = "下载下单批次导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConOrderBatch> util = new ExcelUtil<ConOrderBatch>(ConOrderBatch.class);
        util.importTemplateExcel(response, "下单批次导入模板");
    }


    /**
     * 获取下单批次详细信息
     */
    @ApiOperation(value = "获取下单批次详细信息", notes = "获取下单批次详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConOrderBatch.class))
    @PreAuthorize(hasPermi = "ems:batch:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conOrderBatchService.selectConOrderBatchById(sid));
    }

    /**
     * 新增下单批次
     */
    @ApiOperation(value = "新增下单批次", notes = "新增下单批次")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:batch:add")
    @Log(title = "下单批次", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConOrderBatch conOrderBatch) {
        return toAjax(conOrderBatchService.insertConOrderBatch(conOrderBatch));
    }

    /**
     * 修改下单批次
     */
    @ApiOperation(value = "修改下单批次", notes = "修改下单批次")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:batch:edit")
    @Log(title = "下单批次", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConOrderBatch conOrderBatch) {
        return toAjax(conOrderBatchService.updateConOrderBatch(conOrderBatch));
    }

    /**
     * 变更下单批次
     */
    @ApiOperation(value = "变更下单批次", notes = "变更下单批次")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:batch:change")
    @Log(title = "下单批次", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConOrderBatch conOrderBatch) {
        return toAjax(conOrderBatchService.changeConOrderBatch(conOrderBatch));
    }

    /**
     * 删除下单批次
     */
    @ApiOperation(value = "删除下单批次", notes = "删除下单批次")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:batch:remove")
    @Log(title = "下单批次", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conOrderBatchService.deleteConOrderBatchByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "下单批次", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:batch:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConOrderBatch conOrderBatch) {
        return AjaxResult.success(conOrderBatchService.changeStatus(conOrderBatch));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:batch:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "下单批次", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConOrderBatch conOrderBatch) {
        conOrderBatch.setConfirmDate(new Date());
        conOrderBatch.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conOrderBatch.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conOrderBatchService.check(conOrderBatch));
    }

    /**
     * 下单批次下拉框
     */
    @PostMapping("/getList")
    @ApiOperation(value = "下单批次下拉框", notes = "下单批次下拉框")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConOrderBatch.class))
    public AjaxResult getList() {
        return AjaxResult.success(conOrderBatchService.getList());
    }
}
