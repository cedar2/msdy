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
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.constant.*;
import com.platform.ems.domain.*;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IFinCustomerDeductionBillService;
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

/**
 * 客户扣款单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-08
 */
@Service
@SuppressWarnings("all")
public class FinCustomerDeductionBillServiceImpl extends ServiceImpl<FinCustomerDeductionBillMapper, FinCustomerDeductionBill> implements IFinCustomerDeductionBillService {
    @Autowired
    private FinCustomerDeductionBillMapper finCustomerDeductionBillMapper;
    @Autowired
    private FinCustomerDeductionBillItemMapper itemMapper;
    @Autowired
    private FinCustomerDeductionBillAttachmentMapper atmMapper;
    @Autowired
    private FinBookCustomerDeductionMapper bookMapper;
    @Autowired
    private FinBookCustomerDeductionItemMapper bookItemMapper;
    @Autowired
    private BasCompanyMapper companyMapper;
    @Autowired
    private BasCustomerMapper customerMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISysTodoTaskService sysTodoTaskService;
    @Autowired
    private SysDefaultSettingClientMapper defaultSettingClientMapper;
    @Autowired
    private WorkFlowServiceImpl workflowService;

    private static final String TITLE = "客户扣款单";

    /**
     * 查询客户扣款单
     *
     * @param deductionBillSid 客户扣款单ID
     * @return 客户扣款单
     */
    @Override
    public FinCustomerDeductionBill selectFinCustomerDeductionBillById(Long deductionBillSid) {
        FinCustomerDeductionBill finCustomerDeductionBill = finCustomerDeductionBillMapper.selectFinCustomerDeductionBillById(deductionBillSid);
        if (finCustomerDeductionBill == null) {
            return new FinCustomerDeductionBill();
        }
        FinCustomerDeductionBillItem item = new FinCustomerDeductionBillItem();
        item.setDeductionBillSid(deductionBillSid);
        List<FinCustomerDeductionBillItem> itemList = itemMapper.selectFinCustomerDeductionBillItemList(item);
        if (CollectionUtils.isNotEmpty(itemList)) {
            finCustomerDeductionBill.setItemList(itemList);
        }
        FinCustomerDeductionBillAttachment atm = new FinCustomerDeductionBillAttachment();
        atm.setDeductionBillSid(deductionBillSid);
        finCustomerDeductionBill.setAttachmentList(new ArrayList<>());
        List<FinCustomerDeductionBillAttachment> atmList = atmMapper.selectFinCustomerDeductionBillAttachmentList(atm);
        if (CollectionUtils.isNotEmpty(atmList)) {
            finCustomerDeductionBill.setAttachmentList(atmList);
        }
        //查询操作日志
        MongodbUtil.find(finCustomerDeductionBill);
        return finCustomerDeductionBill;
    }

    /**
     * 查询客户扣款单列表
     *
     * @param finCustomerDeductionBill 客户扣款单
     * @return 客户扣款单
     */
    @Override
    public List<FinCustomerDeductionBill> selectFinCustomerDeductionBillList(FinCustomerDeductionBill finCustomerDeductionBill) {
        List<FinCustomerDeductionBill> response = finCustomerDeductionBillMapper.selectFinCustomerDeductionBillList(finCustomerDeductionBill);
        return response;
    }

