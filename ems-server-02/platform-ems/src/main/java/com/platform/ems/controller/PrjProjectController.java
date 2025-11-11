package com.platform.ems.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.constant.HttpStatus;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.annotation.CreatorScope;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.annotation.RoleDataScope;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsAuthorize;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.form.PrjProjectTaskFormRequest;
import com.platform.ems.domain.dto.response.form.*;
import com.platform.ems.mapper.BasPositionMapper;
import com.platform.ems.mapper.PrjProjectMapper;
import com.platform.ems.mapper.PrjProjectTaskMapper;
import com.platform.ems.service.IPrjProjectImportService;
import com.platform.ems.service.IPrjProjectTaskService;
import com.platform.ems.task.SalSaleOrderWarningTask;
import com.platform.ems.task.WorkBench;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.service.IPrjProjectService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 项目档案Controller
 *
 * @author chenkw
 * @date 2022-12-08
 */
@RestController
@RequestMapping("/prj/project")
@Api(tags = "项目档案")
public class PrjProjectController extends BaseController {

    @Autowired
    private IPrjProjectService prjProjectService;
    @Autowired
    private PrjProjectMapper prjProjectMapper;
    @Autowired
    private IPrjProjectTaskService prjProjectTaskService;
    @Autowired
    private IPrjProjectImportService importService;
    @Autowired
    private PrjProjectTaskMapper prjProjectTaskMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private BasPositionMapper basPositionMapper;

    @Autowired
    private MinioClient client;
    @Autowired
    private MinioConfig minioConfig;

    private static final String FILLE_PATH = "/template";

    @Autowired
    private SalSaleOrderWarningTask task;
    @Autowired
    private WorkBench workBench;

