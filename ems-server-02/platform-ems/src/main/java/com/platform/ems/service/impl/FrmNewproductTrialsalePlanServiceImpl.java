package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.api.service.RemoteMenuService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.entity.SysMenu;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.constant.*;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.FrmNewproductTrialsalePlanAttachMapper;
import com.platform.ems.mapper.FrmNewproductTrialsalePlanMapper;
import com.platform.ems.mapper.PrjProjectMapper;
import com.platform.ems.mapper.PrjProjectTaskMapper;
import com.platform.ems.service.*;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 新品试销计划单Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-16
 */
@Service
@SuppressWarnings("all")
public class FrmNewproductTrialsalePlanServiceImpl extends ServiceImpl<FrmNewproductTrialsalePlanMapper, FrmNewproductTrialsalePlan> implements IFrmNewproductTrialsalePlanService {
    @Autowired
    private FrmNewproductTrialsalePlanMapper frmNewproductTrialsalePlanMapper;
    @Autowired
    private FrmNewproductTrialsalePlanAttachMapper newproductTrialsalePlanAttachMapper;
    @Autowired
    private PrjProjectMapper prjProjectMapper;
    @Autowired
    private PrjProjectTaskMapper prjProjectTaskMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private IPrjProjectTaskService prjProjectTaskService;
    @Autowired
    private IFrmTrialsalePlanKeyWordService trialsalePlanKeyWordService;
    @Autowired
    private IFrmTrialsalePlanCategoryAnalysisService trialsalePlanCategoryAnalysisServiceService;
    @Autowired
    private IFrmTrialsalePlanTargetService trialsalePlanTargetService;
    @Autowired
    private IFrmTrialsalePlanCpcSimulateService trialsalePlanCpcSimulateService;
    @Autowired
    private IFrmTrialsalePlanProfitSimulateService trialsalePlanProfitSimulateService;
    @Autowired
    private ISysTodoTaskService sysTodoTaskService;
    @Autowired
    private SysDefaultSettingClientMapper settingClientMapper;
    @Autowired
    private IWorkFlowService workFlowService;
    @Autowired
    private RemoteMenuService remoteMenuService;

    private static final String TITLE = "新品试销计划单";

