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
import com.platform.ems.service.IFinVendorFundsFreezeBillService;
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
 * 供应商暂押款Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Service
@SuppressWarnings("all")
public class FinVendorFundsFreezeBillServiceImpl extends ServiceImpl<FinVendorFundsFreezeBillMapper, FinVendorFundsFreezeBill> implements IFinVendorFundsFreezeBillService {
    @Autowired
    private FinVendorFundsFreezeBillMapper finVendorFundsFreezeBillMapper;
    @Autowired
    private FinVendorFundsFreezeBillItemMapper finVendorFundsFreezeBillItemMapper;
    @Autowired
    private FinVendorFundsFreezeBillAttachMapper finVendorFundsFreezeBillAttachMapper;
    @Autowired
    private BasCompanyMapper companyMapper;
    @Autowired
    private BasVendorMapper vendorMapper;
    @Autowired
    private SysDefaultSettingClientMapper defaultSettingClientMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISysTodoTaskService sysTodoTaskService;
    @Autowired
    private WorkFlowServiceImpl workflowService;


    private static final String TITLE = "供应商暂押款";

    /**
     * 查询供应商暂押
     *
     * @param FundsFreezeBillSid 供应商暂押ID
     * @UNFREEZE 供应商暂押
     */
    @Override
    public FinVendorFundsFreezeBill selectFinVendorFundsFreezeBillById(Long fundsFreezeBillSid) {
        FinVendorFundsFreezeBill finVendorFundsFreezeBill = finVendorFundsFreezeBillMapper.selectFinVendorFundsFreezeBillById(fundsFreezeBillSid);
        if (finVendorFundsFreezeBill == null) {
            return new FinVendorFundsFreezeBill();
        }
        //明细
        FinVendorFundsFreezeBillItem item = new FinVendorFundsFreezeBillItem();
        item.setFundsFreezeBillSid(fundsFreezeBillSid);
        List<FinVendorFundsFreezeBillItem> finVendorFundsFreezeBillItemList = finVendorFundsFreezeBillItemMapper.selectFinVendorFundsFreezeBillItemList(item);
        finVendorFundsFreezeBillItemList = finVendorFundsFreezeBillItemList.stream().sorted(Comparator.comparing(FinVendorFundsFreezeBillItem::getItemNum,
                Comparator.nullsLast(Long::compareTo))).collect(Collectors.toList());
        finVendorFundsFreezeBill.setItemList(finVendorFundsFreezeBillItemList);
        //附件
        finVendorFundsFreezeBill.setAttachmentList(new ArrayList<>());
        List<FinVendorFundsFreezeBillAttach> attachList =
                finVendorFundsFreezeBillAttachMapper.selectFinVendorFundsFreezeBillAttachList(new FinVendorFundsFreezeBillAttach().setFundsFreezeBillSid(fundsFreezeBillSid));
        if (CollectionUtils.isNotEmpty(attachList)) {
            finVendorFundsFreezeBill.setAttachmentList(attachList);
        }
        MongodbUtil.find(finVendorFundsFreezeBill);
        return finVendorFundsFreezeBill;
    }

    /**
     * 查询供应商暂押列表
     *
     * @param finVendorFundsFreezeBill 供应商暂押
     * @UNFREEZE 供应商暂押
     */
    @Override
    public List<FinVendorFundsFreezeBill> selectFinVendorFundsFreezeBillList(FinVendorFundsFreezeBill finVendorFundsFreezeBill) {
        List<FinVendorFundsFreezeBill> response = finVendorFundsFreezeBillMapper.selectFinVendorFundsFreezeBillList(finVendorFundsFreezeBill);
        return response;
    }

