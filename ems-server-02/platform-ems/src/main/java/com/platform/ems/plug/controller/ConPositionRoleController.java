package com.platform.ems.plug.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConPositionRole;
import com.platform.ems.plug.service.IConPositionRoleService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 工作角色Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/position/role")
@Api(tags = "工作角色")
public class ConPositionRoleController extends BaseController {

    @Autowired
    private IConPositionRoleService conPositionRoleService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询工作角色列表
     */
    @PreAuthorize(hasPermi = "ems:positionRole:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询工作角色列表", notes = "查询工作角色列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPositionRole.class))
    public TableDataInfo list(@RequestBody ConPositionRole conPositionRole) {
        startPage(conPositionRole);
        List<ConPositionRole> list = conPositionRoleService.selectConPositionRoleList(conPositionRole);
        return getDataTable(list);
    }

    /**
     * 导出工作角色列表
     */
    @PreAuthorize(hasPermi = "ems:positionRole:export")
    @Log(title = "工作角色", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出工作角色列表", notes = "导出工作角色列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConPositionRole conPositionRole) throws IOException {
        List<ConPositionRole> list = conPositionRoleService.selectConPositionRoleList(conPositionRole);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConPositionRole> util = new ExcelUtil<ConPositionRole>(ConPositionRole.class, dataMap);
        util.exportExcel(response, list, "工作角色" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入工作角色
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入工作角色", notes = "导入工作角色")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConPositionRole> util = new ExcelUtil<ConPositionRole>(ConPositionRole.class);
        List<ConPositionRole> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conPositionRole -> {
                conPositionRoleService.insertConPositionRole(conPositionRole);
                i++;
            });
        } catch (Exception e) {
            lose = listSize - i;
            msg = StrUtil.format("前{}条数据导入成功，失败{}条,导入成功的数据请勿重复导入", i, lose);
        }
        if (StrUtil.isEmpty(msg)) {
            msg = "导入成功";
        }
        return AjaxResult.success(msg);
    }


    @ApiOperation(value = "下载工作角色导入模板", notes = "下载工作角色导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConPositionRole> util = new ExcelUtil<ConPositionRole>(ConPositionRole.class);
        util.importTemplateExcel(response, "工作角色导入模板");
    }


    /**
     * 获取工作角色详细信息
     */
    @ApiOperation(value = "获取工作角色详细信息", notes = "获取工作角色详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPositionRole.class))
    @PreAuthorize(hasPermi = "ems:positionRole:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conPositionRoleService.selectConPositionRoleById(sid));
    }

    /**
     * 新增工作角色
     */
    @ApiOperation(value = "新增工作角色", notes = "新增工作角色")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:positionRole:add")
    @Log(title = "工作角色", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConPositionRole conPositionRole) {
        return toAjax(conPositionRoleService.insertConPositionRole(conPositionRole));
    }

    /**
     * 修改工作角色
     */
    @ApiOperation(value = "修改工作角色", notes = "修改工作角色")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:positionRole:edit")
    @Log(title = "工作角色", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConPositionRole conPositionRole) {
        return toAjax(conPositionRoleService.updateConPositionRole(conPositionRole));
    }

    /**
     * 变更工作角色
     */
    @ApiOperation(value = "变更工作角色", notes = "变更工作角色")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:positionRole:change")
    @Log(title = "工作角色", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConPositionRole conPositionRole) {
        return toAjax(conPositionRoleService.changeConPositionRole(conPositionRole));
    }

    /**
     * 删除工作角色
     */
    @ApiOperation(value = "删除工作角色", notes = "删除工作角色")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:positionRole:remove")
    @Log(title = "工作角色", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conPositionRoleService.deleteConPositionRoleByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "工作角色", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:positionRole:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConPositionRole conPositionRole) {
        return AjaxResult.success(conPositionRoleService.changeStatus(conPositionRole));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:positionRole:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "工作角色", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConPositionRole conPositionRole) {
        conPositionRole.setConfirmDate(new Date());
        conPositionRole.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conPositionRole.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conPositionRoleService.check(conPositionRole));
    }

}
