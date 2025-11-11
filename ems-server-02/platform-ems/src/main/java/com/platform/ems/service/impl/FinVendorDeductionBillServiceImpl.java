package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
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
import com.platform.ems.service.IFinVendorDeductionBillService;
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
 * 供应商扣款单Service业务层处理
 *
 * @author qhq
 * @date 2021-05-31
 */
@Service
@SuppressWarnings("all")
public class FinVendorDeductionBillServiceImpl extends ServiceImpl<FinVendorDeductionBillMapper, FinVendorDeductionBill> implements IFinVendorDeductionBillService {
    @Autowired
    private FinVendorDeductionBillMapper finVendorDeductionBillMapper;
    @Autowired
    private FinVendorDeductionBillItemMapper itemMapper;
    @Autowired
    private FinVendorDeductionBillAttachmentMapper atmMapper;
    @Autowired
    private FinBookVendorDeductionMapper bookMapper;
    @Autowired
    private FinBookVendorDeductionItemMapper bookItemMapper;
    @Autowired
    private BasCompanyMapper companyMapper;
    @Autowired
    private BasVendorMapper vendorMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISysTodoTaskService sysTodoTaskService;
    @Autowired
    private SysDefaultSettingClientMapper defaultSettingClientMapper;
    @Autowired
    private WorkFlowServiceImpl workflowService;

    private static final String TITLE = "供应商扣款单";

    /**
     * 查询供应商扣款单
     *
     * @param deductionBillSid 供应商扣款单ID
     * @return 供应商扣款单
     */
    @Override
    public FinVendorDeductionBill selectFinVendorDeductionBillById(Long deductionBillSid) {
        FinVendorDeductionBill finVendorDeductionBill = finVendorDeductionBillMapper.selectFinVendorDeductionBillById(deductionBillSid);
        if (finVendorDeductionBill == null) {
            return new FinVendorDeductionBill();
        }
        FinVendorDeductionBillItem item = new FinVendorDeductionBillItem();
        item.setDeductionBillSid(deductionBillSid);
        List<FinVendorDeductionBillItem> itemList = itemMapper.selectFinVendorDeductionBillItemList(item);
        if (CollectionUtil.isNotEmpty(itemList)) {
            finVendorDeductionBill.setItemList(itemList);
        }
        FinVendorDeductionBillAttachment atm = new FinVendorDeductionBillAttachment();
        atm.setDeductionBillSid(deductionBillSid);
        finVendorDeductionBill.setAttachmentList(new ArrayList<>());
        List<FinVendorDeductionBillAttachment> atmList = atmMapper.selectFinVendorDeductionBillAttachmentList(atm);
        if (CollectionUtil.isNotEmpty(atmList)) {
            finVendorDeductionBill.setAttachmentList(atmList);
        }
        //查询操作日志
        MongodbUtil.find(finVendorDeductionBill);
        return finVendorDeductionBill;
    }

    /**
     * 查询供应商扣款单列表
     *
     * @param finVendorDeductionBill 供应商扣款单
     * @return 供应商扣款单
     */
    @Override
    public List<FinVendorDeductionBill> selectFinVendorDeductionBillList(FinVendorDeductionBill finVendorDeductionBill) {
        List<FinVendorDeductionBill> response = finVendorDeductionBillMapper.selectFinVendorDeductionBillList(finVendorDeductionBill);
        return response;
    }

