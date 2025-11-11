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
import com.platform.ems.service.IFinVendorCashPledgeBillService;
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
 * 供应商押金Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Service
@SuppressWarnings("all")
public class FinVendorCashPledgeBillServiceImpl extends ServiceImpl<FinVendorCashPledgeBillMapper, FinVendorCashPledgeBill> implements IFinVendorCashPledgeBillService {

    @Autowired
    private FinVendorCashPledgeBillMapper finVendorCashPledgeBillMapper;
    @Autowired
    private FinVendorCashPledgeBillItemMapper finVendorCashPledgeBillItemMapper;
    @Autowired
    private FinVendorCashPledgeBillAttachMapper finVendorCashPledgeBillAttachMapper;
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

    private static final String TITLE = "供应商押金";

    /**
     * 查询供应商押金
     *
     * @param cashPledgeBillSid 供应商押金ID
     * @return 供应商押金
     */
    @Override
    public FinVendorCashPledgeBill selectFinVendorCashPledgeBillById(Long cashPledgeBillSid) {
        FinVendorCashPledgeBill finVendorCashPledgeBill = finVendorCashPledgeBillMapper.selectFinVendorCashPledgeBillById(cashPledgeBillSid);
        if (finVendorCashPledgeBill == null) {
            return new FinVendorCashPledgeBill();
        }
        //明细
        FinVendorCashPledgeBillItem item = new FinVendorCashPledgeBillItem();
        item.setCashPledgeBillSid(cashPledgeBillSid);
        List<FinVendorCashPledgeBillItem> finVendorCashPledgeBillItemList = finVendorCashPledgeBillItemMapper.selectFinVendorCashPledgeBillItemList(item);
        finVendorCashPledgeBillItemList = finVendorCashPledgeBillItemList.stream().sorted(Comparator.comparing(FinVendorCashPledgeBillItem::getItemNum,
                Comparator.nullsLast(Long::compareTo))).collect(Collectors.toList());
        finVendorCashPledgeBill.setItemList(finVendorCashPledgeBillItemList);
        //附件
        finVendorCashPledgeBill.setAttachmentList(new ArrayList<>());
        List<FinVendorCashPledgeBillAttach> finVendorCashPledgeBillAttachListList =
                finVendorCashPledgeBillAttachMapper.selectFinVendorCashPledgeBillAttachList(new FinVendorCashPledgeBillAttach().setCashPledgeBillSid(cashPledgeBillSid));
        if (CollectionUtils.isNotEmpty(finVendorCashPledgeBillAttachListList)) {
            finVendorCashPledgeBill.setAttachmentList(finVendorCashPledgeBillAttachListList);
        }
        MongodbUtil.find(finVendorCashPledgeBill);
        return finVendorCashPledgeBill;
    }

    /**
     * 查询供应商押金列表
     *
     * @param finVendorCashPledgeBill 供应商押金
     * @return 供应商押金
     */
    @Override
    public List<FinVendorCashPledgeBill> selectFinVendorCashPledgeBillList(FinVendorCashPledgeBill finVendorCashPledgeBill) {
        List<FinVendorCashPledgeBill> response = finVendorCashPledgeBillMapper.selectFinVendorCashPledgeBillList(finVendorCashPledgeBill);
        return response;
    }

