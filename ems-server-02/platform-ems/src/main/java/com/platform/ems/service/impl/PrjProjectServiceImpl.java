package com.platform.ems.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.api.service.RemoteMenuService;
import com.platform.api.service.RemoteSystemService;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.document.UserOperLog;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.entity.SysDefaultSettingSystem;
import com.platform.common.core.domain.entity.SysMenu;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsPdm;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.form.PrjProjectExecuteCondition;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConSaleStation;
import com.platform.ems.plug.mapper.ConSaleStationMapper;
import com.platform.ems.service.*;
import com.platform.ems.task.pdm.PrjProjectWarningTask;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 项目档案Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-08
 */
@Service
@SuppressWarnings("all")
public class PrjProjectServiceImpl extends ServiceImpl<PrjProjectMapper, PrjProject> implements IPrjProjectService {
    @Autowired
    private PrjProjectMapper prjProjectMapper;
    @Autowired
    private PrjProjectTaskMapper prjProjectTaskMapper;
    @Autowired
    private IPrjProjectTaskService prjProjectTaskService;
    @Autowired
    private PrjProjectAttachMapper prjProjectAttachMapper;
    @Autowired
    private PrjTaskTemplateItemMapper prjTaskTemplateItemMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private DevDevelopPlanMapper developPlanMapper;
    @Autowired
    private FrmDraftDesignMapper draftDesignMapper;
    @Autowired
    private FrmSampleReviewMapper sampleReviewMapper;
    @Autowired
    private FrmPhotoSampleGainMapper photoSampleGainMapper;
    @Autowired
    private FrmDocumentVisionMapper documentVisionMapper;
    @Autowired
    private FrmArrivalNoticeMapper arrivalNoticeMapper;
    @Autowired
    private FrmNewproductTrialsalePlanMapper newproductTrialsalePlanMapper;
    @Autowired
    private FrmTrialsaleResultMapper trialsaleResultMapper;
    @Autowired
    private BasStaffMapper basStaffMapper;
    @Autowired
    private ConSaleStationMapper conSaleStationMapper;
    @Autowired
    private SysDefaultSettingClientMapper settingClientMapper;

    @Autowired
    private RemoteMenuService remoteMenuService;
    @Autowired
    private RemoteSystemService remoteSystemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private IFrmDraftDesignService draftDesignService;
    @Autowired
    private IFrmSampleReviewService sampleReviewService;
    @Autowired
    private IFrmPhotoSampleGainService photoSampleGainService;
    @Autowired
    private IFrmDocumentVisionService documentVisionService;
    @Autowired
    private IFrmArrivalNoticeService arrivalNoticeService;
    @Autowired
    private IFrmNewproductTrialsalePlanService newproductTrialsalePlanService;
    @Autowired
    private IFrmTrialsaleResultService trialsaleResultService;

    @Autowired
    private PrjProjectWarningTask projectWarningTask;

    public static final String TITLE = "项目档案";

