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
import com.platform.ems.plug.domain.ConDocTypeInventoryTransfer;
import com.platform.ems.plug.service.IConDocTypeInventoryTransferService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 单据类型_调拨单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/docType/inventory/transfer")
@Api(tags = "单据类型_调拨单")
public class ConDocTypeInventoryTransferController extends BaseController {

    @Autowired
    private IConDocTypeInventoryTransferService conDocTypeInventoryTransferService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询单据类型_调拨单列表
     */
//    @PreAuthorize(hasPermi = "ems:docTypeInventoryTransfer:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型_调拨单列表", notes = "查询单据类型_调拨单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeInventoryTransfer.class))
    public TableDataInfo list(@RequestBody ConDocTypeInventoryTransfer conDocTypeInventoryTransfer) {
        startPage(conDocTypeInventoryTransfer);
        List<ConDocTypeInventoryTransfer> list = conDocTypeInventoryTransferService.selectConDocTypeInventoryTransferList(conDocTypeInventoryTransfer);
        return getDataTable(list);
    }

    /**
     * 导出单据类型_调拨单列表
     */
    @PreAuthorize(hasPermi = "ems:docTypeInventoryTransfer:export")
    @Log(title = "单据类型_调拨单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出单据类型_调拨单列表", notes = "导出单据类型_调拨单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypeInventoryTransfer conDocTypeInventoryTransfer) throws IOException {
        List<ConDocTypeInventoryTransfer> list = conDocTypeInventoryTransferService.selectConDocTypeInventoryTransferList(conDocTypeInventoryTransfer);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypeInventoryTransfer> util = new ExcelUtil<ConDocTypeInventoryTransfer>(ConDocTypeInventoryTransfer.class,dataMap);
        util.exportExcel(response, list, "单据类型_调拨单"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入单据类型_调拨单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入单据类型_调拨单", notes = "导入单据类型_调拨单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<ConDocTypeInventoryTransfer> util = new ExcelUtil<ConDocTypeInventoryTransfer>(ConDocTypeInventoryTransfer.class);
        List<ConDocTypeInventoryTransfer> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(conDocTypeInventoryTransfer ->{
                conDocTypeInventoryTransferService.insertConDocTypeInventoryTransfer(conDocTypeInventoryTransfer);
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


    @ApiOperation(value = "下载单据类型_调拨单导入模板", notes = "下载单据类型_调拨单导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConDocTypeInventoryTransfer> util = new ExcelUtil<ConDocTypeInventoryTransfer>(ConDocTypeInventoryTransfer.class);
        util.importTemplateExcel(response, "单据类型_调拨单导入模板");
    }


    /**
     * 获取单据类型_调拨单详细信息
     */
    @ApiOperation(value = "获取单据类型_调拨单详细信息", notes = "获取单据类型_调拨单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeInventoryTransfer.class))
    @PreAuthorize(hasPermi = "ems:docTypeInventoryTransfer:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conDocTypeInventoryTransferService.selectConDocTypeInventoryTransferById(sid));
    }

    /**
     * 新增单据类型_调拨单
     */
    @ApiOperation(value = "新增单据类型_调拨单", notes = "新增单据类型_调拨单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeInventoryTransfer:add")
    @Log(title = "单据类型_调拨单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypeInventoryTransfer conDocTypeInventoryTransfer) {
        return toAjax(conDocTypeInventoryTransferService.insertConDocTypeInventoryTransfer(conDocTypeInventoryTransfer));
    }

    /**
     * 修改单据类型_调拨单
     */
    @ApiOperation(value = "修改单据类型_调拨单", notes = "修改单据类型_调拨单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeInventoryTransfer:edit")
    @Log(title = "单据类型_调拨单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDocTypeInventoryTransfer conDocTypeInventoryTransfer) {
        return toAjax(conDocTypeInventoryTransferService.updateConDocTypeInventoryTransfer(conDocTypeInventoryTransfer));
    }

    /**
     * 变更单据类型_调拨单
     */
    @ApiOperation(value = "变更单据类型_调拨单", notes = "变更单据类型_调拨单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeInventoryTransfer:change")
    @Log(title = "单据类型_调拨单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocTypeInventoryTransfer conDocTypeInventoryTransfer) {
        return toAjax(conDocTypeInventoryTransferService.changeConDocTypeInventoryTransfer(conDocTypeInventoryTransfer));
    }

    /**
     * 删除单据类型_调拨单
     */
    @ApiOperation(value = "删除单据类型_调拨单", notes = "删除单据类型_调拨单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeInventoryTransfer:remove")
    @Log(title = "单据类型_调拨单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(ArrayUtil.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypeInventoryTransferService.deleteConDocTypeInventoryTransferByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_调拨单", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:docTypeInventoryTransfer:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypeInventoryTransfer conDocTypeInventoryTransfer) {
        return AjaxResult.success(conDocTypeInventoryTransferService.changeStatus(conDocTypeInventoryTransfer));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:docTypeInventoryTransfer:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_调拨单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypeInventoryTransfer conDocTypeInventoryTransfer) {
        conDocTypeInventoryTransfer.setConfirmDate(new Date());
        conDocTypeInventoryTransfer.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypeInventoryTransfer.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypeInventoryTransferService.check(conDocTypeInventoryTransfer));
    }

}
