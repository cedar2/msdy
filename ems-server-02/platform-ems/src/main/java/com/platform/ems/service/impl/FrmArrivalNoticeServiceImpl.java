package com.platform.ems.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.*;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IPrjProjectTaskService;
import com.platform.ems.service.ISysTodoTaskService;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.api.service.RemoteMenuService;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.common.core.domain.entity.SysMenu;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.IFrmArrivalNoticeService;

/**
 * 到货通知单Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-13
 */
@Service
@SuppressWarnings("all")
public class FrmArrivalNoticeServiceImpl extends ServiceImpl<FrmArrivalNoticeMapper, FrmArrivalNotice> implements IFrmArrivalNoticeService {
    @Autowired
    private FrmArrivalNoticeMapper frmArrivalNoticeMapper;
    @Autowired
    private FrmArrivalNoticeAttachMapper frmArrivalNoticeAttachMapper;
    @Autowired
    private PrjProjectMapper prjProjectMapper;
    @Autowired
    private PrjProjectTaskMapper prjProjectTaskMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private IPrjProjectTaskService prjProjectTaskService;
    @Autowired
    private ISysTodoTaskService sysTodoTaskService;
    @Autowired
    private IWorkFlowService workFlowService;
    @Autowired
    private RemoteMenuService remoteMenuService;

    private static final String TITLE = "到货通知单";

    /**
     * 查询到货通知单
     *
     * @param arrivalNoticeSid 到货通知单ID
     * @return 到货通知单
     */
    @Override
    public FrmArrivalNotice selectFrmArrivalNoticeById(Long arrivalNoticeSid) {
        FrmArrivalNotice frmArrivalNotice = frmArrivalNoticeMapper.selectFrmArrivalNoticeById(arrivalNoticeSid);
        frmArrivalNotice.setAttachmentList(new ArrayList<>());
        // 附件列表
        List<FrmArrivalNoticeAttach> attachList = frmArrivalNoticeAttachMapper.selectFrmArrivalNoticeAttachList(
                new FrmArrivalNoticeAttach().setArrivalNoticeSid(arrivalNoticeSid));
        if (CollectionUtil.isNotEmpty(attachList)) {
            frmArrivalNotice.setAttachmentList(attachList);
        }
        // 操作日志
        MongodbUtil.find(frmArrivalNotice);
        return frmArrivalNotice;
    }

    /**
     * 查询到货通知单列表
     *
     * @param frmArrivalNotice 到货通知单
     * @return 到货通知单
     */
    @Override
    public List<FrmArrivalNotice> selectFrmArrivalNoticeList(FrmArrivalNotice frmArrivalNotice) {
        return frmArrivalNoticeMapper.selectFrmArrivalNoticeList(frmArrivalNotice);
    }

