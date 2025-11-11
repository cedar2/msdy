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
import com.platform.ems.mapper.FrmDocumentVisionAttachMapper;
import com.platform.ems.mapper.FrmDocumentVisionMapper;
import com.platform.ems.mapper.PrjProjectMapper;
import com.platform.ems.mapper.PrjProjectTaskMapper;
import com.platform.ems.service.IFrmDocumentVisionService;
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
 * 文案脚本单Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-13
 */
@Service
@SuppressWarnings("all")
public class FrmDocumentVisionServiceImpl extends ServiceImpl<FrmDocumentVisionMapper, FrmDocumentVision> implements IFrmDocumentVisionService {
    @Autowired
    private FrmDocumentVisionMapper frmDocumentVisionMapper;
    @Autowired
    private FrmDocumentVisionAttachMapper frmDocumentVisionAttachMapper;
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

    private static final String TITLE = "文案脚本单";

    /**
     * 查询文案脚本单
     *
     * @param documentVisionSid 文案脚本单ID
     * @return 文案脚本单
     */
    @Override
    public FrmDocumentVision selectFrmDocumentVisionById(Long documentVisionSid) {
        FrmDocumentVision frmDocumentVision = frmDocumentVisionMapper.selectFrmDocumentVisionById(documentVisionSid);
        frmDocumentVision.setAttachmentList(new ArrayList<>());
        // 附件列表
        List<FrmDocumentVisionAttach> attachList = frmDocumentVisionAttachMapper.selectFrmDocumentVisionAttachList(
                new FrmDocumentVisionAttach().setDocumentVisionSid(documentVisionSid));
        if (CollectionUtil.isNotEmpty(attachList)) {
            frmDocumentVision.setAttachmentList(attachList);
        }
        // 操作日志
        MongodbUtil.find(frmDocumentVision);
        return frmDocumentVision;
    }

    /**
     * 查询文案脚本单列表
     *
     * @param frmDocumentVision 文案脚本单
     * @return 文案脚本单
     */
    @Override
    public List<FrmDocumentVision> selectFrmDocumentVisionList(FrmDocumentVision frmDocumentVision) {
        return frmDocumentVisionMapper.selectFrmDocumentVisionList(frmDocumentVision);
    }

