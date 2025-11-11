package com.platform.ems.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.annotation.Idempotent;
import com.platform.common.annotation.Log;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.PrjMatterListMapper;
import com.platform.ems.service.IPrjProjectService;
import com.platform.ems.service.IPrjMatterListService;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.mapper.SysUserMapper;
import com.platform.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;

/**
 * 事项清单Controller
 *
 * @author platform
 * @date 2023-11-20
 */
@Api(tags = "事项清单")
@RestController
@RequestMapping("/prj/matter/list")
public class PrjMatterListController extends BaseController {

    @Autowired
    private IPrjMatterListService prjMatterListService;
    @Autowired
    private IPrjProjectService prjProjectService;
    @Autowired
    private ISysDictDataService sysDictDataService;
    @Resource
    private PrjMatterListMapper prjMatterListMapper;
    @Resource
    private SysTodoTaskMapper todoTaskMapper;
    @Autowired
    private SysUserMapper userMapper;

    /**
     * 查询事项清单列表
     */
    @ApiOperation(value = "查询事项清单列表", notes = "查询事项清单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjMatterList.class))
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody PrjMatterList prjMatterList) {
        startPage(prjMatterList);
        List<PrjMatterList> list = prjMatterListService.selectPrjMatterListList(prjMatterList);
        return getDataTable(list);
    }

    /**
     * 导出事项清单列表
     */
    @ApiOperation(value = "导出事项清单列表", notes = "导出事项清单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PrjMatterList prjMatterList) throws IOException {
        List<PrjMatterList> list = prjMatterListService.selectPrjMatterListList(prjMatterList);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PrjMatterList> util = new ExcelUtil<>(PrjMatterList.class, dataMap);
        util.exportExcel(response, list, "事项清单");
    }

