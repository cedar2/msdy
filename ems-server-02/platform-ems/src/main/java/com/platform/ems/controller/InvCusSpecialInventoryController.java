package com.platform.ems.controller;

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
import com.platform.ems.domain.InvCusSpecialInventory;
import com.platform.ems.service.IInvCusSpecialInventoryService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 客户特殊库存（寄售/客供料）Controller
 *
 * @author linhongwei
 * @date 2021-06-01
 */
@RestController
@RequestMapping("/invCusSpecialInventory")
@Api(tags = "客户特殊库存（寄售/客供料）")
public class InvCusSpecialInventoryController extends BaseController {

    @Autowired
    private IInvCusSpecialInventoryService invCusSpecialInventoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询客户特殊库存（寄售/客供料）列表
     */
//    @PreAuthorize(hasPermi = "ems:inventory:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询客户特殊库存（寄售/客供料）列表", notes = "查询客户特殊库存（寄售/客供料）列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvCusSpecialInventory.class))
    public TableDataInfo list(@RequestBody InvCusSpecialInventory invCusSpecialInventory) {
        startPage(invCusSpecialInventory);
        List<InvCusSpecialInventory> list = invCusSpecialInventoryService.selectInvCusSpecialInventoryList(invCusSpecialInventory);
        return getDataTable(list);
    }

    /**
     * 导出客户特殊库存（寄售/客供料）列表
     */
    @PreAuthorize(hasPermi = "ems:inventory:export")
    @Log(title = "客户特殊库存（寄售/客供料）", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出客户特殊库存（寄售/客供料）列表", notes = "导出客户特殊库存（寄售/客供料）列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, InvCusSpecialInventory invCusSpecialInventory) throws IOException {
        List<InvCusSpecialInventory> list = invCusSpecialInventoryService.selectInvCusSpecialInventoryList(invCusSpecialInventory);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<InvCusSpecialInventory> util = new ExcelUtil<InvCusSpecialInventory>(InvCusSpecialInventory.class,dataMap);
        util.exportExcel(response, list, "客户特殊库存（寄售-客供料）");
    }

    /**
     * 导入客户特殊库存（寄售/客供料）
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入客户特殊库存（寄售/客供料）", notes = "导入客户特殊库存（寄售/客供料）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<InvCusSpecialInventory> util = new ExcelUtil<InvCusSpecialInventory>(InvCusSpecialInventory.class);
        List<InvCusSpecialInventory> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(invCusSpecialInventory ->{
                invCusSpecialInventoryService.insertInvCusSpecialInventory(invCusSpecialInventory);
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


    @ApiOperation(value = "下载客户特殊库存（寄售/客供料）导入模板", notes = "下载客户特殊库存（寄售/客供料）导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<InvCusSpecialInventory> util = new ExcelUtil<InvCusSpecialInventory>(InvCusSpecialInventory.class);
        util.importTemplateExcel(response, "客户特殊库存（寄售/客供料）导入模板");
    }


    /**
     * 获取客户特殊库存（寄售/客供料）详细信息
     */
    @ApiOperation(value = "获取客户特殊库存（寄售/客供料）详细信息", notes = "获取客户特殊库存（寄售/客供料）详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvCusSpecialInventory.class))
    @PreAuthorize(hasPermi = "ems:inventory:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long customerSpecialStockSid) {
                    if(customerSpecialStockSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(invCusSpecialInventoryService.selectInvCusSpecialInventoryById(customerSpecialStockSid));
    }

    /**
     * 新增客户特殊库存（寄售/客供料）
     */
    @ApiOperation(value = "新增客户特殊库存（寄售/客供料）", notes = "新增客户特殊库存（寄售/客供料）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:inventory:add")
    @Log(title = "客户特殊库存（寄售/客供料）", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid InvCusSpecialInventory invCusSpecialInventory) {
        return toAjax(invCusSpecialInventoryService.insertInvCusSpecialInventory(invCusSpecialInventory));
    }

    /**
     * 修改客户特殊库存（寄售/客供料）
     */
    @ApiOperation(value = "修改客户特殊库存（寄售/客供料）", notes = "修改客户特殊库存（寄售/客供料）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:inventory:edit")
    @Log(title = "客户特殊库存（寄售/客供料）", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody InvCusSpecialInventory invCusSpecialInventory) {
        return toAjax(invCusSpecialInventoryService.updateInvCusSpecialInventory(invCusSpecialInventory));
    }

    /**
     * 变更客户特殊库存（寄售/客供料）
     */
    @ApiOperation(value = "变更客户特殊库存（寄售/客供料）", notes = "变更客户特殊库存（寄售/客供料）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:inventory:change")
    @Log(title = "客户特殊库存（寄售/客供料）", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody InvCusSpecialInventory invCusSpecialInventory) {
        return toAjax(invCusSpecialInventoryService.changeInvCusSpecialInventory(invCusSpecialInventory));
    }

    /**
     * 删除客户特殊库存（寄售/客供料）
     */
    @ApiOperation(value = "删除客户特殊库存（寄售/客供料）", notes = "删除客户特殊库存（寄售/客供料）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:inventory:remove")
    @Log(title = "客户特殊库存（寄售/客供料）", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  customerSpecialStockSids) {
        if(ArrayUtil.isEmpty( customerSpecialStockSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(invCusSpecialInventoryService.deleteInvCusSpecialInventoryByIds(customerSpecialStockSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "客户特殊库存（寄售/客供料）", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:inventory:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody InvCusSpecialInventory invCusSpecialInventory) {
        return AjaxResult.success(invCusSpecialInventoryService.changeStatus(invCusSpecialInventory));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:inventory:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "客户特殊库存（寄售/客供料）", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody InvCusSpecialInventory invCusSpecialInventory) {
        invCusSpecialInventory.setConfirmDate(new Date());
        invCusSpecialInventory.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        return toAjax(invCusSpecialInventoryService.check(invCusSpecialInventory));
    }

}
