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
import com.platform.ems.service.IFinReceiptEstimationAdjustBillService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 应收暂估调价量单Service业务层处理
 *
 * @author chenkw
 * @date 2022-01-10
 */
@Service
@SuppressWarnings("all")
public class FinReceiptEstimationAdjustBillServiceImpl extends ServiceImpl<FinReceiptEstimationAdjustBillMapper, FinReceiptEstimationAdjustBill> implements IFinReceiptEstimationAdjustBillService {
    @Autowired
    private FinReceiptEstimationAdjustBillMapper finReceiptEstimationAdjustBillMapper;
    @Autowired
    private FinReceiptEstimationAdjustBillItemMapper finReceiptEstimationAdjustBillItemMapper;
    @Autowired
    private FinReceiptEstimationAdjustBillAttachMapper finReceiptEstimationAdjustBillAttachMapper;
    @Autowired
    private FinBookReceiptEstimationItemMapper finBookReceiptEstimationItemMapper;
    @Autowired
    private BasCompanyMapper companyMapper;
    @Autowired
    private BasCustomerMapper customerMapper;
    @Autowired
    private SysDefaultSettingClientMapper defaultSettingClientMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISystemUserService userService;
    @Autowired
    private ISysTodoTaskService sysTodoTaskService;
    @Autowired
    private WorkFlowServiceImpl workflowService;

    private static final String TITLE = "应收暂估调价量单";

