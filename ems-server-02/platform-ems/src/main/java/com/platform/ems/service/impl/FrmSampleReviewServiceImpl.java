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
import com.platform.ems.service.IFrmSampleReviewService;
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
 * 样品评审单Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-12
 */
@Service
@SuppressWarnings("all")
public class FrmSampleReviewServiceImpl extends ServiceImpl<FrmSampleReviewMapper, FrmSampleReview> implements IFrmSampleReviewService {
    @Autowired
    private FrmSampleReviewMapper frmSampleReviewMapper;
    @Autowired
    private FrmSampleReviewAttachMapper frmSampleReviewAttachMapper;
    @Autowired
    private PrjProjectMapper prjProjectMapper;
    @Autowired
    private PrjProjectTaskMapper prjProjectTaskMapper;
    @Autowired
    private BasStaffMapper basStaffMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
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

    private static final String TITLE = "样品评审单";

    /**
     * 查询样品评审单
     *
     * @param sampleReviewSid 样品评审单ID
     * @return 样品评审单
     */
    @Override
    public FrmSampleReview selectFrmSampleReviewById(Long sampleReviewSid) {
        FrmSampleReview frmSampleReview = frmSampleReviewMapper.selectFrmSampleReviewById(sampleReviewSid);
        // sku1Code 处理
        if (StrUtil.isNotBlank(frmSampleReview.getSku1Code())) {
            String[] sku1CodeList = frmSampleReview.getSku1Code().split(";");
            frmSampleReview.setSku1CodeList(sku1CodeList);
        }
        // 附件列表
        frmSampleReview.setAttachmentList(new ArrayList<>());
        List<FrmSampleReviewAttach> attachList = frmSampleReviewAttachMapper.selectFrmSampleReviewAttachList(
                new FrmSampleReviewAttach().setSampleReviewSid(sampleReviewSid));
        if (CollectionUtil.isNotEmpty(attachList)) {
            frmSampleReview.setAttachmentList(attachList);
        }
        // 操作日志
        MongodbUtil.find(frmSampleReview);
        return frmSampleReview;
    }

    /**
     * 查询样品评审单列表
     *
     * @param frmSampleReview 样品评审单
     * @return 样品评审单
     */
    @Override
    public List<FrmSampleReview> selectFrmSampleReviewList(FrmSampleReview frmSampleReview) {
        return frmSampleReviewMapper.selectFrmSampleReviewList(frmSampleReview);
    }

