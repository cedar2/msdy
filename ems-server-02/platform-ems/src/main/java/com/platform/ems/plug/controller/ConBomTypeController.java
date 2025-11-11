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
import com.platform.ems.plug.domain.ConBomType;
import com.platform.ems.plug.service.IConBomTypeService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * BOM类型Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/bom/type")
@Api(tags = "BOM类型")
public class ConBomTypeController extends BaseController {

    @Autowired
    private IConBomTypeService conBomTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询BOM类型列表
     */
    @PreAuthorize(hasPermi = "ems:bomType:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询BOM类型列表", notes = "查询BOM类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBomType.class))
    public TableDataInfo list(@RequestBody ConBomType conBomType) {
        startPage(conBomType);
        List<ConBomType> list = conBomTypeService.selectConBomTypeList(conBomType);
        return getDataTable(list);
    }

    /**
     * 导出BOM类型列表
     */
    @PreAuthorize(hasPermi = "ems:bomType:export")
    @Log(title = "BOM类型", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出BOM类型列表", notes = "导出BOM类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBomType conBomType) throws IOException {
        List<ConBomType> list = conBomTypeService.selectConBomTypeList(conBomType);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConBomType> util = new ExcelUtil<ConBomType>(ConBomType.class,dataMap);
        util.exportExcel(response, list, "BOM类型"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入BOM类型
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入BOM类型", notes = "导入BOM类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<ConBomType> util = new ExcelUtil<ConBomType>(ConBomType.class);
        List<ConBomType> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(conBomType ->{
                conBomTypeService.insertConBomType(conBomType);
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


    @ApiOperation(value = "下载BOM类型导入模板", notes = "下载BOM类型导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConBomType> util = new ExcelUtil<ConBomType>(ConBomType.class);
        util.importTemplateExcel(response, "BOM类型导入模板");
    }


    /**
     * 获取BOM类型详细信息
     */
    @ApiOperation(value = "获取BOM类型详细信息", notes = "获取BOM类型详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBomType.class))
    @PreAuthorize(hasPermi = "ems:bomType:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conBomTypeService.selectConBomTypeById(sid));
    }

    /**
     * 新增BOM类型
     */
    @ApiOperation(value = "新增BOM类型", notes = "新增BOM类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bomType:add")
    @Log(title = "BOM类型", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBomType conBomType) {
        return toAjax(conBomTypeService.insertConBomType(conBomType));
    }

    /**
     * 修改BOM类型
     */
    @ApiOperation(value = "修改BOM类型", notes = "修改BOM类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bomType:edit")
    @Log(title = "BOM类型", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConBomType conBomType) {
        return toAjax(conBomTypeService.updateConBomType(conBomType));
    }

    /**
     * 变更BOM类型
     */
    @ApiOperation(value = "变更BOM类型", notes = "变更BOM类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bomType:change")
    @Log(title = "BOM类型", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBomType conBomType) {
        return toAjax(conBomTypeService.changeConBomType(conBomType));
    }

    /**
     * 删除BOM类型
     */
    @ApiOperation(value = "删除BOM类型", notes = "删除BOM类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bomType:remove")
    @Log(title = "BOM类型", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(ArrayUtil.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBomTypeService.deleteConBomTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "BOM类型", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:bomType:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBomType conBomType) {
        return AjaxResult.success(conBomTypeService.changeStatus(conBomType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:bomType:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "BOM类型", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBomType conBomType) {
        conBomType.setConfirmDate(new Date());
        conBomType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBomType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBomTypeService.check(conBomType));
    }

}
