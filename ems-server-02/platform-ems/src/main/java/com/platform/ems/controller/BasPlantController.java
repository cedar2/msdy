package com.platform.ems.controller;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollectionUtil;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasStaff;
import com.platform.ems.domain.PayProductProcessStep;
import com.platform.ems.service.IBasStaffService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.api.service.RemoteSystemService;
import com.platform.common.core.domain.entity.SysRole;
import com.platform.system.domain.SysRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import com.platform.ems.domain.BasPlant;
import com.platform.ems.service.IBasPlantService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;

/**
 * 工厂档案Controller
 *
 * @author linhongwei
 * @date 2021-03-15
 */
@RestController
@RequestMapping("/plant")
@Api(tags = "工厂档案")
public class BasPlantController extends BaseController {

    @Autowired
    private IBasPlantService basPlantService;
    @Autowired
    private IBasStaffService basStaffService;
    @Autowired
    private RemoteSystemService remoteSystemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询工厂档案列表
     */
    @PreAuthorize(hasPermi = "ems:plant:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询工厂档案列表", notes = "查询工厂档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPlant.class))
    public TableDataInfo list(@RequestBody BasPlant basPlant) {
        Long[] roleIds = null;
        List<SysRole> roleList = ApiThreadLocalUtil.get().getSysUser().getRoles();
        if (CollectionUtil.isNotEmpty(roleList)){
            roleIds = roleList.stream().map(SysRole::getRoleId).toArray(Long[]::new);
        }
        SysRoleMenu roleMenu = new SysRoleMenu();
        roleMenu.setRoleIds(roleIds);
        roleMenu.setPerms("ems:plant:all");
        boolean isAll = true;
        if (!"10000".equals(ApiThreadLocalUtil.get().getClientId())){
            isAll = remoteSystemService.isHavePerms(roleMenu).getData();
        }
        if (!isAll){
            Long staffSid = ApiThreadLocalUtil.get().getSysUser().getStaffSid();
            if (staffSid != null){
                BasStaff staff = basStaffService.selectBasStaffById(staffSid);
                if (staff.getDefaultPlantSid() != null){
                    basPlant.setPlantSid(staff.getDefaultPlantSid().toString());
                    startPage(basPlant);
                    List<BasPlant> list = basPlantService.selectBasPlantList(basPlant);
                    return getDataTable(list);
                }
            }
            return getDataTable(new ArrayList<>());
        }
        else {
            startPage(basPlant);
            List<BasPlant> list = basPlantService.selectBasPlantList(basPlant);
            return getDataTable(list);
        }
    }

    /**
     * 导出工厂档案列表
     */
    @PreAuthorize(hasPermi = "ems:plant:export")
    @Log(title = "工厂档案", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出工厂档案列表", notes = "导出工厂档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasPlant basPlant) throws IOException {
        List<BasPlant> list = basPlantService.selectBasPlantList(basPlant);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasPlant> util = new ExcelUtil<>(BasPlant.class, dataMap);
        util.exportExcel(response, list, "工厂");
    }

    /**
     * 获取工厂档案详细信息
     */
    @ApiOperation(value = "获取工厂档案详细信息", notes = "获取工厂档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPlant.class))
    @PreAuthorize(hasPermi = "ems:plant:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long plantSid) {
        return AjaxResult.success(basPlantService.selectBasPlantById(plantSid));
    }

    /**
     * 新增工厂档案
     */
    @ApiOperation(value = "新增工厂档案", notes = "新增工厂档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:plant:add")
    @Log(title = "工厂档案", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasPlant basPlant) {
        int row = basPlantService.insertBasPlant(basPlant);
        return AjaxResult.success(basPlant);
    }

    /**
     * 修改工厂档案
     */
    @ApiOperation(value = "修改工厂档案", notes = "修改工厂档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:plant:edit")
    @Log(title = "工厂档案", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasPlant basPlant) {
        return toAjax(basPlantService.updateBasPlant(basPlant));
    }

    /**
     * 删除工厂档案
     */
    @ApiOperation(value = "删除工厂档案", notes = "删除工厂档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:plant:remove")
    @Log(title = "工厂档案", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody String[] plantSids) {
        return toAjax(basPlantService.deleteBasPlantByIds(plantSids));
    }

    /**
     * 工厂档案确认
     */
    @PreAuthorize(hasPermi = "ems:plant:check")
    @Log(title = "工厂档案", businessType = BusinessType.CHECK)
    @PostMapping("/confirm")
    @ApiOperation(value = "工厂档案确认", notes = "工厂档案确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult confirm(@RequestBody BasPlant basPlant) {
        return AjaxResult.success(basPlantService.confirm(basPlant));
    }

    /**
     * 工厂档案变更
     */
    @PreAuthorize(hasPermi = "ems:plant:change")
    @Log(title = "工厂档案", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    @ApiOperation(value = "工厂档案变更", notes = "工厂档案变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody BasPlant basPlant) {
        return AjaxResult.success(basPlantService.change(basPlant));
    }

    /**
     * 批量启用/停用工厂档案
     */
    @PreAuthorize(hasPermi = "ems:plant:enableordisable")
    @Log(title = "工厂档案", businessType = BusinessType.ENBLEORDISABLE)
    @PostMapping("/status")
    @ApiOperation(value = "工厂档案启用/停用", notes = "工厂档案启用/停用")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult status(@RequestBody BasPlant basPlant) {
        return AjaxResult.success(basPlantService.status(basPlant));
    }

    /**
     * 工厂档案下拉框列表
     */
    @PostMapping("/getPlantList")
    @ApiOperation(value = "工厂档案下拉框列表", notes = "工厂档案下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPlant.class))
    public AjaxResult getPlantList() {
        Long[] roleIds = null;
        List<SysRole> roleList = ApiThreadLocalUtil.get().getSysUser().getRoles();
        if (CollectionUtil.isNotEmpty(roleList)){
            roleIds = roleList.stream().map(SysRole::getRoleId).toArray(Long[]::new);
        }
        SysRoleMenu roleMenu = new SysRoleMenu();
        roleMenu.setRoleIds(roleIds);
        roleMenu.setPerms("ems:plant:all");
        boolean isAll = true;
        if (!"10000".equals(ApiThreadLocalUtil.get().getClientId())){
            isAll = remoteSystemService.isHavePerms(roleMenu).getData();
        }
        if (!isAll) {
            Long staffSid = ApiThreadLocalUtil.get().getSysUser().getStaffSid();
            if (staffSid != null) {
                BasStaff staff = basStaffService.selectBasStaffById(staffSid);
                if (staff.getDefaultPlantSid() != null) {
                    return AjaxResult.success(basPlantService.getPlantList(new BasPlant().setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.CHECK_STATUS)
                            .setPlantSid(staff.getDefaultPlantSid().toString())));
                }
            }
            return AjaxResult.success(new ArrayList<BasPlant>());
        }
        else {
            return AjaxResult.success(basPlantService.getPlantList(new BasPlant().setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.CHECK_STATUS)));
        }
    }


    /**
     * 工厂档案下拉框列表(带参数)
     */
    @PostMapping("/getList")
    @ApiOperation(value = "工厂档案下拉框列表-带参数", notes = "工厂档案下拉框列表-带参数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPlant.class))
    public AjaxResult getList(@RequestBody BasPlant basPlant) {
        Long[] roleIds = null;
        List<SysRole> roleList = ApiThreadLocalUtil.get().getSysUser().getRoles();
        if (CollectionUtil.isNotEmpty(roleList)){
            roleIds = roleList.stream().map(SysRole::getRoleId).toArray(Long[]::new);
        }
        SysRoleMenu roleMenu = new SysRoleMenu();
        roleMenu.setRoleIds(roleIds);
        roleMenu.setPerms("ems:plant:all");
        boolean isAll = true;
        if (!"10000".equals(ApiThreadLocalUtil.get().getClientId())){
            isAll = remoteSystemService.isHavePerms(roleMenu).getData();
        }
        if (!isAll){
            Long staffSid = ApiThreadLocalUtil.get().getSysUser().getStaffSid();
            if (staffSid != null){
                BasStaff staff = basStaffService.selectBasStaffById(staffSid);
                if (staff.getDefaultPlantSid() != null){
                    basPlant.setPlantSid(staff.getDefaultPlantSid().toString());
                    return AjaxResult.success(basPlantService.getPlantList(basPlant));
                }
            }
            return AjaxResult.success(new ArrayList<BasPlant>());
        }else {
            return AjaxResult.success(basPlantService.getPlantList(basPlant));
        }
    }

    /**
     * 获取工厂关联公司下所有部门
     */
    @PostMapping("/getDepartmentList")
    @ApiOperation(value = "获取工厂关联公司下所有部门", notes = "获取工厂关联公司下所有部门")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPlant.class))
    public AjaxResult getDepartmentList(@RequestBody BasPlant basPlant) {
        return AjaxResult.success(basPlantService.getDepartmentList(basPlant));
    }
}
