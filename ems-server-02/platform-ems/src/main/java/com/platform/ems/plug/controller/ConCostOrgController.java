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
import com.platform.ems.plug.domain.ConCostOrg;
import com.platform.ems.plug.service.IConCostOrgService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 成本组织Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/costOrg")
@Api(tags = "成本组织")
public class ConCostOrgController extends BaseController {

    @Autowired
    private IConCostOrgService conCostOrgService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询成本组织列表
     */
    @PreAuthorize(hasPermi = "ems:costOrg:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询成本组织列表", notes = "查询成本组织列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCostOrg.class))
    public TableDataInfo list(@RequestBody ConCostOrg conCostOrg) {
        startPage(conCostOrg);
        List<ConCostOrg> list = conCostOrgService.selectConCostOrgList(conCostOrg);
        return getDataTable(list);
    }

    /**
     * 成本组织下拉框列表
     */
    @PostMapping("/getCostOrgList")
    @ApiOperation(value = "成本组织下拉框列表", notes = "成本组织下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCostOrg.class))
    public AjaxResult getCostOrgList(@RequestBody ConCostOrg conCostOrg) {
        return AjaxResult.success(conCostOrgService.getCostOrgList(conCostOrg));
    }

    /**
     * 导出成本组织列表
     */
    @PreAuthorize(hasPermi = "ems:costOrg:export")
    @Log(title = "成本组织", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出成本组织列表", notes = "导出成本组织列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConCostOrg conCostOrg) throws IOException {
        List<ConCostOrg> list = conCostOrgService.selectConCostOrgList(conCostOrg);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConCostOrg> util = new ExcelUtil<ConCostOrg>(ConCostOrg.class,dataMap);
        util.exportExcel(response, list, "成本组织"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入成本组织
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入成本组织", notes = "导入成本组织")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<ConCostOrg> util = new ExcelUtil<ConCostOrg>(ConCostOrg.class);
        List<ConCostOrg> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(conCostOrg ->{
                conCostOrgService.insertConCostOrg(conCostOrg);
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


    @ApiOperation(value = "下载成本组织导入模板", notes = "下载成本组织导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConCostOrg> util = new ExcelUtil<ConCostOrg>(ConCostOrg.class);
        util.importTemplateExcel(response, "成本组织导入模板");
    }


    /**
     * 获取成本组织详细信息
     */
    @ApiOperation(value = "获取成本组织详细信息", notes = "获取成本组织详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCostOrg.class))
    @PreAuthorize(hasPermi = "ems:costOrg:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conCostOrgService.selectConCostOrgById(sid));
    }

    /**
     * 新增成本组织
     */
    @ApiOperation(value = "新增成本组织", notes = "新增成本组织")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:costOrg:add")
    @Log(title = "成本组织", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConCostOrg conCostOrg) {
        return toAjax(conCostOrgService.insertConCostOrg(conCostOrg));
    }

    /**
     * 修改成本组织
     */
    @ApiOperation(value = "修改成本组织", notes = "修改成本组织")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:costOrg:edit")
    @Log(title = "成本组织", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConCostOrg conCostOrg) {
        return toAjax(conCostOrgService.updateConCostOrg(conCostOrg));
    }

    /**
     * 变更成本组织
     */
    @ApiOperation(value = "变更成本组织", notes = "变更成本组织")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:costOrg:change")
    @Log(title = "成本组织", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConCostOrg conCostOrg) {
        return toAjax(conCostOrgService.changeConCostOrg(conCostOrg));
    }

    /**
     * 删除成本组织
     */
    @ApiOperation(value = "删除成本组织", notes = "删除成本组织")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:costOrg:remove")
    @Log(title = "成本组织", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(ArrayUtil.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conCostOrgService.deleteConCostOrgByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "成本组织", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:costOrg:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConCostOrg conCostOrg) {
        return AjaxResult.success(conCostOrgService.changeStatus(conCostOrg));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:costOrg:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "成本组织", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConCostOrg conCostOrg) {
        conCostOrg.setConfirmDate(new Date());
        conCostOrg.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conCostOrg.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conCostOrgService.check(conCostOrg));
    }

}
