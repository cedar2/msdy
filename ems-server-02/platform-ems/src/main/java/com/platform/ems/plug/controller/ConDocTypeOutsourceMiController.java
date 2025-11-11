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
import com.platform.ems.plug.domain.ConDocTypeOutsourceMi;
import com.platform.ems.plug.service.IConDocTypeOutsourceMiService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 单据类型_外发加工发料单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/docType/outsource/mi")
@Api(tags = "单据类型_外发加工发料单")
public class ConDocTypeOutsourceMiController extends BaseController {

    @Autowired
    private IConDocTypeOutsourceMiService conDocTypeOutsourceMiService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询单据类型_外发加工发料单列表
     */
//    @PreAuthorize(hasPermi = "ems:doc:Type:Outsource:Mi:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型_外发加工发料单列表", notes = "查询单据类型_外发加工发料单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeOutsourceMi.class))
    public TableDataInfo list(@RequestBody ConDocTypeOutsourceMi conDocTypeOutsourceMi) {
        startPage(conDocTypeOutsourceMi);
        List<ConDocTypeOutsourceMi> list = conDocTypeOutsourceMiService.selectConDocTypeOutsourceMiList(conDocTypeOutsourceMi);
        return getDataTable(list);
    }

    /**
     * 导出单据类型_外发加工发料单列表
     */
    @PreAuthorize(hasPermi = "ems:docTypeOutsourceMi:export")
    @Log(title = "单据类型_外发加工发料单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出单据类型_外发加工发料单列表", notes = "导出单据类型_外发加工发料单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypeOutsourceMi conDocTypeOutsourceMi) throws IOException {
        List<ConDocTypeOutsourceMi> list = conDocTypeOutsourceMiService.selectConDocTypeOutsourceMiList(conDocTypeOutsourceMi);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypeOutsourceMi> util = new ExcelUtil<ConDocTypeOutsourceMi>(ConDocTypeOutsourceMi.class,dataMap);
        util.exportExcel(response, list, "单据类型_外发加工发料单"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入单据类型_外发加工发料单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入单据类型_外发加工发料单", notes = "导入单据类型_外发加工发料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<ConDocTypeOutsourceMi> util = new ExcelUtil<ConDocTypeOutsourceMi>(ConDocTypeOutsourceMi.class);
        List<ConDocTypeOutsourceMi> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(conDocTypeOutsourceMi ->{
                conDocTypeOutsourceMiService.insertConDocTypeOutsourceMi(conDocTypeOutsourceMi);
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


    @ApiOperation(value = "下载单据类型_外发加工发料单导入模板", notes = "下载单据类型_外发加工发料单导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConDocTypeOutsourceMi> util = new ExcelUtil<ConDocTypeOutsourceMi>(ConDocTypeOutsourceMi.class);
        util.importTemplateExcel(response, "单据类型_外发加工发料单导入模板");
    }


    /**
     * 获取单据类型_外发加工发料单详细信息
     */
    @ApiOperation(value = "获取单据类型_外发加工发料单详细信息", notes = "获取单据类型_外发加工发料单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeOutsourceMi.class))
    @PreAuthorize(hasPermi = "ems:docTypeOutsourceMi:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conDocTypeOutsourceMiService.selectConDocTypeOutsourceMiById(sid));
    }

    /**
     * 新增单据类型_外发加工发料单
     */
    @ApiOperation(value = "新增单据类型_外发加工发料单", notes = "新增单据类型_外发加工发料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:doc:Type:Outsource:Mi:add")
    @Log(title = "单据类型_外发加工发料单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypeOutsourceMi conDocTypeOutsourceMi) {
        return toAjax(conDocTypeOutsourceMiService.insertConDocTypeOutsourceMi(conDocTypeOutsourceMi));
    }

    /**
     * 修改单据类型_外发加工发料单
     */
    @ApiOperation(value = "修改单据类型_外发加工发料单", notes = "修改单据类型_外发加工发料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeOutsourceMi:edit")
    @Log(title = "单据类型_外发加工发料单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDocTypeOutsourceMi conDocTypeOutsourceMi) {
        return toAjax(conDocTypeOutsourceMiService.updateConDocTypeOutsourceMi(conDocTypeOutsourceMi));
    }

    /**
     * 变更单据类型_外发加工发料单
     */
    @ApiOperation(value = "变更单据类型_外发加工发料单", notes = "变更单据类型_外发加工发料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:doc:Type:Outsource:Mi:change")
    @Log(title = "单据类型_外发加工发料单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocTypeOutsourceMi conDocTypeOutsourceMi) {
        return toAjax(conDocTypeOutsourceMiService.changeConDocTypeOutsourceMi(conDocTypeOutsourceMi));
    }

    /**
     * 删除单据类型_外发加工发料单
     */
    @ApiOperation(value = "删除单据类型_外发加工发料单", notes = "删除单据类型_外发加工发料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:docTypeOutsourceMi:remove")
    @Log(title = "单据类型_外发加工发料单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(ArrayUtil.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypeOutsourceMiService.deleteConDocTypeOutsourceMiByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_外发加工发料单", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:docTypeOutsourceMi:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypeOutsourceMi conDocTypeOutsourceMi) {
        return AjaxResult.success(conDocTypeOutsourceMiService.changeStatus(conDocTypeOutsourceMi));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:docTypeOutsourceMi:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_外发加工发料单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypeOutsourceMi conDocTypeOutsourceMi) {
        conDocTypeOutsourceMi.setConfirmDate(new Date());
        conDocTypeOutsourceMi.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypeOutsourceMi.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypeOutsourceMiService.check(conDocTypeOutsourceMi));
    }

}
