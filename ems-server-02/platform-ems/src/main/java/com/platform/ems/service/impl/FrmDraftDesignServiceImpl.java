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
import com.platform.ems.mapper.FrmDraftDesignAttachMapper;
import com.platform.ems.mapper.FrmDraftDesignMapper;
import com.platform.ems.mapper.PrjProjectMapper;
import com.platform.ems.mapper.PrjProjectTaskMapper;
import com.platform.ems.service.IFrmDraftDesignService;
import com.platform.ems.service.IPrjProjectTaskService;
import com.platform.ems.service.ISysTodoTaskService;
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
 * 图稿绘制单Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-12
 */
@Service
@SuppressWarnings("all")
public class FrmDraftDesignServiceImpl extends ServiceImpl<FrmDraftDesignMapper, FrmDraftDesign> implements IFrmDraftDesignService {
    @Autowired
    private FrmDraftDesignMapper frmDraftDesignMapper;
    @Autowired
    private FrmDraftDesignAttachMapper frmDraftDesignAttachMapper;
    @Autowired
    private PrjProjectMapper prjProjectMapper;
    @Autowired
    private PrjProjectTaskMapper prjProjectTaskMapper;
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
    private RemoteMenuService remoteMenuService;

    private static final String TITLE = "图稿绘制单";

    /**
     * 查询图稿绘制单
     *
     * @param draftDesignSid 图稿绘制单ID
     * @return 图稿绘制单
     */
    @Override
    public FrmDraftDesign selectFrmDraftDesignById(Long draftDesignSid) {
        FrmDraftDesign frmDraftDesign = frmDraftDesignMapper.selectFrmDraftDesignById(draftDesignSid);
        frmDraftDesign.setAttachmentList(new ArrayList<>());
        // 附件列表
        List<FrmDraftDesignAttach> attachList = frmDraftDesignAttachMapper.selectFrmDraftDesignAttachList(
                new FrmDraftDesignAttach().setDraftDesignSid(draftDesignSid));
        if (CollectionUtil.isNotEmpty(attachList)) {
            frmDraftDesign.setAttachmentList(attachList);
        }
        // 操作日志
        MongodbUtil.find(frmDraftDesign);
        return frmDraftDesign;
    }

    /**
     * 查询图稿绘制单列表
     *
     * @param frmDraftDesign 图稿绘制单
     * @return 图稿绘制单
     */
    @Override
    public List<FrmDraftDesign> selectFrmDraftDesignListOrderByDesc(FrmDraftDesign frmDraftDesign) {
        return frmDraftDesignMapper.selectFrmDraftDesignListOrderByDesc(frmDraftDesign);
    }

