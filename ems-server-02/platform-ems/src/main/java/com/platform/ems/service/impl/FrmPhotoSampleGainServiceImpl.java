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
import com.platform.ems.mapper.FrmPhotoSampleGainAttachMapper;
import com.platform.ems.mapper.FrmPhotoSampleGainMapper;
import com.platform.ems.mapper.PrjProjectMapper;
import com.platform.ems.mapper.PrjProjectTaskMapper;
import com.platform.ems.service.IFrmPhotoSampleGainService;
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
 * 视觉设计单Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-13
 */
@Service
@SuppressWarnings("all")
public class FrmPhotoSampleGainServiceImpl extends ServiceImpl<FrmPhotoSampleGainMapper, FrmPhotoSampleGain> implements IFrmPhotoSampleGainService {
    @Autowired
    private FrmPhotoSampleGainMapper frmPhotoSampleGainMapper;
    @Autowired
    private FrmPhotoSampleGainAttachMapper frmPhotoSampleGainAttachMapper;
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

    private static final String TITLE = "视觉设计单";

    /**
     * 查询视觉设计单
     *
     * @param photoSampleGainSid 视觉设计单ID
     * @return 视觉设计单
     */
    @Override
    public FrmPhotoSampleGain selectFrmPhotoSampleGainById(Long photoSampleGainSid) {
        FrmPhotoSampleGain frmPhotoSampleGain = frmPhotoSampleGainMapper.selectFrmPhotoSampleGainById(photoSampleGainSid);
        // 销售站点 处理
        if (StrUtil.isNotBlank(frmPhotoSampleGain.getSaleStation())) {
            String[] saleStationList = frmPhotoSampleGain.getSaleStation().split(";");
            frmPhotoSampleGain.setSaleStationList(saleStationList);
        }
        // 附件列表
        frmPhotoSampleGain.setAttachmentList(new ArrayList<>());
        List<FrmPhotoSampleGainAttach> attachList = frmPhotoSampleGainAttachMapper.selectFrmPhotoSampleGainAttachList(
                new FrmPhotoSampleGainAttach().setPhotoSampleGainSid(photoSampleGainSid));
        if (CollectionUtil.isNotEmpty(attachList)) {
            frmPhotoSampleGain.setAttachmentList(attachList);
        }
        // 操作日志
        MongodbUtil.find(frmPhotoSampleGain);
        return frmPhotoSampleGain;
    }

    /**
     * 查询视觉设计单列表
     *
     * @param frmPhotoSampleGain 视觉设计单
     * @return 视觉设计单
     */
    @Override
    public List<FrmPhotoSampleGain> selectFrmPhotoSampleGainList(FrmPhotoSampleGain frmPhotoSampleGain) {
        return frmPhotoSampleGainMapper.selectFrmPhotoSampleGainList(frmPhotoSampleGain);
    }

    /**
     * 数据字段处理
     *
     * @param frmPhotoSampleGain 视觉设计单
     * @return 结果
     */
    private void setData(FrmPhotoSampleGain frmPhotoSampleGain) {
        frmPhotoSampleGain.setDevelopPlanSid(null).setDevelopPlanCode(null)
                .setSampleSid(null).setSampleCode(null);
        // 销售站点处理
        if (ArrayUtil.isNotEmpty(frmPhotoSampleGain.getSaleStationList())) {
            String saleStation = "";
            for (int i = 0; i < frmPhotoSampleGain.getSaleStationList().length; i++) {
                saleStation = saleStation + frmPhotoSampleGain.getSaleStationList()[i] + ";";
            }
            frmPhotoSampleGain.setSaleStation(saleStation);
        }
    }