    /**
     * 新增供应商暂押
     * 需要注意编码重复校验
     *
     * @param finVendorFundsFreezeBill 供应商暂押
     * @UNFREEZE 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinVendorFundsFreezeBill(FinVendorFundsFreezeBill finVendorFundsFreezeBill) {
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finVendorFundsFreezeBill.getHandleStatus())) {
            SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
            if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowGyszyk())) {
                // 不需要走审批，提交及确认
                finVendorFundsFreezeBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        //设置确认信息，校验
        setConfirmedInfo(finVendorFundsFreezeBill);
        if (ConstantsFinance.DOC_TYPE_FREEZE_ZYK.equals(finVendorFundsFreezeBill.getDocumentType())){
            finVendorFundsFreezeBill.setUnfreezeStatus(ConstantsFinance.UNFREEZE_STATUS_WJD);
        }
        int row = finVendorFundsFreezeBillMapper.insert(finVendorFundsFreezeBill);
        if (row > 0) {
            FinVendorFundsFreezeBill bill = finVendorFundsFreezeBillMapper.selectById(finVendorFundsFreezeBill.getFundsFreezeBillSid());
            finVendorFundsFreezeBill.setFundsFreezeBillCode(bill.getFundsFreezeBillCode());
            //插入子表，附件表
            insertChild(finVendorFundsFreezeBill.getItemList(), finVendorFundsFreezeBill.getAttachmentList(), finVendorFundsFreezeBill);
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(finVendorFundsFreezeBill.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_FIN_VENDOR_FUNDS_FREEZE_BILL)
                        .setDocumentSid(finVendorFundsFreezeBill.getFundsFreezeBillSid());
                sysTodoTask.setTitle("供应商暂押款: " + finVendorFundsFreezeBill.getFundsFreezeBillCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(finVendorFundsFreezeBill.getFundsFreezeBillCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskService.insertSysTodoTaskMenu(sysTodoTask, ConstantsWorkbench.TODO_FIN_VEN_FREEZE_INFO);
            }
            // 走提交审批，参数从查询页面提交按钮参考
            else if (ConstantsEms.SUBMIT_STATUS.equals(finVendorFundsFreezeBill.getHandleStatus())) {
                this.submit(finVendorFundsFreezeBill);
            }
            //更新原暂押明细
            if (ConstantsFinance.DOC_TYPE_FREEZE_SF.equals(finVendorFundsFreezeBill.getDocumentType())){
                updateChild(finVendorFundsFreezeBill);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(finVendorFundsFreezeBill.getFundsFreezeBillSid(), finVendorFundsFreezeBill.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改供应商暂押
     *
     * @param finVendorFundsFreezeBill 供应商暂押
     * @UNFREEZE 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinVendorFundsFreezeBill(FinVendorFundsFreezeBill finVendorFundsFreezeBill) {
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finVendorFundsFreezeBill.getHandleStatus())) {
            SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
            if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowGyszyk())) {
                // 不需要走审批，提交及确认
                finVendorFundsFreezeBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        //设置确认信息，校验
        setConfirmedInfo(finVendorFundsFreezeBill);
        FinVendorFundsFreezeBill response = finVendorFundsFreezeBillMapper.selectFinVendorFundsFreezeBillById(finVendorFundsFreezeBill.getFundsFreezeBillSid());
        List<Long> sids = new ArrayList<>();
        sids.add(finVendorFundsFreezeBill.getFundsFreezeBillSid());
        if (ConstantsFinance.DOC_TYPE_FREEZE_SF.equals(finVendorFundsFreezeBill.getDocumentType())){
            //回退暂押明细来源
            returnChild(sids);
        }
        int row = finVendorFundsFreezeBillMapper.updateAllById(finVendorFundsFreezeBill);
        if (row > 0) {
            //删除子表，附件表
            deleteItem(sids);
            //插入子表，附件表
            insertChild(finVendorFundsFreezeBill.getItemList(), finVendorFundsFreezeBill.getAttachmentList(), finVendorFundsFreezeBill);
            //不是保存状态时删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(finVendorFundsFreezeBill.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, finVendorFundsFreezeBill.getFundsFreezeBillSid()));
            }
            // 走提交审批，参数从查询页面提交按钮参考
            if (ConstantsEms.SUBMIT_STATUS.equals(finVendorFundsFreezeBill.getHandleStatus())) {
                this.submit(finVendorFundsFreezeBill);
            }
            //更新原暂押明细
            if (ConstantsFinance.DOC_TYPE_FREEZE_SF.equals(finVendorFundsFreezeBill.getDocumentType())){
                updateChild(finVendorFundsFreezeBill);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finVendorFundsFreezeBill);
            MongodbDeal.update(finVendorFundsFreezeBill.getFundsFreezeBillSid(), response.getHandleStatus(), finVendorFundsFreezeBill.getHandleStatus(), msgList, TITLE,null);

        }
        return row;
    }

    /**
     * 变更供应商暂押
     *
     * @param finVendorFundsFreezeBill 供应商暂押
     * @UNFREEZE 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinVendorFundsFreezeBill(FinVendorFundsFreezeBill finVendorFundsFreezeBill) {
        //设置确认信息，校验
        setConfirmedInfo(finVendorFundsFreezeBill);
        FinVendorFundsFreezeBill response = finVendorFundsFreezeBillMapper.selectFinVendorFundsFreezeBillById(finVendorFundsFreezeBill.getFundsFreezeBillSid());
        List<Long> sids = new ArrayList<>();
        sids.add(finVendorFundsFreezeBill.getFundsFreezeBillSid());
        if (ConstantsFinance.DOC_TYPE_FREEZE_SF.equals(finVendorFundsFreezeBill.getDocumentType())){
            //回退暂押明细来源
            returnChild(sids);
        }
        int row = finVendorFundsFreezeBillMapper.updateAllById(finVendorFundsFreezeBill);
        if (row > 0) {
            //删除子表，附件表
            deleteItem(sids);
            //插入子表，附件表
            insertChild(finVendorFundsFreezeBill.getItemList(), finVendorFundsFreezeBill.getAttachmentList(), finVendorFundsFreezeBill);
            //更新原暂押明细
            if (ConstantsFinance.DOC_TYPE_FREEZE_SF.equals(finVendorFundsFreezeBill.getDocumentType())){
                updateChild(finVendorFundsFreezeBill);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, finVendorFundsFreezeBill);
            MongodbUtil.insertUserLog(finVendorFundsFreezeBill.getFundsFreezeBillSid(), BusinessType.CHANGE.getValue(), msgList,TITLE);
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
    public int deleteFinVendorFundsFreezeBillByIds(List<Long> fundsFreezeBillSids) {
        if (CollectionUtils.isEmpty(fundsFreezeBillSids)){
            throw new BaseException("请选择行！");
        }
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(HandleStatus.SAVE.getCode());
        handleStatusList.add(HandleStatus.RETURNED.getCode());
        List<FinVendorFundsFreezeBill> finVendorFundsFreezeBillList = finVendorFundsFreezeBillMapper.selectList(new QueryWrapper<FinVendorFundsFreezeBill>().lambda()
                .in(FinVendorFundsFreezeBill::getFundsFreezeBillSid,fundsFreezeBillSids)
                .notIn(FinVendorFundsFreezeBill::getHandleStatus, handleStatusList));
        if (CollectionUtils.isNotEmpty(finVendorFundsFreezeBillList)){
            throw new BaseException("仅保存状态和退回状态允许删除操作！");
        }
        //筛选退回
        List<FinVendorFundsFreezeBill> billList = finVendorFundsFreezeBillMapper.selectList(new QueryWrapper<FinVendorFundsFreezeBill>()
                .lambda().in(FinVendorFundsFreezeBill::getFundsFreezeBillSid,fundsFreezeBillSids));
        int i = finVendorFundsFreezeBillMapper.deleteBatchIds(fundsFreezeBillSids);
        if (i > 0) {
            fundsFreezeBillSids.forEach(sid -> {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
            });
            List<Long> sids = new ArrayList<>();
            billList.forEach(item->{
                if (ConstantsFinance.DOC_TYPE_FREEZE_SF.equals(item.getDocumentType())){
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
     * @param finVendorFundsFreezeBill
     * @UNFREEZE
     */
    @Override
    public int check(FinVendorFundsFreezeBill finVendorFundsFreezeBill) {
        int row = 0;
        Long[] sids = finVendorFundsFreezeBill.getFundsFreezeBillSidList();
        if (sids != null && sids.length > 0) {

            Map<Long, FinVendorFundsFreezeBill> billMap = new HashMap<>();
            List<Long> sfBillSids = new ArrayList<>();


            for (Long sid : sids) {
                List<FinVendorFundsFreezeBillItem> itemList = finVendorFundsFreezeBillItemMapper.selectFinVendorFundsFreezeBillItemList(
                        (new FinVendorFundsFreezeBillItem().setFundsFreezeBillSid(sid)));
                if (CollectionUtils.isEmpty(itemList)){
                    throw new CustomException("明细不能为空!");
                }
                // 主表
                FinVendorFundsFreezeBill bill = this.selectFinVendorFundsFreezeBillById(sid);
                if (ConstantsFinance.DOC_TYPE_FREEZE_SF.equals(bill.getDocumentType())){
                    sfBillSids.add(sid);
                }
                billMap.put(sid, bill);
            }
            // 判断是否要走审批
            if (BusinessType.SUBMIT.getValue().equals(finVendorFundsFreezeBill.getOperateType())) {
                SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                        .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
                if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowGyszyk())) {

                    // 确认后操作
                    if (CollectionUtils.isNotEmpty(sfBillSids)) {
                        // 回退暂押明细来源
                        returnChild(sfBillSids);
                        // 更新原暂押明细
                        for (Long bsfBillSid : sfBillSids) {
                            if (billMap.containsKey(bsfBillSid)) {
                                FinVendorFundsFreezeBill bill = billMap.get(bsfBillSid);
                                bill.setHandleStatus(ConstantsEms.CHECK_STATUS);
                                updateChild(bill);
                            }
                        }
                    }

                    // 不需要走审批，提交及确认
                    LambdaUpdateWrapper<FinVendorFundsFreezeBill> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.in(FinVendorFundsFreezeBill::getFundsFreezeBillSid,sids)
                            .set(FinVendorFundsFreezeBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                            .set(FinVendorFundsFreezeBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                            .set(FinVendorFundsFreezeBill::getConfirmDate, new Date());
                    row = finVendorFundsFreezeBillMapper.update(null, updateWrapper);
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
            row = workFlow(finVendorFundsFreezeBill);
        }
        return row;
    }

    /**
     * 提交
     */
    private void submit(FinVendorFundsFreezeBill finVendorFundsFreezeBill){
        Map<String, Object> variables = new HashMap<>();
        variables.put("formCode", finVendorFundsFreezeBill.getFundsFreezeBillCode());
        variables.put("formId", finVendorFundsFreezeBill.getFundsFreezeBillSid());
        variables.put("formType", FormType.VendorFundsFreezeBill.getCode());
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
        LambdaUpdateWrapper<FinVendorFundsFreezeBill> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinVendorFundsFreezeBill::getFundsFreezeBillSid, sids);
        updateWrapper.set(FinVendorFundsFreezeBill::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(FinVendorFundsFreezeBill::getConfirmDate, new Date());
            updateWrapper.set(FinVendorFundsFreezeBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return finVendorFundsFreezeBillMapper.update(null, updateWrapper);
    }

    /**
     * 工作流流程
     */
    public int workFlow(FinVendorFundsFreezeBill finVendorFundsFreezeBill) {
        int row = 1;
        Long[] sids = finVendorFundsFreezeBill.getFundsFreezeBillSidList();
        // 处理状态
        String handleStatus = finVendorFundsFreezeBill.getHandleStatus();
        if (StrUtil.isNotBlank(finVendorFundsFreezeBill.getOperateType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(finVendorFundsFreezeBill.getOperateType());
        }
        if (sids != null && sids.length > 0) {
            // 获取数据
            List<FinVendorFundsFreezeBill> billList = finVendorFundsFreezeBillMapper.selectFinVendorFundsFreezeBillList(
                    (new FinVendorFundsFreezeBill().setFundsFreezeBillSidList(sids)));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus,
                        ConstantsTable.TABLE_FIN_VENDOR_FUNDS_FREEZE_BILL);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(finVendorFundsFreezeBill.getOperateType())) {
                // 修改处理状态
                row = this.updateHandle(sids, ConstantsEms.SUBMIT_STATUS);
                // 开启工作流
                for (int i = 0; i < billList.size(); i++) {
                    this.submit(billList.get(i));
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getFundsFreezeBillSid(),
                            BusinessType.SUBMIT.getValue(), null, TITLE, finVendorFundsFreezeBill.getComment());
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(finVendorFundsFreezeBill.getOperateType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();

                // 确认后操作 回退暂押明细来源
                List<Long> bsfBillSids = new ArrayList<>();
                Map<Long, FinVendorFundsFreezeBill> billMap = new HashMap<>();
                for (int i = 0; i < billList.size(); i++) {
                    if (ConstantsFinance.DOC_TYPE_FREEZE_SF.equals(billList.get(i).getDocumentType())){
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
                    taskVo.setFormType(FormType.VendorFundsFreezeBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finVendorFundsFreezeBill.getComment());
                    try {
                        SysFormProcess process = workflowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getFundsFreezeBillSid()}, ConstantsEms.CHECK_STATUS);
                            // 更新原暂押明细
                            for (Long bsfBillSid : bsfBillSids) {
                                if (billMap.containsKey(bsfBillSid)) {
                                    FinVendorFundsFreezeBill bill = billMap.get(bsfBillSid);
                                    List<FinVendorFundsFreezeBillItem> itemList = finVendorFundsFreezeBillItemMapper.selectFinVendorFundsFreezeBillItemList(
                                            (new FinVendorFundsFreezeBillItem().setFundsFreezeBillSid(bsfBillSid)));
                                    bill.setItemList(itemList);
                                    bill.setHandleStatus(ConstantsEms.CHECK_STATUS);
                                    updateChild(bill);
                                }
                            }
                        }
                        finVendorFundsFreezeBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getFundsFreezeBillSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, finVendorFundsFreezeBill.getComment());
                }
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(finVendorFundsFreezeBill.getOperateType())) {
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
                    taskVo.setFormType(FormType.VendorFundsFreezeBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finVendorFundsFreezeBill.getComment());
                    try {
                        SysFormProcess process = workflowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getFundsFreezeBillSid()}, HandleStatus.RETURNED.getCode());
                        }
                        finVendorFundsFreezeBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(billList.get(i).getFundsFreezeBillSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, finVendorFundsFreezeBill.getComment());
                }
            }
        }
        return row;
    }

    /**
     * 设置确认信息
     *
     * @param entity
     * @UNFREEZE
     */
    public FinVendorFundsFreezeBill setConfirmedInfo(FinVendorFundsFreezeBill entity) {
        if (CollectionUtils.isEmpty(entity.getItemList())){
            throw new CustomException("明细不能为空!");
        }
        //确认人，确认日期
        if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)){
            entity.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            entity.setConfirmDate(new Date());
        }
        if (!ConstantsFinance.DOC_TYPE_FREEZE_SF.equals(entity.getDocumentType())){
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
        if (ConstantsFinance.DOC_TYPE_FREEZE_SF.equals(entity.getDocumentType())){
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
     * 处理解冻状态
     *
     * @param entity
     * @UNFREEZE
     */
    public String checkStatus(List<FinVendorFundsFreezeBillItem> list){
        int flag = -1;
        String unfreezeStatus = ConstantsFinance.UNFREEZE_STATUS_WJD;
        BigDecimal sumAccount = BigDecimal.ZERO;
        BigDecimal sumYsf = BigDecimal.ZERO;
        BigDecimal sumSfz = BigDecimal.ZERO;
        for (FinVendorFundsFreezeBillItem item : list){
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
     * 删除子表
     */
    public void deleteItem(List<Long> FundsFreezeBillSids) {
        //明细表
        finVendorFundsFreezeBillItemMapper.delete(new QueryWrapper<FinVendorFundsFreezeBillItem>().lambda().in(FinVendorFundsFreezeBillItem::getFundsFreezeBillSid,FundsFreezeBillSids));
        //附件表
        finVendorFundsFreezeBillAttachMapper.delete(new QueryWrapper<FinVendorFundsFreezeBillAttach>().lambda().in(FinVendorFundsFreezeBillAttach::getFundsFreezeBillSid,FundsFreezeBillSids));
    }

    /**
     * 回退暂押明细来源
     */
    public void returnChild(List<Long> fundsFreezeBillSids){
        if (CollectionUtils.isEmpty(fundsFreezeBillSids)) {
            return;
        }
        List<FinVendorFundsFreezeBillItem> itemList = finVendorFundsFreezeBillItemMapper.selectFinVendorFundsFreezeBillItemList(
                new FinVendorFundsFreezeBillItem().setFundsFreezeBillSidList(fundsFreezeBillSids.toArray(new Long[fundsFreezeBillSids.size()])));
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        itemList.forEach(item->{
            //暂押明细来源
            FinVendorFundsFreezeBillItem original = finVendorFundsFreezeBillItemMapper.selectFinVendorFundsFreezeBillItemById(item.getPreFundsFreezeBillItemSid());
            if (original != null){
                if (ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus()) || ConstantsEms.INVALID_STATUS.equals(item.getHandleStatus())){
                    original.setCurrencyAmountYsf(original.getCurrencyAmountYsf().subtract(item.getCurrencyAmount()));
                }
                if (!ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus()) & !ConstantsEms.INVALID_STATUS.equals(item.getHandleStatus())){
                    original.setCurrencyAmountSfz(original.getCurrencyAmountSfz().subtract(item.getCurrencyAmount()));
                }
                original.setUnfreezeStatus(ConstantsFinance.UNFREEZE_STATUS_BFJD);
                if ((original.getCurrencyAmountYsf().add(original.getCurrencyAmountSfz())).compareTo(BigDecimal.ZERO) == 0){
                    original.setUnfreezeStatus(ConstantsFinance.UNFREEZE_STATUS_WJD);
                }
                if (original.getCurrencyAmountYsf().compareTo(original.getCurrencyAmount()) == 0){
                    original.setUnfreezeStatus(ConstantsFinance.UNFREEZE_STATUS_QBJD);
                }
                finVendorFundsFreezeBillItemMapper.updateAllById(original);
            }
        });
        // 修改来源主表的退回状态
        List<Long> preFundsFreezeBillSidList = itemList.stream().map(FinVendorFundsFreezeBillItem::getPreFundsFreezeBillSid).distinct().collect(Collectors.toList());
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
    public void insertChild(List<FinVendorFundsFreezeBillItem> itemList, List<FinVendorFundsFreezeBillAttach> atmList, FinVendorFundsFreezeBill entity) {
        Long sid = entity.getFundsFreezeBillSid();
        //退回类型单据的操作
        if (ConstantsFinance.DOC_TYPE_FREEZE_SF.equals(entity.getDocumentType()) && CollectionUtils.isNotEmpty(itemList)){
            //明细表
            List<FinVendorFundsFreezeBillItem> sortList = itemList.stream().sorted(Comparator.comparing(FinVendorFundsFreezeBillItem::getItemNum, Comparator.nullsFirst(Long::compareTo)).reversed()).collect(Collectors.toList());
            Long maxNum = sortList.get(0).getItemNum();
            if (maxNum == null || maxNum.equals(0)){
                maxNum = new Long("0");
            }
            for (FinVendorFundsFreezeBillItem item : itemList) {
                item.setFundsFreezeBillSid(sid);
                item.setCurrencyAmountSfz(null);
                item.setCurrencyAmountYsf(null);
                item.setUnfreezeStatus(null);
                if (item.getItemNum() == null){
                    maxNum = maxNum + new Long("1");
                    item.setItemNum(maxNum);
                }
            };
            finVendorFundsFreezeBillItemMapper.inserts(itemList);
        }
        //非退回类型的单据操作
        if (!ConstantsFinance.DOC_TYPE_FREEZE_SF.equals(entity.getDocumentType()) && CollectionUtils.isNotEmpty(itemList)){
            if (ConstantsEms.CHECK_STATUS.equals(entity.getHandleStatus())){
                //明细表
                List<FinVendorFundsFreezeBillItem> sortList = itemList.stream().sorted(Comparator.comparing(FinVendorFundsFreezeBillItem::getItemNum, Comparator.nullsFirst(Long::compareTo)).reversed()).collect(Collectors.toList());
                Long maxNum = sortList.get(0).getItemNum();
                if (maxNum == null || maxNum.equals(0)){
                    maxNum = new Long("0");
                }
                for (FinVendorFundsFreezeBillItem item : itemList) {
                    item.setFundsFreezeBillSid(sid);
                    if (item.getFundsFreezeBillItemSid() == null){
                        item.setUnfreezeStatus(ConstantsFinance.UNFREEZE_STATUS_WJD);
                    }
                    if (item.getItemNum() == null){
                        maxNum = maxNum + new Long("1");
                        item.setItemNum(maxNum);
                    }
                };
                finVendorFundsFreezeBillItemMapper.inserts(itemList);
            }
            if (!ConstantsEms.CHECK_STATUS.equals(entity.getHandleStatus())){
                //明细表
                List<FinVendorFundsFreezeBillItem> sortList = itemList.stream().sorted(Comparator.comparing(FinVendorFundsFreezeBillItem::getItemNum, Comparator.nullsFirst(Long::compareTo)).reversed()).collect(Collectors.toList());
                Long maxNum = sortList.get(0).getItemNum();
                if (maxNum == null || maxNum.equals(0)){
                    maxNum = new Long("0");
                }
                for (FinVendorFundsFreezeBillItem item : itemList) {
                    item.setFundsFreezeBillSid(sid);
                    if (item.getFundsFreezeBillItemSid() == null){
                        item.setUnfreezeStatus(ConstantsFinance.UNFREEZE_STATUS_WJD);
                    }
                    if (item.getItemNum() == null){
                        maxNum = maxNum + new Long("1");
                        item.setItemNum(maxNum);
                    }
                };
                finVendorFundsFreezeBillItemMapper.inserts(itemList);
            }
        }
        //附件表
        if (CollectionUtils.isNotEmpty(atmList)) {
            atmList.forEach(item -> {
                item.setFundsFreezeBillSid(sid);
            });
            finVendorFundsFreezeBillAttachMapper.inserts(atmList);
        }
    }

    /**
     * 更新暂押来源明细
     *
     * @param itemList
     * @param atmList
     * @param sid
     */
    public void updateChild(FinVendorFundsFreezeBill entity) {
        Long sid = entity.getFundsFreezeBillSid();
        //明细表
        if (CollectionUtils.isNotEmpty(entity.getItemList())) {
            entity.getItemList().forEach(item -> {
                FinVendorFundsFreezeBillItem fundsFreezeBillItem = new FinVendorFundsFreezeBillItem();
                fundsFreezeBillItem.setFundsFreezeBillItemSid(item.getPreFundsFreezeBillItemSid());
                //原暂押明细
                fundsFreezeBillItem = finVendorFundsFreezeBillItemMapper.selectFinVendorFundsFreezeBillItemById(item.getPreFundsFreezeBillItemSid());
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
                finVendorFundsFreezeBillItemMapper.updateAllById(fundsFreezeBillItem);
                //修改来源主表的状态
                updatePreUnfreezeStatus(item.getPreFundsFreezeBillSid());
            });
        }
    }

    /**
     * 修改来源主表的释放状态
     */
    public void updatePreUnfreezeStatus(Long preFundsFreezeBillSid) {
        //修改来源主表的状态
        //得到来源主表的所有明细
        List<FinVendorFundsFreezeBillItem> itemList = finVendorFundsFreezeBillItemMapper.selectList(new QueryWrapper<FinVendorFundsFreezeBillItem>()
                .lambda().in(FinVendorFundsFreezeBillItem::getFundsFreezeBillSid, preFundsFreezeBillSid));
        //获得来源主表应有的状态
        String unfreezeStatus = checkStatus(itemList);
        FinVendorFundsFreezeBill finVendorFundsFreezeBill = new FinVendorFundsFreezeBill().setUnfreezeStatus(unfreezeStatus)
                .setFundsFreezeBillSid(preFundsFreezeBillSid);
        finVendorFundsFreezeBillMapper.updateById(finVendorFundsFreezeBill);
    }

    /**
     * 作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int invalid(Long fundsFreezeBillSid){
        FinVendorFundsFreezeBill bill = finVendorFundsFreezeBillMapper.selectById(fundsFreezeBillSid);
        if (!HandleStatus.CONFIRMED.getCode().equals(bill.getHandleStatus())){
            throw new BaseException("请选择处理状态是“已确认”的单据!");
        }
        if (ConstantsFinance.DOC_TYPE_FREEZE_ZYK.equals(bill.getDocumentType()) && !ConstantsFinance.UNFREEZE_STATUS_WJD.equals(bill.getUnfreezeStatus())){
            throw new BaseException("暂押款已开始释放，无法作废!");
        }
        int i = finVendorFundsFreezeBillMapper.updateAllById(bill.setHandleStatus(HandleStatus.INVALID.getCode()));
        if (i > 0 && ConstantsFinance.DOC_TYPE_FREEZE_SF.equals(bill.getDocumentType())){
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
