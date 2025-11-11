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
import com.platform.ems.plug.domain.ConDataObjectFileType;
import com.platform.ems.plug.service.IConDataObjectFileTypeService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 数据对象&附件类型对照Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/data/file/type")
@Api(tags = "数据对象&附件类型对照")
public class ConDataObjectFileTypeController extends BaseController {

    @Autowired
    private IConDataObjectFileTypeService conDataObjectFileTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询数据对象&附件类型对照列表
     */
    @PreAuthorize(hasPermi = "ems:type:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询数据对象&附件类型对照列表", notes = "查询数据对象&附件类型对照列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDataObjectFileType.class))
    public TableDataInfo list(@RequestBody ConDataObjectFileType conDataObjectFileType) {
        startPage(conDataObjectFileType);
        List<ConDataObjectFileType> list = conDataObjectFileTypeService.selectConDataObjectFileTypeList(conDataObjectFileType);
        return getDataTable(list);
    }

    /**
     * 导出数据对象&附件类型对照列表
     */
    @PreAuthorize(hasPermi = "ems:type:export")
    @Log(title = "数据对象&附件类型对照", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出数据对象&附件类型对照列表", notes = "导出数据对象&附件类型对照列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDataObjectFileType conDataObjectFileType) throws IOException {
        List<ConDataObjectFileType> list = conDataObjectFileTypeService.selectConDataObjectFileTypeList(conDataObjectFileType);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConDataObjectFileType> util = new ExcelUtil<ConDataObjectFileType>(ConDataObjectFileType.class,dataMap);
        util.exportExcel(response, list, "数据对象&附件类型对照"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入数据对象&附件类型对照
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入数据对象&附件类型对照", notes = "导入数据对象&附件类型对照")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<ConDataObjectFileType> util = new ExcelUtil<ConDataObjectFileType>(ConDataObjectFileType.class);
        List<ConDataObjectFileType> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(conDataObjectFileType ->{
                conDataObjectFileTypeService.insertConDataObjectFileType(conDataObjectFileType);
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


    @ApiOperation(value = "下载数据对象&附件类型对照导入模板", notes = "下载数据对象&附件类型对照导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConDataObjectFileType> util = new ExcelUtil<ConDataObjectFileType>(ConDataObjectFileType.class);
        util.importTemplateExcel(response, "数据对象&附件类型对照导入模板");
    }


    /**
     * 获取数据对象&附件类型对照详细信息
     */
    @ApiOperation(value = "获取数据对象&附件类型对照详细信息", notes = "获取数据对象&附件类型对照详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDataObjectFileType.class))
    @PreAuthorize(hasPermi = "ems:type:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conDataObjectFileTypeService.selectConDataObjectFileTypeById(sid));
    }

    /**
     * 新增数据对象&附件类型对照
     */
    @ApiOperation(value = "新增数据对象&附件类型对照", notes = "新增数据对象&附件类型对照")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:add")
    @Log(title = "数据对象&附件类型对照", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDataObjectFileType conDataObjectFileType) {
        return toAjax(conDataObjectFileTypeService.insertConDataObjectFileType(conDataObjectFileType));
    }

    /**
     * 修改数据对象&附件类型对照
     */
    @ApiOperation(value = "修改数据对象&附件类型对照", notes = "修改数据对象&附件类型对照")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:edit")
    @Log(title = "数据对象&附件类型对照", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDataObjectFileType conDataObjectFileType) {
        return toAjax(conDataObjectFileTypeService.updateConDataObjectFileType(conDataObjectFileType));
    }

    /**
     * 变更数据对象&附件类型对照
     */
    @ApiOperation(value = "变更数据对象&附件类型对照", notes = "变更数据对象&附件类型对照")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:change")
    @Log(title = "数据对象&附件类型对照", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDataObjectFileType conDataObjectFileType) {
        return toAjax(conDataObjectFileTypeService.changeConDataObjectFileType(conDataObjectFileType));
    }

    /**
     * 删除数据对象&附件类型对照
     */
    @ApiOperation(value = "删除数据对象&附件类型对照", notes = "删除数据对象&附件类型对照")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:remove")
    @Log(title = "数据对象&附件类型对照", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(ArrayUtil.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDataObjectFileTypeService.deleteConDataObjectFileTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "数据对象&附件类型对照", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:type:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDataObjectFileType conDataObjectFileType) {
        return AjaxResult.success(conDataObjectFileTypeService.changeStatus(conDataObjectFileType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:type:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "数据对象&附件类型对照", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDataObjectFileType conDataObjectFileType) {
        conDataObjectFileType.setConfirmDate(new Date());
        conDataObjectFileType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDataObjectFileType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDataObjectFileTypeService.check(conDataObjectFileType));
    }

}
