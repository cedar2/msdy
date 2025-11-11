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
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import javax.validation.Valid;
import com.platform.ems.plug.domain.ConDiscountType;
import com.platform.ems.plug.service.IConDiscountTypeService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 折扣类型Controller
 *
 * @author linhongwei
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/discount/type")
@Api(tags = "折扣类型")
public class ConDiscountTypeController extends BaseController {

    @Autowired
    private IConDiscountTypeService conDiscountTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询折扣类型列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询折扣类型列表", notes = "查询折扣类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDiscountType.class))
    public TableDataInfo list(@RequestBody ConDiscountType conDiscountType) {
        startPage(conDiscountType);
        List<ConDiscountType> list = conDiscountTypeService.selectConDiscountTypeList(conDiscountType);
        return getDataTable(list);
    }

    /**
     * 导出折扣类型列表
     */
    @Log(title = "折扣类型", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出折扣类型列表", notes = "导出折扣类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDiscountType conDiscountType) throws IOException {
        List<ConDiscountType> list = conDiscountTypeService.selectConDiscountTypeList(conDiscountType);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConDiscountType> util = new ExcelUtil<>(ConDiscountType.class,dataMap);
        util.exportExcel(response, list, "折扣类型");
    }

    /**
     * 导入折扣类型
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入折扣类型", notes = "导入折扣类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<ConDiscountType> util = new ExcelUtil<ConDiscountType>(ConDiscountType.class);
        List<ConDiscountType> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(conDiscountType ->{
                conDiscountTypeService.insertConDiscountType(conDiscountType);
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


    @ApiOperation(value = "下载折扣类型导入模板", notes = "下载折扣类型导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConDiscountType> util = new ExcelUtil<ConDiscountType>(ConDiscountType.class);
        util.importTemplateExcel(response, "折扣类型导入模板");
    }


    /**
     * 获取折扣类型详细信息
     */
    @ApiOperation(value = "获取折扣类型详细信息", notes = "获取折扣类型详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDiscountType.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conDiscountTypeService.selectConDiscountTypeById(sid));
    }

    /**
     * 新增折扣类型
     */
    @ApiOperation(value = "新增折扣类型", notes = "新增折扣类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "折扣类型", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDiscountType conDiscountType) {
        return toAjax(conDiscountTypeService.insertConDiscountType(conDiscountType));
    }

    /**
     * 修改折扣类型
     */
    @ApiOperation(value = "修改折扣类型", notes = "修改折扣类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "折扣类型", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDiscountType conDiscountType) {
        return toAjax(conDiscountTypeService.updateConDiscountType(conDiscountType));
    }

    /**
     * 变更折扣类型
     */
    @ApiOperation(value = "变更折扣类型", notes = "变更折扣类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "折扣类型", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDiscountType conDiscountType) {
        return toAjax(conDiscountTypeService.changeConDiscountType(conDiscountType));
    }

    /**
     * 删除折扣类型
     */
    @ApiOperation(value = "删除折扣类型", notes = "删除折扣类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "折扣类型", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(ArrayUtil.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDiscountTypeService.deleteConDiscountTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "折扣类型", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDiscountType conDiscountType) {
        return AjaxResult.success(conDiscountTypeService.changeStatus(conDiscountType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "折扣类型", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDiscountType conDiscountType) {
        conDiscountType.setConfirmDate(new Date());
        conDiscountType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDiscountType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDiscountTypeService.check(conDiscountType));
    }

    @PostMapping("/getConDiscountTypeList")
    @ApiOperation(value = "折扣类型下拉列表", notes = "折扣类型下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDiscountType.class))
    public AjaxResult getConDiscountTypeList(){
        return AjaxResult.success(conDiscountTypeService.getConDiscountTypeList());
    }

}
