package com.platform.ems.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.exception.CustomException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.constant.*;
import com.platform.ems.domain.*;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IFinCustomerFundsFreezeBillService;
import com.platform.ems.service.ISysTodoTaskService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.workflow.service.impl.WorkFlowServiceImpl;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 客户暂押款Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Service
@SuppressWarnings("all")
public class FinCustomerFundsFreezeBillServiceImpl extends ServiceImpl<FinCustomerFundsFreezeBillMapper, FinCustomerFundsFreezeBill> implements IFinCustomerFundsFreezeBillService {
    @Autowired
    private FinCustomerFundsFreezeBillMapper finCustomerFundsFreezeBillMapper;
    @Autowired
    private FinCustomerFundsFreezeBillItemMapper finCustomerFundsFreezeBillItemMapper;
    @Autowired
    private FinCustomerFundsFreezeBillAttachMapper finCustomerFundsFreezeBillAttachMapper;
    @Autowired
    private BasCompanyMapper companyMapper;
    @Autowired
    private BasCustomerMapper customerMapper;
    @Autowired
    private SysDefaultSettingClientMapper defaultSettingClientMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISysTodoTaskService sysTodoTaskService;
    @Autowired
    private WorkFlowServiceImpl workflowService;

    private static final String TITLE = "客户暂押款";

    /**
     * 查询供应商暂押
     *
     * @param FundsFreezeBillSid 供应商暂押ID
     * @UNFREEZE 供应商暂押
     */
    @Override
    public FinCustomerFundsFreezeBill selectFinCustomerFundsFreezeBillById(Long fundsFreezeBillSid) {
        FinCustomerFundsFreezeBill finCustomerFundsFreezeBill = finCustomerFundsFreezeBillMapper.selectFinCustomerFundsFreezeBillById(fundsFreezeBillSid);
        if (finCustomerFundsFreezeBill == null) {
            return new FinCustomerFundsFreezeBill();
        }
        //明细
        FinCustomerFundsFreezeBillItem item = new FinCustomerFundsFreezeBillItem();
        item.setFundsFreezeBillSid(fundsFreezeBillSid);
        List<FinCustomerFundsFreezeBillItem> finCustomerFundsFreezeBillItemList = finCustomerFundsFreezeBillItemMapper.selectFinCustomerFundsFreezeBillItemList(item);
        finCustomerFundsFreezeBillItemList = finCustomerFundsFreezeBillItemList.stream().sorted(Comparator.comparing(FinCustomerFundsFreezeBillItem::getItemNum,
                Comparator.nullsLast(Long::compareTo))).collect(Collectors.toList());
        finCustomerFundsFreezeBill.setItemList(finCustomerFundsFreezeBillItemList);
        //附件
        finCustomerFundsFreezeBill.setAttachmentList(new ArrayList<>());
        List<FinCustomerFundsFreezeBillAttach> attachList =
                finCustomerFundsFreezeBillAttachMapper.selectFinCustomerFundsFreezeBillAttachList(new FinCustomerFundsFreezeBillAttach().setFundsFreezeBillSid(fundsFreezeBillSid));
        if (CollectionUtils.isNotEmpty(attachList)) {
            finCustomerFundsFreezeBill.setAttachmentList(attachList);
        }
        MongodbUtil.find(finCustomerFundsFreezeBill);
        return finCustomerFundsFreezeBill;
    }

    /**
     * 查询供应商暂押列表
     *
     * @param finCustomerFundsFreezeBill 供应商暂押
     * @UNFREEZE 供应商暂押
     */
    @Override
    public List<FinCustomerFundsFreezeBill> selectFinCustomerFundsFreezeBillList(FinCustomerFundsFreezeBill finCustomerFundsFreezeBill) {
        List<FinCustomerFundsFreezeBill> response = finCustomerFundsFreezeBillMapper.selectFinCustomerFundsFreezeBillList(finCustomerFundsFreezeBill);
        return response;
    }

