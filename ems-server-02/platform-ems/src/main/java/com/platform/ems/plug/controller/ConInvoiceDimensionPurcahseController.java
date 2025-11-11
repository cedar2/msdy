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
import com.platform.ems.plug.domain.ConInvoiceDimensionPurcahse;
import com.platform.ems.plug.service.IConInvoiceDimensionPurcahseService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 发票维度_采购Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/invoice/dimension/purcahse")
@Api(tags = "发票维度_采购")
public class ConInvoiceDimensionPurcahseController extends BaseController {

    @Autowired
    private IConInvoiceDimensionPurcahseService conInvoiceDimensionPurcahseService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询发票维度_采购列表
     */
    @PreAuthorize(hasPermi = "ems:invoiceDimensionPurcahse:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询发票维度_采购列表", notes = "查询发票维度_采购列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInvoiceDimensionPurcahse.class))
    public TableDataInfo list(@RequestBody ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse) {
        startPage(conInvoiceDimensionPurcahse);
        List<ConInvoiceDimensionPurcahse> list = conInvoiceDimensionPurcahseService.selectConInvoiceDimensionPurcahseList(conInvoiceDimensionPurcahse);
        return getDataTable(list);
    }

    /**
     * 导出发票维度_采购列表
     */
    @PreAuthorize(hasPermi = "ems:invoiceDimensionPurcahse:export")
    @Log(title = "发票维度_采购", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出发票维度_采购列表", notes = "导出发票维度_采购列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse) throws IOException {
        List<ConInvoiceDimensionPurcahse> list = conInvoiceDimensionPurcahseService.selectConInvoiceDimensionPurcahseList(conInvoiceDimensionPurcahse);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConInvoiceDimensionPurcahse> util = new ExcelUtil<ConInvoiceDimensionPurcahse>(ConInvoiceDimensionPurcahse.class,dataMap);
        util.exportExcel(response, list, "发票维度_采购"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入发票维度_采购
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入发票维度_采购", notes = "导入发票维度_采购")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<ConInvoiceDimensionPurcahse> util = new ExcelUtil<ConInvoiceDimensionPurcahse>(ConInvoiceDimensionPurcahse.class);
        List<ConInvoiceDimensionPurcahse> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(conInvoiceDimensionPurcahse ->{
                conInvoiceDimensionPurcahseService.insertConInvoiceDimensionPurcahse(conInvoiceDimensionPurcahse);
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


    @ApiOperation(value = "下载发票维度_采购导入模板", notes = "下载发票维度_采购导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConInvoiceDimensionPurcahse> util = new ExcelUtil<ConInvoiceDimensionPurcahse>(ConInvoiceDimensionPurcahse.class);
        util.importTemplateExcel(response, "发票维度_采购导入模板");
    }


    /**
     * 获取发票维度_采购详细信息
     */
    @ApiOperation(value = "获取发票维度_采购详细信息", notes = "获取发票维度_采购详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInvoiceDimensionPurcahse.class))
    @PreAuthorize(hasPermi = "ems:invoiceDimensionPurcahse:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conInvoiceDimensionPurcahseService.selectConInvoiceDimensionPurcahseById(sid));
    }

    /**
     * 新增发票维度_采购
     */
    @ApiOperation(value = "新增发票维度_采购", notes = "新增发票维度_采购")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:invoiceDimensionPurcahse:add")
    @Log(title = "发票维度_采购", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse) {
        return toAjax(conInvoiceDimensionPurcahseService.insertConInvoiceDimensionPurcahse(conInvoiceDimensionPurcahse));
    }

    /**
     * 修改发票维度_采购
     */
    @ApiOperation(value = "修改发票维度_采购", notes = "修改发票维度_采购")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:invoiceDimensionPurcahse:edit")
    @Log(title = "发票维度_采购", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse) {
        return toAjax(conInvoiceDimensionPurcahseService.updateConInvoiceDimensionPurcahse(conInvoiceDimensionPurcahse));
    }

    /**
     * 变更发票维度_采购
     */
    @ApiOperation(value = "变更发票维度_采购", notes = "变更发票维度_采购")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:invoiceDimensionPurcahse:change")
    @Log(title = "发票维度_采购", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse) {
        return toAjax(conInvoiceDimensionPurcahseService.changeConInvoiceDimensionPurcahse(conInvoiceDimensionPurcahse));
    }

    /**
     * 删除发票维度_采购
     */
    @ApiOperation(value = "删除发票维度_采购", notes = "删除发票维度_采购")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:invoiceDimensionPurcahse:remove")
    @Log(title = "发票维度_采购", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(ArrayUtil.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conInvoiceDimensionPurcahseService.deleteConInvoiceDimensionPurcahseByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "发票维度_采购", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:invoiceDimensionPurcahse:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse) {
        return AjaxResult.success(conInvoiceDimensionPurcahseService.changeStatus(conInvoiceDimensionPurcahse));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:invoiceDimensionPurcahse:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "发票维度_采购", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse) {
        conInvoiceDimensionPurcahse.setConfirmDate(new Date());
        conInvoiceDimensionPurcahse.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conInvoiceDimensionPurcahse.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conInvoiceDimensionPurcahseService.check(conInvoiceDimensionPurcahse));
    }

}
