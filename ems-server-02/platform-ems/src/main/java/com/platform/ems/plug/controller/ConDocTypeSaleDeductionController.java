package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import javax.validation.Valid;
import com.platform.ems.plug.domain.ConDocTypeSaleDeduction;
import com.platform.ems.plug.service.IConDocTypeSaleDeductionService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 单据类型_销售扣款单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/docType/deduction/sale")
@Api(tags = "单据类型_销售扣款单")
public class ConDocTypeSaleDeductionController extends BaseController {

    @Autowired
    private IConDocTypeSaleDeductionService conDocTypeSaleDeductionService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询单据类型_销售扣款单列表
     */
    @PreAuthorize(hasPermi = "ems:docTypeSaleDeduction:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型_销售扣款单列表", notes = "查询单据类型_销售扣款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeSaleDeduction.class))
    public TableDataInfo list(@RequestBody ConDocTypeSaleDeduction conDocTypeSaleDeduction) {
        startPage(conDocTypeSaleDeduction);
        List<ConDocTypeSaleDeduction> list = conDocTypeSaleDeductionService.selectConDocTypeSaleDeductionList(conDocTypeSaleDeduction);
        return getDataTable(list);
    }

    /**
     * 导出单据类型_销售扣款单列表
     */
    @PreAuthorize(hasPermi = "ems:docTypeSaleDeduction:export")
    @Log(title = "单据类型_销售扣款单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出单据类型_销售扣款单列表", notes = "导出单据类型_销售扣款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypeSaleDeduction conDocTypeSaleDeduction) throws IOException {
        List<ConDocTypeSaleDeduction> list = conDocTypeSaleDeductionService.selectConDocTypeSaleDeductionList(conDocTypeSaleDeduction);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypeSaleDeduction> util = new ExcelUtil<ConDocTypeSaleDeduction>(ConDocTypeSaleDeduction.class,dataMap);
        util.exportExcel(response, list, "单据类型_销售扣款单"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入单据类型_销售扣款单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入单据类型_销售扣款单", notes = "导入单据类型_销售扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<ConDocTypeSaleDeduction> util = new ExcelUtil<ConDocTypeSaleDeduction>(ConDocTypeSaleDeduction.class);
        List<ConDocTypeSaleDeduction> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(conDocTypeSaleDeduction ->{
                conDocTypeSaleDeductionService.insertConDocTypeSaleDeduction(conDocTypeSaleDeduction);
                i++;
            });
        }catch (Exception e){
            lose=listSize-i;
            msg=StrUtil.format("前{}条数据导入成功，失败{}条,导入成功的数据请勿重复导入",i,lose);
        }
        if(StrUtil.isEmpty(msg)){
            msg="导入成功";
        }
        return AjaxResult.success(msg);
    }


    @ApiOperation(value = "下载单据类型_销售扣款单导入模板", notes = "下载单据类型_销售扣款单导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConDocTypeSaleDeduction> util = new ExcelUtil<ConDocTypeSaleDeduction>(ConDocTypeSaleDeduction.class);
        util.importTemplateExcel(response, "单据类型_销售扣款单导入模板");
    }


    /**
     * 获取单据类型_销售扣款单详细信息
     */
    @ApiOperation(value = "获取单据类型_销售扣款单详细信息", notes = "获取单据类型_销售扣款单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeSaleDeduction.class))
    @PreAuthorize(hasPermi = "ems:docTypeSaleDeduction:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conDocTypeSaleDeductionService.selectConDocTypeSaleDeductionById(sid));
    }

    /**
     * 新增单据类型_销售扣款单
     */
    @ApiOperation(value = "新增单据类型_销售扣款单", notes = "新增单据类型_销售扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeSaleDeduction:add")
    @Log(title = "单据类型_销售扣款单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypeSaleDeduction conDocTypeSaleDeduction) {
        return toAjax(conDocTypeSaleDeductionService.insertConDocTypeSaleDeduction(conDocTypeSaleDeduction));
    }

    /**
     * 修改单据类型_销售扣款单
     */
    @ApiOperation(value = "修改单据类型_销售扣款单", notes = "修改单据类型_销售扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeSaleDeduction:edit")
    @Log(title = "单据类型_销售扣款单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDocTypeSaleDeduction conDocTypeSaleDeduction) {
        return toAjax(conDocTypeSaleDeductionService.updateConDocTypeSaleDeduction(conDocTypeSaleDeduction));
    }

    /**
     * 变更单据类型_销售扣款单
     */
    @ApiOperation(value = "变更单据类型_销售扣款单", notes = "变更单据类型_销售扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeSaleDeduction:change")
    @Log(title = "单据类型_销售扣款单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody ConDocTypeSaleDeduction conDocTypeSaleDeduction) {
        return toAjax(conDocTypeSaleDeductionService.changeConDocTypeSaleDeduction(conDocTypeSaleDeduction));
    }

    /**
     * 删除单据类型_销售扣款单
     */
    @ApiOperation(value = "删除单据类型_销售扣款单", notes = "删除单据类型_销售扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeSaleDeduction:remove")
    @Log(title = "单据类型_销售扣款单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(ArrayUtil.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypeSaleDeductionService.deleteConDocTypeSaleDeductionByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_销售扣款单", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:docTypeSaleDeduction:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypeSaleDeduction conDocTypeSaleDeduction) {
        return AjaxResult.success(conDocTypeSaleDeductionService.changeStatus(conDocTypeSaleDeduction));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:docTypeSaleDeduction:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_销售扣款单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypeSaleDeduction conDocTypeSaleDeduction) {
        conDocTypeSaleDeduction.setConfirmDate(new Date());
        conDocTypeSaleDeduction.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypeSaleDeduction.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypeSaleDeductionService.check(conDocTypeSaleDeduction));
    }

}