    /**
     * 新增图稿绘制单
     * 需要注意编码重复校验
     *
     * @param frmDraftDesign 图稿绘制单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmDraftDesign(FrmDraftDesign frmDraftDesign) {
        String handleStatus = "";
        handleStatus = frmDraftDesign.getHandleStatus();
        // 判断是否配置需要审批
        boolean flag = true;
        String remark = null;
        if (ConstantsEms.SUBMIT_STATUS.equals(frmDraftDesign.getHandleStatus())) {
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowTughzd())) {
                flag = false;
                remark = "提交并确认";
                frmDraftDesign.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(frmDraftDesign.getHandleStatus())) {
            frmDraftDesign.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        frmDraftDesign.setDevelopPlanSid(null).setDevelopPlanCode(null)
                .setSampleSid(null).setSampleCode(null);
        int row = frmDraftDesignMapper.insert(frmDraftDesign);
        if (row > 0) {
            FrmDraftDesign design = frmDraftDesignMapper.selectFrmDraftDesignById(frmDraftDesign.getDraftDesignSid());
            frmDraftDesign.setDraftDesignCode(design.getDraftDesignCode());
            // 写入附件
            if (CollectionUtil.isNotEmpty(frmDraftDesign.getAttachmentList())) {
                frmDraftDesign.getAttachmentList().forEach(item->{
                    item.setDraftDesignSid(frmDraftDesign.getDraftDesignSid());
                    item.setDraftDesignCode(design.getDraftDesignCode());
                });
                frmDraftDesignAttachMapper.inserts(frmDraftDesign.getAttachmentList());
            }
            // 删除项目档案任务明细的待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_PROJECT_TASK)
                    .eq(SysTodoTask::getDocumentSid, frmDraftDesign.getProjectSid())
                    .eq(SysTodoTask::getDocumentItemSid, frmDraftDesign.getProjectTaskSid())
                    .likeLeft(SysTodoTask::getTitle, "还未开始，请及时跟进！"));
            // 待办
            if (ConstantsEms.SAVA_STATUS.equals(frmDraftDesign.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_FRM_DRAFT_DESIGN)
                        .setDocumentSid(frmDraftDesign.getDraftDesignSid());
                String erpCode = design.getErpMaterialSkuBarcode() == null ? "" : design.getErpMaterialSkuBarcode();
                sysTodoTask.setTitle(erpCode + "图稿绘制单" + design.getDraftDesignCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(design.getDraftDesignCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                // 获取菜单id
                SysMenu menu = new SysMenu();
                menu.setMenuName(ConstantsWorkbench.TODO_FRM_DRAFT_DESIGN_MENU_NAME);
                menu = remoteMenuService.getInfoByName(menu).getData();
                if (menu != null && menu.getMenuId() != null) {
                    sysTodoTask.setMenuId(menu.getMenuId());
                }
                sysTodoTaskService.insertSysTodoTask(sysTodoTask);
            }
            // 提交启动审批
            else if (ConstantsEms.SUBMIT_STATUS.equals(frmDraftDesign.getHandleStatus())) {
                if (frmDraftDesign.getProjectSid() != null) {
                    List<PrjProject> projectList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda()
                            .eq(PrjProject::getProjectSid, frmDraftDesign.getProjectSid())
                            .eq(PrjProject::getHandleStatus, ConstantsEms.INVALID_STATUS));
                    if (CollectionUtil.isNotEmpty(projectList)) {
                        throw new BaseException("所属项目已作废，不允许提交！");
                    }
                }
                // 启用审批流程
                if (flag) {
                    this.submit(frmDraftDesign);
                }
            }
            // 提交或者确认处理逻辑
            Long[] sids = new Long[]{frmDraftDesign.getDraftDesignSid()};
            this.approve(sids, frmDraftDesign.getHandleStatus());
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FrmDraftDesign(), frmDraftDesign);
            MongodbDeal.insert(frmDraftDesign.getDraftDesignSid(), handleStatus, msgList, TITLE, remark);
        }
        return row;
    }

    /**
     * 提交前校验
     *
     * @param frmDraftDesign 图稿绘制
     * @return 结果
     */
    @Override
    public EmsResultEntity submitVerify(FrmDraftDesign frmDraftDesign) {
        if (HandleStatus.SUBMIT.getCode().equals(frmDraftDesign.getHandleStatus())
            && frmDraftDesign.getProjectTaskSid() != null && frmDraftDesign.getProjectSid() != null) {
            if (frmDraftDesign.getProjectSid() != null) {
                List<PrjProject> projectList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda()
                        .eq(PrjProject::getProjectSid, frmDraftDesign.getProjectSid())
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
            List<PrjProjectTask> taskList = prjProjectTaskService.selectPrjProjectTaskListById(frmDraftDesign.getProjectSid());
            if (CollectionUtil.isNotEmpty(taskList)) {
                // 图稿绘制对应的项目任务明细
                PrjProjectTask task = taskList.stream().filter(o->o.getProjectTaskSid().equals(frmDraftDesign.getProjectTaskSid())).findFirst().get();
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
     * 批量修改附件信息
     *
     * @param frmDraftDesign 图稿绘制
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateFrmDraftDesignAttach(FrmDraftDesign frmDraftDesign) {
        // 先删后加
        frmDraftDesignAttachMapper.delete(new QueryWrapper<FrmDraftDesignAttach>().lambda()
                .eq(FrmDraftDesignAttach::getDraftDesignSid, frmDraftDesign.getDraftDesignSid()));
        if (CollectionUtil.isNotEmpty(frmDraftDesign.getAttachmentList())) {
            frmDraftDesign.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getDraftDesignAttachSid() == null) {
                    att.setDraftDesignSid(frmDraftDesign.getDraftDesignSid());
                    att.setDraftDesignCode(frmDraftDesign.getDraftDesignCode());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            frmDraftDesignAttachMapper.inserts(frmDraftDesign.getAttachmentList());
        }
    }

    /**
     * 修改图稿绘制单
     *
     * @param frmDraftDesign 图稿绘制单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmDraftDesign(FrmDraftDesign frmDraftDesign) {
        FrmDraftDesign original = frmDraftDesignMapper.selectFrmDraftDesignById(frmDraftDesign.getDraftDesignSid());
        // 判断是否配置需要审批
        boolean flag = true;
        String remark = null;
        String handleStatus = "";
        handleStatus = frmDraftDesign.getHandleStatus();
        if (ConstantsEms.SUBMIT_STATUS.equals(frmDraftDesign.getHandleStatus())) {
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowTughzd())) {
                flag = false;
                remark = "提交并确认";
                frmDraftDesign.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(frmDraftDesign.getHandleStatus())) {
            frmDraftDesign.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        frmDraftDesign.setDevelopPlanSid(null).setDevelopPlanCode(null)
                .setSampleSid(null).setSampleCode(null);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, frmDraftDesign);
        if (CollectionUtil.isNotEmpty(msgList)) {
            frmDraftDesign.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = frmDraftDesignMapper.updateAllById(frmDraftDesign);
        if (row > 0) {
            // 修改附件
            this.updateFrmDraftDesignAttach(frmDraftDesign);
            // 删除待办
            Long[] sids = new Long[]{frmDraftDesign.getDraftDesignSid()};
            if (!ConstantsEms.SAVA_STATUS.equals(frmDraftDesign.getHandleStatus())) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, frmDraftDesign.getHandleStatus(), null);
            }
            // 提交启动审批
            if (ConstantsEms.SUBMIT_STATUS.equals(frmDraftDesign.getHandleStatus())) {
                if (frmDraftDesign.getProjectSid() != null) {
                    List<PrjProject> projectList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda()
                            .eq(PrjProject::getProjectSid, frmDraftDesign.getProjectSid())
                            .eq(PrjProject::getHandleStatus, ConstantsEms.INVALID_STATUS));
                    if (CollectionUtil.isNotEmpty(projectList)) {
                        throw new BaseException("所属项目已作废，不允许提交！");
                    }
                }
                // 启用审批流程
                if (flag) {
                    this.submit(frmDraftDesign);
                }
            }
            // 审批处理逻辑
            this.approve(sids, frmDraftDesign.getHandleStatus());
            //插入日志
            MongodbDeal.update(frmDraftDesign.getDraftDesignSid(), original.getHandleStatus(), handleStatus, msgList, TITLE, remark);
        }
        return row;
    }

    /**
     * 变更图稿绘制单
     *
     * @param frmDraftDesign 图稿绘制单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFrmDraftDesign(FrmDraftDesign frmDraftDesign) {
        FrmDraftDesign response = frmDraftDesignMapper.selectFrmDraftDesignById(frmDraftDesign.getDraftDesignSid());
        frmDraftDesign.setDevelopPlanSid(null).setDevelopPlanCode(null)
                .setSampleSid(null).setSampleCode(null);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, frmDraftDesign);
        if (CollectionUtil.isNotEmpty(msgList)) {
            frmDraftDesign.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = frmDraftDesignMapper.updateAllById(frmDraftDesign);
        if (row > 0) {
            // 修改附件
            this.updateFrmDraftDesignAttach(frmDraftDesign);
            //插入日志
            MongodbUtil.insertUserLog(frmDraftDesign.getDraftDesignSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除图稿绘制单
     *
     * @param draftDesignSids 需要删除的图稿绘制单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmDraftDesignByIds(List<Long> draftDesignSids) {
        List<FrmDraftDesign> list = frmDraftDesignMapper.selectList(new QueryWrapper<FrmDraftDesign>()
                .lambda().in(FrmDraftDesign::getDraftDesignSid, draftDesignSids));
        int row = frmDraftDesignMapper.deleteBatchIds(draftDesignSids);
        if (row > 0) {
            // 删除附件
            frmDraftDesignAttachMapper.delete(new QueryWrapper<FrmDraftDesignAttach>().lambda()
                    .in(FrmDraftDesignAttach::getDraftDesignSid, draftDesignSids));
            // 删除待办
            Long[] sids = draftDesignSids.toArray(new Long[draftDesignSids.size()]);
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTableName, FormType.DraftDesign.getInfo()).in(SysTodoTask::getDocumentSid, sids));
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_FRM_DRAFT_DESIGN).in(SysTodoTask::getDocumentSid, sids));
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FrmDraftDesign());
                MongodbUtil.insertUserLog(o.getDraftDesignSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
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
            List<FrmDraftDesign> designList = frmDraftDesignMapper.selectList(new QueryWrapper<FrmDraftDesign>()
                    .lambda().in(FrmDraftDesign::getDraftDesignSid, sids));
            Long[] projectTaskSids = designList.stream().map(FrmDraftDesign::getProjectTaskSid).toArray(Long[]::new);
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
     * 更改确认状态
     *
     * @param frmDraftDesign
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FrmDraftDesign frmDraftDesign) {
        int row = 0;
        Long[] sids = frmDraftDesign.getDraftDesignSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<FrmDraftDesign> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(FrmDraftDesign::getDraftDesignSid, sids);
            updateWrapper.set(FrmDraftDesign::getHandleStatus, frmDraftDesign.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(frmDraftDesign.getHandleStatus())) {
                updateWrapper.set(FrmDraftDesign::getConfirmDate, new Date());
                updateWrapper.set(FrmDraftDesign::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = frmDraftDesignMapper.update(null, updateWrapper);
            if (row > 0) {
                // 删除待办
                if (ConstantsEms.CHECK_STATUS.equals(frmDraftDesign.getHandleStatus())
                        || HandleStatus.RETURNED.getCode().equals(frmDraftDesign.getHandleStatus())
                        || ConstantsEms.SUBMIT_STATUS.equals(frmDraftDesign.getHandleStatus())) {
                    sysTodoTaskService.deleteSysTodoTaskList(sids, frmDraftDesign.getHandleStatus(), null);
                }
                // 审批处理逻辑
                this.approve(sids, frmDraftDesign.getHandleStatus());
                for (Long id : sids) {
                    //插入日志
                    if (HandleStatus.RETURNED.getCode().equals(frmDraftDesign.getHandleStatus())) {
                        MongodbUtil.insertUserLog(id, BusinessType.APPROVAL.getValue(), null, TITLE, frmDraftDesign.getComment());
                    } else if (HandleStatus.CONFIRMED.getCode().equals(frmDraftDesign.getHandleStatus())) {
                        MongodbUtil.insertUserLog(id, BusinessType.APPROVAL.getValue(), null, TITLE, frmDraftDesign.getComment());
                    } else {
                        MongodbDeal.check(id, frmDraftDesign.getHandleStatus(), null, TITLE, null);
                    }
                }
            }
        }
        return row;
    }

    /**
     * 更改处理状态
     *
     * @param sids, handleStatus
     * @return
     */
    private int updateHandle(Long[] sids, String handleStatus) {
        LambdaUpdateWrapper<FrmDraftDesign> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FrmDraftDesign::getDraftDesignSid, sids);
        updateWrapper.set(FrmDraftDesign::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(FrmDraftDesign::getConfirmDate, new Date());
            updateWrapper.set(FrmDraftDesign::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return frmDraftDesignMapper.update(null, updateWrapper);
    }

    /**
     * 提交
     *
     * @param frmSampleReview
     * @return
     */
    private void submit(FrmDraftDesign frmDraftDesign) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("formId", frmDraftDesign.getDraftDesignSid());
        variables.put("formCode", frmDraftDesign.getDraftDesignCode());
        variables.put("erpCode", frmDraftDesign.getErpMaterialSkuBarcode());
        variables.put("formType", FormType.DraftDesign.getCode());
        variables.put("startUserId", ApiThreadLocalUtil.get().getSysUser().getUserId());
        try {
            AjaxResult result = workFlowService.submitOnly(variables);
        } catch (BaseException e) {
            throw e;
        }
    }

    /**
     * 更改确认状态
     *
     * @param frmDraftDesign
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approval(FrmDraftDesign frmDraftDesign) {
        int row = 0;
        Long[] sids = frmDraftDesign.getDraftDesignSidList();
        // 处理状态
        String handleStatus = "";
        handleStatus = frmDraftDesign.getHandleStatus();
        if (StrUtil.isNotBlank(frmDraftDesign.getBusinessType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(frmDraftDesign.getBusinessType());
        }
        if (sids != null && sids.length > 0) {
            row = sids.length;
            // 获取数据
            List<FrmDraftDesign> designList = frmDraftDesignMapper.selectFrmDraftDesignListOrderByDesc(new FrmDraftDesign().setDraftDesignSidList(sids));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus, null);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(frmDraftDesign.getBusinessType())) {
                // 判断是否配置需要审批
                SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                        .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
                // 不启用审批流程
                if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowTughzd())) {
                    // 审批处理逻辑
                    this.approve(sids, ConstantsEms.CHECK_STATUS);
                    // 修改处理状态
                    this.updateHandle(sids, ConstantsEms.CHECK_STATUS);
                    //插入日志
                    for (int i = 0; i < designList.size(); i++) {
                        MongodbUtil.insertUserLog(designList.get(i).getDraftDesignSid(),
                                BusinessType.SUBMIT.getValue(), null, TITLE, "提交并确认");
                    }
                    return row;
                }
                // 审批处理逻辑
                this.approve(sids, ConstantsEms.SUBMIT_STATUS);
                // 修改处理状态
                this.updateHandle(sids, ConstantsEms.SUBMIT_STATUS);
                // 开启工作流
                for (int i = 0; i < designList.size(); i++) {
                    // 提交
                    this.submit(designList.get(i));
                    //插入日志
                    MongodbUtil.insertUserLog(designList.get(i).getDraftDesignSid(),
                            BusinessType.SUBMIT.getValue(), null, TITLE, frmDraftDesign.getComment());
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(frmDraftDesign.getBusinessType())) {
                Map<String, List<FrmDraftDesign>> map = designList.stream()
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
                for (int i = 0; i < designList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(designList.get(i).getDraftDesignSid().toString());
                    taskVo.setFormId(Long.valueOf(designList.get(i).getDraftDesignSid().toString()));
                    taskVo.setFormCode(designList.get(i).getDraftDesignCode().toString());
                    taskVo.setErpCode(designList.get(i).getErpMaterialSkuBarcode());
                    taskVo.setFormType(FormType.DraftDesign.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(frmDraftDesign.getComment());
                    try {
                        SysFormProcess process = workFlowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 最后一级审批通过处理逻辑
                            this.approve(new Long[]{designList.get(i).getDraftDesignSid()}, ConstantsEms.CHECK_STATUS);
                            // 修改处理状态
                            this.updateHandle(new Long[]{designList.get(i).getDraftDesignSid()}, ConstantsEms.CHECK_STATUS);
                        }
                        comment = process.getRemark();
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(designList.get(i).getDraftDesignSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, comment);
                }
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(frmDraftDesign.getBusinessType())) {
                Map<String, List<FrmDraftDesign>> map = designList.stream()
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
                for (int i = 0; i < designList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setTargetKey("2");
                    taskVo.setBusinessKey(designList.get(i).getDraftDesignSid().toString());
                    taskVo.setFormId(Long.valueOf(designList.get(i).getDraftDesignSid().toString()));
                    taskVo.setFormCode(designList.get(i).getDraftDesignCode().toString());
                    taskVo.setErpCode(designList.get(i).getErpMaterialSkuBarcode());
                    taskVo.setFormType(FormType.DraftDesign.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(frmDraftDesign.getComment());
                    try {
                        SysFormProcess process = workFlowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 审批驳回到提交人处理逻辑
                            this.approve(new Long[]{designList.get(i).getDraftDesignSid()}, HandleStatus.RETURNED.getCode());
                            // 修改处理状态
                            this.updateHandle(new Long[]{designList.get(i).getDraftDesignSid()}, HandleStatus.RETURNED.getCode());
                        }
                        comment = process.getRemark();
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(designList.get(i).getDraftDesignSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, comment);
                }
            }
        }
        return row;
    }
}
