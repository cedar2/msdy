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
import com.platform.ems.plug.domain.ConContactBusinessTypeC;
import com.platform.ems.plug.service.IConContactBusinessTypeCService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 对接业务类型_客户Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/contact/business/C")
@Api(tags = "对接业务类型_客户")
public class ConContactBusinessTypeCController extends BaseController {

    @Autowired
    private IConContactBusinessTypeCService conContactBusinessTypeCService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询对接业务类型_客户列表
     */
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeC:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询对接业务类型_客户列表", notes = "查询对接业务类型_客户列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConContactBusinessTypeC.class))
    public TableDataInfo list(@RequestBody ConContactBusinessTypeC conContactBusinessTypeC) {
        startPage(conContactBusinessTypeC);
        List<ConContactBusinessTypeC> list = conContactBusinessTypeCService.selectConContactBusinessTypeCList(conContactBusinessTypeC);
        return getDataTable(list);
    }

    /**
     * 导出对接业务类型_客户列表
     */
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeC:export")
    @Log(title = "对接业务类型_客户", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出对接业务类型_客户列表", notes = "导出对接业务类型_客户列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConContactBusinessTypeC conContactBusinessTypeC) throws IOException {
        List<ConContactBusinessTypeC> list = conContactBusinessTypeCService.selectConContactBusinessTypeCList(conContactBusinessTypeC);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConContactBusinessTypeC> util = new ExcelUtil<ConContactBusinessTypeC>(ConContactBusinessTypeC.class,dataMap);
        util.exportExcel(response, list, "对接业务类型_客户"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入对接业务类型_客户
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入对接业务类型_客户", notes = "导入对接业务类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<ConContactBusinessTypeC> util = new ExcelUtil<ConContactBusinessTypeC>(ConContactBusinessTypeC.class);
        List<ConContactBusinessTypeC> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(conContactBusinessTypeC ->{
                conContactBusinessTypeCService.insertConContactBusinessTypeC(conContactBusinessTypeC);
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


    @ApiOperation(value = "下载对接业务类型_客户导入模板", notes = "下载对接业务类型_客户导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConContactBusinessTypeC> util = new ExcelUtil<ConContactBusinessTypeC>(ConContactBusinessTypeC.class);
        util.importTemplateExcel(response, "对接业务类型_客户导入模板");
    }


    /**
     * 获取对接业务类型_客户详细信息
     */
    @ApiOperation(value = "获取对接业务类型_客户详细信息", notes = "获取对接业务类型_客户详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConContactBusinessTypeC.class))
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeC:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conContactBusinessTypeCService.selectConContactBusinessTypeCById(sid));
    }

    /**
     * 新增对接业务类型_客户
     */
    @ApiOperation(value = "新增对接业务类型_客户", notes = "新增对接业务类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeC:add")
    @Log(title = "对接业务类型_客户", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConContactBusinessTypeC conContactBusinessTypeC) {
        return toAjax(conContactBusinessTypeCService.insertConContactBusinessTypeC(conContactBusinessTypeC));
    }

    /**
     * 修改对接业务类型_客户
     */
    @ApiOperation(value = "修改对接业务类型_客户", notes = "修改对接业务类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeC:edit")
    @Log(title = "对接业务类型_客户", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConContactBusinessTypeC conContactBusinessTypeC) {
        return toAjax(conContactBusinessTypeCService.updateConContactBusinessTypeC(conContactBusinessTypeC));
    }

    /**
     * 变更对接业务类型_客户
     */
    @ApiOperation(value = "变更对接业务类型_客户", notes = "变更对接业务类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeC:change")
    @Log(title = "对接业务类型_客户", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConContactBusinessTypeC conContactBusinessTypeC) {
        return toAjax(conContactBusinessTypeCService.changeConContactBusinessTypeC(conContactBusinessTypeC));
    }

    /**
     * 删除对接业务类型_客户
     */
    @ApiOperation(value = "删除对接业务类型_客户", notes = "删除对接业务类型_客户")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeC:remove")
    @Log(title = "对接业务类型_客户", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(ArrayUtil.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conContactBusinessTypeCService.deleteConContactBusinessTypeCByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "对接业务类型_客户", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeC:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConContactBusinessTypeC conContactBusinessTypeC) {
        return AjaxResult.success(conContactBusinessTypeCService.changeStatus(conContactBusinessTypeC));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:contactBusinessTypeC:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "对接业务类型_客户", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConContactBusinessTypeC conContactBusinessTypeC) {
        conContactBusinessTypeC.setConfirmDate(new Date());
        conContactBusinessTypeC.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conContactBusinessTypeC.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conContactBusinessTypeCService.check(conContactBusinessTypeC));
    }

}