    /**
     * 新增供应商押金
     * 需要注意编码重复校验
     *
     * @param finVendorCashPledgeBill 供应商押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinVendorCashPledgeBill(FinVendorCashPledgeBill finVendorCashPledgeBill) {
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finVendorCashPledgeBill.getHandleStatus())) {
            SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
            if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowGysyj())) {
                // 不需要走审批，提交及确认
                finVendorCashPledgeBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        //设置确认信息，校验
        setConfirmedInfo(finVendorCashPledgeBill);
        if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZF.equals(finVendorCashPledgeBill.getDocumentType())
            || ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQ.equals(finVendorCashPledgeBill.getDocumentType())){
            finVendorCashPledgeBill.setReturnStatus(ConstantsFinance.RETURN_STATUS_WTH);
        }
        int row = finVendorCashPledgeBillMapper.insert(finVendorCashPledgeBill);
        if (row > 0) {
            // 找到code
            FinVendorCashPledgeBill bill = finVendorCashPledgeBillMapper.selectById(finVendorCashPledgeBill.getCashPledgeBillSid());
            finVendorCashPledgeBill.setCashPledgeBillCode(bill.getCashPledgeBillCode());
            //插入子表，附件表
            insertChild(finVendorCashPledgeBill.getItemList(), finVendorCashPledgeBill.getAttachmentList(), finVendorCashPledgeBill);
            //更新原押金明细
            if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(finVendorCashPledgeBill.getDocumentType())
                || ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(finVendorCashPledgeBill.getDocumentType())){
                updateChild(finVendorCashPledgeBill);
            }
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(finVendorCashPledgeBill.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_FIN_VENDOR_CASH_PLEDGE_BILL)
                        .setDocumentSid(finVendorCashPledgeBill.getCashPledgeBillSid());
                sysTodoTask.setTitle("供应商押金: " + finVendorCashPledgeBill.getCashPledgeBillCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(finVendorCashPledgeBill.getCashPledgeBillCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskService.insertSysTodoTaskMenu(sysTodoTask, ConstantsWorkbench.TODO_FIN_VEN_CASH_INFO);
            }
            // 走提交审批，参数从查询页面提交按钮参考
            else if (ConstantsEms.SUBMIT_STATUS.equals(finVendorCashPledgeBill.getHandleStatus())) {
                this.submit(finVendorCashPledgeBill);
            }
            else if (ConstantsEms.CHECK_STATUS.equals(finVendorCashPledgeBill.getHandleStatus())) {
                // 确认后操作
                confirmAfter(finVendorCashPledgeBill.getItemList());
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(finVendorCashPledgeBill.getCashPledgeBillSid(), finVendorCashPledgeBill.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改供应商押金
     *
     * @param finVendorCashPledgeBill 供应商押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinVendorCashPledgeBill(FinVendorCashPledgeBill finVendorCashPledgeBill) {
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finVendorCashPledgeBill.getHandleStatus())) {
            SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
            if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowGysyj())) {
                // 不需要走审批，提交及确认
                finVendorCashPledgeBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        //设置确认信息，校验
        setConfirmedInfo(finVendorCashPledgeBill);
        FinVendorCashPledgeBill response = finVendorCashPledgeBillMapper.selectFinVendorCashPledgeBillById(finVendorCashPledgeBill.getCashPledgeBillSid());
        List<Long> sids = new ArrayList<>();
        sids.add(finVendorCashPledgeBill.getCashPledgeBillSid());
        if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(finVendorCashPledgeBill.getDocumentType())
                || ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(finVendorCashPledgeBill.getDocumentType())){
            //暂时回退押金明细来源，会在更新原押金明细的时候根据实际再处理
            returnChild(sids);
        }
        int row = finVendorCashPledgeBillMapper.updateAllById(finVendorCashPledgeBill);
        if (row > 0) {
            //删除子表，附件表
            deleteItem(sids);
            //插入子表，附件表
            insertChild(finVendorCashPledgeBill.getItemList(), finVendorCashPledgeBill.getAttachmentList(), finVendorCashPledgeBill);
            //更新原押金明细
            if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(finVendorCashPledgeBill.getDocumentType())
                    || ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(finVendorCashPledgeBill.getDocumentType())){
                updateChild(finVendorCashPledgeBill);
            }
            //不是保存状态时删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(finVendorCashPledgeBill.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, finVendorCashPledgeBill.getCashPledgeBillSid()));
            }
            // 走提交审批，参数从查询页面提交按钮参考
            if (ConstantsEms.SUBMIT_STATUS.equals(finVendorCashPledgeBill.getHandleStatus())) {
                this.submit(finVendorCashPledgeBill);
            }
            else if (ConstantsEms.CHECK_STATUS.equals(finVendorCashPledgeBill.getHandleStatus())) {
                // 确认后操作
                confirmAfter(finVendorCashPledgeBill.getItemList());
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finVendorCashPledgeBill);
            MongodbDeal.update(finVendorCashPledgeBill.getCashPledgeBillSid(), response.getHandleStatus(), finVendorCashPledgeBill.getHandleStatus(), msgList, TITLE,null);

        }
        return row;
    }

    /**
     * 变更供应商押金
     *
     * @param finVendorCashPledgeBill 供应商押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinVendorCashPledgeBill(FinVendorCashPledgeBill finVendorCashPledgeBill) {
        //设置确认信息，校验
        setConfirmedInfo(finVendorCashPledgeBill);
        FinVendorCashPledgeBill response = finVendorCashPledgeBillMapper.selectFinVendorCashPledgeBillById(finVendorCashPledgeBill.getCashPledgeBillSid());
        List<Long> sids = new ArrayList<>();
        sids.add(finVendorCashPledgeBill.getCashPledgeBillSid());
        if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(finVendorCashPledgeBill.getDocumentType())
                || ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(finVendorCashPledgeBill.getDocumentType())){
            //暂时回退押金明细来源，会在更新原押金明细的时候根据实际再处理
            returnChild(sids);
        }
        int row = finVendorCashPledgeBillMapper.updateAllById(finVendorCashPledgeBill);
        if (row > 0) {
            //删除子表，附件表
            deleteItem(sids);
            //插入子表，附件表
            insertChild(finVendorCashPledgeBill.getItemList(), finVendorCashPledgeBill.getAttachmentList(), finVendorCashPledgeBill);
            //更新原押金明细
            if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(finVendorCashPledgeBill.getDocumentType())
                    || ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(finVendorCashPledgeBill.getDocumentType())){
                updateChild(finVendorCashPledgeBill);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finVendorCashPledgeBill);
            MongodbUtil.insertUserLog(finVendorCashPledgeBill.getCashPledgeBillSid(), BusinessType.CHANGE.getValue(), msgList,TITLE);
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
    public int deleteFinVendorCashPledgeBillByIds(List<Long> cashPledgeBillSids) {
        if (CollectionUtils.isEmpty(cashPledgeBillSids)){
            throw new BaseException("请选择行！");
        }
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(HandleStatus.SAVE.getCode());
        handleStatusList.add(HandleStatus.RETURNED.getCode());
        List<FinVendorCashPledgeBill> finVendorFundsFreezeBillList = finVendorCashPledgeBillMapper.selectList(new QueryWrapper<FinVendorCashPledgeBill>().lambda()
                .in(FinVendorCashPledgeBill::getCashPledgeBillSid,cashPledgeBillSids)
                .notIn(FinVendorCashPledgeBill::getHandleStatus, handleStatusList));
        if (CollectionUtils.isNotEmpty(finVendorFundsFreezeBillList)){
            throw new BaseException("仅保存状态和退回状态允许删除操作！");
        }
        List<FinVendorCashPledgeBill> list = finVendorCashPledgeBillMapper.selectList(new QueryWrapper<FinVendorCashPledgeBill>().lambda()
                .in(FinVendorCashPledgeBill::getCashPledgeBillSid,cashPledgeBillSids));
        int i = finVendorCashPledgeBillMapper.deleteBatchIds(cashPledgeBillSids);
        if (i > 0) {
            cashPledgeBillSids.forEach(sid -> {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
            });
            List<Long> sids = new ArrayList<>();
            list.forEach(item->{
                if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(item.getDocumentType())
                        || ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(item.getDocumentType())){
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
    public int check(FinVendorCashPledgeBill finVendorCashPledgeBill) {
        int row = 0;
        Long[] sids = finVendorCashPledgeBill.getCashPledgeBillSidList();
        if (sids != null && sids.length > 0) {
            Map<Long, List<FinVendorCashPledgeBillItem>> itemMap = new HashMap<>();
            for (Long sid : sids) {
                List<FinVendorCashPledgeBillItem> itemList = finVendorCashPledgeBillItemMapper.selectFinVendorCashPledgeBillItemList(
                        (new FinVendorCashPledgeBillItem().setCashPledgeBillSid(sid)));
                if (CollectionUtils.isEmpty(itemList)){
                    throw new CustomException("明细不能为空!");
                }
                itemMap.put(sid, itemList);
            }
            // 判断是否要走审批
            if (BusinessType.SUBMIT.getValue().equals(finVendorCashPledgeBill.getOperateType())) {
                SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                        .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
                if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowGysyj())) {
                    // 不需要走审批，提交及确认
                    LambdaUpdateWrapper<FinVendorCashPledgeBill> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.in(FinVendorCashPledgeBill::getCashPledgeBillSid,sids)
                            .set(FinVendorCashPledgeBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                            .set(FinVendorCashPledgeBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                            .set(FinVendorCashPledgeBill::getConfirmDate, new Date());
                    row = finVendorCashPledgeBillMapper.update(null, updateWrapper);
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
            row = workFlow(finVendorCashPledgeBill);
        }
        return row;
    }

    /**
     * 审批通过后处理
     */
    public void confirmAfter(List<FinVendorCashPledgeBillItem> itemList) {
        //过滤重复操作的单据
        if (CollectionUtils.isNotEmpty(itemList)){
            Set<Long> preCashPledgeBillSids = new HashSet<>();
            for (FinVendorCashPledgeBillItem item : itemList) {
                if (item.getPreCashPledgeBillItemSid() != null){
                    //押金明细来源
                    FinVendorCashPledgeBillItem original = finVendorCashPledgeBillItemMapper.selectFinVendorCashPledgeBillItemById(item.getPreCashPledgeBillItemSid());
                    original.setCurrencyAmountYth(original.getCurrencyAmountYth().add(item.getCurrencyAmount()));
                    original.setCurrencyAmountThz(original.getCurrencyAmountThz().subtract(item.getCurrencyAmount()));
                    if (original.getCurrencyAmountYth().compareTo(original.getCurrencyAmount()) == 0){
                        original.setReturnStatus(ConstantsFinance.RETURN_STATUS_QBTH);
                    }
                    finVendorCashPledgeBillItemMapper.updateAllById(original);
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
    private void submit(FinVendorCashPledgeBill finVendorCashPledgeBill){
        Map<String, Object> variables = new HashMap<>();
        variables.put("formCode", finVendorCashPledgeBill.getCashPledgeBillCode());
        variables.put("formId", finVendorCashPledgeBill.getCashPledgeBillSid());
        variables.put("formType", FormType.VendorCashPledgeBill.getCode());
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
        LambdaUpdateWrapper<FinVendorCashPledgeBill> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinVendorCashPledgeBill::getCashPledgeBillSid, sids);
        updateWrapper.set(FinVendorCashPledgeBill::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(FinVendorCashPledgeBill::getConfirmDate, new Date());
            updateWrapper.set(FinVendorCashPledgeBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return finVendorCashPledgeBillMapper.update(null, updateWrapper);
    }

    /**
     * 工作流流程
     */
    public int workFlow(FinVendorCashPledgeBill finVendorCashPledgeBill) {
        int row = 1;
        Long[] sids = finVendorCashPledgeBill.getCashPledgeBillSidList();
        // 处理状态
        String handleStatus = finVendorCashPledgeBill.getHandleStatus();
        if (StrUtil.isNotBlank(finVendorCashPledgeBill.getOperateType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(finVendorCashPledgeBill.getOperateType());
        }
        if (sids != null && sids.length > 0) {
            // 获取数据
            List<FinVendorCashPledgeBill> billList = finVendorCashPledgeBillMapper.selectFinVendorCashPledgeBillList
                    (new FinVendorCashPledgeBill().setCashPledgeBillSidList(sids));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus,
                        ConstantsTable.TABLE_FIN_VENDOR_CASH_PLEDGE_BILL);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(finVendorCashPledgeBill.getOperateType())) {
                // 修改处理状态
                row = this.updateHandle(sids, ConstantsEms.SUBMIT_STATUS);
                // 开启工作流
                for (int i = 0; i < billList.size(); i++) {
                    this.submit(billList.get(i));
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getCashPledgeBillSid(),
                            BusinessType.SUBMIT.getValue(), null, TITLE, finVendorCashPledgeBill.getComment());
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(finVendorCashPledgeBill.getOperateType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                for (int i = 0; i < billList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(billList.get(i).getCashPledgeBillSid().toString());
                    taskVo.setFormId(Long.valueOf(billList.get(i).getCashPledgeBillSid().toString()));
                    taskVo.setFormCode(billList.get(i).getCashPledgeBillCode().toString());
                    taskVo.setFormType(FormType.VendorCashPledgeBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finVendorCashPledgeBill.getComment());
                    try {
                        SysFormProcess process = workflowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getCashPledgeBillSid()}, ConstantsEms.CHECK_STATUS);
                            // 确认后操作
                            List<FinVendorCashPledgeBillItem> itemList = finVendorCashPledgeBillItemMapper.selectFinVendorCashPledgeBillItemList(
                                    (new FinVendorCashPledgeBillItem().setCashPledgeBillSid(billList.get(i).getCashPledgeBillSid())));
                            confirmAfter(itemList);
                        }
                        finVendorCashPledgeBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getCashPledgeBillSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, finVendorCashPledgeBill.getComment());
                }
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(finVendorCashPledgeBill.getOperateType())) {
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
                    taskVo.setFormType(FormType.VendorCashPledgeBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finVendorCashPledgeBill.getComment());
                    try {
                        SysFormProcess process = workflowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getCashPledgeBillSid()}, HandleStatus.RETURNED.getCode());
                        }
                        finVendorCashPledgeBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getCashPledgeBillSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, finVendorCashPledgeBill.getComment());
                }
            }
        }
        return 1;
    }

    /**
     * 设置确认信息
     *
     * @param entity
     * @return
     */
    public FinVendorCashPledgeBill setConfirmedInfo(FinVendorCashPledgeBill entity) {
        if (CollectionUtils.isEmpty(entity.getItemList())){
            throw new CustomException("明细不能为空!");
        }
        if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
            //确认人，确认日期
            entity.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            entity.setConfirmDate(new Date());
        }
        if (!ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(entity.getDocumentType())
                || ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(entity.getDocumentType())){
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
        if (ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(entity.getDocumentType())
                || ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(entity.getDocumentType())){
            if (CollectionUtils.isNotEmpty(entity.getItemList())){
                entity.getItemList().forEach(item->{
                    if (BigDecimal.ZERO.compareTo(item.getCurrencyAmount()) >= 0){
                        throw new BaseException("本次退回金额金额必须大于 0 ！");
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
     * 修改来源主表的退回状态
     * item 的 preCashPledgeBillSid
     */
    public void updatePreReturnStatus(Long preCashPledgeBillSid) {
        List<FinVendorCashPledgeBillItem> originalList = finVendorCashPledgeBillItemMapper.selectList(new QueryWrapper<FinVendorCashPledgeBillItem>()
                .lambda().eq(FinVendorCashPledgeBillItem::getCashPledgeBillSid, preCashPledgeBillSid));
        //获得来源主表应有的状态
        String returnStatus = checkStatus(originalList);
        FinVendorCashPledgeBill originalBill = new FinVendorCashPledgeBill().setReturnStatus(returnStatus)
                .setCashPledgeBillSid(preCashPledgeBillSid);
        finVendorCashPledgeBillMapper.updateById(originalBill);
    }

    /**
     * 处理退回
     *
     * @param entity
     * @UNFREEZE
     */
    public String checkStatus(List<FinVendorCashPledgeBillItem> list){
        int flag = -1;
        String returnStatus = ConstantsFinance.RETURN_STATUS_WTH;
        BigDecimal sumAccount = BigDecimal.ZERO;
        BigDecimal sumYth = BigDecimal.ZERO;
        BigDecimal sumThz = BigDecimal.ZERO;
        for (FinVendorCashPledgeBillItem item : list){
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
        finVendorCashPledgeBillItemMapper.delete(new QueryWrapper<FinVendorCashPledgeBillItem>().lambda().in(FinVendorCashPledgeBillItem::getCashPledgeBillSid,cashPledgeBillSids));
        //附件表
        finVendorCashPledgeBillAttachMapper.delete(new QueryWrapper<FinVendorCashPledgeBillAttach>().lambda().in(FinVendorCashPledgeBillAttach::getCashPledgeBillSid,cashPledgeBillSids));
    }

    /**
     * 回退押金明细来源
     */
    public void returnChild(List<Long> cashPledgeBillSids){
        if (CollectionUtils.isEmpty(cashPledgeBillSids)) {
            return;
        }
        List<FinVendorCashPledgeBillItem> itemList = finVendorCashPledgeBillItemMapper.selectFinVendorCashPledgeBillItemList(
                new FinVendorCashPledgeBillItem().setCashPledgeBillSidList(cashPledgeBillSids.toArray(new Long[cashPledgeBillSids.size()])));
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        itemList.forEach(item->{
            //押金明细来源
            FinVendorCashPledgeBillItem original = finVendorCashPledgeBillItemMapper.selectFinVendorCashPledgeBillItemById(item.getPreCashPledgeBillItemSid());
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
                finVendorCashPledgeBillItemMapper.updateAllById(original);
            }
        });
        // 修改来源主表的退回状态
        List<Long> preCashPledgeBillSidList = itemList.stream().map(FinVendorCashPledgeBillItem::getPreCashPledgeBillSid).distinct().collect(Collectors.toList());
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
    public void insertChild(List<FinVendorCashPledgeBillItem> itemList, List<FinVendorCashPledgeBillAttach> atmList, FinVendorCashPledgeBill entity) {
        Long sid = entity.getCashPledgeBillSid();
        //退回类型单据的操作
        if ((ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZFTH.equals(entity.getDocumentType())
                || ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQTH.equals(entity.getDocumentType()))
                && CollectionUtils.isNotEmpty(itemList)){
            //明细表
            List<FinVendorCashPledgeBillItem> sortList = itemList.stream().sorted(Comparator.comparing(FinVendorCashPledgeBillItem::getItemNum, Comparator.nullsFirst(Long::compareTo)).reversed()).collect(Collectors.toList());
            Long maxNum = sortList.get(0).getItemNum();
            if (maxNum == null || maxNum.equals(0)){
                maxNum = new Long("0");
            }
            //明细表
            for (FinVendorCashPledgeBillItem item : itemList) {
                item.setCashPledgeBillSid(sid);
                item.setCurrencyAmountThz(null);
                item.setCurrencyAmountYth(null);
                item.setReturnStatus(null);
                if (item.getItemNum() == null){
                    maxNum = maxNum + new Long("1");
                    item.setItemNum(maxNum);
                }
            };
            finVendorCashPledgeBillItemMapper.inserts(itemList);
        }
        //非退回类型的单据操作
        if ((ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZF.equals(entity.getDocumentType())
                || ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQ.equals(entity.getDocumentType()))
                && CollectionUtils.isNotEmpty(itemList)){
            if (ConstantsEms.CHECK_STATUS.equals(entity.getHandleStatus())){
                List<FinVendorCashPledgeBillItem> sortList = itemList.stream().sorted(Comparator.comparing(FinVendorCashPledgeBillItem::getItemNum, Comparator.nullsFirst(Long::compareTo)).reversed()).collect(Collectors.toList());
                Long maxNum = sortList.get(0).getItemNum();
                if (maxNum == null || maxNum.equals(0)){
                    maxNum = new Long("0");
                }
                //明细表
                for (FinVendorCashPledgeBillItem item : itemList) {
                    item.setCashPledgeBillSid(sid);
                    if (item.getCashPledgeBillItemSid() == null){
                        item.setReturnStatus(ConstantsFinance.RETURN_STATUS_WTH);
                    }
                    if (item.getItemNum() == null){
                        maxNum = maxNum + new Long("1");
                        item.setItemNum(maxNum);
                    }
                };
                finVendorCashPledgeBillItemMapper.inserts(itemList);
            }
            if (!ConstantsEms.CHECK_STATUS.equals(entity.getHandleStatus())){
                List<FinVendorCashPledgeBillItem> sortList = itemList.stream().sorted(Comparator.comparing(FinVendorCashPledgeBillItem::getItemNum, Comparator.nullsFirst(Long::compareTo)).reversed()).collect(Collectors.toList());
                Long maxNum = sortList.get(0).getItemNum();
                if (maxNum == null || maxNum.equals(0)){
                    maxNum = new Long("0");
                }
                //明细表
                for (FinVendorCashPledgeBillItem item : itemList) {
                    item.setCashPledgeBillSid(sid);
                    if (item.getCashPledgeBillItemSid() == null){
                        item.setReturnStatus(ConstantsFinance.RETURN_STATUS_WTH);
                    }
                    if (item.getItemNum() == null){
                        maxNum = maxNum + new Long("1");
                        item.setItemNum(maxNum);
                    }
                };
                finVendorCashPledgeBillItemMapper.inserts(itemList);
            }
        }
        //附件表
        if (CollectionUtils.isNotEmpty(atmList)) {
            atmList.forEach(item -> {
                item.setCashPledgeBillSid(sid);
            });
            finVendorCashPledgeBillAttachMapper.inserts(atmList);
        }
    }

    /**
     * 更新押金来源明细
     *
     * @param itemList
     * @param atmList
     * @param sid
     */
    public void updateChild(FinVendorCashPledgeBill entity) {
        Long sid = entity.getCashPledgeBillSid();
        //明细表
        if (CollectionUtils.isNotEmpty(entity.getItemList())) {
            entity.getItemList().forEach(item -> {
                FinVendorCashPledgeBillItem cashPledgeBillItem = new FinVendorCashPledgeBillItem();
                cashPledgeBillItem.setCashPledgeBillItemSid(item.getPreCashPledgeBillItemSid());
                //原押金明细
                cashPledgeBillItem = finVendorCashPledgeBillItemMapper.selectFinVendorCashPledgeBillItemById(item.getPreCashPledgeBillItemSid());
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
                finVendorCashPledgeBillItemMapper.updateAllById(cashPledgeBillItem);
                //修改来源主表的状态
                this.updatePreReturnStatus(item.getPreCashPledgeBillSid());
            });
        }
    }

    /**
     * 作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int invalid(Long cashPledgeBillSid){
        FinVendorCashPledgeBill bill = finVendorCashPledgeBillMapper.selectById(cashPledgeBillSid);
        if (!HandleStatus.CONFIRMED.getCode().equals(bill.getHandleStatus())){
            throw new BaseException("请选择处理状态是“已确认”的单据!");
        }
        if ((ConstantsFinance.DOC_TYPE_CASHPLEDGE_ZF.equals(bill.getDocumentType()) || ConstantsFinance.DOC_TYPE_CASHPLEDGE_SQ.equals(bill.getDocumentType()))
                && !ConstantsFinance.RETURN_STATUS_WTH.equals(bill.getReturnStatus())){
            throw new BaseException("押金已开始退回，无法作废!");
        }
        int i = finVendorCashPledgeBillMapper.updateAllById(bill.setHandleStatus(HandleStatus.INVALID.getCode()));
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
