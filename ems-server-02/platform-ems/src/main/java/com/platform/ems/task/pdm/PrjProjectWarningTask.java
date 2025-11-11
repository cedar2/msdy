package com.platform.ems.task.pdm;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.api.service.RemoteMenuService;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.entity.SysMenu;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.security.utils.feishu.FeishuPushUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsPdm;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.BasStaff;
import com.platform.ems.domain.JijiaOpenApi;
import com.platform.ems.domain.PrjProject;
import com.platform.ems.domain.PrjProjectTask;
import com.platform.ems.mapper.BasStaffMapper;
import com.platform.ems.mapper.PrjProjectMapper;
import com.platform.ems.mapper.PrjProjectTaskMapper;
import com.platform.ems.service.impl.JijiaOpenApiService;
import com.platform.ems.service.impl.PrjProjectServiceImpl;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysOverdueBusiness;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.domain.SysToexpireBusiness;
import com.platform.system.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 项目档案定时任务
 *
 * @author chenkw
 * @date 2023-01-02
 */
@Slf4j
@Service
@Component
@EnableScheduling
public class PrjProjectWarningTask {

    @Autowired
    private PrjProjectMapper projectMapper;
    @Autowired
    private PrjProjectTaskMapper projectTaskMapper;
    @Autowired
    private BasStaffMapper staffMapper;
    @Autowired
    private SystemUserMapper userMapper;
    @Autowired
    private RemoteMenuService remoteMenuService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SysToexpireBusinessMapper sysToexpireBusinessMapper;
    @Autowired
    private SysOverdueBusinessMapper sysOverdueBusinessMapper;
    @Autowired
    private SysDefaultSettingClientMapper settingClientMapper;
    @Autowired
    private JijiaOpenApiService jijiaOpenApiService;

    private static final String YYYY_MM_DD = "yyyy-MM-dd";

    public final SimpleDateFormat FORMAT = new SimpleDateFormat(YYYY_MM_DD);

