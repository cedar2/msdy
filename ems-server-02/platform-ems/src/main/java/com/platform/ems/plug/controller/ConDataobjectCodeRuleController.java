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
import javax.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.plug.domain.ConDataobjectCodeRule;
import com.platform.ems.plug.service.IConDataobjectCodeRuleService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 数据对象类别编码规则Controller
 *
 * @author chenkw
 * @date 2021-11-25
 */
@RestController
@RequestMapping("/con/dataobject/codeRule")
@Api(tags = "数据对象类别编码规则")
public class ConDataobjectCodeRuleController extends BaseController {

    @Autowired
    private IConDataobjectCodeRuleService conDataobjectCodeRuleService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询数据对象类别编码规则列表
     */
    @PreAuthorize(hasPermi = "ems:con:dataobject:codeRule:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询数据对象类别编码规则列表", notes = "查询数据对象类别编码规则列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDataobjectCodeRule.class))
    public TableDataInfo list(@RequestBody ConDataobjectCodeRule conDataobjectCodeRule) {
        startPage(conDataobjectCodeRule);
        List<ConDataobjectCodeRule> list = conDataobjectCodeRuleService.selectConDataobjectCodeRuleList(conDataobjectCodeRule);
        return getDataTable(list);
    }

    /**
     * 导出数据对象类别编码规则列表
     */
    @PreAuthorize(hasPermi = "ems:con:dataobject:codeRule:export")
    @Log(title = "数据对象类别编码规则", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出数据对象类别编码规则列表", notes = "导出数据对象类别编码规则列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDataobjectCodeRule conDataobjectCodeRule) throws IOException {
        List<ConDataobjectCodeRule> list = conDataobjectCodeRuleService.selectConDataobjectCodeRuleList(conDataobjectCodeRule);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConDataobjectCodeRule> util = new ExcelUtil<>(ConDataobjectCodeRule.class,dataMap);
        util.exportExcel(response, list, "数据对象类别编码规则"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取数据对象类别编码规则详细信息
     */
    @ApiOperation(value = "获取数据对象类别编码规则详细信息", notes = "获取数据对象类别编码规则详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDataobjectCodeRule.class))
    @PreAuthorize(hasPermi = "ems:con:dataobject:codeRule:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if(sid==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDataobjectCodeRuleService.selectConDataobjectCodeRuleById(sid));
    }

    /**
     * 新增数据对象类别编码规则
     */
    @ApiOperation(value = "新增数据对象类别编码规则", notes = "新增数据对象类别编码规则")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:dataobject:codeRule:add")
    @Log(title = "数据对象类别编码规则", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDataobjectCodeRule conDataobjectCodeRule) {
        return toAjax(conDataobjectCodeRuleService.insertConDataobjectCodeRule(conDataobjectCodeRule));
    }

    /**
     * 修改数据对象类别编码规则
     */
    @ApiOperation(value = "修改数据对象类别编码规则", notes = "修改数据对象类别编码规则")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:dataobject:codeRule:edit")
    @Log(title = "数据对象类别编码规则", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDataobjectCodeRule conDataobjectCodeRule) {
        return toAjax(conDataobjectCodeRuleService.updateConDataobjectCodeRule(conDataobjectCodeRule));
    }

    /**
     * 变更数据对象类别编码规则
     */
    @ApiOperation(value = "变更数据对象类别编码规则", notes = "变更数据对象类别编码规则")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:dataobject:codeRule:change")
    @Log(title = "数据对象类别编码规则", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody ConDataobjectCodeRule conDataobjectCodeRule) {
        return toAjax(conDataobjectCodeRuleService.changeConDataobjectCodeRule(conDataobjectCodeRule));
    }

    /**
     * 删除数据对象类别编码规则
     */
    @ApiOperation(value = "删除数据对象类别编码规则", notes = "删除数据对象类别编码规则")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:con:dataobject:codeRule:remove")
    @Log(title = "数据对象类别编码规则", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(CollectionUtils.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDataobjectCodeRuleService.deleteConDataobjectCodeRuleByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "数据对象类别编码规则", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:con:dataobject:codeRule:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDataobjectCodeRule conDataobjectCodeRule) {
        return AjaxResult.success(conDataobjectCodeRuleService.changeStatus(conDataobjectCodeRule));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:con:dataobject:codeRule:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "数据对象类别编码规则", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDataobjectCodeRule conDataobjectCodeRule) {
        conDataobjectCodeRule.setConfirmDate(new Date());
        conDataobjectCodeRule.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDataobjectCodeRule.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDataobjectCodeRuleService.check(conDataobjectCodeRule));
    }

    @ApiOperation(value = "测试", notes = "测试")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "测试", businessType = BusinessType.CHECK)
    @PostMapping("/test")
    public AjaxResult test(@RequestBody ConDataobjectCodeRule conDataobjectCodeRule) {
        return toAjax(conDataobjectCodeRuleService.addCurrentNumber(conDataobjectCodeRule));
    }

}