    /**
     * 新增客户扣款单
     * 需要注意编码重复校验
     *
     * @param finCustomerDeductionBill 客户扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinCustomerDeductionBill(FinCustomerDeductionBill finCustomerDeductionBill) {
        judgePrice(finCustomerDeductionBill.getItemList());
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finCustomerDeductionBill.getHandleStatus())) {
            SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
            if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowKhkk())) {
                // 不需要走审批，提交及确认
                finCustomerDeductionBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        //设置确认信息
        confirmedInfo(finCustomerDeductionBill);
        finCustomerDeductionBill.setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN);
        int row = finCustomerDeductionBillMapper.insert(finCustomerDeductionBill);
        if (row > 0) {
            // 找到code
            FinCustomerDeductionBill bill = finCustomerDeductionBillMapper.selectById(finCustomerDeductionBill.getDeductionBillSid());
            finCustomerDeductionBill.setDeductionBillCode(bill.getDeductionBillCode());
            //新增明细表，附件表
            insertChild(finCustomerDeductionBill.getItemList(), finCustomerDeductionBill.getAttachmentList(), finCustomerDeductionBill.getDeductionBillSid());
            //新增流水报表
            insertBookAccount(finCustomerDeductionBill);
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(finCustomerDeductionBill.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_FIN_CUSTOEMR_DEDUCTION_BILL)
                        .setDocumentSid(finCustomerDeductionBill.getDeductionBillSid());
                sysTodoTask.setTitle("客户扣款单: " + finCustomerDeductionBill.getDeductionBillCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(finCustomerDeductionBill.getDeductionBillCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskService.insertSysTodoTaskMenu(sysTodoTask, ConstantsWorkbench.TODO_FIN_CUS_DEDUC_INFO);
            }
            // 走提交审批，参数从查询页面提交按钮参考
            if (ConstantsEms.SUBMIT_STATUS.equals(finCustomerDeductionBill.getHandleStatus())) {
                this.submit(finCustomerDeductionBill);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(finCustomerDeductionBill.getDeductionBillSid(), finCustomerDeductionBill.getHandleStatus(), msgList, TITLE,null);
        }
        return row;
    }

    /**
     * 修改客户扣款单
     *
     * @param finCustomerDeductionBill 客户扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinCustomerDeductionBill(FinCustomerDeductionBill finCustomerDeductionBill) {
        judgePrice(finCustomerDeductionBill.getItemList());
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finCustomerDeductionBill.getHandleStatus())) {
            SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
            if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowKhkk())) {
                // 不需要走审批，提交及确认
                finCustomerDeductionBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        //设置确认信息
        confirmedInfo(finCustomerDeductionBill);
        FinCustomerDeductionBill old = finCustomerDeductionBillMapper.selectFinCustomerDeductionBillById(finCustomerDeductionBill.getDeductionBillSid());
        int row = finCustomerDeductionBillMapper.updateAllById(finCustomerDeductionBill);
        if (row > 0) {
            deleteItem(finCustomerDeductionBill.getDeductionBillSid());
            insertChild(finCustomerDeductionBill.getItemList(), finCustomerDeductionBill.getAttachmentList(), finCustomerDeductionBill.getDeductionBillSid());
            insertBookAccount(finCustomerDeductionBill);
            //不是保存状态时删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(finCustomerDeductionBill.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, finCustomerDeductionBill.getDeductionBillSid()));
            }
            // 走提交审批，参数从查询页面提交按钮参考
            if (ConstantsEms.SUBMIT_STATUS.equals(finCustomerDeductionBill.getHandleStatus())) {
                this.submit(finCustomerDeductionBill);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(old, finCustomerDeductionBill);
            MongodbDeal.update(finCustomerDeductionBill.getDeductionBillSid(), old.getHandleStatus(), finCustomerDeductionBill.getHandleStatus(), msgList, TITLE,null);
        }
        return row;
    }

    /**
     * 变更客户扣款单
     *
     * @param finCustomerDeductionBill 客户扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinCustomerDeductionBill(FinCustomerDeductionBill finCustomerDeductionBill) {
        judgePrice(finCustomerDeductionBill.getItemList());
        //设置确认信息
        confirmedInfo(finCustomerDeductionBill);
        FinCustomerDeductionBill old = finCustomerDeductionBillMapper.selectFinCustomerDeductionBillById(finCustomerDeductionBill.getDeductionBillSid());
        int row = finCustomerDeductionBillMapper.updateAllById(finCustomerDeductionBill);
        if (row > 0) {
            deleteItem(finCustomerDeductionBill.getDeductionBillSid());
            insertChild(finCustomerDeductionBill.getItemList(), finCustomerDeductionBill.getAttachmentList(), finCustomerDeductionBill.getDeductionBillSid());
            //生成流水账
            insertBookAccount(finCustomerDeductionBill);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(old, finCustomerDeductionBill);
            MongodbUtil.insertUserLog(finCustomerDeductionBill.getDeductionBillSid(), BusinessType.CHANGE.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 批量删除客户扣款单
     *
     * @param deductionBillSids 需要删除的客户扣款单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinCustomerDeductionBillByIds(List<Long> deductionBillSids) {
        if (CollectionUtils.isEmpty(deductionBillSids)){
            throw new BaseException("请选择行！");
        }
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(HandleStatus.SAVE.getCode());
        handleStatusList.add(HandleStatus.RETURNED.getCode());
        List<FinCustomerDeductionBill> finCustomerDeductionBillList = finCustomerDeductionBillMapper.selectList(new QueryWrapper<FinCustomerDeductionBill>().lambda()
                .in(FinCustomerDeductionBill::getDeductionBillSid,deductionBillSids)
                .notIn(FinCustomerDeductionBill::getHandleStatus, handleStatusList));
        if (CollectionUtils.isNotEmpty(finCustomerDeductionBillList)){
            throw new BaseException("仅保存状态和退回状态允许删除操作！");
        }
        int i = finCustomerDeductionBillMapper.deleteBatchIds(deductionBillSids);
        if (i > 0) {
            deductionBillSids.forEach(sid -> {
                //删除明细
                deleteItem(sid);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
            });
            //删除待办
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, deductionBillSids));
        }
        return i;
    }

    /**
     * 更改确认状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FinCustomerDeductionBill finCustomerDeductionBill) {
        int row = 0;
        Long[] sids = finCustomerDeductionBill.getDeductionBillSidList();
        if (sids != null && sids.length > 0) {
            Map<Long, List<FinCustomerDeductionBillItem>> itemMap = new HashMap<>();
            for (Long sid : sids) {
                List<FinCustomerDeductionBillItem> itemList = itemMapper.selectList(new QueryWrapper<FinCustomerDeductionBillItem>()
                        .lambda().eq(FinCustomerDeductionBillItem::getDeductionBillSid, sid));
                if (CollectionUtils.isEmpty(itemList)){
                    throw new CustomException("明细不能为空!");
                }
                itemMap.put(sid, itemList);
            }
            // 判断是否要走审批
            if (BusinessType.SUBMIT.getValue().equals(finCustomerDeductionBill.getOperateType())) {
                SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                        .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
                if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowKhkk())) {
                    // 不需要走审批，提交及确认
                    LambdaUpdateWrapper<FinCustomerDeductionBill> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.in(FinCustomerDeductionBill::getDeductionBillSid,sids)
                            .set(FinCustomerDeductionBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                            .set(FinCustomerDeductionBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                            .set(FinCustomerDeductionBill::getConfirmDate, new Date());
                    row = finCustomerDeductionBillMapper.update(null, updateWrapper);
                    for (Long id : sids) {
                        //插入日志
                        MongodbDeal.check(id,  ConstantsEms.CHECK_STATUS, null, TITLE, null);
                        // 确认后操作
                        FinCustomerDeductionBill bill = finCustomerDeductionBillMapper.selectById(id);
                        bill.setItemList(itemMap.get(id));
                        this.insertBookAccount(bill);
                    }
                    //删除待办
                    sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                            .in(SysTodoTask::getDocumentSid,sids));
                    return row;
                }
            }
            // 走工作流程
            row = workFlow(finCustomerDeductionBill);
        }
        return row;
    }

    /**
     * 提交
     */
    private void submit(FinCustomerDeductionBill finCustomerDeductionBill){
        Map<String, Object> variables = new HashMap<>();
        variables.put("formCode", finCustomerDeductionBill.getDeductionBillCode());
        variables.put("formId", finCustomerDeductionBill.getDeductionBillSid());
        variables.put("formType", FormType.CustomerDeductionBill.getCode());
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
        LambdaUpdateWrapper<FinCustomerDeductionBill> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinCustomerDeductionBill::getDeductionBillSid, sids);
        updateWrapper.set(FinCustomerDeductionBill::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(FinCustomerDeductionBill::getConfirmDate, new Date());
            updateWrapper.set(FinCustomerDeductionBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return finCustomerDeductionBillMapper.update(null, updateWrapper);
    }

    /**
     * 工作流流程
     */
    public int workFlow(FinCustomerDeductionBill finCustomerDeductionBill) {
        int row = 1;
        Long[] sids = finCustomerDeductionBill.getDeductionBillSidList();
        // 处理状态
        String handleStatus = finCustomerDeductionBill.getHandleStatus();
        if (StrUtil.isNotBlank(finCustomerDeductionBill.getOperateType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(finCustomerDeductionBill.getOperateType());
        }
        if (sids != null && sids.length > 0) {
            // 获取数据
            List<FinCustomerDeductionBill> billList = finCustomerDeductionBillMapper.selectFinCustomerDeductionBillList
                    (new FinCustomerDeductionBill().setDeductionBillSidList(sids));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus,
                        ConstantsTable.TABLE_FIN_CUSTOEMR_DEDUCTION_BILL);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(finCustomerDeductionBill.getOperateType())) {
                // 修改处理状态
                row = this.updateHandle(sids, ConstantsEms.SUBMIT_STATUS);
                // 开启工作流
                for (int i = 0; i < billList.size(); i++) {
                    this.submit(billList.get(i));
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getDeductionBillSid(),
                            BusinessType.SUBMIT.getValue(), null, TITLE, finCustomerDeductionBill.getComment());
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(finCustomerDeductionBill.getOperateType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                for (int i = 0; i < billList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(billList.get(i).getDeductionBillSid().toString());
                    taskVo.setFormId(Long.valueOf(billList.get(i).getDeductionBillSid().toString()));
                    taskVo.setFormCode(billList.get(i).getDeductionBillCode().toString());
                    taskVo.setFormType(FormType.CustomerDeductionBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finCustomerDeductionBill.getComment());
                    try {
                        SysFormProcess process = workflowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getDeductionBillSid()}, ConstantsEms.CHECK_STATUS);
                            // 确认后操作
                            List<FinCustomerDeductionBillItem> itemList = itemMapper.selectList(new QueryWrapper<FinCustomerDeductionBillItem>()
                                    .lambda().eq(FinCustomerDeductionBillItem::getDeductionBillSid, billList.get(i).getDeductionBillSid()));
                            FinCustomerDeductionBill bill = billList.get(i);
                            bill.setHandleStatus(ConstantsEms.CHECK_STATUS);
                            bill.setItemList(itemList);
                            this.insertBookAccount(bill);
                        }
                        finCustomerDeductionBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getDeductionBillSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, finCustomerDeductionBill.getComment());
                }
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(finCustomerDeductionBill.getOperateType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                // 审批意见
                String comment = "";
                for (int i = 0; i < billList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setTargetKey("2");
                    taskVo.setBusinessKey(billList.get(i).getDeductionBillSid().toString());
                    taskVo.setFormId(Long.valueOf(billList.get(i).getDeductionBillSid().toString()));
                    taskVo.setFormCode(billList.get(i).getDeductionBillCode().toString());
                    taskVo.setFormType(FormType.CustomerDeductionBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finCustomerDeductionBill.getComment());
                    try {
                        SysFormProcess process = workflowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getDeductionBillSid()}, HandleStatus.RETURNED.getCode());
                        }
                        finCustomerDeductionBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getDeductionBillSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, finCustomerDeductionBill.getComment());
                }
            }
        }
        return row;
    }

    /**
     * 校验明细金额只能输入正式要么输入负数
     *
     * @param List<FinCustomerDeductionBillItem>
     * @return
     */
    private void judgePrice(List<FinCustomerDeductionBillItem> itemList) {
        int flag = 0;
        for (FinCustomerDeductionBillItem item : itemList) {
            if (BigDecimal.ZERO.compareTo(item.getCurrencyAmountTax()) == 0){
                throw new BaseException("扣款单明细中的扣款金额(含税)不能输入 0！");
            }
            if (BigDecimal.ZERO.compareTo(item.getCurrencyAmountTax()) > 0){
                flag -= 1;
            }
            else {
                flag += 1;
            }
            if (flag == 0){
                throw new BaseException("同一笔扣款单，扣款金额不能同时存在正负数");
            }
        }
    }

    /**
     * 设置确认信息
     *
     * @param entity
     * @return
     */
    public FinCustomerDeductionBill confirmedInfo(FinCustomerDeductionBill entity) {
        if (entity.getItemList() == null){
            throw new CustomException("明细不能为空");
        }
        if (HandleStatus.CONFIRMED.getCode().equals(entity.getHandleStatus())) {
            entity.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            entity.setConfirmDate(new Date());
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
     * 生成流水账
     *
     * @param entity
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBookAccount(FinCustomerDeductionBill entity) {
        if (CollectionUtils.isNotEmpty(entity.getItemList()) && entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
            //确认后需要添加一笔流水
            FinCustomerDeductionBill bill = entity;
            FinBookCustomerDeduction book = new FinBookCustomerDeduction();
            BeanCopyUtils.copyProperties(bill, book);
            Calendar cal = Calendar.getInstance();
            //年份月份
            book.setPaymentMonth(cal.get(Calendar.MONTH) + 1).setPaymentYear(cal.get(Calendar.YEAR));
            //流水类型，流水来源类别
            book.setBookType(ConstantsFinance.BOOK_TYPE_CKK).setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_CKKD);
            bookMapper.insert(book);
            //得到扣款明细表列表
            List<FinCustomerDeductionBillItem> billItemList = bill.getItemList();
            //生成一笔扣款流水明细
            FinBookCustomerDeductionItem bookItem = new FinBookCustomerDeductionItem();
            BeanCopyUtils.copyProperties(book, bookItem);
            bookItem.setBookDeductionSid(book.getBookDeductionSid()).setItemNum((long)1)
                    .setReferDocSid(bill.getDeductionBillSid()).setReferDocCode(bill.getDeductionBillCode());
            bookItem.setCurrencyAmountTaxKk(BigDecimal.ZERO).setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX)
                    .setCurrencyAmountTaxYhx(BigDecimal.ZERO).setCurrencyAmountTaxHxz(BigDecimal.ZERO);
            //扣款明细中的扣款金额相加写入流水明细的扣款金额
            billItemList.forEach(item -> {
                bookItem.setCurrencyAmountTaxKk(bookItem.getCurrencyAmountTaxKk().add(item.getCurrencyAmountTax()));
            });
            bookItemMapper.insert(bookItem);
        }
    }

    /**
     * 添加子表
     *
     * @param itemList
     * @param atmList
     * @param sid
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertChild(List<FinCustomerDeductionBillItem> itemList, List<FinCustomerDeductionBillAttachment> atmList, Long sid) {
        //明细表
        if (CollectionUtils.isNotEmpty(itemList)) {
            int i = 1;
            for (FinCustomerDeductionBillItem finCustomerDeductionBillItem : itemList) {
                finCustomerDeductionBillItem.setDeductionBillSid(sid);
                finCustomerDeductionBillItem.setItemNum((long)i++);
                itemMapper.insert(finCustomerDeductionBillItem);
            }
        }
        //附件表
        if (CollectionUtils.isNotEmpty(atmList)) {
            atmList.forEach(o -> {
                o.setDeductionBillSid(sid);
            });
            atmMapper.inserts(atmList);
        }
    }

    /**
     * 删除子表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteItem(Long sid) {
        //明细表
        QueryWrapper<FinCustomerDeductionBillItem> itemwrapper = new QueryWrapper<>();
        itemwrapper.eq("deduction_bill_sid", sid);
        itemMapper.delete(itemwrapper);
        //附加表
        QueryWrapper<FinCustomerDeductionBillAttachment> atmWrapper = new QueryWrapper<>();
        atmWrapper.eq("deduction_bill_sid", sid);
        atmMapper.delete(atmWrapper);
        //删除流水账
        FinBookCustomerDeductionItem finBookCustomerDeductionItem = bookItemMapper.selectOne(new QueryWrapper<FinBookCustomerDeductionItem>().lambda().eq(FinBookCustomerDeductionItem::getReferDocSid, sid));
        if (finBookCustomerDeductionItem != null){
            bookMapper.deleteById(finBookCustomerDeductionItem.getBookDeductionSid());
            bookItemMapper.deleteById(finBookCustomerDeductionItem.getBookDeductionItemSid());
        }
    }

    @Override
    public int invalid(Long deductionBillSid){
        FinCustomerDeductionBill bill = finCustomerDeductionBillMapper.selectById(deductionBillSid);
        if (!HandleStatus.CONFIRMED.getCode().equals(bill.getHandleStatus())){
            throw new BaseException("请选择处理状态是“已确认”的单据!");
        }
        List<FinBookCustomerDeduction> bookList = bookMapper.getReportForm(new FinBookCustomerDeduction().setReferDocSid(bill.getDeductionBillSid()));
        bookList.forEach(item->{
            if (!ConstantsFinance.CLEAR_STATUS_WHX.equals(item.getClearStatus())){
                throw new BaseException("对应的财务流水已开始核销，无法作废!");
            }
            item.setHandleStatus(HandleStatus.INVALID.getCode());
            bookMapper.updateById(item);
        });
        int i = finCustomerDeductionBillMapper.updateAllById(bill.setHandleStatus(HandleStatus.INVALID.getCode()));
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbUtil.insertUserLog(bill.getDeductionBillSid(), BusinessType.CANCEL.getValue(), msgList, TITLE);
        return i;
    }
}
