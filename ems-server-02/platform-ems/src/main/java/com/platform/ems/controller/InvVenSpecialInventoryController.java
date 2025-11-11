package com.platform.ems.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.ems.domain.dto.request.InvInventorySpecialRequest;
import com.platform.ems.domain.dto.response.InvInventorySpecialResponse;
import com.platform.common.utils.bean.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
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
import com.platform.ems.domain.InvVenSpecialInventory;
import com.platform.ems.service.IInvVenSpecialInventoryService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 供应商特殊库存（寄售/甲供料）Controller
 *
 * @author linhongwei
 * @date 2021-06-01
 */
@RestController
@RequestMapping("/InvVenSpecialInventory")
@Api(tags = "供应商特殊库存（寄售/甲供料）")
public class InvVenSpecialInventoryController extends BaseController {

    @Autowired
    private IInvVenSpecialInventoryService invVenSpecialInventoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 特殊库存明细报表
     */
//    @PreAuthorize(hasPermi = "ems:inventory:list")
    @PostMapping("/report")
    @ApiOperation(value = "特殊库存明细报表", notes = "特殊库存明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventorySpecialResponse.class))
    public TableDataInfo report(@RequestBody InvInventorySpecialRequest request) {
        InvVenSpecialInventory invVenSpecialInventory = new InvVenSpecialInventory();
        BeanCopyUtils.copyProperties(request,invVenSpecialInventory);
        startPage(invVenSpecialInventory);
        List<InvVenSpecialInventory> list = invVenSpecialInventoryService.report(invVenSpecialInventory);
        return getDataTable(list,InvInventorySpecialResponse::new);

    }

    /**
     * 查询供应商特殊库存（寄售/甲供料）列表
     */
//    @PreAuthorize(hasPermi = "ems:inventory:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商特殊库存（寄售/甲供料）列表", notes = "查询供应商特殊库存（寄售/甲供料）列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvVenSpecialInventory.class))
    public TableDataInfo list(@RequestBody InvVenSpecialInventory invVenSpecialInventory) {
        startPage(invVenSpecialInventory);
        List<InvVenSpecialInventory> list = invVenSpecialInventoryService.selectInvVenSpecialInventoryList(invVenSpecialInventory);
        return getDataTable(list);
    }


    @PostMapping("/judge/add")
    @ApiOperation(value = "甲供料添加物料时校验", notes = "甲供料添加物料时校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvVenSpecialInventory.class))
    public AjaxResult list(@RequestBody List<InvVenSpecialInventory> list) {
        return AjaxResult.success(invVenSpecialInventoryService.judgeAdd(list));
    }
    /**
     * 导出供应商特殊库存（寄售/甲供料）列表
     */
    @PreAuthorize(hasPermi = "ems:inventory:export")
    @Log(title = "特殊库存导出", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "特殊库存导出", notes = "特殊库存导出")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, InvVenSpecialInventory invVenSpecialInventory) throws IOException {
        List<InvVenSpecialInventory> list = invVenSpecialInventoryService.report(invVenSpecialInventory);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<InvInventorySpecialResponse> util = new ExcelUtil<>(InvInventorySpecialResponse.class,dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list,InvInventorySpecialResponse::new), "特殊库存导出");
    }

    /**
     * 导入供应商特殊库存（寄售/甲供料）
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入供应商特殊库存（寄售/甲供料）", notes = "导入供应商特殊库存（寄售/甲供料）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<InvVenSpecialInventory> util = new ExcelUtil<InvVenSpecialInventory>(InvVenSpecialInventory.class);
        List<InvVenSpecialInventory> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(invVenSpecialInventory ->{
                invVenSpecialInventoryService.insertInvVenSpecialInventory(invVenSpecialInventory);
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


    @ApiOperation(value = "下载供应商特殊库存（寄售/甲供料）导入模板", notes = "下载供应商特殊库存（寄售/甲供料）导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<InvVenSpecialInventory> util = new ExcelUtil<InvVenSpecialInventory>(InvVenSpecialInventory.class);
        util.importTemplateExcel(response, "供应商特殊库存（寄售/甲供料）导入模板");
    }


    /**
     * 获取供应商特殊库存（寄售/甲供料）详细信息
     */
    @ApiOperation(value = "获取供应商特殊库存（寄售/甲供料）详细信息", notes = "获取供应商特殊库存（寄售/甲供料）详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvVenSpecialInventory.class))
    @PreAuthorize(hasPermi = "ems:inventory:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long vendorSpecialStockSid) {
                    if(vendorSpecialStockSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(invVenSpecialInventoryService.selectInvVenSpecialInventoryById(vendorSpecialStockSid));
    }

    /**
     * 新增供应商特殊库存（寄售/甲供料）
     */
    @ApiOperation(value = "新增供应商特殊库存（寄售/甲供料）", notes = "新增供应商特殊库存（寄售/甲供料）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:inventory:add")
    @Log(title = "供应商特殊库存（寄售/甲供料）", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid InvVenSpecialInventory invVenSpecialInventory) {
        return toAjax(invVenSpecialInventoryService.insertInvVenSpecialInventory(invVenSpecialInventory));
    }

    /**
     * 修改供应商特殊库存（寄售/甲供料）
     */
    @ApiOperation(value = "修改供应商特殊库存（寄售/甲供料）", notes = "修改供应商特殊库存（寄售/甲供料）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:inventory:edit")
    @Log(title = "供应商特殊库存（寄售/甲供料）", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody InvVenSpecialInventory invVenSpecialInventory) {
        return toAjax(invVenSpecialInventoryService.updateInvVenSpecialInventory(invVenSpecialInventory));
    }

    /**
     * 变更供应商特殊库存（寄售/甲供料）
     */
    @ApiOperation(value = "变更供应商特殊库存（寄售/甲供料）", notes = "变更供应商特殊库存（寄售/甲供料）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:inventory:change")
    @Log(title = "供应商特殊库存（寄售/甲供料）", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody InvVenSpecialInventory invVenSpecialInventory) {
        return toAjax(invVenSpecialInventoryService.changeInvVenSpecialInventory(invVenSpecialInventory));
    }

    /**
     * 删除供应商特殊库存（寄售/甲供料）
     */
    @ApiOperation(value = "删除供应商特殊库存（寄售/甲供料）", notes = "删除供应商特殊库存（寄售/甲供料）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:inventory:remove")
    @Log(title = "供应商特殊库存（寄售/甲供料）", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  vendorSpecialStockSids) {
        if(ArrayUtil.isEmpty( vendorSpecialStockSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(invVenSpecialInventoryService.deleteInvVenSpecialInventoryByIds(vendorSpecialStockSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商特殊库存（寄售/甲供料）", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:inventory:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody InvVenSpecialInventory invVenSpecialInventory) {
        return AjaxResult.success(invVenSpecialInventoryService.changeStatus(invVenSpecialInventory));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:inventory:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商特殊库存（寄售/甲供料）", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody InvVenSpecialInventory invVenSpecialInventory) {
        invVenSpecialInventory.setConfirmDate(new Date());
        invVenSpecialInventory.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        return toAjax(invVenSpecialInventoryService.check(invVenSpecialInventory));
    }

}
