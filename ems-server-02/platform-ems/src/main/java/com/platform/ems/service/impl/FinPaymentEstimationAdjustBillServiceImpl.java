package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.entity.SysUser;
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
import com.platform.ems.service.IFinPaymentEstimationAdjustBillService;
import com.platform.ems.service.ISysTodoTaskService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.data.BigDecimalSum;
import com.platform.ems.workflow.service.impl.WorkFlowServiceImpl;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 应付暂估调价量单Service业务层处理
 *
 * @author chenkw
 * @date 2022-01-10
 */
@Service
@SuppressWarnings("all")
public class FinPaymentEstimationAdjustBillServiceImpl extends ServiceImpl<FinPaymentEstimationAdjustBillMapper, FinPaymentEstimationAdjustBill> implements IFinPaymentEstimationAdjustBillService {
    @Autowired
    private FinPaymentEstimationAdjustBillMapper finPaymentEstimationAdjustBillMapper;
    @Autowired
    private FinPaymentEstimationAdjustBillItemMapper finPaymentEstimationAdjustBillItemMapper;
    @Autowired
    private FinPaymentEstimationAdjustBillAttachMapper finPaymentEstimationAdjustBillAttachMapper;
    @Autowired
    private FinBookPaymentEstimationItemMapper finBookPaymentEstimationItemMapper;
    @Autowired
    private BasCompanyMapper companyMapper;
    @Autowired
    private BasVendorMapper vendorMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SysDefaultSettingClientMapper defaultSettingClientMapper;
    @Autowired
    private ISystemUserService userService;
    @Autowired
    private ISysTodoTaskService sysTodoTaskService;
    @Autowired
    private WorkFlowServiceImpl workflowService;

    private static final String TITLE = "应付暂估调价量单";

