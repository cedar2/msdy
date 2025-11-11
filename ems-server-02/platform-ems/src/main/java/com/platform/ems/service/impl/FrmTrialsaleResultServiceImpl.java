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
import com.platform.ems.mapper.*;
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
 * 试销结果单Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-19
 */
@Service
@SuppressWarnings("all")
public class FrmTrialsaleResultServiceImpl extends ServiceImpl<FrmTrialsaleResultMapper, FrmTrialsaleResult> implements IFrmTrialsaleResultService {
    @Autowired
    private FrmTrialsaleResultMapper frmTrialsaleResultMapper;
    @Autowired
    private IFrmTrialsaleResultAdviceService resultAdviceService;
    @Autowired
    private IFrmTrialsaleResultPlanItemService resultPlanItemService;
    @Autowired
    private IFrmTrialsaleResultPriceSchemeService resultPriceSchemeService;
    @Autowired
    private FrmTrialsaleResultAttachMapper frmTrialsaleResultAttachMapper;
    @Autowired
    private PrjProjectMapper prjProjectMapper;
    @Autowired
    private PrjProjectTaskMapper prjProjectTaskMapper;
    @Autowired
    private DevDevelopPlanMapper developPlanMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SysDefaultSettingClientMapper settingClientMapper;
    @Autowired
    private IPrjProjectTaskService prjProjectTaskService;
    @Autowired
    private ISysTodoTaskService sysTodoTaskService;
    @Autowired
    private IWorkFlowService workFlowService;
    @Autowired
    private SysPdmApproveSettingConfirmMapper sysPdmApproveMapper;
    @Autowired
    private RemoteMenuService remoteMenuService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static final String TITLE = "试销结果单";

