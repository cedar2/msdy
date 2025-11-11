package com.platform.ems.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.ContractTemplateAttach;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.PurPurchasePriceReportResponse;
import com.platform.ems.domain.dto.response.form.PurPurchaseContractFormResponse;
import com.platform.ems.enums.FormType;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.*;
import com.platform.ems.plug.mapper.*;
import com.platform.ems.service.*;
import com.platform.ems.util.CommonUtil;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.data.BigDecimalSum;
import com.platform.ems.workflow.domain.Submit;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.api.service.RemoteFlowableService;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.flowable.domain.vo.FormParameter;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysBusinessBcstMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.mapper.SystemUserMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.util.MongodbUtil;
import org.springframework.web.multipart.MultipartFile;

import static java.util.stream.Collectors.toList;

/**
 * 采购合同信息Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@Service
@SuppressWarnings("all")
public class PurPurchaseContractServiceImpl extends ServiceImpl<PurPurchaseContractMapper, PurPurchaseContract> implements IPurPurchaseContractService {
    @Autowired
    private PurPurchaseContractMapper purPurchaseContractMapper;
    @Autowired
    private PurPurchaseContractAttachmentMapper purPurchaseContractAttachmentMapper;
    @Autowired
    private IPurPurchaseContractPayMethodService purPurchaseContractPayMethodService;
    @Autowired
    private FinRecordAdvancePaymentMapper finRecordAdvancePaymentMapper;
    @Autowired
    private FinRecordAdvancePaymentItemMapper finRecordAdvancePaymentItemMapper;
    @Autowired
    private ConAccountMethodGroupMapper conAccountMethodGroupMapper;
    @Autowired
    private ConAdvanceSettleModeMapper conAdvanceSettleModeMapper;
    @Autowired
    private ConRemainSettleModeMapper conRemainSettleModeMapper;
    @Autowired
    private ConTaxRateMapper conTaxRateMapper;
    @Autowired
    private ConBusinessChannelMapper conBusinessChannelMapper;
    @Autowired
    private ConMaterialTypeMapper conMaterialTypeMapper;
    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;
    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private PurPurchaseOrderMapper purPurchaseOrderMapper;
    @Autowired
    private ManManufactureOutsourceSettleMapper manufactureOutsourceSettleMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private BasProductSeasonMapper productSeasonMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SystemUserMapper sysUserMapper;
    @Autowired
	private ISystemUserService userService;
    @Autowired
    private IPurPurchasePriceService purPurchasePriceService;
    @Autowired
    private IPurPurchaseOrderService purPurchaseOrderService;
    @Autowired
    private PurPurchaseOrderItemMapper purPurchaseOrderItemMapper;
    @Autowired
    private IWorkFlowService workflowService;
    @Autowired
    private RemoteFlowableService remoteFlowableService;
    @Autowired
    private IPurPurchasePriceService iPurPurchasePriceService;
    private static final String TITLE = "采购合同信息";

    /**
     * 查询采购合同信息
     *
     * @param purchaseContractSid 采购合同信息ID
     * @return 采购合同信息
     */
    @Override
    public PurPurchaseContract selectPurPurchaseContractById(Long purchaseContractSid) {
        PurPurchaseContract purPurchaseContract = purPurchaseContractMapper.selectPurPurchaseContractById(purchaseContractSid);
        if (purPurchaseContract == null) {
            return null;
        }
        // 支付方式
        List<PurPurchaseContractPayMethod> payMethodList = purPurchaseContractPayMethodService.selectPurPurchaseContractPayMethodListByContract(purchaseContractSid);
        List<PurPurchaseContractPayMethod> yusf = new ArrayList<>();
        List<PurPurchaseContractPayMethod> zq = new ArrayList<>();
        List<PurPurchaseContractPayMethod> wq = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(payMethodList)){
            yusf = payMethodList.stream().filter(o->ConstantsFinance.ACCOUNT_CAT_YSFK.equals(o.getAccountCategory())).collect(Collectors.toList());
            zq = payMethodList.stream().filter(o->ConstantsFinance.ACCOUNT_CAT_ZQK.equals(o.getAccountCategory())).collect(Collectors.toList());
            wq = payMethodList.stream().filter(o->ConstantsFinance.ACCOUNT_CAT_WK.equals(o.getAccountCategory())).collect(Collectors.toList());
        }
        purPurchaseContract.setPayMethodListYusf(yusf);
        purPurchaseContract.setPayMethodListZq(zq);
        purPurchaseContract.setPayMethodListWq(wq);
        //采购合同信息-附件
        PurPurchaseContractAttachment purPurchaseContractAttachment = new PurPurchaseContractAttachment();
        purPurchaseContractAttachment.setPurchaseContractSid(purchaseContractSid);
        List<PurPurchaseContractAttachment> purPurchaseContractAttachmList =
                purPurchaseContractAttachmentMapper.selectPurPurchaseContractAttachmentList(purPurchaseContractAttachment);
        purPurchaseContract.setAttachmentList(purPurchaseContractAttachmList);
        FlowTaskVo flowTaskVo = new FlowTaskVo();
        flowTaskVo.setFormId(purchaseContractSid);
        //判断是否是最后一个节点
        AjaxResult nextFlowNode = remoteFlowableService.getNextFlowNode(flowTaskVo);
        Object code = nextFlowNode.get("code");
        if(ConstantsEms.CODE_SUCESS.equals(nextFlowNode.get("code").toString())) {
            purPurchaseContract.setIsFinallyNode(ConstantsEms.YES);
        }
        //采购价明细
        PurPurchasePriceReportResponse request = new PurPurchasePriceReportResponse();
        request.setHandleStatuses(new String[]{HandleStatus.SUBMIT.getCode(),HandleStatus.CHANGEAPPROVAL.getCode()});
        request.setVendorSids(new Long[]{purPurchaseContract.getVendorSid()});
        request.setIsFinallyNode(ConstantsEms.YES);
        List<PurPurchasePriceReportResponse> list = purPurchasePriceService.report(request);
        purPurchaseContract.setPurPurchasePriceItems(list);
        //采购订单明细
        PurPurchaseOrderItem purPurchaseOrderItem = new PurPurchaseOrderItem();
        purPurchaseOrderItem.setContractCode(purPurchaseContract.getPurchaseContractCode());
        purPurchaseOrderItem.setNotInHandleStatus(new String[]{ConstantsEms.INVALID_STATUS});
        List<PurPurchaseOrderItem> itemList = purPurchaseOrderItemMapper.getItemList(purPurchaseOrderItem);
        getPrice(itemList, purPurchaseContract);
        purPurchaseContract.setPurPurchaseOrderItems(itemList);
        // 排序
        try {
            if (CollectionUtil.isNotEmpty(itemList)){
                purPurchaseContract.setPurPurchaseOrderItems(purPurchaseOrderService.newSort(itemList));
            }
        } catch (Exception e) {
            log.warn("采购订单明细排序错误");
        }
        MongodbUtil.find(purPurchaseContract);
        return purPurchaseContract;
    }

    /**
     * 对销售订单明细 sku1为尺码 的 进行关于尺码排序
     * @param itemList
     */
    public List<PurPurchaseOrderItem> sortPurchaseOrderItemBySku1Cm(List<PurPurchaseOrderItem> itemList){
        itemList.forEach(li -> {
            String skuName = li.getSku1Name();
            String[] nameSplit = skuName.split("/");
            if (nameSplit.length == 1) {
                li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
            } else {
                String[] name2split = nameSplit[1].split("\\(");
                if (name2split.length == 2) {
                    li.setSecondSort(name2split[0].replaceAll("[a-zA-Z]", ""));

                    li.setThirdSort(name2split[1].replaceAll("[a-zA-Z]", ""));
                } else {
                    li.setSecondSort(nameSplit[1].replaceAll("[a-zA-Z]", ""));
                }
                li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
            }
        });
        List<PurPurchaseOrderItem> allList = new ArrayList<>();
        List<PurPurchaseOrderItem> allThirdList = new ArrayList<>();
        List<PurPurchaseOrderItem> sortThird = itemList.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
        List<PurPurchaseOrderItem> sortThirdNull = itemList.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
        sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
        allThirdList.addAll(sortThird);
        allThirdList.addAll(sortThirdNull);
        List<PurPurchaseOrderItem> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
        sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
        List<PurPurchaseOrderItem> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
        allList.addAll(sort);
        allList.addAll(sortNull);
        itemList = allList.stream().sorted(Comparator.comparing(item -> Double.valueOf(item.getFirstSort()))).collect(Collectors.toList());
        return itemList;
    }

    /**
     * 对销售订单明细 sku2为尺码 的 进行关于尺码排序
     * @param itemList
     */
    public List<PurPurchaseOrderItem> sortPurchaseOrderItemBySku2Cm(List<PurPurchaseOrderItem> itemList){
        itemList.forEach(li -> {
            String skuName = li.getSku2Name();
            String[] nameSplit = skuName.split("/");
            if (nameSplit.length == 1) {
                li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
            } else {
                String[] name2split = nameSplit[1].split("\\(");
                if (name2split.length == 2) {
                    li.setSecondSort(name2split[0].replaceAll("[a-zA-Z]", ""));

                    li.setThirdSort(name2split[1].replaceAll("[a-zA-Z]", ""));
                } else {
                    li.setSecondSort(nameSplit[1].replaceAll("[a-zA-Z]", ""));
                }
                li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
            }
        });
        List<PurPurchaseOrderItem> allList = new ArrayList<>();
        List<PurPurchaseOrderItem> allThirdList = new ArrayList<>();
        List<PurPurchaseOrderItem> sortThird = itemList.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
        List<PurPurchaseOrderItem> sortThirdNull = itemList.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
        sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
        allThirdList.addAll(sortThird);
        allThirdList.addAll(sortThirdNull);
        List<PurPurchaseOrderItem> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
        sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
        List<PurPurchaseOrderItem> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
        allList.addAll(sort);
        allList.addAll(sortNull);
        itemList = allList.stream().sorted(Comparator.comparing(item -> Double.valueOf(item.getFirstSort()))).collect(Collectors.toList());
        return itemList;
    }

    public void getPrice(List<PurPurchaseOrderItem> itemList,PurPurchaseContract purPurchaseContract){
        if(CollectionUtil.isNotEmpty(itemList)){
            itemList.forEach(li->{
                PurPurchasePrice purchasePrice = new PurPurchasePrice();
                if (li.getPurchasePriceTax() == null) {
                    /**
                     * 3、新增列：价格(元)
                     * 若订单中有价格，就显示订单价格；
                     * 若没有，进行如下操作：
                     * 1）根据订单中的“编码+供应商+甲供料方式+采购模式”在采购价档案中获取采购价的处理状态为“审批中/已确认/变更审批中”且有效期（至）>=当前日期的采购价数据
                     * 若1）查找到多笔数据，则选择有效期（至）距离当前日期最近的价格
                     */
                    purchasePrice.setVendorSid(li.getVendorSid())
                            .setPurchaseMode(li.getPurchaseMode())
                            .setRawMaterialMode(li.getRawMaterialMode())
                            .setMaterialSid(li.getMaterialSid())
                            .setSku1Sid(li.getSku1Sid())
                            .setSku2Sid(li.getSku2Sid())
                            .setHandleStatuses(new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CONFIRMED.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()});
                    PurPurchasePriceItem item = iPurPurchasePriceService.getNearPurchase(purchasePrice);
                    if(item != null && item.getPurchasePriceTax() != null){
                        li.setNearPurchasePriceTax(item.getPurchasePriceTax());
                    }
                }
                else {
                    li.setNearPurchasePriceTax(li.getPurchasePriceTax());
                }
                // 待批价格，待批金额，待批税率
                purchasePrice = new PurPurchasePrice();
                purchasePrice.setVendorSid(li.getVendorSid())
                        .setPurchaseMode(li.getPurchaseMode())
                        .setSku2Sid(li.getSku2Sid())
                        .setRawMaterialMode(li.getRawMaterialMode())
                        .setMaterialSid(li.getMaterialSid())
                        .setNotApprovalStatus(new String[]{HandleStatus.SUBMIT.getCode(),HandleStatus.CHANGEAPPROVAL.getCode()})
                        .setSku1Sid(li.getSku1Sid());
                PurPurchasePriceItem item = iPurPurchasePriceService.getNewPurchase(purchasePrice);
                if(item.getPurchasePriceTax()!=null){
                    li.setWaitApprovalPrice(item.getPurchasePriceTax())
                            .setWaitApprovalTaxRate(item.getTaxRate());
                    if(li.getQuantity()!=null){
                        li.setWaitApprovalAmount(li.getQuantity().multiply(item.getPurchasePriceTax()));
                        li.setWaitApprovalAmount(li.getWaitApprovalAmount().divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP));
                    }
                }
                if(li.getQuantity()!=null&&li.getNearPurchasePriceTax()!=null){
                    li.setPriceTax(li.getQuantity().multiply(li.getNearPurchasePriceTax()));
                    li.setPriceTax(li.getPriceTax().divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP));
                }
                if(ConstantsEms.YES.equals(li.getFreeFlag())){
                    if(li.getWaitApprovalPrice()!=null){
                        li.setPurchasePriceTax(null)
                                .setPriceTax(null)
                                .setWaitApprovalAmount(BigDecimal.ZERO)
                                .setWaitApprovalPrice(BigDecimal.ZERO);
                    }else{
                        li.setPurchasePriceTax(BigDecimal.ZERO)
                                .setPriceTax(BigDecimal.ZERO)
                                .setWaitApprovalAmount(null)
                                .setWaitApprovalPrice(null);
                    }
                }
            });
            BigDecimal sumQuantity = itemList.stream().filter(li -> li.getQuantity() != null).map(li -> li.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumAmount = itemList.stream().filter(li -> li.getPriceTax() != null).map(li -> li.getPriceTax()).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumWaitApprovalAmount = itemList.stream().filter(li -> li.getWaitApprovalAmount() != null).map(li -> li.getWaitApprovalAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
            purPurchaseContract.setSumQuantity(sumQuantity)
                    .setSumAmount(sumAmount)
                    .setSumWaitApprovalAmount(sumWaitApprovalAmount);
        }
    }
    /**
     * 复制采购合同信息
     *
     * @param purchaseContractSid 采购合同信息ID
     * @return 采购合同信息
     */
    @Override
    public PurPurchaseContract copyPurPurchaseContractById(Long purchaseContractSid) {
        PurPurchaseContract purPurchaseContract = purPurchaseContractMapper.selectPurPurchaseContractById(purchaseContractSid);
        if (purPurchaseContract == null) {
            return null;
        }
        purPurchaseContract.setPurchaseContractCode(null).setPurchaseContractSid(null).setCurrencyAmountTax(null) .setOperLogList(null);
        purPurchaseContract.setHandleStatus(ConstantsEms.SAVA_STATUS).setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(null)
                .setUpdaterAccount(null).setUpdateDate(null).setConfirmerAccount(null).setConfirmDate(null).setContractSignDate(null).setCancelRemark(null)
                .setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS).setSignInStatus(ConstantsEms.SIGN_IN_STATUS_WQS).setContractSigner(null);
        // 支付方式
        List<PurPurchaseContractPayMethod> yusf = new ArrayList<>();
        List<PurPurchaseContractPayMethod> zq = new ArrayList<>();
        List<PurPurchaseContractPayMethod> wq = new ArrayList<>();
        List<PurPurchaseContractPayMethod> payMethodList = purPurchaseContractPayMethodService.selectPurPurchaseContractPayMethodListByContract(purchaseContractSid);
        for (PurPurchaseContractPayMethod method : payMethodList) {
            method.setPurchaseContractSid(null).setContractPayMethodSid(null) .setOperLogList(null);
            method.setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(null).setUpdaterAccount(null).setUpdateDate(null);
            if (ConstantsFinance.ACCOUNT_CAT_YSFK.equals(method.getAccountCategory())){
                yusf.add(method);
            }
            else if (ConstantsFinance.ACCOUNT_CAT_ZQK.equals(method.getAccountCategory())){
                zq.add(method);
            }
            else if (ConstantsFinance.ACCOUNT_CAT_WK.equals(method.getAccountCategory())){
                wq.add(method);
            }
        }
        purPurchaseContract.setPayMethodListYusf(yusf);
        purPurchaseContract.setPayMethodListZq(zq);
        purPurchaseContract.setPayMethodListWq(wq);
        return purPurchaseContract;
    }

    /**
     * 查询采购合同信息列表
     *
     * @param purPurchaseContract 采购合同信息
     * @return 采购合同信息
     */
    @Override
    public List<PurPurchaseContract> selectPurPurchaseContractList(PurPurchaseContract purPurchaseContract) {
        List<PurPurchaseContract> list = purPurchaseContractMapper.selectPurPurchaseContractList(purPurchaseContract);
        return list;
    }

    /**
     * 新增采购合同信息
     * 需要注意编码重复校验
     *
     * @param purPurchaseContract 采购合同信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long insertPurPurchaseContract(PurPurchaseContract purPurchaseContract) {
        //校验同公司下采购合同号是否重复
        checkCodeUnique(purPurchaseContract);
        //校验同公司下采购合同名称是否重复
        if (StrUtil.isNotBlank(purPurchaseContract.getContractName())){
            checkNameUnique(purPurchaseContract);
        }
        //设置账期
        setZhangqi(purPurchaseContract);
        //初始化合同上传状态
        purPurchaseContract.setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS);
        //设置确认信息
        setConfirmInfo(purPurchaseContract);
        // 预收付款方式组合的编码
        if (purPurchaseContract.getAccountsMethodGroup() != null){
            ConAccountMethodGroup methodGroup = conAccountMethodGroupMapper.selectById(purPurchaseContract.getAccountsMethodGroup());
            if (methodGroup != null){
                purPurchaseContract.setAccountsMethodGroupCode(methodGroup.getCode());
            }
        }
        int row = purPurchaseContractMapper.insert(purPurchaseContract);
        if (!ConstantsEms.CONTRACT_TYPE_KJ.equals(purPurchaseContract.getContractType())){
            advancePayment(purPurchaseContract);
        }
        if (row > 0) {
            // 支付方式
            List<PurPurchaseContractPayMethod> payMethodList = setMerge(purPurchaseContract);
            purPurchaseContractPayMethodService.insertPurPurchaseContractPayMethodList(purPurchaseContract.getPurchaseContractSid(),payMethodList);
            //采购合同信息-附件
            List<PurPurchaseContractAttachment> purPurchaseContractAttachmentList = purPurchaseContract.getAttachmentList();
            if (CollectionUtils.isNotEmpty(purPurchaseContractAttachmentList)) {
                addPurPurchaseContractAttachment(purPurchaseContract, purPurchaseContractAttachmentList);
            }
            if (!ConstantsEms.CHECK_STATUS.equals(purPurchaseContract.getHandleStatus())){
                //待办通知
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_PURCHASE_CONTRACT)
                        .setDocumentSid(purPurchaseContract.getPurchaseContractSid())
                        .setDocumentCode(purPurchaseContract.getPurchaseContractCode())
                        .setNoticeDate(new Date()).setTitle("")
                        .setMenuId(ConstantsWorkbench.purchase_contract)
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                if (ConstantsEms.SAVA_STATUS.equals(purPurchaseContract.getHandleStatus())){
                    sysTodoTask.setTitle("采购合同 " + purPurchaseContract.getPurchaseContractCode() + " 当前是保存状态，请及时处理！");
                }
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //采购订单创建采购合同回写
            List<String> purchaseOrderSidList = purPurchaseContract.getPurchaseOrderSidList();
            if(CollectionUtil.isNotEmpty(purchaseOrderSidList)){
                purPurchaseOrderMapper.update(new PurPurchaseOrder(),new UpdateWrapper<PurPurchaseOrder>().lambda()
                        .in(PurPurchaseOrder::getPurchaseOrderSid,purchaseOrderSidList)
                        .set(PurPurchaseOrder::getPurchaseContractSid,purPurchaseContract.getPurchaseContractSid())
                );
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(purPurchaseContract.getPurchaseContractSid() , purPurchaseContract.getHandleStatus(), msgList, TITLE,null);
        }
        return purPurchaseContract.getPurchaseContractSid();
    }

    /*
     * 合并支付方式
     */
    private List<PurPurchaseContractPayMethod> setMerge(PurPurchaseContract purPurchaseContract){
        List<PurPurchaseContractPayMethod> payMethodList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(purPurchaseContract.getPayMethodListYusf())){
            payMethodList.addAll(purPurchaseContract.getPayMethodListYusf());
        }
        if (CollectionUtil.isNotEmpty(purPurchaseContract.getPayMethodListZq())){
            payMethodList.addAll(purPurchaseContract.getPayMethodListZq());
        }
        if (CollectionUtil.isNotEmpty(purPurchaseContract.getPayMethodListWq())){
            payMethodList.addAll(purPurchaseContract.getPayMethodListWq());
        }
        return payMethodList;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(PurPurchaseContract purPurchaseContract) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, purPurchaseContract.getPurchaseContractSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, purPurchaseContract.getPurchaseContractSid()));
        }
    }

    @Override
    public void advancePayment(PurPurchaseContract purPurchaseContract) {
        if (!ConstantsEms.YES.equals(ApiThreadLocalUtil.get().getSysUser().getIsBusinessFinance())){
            return;
        }
        if (ConstantsEms.CONTRACT_TYPE_KJ.equals(purPurchaseContract.getContractType())){
            return;
        }
        FinRecordAdvancePayment o = new FinRecordAdvancePayment();
        o.setPurchaseContractSid(purPurchaseContract.getPurchaseContractSid());
        List<FinRecordAdvancePayment> finRecordAdvancePaymentList =
                finRecordAdvancePaymentMapper.selectFinRecordAdvancePaymentList(o);
        if (CollectionUtils.isNotEmpty(finRecordAdvancePaymentList)) {
            finRecordAdvancePaymentList.forEach(item->{
                LambdaUpdateWrapper<FinRecordAdvancePayment> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(FinRecordAdvancePayment::getRecordAdvancePaymentSid,item.getRecordAdvancePaymentSid())
                        .set(FinRecordAdvancePayment::getDocumentDate, new Date()).set(FinRecordAdvancePayment::getProductSeasonSid,purPurchaseContract.getProductSeasonSid())
                        .set(FinRecordAdvancePayment::getBuyer,purPurchaseContract.getBuyer()).set(FinRecordAdvancePayment::getPaymentYear,purPurchaseContract.getYear())
                        .set(FinRecordAdvancePayment::getRemark,purPurchaseContract.getRemark());
                int row = finRecordAdvancePaymentMapper.update(null, updateWrapper);
                LambdaUpdateWrapper<FinRecordAdvancePaymentItem> updateWrapper2 = new LambdaUpdateWrapper<>();
                updateWrapper2.eq(FinRecordAdvancePaymentItem::getRecordAdvancePaymentSid,item.getRecordAdvancePaymentSid())
                        .set(FinRecordAdvancePaymentItem::getTaxRate, purPurchaseContract.getTaxRate());
                finRecordAdvancePaymentItemMapper.update(null, updateWrapper2);
            });
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(purPurchaseContract.getHandleStatus())) {
            //1.预付款结算方式是否选择：按合同   2.预付款比例是否大于0   3.合同类型是否选择：标准合同，如是则合同金额需大于0
            //预付款方式组合
            ConAccountMethodGroup accountMethodGroup = conAccountMethodGroupMapper.selectConAccountMethodGroupById(purPurchaseContract.getAccountsMethodGroup());
            //预付款结算方式
            if (ConstantsEms.ADVANCE_SETTLE_MODE_HT.equals(purPurchaseContract.getAdvanceSettleMode()) && Double.parseDouble(accountMethodGroup.getAdvanceRate()) > 0) {
                if (purPurchaseContract.getCurrencyAmountTax() == null || purPurchaseContract.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BaseException("此操作合同金额为必填且必须大于 0 ，请检查！");
                }
                //凭证日期
                FinRecordAdvancePayment finRecordAdvancePayment = new FinRecordAdvancePayment();
                BeanCopyUtils.copyProperties(purPurchaseContract, finRecordAdvancePayment);
                finRecordAdvancePayment.setDocumentDate(new Date())
                        .setPaymentYear(purPurchaseContract.getYear())
                        .setBookType(ConstantsFinance.BOOK_TYPE_YUF)
                        .setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_PC).setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                        .setSettleMode(purPurchaseContract.getAdvanceSettleMode()).setCreateDate(new Date())
                        .setPurchaseContractSid(purPurchaseContract.getPurchaseContractSid())
                        .setPurchaseContractCode(purPurchaseContract.getPurchaseContractCode());
                finRecordAdvancePayment.setAdvanceRate(new BigDecimal(accountMethodGroup.getAdvanceRate()));
                finRecordAdvancePayment.setCurrencyAmountTaxContract(purPurchaseContract.getCurrencyAmountTax());
                finRecordAdvancePaymentMapper.insert(finRecordAdvancePayment);

                //应付金额
                FinRecordAdvancePaymentItem finRecordAdvancePaymentItem = new FinRecordAdvancePaymentItem();
                if (purPurchaseContract.getCurrencyAmountTax() != null && purPurchaseContract.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) == 1
                        && accountMethodGroup.getAdvanceRate() != null) {
                    finRecordAdvancePaymentItem.setCurrencyAmountTaxYingf(purPurchaseContract.getCurrencyAmountTax().multiply(new BigDecimal(accountMethodGroup.getAdvanceRate())));
                }
                finRecordAdvancePaymentItem.setRecordAdvancePaymentSid(finRecordAdvancePayment.getRecordAdvancePaymentSid())
                        .setCurrencyAmountTaxYhx(new BigDecimal("0"))
                        .setCurrencyAmountTaxHxz(new BigDecimal("0")).setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                        .setTaxRate(purPurchaseContract.getTaxRate()).setCreateDate(new Date())
                        .setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                if (purPurchaseContract.getYfAccountValidDays() != null){
                    Date dateValid = new Date();
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(dateValid);
                    calendar.add(calendar.DATE,purPurchaseContract.getYfAccountValidDays()); //把日期往后增加i天,整数  往后推,负数往前移动
                    dateValid = calendar.getTime(); //这个时间就是日期往后推i天的结果
                    finRecordAdvancePaymentItem.setAccountValidDays(new Long(purPurchaseContract.getYfAccountValidDays()));
                    finRecordAdvancePaymentItem.setAccountValidDate(dateValid);
                }
                else {
                    finRecordAdvancePaymentItem.setAccountValidDays(new Long(0));
                    finRecordAdvancePaymentItem.setAccountValidDate(new Date());
                }
                finRecordAdvancePaymentItemMapper.insert(finRecordAdvancePaymentItem);
            }
        }
    }

    /**
     * 采购合同号校验同公司下
     */
    private void checkCodeUnique(PurPurchaseContract purPurchaseContract) {
        QueryWrapper<PurPurchaseContract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("purchase_contract_code", purPurchaseContract.getPurchaseContractCode())
                .eq("company_sid", purPurchaseContract.getCompanySid());
        PurPurchaseContract checkCodeResust = purPurchaseContractMapper.selectOne(queryWrapper);
        if (checkCodeResust != null) {
            throw new BaseException("该公司下已存在相同采购合同号");
        }
    }

    /**
     * 采购合同号校验不同公司下  多选提交时可能需要改一下
     */
    @Override
    public void checkCode(PurPurchaseContract purPurchaseContract) {
        List<PurPurchaseContract> checkCodeResust = new ArrayList<>();
        String msg = "";
        String methodMsg = "";
        //新建时
        if (purPurchaseContract.getPurchaseContractSid() == null) {
            checkCodeResust = purPurchaseContractMapper.selectPurPurchaseContractList(new PurPurchaseContract().setContractCode(purPurchaseContract.getPurchaseContractCode()));
            checkCodeResust = checkCodeResust.stream().filter(o->!o.getCompanySid().toString().equals(purPurchaseContract.getCompanySid().toString())).collect(Collectors.toList());
            msg = purPurchaseContract.getPurchaseContractCode() + "，是否确认创建？";
        } else {
            //提交时
            PurPurchaseContract contract = purPurchaseContractMapper.selectById(purPurchaseContract.getPurchaseContractSid());
            checkCodeResust = purPurchaseContractMapper.selectPurPurchaseContractList(new PurPurchaseContract().setContractCode(contract.getPurchaseContractCode()));
            checkCodeResust = checkCodeResust.stream().filter(o->!o.getCompanySid().toString().equals(contract.getCompanySid().toString())).collect(Collectors.toList());
            // 支付方式占比不能不等于1
            methodMsg = purPurchaseContractPayMethodService.submitVerifyById(purPurchaseContract.getPurchaseContractSid());
            if (StrUtil.isNotBlank(methodMsg)){
                msg = contract.getPurchaseContractCode() + "，且" + methodMsg +"，是否确认提交？";
            }else {
                msg = contract.getPurchaseContractCode() + "，是否确认提交？";
            }
        }
        if (CollectionUtil.isNotEmpty(checkCodeResust)) {
            String shortName = "";
            for (PurPurchaseContract contract : checkCodeResust) {
                if (StrUtil.isNotBlank(contract.getCompanyShortName())){
                    shortName = shortName + contract.getCompanyShortName() + ",";
                }else {
                    shortName = shortName + contract.getCompanyName() + ",";
                }
            }
            if (shortName.endsWith(",")) {
                shortName = shortName.substring(0,shortName.length() - 1);
            }
            throw new CustomException("公司:" + shortName + "下已存在采购合同号 " + msg);
        }else {
            if (StrUtil.isNotBlank(methodMsg)){
                throw new CustomException(methodMsg + "，是否确认提交？");
            }
        }
    }

    /**
     * 销售合同名称校验
     */
    private void checkNameUnique(PurPurchaseContract purPurchaseContract) {
        QueryWrapper<PurPurchaseContract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contract_name", purPurchaseContract.getContractName())
                .eq("company_sid", purPurchaseContract.getCompanySid());
        PurPurchaseContract checkNameResust = purPurchaseContractMapper.selectOne(queryWrapper);
        if (checkNameResust != null) {
            throw new BaseException("该公司下已存在相同采购合同名称");
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(PurPurchaseContract purPurchaseContract) {
        if (purPurchaseContract == null) {
            return;
        }
        if (purPurchaseContract.getCurrencyAmountTax() != null && BigDecimal.ZERO.compareTo(purPurchaseContract.getCurrencyAmountTax()) >= 0){
            throw new CustomException("合同金额只能填写正数，请检查！");
        }
        if (ConstantsEms.CONTRACT_TYPE_BZ.equals(purPurchaseContract.getContractType())){
            if (purPurchaseContract.getCurrencyAmountTax() == null || purPurchaseContract.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0){
                throw new CustomException("标准合同，合同金额为必填且必须大于 0 ！");
            }
        }
        if (ConstantsEms.CONTRACT_TYPE_BZHTKJS.equals(purPurchaseContract.getContractType()) && purPurchaseContract.getFrameworkAgreementSid() == null){
            throw new CustomException("合同类型为“标准合同（框架式）”，框架协议号不能为空");
        }
        if (!ConstantsEms.CONTRACT_TYPE_KJ.equals(purPurchaseContract.getContractType()) && StrUtil.isBlank(purPurchaseContract.getPurchaseMode())){
            throw new CustomException("合同类型不是“框架协议”时，采购模式不能为空");
        }
        //预付款方式组合
        ConAccountMethodGroup accountMethodGroup = conAccountMethodGroupMapper.selectConAccountMethodGroupById(purPurchaseContract.getAccountsMethodGroup());
        //预付款结算方式
        if (!ConstantsEms.CONTRACT_TYPE_KJ.equals(purPurchaseContract.getContractType())){
            if (ConstantsEms.ADVANCE_SETTLE_MODE_HT.equals(purPurchaseContract.getAdvanceSettleMode()) && Double.parseDouble(accountMethodGroup.getAdvanceRate()) > 0) {
                if (purPurchaseContract.getCurrencyAmountTax() == null || purPurchaseContract.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new CustomException("此操作合同金额为必填且必须大于 0 ，请检查！");
                }
            }
        }
        if (ConstantsEms.CHECK_STATUS.equals(purPurchaseContract.getHandleStatus())
            || ConstantsEms.SUBMIT_STATUS.equals(purPurchaseContract.getHandleStatus())) {
            if (ConstantsEms.CONTRACT_TYPE_BC.equals(purPurchaseContract.getContractType()) && purPurchaseContract.getOriginalPurchaseContractSid() == null){
                throw new CustomException("补充协议，原合同号为必填，请填写后再操作！");
            }
            if (StrUtil.isBlank(purPurchaseContract.getContractSigner())){
                throw new CustomException("此操作合同签约人不能为空");
            }
            if (purPurchaseContract.getContractSignDate() == null){
                throw new CustomException("此操作合同签约日期不能为空");
            }
            if (ConstantsEms.CHECK_STATUS.equals(purPurchaseContract.getHandleStatus())){
                purPurchaseContract.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
                purPurchaseContract.setConfirmDate(new Date());
            }
        }
        if (CollectionUtils.isNotEmpty(purPurchaseContract.getAttachmentList())){
            int i = 0;
            for (PurPurchaseContractAttachment item : purPurchaseContract.getAttachmentList()) {
                if (ConstantsEms.FILE_TYPE_CGHT.equals(item.getFileType())){
                    i++;
                    if (i >= 2){
                        throw new CustomException("每份合同只允许上传一份类型为“采购合同(电子版)”的附件");
                    }
                    purPurchaseContract.setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS_Y);
                }
            }
        }
    }

    /**
     * 采购合同信息-附件对象
     */
    private void addPurPurchaseContractAttachment(PurPurchaseContract purPurchaseContract, List<PurPurchaseContractAttachment> purPurchaseContractAttachmList) {
        purPurchaseContractAttachmentMapper.delete(
                new UpdateWrapper<PurPurchaseContractAttachment>()
                        .lambda()
                        .eq(PurPurchaseContractAttachment::getPurchaseContractSid, purPurchaseContract.getPurchaseContractSid())
        );
        if (CollectionUtils.isNotEmpty(purPurchaseContract.getAttachmentList())) {
            purPurchaseContractAttachmList.forEach(o -> {
                o.setPurchaseContractSid(purPurchaseContract.getPurchaseContractSid());
                if (o.getPurchaseContractSid() != null){
                    o.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            purPurchaseContractAttachmentMapper.inserts(purPurchaseContract.getAttachmentList());
        }
    }

    /**
     * 修改采购合同信息
     *
     * @param purPurchaseContract 采购合同信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurPurchaseContract(PurPurchaseContract purPurchaseContract) {
        PurPurchaseContract response = purPurchaseContractMapper.selectPurPurchaseContractById(purPurchaseContract.getPurchaseContractSid());
        if (!purPurchaseContract.getPurchaseContractCode().equals(response.getPurchaseContractCode()) ||
                !purPurchaseContract.getCompanySid().equals(response.getCompanySid())) {
            //校验同公司下采购合同号是否重复
            checkCodeUnique(purPurchaseContract);
        }
        if (StrUtil.isNotBlank(purPurchaseContract.getContractName())){
            if (!purPurchaseContract.getContractName().equals(response.getContractName()) ||
                    !purPurchaseContract.getCompanySid().equals(response.getCompanySid())) {
                //校验同公司下采购合同名称是否重复
                checkNameUnique(purPurchaseContract);
            }
        }
        //设置账期
        setZhangqi(purPurchaseContract);
        //初始化合同上传状态
        purPurchaseContract.setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS);
        //设置确认信息
        setConfirmInfo(purPurchaseContract);
        // 预收付款方式组合的编码
        if (purPurchaseContract.getAccountsMethodGroup() != null){
            ConAccountMethodGroup methodGroup = conAccountMethodGroupMapper.selectById(purPurchaseContract.getAccountsMethodGroup());
            if (methodGroup != null){
                purPurchaseContract.setAccountsMethodGroupCode(methodGroup.getCode());
            }
        }
        int row = purPurchaseContractMapper.updateAllById(purPurchaseContract);
        if (row > 0) {
            // 支付方式
            List<PurPurchaseContractPayMethod> payMethodList = setMerge(purPurchaseContract);
            purPurchaseContractPayMethodService.updatePurPurchaseContractPayMethodList(purPurchaseContract.getPurchaseContractSid(), payMethodList);
            //采购合同信息-附件
            List<PurPurchaseContractAttachment> purPurchaseContractAttachmList = purPurchaseContract.getAttachmentList();
            if (CollectionUtils.isNotEmpty(purPurchaseContractAttachmList)) {
                purPurchaseContractAttachmList.stream().forEach(o -> {
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addPurPurchaseContractAttachment(purPurchaseContract, purPurchaseContractAttachmList);
            }
            else {
                purPurchaseContractAttachmentMapper.delete(
                        new UpdateWrapper<PurPurchaseContractAttachment>()
                                .lambda()
                                .eq(PurPurchaseContractAttachment::getPurchaseContractSid, purPurchaseContract.getPurchaseContractSid())
                );
            }
            if (ConstantsEms.CHECK_STATUS.equals(purPurchaseContract.getHandleStatus())){
                if (!ConstantsEms.CONTRACT_TYPE_KJ.equals(purPurchaseContract.getContractType())){
                    FinRecordAdvancePayment finRecordAdvancePayment = new FinRecordAdvancePayment();
                    finRecordAdvancePayment.setPurchaseContractSid(response.getPurchaseContractSid());
                    List<FinRecordAdvancePayment> finRecordAdvancePaymentList =
                            finRecordAdvancePaymentMapper.selectFinRecordAdvancePaymentList(finRecordAdvancePayment);
                    if (CollectionUtils.isEmpty(finRecordAdvancePaymentList)) {
                        advancePayment(purPurchaseContract);
                    }
                }
                //确认操作后删除待办
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, purPurchaseContract.getPurchaseContractSid()));
            }
            if (ConstantsEms.CHECK_STATUS.equals(response.getHandleStatus())){
                //动态栏
                String title = response.getShortName() + "的采购合同 " + response.getPurchaseContractCode() + "，合同信息发生变更，请知悉！";
                SysBusinessBcst businessBcst = new SysBusinessBcst();
                businessBcst.setUserId(response.getCreatorUserId()).setDocumentSid(response.getPurchaseContractSid()).setDocumentCode(response.getPurchaseContractCode())
                        .setMenuId(ConstantsWorkbench.purchase_contract)
                        .setTitle(title).setNoticeDate(new Date());
                sysBusinessBcstMapper.insert(businessBcst);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, purPurchaseContract);
            MongodbDeal.update(purPurchaseContract.getPurchaseContractSid(), response.getHandleStatus(), purPurchaseContract.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更采购合同信息
     *
     * @param purPurchaseContract 采购合同信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurPurchaseContract(PurPurchaseContract purPurchaseContract) {
        if (ConstantsEms.CONTRACT_TYPE_BC.equals(purPurchaseContract.getContractType()) && purPurchaseContract.getOriginalPurchaseContractSid() == null){
            throw new BaseException("补充协议，原合同号不能为空");
        }
        if (ConstantsEms.CONTRACT_TYPE_BZHTKJS.equals(purPurchaseContract.getContractType()) && purPurchaseContract.getFrameworkAgreementSid() == null){
            throw new CustomException("合同类型为“标准合同（框架式）”，框架协议号不能为空");
        }
        if (!ConstantsEms.CONTRACT_TYPE_KJ.equals(purPurchaseContract.getContractType()) && StrUtil.isBlank(purPurchaseContract.getPurchaseMode())){
            throw new CustomException("合同类型不是“框架协议”时，采购模式不能为空");
        }
        if (purPurchaseContract.getCurrencyAmountTax() != null && BigDecimal.ZERO.compareTo(purPurchaseContract.getCurrencyAmountTax()) >= 0){
            throw new BaseException("合同金额只能填写正数，请检查！");
        }
        if (ConstantsEms.CONTRACT_TYPE_BZ.equals(purPurchaseContract.getContractType())){
            if (purPurchaseContract.getCurrencyAmountTax() == null || purPurchaseContract.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0){
                throw new BaseException("标准合同，合同金额为必填且必须大于 0 ！");
            }
        }
        if (ConstantsEms.CONTRACT_TYPE_BC.equals(purPurchaseContract.getContractType()) && purPurchaseContract.getOriginalPurchaseContractSid() == null){
            throw new CustomException("补充协议，原合同号为必填，请填写后再操作！");
        }
        if (StrUtil.isBlank(purPurchaseContract.getContractSigner())){
            throw new BaseException("此操作合同签约人不能为空");
        }
        if (purPurchaseContract.getContractSignDate() == null){
            throw new BaseException("此操作合同签约日期不能为空");
        }
        //设置账期
        setZhangqi(purPurchaseContract);
        //预付款方式组合
        ConAccountMethodGroup accountMethodGroup = conAccountMethodGroupMapper.selectConAccountMethodGroupById(purPurchaseContract.getAccountsMethodGroup());
        //预付款结算方式
        if (!ConstantsEms.CONTRACT_TYPE_KJ.equals(purPurchaseContract.getContractType())){
            if (ConstantsEms.ADVANCE_SETTLE_MODE_HT.equals(purPurchaseContract.getAdvanceSettleMode()) && Double.parseDouble(accountMethodGroup.getAdvanceRate()) > 0) {
                if (purPurchaseContract.getCurrencyAmountTax() == null || purPurchaseContract.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new CustomException("此操作合同金额为必填且必须大于 0 ，请检查！");
                }
            }
        }
        purPurchaseContract.setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS);
        if (CollectionUtils.isNotEmpty(purPurchaseContract.getAttachmentList())){
            int i = 0;
            for (PurPurchaseContractAttachment item : purPurchaseContract.getAttachmentList()) {
                if (ConstantsEms.FILE_TYPE_CGHT.equals(item.getFileType())){
                    i++;
                    if (i >= 2){
                        throw new CustomException("每份合同只允许上传一份类型为“采购合同(电子版)”的附件");
                    }
                    purPurchaseContract.setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS_Y);
                }
            }
        }
        PurPurchaseContract response = purPurchaseContractMapper.selectPurPurchaseContractById(purPurchaseContract.getPurchaseContractSid());
        if (!purPurchaseContract.getPurchaseContractCode().equals(response.getPurchaseContractCode()) ||
                !purPurchaseContract.getCompanySid().equals(response.getCompanySid())) {
            //校验同公司下采购合同号是否重复
            checkCodeUnique(purPurchaseContract);
        }
        if (StrUtil.isNotBlank(purPurchaseContract.getContractName())){
            if (!purPurchaseContract.getContractName().equals(response.getContractName()) ||
                    !purPurchaseContract.getCompanySid().equals(response.getCompanySid())) {
                //校验同公司下采购合同名称是否重复
                checkNameUnique(purPurchaseContract);
            }
        }
        int row = purPurchaseContractMapper.updateAllById(purPurchaseContract);
        boolean warn = false, codeChange = false;
        if (row > 0) {
            // 支付方式
            List<PurPurchaseContractPayMethod> payMethodList = setMerge(purPurchaseContract);
            purPurchaseContractPayMethodService.updatePurPurchaseContractPayMethodList(purPurchaseContract.getPurchaseContractSid(), payMethodList);
            //采购合同信息-附件
            List<PurPurchaseContractAttachment> purPurchaseContractAttachmList = purPurchaseContract.getAttachmentList();
            addPurPurchaseContractAttachment(purPurchaseContract, purPurchaseContractAttachmList);
            //动态栏
            String title = response.getShortName() + "的采购合同 " + response.getPurchaseContractCode() + "，合同信息发生变更，请知悉！";
            SysBusinessBcst businessBcst = new SysBusinessBcst();
            businessBcst.setUserId(response.getCreatorUserId()).setDocumentSid(response.getPurchaseContractSid()).setDocumentCode(response.getPurchaseContractCode())
                    .setMenuId(ConstantsWorkbench.purchase_contract)
                    .setTitle(title).setNoticeDate(new Date());
            sysBusinessBcstMapper.insert(businessBcst);

            if (ConstantsEms.SUBMIT_STATUS.equals(purPurchaseContract.getHandleStatus())){
                Submit submit = new Submit();
                submit.setStartUserId(ApiThreadLocalUtil.get().getUserid().toString());
                submit.setFormType(FormType.CGHT_BG.getCode());
                List<FormParameter> list = new ArrayList();
                FormParameter formParameter = new FormParameter();
                formParameter.setParentId(purPurchaseContract.getPurchaseContractSid().toString());
                formParameter.setFormId(purPurchaseContract.getPurchaseContractSid().toString());
                formParameter.setFormCode(purPurchaseContract.getPurchaseContractCode());
                list.add(formParameter);
                submit.setFormParameters(list);
                workflowService.change(submit);
            }

            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, purPurchaseContract);
            // 变更时记录部分字段变更说明
            String remark = "";
            // 采购合同号
            if ((response.getPurchaseContractCode() != null && !response.getPurchaseContractCode().equals(purPurchaseContract.getPurchaseContractCode()))
                    || (StrUtil.isBlank(response.getPurchaseContractCode()) && StrUtil.isNotBlank(purPurchaseContract.getPurchaseContractCode()))) {
                String oldData = StrUtil.isBlank(response.getPurchaseContractCode()) ? "" : response.getPurchaseContractCode();
                String newData = StrUtil.isBlank(purPurchaseContract.getPurchaseContractCode()) ? "" : purPurchaseContract.getPurchaseContractCode();
                remark = remark + "采购合同号字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                warn = true;
                codeChange = true;
            }
            // 公司
            if ((response.getCompanySid() != null && !response.getCompanySid().equals(purPurchaseContract.getCompanySid()))
                    || (response.getCompanySid() == null && purPurchaseContract.getCompanySid() != null)) {
                List<BasCompany> companyList = basCompanyMapper.selectList(new QueryWrapper<BasCompany>().lambda()
                        .eq(BasCompany::getCompanySid, response.getCompanySid()).or().eq(BasCompany::getCompanySid, purPurchaseContract.getCompanySid()));
                Map<Long, BasCompany> companyMaps = companyList.stream().collect(Collectors.toMap(BasCompany::getCompanySid, Function.identity()));
                String oldData = response.getCompanySid() == null ? "" :
                        StrUtil.isBlank(companyMaps.get(response.getCompanySid()).getShortName()) ? "" : companyMaps.get(response.getCompanySid()).getShortName();
                String newData = purPurchaseContract.getCompanySid() == null ? "" :
                        StrUtil.isBlank(companyMaps.get(purPurchaseContract.getCompanySid()).getShortName()) ? "" : companyMaps.get(purPurchaseContract.getCompanySid()).getShortName();
                remark = remark + "公司字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                warn = true;
            }
            // 供应商
            if ((response.getVendorSid() != null && !response.getVendorSid().equals(purPurchaseContract.getVendorSid()))
                    || (response.getVendorSid() == null && purPurchaseContract.getVendorSid() != null)) {
                List<BasVendor> vendorList = basVendorMapper.selectList(new QueryWrapper<BasVendor>().lambda()
                        .eq(BasVendor::getVendorSid, response.getVendorSid()).or().eq(BasVendor::getVendorSid, purPurchaseContract.getVendorSid()));
                Map<Long, BasVendor> vendorMaps = vendorList.stream().collect(Collectors.toMap(BasVendor::getVendorSid, Function.identity()));
                String oldData = response.getVendorSid() == null ? "" :
                        StrUtil.isBlank(vendorMaps.get(response.getVendorSid()).getShortName()) ? "" : vendorMaps.get(response.getVendorSid()).getShortName();
                String newData = purPurchaseContract.getVendorSid() == null ? "" :
                        StrUtil.isBlank(vendorMaps.get(purPurchaseContract.getVendorSid()).getShortName()) ? "" : vendorMaps.get(purPurchaseContract.getVendorSid()).getShortName();
                remark = remark + "供应商字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                warn = true;
            }
            // 合同类型
            if ((response.getContractType() != null && !response.getContractType().equals(purPurchaseContract.getContractType()))
                    || (StrUtil.isBlank(response.getContractType()) && StrUtil.isNotBlank(purPurchaseContract.getContractType()))) {
                List<DictData> contractTypeList = sysDictDataService.selectDictData("s_contract_type");
                Map<String, String> contractTypeMaps = contractTypeList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                String oldData = StrUtil.isBlank(response.getContractType()) ? "" : contractTypeMaps.get(response.getContractType());
                String newData = StrUtil.isBlank(purPurchaseContract.getContractType()) ? "" : contractTypeMaps.get(purPurchaseContract.getContractType());
                remark = remark + "合同类型字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                warn = true;
            }
            // 合同金额(含税)
            if ((response.getCurrencyAmountTax() != null && response.getCurrencyAmountTax().compareTo(purPurchaseContract.getCurrencyAmountTax()) != 0)
                    || (response.getCurrencyAmountTax() == null && purPurchaseContract.getCurrencyAmountTax() != null)) {
                String oldData = response.getCurrencyAmountTax() == null ? "" : response.getCurrencyAmountTax().toString();
                String newData = purPurchaseContract.getCurrencyAmountTax() == null ? "" : purPurchaseContract.getCurrencyAmountTax().toString();
                remark = remark + "合同金额(含税)字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                warn = true;
            }
            // 付款方式组合
            if ((response.getAccountsMethodGroup() != null && !response.getAccountsMethodGroup().equals(purPurchaseContract.getAccountsMethodGroup()))
                    || (response.getAccountsMethodGroup() == null && purPurchaseContract.getAccountsMethodGroup() != null)) {
                List<ConAccountMethodGroup> methodList = conAccountMethodGroupMapper.selectList(new QueryWrapper<ConAccountMethodGroup>().lambda()
                        .eq(ConAccountMethodGroup::getSid, response.getAccountsMethodGroup())
                        .or().eq(ConAccountMethodGroup::getSid, purPurchaseContract.getAccountsMethodGroup()));
                Map<Long, String> methodMaps = methodList.stream().collect(Collectors.toMap(ConAccountMethodGroup::getSid, ConAccountMethodGroup::getName, (key1, key2) -> key2));
                String oldData = response.getAccountsMethodGroup() == null ? "" : methodMaps.get(response.getAccountsMethodGroup());
                String newData = purPurchaseContract.getAccountsMethodGroup() == null ? "" : methodMaps.get(purPurchaseContract.getAccountsMethodGroup());
                remark = remark + "付款方式组合字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                warn = true;
            }
            // 预付款结算方式
            if ((response.getAdvanceSettleMode() != null && !response.getAdvanceSettleMode().equals(purPurchaseContract.getAdvanceSettleMode()))
                    || (StrUtil.isBlank(response.getAdvanceSettleMode()) && StrUtil.isNotBlank(purPurchaseContract.getAdvanceSettleMode()))) {
                List<ConAdvanceSettleMode> advanceList = conAdvanceSettleModeMapper.selectList(new QueryWrapper<ConAdvanceSettleMode>().lambda()
                        .eq(ConAdvanceSettleMode::getCode, response.getAdvanceSettleMode())
                        .or().eq(ConAdvanceSettleMode::getCode, purPurchaseContract.getAdvanceSettleMode()));
                Map<String, String> advanceMaps = advanceList.stream().collect(Collectors.toMap(ConAdvanceSettleMode::getCode, ConAdvanceSettleMode::getName, (key1, key2) -> key2));
                String oldData = StrUtil.isBlank(response.getAdvanceSettleMode()) ? "" : advanceMaps.get(response.getAdvanceSettleMode());
                String newData = StrUtil.isBlank(purPurchaseContract.getAdvanceSettleMode()) ? "" : advanceMaps.get(purPurchaseContract.getAdvanceSettleMode());
                remark = remark + "预付款结算方式字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                warn = true;
            }
            MongodbUtil.insertUserLog(purPurchaseContract.getPurchaseContractSid(), BusinessType.CHANGE.getValue(), msgList, TITLE, remark);
            try {
                if (codeChange) {
                    String newData = purPurchaseContract.getPurchaseContractCode() == null ? "" : purPurchaseContract.getPurchaseContractCode();
                    // 采购订单
                    List<PurPurchaseOrder> orderList = purPurchaseOrderMapper.selectList(new QueryWrapper<PurPurchaseOrder>()
                            .lambda().eq(PurPurchaseOrder::getPurchaseContractSid, purPurchaseContract.getPurchaseContractSid()));
                    if (CollectionUtil.isNotEmpty(orderList)) {
                        orderList.forEach(order->{
                            String oldData = order.getPurchaseContractCode() == null ? "" : order.getPurchaseContractCode();
                            MongodbUtil.insertUserLog(order.getPurchaseOrderSid(), BusinessType.QITA.getValue(), null, "采购订单",
                                    "更新合同号字段值（变更前：" + oldData + "，变更后：" + newData + "）");
                        });
                        List<Long> orderSidList = orderList.stream().map(PurPurchaseOrder::getPurchaseOrderSid).collect(toList());
                        UpdateWrapper<PurPurchaseOrder> orderUpdateWrapper = new UpdateWrapper<>();
                        orderUpdateWrapper.lambda().in(PurPurchaseOrder::getPurchaseOrderSid, orderSidList);
                        orderUpdateWrapper.lambda().set(PurPurchaseOrder::getPurchaseContractCode, purPurchaseContract.getPurchaseContractCode());
                        purPurchaseOrderMapper.update(null, orderUpdateWrapper);
                    }
                    // 外发加工费单
                    List<ManManufactureOutsourceSettle> settleList = manufactureOutsourceSettleMapper.selectList(new QueryWrapper<ManManufactureOutsourceSettle>()
                            .lambda().eq(ManManufactureOutsourceSettle::getPurchaseContractSid, purPurchaseContract.getPurchaseContractSid()));
                    if (CollectionUtil.isNotEmpty(settleList)) {
                        settleList.forEach(order->{
                            String oldData = order.getPurchaseContractCode() == null ? "" : order.getPurchaseContractCode();
                            MongodbUtil.insertUserLog(order.getManufactureOutsourceSettleSid(), BusinessType.QITA.getValue(), null,
                                    "外发加工费结算单", "更新合同号字段值（变更前：" + oldData + "，变更后：" + newData + "）");
                        });
                        List<Long> orderSidList = settleList.stream().map(ManManufactureOutsourceSettle::getManufactureOutsourceSettleSid).collect(toList());
                        UpdateWrapper<ManManufactureOutsourceSettle> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.lambda().in(ManManufactureOutsourceSettle::getManufactureOutsourceSettleSid, orderSidList);
                        updateWrapper.lambda().set(ManManufactureOutsourceSettle::getPurchaseContractCode, purPurchaseContract.getPurchaseContractCode());
                        manufactureOutsourceSettleMapper.update(null, updateWrapper);
                    }
                }
            } catch (Exception e) {
                log.error("更新其它单据合同号报错");
            }
       }
        if (warn) {
            return 100;
        }
        return row;
    }

    /**
     * 校验和设置账期
     *
     * @param contract 合同
     * @return
     */
    private void setZhangqi(PurPurchaseContract contract){
        if (contract.getYfAccountValidDays() != null && contract.getYfAccountValidDays() <= 0){
            throw new BaseException("预付款账期(天)仅允许输入正整数");
        }
        if (contract.getZqAccountValidDays() != null && contract.getZqAccountValidDays() <= 0){
            throw new BaseException("中期款账期(天)仅允许输入正整数");
        }
        if (contract.getWqAccountValidDays() != null && contract.getWqAccountValidDays() <= 0){
            throw new BaseException("尾款账期(天)仅允许输入正整数");
        }
        if (contract.getDayType() == null){
            contract.setDayType(ConstantsFinance.DAY_TYPE_ZRR);
        }
    }

    /**
     * 批量删除采购合同信息
     *
     * @param purchaseContractSids 需要删除的采购合同信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurPurchaseContractByIds(List<Long> purchaseContractSids) {
        PurPurchaseContract params = new PurPurchaseContract();
        params.setPurchaseContractSids(purchaseContractSids);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = purPurchaseContractMapper.countByDomain(params);
        if (count != purchaseContractSids.size()) {
            throw new BaseException("仅保存和已退回状态才允许删除");
        }
        List<PurPurchaseOrder> orderList = purPurchaseOrderMapper.selectPurPurchaseOrderList(new PurPurchaseOrder().setPurchaseContractSidList(purchaseContractSids));
        if (CollectionUtil.isNotEmpty(orderList)) {
            List<String> codeList = orderList.stream().map(PurPurchaseOrder::getPurchaseContractCode).distinct().collect(Collectors.toList());
            String codes = "";
            for (String code : codeList) {
                codes = codes + code + ";";
            }
            if (codes.endsWith(";")) {
                codes = codes.substring(0, codes.length() - 1);
                throw new BaseException("合同" + codes + "已被采购订单引用，无法删除！");
            }
        }
        //删除采购合同信息
        purPurchaseContractMapper.deleteBatchIds(purchaseContractSids);
        // 支付方式
        purPurchaseContractPayMethodService.deletePurPurchaseContractPayMethodByContract(purchaseContractSids);
        //删除采购合同信息附件
        purPurchaseContractAttachmentMapper.deletePurPurchaseContractAttachmentByIds(purchaseContractSids);
        PurPurchaseContract purPurchaseContract = new PurPurchaseContract();
        purchaseContractSids.forEach(purchaseContractSid -> {
            purPurchaseContract.setPurchaseContractSid(purchaseContractSid);
            //校验是否存在待办
            checkTodoExist(purPurchaseContract);
        });
        //插入日志
        purchaseContractSids.forEach(id->{
            MongodbUtil.insertUserLog(Long.valueOf(id), BusinessType.DELETE.getValue(), null, TITLE);
        });
        return purchaseContractSids.size();
    }

    /**
     * 更改确认状态
     *
     * @param purPurchaseContract
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(PurPurchaseContract purchaseContract) {
        int row = 1;
        Long[] sids = purchaseContract.getPurchaseContractSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                purchaseContract.setPurchaseContractSid(id);
                PurPurchaseContract entity = purPurchaseContractMapper.selectPurPurchaseContractById(id);
                if (ConstantsEms.CHECK_STATUS.equals(entity.getHandleStatus())) {
                    throw new BaseException(purchaseContract.getPurchaseContractCode() + "请不要重复确认！");
                }
                entity.setHandleStatus(purchaseContract.getHandleStatus());
                setConfirmInfo(entity);
                if (!ConstantsEms.SUBMIT_STATUS.equals(purchaseContract.getHandleStatus())){
                    row = purPurchaseContractMapper.updateById(purchaseContract);
                }
                //校验是否存在待办
                checkTodoExist(purchaseContract);
                if (!ConstantsEms.CONTRACT_TYPE_KJ.equals(entity.getContractType()) && ConstantsEms.CHECK_STATUS.equals(entity.getHandleStatus())){
                    advancePayment(entity);
                }
                if (ConstantsEms.CHECK_STATUS.equals(purchaseContract.getHandleStatus())){
                    //确认操作后删除待办
                    sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                            .in(SysTodoTask::getDocumentSid, sids));
                    //插入日志
                    for (Long sid : sids) {
                        MongodbUtil.insertUserLog(sid, BusinessType.CHECK.getValue(),null,TITLE);
                    }
                }
            }
        }
        return row;
    }

    /**
     * 采购合同下拉框列表
     */
    @Override
    public List<PurPurchaseContract> getPurPurchaseContractList() {
        return purPurchaseContractMapper.getPurPurchaseContractList();
    }

    /**
     * 采购合同下拉框列表
     */
    @Override
    public List<PurPurchaseContract> getPurchaseContractList(PurPurchaseContract purPurchaseContract) {
        return purPurchaseContractMapper.getPurchaseContractList(purPurchaseContract);
    }

    /**
     * 原合同号下拉框接口
     */
    @Override
    public List<PurPurchaseContract> getOriginalContractList(PurPurchaseContract purPurchaseContract) {
        return purPurchaseContractMapper.getOriginalContractList(purPurchaseContract);
    }

    /**
     * 作废销售合同信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancellationPurPurchaseContractById(PurPurchaseContract request) {
        Long purchaseContractSid = request.getPurchaseContractSid();
        PurPurchaseContract purPurchaseContract = purPurchaseContractMapper.selectPurPurchaseContractById(purchaseContractSid);
        if (!ConstantsEms.CHECK_STATUS.equals(purPurchaseContract.getHandleStatus())) {
            throw new BaseException("所选数据非'已确认'状态，无法作废！");
        }
        List<PurPurchaseOrder> purchaseOrderList = purPurchaseOrderMapper.selectList(new QueryWrapper<PurPurchaseOrder>().lambda()
                .eq(PurPurchaseOrder::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .eq(PurPurchaseOrder::getPurchaseContractSid, purchaseContractSid));
        if (CollectionUtil.isNotEmpty(purchaseOrderList)){
            throw new BaseException("存在'已确认'状态的采购订单已引用该合同，无法作废！");
        }
        List<FinRecordAdvancePayment> recordAdvancePaymentList =
                finRecordAdvancePaymentMapper.selectList(new QueryWrapper<FinRecordAdvancePayment>().lambda()
                        .eq(FinRecordAdvancePayment::getPurchaseContractSid, purchaseContractSid));
        if (CollectionUtil.isNotEmpty(recordAdvancePaymentList)){
            FinRecordAdvancePayment finRecordAdvancePayment = new FinRecordAdvancePayment();
            recordAdvancePaymentList.forEach(recordAdvancePayment ->{
                List<FinRecordAdvancePaymentItem> recordAdvanceReceiptItemList =
                        finRecordAdvancePaymentItemMapper.selectList(new QueryWrapper<FinRecordAdvancePaymentItem>().lambda()
                                .eq(FinRecordAdvancePaymentItem::getRecordAdvancePaymentSid, recordAdvancePayment.getRecordAdvancePaymentSid()));
                if (CollectionUtil.isNotEmpty(recordAdvanceReceiptItemList)){
                    recordAdvanceReceiptItemList.forEach(recordAdvanceReceiptItem ->{
                        if (!ConstantsEms.CLEAR_STATUS_WHX.equals(recordAdvanceReceiptItem.getClearStatus())){
                            throw new BaseException("该合同对应的供应商待付预付款流水非'未核销'状态，无法作废！");
                        }
                    });
                }
                finRecordAdvancePayment.setHandleStatus(HandleStatus.INVALID.getCode());
                finRecordAdvancePayment.setRecordAdvancePaymentSid(recordAdvancePayment.getRecordAdvancePaymentSid());
                finRecordAdvancePaymentMapper.updateById(finRecordAdvancePayment);
            });
        }
        purPurchaseContract.setHandleStatus(HandleStatus.INVALID.getCode());
        purPurchaseContract.setCancelRemark(request.getCancelRemark());
        String code = purPurchaseContract.getPurchaseContractCode();
        String name = purPurchaseContract.getContractName();
        if (code == null){
            code = "（作废）";
        } else {
            code = code + "（作废）";
        }
        if (name == null){
            name = "（作废）";
        } else {
            name = name + "（作废）";
        }
        purPurchaseContract.setPurchaseContractCode(code);
        purPurchaseContract.setContractName(name);
        int row = purPurchaseContractMapper.updateById(purPurchaseContract);
        MongodbUtil.insertUserLog(purchaseContractSid,BusinessType.CANCEL.getValue(), null, TITLE, request.getCancelRemark());
        return row;
    }

    /**
     * 结案销售合同信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int closingPurPurchaseContractById(Long purchaseContractSid) {
        PurPurchaseContract purPurchaseContract = purPurchaseContractMapper.selectPurPurchaseContractById(purchaseContractSid);
        if (!ConstantsEms.CHECK_STATUS.equals(purPurchaseContract.getHandleStatus())) {
            throw new BaseException("所选数据非'已确认'状态，无法结案！");
        }
        purPurchaseOrderMapper.update(null, new UpdateWrapper<PurPurchaseOrder>().lambda()
                .set(PurPurchaseOrder::getHandleStatus, HandleStatus.CLOSED.getCode())
                .in(PurPurchaseOrder::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .in(PurPurchaseOrder::getPurchaseContractSid, purchaseContractSid));
        purPurchaseContract.setHandleStatus(HandleStatus.CONCLUDE.getCode());
        return purPurchaseContractMapper.updateById(purPurchaseContract);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setToexpireDays(PurPurchaseContract purPurchaseContract) {
        if (purPurchaseContract.getPurchaseContractSidList().length == 0){
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<PurPurchaseContract> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //即将到期天数
        updateWrapper.in(PurPurchaseContract::getPurchaseContractSid, purPurchaseContract.getPurchaseContractSidList())
                .set(PurPurchaseContract::getToexpireDays, purPurchaseContract.getToexpireDays());
        row = purPurchaseContractMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 纸质合同签收
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int signPurPurchaseContractById(PurPurchaseContract purPurchaseContract) {
        LambdaUpdateWrapper<PurPurchaseContract> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(PurPurchaseContract::getPurchaseContractSid,purPurchaseContract.getPurchaseContractSids()).set(PurPurchaseContract::getSignInStatus, purPurchaseContract.getSignInStatus());
        int row = purPurchaseContractMapper.update(null, updateWrapper);
        return row;
    }

    @Override
    public List<PurPurchaseContractFormResponse> getCountForm(PurPurchaseContract purPurchaseContract) {
        List<PurPurchaseContractFormResponse> list = purPurchaseContractMapper.getCountForm(purPurchaseContract);
        return list;
    }

    @Override
    public List<PurPurchaseContractFormResponse> getCountFormItem(PurPurchaseContract purPurchaseContract) {
        List<PurPurchaseContractFormResponse> list = purPurchaseContractMapper.getCountFormItem(purPurchaseContract);
        return list;
    }

    /**
     * 按“商品编码+合同交期”汇总“订单明细”页签的数据
     *
     * @param purPurchaseContract 合同信息
     * @return 合同信息集合
     */
    @Override
    public List<PurPurchaseOrderItem> groupPurchaseOrderItemList(PurPurchaseContract purPurchaseContract) {
        List<PurPurchaseOrderItem> response = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(purPurchaseContract.getPurPurchaseOrderItems())) {
            // 按“商品编码+合同交期” 分组
            Map<String, List<PurPurchaseOrderItem>> map = purPurchaseContract.getPurPurchaseOrderItems().stream()
                    .collect(Collectors.groupingBy(o -> String.valueOf(o.getMaterialSid())+"-"+String.valueOf(o.getContractDate())));
            // 组内求和
            for (String key : map.keySet()) {
                List<PurPurchaseOrderItem> itemList = map.get(key);
                // 数量小计
                BigDecimal quantity = itemList.stream().map(PurPurchaseOrderItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
                // 金额小计 （金额 = 价格 * 数量）
                BigDecimal money = itemList.stream().map(x-> {
                    if (x.getNearPurchasePriceTax() == null || x.getQuantity()==null) {
                        return BigDecimal.ZERO;
                    }
                    else {
                        return x.getNearPurchasePriceTax().multiply(x.getQuantity());
                    }
                }).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
                PurPurchaseOrderItem item = new PurPurchaseOrderItem();
                BeanCopyUtils.copyProperties(itemList.get(0), item);
                money = money.divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP);
                item.setSumMoneyAmount(money).setSumQuantity(quantity);
                response.add(item);
            }
        }
        return response;
    }

    /**
     * 查询合同模板列表
     *
     * @param request 请求
     * @return 合同模板列表
     */
    @Override
    public List<ContractTemplateAttach> selectContractTemplateList(ContractTemplateAttach request) {
        return purPurchaseContractMapper.selectContractTemplateList(request);
    }

    /**
     * 根据模板和合同数据自动生成电子合同
     *
     * @param saleContractSid 销售合同信息ID
     * @return
     */
    @Override
    public MultipartFile autoGenContract(String filePath, String pre, Long purchaseContractSid){
        String contractFileName = pre;
        PurPurchaseContract purPurchaseContract = this.selectPurPurchaseContractById(purchaseContractSid);
        try {
            MultipartFile multipartFile = CommonUtil.createPDF(filePath,purPurchaseContract,null,true,null, contractFileName);
            return multipartFile;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 导入
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object importData(MultipartFile file) {
        int num = 0;
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            //数据字典Map
            List<DictData> yearDict = sysDictDataService.selectDictData("s_year"); //年份
            yearDict = yearDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> yearMaps = yearDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> contractTypeDict = sysDictDataService.selectDictData("s_contract_type"); //合同类型
            contractTypeDict = contractTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> contractTypeMaps = contractTypeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> purchaseModeDict = sysDictDataService.selectDictData("s_price_type"); //采购模式
            purchaseModeDict = purchaseModeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> purchaseModeMaps = purchaseModeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> rawMaterialModeDict = sysDictDataService.selectDictData("s_raw_material_mode"); //供料方式
            rawMaterialModeDict = rawMaterialModeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> rawMaterialModeMaps = rawMaterialModeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> contractTagDict = sysDictDataService.selectDictData("s_contract_tag");
            contractTagDict = contractTagDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> contractTagDictMaps = contractTagDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //错误信息
            CommonErrMsgResponse errMsg = null;
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            HashMap<String, String> codeMap = new HashMap<>();
            HashMap<String, String> nameMap = new HashMap<>();
            //
            PurPurchaseContract purPurchaseContract = null;
            List<PurPurchaseContract> purchaseContractList = new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                //填充总列数
                copy(objects, readAll);
                num = i + 1;
                /**
                 * 采购合同号 必填
                 */
                String purchaseContractCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (StrUtil.isBlank(purchaseContractCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("采购合同号不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (purchaseContractCode.length() > 20){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("采购合同号长度不能超过20个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        purchaseContractCode = purchaseContractCode.replaceAll(" ","");
                    }
                }

                /**
                 * 合同名称 非必填
                 */
                String contractName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                if (StrUtil.isNotBlank(contractName)) {
                    if (contractName.length() > 200){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("合同名称长度不能超过200个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 供应商简称 必填
                 */
                String vendorShortName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                Long vendorSid = null;
                Long vendorCode = null;
                String vendorName = null;
                if (StrUtil.isBlank(vendorShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("供应商简称不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>().lambda().eq(BasVendor::getShortName, vendorShortName));
                        if (basVendor == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("简称为"+ vendorShortName +"没有对应的供应商，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(basVendor.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basVendor.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的供应商必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            vendorSid = basVendor.getVendorSid();
                            vendorName = basVendor.getVendorName();
                            vendorCode = basVendor. getVendorCode();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(vendorShortName + "供应商档案存在重复，请先检查该供应商，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 合同类型(数据字典) 必填
                 */
                String contractTypeName = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                String contractType = null;
                if (StrUtil.isBlank(contractTypeName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("合同类型不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    contractType = contractTypeMaps.get(contractTypeName); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(contractType)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("合同类型配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 公司简称 必填
                 */
                String companyShortName = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                Long companySid = null;
                String companyCode = null;
                String companyName = null;
                if (StrUtil.isBlank(companyShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("公司简称不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        BasCompany basCompany = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda().eq(BasCompany::getShortName,companyShortName));
                        if (basCompany == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("简称为"+ companyShortName +"没有对应的公司，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(basCompany.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basCompany.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的公司必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            companySid = basCompany.getCompanySid();
                            companyCode = basCompany.getCompanyCode();
                            companyName = basCompany.getCompanyName();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(companyShortName + "公司档案存在重复，请先检查该公司，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                //校验合同号
                if (StrUtil.isNotBlank(purchaseContractCode) && companySid != null){
                    if (codeMap.get(purchaseContractCode+companySid.toString()) == null) {
                        codeMap.put(purchaseContractCode + companySid.toString(), String.valueOf(num));
                        QueryWrapper<PurPurchaseContract> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("purchase_contract_code", purchaseContractCode)
                                .eq("company_sid", companySid);
                        PurPurchaseContract checkCodeResust = purPurchaseContractMapper.selectOne(queryWrapper);
                        if (checkCodeResust != null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("系统中，该公司下已存在相同采购合同号，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("表格中，该公司下已存在相同采购合同号，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                //校验合同名称
                if (StrUtil.isNotBlank(contractName) && companySid != null){
                    if (nameMap.get(contractName+companySid.toString()) == null) {
                        nameMap.put(contractName + companySid.toString(), String.valueOf(num));
                        QueryWrapper<PurPurchaseContract> queryWrapper2 = new QueryWrapper<>();
                        queryWrapper2.eq("contract_name", contractName)
                                .eq("company_sid", companySid);
                        PurPurchaseContract checkNameResust = purPurchaseContractMapper.selectOne(queryWrapper2);
                        if (checkNameResust != null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("系统中，该公司下已存在相同采购合同名称，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("表格中，该公司下已存在相同采购合同名称，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 年份 必填
                 */
                String yearName = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                String year = null;
                if (StrUtil.isBlank(yearName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("年份不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    year = yearMaps.get(yearName); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(year)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("年份配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 合同金额 选填
                 */
                String currencyAmountTax_s = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                BigDecimal currencyAmountTax = null;
                if (StrUtil.isNotBlank(currencyAmountTax_s)) {
                    if (!JudgeFormat.isValidDouble(currencyAmountTax_s,10,5)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("合同金额格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        currencyAmountTax = new BigDecimal(currencyAmountTax_s);
                        if (currencyAmountTax != null && BigDecimal.ZERO.compareTo(currencyAmountTax) >= 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("合同金额只能填写正数，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                if (contractType != null && ConstantsEms.CONTRACT_TYPE_BZ.equals(contractType)) {
                    if (currencyAmountTax == null || currencyAmountTax.compareTo(BigDecimal.ZERO) <= 0) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("合同类型为“标准合同”，合同金额为必填且必须大于0，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 付款方式组合编码 ，预付款结算方式 ， 尾款结算方式 在合同类型为标准合同框架式的时候不能填
                 */
                String accountsMethodGroupCode = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                String advanceSettleModeName = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                String remainSettleModeName = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
                if (contractType != null && ConstantsEms.CONTRACT_TYPE_BZHTKJS.equals(contractType)){
                    if (StrUtil.isNotBlank(accountsMethodGroupCode) || StrUtil.isNotBlank(advanceSettleModeName) || StrUtil.isNotBlank(remainSettleModeName)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("合同类型为标准合同(框架式)时，付款方式组合/预付款结算方式/尾款结算方式必须为空，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 付款方式组合编码 必填
                 */
                ConAccountMethodGroup conAccountMethodGroup = null;
                Long accountsMethodGroup = null;
                String weikuanRemark = null, zhongqikuanRemark = null, yufukuanRemark = null;
                /**
                 * 预付款结算方式 必填
                 */
                String advanceSettleMode = null;
                /**
                 * 尾款结算方式 必填
                 */
                String remainSettleMode = null;
                if (contractType != null){
                    /**
                     * 付款方式组合编码 必填
                     */
                    if (StrUtil.isNotBlank(accountsMethodGroupCode)) {
                        try {
                            conAccountMethodGroup = conAccountMethodGroupMapper.selectOne(new QueryWrapper<ConAccountMethodGroup>()
                                    .lambda().eq(ConAccountMethodGroup::getCode,accountsMethodGroupCode));
                            if (conAccountMethodGroup == null){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("没有找到 " + accountsMethodGroupCode + " 付款方式组合，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                if (!ConstantsEms.SHOUFUKUAN_TYPE_FK.equals(conAccountMethodGroup.getShoufukuanType())){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("付款方式组合的收付款类型应为付款，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                if (ConstantsEms.DISENABLE_STATUS.equals(conAccountMethodGroup.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(conAccountMethodGroup.getHandleStatus())){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("对应的付款方式组合必须是确认且已启用的状态，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                yufukuanRemark = conAccountMethodGroup.getYushoufukuanRemark();
                                zhongqikuanRemark = conAccountMethodGroup.getZhongqikuanRemark();
                                weikuanRemark = conAccountMethodGroup.getWeikuanRemark();
                                accountsMethodGroup = conAccountMethodGroup.getSid();
                            }
                        }catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(accountsMethodGroupCode + "付款方式组合配置存在重复，请先检查该付款方式组合，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    /**
                     * 预付款结算方式 必填
                     */
                    if (StrUtil.isNotBlank(advanceSettleModeName)) {
                        try {
                            ConAdvanceSettleMode conAdvanceSettleMode = conAdvanceSettleModeMapper.selectOne(new QueryWrapper<ConAdvanceSettleMode>()
                                    .lambda().eq(ConAdvanceSettleMode::getName,advanceSettleModeName));
                            if (conAdvanceSettleMode == null){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("预付款结算方式配置错误，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            else {
                                if (ConstantsEms.DISENABLE_STATUS.equals(conAdvanceSettleMode.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(conAdvanceSettleMode.getHandleStatus())){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("对应的预付款结算方式必须是确认且已启用的状态，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                advanceSettleMode = conAdvanceSettleMode.getCode();
                            }
                        }catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(advanceSettleModeName + "预付款结算方式配置存在重复，请先检查该预付款结算方式，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    //预付款结算方式
                    if (contractType != null && !ConstantsEms.CONTRACT_TYPE_KJ.equals(contractType)){
                        if (ConstantsEms.ADVANCE_SETTLE_MODE_HT.equals(advanceSettleMode) &&
                                conAccountMethodGroup != null && Double.parseDouble(conAccountMethodGroup.getAdvanceRate()) > 0) {
                            if (currencyAmountTax == null || currencyAmountTax.compareTo(BigDecimal.ZERO) <= 0) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("按合同的预付款结算方式且预付款比例大于0的合同金额为必填且必须大于 0 ，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                    /**
                     * 尾款结算方式 必填
                     */
                    if (StrUtil.isNotBlank(remainSettleModeName)) {
                        if (!"按发票".equals(remainSettleModeName)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("尾款结算方式只能是“按发票”，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            try {
                                ConRemainSettleMode conRemainSettleMode = conRemainSettleModeMapper.selectOne(new QueryWrapper<ConRemainSettleMode>()
                                        .lambda().eq(ConRemainSettleMode::getName,remainSettleModeName));
                                if (conRemainSettleMode == null){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("尾款结算方式配置错误，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                else {
                                    if (ConstantsEms.DISENABLE_STATUS.equals(conRemainSettleMode.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(conRemainSettleMode.getHandleStatus())){
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("对应的尾款结算方式必须是确认且已启用的状态，导入失败！");
                                        errMsgList.add(errMsg);
                                    }
                                    remainSettleMode = conRemainSettleMode.getCode();
                                }
                            }catch (Exception e){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(remainSettleModeName + "尾款结算方式配置存在重复，请先检查该尾款结算方式，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }
                /**
                 * 有效期(起) 必填
                 */
                String startDate_s = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                Date startDate = null;
                if (StrUtil.isBlank(startDate_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("有效期(起)不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    if (!JudgeFormat.isValidDate(startDate_s)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("有效期(起)日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        startDate = new Date();
                        try {
                            startDate = DateUtil.parse(startDate_s);
                        } catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("有效期(起)日期格式错误，导入失败！");
                            errMsgList.add(errMsg);
                            startDate = null;
                        }
                    }
                }
                /**
                 * 有限期(至) 必填
                 */
                String endDate_s = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString();
                Date endDate = null;
                if (StrUtil.isBlank(endDate_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("有效期(至)不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    if (!JudgeFormat.isValidDate(endDate_s)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("有效期(至)日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        endDate = new Date();
                        try {
                            endDate = DateUtil.parse(endDate_s);
                        } catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("有效期(至)日期格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                if (startDate != null && endDate != null && endDate.before(startDate)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("有效期(起)不能大于有效期(至)，导入失败！");
                    errMsgList.add(errMsg);
                }
                /**
                 * 税率 必填
                 */
                String taxRateName = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString();
                BigDecimal taxRate = null;
                if (StrUtil.isBlank(taxRateName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("税率不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        ConTaxRate conTaxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda().eq(ConTaxRate::getTaxRateName,taxRateName));
                        if (conTaxRate == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("税率配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(conTaxRate.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(conTaxRate.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的税率必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            taxRate = conTaxRate.getTaxRateValue();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(taxRateName + "税率配置存在重复，请先检查该税率，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 供方合同号 选填
                 */
                String vendorContractCode = objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString();
                if (StrUtil.isNotBlank(vendorContractCode)){
                    if (vendorContractCode.length() > 20){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("供方合同号长度不能超过20个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 产品季 选填
                 */
                String productSeasonName = objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString();
                Long productSeasonSid = null;
                if (StrUtil.isNotBlank(productSeasonName)){
                    try {
                        BasProductSeason basProductSeason = productSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>()
                                .lambda().eq(BasProductSeason::getProductSeasonName,productSeasonName));
                        if (basProductSeason == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("名称为"+ productSeasonName +"没有对应的产品季，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(basProductSeason.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basProductSeason.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的产品季必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            productSeasonSid = basProductSeason.getProductSeasonSid();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(productSeasonName + "产品季存在重复，请先检查该产品季，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 采购员账号 选填
                 */
                String buyer = objects.get(15) == null || objects.get(15) == "" ? null : objects.get(15).toString();
                if (StrUtil.isNotBlank(buyer)){
                    try {
                        SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>()
                                .lambda().eq(SysUser::getUserName,buyer));
                        if (sysUser == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("账号为"+ buyer +"没有对应的采购员，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.SYS_COMMON_STATUS_Y.equals(sysUser.getStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的采购员账号必须是已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(buyer + "采购员账号存在重复，请先检查该采购员，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 物料类型 选填
                 */
                String materialTypeName = objects.get(16) == null || objects.get(16) == "" ? null : objects.get(16).toString();
                String materialType = null;
                if (StrUtil.isBlank(materialTypeName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("物料类型不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else{
                    try {
                        ConMaterialType conMaterialType = conMaterialTypeMapper.selectOne(new QueryWrapper<ConMaterialType>()
                                .lambda().eq(ConMaterialType::getName,materialTypeName));
                        if (conMaterialType == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("物料类型配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(conMaterialType.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(conMaterialType.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的物料类型必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            materialType = conMaterialType.getCode();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(materialTypeName + "物料类型存在重复，请先检查该物料类型，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 货期免责天数 选填
                 */
                String exemptionDay_s = objects.get(17) == null || objects.get(17) == "" ? null : objects.get(17).toString();
                Long exemptionDay = null;
                if (StrUtil.isNotBlank(exemptionDay_s)){
                    try {
                        exemptionDay = Long.parseLong(exemptionDay_s);
                        if (exemptionDay < new Long(0)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("货期免责天数的数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    } catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("货期免责天数的数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 质保期(月) 选填
                 */
                String warranty_s = objects.get(18) == null || objects.get(18) == "" ? null : objects.get(18).toString();
                Long warranty = null;
                String warrantyUnit = null;
                if (StrUtil.isNotBlank(warranty_s)){
                    try {
                        warranty = Long.parseLong(warranty_s);
                        if (warranty < new Long(0)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("货期免责天数的数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    } catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("质保期(月)的数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                warrantyUnit = "MO";
                /**
                 * 短发允许比例(%) 选填
                 */
                String allowableRatioShort_s = objects.get(19) == null || objects.get(19) == "" ? null : objects.get(19).toString();
                BigDecimal allowableRatioShort = null;
                if (StrUtil.isNotBlank(allowableRatioShort_s)){
                    try {
                        allowableRatioShort = new BigDecimal(allowableRatioShort_s);
                        if (new BigDecimal(100).compareTo(allowableRatioShort) < 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("短交允许比例(%)不能超过100%，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (allowableRatioShort.compareTo(BigDecimal.ZERO) < 0){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("短交允许比例(%)不能小于0，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                allowableRatioShort = allowableRatioShort.divide(new BigDecimal(100));
                                allowableRatioShort = allowableRatioShort.setScale(4, BigDecimal.ROUND_HALF_DOWN);
                                if (!JudgeFormat.isValidDouble(String.valueOf(allowableRatioShort),3,4)){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("短交允许比例(%)的数据格式错误，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            }
                        }
                    } catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("短交允许比例(%)的数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 超发允许比例(%) 选填
                 */
                String allowableRatioMore_s = objects.get(20) == null || objects.get(20) == "" ? null : objects.get(20).toString();
                BigDecimal allowableRatioMore = null;
                if (StrUtil.isNotBlank(allowableRatioMore_s)){
                    try {
                        allowableRatioMore = new BigDecimal(allowableRatioMore_s);
                        if (new BigDecimal(100).compareTo(allowableRatioMore) < 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("超交允许比例(%)不能超过100%，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (allowableRatioMore.compareTo(BigDecimal.ZERO) < 0){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("超交允许比例(%)不能小于0，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                allowableRatioMore = allowableRatioMore.divide(new BigDecimal(100));
                                allowableRatioMore = allowableRatioMore.setScale(4, BigDecimal.ROUND_HALF_DOWN);
                                if (!JudgeFormat.isValidDouble(String.valueOf(allowableRatioMore),1,4)){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("超交允许比例(%)的数据格式错误，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            }

                        }
                    } catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("超交允许比例(%)的数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 采购模式(数据字典) 非必填
                 */
                String purchaseMode = objects.get(21) == null || objects.get(21) == "" ? null : objects.get(21).toString();
                if (contractType != null && !ConstantsEms.CONTRACT_TYPE_KJ.equals(contractType) && StrUtil.isBlank(purchaseMode)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("合同类型为标准合同/标准合同(框架式)时，采购模式不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (StrUtil.isNotBlank(purchaseMode)) {
                    purchaseMode = purchaseModeMaps.get(purchaseMode); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(purchaseMode)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("采购模式配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 框架协议号 非必填
                 */
                String frameworkAgreementCode = objects.get(22) == null || objects.get(22) == "" ? null : objects.get(22).toString();
                Long frameworkAgreementSid = null;
                if (contractType != null && ConstantsEms.CONTRACT_TYPE_BZHTKJS.equals(contractType) && StrUtil.isBlank(frameworkAgreementCode)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("标准合同(框架式)的框架协议号为必填，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (contractType != null && (ConstantsEms.CONTRACT_TYPE_KJ.equals(contractType) && StrUtil.isNotBlank(frameworkAgreementCode) ||
                        (ConstantsEms.CONTRACT_TYPE_BZ.equals(contractType) && StrUtil.isNotBlank(frameworkAgreementCode)))){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("合同类型为框架协议/标准合同时，框架协议号必须为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (StrUtil.isNotBlank(frameworkAgreementCode)){
                    try {
                        PurPurchaseContract contract  = purPurchaseContractMapper.selectOne(new QueryWrapper<PurPurchaseContract>()
                                .lambda()
                                .eq(PurPurchaseContract::getVendorSid,vendorSid)
                                .eq(PurPurchaseContract::getCompanySid,companySid)
                                .eq(PurPurchaseContract::getPurchaseContractCode,frameworkAgreementCode));
                        if (contract == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("该公司和供应商组合下，不存在 " + frameworkAgreementCode + " 的框架协议号，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (!ConstantsEms.CHECK_STATUS.equals(contract.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("框架协议号必须为确认状态的数据，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            if (contractType != null && ConstantsEms.CONTRACT_TYPE_BZHTKJS.equals(contractType)){
                                accountsMethodGroup = contract.getAccountsMethodGroup();
                                advanceSettleMode = contract.getAdvanceSettleMode();
                                remainSettleMode = contract.getRemainSettleMode();
                                if (accountsMethodGroup != null){
                                    conAccountMethodGroup = new ConAccountMethodGroup();
                                    conAccountMethodGroup = conAccountMethodGroupMapper.selectOne(new QueryWrapper<ConAccountMethodGroup>()
                                            .lambda().eq(ConAccountMethodGroup::getSid,accountsMethodGroup));
                                    if (Double.parseDouble(conAccountMethodGroup.getAdvanceRate()) > 0
                                            && ConstantsEms.ADVANCE_SETTLE_MODE_HT.equals(advanceSettleMode)){
                                        if (currencyAmountTax == null){
                                            errMsg = new CommonErrMsgResponse();
                                            errMsg.setItemNum(num);
                                            errMsg.setMsg("框架协议号中付款方式组合的预付款比例大于0且预付款结算方式按合同，合同金额为必填，导入失败！");
                                            errMsgList.add(errMsg);
                                        }else {
                                            if (currencyAmountTax.compareTo(BigDecimal.ZERO) <= 0 ){
                                                errMsg = new CommonErrMsgResponse();
                                                errMsg.setItemNum(num);
                                                errMsg.setMsg("框架协议号中付款方式组合的预付款比例大于0且预付款结算方式按合同，合同金额不能小于等于0，导入失败！");
                                                errMsgList.add(errMsg);
                                            }
                                        }
                                    }
                                }
                            }
                            frameworkAgreementSid = contract.getPurchaseContractSid();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(frameworkAgreementCode + "框架协议存在重复，请先检查该框架协议，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 合同签约人 必填
                 */
                String contractSigner = objects.get(23) == null || objects.get(23) == "" ? null : objects.get(23).toString();
                String contractSignerName = null;
                if (StrUtil.isBlank(contractSigner)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("合同签约人不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>()
                                .lambda().eq(SysUser::getUserName,contractSigner));
                        if (sysUser == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("账号为"+ contractSigner +"没有对应的合同签约人，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.SYS_COMMON_STATUS_Y.equals(sysUser.getStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的合同签约人必须是已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            contractSignerName = sysUser.getNickName();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(contractSigner + "合同签约人存在重复，请先检查该合同签约人，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 合同签约日期 必填
                 */
                String contractSignDate_s = objects.get(24) == null || objects.get(24) == "" ? null : objects.get(24).toString();
                Date contractSignDate = null;
                if (StrUtil.isBlank(contractSignDate_s)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("合同签约日期不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDate(contractSignDate_s)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("合同签约日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        contractSignDate = new Date();
                        try {
                            contractSignDate = DateUtil.parse(contractSignDate_s);
                        } catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("合同签约日期格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /**
                 * 供料方式 选填
                 */
                String rawMaterialMode = objects.get(25) == null || objects.get(25) == "" ? null : objects.get(25).toString();
                if (StrUtil.isNotBlank(rawMaterialMode)){
                    rawMaterialMode = rawMaterialModeMaps.get(rawMaterialMode); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(rawMaterialMode)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("甲供料方式配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 合同标识 选填
                 */
                String contractTag = objects.get(26) == null || objects.get(26) == "" ? null : objects.get(26).toString();
                if (StrUtil.isNotBlank(contractTag)){
                    contractTag = contractTagDictMaps.get(contractTag); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(contractTag)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("合同标识填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 备注 选填
                 */
                String performRemark = objects.get(27) == null || objects.get(27) == "" ? null : objects.get(27).toString();
                /**
                 * 备注 选填
                 */
                String remark = objects.get(28) == null || objects.get(28) == "" ? null : objects.get(28).toString();
                if (allowableRatioShort == null){
                    allowableRatioShort = BigDecimal.ZERO;
                }
                if (allowableRatioMore == null){
                    allowableRatioMore = BigDecimal.ZERO;
                }
                if (CollectionUtil.isEmpty(errMsgList)){
                    purPurchaseContract = new PurPurchaseContract();
                    purPurchaseContract.setPurchaseContractCode(purchaseContractCode).setContractName(contractName).setAccountsMethodGroupCode(accountsMethodGroupCode)
                            .setVendorSid(vendorSid).setContractType(contractType).setCompanySid(companySid).setPurchaseMode(purchaseMode)
                            .setStartDate(startDate).setEndDate(endDate).setProductSeasonSid(productSeasonSid).setRawMaterialMode(rawMaterialMode)
                            .setVendorContractCode(vendorContractCode).setTaxRate(taxRate).setDayType(ConstantsFinance.DAY_TYPE_ZRR)
                            .setYear(Integer.parseInt(year)).setCurrencyAmountTax(currencyAmountTax).setAccountsMethodGroup(accountsMethodGroup)
                            .setAdvanceSettleMode(advanceSettleMode).setRemainSettleMode(remainSettleMode).setBuyer(buyer)
                            .setMaterialType(materialType).setExemptionDay(exemptionDay).setWarranty(warranty).setWarrantyUnit(warrantyUnit)
                            .setAllowableRatioShort(allowableRatioShort).setAllowableRatioMore(allowableRatioMore).setRemark(remark);
                    purPurchaseContract.setContractSigner(contractSigner).setContractSignerName(contractSignerName).setContractSignDate(contractSignDate);
                    purPurchaseContract.setFrameworkAgreementSid(frameworkAgreementSid).setFrameworkAgreementCode(frameworkAgreementCode)
                            .setYufukuanRemark(yufukuanRemark).setZhongqikuanRemark(zhongqikuanRemark).setWeikuanRemark(weikuanRemark);
                    purPurchaseContract.setHandleStatus(ConstantsEms.SAVA_STATUS).setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date());
                    purPurchaseContract.setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS).setSignInStatus(ConstantsEms.CONTRACT_SIGNIN_STATUS)
                            .setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN)
                            .setContractTag(contractTag)
                            .setPerformRemark(performRemark);
                    purchaseContractList.add(purPurchaseContract);
                }
            }

            if (CollectionUtil.isNotEmpty(errMsgList)){
                return errMsgList;
            }
            if (CollectionUtil.isNotEmpty(purchaseContractList)){
                purchaseContractList.forEach(item->{
                    //插入数据
                    purPurchaseContractMapper.insert(item);
                    //待办通知
                    SysTodoTask sysTodoTask = new SysTodoTask();
                    sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName(ConstantsEms.TABLE_PURCHASE_CONTRACT)
                            .setDocumentSid(item.getPurchaseContractSid())
                            .setDocumentCode(item.getPurchaseContractCode())
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTask.setTitle("采购合同 " + item.getPurchaseContractCode() + " 当前是保存状态，请及时处理！");
                    sysTodoTaskMapper.insert(sysTodoTask);
                    MongodbUtil.insertUserLog(item.getPurchaseContractSid(), BusinessType.IMPORT.getValue(),null,TITLE);
                });
            }
        }catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return num-2;
    }

    //填充-主表
    public void copy(List<Object> objects, List<List<Object>> readAll){
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i=lineSize;i<size;i++){
            Object o = new Object();
            o=null;
            objects.add(o);
        }
    }

}