    @Scheduled(cron = "00 25 06 * * *")
    public void warningProject() {
        // 获取菜单id
        SysMenu menu = new SysMenu();
        menu.setMenuName(ConstantsWorkbench.TODO_PRJ_PROJECT);
        try {
            menu = remoteMenuService.getInfoByName(menu).getData();
        } catch (Exception e) {
            log.warn(ConstantsWorkbench.TODO_PRJ_PROJECT + "菜单获取失败！");
        }
        Long menuId = null;
        if (menu != null && menu.getMenuId() != null) {
            menuId = menu.getMenuId();
        }
        // 即将到期
        List<SysToexpireBusiness> toexpireBusinessList = new ArrayList<>();
        // 已逾期
        List<SysOverdueBusiness> overdueBusinessList = new ArrayList<>();

        log.info("=====>开始查询即将逾期项目档案");
        List<PrjProject> toexpireProjectList = projectMapper.getToexpireBusiness(new PrjProject(), 15);
        // 因为有关联员工，一个员工可以有多个用户档案，所以同一个项目可能关联出多条，所以要对创建人进行控制
        HashMap<Long, Long> toexpireCreatorMap = new HashMap<>();
        if (CollUtil.isNotEmpty(toexpireProjectList)) {
            Long finalMenuId = menuId;
            toexpireProjectList.forEach(item->{
                SysToexpireBusiness toexpireProject = new SysToexpireBusiness();
                String endDate = DateUtil.format(item.getPlanEndDate(), YYYY_MM_DD);
                String projectName = "";
                if (CharSequenceUtil.isNotBlank(item.getProjectName())) {
                    projectName = "“" + item.getProjectName() + "”";
                }
                String erpMaterialSkuBarcode = "";
                if (CharSequenceUtil.isNotBlank(item.getErpMaterialSkuBarcode())) {
                    erpMaterialSkuBarcode = item.getErpMaterialSkuBarcode() + "(ERP)";
                }
                String productCode = "";
                if (CharSequenceUtil.isNotBlank(item.getProductCode())) {
                    if (CharSequenceUtil.isNotBlank(item.getErpMaterialSkuBarcode())) {
                        productCode = "/";
                    }
                    productCode = productCode + item.getProductCode() + "(SPU)";
                }
                String code = "";
                if (CharSequenceUtil.isNotBlank(erpMaterialSkuBarcode) || CharSequenceUtil.isNotBlank(productCode)) {
                    code = "”" + erpMaterialSkuBarcode + productCode + "“";
                }
                toexpireProject.setClientId(item.getClientId())
                        .setTitle("项目档案" + projectName + "（" +
                                item.getProjectCode() + "）" + code + "即将到期，项目计划完成日期为 " + endDate)
                        .setTableName(ConstantsTable.TABLE_PRJ_PROJECT)
                        .setDocumentSid(item.getProjectSid())
                        .setDocumentCode(String.valueOf(item.getProjectCode()))
                        .setExpiredDate(item.getPlanEndDate())
                        .setNoticeDate(new Date())
                        .setMenuId(finalMenuId);
                // 创建人
                if (!item.getCreatorAccountId().equals(toexpireCreatorMap.get(item.getProjectSid()))) {
                    toexpireProject.setUserId(item.getCreatorAccountId());
                    toexpireBusinessList.add(toexpireProject);
                    toexpireCreatorMap.put(item.getProjectSid(), item.getCreatorAccountId());
                }

                // 项目负责人
                if (item.getProjectLeaderId() != null &&
                        !item.getProjectLeaderId().equals(item.getCreatorAccountId())) {
                    SysToexpireBusiness projectLeader = new SysToexpireBusiness();
                    BeanCopyUtils.copyProperties(toexpireProject, projectLeader);
                    projectLeader.setUserId(item.getProjectLeaderId());
                    toexpireBusinessList.add(projectLeader);
                }
            });
        }

        log.info("=====>开始查询即已逾期项目档案");
        List<PrjProject> overDueProjectList = projectMapper.getOverdueBusiness(new PrjProject());
        // 因为有关联员工，一个员工可以有多个用户档案，所以同一个项目可能关联出多条，所以要对创建人进行控制
        HashMap<Long, Long> overCreatorMap = new HashMap<>();
        if (CollUtil.isNotEmpty(overDueProjectList)) {
            Long finalMenuId = menuId;
            overDueProjectList.forEach(item->{
                SysOverdueBusiness overDueProject = new SysOverdueBusiness();
                String endDate = DateUtil.format(item.getPlanEndDate(), YYYY_MM_DD);
                String projectName = "";
                if (CharSequenceUtil.isNotBlank(item.getProjectName())) {
                    projectName = "“" + item.getProjectName() + "”";
                }
                String erpMaterialSkuBarcode = "";
                if (CharSequenceUtil.isNotBlank(item.getErpMaterialSkuBarcode())) {
                    erpMaterialSkuBarcode = item.getErpMaterialSkuBarcode() + "(ERP)";
                }
                String productCode = "";
                if (CharSequenceUtil.isNotBlank(item.getProductCode())) {
                    if (CharSequenceUtil.isNotBlank(item.getErpMaterialSkuBarcode())) {
                        productCode = "/";
                    }
                    productCode = productCode + item.getProductCode() + "(SPU)";
                }
                String code = "";
                if (CharSequenceUtil.isNotBlank(erpMaterialSkuBarcode) || CharSequenceUtil.isNotBlank(productCode)) {
                    code = "”" + erpMaterialSkuBarcode + productCode + "“";
                }
                overDueProject.setClientId(item.getClientId())
                        .setTitle("项目档案" + projectName + "（" +
                                item.getProjectCode() + "）" + code + "已逾期，项目计划完成日期为 " + endDate)
                        .setTableName(ConstantsTable.TABLE_PRJ_PROJECT)
                        .setDocumentSid(item.getProjectSid())
                        .setDocumentCode(String.valueOf(item.getProjectCode()))
                        .setExpiredDate(item.getPlanEndDate())
                        .setNoticeDate(new Date())
                        .setMenuId(finalMenuId);
                // 创建人
                if (!item.getCreatorAccountId().equals(overCreatorMap.get(item.getProjectSid()))) {
                    overDueProject.setUserId(item.getCreatorAccountId());
                    overdueBusinessList.add(overDueProject);
                    overCreatorMap.put(item.getProjectSid(), item.getCreatorAccountId());
                }

                // 项目负责人
                if (item.getProjectLeaderId() != null &&
                        !item.getProjectLeaderId().equals(item.getCreatorAccountId())) {
                    SysOverdueBusiness projectLeader = new SysOverdueBusiness();
                    BeanCopyUtils.copyProperties(overDueProject, projectLeader);
                    projectLeader.setUserId(item.getProjectLeaderId());
                    overdueBusinessList.add(projectLeader);
                }
            });
        }

        log.info("=====>开始查询即将逾期项目档案任务明细");
        List<PrjProjectTask> toexpireTaskList = projectTaskMapper.getToexpireBusiness(new PrjProjectTask(),15);
        if (CollUtil.isNotEmpty(toexpireTaskList)) {
            Long finalMenuId = menuId;
            // 岗位的code
            List<String> positionList = new ArrayList<>();
            toexpireTaskList.forEach(item->{
                // 发起岗位
                if (CharSequenceUtil.isNotBlank(item.getStartPositionCode())) {
                    positionList.addAll(Arrays.asList(item.getStartPositionCode().split(";")));
                }
                // 负责岗位
                if (CharSequenceUtil.isNotBlank(item.getChargePositionCode())) {
                    positionList.addAll(Arrays.asList(item.getChargePositionCode().split(";")));
                }
                // 告知岗位
                if (CharSequenceUtil.isNotBlank(item.getNoticePositionCode())) {
                    positionList.addAll(Arrays.asList(item.getNoticePositionCode().split(";")));
                }
            });
            Map<String, List<BasStaff>> staffMap = new HashMap<>();
            if (CollUtil.isNotEmpty(positionList)) {
                // 员工查询 条件
                BasStaff staff = new BasStaff();
                staff.setDefaultPositionCodeList(positionList.toArray(new String[positionList.size()]));
                // 获取通知对象
                List<BasStaff> staffList = staffMapper.getStaffListBySidOrPosition(staff);
                staffMap = staffList.stream().collect(Collectors.groupingBy(e -> String.valueOf(e.getDefaultPositionCode())));
            }
            for (PrjProjectTask item : toexpireTaskList) {
                List<BasStaff> taskStaffList = new ArrayList<>();
                // 处理人
                if (StrUtil.isNotBlank(item.getHandlerTaskId())) {
                    String[] handlerTaskIds = item.getHandlerTaskId().split(";");
                    for (int i = 0; i < handlerTaskIds.length; i++) {
                        taskStaffList.add(new BasStaff().setUserId(Long.parseLong(handlerTaskIds[i])));
                    }
                }
                // 告知岗位
                if (CharSequenceUtil.isNotBlank(item.getStartPositionCode())) {
                    List<String> startPositionCodeList = Arrays.asList(item.getStartPositionCode().split(";"));
                    for (String s : startPositionCodeList) {
                        if (staffMap.get(s) != null) {
                            taskStaffList.addAll(staffMap.get(s));
                        }
                    }
                }
                // 负责岗位
                if (CharSequenceUtil.isNotBlank(item.getChargePositionCode())) {
                    List<String> chargePositionCodeList = Arrays.asList(item.getChargePositionCode().split(";"));
                    for (String s : chargePositionCodeList) {
                        if (staffMap.get(s) != null) {
                            taskStaffList.addAll(staffMap.get(s));
                        }
                    }
                }
                // 通知岗位
                if (CharSequenceUtil.isNotBlank(item.getNoticePositionCode())) {
                    List<String> noticePositionCodeList = Arrays.asList(item.getNoticePositionCode().split(";"));
                    for (String s : noticePositionCodeList) {
                        if (staffMap.get(s) != null) {
                            taskStaffList.addAll(staffMap.get(s));
                        }
                    }
                }
                Long[] staffUserId = taskStaffList.stream().map(BasStaff::getUserId).filter(Objects::nonNull).distinct().toArray(Long[]::new);
                if (ArrayUtil.isNotEmpty(staffUserId)) {
                    SysToexpireBusiness toexpireTask = new SysToexpireBusiness();
                    String endDate = DateUtil.format(item.getPlanEndDate(), YYYY_MM_DD);
                    String projectName = "";
                    if (CharSequenceUtil.isNotBlank(item.getProjectName())) {
                        projectName = item.getProjectName();
                    }
                    toexpireTask.setClientId(item.getClientId())
                            .setTitle("项目" + projectName + "（" +
                                    item.getProjectCode() + "）的任务 " + item.getTaskName()
                                    + " 即将到期，任务的计划完成日期为 " + endDate)
                            .setTableName(ConstantsTable.TABLE_PRJ_PROJECT_TASK)
                            .setDocumentSid(item.getProjectTaskSid())
                            .setDocumentCode(String.valueOf(item.getProjectCode()))
                            .setExpiredDate(item.getPlanEndDate())
                            .setNoticeDate(new Date())
                            .setMenuId(null);
                    for (int i = 0; i < staffUserId.length; i++) {
                        SysToexpireBusiness temp = new SysToexpireBusiness();
                        BeanCopyUtils.copyProperties(toexpireTask, temp);
                        temp.setUserId(staffUserId[i]);
                        toexpireBusinessList.add(temp);
                    }
                }
            }
        }

        log.info("=====>开始查询已到期项目档案任务明细");
        List<PrjProjectTask> overDueTaskList = projectTaskMapper.getOverdueBusiness(new PrjProjectTask());
        if (CollUtil.isNotEmpty(overDueTaskList)) {
            Long finalMenuId = menuId;
            // 岗位的code
            List<String> positionList = new ArrayList<>();
            overDueTaskList.forEach(item->{
                // 发起岗位
                if (CharSequenceUtil.isNotBlank(item.getStartPositionCode())) {
                    positionList.addAll(Arrays.asList(item.getStartPositionCode().split(";")));
                }
                // 负责岗位
                if (CharSequenceUtil.isNotBlank(item.getChargePositionCode())) {
                    positionList.addAll(Arrays.asList(item.getChargePositionCode().split(";")));
                }
                // 告知岗位
                if (CharSequenceUtil.isNotBlank(item.getNoticePositionCode())) {
                    positionList.addAll(Arrays.asList(item.getNoticePositionCode().split(";")));
                }
            });

            Map<String, List<BasStaff>> staffMap = new HashMap<>();
            if (CollUtil.isNotEmpty(positionList)) {
                // 员工查询 条件
                BasStaff staff = new BasStaff();
                staff.setDefaultPositionCodeList(positionList.toArray(new String[positionList.size()]));
                // 获取通知对象
                List<BasStaff> staffList = staffMapper.getStaffListBySidOrPosition(staff);
                staffMap = staffList.stream().collect(Collectors.groupingBy(e -> String.valueOf(e.getDefaultPositionCode())));
            }
            for (PrjProjectTask item : overDueTaskList) {
                List<BasStaff> taskStaffList = new ArrayList<>();
                // 处理人
                if (StrUtil.isNotBlank(item.getHandlerTaskId())) {
                    String[] handlerTaskIds = item.getHandlerTaskId().split(";");
                    for (int i = 0; i < handlerTaskIds.length; i++) {
                        taskStaffList.add(new BasStaff().setUserId(Long.parseLong(handlerTaskIds[i])));
                    }
                }
                // 告知岗位
                if (CharSequenceUtil.isNotBlank(item.getStartPositionCode())) {
                    List<String> startPositionCodeList = Arrays.asList(item.getStartPositionCode().split(";"));
                    for (String s : startPositionCodeList) {
                        if (staffMap.get(s) != null) {
                            taskStaffList.addAll(staffMap.get(s));
                        }
                    }
                }
                // 负责岗位
                if (CharSequenceUtil.isNotBlank(item.getChargePositionCode())) {
                    List<String> chargePositionCodeList = Arrays.asList(item.getChargePositionCode().split(";"));
                    for (String s : chargePositionCodeList) {
                        if (staffMap.get(s) != null) {
                            taskStaffList.addAll(staffMap.get(s));
                        }
                    }
                }
                // 通知岗位
                if (CharSequenceUtil.isNotBlank(item.getNoticePositionCode())) {
                    List<String> noticePositionCodeList = Arrays.asList(item.getNoticePositionCode().split(";"));
                    for (String s : noticePositionCodeList) {
                        if (staffMap.get(s) != null) {
                            taskStaffList.addAll(staffMap.get(s));
                        }
                    }
                }
                Long[] staffUserId = taskStaffList.stream().map(BasStaff::getUserId).filter(Objects::nonNull).distinct().toArray(Long[]::new);
                if (ArrayUtil.isNotEmpty(staffUserId)) {
                    SysOverdueBusiness overDueTask = new SysOverdueBusiness();
                    String endDate = DateUtil.format(item.getPlanEndDate(), YYYY_MM_DD);
                    String projectName = "";
                    if (CharSequenceUtil.isNotBlank(item.getProjectName())) {
                        projectName = item.getProjectName();
                    }
                    overDueTask.setClientId(item.getClientId())
                            .setTitle("项目" + projectName + "（" +
                                    item.getProjectCode() + "）的任务 " + item.getTaskName()
                                    + " 已逾期，任务的计划完成日期为 " + endDate)
                            .setTableName(ConstantsTable.TABLE_PRJ_PROJECT_TASK)
                            .setDocumentSid(item.getProjectTaskSid())
                            .setDocumentCode(String.valueOf(item.getProjectCode()))
                            .setExpiredDate(item.getPlanEndDate())
                            .setNoticeDate(new Date())
                            .setMenuId(null);
                    for (int i = 0; i < staffUserId.length; i++) {
                        SysOverdueBusiness temp = new SysOverdueBusiness();
                        BeanCopyUtils.copyProperties(overDueTask, temp);
                        temp.setUserId(staffUserId[i]);
                        overdueBusinessList.add(temp);
                    }
                }
            }
        }

        log.info("=====>更新新数据");
        // 删除过期的数据
        String[] tableNames = new String[]{ConstantsTable.TABLE_PRJ_PROJECT,ConstantsTable.TABLE_PRJ_PROJECT_TASK};
        sysToexpireBusinessMapper.delete(new SysToexpireBusiness().setTableNameList(tableNames));
        sysOverdueBusinessMapper.delete(new SysOverdueBusiness().setTableNameList(tableNames));
        // 写入即将到期
        if (CollUtil.isNotEmpty(toexpireBusinessList)){
            sysToexpireBusinessMapper.inserts(toexpireBusinessList);
        }
        // 写入已逾期
        if (CollUtil.isNotEmpty(overdueBusinessList)){
            sysOverdueBusinessMapper.inserts(overdueBusinessList);
        }
        log.info("=====>更新完成");
    }