    /**
     * 查询应付暂估调价量单
     *
     * @param paymentEstimationAdjustBillSid 应付暂估调价量单ID
     * @return 应付暂估调价量单
     */
    @Override
    public FinPaymentEstimationAdjustBill selectFinPaymentEstimationAdjustBillById(Long paymentEstimationAdjustBillSid) {
        FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill = finPaymentEstimationAdjustBillMapper.selectFinPaymentEstimationAdjustBillById(paymentEstimationAdjustBillSid);
        // 明细
        finPaymentEstimationAdjustBill.setItemList(new ArrayList<>());
        List<FinPaymentEstimationAdjustBillItem> itemList = finPaymentEstimationAdjustBillItemMapper.selectFinPaymentEstimationAdjustBillItemList(
                new FinPaymentEstimationAdjustBillItem().setPaymentEstimationAdjustBillSid(paymentEstimationAdjustBillSid));
        if (CollectionUtil.isNotEmpty(itemList)) {
            // 价格含税新小计
            BigDecimal sumPrice = itemList.stream().map(FinPaymentEstimationAdjustBillItem::getPriceTaxNew)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            // 数量新小计
            BigDecimal sumQuantity = itemList.stream().map(FinPaymentEstimationAdjustBillItem::getQuantityNew)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            finPaymentEstimationAdjustBill.setPriceTaxNewTotal(sumPrice).setQuantityNewTotal(sumQuantity).setItemList(itemList);
        }
        // 附件清单
        finPaymentEstimationAdjustBill.setAttachmentList(new ArrayList<>());
        List<FinPaymentEstimationAdjustBillAttach> attachmentList = finPaymentEstimationAdjustBillAttachMapper.selectFinPaymentEstimationAdjustBillAttachList(
                new FinPaymentEstimationAdjustBillAttach().setPaymentEstimationAdjustBillSid(paymentEstimationAdjustBillSid));
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            finPaymentEstimationAdjustBill.setAttachmentList(attachmentList);
        }
        // 操作日志
        MongodbUtil.find(finPaymentEstimationAdjustBill);
        return finPaymentEstimationAdjustBill;
    }

    /**
     * 查询应付暂估调价量单列表
     *
     * @param finPaymentEstimationAdjustBill 应付暂估调价量单
     * @return 应付暂估调价量单
     */
    @Override
    public List<FinPaymentEstimationAdjustBill> selectFinPaymentEstimationAdjustBillList(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill) {
        return finPaymentEstimationAdjustBillMapper.selectFinPaymentEstimationAdjustBillList(finPaymentEstimationAdjustBill);
    }

    /**
     * 判断是否走审批
     * true 走审批
     * false 不走审批
     */
    public boolean isApproval() {
        SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
        if (settingClient != null && ConstantsEms.YES.equals(settingClient.getIsWorkflowYfzgtjld())) {
            return true;
        }
        return false;
    }

    /**
     * 新增应付暂估调价量单
     * 需要注意编码重复校验
     *
     * @param finPaymentEstimationAdjustBill 应付暂估调价量单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinPaymentEstimationAdjustBill(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill) {
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finPaymentEstimationAdjustBill.getHandleStatus()) && !isApproval()) {
            // 不需要走审批，提交及确认
            finPaymentEstimationAdjustBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
        }
        setConfirm(finPaymentEstimationAdjustBill);
        int row = finPaymentEstimationAdjustBillMapper.insert(finPaymentEstimationAdjustBill);
        if (row > 0) {
            FinPaymentEstimationAdjustBill bill = finPaymentEstimationAdjustBillMapper.selectById(finPaymentEstimationAdjustBill.getPaymentEstimationAdjustBillSid());
            finPaymentEstimationAdjustBill.setPaymentEstimationAdjustBillCode(bill.getPaymentEstimationAdjustBillCode());
            //
            addItemList(finPaymentEstimationAdjustBill);
            addAttachmentList(finPaymentEstimationAdjustBill);
            addTodoTask(finPaymentEstimationAdjustBill);
            // 走提交审批，参数从查询页面提交按钮参考
            if (ConstantsEms.SUBMIT_STATUS.equals(finPaymentEstimationAdjustBill.getHandleStatus())) {
                this.submit(finPaymentEstimationAdjustBill);
            }
            else if (ConstantsEms.CHECK_STATUS.equals(finPaymentEstimationAdjustBill.getHandleStatus())) {
                // 确认后操作
                updateBookItem(finPaymentEstimationAdjustBill);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(finPaymentEstimationAdjustBill.getPaymentEstimationAdjustBillSid(), finPaymentEstimationAdjustBill.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改应付暂估调价量单
     *
     * @param finPaymentEstimationAdjustBill 应付暂估调价量单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinPaymentEstimationAdjustBill(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill) {
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finPaymentEstimationAdjustBill.getHandleStatus()) && !isApproval()) {
            // 不需要走审批，提交及确认
            finPaymentEstimationAdjustBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
        }
        setConfirm(finPaymentEstimationAdjustBill);
        FinPaymentEstimationAdjustBill response = finPaymentEstimationAdjustBillMapper.selectFinPaymentEstimationAdjustBillById(finPaymentEstimationAdjustBill.getPaymentEstimationAdjustBillSid());
        finPaymentEstimationAdjustBill.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = finPaymentEstimationAdjustBillMapper.updateAllById(finPaymentEstimationAdjustBill);
        if (row > 0) {
            addItemList(finPaymentEstimationAdjustBill);
            addAttachmentList(finPaymentEstimationAdjustBill);
            addTodoTask(finPaymentEstimationAdjustBill);
            // 走提交审批，参数从查询页面提交按钮参考
            if (ConstantsEms.SUBMIT_STATUS.equals(finPaymentEstimationAdjustBill.getHandleStatus())) {
                this.submit(finPaymentEstimationAdjustBill);
            }
            else if (ConstantsEms.CHECK_STATUS.equals(finPaymentEstimationAdjustBill.getHandleStatus())) {
                // 确认后操作
                updateBookItem(finPaymentEstimationAdjustBill);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finPaymentEstimationAdjustBill);
            MongodbDeal.update(finPaymentEstimationAdjustBill.getPaymentEstimationAdjustBillSid(), response.getHandleStatus(), finPaymentEstimationAdjustBill.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更应付暂估调价量单
     *
     * @param finPaymentEstimationAdjustBill 应付暂估调价量单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinPaymentEstimationAdjustBill(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill) {
        setConfirm(finPaymentEstimationAdjustBill);
        FinPaymentEstimationAdjustBill response = finPaymentEstimationAdjustBillMapper.selectFinPaymentEstimationAdjustBillById(finPaymentEstimationAdjustBill.getPaymentEstimationAdjustBillSid());
        finPaymentEstimationAdjustBill.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = finPaymentEstimationAdjustBillMapper.updateAllById(finPaymentEstimationAdjustBill);
        if (row > 0) {
            addItemList(finPaymentEstimationAdjustBill);
            addAttachmentList(finPaymentEstimationAdjustBill);
            addTodoTask(finPaymentEstimationAdjustBill);
            updateBookItem(finPaymentEstimationAdjustBill);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finPaymentEstimationAdjustBill);
            MongodbDeal.update(finPaymentEstimationAdjustBill.getPaymentEstimationAdjustBillSid(), response.getHandleStatus(), finPaymentEstimationAdjustBill.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量删除应付暂估调价量单
     *
     * @param paymentEstimationAdjustBillSids 需要删除的应付暂估调价量单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinPaymentEstimationAdjustBillByIds(List<Long> paymentEstimationAdjustBillSids) {
        int i = 0;
        i = finPaymentEstimationAdjustBillItemMapper.delete(new QueryWrapper<FinPaymentEstimationAdjustBillItem>()
                .lambda().in(FinPaymentEstimationAdjustBillItem::getPaymentEstimationAdjustBillSid, paymentEstimationAdjustBillSids));
        i = finPaymentEstimationAdjustBillAttachMapper.delete(new QueryWrapper<FinPaymentEstimationAdjustBillAttach>()
                .lambda().in(FinPaymentEstimationAdjustBillAttach::getPaymentEstimationAdjustBillSid, paymentEstimationAdjustBillSids));
        i = finPaymentEstimationAdjustBillMapper.deleteBatchIds(paymentEstimationAdjustBillSids);
        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentSid,paymentEstimationAdjustBillSids));
        //插入日志
        paymentEstimationAdjustBillSids.forEach(sid -> {
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), TITLE);
        });
        return i;
    }

    /**
     * 更改确认状态
     *
     * @param finPaymentEstimationAdjustBill
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill) {
        int row = 0;
        Long[] sids = finPaymentEstimationAdjustBill.getPaymentEstimationAdjustBillSidList();
        if (sids != null && sids.length > 0) {
            Map<Long, List<FinPaymentEstimationAdjustBillItem>> itemMap = new HashMap<>();
            for (Long sid : sids) {
                List<FinPaymentEstimationAdjustBillItem> itemList = finPaymentEstimationAdjustBillItemMapper.selectFinPaymentEstimationAdjustBillItemList(
                        (new FinPaymentEstimationAdjustBillItem().setPaymentEstimationAdjustBillSid(sid)));
                if (CollectionUtils.isEmpty(itemList)){
                    throw new CustomException("明细不能为空!");
                }
                itemMap.put(sid, itemList);
            }
            // 判断是否要走审批
            if (BusinessType.SUBMIT.getValue().equals(finPaymentEstimationAdjustBill.getOperateType()) && !isApproval()) {
                // 不需要走审批，提交及确认
                LambdaUpdateWrapper<FinPaymentEstimationAdjustBill> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.in(FinPaymentEstimationAdjustBill::getPaymentEstimationAdjustBillSid,sids)
                        .set(FinPaymentEstimationAdjustBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                        .set(FinPaymentEstimationAdjustBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                        .set(FinPaymentEstimationAdjustBill::getConfirmDate, new Date());
                row = finPaymentEstimationAdjustBillMapper.update(null, updateWrapper);
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, ConstantsEms.CHECK_STATUS, null, TITLE, null);
                    // 确认后操作
                    List<FinPaymentEstimationAdjustBillItem> itemList = itemMap.get(id);
                    FinPaymentEstimationAdjustBill bill =finPaymentEstimationAdjustBillMapper.selectById(id);
                    bill.setItemList(itemList);
                    updateBookItem(bill);
                }
                //删除待办
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                        .in(SysTodoTask::getDocumentSid,sids));
                return row;
            }
            // 走工作流程
            row = workFlow(finPaymentEstimationAdjustBill);
        }
        return row;
    }

    /**
     * 提交
     */
    private void submit(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill){
        Map<String, Object> variables = new HashMap<>();
        variables.put("formCode", finPaymentEstimationAdjustBill.getPaymentEstimationAdjustBillCode());
        variables.put("formId", finPaymentEstimationAdjustBill.getPaymentEstimationAdjustBillSid());
        variables.put("formType", FormType.PaymentEstimationAdjustBill.getCode());
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
        LambdaUpdateWrapper<FinPaymentEstimationAdjustBill> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinPaymentEstimationAdjustBill::getPaymentEstimationAdjustBillSid, sids);
        updateWrapper.set(FinPaymentEstimationAdjustBill::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(FinPaymentEstimationAdjustBill::getConfirmDate, new Date());
            updateWrapper.set(FinPaymentEstimationAdjustBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return finPaymentEstimationAdjustBillMapper.update(null, updateWrapper);
    }

    /**
     * 工作流流程
     */
    public int workFlow(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill) {
        int row = 1;
        Long[] sids = finPaymentEstimationAdjustBill.getPaymentEstimationAdjustBillSidList();
        // 处理状态
        String handleStatus = finPaymentEstimationAdjustBill.getHandleStatus();
        if (StrUtil.isNotBlank(finPaymentEstimationAdjustBill.getOperateType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(finPaymentEstimationAdjustBill.getOperateType());
        }
        if (sids != null && sids.length > 0) {
            // 获取数据
            List<FinPaymentEstimationAdjustBill> billList = finPaymentEstimationAdjustBillMapper.selectFinPaymentEstimationAdjustBillList(
                    (new FinPaymentEstimationAdjustBill().setPaymentEstimationAdjustBillSidList(sids)));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus,
                        ConstantsTable.TABLE_FIN_PAY_EST_ADJ_BILL);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(finPaymentEstimationAdjustBill.getOperateType())) {
                // 修改处理状态
                row = this.updateHandle(sids, ConstantsEms.SUBMIT_STATUS);
                // 开启工作流
                for (int i = 0; i < billList.size(); i++) {
                    this.submit(billList.get(i));
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getPaymentEstimationAdjustBillSid(),
                            BusinessType.SUBMIT.getValue(), null, TITLE, finPaymentEstimationAdjustBill.getComment());
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(finPaymentEstimationAdjustBill.getOperateType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                for (int i = 0; i < billList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(billList.get(i).getPaymentEstimationAdjustBillSid().toString());
                    taskVo.setFormId(Long.valueOf(billList.get(i).getPaymentEstimationAdjustBillSid().toString()));
                    taskVo.setFormCode(billList.get(i).getPaymentEstimationAdjustBillCode().toString());
                    taskVo.setFormType(FormType.PaymentEstimationAdjustBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finPaymentEstimationAdjustBill.getComment());
                    try {
                        SysFormProcess process = workflowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getPaymentEstimationAdjustBillSid()}, ConstantsEms.CHECK_STATUS);
                            // 确认后操作
                            List<FinPaymentEstimationAdjustBillItem> itemList = finPaymentEstimationAdjustBillItemMapper.selectFinPaymentEstimationAdjustBillItemList(
                                    (new FinPaymentEstimationAdjustBillItem().setPaymentEstimationAdjustBillSid(billList.get(i).getPaymentEstimationAdjustBillSid())));
                            billList.get(i).setItemList(itemList).setHandleStatus(ConstantsEms.CHECK_STATUS);
                            updateBookItem(billList.get(i));
                        }
                        finPaymentEstimationAdjustBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getPaymentEstimationAdjustBillSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, finPaymentEstimationAdjustBill.getComment());
                }
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(finPaymentEstimationAdjustBill.getOperateType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                // 审批意见
                String comment = "";
                for (int i = 0; i < billList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setTargetKey("2");
                    taskVo.setBusinessKey(billList.get(i).getPaymentEstimationAdjustBillSid().toString());
                    taskVo.setFormId(Long.valueOf(billList.get(i).getPaymentEstimationAdjustBillSid().toString()));
                    taskVo.setFormCode(billList.get(i).getPaymentEstimationAdjustBillCode().toString());
                    taskVo.setFormType(FormType.PaymentEstimationAdjustBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finPaymentEstimationAdjustBill.getComment());
                    try {
                        SysFormProcess process = workflowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getPaymentEstimationAdjustBillSid()}, HandleStatus.RETURNED.getCode());
                        }
                        finPaymentEstimationAdjustBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getPaymentEstimationAdjustBillSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, finPaymentEstimationAdjustBill.getComment());
                }
            }
        }
        return row;
    }

    /**
     * 确认准备
     *
     * @param finPaymentEstimationAdjustBill
     * @return
     */
    private void setConfirm(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill){
        if (CollectionUtil.isEmpty(finPaymentEstimationAdjustBill.getItemList())){
            throw new CustomException("该操作明细信息不能为空！");
        }else {
            finPaymentEstimationAdjustBill.getItemList().forEach(item->{
                if (finPaymentEstimationAdjustBill.getDocumentType().equals(ConstantsFinance.DOC_TYPE_ESTI_TJ)
                        && item.getPriceTaxNew() == null){
                    throw new CustomException("该操作明细信息中的价格（新）不能为空！");
                }
                if (finPaymentEstimationAdjustBill.getDocumentType().equals(ConstantsFinance.DOC_TYPE_ESTI_TL)
                        && item.getQuantityNew() == null){
                    throw new CustomException("该操作明细信息中的数量（新）不能为空！");
                }
                if (ConstantsFinance.BOOK_SOURCE_CAT_RPOGI.equals(item.getBookSourceCategory())) {
                    if (item.getQuantityNew() != null && BigDecimal.ZERO.compareTo(item.getQuantityNew()) < 0) {
                            throw new CustomException("财务流水号" + item.getBookPaymentEstimationCode() + "的明细数量(新)请输入负数");
                    }
                }
                else {
                    if (item.getQuantityNew() != null && BigDecimal.ZERO.compareTo(item.getQuantityNew()) > 0) {
                        throw new CustomException("财务流水号" + item.getBookPaymentEstimationCode() + "的明细数量(新)请输入正数");
                    }
                }
            });
        }
        if (ConstantsEms.CHECK_STATUS.equals(finPaymentEstimationAdjustBill.getHandleStatus())){
            finPaymentEstimationAdjustBill.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername())
                    .setConfirmDate(new Date());
        }
        //
        finPaymentEstimationAdjustBill.setCompanyCode(null);
        if (finPaymentEstimationAdjustBill.getCompanySid() != null) {
            BasCompany company = companyMapper.selectById(finPaymentEstimationAdjustBill.getCompanySid());
            if (company != null) {
                finPaymentEstimationAdjustBill.setCompanyCode(company.getCompanyCode());
            }
        }
        finPaymentEstimationAdjustBill.setVendorCode(null);
        if (finPaymentEstimationAdjustBill.getVendorSid() != null) {
            BasVendor vendor = vendorMapper.selectById(finPaymentEstimationAdjustBill.getVendorSid());
            if (vendor != null) {
                finPaymentEstimationAdjustBill.setVendorCode(String.valueOf(vendor.getVendorCode()));
            }
        }
    }

    /**
     * 更新子表明细
     *
     * @param finPaymentEstimationAdjustBill
     * @return
     */
    private int addItemList(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill) {
        finPaymentEstimationAdjustBillItemMapper.delete(new QueryWrapper<FinPaymentEstimationAdjustBillItem>()
                .lambda().eq(FinPaymentEstimationAdjustBillItem::getPaymentEstimationAdjustBillSid, finPaymentEstimationAdjustBill.getPaymentEstimationAdjustBillSid()));
        if (CollectionUtil.isNotEmpty(finPaymentEstimationAdjustBill.getItemList())) {
            finPaymentEstimationAdjustBill.getItemList().forEach(item -> {
                if (ConstantsFinance.DOC_TYPE_ESTI_TJ.equals(finPaymentEstimationAdjustBill.getDocumentType())){
                    if (item.getPriceTaxNew() != null &&
                            item.getPriceTaxNew().multiply(item.getQuantity()).abs().compareTo(item.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTaxYhx()).abs()) < 0){
                        throw new BaseException("经过调价后的金额不能小于已核销与核销中金额之和");
                    }
                }
                if (ConstantsFinance.DOC_TYPE_ESTI_TL.equals(finPaymentEstimationAdjustBill.getDocumentType())){
                    if (item.getQuantityNew() != null &&
                            item.getQuantityNew().abs().compareTo(item.getQuantity().subtract(item.getQuantityLeft()).abs()) < 0){
                        throw new BaseException("经过调量后的数量不能小于已核销与核销中数量之和");
                    }
                }
                item.setPaymentEstimationAdjustBillSid(finPaymentEstimationAdjustBill.getPaymentEstimationAdjustBillSid());
                item.setQuantityOld(item.getQuantity()).setPriceOld(item.getPrice()).setPriceTaxOld(item.getPriceTax());
                item.setAccountDocumentSid(item.getBookPaymentEstimationSid()).setAccountDocumentCode(item.getBookPaymentEstimationCode())
                        .setAccountItemSid(item.getBookPaymentEstimationItemSid());
                if (item.getPaymentEstimationAdjustBillItemSid() != null) {
                    item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            return finPaymentEstimationAdjustBillItemMapper.inserts(finPaymentEstimationAdjustBill.getItemList());
        }
        return 0;
    }

    /**
     * 变更明细中引用的流水
     *
     * @param finPaymentEstimationAdjustBill
     * @return
     */
    private int updateBookItem(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill){
        int row = 0;
        if (!ConstantsEms.CHECK_STATUS.equals(finPaymentEstimationAdjustBill.getHandleStatus())){
            return row;
        }else {
            if (CollectionUtil.isNotEmpty(finPaymentEstimationAdjustBill.getItemList())) {
                finPaymentEstimationAdjustBill.getItemList().forEach(item -> {
                    LambdaUpdateWrapper<FinBookPaymentEstimationItem> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.eq(FinBookPaymentEstimationItem::getBookPaymentEstimationItemSid,item.getAccountItemSid());
                    //调价
                    if (ConstantsFinance.DOC_TYPE_ESTI_TJ.equals(finPaymentEstimationAdjustBill.getDocumentType())){
                        updateWrapper.set(FinBookPaymentEstimationItem::getPriceTax,item.getPriceTaxNew())
                                .set(FinBookPaymentEstimationItem::getCurrencyAmountTax,item.getPriceTaxNew().multiply(item.getQuantity()));
                        // 不含税价=含税价/（1+税率）
                        if (item.getPriceTaxNew() != null) {
                            BigDecimal taxRate = item.getTaxRate() == null ? BigDecimal.ZERO : item.getTaxRate();
                            BigDecimal divisor = BigDecimal.ONE.add(taxRate);
                            BigDecimal result = item.getPriceTaxNew().divide(divisor, 6, BigDecimal.ROUND_HALF_UP);
                            updateWrapper.set(FinBookPaymentEstimationItem::getPrice, result);
                        }
                    }
                    //调量
                    if (ConstantsFinance.DOC_TYPE_ESTI_TL.equals(finPaymentEstimationAdjustBill.getDocumentType())){
                        updateWrapper.set(FinBookPaymentEstimationItem::getQuantity,item.getQuantityNew())
                                .set(FinBookPaymentEstimationItem::getCurrencyAmountTax,item.getQuantityNew().multiply(item.getPriceTax()));
                    }
                    finBookPaymentEstimationItemMapper.update(null, updateWrapper);
                });
                row = finPaymentEstimationAdjustBill.getItemList().size();
            }
        }
        return row;
    }

    /**
     * 更新附件清单
     *
     * @param finPaymentEstimationAdjustBill
     * @return
     */
    private int addAttachmentList(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill) {
        finPaymentEstimationAdjustBillAttachMapper.delete(new QueryWrapper<FinPaymentEstimationAdjustBillAttach>()
                .lambda().eq(FinPaymentEstimationAdjustBillAttach::getPaymentEstimationAdjustBillSid, finPaymentEstimationAdjustBill.getPaymentEstimationAdjustBillSid()));
        if (CollectionUtil.isNotEmpty(finPaymentEstimationAdjustBill.getAttachmentList())) {
            finPaymentEstimationAdjustBill.getAttachmentList().forEach(item -> {
                item.setPaymentEstimationAdjustBillSid(finPaymentEstimationAdjustBill.getPaymentEstimationAdjustBillSid());
                if (item.getPaymentEstimationAdjustBillAttachSid() != null) {
                    item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            return finPaymentEstimationAdjustBillAttachMapper.inserts(finPaymentEstimationAdjustBill.getAttachmentList());
        }
        return 0;
    }

    /**
     * 更新待办
     *
     * @param finPaymentEstimationAdjustBill
     * @return
     */
    private int addTodoTask(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill){
        int row = 0;
        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().eq(SysTodoTask::getDocumentSid,finPaymentEstimationAdjustBill.getPaymentEstimationAdjustBillSid()));
        if (ConstantsEms.SAVA_STATUS.equals(finPaymentEstimationAdjustBill.getHandleStatus())){
            SysUser user = userService.selectSysUserByName(finPaymentEstimationAdjustBill.getCreatorAccount());
            //确认待办
            FinPaymentEstimationAdjustBill one = finPaymentEstimationAdjustBillMapper.selectById(finPaymentEstimationAdjustBill.getPaymentEstimationAdjustBillSid());
            SysTodoTask sysTodoTask = new SysTodoTask();
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsTable.TABLE_FIN_PAY_EST_ADJ_BILL)
                    .setNoticeDate(new Date())
                    .setUserId(user.getUserId());
            sysTodoTask.setDocumentSid(one.getPaymentEstimationAdjustBillSid());
            sysTodoTask.setTitle("应付暂估调价量单: " + one.getPaymentEstimationAdjustBillCode() + " 当前是保存状态，请及时处理！")
                    .setDocumentCode(String.valueOf(one.getPaymentEstimationAdjustBillCode()));
            sysTodoTaskService.insertSysTodoTaskMenu(sysTodoTask, ConstantsWorkbench.TODO_FIN_VEN_ADJ_INFO);
        }
        return row;
    }

}