    /**
     * 查询项目档案列表
     */
    @PostMapping("/list")
    @RoleDataScope(objectCode = "PrjProject")
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL)
    @ApiOperation(value = "查询项目档案列表", notes = "查询项目档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjProject.class))
    public TableDataInfo list(@RequestBody PrjProject prjProject) {
        startPage(prjProject);
        List<PrjProject> list = prjProjectService.selectPrjProjectList(prjProject);
        TableDataInfo rspData = getDataTable(list);
        // 预警灯
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(item->{
                if (!ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus())) {
                    return;
                }
                prjProjectService.setLight(item);
            });
        }
        return rspData;
    }

    /**
     * 导出项目档案列表
     */
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL, loc = 1)
    @Log(title = "项目档案", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出项目档案列表", notes = "导出项目档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PrjProject prjProject) throws IOException {
        List<PrjProject> list = prjProjectService.selectPrjProjectList(prjProject);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PrjProject> util = new ExcelUtil<>(PrjProject.class, dataMap);
        util.exportExcel(response, list, "项目档案");
    }

    /**
     * 获取项目档案详细信息
     */
    @ApiOperation(value = "获取项目档案详细信息", notes = "获取项目档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjProject.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long projectSid) {
        if (projectSid == null) {
            throw new CheckedException("参数缺失");
        }
        PrjProject prjProject = prjProjectService.selectPrjProjectById(projectSid);
        if (prjProject != null && prjProject.getPreProjectSid() != null) {
            // 前置项目
            List<PrjProject> preProjectList = new ArrayList<>();
            PrjProject preProject = prjProjectService.selectPrjProjectById(prjProject.getPreProjectSid());
            if (preProject != null) {
                preProjectList.add(preProject);
            }
            prjProject.setPreProjectList(preProjectList);
        }
        return AjaxResult.success(prjProject);
    }

    /**
     * 复制项目档案详细信息
     */
    @ApiOperation(value = "复制项目档案详细信息", notes = "复制项目档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjProject.class))
    @PostMapping("/copy")
    public AjaxResult copy(Long projectSid) {
        if (projectSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(prjProjectService.copyPrjProjectById(projectSid));
    }

    /**
     * 新增项目档案
     */
    @ApiOperation(value = "新增项目档案", notes = "新增项目档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "项目档案", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid PrjProject prjProject) {
        int row = prjProjectService.insertPrjProject(prjProject);
        if (row > 0) {
            return AjaxResult.success(prjProjectService.selectPrjProjectById(prjProject.getProjectSid()));
        } else {
            return toAjax(row);
        }
    }
    /**
     * 新增项目档案
     */
    @ApiOperation(value = "批量新增项目档案", notes = "批量新增项目档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "项目档案", businessType = BusinessType.INSERT)
    @PostMapping("/addList")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult addList(@RequestBody List<PrjProject> prjProjectList) {
        int row = 0;
        if (CollectionUtil.isNotEmpty(prjProjectList)) {
            for (PrjProject project : prjProjectList) {
                row += prjProjectService.insertPrjProject(project);
            }
        }
        return toAjax(row);
    }

    @ApiOperation(value = "修改项目档案", notes = "修改项目档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "项目档案", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody @Valid  PrjProject prjProject) {
        return toAjax(prjProjectService.updatePrjProject(prjProject));
    }

    /**
     * 变更项目档案
     */
    @ApiOperation(value = "变更项目档案", notes = "变更项目档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "项目档案", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid PrjProject prjProject) {
        return toAjax(prjProjectService.changePrjProject(prjProject));
    }

    /**
     * 删除项目档案
     */
    @ApiOperation(value = "删除项目档案", notes = "删除项目档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "项目档案", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> projectSids) {
        if (CollectionUtils.isEmpty(projectSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(prjProjectService.deletePrjProjectByIds(projectSids));
    }

    /**
     * 修改项目档案处理状态（确认）
     */
    @ApiOperation(value = "查询页面的确认", notes = "查询页面的确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "项目档案", businessType = BusinessType.HANDLE)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody PrjProject prjProject) {
        if (ArrayUtil.isEmpty(prjProject.getProjectSidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(prjProject.getHandleStatus())) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(prjProjectService.check(prjProject));
    }

    /**
     * 设置项目状态
     */
    @ApiOperation(value = "设置项目状态", notes = "设置项目状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setProjectStatus")
    public AjaxResult setProjectStatus(@RequestBody PrjProject prjProject) {
        if (ArrayUtil.isEmpty(prjProject.getProjectSidList())) {
            throw new CheckedException("请勾选行");
        }
        return toAjax(prjProjectService.setProjectStatus(prjProject));
    }

    /**
     * 设置项目优先级按钮
     */
    @ApiOperation(value = "设置项目优先级", notes = "设置项目优先级")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setPriority")
    public AjaxResult setPriority(@RequestBody PrjProject prjProject) {
        if (ArrayUtil.isEmpty(prjProject.getProjectSidList())) {
            throw new CheckedException("请勾选行");
        }
        return toAjax(prjProjectService.setPriority(prjProject));
    }

    /**
     * 设置即将到期提醒天数
     */
    @ApiOperation(value = "设置即将到期提醒天数", notes = "设置即将到期提醒天数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setToexpireDays")
    public AjaxResult setToexpireDays(@RequestBody PrjProject prjProject) {
        return toAjax(prjProjectService.setToexpireDays(prjProject));
    }

    /**
     * 设置开发计划
     */
    @ApiOperation(value = "设置开发计划", notes = "设置开发计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setDevelopPlan")
    public AjaxResult setDevelopPlan(@RequestBody PrjProject prjProject) {
        return toAjax(prjProjectService.setDevelopPlan(prjProject));
    }

    /**
     * 设置商品款号/SPU号
     */
    @ApiOperation(value = "设置商品款号/SPU号", notes = "设置商品款号/SPU号")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setProduct")
    public AjaxResult setProduct(@RequestBody PrjProject prjProject) {
        if (prjProject.getProjectSid() == null) {
            throw new CheckedException("请勾选行");
        }
        return toAjax(prjProjectService.setProduct(prjProject));
    }

    /**
     * 设置商品SKU号
     */
    @ApiOperation(value = "设置商品SKU号", notes = "设置商品SKU号")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setBarcode")
    public AjaxResult setBarcode(@RequestBody PrjProject prjProject) {
        if (prjProject.getProjectSid() == null) {
            throw new CheckedException("请勾选行");
        }
        return toAjax(prjProjectService.setMaterialBarcode(prjProject));
    }

    /**
     * 设置计划日期
     */
    @ApiOperation(value = "设置计划日期", notes = "设置计划日期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setPlanDate")
    public AjaxResult setPlanDate(@RequestBody PrjProject prjProject) {
        if (ArrayUtil.isEmpty(prjProject.getProjectSidList())) {
            throw new CheckedException("请勾选行");
        }
        return toAjax(prjProjectService.setPlanDate(prjProject));
    }

    /**
     * 设置实际完成日期
     */
    @ApiOperation(value = "设置实际完成日期", notes = "设置实际完成日期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setActualEndDate")
    public AjaxResult setActualEndDate(@RequestBody PrjProject prjProject) {
        if (ArrayUtil.isEmpty(prjProject.getProjectSidList())) {
            throw new CheckedException("请勾选行");
        }
        return toAjax(prjProjectService.setActualEndDate(prjProject));
    }

    /**
     * 查询页面开始执行的按钮
     */
    @ApiOperation(value = "查询页面开始执行的按钮", notes = "查询页面开始执行的按钮")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/startTask")
    public AjaxResult startTask(@RequestBody PrjProject prjProject) {
        int row = 0;
        if (prjProject.getProjectSid() == null && ArrayUtil.isNotEmpty(prjProject.getProjectSidList())) {
            for (int i = 0; i < prjProject.getProjectSidList().length; i++) {
                prjProject.setProjectSid(prjProject.getProjectSidList()[i]);
                row += prjProjectService.startTask(prjProject);
            }
        }
        if (prjProject.getProjectSid() != null) {
            row = prjProjectService.startTask(prjProject);
        }
        return toAjax(row);
    }

    /**
     * 查询页面跳转其它单据
     */
    @ApiOperation(value = "查询页面跳转其它单据", notes = "查询页面跳转其它单据")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/jumpTo")
    public AjaxResult jumpTo(@RequestBody PrjProject prjProject) {
        return AjaxResult.success(prjProjectService.jumpTo(prjProject));
    }

    /**
     * 查询项目任务明细报表
     */
    @PostMapping("/task/form")
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL)
    @ApiOperation(value = "查询项目任务明细报表", notes = "查询项目任务明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjProject.class))
    public TableDataInfo taskForm(@RequestBody PrjProjectTaskFormRequest prjProject) {
        startPage(prjProject);
        List<PrjProjectTaskFormResponse> list = prjProjectTaskService.selectPrjProjectTaskForm(prjProject);
        TableDataInfo response = getDataTable(list);
        for (int i = 0; i < list.size(); i++) {
            // 岗位名称
            setPositionName(list.get(i));
        }
        return response;
    }

    private void setPositionName(PrjProjectTaskFormResponse task) {
        // 前置任务节点
        if (StrUtil.isNotBlank(task.getPreTask())) {
            String[] preTaskList = task.getPreTask().split(";");
            task.setPreTaskList(preTaskList);
        }
        // 发起岗位
        if (StrUtil.isNotBlank(task.getStartPositionCode())) {
            String[] starts = task.getStartPositionCode().split(";");
            task.setStartPositionCodeList(starts);
            List<BasPosition> startList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                    .lambda().in(BasPosition::getPositionCode, starts));
            if (ArrayUtil.isNotEmpty(startList)) {
                String startName = "";
                for (int i = 0; i < startList.size(); i++) {
                    startName = startName + startList.get(i).getPositionName() + ";";
                }
                task.setStartPositionName(startName);
            }
        }
        // 负责岗位
        if (StrUtil.isNotBlank(task.getChargePositionCode())) {
            String[] charges = task.getChargePositionCode().split(";");
            task.setChargePositionCodeList(charges);
            List<BasPosition> chargeList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                    .lambda().in(BasPosition::getPositionCode, charges));
            if (ArrayUtil.isNotEmpty(chargeList)) {
                String chargeName = "";
                for (int i = 0; i < chargeList.size(); i++) {
                    chargeName = chargeName + chargeList.get(i).getPositionName() + ";";
                }
                task.setChargePositionName(chargeName);
            }
        }
        // 告知岗位
        if (StrUtil.isNotBlank(task.getNoticePositionCode())) {
            String[] notices = task.getNoticePositionCode().split(";");
            task.setNoticePositionCodeList(notices);
            List<BasPosition> noticeList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                    .lambda().in(BasPosition::getPositionCode, notices));
            if (ArrayUtil.isNotEmpty(noticeList)) {
                String noticeName = "";
                for (int i = 0; i < noticeList.size(); i++) {
                    noticeName = noticeName + noticeList.get(i).getPositionName() + ";";
                }
                task.setNoticePositionName(noticeName);
            }
        }
        // 处理人
        if (StrUtil.isNotBlank(task.getHandlerTask())) {
            String[] handler = task.getHandlerTask().split(";");
            task.setHandlerTaskList(handler);
        }
        // 图片
        if (StrUtil.isNotBlank(task.getPicturePath())) {
            String[] picture = task.getPicturePath().split(";");
            task.setPicturePathList(picture);
        }
    }

    /**
     * 导出项目任务明细报表
     */
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL, loc = 1)
    @ApiOperation(value = "导出项目任务明细报表", notes = "导出项目任务明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/task/form/export")
    public void export(HttpServletResponse response, PrjProjectTaskFormRequest prjProject) throws IOException {
        List<PrjProjectTaskFormResponse> list = prjProjectTaskService.selectPrjProjectTaskForm(prjProject);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PrjProjectTaskFormResponse> util = new ExcelUtil<>(PrjProjectTaskFormResponse.class, dataMap);
        util.exportExcel(response, list, "项目任务明细报表");
    }

    /**
     * 获取项目档案任务明细详细信息
     */
    @ApiOperation(value = "获取项目档案任务明细详细信息", notes = "获取项目档案任务明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjProject.class))
    @PostMapping("/task/getInfo")
    public AjaxResult getTaskInfo(Long projectTaskSid) {
        if (projectTaskSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(prjProjectTaskService.selectPrjProjectTaskById(projectTaskSid));
    }

    /**
     * 项目任务明细报表分配任务处理人
     */
    @ApiOperation(value = "项目任务明细报表分配任务处理人", notes = "项目任务明细报表分配任务处理人")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/task/setHandler")
    public AjaxResult setTaskHandler(@RequestBody PrjProjectTask prjProjectTask) {
        if (ArrayUtil.isEmpty(prjProjectTask.getProjectTaskSidList())) {
            throw new BaseException("请选择行");
        }
        return AjaxResult.success(prjProjectTaskService.setTaskHandler(prjProjectTask));
    }

    /**
     * 设置项目任务优先级
     */
    @ApiOperation(value = "设置项目任务优先级", notes = "设置项目任务优先级")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/task/setPriority")
    public AjaxResult setTaskPriority(@RequestBody PrjProjectTask prjProjectTask) {
        if (ArrayUtil.isEmpty(prjProjectTask.getProjectTaskSidList())) {
            throw new CheckedException("请勾选行");
        }
        return toAjax(prjProjectTaskService.setTaskPriority(prjProjectTask));
    }

    /**
     * 查询项目前置任务完成状况报表
     */
    @PostMapping("/task/preCondition")
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL)
    @ApiOperation(value = "查询项目前置任务完成状况报表", notes = "查询项目前置任务完成状况报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjProjectTaskPreCondition.class))
    public TableDataInfo preCondition(@RequestBody PrjProjectTaskPreCondition prjProject) {
        startPage(prjProject);
        List<PrjProjectTaskPreCondition> list = prjProjectTaskService.selectPrjProjectTaskPreCondition(prjProject);
        TableDataInfo response = getDataTable(list);
        preConditionSetData(list);
        return response;
    }

    /**
     * 导出项目前置任务完成状况报表
     */
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL, loc = 1)
    @ApiOperation(value = "导出项目前置任务完成状况报表", notes = "导出项目前置任务完成状况报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/task/preCondition/export")
    public void export(HttpServletResponse response, PrjProjectTaskPreCondition prjProject) throws IOException {
        List<PrjProjectTaskPreCondition> list = prjProjectTaskService.selectPrjProjectTaskPreCondition(prjProject);
        preConditionSetData(list);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PrjProjectTaskPreCondition> util = new ExcelUtil<>(PrjProjectTaskPreCondition.class, dataMap);
        util.exportExcel(response, list, "前置任务完成状况报表");
    }

    /**
     * 项目前置任务完成状况报表部分字段数据设置
     */
    private void preConditionSetData(List<PrjProjectTaskPreCondition> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(task-> {
                // 发起岗位
                if (StrUtil.isNotBlank(task.getPreStartPositionCode())) {
                    String[] starts = task.getPreStartPositionCode().split(";");
                    List<BasPosition> startList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                            .lambda().in(BasPosition::getPositionCode, starts));
                    if (ArrayUtil.isNotEmpty(startList)) {
                        String startName = "";
                        for (int i = 0; i < startList.size(); i++) {
                            startName = startName + startList.get(i).getPositionName() + ";";
                        }
                        task.setPreStartPositionName(startName);
                    }
                }
                // 负责岗位
                if (StrUtil.isNotBlank(task.getPreChargePositionCode())) {
                    String[] charges = task.getPreChargePositionCode().split(";");
                    List<BasPosition> chargeList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                            .lambda().in(BasPosition::getPositionCode, charges));
                    if (ArrayUtil.isNotEmpty(chargeList)) {
                        String chargeName = "";
                        for (int i = 0; i < chargeList.size(); i++) {
                            chargeName = chargeName + chargeList.get(i).getPositionName() + ";";
                        }
                        task.setPreChargePositionName(chargeName);
                    }
                }
                // 告知岗位
                if (StrUtil.isNotBlank(task.getPreNoticePositionCode())) {
                    String[] notices = task.getPreNoticePositionCode().split(";");
                    List<BasPosition> noticeList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                            .lambda().in(BasPosition::getPositionCode, notices));
                    if (ArrayUtil.isNotEmpty(noticeList)) {
                        String noticeName = "";
                        for (int i = 0; i < noticeList.size(); i++) {
                            noticeName = noticeName + noticeList.get(i).getPositionName() + ";";
                        }
                        task.setPreNoticePositionName(noticeName);
                    }
                }
                // 预警灯
                if (task.getPlanEndDate() != null) {
                    LocalDate ldt1 = task.getPlanStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate localDate = LocalDate.now();
                    if (ldt1.isBefore(localDate)) {
                        task.setLight("0");
                    }
                }
            });
        }
    }

    /**
     * 查询试销站点执行状况报表报表
     */
    @PostMapping("/execute/condition")
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL)
    @ApiOperation(value = "查询试销站点执行状况报表报表", notes = "查询试销站点执行状况报表报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjProjectExecuteCondition.class))
    public TableDataInfo execute(@RequestBody PrjProjectExecuteCondition prjProject) {
        TableDataInfo response = new TableDataInfo();
        response.setCode(HttpStatus.SUCCESS);
        response.setMsg("查询成功");
        response.setTotal(0);
        response.setRows(new ArrayList<>());
        Integer pageNum = prjProject.getPageNum();
        prjProject.setPageNum(null);
        List<PrjProjectExecuteCondition> total = prjProjectService.selectPrjProjectExecuteCondition(prjProject);
        if (CollectionUtil.isNotEmpty(total)) {
            prjProject.setPageNum(pageNum);
            List<PrjProjectExecuteCondition> list = prjProjectService.selectPrjProjectExecuteCondition(prjProject);
            response.setRows(list);
            response.setTotal(total.size());
        }
        return response;
    }

    /**
     * 导出试销站点执行状况报表
     */
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL, loc = 1)
    @ApiOperation(value = "导出试销站点执行状况报表", notes = "导出试销站点执行状况报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/execute/condition/export")
    public void executeExport(HttpServletResponse response, PrjProjectExecuteCondition prjProject) throws IOException {
        List<PrjProjectExecuteCondition> list = prjProjectService.selectPrjProjectExecuteCondition(prjProject);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PrjProjectExecuteCondition> util = new ExcelUtil<>(PrjProjectExecuteCondition.class, dataMap);
        util.exportExcel(response, list, "试销站点执行状况报表");
    }

    /**
     * 按钮设置采购状态
     */
    @ApiOperation(value = "按钮设置采购状态", notes = "按钮设置采购状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "项目档案采购状态", businessType = BusinessType.UPDATE)
    @PostMapping("/update/purchaseFlag")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult updatePurchaseFlag(@RequestBody PrjProject project) {
        return toAjax(prjProjectService.updatePurchaseFlag(project));
    }

    @ApiOperation(value = "项目任务明细报表中修改项目任务明细", notes = "项目任务明细报表中修改项目任务明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "项目任务明细报表中修改项目任务明细", businessType = BusinessType.UPDATE)
    @PostMapping("/task/update")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult taskUpdate(@RequestBody @Valid PrjProjectTask prjProjectTask) {
        int row = 0;
        if (ConstantsEms.CHECK_STATUS.equals(prjProjectTask.getHandleStatus())) {
            row = prjProjectTaskService.changePrjProjectTask(prjProjectTask);
        } else {
            row = prjProjectTaskService.updatePrjProjectTask(prjProjectTask);
        }
        return toAjax(row);
    }

    /**
     * 项目任务明细报表设置即将到期提醒天数
     */
    @ApiOperation(value = "项目任务明细报表设置即将到期提醒天数", notes = "项目任务明细报表设置即将到期提醒天数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/task/setToexpireDays")
    public AjaxResult setTaskToexpireDays(@RequestBody PrjProjectTask prjProjectTask) {
        return toAjax(prjProjectTaskService.setToexpireDays(prjProjectTask));
    }

    /**
     * 项目任务执行提醒天数
     */
    @ApiOperation(value = "项目任务执行提醒天数", notes = "项目任务执行提醒天数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/task/setToexecuteNoticeDays")
    public AjaxResult setToexecuteNoticeDays(@RequestBody PrjProjectTask prjProjectTask) {
        return toAjax(prjProjectTaskService.setToexpireNoticeDays(prjProjectTask));
    }

    /**
     * 设置任务状态
     */
    @ApiOperation(value = "设置任务状态", notes = "设置任务状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/task/setTaskStatus")
    public AjaxResult setTaskStatus(@RequestBody PrjProjectTask prjProjectTask) {
        if (ArrayUtil.isEmpty(prjProjectTask.getProjectTaskSidList())) {
            throw new CheckedException("请勾选行");
        }
        return AjaxResult.success(prjProjectTaskService.setTaskStatus(prjProjectTask));
    }

    /**
     * 设置计划日期
     */
    @ApiOperation(value = "设置计划日期", notes = "设置计划日期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/task/setPlanDate")
    public AjaxResult setTaskPlanDate(@RequestBody PrjProjectTask prjProjectTask) {
        if (ArrayUtil.isEmpty(prjProjectTask.getProjectTaskSidList())) {
            throw new CheckedException("请勾选行");
        }
        return toAjax(prjProjectTaskService.setTaskPlanDate(prjProjectTask));
    }

    /**
     * 获取项目进度列表
     */
    @ApiOperation(value = "获取项目进度列表", notes = "获取项目进度列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjProject.class))
    @PostMapping("/process")
    public AjaxResult process(Long projectSid) {
        if (projectSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(prjProjectService.getPrjProjectProcessById(projectSid));
    }

    @ApiOperation(value = "测试", notes = "测试")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/test")
    public void test() throws IOException {
        workBench.updateCache();
        return;
    }

    @ApiOperation(value = "测试", notes = "测试")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/test2")
    public void test2() {
        workBench.sentMsgCache();
        return;
    }

    /**
     * 查询项目预警报表 已逾期
     */
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL)
    @PostMapping("/overDue")
    @ApiOperation(value = "查询项目预警报表 已逾期", notes = "查询项目预警报表 已逾期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjProject.class))
    public TableDataInfo overDue(@RequestBody PrjProject request) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setTotal(0);
        int pageNum = request.getPageNum();
        // 得到总数
        request.setPageNum(null);
        request.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<PrjProject> total = prjProjectMapper.getOverdueBusinessNoUser(request);
        rspData.setRows(total);
        if (CollectionUtils.isNotEmpty(total)) {
            // 得到分页后的数据
            request.setPageNum(pageNum);
            List<PrjProject> list = prjProjectMapper.getOverdueBusinessNoUser(request);
            rspData.setRows(list);
            rspData.setTotal(total.size());
        }
        return rspData;
    }

    /**
     * 导出项目预警报表 已逾期
     */
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL, loc = 1)
    @ApiOperation(value = "导出项目预警报表 已逾期", notes = "导出项目预警报表 已逾期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/overDue/export")
    public void overDueExport(HttpServletResponse response, PrjProject prjProject) throws IOException {
        prjProject.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<PrjProject> list = prjProjectMapper.getOverdueBusinessNoUser(prjProject);
        List<PrjProjectOverDueForm> export = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            export = BeanCopyUtils.copyListProperties(list, PrjProjectOverDueForm::new);
        }
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PrjProjectOverDueForm> util = new ExcelUtil<>(PrjProjectOverDueForm.class, dataMap);
        util.exportExcel(response, export, "项目预警报表已逾期");
    }

    /**
     * 查询项目预警报表 即将到期
     */
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL)
    @PostMapping("/toExpire")
    @ApiOperation(value = "查询项目预警报表 即将到期", notes = "查询项目预警报表 即将到期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjProject.class))
    public TableDataInfo toExpire(@RequestBody PrjProject request) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setTotal(0);
        int pageNum = request.getPageNum();
        // 得到总数
        request.setPageNum(null);
        request.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<PrjProject> total = prjProjectMapper.getToexpireBusinessNoUser(request, 15);
        rspData.setRows(total);
        if (CollectionUtils.isNotEmpty(total)) {
            // 得到分页后的数据
            request.setPageNum(pageNum);
            List<PrjProject> list = prjProjectMapper.getToexpireBusinessNoUser(request, 15);
            rspData.setRows(list);
            rspData.setTotal(total.size());
        }
        return rspData;
    }

    /**
     * 导出项目预警报表 即将到期
     */
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL, loc = 1)
    @ApiOperation(value = "导出项目预警报表 即将到期", notes = "导出项目预警报表 即将到期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/toExpire/export")
    public void toExpireExport(HttpServletResponse response, PrjProject prjProject) throws IOException {
        prjProject.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<PrjProject> list = prjProjectMapper.getToexpireBusinessNoUser(prjProject, 15);
        List<PrjProjectOverDueForm> export = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            export = BeanCopyUtils.copyListProperties(list, PrjProjectOverDueForm::new);
        }
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PrjProjectOverDueForm> util = new ExcelUtil<>(PrjProjectOverDueForm.class, dataMap);
        util.exportExcel(response, export, "项目预警报表即将到期");
    }

    /**
     * 查询项目任务预警报表 已逾期
     */
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL)
    @PostMapping("/task/overDue")
    @ApiOperation(value = "查询项目任务预警报表 已逾期", notes = "查询项目任务预警报表 已逾期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjProject.class))
    public TableDataInfo taskOverDue(@RequestBody PrjProjectTask request) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setTotal(0);
        int pageNum = request.getPageNum();
        // 得到总数
        request.setPageNum(null);
        request.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<PrjProjectTask> total = prjProjectTaskMapper.getOverdueBusiness(request);
        rspData.setRows(total);
        if (CollectionUtils.isNotEmpty(total)) {
            // 得到分页后的数据
            request.setPageNum(pageNum);
            List<PrjProjectTask> list = prjProjectTaskMapper.getOverdueBusiness(request);
            // 得到岗位
            list.forEach(item->{
                prjProjectTaskService.getPosition(item);
            });
            rspData.setRows(list);
            rspData.setTotal(total.size());
        }
        return rspData;
    }

    /**
     * 导出项目任务预警报表 已逾期
     */
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL, loc = 1)
    @ApiOperation(value = "导出项目任务预警报表 已逾期", notes = "导出项目任务预警报表 已逾期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/task/overDue/export")
    public void taskOverDueExport(HttpServletResponse response, PrjProjectTask request) throws IOException {
        request.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<PrjProjectTask> list = prjProjectTaskMapper.getOverdueBusiness(request);
        List<PrjProjectTaskOverDueForm> export = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            // 得到岗位
            list.forEach(item->{
                prjProjectTaskService.getPosition(item);
            });
            export = BeanCopyUtils.copyListProperties(list, PrjProjectTaskOverDueForm::new);
        }
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PrjProjectTaskOverDueForm> util = new ExcelUtil<>(PrjProjectTaskOverDueForm.class, dataMap);
        util.exportExcel(response, export, "项目任务预警报表已逾期");
    }

    /**
     * 查询项目任务预警报表 已逾期
     */
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL)
    @PostMapping("/task/toExpire")
    @ApiOperation(value = "查询项目任务预警报表 即将到期", notes = "查询项目任务预警报表 即将到期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjProject.class))
    public TableDataInfo taskToExpire(@RequestBody PrjProjectTask request) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setTotal(0);
        int pageNum = request.getPageNum();
        // 得到总数
        request.setPageNum(null);
        request.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<PrjProjectTask> total = prjProjectTaskMapper.getToexpireBusiness(request, 15);
        rspData.setRows(total);
        if (CollectionUtils.isNotEmpty(total)) {
            // 得到分页后的数据
            request.setPageNum(pageNum);
            List<PrjProjectTask> list = prjProjectTaskMapper.getToexpireBusiness(request, 15);
            // 得到岗位
            list.forEach(item->{
                prjProjectTaskService.getPosition(item);
            });
            rspData.setRows(list);
            rspData.setTotal(total.size());
        }
        return rspData;
    }

    /**
     * 导出项目任务预警报表 即将到期
     */
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL, loc = 1)
    @ApiOperation(value = "导出项目任务预警报表 即将到期", notes = "导出项目任务预警报表 即将到期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/task/toExpire/export")
    public void taskToExpireExport(HttpServletResponse response, PrjProjectTask prjProjectTask) throws IOException {
        prjProjectTask.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<PrjProjectTask> list = prjProjectTaskMapper.getToexpireBusiness(prjProjectTask, 15);
        List<PrjProjectTaskOverDueForm> export = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            // 得到岗位
            list.forEach(item->{
                prjProjectTaskService.getPosition(item);
            });
            export = BeanCopyUtils.copyListProperties(list, PrjProjectTaskOverDueForm::new);
        }
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PrjProjectTaskOverDueForm> util = new ExcelUtil<>(PrjProjectTaskOverDueForm.class, dataMap);
        util.exportExcel(response, export, "项目任务预警报表即将到期");
    }

    /**
     * 任务单据操作日志
     *
     */
    @ApiOperation(value = "任务单据操作日志", notes = "任务单据操作日志")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjProject.class))
    @PostMapping("/task/document/operMsg")
    public AjaxResult taskDocumentOperMsg(Long projectSid) {
        if (projectSid == null) {
            throw new CheckedException("参数缺失");
        }
        PrjProject project = prjProjectService.selectPrjProjectById(projectSid);
        List<PrjProjectTask> taskList = project.getTaskList();
        if (CollectionUtil.isNotEmpty(taskList)) {
            List<Long> projectTaskSidList = taskList.stream().map(PrjProjectTask::getProjectTaskSid).collect(Collectors.toList());
            return AjaxResult.success(prjProjectService.getProjectTaskDocumentOperLogList(projectTaskSidList));
        }
        return AjaxResult.success(new AjaxResult());
    }

    /**
     * 试销项目导入
     */
    @PostMapping("/import/shix")
    @ApiOperation(value = "试销项目导入", notes = "试销项目导入")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importShix(MultipartFile file, PrjProject project) throws Exception{
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(importService.importShix(file, project));
    }

    /**
     * 开发项目导入
     */
    @PostMapping("/import/kaif")
    @ApiOperation(value = "开发项目导入", notes = "开发项目导入")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importKaif(MultipartFile file, PrjProject project) throws Exception{
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(importService.importKaif(file, project));
    }

    /**
     * 下载试销项目导入模板
     */
    @ApiOperation(value = "下载试销项目导入模板", notes = "下载试销项目导入模板")
    @PostMapping("/import/shix/template")
    public void importShixTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        downloadTemplate(response, request, "协服SCM_导入模板_试销项目_V1.0-CMB.xlsx");
    }

    /**
     * 下载开发项目导入模板
     */
    @ApiOperation(value = "下载开发项目导入模板", notes = "下载开发项目导入模板")
    @PostMapping("/import/kaif/template")
    public void importKaifTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        downloadTemplate(response, request, "协服SCM_导入模板_开发项目_V1.0-CMB.xlsx");
    }

    /**
     * 下载模板
     */
    private void downloadTemplate(HttpServletResponse response, HttpServletRequest request, String target) {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/" + target;
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(target, "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            OutputStream out = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        }
    }

}
