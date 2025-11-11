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
import com.platform.ems.plug.domain.ConCreditType;
import com.platform.ems.plug.service.IConCreditTypeService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 信用类型Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/credit/type")
@Api(tags = "信用类型")
public class ConCreditTypeController extends BaseController {

    @Autowired
    private IConCreditTypeService conCreditTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询信用类型列表
     */
    @PreAuthorize(hasPermi = "ems:type:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询信用类型列表", notes = "查询信用类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCreditType.class))
    public TableDataInfo list(@RequestBody ConCreditType conCreditType) {
        startPage(conCreditType);
        List<ConCreditType> list = conCreditTypeService.selectConCreditTypeList(conCreditType);
        return getDataTable(list);
    }

    /**
     * 导出信用类型列表
     */
    @PreAuthorize(hasPermi = "ems:type:export")
    @Log(title = "信用类型", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出信用类型列表", notes = "导出信用类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConCreditType conCreditType) throws IOException {
        List<ConCreditType> list = conCreditTypeService.selectConCreditTypeList(conCreditType);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConCreditType> util = new ExcelUtil<ConCreditType>(ConCreditType.class,dataMap);
        util.exportExcel(response, list, "信用类型"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入信用类型
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入信用类型", notes = "导入信用类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<ConCreditType> util = new ExcelUtil<ConCreditType>(ConCreditType.class);
        List<ConCreditType> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(conCreditType ->{
                conCreditTypeService.insertConCreditType(conCreditType);
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


    @ApiOperation(value = "下载信用类型导入模板", notes = "下载信用类型导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConCreditType> util = new ExcelUtil<ConCreditType>(ConCreditType.class);
        util.importTemplateExcel(response, "信用类型导入模板");
    }


    /**
     * 获取信用类型详细信息
     */
    @ApiOperation(value = "获取信用类型详细信息", notes = "获取信用类型详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCreditType.class))
    @PreAuthorize(hasPermi = "ems:type:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conCreditTypeService.selectConCreditTypeById(sid));
    }

    /**
     * 新增信用类型
     */
    @ApiOperation(value = "新增信用类型", notes = "新增信用类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:add")
    @Log(title = "信用类型", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConCreditType conCreditType) {
        return toAjax(conCreditTypeService.insertConCreditType(conCreditType));
    }

    /**
     * 修改信用类型
     */
    @ApiOperation(value = "修改信用类型", notes = "修改信用类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:edit")
    @Log(title = "信用类型", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConCreditType conCreditType) {
        return toAjax(conCreditTypeService.updateConCreditType(conCreditType));
    }

    /**
     * 变更信用类型
     */
    @ApiOperation(value = "变更信用类型", notes = "变更信用类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:change")
    @Log(title = "信用类型", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConCreditType conCreditType) {
        return toAjax(conCreditTypeService.changeConCreditType(conCreditType));
    }

    /**
     * 删除信用类型
     */
    @ApiOperation(value = "删除信用类型", notes = "删除信用类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:remove")
    @Log(title = "信用类型", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(ArrayUtil.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conCreditTypeService.deleteConCreditTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "信用类型", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:type:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConCreditType conCreditType) {
        return AjaxResult.success(conCreditTypeService.changeStatus(conCreditType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:type:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "信用类型", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConCreditType conCreditType) {
        conCreditType.setConfirmDate(new Date());
        conCreditType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conCreditType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conCreditTypeService.check(conCreditType));
    }

}