    /**
     * 提交前校验
     *
     * @param frmDocumentVision 文案脚本单
     * @return 结果
     */
    @Override
    public EmsResultEntity submitVerify(FrmDocumentVision frmDocumentVision) {
        if (HandleStatus.SUBMIT.getCode().equals(frmDocumentVision.getHandleStatus())
                && frmDocumentVision.getProjectTaskSid() != null && frmDocumentVision.getProjectSid() != null) {
            if (frmDocumentVision.getProjectSid() != null) {
                List<PrjProject> projectList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda()
                        .eq(PrjProject::getProjectSid, frmDocumentVision.getProjectSid())
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
            List<PrjProjectTask> taskList = prjProjectTaskService.selectPrjProjectTaskListById(frmDocumentVision.getProjectSid());
            if (CollectionUtil.isNotEmpty(taskList)) {
                // 拍照样获取单对应的项目任务明细
                PrjProjectTask task = taskList.stream().filter(o->o.getProjectTaskSid().equals(frmDocumentVision.getProjectTaskSid())).findFirst().get();
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
     * 新增文案脚本单
     * 需要注意编码重复校验
     *
     * @param frmDocumentVision 文案脚本单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmDocumentVision(FrmDocumentVision frmDocumentVision) {
        String handleStatus = "";
        handleStatus = frmDocumentVision.getHandleStatus();
        // 判断是否配置需要审批
        boolean flag = true;
        String remark = null;
        if (ConstantsEms.SUBMIT_STATUS.equals(frmDocumentVision.getHandleStatus())) {
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowWenapsd())) {
                flag = false;
                remark = "提交并确认";
                frmDocumentVision.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(frmDocumentVision.getHandleStatus())) {
            frmDocumentVision.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 不存 样品 和 开发计划
        frmDocumentVision.setDevelopPlanSid(null).setDevelopPlanCode(null)
                .setSampleSid(null).setSampleCode(null);
        int row = frmDocumentVisionMapper.insert(frmDocumentVision);
        if (row > 0) {
            // 获取编码等数据
            FrmDocumentVision vision = frmDocumentVisionMapper.selectFrmDocumentVisionById(frmDocumentVision.getDocumentVisionSid());
            frmDocumentVision.setDocumentVisionCode(vision.getDocumentVisionCode());
            // 写入附件
            if (CollectionUtil.isNotEmpty(frmDocumentVision.getAttachmentList())) {
                frmDocumentVision.getAttachmentList().forEach(item->{
                    item.setDocumentVisionSid(frmDocumentVision.getDocumentVisionSid());
                    item.setDocumentVisionCode(frmDocumentVision.getDocumentVisionCode());
                });
                frmDocumentVisionAttachMapper.inserts(frmDocumentVision.getAttachmentList());
            }
            // 删除项目档案任务明细的待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_PROJECT_TASK)
                    .eq(SysTodoTask::getDocumentSid, frmDocumentVision.getProjectSid())
                    .eq(SysTodoTask::getDocumentItemSid, frmDocumentVision.getProjectTaskSid())
                    .likeLeft(SysTodoTask::getTitle, "还未开始，请及时跟进！"));
            // 待办
            if (ConstantsEms.SAVA_STATUS.equals(frmDocumentVision.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_FRM_DOCUMENT_VISION)
                        .setDocumentSid(frmDocumentVision.getDocumentVisionSid());
                String erpCode = vision.getErpMaterialSkuBarcode() == null ? "" : vision.getErpMaterialSkuBarcode();
                sysTodoTask.setTitle(erpCode + "文案脚本单" + frmDocumentVision.getDocumentVisionCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(frmDocumentVision.getDocumentVisionCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                // 获取菜单id
                SysMenu menu = new SysMenu();
                menu.setMenuName(ConstantsWorkbench.TODO_FRM_DOC_VIS_MENU_NAME);
                menu = remoteMenuService.getInfoByName(menu).getData();
                if (menu != null && menu.getMenuId() != null) {
                    sysTodoTask.setMenuId(menu.getMenuId());
                }
                sysTodoTaskService.insertSysTodoTask(sysTodoTask);
            }
            // 提交启动审批
            else if (ConstantsEms.SUBMIT_STATUS.equals(frmDocumentVision.getHandleStatus())) {
                // 启用审批流程
                if (flag) {
                    frmDocumentVision.setErpMaterialSkuBarcode(vision.getErpMaterialSkuBarcode());
                    this.submit(frmDocumentVision);
                }
            }
            // 提交或者确认处理逻辑
            Long[] sids = new Long[]{frmDocumentVision.getDocumentVisionSid()};
            this.approve(sids, frmDocumentVision.getHandleStatus());
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FrmDocumentVision(), frmDocumentVision);
            MongodbDeal.insert(frmDocumentVision.getDocumentVisionSid(), handleStatus, msgList, TITLE, remark);
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
            List<FrmDocumentVision> designList = frmDocumentVisionMapper.selectList(new QueryWrapper<FrmDocumentVision>()
                    .lambda().in(FrmDocumentVision::getDocumentVisionSid, sids));
            Long[] projectTaskSids = designList.stream().map(FrmDocumentVision::getProjectTaskSid).toArray(Long[]::new);
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
     * @param frmDocumentVision 文案脚本单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateFrmDocumentVisionAttach(FrmDocumentVision frmDocumentVision) {
        // 先删后加
        frmDocumentVisionAttachMapper.delete(new QueryWrapper<FrmDocumentVisionAttach>().lambda()
                .eq(FrmDocumentVisionAttach::getDocumentVisionSid, frmDocumentVision.getDocumentVisionSid()));
        if (CollectionUtil.isNotEmpty(frmDocumentVision.getAttachmentList())) {
            frmDocumentVision.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getDocumentVisionAttachSid() == null) {
                    att.setDocumentVisionSid(frmDocumentVision.getDocumentVisionSid());
                    att.setDocumentVisionCode(frmDocumentVision.getDocumentVisionCode());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            frmDocumentVisionAttachMapper.inserts(frmDocumentVision.getAttachmentList());
        }
    }

    /**
     * 修改文案脚本单
     *
     * @param frmDocumentVision 文案脚本单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmDocumentVision(FrmDocumentVision frmDocumentVision) {
        FrmDocumentVision original = frmDocumentVisionMapper.selectFrmDocumentVisionById(frmDocumentVision.getDocumentVisionSid());
        // 不存 样品 和 开发计划
        frmDocumentVision.setDevelopPlanSid(null).setDevelopPlanCode(null)
                .setSampleSid(null).setSampleCode(null);
        // 判断是否配置需要审批
        boolean flag = true;
        String remark = null;
        String handleStatus = "";
        handleStatus = frmDocumentVision.getHandleStatus();
        if (ConstantsEms.SUBMIT_STATUS.equals(frmDocumentVision.getHandleStatus())) {
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowWenapsd())) {
                flag = false;
                remark = "提交并确认";
                frmDocumentVision.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(frmDocumentVision.getHandleStatus())) {
            frmDocumentVision.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, frmDocumentVision);
        if (CollectionUtil.isNotEmpty(msgList)) {
            frmDocumentVision.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = frmDocumentVisionMapper.updateById(frmDocumentVision);
        if (row > 0) {

            // 修改附件
            this.updateFrmDocumentVisionAttach(frmDocumentVision);
            // 不是保存状态删除待办
            Long[] sids = new Long[]{frmDocumentVision.getDocumentVisionSid()};
            if (!ConstantsEms.SAVA_STATUS.equals(frmDocumentVision.getHandleStatus())) {
                sysTodoTaskService.deleteSysTodoTaskList(sids,
                        frmDocumentVision.getHandleStatus(), ConstantsTable.TABLE_FRM_DOCUMENT_VISION);
            }
            // 提交启动审批
            if (ConstantsEms.SUBMIT_STATUS.equals(frmDocumentVision.getHandleStatus())) {
                // 启用审批流程
                if (flag) {
                    this.submit(frmDocumentVision);
                }
            }
            // 审批处理逻辑
            this.approve(sids, frmDocumentVision.getHandleStatus());
            //插入日志
            MongodbDeal.update(frmDocumentVision.getDocumentVisionSid(), original.getHandleStatus(), handleStatus, msgList, TITLE, remark);
        }
        return row;
    }

    /**
     * 变更文案脚本单
     *
     * @param frmDocumentVision 文案脚本单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFrmDocumentVision(FrmDocumentVision frmDocumentVision) {
        FrmDocumentVision response = frmDocumentVisionMapper.selectFrmDocumentVisionById(frmDocumentVision.getDocumentVisionSid());
        // 不存 样品 和 开发计划
        frmDocumentVision.setDevelopPlanSid(null).setDevelopPlanCode(null)
                .setSampleSid(null).setSampleCode(null);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, frmDocumentVision);
        if (CollectionUtil.isNotEmpty(msgList)) {
            frmDocumentVision.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = frmDocumentVisionMapper.updateAllById(frmDocumentVision);
        // 附件信息
        this.updateFrmDocumentVisionAttach(frmDocumentVision);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(frmDocumentVision.getDocumentVisionSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除文案脚本单
     *
     * @param documentVisionSids 需要删除的文案脚本单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmDocumentVisionByIds(List<Long> documentVisionSids) {
        List<FrmDocumentVision> list = frmDocumentVisionMapper.selectList(new QueryWrapper<FrmDocumentVision>()
                .lambda().in(FrmDocumentVision::getDocumentVisionSid, documentVisionSids));
        int row = frmDocumentVisionMapper.deleteBatchIds(documentVisionSids);
        if (row > 0) {
            // 删除附件
            frmDocumentVisionAttachMapper.delete(new QueryWrapper<FrmDocumentVisionAttach>().lambda()
                    .in(FrmDocumentVisionAttach::getDocumentVisionSid, documentVisionSids));
            // 删除待办
            Long[] sids = documentVisionSids.toArray(new Long[documentVisionSids.size()]);
            sysTodoTaskService.deleteSysTodoTaskList(sids, null,
                    ConstantsTable.TABLE_FRM_DOCUMENT_VISION);
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FrmDocumentVision());
                MongodbUtil.insertUserLog(o.getDocumentVisionSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 提交
     *
     * @param frmDocumentVision
     * @return
     */
    private void submit(FrmDocumentVision frmDocumentVision) {
        if (frmDocumentVision.getProjectSid() != null) {
            List<PrjProject> projectList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda()
                    .eq(PrjProject::getProjectSid, frmDocumentVision.getProjectSid())
                    .eq(PrjProject::getHandleStatus, ConstantsEms.INVALID_STATUS));
            if (CollectionUtil.isNotEmpty(projectList)) {
                throw new BaseException("所属项目已作废，不允许提交！");
            }
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("formId", frmDocumentVision.getDocumentVisionSid());
        variables.put("formCode", frmDocumentVision.getDocumentVisionCode());
        variables.put("formType", FormType.DocumentVision.getCode());
        variables.put("startUserId", ApiThreadLocalUtil.get().getSysUser().getUserId());
        variables.put("erpCode", frmDocumentVision.getErpMaterialSkuBarcode());
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
        LambdaUpdateWrapper<FrmDocumentVision> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FrmDocumentVision::getDocumentVisionSid, sids);
        updateWrapper.set(FrmDocumentVision::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(FrmDocumentVision::getConfirmDate, new Date());
            updateWrapper.set(FrmDocumentVision::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return frmDocumentVisionMapper.update(null, updateWrapper);
    }

    /**
     * 更改确认状态
     *
     * @param frmDocumentVision
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FrmDocumentVision frmDocumentVision) {
        int row = 0;
        Long[] sids = frmDocumentVision.getDocumentVisionSidList();
        // 处理状态
        String handleStatus = "";
        handleStatus = frmDocumentVision.getHandleStatus();
        if (StrUtil.isNotBlank(frmDocumentVision.getBusinessType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(frmDocumentVision.getBusinessType());
        }
        if (sids != null && sids.length > 0) {
            row = sids.length;
            // 获取数据
            List<FrmDocumentVision> visionList = frmDocumentVisionMapper.selectFrmDocumentVisionList(new FrmDocumentVision().setDocumentVisionSidList(sids));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus,
                        ConstantsTable.TABLE_FRM_DOCUMENT_VISION);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(frmDocumentVision.getBusinessType())) {
                // 判断是否配置需要审批
                SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                        .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
                // 不启用审批流程
                if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowWenapsd())) {
                    // 审批处理逻辑
                    this.approve(sids, ConstantsEms.CHECK_STATUS);
                    // 修改处理状态
                    this.updateHandle(sids, ConstantsEms.CHECK_STATUS);
                    //插入日志
                    for (int i = 0; i < visionList.size(); i++) {
                        MongodbUtil.insertUserLog(visionList.get(i).getDocumentVisionSid(),
                                BusinessType.SUBMIT.getValue(), null, TITLE, "提交并确认");
                    }
                    return row;
                }
                // 审批处理逻辑
                this.approve(sids, ConstantsEms.SUBMIT_STATUS);
                // 修改处理状态
                this.updateHandle(sids, ConstantsEms.SUBMIT_STATUS);
                // 开启工作流
                for (int i = 0; i < visionList.size(); i++) {
                    this.submit(visionList.get(i));
                    //插入日志
                    MongodbUtil.insertUserLog(visionList.get(i).getDocumentVisionSid(),
                            BusinessType.SUBMIT.getValue(), null, TITLE, frmDocumentVision.getComment());
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(frmDocumentVision.getBusinessType())) {
                Map<String, List<FrmDocumentVision>> map = visionList.stream()
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
                for (int i = 0; i < visionList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(visionList.get(i).getDocumentVisionSid().toString());
                    taskVo.setFormId(Long.valueOf(visionList.get(i).getDocumentVisionSid().toString()));
                    taskVo.setFormCode(visionList.get(i).getDocumentVisionCode().toString());
                    taskVo.setErpCode(visionList.get(i).getErpMaterialSkuBarcode());
                    taskVo.setFormType(FormType.DocumentVision.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(frmDocumentVision.getComment());
                    try {
                        SysFormProcess process = workFlowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 审批处理逻辑
                            this.approve(new Long[]{visionList.get(i).getDocumentVisionSid()}, ConstantsEms.CHECK_STATUS);
                            // 修改处理状态
                            this.updateHandle(new Long[]{visionList.get(i).getDocumentVisionSid()}, ConstantsEms.CHECK_STATUS);
                        }
                        comment = process.getRemark();
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(visionList.get(i).getDocumentVisionSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, comment);
                }
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(frmDocumentVision.getBusinessType())) {
                Map<String, List<FrmDocumentVision>> map = visionList.stream()
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
                for (int i = 0; i < visionList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setTargetKey("2");
                    taskVo.setBusinessKey(visionList.get(i).getDocumentVisionSid().toString());
                    taskVo.setFormId(Long.valueOf(visionList.get(i).getDocumentVisionSid().toString()));
                    taskVo.setFormCode(visionList.get(i).getDocumentVisionCode().toString());
                    taskVo.setErpCode(visionList.get(i).getErpMaterialSkuBarcode());
                    taskVo.setFormType(FormType.DocumentVision.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(frmDocumentVision.getComment());
                    try {
                        SysFormProcess process = workFlowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 审批驳回到提交人处理逻辑
                            this.approve(new Long[]{visionList.get(i).getDocumentVisionSid()}, HandleStatus.RETURNED.getCode());
                            // 修改处理状态
                            this.updateHandle(new Long[]{visionList.get(i).getDocumentVisionSid()}, HandleStatus.RETURNED.getCode());
                        }
                        comment = process.getRemark();
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(visionList.get(i).getDocumentVisionSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, comment);
                }
            }
        }
        return row;
    }

}