    /**
     * 1、定时时间：每天早上6：00
     * 2、若项目任务满足以下条件：
     * 任务所属的项目状态为进行中且已确认，且任务状态为未开始，且当前日期<计划开始日期的同时，当前日期+任务执行提醒天数>=计划完成日期
     * 给项目负责人和任务的发起岗位下员工所属账号，发送待办消息：项目“XXXXX”（YYY）的任务 ZZZZZ 还未开始，请及时跟进！
     * 其中，XXXXX为项目名称，YYY为项目编码，ZZZZZ为任务节点名称
     * 待办表保存数据：数据库表保存为s_prj_project_task，单号sid保存项目SID，单号行明细sid保存为项目任务SID，菜单id无需保存
     * 3、单据中心的各个单据新建页面，点击“暂存”或“提交”时，根据项目SID和项目任务SID，删除此单据对应的任务的待办提醒信息
     * 4、项目任务明细报表，设置任务状态时，任务状态改为非“未开始”，删除此任务对应的待开始的待办提示信息
     */
    @Scheduled(cron = "00 15 06 * * *")
    public void startProject() {
        // 删除旧数据
        sysTodoTaskMapper.deleteIgnore(new SysTodoTask()
                .setTaskCategory(ConstantsEms.TODO_TASK_DB).setTableName(ConstantsTable.TABLE_PRJ_PROJECT_TASK)
                        .setTitle("还未开始，请及时跟进！"));
        // 获取需要发送待办的数据并发送待办
        startProjectExecute(new PrjProjectTask());
    }