    /**
     * 提交前校验
     *
     * @param frmPhotoSampleGain 视觉设计单
     * @return 结果
     */
    @Override
    public EmsResultEntity submitVerify(FrmPhotoSampleGain frmPhotoSampleGain) {
        if (HandleStatus.SUBMIT.getCode().equals(frmPhotoSampleGain.getHandleStatus())
                && frmPhotoSampleGain.getProjectTaskSid() != null && frmPhotoSampleGain.getProjectSid() != null) {
            if (frmPhotoSampleGain.getProjectSid() != null) {
                List<PrjProject> projectList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda()
                        .eq(PrjProject::getProjectSid, frmPhotoSampleGain.getProjectSid())
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
            List<PrjProjectTask> taskList = prjProjectTaskService.selectPrjProjectTaskListById(frmPhotoSampleGain.getProjectSid());
            if (CollectionUtil.isNotEmpty(taskList)) {
                // 视觉设计单对应的项目任务明细
                PrjProjectTask task = taskList.stream().filter(o->o.getProjectTaskSid().equals(frmPhotoSampleGain.getProjectTaskSid())).findFirst().get();
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
     * 新增视觉设计单
     * 需要注意编码重复校验
     *
     * @param frmPhotoSampleGain 视觉设计单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFrmPhotoSampleGain(FrmPhotoSampleGain frmPhotoSampleGain) {
        String handleStatus = "";
        handleStatus = frmPhotoSampleGain.getHandleStatus();
        // 判断是否配置需要审批
        boolean flag = true;
        String remark = null;
        if (ConstantsEms.SUBMIT_STATUS.equals(frmPhotoSampleGain.getHandleStatus())) {
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowShijsjd())) {
                flag = false;
                remark = "提交并确认";
                frmPhotoSampleGain.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(frmPhotoSampleGain.getHandleStatus())) {
            frmPhotoSampleGain.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 字段数据处理
        setData(frmPhotoSampleGain);
        int row = frmPhotoSampleGainMapper.insert(frmPhotoSampleGain);
        if (row > 0) {
            // 获取编码等字段
            FrmPhotoSampleGain gain = frmPhotoSampleGainMapper.selectFrmPhotoSampleGainById(frmPhotoSampleGain.getPhotoSampleGainSid());
            frmPhotoSampleGain.setPhotoSampleGainCode(gain.getPhotoSampleGainCode());
            // 写入附件
            if (CollectionUtil.isNotEmpty(frmPhotoSampleGain.getAttachmentList())) {
                frmPhotoSampleGain.getAttachmentList().forEach(item->{
                    item.setPhotoSampleGainSid(frmPhotoSampleGain.getPhotoSampleGainSid());
                    item.setPhotoSampleGainCode(frmPhotoSampleGain.getPhotoSampleGainCode());
                });
                frmPhotoSampleGainAttachMapper.inserts(frmPhotoSampleGain.getAttachmentList());
            }
            // 删除项目档案任务明细的待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_PROJECT_TASK)
                    .eq(SysTodoTask::getDocumentSid, frmPhotoSampleGain.getProjectSid())
                    .eq(SysTodoTask::getDocumentItemSid, frmPhotoSampleGain.getProjectTaskSid())
                    .likeLeft(SysTodoTask::getTitle, "还未开始，请及时跟进！"));
            // 待办
            if (ConstantsEms.SAVA_STATUS.equals(frmPhotoSampleGain.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_FRM_PHOTO_SAMPLE_GAIN)
                        .setDocumentSid(frmPhotoSampleGain.getPhotoSampleGainSid());
                String erpCode = gain.getErpMaterialSkuBarcode() == null ? "" : gain.getErpMaterialSkuBarcode();
                sysTodoTask.setTitle(erpCode + "视觉设计单" + frmPhotoSampleGain.getPhotoSampleGainCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(frmPhotoSampleGain.getPhotoSampleGainCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                // 获取菜单id
                SysMenu menu = new SysMenu();
                menu.setMenuName(ConstantsWorkbench.TODO_FRM_PHO_SAM_GAIN_MENU_NAME);
                menu = remoteMenuService.getInfoByName(menu).getData();
                if (menu != null && menu.getMenuId() != null) {
                    sysTodoTask.setMenuId(menu.getMenuId());
                }
                sysTodoTaskService.insertSysTodoTask(sysTodoTask);
            }
            // 提交启动审批
            else if (ConstantsEms.SUBMIT_STATUS.equals(frmPhotoSampleGain.getHandleStatus())) {
                // 启用审批流程
                if (flag) {
                    frmPhotoSampleGain.setErpMaterialSkuBarcode(gain.getErpMaterialSkuBarcode());
                    this.submit(frmPhotoSampleGain);
                }
            }
            // 提交或者确认处理逻辑
            Long[] sids = new Long[]{frmPhotoSampleGain.getPhotoSampleGainSid()};
            this.approve(sids, frmPhotoSampleGain.getHandleStatus());
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FrmPhotoSampleGain(), frmPhotoSampleGain);
            MongodbDeal.insert(frmPhotoSampleGain.getPhotoSampleGainSid(), handleStatus, msgList, TITLE, remark);
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
            List<FrmPhotoSampleGain> designList = frmPhotoSampleGainMapper.selectList(new QueryWrapper<FrmPhotoSampleGain>()
                    .lambda().in(FrmPhotoSampleGain::getPhotoSampleGainSid, sids));
            Long[] projectTaskSids = designList.stream().map(FrmPhotoSampleGain::getProjectTaskSid).toArray(Long[]::new);
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
     * @param frmPhotoSampleGain 视觉设计单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateFrmPhotoSampleGainAttach(FrmPhotoSampleGain frmPhotoSampleGain) {
        // 先删后加
        frmPhotoSampleGainAttachMapper.delete(new QueryWrapper<FrmPhotoSampleGainAttach>().lambda()
                .eq(FrmPhotoSampleGainAttach::getPhotoSampleGainSid, frmPhotoSampleGain.getPhotoSampleGainSid()));
        if (CollectionUtil.isNotEmpty(frmPhotoSampleGain.getAttachmentList())) {
            frmPhotoSampleGain.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getPhotoSampleGainAttachSid() == null) {
                    att.setPhotoSampleGainSid(frmPhotoSampleGain.getPhotoSampleGainSid());
                    att.setPhotoSampleGainCode(frmPhotoSampleGain.getPhotoSampleGainCode());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            frmPhotoSampleGainAttachMapper.inserts(frmPhotoSampleGain.getAttachmentList());
        }
    }

    /**
     * 修改视觉设计单
     *
     * @param frmPhotoSampleGain 视觉设计单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFrmPhotoSampleGain(FrmPhotoSampleGain frmPhotoSampleGain) {
        FrmPhotoSampleGain original = frmPhotoSampleGainMapper.selectFrmPhotoSampleGainById(frmPhotoSampleGain.getPhotoSampleGainSid());
        // 字段数据处理
        setData(frmPhotoSampleGain);
        // 判断是否配置需要审批
        boolean flag = true;
        String remark = null;
        String handleStatus = "";
        handleStatus = frmPhotoSampleGain.getHandleStatus();
        if (ConstantsEms.SUBMIT_STATUS.equals(frmPhotoSampleGain.getHandleStatus())) {
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            // 启用审批流程
            if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowShijsjd())) {
                flag = false;
                remark = "提交并确认";
                frmPhotoSampleGain.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(frmPhotoSampleGain.getHandleStatus())) {
            frmPhotoSampleGain.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, frmPhotoSampleGain);
        if (CollectionUtil.isNotEmpty(msgList)) {
            frmPhotoSampleGain.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = frmPhotoSampleGainMapper.updateById(frmPhotoSampleGain);
        if (row > 0) {
            // 修改附件
            this.updateFrmPhotoSampleGainAttach(frmPhotoSampleGain);
            // 不是保存状态删除待办
            Long[] sids = new Long[]{frmPhotoSampleGain.getPhotoSampleGainSid()};
            if (!ConstantsEms.SAVA_STATUS.equals(frmPhotoSampleGain.getHandleStatus())) {
                sysTodoTaskService.deleteSysTodoTaskList(sids,
                        frmPhotoSampleGain.getHandleStatus(), ConstantsTable.TABLE_FRM_PHOTO_SAMPLE_GAIN);
            }
            // 提交启动审批
            if (ConstantsEms.SUBMIT_STATUS.equals(frmPhotoSampleGain.getHandleStatus())) {
                // 启用审批流程
                if (flag) {
                    this.submit(frmPhotoSampleGain);
                }
            }
            // 审批处理逻辑
            this.approve(sids, frmPhotoSampleGain.getHandleStatus());
            // 插入日志
            MongodbDeal.update(frmPhotoSampleGain.getPhotoSampleGainSid(), original.getHandleStatus(), handleStatus, msgList, TITLE, remark);
        }
        return row;
    }

    /**
     * 变更视觉设计单
     *
     * @param frmPhotoSampleGain 视觉设计单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFrmPhotoSampleGain(FrmPhotoSampleGain frmPhotoSampleGain) {
        FrmPhotoSampleGain response = frmPhotoSampleGainMapper.selectFrmPhotoSampleGainById(frmPhotoSampleGain.getPhotoSampleGainSid());
        // 字段数据处理
        setData(frmPhotoSampleGain);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, frmPhotoSampleGain);
        if (CollectionUtil.isNotEmpty(msgList)) {
            frmPhotoSampleGain.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = frmPhotoSampleGainMapper.updateAllById(frmPhotoSampleGain);
        if (row > 0) {
            // 附件信息
            this.updateFrmPhotoSampleGainAttach(frmPhotoSampleGain);
            // 插入日志
            MongodbUtil.insertUserLog(frmPhotoSampleGain.getPhotoSampleGainSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除视觉设计单
     *
     * @param photoSampleGainSids 需要删除的视觉设计单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFrmPhotoSampleGainByIds(List<Long> photoSampleGainSids) {
        List<FrmPhotoSampleGain> list = frmPhotoSampleGainMapper.selectList(new QueryWrapper<FrmPhotoSampleGain>()
                .lambda().in(FrmPhotoSampleGain::getPhotoSampleGainSid, photoSampleGainSids));
        int row = frmPhotoSampleGainMapper.deleteBatchIds(photoSampleGainSids);
        if (row > 0) {
            // 删除附件
            frmPhotoSampleGainAttachMapper.delete(new QueryWrapper<FrmPhotoSampleGainAttach>().lambda()
                    .in(FrmPhotoSampleGainAttach::getPhotoSampleGainSid, photoSampleGainSids));
            // 删除待办
            Long[] sids = photoSampleGainSids.toArray(new Long[photoSampleGainSids.size()]);
            sysTodoTaskService.deleteSysTodoTaskList(sids, null,
                    ConstantsTable.TABLE_FRM_PHOTO_SAMPLE_GAIN);
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FrmPhotoSampleGain());
                MongodbUtil.insertUserLog(o.getPhotoSampleGainSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 提交
     *
     * @param frmPhotoSampleGain
     * @return
     */
    private void submit(FrmPhotoSampleGain frmPhotoSampleGain) {
        if (frmPhotoSampleGain.getProjectSid() != null) {
            List<PrjProject> projectList = prjProjectMapper.selectList(new QueryWrapper<PrjProject>().lambda()
                    .eq(PrjProject::getProjectSid, frmPhotoSampleGain.getProjectSid())
                    .eq(PrjProject::getHandleStatus, ConstantsEms.INVALID_STATUS));
            if (CollectionUtil.isNotEmpty(projectList)) {
                throw new BaseException("所属项目已作废，不允许提交！");
            }
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("formId", frmPhotoSampleGain.getPhotoSampleGainSid());
        variables.put("formCode", frmPhotoSampleGain.getPhotoSampleGainCode());
        variables.put("formType", FormType.PhotoSampleGain.getCode());
        variables.put("startUserId", ApiThreadLocalUtil.get().getSysUser().getUserId());
        variables.put("erpCode", frmPhotoSampleGain.getErpMaterialSkuBarcode());
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
        // 更新状态
        LambdaUpdateWrapper<FrmPhotoSampleGain> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FrmPhotoSampleGain::getPhotoSampleGainSid, sids);
        updateWrapper.set(FrmPhotoSampleGain::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(FrmPhotoSampleGain::getConfirmDate, new Date());
            updateWrapper.set(FrmPhotoSampleGain::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return frmPhotoSampleGainMapper.update(null, updateWrapper);
    }

    /**
     * 更改确认状态
     *
     * @param frmPhotoSampleGain
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FrmPhotoSampleGain frmPhotoSampleGain) {
        int row = 0;
        Long[] sids = frmPhotoSampleGain.getPhotoSampleGainSidList();
        // 处理状态
        String handleStatus = "";
        handleStatus = frmPhotoSampleGain.getHandleStatus();
        if (StrUtil.isNotBlank(frmPhotoSampleGain.getBusinessType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(frmPhotoSampleGain.getBusinessType());
        }
        if (sids != null && sids.length > 0) {
            row = sids.length;
            // 获取数据
            List<FrmPhotoSampleGain> gainList = frmPhotoSampleGainMapper.selectFrmPhotoSampleGainList(new FrmPhotoSampleGain().setPhotoSampleGainSidList(sids));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus,
                        ConstantsTable.TABLE_FRM_PHOTO_SAMPLE_GAIN);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(frmPhotoSampleGain.getBusinessType())) {
                // 判断是否配置需要审批
                SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                        .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
                // 不启用审批流程
                if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowShijsjd())) {
                    // 审批处理逻辑
                    this.approve(sids, ConstantsEms.CHECK_STATUS);
                    // 修改处理状态
                    this.updateHandle(sids, ConstantsEms.CHECK_STATUS);
                    //插入日志
                    for (int i = 0; i < gainList.size(); i++) {
                        MongodbUtil.insertUserLog(gainList.get(i).getPhotoSampleGainSid(),
                                BusinessType.SUBMIT.getValue(), null, TITLE, "提交并确认");
                    }
                    return row;
                }
                // 审批处理逻辑
                this.approve(sids, ConstantsEms.SUBMIT_STATUS);
                // 修改处理状态
                this.updateHandle(sids, ConstantsEms.SUBMIT_STATUS);
                // 开启工作流
                for (int i = 0; i < gainList.size(); i++) {
                    this.submit(gainList.get(i));
                    //插入日志
                    MongodbUtil.insertUserLog(gainList.get(i).getPhotoSampleGainSid(),
                            BusinessType.SUBMIT.getValue(), null, TITLE, frmPhotoSampleGain.getComment());
                }
            }
            // 审批
            else if (BusinessType.APPROVED.getValue().equals(frmPhotoSampleGain.getBusinessType())) {
                Map<String, List<FrmPhotoSampleGain>> map = gainList.stream()
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
                for (int i = 0; i < gainList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(gainList.get(i).getPhotoSampleGainSid().toString());
                    taskVo.setFormId(Long.valueOf(gainList.get(i).getPhotoSampleGainSid().toString()));
                    taskVo.setFormCode(gainList.get(i).getPhotoSampleGainCode().toString());
                    taskVo.setErpCode(gainList.get(i).getErpMaterialSkuBarcode());
                    taskVo.setFormType(FormType.PhotoSampleGain.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(frmPhotoSampleGain.getComment());
                    try {
                        SysFormProcess process = workFlowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 审批处理逻辑
                            this.approve(new Long[]{gainList.get(i).getPhotoSampleGainSid()}, ConstantsEms.CHECK_STATUS);
                            // 修改处理状态
                            this.updateHandle(new Long[]{gainList.get(i).getPhotoSampleGainSid()}, ConstantsEms.CHECK_STATUS);
                        }
                        comment = process.getRemark();
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(gainList.get(i).getPhotoSampleGainSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, comment);
                }
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(frmPhotoSampleGain.getBusinessType())) {
                Map<String, List<FrmPhotoSampleGain>> map = gainList.stream()
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
                for (int i = 0; i < gainList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setTargetKey("2");
                    taskVo.setBusinessKey(gainList.get(i).getPhotoSampleGainSid().toString());
                    taskVo.setFormId(Long.valueOf(gainList.get(i).getPhotoSampleGainSid().toString()));
                    taskVo.setFormCode(gainList.get(i).getPhotoSampleGainCode().toString());
                    taskVo.setErpCode(gainList.get(i).getErpMaterialSkuBarcode());
                    taskVo.setFormType(FormType.PhotoSampleGain.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(frmPhotoSampleGain.getComment());
                    try {
                        SysFormProcess process = workFlowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 审批驳回到提交人处理逻辑
                            this.approve(new Long[]{gainList.get(i).getPhotoSampleGainSid()}, HandleStatus.RETURNED.getCode());
                            // 修改处理状态
                            this.updateHandle(new Long[]{gainList.get(i).getPhotoSampleGainSid()}, HandleStatus.RETURNED.getCode());
                        }
                        comment = process.getRemark();
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(gainList.get(i).getPhotoSampleGainSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, comment);
                }
            }
        }
        return row;
    }

}