    /**
     * 提交前校验
     *
     * @param frmSampleReview 样品初审终审单
     * @return 结果
     */
    @Override
    public EmsResultEntity submitVerify(FrmSampleReview frmSampleReview) {
        if (HandleStatus.SUBMIT.getCode().equals(frmSampleReview.getHandleStatus())
                && frmSampleReview.getProjectTaskSid() != null && frmSampleReview.getProjectSid() != null) {
            if (frmSampleReview.getProjectSid() != null) {
                List<PrjProject> projectList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda()
                        .eq(PrjProject::getProjectSid, frmSampleReview.getProjectSid())
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
            List<PrjProjectTask> taskList = prjProjectTaskService.selectPrjProjectTaskListById(frmSampleReview.getProjectSid());
            if (CollectionUtil.isNotEmpty(taskList)) {
                // 拍照样获取单对应的项目任务明细
                PrjProjectTask task = taskList.stream().filter(o->o.getProjectTaskSid().equals(frmSampleReview.getProjectTaskSid())).findFirst().get();
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
     * 新增样品评审单
     * 需要注意编码重复校验
     *
     * @param frmSampleReview 样品评审单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmSampleReview(FrmSampleReview frmSampleReview) {
        String handleStatus = "";
        handleStatus = frmSampleReview.getHandleStatus();
        // 判断是否配置需要审批
        boolean flag = true;
        String remark = null;
        if (ConstantsEms.SUBMIT_STATUS.equals(frmSampleReview.getHandleStatus())) {
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            if (ConstantsPdm.REVIEW_STAGE_YPCS.equals(frmSampleReview.getReviewPhase())) {
                // 不启用审批流程
                if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowYangpcsd())) {
                    flag = false;
                    remark = "提交并确认";
                    frmSampleReview.setHandleStatus(ConstantsEms.CHECK_STATUS);
                }
            }
            else if (ConstantsPdm.REVIEW_STAGE_YPZS.equals(frmSampleReview.getReviewPhase())) {
                // 不启用审批流程
                if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowYangpzsd())) {
                    flag = false;
                    remark = "提交并确认";
                    frmSampleReview.setHandleStatus(ConstantsEms.CHECK_STATUS);
                }
            }
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(frmSampleReview.getHandleStatus())) {
            frmSampleReview.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 字段数据处理
        setData(frmSampleReview);
        // 写入部分字段的code
        this.setData(new FrmSampleReview(), frmSampleReview);
        int row = frmSampleReviewMapper.insert(frmSampleReview);
        if (row > 0) {
            // 获取编码等字段
            FrmSampleReview review = frmSampleReviewMapper.selectFrmSampleReviewById(frmSampleReview.getSampleReviewSid());
            frmSampleReview.setSampleReviewCode(review.getSampleReviewCode());
            // 写入附件
            if (CollectionUtil.isNotEmpty(frmSampleReview.getAttachmentList())) {
                frmSampleReview.getAttachmentList().forEach(item->{
                    item.setSampleReviewSid(frmSampleReview.getSampleReviewSid());
                    item.setSampleReviewCode(frmSampleReview.getSampleReviewCode());
                });
                frmSampleReviewAttachMapper.inserts(frmSampleReview.getAttachmentList());
            }
            // 删除项目档案任务明细的待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_PROJECT_TASK)
                    .eq(SysTodoTask::getDocumentSid, frmSampleReview.getProjectSid())
                    .eq(SysTodoTask::getDocumentItemSid, frmSampleReview.getProjectTaskSid())
                    .likeLeft(SysTodoTask::getTitle, "还未开始，请及时跟进！"));
            // 待办
            if (ConstantsEms.SAVA_STATUS.equals(frmSampleReview.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_FRM_SAMPLE_REVIEW)
                        .setDocumentSid(frmSampleReview.getSampleReviewSid());
                String erpCode = review.getErpMaterialSkuBarcode() == null ? "" : review.getErpMaterialSkuBarcode();
                if (ConstantsPdm.REVIEW_STAGE_YPCS.equals(frmSampleReview.getReviewPhase())) {
                    sysTodoTask.setTitle(erpCode + "样品初审单" + frmSampleReview.getSampleReviewCode() + "当前是保存状态，请及时处理！");
                } else if (ConstantsPdm.REVIEW_STAGE_YPZS.equals(frmSampleReview.getReviewPhase())) {
                    sysTodoTask.setTitle(erpCode + "样品终审单" + frmSampleReview.getSampleReviewCode() + "当前是保存状态，请及时处理！");
                } else {
                    sysTodoTask.setTitle(erpCode + "样品评审单" + frmSampleReview.getSampleReviewCode() + "当前是保存状态，请及时处理！");
                }
                sysTodoTask.setDocumentCode(frmSampleReview.getSampleReviewCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                // 获取菜单id
                SysMenu menu = new SysMenu();
                if (ConstantsPdm.REVIEW_STAGE_YPZS.equals(frmSampleReview.getReviewPhase())) {
                    menu.setMenuName(ConstantsWorkbench.TODO_FRM_SAM_REV_ZS_MENU_NAME);
                } else {
                    menu.setMenuName(ConstantsWorkbench.TODO_FRM_SAM_REV_CS_MENU_NAME);
                }
                menu = remoteMenuService.getInfoByName(menu).getData();
                if (menu != null && menu.getMenuId() != null) {
                    sysTodoTask.setMenuId(menu.getMenuId());
                }
                sysTodoTaskService.insertSysTodoTask(sysTodoTask);
            }
            // 提交启动审批
            else if (ConstantsEms.SUBMIT_STATUS.equals(frmSampleReview.getHandleStatus())) {
                if (flag) {
                    frmSampleReview.setErpMaterialSkuBarcode(review.getErpMaterialSkuBarcode());
                    this.submit(frmSampleReview);
                }
            }
            // 提交或者确认处理逻辑
            Long[] projectTaskSids = new Long[]{frmSampleReview.getProjectTaskSid()};
            this.approve(projectTaskSids, frmSampleReview.getHandleStatus(), frmSampleReview.getReviewPhase());
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FrmSampleReview(), frmSampleReview);
            MongodbDeal.insert(frmSampleReview.getSampleReviewSid(), handleStatus, msgList, TITLE, remark);
        }
        return row;
    }

