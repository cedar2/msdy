package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.ems.plug.domain.ConDocTypeSampleLendreturn;
import com.platform.ems.plug.service.IConDocTypeSampleLendreturnService;
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
import cn.hutool.core.util.StrUtil;
import javax.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 单据类型_样品借还单Controller
 *
 * @author linhongwei
 * @date 2022-01-24
 */
@RestController
@RequestMapping("con/doc/lendreturn")
@Api(tags = "单据类型_样品借还单")
public class ConDocTypeSampleLendreturnController extends BaseController {

    @Autowired
    private IConDocTypeSampleLendreturnService conDocTypeSampleLendreturnService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询单据类型_样品借还单列表
     */
//    @PreAuthorize(hasPermi = "ems:lendreturn:list")
    @PostMapping("/getList")
    @ApiOperation(value = "查询单据类型_样品借还单下拉列表", notes = "查询单据类型_样品借还单下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeSampleLendreturn.class))
    public List<ConDocTypeSampleLendreturn>  list(@RequestBody ConDocTypeSampleLendreturn conDocTypeSampleLendreturn) {
        List<ConDocTypeSampleLendreturn> list = conDocTypeSampleLendreturnService.selectConDocTypeSampleLendreturnList(conDocTypeSampleLendreturn);
        return list;
    }

    /**
     * 导出单据类型_样品借还单列表
     */
    @PreAuthorize(hasPermi = "ems:lendreturn:export")
    @Log(title = "单据类型_样品借还单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出单据类型_样品借还单列表", notes = "导出单据类型_样品借还单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypeSampleLendreturn conDocTypeSampleLendreturn) throws IOException {
        List<ConDocTypeSampleLendreturn> list = conDocTypeSampleLendreturnService.selectConDocTypeSampleLendreturnList(conDocTypeSampleLendreturn);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypeSampleLendreturn> util = new ExcelUtil<ConDocTypeSampleLendreturn>(ConDocTypeSampleLendreturn.class,dataMap);
        util.exportExcel(response, list, "单据类型_样品借还单"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取单据类型_样品借还单详细信息
     */
    @ApiOperation(value = "获取单据类型_样品借还单详细信息", notes = "获取单据类型_样品借还单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeSampleLendreturn.class))
    @PreAuthorize(hasPermi = "ems:lendreturn:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conDocTypeSampleLendreturnService.selectConDocTypeSampleLendreturnById(sid));
    }

    /**
     * 新增单据类型_样品借还单
     */
    @ApiOperation(value = "新增单据类型_样品借还单", notes = "新增单据类型_样品借还单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:lendreturn:add")
    @Log(title = "单据类型_样品借还单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypeSampleLendreturn conDocTypeSampleLendreturn) {
        return toAjax(conDocTypeSampleLendreturnService.insertConDocTypeSampleLendreturn(conDocTypeSampleLendreturn));
    }

    /**
     * 修改单据类型_样品借还单
     */
    @ApiOperation(value = "修改单据类型_样品借还单", notes = "修改单据类型_样品借还单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:lendreturn:edit")
    @Log(title = "单据类型_样品借还单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDocTypeSampleLendreturn conDocTypeSampleLendreturn) {
        return toAjax(conDocTypeSampleLendreturnService.updateConDocTypeSampleLendreturn(conDocTypeSampleLendreturn));
    }

    /**
     * 变更单据类型_样品借还单
     */
    @ApiOperation(value = "变更单据类型_样品借还单", notes = "变更单据类型_样品借还单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:lendreturn:change")
    @Log(title = "单据类型_样品借还单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody ConDocTypeSampleLendreturn conDocTypeSampleLendreturn) {
        return toAjax(conDocTypeSampleLendreturnService.changeConDocTypeSampleLendreturn(conDocTypeSampleLendreturn));
    }

    /**
     * 删除单据类型_样品借还单
     */
    @ApiOperation(value = "删除单据类型_样品借还单", notes = "删除单据类型_样品借还单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:lendreturn:remove")
    @Log(title = "单据类型_样品借还单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(CollectionUtils.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypeSampleLendreturnService.deleteConDocTypeSampleLendreturnByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_样品借还单", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:lendreturn:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypeSampleLendreturn conDocTypeSampleLendreturn) {
        return AjaxResult.success(conDocTypeSampleLendreturnService.changeStatus(conDocTypeSampleLendreturn));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:lendreturn:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_样品借还单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypeSampleLendreturn conDocTypeSampleLendreturn) {
        conDocTypeSampleLendreturn.setConfirmDate(new Date());
        conDocTypeSampleLendreturn.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypeSampleLendreturn.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypeSampleLendreturnService.check(conDocTypeSampleLendreturn));
    }

}
