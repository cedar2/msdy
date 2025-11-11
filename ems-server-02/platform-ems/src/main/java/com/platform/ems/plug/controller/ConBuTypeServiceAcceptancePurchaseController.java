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
import com.platform.ems.plug.domain.ConBuTypeServiceAcceptancePurchase;
import com.platform.ems.plug.service.IConBuTypeServiceAcceptancePurchaseService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 业务类型_服务采购验收单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/buType/acceptance/servicePurchase")
@Api(tags = "业务类型_服务采购验收单")
public class ConBuTypeServiceAcceptancePurchaseController extends BaseController {

    @Autowired
    private IConBuTypeServiceAcceptancePurchaseService conBuTypeServiceAcceptancePurchaseService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询业务类型_服务采购验收单列表
     */
    @PreAuthorize(hasPermi = "ems:buTypeServiceAcceptPurchase:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询业务类型_服务采购验收单列表", notes = "查询业务类型_服务采购验收单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeServiceAcceptancePurchase.class))
    public TableDataInfo list(@RequestBody ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase) {
        startPage(conBuTypeServiceAcceptancePurchase);
        List<ConBuTypeServiceAcceptancePurchase> list = conBuTypeServiceAcceptancePurchaseService.selectConBuTypeServiceAcceptancePurchaseList(conBuTypeServiceAcceptancePurchase);
        return getDataTable(list);
    }

    /**
     * 导出业务类型_服务采购验收单列表
     */
    @PreAuthorize(hasPermi = "ems:buTypeServiceAcceptPurchase:export")
    @Log(title = "业务类型_服务采购验收单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出业务类型_服务采购验收单列表", notes = "导出业务类型_服务采购验收单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase) throws IOException {
        List<ConBuTypeServiceAcceptancePurchase> list = conBuTypeServiceAcceptancePurchaseService.selectConBuTypeServiceAcceptancePurchaseList(conBuTypeServiceAcceptancePurchase);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConBuTypeServiceAcceptancePurchase> util = new ExcelUtil<ConBuTypeServiceAcceptancePurchase>(ConBuTypeServiceAcceptancePurchase.class,dataMap);
        util.exportExcel(response, list, "业务类型_服务采购验收单"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入业务类型_服务采购验收单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入业务类型_服务采购验收单", notes = "导入业务类型_服务采购验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<ConBuTypeServiceAcceptancePurchase> util = new ExcelUtil<ConBuTypeServiceAcceptancePurchase>(ConBuTypeServiceAcceptancePurchase.class);
        List<ConBuTypeServiceAcceptancePurchase> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(conBuTypeServiceAcceptancePurchase ->{
                conBuTypeServiceAcceptancePurchaseService.insertConBuTypeServiceAcceptancePurchase(conBuTypeServiceAcceptancePurchase);
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


    @ApiOperation(value = "下载业务类型_服务采购验收单导入模板", notes = "下载业务类型_服务采购验收单导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConBuTypeServiceAcceptancePurchase> util = new ExcelUtil<ConBuTypeServiceAcceptancePurchase>(ConBuTypeServiceAcceptancePurchase.class);
        util.importTemplateExcel(response, "业务类型_服务采购验收单导入模板");
    }


    /**
     * 获取业务类型_服务采购验收单详细信息
     */
    @ApiOperation(value = "获取业务类型_服务采购验收单详细信息", notes = "获取业务类型_服务采购验收单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeServiceAcceptancePurchase.class))
    @PreAuthorize(hasPermi = "ems:buTypeServiceAcceptPurchase:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conBuTypeServiceAcceptancePurchaseService.selectConBuTypeServiceAcceptancePurchaseById(sid));
    }

    /**
     * 新增业务类型_服务采购验收单
     */
    @ApiOperation(value = "新增业务类型_服务采购验收单", notes = "新增业务类型_服务采购验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:buTypeServiceAcceptPurchase:add")
    @Log(title = "业务类型_服务采购验收单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase) {
        return toAjax(conBuTypeServiceAcceptancePurchaseService.insertConBuTypeServiceAcceptancePurchase(conBuTypeServiceAcceptancePurchase));
    }

    /**
     * 修改业务类型_服务采购验收单
     */
    @ApiOperation(value = "修改业务类型_服务采购验收单", notes = "修改业务类型_服务采购验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:buTypeServiceAcceptPurchase:edit")
    @Log(title = "业务类型_服务采购验收单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase) {
        return toAjax(conBuTypeServiceAcceptancePurchaseService.updateConBuTypeServiceAcceptancePurchase(conBuTypeServiceAcceptancePurchase));
    }

    /**
     * 变更业务类型_服务采购验收单
     */
    @ApiOperation(value = "变更业务类型_服务采购验收单", notes = "变更业务类型_服务采购验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:buTypeServiceAcceptPurchase:change")
    @Log(title = "业务类型_服务采购验收单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase) {
        return toAjax(conBuTypeServiceAcceptancePurchaseService.changeConBuTypeServiceAcceptancePurchase(conBuTypeServiceAcceptancePurchase));
    }

    /**
     * 删除业务类型_服务采购验收单
     */
    @ApiOperation(value = "删除业务类型_服务采购验收单", notes = "删除业务类型_服务采购验收单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:buTypeServiceAcceptPurchase:remove")
    @Log(title = "业务类型_服务采购验收单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(ArrayUtil.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuTypeServiceAcceptancePurchaseService.deleteConBuTypeServiceAcceptancePurchaseByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_服务采购验收单", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:buTypeServiceAcceptPurchase:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase) {
        return AjaxResult.success(conBuTypeServiceAcceptancePurchaseService.changeStatus(conBuTypeServiceAcceptancePurchase));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:buTypeServiceAcceptPurchase:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_服务采购验收单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase) {
        conBuTypeServiceAcceptancePurchase.setConfirmDate(new Date());
        conBuTypeServiceAcceptancePurchase.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBuTypeServiceAcceptancePurchase.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBuTypeServiceAcceptancePurchaseService.check(conBuTypeServiceAcceptancePurchase));
    }

}