    /**
     * 提交，审批 的处理逻辑 先
     *
     * @param sids，handleStatus
     * @return
     */
    private void approveList(List<FrmSampleReview> reviewList, String handleStatus) {
        if (CollectionUtil.isEmpty(reviewList) || StrUtil.isBlank(handleStatus)) {
            return;
        }
        // 初审的审批处理逻辑
        List<FrmSampleReview> reviewCsList = reviewList.stream().filter(o->
                ConstantsPdm.REVIEW_STAGE_YPCS.equals(o.getReviewPhase())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(reviewCsList)) {
            Long[] projectTaskSids = reviewCsList.stream().map(FrmSampleReview::getProjectTaskSid).toArray(Long[]::new);
            this.approve(projectTaskSids, handleStatus, ConstantsPdm.REVIEW_STAGE_YPCS);
        }
        // 终审的审批处理逻辑
        List<FrmSampleReview> reviewZsList = reviewList.stream().filter(o->
                ConstantsPdm.REVIEW_STAGE_YPZS.equals(o.getReviewPhase())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(reviewZsList)) {
            Long[] projectTaskSids = reviewZsList.stream().map(FrmSampleReview::getProjectTaskSid).toArray(Long[]::new);
            this.approve(projectTaskSids, handleStatus, ConstantsPdm.REVIEW_STAGE_YPZS);
        }
    }