    /**
     * 新增供应商暂押
     * 需要注意编码重复校验
     *
     * @param finCustomerFundsFreezeBill 供应商暂押
     * @UNFREEZE 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinCustomerFundsFreezeBill(FinCustomerFundsFreezeBill finCustomerFundsFreezeBill) {
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finCustomerFundsFreezeBill.getHandleStatus())) {
            SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
            if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowKhzyk())) {
                // 不需要走审批，提交及确认
                finCustomerFundsFreezeBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        //设置确认信息，校验
        setConfirmedInfo(finCustomerFundsFreezeBill);
        if (ConstantsFinance.DOC_TYPE_FREEZE_BZYK.equals(finCustomerFundsFreezeBill.getDocumentType())){
            finCustomerFundsFreezeBill.setUnfreezeStatus(ConstantsFinance.UNFREEZE_STATUS_WJD);
        }
        int row = finCustomerFundsFreezeBillMapper.insert(finCustomerFundsFreezeBill);
        if (row > 0) {
            FinCustomerFundsFreezeBill bill = finCustomerFundsFreezeBillMapper.selectById(finCustomerFundsFreezeBill.getFundsFreezeBillSid());
            finCustomerFundsFreezeBill.setFundsFreezeBillCode(bill.getFundsFreezeBillCode());
            //插入子表，附件表
            insertChild(finCustomerFundsFreezeBill.getItemList(), finCustomerFundsFreezeBill.getAttachmentList(), finCustomerFundsFreezeBill);
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(finCustomerFundsFreezeBill.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_FIN_CUSTOMER_FUNDS_FREEZE_BILL)
                        .setDocumentSid(finCustomerFundsFreezeBill.getFundsFreezeBillSid());
                sysTodoTask.setTitle("客户暂押款: " + finCustomerFundsFreezeBill.getFundsFreezeBillCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(finCustomerFundsFreezeBill.getFundsFreezeBillCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskService.insertSysTodoTaskMenu(sysTodoTask, ConstantsWorkbench.TODO_FIN_CUS_FREEZE_INFO);
            }
            // 走提交审批，参数从查询页面提交按钮参考
            else if (ConstantsEms.SUBMIT_STATUS.equals(finCustomerFundsFreezeBill.getHandleStatus())) {
                this.submit(finCustomerFundsFreezeBill);
            }
            //更新原暂押明细
            if (ConstantsFinance.DOC_TYPE_FREEZE_BSF.equals(finCustomerFundsFreezeBill.getDocumentType())){
                updateChild(finCustomerFundsFreezeBill);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(finCustomerFundsFreezeBill.getFundsFreezeBillSid(), finCustomerFundsFreezeBill.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改供应商暂押
     *
     * @param finCustomerFundsFreezeBill 供应商暂押
     * @UNFREEZE 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinCustomerFundsFreezeBill(FinCustomerFundsFreezeBill finCustomerFundsFreezeBill) {
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finCustomerFundsFreezeBill.getHandleStatus())) {
            SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
            if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowKhzyk())) {
                // 不需要走审批，提交及确认
                finCustomerFundsFreezeBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        //设置确认信息，校验
        setConfirmedInfo(finCustomerFundsFreezeBill);
        FinCustomerFundsFreezeBill response = finCustomerFundsFreezeBillMapper.selectFinCustomerFundsFreezeBillById(finCustomerFundsFreezeBill.getFundsFreezeBillSid());
        List<Long> sids = new ArrayList<>();
        sids.add(finCustomerFundsFreezeBill.getFundsFreezeBillSid());
        if (ConstantsFinance.DOC_TYPE_FREEZE_BSF.equals(finCustomerFundsFreezeBill.getDocumentType())){
            //回退暂押明细来源
            returnChild(sids);
        }
        int row = finCustomerFundsFreezeBillMapper.updateAllById(finCustomerFundsFreezeBill);
        if (row > 0) {
            //删除子表，附件表
            deleteItem(sids);
            //插入子表，附件表
            insertChild(finCustomerFundsFreezeBill.getItemList(), finCustomerFundsFreezeBill.getAttachmentList(), finCustomerFundsFreezeBill);
            //不是保存状态时删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(finCustomerFundsFreezeBill.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, finCustomerFundsFreezeBill.getFundsFreezeBillSid()));
            }
            // 走提交审批，参数从查询页面提交按钮参考
            if (ConstantsEms.SUBMIT_STATUS.equals(finCustomerFundsFreezeBill.getHandleStatus())) {
                this.submit(finCustomerFundsFreezeBill);
            }
            //更新原暂押明细
            if (ConstantsFinance.DOC_TYPE_FREEZE_BSF.equals(finCustomerFundsFreezeBill.getDocumentType())){
                updateChild(finCustomerFundsFreezeBill);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finCustomerFundsFreezeBill);
            MongodbDeal.update(finCustomerFundsFreezeBill.getFundsFreezeBillSid(), response.getHandleStatus(), finCustomerFundsFreezeBill.getHandleStatus(), msgList, TITLE,null);

        }
        return row;
    }

    /**
     * 变更供应商暂押
     *
     * @param finCustomerFundsFreezeBill 供应商暂押
     * @UNFREEZE 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinCustomerFundsFreezeBill(FinCustomerFundsFreezeBill finCustomerFundsFreezeBill) {
        //设置确认信息，校验
        setConfirmedInfo(finCustomerFundsFreezeBill);
        FinCustomerFundsFreezeBill response = finCustomerFundsFreezeBillMapper.selectFinCustomerFundsFreezeBillById(finCustomerFundsFreezeBill.getFundsFreezeBillSid());
        List<Long> sids = new ArrayList<>();
        sids.add(finCustomerFundsFreezeBill.getFundsFreezeBillSid());
        if (ConstantsFinance.DOC_TYPE_FREEZE_BSF.equals(finCustomerFundsFreezeBill.getDocumentType())){
            //回退暂押明细来源
            returnChild(sids);
        }
        int row = finCustomerFundsFreezeBillMapper.updateAllById(finCustomerFundsFreezeBill);
        if (row > 0) {
            //删除子表，附件表
            deleteItem(sids);
            //插入子表，附件表
            insertChild(finCustomerFundsFreezeBill.getItemList(), finCustomerFundsFreezeBill.getAttachmentList(), finCustomerFundsFreezeBill);
            //更新原暂押明细
            if (ConstantsFinance.DOC_TYPE_FREEZE_BSF.equals(finCustomerFundsFreezeBill.getDocumentType())){
                updateChild(finCustomerFundsFreezeBill);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finCustomerFundsFreezeBill);
            MongodbUtil.insertUserLog(finCustomerFundsFreezeBill.getFundsFreezeBillSid(), BusinessType.CHANGE.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商暂押
     *
     * @param fundsFreezeBillSids 需要删除的供应商暂押ID
     * @UNFREEZE 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinCustomerFundsFreezeBillByIds(List<Long> fundsFreezeBillSids) {
        if (CollectionUtils.isEmpty(fundsFreezeBillSids)){
            throw new BaseException("请选择行！");
        }
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(HandleStatus.SAVE.getCode());
        handleStatusList.add(HandleStatus.RETURNED.getCode());
        List<FinCustomerFundsFreezeBill> finCustomerFundsFreezeBillList = finCustomerFundsFreezeBillMapper.selectList(new QueryWrapper<FinCustomerFundsFreezeBill>().lambda()
                .in(FinCustomerFundsFreezeBill::getFundsFreezeBillSid,fundsFreezeBillSids)
                .notIn(FinCustomerFundsFreezeBill::getHandleStatus, handleStatusList));
        if (CollectionUtils.isNotEmpty(finCustomerFundsFreezeBillList)){
            throw new BaseException("仅保存状态和退回状态允许删除操作！");
        }
        //筛选退回
        List<FinCustomerFundsFreezeBill> billList = finCustomerFundsFreezeBillMapper.selectList(new QueryWrapper<FinCustomerFundsFreezeBill>()
                .lambda().in(FinCustomerFundsFreezeBill::getFundsFreezeBillSid,fundsFreezeBillSids));
        int i = finCustomerFundsFreezeBillMapper.deleteBatchIds(fundsFreezeBillSids);
        if (i > 0) {
            fundsFreezeBillSids.forEach(sid -> {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
            });
            List<Long> sids = new ArrayList<>();
            billList.forEach(item->{
                if (ConstantsFinance.DOC_TYPE_FREEZE_BSF.equals(item.getDocumentType())){
                    sids.add(item.getFundsFreezeBillSid());
                }
            });
            //回退明细来源
            if (CollectionUtils.isNotEmpty(sids)){
                returnChild(sids);
            }
            //删除明细
            deleteItem(fundsFreezeBillSids);
            //删除待办
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, fundsFreezeBillSids));
        }
        return i;
    }

    /**
     * 更改确认状态
     *
     * @param finCustomerFundsFreezeBill
     * @UNFREEZE
     */
    @Override
    public int check(FinCustomerFundsFreezeBill finCustomerFundsFreezeBill) {
        int row = 0;
        Long[] sids = finCustomerFundsFreezeBill.getFundsFreezeBillSidList();
        if (sids != null && sids.length > 0) {
            Map<Long, FinCustomerFundsFreezeBill> billMap = new HashMap<>();
            List<Long> bsfBillSids = new ArrayList<>();
            for (Long sid : sids) {
                List<FinCustomerFundsFreezeBillItem> itemList = finCustomerFundsFreezeBillItemMapper.selectFinCustomerFundsFreezeBillItemList(
                        (new FinCustomerFundsFreezeBillItem().setFundsFreezeBillSid(sid)));
                if (CollectionUtils.isEmpty(itemList)){
                    throw new CustomException("明细不能为空!");
                }
                // 主表
                FinCustomerFundsFreezeBill bill = this.selectFinCustomerFundsFreezeBillById(sid);
                if (ConstantsFinance.DOC_TYPE_FREEZE_BSF.equals(bill.getDocumentType())){
                    bsfBillSids.add(sid);
                }
                billMap.put(sid, bill);
            }
            // 判断是否要走审批
            if (BusinessType.SUBMIT.getValue().equals(finCustomerFundsFreezeBill.getOperateType())) {
                SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                        .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
                if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowKhzyk())) {

                    // 确认后操作
                    if (CollectionUtils.isNotEmpty(bsfBillSids)) {
                        // 回退暂押明细来源
                        returnChild(bsfBillSids);
                        // 更新原暂押明细
                        for (Long bsfBillSid : bsfBillSids) {
                            if (billMap.containsKey(bsfBillSid)) {
                                FinCustomerFundsFreezeBill bill = billMap.get(bsfBillSid);
                                bill.setHandleStatus(ConstantsEms.CHECK_STATUS);
                                updateChild(bill);
                            }
                        }
                    }

                    // 不需要走审批，提交及确认
                    LambdaUpdateWrapper<FinCustomerFundsFreezeBill> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.in(FinCustomerFundsFreezeBill::getFundsFreezeBillSid,sids)
                            .set(FinCustomerFundsFreezeBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                            .set(FinCustomerFundsFreezeBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                            .set(FinCustomerFundsFreezeBill::getConfirmDate, new Date());
                    row = finCustomerFundsFreezeBillMapper.update(null, updateWrapper);
                    for (Long id : sids) {
                        //插入日志
                        MongodbDeal.check(id, ConstantsEms.CHECK_STATUS, null, TITLE, null);
                    }
                    //删除待办
                    sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                            .in(SysTodoTask::getDocumentSid,sids));
                    return row;
                }
            }
            // 走工作流程
            row = workFlow(finCustomerFundsFreezeBill);
        }
        return row;
    }

    /**
     * 提交
     */
    private void submit(FinCustomerFundsFreezeBill finCustomerFundsFreezeBill){
        Map<String, Object> variables = new HashMap<>();
        variables.put("formCode", finCustomerFundsFreezeBill.getFundsFreezeBillCode());
        variables.put("formId", finCustomerFundsFreezeBill.getFundsFreezeBillSid());
        variables.put("formType", FormType.CustomerFundsFreezeBill.getCode());
        variables.put("startUserId", ApiThreadLocalUtil.get().getSysUser().getUserId());
        try {
            AjaxResult result = workflowService.submitOnly(variables);
        } catch (BaseException e) {
            throw e;
        }
    }

    /**
     * 更改处理状态
     */
    private int updateHandle(Long[] sids, String handleStatus) {
        LambdaUpdateWrapper<FinCustomerFundsFreezeBill> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinCustomerFundsFreezeBill::getFundsFreezeBillSid, sids);
        updateWrapper.set(FinCustomerFundsFreezeBill::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(FinCustomerFundsFreezeBill::getConfirmDate, new Date());
            updateWrapper.set(FinCustomerFundsFreezeBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return finCustomerFundsFreezeBillMapper.update(null, updateWrapper);
    }

    /**
     * 工作流流程
     */
    public int workFlow(FinCustomerFundsFreezeBill finCustomerFundsFreezeBill) {
        int row = 1;
        Long[] sids = finCustomerFundsFreezeBill.getFundsFreezeBillSidList();
        // 处理状态
        String handleStatus = finCustomerFundsFreezeBill.getHandleStatus();
        if (StrUtil.isNotBlank(finCustomerFundsFreezeBill.getOperateType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(finCustomerFundsFreezeBill.getOperateType());
        }
        if (sids != null && sids.length > 0) {
            // 获取数据
            List<FinCustomerFundsFreezeBill> billList = finCustomerFundsFreezeBillMapper.selectFinCustomerFundsFreezeBillList(
                    (new FinCustomerFundsFreezeBill().setFundsFreezeBillSidList(sids)));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus,
                        ConstantsTable.TABLE_FIN_CUSTOMER_FUNDS_FREEZE_BILL);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(finCustomerFundsFreezeBill.getOperateType())) {
                // 修改处理状态
                row = this.updateHandle(sids, ConstantsEms.SUBMIT_STATUS);
                // 开启工作流
                for (int i = 0; i < billList.size(); i++) {
                    this.submit(billList.get(i));
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getFundsFreezeBillSid(),
                            BusinessType.SUBMIT.getValue(), null, TITLE, finCustomerFundsFreezeBill.getComment());
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(finCustomerFundsFreezeBill.getOperateType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();

                // 确认后操作 回退暂押明细来源
                List<Long> bsfBillSids = new ArrayList<>();
                Map<Long, FinCustomerFundsFreezeBill> billMap = new HashMap<>();
                for (int i = 0; i < billList.size(); i++) {
                    if (ConstantsFinance.DOC_TYPE_FREEZE_BSF.equals(billList.get(i).getDocumentType())){
                        bsfBillSids.add(billList.get(i).getFundsFreezeBillSid());
                        billMap.put(billList.get(i).getFundsFreezeBillSid(), billList.get(i));
                    }
                }
                if (CollectionUtils.isNotEmpty(bsfBillSids)) {
                    // 回退暂押明细来源
                    returnChild(bsfBillSids);
                }

                for (int i = 0; i < billList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(billList.get(i).getFundsFreezeBillSid().toString());
                    taskVo.setFormId(Long.valueOf(billList.get(i).getFundsFreezeBillSid().toString()));
                    taskVo.setFormCode(billList.get(i).getFundsFreezeBillCode().toString());
                    taskVo.setFormType(FormType.CustomerFundsFreezeBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finCustomerFundsFreezeBill.getComment());
                    try {
                        SysFormProcess process = workflowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 确认后操作
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getFundsFreezeBillSid()}, ConstantsEms.CHECK_STATUS);
                            // 更新原暂押明细
                            for (Long bsfBillSid : bsfBillSids) {
                                if (billMap.containsKey(bsfBillSid)) {
                                    FinCustomerFundsFreezeBill bill = billMap.get(bsfBillSid);
                                    List<FinCustomerFundsFreezeBillItem> itemList = finCustomerFundsFreezeBillItemMapper.selectFinCustomerFundsFreezeBillItemList(
                                            (new FinCustomerFundsFreezeBillItem().setFundsFreezeBillSid(bsfBillSid)));
                                    bill.setItemList(itemList);
                                    bill.setHandleStatus(ConstantsEms.CHECK_STATUS);
                                    updateChild(bill);
                                }
                            }
                        }
                        finCustomerFundsFreezeBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getFundsFreezeBillSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, finCustomerFundsFreezeBill.getComment());
                }
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(finCustomerFundsFreezeBill.getOperateType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                // 审批意见
                String comment = "";
                for (int i = 0; i < billList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setTargetKey("2");
                    taskVo.setBusinessKey(billList.get(i).getFundsFreezeBillSid().toString());
                    taskVo.setFormId(Long.valueOf(billList.get(i).getFundsFreezeBillSid().toString()));
                    taskVo.setFormCode(billList.get(i).getFundsFreezeBillCode().toString());
                    taskVo.setFormType(FormType.CustomerFundsFreezeBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finCustomerFundsFreezeBill.getComment());
                    try {
                        SysFormProcess process = workflowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getFundsFreezeBillSid()}, HandleStatus.RETURNED.getCode());
                        }
                        finCustomerFundsFreezeBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getFundsFreezeBillSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, finCustomerFundsFreezeBill.getComment());
                }
            }
        }
        return row;
    }

    /**
     * 处理解冻状态
     *
     * @param entity
     * @UNFREEZE
     */
    public String checkStatus(List<FinCustomerFundsFreezeBillItem> list){
        int flag = -1;
        String unfreezeStatus = ConstantsFinance.UNFREEZE_STATUS_WJD;
        BigDecimal sumAccount = BigDecimal.ZERO;
        BigDecimal sumYsf = BigDecimal.ZERO;
        BigDecimal sumSfz = BigDecimal.ZERO;
        for (FinCustomerFundsFreezeBillItem item : list){
            sumAccount = sumAccount.add(item.getCurrencyAmount());
            sumYsf = sumYsf.add(item.getCurrencyAmountYsf());
            sumSfz = sumSfz.add(item.getCurrencyAmountSfz());
        }
        if (sumAccount.compareTo(sumYsf) == 0){
            unfreezeStatus = ConstantsFinance.UNFREEZE_STATUS_QBJD;
        }
        else {
            unfreezeStatus = ConstantsFinance.UNFREEZE_STATUS_BFJD;
        }
        if ((sumYsf.add(sumSfz)).compareTo(BigDecimal.ZERO) == 0){
            unfreezeStatus = ConstantsFinance.UNFREEZE_STATUS_WJD;
        }
        return unfreezeStatus;
    }

    /**
     * 设置确认信息
     *
     * @param entity
     * @UNFREEZE
     */
    public FinCustomerFundsFreezeBill setConfirmedInfo(FinCustomerFundsFreezeBill entity) {
        if (CollectionUtils.isEmpty(entity.getItemList())){
            throw new CustomException("明细不能为空!");
        }
        //确认人，确认日期
        if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)){
            entity.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            entity.setConfirmDate(new Date());
        }
        if (!ConstantsFinance.DOC_TYPE_FREEZE_BSF.equals(entity.getDocumentType())){
            if (CollectionUtils.isNotEmpty(entity.getItemList())){
                entity.getItemList().forEach(item->{
                    if (BigDecimal.ZERO.compareTo(item.getCurrencyAmount()) >= 0){
                        throw new BaseException("暂押金额必须大于 0 ！");
                    }
                    if (item.getFundsFreezeBillItemSid() == null){
                        item.setCurrencyAmountSfz(BigDecimal.ZERO).setCurrencyAmountYsf(BigDecimal.ZERO);
                    }
                });
            }
        }
        if (ConstantsFinance.DOC_TYPE_FREEZE_BSF.equals(entity.getDocumentType())){
            if (CollectionUtils.isNotEmpty(entity.getItemList())){
                entity.getItemList().forEach(item->{
                    if (BigDecimal.ZERO.compareTo(item.getCurrencyAmount()) >= 0){
                        throw new BaseException("本次释放金额必须大于 0 ！");
                    }
                    if (item.getCurrencyAmount().compareTo(item.getPreCurrencyAmountDsf()) > 0){
                        throw new BaseException("明细中本次释放金额不能大于待释放金额！");
                    }
                });
            }
        }
        //
        entity.setCompanyCode(null);
        if (entity.getCompanySid() != null) {
            BasCompany company = companyMapper.selectById(entity.getCompanySid());
            if (company != null) {
                entity.setCompanyCode(company.getCompanyCode());
            }
        }
        entity.setCustomerCode(null);
        if (entity.getCustomerSid() != null) {
            BasCustomer customer = customerMapper.selectById(entity.getCustomerSid());
            if (customer != null) {
                entity.setCustomerCode(customer.getCustomerCode());
            }
        }
        return entity;
    }

    /**
     * 删除子表
     */
    public void deleteItem(List<Long> FundsFreezeBillSids) {
        //明细表
        finCustomerFundsFreezeBillItemMapper.delete(new QueryWrapper<FinCustomerFundsFreezeBillItem>().lambda().in(FinCustomerFundsFreezeBillItem::getFundsFreezeBillSid,FundsFreezeBillSids));
        //附件表
        finCustomerFundsFreezeBillAttachMapper.delete(new QueryWrapper<FinCustomerFundsFreezeBillAttach>().lambda().in(FinCustomerFundsFreezeBillAttach::getFundsFreezeBillSid,FundsFreezeBillSids));
    }

    /**
     * 回退暂押明细来源
     */
    public void returnChild(List<Long> fundsFreezeBillSids){
        if (CollectionUtils.isEmpty(fundsFreezeBillSids)) {
            return;
        }
        List<FinCustomerFundsFreezeBillItem> itemList = finCustomerFundsFreezeBillItemMapper.selectFinCustomerFundsFreezeBillItemList(
                new FinCustomerFundsFreezeBillItem().setFundsFreezeBillSidList(fundsFreezeBillSids.toArray(new Long[fundsFreezeBillSids.size()])));
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        itemList.forEach(item->{
            //暂押明细来源
            FinCustomerFundsFreezeBillItem original = finCustomerFundsFreezeBillItemMapper.selectFinCustomerFundsFreezeBillItemById(item.getPreFundsFreezeBillItemSid());
            if (original != null){

                if (ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus()) || ConstantsEms.INVALID_STATUS.equals(item.getHandleStatus())){
                    original.setCurrencyAmountYsf(original.getCurrencyAmountYsf().subtract(item.getCurrencyAmount()));
                }
                if (!ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus()) && !ConstantsEms.INVALID_STATUS.equals(item.getHandleStatus())){
                    original.setCurrencyAmountSfz(original.getCurrencyAmountSfz().subtract(item.getCurrencyAmount()));
                }
                original.setUnfreezeStatus(ConstantsFinance.UNFREEZE_STATUS_BFJD);
                if ((original.getCurrencyAmountYsf().add(original.getCurrencyAmountSfz())).compareTo(BigDecimal.ZERO) == 0){
                    original.setUnfreezeStatus(ConstantsFinance.UNFREEZE_STATUS_WJD);
                }
                if (original.getCurrencyAmountYsf().compareTo(original.getCurrencyAmount()) == 0){
                    original.setUnfreezeStatus(ConstantsFinance.UNFREEZE_STATUS_QBJD);
                }
                finCustomerFundsFreezeBillItemMapper.updateAllById(original);
            }
        });
        // 修改来源主表的退回状态
        List<Long> preFundsFreezeBillSidList = itemList.stream().map(FinCustomerFundsFreezeBillItem::getPreFundsFreezeBillSid).distinct().collect(Collectors.toList());
        for (Long preFundsFreezeBillSid : preFundsFreezeBillSidList) {
            updatePreUnfreezeStatus(preFundsFreezeBillSid);
        }
    }


    /**
     * 添加子表
     *
     * @param itemList
     * @param atmList
     * @param sid
     */
    public void insertChild(List<FinCustomerFundsFreezeBillItem> itemList, List<FinCustomerFundsFreezeBillAttach> atmList, FinCustomerFundsFreezeBill entity) {
        Long sid = entity.getFundsFreezeBillSid();
        //退回类型单据的操作
        if (ConstantsFinance.DOC_TYPE_FREEZE_BSF.equals(entity.getDocumentType()) && CollectionUtils.isNotEmpty(itemList)){
            //明细表
            List<FinCustomerFundsFreezeBillItem> sortList = itemList.stream().sorted(Comparator.comparing(FinCustomerFundsFreezeBillItem::getItemNum, Comparator.nullsFirst(Long::compareTo)).reversed()).collect(Collectors.toList());
            Long maxNum = sortList.get(0).getItemNum();
            if (maxNum == null || maxNum.equals(0)){
                maxNum = new Long("0");
            }
            for (FinCustomerFundsFreezeBillItem item : itemList) {
                item.setFundsFreezeBillSid(sid);
                item.setCurrencyAmountSfz(null);
                item.setCurrencyAmountYsf(null);
                item.setUnfreezeStatus(null);
                if (item.getItemNum() == null){
                    maxNum = maxNum + new Long("1");
                    item.setItemNum(maxNum);
                }
            };
            finCustomerFundsFreezeBillItemMapper.inserts(itemList);
        }
        //非退回类型的单据操作
        if (!ConstantsFinance.DOC_TYPE_FREEZE_BSF.equals(entity.getDocumentType()) && CollectionUtils.isNotEmpty(itemList)){
            if (ConstantsEms.CHECK_STATUS.equals(entity.getHandleStatus())){
                //明细表
                List<FinCustomerFundsFreezeBillItem> sortList = itemList.stream().sorted(Comparator.comparing(FinCustomerFundsFreezeBillItem::getItemNum, Comparator.nullsFirst(Long::compareTo)).reversed()).collect(Collectors.toList());
                Long maxNum = sortList.get(0).getItemNum();
                if (maxNum == null || maxNum.equals(0)){
                    maxNum = new Long("0");
                }
                for (FinCustomerFundsFreezeBillItem item : itemList) {
                    item.setFundsFreezeBillSid(sid);
                    if (item.getFundsFreezeBillItemSid() == null){
                        item.setUnfreezeStatus(ConstantsFinance.UNFREEZE_STATUS_WJD);
                    }
                    if (item.getItemNum() == null){
                        maxNum = maxNum + new Long("1");
                        item.setItemNum(maxNum);
                    }
                };
                finCustomerFundsFreezeBillItemMapper.inserts(itemList);
            }
            if (!ConstantsEms.CHECK_STATUS.equals(entity.getHandleStatus())){
                //明细表
                List<FinCustomerFundsFreezeBillItem> sortList = itemList.stream().sorted(Comparator.comparing(FinCustomerFundsFreezeBillItem::getItemNum, Comparator.nullsFirst(Long::compareTo)).reversed()).collect(Collectors.toList());
                Long maxNum = sortList.get(0).getItemNum();
                if (maxNum == null || maxNum.equals(0)){
                    maxNum = new Long("0");
                }
                for (FinCustomerFundsFreezeBillItem item : itemList) {
                    item.setFundsFreezeBillSid(sid);
                    if (item.getFundsFreezeBillItemSid() == null){
                        item.setUnfreezeStatus(ConstantsFinance.UNFREEZE_STATUS_WJD);
                    }
                    if (item.getItemNum() == null){
                        maxNum = maxNum + new Long("1");
                        item.setItemNum(maxNum);
                    }
                };
                finCustomerFundsFreezeBillItemMapper.inserts(itemList);
            }
        }
        //附件表
        if (CollectionUtils.isNotEmpty(atmList)) {
            atmList.forEach(item -> {
                item.setFundsFreezeBillSid(sid);
            });
            finCustomerFundsFreezeBillAttachMapper.inserts(atmList);
        }
    }

    /**
     * 更新暂押来源明细
     *
     * @param itemList
     * @param atmList
     * @param sid
     */
    public void updateChild(FinCustomerFundsFreezeBill entity) {
        Long sid = entity.getFundsFreezeBillSid();
        //明细表
        if (CollectionUtils.isNotEmpty(entity.getItemList())) {
            Set<Long> preFundsFreezeBillSids = new HashSet<>();
            entity.getItemList().forEach(item -> {
                FinCustomerFundsFreezeBillItem fundsFreezeBillItem = new FinCustomerFundsFreezeBillItem();
                fundsFreezeBillItem.setFundsFreezeBillItemSid(item.getPreFundsFreezeBillItemSid());
                //原暂押明细
                fundsFreezeBillItem = finCustomerFundsFreezeBillItemMapper.selectFinCustomerFundsFreezeBillItemById(item.getPreFundsFreezeBillItemSid());
                if (ConstantsEms.CHECK_STATUS.equals(entity.getHandleStatus())){
                    fundsFreezeBillItem.setCurrencyAmountYsf(fundsFreezeBillItem.getCurrencyAmountYsf().add(item.getCurrencyAmount()));
                    if (fundsFreezeBillItem.getCurrencyAmountYsf().compareTo(fundsFreezeBillItem.getCurrencyAmount()) == 0){
                        fundsFreezeBillItem.setUnfreezeStatus(ConstantsFinance.UNFREEZE_STATUS_QBJD);
                    }
                    else {
                        fundsFreezeBillItem.setUnfreezeStatus(ConstantsFinance.UNFREEZE_STATUS_BFJD);
                    }
                }
                if (!ConstantsEms.CHECK_STATUS.equals(entity.getHandleStatus())){
                    fundsFreezeBillItem.setCurrencyAmountSfz(fundsFreezeBillItem.getCurrencyAmountSfz().add(item.getCurrencyAmount()));
                    fundsFreezeBillItem.setUnfreezeStatus(ConstantsFinance.UNFREEZE_STATUS_BFJD);
                }
                finCustomerFundsFreezeBillItemMapper.updateAllById(fundsFreezeBillItem);

                //修改来源主表的状态
                preFundsFreezeBillSids.add(item.getPreFundsFreezeBillSid());
            });
            //
            for (Long preSid : preFundsFreezeBillSids) {
                updatePreUnfreezeStatus(preSid);
            }
        }
    }

    /**
     * 修改来源主表的释放状态
     */
    public void updatePreUnfreezeStatus(Long preFundsFreezeBillSid) {
        //修改来源主表的状态
        //得到来源主表的所有明细
        List<FinCustomerFundsFreezeBillItem> itemList = finCustomerFundsFreezeBillItemMapper.selectList(new QueryWrapper<FinCustomerFundsFreezeBillItem>()
                .lambda().in(FinCustomerFundsFreezeBillItem::getFundsFreezeBillSid, preFundsFreezeBillSid));
        //获得来源主表应有的状态
        String unfreezeStatus = checkStatus(itemList);
        FinCustomerFundsFreezeBill finCustomerFundsFreezeBill = new FinCustomerFundsFreezeBill().setUnfreezeStatus(unfreezeStatus)
                .setFundsFreezeBillSid(preFundsFreezeBillSid);
        finCustomerFundsFreezeBillMapper.updateById(finCustomerFundsFreezeBill);
    }

    /**
     * 作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int invalid(Long fundsFreezeBillSid){
        FinCustomerFundsFreezeBill bill = finCustomerFundsFreezeBillMapper.selectById(fundsFreezeBillSid);
        if (!HandleStatus.CONFIRMED.getCode().equals(bill.getHandleStatus())){
            throw new BaseException("请选择处理状态是“已确认”的单据!");
        }
        if (ConstantsFinance.DOC_TYPE_FREEZE_BZYK.equals(bill.getDocumentType()) && !ConstantsFinance.UNFREEZE_STATUS_WJD.equals(bill.getUnfreezeStatus())){
            throw new BaseException("暂押款已开始释放，无法作废!");
        }
        int i = finCustomerFundsFreezeBillMapper.updateAllById(bill.setHandleStatus(HandleStatus.INVALID.getCode()));
        if (i > 0 && ConstantsFinance.DOC_TYPE_FREEZE_BSF.equals(bill.getDocumentType())){
            List<Long> sids = new ArrayList<>();
            sids.add(fundsFreezeBillSid);
            returnChild(sids);
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbUtil.insertUserLog(bill.getFundsFreezeBillSid(), BusinessType.CANCEL.getValue(), msgList, TITLE);
        return i;
    }
}
