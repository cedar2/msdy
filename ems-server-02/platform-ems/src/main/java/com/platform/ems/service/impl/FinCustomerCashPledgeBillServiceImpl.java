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
import com.platform.ems.service.IFinCustomerCashPledgeBillService;
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
 * 客户押金Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Service
@SuppressWarnings("all")
public class FinCustomerCashPledgeBillServiceImpl extends ServiceImpl<FinCustomerCashPledgeBillMapper, FinCustomerCashPledgeBill> implements IFinCustomerCashPledgeBillService {
    @Autowired
    private FinCustomerCashPledgeBillMapper finCustomerCashPledgeBillMapper;
    @Autowired
    private FinCustomerCashPledgeBillItemMapper finCustomerCashPledgeBillItemMapper;
    @Autowired
    private FinCustomerCashPledgeBillAttachMapper finCustomerCashPledgeBillAttachMapper;
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

    private static final String TITLE = "客户押金";

    /**
     * 查询供应商押金
     *
     * @param cashPledgeBillSid 供应商押金ID
     * @return 供应商押金
     */
    @Override
    public FinCustomerCashPledgeBill selectFinCustomerCashPledgeBillById(Long cashPledgeBillSid) {
        FinCustomerCashPledgeBill finCustomerCashPledgeBill = finCustomerCashPledgeBillMapper.selectFinCustomerCashPledgeBillById(cashPledgeBillSid);
        if (finCustomerCashPledgeBill == null) {
            return new FinCustomerCashPledgeBill();
        }
        //明细
        FinCustomerCashPledgeBillItem item = new FinCustomerCashPledgeBillItem();
        item.setCashPledgeBillSid(cashPledgeBillSid);
        List<FinCustomerCashPledgeBillItem> finCustomerCashPledgeBillItemList = finCustomerCashPledgeBillItemMapper.selectFinCustomerCashPledgeBillItemList(item);
        finCustomerCashPledgeBillItemList = finCustomerCashPledgeBillItemList.stream().sorted(Comparator.comparing(FinCustomerCashPledgeBillItem::getItemNum,
                Comparator.nullsLast(Long::compareTo))).collect(Collectors.toList());
        finCustomerCashPledgeBill.setItemList(finCustomerCashPledgeBillItemList);
        //附件
        finCustomerCashPledgeBill.setAttachmentList(new ArrayList<>());
        List<FinCustomerCashPledgeBillAttach> finCustomerCashPledgeBillAttachListList =
                finCustomerCashPledgeBillAttachMapper.selectFinCustomerCashPledgeBillAttachList(new FinCustomerCashPledgeBillAttach().setCashPledgeBillSid(cashPledgeBillSid));
        if (CollectionUtils.isNotEmpty(finCustomerCashPledgeBillAttachListList)) {
            finCustomerCashPledgeBill.setAttachmentList(finCustomerCashPledgeBillAttachListList);
        }
        MongodbUtil.find(finCustomerCashPledgeBill);
        return finCustomerCashPledgeBill;
    }

    /**
     * 查询供应商押金列表
     *
     * @param finCustomerCashPledgeBill 供应商押金
     * @return 供应商押金
     */
    @Override
    public List<FinCustomerCashPledgeBill> selectFinCustomerCashPledgeBillList(FinCustomerCashPledgeBill finCustomerCashPledgeBill) {
        List<FinCustomerCashPledgeBill> response = finCustomerCashPledgeBillMapper.selectFinCustomerCashPledgeBillList(finCustomerCashPledgeBill);
        return response;
    }