    /**
     * 获取事项清单详细信息
     */
    @ApiOperation(value = "获取事项清单详细信息", notes = "获取事项清单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjMatterList.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long matterListSid) {
        if (matterListSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(prjMatterListService.selectPrjMatterListById(matterListSid));
    }

    /**
     * 新增事项清单
     */
    @ApiOperation(value = "新增事项清单", notes = "新增事项清单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "事项清单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PrjMatterList prjMatterList) {
        int row = prjMatterListService.insertPrjMatterList(prjMatterList);
        if (row == 0) {
            throw new CheckedException("新建数据失败，请联系管理员");
        }
        return AjaxResult.success(new PrjMatterList()
                .setMatterListSid(prjMatterList.getMatterListSid()));
    }


    /**
     * 选择项目自动创建事项
     */
    @ApiOperation(value = "选择项目自动创建事项", notes = "选择项目自动创建事项")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add/byProject")
    public AjaxResult setProjectYwcInsertMatter(Long projectSid) {
        if (projectSid == null) {
            throw new CheckedException("请勾选行");
        }
        return AjaxResult.success(prjProjectService.setProjectYwcInsertMatterById(projectSid));
    }

    @ApiOperation(value = "修改事项清单", notes = "修改事项清单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "事项清单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid PrjMatterList prjMatterList) {
        return toAjax(prjMatterListService.updatePrjMatterList(prjMatterList));
    }

    /**
     * 变更事项清单
     */
    @ApiOperation(value = "变更事项清单", notes = "变更事项清单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "事项清单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid PrjMatterList prjMatterList) {
        return toAjax(prjMatterListService.changePrjMatterList(prjMatterList));
    }

    /**
     * 删除事项清单
     */
    @ApiOperation(value = "删除事项清单", notes = "删除事项清单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "事项清单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> matterListSids) {
        if (CollectionUtils.isEmpty(matterListSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(prjMatterListService.deletePrjMatterListByIds(matterListSids));
    }

    /**
     * 设置事项状态
     */
    @ApiOperation(value = "设置事项状态", notes = "设置事项状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "事项清单", businessType = BusinessType.CHANGE)
    @PostMapping("/setMatterStatus")
    public AjaxResult setMatterStatus(@RequestBody PrjMatterList prjMatterList) {
        if (ArrayUtil.isEmpty(prjMatterList.getMatterListSidList())) {
            throw new CheckedException("请选择行");
        }
        return toAjax(prjMatterListService.setMatterStatus(prjMatterList));
    }

    /**
     * 分配事项处理人
     */
    @ApiOperation(value = "分配事项处理人", notes = "分配事项处理人")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "事项清单", businessType = BusinessType.CHANGE)
    @PostMapping("/setHandler")
    public AjaxResult setTaskHandler(@RequestBody PrjMatterList prjMatterList) {
        if (ArrayUtil.isEmpty(prjMatterList.getMatterListSidList())) {
            throw new BaseException("请选择行");
        }
        return AjaxResult.success(prjMatterListService.setMatterHandler(prjMatterList));
    }

    /**
     * 设置日期
     */
    @ApiOperation(value = "设置日期", notes = "设置日期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "事项清单", businessType = BusinessType.CHANGE)
    @PostMapping("/setPlanDate")
    public AjaxResult setPlanDate(@RequestBody PrjMatterList prjMatterList) {
        if (ArrayUtil.isEmpty(prjMatterList.getMatterListSidList())) {
            throw new CheckedException("请选择行");
        }
        return toAjax(prjMatterListService.setPlanDate(prjMatterList));
    }

    /**
     * 设置即将到期提醒天数
     */
    @ApiOperation(value = "设置即将到期提醒天数", notes = "设置即将到期提醒天数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "事项清单", businessType = BusinessType.CHANGE)
    @PostMapping("/setToexpireDays")
    public AjaxResult setToexpireDays(@RequestBody PrjMatterList prjMatterList) {
        if (ArrayUtil.isEmpty(prjMatterList.getMatterListSidList())) {
            throw new CheckedException("请选择行");
        }
        return toAjax(prjMatterListService.setToexpireDays(prjMatterList));
    }

    /**
     * 设置待办提醒天数
     */
    @ApiOperation(value = "设置待办提醒天数", notes = "设置待办提醒天数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "事项清单", businessType = BusinessType.CHANGE)
    @PostMapping("/setTodoDays")
    public AjaxResult setTodoDays(@RequestBody PrjMatterList prjMatterList) {
        if (ArrayUtil.isEmpty(prjMatterList.getMatterListSidList())) {
            throw new CheckedException("请选择行");
        }
        return toAjax(prjMatterListService.setTodoDays(prjMatterList));
    }

    /**
     * 设置优先级按钮
     */
    @ApiOperation(value = "设置优先级", notes = "设置优先级")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "事项清单", businessType = BusinessType.CHANGE)
    @PostMapping("/setPriority")
    public AjaxResult setPriority(@RequestBody PrjMatterList prjMatterList) {
        if (ArrayUtil.isEmpty(prjMatterList.getMatterListSidList())) {
            throw new CheckedException("请选择行");
        }
        return toAjax(prjMatterListService.setPriority(prjMatterList));
    }

    /**
     * 查询事项待办报表
     */
    @PostMapping("/todo/form")
    @ApiOperation(value = "查询事项待办报表", notes = "查询事项待办报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjProject.class))
    public TableDataInfo taskTodoForm(@RequestBody PrjMatterList prjMatterList) {
        // 传 todoCategory DB  businessType  DB  tableName  s_prj_matter_list
        QueryWrapper<SysTodoTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysTodoTask::getTableName, prjMatterList.getTableName())
                .eq(SysTodoTask::getTaskCategory, prjMatterList.getTaskCategory())
                .eq(SysTodoTask::getBusinessType, prjMatterList.getBusinessType())
                .isNotNull(SysTodoTask::getDocumentSid);
        if (StrUtil.isNotBlank(prjMatterList.getUserName())) {
            SysUser user = userMapper.selectOne(new QueryWrapper<SysUser>().lambda()
                    .eq(SysUser::getUserName, prjMatterList.getUserName()));
            if (user != null) {
                queryWrapper.lambda().eq(SysTodoTask::getUserId, user.getUserId());
            }
        }
        List<SysTodoTask> todoTaskList = todoTaskMapper.selectList(queryWrapper);
        Map<Long, String> titleMap = new HashMap<>();
        if (CollUtil.isNotEmpty(todoTaskList)) {
            Long[] matterListSidList = todoTaskList.stream().map(SysTodoTask::getDocumentSid).toArray(Long[]::new);
            prjMatterList.setMatterListSidList(matterListSidList);
            // 得到待办的标题
            titleMap = todoTaskList.stream().collect(Collectors.toMap(SysTodoTask::getDocumentSid, SysTodoTask::getTitle, (key1, key2) -> key2));
        } else {
            return getDataTable();
        }
        startPage(prjMatterList);
        List<PrjMatterList> list = prjMatterListService.selectPrjMatterListList(prjMatterList);
        // 得到待办的标题
        if (CollUtil.isNotEmpty(list)) {
            for (PrjMatterList item : list) {
                item.setTitle(titleMap.get(item.getMatterListSid()));
            }
        }
        return getDataTable(list);
    }

    /**
     * 查询事项预警报表 即将到期
     */
    @PostMapping("/toExpire")
    @ApiOperation(value = "查询事项预警报表 即将到期", notes = "查询事项预警报表 即将到期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjProject.class))
    public TableDataInfo toExpire(@RequestBody PrjMatterList prjMatterList) {
        startPage(prjMatterList);
        List<PrjMatterList> list = prjMatterListMapper.getToexpireBusiness(prjMatterList);
        return getDataTable(list);
    }

    /**
     * 查询事项预警报表 已逾期
     */
    @PostMapping("/overDue")
    @ApiOperation(value = "查询事项预警报表 已逾期", notes = "查询事项预警报表 已逾期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjProject.class))
    public TableDataInfo taskOverDue(@RequestBody PrjMatterList prjMatterList) {
        startPage(prjMatterList);
        List<PrjMatterList> list = prjMatterListMapper.getOverdueBusiness(prjMatterList);
        return getDataTable(list);
    }

    /**
     * 事项进度跟踪报表
     */
    @PostMapping("/matterTraceTable")
    @ApiOperation(value = "事项进度跟踪报表", notes = "事项进度跟踪报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = MatterTraceTableVo.class))
    public AjaxResult matterTraceTable(@RequestBody PrjProjectQuery query) {
        query.setPageNum(query.getPageNum() == 0 ? 0 : ((query.getPageNum() - 1) * query.getPageSize()));
        return AjaxResult.success(prjMatterListService.matterTraceTable(query));
    }

    /**
     * 事项进度跟踪报表导出
     */
    @PostMapping("/matterTraceTableExport")
    @ApiOperation(value = "事项进度跟踪报表导出", notes = "事项进度跟踪报表导出")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PrjProject.class))
    public void matterTraceTableExport(HttpServletResponse response, @RequestBody PrjProjectQuery query) throws IOException {
        query.setPageNum(null);
        query.setPageSize(null);
        DataTotal<MatterTraceTableVo> dataTotal = prjMatterListService.matterTraceTable(query);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<MatterTraceTableVo> util = new ExcelUtil<>(MatterTraceTableVo.class, dataMap);
        util.exportExcel(response, dataTotal.getList(), "事项进度跟踪报表");
    }

    /**
     * 事项进度指标统计
     */
    @PostMapping("/matterTraceTarget")
    @ApiOperation(value = "事项进度指标统计", notes = "事项进度指标统计")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TargetVo.class))
    public AjaxResult matterTraceTarget(@RequestBody PrjProjectQuery query) {
        return AjaxResult.success(prjMatterListService.matterTraceTarget(query));
    }

}

