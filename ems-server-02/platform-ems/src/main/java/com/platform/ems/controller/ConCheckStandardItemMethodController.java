package com.platform.ems.controller;

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
import cn.hutool.core.util.StrUtil;
import javax.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.ConCheckStandardItemMethod;
import com.platform.ems.service.IConCheckStandardItemMethodService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 检测标准/项目/方法关联Controller
 *
 * @author qhq
 * @date 2021-11-01
 */
@RestController
@RequestMapping("/check/standard/item/method")
@Api(tags = "检测标准/项目/方法关联")
public class ConCheckStandardItemMethodController extends BaseController {

    @Autowired
    private IConCheckStandardItemMethodService conCheckStandardItemMethodService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询检测标准/项目/方法关联列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询检测标准/项目/方法关联列表", notes = "查询检测标准/项目/方法关联列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCheckStandardItemMethod.class))
    public TableDataInfo list(@RequestBody ConCheckStandardItemMethod conCheckStandardItemMethod) {
        startPage(conCheckStandardItemMethod);
        List<ConCheckStandardItemMethod> list = conCheckStandardItemMethodService.selectConCheckStandardItemMethodList(conCheckStandardItemMethod);
        return getDataTable(list);
    }

    /**
     * 导出检测标准/项目/方法关联列表
     */
    @Log(title = "检测标准/项目/方法关联", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出检测标准/项目/方法关联列表", notes = "导出检测标准/项目/方法关联列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConCheckStandardItemMethod conCheckStandardItemMethod) throws IOException {
        List<ConCheckStandardItemMethod> list = conCheckStandardItemMethodService.selectConCheckStandardItemMethodList(conCheckStandardItemMethod);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConCheckStandardItemMethod> util = new ExcelUtil<ConCheckStandardItemMethod>(ConCheckStandardItemMethod.class,dataMap);
        util.exportExcel(response, list, "检测标准项");
    }


    /**
     * 获取检测标准/项目/方法关联详细信息
     */
    @ApiOperation(value = "获取检测标准/项目/方法关联详细信息", notes = "获取检测标准/项目/方法关联详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConCheckStandardItemMethod.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long checkStandardItemMethodSid) {
                    if(checkStandardItemMethodSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conCheckStandardItemMethodService.selectConCheckStandardItemMethodById(checkStandardItemMethodSid));
    }

    /**
     * 新增检测标准/项目/方法关联
     */
    @ApiOperation(value = "新增检测标准/项目/方法关联", notes = "新增检测标准/项目/方法关联")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测标准/项目/方法关联", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConCheckStandardItemMethod conCheckStandardItemMethod) {
        return toAjax(conCheckStandardItemMethodService.insertConCheckStandardItemMethod(conCheckStandardItemMethod));
    }

    /**
     * 修改检测标准/项目/方法关联
     */
    @ApiOperation(value = "修改检测标准/项目/方法关联", notes = "修改检测标准/项目/方法关联")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "检测标准/项目/方法关联", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConCheckStandardItemMethod conCheckStandardItemMethod) {
        return toAjax(conCheckStandardItemMethodService.updateConCheckStandardItemMethod(conCheckStandardItemMethod));
    }

    /**
     * 变更检测标准/项目/方法关联
     */
    @ApiOperation(value = "变更检测标准/项目/方法关联", notes = "变更检测标准/项目/方法关联")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:method:change")
    @Log(title = "检测标准/项目/方法关联", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody ConCheckStandardItemMethod conCheckStandardItemMethod) {
        return toAjax(conCheckStandardItemMethodService.changeConCheckStandardItemMethod(conCheckStandardItemMethod));
    }

    /**
     * 删除检测标准/项目/方法关联
     */
    @ApiOperation(value = "删除检测标准/项目/方法关联", notes = "删除检测标准/项目/方法关联")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "检测标准/项目/方法关联", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  checkStandardItemMethodSids) {
        if(CollectionUtils.isEmpty( checkStandardItemMethodSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conCheckStandardItemMethodService.deleteConCheckStandardItemMethodByIds(checkStandardItemMethodSids));
    }

}