    /**
     * 提交，审批 的处理逻辑 后
     *
     * @param projectTaskSids，handleStatus
     * @return
     */
    private void approve(Long[] projectTaskSids, String handleStatus, String reviewPhase) {
        if (projectTaskSids == null || projectTaskSids.length == 0 || StrUtil.isBlank(handleStatus)) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
            // 根据 project_sid + 硬编码”TGHZ“ 从 ”项目档案-任务表（s_prj_project_task）“中，
            // 根据”project_sid + relate_business_form_code“查找到对应的任务明细行，
            // 然后将该行的”任务状态“更新为 已完成 / 进行中
            LambdaUpdateWrapper<PrjProjectTask> updateProjectTaskWrapper = new LambdaUpdateWrapper<>();
            updateProjectTaskWrapper.in(PrjProjectTask::getProjectTaskSid, projectTaskSids);
            if (ConstantsPdm.REVIEW_STAGE_YPZS.equals(reviewPhase)) {
                updateProjectTaskWrapper.eq(PrjProjectTask::getRelateBusinessFormCode, ConstantsPdm.RELATE_BUSINESS_FORM_YPZS);
            } else {
                updateProjectTaskWrapper.eq(PrjProjectTask::getRelateBusinessFormCode, ConstantsPdm.RELATE_BUSINESS_FORM_YPCS);
            }
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
     * 设置部分字段的编码
     *
     * @param old 表中数据，new修改后数据
     * @return 结果
     */
    private void setData(FrmSampleReview oldReview, FrmSampleReview newReview) {
        // 设计师
        if (newReview.getDesignerSid() != null && !newReview.getDesignerSid().equals(oldReview.getDesignerSid())) {
            BasStaff staff = basStaffMapper.selectById(newReview.getDesignerSid());
            if (staff != null) { newReview.setDesignerCode(staff.getStaffCode()); }
            else { newReview.setDesignerCode(null); }
        } else if (newReview.getDesignerSid() == null) {
            newReview.setDesignerCode(null);
        }
        // 设计师助理
        if (newReview.getDesignerAssistantSid() != null && !newReview.getDesignerAssistantSid().equals(oldReview.getDesignerAssistantSid())) {
            BasStaff staff = basStaffMapper.selectById(newReview.getDesignerAssistantSid());
            if (staff != null) { newReview.setDesignerAssistantCode(staff.getStaffCode()); }
            else { newReview.setDesignerAssistantCode(null); }
        } else if (newReview.getDesignerAssistantSid() == null) {
            newReview.setDesignerAssistantCode(null);
        }
        // 打样供应商
        if (newReview.getSampleVendorSid() != null && !newReview.getSampleVendorSid().equals(oldReview.getSampleVendorSid())) {
            BasVendor vendor = basVendorMapper.selectById(newReview.getSampleVendorSid());
            if (vendor != null) { newReview.setSampleVendorCode(String.valueOf(vendor.getVendorCode())); }
            else { newReview.setSampleVendorCode(null); }
        } else if (newReview.getSampleVendorSid() == null) {
            newReview.setSampleVendorCode(null);
        }
        // 生产供应商
        if (newReview.getProductVendorSid() != null && !newReview.getProductVendorSid().equals(oldReview.getProductVendorSid())) {
            BasVendor vendor = basVendorMapper.selectById(newReview.getProductVendorSid());
            if (vendor != null) { newReview.setProductVendorCode(String.valueOf(vendor.getVendorCode())); }
            else { newReview.setProductVendorCode(null); }
        } else if (newReview.getProductVendorSid() == null) {
            newReview.setProductVendorCode(null);
        }
    }

    /**
     * 数据字段处理
     *
     * @param frmSampleReview 样品评审单
     * @return 结果
     */
    private void setData(FrmSampleReview frmSampleReview) {
        // 不存 样品 和 开发计划
        frmSampleReview.setDevelopPlanSid(null).setDevelopPlanCode(null)
                .setSampleSid(null).setSampleCode(null);
        // sku1Code处理
        if (ArrayUtil.isNotEmpty(frmSampleReview.getSku1CodeList())) {
            String sku1Code = "";
            for (int i = 0; i < frmSampleReview.getSku1CodeList().length; i++) {
                sku1Code = sku1Code + frmSampleReview.getSku1CodeList()[i] + ";";
            }
            frmSampleReview.setSku1Code(sku1Code);
        }
        // 新建默认币种
        if (frmSampleReview.getSampleReviewSid() == null) {
            frmSampleReview.setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
        }
    }

    /**
     * 批量修改附件信息
     *
     * @param frmSampleReview 样品评审单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateFrmSampleReviewAttach(FrmSampleReview frmSampleReview) {
        // 先删后加
        frmSampleReviewAttachMapper.delete(new QueryWrapper<FrmSampleReviewAttach>().lambda()
                .eq(FrmSampleReviewAttach::getSampleReviewSid, frmSampleReview.getSampleReviewSid()));
        if (CollectionUtil.isNotEmpty(frmSampleReview.getAttachmentList())) {
            frmSampleReview.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getSampleReviewAttachSid() == null) {
                    att.setSampleReviewSid(frmSampleReview.getSampleReviewSid());
                    att.setSampleReviewCode(frmSampleReview.getSampleReviewCode());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            frmSampleReviewAttachMapper.inserts(frmSampleReview.getAttachmentList());
        }
    }

    /**
     * 修改样品评审单
     *
     * @param frmSampleReview 样品评审单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmSampleReview(FrmSampleReview frmSampleReview) {
        FrmSampleReview original = frmSampleReviewMapper.selectFrmSampleReviewById(frmSampleReview.getSampleReviewSid());
        // 字段数据处理
        setData(frmSampleReview);
        // 判断是否配置需要审批
        boolean flag = true;
        String remark = null;
        String handleStatus = "";
        handleStatus = frmSampleReview.getHandleStatus();
        if (ConstantsEms.SUBMIT_STATUS.equals(frmSampleReview.getHandleStatus())) {
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            if (ConstantsPdm.REVIEW_STAGE_YPCS.equals(frmSampleReview.getReviewPhase())) {
                // 不启用审批流程
                if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowYangpcsd())) {
                    flag = false;
                }
            } else if (ConstantsPdm.REVIEW_STAGE_YPZS.equals(frmSampleReview.getReviewPhase())) {
                // 不启用审批流程
                if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowYangpzsd())) {
                    flag = false;
                }
            }
            if (!flag){
                remark = "提交并确认";
                frmSampleReview.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(frmSampleReview.getHandleStatus())) {
            frmSampleReview.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, frmSampleReview);
        if (CollectionUtil.isNotEmpty(msgList)) {
            frmSampleReview.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 写入部分字段的code
        this.setData(original, frmSampleReview);
        // 更新主表
        int row = frmSampleReviewMapper.updateById(frmSampleReview);
        if (row > 0) {
            // 修改附件
            this.updateFrmSampleReviewAttach(frmSampleReview);
            // 不是保存状态删除待办
            Long[] sids = new Long[]{frmSampleReview.getSampleReviewSid()};
            if (!ConstantsEms.SAVA_STATUS.equals(frmSampleReview.getHandleStatus())) {
                sysTodoTaskService.deleteSysTodoTaskList(sids,
                        frmSampleReview.getHandleStatus(), ConstantsTable.TABLE_FRM_SAMPLE_REVIEW);
            }
            // 提交启动审批
            if (ConstantsEms.SUBMIT_STATUS.equals(frmSampleReview.getHandleStatus())) {
                if (flag) {
                    this.submit(frmSampleReview);
                }
            }
            // 审批处理逻辑
            Long[] projectTaskSids = new Long[]{frmSampleReview.getProjectTaskSid()};
            this.approve(projectTaskSids, frmSampleReview.getHandleStatus(), frmSampleReview.getReviewPhase());
            //插入日志
            MongodbDeal.update(frmSampleReview.getSampleReviewSid(), original.getHandleStatus(), handleStatus, msgList, TITLE, remark);
        }
        return row;
    }

    /**
     * 变更样品评审单
     *
     * @param frmSampleReview 样品评审单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFrmSampleReview(FrmSampleReview frmSampleReview) {
        FrmSampleReview response = frmSampleReviewMapper.selectFrmSampleReviewById(frmSampleReview.getSampleReviewSid());
        // 字段数据处理
        setData(frmSampleReview);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, frmSampleReview);
        if (CollectionUtil.isNotEmpty(msgList)) {
            frmSampleReview.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 写入部分字段的code
        this.setData(response, frmSampleReview);
        // 更新主表
        int row = frmSampleReviewMapper.updateAllById(frmSampleReview);
        if (row > 0) {
            // 修改附件
            this.updateFrmSampleReviewAttach(frmSampleReview);
            //插入日志
            MongodbUtil.insertUserLog(frmSampleReview.getSampleReviewSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除样品评审单
     *
     * @param sampleReviewSids 需要删除的样品评审单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmSampleReviewByIds(List<Long> sampleReviewSids) {
        List<FrmSampleReview> list = frmSampleReviewMapper.selectList(new QueryWrapper<FrmSampleReview>()
                .lambda().in(FrmSampleReview::getSampleReviewSid, sampleReviewSids));
        int row = frmSampleReviewMapper.deleteBatchIds(sampleReviewSids);
        if (row > 0) {
            // 删除附件
            frmSampleReviewAttachMapper.delete(new QueryWrapper<FrmSampleReviewAttach>().lambda()
                    .in(FrmSampleReviewAttach::getSampleReviewSid, sampleReviewSids));
            // 删除待办
            Long[] sids = sampleReviewSids.toArray(new Long[sampleReviewSids.size()]);
            sysTodoTaskService.deleteSysTodoTaskList(sids, null,
                    ConstantsTable.TABLE_FRM_SAMPLE_REVIEW);
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FrmSampleReview());
                MongodbUtil.insertUserLog(o.getSampleReviewSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 提交
     *
     * @param frmSampleReview
     * @return
     */
    private void submit(FrmSampleReview frmSampleReview) {
        if (frmSampleReview.getProjectSid() != null) {
            List<PrjProject> projectList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda()
                    .eq(PrjProject::getProjectSid, frmSampleReview.getProjectSid())
                    .eq(PrjProject::getHandleStatus, ConstantsEms.INVALID_STATUS));
            if (CollectionUtil.isNotEmpty(projectList)) {
                throw new BaseException("所属项目已作废，不允许提交！");
            }
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("formId", frmSampleReview.getSampleReviewSid());
        variables.put("formCode", frmSampleReview.getSampleReviewCode());
        variables.put("erpCode", frmSampleReview.getErpMaterialSkuBarcode());
        if (ConstantsPdm.REVIEW_STAGE_YPZS.equals(frmSampleReview.getReviewPhase())) {
            variables.put("formType", FormType.SampleReviewZs.getCode());
        } else {
            variables.put("formType", FormType.SampleReviewCs.getCode());
        }
        variables.put("startUserId", ApiThreadLocalUtil.get().getSysUser().getUserId());
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
        LambdaUpdateWrapper<FrmSampleReview> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FrmSampleReview::getSampleReviewSid, sids);
        updateWrapper.set(FrmSampleReview::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(FrmSampleReview::getConfirmDate, new Date());
            updateWrapper.set(FrmSampleReview::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return frmSampleReviewMapper.update(null, updateWrapper);
    }

    /**
     * 更改确认状态
     *
     * @param frmSampleReview
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FrmSampleReview frmSampleReview) {
        int row = 0;
        Long[] sids = frmSampleReview.getSampleReviewSidList();
        // 处理状态
        String handleStatus = "";
        handleStatus = frmSampleReview.getHandleStatus();
        if (StrUtil.isNotBlank(frmSampleReview.getBusinessType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(frmSampleReview.getBusinessType());
        }
        if (sids != null && sids.length > 0) {
            row = sids.length;
            // 获取数据
            List<FrmSampleReview> reviewList = frmSampleReviewMapper.selectFrmSampleReviewList(new FrmSampleReview().setSampleReviewSidList(sids));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus,
                        ConstantsTable.TABLE_FRM_SAMPLE_REVIEW);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(frmSampleReview.getBusinessType())) {
                boolean flagCs = true, flagZs = true;
                // 判断是否配置需要审批
                SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                        .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
                if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowYangpcsd())) {
                    flagCs = false;
                }
                if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowYangpzsd())) {
                    flagZs = false;
                }
                // 初审的审批处理逻辑
                List<FrmSampleReview> reviewCsList = reviewList.stream().filter(o->
                        ConstantsPdm.REVIEW_STAGE_YPCS.equals(o.getReviewPhase())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(reviewCsList)) {
                    Long[] csSids = reviewCsList.stream().map(FrmSampleReview::getSampleReviewSid).toArray(Long[]::new);
                    Long[] projectTaskSids = reviewCsList.stream().map(FrmSampleReview::getProjectTaskSid).toArray(Long[]::new);
                    if (!flagCs) {
                        // 不用走审批
                        this.approve(projectTaskSids, ConstantsEms.CHECK_STATUS, ConstantsPdm.REVIEW_STAGE_YPCS);
                        // 修改处理状态
                        this.updateHandle(csSids, ConstantsEms.CHECK_STATUS);
                        //插入日志
                        for (int i = 0; i < reviewCsList.size(); i++) {
                            MongodbUtil.insertUserLog(reviewCsList.get(i).getSampleReviewSid(),
                                    BusinessType.SUBMIT.getValue(), null, TITLE, "提交并确认");
                        }
                    }
                    else {
                        // 走审批正常提交
                        this.approve(projectTaskSids, handleStatus, ConstantsPdm.REVIEW_STAGE_YPCS);
                        // 修改处理状态
                        this.updateHandle(csSids, ConstantsEms.SUBMIT_STATUS);
                        // 开启工作流
                        for (int i = 0; i < reviewCsList.size(); i++) {
                            // 提交
                            this.submit(reviewCsList.get(i));
                            //插入日志
                            MongodbUtil.insertUserLog(reviewCsList.get(i).getSampleReviewSid(),
                                    BusinessType.SUBMIT.getValue(), null, TITLE, frmSampleReview.getComment());
                        }
                    }
                }
                // 终审的审批处理逻辑
                List<FrmSampleReview> reviewZsList = reviewList.stream().filter(o->
                        ConstantsPdm.REVIEW_STAGE_YPZS.equals(o.getReviewPhase())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(reviewZsList)) {
                    Long[] zsSids = reviewZsList.stream().map(FrmSampleReview::getSampleReviewSid).toArray(Long[]::new);
                    Long[] projectTaskSids = reviewZsList.stream().map(FrmSampleReview::getProjectTaskSid).toArray(Long[]::new);
                    if (!flagZs) {
                        // 不用走审批
                        this.approve(projectTaskSids, ConstantsEms.CHECK_STATUS, ConstantsPdm.REVIEW_STAGE_YPZS);
                        // 修改处理状态
                        this.updateHandle(zsSids, ConstantsEms.CHECK_STATUS);
                        //插入日志
                        for (int i = 0; i < reviewZsList.size(); i++) {
                            MongodbUtil.insertUserLog(reviewZsList.get(i).getSampleReviewSid(),
                                    BusinessType.SUBMIT.getValue(), null, TITLE, "提交并确认");
                        }
                    }
                    else {
                        // 走审批正常提交
                        this.approve(projectTaskSids, handleStatus, ConstantsPdm.REVIEW_STAGE_YPZS);
                        // 修改处理状态
                        this.updateHandle(zsSids, ConstantsEms.SUBMIT_STATUS);
                        // 开启工作流
                        for (int i = 0; i < reviewZsList.size(); i++) {
                            // 提交
                            this.submit(reviewZsList.get(i));
                            //插入日志
                            MongodbUtil.insertUserLog(reviewZsList.get(i).getSampleReviewSid(),
                                    BusinessType.SUBMIT.getValue(), null, TITLE, frmSampleReview.getComment());
                        }
                    }
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(frmSampleReview.getBusinessType())) {
                Map<String, List<FrmSampleReview>> map = reviewList.stream()
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
                for (int i = 0; i < reviewList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(reviewList.get(i).getSampleReviewSid().toString());
                    taskVo.setFormId(Long.valueOf(reviewList.get(i).getSampleReviewSid().toString()));
                    taskVo.setFormCode(reviewList.get(i).getSampleReviewCode().toString());
                    taskVo.setErpCode(reviewList.get(i).getErpMaterialSkuBarcode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(frmSampleReview.getComment());
                    if (ConstantsPdm.REVIEW_STAGE_YPZS.equals(reviewList.get(i).getReviewPhase())) {
                        taskVo.setFormType(FormType.SampleReviewZs.getCode());
                    } else {
                        taskVo.setFormType(FormType.SampleReviewCs.getCode());
                    }
                    // 主动写入操作日志
                    List<OperMsg> operMsgList = new ArrayList<>();
                    try {
                        SysFormProcess process = workFlowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 最后一级审批通过处理逻辑
                            this.approve(new Long[]{reviewList.get(i).getProjectTaskSid()}, ConstantsEms.CHECK_STATUS,
                                    reviewList.get(i).getReviewPhase());
                            // 修改处理状态
                            this.updateHandle(new Long[]{reviewList.get(i).getSampleReviewSid()}, ConstantsEms.CHECK_STATUS);
                            // 操作日志字段
                            OperMsg msg = new OperMsg("handleStatus", "处理状态", reviewList.get(i).getHandleStatus(), HandleStatus.RETURNED.getCode());
                            operMsgList.add(msg);
                        }
                        comment = process.getRemark();
                    } catch (BaseException e) {
                        throw e;
                    }
                    OperMsg msg = new OperMsg("reviewResult", "评审结果", reviewList.get(i).getReviewResult(), frmSampleReview.getReviewResult());
                    operMsgList.add(msg);
                    //插入日志
                    MongodbUtil.insertUserLog(reviewList.get(i).getSampleReviewSid(),
                            BusinessType.APPROVAL.getValue(), operMsgList, TITLE, comment);
                }
                // 写入评审结果
                LambdaUpdateWrapper<FrmSampleReview> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.in(FrmSampleReview::getSampleReviewSid, sids);
                updateWrapper.set(FrmSampleReview::getReviewResult, frmSampleReview.getReviewResult());
                frmSampleReviewMapper.update(null, updateWrapper);
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(frmSampleReview.getBusinessType())) {
                Map<String, List<FrmSampleReview>> map = reviewList.stream()
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
                for (int i = 0; i < reviewList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setTargetKey("2");
                    taskVo.setBusinessKey(reviewList.get(i).getSampleReviewSid().toString());
                    taskVo.setFormId(Long.valueOf(reviewList.get(i).getSampleReviewSid().toString()));
                    taskVo.setFormCode(reviewList.get(i).getSampleReviewCode().toString());
                    taskVo.setErpCode(reviewList.get(i).getErpMaterialSkuBarcode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(frmSampleReview.getComment());
                    if (ConstantsPdm.REVIEW_STAGE_YPZS.equals(reviewList.get(i).getReviewPhase())) {
                        taskVo.setFormType(FormType.SampleReviewZs.getCode());
                    } else {
                        taskVo.setFormType(FormType.SampleReviewCs.getCode());
                    }
                    // 主动写入操作日志
                    List<OperMsg> operMsgList = new ArrayList<>();
                    try {
                        SysFormProcess process = workFlowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 审批驳回到提交人处理逻辑
                            this.approve(new Long[]{reviewList.get(i).getProjectTaskSid()},
                                    HandleStatus.RETURNED.getCode(), reviewList.get(i).getReviewPhase());
                            // 修改处理状态
                            this.updateHandle(new Long[]{reviewList.get(i).getSampleReviewSid()}, HandleStatus.RETURNED.getCode());
                            // 操作日志字段
                            OperMsg msg = new OperMsg("handleStatus", "处理状态", reviewList.get(i).getHandleStatus(), HandleStatus.RETURNED.getCode());
                            operMsgList.add(msg);
                        }
                        comment = process.getRemark();
                    } catch (BaseException e) {
                        throw e;
                    }
                    OperMsg msg = new OperMsg("reviewResult", "评审结果", reviewList.get(i).getReviewResult(), frmSampleReview.getReviewResult());
                    operMsgList.add(msg);
                    //插入日志
                    MongodbUtil.insertUserLog(reviewList.get(i).getSampleReviewSid(),
                            BusinessType.APPROVAL.getValue(), operMsgList, TITLE, comment);
                }
                // 写入评审结果
                LambdaUpdateWrapper<FrmSampleReview> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.in(FrmSampleReview::getSampleReviewSid, sids);
                updateWrapper.set(FrmSampleReview::getReviewResult, frmSampleReview.getReviewResult());
                frmSampleReviewMapper.update(null, updateWrapper);
            }
        }
        return 1;
    }

}
