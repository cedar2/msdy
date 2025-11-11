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
import com.platform.ems.plug.domain.ConContactBusinessTypeV;
import com.platform.ems.plug.service.IConContactBusinessTypeVService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 对接业务类型_供应商Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/contact/business/V")
@Api(tags = "对接业务类型_供应商")
public class ConContactBusinessTypeVController extends BaseController {

    @Autowired
    private IConContactBusinessTypeVService conContactBusinessTypeVService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询对接业务类型_供应商列表
     */
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeV:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询对接业务类型_供应商列表", notes = "查询对接业务类型_供应商列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConContactBusinessTypeV.class))
    public TableDataInfo list(@RequestBody ConContactBusinessTypeV conContactBusinessTypeV) {
        startPage(conContactBusinessTypeV);
        List<ConContactBusinessTypeV> list = conContactBusinessTypeVService.selectConContactBusinessTypeVList(conContactBusinessTypeV);
        return getDataTable(list);
    }

    /**
     * 导出对接业务类型_供应商列表
     */
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeV:export")
    @Log(title = "对接业务类型_供应商", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出对接业务类型_供应商列表", notes = "导出对接业务类型_供应商列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConContactBusinessTypeV conContactBusinessTypeV) throws IOException {
        List<ConContactBusinessTypeV> list = conContactBusinessTypeVService.selectConContactBusinessTypeVList(conContactBusinessTypeV);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConContactBusinessTypeV> util = new ExcelUtil<ConContactBusinessTypeV>(ConContactBusinessTypeV.class,dataMap);
        util.exportExcel(response, list, "对接业务类型_供应商"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入对接业务类型_供应商
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入对接业务类型_供应商", notes = "导入对接业务类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<ConContactBusinessTypeV> util = new ExcelUtil<ConContactBusinessTypeV>(ConContactBusinessTypeV.class);
        List<ConContactBusinessTypeV> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(conContactBusinessTypeV ->{
                conContactBusinessTypeVService.insertConContactBusinessTypeV(conContactBusinessTypeV);
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


    @ApiOperation(value = "下载对接业务类型_供应商导入模板", notes = "下载对接业务类型_供应商导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConContactBusinessTypeV> util = new ExcelUtil<ConContactBusinessTypeV>(ConContactBusinessTypeV.class);
        util.importTemplateExcel(response, "对接业务类型_供应商导入模板");
    }


    /**
     * 获取对接业务类型_供应商详细信息
     */
    @ApiOperation(value = "获取对接业务类型_供应商详细信息", notes = "获取对接业务类型_供应商详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConContactBusinessTypeV.class))
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeV:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conContactBusinessTypeVService.selectConContactBusinessTypeVById(sid));
    }

    /**
     * 新增对接业务类型_供应商
     */
    @ApiOperation(value = "新增对接业务类型_供应商", notes = "新增对接业务类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeV:add")
    @Log(title = "对接业务类型_供应商", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConContactBusinessTypeV conContactBusinessTypeV) {
        return toAjax(conContactBusinessTypeVService.insertConContactBusinessTypeV(conContactBusinessTypeV));
    }

    /**
     * 修改对接业务类型_供应商
     */
    @ApiOperation(value = "修改对接业务类型_供应商", notes = "修改对接业务类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeV:edit")
    @Log(title = "对接业务类型_供应商", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConContactBusinessTypeV conContactBusinessTypeV) {
        return toAjax(conContactBusinessTypeVService.updateConContactBusinessTypeV(conContactBusinessTypeV));
    }

    /**
     * 变更对接业务类型_供应商
     */
    @ApiOperation(value = "变更对接业务类型_供应商", notes = "变更对接业务类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeV:change")
    @Log(title = "对接业务类型_供应商", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConContactBusinessTypeV conContactBusinessTypeV) {
        return toAjax(conContactBusinessTypeVService.changeConContactBusinessTypeV(conContactBusinessTypeV));
    }

    /**
     * 删除对接业务类型_供应商
     */
    @ApiOperation(value = "删除对接业务类型_供应商", notes = "删除对接业务类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeV:remove")
    @Log(title = "对接业务类型_供应商", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(ArrayUtil.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conContactBusinessTypeVService.deleteConContactBusinessTypeVByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "对接业务类型_供应商", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeV:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConContactBusinessTypeV conContactBusinessTypeV) {
        return AjaxResult.success(conContactBusinessTypeVService.changeStatus(conContactBusinessTypeV));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeV:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "对接业务类型_供应商", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConContactBusinessTypeV conContactBusinessTypeV) {
        conContactBusinessTypeV.setConfirmDate(new Date());
        conContactBusinessTypeV.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conContactBusinessTypeV.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conContactBusinessTypeVService.check(conContactBusinessTypeV));
    }

}