    /**
     * 提交前校验
     *
     * @param frmArrivalNotice 到货通知
     * @return 结果
     */
    @Override
    public EmsResultEntity submitVerify(FrmArrivalNotice frmArrivalNotice) {
        if (HandleStatus.SUBMIT.getCode().equals(frmArrivalNotice.getHandleStatus())
                && frmArrivalNotice.getProjectTaskSid() != null && frmArrivalNotice.getProjectSid() != null) {
            if (frmArrivalNotice.getProjectSid() != null) {
                List<PrjProject> projectList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda()
                        .eq(PrjProject::getProjectSid, frmArrivalNotice.getProjectSid())
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
            List<PrjProjectTask> taskList = prjProjectTaskService.selectPrjProjectTaskListById(frmArrivalNotice.getProjectSid());
            if (CollectionUtil.isNotEmpty(taskList)) {
                // 拍照样获取单对应的项目任务明细
                PrjProjectTask task = taskList.stream().filter(o->o.getProjectTaskSid().equals(frmArrivalNotice.getProjectTaskSid())).findFirst().get();
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
     * 新增到货通知单
     * 需要注意编码重复校验
     *
     * @param frmArrivalNotice 到货通知单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmArrivalNotice(FrmArrivalNotice frmArrivalNotice) {
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(frmArrivalNotice.getHandleStatus())) {
            frmArrivalNotice.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 不存 样品 和 开发计划
        frmArrivalNotice.setDevelopPlanSid(null).setDevelopPlanCode(null)
                .setSampleSid(null).setSampleCode(null);
        int row = frmArrivalNoticeMapper.insert(frmArrivalNotice);
        if (row > 0) {
            FrmArrivalNotice notice = frmArrivalNoticeMapper.selectFrmArrivalNoticeById(frmArrivalNotice.getArrivalNoticeSid());
            frmArrivalNotice.setArrivalNoticeCode(notice.getArrivalNoticeCode());
            // 写入附件
            if (CollectionUtil.isNotEmpty(frmArrivalNotice.getAttachmentList())) {
                frmArrivalNotice.getAttachmentList().forEach(item->{
                    item.setArrivalNoticeSid(frmArrivalNotice.getArrivalNoticeSid());
                    item.setArrivalNoticeCode(frmArrivalNotice.getArrivalNoticeCode());
                });
                frmArrivalNoticeAttachMapper.inserts(frmArrivalNotice.getAttachmentList());
            }
            // 提交或者确认处理逻辑
            Long[] sids = new Long[]{frmArrivalNotice.getArrivalNoticeSid()};
            this.approve(sids, frmArrivalNotice.getHandleStatus());
            // 删除项目档案任务明细的待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_PROJECT_TASK)
                    .eq(SysTodoTask::getDocumentSid, frmArrivalNotice.getProjectSid())
                    .eq(SysTodoTask::getDocumentItemSid, frmArrivalNotice.getProjectTaskSid())
                    .likeLeft(SysTodoTask::getTitle, "还未开始，请及时跟进！"));
            // 待办
            if (ConstantsEms.SAVA_STATUS.equals(frmArrivalNotice.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_FRM_ARRIVAL_NOTICE)
                        .setDocumentSid(frmArrivalNotice.getArrivalNoticeSid());
                String erpCode = notice.getErpMaterialSkuBarcode() == null ? "" : notice.getErpMaterialSkuBarcode();
                sysTodoTask.setTitle(erpCode + "到货通知单" + frmArrivalNotice.getArrivalNoticeCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(frmArrivalNotice.getArrivalNoticeCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                // 获取菜单id
                SysMenu menu = new SysMenu();
                menu.setMenuName(ConstantsWorkbench.TODO_FRM_ARR_NOTICE_MENU_NAME);
                menu = remoteMenuService.getInfoByName(menu).getData();
                if (menu != null && menu.getMenuId() != null) {
                    sysTodoTask.setMenuId(menu.getMenuId());
                }
                sysTodoTaskService.insertSysTodoTask(sysTodoTask);
            }
            // 提交启动审批
            else if (ConstantsEms.SUBMIT_STATUS.equals(frmArrivalNotice.getHandleStatus())) {
                frmArrivalNotice.setErpMaterialSkuBarcode(notice.getErpMaterialSkuBarcode());
                this.submit(frmArrivalNotice);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FrmArrivalNotice(), frmArrivalNotice);
            MongodbDeal.insert(frmArrivalNotice.getArrivalNoticeSid(), frmArrivalNotice.getHandleStatus(), msgList, TITLE, null);
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
            List<FrmArrivalNotice> designList = frmArrivalNoticeMapper.selectList(new QueryWrapper<FrmArrivalNotice>()
                    .lambda().in(FrmArrivalNotice::getArrivalNoticeSid, sids));
            Long[] projectTaskSids = designList.stream().map(FrmArrivalNotice::getProjectTaskSid).toArray(Long[]::new);
            if (ArrayUtil.isEmpty(projectTaskSids)) {
                return;
            }
            LambdaUpdateWrapper<PrjProjectTask> updateProjectTaskWrapper = new LambdaUpdateWrapper<>();
            updateProjectTaskWrapper.in(PrjProjectTask::getProjectTaskSid, projectTaskSids)
                    .eq(PrjProjectTask::getRelateBusinessFormCode, ConstantsPdm.RELATE_BUSINESS_FORM_DHTZ);
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
     * @param frmPhotoSampleGain 拍照样获取单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateFrmArrivalNoticeAttach(FrmArrivalNotice frmArrivalNotice) {
        // 先删后加
        frmArrivalNoticeAttachMapper.delete(new QueryWrapper<FrmArrivalNoticeAttach>().lambda()
                .eq(FrmArrivalNoticeAttach::getArrivalNoticeSid, frmArrivalNotice.getArrivalNoticeSid()));
        if (CollectionUtil.isNotEmpty(frmArrivalNotice.getAttachmentList())) {
            frmArrivalNotice.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getArrivalNoticeAttachSid() == null) {
                    att.setArrivalNoticeSid(frmArrivalNotice.getArrivalNoticeSid());
                    att.setArrivalNoticeCode(frmArrivalNotice.getArrivalNoticeCode());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            frmArrivalNoticeAttachMapper.inserts(frmArrivalNotice.getAttachmentList());
        }
    }

    /**
     * 修改到货通知单
     *
     * @param frmArrivalNotice 到货通知单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmArrivalNotice(FrmArrivalNotice frmArrivalNotice) {
        FrmArrivalNotice original = frmArrivalNoticeMapper.selectFrmArrivalNoticeById(frmArrivalNotice.getArrivalNoticeSid());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(frmArrivalNotice.getHandleStatus())) {
            frmArrivalNotice.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 不存 样品 和 开发计划
        frmArrivalNotice.setDevelopPlanSid(null).setDevelopPlanCode(null)
                .setSampleSid(null).setSampleCode(null);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, frmArrivalNotice);
        if (CollectionUtil.isNotEmpty(msgList)) {
            frmArrivalNotice.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = frmArrivalNoticeMapper.updateById(frmArrivalNotice);
        if (row > 0) {
            // 修改附件
            this.updateFrmArrivalNoticeAttach(frmArrivalNotice);
            // 不是保存状态删除待办
            Long[] sids = new Long[]{frmArrivalNotice.getArrivalNoticeSid()};
            if (!ConstantsEms.SAVA_STATUS.equals(frmArrivalNotice.getHandleStatus())) {
                sysTodoTaskService.deleteSysTodoTaskList(sids,
                        frmArrivalNotice.getHandleStatus(), ConstantsTable.TABLE_FRM_ARRIVAL_NOTICE);
            }
            // 审批处理逻辑
            this.approve(sids, frmArrivalNotice.getHandleStatus());
            // 提交启动审批
            if (ConstantsEms.SUBMIT_STATUS.equals(frmArrivalNotice.getHandleStatus())) {
                this.submit(frmArrivalNotice);
            }
            // 插入日志
            MongodbDeal.update(frmArrivalNotice.getArrivalNoticeSid(), original.getHandleStatus(), frmArrivalNotice.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更到货通知单
     *
     * @param frmArrivalNotice 到货通知单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFrmArrivalNotice(FrmArrivalNotice frmArrivalNotice) {
        FrmArrivalNotice response = frmArrivalNoticeMapper.selectFrmArrivalNoticeById(frmArrivalNotice.getArrivalNoticeSid());
        // 不存 样品 和 开发计划
        frmArrivalNotice.setDevelopPlanSid(null).setDevelopPlanCode(null)
                .setSampleSid(null).setSampleCode(null);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, frmArrivalNotice);
        if (CollectionUtil.isNotEmpty(msgList)) {
            frmArrivalNotice.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = frmArrivalNoticeMapper.updateAllById(frmArrivalNotice);
        if (row > 0) {
            // 附件信息
            this.updateFrmArrivalNoticeAttach(frmArrivalNotice);
            //插入日志
            MongodbUtil.insertUserLog(frmArrivalNotice.getArrivalNoticeSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除到货通知单
     *
     * @param arrivalNoticeSids 需要删除的到货通知单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmArrivalNoticeByIds(List<Long> arrivalNoticeSids) {
        List<FrmArrivalNotice> list = frmArrivalNoticeMapper.selectList(new QueryWrapper<FrmArrivalNotice>()
                .lambda().in(FrmArrivalNotice::getArrivalNoticeSid, arrivalNoticeSids));
        int row = frmArrivalNoticeMapper.deleteBatchIds(arrivalNoticeSids);
        if (row > 0) {
            // 删除附件
            frmArrivalNoticeAttachMapper.delete(new QueryWrapper<FrmArrivalNoticeAttach>().lambda()
                    .in(FrmArrivalNoticeAttach::getArrivalNoticeSid, arrivalNoticeSids));
            // 删除待办
            Long[] sids = arrivalNoticeSids.toArray(new Long[arrivalNoticeSids.size()]);
            sysTodoTaskService.deleteSysTodoTaskList(sids, null,
                    ConstantsTable.TABLE_FRM_ARRIVAL_NOTICE);
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FrmArrivalNotice());
                MongodbUtil.insertUserLog(o.getArrivalNoticeSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 提交
     *
     * @param frmArrivalNotice
     * @return
     */
    private void submit(FrmArrivalNotice frmArrivalNotice){
        if (frmArrivalNotice.getProjectSid() != null) {
            List<PrjProject> projectList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda()
                    .eq(PrjProject::getProjectSid, frmArrivalNotice.getProjectSid())
                    .eq(PrjProject::getHandleStatus, ConstantsEms.INVALID_STATUS));
            if (CollectionUtil.isNotEmpty(projectList)) {
                throw new BaseException("所属项目已作废，不允许提交！");
            }
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("formId", frmArrivalNotice.getArrivalNoticeSid());
        variables.put("formCode", frmArrivalNotice.getArrivalNoticeCode());
        variables.put("formType", FormType.ArrivalNotice.getCode());
        variables.put("startUserId", ApiThreadLocalUtil.get().getSysUser().getUserId());
        variables.put("erpCode", frmArrivalNotice.getErpMaterialSkuBarcode());
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
        LambdaUpdateWrapper<FrmArrivalNotice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FrmArrivalNotice::getArrivalNoticeSid, sids);
        updateWrapper.set(FrmArrivalNotice::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(FrmArrivalNotice::getConfirmDate, new Date());
            updateWrapper.set(FrmArrivalNotice::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return frmArrivalNoticeMapper.update(null, updateWrapper);
    }

    /**
     * 更改确认状态
     *
     * @param frmArrivalNotice
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FrmArrivalNotice frmArrivalNotice) {
        int row = 0;
        Long[] sids = frmArrivalNotice.getArrivalNoticeSidList();
        // 处理状态
        String handleStatus = frmArrivalNotice.getHandleStatus();
        if (StrUtil.isNotBlank(frmArrivalNotice.getBusinessType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(frmArrivalNotice.getBusinessType());
        }
        if (sids != null && sids.length > 0) {
            // 获取数据
            List<FrmArrivalNotice> noticeList = frmArrivalNoticeMapper.selectFrmArrivalNoticeList(new FrmArrivalNotice().setArrivalNoticeSidList(sids));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus,
                        ConstantsTable.TABLE_FRM_ARRIVAL_NOTICE);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(frmArrivalNotice.getBusinessType())) {
                // 审批处理逻辑
                this.approve(sids, ConstantsEms.SUBMIT_STATUS);
                // 修改处理状态
                this.updateHandle(sids, ConstantsEms.SUBMIT_STATUS);
                // 开启工作流
                for (int i = 0; i < noticeList.size(); i++) {
                    this.submit(noticeList.get(i));
                    //插入日志
                    MongodbUtil.insertUserLog(noticeList.get(i).getArrivalNoticeSid(),
                            BusinessType.SUBMIT.getValue(), null, TITLE, frmArrivalNotice.getComment());
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(frmArrivalNotice.getBusinessType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                for (int i = 0; i < noticeList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(noticeList.get(i).getArrivalNoticeSid().toString());
                    taskVo.setFormId(Long.valueOf(noticeList.get(i).getArrivalNoticeSid().toString()));
                    taskVo.setFormCode(noticeList.get(i).getArrivalNoticeCode().toString());
                    taskVo.setErpCode(noticeList.get(i).getErpMaterialSkuBarcode());
                    taskVo.setFormType(FormType.ArrivalNotice.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(frmArrivalNotice.getComment());
                    try {
                        SysFormProcess process = workFlowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 审批处理逻辑
                            this.approve(new Long[]{noticeList.get(i).getArrivalNoticeSid()}, ConstantsEms.CHECK_STATUS);
                            // 修改处理状态
                            this.updateHandle(new Long[]{noticeList.get(i).getArrivalNoticeSid()}, ConstantsEms.CHECK_STATUS);
                        }
                        frmArrivalNotice.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(noticeList.get(i).getArrivalNoticeSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, frmArrivalNotice.getComment());
                }
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(frmArrivalNotice.getBusinessType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                // 审批意见
                String comment = "";
                for (int i = 0; i < noticeList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setTargetKey("2");
                    taskVo.setBusinessKey(noticeList.get(i).getArrivalNoticeSid().toString());
                    taskVo.setFormId(Long.valueOf(noticeList.get(i).getArrivalNoticeSid().toString()));
                    taskVo.setFormCode(noticeList.get(i).getArrivalNoticeCode().toString());
                    taskVo.setErpCode(noticeList.get(i).getErpMaterialSkuBarcode());
                    taskVo.setFormType(FormType.ArrivalNotice.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(frmArrivalNotice.getComment());
                    try {
                        SysFormProcess process = workFlowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 审批驳回到提交人处理逻辑
                            this.approve(new Long[]{noticeList.get(i).getArrivalNoticeSid()}, HandleStatus.RETURNED.getCode());
                            // 修改处理状态
                            this.updateHandle(new Long[]{noticeList.get(i).getArrivalNoticeSid()}, HandleStatus.RETURNED.getCode());
                        }
                        frmArrivalNotice.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(noticeList.get(i).getArrivalNoticeSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, frmArrivalNotice.getComment());
                }
            }
        }
        return 1;
    }

}