    /**
     * 查询新品试销计划单
     *
     * @param newproductTrialsalePlanSid 新品试销计划单ID
     * @return 新品试销计划单
     */
    @Override
    public FrmNewproductTrialsalePlan selectFrmNewproductTrialsalePlanById(Long newproductTrialsalePlanSid) {
        FrmNewproductTrialsalePlan trialsalePlan = frmNewproductTrialsalePlanMapper
                .selectFrmNewproductTrialsalePlanById(newproductTrialsalePlanSid);
        // 关键词分析
        trialsalePlan.setKeyWordList(new ArrayList<>());
        List<FrmTrialsalePlanKeyWord> keyWordList = trialsalePlanKeyWordService
                .selectFrmTrialsalePlanKeyWordListById(newproductTrialsalePlanSid);
        if (CollectionUtil.isNotEmpty(keyWordList)) {
            trialsalePlan.setKeyWordList(keyWordList);
        }
        // 类目分析
        trialsalePlan.setAnalysisList(new ArrayList<>());
        List<FrmTrialsalePlanCategoryAnalysis> analysisList = trialsalePlanCategoryAnalysisServiceService
                .selectFrmTrialsalePlanCategoryAnalysisListById(newproductTrialsalePlanSid);
        if (CollectionUtil.isNotEmpty(analysisList)) {
            trialsalePlan.setAnalysisList(analysisList);
        }
        // 目标预定
        trialsalePlan.setTargetList(new ArrayList<>());
        List<FrmTrialsalePlanTarget> targetList = trialsalePlanTargetService
                .selectFrmTrialsalePlanTargetListById(newproductTrialsalePlanSid);
        if (CollectionUtil.isNotEmpty(targetList)) {
            trialsalePlan.setTargetList(targetList);
        }
        // CPC模拟数据
        trialsalePlan.setCpcSimulateList(new ArrayList<>());
        List<FrmTrialsalePlanCpcSimulate> cpcSimulateList = trialsalePlanCpcSimulateService
                .selectFrmTrialsalePlanCpcSimulateListById(newproductTrialsalePlanSid);
        if (CollectionUtil.isNotEmpty(cpcSimulateList)) {
            trialsalePlan.setCpcSimulateList(cpcSimulateList);
        }
        // 利润模拟
        trialsalePlan.setProfitSimulateList(new ArrayList<>());
        List<FrmTrialsalePlanProfitSimulate> profitSimulateList = trialsalePlanProfitSimulateService
                .selectFrmTrialsalePlanProfitSimulateListById(newproductTrialsalePlanSid);
        if (CollectionUtil.isNotEmpty(profitSimulateList)) {
            trialsalePlan.setProfitSimulateList(profitSimulateList);
        }
        // 附件
        trialsalePlan.setAttachmentList(new ArrayList<>());
        List<FrmNewproductTrialsalePlanAttach> attachmentList = newproductTrialsalePlanAttachMapper
                .selectFrmNewproductTrialsalePlanAttachList(new FrmNewproductTrialsalePlanAttach()
                        .setNewproductTrialsalePlanSid(newproductTrialsalePlanSid));
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            trialsalePlan.setAttachmentList(attachmentList);
        }
        // 操作日志
        MongodbUtil.find(trialsalePlan);
        return trialsalePlan;
    }

    /**
     * 查询新品试销计划单列表
     *
     * @param frmNewproductTrialsalePlan 新品试销计划单
     * @return 新品试销计划单
     */
    @Override
    public List<FrmNewproductTrialsalePlan> selectFrmNewproductTrialsalePlanList(FrmNewproductTrialsalePlan trialsalePlan) {
        return frmNewproductTrialsalePlanMapper.selectFrmNewproductTrialsalePlanList(trialsalePlan);
    }

    /**
     * 提交前校验
     *
     * @param trialsalePlan 试销计划单 @return 结果
     */
    @Override
    public EmsResultEntity submitVerify(FrmNewproductTrialsalePlan trialsalePlan) {
        if (HandleStatus.SUBMIT.getCode().equals(trialsalePlan.getHandleStatus())
                && trialsalePlan.getProjectTaskSid() != null && trialsalePlan.getProjectSid() != null) {
            if (trialsalePlan.getProjectSid() != null) {
                List<PrjProject> projectList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda()
                        .eq(PrjProject::getProjectSid, trialsalePlan.getProjectSid())
                        .eq(PrjProject::getHandleStatus, ConstantsEms.INVALID_STATUS));
                if (CollectionUtil.isNotEmpty(projectList)) {
                    throw new BaseException("所属项目已作废，不允许提交！");
                }
            }
            // 项目下的任务明细
            String noticeType = ApiThreadLocalUtil.get().getSysUser().getClient().getNoticeTypePreTaskIncomplete();
            if (StrUtil.isBlank(noticeType)) {
                return EmsResultEntity.success();
            }
            List<PrjProjectTask> taskList = prjProjectTaskService.selectPrjProjectTaskListById(trialsalePlan.getProjectSid());
            if (CollectionUtil.isNotEmpty(taskList)) {
                // 拍照样获取单对应的项目任务明细
                PrjProjectTask task = taskList.stream().filter(o->o.getProjectTaskSid().equals(trialsalePlan.getProjectTaskSid())).findFirst().get();
                if (task != null && ArrayUtil.isNotEmpty(task.getPreTaskList())) {
                    // 1）根据项目任务SID，从数据库表结构（s_prj_project_task）中找到对应任务明细的前置任务节点；若前置任务节点为空，跳过2操作
                    // 2）根据项目SID和1）得到的“前置任务节点”（前置任务节点可能存在多个），从数据库表结构（s_prj_project_task）
                    // 中找到对应任务明细的任务状态，若任务状态非“已完成”，提示：前置任务未完成，是否确认修改？点击是则进行3）操作，点击否则关闭弹窗
                    List<String> preTaskList = Arrays.asList(task.getPreTaskList());
                    List<PrjProjectTask> notComplete = taskList.stream().filter(o -> preTaskList.contains(o.getTaskCode())
                            && !ConstantsPdm.PROJECT_TASK_YWC.equals(o.getTaskStatus())).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(notComplete)) {
                        if (ConstantsEms.S_MESSAGE_DISPLAT_TYPE_TS.equals(noticeType)) {
                            return EmsResultEntity.warning(null, null, "前置任务未完成，是否确认提交？");
                        }
                        else if (ConstantsEms.S_MESSAGE_DISPLAT_TYPE_BC.equals(noticeType)) {
                            return EmsResultEntity.error(null, null, "前置任务未完成，无法提交！");
                        }
                    }
                }
            }
        }
        return EmsResultEntity.success();
    }

    /**
     * 新增新品试销计划单
     * 需要注意编码重复校验
     *
     * @param frmNewproductTrialsalePlan 新品试销计划单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmNewproductTrialsalePlan(FrmNewproductTrialsalePlan trialsalePlan) {
        String handleStatus = "";
        handleStatus = trialsalePlan.getHandleStatus();
        // 判断是否配置需要审批
        boolean flag = true;
        String remark = null;
        if (ConstantsEms.SUBMIT_STATUS.equals(trialsalePlan.getHandleStatus())) {
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            // 不启用审批流程
            if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowShixjhd())) {
                flag = false;
                remark = "提交并确认";
                trialsalePlan.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(trialsalePlan.getHandleStatus())) {
            trialsalePlan.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 不存 样品 和 开发计划
        trialsalePlan.setDevelopPlanSid(null).setDevelopPlanCode(null)
                .setSampleSid(null).setSampleCode(null);
        int row = frmNewproductTrialsalePlanMapper.insert(trialsalePlan);
        if (row > 0) {
            FrmNewproductTrialsalePlan plan = frmNewproductTrialsalePlanMapper.selectFrmNewproductTrialsalePlanById(
                    trialsalePlan.getNewproductTrialsalePlanSid());
            trialsalePlan.setNewproductTrialsalePlanCode(plan.getNewproductTrialsalePlanCode());
            // 关键词分析
            if (CollectionUtil.isNotEmpty(trialsalePlan.getKeyWordList())) {
                trialsalePlanKeyWordService.insertFrmTrialsalePlanKeyWordList(trialsalePlan);
            }
            // 类目分析
            if (CollectionUtil.isNotEmpty(trialsalePlan.getAnalysisList())) {
                trialsalePlanCategoryAnalysisServiceService.insertFrmTrialsalePlanCategoryAnalysisList(trialsalePlan);
            }
            // 目标预定
            if (CollectionUtil.isNotEmpty(trialsalePlan.getTargetList())) {
                trialsalePlanTargetService.insertFrmTrialsalePlanTargetList(trialsalePlan);
            }
            // CPC模拟数据
            if (CollectionUtil.isNotEmpty(trialsalePlan.getCpcSimulateList())) {
                trialsalePlanCpcSimulateService.insertFrmTrialsalePlanCpcSimulateList(trialsalePlan);
            }
            // 利润模拟
            if (CollectionUtil.isNotEmpty(trialsalePlan.getProfitSimulateList())) {
                trialsalePlanProfitSimulateService.insertFrmTrialsalePlanProfitSimulateList(trialsalePlan);
            }
            // 写入附件
            if (CollectionUtil.isNotEmpty(trialsalePlan.getAttachmentList())) {
                trialsalePlan.getAttachmentList().forEach(item->{
                    item.setNewproductTrialsalePlanSid(trialsalePlan.getNewproductTrialsalePlanSid());
                    item.setNewproductTrialsalePlanCode(trialsalePlan.getNewproductTrialsalePlanCode());
                });
                newproductTrialsalePlanAttachMapper.inserts(trialsalePlan.getAttachmentList());
            }
            // 删除项目档案任务明细的待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_PROJECT_TASK)
                    .eq(SysTodoTask::getDocumentSid, trialsalePlan.getProjectSid())
                    .eq(SysTodoTask::getDocumentItemSid, trialsalePlan.getProjectTaskSid())
                    .likeLeft(SysTodoTask::getTitle, "还未开始，请及时跟进！"));
            // 提交或者确认处理逻辑
            Long[] sids = new Long[]{trialsalePlan.getNewproductTrialsalePlanSid()};
            this.approve(sids, trialsalePlan.getHandleStatus());
            // 待办
            if (ConstantsEms.SAVA_STATUS.equals(trialsalePlan.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_FRM_NEWPRODUCT_TRIALSALE_PLAN)
                        .setDocumentSid(trialsalePlan.getNewproductTrialsalePlanSid());
                String erpCode = plan.getErpMaterialSkuBarcode() == null ? "" : plan.getErpMaterialSkuBarcode();
                sysTodoTask.setTitle(erpCode + "新品试销计划单" + trialsalePlan.getNewproductTrialsalePlanCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(trialsalePlan.getNewproductTrialsalePlanCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                // 获取菜单id
                SysMenu menu = new SysMenu();
                menu.setMenuName(ConstantsWorkbench.TODO_FRM_NEW_TRI_PLAN_MENU_NAME);
                menu = remoteMenuService.getInfoByName(menu).getData();
                if (menu != null && menu.getMenuId() != null) {
                    sysTodoTask.setMenuId(menu.getMenuId());
                }
                sysTodoTaskService.insertSysTodoTask(sysTodoTask);
            }
            // 提交启动审批
            else if (ConstantsEms.SUBMIT_STATUS.equals(trialsalePlan.getHandleStatus())) {
                if (flag) {
                    trialsalePlan.setErpMaterialSkuBarcode(plan.getErpMaterialSkuBarcode());
                    this.submit(trialsalePlan);
                }
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FrmNewproductTrialsalePlan(), trialsalePlan);
            MongodbDeal.insert(trialsalePlan.getNewproductTrialsalePlanSid(), handleStatus,
                    msgList, TITLE, remark);
        }
        return row;
    }

    /**
     * 提交，审批 的处理逻辑
     *
     * @param sids，handleStatus
     * @return
     */
    private void approve(Long[] sids, String handleStatus) {
        if (sids == null || sids.length == 0 || StrUtil.isBlank(handleStatus)) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
            // 根据 project_sid + 硬编码”TGHZ“ 从 ”项目档案-任务表（s_prj_project_task）“中，
            // 根据”project_sid + relate_business_form_code“查找到对应的任务明细行，
            // 然后将该行的”任务状态“更新为 已完成 / 进行中
            List<FrmNewproductTrialsalePlan> designList = frmNewproductTrialsalePlanMapper.selectList(new QueryWrapper<FrmNewproductTrialsalePlan>()
                    .lambda().in(FrmNewproductTrialsalePlan::getNewproductTrialsalePlanSid, sids));
            Long[] projectTaskSids = designList.stream().map(FrmNewproductTrialsalePlan::getProjectTaskSid).toArray(Long[]::new);
            if (ArrayUtil.isEmpty(projectTaskSids)) {
                return;
            }
            LambdaUpdateWrapper<PrjProjectTask> updateProjectTaskWrapper = new LambdaUpdateWrapper<>();
            updateProjectTaskWrapper.in(PrjProjectTask::getProjectTaskSid, projectTaskSids);
            // 确认
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                // 已完成
                updateProjectTaskWrapper.set(PrjProjectTask::getTaskStatus, ConstantsPdm.PROJECT_TASK_YWC);
                updateProjectTaskWrapper.set(PrjProjectTask::getActualEndDate, new Date());
            }
            // 提交
            else if (ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                // 进行中
                updateProjectTaskWrapper.set(PrjProjectTask::getTaskStatus, ConstantsPdm.PROJECT_TASK_JXZ);
            }
            // 驳回到提交人
            else if (HandleStatus.RETURNED.getCode().equals(handleStatus)) {
                // 未开始
                updateProjectTaskWrapper.set(PrjProjectTask::getTaskStatus, ConstantsPdm.PROJECT_TASK_WKS);
            }
            prjProjectTaskMapper.update(null, updateProjectTaskWrapper);
        }
    }

    /**
     * 批量修改附件信息
     *
     * @param trialsalePlan 新品试销计划单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateFrmNewproductTrialsalePlanAttach(FrmNewproductTrialsalePlan trialsalePlan) {
        // 先删后加
        newproductTrialsalePlanAttachMapper.delete(new QueryWrapper<FrmNewproductTrialsalePlanAttach>().lambda()
                .eq(FrmNewproductTrialsalePlanAttach::getNewproductTrialsalePlanSid, trialsalePlan.getNewproductTrialsalePlanSid()));
        if (CollectionUtil.isNotEmpty(trialsalePlan.getAttachmentList())) {
            trialsalePlan.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getNewproductTrialsalePlanAttachSid() == null) {
                    att.setNewproductTrialsalePlanSid(trialsalePlan.getNewproductTrialsalePlanSid());
                    att.setNewproductTrialsalePlanCode(trialsalePlan.getNewproductTrialsalePlanCode());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            newproductTrialsalePlanAttachMapper.inserts(trialsalePlan.getAttachmentList());
        }
    }

    /**
     * 修改新品试销计划单
     *
     * @param frmNewproductTrialsalePlan 新品试销计划单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmNewproductTrialsalePlan(FrmNewproductTrialsalePlan trialsalePlan) {
        FrmNewproductTrialsalePlan original = frmNewproductTrialsalePlanMapper
                .selectFrmNewproductTrialsalePlanById(trialsalePlan.getNewproductTrialsalePlanSid());
        String handleStatus = "";
        handleStatus = trialsalePlan.getHandleStatus();
        // 判断是否配置需要审批
        boolean flag = true;
        String remark = null;
        if (ConstantsEms.SUBMIT_STATUS.equals(trialsalePlan.getHandleStatus())) {
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            // 不启用审批流程
            if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowShixjhd())) {
                flag = false;
                remark = "提交并确认";
                trialsalePlan.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(trialsalePlan.getHandleStatus())) {
            trialsalePlan.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 不存 样品 和 开发计划
        trialsalePlan.setDevelopPlanSid(null).setDevelopPlanCode(null)
                .setSampleSid(null).setSampleCode(null);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, trialsalePlan);
        if (CollectionUtil.isNotEmpty(msgList)) {
            trialsalePlan.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = frmNewproductTrialsalePlanMapper.updateAllById(trialsalePlan);
        if (row > 0) {
            // 关键词分析
            trialsalePlanKeyWordService.updateFrmTrialsalePlanKeyWordList(trialsalePlan);
            // 类目分析
            trialsalePlanCategoryAnalysisServiceService.updateFrmTrialsalePlanCategoryAnalysisList(trialsalePlan);
            // 目标预定
            trialsalePlanTargetService.updateFrmTrialsalePlanTargetList(trialsalePlan);
            // CPC模拟数据
            trialsalePlanCpcSimulateService.updateFrmTrialsalePlanCpcSimulateList(trialsalePlan);
            // 利润模拟
            trialsalePlanProfitSimulateService.updateFrmTrialsalePlanProfitSimulateList(trialsalePlan);
            // 修改附件
            this.updateFrmNewproductTrialsalePlanAttach(trialsalePlan);
            // 不是保存状态删除待办
            Long[] sids = new Long[]{trialsalePlan.getNewproductTrialsalePlanSid()};
            if (!ConstantsEms.SAVA_STATUS.equals(trialsalePlan.getHandleStatus())) {
                sysTodoTaskService.deleteSysTodoTaskList(sids,
                        trialsalePlan.getHandleStatus(), ConstantsTable.TABLE_FRM_NEWPRODUCT_TRIALSALE_PLAN);
            }
            // 审批处理逻辑
            this.approve(sids, trialsalePlan.getHandleStatus());
            // 提交启动审批
            if (ConstantsEms.SUBMIT_STATUS.equals(trialsalePlan.getHandleStatus())) {
                if (flag) {
                    this.submit(trialsalePlan);
                }
            }
            //插入日志
            MongodbDeal.update(trialsalePlan.getNewproductTrialsalePlanSid(), original.getHandleStatus(),
                    handleStatus, msgList, TITLE, remark);
        }
        return row;
    }

    /**
     * 变更新品试销计划单
     *
     * @param frmNewproductTrialsalePlan 新品试销计划单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFrmNewproductTrialsalePlan(FrmNewproductTrialsalePlan trialsalePlan) {
        FrmNewproductTrialsalePlan response = frmNewproductTrialsalePlanMapper
                .selectFrmNewproductTrialsalePlanById(trialsalePlan.getNewproductTrialsalePlanSid());
        // 不存 样品 和 开发计划
        trialsalePlan.setDevelopPlanSid(null).setDevelopPlanCode(null)
                .setSampleSid(null).setSampleCode(null);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, trialsalePlan);
        if (CollectionUtil.isNotEmpty(msgList)) {
            trialsalePlan.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = frmNewproductTrialsalePlanMapper.updateAllById(trialsalePlan);
        if (row > 0) {
            // 关键词分析
            trialsalePlanKeyWordService.updateFrmTrialsalePlanKeyWordList(trialsalePlan);
            // 类目分析
            trialsalePlanCategoryAnalysisServiceService.updateFrmTrialsalePlanCategoryAnalysisList(trialsalePlan);
            // 目标预定
            trialsalePlanTargetService.updateFrmTrialsalePlanTargetList(trialsalePlan);
            // CPC模拟数据
            trialsalePlanCpcSimulateService.updateFrmTrialsalePlanCpcSimulateList(trialsalePlan);
            // 利润模拟
            trialsalePlanProfitSimulateService.updateFrmTrialsalePlanProfitSimulateList(trialsalePlan);
            // 修改附件
            this.updateFrmNewproductTrialsalePlanAttach(trialsalePlan);
            //插入日志
            MongodbUtil.insertUserLog(trialsalePlan.getNewproductTrialsalePlanSid(),
                    BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除新品试销计划单
     *
     * @param newproductTrialsalePlanSids 需要删除的新品试销计划单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmNewproductTrialsalePlanByIds(List<Long> newproductTrialsalePlanSids) {
        List<FrmNewproductTrialsalePlan> list = frmNewproductTrialsalePlanMapper.selectList(new QueryWrapper<FrmNewproductTrialsalePlan>()
                .lambda().in(FrmNewproductTrialsalePlan::getNewproductTrialsalePlanSid, newproductTrialsalePlanSids));
        int row = frmNewproductTrialsalePlanMapper.deleteBatchIds(newproductTrialsalePlanSids);
        if (row > 0) {
            // 关键词分析
            trialsalePlanKeyWordService.deleteFrmTrialsalePlanKeyWordByPlan(newproductTrialsalePlanSids);
            // 类目分析
            trialsalePlanCategoryAnalysisServiceService.deleteFrmTrialsalePlanCategoryAnalysisByPlan(newproductTrialsalePlanSids);
            // 目标预定
            trialsalePlanTargetService.deleteFrmTrialsalePlanTargetByPlan(newproductTrialsalePlanSids);
            // CPC模拟数据
            trialsalePlanCpcSimulateService.deleteFrmTrialsalePlanCpcSimulateByPlan(newproductTrialsalePlanSids);
            // 利润模拟
            trialsalePlanProfitSimulateService.deleteFrmTrialsalePlanProfitSimulateByPlan(newproductTrialsalePlanSids);
            // 删除附件
            newproductTrialsalePlanAttachMapper.delete(new QueryWrapper<FrmNewproductTrialsalePlanAttach>().lambda()
                    .in(FrmNewproductTrialsalePlanAttach::getNewproductTrialsalePlanSid, newproductTrialsalePlanSids));
            // 删除待办
            Long[] sids = newproductTrialsalePlanSids.toArray(new Long[newproductTrialsalePlanSids.size()]);
            sysTodoTaskService.deleteSysTodoTaskList(sids, null,
                    ConstantsTable.TABLE_FRM_NEWPRODUCT_TRIALSALE_PLAN);
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FrmNewproductTrialsalePlan());
                MongodbUtil.insertUserLog(o.getNewproductTrialsalePlanSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 提交
     *
     * @param trialsalePlan
     * @return
     */
    private void submit(FrmNewproductTrialsalePlan trialsalePlan){
        if (trialsalePlan.getProjectSid() != null) {
            List<PrjProject> projectList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda()
                    .eq(PrjProject::getProjectSid, trialsalePlan.getProjectSid())
                    .eq(PrjProject::getHandleStatus, ConstantsEms.INVALID_STATUS));
            if (CollectionUtil.isNotEmpty(projectList)) {
                throw new BaseException("所属项目已作废，不允许提交！");
            }
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("formId", trialsalePlan.getNewproductTrialsalePlanSid());
        variables.put("formCode", trialsalePlan.getNewproductTrialsalePlanCode());
        variables.put("formType", FormType.NewproductTrialsalePlan.getCode());
        variables.put("startUserId", ApiThreadLocalUtil.get().getSysUser().getUserId());
        variables.put("erpCode", trialsalePlan.getErpMaterialSkuBarcode());
        try {
            AjaxResult result = workFlowService.submitOnly(variables);
        } catch (BaseException e) {
            throw e;
        }
    }

    /**
     * 更改处理状态
     *
     * @param sids, handleStatus
     * @return
     */
    private int updateHandle(Long[] sids, String handleStatus) {
        LambdaUpdateWrapper<FrmNewproductTrialsalePlan> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FrmNewproductTrialsalePlan::getNewproductTrialsalePlanSid, sids);
        updateWrapper.set(FrmNewproductTrialsalePlan::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(FrmNewproductTrialsalePlan::getConfirmDate, new Date());
            updateWrapper.set(FrmNewproductTrialsalePlan::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return frmNewproductTrialsalePlanMapper.update(null, updateWrapper);
    }

    /**
     * 更改确认状态
     *
     * @param frmNewproductTrialsalePlan
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FrmNewproductTrialsalePlan trialsalePlan) {
        int row = 0;
        Long[] sids = trialsalePlan.getNewproductTrialsalePlanSidList();
        // 处理状态
        String handleStatus = "";
        handleStatus = trialsalePlan.getHandleStatus();
        if (StrUtil.isNotBlank(trialsalePlan.getBusinessType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(trialsalePlan.getBusinessType());
        }
        if (sids != null && sids.length > 0) {
            row = sids.length;
            // 获取数据
            List<FrmNewproductTrialsalePlan> planList = frmNewproductTrialsalePlanMapper.selectFrmNewproductTrialsalePlanList(
                    new FrmNewproductTrialsalePlan().setNewproductTrialsalePlanSidList(sids));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus,
                        ConstantsTable.TABLE_FRM_NEWPRODUCT_TRIALSALE_PLAN);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(trialsalePlan.getBusinessType())) {
                // 判断是否配置需要审批
                SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                        .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
                // 不启用审批流程
                if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowShixjhd())) {
                    // 审批处理逻辑
                    this.approve(sids, ConstantsEms.CHECK_STATUS);
                    // 修改处理状态
                    this.updateHandle(sids, ConstantsEms.CHECK_STATUS);
                    //插入日志
                    for (int i = 0; i < planList.size(); i++) {
                        MongodbUtil.insertUserLog(planList.get(i).getNewproductTrialsalePlanSid(),
                                BusinessType.SUBMIT.getValue(), null, TITLE, "提交并确认");
                    }
                    return row;
                }
                // 审批处理逻辑
                this.approve(sids, ConstantsEms.SUBMIT_STATUS);
                // 修改处理状态
                this.updateHandle(sids, ConstantsEms.SUBMIT_STATUS);
                // 开启工作流
                for (int i = 0; i < planList.size(); i++) {
                    this.submit(planList.get(i));
                    //插入日志
                    MongodbUtil.insertUserLog(planList.get(i).getNewproductTrialsalePlanSid(),
                            BusinessType.SUBMIT.getValue(), null, TITLE, trialsalePlan.getComment());
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(trialsalePlan.getBusinessType())) {
                Map<String, List<FrmNewproductTrialsalePlan>> map = planList.stream()
                        .collect(Collectors.groupingBy(o -> String.valueOf(o.getApprovalUserId())));
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                if (map != null) {
                    for (String key : map.keySet()) {
                        if (key == null || (!key.equals(userId.toString()) && !key.startsWith(userId.toString()+",")
                                && key.indexOf(","+userId.toString()) == -1 && key.indexOf(", "+userId.toString()) == -1)) {
                            throw new BaseException("您不是当前审批节点处理人，无法点击此按钮！");
                        }
                    }
                }
                // 审批意见
                String comment = "";
                for (int i = 0; i < planList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(planList.get(i).getNewproductTrialsalePlanSid().toString());
                    taskVo.setFormId(Long.valueOf(planList.get(i).getNewproductTrialsalePlanSid().toString()));
                    taskVo.setFormCode(planList.get(i).getNewproductTrialsalePlanCode().toString());
                    taskVo.setErpCode(planList.get(i).getErpMaterialSkuBarcode());
                    taskVo.setFormType(FormType.NewproductTrialsalePlan.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(trialsalePlan.getComment());
                    try {
                        SysFormProcess process = workFlowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 最后一级审批通过处理逻辑
                            this.approve(new Long[]{planList.get(i).getNewproductTrialsalePlanSid()}, ConstantsEms.CHECK_STATUS);
                            // 修改处理状态
                            this.updateHandle(new Long[]{planList.get(i).getNewproductTrialsalePlanSid()}, ConstantsEms.CHECK_STATUS);
                        }
                        comment = process.getRemark();
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(planList.get(i).getNewproductTrialsalePlanSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, comment);
                }
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(trialsalePlan.getBusinessType())) {
                Map<String, List<FrmNewproductTrialsalePlan>> map = planList.stream()
                        .collect(Collectors.groupingBy(o -> String.valueOf(o.getApprovalUserId())));
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                if (map != null) {
                    for (String key : map.keySet()) {
                        if (key == null || (!key.equals(userId.toString()) && !key.startsWith(userId.toString()+",")
                                && key.indexOf(","+userId.toString()) == -1 && key.indexOf(", "+userId.toString()) == -1)) {
                            throw new BaseException("您不是当前审批节点处理人，无法点击此按钮！");
                        }
                    }
                }
                // 审批意见
                String comment = "";
                for (int i = 0; i < planList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setTargetKey("2");
                    taskVo.setBusinessKey(planList.get(i).getNewproductTrialsalePlanSid().toString());
                    taskVo.setFormId(Long.valueOf(planList.get(i).getNewproductTrialsalePlanSid().toString()));
                    taskVo.setFormCode(planList.get(i).getNewproductTrialsalePlanCode().toString());
                    taskVo.setErpCode(planList.get(i).getErpMaterialSkuBarcode());
                    taskVo.setFormType(FormType.NewproductTrialsalePlan.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(trialsalePlan.getComment());
                    try {
                        SysFormProcess process = workFlowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 审批驳回到提交人处理逻辑
                            this.approve(new Long[]{planList.get(i).getNewproductTrialsalePlanSid()}, HandleStatus.RETURNED.getCode());
                            // 修改处理状态
                            this.updateHandle(new Long[]{planList.get(i).getNewproductTrialsalePlanSid()}, HandleStatus.RETURNED.getCode());
                        }
                        comment = process.getRemark();
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(planList.get(i).getNewproductTrialsalePlanSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, comment);
                }
            }
        }
        return row;
    }

}