    /**
     * 查询项目档案
     *
     * @param projectSid 项目档案ID
     * @return 项目档案
     */
    @Override
    public PrjProject selectPrjProjectById(Long projectSid) {
        PrjProject prjProject = prjProjectMapper.selectPrjProjectById(projectSid);
        if (prjProject != null) {
            this.getDateList(prjProject);
        }
        prjProject.setTaskList(new ArrayList<>());
        prjProject.setAttachmentList(new ArrayList<>());
        // 明细列表
        List<PrjProjectTask> taskList = prjProjectTaskService.selectPrjProjectTaskListById(projectSid);
        if (CollectionUtil.isNotEmpty(taskList)) {
            // 排序
            taskList = taskList.stream().sorted(Comparator.comparing(PrjProjectTask::getSort, Comparator.nullsFirst(BigDecimal::compareTo))
                    .thenComparing(PrjProjectTask::getTaskName, Comparator.nullsFirst(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
            ).collect(toList());
            prjProject.setTaskList(taskList);
        }
        // 附件
        List<PrjProjectAttach> attachmentList = prjProjectAttachMapper.selectPrjProjectAttachList(
                new PrjProjectAttach().setProjectSid(projectSid));
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            prjProject.setAttachmentList(attachmentList);
        }
        MongodbUtil.find(prjProject);
        return prjProject;
    }

    /**
     * 复制项目档案
     *
     * @param projectSid 项目档案ID
     * @return 项目档案
     */
    @Override
    public PrjProject copyPrjProjectById(Long projectSid) {
        PrjProject prjProject = prjProjectMapper.selectPrjProjectById(projectSid);
        if (prjProject != null) {
            prjProject.setCreateDate(null).setCreatorAccount(null).setCreatorAccountName(null)
                    .setUpdateDate(null).setUpdaterAccount(null).setUpdaterAccountName(null)
                    .setConfirmDate(null).setConfirmerAccount(null).setConfirmerAccountName(null);
            prjProject.setProjectSid(null).setProjectCode(null).setHandleStatus(ConstantsEms.SAVA_STATUS);
        }
        return prjProject;
    }

    /**
     * 查询项目档案列表
     *
     * @param prjProject 项目档案
     * @return 项目档案
     */
    @Override
    public List<PrjProject> selectPrjProjectList(PrjProject prjProject) {
        return prjProjectMapper.selectPrjProjectList(prjProject);
    }

    /**
     * 获取需要分号隔开的字段 ，分割成数组传给前端
     * @param prjProject
     */
    private void getDateList(PrjProject prjProject) {
        // 销售站点
        if (StrUtil.isNotBlank(prjProject.getSaleStationCode())) {
            String[] saleStationCode = prjProject.getSaleStationCode().split(";");
            prjProject.setSaleStationCodeList(saleStationCode);
        }
        // 市场区域
        if (StrUtil.isNotBlank(prjProject.getMarketRegion())) {
            String[] marketRegion = prjProject.getMarketRegion().split(";");
            prjProject.setMarketRegionList(marketRegion);
        }
    }

    /**
     * 获取需要分号隔开的字段，数组 转 字符串 存数据
     * @param prjProject
     */
    private void setDataList(PrjProject prjProject) {
        // 销售站点
        String saleStationCode = null;
        if (ArrayUtil.isNotEmpty(prjProject.getSaleStationCodeList())) {
            for (int i = 0; i < prjProject.getSaleStationCodeList().length; i++) {
                if (i > 0) {
                    saleStationCode = saleStationCode + ";" + prjProject.getSaleStationCodeList()[i];
                } else {
                    saleStationCode = prjProject.getSaleStationCodeList()[i];
                }
            }
        }
        prjProject.setSaleStationCode(saleStationCode);
        // 市场区域
        String marketRegion = null;
        if (ArrayUtil.isNotEmpty(prjProject.getMarketRegionList())) {
            for (int i = 0; i < prjProject.getMarketRegionList().length; i++) {
                if (i > 0) {
                    marketRegion = marketRegion + ";" + prjProject.getMarketRegionList()[i];
                } else {
                    marketRegion = prjProject.getMarketRegionList()[i];
                }
            }
        }
        prjProject.setMarketRegion(marketRegion);
    }

    /**
     * 校验编号不能重复
     *
     * @param prjProject 项目档案
     * @return 结果
     */
    private void judgeCode(PrjProject prjProject) {
        QueryWrapper<PrjProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PrjProject::getProjectCode, prjProject.getProjectCode());
        if (prjProject.getProjectSid() != null) {
            queryWrapper.lambda().ne(PrjProject::getProjectSid, prjProject.getProjectSid());
        }
        List<PrjProject> projectCodeList = prjProjectMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(projectCodeList)) {
            throw new BaseException("项目编号已存在！");
        }
    }

    /**
     * 校验名称不能重复
     *
     * @param prjProject 项目档案
     * @return 结果
     */
    private void judgeName(PrjProject prjProject) {
        if (StrUtil.isNotBlank(prjProject.getProjectName())) {
            QueryWrapper<PrjProject> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(PrjProject::getProjectName, prjProject.getProjectName());
            if (prjProject.getProjectSid() != null) {
                queryWrapper.lambda().ne(PrjProject::getProjectSid, prjProject.getProjectSid());
            }
            List<PrjProject> projectNameList = prjProjectMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(projectNameList)) {
                throw new BaseException("项目名称已存在！");
            }
        }
    }

    /**
     * 校验
     *
     * @param prjProject 项目档案
     * @return 结果
     */
    private void judge(PrjProject prjProject) {
        if (ConstantsEms.CHECK_STATUS.equals(prjProject.getHandleStatus())) {
            if (prjProject.getPlanEndDate() == null) {
                throw new BaseException("计划完成日期不能为空！");
            }
        }
    }

    /**
     * 设置部分字段的编码
     *
     * @param old 表中数据，new修改后数据
     * @return 结果
     */
    private void setData(PrjProject oldProject, PrjProject newProject) {
        // 项目负责人
        if (newProject.getProjectLeaderSid() != null && !newProject.getProjectLeaderSid().equals(oldProject.getProjectLeaderSid())) {
            BasStaff staff = basStaffMapper.selectById(newProject.getProjectLeaderSid());
            if (staff != null) {
                newProject.setProjectLeaderCode(staff.getStaffCode());
            } else {
                newProject.setProjectLeaderCode(null);
            }
        } else if (newProject.getProjectLeaderSid() == null) {
            newProject.setProjectLeaderCode(null);
        }
    }

    /**
     * 新增项目档案
     * 需要注意编码重复校验
     *
     * @param prjProject 项目档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPrjProject(PrjProject prjProject) {
        // 校验编号是否重复
        this.judgeCode(prjProject);
        // 校验名称是否重复
        if (StrUtil.isNotBlank(prjProject.getProjectName()) && !BusinessType.IMPORT.getValue().equals(prjProject.getImportType())) {
            this.judgeName(prjProject);
        }
        // 其它校验
        judge(prjProject);
        // 字段数据
        this.setData(prjProject);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(prjProject.getHandleStatus())) {
            prjProject.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 写入部分字段的code
        this.setData(new PrjProject(), prjProject);
        int row = prjProjectMapper.insert(prjProject);
        if (row > 0) {
            // 主要获取编码
            PrjProject project = prjProjectMapper.selectById(prjProject.getProjectSid());
            prjProject.setProjectCode(project.getProjectCode());
            // 写入明细
            if (CollectionUtil.isNotEmpty(prjProject.getTaskList())) {
                prjProjectTaskService.insertPrjProjectTaskList(prjProject);
            }
            // 写入附件
            if (CollectionUtil.isNotEmpty(prjProject.getAttachmentList())) {
                prjProject.getAttachmentList().forEach(item->{
                    item.setProjectSid(prjProject.getProjectSid());
                    item.setProjectCode(Long.parseLong(prjProject.getProjectCode()));
                });
                prjProjectAttachMapper.inserts(prjProject.getAttachmentList());
            }
            // 待办
            if (ConstantsEms.SAVA_STATUS.equals(prjProject.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_PRJ_PROJECT)
                        .setDocumentSid(prjProject.getProjectSid());
                sysTodoTask.setTitle("项目档案" + project.getProjectCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(project.getProjectCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                // 获取菜单id
                SysMenu menu = new SysMenu();
                menu.setMenuName(ConstantsWorkbench.TODO_PRJ_PROJECT);
                try {
                    menu = remoteMenuService.getInfoByName(menu).getData();
                } catch (Exception e){
                    log.warn(ConstantsWorkbench.TODO_PRJ_PROJECT + "菜单获取失败！");
                }
                if (menu != null && menu.getMenuId() != null) {
                    sysTodoTask.setMenuId(menu.getMenuId());
                }
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new PrjProject(), prjProject);
            MongodbDeal.insert(prjProject.getProjectSid(), prjProject.getHandleStatus(), msgList, TITLE, null, prjProject.getImportType());
        }
        return row;
    }

    /**
     * 字段数据录入更新
     *
     * @param prjProject 项目档案
     * @return 结果
     */
    private void setData(PrjProject prjProject) {
        // 设置需要分号隔开的字段
        this.setDataList(prjProject);
        // 去掉首尾空格
        if (StrUtil.isNotBlank(prjProject.getErpMaterialMskuCode())) {
            String code = prjProject.getErpMaterialMskuCode();
            code = code.trim();
            prjProject.setErpMaterialMskuCode(code);
        }
        if (StrUtil.isNotBlank(prjProject.getErpMaterialSkuBarcode())) {
            String code = prjProject.getErpMaterialSkuBarcode();
            code = code.trim();
            prjProject.setErpMaterialSkuBarcode(code);
        }
        if (StrUtil.isNotBlank(prjProject.getProductCode())) {
            String code = prjProject.getProductCode();
            code = code.trim();
            prjProject.setProductCode(code);
        }
        // 所属年月项目
        if (prjProject.getPlanStartDate() != null) {
            prjProject.setYearmonthProject(DateUtil.format(prjProject.getPlanStartDate(), "yyyy-MM"));
        } else {
            prjProject.setYearmonthProject(null);
        }
        //
        if (prjProject.getProjectSid() == null) {
            // 设置即将到期提醒天数项目
            SysDefaultSettingClient client = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            if (client != null && client.getToexpireDaysProject() != null) {
                prjProject.setToexpireDaysProj(client.getToexpireDaysProject().intValue());
            }
            else {
                SysDefaultSettingSystem system = settingClientMapper.selectSysDefaultSettingSystem();
                if (system != null && system.getToexpireDaysProject() != null) {
                    prjProject.setToexpireDaysProj(system.getToexpireDaysProject().intValue());
                }
                else {
                    prjProject.setToexpireDaysProj(15);
                }
            }
        }
        // 明细行处理
        if (CollectionUtil.isNotEmpty(prjProject.getTaskList())) {
            prjProject.getTaskList().forEach(item->{
                String preTask = null;
                // 前置节点处理
                if (ArrayUtil.isNotEmpty(item.getPreTaskList())) {
                    preTask = "";
                    for (int i = 0; i < item.getPreTaskList().length; i++) {
                        preTask = preTask + item.getPreTaskList()[i] + ";";
                    }
                }
                item.setPreTask(preTask);
            });
        }
    }

    /**
     * 批量修改附件信息
     *
     * @param prjProject 项目档案
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePrjProjectAttach(PrjProject prjProject) {
        // 先删后加
        prjProjectAttachMapper.delete(new QueryWrapper<PrjProjectAttach>().lambda()
                .eq(PrjProjectAttach::getProjectSid, prjProject.getProjectSid()));
        if (CollectionUtil.isNotEmpty(prjProject.getAttachmentList())) {
            prjProject.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getProjectAttachSid() == null) {
                    att.setProjectSid(prjProject.getProjectSid());
                    att.setProjectCode(Long.parseLong(prjProject.getProjectCode()));
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            prjProjectAttachMapper.inserts(prjProject.getAttachmentList());
        }
    }

    /**
     * 修改项目档案
     *
     * @param prjProject 项目档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePrjProject(PrjProject prjProject) {
        PrjProject original = prjProjectMapper.selectPrjProjectById(prjProject.getProjectSid());
        // 校验编号不能重复
        if (!prjProject.getProjectCode().equals(original.getProjectCode())) {
            this.judgeCode(prjProject);
        }
        // 校验名称不能重复
        if (StrUtil.isNotBlank(prjProject.getProjectName()) && !prjProject.getProjectName().equals(original.getProjectName())) {
            this.judgeName(prjProject);
        }
        // 其它校验
        judge(prjProject);
        // 字段数据
        this.setData(prjProject);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(prjProject.getHandleStatus())) {
            prjProject.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, prjProject);
        if (CollectionUtil.isNotEmpty(msgList)) {
            prjProject.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 写入部分字段的code
        this.setData(new PrjProject(), prjProject);
        // 更新主表
        int row = prjProjectMapper.updateAllById(prjProject);
        if (row > 0) {
            // 修改明细
            prjProjectTaskService.updatePrjProjectTaskList(prjProject);
            // 修改附件
            this.updatePrjProjectAttach(prjProject);
            // 不是保存状态删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(prjProject.getHandleStatus())) {
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, prjProject.getProjectSid())
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                        .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_PROJECT));
            }
            if (ConstantsEms.CHECK_STATUS.equals(prjProject.getHandleStatus())) {
                // 修改对应试销档案的 SPU字段 和 ERP字段
                if (prjProject.getProductCode() != null && !prjProject.getProductCode().equals(original.getProductCode())) {
                    LambdaUpdateWrapper<PrjProject> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.eq(PrjProject::getPreProjectSid, prjProject.getProjectSid());
                    try {
                        BasMaterial material = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                                .eq(BasMaterial::getMaterialCode, prjProject.getProductCode()));
                        if (material != null) {
                            prjProject.setProductSid(material.getMaterialSid());
                            updateWrapper.set(PrjProject::getProductSid, material.getMaterialSid());
                        }
                    } catch (Exception e) {
                        log.warn("系统中该SPU： " + prjProject.getProductCode() + "存在多笔");
                    }
                    updateWrapper.set(PrjProject::getProductCode, prjProject.getProductCode());
                    prjProjectMapper.update(null, updateWrapper);
                }
                if (StrUtil.isNotBlank(prjProject.getErpMaterialSkuBarcode()) && !prjProject.getErpMaterialSkuBarcode().equals(original.getErpMaterialSkuBarcode())) {
                    LambdaUpdateWrapper<PrjProject> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.eq(PrjProject::getPreProjectSid, prjProject.getProjectSid());
                    updateWrapper.set(PrjProject::getErpMaterialSkuBarcode, prjProject.getErpMaterialSkuBarcode());
                    prjProjectMapper.update(null, updateWrapper);
                }
            }
            // 变更时记录部分字段变更说明
            String remark = "";
            if (ConstantsEms.CHECK_STATUS.equals(original.getHandleStatus())) {
                SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
                // 直接拿操作日志的字段
                if (CollectionUtil.isNotEmpty(msgList)) {
                    // 项目状态数据字典
                    List<DictData> projectStatusList = sysDictDataService.selectDictData("s_project_status");
                    Map<String, String> projectStatusMaps = projectStatusList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                    projectStatusMaps.put("", "");
                    // 试销类型数据字典
                    List<DictData> trialsaleTypeList = sysDictDataService.selectDictData("s_trialsale_type");
                    Map<String, String> trialsaleTypeMaps = trialsaleTypeList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                    trialsaleTypeMaps.put("", "");
                    // 所属阶段数据字典
                    List<DictData> projectPhaseList = sysDictDataService.selectDictData("s_project_phase");
                    Map<String, String> projectPhaseMaps = projectPhaseList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                    projectPhaseMaps.put("", "");
                    for (OperMsg operMsg : msgList) {
                        // 年度
                        if ("year".equals(operMsg.getName())) {
                            remark = remark + "年度字段变更，更新前：" + operMsg.getOldValue() + "，更新后：" + operMsg.getNewValue() + "\n";
                            continue;
                        }
                        // 项目状态
                        else if ("projectStatus".equals(operMsg.getName())) {
                            remark = remark + "项目状态字段变更，更新前：" + projectStatusMaps.get(operMsg.getOldValue()) + "，更新后：" + projectStatusMaps.get(operMsg.getNewValue()) + "\n";
                            continue;
                        }
                        // 试销类型
                        else if ("trialsaleType".equals(operMsg.getName())) {
                            remark = remark + "试销类型字段变更，更新前：" + trialsaleTypeMaps.get(operMsg.getOldValue()) + "，更新后：" + trialsaleTypeMaps.get(operMsg.getNewValue()) + "\n";
                            continue;
                        }
                        // 所属阶段
                        else if ("projectPhase".equals(operMsg.getName())) {
                            remark = remark + "所属阶段字段变更，更新前：" + projectPhaseMaps.get(operMsg.getOldValue()) + "，更新后：" + projectPhaseMaps.get(operMsg.getNewValue()) + "\n";
                            continue;
                        }
                        // 商品MSKU编码(ERP)
                        else if ("erpMaterialMskuCode".equals(operMsg.getName())) {
                            remark = remark + "商品MSKU编码(ERP)字段变更，更新前：" + operMsg.getOldValue() + "，更新后：" + operMsg.getNewValue() + "\n";
                            continue;
                        }
                    }
                }
                // 计划开始日期
                String oldPlanBeginData = original.getPlanStartDate() == null ? "" : FORMAT.format(original.getPlanStartDate());
                String newPlanBeginData = prjProject.getPlanStartDate() == null ? "" : FORMAT.format(prjProject.getPlanStartDate());
                if ((StrUtil.isNotBlank(oldPlanBeginData) && !oldPlanBeginData.equals(newPlanBeginData))
                        || (StrUtil.isBlank(oldPlanBeginData) && StrUtil.isNotBlank(newPlanBeginData))) {
                    remark = remark + "计划开始日期字段变更，更新前：" + oldPlanBeginData + "，更新后：" + newPlanBeginData + "\n";

                }
                // 计划完成日期
                String oldPlanEndData = original.getPlanEndDate() == null ? "" : FORMAT.format(original.getPlanEndDate());
                String newPlanEndData = prjProject.getPlanEndDate() == null ? "" : FORMAT.format(prjProject.getPlanEndDate());
                if ((StrUtil.isNotBlank(oldPlanEndData) && !oldPlanEndData.equals(newPlanEndData))
                        || (StrUtil.isBlank(oldPlanEndData) && StrUtil.isNotBlank(newPlanEndData))) {
                    remark = remark + "计划完成日期字段变更，更新前：" + oldPlanEndData + "，更新后：" + newPlanEndData + "\n";

                }
                // 项目负责人
                if ((original.getProjectLeaderSid() != null && !original.getProjectLeaderSid().equals(prjProject.getProjectLeaderSid()))
                        || (original.getProjectLeaderSid() == null && prjProject.getProjectLeaderSid() != null)) {
                    List<BasStaff> staffList = basStaffMapper.selectList(new QueryWrapper<BasStaff>().lambda()
                            .eq(BasStaff::getStaffSid, original.getProjectLeaderSid()).or().eq(BasStaff::getStaffSid, prjProject.getProjectLeaderSid()));
                    Map<Long, String> staffMaps = staffList.stream().collect(Collectors.toMap(BasStaff::getStaffSid, BasStaff::getStaffName, (key1, key2) -> key2));
                    String oldData = original.getProjectLeaderSid() == null ? "" : staffMaps.get(original.getProjectLeaderSid());
                    String newData = prjProject.getProjectLeaderSid() == null ? "" : staffMaps.get(prjProject.getProjectLeaderSid());
                    remark = remark + "项目负责人字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                }
                getDateList(original);
                // 市场区域
                if ((original.getMarketRegion() != null && !original.getMarketRegion().equals(prjProject.getMarketRegion()))
                        || (original.getMarketRegion() == null && prjProject.getMarketRegion() != null)) {
                    // 市场区域数据字典
                    List<DictData> marketRegionList = sysDictDataService.selectDictData("s_market_region");
                    Map<String, String> marketRegionMaps = marketRegionList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                    marketRegionMaps.put("", "");
                    String marketRegionOld = "";
                    if (ArrayUtil.isNotEmpty(original.getMarketRegionList())) {
                        for (int i = 0; i < original.getMarketRegionList().length; i++) {
                            if (i > 0) {
                                marketRegionOld = marketRegionOld + ";" + marketRegionMaps.get(original.getMarketRegionList()[i]);
                            } else {
                                marketRegionOld = marketRegionMaps.get(original.getMarketRegionList()[i]);
                            }
                        }
                    }
                    String marketRegionNew = "";
                    if (ArrayUtil.isNotEmpty(prjProject.getMarketRegionList())) {
                        for (int i = 0; i < prjProject.getMarketRegionList().length; i++) {
                            if (i > 0) {
                                marketRegionNew = marketRegionNew + ";" + marketRegionMaps.get(prjProject.getMarketRegionList()[i]);
                            } else {
                                marketRegionNew = marketRegionMaps.get(prjProject.getMarketRegionList()[i]);
                            }
                        }
                    }
                    remark = remark + "市场区域字段变更，更新前：" + marketRegionOld + "，更新后：" + marketRegionNew + "\n";
                }
                // 销售站点/网店
                if ((original.getSaleStationCode() != null && !original.getSaleStationCode().equals(prjProject.getSaleStationCode()))
                        || (original.getSaleStationCode() == null && prjProject.getSaleStationCode() != null)) {
                    // 销售站点配置档案
                    List<ConSaleStation> conSaleStationList = conSaleStationMapper.selectList(new QueryWrapper<>());
                    Map<Long, String> saleStationMaps = conSaleStationList.stream().collect(Collectors.toMap(ConSaleStation::getCode, ConSaleStation::getName, (key1, key2) -> key2));
                    saleStationMaps.put(null, "");
                    String saleStationNameOld = "";
                    if (ArrayUtil.isNotEmpty(original.getSaleStationCodeList())) {
                        for (int i = 0; i < original.getSaleStationCodeList().length; i++) {
                            if (i > 0) {
                                saleStationNameOld = saleStationNameOld + ";" + saleStationMaps.get(Long.parseLong(original.getSaleStationCodeList()[i]));
                            } else {
                                saleStationNameOld = saleStationMaps.get(Long.parseLong(original.getSaleStationCodeList()[i]));
                            }
                        }
                    }
                    String saleStationNameNew = "";
                    if (ArrayUtil.isNotEmpty(prjProject.getSaleStationCodeList())) {
                        for (int i = 0; i < prjProject.getSaleStationCodeList().length; i++) {
                            if (i > 0) {
                                saleStationNameNew = saleStationNameNew + ";" + saleStationMaps.get(Long.parseLong(prjProject.getSaleStationCodeList()[i]));
                            } else {
                                saleStationNameNew = saleStationMaps.get(Long.parseLong(prjProject.getSaleStationCodeList()[i]));
                            }
                        }
                    }
                    remark = remark + "销售站点/网店字段变更，更新前：" + saleStationNameOld + "，更新后：" + saleStationNameNew + "\n";
                }
            }
            //插入日志
            MongodbDeal.update(prjProject.getProjectSid(), original.getHandleStatus(), prjProject.getHandleStatus(), msgList, TITLE, remark);
        }
        return row;
    }

    /**
     * 变更项目档案
     *
     * @param prjProject 项目档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePrjProject(PrjProject prjProject) {
        PrjProject response = prjProjectMapper.selectPrjProjectById(prjProject.getProjectSid());
        // 校验编号不能重复
        if (!prjProject.getProjectCode().equals(response.getProjectCode())) {
            this.judgeCode(prjProject);
        }
        // 校验名称不能重复
        if (StrUtil.isNotBlank(prjProject.getProjectName()) && !prjProject.getProjectName().equals(response.getProjectName())) {
            this.judgeName(prjProject);
        }
        // 其它校验
        judge(prjProject);
        // 字段数据
        this.setData(prjProject);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, prjProject);
        if (CollectionUtil.isNotEmpty(msgList)) {
            prjProject.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 写入部分字段的code
        this.setData(new PrjProject(), prjProject);
        // 更新主表
        int row = prjProjectMapper.updateAllById(prjProject);
        if (row > 0) {
            // 修改明细
            prjProjectTaskService.updatePrjProjectTaskList(prjProject);
            // 修改附件
            this.updatePrjProjectAttach(prjProject);
            // 修改对应试销档案的 SPU字段 和 ERP字段
            if (prjProject.getProductCode() != null && !prjProject.getProductCode().equals(response.getProductCode())) {
                LambdaUpdateWrapper<PrjProject> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(PrjProject::getPreProjectSid, prjProject.getProjectSid());
                try {
                    BasMaterial material = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                            .eq(BasMaterial::getMaterialCode, prjProject.getProductCode()));
                    if (material != null) {
                        prjProject.setProductSid(material.getMaterialSid());
                        updateWrapper.set(PrjProject::getProductSid, material.getMaterialSid());
                    }
                } catch (Exception e) {
                    log.warn("系统中该SPU： " + prjProject.getProductCode() + "存在多笔");
                }
                updateWrapper.set(PrjProject::getProductCode, prjProject.getProductCode());
                prjProjectMapper.update(null, updateWrapper);
            }
            if (StrUtil.isNotBlank(prjProject.getErpMaterialSkuBarcode()) && !prjProject.getErpMaterialSkuBarcode().equals(response.getErpMaterialSkuBarcode())) {
                LambdaUpdateWrapper<PrjProject> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(PrjProject::getPreProjectSid, prjProject.getProjectSid());
                updateWrapper.set(PrjProject::getErpMaterialSkuBarcode, prjProject.getErpMaterialSkuBarcode());
                prjProjectMapper.update(null, updateWrapper);
            }
            //插入日志
            MongodbUtil.insertUserLog(prjProject.getProjectSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除项目档案
     *
     * @param projectSids 需要删除的项目档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePrjProjectByIds(List<Long> projectSids) {
        List<PrjProject> list = prjProjectMapper.selectList(new QueryWrapper<PrjProject>()
                .lambda().in(PrjProject::getProjectSid, projectSids));
        // 删除校验
        list = list.stream().filter(o-> !HandleStatus.SAVE.getCode().equals(o.getHandleStatus()) && !HandleStatus.RETURNED.getCode().equals(o.getHandleStatus()))
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(list)) {
            throw new BaseException("只有保存或者已退回状态才允许删除！");
        }
        int row = prjProjectMapper.deleteBatchIds(projectSids);
        if (row > 0) {
            // 删除明细
            prjProjectTaskService.deletePrjProjectTaskByProject(projectSids);
            // 删除附件
            prjProjectAttachMapper.delete(new QueryWrapper<PrjProjectAttach>().lambda()
                    .in(PrjProjectAttach::getProjectSid, projectSids));
            // 删除待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, projectSids)
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_PROJECT));
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new PrjProject());
                MongodbUtil.insertUserLog(o.getProjectSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
            // 删除八个单据
            deleteDocByProject(projectSids);
        }
        return row;
    }

    /**
     * 删除项目同步删除八个单据
     * @param projectSids
     */
    public void deleteDocByProject(List<Long> projectSids) {
        // 图稿绘制
        List<FrmDraftDesign> draftDesignList = draftDesignMapper.selectList(new QueryWrapper<FrmDraftDesign>().lambda()
                .in(FrmDraftDesign::getProjectSid, projectSids));
        if (CollectionUtil.isNotEmpty(draftDesignList)) {
            List<Long> sids = draftDesignList.stream().map(FrmDraftDesign::getDraftDesignSid).collect(toList());
            draftDesignService.deleteFrmDraftDesignByIds(sids);
        }
        // 视觉设计
        List<FrmPhotoSampleGain> photoSampleGainList = photoSampleGainMapper.selectList(new QueryWrapper<FrmPhotoSampleGain>().lambda()
                .in(FrmPhotoSampleGain::getProjectSid, projectSids));
        if (CollectionUtil.isNotEmpty(photoSampleGainList)) {
            List<Long> sids = photoSampleGainList.stream().map(FrmPhotoSampleGain::getPhotoSampleGainSid).collect(toList());
            photoSampleGainService.deleteFrmPhotoSampleGainByIds(sids);
        }
        // 文案脚本
        List<FrmDocumentVision> documentVisionList = documentVisionMapper.selectList(new QueryWrapper<FrmDocumentVision>().lambda()
                .in(FrmDocumentVision::getProjectSid, projectSids));
        if (CollectionUtil.isNotEmpty(documentVisionList)) {
            List<Long> sids = documentVisionList.stream().map(FrmDocumentVision::getDocumentVisionSid).collect(toList());
            documentVisionService.deleteFrmDocumentVisionByIds(sids);
        }
        // 样品初审单终审单
        List<FrmSampleReview> sampleReviewList = sampleReviewMapper.selectList(new QueryWrapper<FrmSampleReview>().lambda()
                .in(FrmSampleReview::getProjectSid, projectSids));
        if (CollectionUtil.isNotEmpty(sampleReviewList)) {
            List<Long> sids = sampleReviewList.stream().map(FrmSampleReview::getSampleReviewSid).collect(toList());
            sampleReviewService.deleteFrmSampleReviewByIds(sids);
        }
        // 到货通知单
        List<FrmArrivalNotice> arrivalNoticeList = arrivalNoticeMapper.selectList(new QueryWrapper<FrmArrivalNotice>().lambda()
                .in(FrmArrivalNotice::getProjectSid, projectSids));
        if (CollectionUtil.isNotEmpty(arrivalNoticeList)) {
            List<Long> sids = arrivalNoticeList.stream().map(FrmArrivalNotice::getArrivalNoticeSid).collect(toList());
            arrivalNoticeService.deleteFrmArrivalNoticeByIds(sids);
        }
        // 新品试销计划单
        List<FrmNewproductTrialsalePlan> newproductTrialsalePlanList = newproductTrialsalePlanMapper.selectList(
                new QueryWrapper<FrmNewproductTrialsalePlan>().lambda().in(FrmNewproductTrialsalePlan::getProjectSid, projectSids));
        if (CollectionUtil.isNotEmpty(newproductTrialsalePlanList)) {
            List<Long> sids = newproductTrialsalePlanList.stream().map(FrmNewproductTrialsalePlan::getNewproductTrialsalePlanSid).collect(toList());
            newproductTrialsalePlanService.deleteFrmNewproductTrialsalePlanByIds(sids);
        }
        // 试销结果单
        List<FrmTrialsaleResult> trialsaleResultList = trialsaleResultMapper.selectList(
                new QueryWrapper<FrmTrialsaleResult>().lambda().in(FrmTrialsaleResult::getProjectSid, projectSids));
        if (CollectionUtil.isNotEmpty(trialsaleResultList)) {
            List<Long> sids = trialsaleResultList.stream().map(FrmTrialsaleResult::getTrialsaleResultSid).collect(toList());
            trialsaleResultService.deleteFrmTrialsaleResultByIds(sids);
        }
    }

    /**
     * 更改确认状态
     *
     * @param prjProject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(PrjProject prjProject) {
        int row = 0;
        Long[] sids = prjProject.getProjectSidList();
        if (sids != null && sids.length > 0) {
            // 更新
            LambdaUpdateWrapper<PrjProject> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(PrjProject::getProjectSid, sids);
            updateWrapper.set(PrjProject::getHandleStatus, prjProject.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(prjProject.getHandleStatus())) {
                // 校验计划完成日期
                List<PrjProject> prjProjectList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda()
                        .in(PrjProject::getProjectSid, sids).isNull(PrjProject::getPlanEndDate));
                if (CollectionUtil.isNotEmpty(prjProjectList)) {
                    throw new BaseException("存在计划完成日期为空的项目，确认失败！");
                }
                updateWrapper.set(PrjProject::getConfirmDate, new Date());
                updateWrapper.set(PrjProject::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            else if (ConstantsEms.INVALID_STATUS.equals(prjProject.getHandleStatus())) {
                List<PrjProject> prjProjectList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda()
                        .in(PrjProject::getProjectSid, sids).ne(PrjProject::getHandleStatus, ConstantsEms.CHECK_STATUS));
                if (CollectionUtil.isNotEmpty(prjProjectList)) {
                    throw new BaseException("仅“已确认”的项目档案，才允许作废操作!");
                }
            }
            row = prjProjectMapper.update(null, updateWrapper);
            if (row > 0) {
                // 删除待办
                if (ConstantsEms.CHECK_STATUS.equals(prjProject.getHandleStatus())) {
                    sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                            .in(SysTodoTask::getDocumentSid, sids)
                            .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                            .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_PROJECT));
                }
                if (ConstantsEms.INVALID_STATUS.equals(prjProject.getHandleStatus())) {
                    // 同时作废相关单据
                    // 图稿绘制
                    List<FrmDraftDesign> draftDesignList = draftDesignMapper.selectList(new QueryWrapper<FrmDraftDesign>().lambda()
                            .in(FrmDraftDesign::getProjectSid, sids).eq(FrmDraftDesign::getHandleStatus, ConstantsEms.CHECK_STATUS));
                    if (CollectionUtil.isNotEmpty(draftDesignList)) {
                        Long[] sidList = draftDesignList.stream().map(FrmDraftDesign::getDraftDesignSid).toArray(Long[]::new);
                        draftDesignMapper.update(null, new UpdateWrapper<FrmDraftDesign>()
                                .lambda().in(FrmDraftDesign::getDraftDesignSid, sidList).set(FrmDraftDesign::getHandleStatus, ConstantsEms.INVALID_STATUS));
                        for (Long sid : sidList) {
                            MongodbUtil.insertUserLog(sid, BusinessType.CANCEL.getValue(), null, "图稿绘制单", null);
                        }
                    }
                    // 视觉设计
                    List<FrmPhotoSampleGain> photoSampleGainList = photoSampleGainMapper.selectList(new QueryWrapper<FrmPhotoSampleGain>().lambda()
                            .in(FrmPhotoSampleGain::getProjectSid, sids).eq(FrmPhotoSampleGain::getHandleStatus, ConstantsEms.CHECK_STATUS));
                    if (CollectionUtil.isNotEmpty(photoSampleGainList)) {
                        Long[] sidList = photoSampleGainList.stream().map(FrmPhotoSampleGain::getPhotoSampleGainSid).toArray(Long[]::new);
                        photoSampleGainMapper.update(null, new UpdateWrapper<FrmPhotoSampleGain>()
                                .lambda().in(FrmPhotoSampleGain::getPhotoSampleGainSid, sidList).set(FrmPhotoSampleGain::getHandleStatus, ConstantsEms.INVALID_STATUS));
                        for (Long sid : sidList) {
                            MongodbUtil.insertUserLog(sid, BusinessType.CANCEL.getValue(), null, "视觉设计单", null);
                        }
                    }
                    // 文案脚本单
                    List<FrmDocumentVision> documentVisionList = documentVisionMapper.selectList(new QueryWrapper<FrmDocumentVision>().lambda()
                            .in(FrmDocumentVision::getProjectSid, sids).eq(FrmDocumentVision::getHandleStatus, ConstantsEms.CHECK_STATUS));
                    if (CollectionUtil.isNotEmpty(documentVisionList)) {
                        Long[] sidList = documentVisionList.stream().map(FrmDocumentVision::getDocumentVisionSid).toArray(Long[]::new);
                        documentVisionMapper.update(null, new UpdateWrapper<FrmDocumentVision>()
                                .lambda().in(FrmDocumentVision::getDocumentVisionSid, sidList).set(FrmDocumentVision::getHandleStatus, ConstantsEms.INVALID_STATUS));
                        for (Long sid : sidList) {
                            MongodbUtil.insertUserLog(sid, BusinessType.CANCEL.getValue(), null, "文案脚本单", null);
                        }
                    }
                    // 样品初审单/样品终审单
                    List<FrmSampleReview> sampleReviewList = sampleReviewMapper.selectList(new QueryWrapper<FrmSampleReview>().lambda()
                            .in(FrmSampleReview::getProjectSid, sids).eq(FrmSampleReview::getHandleStatus, ConstantsEms.CHECK_STATUS));
                    if (CollectionUtil.isNotEmpty(sampleReviewList)) {
                        Long[] sidList = sampleReviewList.stream().map(FrmSampleReview::getSampleReviewSid).toArray(Long[]::new);
                        sampleReviewMapper.update(null, new UpdateWrapper<FrmSampleReview>()
                                .lambda().in(FrmSampleReview::getSampleReviewSid, sidList).set(FrmSampleReview::getHandleStatus, ConstantsEms.INVALID_STATUS));
                        for (Long sid : sidList) {
                            MongodbUtil.insertUserLog(sid, BusinessType.CANCEL.getValue(), null, "样品评审单", null);
                        }
                    }
                    // 新品试销计划单
                    List<FrmNewproductTrialsalePlan> newproductTrialsalePlanList = newproductTrialsalePlanMapper.selectList(new QueryWrapper<FrmNewproductTrialsalePlan>().lambda()
                            .in(FrmNewproductTrialsalePlan::getProjectSid, sids).eq(FrmNewproductTrialsalePlan::getHandleStatus, ConstantsEms.CHECK_STATUS));
                    if (CollectionUtil.isNotEmpty(newproductTrialsalePlanList)) {
                        Long[] sidList = newproductTrialsalePlanList.stream().map(FrmNewproductTrialsalePlan::getNewproductTrialsalePlanSid).toArray(Long[]::new);
                        newproductTrialsalePlanMapper.update(null, new UpdateWrapper<FrmNewproductTrialsalePlan>()
                                .lambda().in(FrmNewproductTrialsalePlan::getNewproductTrialsalePlanSid, sidList).set(FrmNewproductTrialsalePlan::getHandleStatus, ConstantsEms.INVALID_STATUS));
                        for (Long sid : sidList) {
                            MongodbUtil.insertUserLog(sid, BusinessType.CANCEL.getValue(), null, "新品试销计划单", null);
                        }
                    }
                    // 到货通知单
                    List<FrmArrivalNotice> arrivalNoticeList = arrivalNoticeMapper.selectList(new QueryWrapper<FrmArrivalNotice>().lambda()
                            .in(FrmArrivalNotice::getProjectSid, sids).eq(FrmArrivalNotice::getHandleStatus, ConstantsEms.CHECK_STATUS));
                    if (CollectionUtil.isNotEmpty(arrivalNoticeList)) {
                        Long[] sidList = arrivalNoticeList.stream().map(FrmArrivalNotice::getArrivalNoticeSid).toArray(Long[]::new);
                        arrivalNoticeMapper.update(null, new UpdateWrapper<FrmArrivalNotice>()
                                .lambda().in(FrmArrivalNotice::getArrivalNoticeSid, sidList).set(FrmArrivalNotice::getHandleStatus, ConstantsEms.INVALID_STATUS));
                        for (Long sid : sidList) {
                            MongodbUtil.insertUserLog(sid, BusinessType.CANCEL.getValue(), null, "到货通知单", null);
                        }
                    }
                    // 试销结果单
                    List<FrmTrialsaleResult> trialsaleResultList = trialsaleResultMapper.selectList(new QueryWrapper<FrmTrialsaleResult>().lambda()
                            .in(FrmTrialsaleResult::getProjectSid, sids).eq(FrmTrialsaleResult::getHandleStatus, ConstantsEms.CHECK_STATUS));
                    if (CollectionUtil.isNotEmpty(trialsaleResultList)) {
                        Long[] sidList = trialsaleResultList.stream().map(FrmTrialsaleResult::getTrialsaleResultSid).toArray(Long[]::new);
                        trialsaleResultMapper.update(null, new UpdateWrapper<FrmTrialsaleResult>()
                                .lambda().in(FrmTrialsaleResult::getTrialsaleResultSid, sidList).set(FrmTrialsaleResult::getHandleStatus, ConstantsEms.INVALID_STATUS));
                        for (Long sid : sidList) {
                            MongodbUtil.insertUserLog(sid, BusinessType.CANCEL.getValue(), null, "试销结果单", null);
                        }
                    }
                    // 插入日志
                    for (Long id : sids) {
                        MongodbUtil.insertUserLog(id, BusinessType.CANCEL.getValue(), null, TITLE, prjProject.getCancelRemark());
                    }
                }
                else {
                    for (Long id : sids) {
                        // 插入日志
                        MongodbDeal.check(id, prjProject.getHandleStatus(), null, TITLE, null);
                    }
                }
            }
        }
        return row;
    }

    /**
     * 设置项目状态
     * @param prjProject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setProjectStatus(PrjProject prjProject) {
        if (prjProject.getProjectSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        if (StrUtil.isBlank(prjProject.getProjectStatus())) {
            throw new BaseException("项目状态不能为空！");
        }
        // 原数据
        List<PrjProject> projectList = prjProjectMapper.selectBatchIds(Arrays.asList(prjProject.getProjectSidList()));
        // 修改
        LambdaUpdateWrapper<PrjProject> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        if (StrUtil.isBlank(prjProject.getProjectStatus())) {
            prjProject.setProjectStatus(null);
        }
        // 项目状态
        updateWrapper.in(PrjProject::getProjectSid, prjProject.getProjectSidList()).set(PrjProject::getProjectStatus, prjProject.getProjectStatus());
        row = prjProjectMapper.update(new PrjProject(), updateWrapper);
        // 操作日志
        List<DictData> projectStatusList = sysDictDataService.selectDictData("s_project_status");
        projectStatusList = projectStatusList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
        Map<String,String> projectStatusMaps = projectStatusList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel,(key1, key2)->key2));
        for (int i = 0; i < projectList.size(); i++) {
            if (!prjProject.getProjectStatus().equals(projectList.get(i).getProjectStatus())) {
                PrjProject nowData = new PrjProject();
                BeanUtil.copyProperties(projectList.get(i), nowData);
                nowData.setProjectStatus(prjProject.getProjectStatus());
                List<OperMsg> msgList;
                msgList = BeanUtils.eq(projectList.get(i), nowData);
                String oldCode = projectList.get(i).getProjectStatus() == null ? "" : projectStatusMaps.get(projectList.get(i).getProjectStatus());
                String newCode = nowData.getProjectStatus() == null ? "" : projectStatusMaps.get(nowData.getProjectStatus());
                String remark = "项目状态变更，变更前：" + oldCode + "；变更后：" + newCode;
                MongodbUtil.insertUserLog(projectList.get(i).getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }

    /**
     * 设置项目优先级
     * @param prjProject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setPriority(PrjProject prjProject) {
        if (prjProject.getProjectSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        // 原数据
        List<PrjProject> projectList = prjProjectMapper.selectBatchIds(Arrays.asList(prjProject.getProjectSidList()));
        // 修改
        LambdaUpdateWrapper<PrjProject> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        if (StrUtil.isBlank(prjProject.getPriorityProject())) {
            prjProject.setPriorityProject(null);
        }
        // 项目状态
        updateWrapper.in(PrjProject::getProjectSid, prjProject.getProjectSidList()).set(PrjProject::getPriorityProject, prjProject.getPriorityProject());
        row = prjProjectMapper.update(new PrjProject(), updateWrapper);
        // 操作日志
        List<DictData> priorityList = sysDictDataService.selectDictData("s_urgency_type");
        priorityList = priorityList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
        Map<String,String> priorityMaps = priorityList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel,(key1, key2)->key2));
        for (int i = 0; i < projectList.size(); i++) {
            if ((prjProject.getPriorityProject() == null && projectList.get(i).getPriorityProject() != null)
                    || (prjProject.getPriorityProject() != null && !prjProject.getPriorityProject().equals(projectList.get(i).getPriorityProject()))) {
                PrjProject nowData = new PrjProject();
                BeanUtil.copyProperties(projectList.get(i), nowData);
                nowData.setPriorityProject(prjProject.getPriorityProject());
                List<OperMsg> msgList;
                msgList = BeanUtils.eq(projectList.get(i), nowData);
                String oldCode = projectList.get(i).getPriorityProject() == null ? "" : priorityMaps.get(projectList.get(i).getPriorityProject());
                String newCode = nowData.getPriorityProject() == null ? "" : priorityMaps.get(nowData.getPriorityProject());
                String remark = "项目优先级变更，变更前：" + oldCode + "；变更后：" + newCode;
                MongodbUtil.insertUserLog(projectList.get(i).getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }

    /**
     * 设置即将到期提醒天数
     * @param prjProject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setToexpireDays(PrjProject prjProject) {
        if (prjProject.getProjectSidList().length == 0){
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<PrjProject> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //即将到期天数
        updateWrapper.in(PrjProject::getProjectSid, prjProject.getProjectSidList());
        updateWrapper.set(PrjProject::getToexpireDaysProj, prjProject.getToexpireDaysProj());
        row = prjProjectMapper.update(new PrjProject(), updateWrapper);
        return row;
    }

    /**
     * 设置开发计划
     * @param prjProject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setDevelopPlan(PrjProject prjProject) {
        if (prjProject.getProjectSidList().length == 0){
            throw new BaseException("请选择行！");
        }
        if (StrUtil.isBlank(prjProject.getDevelopPlanCode())) {
            throw new BaseException("开发计划号不能为空！");
        } else {
            List<DevDevelopPlan> planList = developPlanMapper.selectDevDevelopPlanListByCode(new DevDevelopPlan()
                            .setClientId(ApiThreadLocalUtil.get().getClientId())
                    .setDevelopPlanCode(prjProject.getDevelopPlanCode()));
            if (CollectionUtil.isEmpty(planList)) {
                throw new BaseException("开发计划号不存在！");
            }
            else if (!ConstantsEms.CHECK_STATUS.equals(planList.get(0).getHandleStatus())) {
                throw new BaseException("开发计划未确认，请核实！");
            }
            else {
                prjProject.setDevelopPlanSid(planList.get(0).getDevelopPlanSid());
                prjProject.setCategoryPlanSid(planList.get(0).getCategoryPlanSid());
                prjProject.setCategoryPlanCode(planList.get(0).getCategoryPlanCode());
            }
        }
        // 原数据
        List<PrjProject> projectList = prjProjectMapper.selectBatchIds(Arrays.asList(prjProject.getProjectSidList()));
        // 修改
        LambdaUpdateWrapper<PrjProject> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        // 开发计划号
        updateWrapper.in(PrjProject::getProjectSid, prjProject.getProjectSidList());
        updateWrapper.set(PrjProject::getDevelopPlanSid, prjProject.getDevelopPlanSid());
        updateWrapper.set(PrjProject::getDevelopPlanCode, prjProject.getDevelopPlanCode());
        updateWrapper.set(PrjProject::getCategoryPlanSid, prjProject.getCategoryPlanSid());
        updateWrapper.set(PrjProject::getCategoryPlanCode, prjProject.getCategoryPlanCode());
        row = prjProjectMapper.update(new PrjProject(), updateWrapper);
        // 操作日志
        for (int i = 0; i < projectList.size(); i++) {
            if (!prjProject.getDevelopPlanCode().equals(projectList.get(i).getDevelopPlanCode())) {
                PrjProject nowData = new PrjProject();
                BeanUtil.copyProperties(projectList.get(i), nowData);
                nowData.setDevelopPlanCode(prjProject.getDevelopPlanCode());
                nowData.setDevelopPlanSid(prjProject.getDevelopPlanSid())
                        .setCategoryPlanSid(prjProject.getCategoryPlanSid())
                        .setCategoryPlanCode(prjProject.getCategoryPlanCode());
                List<OperMsg> msgList;
                msgList = BeanUtils.eq(projectList.get(i), nowData);
                String oldCode = projectList.get(i).getDevelopPlanCode() == null ? "" : projectList.get(i).getDevelopPlanCode();
                String newCode = nowData.getDevelopPlanCode() == null ? "" : nowData.getDevelopPlanCode();
                String remark = "开发计划号变更，变更前：" + oldCode + "；变更后：" + newCode;
                MongodbUtil.insertUserLog(projectList.get(i).getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }

    /**
     * 设置商品款号/SPU号
     * @param prjProject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setProduct(PrjProject prjProject) {
        if (prjProject.getProjectSid() == null){
            throw new BaseException("请选择行！");
        }
        PrjProject request = prjProjectMapper.selectById(prjProject.getProjectSid());
        if (request == null) {
            throw new BaseException("项目档案不存在！");
        }
        if (!ConstantsPdm.PROJECT_TYPE_KAIF.equals(request.getProjectType())) {
            throw new BaseException("只允许设置项目类型为开发的项目档案！");
        }
        int row = 0;
        // 去掉首尾空格
        if (StrUtil.isNotBlank(prjProject.getProductCode())) {
            String code = prjProject.getProductCode();
            code = code.trim();
            prjProject.setProductCode(code);
        }
        // 开发项目
        LambdaUpdateWrapper<PrjProject> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PrjProject::getProjectSid, prjProject.getProjectSid());
        if (StrUtil.isNotBlank(prjProject.getProductCode())) {
            try {
                BasMaterial material = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                        .eq(BasMaterial::getMaterialCode, prjProject.getProductCode()));
                if (material != null) {
                    prjProject.setProductSid(material.getMaterialSid());
                    updateWrapper.set(PrjProject::getProductSid, material.getMaterialSid());
                }
            } catch (Exception e) {
                log.warn("系统中该SPU： " + prjProject.getProductCode() + "存在多笔");
            }
        }
        updateWrapper.set(PrjProject::getProductCode, prjProject.getProductCode());
        row = prjProjectMapper.update(new PrjProject(), updateWrapper);
        // 操作日志
        if (row > 0) {
            PrjProject nowData = new PrjProject();
            BeanUtil.copyProperties(request, nowData);
            nowData.setProductSid(prjProject.getProductSid())
                    .setProductCode(prjProject.getProductCode());
            List<OperMsg> msgList;
            msgList = BeanUtils.eq(request, nowData);
            String oldCode = request.getProductCode() == null ? "" : request.getProductCode();
            String newCode = nowData.getProductCode() == null ? "" : nowData.getProductCode();
            String remark = "商品款号/SPU号变更，变更前：" + oldCode + "；变更后：" + newCode;
            MongodbUtil.insertUserLog(prjProject.getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
            // 试销项目
            List<PrjProject> shixiaoList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda().eq(PrjProject::getPreProjectSid, prjProject.getProjectSid()));
            if (CollectionUtil.isNotEmpty(shixiaoList)) {
                List<Long> shixiaoSidList = shixiaoList.stream().map(PrjProject::getProjectSid).collect(toList());
                // 修改
                LambdaUpdateWrapper<PrjProject> updateWrapper2 = new LambdaUpdateWrapper<>();
                updateWrapper2.in(PrjProject::getProjectSid, shixiaoSidList);
                updateWrapper2.set(PrjProject::getProductSid, prjProject.getProductSid());
                updateWrapper2.set(PrjProject::getProductCode, prjProject.getProductCode());
                row = row + prjProjectMapper.update(new PrjProject(), updateWrapper2);
                // 记录
                for (PrjProject project : shixiaoList) {
                    PrjProject nowShixiao = new PrjProject();
                    BeanUtil.copyProperties(project, nowShixiao);
                    nowShixiao.setProductSid(prjProject.getProductSid())
                            .setProductCode(prjProject.getProductCode());
                    List<OperMsg> msgList2;
                    msgList2 = BeanUtils.eq(project, nowShixiao);
                    String oldCode2 = project.getProductCode() == null ? "" : project.getProductCode();
                    String newCode2 = nowShixiao.getProductCode() == null ? "" : nowShixiao.getProductCode();
                    String remark2 = "商品款号/SPU号变更，变更前：" + oldCode2 + "；变更后：" + newCode2;
                    MongodbUtil.insertUserLog(project.getProjectSid(), BusinessType.QITA.getValue(), msgList2, TITLE, remark2);
                }
            }
        }
        return row;
    }

    /**
     * 设置商品SKU号
     * @param prjProject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setMaterialBarcode(PrjProject prjProject) {
        if (prjProject.getProjectSid() == null){
            throw new BaseException("请选择行！");
        }
        PrjProject request = prjProjectMapper.selectById(prjProject.getProjectSid());
        if (request == null) {
            throw new BaseException("项目档案不存在！");
        }
        int row = 0;
        // 开发项目
        LambdaUpdateWrapper<PrjProject> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PrjProject::getProjectSid, prjProject.getProjectSid());
        // 根据当前登录账号的租户，从数据库表“系统默认设置(租户)”中获取数据：商品SKU编码(ERP)录入方式
        String erpMaterialSkuEnterModeProject = ApiThreadLocalUtil.get().getSysUser().getErpMaterialSkuEnterModeProject();
        // 若该字段为“选择”
        if ("XZ".equals(erpMaterialSkuEnterModeProject)) {
            // 开发项目
            updateWrapper.set(PrjProject::getMaterialBarcodeSid, prjProject.getMaterialBarcodeSid());
            updateWrapper.set(PrjProject::getMaterialBarcodeCode, prjProject.getMaterialBarcodeCode());
            // 获取条码中的ERP
            String erpMaterialSkuBarcode = null;
            if (prjProject.getMaterialBarcodeSid() != null) {
                BasMaterialBarcode materialBarcode = basMaterialBarcodeMapper.selectById(prjProject.getMaterialBarcodeSid());
                if (materialBarcode != null) {
                    erpMaterialSkuBarcode = materialBarcode.getErpMaterialSkuBarcode();
                    updateWrapper.set(PrjProject::getErpMaterialSkuBarcode, erpMaterialSkuBarcode);
                }
            }
            row = prjProjectMapper.update(new PrjProject(), updateWrapper);
            // 记录操作日志
            if (row > 0) {
                // 开发项目
                PrjProject nowData = new PrjProject();
                BeanUtil.copyProperties(request, nowData);
                nowData.setMaterialBarcodeSid(prjProject.getMaterialBarcodeSid())
                        .setMaterialBarcodeCode(prjProject.getMaterialBarcodeCode())
                        .setErpMaterialSkuBarcode(erpMaterialSkuBarcode);
                List<OperMsg> msgList;
                msgList = BeanUtils.eq(request, nowData);
                String oldCode = request.getErpMaterialSkuBarcode() == null ? "" : request.getErpMaterialSkuBarcode();
                String newCode = nowData.getErpMaterialSkuBarcode() == null ? "" : nowData.getErpMaterialSkuBarcode();
                String remark = "商品SKU编码(ERP)变更，变更前：" + oldCode + "；变更后：" + newCode;
                MongodbUtil.insertUserLog(prjProject.getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
            }
            // 试销项目
            List<PrjProject> shixiaoList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda().eq(PrjProject::getPreProjectSid, prjProject.getProjectSid()));
            if (CollectionUtil.isNotEmpty(shixiaoList)) {
                List<Long> shixiaoSidList = shixiaoList.stream().map(PrjProject::getProjectSid).collect(toList());
                // 修改
                LambdaUpdateWrapper<PrjProject> updateWrapper2 = new LambdaUpdateWrapper<>();
                updateWrapper2.in(PrjProject::getProjectSid, shixiaoSidList);
                updateWrapper2.set(PrjProject::getMaterialBarcodeSid, prjProject.getMaterialBarcodeSid());
                updateWrapper2.set(PrjProject::getMaterialBarcodeCode, prjProject.getMaterialBarcodeCode());
                updateWrapper2.set(PrjProject::getErpMaterialSkuBarcode, erpMaterialSkuBarcode);
                row = row + prjProjectMapper.update(new PrjProject(), updateWrapper2);
                // 记录
                for (PrjProject project : shixiaoList) {
                    PrjProject nowShixiao = new PrjProject();
                    BeanUtil.copyProperties(project, nowShixiao);
                    nowShixiao.setMaterialBarcodeSid(prjProject.getMaterialBarcodeSid())
                            .setMaterialBarcodeCode(prjProject.getMaterialBarcodeCode())
                            .setErpMaterialSkuBarcode(erpMaterialSkuBarcode);
                    List<OperMsg> msgList2;
                    msgList2 = BeanUtils.eq(project, nowShixiao);
                    String oldCode2 = project.getErpMaterialSkuBarcode() == null ? "" : project.getErpMaterialSkuBarcode();
                    String newCode2 = nowShixiao.getErpMaterialSkuBarcode() == null ? "" : nowShixiao.getErpMaterialSkuBarcode();
                    String remark2 = "商品SKU编码(ERP)变更，变更前：" + oldCode2 + "；变更后：" + newCode2;
                    MongodbUtil.insertUserLog(project.getProjectSid(), BusinessType.QITA.getValue(), msgList2, TITLE, remark2);
                }
            }
        }
        // 若该字段为“手工录入”
        else if ("SG".equals(erpMaterialSkuEnterModeProject)) {
            if (StrUtil.isBlank(prjProject.getErpMaterialSkuBarcode())) {
                throw new BaseException("商品SKU编码(ERP)不能为空！");
            }
            // 去掉首尾空格
            String code = prjProject.getErpMaterialSkuBarcode();
            code = code.trim();
            prjProject.setErpMaterialSkuBarcode(code);
            // 开发项目
            updateWrapper.set(PrjProject::getErpMaterialSkuBarcode, prjProject.getErpMaterialSkuBarcode());
            row = prjProjectMapper.update(new PrjProject(), updateWrapper);
            // 记录操作日志
            if (row > 0) {
                // 开发项目
                PrjProject nowData = new PrjProject();
                BeanUtil.copyProperties(request, nowData);
                nowData.setErpMaterialSkuBarcode(prjProject.getErpMaterialSkuBarcode());
                List<OperMsg> msgList;
                msgList = BeanUtils.eq(request, nowData);
                String oldCode = request.getErpMaterialSkuBarcode() == null ? "" : request.getErpMaterialSkuBarcode();
                String newCode = nowData.getErpMaterialSkuBarcode() == null ? "" : nowData.getErpMaterialSkuBarcode();
                String remark = "商品SKU编码(ERP)变更，变更前：" + oldCode + "；变更后：" + newCode;
                MongodbUtil.insertUserLog(prjProject.getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
            }
            // 试销项目
            List<PrjProject> shixiaoList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda().eq(PrjProject::getPreProjectSid, prjProject.getProjectSid()));
            if (CollectionUtil.isNotEmpty(shixiaoList)) {
                List<Long> shixiaoSidList = shixiaoList.stream().map(PrjProject::getProjectSid).collect(toList());
                // 修改
                LambdaUpdateWrapper<PrjProject> updateWrapper2 = new LambdaUpdateWrapper<>();
                updateWrapper2.in(PrjProject::getProjectSid, shixiaoSidList);
                updateWrapper2.set(PrjProject::getErpMaterialSkuBarcode, prjProject.getErpMaterialSkuBarcode());
                row = row + prjProjectMapper.update(new PrjProject(), updateWrapper2);
                // 记录
                for (PrjProject project : shixiaoList) {
                    PrjProject nowShixiao = new PrjProject();
                    BeanUtil.copyProperties(project, nowShixiao);
                    nowShixiao.setErpMaterialSkuBarcode(prjProject.getErpMaterialSkuBarcode());
                    List<OperMsg> msgList2;
                    msgList2 = BeanUtils.eq(project, nowShixiao);
                    String oldCode2 = project.getErpMaterialSkuBarcode() == null ? "" : project.getErpMaterialSkuBarcode();
                    String newCode2 = nowShixiao.getErpMaterialSkuBarcode() == null ? "" : nowShixiao.getErpMaterialSkuBarcode();
                    String remark2 = "商品SKU编码(ERP)变更，变更前：" + oldCode2 + "；变更后：" + newCode2;
                    MongodbUtil.insertUserLog(project.getProjectSid(), BusinessType.QITA.getValue(), msgList2, TITLE, remark2);
                }
            }
        }
        return row;
    }

    /**
     * 查询页面开始执行的按钮
     * @param prjProject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int startTask(PrjProject prjProject) {
        if (prjProject.getProjectSid() == null) {
            throw new BaseException("请选择行！");
        }
        if (prjProject.getTaskTemplateSid() == null) {
            throw new BaseException("请选择任务模板！");
        }
        PrjProject project = prjProjectMapper.selectById(prjProject.getProjectSid());
        // 如果项目有明细了，则就是已启动，则直接修改 项目档案为进行中，然后返回
        List<PrjProjectTask> taskList = prjProjectTaskMapper.selectList(new QueryWrapper<PrjProjectTask>().lambda()
                .eq(PrjProjectTask::getProjectSid, prjProject.getProjectSid()));
        if (CollectionUtil.isNotEmpty(taskList)) {
            PrjProject ject = new PrjProject();
            ject.setProjectSidList(new Long[]{prjProject.getProjectSid()}).setProjectStatus(ConstantsPdm.PROJECT_STATUS_JXZ);
            return this.setProjectStatus(ject);
        }
        List<PrjTaskTemplateItem> taskTemplateItemList = prjTaskTemplateItemMapper.selectList(new QueryWrapper<PrjTaskTemplateItem>()
                .lambda().eq(PrjTaskTemplateItem::getTaskTemplateSid, prjProject.getTaskTemplateSid()));
        if (CollectionUtil.isEmpty(taskTemplateItemList)) {
            throw new BaseException("请选择带有任务明细的任务模板！");
        }
        List<PrjProjectTask> itemList = BeanCopyUtils.copyListProperties(taskTemplateItemList, PrjProjectTask::new);
        project.setTaskList(itemList);
        int row = 0;
        // 设置项目任务执行提醒天数
        int tian = 7;
        try {
            SysDefaultSettingClient client = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            if (client != null && client.getToexecuteNoticeDaysPrjTask() != null) {
                tian = client.getToexecuteNoticeDaysPrjTask().intValue();
            }
            else {
                SysDefaultSettingSystem system = settingClientMapper.selectSysDefaultSettingSystem();
                if (system != null && system.getToexecuteNoticeDaysPrjTask() != null) {
                    tian = system.getToexecuteNoticeDaysPrjTask().intValue();
                }
            }
        } catch (Exception e) {
            log.error("获取项目任务执行提醒天数报错");
        }
        // 明细字段初始化
        for (PrjProjectTask item : project.getTaskList()) {
            // 岗位字段处理成数组
            prjProjectTaskService.getPosition(item);
            // 设置项目任务执行提醒天数
            item.setToexecuteNoticeDaysPrjTask(tian);
            // 任务状态
            item.setTaskStatus(ConstantsPdm.PROJECT_TASK_WKS);
            // 2、根据项目的“计划完成日期”和任务模板明细的“计划完成日期设置T-”，
            // 自动计算出项目任务明细的计划完成日期（公式：项目任务明细的计划完成日期=项目的计划完成日期-模板明细的计划完成日期设置T-）；
            item.setPlanEndDate(project.getPlanEndDate());
            if (project.getPlanEndDate() != null && item.getPlanEndDateConfig() != null) {
                LocalDate date = project.getPlanEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                item.setPlanEndDate(Date.from(date.minusDays(item.getPlanEndDateConfig())
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            // 3、根据项目任务明细的“计划完成日期”和“任务用时(天)”，自动计算出项目任务明细的计划开始日期
            // （公式：项目任务明细的计划开始日期=项目任务明细的计划完成日期-任务用时(天)）
            item.setPlanStartDate(item.getPlanEndDate());
            if (item.getPlanEndDate() != null && item.getTemplateTime() != null) {
                LocalDate date = item.getPlanEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                item.setPlanStartDate(Date.from(date.minusDays(item.getTemplateTime())
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            // 4、根据项目任务明细的“任务用时(天)”和“即将到期预警天数比例”，自动计算出项目任务明细的即将到期提醒天数
            // （公式：项目任务明细的即将到期提醒天数（计算结果向上取整）=任务用时(天) X即将到期预警天数比例）
            if (item.getTemplateTime() != null && item.getOverdueWarnRate() != null) {
                item.setToexpireDaysTask(BigDecimal.valueOf(item.getTemplateTime()).multiply(item.getOverdueWarnRate())
                        .setScale(0, BigDecimal.ROUND_UP ).longValue());
            }
            item.setCreateDate(null).setCreatorAccount(null).setUpdateDate(null).setUpdaterAccount(null);
        }
        // 主表修改状态
        row = prjProjectMapper.update(null, new UpdateWrapper<PrjProject>().lambda()
                .set(PrjProject::getProjectStatus, ConstantsPdm.PROJECT_STATUS_JXZ)
                .eq(PrjProject::getProjectSid, prjProject.getProjectSid()));
        if (row > 0) {
            prjProjectTaskService.insertPrjProjectTaskList(project);
        }
        // 发送待办
        projectWarningTask.startProjectExecute(new PrjProjectTask().setProjectSid(prjProject.getProjectSid()));
        MongodbUtil.insertUserLog(prjProject.getProjectSid(), BusinessType.QITA.getValue(), null, TITLE, "开始执行");
        return row;
    }

    /**
     * 跳转其它单据
     * @param prjProject
     * @return
     */
    @Override
    public PrjProject jumpTo(PrjProject prjProject) {
        if (prjProject.getProjectSid() == null) {
            throw new BaseException("请选择行！");
        }
        if (StrUtil.isBlank(prjProject.getRelateBusinessFormCode())) {
            throw new BaseException("请选择目标单据类型！");
        }
        List<PrjProjectTask> taskList = prjProjectTaskMapper.selectList(new QueryWrapper<PrjProjectTask>().lambda()
                .eq(PrjProjectTask::getProjectSid, prjProject.getProjectSid()));
        PrjProject project = prjProjectMapper.selectPrjProjectById(prjProject.getProjectSid());
        if (project == null) {
            throw new BaseException("所选项目档案不存在！");
        }
        if (!ConstantsEms.CHECK_STATUS.equals(project.getHandleStatus())
                || !ConstantsPdm.PROJECT_STATUS_JXZ.equals(project.getProjectStatus())) {
            throw new BaseException("仅处理状态为“已确认“且项目状态为“进行中”才可进行此操作！");
        }
        if (ConstantsPdm.RELATE_BUSINESS_FORM_TGHZ.equals(prjProject.getRelateBusinessFormCode())) {
            // 图稿批复
            // 校验是否已存在
            List<FrmDraftDesign> designList = draftDesignMapper.selectList(new QueryWrapper<FrmDraftDesign>().lambda()
                    .eq(FrmDraftDesign::getProjectSid, prjProject.getProjectSid()));
            if (CollectionUtil.isNotEmpty(designList)) {
                prjProject.setReferMsg("该项目的图稿绘制单已创建！")
                        .setReferSid(designList.get(designList.size()-1).getDraftDesignSid());
                return prjProject;
            }
            // 校验任务明细是否需要创建
            if (CollectionUtil.isEmpty(taskList)) {
                throw new BaseException("该项目无需创建图稿绘制单！");
            }
            taskList = taskList.stream().filter(o->ConstantsPdm.RELATE_BUSINESS_FORM_TGHZ.equals(o.getRelateBusinessFormCode())).collect(toList());
            if (CollectionUtil.isEmpty(taskList)) {
                throw new BaseException("该项目无需创建图稿绘制单！");
            }
        }
        else if (ConstantsPdm.RELATE_BUSINESS_FORM_YPCS.equals(prjProject.getRelateBusinessFormCode())) {
            // 样品初审单
            // 校验是否已存在
            List<FrmSampleReview> reviewList = sampleReviewMapper.selectList(new QueryWrapper<FrmSampleReview>().lambda()
                    .eq(FrmSampleReview::getProjectSid, prjProject.getProjectSid())
                    .eq(FrmSampleReview::getReviewPhase, ConstantsPdm.REVIEW_STAGE_YPCS));
            if (CollectionUtil.isNotEmpty(reviewList)) {
                prjProject.setReferMsg("该项目的样品初审单已创建！")
                        .setReferSid(reviewList.get(reviewList.size()-1).getSampleReviewSid());
                return prjProject;
            }
            // 校验任务明细是否需要创建
            if (CollectionUtil.isEmpty(taskList)) {
                throw new BaseException("该项目无需创建样品初审单！");
            }
            taskList = taskList.stream().filter(o->ConstantsPdm.RELATE_BUSINESS_FORM_YPCS.equals(o.getRelateBusinessFormCode())).collect(toList());
            if (CollectionUtil.isEmpty(taskList)) {
                throw new BaseException("该项目无需创建样品初审单！");
            }
        }
        else if (ConstantsPdm.RELATE_BUSINESS_FORM_YPZS.equals(prjProject.getRelateBusinessFormCode())) {
            // 样品终审单
            // 校验是否已存在
            List<FrmSampleReview> reviewList = sampleReviewMapper.selectList(new QueryWrapper<FrmSampleReview>().lambda()
                    .eq(FrmSampleReview::getProjectSid, prjProject.getProjectSid())
                    .eq(FrmSampleReview::getReviewPhase, ConstantsPdm.REVIEW_STAGE_YPZS));
            if (CollectionUtil.isNotEmpty(reviewList)) {
                prjProject.setReferMsg("该项目的样品终审单已存在！")
                        .setReferSid(reviewList.get(reviewList.size()-1).getSampleReviewSid());
                return prjProject;
            }
            // 校验任务明细是否需要创建
            if (CollectionUtil.isEmpty(taskList)) {
                throw new BaseException("该项目无需创建样品终审单！");
            }
            taskList = taskList.stream().filter(o->ConstantsPdm.RELATE_BUSINESS_FORM_YPZS.equals(o.getRelateBusinessFormCode())).collect(toList());
            if (CollectionUtil.isEmpty(taskList)) {
                throw new BaseException("该项目无需创建样品终审单！");
            }
        }
        else if (ConstantsPdm.RELATE_BUSINESS_FORM_PZYHQ.equals(prjProject.getRelateBusinessFormCode())) {
            // 视觉设计单
            // 校验是否已存在
            List<FrmPhotoSampleGain> gainList = photoSampleGainMapper.selectList(new QueryWrapper<FrmPhotoSampleGain>().lambda()
                    .eq(FrmPhotoSampleGain::getProjectSid, prjProject.getProjectSid()));
            if (CollectionUtil.isNotEmpty(gainList)) {
                prjProject.setReferMsg("该项目的视觉设计单已存在！")
                        .setReferSid(gainList.get(gainList.size()-1).getPhotoSampleGainSid());
                return prjProject;
            }
            // 校验任务明细是否需要创建
            if (CollectionUtil.isEmpty(taskList)) {
                throw new BaseException("该项目无需创建视觉设计单！");
            }
            taskList = taskList.stream().filter(o->ConstantsPdm.RELATE_BUSINESS_FORM_PZYHQ.equals(o.getRelateBusinessFormCode())).collect(toList());
            if (CollectionUtil.isEmpty(taskList)) {
                throw new BaseException("该项目无需创建视觉设计单！");
            }
        }
        else if (ConstantsPdm.RELATE_BUSINESS_FORM_WASJ.equals(prjProject.getRelateBusinessFormCode())) {
            // 文案脚本单
            // 校验是否已存在
            List<FrmDocumentVision> visionList = documentVisionMapper.selectList(new QueryWrapper<FrmDocumentVision>().lambda()
                    .eq(FrmDocumentVision::getProjectSid, prjProject.getProjectSid()));
            if (CollectionUtil.isNotEmpty(visionList)) {
                prjProject.setReferMsg("该项目的文案脚本单已存在！")
                        .setReferSid(visionList.get(visionList.size()-1).getDocumentVisionSid());
                return prjProject;
            }
            // 校验任务明细是否需要创建
            if (CollectionUtil.isEmpty(taskList)) {
                throw new BaseException("该项目无需创建文案脚本单！");
            }
            taskList = taskList.stream().filter(o->ConstantsPdm.RELATE_BUSINESS_FORM_WASJ.equals(o.getRelateBusinessFormCode())).collect(toList());
            if (CollectionUtil.isEmpty(taskList)) {
                throw new BaseException("该项目无需创建文案脚本单！");
            }
        }
        else if (ConstantsPdm.RELATE_BUSINESS_FORM_XPSXJH.equals(prjProject.getRelateBusinessFormCode())) {
            // 新品试销计划单
            // 校验是否已存在
            List<FrmNewproductTrialsalePlan> trialsalePlanList = newproductTrialsalePlanMapper
                    .selectList(new QueryWrapper<FrmNewproductTrialsalePlan>().lambda()
                    .eq(FrmNewproductTrialsalePlan::getProjectSid, prjProject.getProjectSid()));
            if (CollectionUtil.isNotEmpty(trialsalePlanList)) {
                prjProject.setReferMsg("该项目的新品试销单已存在！")
                        .setReferSid(trialsalePlanList.get(trialsalePlanList.size()-1).getNewproductTrialsalePlanSid());
                return prjProject;
            }
            // 校验任务明细是否需要创建
            if (CollectionUtil.isEmpty(taskList)) {
                throw new BaseException("该项目无需创建新品试销计划单！");
            }
            taskList = taskList.stream().filter(o->ConstantsPdm.RELATE_BUSINESS_FORM_XPSXJH.equals(o.getRelateBusinessFormCode())).collect(toList());
            if (CollectionUtil.isEmpty(taskList)) {
                throw new BaseException("该项目无需创建新品试销计划单！");
            }
        }
        else if (ConstantsPdm.RELATE_BUSINESS_FORM_DHTZ.equals(prjProject.getRelateBusinessFormCode())) {
            // 到货通知单
            // 校验是否已存在
            List<FrmArrivalNotice> arrivalNoticeList = arrivalNoticeMapper.selectList(new QueryWrapper<FrmArrivalNotice>().lambda()
                    .eq(FrmArrivalNotice::getProjectSid, prjProject.getProjectSid()));
            if (CollectionUtil.isNotEmpty(arrivalNoticeList)) {
                prjProject.setReferMsg("该项目的到货通知单已存在！")
                        .setReferSid(arrivalNoticeList.get(arrivalNoticeList.size()-1).getArrivalNoticeSid());
                return prjProject;
            }
            // 校验任务明细是否需要创建
            if (CollectionUtil.isEmpty(taskList)) {
                throw new BaseException("该项目无需创建到货通知单！");
            }
            taskList = taskList.stream().filter(o->ConstantsPdm.RELATE_BUSINESS_FORM_DHTZ.equals(o.getRelateBusinessFormCode())).collect(toList());
            if (CollectionUtil.isEmpty(taskList)) {
                throw new BaseException("该项目无需创建到货通知单！");
            }
        }
        else if (ConstantsPdm.RELATE_BUSINESS_FORM_SXJG.equals(prjProject.getRelateBusinessFormCode())) {
            // 试销结果单
            // 校验是否已存在
            List<FrmTrialsaleResult> resultList = trialsaleResultMapper
                    .selectList(new QueryWrapper<FrmTrialsaleResult>().lambda()
                    .eq(FrmTrialsaleResult::getProjectSid, prjProject.getProjectSid()));
            if (CollectionUtil.isNotEmpty(resultList)) {
                prjProject.setReferMsg("该项目的试销结果单已存在！")
                        .setReferSid(resultList.get(resultList.size()-1).getTrialsaleResultSid());
                return prjProject;
            }
            // 校验任务明细是否需要创建
            if (CollectionUtil.isEmpty(taskList)) {
                throw new BaseException("该项目无需创建试销结果单！");
            }
            taskList = taskList.stream().filter(o->ConstantsPdm.RELATE_BUSINESS_FORM_SXJG.equals(o.getRelateBusinessFormCode())).collect(toList());
            if (CollectionUtil.isEmpty(taskList)) {
                throw new BaseException("该项目无需创建试销结果单！");
            }
        }
        project.setTaskList(taskList);
        getDateList(project);
        return project;
    }

    /**
     * 查询项目进度列表
     *
     * @param projectSid 项目档案ID
     * @return 项目档案
     */
    @Override
    public PrjProject getPrjProjectProcessById(Long projectSid) {
        PrjProject prjProject = this.selectPrjProjectById(projectSid);
        // 总任务数
        List<PrjProjectTask> taskList = prjProject.getTaskList();
        if (CollectionUtil.isNotEmpty(taskList)) {
            prjProject.setItemCount(taskList.size());
            // 状态灯
            taskList.forEach(item->{
                // 岗位
                prjProjectTaskService.getPosition(item);
                // 灯
                Long toexpireDays = item.getToexpireDaysTask();
                if (toexpireDays == null) {
                    toexpireDays = new Long(0);
                }
                if (ConstantsPdm.PROJECT_TASK_WKS.equals(item.getTaskStatus())
                        || ConstantsPdm.PROJECT_STATUS_ZT.equals(item.getTaskStatus())
                        || ConstantsPdm.PROJECT_STATUS_QX.equals(item.getTaskStatus())) {
                    item.setLight("4");
                } else if (ConstantsPdm.PROJECT_TASK_YWC.equals(item.getTaskStatus())
                        || ConstantsPdm.PROJECT_STATUS_ZZ.equals(item.getTaskStatus())) {
                    item.setLight("3");
                } else {
                    if (item.getPlanEndDate() != null) {
                        LocalDate ldt1 = item.getPlanEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate localDate = LocalDate.now();
                        if (ldt1.isBefore(localDate)) {
                            item.setLight("0");
                        } else{
                            localDate = localDate.plusDays(toexpireDays);
                            if (localDate.isBefore(ldt1)) {
                                item.setLight("1");
                            } else {
                                item.setLight("2");
                            }
                        }
                    }
                }
            });
            // 已完成任务明细数量
            List<PrjProjectTask> ywc = taskList.stream()
                    .filter(o-> ConstantsPdm.PROJECT_TASK_YWC.equals(o.getTaskStatus())).collect(toList());
            prjProject.setItemCountYwc(ywc.size());
            // 进行中 且当前日期+到期提醒天数<计划完成日期的项目明细数量和
            List<PrjProjectTask> j = taskList.stream().filter(
                    o->"1".equals(o.getLight())
            ).collect(toList());
            if (CollectionUtil.isNotEmpty(j)) {
                prjProject.setItemCountJxz(j.size());
            }
            // 预警 且当前日期<计划完成日期，当前日期+到期提醒天数>=计划完成日期的项目明细数量和
            List<PrjProjectTask> yj = taskList.stream().filter(
                    o->"2".equals(o.getLight())
            ).collect(toList());
            if (CollectionUtil.isNotEmpty(yj)) {
                prjProject.setItemCountYj(yj.size());
            }
            // 逾期 且当前日期>计划完成日期的项目明细数量和
            List<PrjProjectTask> yq = taskList.stream().filter(
                    o->"0".equals(o.getLight())
            ).collect(toList());
            if (CollectionUtil.isNotEmpty(yq)) {
                prjProject.setItemCountYq(yq.size());
            }
        }
        // 项目进度 灯
        this.setLight(prjProject);
        return prjProject;
    }

    /**
     * 设置状态灯
     *
     * @param prjProject 项目档案
     * @return 项目档案
     */
    @Override
    public void setLight(PrjProject prjProject) {
        if (ConstantsPdm.PROJECT_STATUS_WKS.equals(prjProject.getProjectStatus())
            || ConstantsPdm.PROJECT_STATUS_ZT.equals(prjProject.getProjectStatus())
            || ConstantsPdm.PROJECT_STATUS_QX.equals(prjProject.getProjectStatus())) {
            prjProject.setLight("4");
        } else if (ConstantsPdm.PROJECT_STATUS_YWC.equals(prjProject.getProjectStatus())
                || ConstantsPdm.PROJECT_STATUS_ZZ.equals(prjProject.getProjectStatus())) {
            prjProject.setLight("3");
        } else {
            if (prjProject.getPlanEndDate() != null) {
                LocalDate ldt1 = prjProject.getPlanEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate localDate = LocalDate.now();
                if (ldt1.isBefore(localDate)) {
                    prjProject.setLight("0");
                } else{
                    localDate = localDate.plusDays(prjProject.getToexpireDaysProj());
                    if (localDate.isBefore(ldt1)) {
                        prjProject.setLight("1");
                    } else {
                        prjProject.setLight("2");
                    }
                }
            }
        }
    }

    /**
     * 试销站点执行状况报表
     */
    @Override
    public List<PrjProjectExecuteCondition> selectPrjProjectExecuteCondition(PrjProjectExecuteCondition prjProject) {
        prjProject.setClientId(ApiThreadLocalUtil.get().getClientId());
        return prjProjectMapper.selectPrjProjectExecuteCondition(prjProject);
    }

    /**
     * 按钮设置采购状态
     *
     * @param PrjProject request
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurchaseFlag(PrjProject request) {
        int row = 0;
        if (request.getProjectSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<PrjProject> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(PrjProject::getProjectSid, request.getProjectSidList());
        // 判读是否修改
        boolean flag = false;
        // 一次采购
        if (ConstantsEms.YES.equals(request.getFirstPurchaseFlagIsUpd())) {
            updateWrapper.set(PrjProject::getFirstPurchaseFlag, request.getFirstPurchaseFlag());
            flag = true;
        }
        // 二次采购
        if (ConstantsEms.YES.equals(request.getSecondPurchaseFlagIsUpd())) {
            updateWrapper.set(PrjProject::getSecondPurchaseFlag, request.getSecondPurchaseFlag());
            flag = true;
        }
        // 一次到货通知
        if (ConstantsEms.YES.equals(request.getArrivalNoticeFlagFirstPurchaseIsUpd())) {
            updateWrapper.set(PrjProject::getArrivalNoticeFlagFirstPurchase, request.getArrivalNoticeFlagFirstPurchase());
            flag = true;
        }
        // 修改
        if (flag) {
            row = prjProjectMapper.update(null, updateWrapper);
        }
        return row;
    }

    /**
     * 设置计划日期
     * @param project
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setPlanDate(PrjProject project) {
        if (project.getProjectSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        // 原数据
        List<PrjProject> projectList = prjProjectMapper.selectPrjProjectList(new PrjProject().setProjectSidList(project.getProjectSidList()));
        // 修改
        LambdaUpdateWrapper<PrjProject> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        // 计划开始日期
        updateWrapper.in(PrjProject::getProjectSid, project.getProjectSidList());
        if (ConstantsEms.YES.equals(project.getPlanStartDateIsUpdate())) {
            updateWrapper.set(PrjProject::getPlanStartDate, project.getPlanStartDate());
        }
        // 计划完成日期
        if (ConstantsEms.YES.equals(project.getPlanEndDateIsUpdate())) {
            updateWrapper.set(PrjProject::getPlanEndDate, project.getPlanEndDate());
        }
        if (ConstantsEms.YES.equals(project.getPlanStartDateIsUpdate())
                || ConstantsEms.YES.equals(project.getPlanEndDateIsUpdate())) {
            row = prjProjectMapper.update(new PrjProject(), updateWrapper);
            // 操作日志记录
            for (int i = 0; i < projectList.size(); i++) {
                PrjProject nowData = new PrjProject();
                BeanUtil.copyProperties(projectList.get(i), nowData);
                // 计划开始日期
                if (ConstantsEms.YES.equals(project.getPlanStartDateIsUpdate())) {
                    if (projectList.get(i).getPlanStartDate() == null || !projectList.get(i).getPlanStartDate().equals(project.getPlanStartDate())) {
                        nowData.setPlanStartDate(project.getPlanStartDate());
                        List<OperMsg> msgList;
                        msgList = BeanUtils.eq(projectList.get(i), nowData);
                        if (CollectionUtil.isNotEmpty(msgList)) {
                            String oldCode = projectList.get(i).getPlanStartDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(projectList.get(i).getPlanStartDate());
                            String newCode = nowData.getPlanStartDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(nowData.getPlanStartDate());
                            String remark = "项目的计划开始日期变更，变更前：" + oldCode + "；变更后：" + newCode;
                            MongodbUtil.insertUserLog(projectList.get(i).getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                            MongodbUtil.insertUserLog(projectList.get(i).getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                        }
                    }
                }
                // 计划完成日期
                if (ConstantsEms.YES.equals(project.getPlanEndDateIsUpdate())) {
                    if (projectList.get(i).getPlanEndDate() == null || !projectList.get(i).getPlanEndDate().equals(project.getPlanEndDate())) {
                        // 上面计划开始日期有改到，这里改回旧的，值判断计划完成日期
                        nowData.setPlanStartDate(projectList.get(i).getPlanStartDate());
                        nowData.setPlanEndDate(project.getPlanEndDate());
                        List<OperMsg> msgList;
                        msgList = BeanUtils.eq(projectList.get(i), nowData);
                        if (CollectionUtil.isNotEmpty(msgList)) {
                            String oldCode = projectList.get(i).getPlanEndDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(projectList.get(i).getPlanEndDate());
                            String newCode = nowData.getPlanEndDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(nowData.getPlanEndDate());
                            String remark = "项目的计划完成日期变更，变更前：" + oldCode + "；变更后：" + newCode;
                            MongodbUtil.insertUserLog(projectList.get(i).getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                            MongodbUtil.insertUserLog(projectList.get(i).getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                        }
                    }
                }
            }
        }
        return project.getProjectSidList().length;
    }

    /**
     * 设置实际完成日期
     * @param project
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setActualEndDate(PrjProject project){
        if (project.getProjectSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        // 原数据
        List<PrjProject> projectList = prjProjectMapper.selectPrjProjectList(new PrjProject().setProjectSidList(project.getProjectSidList()));
        // 修改
        LambdaUpdateWrapper<PrjProject> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        // 实际完成日期
        updateWrapper.in(PrjProject::getProjectSid, project.getProjectSidList());
        updateWrapper.set(PrjProject::getActualEndDate, project.getActualEndDate());
        row = prjProjectMapper.update(new PrjProject(), updateWrapper);
        // 操作日志记录
        for (int i = 0; i < projectList.size(); i++) {
            // 实际完成日期
            if (projectList.get(i).getActualEndDate() == null || !projectList.get(i).getActualEndDate().equals(project.getActualEndDate())) {
                PrjProject nowData = new PrjProject();
                BeanUtil.copyProperties(projectList.get(i), nowData);
                nowData.setActualEndDate(project.getActualEndDate());
                List<OperMsg> msgList;
                msgList = BeanUtils.eq(projectList.get(i), nowData);
                if (CollectionUtil.isNotEmpty(msgList)) {
                    String oldCode = projectList.get(i).getActualEndDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(projectList.get(i).getActualEndDate());
                    String newCode = nowData.getActualEndDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(nowData.getActualEndDate());
                    String remark = "项目的实际完成日期变更，变更前：" + oldCode + "；变更后：" + newCode;
                    MongodbUtil.insertUserLog(projectList.get(i).getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                    MongodbUtil.insertUserLog(projectList.get(i).getProjectSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                }
            }
        }
        return project.getProjectSidList().length;
    }

    /**
     * 任务单据操作日志
     * @param projectTaskSidList
     * @return
     */
    @Override
    public List<UserOperLog> getProjectTaskDocumentOperLogList(List<Long> projectTaskSidList) {
        List<UserOperLog> operLogList = new ArrayList<>();
        // 图稿绘制
        List<FrmDraftDesign> designList = draftDesignMapper.selectList(new QueryWrapper<FrmDraftDesign>().lambda()
                .in(FrmDraftDesign::getProjectTaskSid, projectTaskSidList));
        if (CollectionUtil.isNotEmpty(designList)) {
            for (FrmDraftDesign item : designList) {
                operLogList.addAll(MongodbUtil.find(item.getDraftDesignSid()));
            }
        }
        // 样品初审单
        List<FrmSampleReview> reviewCsList = sampleReviewMapper.selectList(new QueryWrapper<FrmSampleReview>().lambda()
                .in(FrmSampleReview::getProjectTaskSid, projectTaskSidList));
        if (CollectionUtil.isNotEmpty(reviewCsList)) {
            for (FrmSampleReview item : reviewCsList) {
                List<UserOperLog> operList = MongodbUtil.find(item.getSampleReviewSid());
                if (CollectionUtil.isNotEmpty(operList)) {
                    for (UserOperLog oper : operList) {
                        oper.setTitle("样品初审单");
                    }
                    operLogList.addAll(operList);
                }
            }
        }
        // 样品终审单
        List<FrmSampleReview> reviewZsList = sampleReviewMapper.selectList(new QueryWrapper<FrmSampleReview>().lambda()
                .in(FrmSampleReview::getProjectTaskSid, projectTaskSidList));
        if (CollectionUtil.isNotEmpty(reviewZsList)) {
            for (FrmSampleReview item : reviewZsList) {
                List<UserOperLog> operList = MongodbUtil.find(item.getSampleReviewSid());
                if (CollectionUtil.isNotEmpty(operList)) {
                    for (UserOperLog oper : operList) {
                        oper.setTitle("样品终审单");
                    }
                    operLogList.addAll(operList);
                }
            }
        }
        // 视觉设计单
        List<FrmPhotoSampleGain> photoList = photoSampleGainMapper.selectList(new QueryWrapper<FrmPhotoSampleGain>().lambda()
                .in(FrmPhotoSampleGain::getProjectTaskSid, projectTaskSidList));
        if (CollectionUtil.isNotEmpty(photoList)) {
            for (FrmPhotoSampleGain item : photoList) {
                operLogList.addAll(MongodbUtil.find(item.getPhotoSampleGainSid()));
            }
        }
        // 文案脚本单
        List<FrmDocumentVision> visionList = documentVisionMapper.selectList(new QueryWrapper<FrmDocumentVision>().lambda()
                .in(FrmDocumentVision::getProjectTaskSid, projectTaskSidList));
        if (CollectionUtil.isNotEmpty(visionList)) {
            for (FrmDocumentVision item : visionList) {
                operLogList.addAll(MongodbUtil.find(item.getDocumentVisionSid()));
            }
        }
        // 新品试销计划单
        List<FrmNewproductTrialsalePlan> newPlanList = newproductTrialsalePlanMapper.selectList(new QueryWrapper<FrmNewproductTrialsalePlan>().lambda()
                .in(FrmNewproductTrialsalePlan::getProjectTaskSid, projectTaskSidList));
        if (CollectionUtil.isNotEmpty(newPlanList)) {
            for (FrmNewproductTrialsalePlan item : newPlanList) {
                operLogList.addAll(MongodbUtil.find(item.getNewproductTrialsalePlanSid()));
            }
        }
        // 到货通知单
        List<FrmArrivalNotice> arrivalList = arrivalNoticeMapper.selectList(new QueryWrapper<FrmArrivalNotice>().lambda()
                .in(FrmArrivalNotice::getProjectTaskSid, projectTaskSidList));
        if (CollectionUtil.isNotEmpty(arrivalList)) {
            for (FrmArrivalNotice item : arrivalList) {
                operLogList.addAll(MongodbUtil.find(item.getArrivalNoticeSid()));
            }
        }
        // 试销结果单
        List<FrmTrialsaleResult> resultList = trialsaleResultMapper.selectList(new QueryWrapper<FrmTrialsaleResult>().lambda()
                .in(FrmTrialsaleResult::getProjectTaskSid, projectTaskSidList));
        if (CollectionUtil.isNotEmpty(resultList)) {
            for (FrmTrialsaleResult item : resultList) {
                operLogList.addAll(MongodbUtil.find(item.getTrialsaleResultSid()));
            }
        }
        // 排序
        if (CollectionUtil.isNotEmpty(operLogList)) {
            operLogList = operLogList.stream().sorted(Comparator.comparing(UserOperLog::getOperTime).reversed()).collect(toList());
        }
        return operLogList;
    }


    @Autowired
    private IPrjMatterListService prjMatterListService;
    @Autowired
    private IPrjTaskTemplateItemService prjTaskTemplateItemService;
    @Autowired
    private ConTaskTemplateCompareMapper conTaskTemplateCompareMapper;

    /**
     * 将项目设置为已完成新建事项清单的逻辑byid
     *
     * @param project
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setProjectYwcInsertMatterById(Long projectSid) {
        int row = 0;
        // 如果项目有事项清单了
        List<PrjMatterList> matterListList = prjMatterListService.selectPrjMatterListList(new PrjMatterList().setProjectSid(projectSid));
        if (CollectionUtil.isNotEmpty(matterListList)) {
            throw new BaseException("所选项目已创建事项清单");
        }
        PrjProject project = prjProjectMapper.selectById(projectSid);
        if (ConstantsPdm.PROJECT_STATUS_YWC.equals(project.getProjectStatus())
                && ConstantsEms.CHECK_STATUS.equals(project.getHandleStatus())) {
            // 获取模板
            List<ConTaskTemplateCompare> compares = setProjectYwcInsertMatterCompare(project);
            if (CollectionUtil.isEmpty(compares)) {
                throw new BaseException("所选项目不存在后续事项模板");
            } else {
                // 找模板明细 带有 List 值的方法
                List<PrjTaskTemplateItem> templateItemList = prjTaskTemplateItemService
                        .selectPrjTaskTemplateItemListById(compares.get(0).getAfterTaskTemplateSid());
                if (CollectionUtil.isEmpty(templateItemList)) {
                    throw new BaseException("所选项目不存在后续事项模板");
                }
            }
            row = setProjectYwcInsertMatter(project);
        } else {
            throw new BaseException("所选项目必须是已确认且已完成");
        }
        return row;
    }

    /**
     * 将项目设置为已完成新建事项清单的逻辑
     *
     * @param project
     */
    public int setProjectYwcInsertMatter(PrjProject project) {
        int row = 0;
        List<PrjMatterList> matterListList = prjMatterListService.selectPrjMatterListList(new PrjMatterList().setProjectSid(project.getProjectSid()));
        if (CollectionUtil.isNotEmpty(matterListList)) {
            return row;
        }
        // 获取模板
        List<ConTaskTemplateCompare> compares = setProjectYwcInsertMatterCompare(project);
        if (CollectionUtil.isNotEmpty(compares)) {
            // 找模板明细 带有 List 值的方法
            List<PrjTaskTemplateItem> templateItemList = prjTaskTemplateItemService
                    .selectPrjTaskTemplateItemListById(compares.get(0).getAfterTaskTemplateSid());
/*            if (CollectionUtil.isNotEmpty(templateItemList)) {
                // 若“岗位类型(任务处理人)”为“销售专员”，通过对应的门店档案(若项目类型为“新店”，则通过对应的新店开店申报单)中的销售专员作为此任务的“任务处理人“
                String handler = null;
                if (ConstantsPdm.PROJECT_TYPE_XD.equals(project.getProjectType()) && project.getReferDocumentSid() != null) {
                    OpnOpenStoreApply openStoreApply = openStoreApplyMapper.selectById(project.getReferDocumentSid());
                    if (openStoreApply != null) {
                        handler = openStoreApply.getSalespersonAccount();
                    }
                } else if (project.getStoreSid() != null) {
                    BasStore store = storeMapper.selectById(project.getStoreSid());
                    if (store != null) {
                        handler = store.getSalespersonAccount();
                    }
                }
                //  若后续事项的事项处理人为空且岗位类型为”YWZY“，根据对应项目的门店的区域
                String yunweiStaffAccount = null;
                if (project.getStoreSid() != null) {
                    BasStore store = storeMapper.selectById(project.getStoreSid());
                    if (store != null && store.getSaleRegionSid() != null) {
                        List<ConRegionYunweiStaff> yunweiStaffs = conRegionYunweiStaffMapper.selectList(new LambdaQueryWrapper<ConRegionYunweiStaff>().eq(ConRegionYunweiStaff::getSaleRegionSid, store.getSaleRegionSid()));
                        if (!yunweiStaffs.isEmpty() && yunweiStaffs.size() == 1) {
                            yunweiStaffAccount = yunweiStaffs.get(0).getYunweiStaffAccount();
                        }
                    }
                }
                String finalHandler = handler;
                String finalYunweiStaffAccount = yunweiStaffAccount;
                for (PrjTaskTemplateItem item : templateItemList) {
                    PrjMatterList prjMatterList = new PrjMatterList();
                    prjMatterList.setCreateDate(new Date()).setCreatorAccount(ConstantsCms.ADMIN_ACCOUNT)
                            .setMatterStatus(ConstantsCms.PROJECT_TASK_WKS)
                            .setMatterName(item.getTaskName()).setReferDocCategory(item.getReferDocCategory())
                            .setPersonAttent(item.getPersonAttent()).setPersonAttentList(item.getPersonAttentList())
                            .setIsPictureUpload(item.getIsPictureUpload()).setMatterManager(item.getTaskManager())
                            .setPositionTypeMatterHandler(item.getPositionTypeTaskHandler())
                            .setMatterHandler(item.getTaskHandler()).setPersonNoticeMatter(item.getPersonNoticeTask())
                            .setPersonNoticeMatterList(item.getPersonNoticeTaskList()).setMatterPhase(item.getTaskPhase())
                            .setMatterBusiness(item.getTaskBusiness())
                            .setTodoDaysMatter(Integer.valueOf(String.valueOf(item.getTodoDays())))
                            .setToexpireDaysMatter(Integer.valueOf(String.valueOf(item.getToexpireDays())));
                    prjMatterList.setProjectSid(project.getProjectSid()).setProjectCode(project.getProjectCode())
                            .setPlanStartDate(project.getActualEndDate())
                            .setStoreSid(project.getStoreSid()).setStoreCode(project.getStoreCode());
                    if (prjMatterList.getPlanStartDate() != null && item.getTemplateTime() != null) {
                        // 事项的计划完成日期=事项的计划开始日期+后续事项模板的任务天数
                        LocalDate date = prjMatterList.getPlanStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        int templateTime = Integer.valueOf(String.valueOf(item.getTemplateTime()));
                        prjMatterList.setPlanEndDate(Date.from(date.plusDays(templateTime)
                                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    }

                    // 若“岗位类型(任务处理人)”为“销售专员”，通过对应的门店档案(若项目类型为“新店”，则通过对应的新店开店申报单)中的销售专员作为此任务的“任务处理人“
                    if (StrUtil.isBlank(item.getTaskHandler())
                            && ConstantsCms.POSITION_TYPE_TASK_HANDLER_XSZY.equals(item.getPositionTypeTaskHandler())) {
                        prjMatterList.setMatterHandler(finalHandler);
                    }
                    //  若后续事项的事项处理人为空且岗位类型为”YWZY“，根据对应项目的门店的区域
                    if (StrUtil.isBlank(item.getTaskHandler())
                            && ConstantsEms.POSITION_TYPE_TASK_HANDLER_YWZY.equals(item.getPositionTypeTaskHandler())) {
                        prjMatterList.setMatterHandler(finalYunweiStaffAccount);
                    }
                    row = row + prjMatterListService.insertPrjMatterList(prjMatterList);
                }
            }*/
        }
        return row;
    }


    /**
     * 将项目设置为已完成新建事项清单的逻辑
     *
     * @param project
     */
    public List<ConTaskTemplateCompare> setProjectYwcInsertMatterCompare(PrjProject project) {
        QueryWrapper<ConTaskTemplateCompare> queryWrapper = new QueryWrapper();
        // 项目类型
        queryWrapper.lambda().eq(ConTaskTemplateCompare::getProjectType, project.getProjectType());
        // 经营模式
        if (StrUtil.isBlank(project.getOperateMode())) {
            queryWrapper.lambda().isNull(ConTaskTemplateCompare::getOperateMode);
        } else {
            queryWrapper.lambda().eq(ConTaskTemplateCompare::getOperateMode, project.getOperateMode());
        }
        // 后续模板
        queryWrapper.lambda().isNotNull(ConTaskTemplateCompare::getAfterTaskTemplateSid);
        queryWrapper.lambda().orderByDesc(ConTaskTemplateCompare::getCreateDate);
        return conTaskTemplateCompareMapper.selectList(queryWrapper);
    }

}