    /**
     * 还未开始，请及时跟进！ 具体逻辑
     */
    public void startProjectExecute(PrjProjectTask projectTask) {
        List<PrjProjectTask> prjProjectTaskList = projectTaskMapper.getNotYetStartTaskList(projectTask);
        if (CollUtil.isNotEmpty(prjProjectTaskList)) {
            // 初始化通知信息
            SysTodoTask sysTodoTask = new SysTodoTask();
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsTable.TABLE_PRJ_PROJECT_TASK)
                    .setNoticeDate(new Date());

            String title;
            Long documentSid;
            Long documentCode;
            String documentName;

            List<SysTodoTask> todoTaskList = new ArrayList<>();

            // 因为有关联岗位的员工，员工的用户可能跟处理人重复，所以要对用户进行控制
            HashMap<String, Long> userMap = new HashMap<>();

            // 遍历数据
            for (PrjProjectTask task : prjProjectTaskList) {
                documentName = task.getProjectName() == null ? "" : "“" + task.getProjectName() + "”";
                title = "项目" + documentName + "（" + task.getProjectCode() + "）的任务 " + task.getTaskName() + " 还未开始，请及时跟进！";
                documentSid = task.getProjectSid();
                documentCode = task.getProjectCode();
                sysTodoTask.setClientId(task.getClientId()).setTitle(title).setDocumentCode(documentCode.toString())
                        .setDocumentSid(documentSid).setDocumentItemSid(task.getProjectTaskSid());

                List<BasStaff> staffList = getNoticeStaff(task, true, false, false);
                // 得到 userId
                if (CollUtil.isNotEmpty(staffList)) {
                    List<Long> staffUserId = staffList.stream().map(BasStaff::getUserId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(staffUserId)) {
                        // 遍历通知对象
                        for (int i = 0; i < staffUserId.size(); i++) {
                            if (!task.getProjectTaskSid().equals(userMap.get(staffUserId.get(i) + "-" + task.getProjectTaskSid()))) {
                                SysTodoTask base = new SysTodoTask();
                                BeanUtil.copyProperties(sysTodoTask, base);
                                base.setUserId(staffUserId.get(i));
                                todoTaskList.add(base);
                                userMap.put(staffUserId.get(i) + "-" + task.getProjectTaskSid(), task.getProjectTaskSid());
                            }
                        }
                    }
                }
                // 处理人
                if (StrUtil.isNotBlank(task.getHandlerTaskId())) {
                    String[] handlerTaskIds = task.getHandlerTaskId().split(";");
                    for (int i = 0; i < handlerTaskIds.length; i++) {
                        if (!task.getProjectTaskSid().equals(userMap.get(Long.valueOf(handlerTaskIds[i]) + "-" + task.getProjectTaskSid()))) {
                            SysTodoTask base = new SysTodoTask();
                            BeanUtil.copyProperties(sysTodoTask, base);
                            base.setUserId(Long.parseLong(handlerTaskIds[i]));
                            todoTaskList.add(base);
                            userMap.put(Long.valueOf(handlerTaskIds[i]) + "-" + task.getProjectTaskSid(), task.getProjectTaskSid());
                        }
                    }
                }
            }

            // 写入待办
            if (CollUtil.isNotEmpty(todoTaskList)) {
                sysTodoTaskMapper.inserts(todoTaskList);
            }
        }
    }

    /**
     * 获取项目任务明细的所有岗位
     * @param projectTaskList 项目任务明细列表
     * @return 员工
     */
    private List<String> getNoticeStaffFromPosition(List<PrjProjectTask> projectTaskList) {
        // 岗位的code
        List<String> positionList = new ArrayList<>();
        if (CollUtil.isNotEmpty(projectTaskList)) {
            projectTaskList.forEach(projectTask->{
                // 发起岗位
                if (CharSequenceUtil.isNotBlank(projectTask.getStartPositionCode())) {
                    positionList.addAll(Arrays.asList(projectTask.getStartPositionCode().split(";")));
                }
                // 负责岗位
                if (CharSequenceUtil.isNotBlank(projectTask.getChargePositionCode())) {
                    positionList.addAll(Arrays.asList(projectTask.getChargePositionCode().split(";")));
                }
            });
        }
        return positionList;
    }

    /**
     * 获取项目任务明细需要通知的所有人
     * @param projectTask 项目任务明细
     * @return 员工
     */
    private List<BasStaff> getNoticeStaff(PrjProjectTask projectTask, boolean start, boolean charge, boolean notice) {
        // 员工查询 条件
        BasStaff staff = new BasStaff();
        // 获取通知对象
        List<BasStaff> staffList = new ArrayList<>();
        // 岗位的code
        List<String> positionList = new ArrayList<>();
        // 发起岗位
        if (start == true && CharSequenceUtil.isNotBlank(projectTask.getStartPositionCode())) {
            positionList.addAll(Arrays.asList(projectTask.getStartPositionCode().split(";")));
        }
        // 负责岗位
        if (charge == true && CharSequenceUtil.isNotBlank(projectTask.getChargePositionCode())) {
            positionList.addAll(Arrays.asList(projectTask.getChargePositionCode().split(";")));
        }
        // 告知岗位
        if (notice == true && CharSequenceUtil.isNotBlank(projectTask.getNoticePositionCode())) {
            positionList.addAll(Arrays.asList(projectTask.getNoticePositionCode().split(";")));
        }
        // 获取岗位下的员工
        if (CollUtil.isNotEmpty(positionList)) {
            String[] positionCodes = positionList.toArray(new String[positionList.size()]);
            staff.setDefaultPositionCodeList(positionCodes);
        }
        // 负责人
        if (projectTask.getProjectLeaderSid() != null) {
            staff.setStaffSid(projectTask.getProjectLeaderSid());
        }
        // 查询员工
        if (ArrayUtil.isNotEmpty(staff.getDefaultPositionCodeList()) || staff.getStaffSid() != null) {
            staffList = staffMapper.getStaffListBySidOrPosition(staff);
        }
        return staffList;
    }

    /**
     1）从项目档案表中，查询出 “项目类型“为”试销“ + 所属阶段为“站点试销”+ ”项目状态“为”进行中“+”处理状态“为”已确认“+
        “二次采购标识 / 一次采购标识“不是”Y“的项目的”项目sid、商品SKU编码(ERP) 、销售站点code、销售站点名称“明细
     2）根据“商品SKU编码(ERP) + 销售站点名称“，将积加返回的采购订单信息与1）获取的数据进行匹配，
        未匹配上的数据，或者仅匹配到1笔明细的数据，直接删除；
        若匹配上的明细大于等于2笔，则根据“项目sid“，将对应项目在项目档案表（s_prj_project）中的“二次采购标识 / 一次采购标识“更新为“Y”
     3）将2）获取的采购订单信息，按照“SKU +站点名称+采购订单创建时间“进行升序排列，获取每个“SKU +站点名称“下排序的第二笔订单明细信息，给飞书推送相关消息通知
        获取的项目以及该项目任务明细，给该项目的”项目负责人“、项目任务明细的发起岗、负责岗发送消息通知；若消息通知人员重复，需去
     */
    @Scheduled(cron = "00 30 08 * * *")
    public void jijiaService() {

        String jijiaToken = getToken();

        if(CharSequenceUtil.isNotBlank(jijiaToken)) {
            this.firstPurchaseFlag(jijiaToken);
            this.secondPurchaseFlag(jijiaToken);
            this.firstPurchaseArrivalNotice(jijiaToken);
        }

    }

    /**
     * 获取token
     */
    public String getToken() {
        String jijiaToken = null;
        // 拿到token
        try {
            jijiaToken = jijiaOpenApiService.getToken();
        } catch (Exception e) {
            log.error("获取积加token失败");
        }
        return jijiaToken;
    }

    /**
     * 一次采购订单
     * @param jijiaToken 积加接口的token
     */
    public void firstPurchaseFlag(String jijiaToken) {
        log.info("=====>开始处理 PDM & 积加一次采购订单");
        List<PrjProject> list = projectMapper.selectPrjProjectListAll(new PrjProject().setProjectType(ConstantsPdm.PROJECT_TYPE_SHIX).setProjectPhase(ConstantsPdm.PROJECT_PHASE_ZDSX)
                .setProjectStatusNot(ConstantsPdm.PROJECT_STATUS_YWC).setHandleStatus(ConstantsEms.CHECK_STATUS).setFirstPurchaseFlag(ConstantsEms.NO)
                .setClientId("30001"));
        List<PrjProject> projectList = new ArrayList<>();
        if (CollUtil.isNotEmpty(list) && CharSequenceUtil.isNotBlank(jijiaToken)) {
            // 因为销售站点是多选存值的，所以这里将销售站点分出来
            for (PrjProject project : list) {
                // 筛选出 有erp编码 和销售站点 的项目档案
                if (CharSequenceUtil.isNotBlank(project.getErpMaterialSkuBarcode()) &&
                        CharSequenceUtil.isNotBlank(project.getSaleStationName())) {
                    String[] names = project.getSaleStationName().split(";");
                    for (String s : names) {
                        PrjProject one = new PrjProject();
                        BeanCopyUtils.copyProperties(project, one);
                        one.setSaleStationName(s);
                        projectList.add(one);
                    }
                }
            }
            // 筛选出 有erp编码的项目档案
            projectList = projectList.stream().filter(o -> o.getErpMaterialSkuBarcode() != null).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(projectList)) {
                String[] erpCodes = projectList.stream().map(PrjProject::getErpMaterialSkuBarcode).toArray(String[]::new);
                JijiaOpenApi jijiaOpenApi = new JijiaOpenApi();
                // 固定传值 3：待交货 5：变更中 6：交货中 7：全部交货 8：已完成
                jijiaOpenApi.setInvoicesStatusList(new String[]{"3","5","6","7","8"});
                jijiaOpenApi.setSkuList(erpCodes);
                List<JijiaOpenApi> purchaseOrderList = new ArrayList<>();
                // 积加的一次采购订单数据
                try {
                    purchaseOrderList = jijiaOpenApiService.selectPurchaseFlag(jijiaOpenApi, jijiaToken);
                } catch (Exception e) {
                    log.error("获取积加新品开发-采购订单数据失败");
                }
                if (CollUtil.isNotEmpty(purchaseOrderList)) {
                    List<PrjProject> updList = new ArrayList<>();
                    // 积加的数据
                    Map<String, List<JijiaOpenApi>> jijiaListMap = purchaseOrderList.stream()
                            .collect(Collectors.groupingBy(o -> o.getSku() +"-"+ o.getArrivalMarketName()));
                    Map<String, List<JijiaOpenApi>> needFeishuMap = new HashMap<>();
                    // pdm的数据
                    Map<String, List<PrjProject>> pdmListMap = projectList.stream()
                            .collect(Collectors.groupingBy(o -> o.getErpMaterialSkuBarcode() +"-"+ o.getSaleStationName()));
                    // 存放匹配上的PDM数据的 项目任务明细，需要 明细种的岗位信息 去 推送飞书
                    Map<String, List<PrjProjectTask>> pdmTaskListMap = new HashMap<>();
                    // 根据“商品SKU编码(ERP) + 销售站点名称“，将积加返回的采购订单信息与1）获取的数据进行匹配，
                    // 若匹配上，则根据“项目sid“，将对应项目在项目档案表（s_prj_project）中的“一次采购标识“（first_puchase_flag）更新为“Y”
                    for (Map.Entry<String, List<PrjProject>> entry : pdmListMap.entrySet()) {
                        if (jijiaListMap.containsKey(entry.getKey()) && jijiaListMap.get(entry.getKey()) != null) {
                            updList.addAll(pdmListMap.get(entry.getKey()));
                            // 获取任务明细通知负责人
                            Long[] sidList = pdmListMap.get(entry.getKey()).stream().map(PrjProject::getProjectSid).toArray(Long[]::new);
                            List<PrjProjectTask> taskList = projectTaskMapper.selectPrjProjectTaskListAll(
                                    new PrjProjectTask().setProjectSidList(sidList));
                            // 如果任务明细是空的，则那项目档案的 负责人 写入 临时的任务明细表种
                            if (CollUtil.isEmpty(taskList)) {
                                entry.getValue().forEach(item->{
                                    if (item.getProjectLeaderSid() != null) {
                                        taskList.add(new PrjProjectTask().setProjectLeaderSid(item.getProjectLeaderSid())
                                                .setClientId(item.getClientId()));
                                    }
                                });
                            }
                            pdmTaskListMap.put(entry.getKey(), taskList);
                            // 需要发推送的采购数据
                            needFeishuMap.put(entry.getKey(), jijiaListMap.get(entry.getKey()));
                        }
                    }
                    // 处理PDM数据
                    if (CollUtil.isNotEmpty(updList)) {
                        Long[] sidList = updList.stream().map(PrjProject::getProjectSid).toArray(Long[]::new);
                        projectMapper.updatePrjProject(new PrjProject().setProjectSidList(sidList)
                                .setFirstPurchaseFlag(ConstantsEms.YES).setFirstPurchaseFlagUpdateDate(new Date()));
                    }
                    // 第一笔订单明细 并用飞书推送，遍历积加的订单数据，推送的也是积加的订单数据
                    for (Map.Entry<String, List<JijiaOpenApi>> entry : needFeishuMap.entrySet()) {
                        // 得到第一次采购
                        List<JijiaOpenApi> tempList = entry.getValue();
                        JijiaOpenApi temp = tempList.stream().min(Comparator.comparing(JijiaOpenApi::getCreatedAt)).orElse(null);
                        if (temp != null) {
                            // 内容
                            String[] content = orderContent(temp);
                            // 关联出这笔 sku(erp) + 销售站点的 pdm的负责人
                            sentFeishu(pdmTaskListMap, entry.getKey(), "一次采购下单通知", content);
                        }
                    }
                }
            }
        }
        log.info("=====>处理 PDM & 积加一次采购订单 完成");
    }

    /**
     * 二次采购订单
     * @param jijiaToken 积加接口的token
     */
    public void secondPurchaseFlag(String jijiaToken) {
        log.info("=====>开始处理 PDM & 积加二次采购订单");
        List<PrjProject> list = projectMapper.selectPrjProjectListAll(new PrjProject().setProjectType(ConstantsPdm.PROJECT_TYPE_SHIX).setProjectPhase(ConstantsPdm.PROJECT_PHASE_ZDSX)
                .setIsRepeatOrder(ConstantsEms.YES).setHandleStatus(ConstantsEms.CHECK_STATUS).setSecondPurchaseFlag(ConstantsEms.NO)
                .setClientId("30001"));
        List<PrjProject> projectList = new ArrayList<>();
        if (CollUtil.isNotEmpty(list) && CharSequenceUtil.isNotBlank(jijiaToken)) {
            // 因为销售站点是多选存值的，所以这里将销售站点分出来
            for (PrjProject project : list) {
                // 筛选出 有erp编码 和销售站点 的项目档案
                if (CharSequenceUtil.isNotBlank(project.getErpMaterialSkuBarcode()) &&
                        CharSequenceUtil.isNotBlank(project.getSaleStationName())) {
                    String[] names = project.getSaleStationName().split(";");
                    for (String s : names) {
                        PrjProject one = new PrjProject();
                        BeanCopyUtils.copyProperties(project, one);
                        one.setSaleStationName(s);
                        projectList.add(one);
                    }
                }
            }
            if (CollUtil.isNotEmpty(projectList)) {
                String[] erpCodes = projectList.stream().map(PrjProject::getErpMaterialSkuBarcode).toArray(String[]::new);
                JijiaOpenApi jijiaOpenApi = new JijiaOpenApi();
                // 固定传值 3：待交货 5：变更中 6：交货中 7：全部交货 8：已完成
                jijiaOpenApi.setInvoicesStatusList(new String[]{"3","5","6","7","8"});
                jijiaOpenApi.setSkuList(erpCodes);
                List<JijiaOpenApi> purchaseOrderList = new ArrayList<>();
                // 积加的二次采购订单数据
                try {
                    purchaseOrderList = jijiaOpenApiService.selectPurchaseFlag(jijiaOpenApi, jijiaToken);
                } catch (Exception e) {
                    log.error("获取积加新品开发-采购订单数据失败");
                }
                if (CollUtil.isNotEmpty(purchaseOrderList)) {
                    List<PrjProject> updList = new ArrayList<>();
                    // 积加的数据
                    Map<String, List<JijiaOpenApi>> jijiaListMap = purchaseOrderList.stream()
                            .collect(Collectors.groupingBy(o -> o.getSku() +"-"+ o.getArrivalMarketName()));
                    Map<String, List<JijiaOpenApi>> needFeishuMap = new HashMap<>();
                    // pdm的数据
                    Map<String, List<PrjProject>> pdmListMap = projectList.stream()
                            .collect(Collectors.groupingBy(o -> o.getErpMaterialSkuBarcode() +"-"+ o.getSaleStationName()));
                    // 存放匹配上的PDM数据的 项目任务明细，需要 明细种的岗位信息 去 推送飞书
                    Map<String, List<PrjProjectTask>> pdmTaskListMap = new HashMap<>();
                    // 根据“商品SKU编码(ERP) + 销售站点名称“，将积加返回的采购订单信息与1）获取的数据进行匹配，
                    // 若匹配上，则根据“项目sid“，将对应项目在项目档案表（s_prj_project）中的“二次采购标识“（second_puchase_flag）更新为“Y”
                    for (Map.Entry<String, List<PrjProject>> entry : pdmListMap.entrySet()) {
                        if (jijiaListMap.containsKey(entry.getKey()) && jijiaListMap.get(entry.getKey()) != null
                                    && jijiaListMap.get(entry.getKey()).size() >= 2) {
                            updList.addAll(pdmListMap.get(entry.getKey()));
                            // 获取任务明细通知负责人
                            Long[] sidList = pdmListMap.get(entry.getKey()).stream().map(PrjProject::getProjectSid).toArray(Long[]::new);
                            List<PrjProjectTask> taskList = projectTaskMapper.selectPrjProjectTaskListAll(
                                    new PrjProjectTask().setProjectSidList(sidList));
                            // 如果任务明细是空的，则那项目档案的 负责人 写入 临时的任务明细表种
                            if (CollUtil.isEmpty(taskList)) {
                                entry.getValue().forEach(item->{
                                    if (item.getProjectLeaderSid() != null) {
                                        taskList.add(new PrjProjectTask().setProjectLeaderSid(item.getProjectLeaderSid())
                                                .setClientId(item.getClientId()));
                                    }
                                });
                            }
                            pdmTaskListMap.put(entry.getKey(), taskList);
                            // 需要发推送的采购数据
                            needFeishuMap.put(entry.getKey(), jijiaListMap.get(entry.getKey()));
                        }
                    }
                    // 处理PDM数据
                    if (CollUtil.isNotEmpty(updList)) {
                        Long[] sidList = updList.stream().map(PrjProject::getProjectSid).toArray(Long[]::new);
                        projectMapper.updatePrjProject(new PrjProject().setProjectSidList(sidList)
                                .setSecondPurchaseFlag(ConstantsEms.YES).setSecondPurchaseFlagUpdateDate(new Date()));
                    }

                    // 第二笔订单明细 并用飞书推送，遍历积加的订单数据，推送的也是积加的订单数据
                    for (Map.Entry<String, List<JijiaOpenApi>> entry : needFeishuMap.entrySet()) {
                        // 得到第二次采购
                        List<JijiaOpenApi> tempList = needFeishuMap.get(entry.getKey());
                        tempList = tempList.stream().sorted(Comparator.comparing(JijiaOpenApi::getCreatedAt, Comparator.nullsLast(Date::compareTo))).collect(toList());
                        if (tempList.size() >= 2) {
                            JijiaOpenApi temp = tempList.get(1);
                            // 内容
                            String[] content = orderContent(temp);
                            // 关联出这笔 sku(erp) + 销售站点的 pdm的负责人
                            sentFeishu(pdmTaskListMap, entry.getKey(), "二次采购下单通知", content);
                        }
                    }
                }
            }
        }
        log.info("=====>处理 PDM & 积加二次采购订单 完成");
    }

    /**
     * 根据多个销售站点切分项目档案，每笔数据只带一个销售站点
     * @param projectList 项目列表
     */
    private List<PrjProject> getErpAndStationList(List<PrjProject> projectList) {
        List<PrjProject> resultList = new ArrayList<>();
        if (CollUtil.isNotEmpty(projectList)) {
            // 因为销售站点是多选存值的，所以这里将销售站点分出来
            for (PrjProject project : projectList) {
                // 筛选出 有msku编码 和销售站点 的项目档案
                if (CharSequenceUtil.isNotBlank(project.getErpMaterialMskuCode()) &&
                        CharSequenceUtil.isNotBlank(project.getSaleStationName())) {
                    String[] names = project.getSaleStationName().split(";");
                    for (String s : names) {
                        // 单独存 sid + msku编码 + 销售站点 + 负责人 就够了
                        PrjProject one = new PrjProject();
                        one.setProjectSid(project.getProjectSid()).setProjectLeaderSid(project.getProjectLeaderSid())
                                .setErpMaterialMskuCode(project.getErpMaterialMskuCode()).setSaleStationName(s);
                        resultList.add(one);
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * 一次采购到货
     * @param jijiaToken 积加接口的token
     */
    public void firstPurchaseArrivalNotice(String jijiaToken) {
        log.info("=====>开始处理 PDM & 积加一次采购到货通知");
        List<PrjProject> list = projectMapper.selectPrjProjectListAll(new PrjProject().setProjectType(ConstantsPdm.PROJECT_TYPE_SHIX).setProjectPhase(ConstantsPdm.PROJECT_PHASE_ZDSX)
                .setProjectStatusNot(ConstantsPdm.PROJECT_STATUS_YWC).setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setArrivalNoticeFlagFirstPurchase(ConstantsEms.NO).setClientId("30001"));
        List<PrjProject> projectList;
        if (CollUtil.isNotEmpty(list) && CharSequenceUtil.isNotBlank(jijiaToken)) {
            // 因为销售站点是多选存值的，所以这里将销售站点分出来
            projectList = getErpAndStationList(list);
            if (CollUtil.isNotEmpty(projectList)) {
                String[] mskuCodes = projectList.stream().map(PrjProject::getErpMaterialMskuCode).toArray(String[]::new);
                JijiaOpenApi jijiaOpenApi = new JijiaOpenApi();
                // 固定传值 3：待交货 5：变更中 6：交货中 7：全部交货 8：已完成
                jijiaOpenApi.setEventTypes(new String[]{"Receipts"});
                jijiaOpenApi.setMskus(mskuCodes);
                List<JijiaOpenApi> purchaseOrderList = new ArrayList<>();
                // 积加的一次采购到货数据
                try {
                    purchaseOrderList = jijiaOpenApiService.selectArrivalNoticeFlagFirstPurchase(jijiaOpenApi, jijiaToken);
                } catch (Exception e) {
                    log.error("获取积加新品开发-一次采购到货数据失败");
                }
                if (CollUtil.isNotEmpty(purchaseOrderList)) {
                    // 循环获取站点名称
                    int i = 0;
                    JijiaOpenApi getStationName = new JijiaOpenApi();
                    for (JijiaOpenApi openApi : purchaseOrderList) {
                        i+=1;
                        if (i%5 == 0) {
                            ThreadUtil.safeSleep(1000);
                        }
                        getStationName.setMsku(openApi.getMsku());
                        // 积加的商品列表数据
                        try {
                            List<JijiaOpenApi> productList = jijiaOpenApiService.selectProductList(getStationName, jijiaToken);
                            if (CollUtil.isNotEmpty(productList)) {
                                openApi.setArrivalMarketName(productList.get(0).getMarketName());
                            }
                        } catch (Exception e) {
                            e.getStackTrace();
                            log.error("获取积加商品列表数据数据失败:" + e.getMessage());
                        }
                    }
                    List<PrjProject> updList = new ArrayList<>();
                    // 积加的数据
                    Map<String, List<JijiaOpenApi>> jijiaListMap = purchaseOrderList.stream()
                            .collect(Collectors.groupingBy(o -> o.getMsku() +"-"+ o.getArrivalMarketName()));
                    Map<String, List<JijiaOpenApi>> needFeishuMap = new HashMap<>();
                    // pdm的数据
                    Map<String, List<PrjProject>> pdmListMap = projectList.stream()
                            .collect(Collectors.groupingBy(o -> o.getErpMaterialMskuCode() +"-"+ o.getSaleStationName()));
                    // 存放匹配上的PDM数据的 项目任务明细，需要 明细种的岗位信息 去 推送飞书
                    Map<String, List<PrjProjectTask>> pdmTaskListMap = new HashMap<>();
                    // 根据“msku编码 + 销售站点名称“，将积加返回的采购订单信息与1）获取的数据进行匹配，
                    // 若匹配上，则根据“项目sid“，将对应项目在项目档案表（s_prj_project）中的“一次采购到货通知标识“更新为“Y”
                    for (Map.Entry<String, List<PrjProject>> entry : pdmListMap.entrySet()) {
                        if (jijiaListMap.containsKey(entry.getKey()) && jijiaListMap.get(entry.getKey()) != null) {
                            updList.addAll(pdmListMap.get(entry.getKey()));
                            // 获取任务明细通知负责人
                            Long[] sidList = pdmListMap.get(entry.getKey()).stream().map(PrjProject::getProjectSid).toArray(Long[]::new);
                            List<PrjProjectTask> taskList = projectTaskMapper.selectPrjProjectTaskListAll(
                                    new PrjProjectTask().setProjectSidList(sidList));
                            // 如果任务明细是空的，则那项目档案的 负责人 写入 临时的任务明细表种
                            if (CollUtil.isEmpty(taskList)) {
                                entry.getValue().forEach(item->{
                                    if (item.getProjectLeaderSid() != null) {
                                        taskList.add(new PrjProjectTask().setProjectLeaderSid(item.getProjectLeaderSid())
                                                .setClientId(item.getClientId()));
                                    }
                                });
                            }
                            pdmTaskListMap.put(entry.getKey(), taskList);
                            // 需要发推送的积加采购数据
                            needFeishuMap.put(entry.getKey(), jijiaListMap.get(entry.getKey()));
                        }
                    }
                    // 处理PDM数据
                    if (CollUtil.isNotEmpty(updList)) {
                        Long[] sidList = updList.stream().map(PrjProject::getProjectSid).toArray(Long[]::new);
                        projectMapper.updatePrjProject(new PrjProject().setProjectSidList(sidList)
                                .setArrivalNoticeFlagFirstPurchase(ConstantsEms.YES).setArrivalNoticeFlagFirstPurchaseUpdateDate(new Date()));
                    }
                    // 第一笔订单明细 并用飞书推送，遍历积加的订单数据，推送的也是积加的订单数据
                    for (Map.Entry<String, List<JijiaOpenApi>> entry : needFeishuMap.entrySet()) {
                        // 得到第一笔收货信息
                        List<JijiaOpenApi> tempList = needFeishuMap.get(entry.getKey());
                        tempList = tempList.stream().sorted(Comparator.comparing(JijiaOpenApi::getReportDate, Comparator.nullsLast(Date::compareTo))).collect(toList());
                        JijiaOpenApi temp = tempList.get(0);
                        // 内容
                        String[] content = daohuoContent(temp);
                        // 关联出这笔 msku编码 + 销售站点的 pdm的负责人
                        sentFeishu(pdmTaskListMap, entry.getKey(), "一次采购到货通知", content);
                    }

                }
            }
        }
        log.info("=====>处理 PDM & 积加一次采购到货通知 完成");
    }

    /**
     * 发送飞书通知
     * @param pdmListMap pdm数据
     * @param key sku(erp) + 销售站点
     * @param titleNotice 通知的标题
     */
    private void sentFeishu(Map<String, List<PrjProjectTask>> pdmListMap, String key, String titleNotice, String[] content) {
        List<PrjProjectTask> taskList = pdmListMap.get(key);
        if (CollUtil.isNotEmpty(taskList)) {
            List<BasStaff> staffList = new ArrayList<>();
            taskList.forEach(task -> {
                List<BasStaff> staffTaskList = getNoticeStaff(task, true, true, true);
                // 得到 userId
                if (CollUtil.isNotEmpty(staffTaskList)) {
                    staffList.addAll(staffTaskList);
                }
            });
            if (CollUtil.isNotEmpty(staffList)) {
                Long[] staffUserId = staffList.stream().map(BasStaff::getUserId).filter(Objects::nonNull).distinct().toArray(Long[]::new);
                if (ArrayUtil.isNotEmpty(staffUserId)) {
                    List<SysUser> userList = userMapper.selectSysUserListAll(new SysUser().setUserIdList(staffUserId));
                    for (SysUser user : userList) {
                        FeishuPushUtil.push(user.getFeishuOpenId(), user.getFeishuAppId(), user.getFeishuAppSecret(), titleNotice, content);
                        try {
                            MongodbUtil.insertUserLogAdmin(new Long(0), BusinessType.GRANT.getValue(), null,
                                    String.valueOf("id：" + user.getUserId() + ";名称：" + user.getNickName() + ";飞书id：" + user.getFeishuOpenId()),
                                    String.valueOf(titleNotice + ":" + ArrayUtil.toString(content)));
                        } catch (Exception e) {
                            log.error("操作日志写入报错");
                        }
                    }
                }
            }
        }
    }

    /**
     * 采购订单飞书通知模板
     * @param temp JijiaOpenApi
     */
    private String[] orderContent(JijiaOpenApi temp) {
        String[] content = new String[8];
        content[0] = "SKU：" + temp.getSku();
        content[1] = "产品：" + temp.getSkuName();
        String msku = temp.getMsku() == null ? "" : temp.getMsku();
        content[2] = "MSKU：" + msku;
        String arrivalMarketName = temp.getArrivalMarketName() == null ? "" : temp.getArrivalMarketName();
        content[3] = "店铺站点：" + arrivalMarketName;
        String arrivalWarehouseName = temp.getArrivalWarehouseName() == null ? "" : temp.getArrivalWarehouseName();
        content[4] = "目的仓：" + arrivalWarehouseName;
        String orderQuantity = temp.getOrderQuantity() == null ? "" : temp.getOrderQuantity();
        content[5] = "采购量：" + orderQuantity;
        String creator = temp.getCreator() == null ? "" : temp.getCreator();
        content[6] = "下单人：" + creator;
        String purchaseDate = temp.getCreatedAt() == null ? "" : FORMAT.format(temp.getCreatedAt());
        content[7] = "采购日期：" + purchaseDate;
        return content;
    }

    /**
     * 到货通知飞书通知模板
     * @param temp JijiaOpenApi
     */
    private String[] daohuoContent(JijiaOpenApi temp) {
        String[] content = new String[8];
        content[0] = "SKU：" + temp.getSku();
        content[1] = "产品：" + temp.getSkuName();
        String msku = temp.getMsku() == null ? "" : temp.getMsku();
        content[2] = "MSKU：" + msku;
        String arrivalMarketName = temp.getArrivalMarketName() == null ? "" : temp.getArrivalMarketName();
        content[3] = "店铺站点：" + arrivalMarketName;
        String warehouseName = temp.getWarehouseName() == null ? "" : temp.getWarehouseName();
        content[4] = "仓库：" + warehouseName;
        String referenceId = temp.getReferenceId() == null ? "" : temp.getReferenceId();
        content[5] = "货件ID：" + referenceId;
        String quantity = temp.getQuantity() == null ? "" : temp.getQuantity();
        content[6] = "收货量：" + quantity;
        String reportDate = temp.getReportDate() == null ? "" : FORMAT.format(temp.getReportDate());
        content[7] = "收货日期：" + reportDate;
        return content;
    }

    /**
     * 定时更新：每天早上6：00
     * 若系统默认设置(租户级)的“项目状态是否自动设置已完成”为“是”，
     * 获取该租户下项目状态为“进行中”的项目，判断项目下的所有任务的任务状态是否都为“已完成”，
     * 若是，自动修改该项目的项目状态为“已完成”，并记录操作日记（操作人设置成admin，日记详情文本：系统自动设置为“已完成”）；
     * 若系统默认设置(租户级)的“项目状态是否自动设置已完成”为“否”或空，则不进行任何操作
     */
    @Scheduled(cron = "00 30 06 * * *")
    public void autoSetProjectStatus() {
        String title = PrjProjectServiceImpl.TITLE;
        // 查询获取该租户下项目状态为“进行中”的项目，判断项目下的所有任务的任务状态是否都为“已完成”，
        List<PrjProject> projectList = projectMapper.selectPrjProjectNeedComplete(new PrjProject());
        if (CollUtil.isNotEmpty(projectList)) {
            Long[] projectSidList = projectList.stream().map(PrjProject::getProjectSid).toArray(Long[]::new);
            // 自动修改该项目的项目状态为“已完成”
            projectMapper.updatePrjProject(new PrjProject().setProjectSidList(projectSidList)
                    .setProjectStatus(ConstantsPdm.PROJECT_STATUS_YWC));
            // 操作日志
            for (Long sid : projectSidList) {
                MongodbUtil.insertUserLogAdmin(sid, BusinessType.QITA.getValue(), null,
                        title, "系统自动设置为“已完成”");
            }
        }

    }

    /**
     * 定时时间：6：30
     * 项目状态为“进行中”且处理状态为“已确认”的项目档案下，
     * 若存在任务状态为“未开始”但任务的计划开始日期小于等于当前日期的任务，自动修改其任务状态为“进行中”
     * 项目档案需记录操作日志如下：
     * 定时任务，修改任务状态（更改前：未开始，更改后：进行中）
     */
    @Scheduled(cron = "00 35 06 * * *")
    public void autoSetProjectStatus2() {
        List<SysDefaultSettingClient> settingClient = settingClientMapper.selectSysDefaultSettingClientAll(new SysDefaultSettingClient()
                .setIsAutoSetProjectTaskStatus(ConstantsEms.YES));
        if (CollUtil.isEmpty(settingClient)) {
            return;
        }
        List<PrjProjectTask> taskList = projectTaskMapper.selectPrjProjectTaskListAll(new PrjProjectTask()
                .setProjectStatus(ConstantsPdm.PROJECT_STATUS_JXZ).setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setTaskStatus(ConstantsPdm.PROJECT_TASK_WKS));
        if (CollUtil.isNotEmpty(taskList)) {
            Date nowDate = new Date();
            Map<String, String> map = settingClient.stream().collect(Collectors.toMap(SysDefaultSettingClient::getClientId,
                    SysDefaultSettingClient::getIsAutoSetProjectTaskStatus, (key1, key2) -> key2));
            taskList = taskList.stream().filter(o -> ConstantsEms.YES.equals(map.get(o.getClientId())) && nowDate.compareTo(o.getPlanStartDate()) >= 0).collect(toList());
            if (CollUtil.isNotEmpty(taskList)) {
                Long[] taskSidList = taskList.stream().map(PrjProjectTask::getProjectTaskSid).toArray(Long[]::new);
                projectTaskMapper.update(null, new UpdateWrapper<PrjProjectTask>().lambda()
                        .in(PrjProjectTask::getProjectTaskSid, taskSidList)
                        .set(PrjProjectTask::getTaskStatus, ConstantsPdm.PROJECT_TASK_JXZ));
                for (int i = 0; i < taskList.size(); i++) {
                    String name = taskList.get(i).getTaskName() == null ? " ": taskList.get(i).getTaskName();
                    String remark = "定时任务，修改任务" + name + "的任务状态（更改前：未开始，更改后：进行中）";
                    MongodbUtil.insertUserLogAdmin(taskList.get(i).getProjectSid(), BusinessType.QITA.getValue(), null, "项目档案", remark);
                    MongodbUtil.insertUserLogAdmin(taskList.get(i).getProjectTaskSid(), BusinessType.QITA.getValue(), null, "项目档案-任务", remark);
                }
            }
        }
    }
}
