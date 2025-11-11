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
import com.platform.ems.plug.domain.ConReferenceProject;
import com.platform.ems.plug.service.IConReferenceProjectService;
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
 * 业务归属项目Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/reference/project")
@Api(tags = "业务归属项目")
public class ConReferenceProjectController extends BaseController {

    @Autowired
    private IConReferenceProjectService conReferenceProjectService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询业务归属项目列表
     */
    @PreAuthorize(hasPermi = "ems:project:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询业务归属项目列表", notes = "查询业务归属项目列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConReferenceProject.class))
    public TableDataInfo list(@RequestBody ConReferenceProject conReferenceProject) {
        startPage(conReferenceProject);
        List<ConReferenceProject> list = conReferenceProjectService.selectConReferenceProjectList(conReferenceProject);
        return getDataTable(list);
    }

    /**
     * 导出业务归属项目列表
     */
    @PreAuthorize(hasPermi = "ems:project:export")
    @Log(title = "业务归属项目", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出业务归属项目列表", notes = "导出业务归属项目列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConReferenceProject conReferenceProject) throws IOException {
        List<ConReferenceProject> list = conReferenceProjectService.selectConReferenceProjectList(conReferenceProject);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConReferenceProject> util = new ExcelUtil<ConReferenceProject>(ConReferenceProject.class, dataMap);
        util.exportExcel(response, list, "业务归属项目" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入业务归属项目
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入业务归属项目", notes = "导入业务归属项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConReferenceProject> util = new ExcelUtil<ConReferenceProject>(ConReferenceProject.class);
        List<ConReferenceProject> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conReferenceProject -> {
                conReferenceProjectService.insertConReferenceProject(conReferenceProject);
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


    @ApiOperation(value = "下载业务归属项目导入模板", notes = "下载业务归属项目导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConReferenceProject> util = new ExcelUtil<ConReferenceProject>(ConReferenceProject.class);
        util.importTemplateExcel(response, "业务归属项目导入模板");
    }


    /**
     * 获取业务归属项目详细信息
     */
    @ApiOperation(value = "获取业务归属项目详细信息", notes = "获取业务归属项目详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConReferenceProject.class))
    @PreAuthorize(hasPermi = "ems:project:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conReferenceProjectService.selectConReferenceProjectById(sid));
    }

    /**
     * 新增业务归属项目
     */
    @ApiOperation(value = "新增业务归属项目", notes = "新增业务归属项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:project:add")
    @Log(title = "业务归属项目", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConReferenceProject conReferenceProject) {
        return toAjax(conReferenceProjectService.insertConReferenceProject(conReferenceProject));
    }

    /**
     * 修改业务归属项目
     */
    @ApiOperation(value = "修改业务归属项目", notes = "修改业务归属项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:project:edit")
    @Log(title = "业务归属项目", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConReferenceProject conReferenceProject) {
        return toAjax(conReferenceProjectService.updateConReferenceProject(conReferenceProject));
    }

    /**
     * 变更业务归属项目
     */
    @ApiOperation(value = "变更业务归属项目", notes = "变更业务归属项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:project:change")
    @Log(title = "业务归属项目", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConReferenceProject conReferenceProject) {
        return toAjax(conReferenceProjectService.changeConReferenceProject(conReferenceProject));
    }

    /**
     * 删除业务归属项目
     */
    @ApiOperation(value = "删除业务归属项目", notes = "删除业务归属项目")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:project:remove")
    @Log(title = "业务归属项目", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conReferenceProjectService.deleteConReferenceProjectByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务归属项目", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:project:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConReferenceProject conReferenceProject) {
        return AjaxResult.success(conReferenceProjectService.changeStatus(conReferenceProject));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:project:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务归属项目", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConReferenceProject conReferenceProject) {
        conReferenceProject.setConfirmDate(new Date());
        conReferenceProject.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conReferenceProject.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conReferenceProjectService.check(conReferenceProject));
    }

}
