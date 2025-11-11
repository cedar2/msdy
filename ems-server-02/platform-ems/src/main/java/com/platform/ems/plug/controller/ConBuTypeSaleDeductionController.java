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
import com.platform.ems.plug.domain.ConBuTypeSaleDeduction;
import com.platform.ems.plug.service.IConBuTypeSaleDeductionService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 业务类型_销售扣款单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/buType/sale/deduction")
@Api(tags = "业务类型_销售扣款单")
public class ConBuTypeSaleDeductionController extends BaseController {

    @Autowired
    private IConBuTypeSaleDeductionService conBuTypeSaleDeductionService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询业务类型_销售扣款单列表
     */
    @PreAuthorize(hasPermi = "ems:buTypeSaleDeduction:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询业务类型_销售扣款单列表", notes = "查询业务类型_销售扣款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeSaleDeduction.class))
    public TableDataInfo list(@RequestBody ConBuTypeSaleDeduction conBuTypeSaleDeduction) {
        startPage(conBuTypeSaleDeduction);
        List<ConBuTypeSaleDeduction> list = conBuTypeSaleDeductionService.selectConBuTypeSaleDeductionList(conBuTypeSaleDeduction);
        return getDataTable(list);
    }

    /**
     * 导出业务类型_销售扣款单列表
     */
    @PreAuthorize(hasPermi = "ems:buTypeSaleDeduction:export")
    @Log(title = "业务类型_销售扣款单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出业务类型_销售扣款单列表", notes = "导出业务类型_销售扣款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBuTypeSaleDeduction conBuTypeSaleDeduction) throws IOException {
        List<ConBuTypeSaleDeduction> list = conBuTypeSaleDeductionService.selectConBuTypeSaleDeductionList(conBuTypeSaleDeduction);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConBuTypeSaleDeduction> util = new ExcelUtil<ConBuTypeSaleDeduction>(ConBuTypeSaleDeduction.class,dataMap);
        util.exportExcel(response, list, "业务类型_销售扣款单"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入业务类型_销售扣款单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入业务类型_销售扣款单", notes = "导入业务类型_销售扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<ConBuTypeSaleDeduction> util = new ExcelUtil<ConBuTypeSaleDeduction>(ConBuTypeSaleDeduction.class);
        List<ConBuTypeSaleDeduction> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(conBuTypeSaleDeduction ->{
                conBuTypeSaleDeductionService.insertConBuTypeSaleDeduction(conBuTypeSaleDeduction);
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


    @ApiOperation(value = "下载业务类型_销售扣款单导入模板", notes = "下载业务类型_销售扣款单导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConBuTypeSaleDeduction> util = new ExcelUtil<ConBuTypeSaleDeduction>(ConBuTypeSaleDeduction.class);
        util.importTemplateExcel(response, "业务类型_销售扣款单导入模板");
    }


    /**
     * 获取业务类型_销售扣款单详细信息
     */
    @ApiOperation(value = "获取业务类型_销售扣款单详细信息", notes = "获取业务类型_销售扣款单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeSaleDeduction.class))
    @PreAuthorize(hasPermi = "ems:buTypeSaleDeduction:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conBuTypeSaleDeductionService.selectConBuTypeSaleDeductionById(sid));
    }

    /**
     * 新增业务类型_销售扣款单
     */
    @ApiOperation(value = "新增业务类型_销售扣款单", notes = "新增业务类型_销售扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:buTypeSaleDeduction:add")
    @Log(title = "业务类型_销售扣款单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBuTypeSaleDeduction conBuTypeSaleDeduction) {
        return toAjax(conBuTypeSaleDeductionService.insertConBuTypeSaleDeduction(conBuTypeSaleDeduction));
    }

    /**
     * 修改业务类型_销售扣款单
     */
    @ApiOperation(value = "修改业务类型_销售扣款单", notes = "修改业务类型_销售扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:buTypeSaleDeduction:edit")
    @Log(title = "业务类型_销售扣款单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConBuTypeSaleDeduction conBuTypeSaleDeduction) {
        return toAjax(conBuTypeSaleDeductionService.updateConBuTypeSaleDeduction(conBuTypeSaleDeduction));
    }

    /**
     * 变更业务类型_销售扣款单
     */
    @ApiOperation(value = "变更业务类型_销售扣款单", notes = "变更业务类型_销售扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:buTypeSaleDeduction:change")
    @Log(title = "业务类型_销售扣款单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBuTypeSaleDeduction conBuTypeSaleDeduction) {
        return toAjax(conBuTypeSaleDeductionService.changeConBuTypeSaleDeduction(conBuTypeSaleDeduction));
    }

    /**
     * 删除业务类型_销售扣款单
     */
    @ApiOperation(value = "删除业务类型_销售扣款单", notes = "删除业务类型_销售扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:buTypeSaleDeduction:remove")
    @Log(title = "业务类型_销售扣款单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(ArrayUtil.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuTypeSaleDeductionService.deleteConBuTypeSaleDeductionByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_销售扣款单", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:buTypeSaleDeduction:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBuTypeSaleDeduction conBuTypeSaleDeduction) {
        return AjaxResult.success(conBuTypeSaleDeductionService.changeStatus(conBuTypeSaleDeduction));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:buTypeSaleDeduction:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_销售扣款单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBuTypeSaleDeduction conBuTypeSaleDeduction) {
        conBuTypeSaleDeduction.setConfirmDate(new Date());
        conBuTypeSaleDeduction.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBuTypeSaleDeduction.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBuTypeSaleDeductionService.check(conBuTypeSaleDeduction));
    }

}
