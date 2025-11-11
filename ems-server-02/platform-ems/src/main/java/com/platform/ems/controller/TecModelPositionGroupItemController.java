package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.dto.response.form.TecModelPositionGroupItemFormResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
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
import com.platform.ems.domain.TecModelPositionGroupItem;
import com.platform.ems.service.ITecModelPositionGroupItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 版型部位组明细Controller
 *
 * @author linhongwei
 * @date 2021-06-02
 */
@RestController
@RequestMapping("//model/pos/group/item")
@Api(tags = "版型部位组明细")
public class TecModelPositionGroupItemController extends BaseController {

    @Autowired
    private ITecModelPositionGroupItemService tecModelPositionGroupItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询版型部位组明细列表
     */
    @PreAuthorize(hasPermi = "ems:model:pos:item:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询版型部位组明细列表", notes = "查询版型部位组明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecModelPositionGroupItem.class))
    public TableDataInfo list(@RequestBody TecModelPositionGroupItem tecModelPositionGroupItem) {
        startPage(tecModelPositionGroupItem);
        List<TecModelPositionGroupItem> list = tecModelPositionGroupItemService.selectTecModelPositionGroupItemList(tecModelPositionGroupItem);
        return getDataTable(list);
    }

    /**
     * 导入版型部位组明细
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入版型部位组明细", notes = "导入版型部位组明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<TecModelPositionGroupItem> util = new ExcelUtil<TecModelPositionGroupItem>(TecModelPositionGroupItem.class);
        List<TecModelPositionGroupItem> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(tecModelPositionGroupItem ->{
                tecModelPositionGroupItemService.insertTecModelPositionGroupItem(tecModelPositionGroupItem);
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


    @ApiOperation(value = "下载版型部位组明细导入模板", notes = "下载版型部位组明细导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<TecModelPositionGroupItem> util = new ExcelUtil<TecModelPositionGroupItem>(TecModelPositionGroupItem.class);
        util.importTemplateExcel(response, "版型部位组明细导入模板");
    }


    /**
     * 获取版型部位组明细详细信息
     */
    @ApiOperation(value = "获取版型部位组明细详细信息", notes = "获取版型部位组明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecModelPositionGroupItem.class))
    @PreAuthorize(hasPermi = "ems:model:pos:item:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long groupItemSid) {
                    if(groupItemSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(tecModelPositionGroupItemService.selectTecModelPositionGroupItemById(groupItemSid));
    }

    /**
     * 新增版型部位组明细
     */
    @ApiOperation(value = "新增版型部位组明细", notes = "新增版型部位组明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:model:pos:item:add")
    @Log(title = "版型部位组明细", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid TecModelPositionGroupItem tecModelPositionGroupItem) {
        return toAjax(tecModelPositionGroupItemService.insertTecModelPositionGroupItem(tecModelPositionGroupItem));
    }

    /**
     * 修改版型部位组明细
     */
    @ApiOperation(value = "修改版型部位组明细", notes = "修改版型部位组明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:model:pos:item:edit")
    @Log(title = "版型部位组明细", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody TecModelPositionGroupItem tecModelPositionGroupItem) {
        return toAjax(tecModelPositionGroupItemService.updateTecModelPositionGroupItem(tecModelPositionGroupItem));
    }

    /**
     * 变更版型部位组明细
     */
    @ApiOperation(value = "变更版型部位组明细", notes = "变更版型部位组明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:model:pos:item:change")
    @Log(title = "版型部位组明细", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody TecModelPositionGroupItem tecModelPositionGroupItem) {
        return toAjax(tecModelPositionGroupItemService.changeTecModelPositionGroupItem(tecModelPositionGroupItem));
    }

    /**
     * 删除版型部位组明细
     */
    @ApiOperation(value = "删除版型部位组明细", notes = "删除版型部位组明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:model:pos:item:remove")
    @Log(title = "版型部位组明细", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  groupItemSids) {
        if(ArrayUtil.isEmpty( groupItemSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(tecModelPositionGroupItemService.deleteTecModelPositionGroupItemByIds(groupItemSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "版型部位组明细", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:model:pos:item:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody TecModelPositionGroupItem tecModelPositionGroupItem) {
        return AjaxResult.success(tecModelPositionGroupItemService.changeStatus(tecModelPositionGroupItem));
    }

    /**
     * 查询版型部位组明细报表
     */
    @PreAuthorize(hasPermi = "ems:model:pos:item:report")
    @PostMapping("/report")
    @ApiOperation(value = "查询版型部位组明细报表", notes = "查询版型部位组明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecModelPositionGroupItem.class))
    public TableDataInfo report(@RequestBody TecModelPositionGroupItem tecModelPositionGroupItem) {
        startPage(tecModelPositionGroupItem);
        List<TecModelPositionGroupItem> list = tecModelPositionGroupItemService.selectTecModelPositionGroupItemList(tecModelPositionGroupItem);
        return getDataTable(list,TecModelPositionGroupItemFormResponse::new);
    }

    /**
     * 导出版型部位组明细报表
     */
    @PreAuthorize(hasPermi = "ems:model:pos:item:export")
    @Log(title = "版型部位组明细", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出版型部位组明细报表", notes = "导出版型部位组明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, TecModelPositionGroupItem tecModelPositionGroupItem) throws IOException {
        List<TecModelPositionGroupItem> list = tecModelPositionGroupItemService.selectTecModelPositionGroupItemList(tecModelPositionGroupItem);
        List<TecModelPositionGroupItemFormResponse> responseList = BeanCopyUtils.copyListProperties(list, TecModelPositionGroupItemFormResponse::new);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<TecModelPositionGroupItemFormResponse> util = new ExcelUtil<>(TecModelPositionGroupItemFormResponse.class,dataMap);
        util.exportExcel(response, responseList, "版型部位组明细报表_"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }
}