    /**
     * 查询试销结果单
     *
     * @param trialsaleResultSid 试销结果单ID
     * @return 试销结果单
     */
    @Override
    public FrmTrialsaleResult selectFrmTrialsaleResultById(Long trialsaleResultSid) {
        FrmTrialsaleResult frmTrialsaleResult = frmTrialsaleResultMapper.selectFrmTrialsaleResultById(trialsaleResultSid);
        // sku1Code 处理
        if (StrUtil.isNotBlank(frmTrialsaleResult.getSku1Code())) {
            String[] skuCodeList = frmTrialsaleResult.getSku1Code().split(";");
            frmTrialsaleResult.setSku1CodeList(skuCodeList);
        }
        // 优化建议
        frmTrialsaleResult.setAdviceList(new ArrayList<>());
        List<FrmTrialsaleResultAdvice> adviceList = resultAdviceService.selectFrmTrialsaleResultAdviceListById(trialsaleResultSid);
        if (CollectionUtil.isNotEmpty(adviceList)) {
            frmTrialsaleResult.setAdviceList(adviceList);
        }
        // 计划项
        frmTrialsaleResult.setPlanItemList(new ArrayList<>());
        List<FrmTrialsaleResultPlanItem> planItemList = resultPlanItemService.selectFrmTrialsaleResultPlanItemListById(trialsaleResultSid);
        if (CollectionUtil.isNotEmpty(planItemList)) {
            frmTrialsaleResult.setPlanItemList(planItemList);
        }
        // 定价方案
        frmTrialsaleResult.setPriceSchemeList(new ArrayList<>());
        List<FrmTrialsaleResultPriceScheme> priceSchemeList = resultPriceSchemeService.selectFrmTrialsaleResultPriceSchemeListById(trialsaleResultSid);
        if (CollectionUtil.isNotEmpty(priceSchemeList)) {
            frmTrialsaleResult.setPriceSchemeList(priceSchemeList);
        }
        // 附件
        frmTrialsaleResult.setAttachmentList(new ArrayList<>());
        List<FrmTrialsaleResultAttach> attachmentList = frmTrialsaleResultAttachMapper.selectFrmTrialsaleResultAttachList(new FrmTrialsaleResultAttach()
                .setTrialsaleResultSid(trialsaleResultSid));
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            frmTrialsaleResult.setAttachmentList(attachmentList);
        }
        // 操作日志
        MongodbUtil.find(frmTrialsaleResult);
        return frmTrialsaleResult;
    }

    /**
     * 查询试销结果单列表
     *
     * @param frmTrialsaleResult 试销结果单
     * @return 试销结果单
     */
    @Override
    public List<FrmTrialsaleResult> selectFrmTrialsaleResultList(FrmTrialsaleResult frmTrialsaleResult) {
        return frmTrialsaleResultMapper.selectFrmTrialsaleResultList(frmTrialsaleResult);
    }

    /**
     * 提交前校验
     *
     * @param frmTrialsaleResult 试销结果单
     * @return 结果
     */
    @Override
    public EmsResultEntity submitVerify(FrmTrialsaleResult frmTrialsaleResult) {
        if (HandleStatus.SUBMIT.getCode().equals(frmTrialsaleResult.getHandleStatus())
                && frmTrialsaleResult.getProjectTaskSid() != null && frmTrialsaleResult.getProjectSid() != null) {
            if (frmTrialsaleResult.getProjectSid() != null) {
                List<PrjProject> projectList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda()
                        .eq(PrjProject::getProjectSid, frmTrialsaleResult.getProjectSid())
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
            List<PrjProjectTask> taskList = prjProjectTaskService.selectPrjProjectTaskListById(frmTrialsaleResult.getProjectSid());
            if (CollectionUtil.isNotEmpty(taskList)) {
                // 拍照样获取单对应的项目任务明细
                PrjProjectTask task = taskList.stream().filter(o->o.getProjectTaskSid().equals(frmTrialsaleResult.getProjectTaskSid())).findFirst().get();
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
     * 新增试销结果单
     * 需要注意编码重复校验
     *
     * @param frmTrialsaleResult 试销结果单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmTrialsaleResult(FrmTrialsaleResult frmTrialsaleResult) {
        String handleStatus = "";
        handleStatus = frmTrialsaleResult.getHandleStatus();
        // 判断是否配置需要审批
        boolean flag = true;
        String remark = null;
        if (ConstantsEms.SUBMIT_STATUS.equals(frmTrialsaleResult.getHandleStatus())) {
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            // 不启用审批流程
            if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowShixjgd())) {
                flag = false;
                remark = "提交并确认";
                frmTrialsaleResult.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(frmTrialsaleResult.getHandleStatus())) {
            frmTrialsaleResult.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 字段数据处理
        setData(frmTrialsaleResult);
        int row = frmTrialsaleResultMapper.insert(frmTrialsaleResult);
        if (row > 0) {
            FrmTrialsaleResult result = frmTrialsaleResultMapper.selectFrmTrialsaleResultById(frmTrialsaleResult.getTrialsaleResultSid());
            frmTrialsaleResult.setTrialsaleResultCode(result.getTrialsaleResultCode());
            // 优化建议
            resultAdviceService.insertFrmTrialsaleResultAdviceList(frmTrialsaleResult);
            // 计划项
            resultPlanItemService.insertFrmTrialsaleResultPlanItemList(frmTrialsaleResult);
            // 定价方案
            resultPriceSchemeService.insertFrmTrialsaleResultPriceSchemeList(frmTrialsaleResult);
            // 写入附件
            if (CollectionUtil.isNotEmpty(frmTrialsaleResult.getAttachmentList())) {
                frmTrialsaleResult.getAttachmentList().forEach(item->{
                    item.setTrialsaleResultSid(frmTrialsaleResult.getTrialsaleResultSid());
                    item.setTrialsaleResultCode(frmTrialsaleResult.getTrialsaleResultCode());
                });
                frmTrialsaleResultAttachMapper.inserts(frmTrialsaleResult.getAttachmentList());
            }
            // 删除项目档案任务明细的待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_PROJECT_TASK)
                    .eq(SysTodoTask::getDocumentSid, frmTrialsaleResult.getProjectSid())
                    .eq(SysTodoTask::getDocumentItemSid, frmTrialsaleResult.getProjectTaskSid())
                    .likeLeft(SysTodoTask::getTitle, "还未开始，请及时跟进！"));
            // 提交或者确认处理逻辑
            Long[] sids = new Long[]{frmTrialsaleResult.getTrialsaleResultSid()};
            this.approve(sids, frmTrialsaleResult.getHandleStatus());
            // 待办
            if (ConstantsEms.SAVA_STATUS.equals(frmTrialsaleResult.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_FRM_TRIALSALE_RESULT)
                        .setDocumentSid(frmTrialsaleResult.getTrialsaleResultSid());
                String erpCode = result.getErpMaterialSkuBarcode() == null ? "" : result.getErpMaterialSkuBarcode();
                sysTodoTask.setTitle(erpCode + "试销结果单" + frmTrialsaleResult.getTrialsaleResultCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(frmTrialsaleResult.getTrialsaleResultCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                // 获取菜单id
                SysMenu menu = new SysMenu();
                menu.setMenuName(ConstantsWorkbench.TODO_FRM_TRIAL_RESULT_MENU_NAME);
                menu = remoteMenuService.getInfoByName(menu).getData();
                if (menu != null && menu.getMenuId() != null) {
                    sysTodoTask.setMenuId(menu.getMenuId());
                }
                sysTodoTaskService.insertSysTodoTask(sysTodoTask);
            }
            // 提交启动审批
            else if (ConstantsEms.SUBMIT_STATUS.equals(frmTrialsaleResult.getHandleStatus())) {
                if (flag) {
                    frmTrialsaleResult.setErpMaterialSkuBarcode(result.getErpMaterialSkuBarcode());
                    this.submit(frmTrialsaleResult);
                }
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FrmTrialsaleResult(), frmTrialsaleResult);
            MongodbDeal.insert(frmTrialsaleResult.getTrialsaleResultSid(), handleStatus, msgList, TITLE, remark);
        }
        return row;
    }

    /**
     * 数据字段处理
     *
     * @param frmTrialsaleResult 试销结果单
     * @return 结果
     */
    private void setData(FrmTrialsaleResult frmTrialsaleResult) {
        // 不存 样品 和 开发计划
        frmTrialsaleResult.setDevelopPlanSid(null).setDevelopPlanCode(null)
                .setSampleSid(null).setSampleCode(null);
        // sku1Ccode处理
        if (ArrayUtil.isNotEmpty(frmTrialsaleResult.getSku1CodeList())) {
            String skuCode = "";
            for (int i = 0; i < frmTrialsaleResult.getSku1CodeList().length; i++) {
                skuCode = skuCode + frmTrialsaleResult.getSku1CodeList()[i] + ";";
            }
            frmTrialsaleResult.setSku1Code(skuCode);
        }
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
            List<FrmTrialsaleResult> designList = frmTrialsaleResultMapper.selectList(new QueryWrapper<FrmTrialsaleResult>()
                    .lambda().in(FrmTrialsaleResult::getTrialsaleResultSid, sids));
            Long[] projectTaskSids = designList.stream().map(FrmTrialsaleResult::getProjectTaskSid).toArray(Long[]::new);
            if (ArrayUtil.isEmpty(projectTaskSids)) {
                return;
            }
            LambdaUpdateWrapper<PrjProjectTask> updateProjectTaskWrapper = new LambdaUpdateWrapper<>();
            updateProjectTaskWrapper.in(PrjProjectTask::getProjectTaskSid, projectTaskSids)
                    .eq(PrjProjectTask::getRelateBusinessFormCode, ConstantsPdm.RELATE_BUSINESS_FORM_SXJG);
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
     * @param trialsaleResult 新品试销结果单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateFrmTrialsaleResultAttach(FrmTrialsaleResult trialsaleResult) {
        // 先删后加
        frmTrialsaleResultAttachMapper.delete(new QueryWrapper<FrmTrialsaleResultAttach>().lambda()
                .eq(FrmTrialsaleResultAttach::getTrialsaleResultSid, trialsaleResult.getTrialsaleResultSid()));
        if (CollectionUtil.isNotEmpty(trialsaleResult.getAttachmentList())) {
            trialsaleResult.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getTrialsaleResultAttachSid() == null) {
                    att.setTrialsaleResultSid(trialsaleResult.getTrialsaleResultSid());
                    att.setTrialsaleResultCode(trialsaleResult.getTrialsaleResultCode());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            frmTrialsaleResultAttachMapper.inserts(trialsaleResult.getAttachmentList());
        }
    }

    /**
     * 修改试销结果单
     *
     * @param frmTrialsaleResult 试销结果单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmTrialsaleResult(FrmTrialsaleResult frmTrialsaleResult) {
        String handleStatus = "";
        handleStatus = frmTrialsaleResult.getHandleStatus();
        // 判断是否配置需要审批
        boolean flag = true;
        String remark = null;
        if (ConstantsEms.SUBMIT_STATUS.equals(frmTrialsaleResult.getHandleStatus())) {
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            // 不启用审批流程
            if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowShixjgd())) {
                flag = false;
                remark = "提交并确认";
                frmTrialsaleResult.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(frmTrialsaleResult.getHandleStatus())) {
            frmTrialsaleResult.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 字段数据处理
        setData(frmTrialsaleResult);
        // 更新人更新日期
        FrmTrialsaleResult original = frmTrialsaleResultMapper.selectFrmTrialsaleResultById(frmTrialsaleResult.getTrialsaleResultSid());
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, frmTrialsaleResult);
        if (CollectionUtil.isNotEmpty(msgList)) {
            frmTrialsaleResult.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = frmTrialsaleResultMapper.updateAllById(frmTrialsaleResult);
        if (row > 0) {
            // 优化建议
            resultAdviceService.updateFrmTrialsaleResultAdviceList(frmTrialsaleResult);
            // 计划项
            resultPlanItemService.updateFrmTrialsaleResultPlanItemList(frmTrialsaleResult);
            // 定价方案
            resultPriceSchemeService.updateFrmTrialsaleResultPriceSchemeList(frmTrialsaleResult);
            // 附件
            this.updateFrmTrialsaleResultAttach(frmTrialsaleResult);
            // 不是保存状态删除待办
            Long[] sids = new Long[]{frmTrialsaleResult.getTrialsaleResultSid()};
            if (!ConstantsEms.SAVA_STATUS.equals(frmTrialsaleResult.getHandleStatus())) {
                sysTodoTaskService.deleteSysTodoTaskList(sids,
                        frmTrialsaleResult.getHandleStatus(), ConstantsTable.TABLE_FRM_TRIALSALE_RESULT);
            }
            // 审批处理逻辑
            this.approve(sids, frmTrialsaleResult.getHandleStatus());
            // 提交启动审批
            if (ConstantsEms.SUBMIT_STATUS.equals(frmTrialsaleResult.getHandleStatus())) {
                if (flag) {
                    this.submit(frmTrialsaleResult);
                }
            }
            // 插入日志
            MongodbDeal.update(frmTrialsaleResult.getTrialsaleResultSid(), original.getHandleStatus(), handleStatus, msgList, TITLE, remark);
        }
        return row;
    }

    /**
     * 变更试销结果单
     *
     * @param frmTrialsaleResult 试销结果单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFrmTrialsaleResult(FrmTrialsaleResult frmTrialsaleResult) {
        FrmTrialsaleResult response = frmTrialsaleResultMapper.selectFrmTrialsaleResultById(frmTrialsaleResult.getTrialsaleResultSid());
        // 字段数据处理
        setData(frmTrialsaleResult);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, frmTrialsaleResult);
        if (CollectionUtil.isNotEmpty(msgList)) {
            frmTrialsaleResult.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = frmTrialsaleResultMapper.updateAllById(frmTrialsaleResult);
        if (row > 0) {
            // 优化建议
            resultAdviceService.updateFrmTrialsaleResultAdviceList(frmTrialsaleResult);
            // 计划项
            resultPlanItemService.updateFrmTrialsaleResultPlanItemList(frmTrialsaleResult);
            // 定价方案
            resultPriceSchemeService.updateFrmTrialsaleResultPriceSchemeList(frmTrialsaleResult);
            // 附件
            this.updateFrmTrialsaleResultAttach(frmTrialsaleResult);
            // 插入日志
            MongodbUtil.insertUserLog(frmTrialsaleResult.getTrialsaleResultSid(), BusinessType.CHANGE.getValue(), response, frmTrialsaleResult, TITLE);
        }
        return row;
    }

    /**
     * 批量删除试销结果单
     *
     * @param trialsaleResultSids 需要删除的试销结果单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmTrialsaleResultByIds(List<Long> trialsaleResultSids) {
        List<FrmTrialsaleResult> list = frmTrialsaleResultMapper.selectList(new QueryWrapper<FrmTrialsaleResult>()
                .lambda().in(FrmTrialsaleResult::getTrialsaleResultSid, trialsaleResultSids));
        int row = frmTrialsaleResultMapper.deleteBatchIds(trialsaleResultSids);
        if (row > 0) {
            // 优化建议
            resultAdviceService.deleteFrmTrialsaleResultAdviceByPlan(trialsaleResultSids);
            // 计划项
            resultPlanItemService.deleteFrmTrialsaleResultPlanItemByPlan(trialsaleResultSids);
            // 定价方案
            resultPriceSchemeService.deleteFrmTrialsaleResultPriceSchemeByPlan(trialsaleResultSids);
            // 删除附件
            frmTrialsaleResultAttachMapper.delete(new QueryWrapper<FrmTrialsaleResultAttach>().lambda()
                    .in(FrmTrialsaleResultAttach::getTrialsaleResultSid, trialsaleResultSids));
            // 删除待办
            Long[] sids = trialsaleResultSids.toArray(new Long[trialsaleResultSids.size()]);
            sysTodoTaskService.deleteSysTodoTaskList(sids, null,
                    ConstantsTable.TABLE_FRM_TRIALSALE_RESULT);
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FrmTrialsaleResult());
                MongodbUtil.insertUserLog(o.getTrialsaleResultSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 提交
     *
     * @param frmTrialsaleResult
     * @return
     */
    private void submit(FrmTrialsaleResult frmTrialsaleResult){
        if (frmTrialsaleResult.getProjectSid() != null) {
            List<PrjProject> projectList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda()
                    .eq(PrjProject::getProjectSid, frmTrialsaleResult.getProjectSid())
                    .eq(PrjProject::getHandleStatus, ConstantsEms.INVALID_STATUS));
            if (CollectionUtil.isNotEmpty(projectList)) {
                throw new BaseException("所属项目已作废，不允许提交！");
            }
        }
        // 根据配置不同组别走不同审批
        String formType = getApprovalFormType(frmTrialsaleResult, FormType.TrialsaleResult.getCode());
        Map<String, Object> variables = new HashMap<>();
        variables.put("formId", frmTrialsaleResult.getTrialsaleResultSid());
        variables.put("formCode", frmTrialsaleResult.getTrialsaleResultCode());
        variables.put("formType", formType);
        variables.put("dataObject", FormType.TrialsaleResult.getCode());
        variables.put("startUserId", ApiThreadLocalUtil.get().getSysUser().getUserId());
        variables.put("erpCode", frmTrialsaleResult.getErpMaterialSkuBarcode());
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
        LambdaUpdateWrapper<FrmTrialsaleResult> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FrmTrialsaleResult::getTrialsaleResultSid, sids);
        updateWrapper.set(FrmTrialsaleResult::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(FrmTrialsaleResult::getConfirmDate, new Date());
            updateWrapper.set(FrmTrialsaleResult::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return frmTrialsaleResultMapper.update(null, updateWrapper);
    }

    /**
     * 根据配置不同组别走不同审批 得到流程keycode
     */
    private String getApprovalFormType(FrmTrialsaleResult frmTrialsaleResult, String formType) {
        if (frmTrialsaleResult.getProjectSid() == null) {
            return formType;
        }
        PrjProject project = prjProjectMapper.selectPrjProjectById(frmTrialsaleResult.getProjectSid());
        if (project == null || (StrUtil.isBlank(project.getGroupType()) &&  StrUtil.isBlank(frmTrialsaleResult.getTrialsaleMarket()))) {
            return formType;
        }
        // 根据配置不同组别 + 试销市场走不同审批
        try {
            List<SysPdmApproveSettingConfirm> sysPdmApproveSettings = sysPdmApproveMapper.selectList(new QueryWrapper<SysPdmApproveSettingConfirm>()
                    .lambda().eq(SysPdmApproveSettingConfirm::getDataObjectCode, FormType.TrialsaleResult.getCode())
                    .eq(SysPdmApproveSettingConfirm::getStatus, ConstantsEms.ENABLE_STATUS));
            if (CollectionUtil.isNotEmpty(sysPdmApproveSettings)) {
                // 判断试销结果单的项目档案的品类规划明细是否 有 组别
                if (StrUtil.isNotBlank(project.getGroupType())) {
                    sysPdmApproveSettings = sysPdmApproveSettings.stream().filter(o->
                            Arrays.asList(o.getGroupCode().split(";")).contains(project.getGroupType())).collect(Collectors.toList());
                }
                else {
                    sysPdmApproveSettings = sysPdmApproveSettings.stream().filter(o->StrUtil.isBlank(o.getGroupCode())).collect(Collectors.toList());
                }
                // 组别一样 就 考虑 配置的试销市场是否包含 试销结果单的 试销市场
                if (CollectionUtil.isNotEmpty(sysPdmApproveSettings)) {
                    // 如果试销市场设置了星号，则直接走这个
                    List<SysPdmApproveSettingConfirm> allMatch = sysPdmApproveSettings.stream().filter(o->"*".equals(o.getTrialsaleMarket())).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(allMatch)) {
                        formType = allMatch.get(0).getApproveKey();
                        return formType;
                    }
                    if (StrUtil.isBlank(frmTrialsaleResult.getTrialsaleMarket())) {
                        for (int i = 0; i < sysPdmApproveSettings.size(); i++) {
                            if (StrUtil.isBlank(sysPdmApproveSettings.get(i).getTrialsaleMarket())) {
                                formType = sysPdmApproveSettings.get(i).getApproveKey();
                                break;
                            }
                        }
                    }
                    else {
                        for (int i = 0; i < sysPdmApproveSettings.size(); i++) {
                            if (StrUtil.isNotBlank(sysPdmApproveSettings.get(i).getTrialsaleMarket())) {
                                String[] trialsaleMarket = sysPdmApproveSettings.get(i).getTrialsaleMarket().split(";");
                                List<String> trialsaleMarketList = Arrays.asList(trialsaleMarket);
                                if (trialsaleMarketList.contains(frmTrialsaleResult.getTrialsaleMarket())) {
                                    formType = sysPdmApproveSettings.get(i).getApproveKey();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("根据配置不同组别走不同审批流程");
        }
        return formType;
    }

    /**
     * 更改确认状态
     *
     * @param frmTrialsaleResult
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FrmTrialsaleResult frmTrialsaleResult) {
        int row = 0;
        Long[] sids = frmTrialsaleResult.getTrialsaleResultSidList();
        // 处理状态
        String handleStatus = "";
        handleStatus = frmTrialsaleResult.getHandleStatus();
        if (StrUtil.isNotBlank(frmTrialsaleResult.getBusinessType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(frmTrialsaleResult.getBusinessType());
        }
        if (sids != null && sids.length > 0) {
            row = sids.length;
            // 获取数据
            List<FrmTrialsaleResult> resultList = frmTrialsaleResultMapper.selectFrmTrialsaleResultList(new FrmTrialsaleResult().setTrialsaleResultSidList(sids));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus,
                        ConstantsTable.TABLE_FRM_TRIALSALE_RESULT);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(frmTrialsaleResult.getBusinessType())) {
                // 判断是否配置需要审批
                SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                        .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
                // 不启用审批流程
                if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowShixjgd())) {
                    // 审批处理逻辑
                    this.approve(sids, ConstantsEms.CHECK_STATUS);
                    // 修改处理状态
                    this.updateHandle(sids, ConstantsEms.CHECK_STATUS);
                    //插入日志
                    for (int i = 0; i < resultList.size(); i++) {
                        MongodbUtil.insertUserLog(resultList.get(i).getTrialsaleResultSid(),
                                BusinessType.SUBMIT.getValue(), null, TITLE, "提交并确认");
                    }
                    return row;
                }
                // 审批处理逻辑
                this.approve(sids, ConstantsEms.SUBMIT_STATUS);
                // 修改处理状态
                this.updateHandle(sids, ConstantsEms.SUBMIT_STATUS);
                // 开启工作流
                for (int i = 0; i < resultList.size(); i++) {
                    this.submit(resultList.get(i));
                    //插入日志
                    MongodbUtil.insertUserLog(resultList.get(i).getTrialsaleResultSid(),
                            BusinessType.SUBMIT.getValue(), null, TITLE, frmTrialsaleResult.getComment());
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(frmTrialsaleResult.getBusinessType())) {
                if (frmTrialsaleResult.getLeaderScore() == null) {
                    throw new BaseException("主管评分不能为空！");
                }
                Map<String, List<FrmTrialsaleResult>> map = resultList.stream()
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
                for (int i = 0; i < resultList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(resultList.get(i).getTrialsaleResultSid().toString());
                    taskVo.setFormId(Long.valueOf(resultList.get(i).getTrialsaleResultSid().toString()));
                    taskVo.setFormCode(resultList.get(i).getTrialsaleResultCode().toString());
                    taskVo.setErpCode(resultList.get(i).getErpMaterialSkuBarcode());
                    // 根据配置不同组别走不同审批
                    String formType = getApprovalFormType(resultList.get(i), FormType.TrialsaleResult.getCode());
                    taskVo.setFormType(formType);
                    taskVo.setDataObject(FormType.TrialsaleResult.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(frmTrialsaleResult.getComment());
                    try {
                        SysFormProcess process = workFlowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 最后一级审批通过处理逻辑
                            this.approve(new Long[]{resultList.get(i).getTrialsaleResultSid()}, ConstantsEms.CHECK_STATUS);
                            // 修改处理状态
                            this.updateHandle(new Long[]{resultList.get(i).getTrialsaleResultSid()}, ConstantsEms.CHECK_STATUS);
                        }
                        comment = process.getRemark();
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(resultList.get(i).getTrialsaleResultSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, comment);
                }
                // 写入主管评分
                LambdaUpdateWrapper<FrmTrialsaleResult> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.in(FrmTrialsaleResult::getTrialsaleResultSid, sids);
                updateWrapper.set(FrmTrialsaleResult::getLeaderScore, frmTrialsaleResult.getLeaderScore());
                frmTrialsaleResultMapper.update(null, updateWrapper);
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(frmTrialsaleResult.getBusinessType())) {
                Map<String, List<FrmTrialsaleResult>> map = resultList.stream()
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
                for (int i = 0; i < resultList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setTargetKey("2");
                    taskVo.setBusinessKey(resultList.get(i).getTrialsaleResultSid().toString());
                    taskVo.setFormId(Long.valueOf(resultList.get(i).getTrialsaleResultSid().toString()));
                    taskVo.setFormCode(resultList.get(i).getTrialsaleResultCode().toString());
                    taskVo.setErpCode(resultList.get(i).getErpMaterialSkuBarcode());
                    // 根据配置不同组别走不同审批
                    String formType = getApprovalFormType(resultList.get(i), FormType.TrialsaleResult.getCode());
                    taskVo.setFormType(formType);
                    taskVo.setDataObject(FormType.TrialsaleResult.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(frmTrialsaleResult.getComment());
                    try {
                        SysFormProcess process = workFlowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 审批驳回到提交人处理逻辑
                            this.approve(new Long[]{resultList.get(i).getTrialsaleResultSid()}, HandleStatus.RETURNED.getCode());
                            // 修改处理状态
                            this.updateHandle(new Long[]{resultList.get(i).getTrialsaleResultSid()}, HandleStatus.RETURNED.getCode());
                        }
                        comment = process.getRemark();
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(resultList.get(i).getTrialsaleResultSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, comment);
                }
                // 写入主管评分
                LambdaUpdateWrapper<FrmTrialsaleResult> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.in(FrmTrialsaleResult::getTrialsaleResultSid, sids);
                updateWrapper.set(FrmTrialsaleResult::getLeaderScore, frmTrialsaleResult.getLeaderScore());
                frmTrialsaleResultMapper.update(null, updateWrapper);
            }
        }
        return row;
    }

}
