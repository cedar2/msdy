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
import com.platform.ems.plug.domain.ConDataObject;
import com.platform.ems.plug.service.IConDataObjectService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 数据对象Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/data/object")
@Api(tags = "数据对象")
public class ConDataObjectController extends BaseController {

    @Autowired
    private IConDataObjectService conDataObjectService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询数据对象列表
     */
    @PreAuthorize(hasPermi = "ems:object:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询数据对象列表", notes = "查询数据对象列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDataObject.class))
    public TableDataInfo list(@RequestBody ConDataObject conDataObject) {
        startPage(conDataObject);
        List<ConDataObject> list = conDataObjectService.selectConDataObjectList(conDataObject);
        return getDataTable(list);
    }

    /**
     * 导出数据对象列表
     */
    @PreAuthorize(hasPermi = "ems:object:export")
    @Log(title = "数据对象", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出数据对象列表", notes = "导出数据对象列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDataObject conDataObject) throws IOException {
        List<ConDataObject> list = conDataObjectService.selectConDataObjectList(conDataObject);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConDataObject> util = new ExcelUtil<ConDataObject>(ConDataObject.class,dataMap);
        util.exportExcel(response, list, "数据对象"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入数据对象
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入数据对象", notes = "导入数据对象")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<ConDataObject> util = new ExcelUtil<ConDataObject>(ConDataObject.class);
        List<ConDataObject> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(conDataObject ->{
                conDataObjectService.insertConDataObject(conDataObject);
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


    @ApiOperation(value = "下载数据对象导入模板", notes = "下载数据对象导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConDataObject> util = new ExcelUtil<ConDataObject>(ConDataObject.class);
        util.importTemplateExcel(response, "数据对象导入模板");
    }


    /**
     * 获取数据对象详细信息
     */
    @ApiOperation(value = "获取数据对象详细信息", notes = "获取数据对象详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDataObject.class))
    @PreAuthorize(hasPermi = "ems:object:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conDataObjectService.selectConDataObjectById(sid));
    }

    /**
     * 新增数据对象
     */
    @ApiOperation(value = "新增数据对象", notes = "新增数据对象")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:object:add")
    @Log(title = "数据对象", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDataObject conDataObject) {
        return toAjax(conDataObjectService.insertConDataObject(conDataObject));
    }

    /**
     * 修改数据对象
     */
    @ApiOperation(value = "修改数据对象", notes = "修改数据对象")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:object:edit")
    @Log(title = "数据对象", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDataObject conDataObject) {
        return toAjax(conDataObjectService.updateConDataObject(conDataObject));
    }

    /**
     * 变更数据对象
     */
    @ApiOperation(value = "变更数据对象", notes = "变更数据对象")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:object:change")
    @Log(title = "数据对象", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDataObject conDataObject) {
        return toAjax(conDataObjectService.changeConDataObject(conDataObject));
    }

    /**
     * 删除数据对象
     */
    @ApiOperation(value = "删除数据对象", notes = "删除数据对象")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:object:remove")
    @Log(title = "数据对象", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(ArrayUtil.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDataObjectService.deleteConDataObjectByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "数据对象", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:object:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDataObject conDataObject) {
        return AjaxResult.success(conDataObjectService.changeStatus(conDataObject));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:object:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "数据对象", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDataObject conDataObject) {
        conDataObject.setConfirmDate(new Date());
        conDataObject.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDataObject.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDataObjectService.check(conDataObject));
    }

}