    /**
     * 新增供应商押金
     * 需要注意编码重复校验
     *
     * @param finCustomerCashPledgeBill 供应商押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinCustomerCashPledgeBill(FinCustomerCashPledgeBill finCustomerCashPledgeBill) {
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finCustomerCashPledgeBill.getHandleStatus())) {
            SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
            if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowKhyj())) {
                // 不需要走审批，提交及确认
                finCustomerCashPledgeBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        //设置确认信息，校验
        setConfirmedInfo(finCustomerCashPledgeBill);
        if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQ.equals(finCustomerCashPledgeBill.getDocumentType())
                || ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZF.equals(finCustomerCashPledgeBill.getDocumentType())){
            finCustomerCashPledgeBill.setReturnStatus(ConstantsFinance.RETURN_STATUS_WTH);
        }
        int row = finCustomerCashPledgeBillMapper.insert(finCustomerCashPledgeBill);
        if (row > 0) {
            // 找到code
            FinCustomerCashPledgeBill bill = finCustomerCashPledgeBillMapper.selectById(finCustomerCashPledgeBill.getCashPledgeBillSid());
            finCustomerCashPledgeBill.setCashPledgeBillCode(bill.getCashPledgeBillCode());
            //插入子表，附件表
            insertChild(finCustomerCashPledgeBill.getItemList(), finCustomerCashPledgeBill.getAttachmentList(), finCustomerCashPledgeBill);
            //更新原押金明细
            if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(finCustomerCashPledgeBill.getDocumentType())
                    || ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(finCustomerCashPledgeBill.getDocumentType())){
                updateChild(finCustomerCashPledgeBill);
            }
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(finCustomerCashPledgeBill.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_FIN_CUSTOEMR_CASH_PLEDGE_BILL)
                        .setDocumentSid(finCustomerCashPledgeBill.getCashPledgeBillSid());
                sysTodoTask.setTitle("客户押金: " + finCustomerCashPledgeBill.getCashPledgeBillCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(finCustomerCashPledgeBill.getCashPledgeBillCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskService.insertSysTodoTaskMenu(sysTodoTask, ConstantsWorkbench.TODO_FIN_CUS_CASH_INFO);
            }
            // 走提交审批，参数从查询页面提交按钮参考
            else if (ConstantsEms.SUBMIT_STATUS.equals(finCustomerCashPledgeBill.getHandleStatus())) {
                this.submit(finCustomerCashPledgeBill);
            }
            else if (ConstantsEms.CHECK_STATUS.equals(finCustomerCashPledgeBill.getHandleStatus())) {
                // 确认后操作
                confirmAfter(finCustomerCashPledgeBill.getItemList());
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(finCustomerCashPledgeBill.getCashPledgeBillSid(), finCustomerCashPledgeBill.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改供应商押金
     *
     * @param finCustomerCashPledgeBill 供应商押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinCustomerCashPledgeBill(FinCustomerCashPledgeBill finCustomerCashPledgeBill) {
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finCustomerCashPledgeBill.getHandleStatus())) {
            SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
            if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowKhyj())) {
                // 不需要走审批，提交及确认
                finCustomerCashPledgeBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        //设置确认信息，校验
        setConfirmedInfo(finCustomerCashPledgeBill);
        FinCustomerCashPledgeBill response = finCustomerCashPledgeBillMapper.selectFinCustomerCashPledgeBillById(finCustomerCashPledgeBill.getCashPledgeBillSid());
        List<Long> sids = new ArrayList<>();
        sids.add(finCustomerCashPledgeBill.getCashPledgeBillSid());
        if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(finCustomerCashPledgeBill.getDocumentType())
                || ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(finCustomerCashPledgeBill.getDocumentType())){
            //暂时回退押金明细来源，会在更新原押金明细的时候根据实际再处理
            returnChild(sids);
        }
        int row = finCustomerCashPledgeBillMapper.updateAllById(finCustomerCashPledgeBill);
        if (row > 0) {
            //删除子表，附件表
            deleteItem(sids);
            //插入子表，附件表
            insertChild(finCustomerCashPledgeBill.getItemList(), finCustomerCashPledgeBill.getAttachmentList(), finCustomerCashPledgeBill);
            //更新原押金明细
            if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(finCustomerCashPledgeBill.getDocumentType())
                    || ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(finCustomerCashPledgeBill.getDocumentType())){
                updateChild(finCustomerCashPledgeBill);
            }
            //不是保存状态时删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(finCustomerCashPledgeBill.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, finCustomerCashPledgeBill.getCashPledgeBillSid()));
            }
            // 走提交审批，参数从查询页面提交按钮参考
            if (ConstantsEms.SUBMIT_STATUS.equals(finCustomerCashPledgeBill.getHandleStatus())) {
                this.submit(finCustomerCashPledgeBill);
            }
            else if (ConstantsEms.CHECK_STATUS.equals(finCustomerCashPledgeBill.getHandleStatus())) {
                // 确认后操作
                confirmAfter(finCustomerCashPledgeBill.getItemList());
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finCustomerCashPledgeBill);
            MongodbDeal.update(finCustomerCashPledgeBill.getCashPledgeBillSid(), response.getHandleStatus(), finCustomerCashPledgeBill.getHandleStatus(), msgList, TITLE,null);

        }
        return row;
    }

    /**
     * 变更供应商押金
     *
     * @param finCustomerCashPledgeBill 供应商押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinCustomerCashPledgeBill(FinCustomerCashPledgeBill finCustomerCashPledgeBill) {
        //设置确认信息，校验
        setConfirmedInfo(finCustomerCashPledgeBill);
        FinCustomerCashPledgeBill response = finCustomerCashPledgeBillMapper.selectFinCustomerCashPledgeBillById(finCustomerCashPledgeBill.getCashPledgeBillSid());
        List<Long> sids = new ArrayList<>();
        sids.add(finCustomerCashPledgeBill.getCashPledgeBillSid());
        if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(finCustomerCashPledgeBill.getDocumentType())
                || ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(finCustomerCashPledgeBill.getDocumentType())){
            //暂时回退押金明细来源，会在更新原押金明细的时候根据实际再处理
            returnChild(sids);
        }
        int row = finCustomerCashPledgeBillMapper.updateAllById(finCustomerCashPledgeBill);
        if (row > 0) {
            //删除子表，附件表
            deleteItem(sids);
            //插入子表，附件表
            insertChild(finCustomerCashPledgeBill.getItemList(), finCustomerCashPledgeBill.getAttachmentList(), finCustomerCashPledgeBill);
            //更新原押金明细
            if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(finCustomerCashPledgeBill.getDocumentType())
                    || ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(finCustomerCashPledgeBill.getDocumentType())){
                updateChild(finCustomerCashPledgeBill);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finCustomerCashPledgeBill);
            MongodbUtil.insertUserLog(finCustomerCashPledgeBill.getCashPledgeBillSid(), BusinessType.CHANGE.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商押金
     *
     * @param cashPledgeBillSids 需要删除的供应商押金ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinCustomerCashPledgeBillByIds(List<Long> cashPledgeBillSids) {
        if (CollectionUtils.isEmpty(cashPledgeBillSids)){
            throw new BaseException("请选择行！");
        }
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(HandleStatus.SAVE.getCode());
        handleStatusList.add(HandleStatus.RETURNED.getCode());
        List<FinCustomerCashPledgeBill> finVendorFundsFreezeBillList = finCustomerCashPledgeBillMapper.selectList(new QueryWrapper<FinCustomerCashPledgeBill>().lambda()
                .in(FinCustomerCashPledgeBill::getCashPledgeBillSid,cashPledgeBillSids)
                .notIn(FinCustomerCashPledgeBill::getHandleStatus, handleStatusList));
        if (CollectionUtils.isNotEmpty(finVendorFundsFreezeBillList)){
            throw new BaseException("仅保存状态和退回状态允许删除操作！");
        }
        List<FinCustomerCashPledgeBill> list = finCustomerCashPledgeBillMapper.selectList(new QueryWrapper<FinCustomerCashPledgeBill>().lambda()
                .in(FinCustomerCashPledgeBill::getCashPledgeBillSid,cashPledgeBillSids));
        int i = finCustomerCashPledgeBillMapper.deleteBatchIds(cashPledgeBillSids);
        if (i > 0) {
            cashPledgeBillSids.forEach(sid -> {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
            });
            List<Long> sids = new ArrayList<>();
            list.forEach(item->{
                if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(item.getDocumentType())
                        || ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(item.getDocumentType())){
                    sids.add(item.getCashPledgeBillSid());
                }
            });
            //回退明细来源
            if (CollectionUtils.isNotEmpty(sids)){
                returnChild(sids);
            }
            //删除明细
            deleteItem(cashPledgeBillSids);
            //删除待办
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, cashPledgeBillSids));
        }
        return i;
    }

    /**
     * 更改确认状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FinCustomerCashPledgeBill finCustomerCashPledgeBill) {
        int row = 0;
        Long[] sids = finCustomerCashPledgeBill.getCashPledgeBillSidList();
        if (sids != null && sids.length > 0) {
            Map<Long, List<FinCustomerCashPledgeBillItem>> itemMap = new HashMap<>();
            for (Long sid : sids) {
                List<FinCustomerCashPledgeBillItem> itemList = finCustomerCashPledgeBillItemMapper.selectFinCustomerCashPledgeBillItemList
                        (new FinCustomerCashPledgeBillItem().setCashPledgeBillSid(sid));
                if (CollectionUtils.isEmpty(itemList)){
                    throw new CustomException("明细不能为空!");
                }
                itemMap.put(sid, itemList);
            }
            // 判断是否要走审批
            if (BusinessType.SUBMIT.getValue().equals(finCustomerCashPledgeBill.getOperateType())) {
                SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                        .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
                if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowKhyj())) {
                    // 不需要走审批，提交及确认
                    LambdaUpdateWrapper<FinCustomerCashPledgeBill> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.in(FinCustomerCashPledgeBill::getCashPledgeBillSid,sids)
                            .set(FinCustomerCashPledgeBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                            .set(FinCustomerCashPledgeBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                            .set(FinCustomerCashPledgeBill::getConfirmDate, new Date());
                    row = finCustomerCashPledgeBillMapper.update(null, updateWrapper);
                    for (Long id : sids) {
                        //插入日志
                        MongodbDeal.check(id, ConstantsEms.CHECK_STATUS, null, TITLE, null);
                        // 确认后操作
                        confirmAfter(itemMap.get(id));
                    }
                    //删除待办
                    sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                            .in(SysTodoTask::getDocumentSid,sids));
                    return row;
                }
            }
            // 走工作流程
            row = workFlow(finCustomerCashPledgeBill);
        }
        return row;
    }

    /**
     * 审批通过后处理
     */
    public void confirmAfter(List<FinCustomerCashPledgeBillItem> itemList) {
        //过滤重复操作的单据
        if (CollectionUtils.isNotEmpty(itemList)){
            Set<Long> preCashPledgeBillSids = new HashSet<>();
            for (FinCustomerCashPledgeBillItem item : itemList) {
                if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(item.getDocumentType())
                        || ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(item.getDocumentType())){
                    //押金明细来源
                    FinCustomerCashPledgeBillItem original = finCustomerCashPledgeBillItemMapper.selectFinCustomerCashPledgeBillItemById(item.getPreCashPledgeBillItemSid());
                    original.setCurrencyAmountYth(original.getCurrencyAmountYth().add(item.getCurrencyAmount()));
                    original.setCurrencyAmountThz(original.getCurrencyAmountThz().subtract(item.getCurrencyAmount()));
                    if (original.getCurrencyAmountYth().compareTo(original.getCurrencyAmount()) == 0){
                        original.setReturnStatus(ConstantsFinance.RETURN_STATUS_QBTH);
                    }
                    finCustomerCashPledgeBillItemMapper.updateAllById(original);
                    //修改来源主表的状态
                    preCashPledgeBillSids.add(item.getPreCashPledgeBillSid());
                }
            }
            //
            for (Long preSid : preCashPledgeBillSids) {
                updatePreReturnStatus(preSid);
            }
        }
    }

    /**
     * 提交
     */
    private void submit(FinCustomerCashPledgeBill finCustomerCashPledgeBill){
        Map<String, Object> variables = new HashMap<>();
        variables.put("formCode", finCustomerCashPledgeBill.getCashPledgeBillCode());
        variables.put("formId", finCustomerCashPledgeBill.getCashPledgeBillSid());
        variables.put("formType", FormType.CustomerCashPledgeBill.getCode());
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
        LambdaUpdateWrapper<FinCustomerCashPledgeBill> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinCustomerCashPledgeBill::getCashPledgeBillSid, sids);
        updateWrapper.set(FinCustomerCashPledgeBill::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(FinCustomerCashPledgeBill::getConfirmDate, new Date());
            updateWrapper.set(FinCustomerCashPledgeBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return finCustomerCashPledgeBillMapper.update(null, updateWrapper);
    }

    /**
     * 工作流流程
     */
    public int workFlow(FinCustomerCashPledgeBill finCustomerCashPledgeBill) {
        int row = 1;
        Long[] sids = finCustomerCashPledgeBill.getCashPledgeBillSidList();
        // 处理状态
        String handleStatus = finCustomerCashPledgeBill.getHandleStatus();
        if (StrUtil.isNotBlank(finCustomerCashPledgeBill.getOperateType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(finCustomerCashPledgeBill.getOperateType());
        }
        if (sids != null && sids.length > 0) {
            // 获取数据
            List<FinCustomerCashPledgeBill> billList = finCustomerCashPledgeBillMapper.selectFinCustomerCashPledgeBillList
                    (new FinCustomerCashPledgeBill().setCashPledgeBillSidList(sids));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus,
                        ConstantsTable.TABLE_FIN_CUSTOEMR_CASH_PLEDGE_BILL);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(finCustomerCashPledgeBill.getOperateType())) {
                // 修改处理状态
                row = this.updateHandle(sids, ConstantsEms.SUBMIT_STATUS);
                // 开启工作流
                for (int i = 0; i < billList.size(); i++) {
                    this.submit(billList.get(i));
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getCashPledgeBillSid(),
                            BusinessType.SUBMIT.getValue(), null, TITLE, finCustomerCashPledgeBill.getComment());
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(finCustomerCashPledgeBill.getOperateType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                for (int i = 0; i < billList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(billList.get(i).getCashPledgeBillSid().toString());
                    taskVo.setFormId(Long.valueOf(billList.get(i).getCashPledgeBillSid().toString()));
                    taskVo.setFormCode(billList.get(i).getCashPledgeBillCode().toString());
                    taskVo.setFormType(FormType.CustomerCashPledgeBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finCustomerCashPledgeBill.getComment());
                    try {
                        SysFormProcess process = workflowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getCashPledgeBillSid()}, ConstantsEms.CHECK_STATUS);
                            // 确认后操作
                            List<FinCustomerCashPledgeBillItem> itemList = finCustomerCashPledgeBillItemMapper.selectFinCustomerCashPledgeBillItemList
                                    (new FinCustomerCashPledgeBillItem().setCashPledgeBillSid(billList.get(i).getCashPledgeBillSid()));
                            confirmAfter(itemList);
                        }
                        finCustomerCashPledgeBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getCashPledgeBillSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, finCustomerCashPledgeBill.getComment());
                }
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(finCustomerCashPledgeBill.getOperateType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                // 审批意见
                String comment = "";
                for (int i = 0; i < billList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setTargetKey("2");
                    taskVo.setBusinessKey(billList.get(i).getCashPledgeBillSid().toString());
                    taskVo.setFormId(Long.valueOf(billList.get(i).getCashPledgeBillSid().toString()));
                    taskVo.setFormCode(billList.get(i).getCashPledgeBillCode().toString());
                    taskVo.setFormType(FormType.CustomerCashPledgeBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finCustomerCashPledgeBill.getComment());
                    try {
                        SysFormProcess process = workflowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getCashPledgeBillSid()}, HandleStatus.RETURNED.getCode());
                        }
                        finCustomerCashPledgeBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getCashPledgeBillSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, finCustomerCashPledgeBill.getComment());
                }
            }
        }
        return row;
    }

    /**
     * 设置确认信息
     *
     * @param entity
     * @return
     */
    public FinCustomerCashPledgeBill setConfirmedInfo(FinCustomerCashPledgeBill entity) {
        if (CollectionUtils.isEmpty(entity.getItemList())){
            throw new CustomException("明细不能为空!");
        }
        if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
            //确认人，确认日期
            entity.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            entity.setConfirmDate(new Date());
        }
        if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQ.equals(entity.getDocumentType())
                || ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZF.equals(entity.getDocumentType())){
            if (CollectionUtils.isNotEmpty(entity.getItemList())){
                entity.getItemList().forEach(item->{
                    if (BigDecimal.ZERO.compareTo(item.getCurrencyAmount()) >= 0){
                        throw new BaseException("押金金额必须大于 0 ！");
                    }
                    if (item.getCashPledgeBillItemSid() == null){
                        item.setCurrencyAmountThz(BigDecimal.ZERO).setCurrencyAmountYth(BigDecimal.ZERO);
                    }
                });
            }
        }
        if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(entity.getDocumentType())
                || ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(entity.getDocumentType())){
            if (CollectionUtils.isNotEmpty(entity.getItemList())){
                entity.getItemList().forEach(item->{
                    if (BigDecimal.ZERO.compareTo(item.getCurrencyAmount()) >= 0){
                        throw new BaseException("本次退回金额必须大于 0 ！");
                    }
                    if (item.getCurrencyAmount().compareTo(item.getPreCurrencyAmountDth()) > 0){
                        throw new BaseException("明细中本次退回金额不能大于待退回金额！");
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
     * 修改来源主表的退回状态
     * item 的 preCashPledgeBillSid
     */
    public void updatePreReturnStatus(Long preCashPledgeBillSid) {
        if (preCashPledgeBillSid == null) {
            return;
        }
        //修改来源主表的状态
        //得到来源主表的所有明细
        List<FinCustomerCashPledgeBillItem> itemList = finCustomerCashPledgeBillItemMapper.selectList(new QueryWrapper<FinCustomerCashPledgeBillItem>()
                .lambda().eq(FinCustomerCashPledgeBillItem::getCashPledgeBillSid, preCashPledgeBillSid));
        //获得来源主表应有的状态
        String returnStatus = checkStatus(itemList);
        FinCustomerCashPledgeBill finCustomerCashPledgeBill = new FinCustomerCashPledgeBill().setReturnStatus(returnStatus)
                .setCashPledgeBillSid(preCashPledgeBillSid);
        finCustomerCashPledgeBillMapper.updateById(finCustomerCashPledgeBill);
    }

    /**
     * 处理退回
     *
     * @param entity
     * @UNFREEZE
     */
    public String checkStatus(List<FinCustomerCashPledgeBillItem> list){
        int flag = -1;
        String returnStatus = ConstantsFinance.RETURN_STATUS_WTH;
        BigDecimal sumAccount = BigDecimal.ZERO;
        BigDecimal sumYth = BigDecimal.ZERO;
        BigDecimal sumThz = BigDecimal.ZERO;
        for (FinCustomerCashPledgeBillItem item : list){
            sumAccount = sumAccount.add(item.getCurrencyAmount());
            sumYth = sumYth.add(item.getCurrencyAmountYth());
            sumThz = sumThz.add(item.getCurrencyAmountThz());
        }
        if (sumAccount.compareTo(sumYth) == 0){
            returnStatus = ConstantsFinance.RETURN_STATUS_QBTH;
        }
        else {
            returnStatus = ConstantsFinance.RETURN_STATUS_BFTH;
        }
        if ((sumYth.add(sumThz)).compareTo(BigDecimal.ZERO) == 0){
            returnStatus = ConstantsFinance.RETURN_STATUS_WTH;
        }
        return returnStatus;
    }

    /**
     * 删除子表
     */
    public void deleteItem(List<Long> cashPledgeBillSids) {
        //明细表
        finCustomerCashPledgeBillItemMapper.delete(new QueryWrapper<FinCustomerCashPledgeBillItem>().lambda().in(FinCustomerCashPledgeBillItem::getCashPledgeBillSid,cashPledgeBillSids));
        //附件表
        finCustomerCashPledgeBillAttachMapper.delete(new QueryWrapper<FinCustomerCashPledgeBillAttach>().lambda().in(FinCustomerCashPledgeBillAttach::getCashPledgeBillSid,cashPledgeBillSids));
    }

    /**
     * 回退押金明细来源
     */
    public void returnChild(List<Long> cashPledgeBillSids){
        if (CollectionUtils.isEmpty(cashPledgeBillSids)) {
            return;
        }
        List<FinCustomerCashPledgeBillItem> itemList = finCustomerCashPledgeBillItemMapper.selectFinCustomerCashPledgeBillItemList(
                new FinCustomerCashPledgeBillItem().setCashPledgeBillSidList(cashPledgeBillSids.toArray(new Long[cashPledgeBillSids.size()])));
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        itemList.forEach(item->{
            //押金明细来源
            FinCustomerCashPledgeBillItem original = finCustomerCashPledgeBillItemMapper.selectFinCustomerCashPledgeBillItemById(item.getPreCashPledgeBillItemSid());
            if (original != null){
                if (ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus()) || ConstantsEms.INVALID_STATUS.equals(item.getHandleStatus())){
                    original.setCurrencyAmountYth(original.getCurrencyAmountYth().subtract(item.getCurrencyAmount()));
                }
                if (!ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus()) && !ConstantsEms.INVALID_STATUS.equals(item.getHandleStatus())){
                    original.setCurrencyAmountThz(original.getCurrencyAmountThz().subtract(item.getCurrencyAmount()));
                }
                original.setReturnStatus(ConstantsFinance.RETURN_STATUS_BFTH);
                if ((original.getCurrencyAmountYth().add(original.getCurrencyAmountThz())).compareTo(BigDecimal.ZERO) == 0){
                    original.setReturnStatus(ConstantsFinance.RETURN_STATUS_WTH);
                }
                if (original.getCurrencyAmountYth().compareTo(original.getCurrencyAmount()) == 0){
                    original.setReturnStatus(ConstantsFinance.RETURN_STATUS_QBTH);
                }
                finCustomerCashPledgeBillItemMapper.updateAllById(original);
            }
        });
        // 修改来源主表的退回状态
        List<Long> preCashPledgeBillSidList = itemList.stream().map(FinCustomerCashPledgeBillItem::getPreCashPledgeBillSid).distinct().collect(Collectors.toList());
        for (Long preCashPledgeBillSid : preCashPledgeBillSidList) {
            updatePreReturnStatus(preCashPledgeBillSid);
        }
    }

    /**
     * 添加子表
     *
     * @param itemList
     * @param atmList
     * @param sid
     */
    public void insertChild(List<FinCustomerCashPledgeBillItem> itemList, List<FinCustomerCashPledgeBillAttach> atmList, FinCustomerCashPledgeBill entity) {
        Long sid = entity.getCashPledgeBillSid();
        //退回类型单据的操作
        if ((ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(entity.getDocumentType())
                || ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(entity.getDocumentType()))
                && CollectionUtils.isNotEmpty(itemList)){
            //明细表
            List<FinCustomerCashPledgeBillItem> sortList = itemList.stream().sorted(Comparator.comparing(FinCustomerCashPledgeBillItem::getItemNum, Comparator.nullsFirst(Long::compareTo)).reversed()).collect(Collectors.toList());
            Long maxNum = sortList.get(0).getItemNum();
            if (maxNum == null || maxNum.equals(0)){
                maxNum = new Long("0");
            }
            for (FinCustomerCashPledgeBillItem item : itemList) {
                item.setCashPledgeBillSid(sid);
                item.setCurrencyAmountThz(null);
                item.setCurrencyAmountYth(null);
                item.setReturnStatus(null);
                if (item.getItemNum() == null){
                    maxNum = maxNum + new Long("1");
                    item.setItemNum(maxNum);
                }
            }
            finCustomerCashPledgeBillItemMapper.inserts(itemList);
        }
        //非退回类型的单据操作
        if ((ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQ.equals(entity.getDocumentType())
                || ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZF.equals(entity.getDocumentType()))
                && CollectionUtils.isNotEmpty(itemList)){
            if (ConstantsEms.CHECK_STATUS.equals(entity.getHandleStatus())){
                //明细表
                List<FinCustomerCashPledgeBillItem> sortList = itemList.stream().sorted(Comparator.comparing(FinCustomerCashPledgeBillItem::getItemNum, Comparator.nullsFirst(Long::compareTo)).reversed()).collect(Collectors.toList());
                Long maxNum = sortList.get(0).getItemNum();
                if (maxNum == null || maxNum.equals(0)){
                    maxNum = new Long("0");
                }
                for (FinCustomerCashPledgeBillItem item : itemList) {
                    item.setCashPledgeBillSid(sid);
                    if (item.getCashPledgeBillItemSid() == null){
                        item.setReturnStatus(ConstantsFinance.RETURN_STATUS_WTH);
                    }
                    if (item.getItemNum() == null){
                        maxNum = maxNum + new Long("1");
                        item.setItemNum(maxNum);
                    }
                };
                finCustomerCashPledgeBillItemMapper.inserts(itemList);
            }
            if (!ConstantsEms.CHECK_STATUS.equals(entity.getHandleStatus())){
                //明细表
                List<FinCustomerCashPledgeBillItem> sortList = itemList.stream().sorted(Comparator.comparing(FinCustomerCashPledgeBillItem::getItemNum, Comparator.nullsFirst(Long::compareTo)).reversed()).collect(Collectors.toList());
                Long maxNum = sortList.get(0).getItemNum();
                if (maxNum == null || maxNum.equals(0)){
                    maxNum = new Long("0");
                }
                for (FinCustomerCashPledgeBillItem item : itemList) {
                    item.setCashPledgeBillSid(sid);
                    if (item.getCashPledgeBillItemSid() == null) {
                        item.setReturnStatus(ConstantsFinance.RETURN_STATUS_WTH);
                    }
                    if (item.getItemNum() == null) {
                        maxNum = maxNum + new Long("1");
                        item.setItemNum(maxNum);
                    }
                };
                finCustomerCashPledgeBillItemMapper.inserts(itemList);
            }
        }
        //附件表
        if (CollectionUtils.isNotEmpty(atmList)) {
            atmList.forEach(item -> {
                item.setCashPledgeBillSid(sid);
            });
            finCustomerCashPledgeBillAttachMapper.inserts(atmList);
        }
    }

    /**
     * 更新押金来源明细
     *
     * @param itemList
     * @param atmList
     * @param sid
     */
    public void updateChild(FinCustomerCashPledgeBill entity) {
        Long sid = entity.getCashPledgeBillSid();
        //明细表
        if (CollectionUtils.isNotEmpty(entity.getItemList())) {
            entity.getItemList().forEach(item -> {
                FinCustomerCashPledgeBillItem cashPledgeBillItem = new FinCustomerCashPledgeBillItem();
                cashPledgeBillItem.setCashPledgeBillItemSid(item.getPreCashPledgeBillItemSid());
                //原押金明细
                cashPledgeBillItem = finCustomerCashPledgeBillItemMapper.selectFinCustomerCashPledgeBillItemById(item.getPreCashPledgeBillItemSid());
                if (ConstantsEms.CHECK_STATUS.equals(entity.getHandleStatus())){
                    cashPledgeBillItem.setCurrencyAmountYth(cashPledgeBillItem.getCurrencyAmountYth().add(item.getCurrencyAmount()));
                    if (cashPledgeBillItem.getCurrencyAmountYth().compareTo(cashPledgeBillItem.getCurrencyAmount()) == 0){
                        cashPledgeBillItem.setReturnStatus(ConstantsFinance.RETURN_STATUS_QBTH);
                    }
                    else {
                        cashPledgeBillItem.setReturnStatus(ConstantsFinance.RETURN_STATUS_BFTH);
                    }
                }
                if (!ConstantsEms.CHECK_STATUS.equals(entity.getHandleStatus())){
                    cashPledgeBillItem.setCurrencyAmountThz(cashPledgeBillItem.getCurrencyAmountThz().add(item.getCurrencyAmount()));
                    cashPledgeBillItem.setReturnStatus(ConstantsFinance.RETURN_STATUS_BFTH);
                }
                finCustomerCashPledgeBillItemMapper.updateAllById(cashPledgeBillItem);
                //修改来源主表的状态
                updatePreReturnStatus(item.getPreCashPledgeBillSid());
            });
        }
    }

    /**
     * 作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int invalid(Long cashPledgeBillSid){
        FinCustomerCashPledgeBill bill = finCustomerCashPledgeBillMapper.selectById(cashPledgeBillSid);
        if (!HandleStatus.CONFIRMED.getCode().equals(bill.getHandleStatus())){
            throw new BaseException("请选择处理状态是“已确认”的单据!");
        }
        if ((ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZF.equals(bill.getDocumentType()) || ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQ.equals(bill.getDocumentType()))
                && !ConstantsFinance.RETURN_STATUS_WTH.equals(bill.getReturnStatus())){
            throw new BaseException("押金已开始退回，无法作废!");
        }
        int i = finCustomerCashPledgeBillMapper.updateAllById(bill.setHandleStatus(HandleStatus.INVALID.getCode()));
        if (i > 0 && (ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(bill.getDocumentType()) || ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(bill.getDocumentType()))){
            List<Long> sids = new ArrayList<>();
            sids.add(cashPledgeBillSid);
            returnChild(sids);
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbUtil.insertUserLog(bill.getCashPledgeBillSid(), BusinessType.CANCEL.getValue(), msgList, TITLE);
        return i;
    }


}