    /**
     * 查询应收暂估调价量单
     *
     * @param receiptEstimationAdjustBillSid 应收暂估调价量单ID
     * @return 应收暂估调价量单
     */
    @Override
    public FinReceiptEstimationAdjustBill selectFinReceiptEstimationAdjustBillById(Long receiptEstimationAdjustBillSid) {
        FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill = finReceiptEstimationAdjustBillMapper.selectFinReceiptEstimationAdjustBillById(receiptEstimationAdjustBillSid);
        // 明细
        finReceiptEstimationAdjustBill.setItemList(new ArrayList<>());
        List<FinReceiptEstimationAdjustBillItem> itemList = finReceiptEstimationAdjustBillItemMapper.selectFinReceiptEstimationAdjustBillItemList(
                new FinReceiptEstimationAdjustBillItem().setReceiptEstimationAdjustBillSid(receiptEstimationAdjustBillSid));
        if (CollectionUtil.isNotEmpty(itemList)) {
            // 价格含税新小计
            BigDecimal sum = itemList.stream().map(FinReceiptEstimationAdjustBillItem::getPriceTaxNew)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            // 数量新小计
            BigDecimal sumQuantity = itemList.stream().map(FinReceiptEstimationAdjustBillItem::getQuantityNew)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            finReceiptEstimationAdjustBill.setPriceTaxNewTotal(sum).setQuantityNewTotal(sumQuantity).setItemList(itemList);
        }
        // 附件清单
        finReceiptEstimationAdjustBill.setAttachmentList(new ArrayList<>());
        List<FinReceiptEstimationAdjustBillAttach> attachmentList = finReceiptEstimationAdjustBillAttachMapper.selectFinReceiptEstimationAdjustBillAttachList(
                new FinReceiptEstimationAdjustBillAttach().setReceiptEstimationAdjustBillSid(receiptEstimationAdjustBillSid));
        finReceiptEstimationAdjustBill.setItemList(itemList).setAttachmentList(attachmentList);
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            finReceiptEstimationAdjustBill.setAttachmentList(attachmentList);
        }
        // 操作日志
        MongodbUtil.find(finReceiptEstimationAdjustBill);
        return finReceiptEstimationAdjustBill;
    }

    /**
     * 查询应收暂估调价量单列表
     *
     * @param finReceiptEstimationAdjustBill 应收暂估调价量单
     * @return 应收暂估调价量单
     */
    @Override
    public List<FinReceiptEstimationAdjustBill> selectFinReceiptEstimationAdjustBillList(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill) {
        return finReceiptEstimationAdjustBillMapper.selectFinReceiptEstimationAdjustBillList(finReceiptEstimationAdjustBill);
    }

    /**
     * 判断是否走审批
     * true 走审批
     * false 不走审批
     */
    public boolean isApproval() {
        SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
        if (settingClient != null && ConstantsEms.YES.equals(settingClient.getIsWorkflowYszgtjld())) {
            return true;
        }
        return false;
    }

    /**
     * 新增应收暂估调价量单
     * 需要注意编码重复校验
     *
     * @param finReceiptEstimationAdjustBill 应收暂估调价量单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinReceiptEstimationAdjustBill(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill) {
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finReceiptEstimationAdjustBill.getHandleStatus()) && !isApproval()) {
            // 不需要走审批，提交及确认
            finReceiptEstimationAdjustBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
        }
        setConfirm(finReceiptEstimationAdjustBill);
        int row = finReceiptEstimationAdjustBillMapper.insert(finReceiptEstimationAdjustBill);
        if (row > 0) {
            FinReceiptEstimationAdjustBill bill = finReceiptEstimationAdjustBillMapper.selectById(finReceiptEstimationAdjustBill.getReceiptEstimationAdjustBillSid());
            finReceiptEstimationAdjustBill.setReceiptEstimationAdjustBillCode(bill.getReceiptEstimationAdjustBillCode());
            //
            addItemList(finReceiptEstimationAdjustBill);
            addAttachmentList(finReceiptEstimationAdjustBill);
            addTodoTask(finReceiptEstimationAdjustBill);
            // 走提交审批，参数从查询页面提交按钮参考
            if (ConstantsEms.SUBMIT_STATUS.equals(finReceiptEstimationAdjustBill.getHandleStatus())) {
                this.submit(finReceiptEstimationAdjustBill);
            }
            else if (ConstantsEms.CHECK_STATUS.equals(finReceiptEstimationAdjustBill.getHandleStatus())) {
                // 确认后操作
                updateBookItem(finReceiptEstimationAdjustBill);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(finReceiptEstimationAdjustBill.getReceiptEstimationAdjustBillSid(), finReceiptEstimationAdjustBill.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改应收暂估调价量单
     *
     * @param finReceiptEstimationAdjustBill 应收暂估调价量单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinReceiptEstimationAdjustBill(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill) {
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finReceiptEstimationAdjustBill.getHandleStatus()) && !isApproval()) {
            // 不需要走审批，提交及确认
            finReceiptEstimationAdjustBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
        }
        setConfirm(finReceiptEstimationAdjustBill);
        FinReceiptEstimationAdjustBill response = finReceiptEstimationAdjustBillMapper.selectFinReceiptEstimationAdjustBillById(finReceiptEstimationAdjustBill.getReceiptEstimationAdjustBillSid());
        finReceiptEstimationAdjustBill.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = finReceiptEstimationAdjustBillMapper.updateAllById(finReceiptEstimationAdjustBill);
        if (row > 0) {
            addItemList(finReceiptEstimationAdjustBill);
            addAttachmentList(finReceiptEstimationAdjustBill);
            addTodoTask(finReceiptEstimationAdjustBill);
            // 走提交审批，参数从查询页面提交按钮参考
            if (ConstantsEms.SUBMIT_STATUS.equals(finReceiptEstimationAdjustBill.getHandleStatus())) {
                this.submit(finReceiptEstimationAdjustBill);
            }
            else if (ConstantsEms.CHECK_STATUS.equals(finReceiptEstimationAdjustBill.getHandleStatus())) {
                // 确认后操作
                updateBookItem(finReceiptEstimationAdjustBill);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finReceiptEstimationAdjustBill);
            MongodbDeal.update(finReceiptEstimationAdjustBill.getReceiptEstimationAdjustBillSid(), response.getHandleStatus(), finReceiptEstimationAdjustBill.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更应收暂估调价量单
     *
     * @param finReceiptEstimationAdjustBill 应收暂估调价量单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinReceiptEstimationAdjustBill(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill) {
        setConfirm(finReceiptEstimationAdjustBill);
        FinReceiptEstimationAdjustBill response = finReceiptEstimationAdjustBillMapper.selectFinReceiptEstimationAdjustBillById(finReceiptEstimationAdjustBill.getReceiptEstimationAdjustBillSid());
        finReceiptEstimationAdjustBill.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = finReceiptEstimationAdjustBillMapper.updateAllById(finReceiptEstimationAdjustBill);
        if (row > 0) {
            addItemList(finReceiptEstimationAdjustBill);
            addAttachmentList(finReceiptEstimationAdjustBill);
            addTodoTask(finReceiptEstimationAdjustBill);
            updateBookItem(finReceiptEstimationAdjustBill);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finReceiptEstimationAdjustBill);
            MongodbDeal.update(finReceiptEstimationAdjustBill.getReceiptEstimationAdjustBillSid(), response.getHandleStatus(), finReceiptEstimationAdjustBill.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量删除应收暂估调价量单
     *
     * @param receiptEstimationAdjustBillSids 需要删除的应收暂估调价量单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinReceiptEstimationAdjustBillByIds(List<Long> receiptEstimationAdjustBillSids) {
        int i = 0;
        i = finReceiptEstimationAdjustBillItemMapper.delete(new QueryWrapper<FinReceiptEstimationAdjustBillItem>()
                .lambda().in(FinReceiptEstimationAdjustBillItem::getReceiptEstimationAdjustBillSid, receiptEstimationAdjustBillSids));
        i = finReceiptEstimationAdjustBillAttachMapper.delete(new QueryWrapper<FinReceiptEstimationAdjustBillAttach>()
                .lambda().in(FinReceiptEstimationAdjustBillAttach::getReceiptEstimationAdjustBillSid, receiptEstimationAdjustBillSids));
        i = finReceiptEstimationAdjustBillMapper.deleteBatchIds(receiptEstimationAdjustBillSids);
        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentSid,receiptEstimationAdjustBillSids));
        //插入日志
        receiptEstimationAdjustBillSids.forEach(sid -> {
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), TITLE);
        });
        return i;
    }

    /**
     * 更改确认状态
     *
     * @param finReceiptEstimationAdjustBill
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill) {
        int row = 0;
        Long[] sids = finReceiptEstimationAdjustBill.getReceiptEstimationAdjustBillSidList();
        if (sids != null && sids.length > 0) {
            Map<Long, List<FinReceiptEstimationAdjustBillItem>> itemMap = new HashMap<>();
            for (Long sid : sids) {
                List<FinReceiptEstimationAdjustBillItem> itemList = finReceiptEstimationAdjustBillItemMapper.selectFinReceiptEstimationAdjustBillItemList(
                        (new FinReceiptEstimationAdjustBillItem().setReceiptEstimationAdjustBillSid(sid)));
                if (CollectionUtils.isEmpty(itemList)){
                    throw new CustomException("明细不能为空!");
                }
                itemMap.put(sid, itemList);
            }
            // 判断是否要走审批
            if (BusinessType.SUBMIT.getValue().equals(finReceiptEstimationAdjustBill.getOperateType()) && !isApproval()) {
                // 不需要走审批，提交及确认
                LambdaUpdateWrapper<FinReceiptEstimationAdjustBill> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.in(FinReceiptEstimationAdjustBill::getReceiptEstimationAdjustBillSid,sids)
                        .set(FinReceiptEstimationAdjustBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                        .set(FinReceiptEstimationAdjustBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                        .set(FinReceiptEstimationAdjustBill::getConfirmDate, new Date());
                row = finReceiptEstimationAdjustBillMapper.update(null, updateWrapper);
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, ConstantsEms.CHECK_STATUS, null, TITLE, null);
                    // 确认后操作
                    List<FinReceiptEstimationAdjustBillItem> itemList = itemMap.get(id);
                    FinReceiptEstimationAdjustBill bill = finReceiptEstimationAdjustBillMapper.selectById(id);
                    bill.setItemList(itemList);
                    updateBookItem(bill);
                }
                //删除待办
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                        .in(SysTodoTask::getDocumentSid,sids));
                return row;
            }
            // 走工作流程
            row = workFlow(finReceiptEstimationAdjustBill);
        }
        return row;
    }

    /**
     * 提交
     */
    private void submit(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill){
        Map<String, Object> variables = new HashMap<>();
        variables.put("formCode", finReceiptEstimationAdjustBill.getReceiptEstimationAdjustBillCode());
        variables.put("formId", finReceiptEstimationAdjustBill.getReceiptEstimationAdjustBillSid());
        variables.put("formType", FormType.ReceiptEstimationAdjustBill.getCode());
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
        LambdaUpdateWrapper<FinReceiptEstimationAdjustBill> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinReceiptEstimationAdjustBill::getReceiptEstimationAdjustBillSid, sids);
        updateWrapper.set(FinReceiptEstimationAdjustBill::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(FinReceiptEstimationAdjustBill::getConfirmDate, new Date());
            updateWrapper.set(FinReceiptEstimationAdjustBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return finReceiptEstimationAdjustBillMapper.update(null, updateWrapper);
    }

    /**
     * 工作流流程
     */
    public int workFlow(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill) {
        int row = 1;
        Long[] sids = finReceiptEstimationAdjustBill.getReceiptEstimationAdjustBillSidList();
        // 处理状态
        String handleStatus = finReceiptEstimationAdjustBill.getHandleStatus();
        if (StrUtil.isNotBlank(finReceiptEstimationAdjustBill.getOperateType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(finReceiptEstimationAdjustBill.getOperateType());
        }
        if (sids != null && sids.length > 0) {
            // 获取数据
            List<FinReceiptEstimationAdjustBill> billList = finReceiptEstimationAdjustBillMapper.selectFinReceiptEstimationAdjustBillList(
                    (new FinReceiptEstimationAdjustBill().setReceiptEstimationAdjustBillSidList(sids)));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus,
                        ConstantsTable.TABLE_FIN_REC_EST_ADJ_BILL);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(finReceiptEstimationAdjustBill.getOperateType())) {
                // 修改处理状态
                row = this.updateHandle(sids, ConstantsEms.SUBMIT_STATUS);
                // 开启工作流
                for (int i = 0; i < billList.size(); i++) {
                    this.submit(billList.get(i));
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getReceiptEstimationAdjustBillSid(),
                            BusinessType.SUBMIT.getValue(), null, TITLE, finReceiptEstimationAdjustBill.getComment());
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(finReceiptEstimationAdjustBill.getOperateType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                for (int i = 0; i < billList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(billList.get(i).getReceiptEstimationAdjustBillSid().toString());
                    taskVo.setFormId(Long.valueOf(billList.get(i).getReceiptEstimationAdjustBillSid().toString()));
                    taskVo.setFormCode(billList.get(i).getReceiptEstimationAdjustBillCode().toString());
                    taskVo.setFormType(FormType.ReceiptEstimationAdjustBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finReceiptEstimationAdjustBill.getComment());
                    try {
                        SysFormProcess process = workflowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getReceiptEstimationAdjustBillSid()}, ConstantsEms.CHECK_STATUS);
                            // 确认后操作
                            List<FinReceiptEstimationAdjustBillItem> itemList = finReceiptEstimationAdjustBillItemMapper.selectFinReceiptEstimationAdjustBillItemList(
                                    (new FinReceiptEstimationAdjustBillItem().setReceiptEstimationAdjustBillSid(billList.get(i).getReceiptEstimationAdjustBillSid())));
                            billList.get(i).setItemList(itemList).setHandleStatus(ConstantsEms.CHECK_STATUS);
                            updateBookItem(billList.get(i));
                        }
                        finReceiptEstimationAdjustBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getReceiptEstimationAdjustBillSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, finReceiptEstimationAdjustBill.getComment());
                }
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(finReceiptEstimationAdjustBill.getOperateType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                // 审批意见
                String comment = "";
                for (int i = 0; i < billList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setTargetKey("2");
                    taskVo.setBusinessKey(billList.get(i).getReceiptEstimationAdjustBillSid().toString());
                    taskVo.setFormId(Long.valueOf(billList.get(i).getReceiptEstimationAdjustBillSid().toString()));
                    taskVo.setFormCode(billList.get(i).getReceiptEstimationAdjustBillCode().toString());
                    taskVo.setFormType(FormType.ReceiptEstimationAdjustBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finReceiptEstimationAdjustBill.getComment());
                    try {
                        SysFormProcess process = workflowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getReceiptEstimationAdjustBillSid()}, HandleStatus.RETURNED.getCode());
                        }
                        finReceiptEstimationAdjustBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getReceiptEstimationAdjustBillSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, finReceiptEstimationAdjustBill.getComment());
                }
            }
        }
        return row;
    }

    /**
     * 确认准备
     *
     * @param finReceiptEstimationAdjustBill
     * @return
     */
    private void setConfirm(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill){
        if (CollectionUtil.isEmpty(finReceiptEstimationAdjustBill.getItemList())){
            throw new CustomException("该操作明细信息不能为空！");
        } else {
            finReceiptEstimationAdjustBill.getItemList().forEach(item->{
                if (finReceiptEstimationAdjustBill.getDocumentType().equals(ConstantsFinance.DOC_TYPE_ESTI_TJ)
                        && item.getPriceTaxNew() == null){
                    throw new CustomException("该操作明细信息中的价格（新）不能为空！");
                }
                if (finReceiptEstimationAdjustBill.getDocumentType().equals(ConstantsFinance.DOC_TYPE_ESTI_TL)
                        && item.getQuantityNew() == null){
                    throw new CustomException("该操作明细信息中的数量（新）不能为空！");
                }
                if (ConstantsFinance.BOOK_SOURCE_CAT_RSOGR.equals(item.getBookSourceCategory())) {
                    if (item.getQuantityNew() != null && BigDecimal.ZERO.compareTo(item.getQuantityNew()) < 0) {
                        throw new CustomException("财务流水号" + item.getBookReceiptEstimationCode() + "的明细数量(新)请输入负数");
                    }
                }
                else {
                    if (item.getQuantityNew() != null && BigDecimal.ZERO.compareTo(item.getQuantityNew()) > 0) {
                        throw new CustomException("财务流水号" + item.getBookReceiptEstimationCode() + "的明细数量(新)请输入正数");
                    }
                }
            });
        }
        if (ConstantsEms.CHECK_STATUS.equals(finReceiptEstimationAdjustBill.getHandleStatus())){
            finReceiptEstimationAdjustBill.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername())
                    .setConfirmDate(new Date());
        }
        //
        finReceiptEstimationAdjustBill.setCompanyCode(null);
        if (finReceiptEstimationAdjustBill.getCompanySid() != null) {
            BasCompany company = companyMapper.selectById(finReceiptEstimationAdjustBill.getCompanySid());
            if (company != null) {
                finReceiptEstimationAdjustBill.setCompanyCode(company.getCompanyCode());
            }
        }
        finReceiptEstimationAdjustBill.setCustomerCode(null);
        if (finReceiptEstimationAdjustBill.getCustomerSid() != null) {
            BasCustomer customer = customerMapper.selectById(finReceiptEstimationAdjustBill.getCustomerSid());
            if (customer != null) {
                finReceiptEstimationAdjustBill.setCustomerCode(String.valueOf(customer.getCustomerCode()));
            }
        }
    }

    /**
     * 更新子表明细
     *
     * @param finReceiptEstimationAdjustBill
     * @return
     */
    private int addItemList(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill) {
        int row = 0;
        finReceiptEstimationAdjustBillItemMapper.delete(new QueryWrapper<FinReceiptEstimationAdjustBillItem>()
                .lambda().eq(FinReceiptEstimationAdjustBillItem::getReceiptEstimationAdjustBillSid, finReceiptEstimationAdjustBill.getReceiptEstimationAdjustBillSid()));
        if (CollectionUtil.isNotEmpty(finReceiptEstimationAdjustBill.getItemList())) {
            finReceiptEstimationAdjustBill.getItemList().forEach(item -> {
                if (ConstantsFinance.DOC_TYPE_ESTI_TJ.equals(finReceiptEstimationAdjustBill.getDocumentType())){
                    if (item.getPriceTaxNew() != null &&
                            item.getPriceTaxNew().multiply(item.getQuantity()).abs().compareTo(item.getCurrencyAmountTaxHxz().add(item.getCurrencyAmountTaxYhx()).abs()) < 0){
                        throw new BaseException("经过调价后的金额不能小于已核销与核销中金额之和");
                    }
                }
                if (ConstantsFinance.DOC_TYPE_ESTI_TL.equals(finReceiptEstimationAdjustBill.getDocumentType())){
                    if (item.getQuantityNew() != null &&
                            item.getQuantityNew().abs().compareTo(item.getQuantity().subtract(item.getQuantityLeft()).abs()) < 0){
                        throw new BaseException("经过调量后的数量不能小于已核销与核销中数量之和");
                    }
                }
                item.setReceiptEstimationAdjustBillSid(finReceiptEstimationAdjustBill.getReceiptEstimationAdjustBillSid());
                item.setQuantityOld(item.getQuantity()).setPriceOld(item.getPrice()).setPriceTaxOld(item.getPriceTax());
                item.setAccountDocumentSid(item.getBookReceiptEstimationSid()).setAccountDocumentCode(item.getBookReceiptEstimationCode())
                        .setAccountItemSid(item.getBookReceiptEstimationItemSid());
                if (item.getReceiptEstimationAdjustBillItemSid() != null) {
                    item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            row = finReceiptEstimationAdjustBillItemMapper.inserts(finReceiptEstimationAdjustBill.getItemList());
        }
        return row;
    }

    /**
     * 确认后变更明细中引用的流水
     *
     * @param finReceiptEstimationAdjustBill
     * @return
     */
    private int updateBookItem(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill){
        int row = 0;
        if (!ConstantsEms.CHECK_STATUS.equals(finReceiptEstimationAdjustBill.getHandleStatus())){
            return row;
        }else {
            if (CollectionUtil.isNotEmpty(finReceiptEstimationAdjustBill.getItemList())) {
                finReceiptEstimationAdjustBill.getItemList().forEach(item -> {
                    LambdaUpdateWrapper<FinBookReceiptEstimationItem> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.eq(FinBookReceiptEstimationItem::getBookReceiptEstimationItemSid,item.getAccountItemSid());
                    //调价
                    if (ConstantsFinance.DOC_TYPE_ESTI_TJ.equals(finReceiptEstimationAdjustBill.getDocumentType())){
                        updateWrapper.set(FinBookReceiptEstimationItem::getPriceTax,item.getPriceTaxNew())
                                .set(FinBookReceiptEstimationItem::getCurrencyAmountTax,item.getPriceTaxNew().multiply(item.getQuantity()));
                        // 不含税价=含税价/（1+税率）
                        if (item.getPriceTaxNew() != null) {
                            BigDecimal taxRate = item.getTaxRate() == null ? BigDecimal.ZERO : item.getTaxRate();
                            BigDecimal divisor = BigDecimal.ONE.add(taxRate);
                            BigDecimal result = item.getPriceTaxNew().divide(divisor, 6, BigDecimal.ROUND_HALF_UP);
                            updateWrapper.set(FinBookReceiptEstimationItem::getPrice, result);
                        }
                    }
                    //调量
                    if (ConstantsFinance.DOC_TYPE_ESTI_TL.equals(finReceiptEstimationAdjustBill.getDocumentType())){
                        updateWrapper.set(FinBookReceiptEstimationItem::getQuantity,item.getQuantityNew())
                                .set(FinBookReceiptEstimationItem::getCurrencyAmountTax,item.getQuantityNew().multiply(item.getPriceTax()));
                    }
                    finBookReceiptEstimationItemMapper.update(null, updateWrapper);
                });
                row = finReceiptEstimationAdjustBill.getItemList().size();
            }
        }
        return row;
    }

    /**
     * 更新附件清单
     *
     * @param finReceiptEstimationAdjustBill
     * @return
     */
    private int addAttachmentList(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill) {
        finReceiptEstimationAdjustBillAttachMapper.delete(new QueryWrapper<FinReceiptEstimationAdjustBillAttach>()
                .lambda().eq(FinReceiptEstimationAdjustBillAttach::getReceiptEstimationAdjustBillSid, finReceiptEstimationAdjustBill.getReceiptEstimationAdjustBillSid()));
        if (CollectionUtil.isNotEmpty(finReceiptEstimationAdjustBill.getAttachmentList())) {
            finReceiptEstimationAdjustBill.getAttachmentList().forEach(item -> {
                item.setReceiptEstimationAdjustBillSid(finReceiptEstimationAdjustBill.getReceiptEstimationAdjustBillSid());
                if (item.getReceiptEstimationAdjustBillAttachSid() != null) {
                    item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            return finReceiptEstimationAdjustBillAttachMapper.inserts(finReceiptEstimationAdjustBill.getAttachmentList());
        }
        return 0;
    }

    /**
     * 更新待办
     *
     * @param finReceiptEstimationAdjustBill
     * @return
     */
    private int addTodoTask(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill){
        int row = 0;
        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().eq(SysTodoTask::getDocumentSid,finReceiptEstimationAdjustBill.getReceiptEstimationAdjustBillSid()));
        if (ConstantsEms.SAVA_STATUS.equals(finReceiptEstimationAdjustBill.getHandleStatus())){
            SysUser user = userService.selectSysUserByName(finReceiptEstimationAdjustBill.getCreatorAccount());
            FinReceiptEstimationAdjustBill one = finReceiptEstimationAdjustBillMapper.selectById(finReceiptEstimationAdjustBill.getReceiptEstimationAdjustBillSid());
            //确认待办
            SysTodoTask sysTodoTask = new SysTodoTask();
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsTable.TABLE_FIN_REC_EST_ADJ_BILL)
                    .setNoticeDate(new Date())
                    .setUserId(user.getUserId());
            sysTodoTask.setDocumentSid(one.getReceiptEstimationAdjustBillSid());
            sysTodoTask.setTitle("应收暂估调价量单: " + one.getReceiptEstimationAdjustBillCode() + " 当前是保存状态，请及时处理！")
                    .setDocumentCode(String.valueOf(one.getReceiptEstimationAdjustBillCode()));
            row = sysTodoTaskService.insertSysTodoTaskMenu(sysTodoTask, ConstantsWorkbench.TODO_FIN_CUS_ADJ_INFO);
        }
        return row;
    }
}