    /**
     * 新增供应商扣款单
     * 需要注意编码重复校验
     *
     * @param finVendorDeductionBill 供应商扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinVendorDeductionBill(FinVendorDeductionBill finVendorDeductionBill) {
        judgePrice(finVendorDeductionBill.getItemList());
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finVendorDeductionBill.getHandleStatus())) {
            SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
            if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowGyskk())) {
                // 不需要走审批，提交及确认
                finVendorDeductionBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        //设置确认信息
        confirmedInfo(finVendorDeductionBill);
        finVendorDeductionBill.setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN);
        int row = finVendorDeductionBillMapper.insert(finVendorDeductionBill);
        if (row > 0) {
            // 找到code
            FinVendorDeductionBill bill = finVendorDeductionBillMapper.selectById(finVendorDeductionBill.getDeductionBillSid());
            finVendorDeductionBill.setDeductionBillCode(bill.getDeductionBillCode());
            //新增明细表，附件表
            insertChild(finVendorDeductionBill.getItemList(), finVendorDeductionBill.getAttachmentList(), finVendorDeductionBill.getDeductionBillSid());
            //新增流水报表
            insertBookAccount(finVendorDeductionBill);
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(finVendorDeductionBill.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_FIN_VENDOR_DEDUCTION_BILL)
                        .setDocumentSid(finVendorDeductionBill.getDeductionBillSid());
                sysTodoTask.setTitle("供应商扣款单: " + finVendorDeductionBill.getDeductionBillCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(finVendorDeductionBill.getDeductionBillCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskService.insertSysTodoTaskMenu(sysTodoTask, ConstantsWorkbench.TODO_FIN_VEN_DEDUC_INFO);
            }
            // 走提交审批，参数从查询页面提交按钮参考
            if (ConstantsEms.SUBMIT_STATUS.equals(finVendorDeductionBill.getHandleStatus())) {
                this.submit(finVendorDeductionBill);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(finVendorDeductionBill.getDeductionBillSid(), finVendorDeductionBill.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改供应商扣款单
     *
     * @param finVendorDeductionBill 供应商扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinVendorDeductionBill(FinVendorDeductionBill finVendorDeductionBill) {
        judgePrice(finVendorDeductionBill.getItemList());
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finVendorDeductionBill.getHandleStatus())) {
            SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
            if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowGyskk())) {
                // 不需要走审批，提交及确认
                finVendorDeductionBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        //设置确认信息
        confirmedInfo(finVendorDeductionBill);
        FinVendorDeductionBill old = finVendorDeductionBillMapper.selectFinVendorDeductionBillById(finVendorDeductionBill.getDeductionBillSid());
        int row = finVendorDeductionBillMapper.updateAllById(finVendorDeductionBill);
        if (row > 0) {
            deleteItem(finVendorDeductionBill.getDeductionBillSid());
            insertChild(finVendorDeductionBill.getItemList(), finVendorDeductionBill.getAttachmentList(), finVendorDeductionBill.getDeductionBillSid());
            insertBookAccount(finVendorDeductionBill);
            //不是保存状态时删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(finVendorDeductionBill.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, finVendorDeductionBill.getDeductionBillSid()));
            }
            // 走提交审批，参数从查询页面提交按钮参考
            if (ConstantsEms.SUBMIT_STATUS.equals(finVendorDeductionBill.getHandleStatus())) {
                this.submit(finVendorDeductionBill);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(old, finVendorDeductionBill);
            MongodbDeal.update(finVendorDeductionBill.getDeductionBillSid(), old.getHandleStatus(), finVendorDeductionBill.getHandleStatus(), msgList, TITLE,null);
        }
        return row;
    }

    /**
     * 变更供应商扣款单
     *
     * @param finVendorDeductionBill 供应商扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinVendorDeductionBill(FinVendorDeductionBill finVendorDeductionBill) {
        judgePrice(finVendorDeductionBill.getItemList());
        confirmedInfo(finVendorDeductionBill);
        FinVendorDeductionBill old = finVendorDeductionBillMapper.selectFinVendorDeductionBillById(finVendorDeductionBill.getDeductionBillSid());
        int row = finVendorDeductionBillMapper.updateAllById(finVendorDeductionBill);
        if (row > 0) {
            deleteItem(finVendorDeductionBill.getDeductionBillSid());
            insertChild(finVendorDeductionBill.getItemList(), finVendorDeductionBill.getAttachmentList(), finVendorDeductionBill.getDeductionBillSid());
            //生成流水账
            insertBookAccount(finVendorDeductionBill);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(old, finVendorDeductionBill);
            MongodbUtil.insertUserLog(finVendorDeductionBill.getDeductionBillSid(), BusinessType.CHANGE.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商扣款单
     *
     * @param deductionBillSids 需要删除的供应商扣款单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinVendorDeductionBillByIds(List<Long> deductionBillSids) {
        if (CollectionUtils.isEmpty(deductionBillSids)){
            throw new BaseException("请选择行！");
        }
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(HandleStatus.SAVE.getCode());
        handleStatusList.add(HandleStatus.RETURNED.getCode());
        List<FinVendorDeductionBill> finVendorDeductionBillList = finVendorDeductionBillMapper.selectList(new QueryWrapper<FinVendorDeductionBill>().lambda()
                .in(FinVendorDeductionBill::getDeductionBillSid,deductionBillSids)
                .notIn(FinVendorDeductionBill::getHandleStatus, handleStatusList));
        if (CollectionUtils.isNotEmpty(finVendorDeductionBillList)){
            throw new BaseException("仅保存状态和退回状态允许删除操作！");
        }
        int i = finVendorDeductionBillMapper.deleteBatchIds(deductionBillSids);
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
    public int check(FinVendorDeductionBill finVendorDeductionBill) {
        int row = 0;
        Long[] sids = finVendorDeductionBill.getDeductionBillSidList();
        if (sids != null && sids.length > 0) {
            Map<Long, List<FinVendorDeductionBillItem>> itemMap = new HashMap<>();
            for (Long sid : sids) {
                List<FinVendorDeductionBillItem> itemList = itemMapper.selectList(new QueryWrapper<FinVendorDeductionBillItem>()
                        .lambda().eq(FinVendorDeductionBillItem::getDeductionBillSid, sid));
                if (CollectionUtils.isEmpty(itemList)){
                    throw new CustomException("明细不能为空!");
                }
                itemMap.put(sid, itemList);
            }
            // 判断是否要走审批
            if (BusinessType.SUBMIT.getValue().equals(finVendorDeductionBill.getOperateType())) {
                SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                        .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
                if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowGyskk())) {
                    // 不需要走审批，提交及确认
                    LambdaUpdateWrapper<FinVendorDeductionBill> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.in(FinVendorDeductionBill::getDeductionBillSid,sids)
                            .set(FinVendorDeductionBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                            .set(FinVendorDeductionBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                            .set(FinVendorDeductionBill::getConfirmDate, new Date());
                    row = finVendorDeductionBillMapper.update(null, updateWrapper);
                    for (Long id : sids) {
                        //插入日志
                        MongodbDeal.check(id,  ConstantsEms.CHECK_STATUS, null, TITLE, null);
                        // 确认后操作
                        FinVendorDeductionBill bill = finVendorDeductionBillMapper.selectById(id);
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
            row = workFlow(finVendorDeductionBill);
        }
        return row;
    }

    /**
     * 提交
     */
    private void submit(FinVendorDeductionBill finVendorDeductionBill){
        Map<String, Object> variables = new HashMap<>();
        variables.put("formCode", finVendorDeductionBill.getDeductionBillCode());
        variables.put("formId", finVendorDeductionBill.getDeductionBillSid());
        variables.put("formType", FormType.VendorDeductionBill.getCode());
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
        LambdaUpdateWrapper<FinVendorDeductionBill> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinVendorDeductionBill::getDeductionBillSid, sids);
        updateWrapper.set(FinVendorDeductionBill::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(FinVendorDeductionBill::getConfirmDate, new Date());
            updateWrapper.set(FinVendorDeductionBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return finVendorDeductionBillMapper.update(null, updateWrapper);
    }

    /**
     * 工作流流程
     */
    public int workFlow(FinVendorDeductionBill finVendorDeductionBill) {
        int row = 1;
        Long[] sids = finVendorDeductionBill.getDeductionBillSidList();
        // 处理状态
        String handleStatus = finVendorDeductionBill.getHandleStatus();
        if (StrUtil.isNotBlank(finVendorDeductionBill.getOperateType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(finVendorDeductionBill.getOperateType());
        }
        if (sids != null && sids.length > 0) {
            // 获取数据
            List<FinVendorDeductionBill> billList = finVendorDeductionBillMapper.selectFinVendorDeductionBillList
                    (new FinVendorDeductionBill().setDeductionBillSidList(sids));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus,
                        ConstantsTable.TABLE_FIN_VENDOR_DEDUCTION_BILL);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(finVendorDeductionBill.getOperateType())) {
                // 修改处理状态
                row = this.updateHandle(sids, ConstantsEms.SUBMIT_STATUS);
                // 开启工作流
                for (int i = 0; i < billList.size(); i++) {
                    this.submit(billList.get(i));
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getDeductionBillSid(),
                            BusinessType.SUBMIT.getValue(), null, TITLE, finVendorDeductionBill.getComment());
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(finVendorDeductionBill.getOperateType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                for (int i = 0; i < billList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(billList.get(i).getDeductionBillSid().toString());
                    taskVo.setFormId(Long.valueOf(billList.get(i).getDeductionBillSid().toString()));
                    taskVo.setFormCode(billList.get(i).getDeductionBillCode().toString());
                    taskVo.setFormType(FormType.VendorDeductionBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finVendorDeductionBill.getComment());
                    try {
                        SysFormProcess process = workflowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getDeductionBillSid()}, ConstantsEms.CHECK_STATUS);
                            // 确认后操作
                            List<FinVendorDeductionBillItem> itemList = itemMapper.selectList(new QueryWrapper<FinVendorDeductionBillItem>()
                                    .lambda().eq(FinVendorDeductionBillItem::getDeductionBillSid, billList.get(i).getDeductionBillSid()));
                            FinVendorDeductionBill bill = billList.get(i);
                            bill.setHandleStatus(ConstantsEms.CHECK_STATUS);
                            bill.setItemList(itemList);
                            this.insertBookAccount(bill);
                        }
                        finVendorDeductionBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getDeductionBillSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, finVendorDeductionBill.getComment());
                }
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(finVendorDeductionBill.getOperateType())) {
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
                    taskVo.setFormType(FormType.VendorDeductionBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finVendorDeductionBill.getComment());
                    try {
                        SysFormProcess process = workflowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getDeductionBillSid()}, HandleStatus.RETURNED.getCode());
                        }
                        finVendorDeductionBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getDeductionBillSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, finVendorDeductionBill.getComment());
                }
            }
        }
        return row;
    }

    /**
     * 校验明细金额只能输入正式要么输入负数
     *
     * @param List<FinVendorDeductionBillItem>
     * @return
     */
    private void judgePrice(List<FinVendorDeductionBillItem> itemList) {
        int flag = 0;
        for (FinVendorDeductionBillItem item : itemList) {
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
    public FinVendorDeductionBill confirmedInfo(FinVendorDeductionBill entity) {
        if (entity.getItemList() == null){
            throw new CustomException("明细不能为空!");
        }
        if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
            //确认人，确认日期
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
        entity.setVendorCode(null);
        if (entity.getVendorSid() != null) {
            BasVendor vendor = vendorMapper.selectById(entity.getVendorSid());
            if (vendor != null) {
                entity.setVendorCode(String.valueOf(vendor.getVendorCode()));
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
    public void insertBookAccount(FinVendorDeductionBill entity) {
        if (CollectionUtils.isNotEmpty(entity.getItemList()) && entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
            //确认后需要添加一笔流水
            FinVendorDeductionBill bill = entity;
            FinBookVendorDeduction book = new FinBookVendorDeduction();
            BeanCopyUtils.copyProperties(bill, book);
            Calendar cal = Calendar.getInstance();
            //年份月份
            book.setPaymentMonth(Long.valueOf(cal.get(Calendar.MONTH) + 1)).setPaymentYear(Long.valueOf(cal.get(Calendar.YEAR)));
            //流水类型，流水来源类别
            book.setBookType(ConstantsFinance.BOOK_TYPE_VKK).setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_VKKD);
            bookMapper.insert(book);
            //得到扣款明细表列表
            List<FinVendorDeductionBillItem> billItemList = bill.getItemList();
            //生成一笔扣款流水明细
            FinBookVendorDeductionItem bookItem = new FinBookVendorDeductionItem();
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
    public void insertChild(List<FinVendorDeductionBillItem> itemList, List<FinVendorDeductionBillAttachment> atmList, Long sid) {
        //明细表
        if (CollectionUtils.isNotEmpty(itemList)) {
            int i = 1;
            for (FinVendorDeductionBillItem finVendorDeductionBillItem : itemList) {
                finVendorDeductionBillItem.setDeductionBillSid(sid);
                finVendorDeductionBillItem.setItemNum((long)i++);
                itemMapper.insert(finVendorDeductionBillItem);
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
        QueryWrapper<FinVendorDeductionBillItem> itemwrapper = new QueryWrapper<>();
        itemwrapper.eq("deduction_bill_sid", sid);
        itemMapper.delete(itemwrapper);
        //附件表
        QueryWrapper<FinVendorDeductionBillAttachment> atmWrapper = new QueryWrapper<>();
        atmWrapper.eq("deduction_bill_sid", sid);
        atmMapper.delete(atmWrapper);
        //流水账
        FinBookVendorDeductionItem finBookVendorDeductionItem = bookItemMapper.selectOne(new QueryWrapper<FinBookVendorDeductionItem>().lambda().eq(FinBookVendorDeductionItem::getReferDocSid, sid));
        if (finBookVendorDeductionItem != null){
            bookMapper.deleteById(finBookVendorDeductionItem.getBookDeductionSid());
            bookItemMapper.deleteById(finBookVendorDeductionItem.getBookDeductionItemSid());
        }
    }

    @Override
    public int invalid(Long deductionBillSid){
        FinVendorDeductionBill bill = finVendorDeductionBillMapper.selectById(deductionBillSid);
        if (!HandleStatus.CONFIRMED.getCode().equals(bill.getHandleStatus())){
            throw new BaseException("请选择处理状态是“已确认”的单据!");
        }
        List<FinBookVendorDeduction> bookList = bookMapper.getReportForm(new FinBookVendorDeduction().setReferDocSid(bill.getDeductionBillSid()));
        bookList.forEach(item->{
            if (!ConstantsFinance.CLEAR_STATUS_WHX.equals(item.getClearStatus())){
                throw new BaseException("对应的财务流水已开始核销，无法作废!");
            }
            item.setHandleStatus(HandleStatus.INVALID.getCode());
            bookMapper.updateById(item);
        });
        int i = finVendorDeductionBillMapper.updateAllById(bill.setHandleStatus(HandleStatus.INVALID.getCode()));
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbUtil.insertUserLog(bill.getDeductionBillSid(), BusinessType.CANCEL.getValue(), msgList, TITLE);
        return i;
    }
}
