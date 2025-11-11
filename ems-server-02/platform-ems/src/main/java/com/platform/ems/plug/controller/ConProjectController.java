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
import com.platform.ems.plug.domain.ConProject;
import com.platform.ems.plug.service.IConProjectService;
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
 * 项目Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/project")
@Api(tags = "项目")
public class ConProjectController extends BaseController {

    @Autowired
    private IConProjectService conProjectService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询项目列表
     */
    @PreAuthorize(hasPermi = "ems:project:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询项目列表", notes = "查询项目列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConProject.class))
    public TableDataInfo list(@RequestBody ConProject conProject) {
        startPage(conProject);
        List<ConProject> list = conProjectService.selectConProjectList(conProject);
        return getDataTable(list);
    }

    /**
     * 导出项目列表
     */
    @PreAuthorize(hasPermi = "ems:project:export")
    @Log(title = "项目", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出项目列表", notes = "导出项目列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConProject conProject) throws IOException {
        List<ConProject> list = conProjectService.selectConProjectList(conProject);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConProject> util = new ExcelUtil<ConProject>(ConProject.class, dataMap);
        util.exportExcel(response, list, "项目" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入项目
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入项目", notes = "导入项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConProject> util = new ExcelUtil<ConProject>(ConProject.class);
        List<ConProject> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conProject -> {
                conProjectService.insertConProject(conProject);
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


    @ApiOperation(value = "下载项目导入模板", notes = "下载项目导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConProject> util = new ExcelUtil<ConProject>(ConProject.class);
        util.importTemplateExcel(response, "项目导入模板");
    }


    /**
     * 获取项目详细信息
     */
    @ApiOperation(value = "获取项目详细信息", notes = "获取项目详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConProject.class))
    @PreAuthorize(hasPermi = "ems:project:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conProjectService.selectConProjectById(sid));
    }

    /**
     * 新增项目
     */
    @ApiOperation(value = "新增项目", notes = "新增项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:project:add")
    @Log(title = "项目", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConProject conProject) {
        return toAjax(conProjectService.insertConProject(conProject));
    }

    /**
     * 修改项目
     */
    @ApiOperation(value = "修改项目", notes = "修改项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:project:edit")
    @Log(title = "项目", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConProject conProject) {
        return toAjax(conProjectService.updateConProject(conProject));
    }

    /**
     * 变更项目
     */
    @ApiOperation(value = "变更项目", notes = "变更项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:project:change")
    @Log(title = "项目", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConProject conProject) {
        return toAjax(conProjectService.changeConProject(conProject));
    }

    /**
     * 删除项目
     */
    @ApiOperation(value = "删除项目", notes = "删除项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:project:remove")
    @Log(title = "项目", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conProjectService.deleteConProjectByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "项目", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:project:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConProject conProject) {
        return AjaxResult.success(conProjectService.changeStatus(conProject));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:project:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "项目", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConProject conProject) {
        conProject.setConfirmDate(new Date());
        conProject.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conProject.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conProjectService.check(conProject));
    }

}
