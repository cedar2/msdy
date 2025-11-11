package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.api.service.RemoteFileService;
import com.platform.api.service.RemoteFlowableService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.CustomException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.SaleReportResponse;
import com.platform.ems.domain.dto.response.form.SalSaleContractFormResponse;
import com.platform.ems.eSignApp.domain.EsignFile;
import com.platform.ems.eSignApp.domain.SignFlowCallback;
import com.platform.ems.eSignApp.domain.SignFlowConfig;
import com.platform.ems.eSignApp.domain.SignerPsnInfo;
import com.platform.ems.eSignApp.domain.dto.response.ESignDownloadFileResponse;
import com.platform.ems.eSignApp.domain.dto.response.ESignDownloadResponse;
import com.platform.ems.eSignApp.service.EsignFileService;
import com.platform.ems.eSignApp.service.EsignFlowService;
import com.platform.ems.eSignApp.service.JssdkService;
import com.platform.ems.eSignApp.util.SignFileUtil;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.*;
import com.platform.ems.plug.mapper.*;
import com.platform.ems.service.*;
import com.platform.ems.util.CommonUtil;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.data.BigDecimalSum;
import com.platform.ems.workflow.domain.Submit;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.flowable.domain.vo.FormParameter;
import com.platform.framework.web.domain.server.SysFile;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysBusinessBcstMapper;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.mapper.SystemUserMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 销售合同信息Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-18
 */
@Service
@SuppressWarnings("all")
public class SalSaleContractServiceImpl extends ServiceImpl<SalSaleContractMapper, SalSaleContract> implements ISalSaleContractService {
    @Autowired
    private SalSaleContractMapper salSaleContractMapper;
    @Autowired
    private SalSaleContractAttachmentMapper salSaleContractAttachmentMapper;
    @Autowired
    private ISalSaleContractPayMethodService salSaleContractPayMethodService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private FinRecordAdvanceReceiptMapper finRecordAdvanceReceiptMapper;
    @Autowired
    private FinRecordAdvanceReceiptItemMapper finRecordAdvanceReceiptItemMapper;
    @Autowired
    private ConAccountMethodGroupMapper conAccountMethodGroupMapper;
    @Autowired
    private ConAdvanceSettleModeMapper conAdvanceSettleModeMapper;
    @Autowired
    private SalSalesOrderMapper salSalesOrderMapper;
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
    private BasProductSeasonMapper productSeasonMapper;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private SystemUserMapper sysUserMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private ISystemUserService userService;
    @Autowired
    private ISalSalePriceService salSalePriceService;
    @Autowired
    private ISalSalesOrderService salSalesOrderService;
    @Autowired
    private SalSalesOrderItemMapper salSalesOrderItemMapper;
    @Autowired
    private ISalSalesIntentOrderItemService salesIntentOrderItemService;
    @Autowired
    private SalSalesIntentOrderMapper salSalesIntentOrderMapper;
    @Autowired
    private SalSalesIntentOrderItemMapper salSalesIntentOrderItemMapper;
    @Autowired
    private IWorkFlowService workflowService;
    @Autowired
    private RemoteFlowableService remoteFlowableService;

    @Autowired
    private SysDefaultSettingClientMapper defaultSettingClientMapper;
    @Autowired
    private EsignFileService esignFileService;
    @Autowired
    private EsignFlowService esignFlowService;
    @Autowired
    private RemoteFileService remoteFileService;
    @Resource
    private JssdkService jssdkService;
    @Resource
    private RestTemplate restTemplate;

    private static final String TITLE = "销售合同信息";

    /**
     * 查询销售合同信息
     *
     * @param saleContractSid 销售合同信息ID
     * @return 销售合同信息
     */
    @Override
    public SalSaleContract selectSalSaleContractById(Long saleContractSid) {
        SalSaleContract salSaleContract = salSaleContractMapper.selectSalSaleContractById(saleContractSid);
        if (salSaleContract == null) {
            return new SalSaleContract();
        }
        // 支付方式
        List<SalSaleContractPayMethod> payMethodList = salSaleContractPayMethodService.selectSalSaleContractPayMethodListByContract(saleContractSid);
        List<SalSaleContractPayMethod> yusf = new ArrayList<>();
        List<SalSaleContractPayMethod> zq = new ArrayList<>();
        List<SalSaleContractPayMethod> wq = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(payMethodList)){
            yusf = payMethodList.stream().filter(o->ConstantsFinance.ACCOUNT_CAT_YSFK.equals(o.getAccountCategory())).collect(Collectors.toList());
            zq = payMethodList.stream().filter(o->ConstantsFinance.ACCOUNT_CAT_ZQK.equals(o.getAccountCategory())).collect(Collectors.toList());
            wq = payMethodList.stream().filter(o->ConstantsFinance.ACCOUNT_CAT_WK.equals(o.getAccountCategory())).collect(Collectors.toList());
        }
        salSaleContract.setPayMethodListYusf(yusf);
        salSaleContract.setPayMethodListZq(zq);
        salSaleContract.setPayMethodListWq(wq);
        //销售合同信息-附件
        SalSaleContractAttachment salSaleContractAttachment = new SalSaleContractAttachment();
        salSaleContractAttachment.setSaleContractSid(saleContractSid);
        List<SalSaleContractAttachment> salSaleContractAttachmentList =
                salSaleContractAttachmentMapper.selectSalSaleContractAttachmentList(salSaleContractAttachment);
        salSaleContract.setAttachmentList(salSaleContractAttachmentList);
        FlowTaskVo flowTaskVo = new FlowTaskVo();
        flowTaskVo.setFormId(saleContractSid);
        //判断是否是最后一个节点
        AjaxResult nextFlowNode = remoteFlowableService.getNextFlowNode(flowTaskVo);
        Object code = nextFlowNode.get("code");
        if(ConstantsEms.CODE_SUCESS.equals(nextFlowNode.get("code").toString())) {
            salSaleContract.setIsFinallyNode(ConstantsEms.YES);
        }
        //获取销售价明细
        SaleReportResponse request = new SaleReportResponse();
        request.setHandleStatuses(new String[]{HandleStatus.SUBMIT.getCode(),HandleStatus.CHANGEAPPROVAL.getCode()});
        request.setCustomerSids(new Long[]{salSaleContract.getCustomerSid()});
        request.setIsFinallyNode(ConstantsEms.YES);
        List<SaleReportResponse> list = salSalePriceService.saleReport(request);
        salSaleContract.setSalePriceItems(list);
        // 获取销售意向单明细
        List<SalSalesIntentOrderItem> salesIntentOrderItemList = salSalesIntentOrderItemMapper.selectSalSalesIntentOrderItemList(new SalSalesIntentOrderItem()
                .setSaleIntentContractSid(saleContractSid));
        if (CollectionUtil.isNotEmpty(salesIntentOrderItemList)) {
            salesIntentOrderItemList = salesIntentOrderItemList.stream().filter(o -> !ConstantsEms.INVALID_STATUS.equals(o.getHandleStatus())).collect(toList());
        }
        salSaleContract.setIntentOrderItemList(new ArrayList<>());
        if (CollectionUtil.isNotEmpty(salesIntentOrderItemList)) {
            salSaleContract.setIntentOrderItemList(salesIntentOrderItemList);
            // 排序
            try {
                salSaleContract.setIntentOrderItemList(salesIntentOrderItemService.newSort(salesIntentOrderItemList));
            } catch (Exception e) {
                log.warn("销售意向单明细排序错误");
            }
            // 计算金额小计
            BigDecimal money = salesIntentOrderItemList.parallelStream().map(SalSalesIntentOrderItem::getCurrentAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            salSaleContract.setIntentOrderAmountTax(money);
            // 计算订单量小计
            BigDecimal quantity = salesIntentOrderItemList.parallelStream().map(SalSalesIntentOrderItem::getQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            salSaleContract.setIntentOrderQuantity(quantity);
        }
        //操作日志
        MongodbUtil.find(salSaleContract);
        return salSaleContract;
    }

    public void getPrice(List<SalSalesOrderItem> itemList,SalSaleContract salSaleContract) {
        if(CollectionUtil.isNotEmpty(itemList)){
            itemList.forEach(li->{
                SalSalePrice salSalePrice = new SalSalePrice();
                if (li.getSalePriceTax() == null) {
                    /**
                     * 新增列：价格（元）
                     * 若订单中有价格，就显示订单价格；
                     * 若没有，进行如下操作：
                     * 1）根据订单中的“编码+客供料方式+销售模式”在销售价档案中获取销售价的处理状态为“审批中/已确认/变更审批中”且有效期（至）>=当前日期的销售价数据
                     * 若1）查找到多笔数据，则选择有效期（至）距离当前日期最近的价格
                     */
                    salSalePrice = new SalSalePrice();
                    salSalePrice.setSaleMode(li.getSaleMode())
                            .setRawMaterialMode(li.getRawMaterialMode())
                            .setCustomerSid(li.getCustomerSid())
                            .setSku2Sid(li.getSku2Sid())
                            .setSku1Sid(li.getSku1Sid())
                            .setHandleStatuses(new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CONFIRMED.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()})
                            .setMaterialSid(li.getMaterialSid());
                    SalSalePriceItem price = salSalePriceService.getNearSalePrice(salSalePrice);
                    if (price != null && price.getSalePriceTax() != null) {
                        li.setNearSalePriceTax(price.getSalePriceTax());
                    }
                }
                else {
                    li.setNearSalePriceTax(li.getSalePriceTax());
                }
                // 待批价格，待批金额，待批税率
                salSalePrice = new SalSalePrice();
                salSalePrice.setSaleMode(li.getSaleMode())
                        .setCustomerSid(li.getCustomerSid())
                        .setRawMaterialMode(li.getRawMaterialMode())
                        .setSku2Sid(li.getSku2Sid())
                        .setSku1Sid(li.getSku1Sid())
                        .setNotApprovalStatus(new String[]{HandleStatus.SUBMIT.getCode(),HandleStatus.CHANGEAPPROVAL.getCode()})
                        .setMaterialSid(li.getMaterialSid());
                //获取销售
                SalSalePriceItem item = salSalePriceService.getNewSalePrice(salSalePrice);
                if(item.getSalePriceTax()!=null){
                    li.setWaitApprovalPrice(item.getSalePriceTax())
                            .setWaitApprovalTaxRate(item.getTaxRate());
                    if(li.getQuantity()!=null){
                        li.setWaitApprovalAmount(li.getQuantity().multiply(item.getSalePriceTax()));
                        li.setWaitApprovalAmount(li.getWaitApprovalAmount().divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP));
                    }
                }
                if(li.getQuantity()!=null&&li.getNearSalePriceTax()!=null){
                    li.setPriceTax(li.getQuantity().multiply(li.getNearSalePriceTax()));
                    li.setPriceTax(li.getPriceTax().divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP));
                }
                if(ConstantsEms.YES.equals(li.getFreeFlag())){
                    if(li.getWaitApprovalPrice()!=null){
                        li.setSalePriceTax(null)
                                .setPriceTax(null)
                                .setWaitApprovalAmount(BigDecimal.ZERO)
                                .setWaitApprovalPrice(BigDecimal.ZERO);
                    }else{
                        li.setSalePriceTax(BigDecimal.ZERO)
                                .setPriceTax(BigDecimal.ZERO)
                                .setWaitApprovalAmount(null)
                                .setWaitApprovalPrice(null);
                    }
                }
            });
            BigDecimal sumQuantity = itemList.stream().filter(li -> li.getQuantity() != null).map(li -> li.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumAmount = itemList.stream().filter(li -> li.getPriceTax() != null).map(li -> li.getPriceTax()).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumWaitApprovalAmount = itemList.stream().filter(li -> li.getWaitApprovalAmount() != null).map(li -> li.getWaitApprovalAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
            salSaleContract.setSumQuantity(sumQuantity)
                    .setSumAmount(sumAmount)
                    .setSumWaitApprovalAmount(sumWaitApprovalAmount);
        }
    }
    /**
     * 复制销售合同信息
     *
     * @param saleContractSid 销售合同信息ID
     * @return 销售合同信息
     */
    @Override
    public SalSaleContract copySalSaleContractById(Long saleContractSid) {
        SalSaleContract salSaleContract = salSaleContractMapper.selectSalSaleContractById(saleContractSid);
        if (salSaleContract == null) {
            return new SalSaleContract();
        }
        salSaleContract.setSaleContractCode(null).setSaleContractSid(null).setCurrencyAmountTax(null) .setOperLogList(null);
        salSaleContract.setHandleStatus(ConstantsEms.SAVA_STATUS).setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(null)
                .setSignInStatus(ConstantsEms.CONTRACT_SIGNIN_STATUS).setContractSignDate(null).setContractSigner(null).setCancelRemark(null)
                .setUpdaterAccount(null).setUpdateDate(null).setConfirmerAccount(null).setConfirmDate(null).setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS);
        // 支付方式
        List<SalSaleContractPayMethod> payMethodList = salSaleContractPayMethodService.selectSalSaleContractPayMethodListByContract(saleContractSid);
        List<SalSaleContractPayMethod> yusf = new ArrayList<>();
        List<SalSaleContractPayMethod> zq = new ArrayList<>();
        List<SalSaleContractPayMethod> wq = new ArrayList<>();
        for (SalSaleContractPayMethod method : payMethodList) {
            method.setSaleContractSid(null).setContractPayMethodSid(null) .setOperLogList(null);
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
        salSaleContract.setPayMethodListYusf(yusf);
        salSaleContract.setPayMethodListZq(zq);
        salSaleContract.setPayMethodListWq(wq);
        return salSaleContract;
    }

    /**
     * 按“商品编码+合同交期”汇总“订单明细”页签的数据
     *
     * @param salSaleContract 销售合同信息
     * @return 销售合同信息集合
     */
    @Override
    public List<SalSalesOrderItem> groupSaleOrderItemList(SalSaleContract salSaleContract) {
        List<SalSalesOrderItem> response = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(salSaleContract.getSalSalesOrderItems())) {
            // 按“商品编码+合同交期” 分组
            Map<String, List<SalSalesOrderItem>> map = salSaleContract.getSalSalesOrderItems().stream()
                    .collect(Collectors.groupingBy(o -> String.valueOf(o.getMaterialSid())+"-"+String.valueOf(o.getContractDate())));
            // 组内求和
            for (String key : map.keySet()) {
                List<SalSalesOrderItem> itemList = map.get(key);
                // 数量小计
                BigDecimal quantity = itemList.stream().map(SalSalesOrderItem::getQuantity).reduce(BigDecimal.ZERO,BigDecimalSum::sum);
                // 金额小计 （金额 = 价格 * 数量）
                BigDecimal money = itemList.stream().map(x-> {
                            if (x.getNearSalePriceTax() == null || x.getQuantity()==null) {
                                return BigDecimal.ZERO;
                            }
                            else {
                                return x.getNearSalePriceTax().multiply(x.getQuantity());
                            }
                }).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
                SalSalesOrderItem item = new SalSalesOrderItem();
                BeanCopyUtils.copyProperties(itemList.get(0), item);
                money = money.divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP);
                item.setSumMoneyAmount(money).setSumQuantity(quantity);
                response.add(item);
            }
        }
        return response;
    }

    /**
     * 合同的订单明细页签调用接口
     */
    @Override
    public SalSaleContract saleOrderItemList(SalSaleContract salSaleContract) {
        //获取销售订单明细
        SalSalesOrderItem salSalesOrderItem = new SalSalesOrderItem();
        salSalesOrderItem.setSaleContractSid(salSaleContract.getSaleContractSid());
        salSalesOrderItem.setNotInHandleStatus(new String[]{ConstantsEms.INVALID_STATUS});
        List<SalSalesOrderItem> itemList = salSalesOrderItemMapper.getItemList(salSalesOrderItem);
        getPrice(itemList, salSaleContract);
        salSaleContract.setSalSalesOrderItems(itemList);
        // 排序
        try {
            if (CollectionUtil.isNotEmpty(itemList)){
                return salSaleContract.setSalSalesOrderItems(salSalesOrderService.newSort(itemList));
            }
        } catch (Exception e) {
            log.warn("销售订单明细排序错误");
        }
        return salSaleContract;
    }

    /**
     * 订单号+商品编码+合同交期 汇总订单量和销售金额含税
     *
     * @param salSaleContract 销售合同信息
     * @return 销售合同信息集合
     */
    @Override
    public List<SalSalesOrder> selectGroupSaleOrderItemList(SalSaleContract salSaleContract) {
        return salSalesOrderItemMapper.selectSalSalesOrderItemGroupList(salSaleContract);
    }

    /**
     * 添加后，将选中销售订单的合同号改成此合同号，并刷新“订单明细”页签。
     *
     * @param salSaleContract 销售合同信息
     * @return 销售合同信息集合
     */
    public EmsResultEntity changeSaleOrderContract(SalSaleContract salSaleContract, String isContinue) {
        List<SalSalesOrderItem> orderItemList = salSaleContract.getSalSalesOrderItems();
        if (CollectionUtil.isEmpty(orderItemList)) {
            return EmsResultEntity.success();
        }
        // 添加订单时，如所选择的订单的合同的“特殊用途”不为“临时过渡”，则提示：选择了非过渡合同的订单，确定继续添加？
        if (!ConstantsEms.YES.equals(isContinue)) {
            List<SalSalesOrderItem> judge = orderItemList.stream().filter(o->!ConstantsEms.CONTRACT_PURPOSE_LS.equals(o.getContractPurpose())).collect(toList());
            if (CollectionUtil.isNotEmpty(judge)) {
                return EmsResultEntity.warning(null, "选择了非过渡合同的订单，确定继续添加？");
            }
        }
        Long[] orderSidList = orderItemList.stream().map(SalSalesOrderItem::getSalesOrderSid).toArray(Long[]::new);
        LambdaUpdateWrapper<SalSalesOrder> uupdateWrapper = new LambdaUpdateWrapper<>();
        uupdateWrapper.in(SalSalesOrder::getSalesOrderSid, orderSidList)
                .set(SalSalesOrder::getSaleContractSid, salSaleContract.getSaleContractSid());
        salSalesOrderMapper.update(null, uupdateWrapper);
        // 刷新订单明细
        return EmsResultEntity.success(saleOrderItemList(salSaleContract), "操作成功");
    }

    /**
     * 查询销售合同信息列表
     *
     * @param salSaleContract 销售合同信息
     * @return 销售合同信息
     */
    @Override
    public List<SalSaleContract> selectSalSaleContractList(SalSaleContract salSaleContract) {
        List<SalSaleContract> list = salSaleContractMapper.selectSalSaleContractList(salSaleContract);
        return list;
    }

    /**
     * 新增销售合同信息
     * 需要注意编码重复校验
     *
     * @param salSaleContract 销售合同信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long insertSalSaleContract(SalSaleContract salSaleContract) {
        //校验同公司下销售合同号是否重复
        checkCodeUnique(salSaleContract);
        //校验同公司下销售合同名称是否重复
        if (StrUtil.isNotBlank(salSaleContract.getContractName())){
            checkNameUnique(salSaleContract);
        }
        //设置账期
        setZhangqi(salSaleContract);
        //初始化合同上传状态
        salSaleContract.setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS);
        //设置确认信息
        setConfirmInfo(salSaleContract);
        // 预收付款方式组合的编码
        if (salSaleContract.getAccountsMethodGroup() != null){
            ConAccountMethodGroup methodGroup = conAccountMethodGroupMapper.selectById(salSaleContract.getAccountsMethodGroup());
            if (methodGroup != null){
                salSaleContract.setAccountsMethodGroupCode(methodGroup.getCode());
            }
        }
        int row = salSaleContractMapper.insert(salSaleContract);
        //报表
        if (!ConstantsEms.CONTRACT_TYPE_KJ.equals(salSaleContract.getContractType())){
            advanceReceipt(salSaleContract);
        }
        if (row > 0) {
            // 支付方式
            List<SalSaleContractPayMethod> payMethodList = setMerge(salSaleContract);
            salSaleContractPayMethodService.insertSalSaleContractPayMethodList(salSaleContract.getSaleContractSid(),payMethodList);
            //销售合同信息-附件
            List<SalSaleContractAttachment> salSaleContractAttachmentList = salSaleContract.getAttachmentList();
            if (CollectionUtils.isNotEmpty(salSaleContractAttachmentList)) {
                addSalSaleContractAttachment(salSaleContract, salSaleContractAttachmentList);
            }
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (!ConstantsEms.CHECK_STATUS.equals(salSaleContract.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName("s_sal_sale_contract").setTitle("")
                        .setDocumentSid(salSaleContract.getSaleContractSid());
                sysTodoTask.setDocumentCode(String.valueOf(salSaleContract.getSaleContractCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                if (ConstantsEms.SAVA_STATUS.equals(salSaleContract.getHandleStatus())){
                    sysTodoTask.setTitle("销售合同 " + salSaleContract.getSaleContractCode() + " 当前是保存状态，请及时处理！");
                }
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(salSaleContract.getSaleContractSid(), salSaleContract.getHandleStatus(), msgList, TITLE,null);
        }
        List<String> saleOrderSidList = salSaleContract.getSaleOrderSidList();
        //销售订单创建销售合同回写
        if(CollectionUtil.isNotEmpty(saleOrderSidList)){
            salSalesOrderMapper.update(new SalSalesOrder(),new UpdateWrapper<SalSalesOrder>().lambda()
            .in(SalSalesOrder::getSalesOrderSid,saleOrderSidList)
                    .set(SalSalesOrder::getSaleContractSid,salSaleContract.getSaleContractSid())
            );
        }
        return salSaleContract.getSaleContractSid();
    }

    /*
     * 合并支付方式
     */
    private List<SalSaleContractPayMethod> setMerge(SalSaleContract salSaleContract){
        List<SalSaleContractPayMethod> payMethodList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(salSaleContract.getPayMethodListYusf())){
            payMethodList.addAll(salSaleContract.getPayMethodListYusf());
        }
        if (CollectionUtil.isNotEmpty(salSaleContract.getPayMethodListZq())){
            payMethodList.addAll(salSaleContract.getPayMethodListZq());
        }
        if (CollectionUtil.isNotEmpty(salSaleContract.getPayMethodListWq())){
            payMethodList.addAll(salSaleContract.getPayMethodListWq());
        }
        return payMethodList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void advanceReceipt(SalSaleContract salSaleContract) {
        if (!ConstantsEms.YES.equals(ApiThreadLocalUtil.get().getSysUser().getIsBusinessFinance())){
            return;
        }
        if (ConstantsEms.CONTRACT_TYPE_KJ.equals(salSaleContract.getContractType())){
            return;
        }
        FinRecordAdvanceReceipt o = new FinRecordAdvanceReceipt();
        o.setSaleContractSid(salSaleContract.getSaleContractSid());
        List<FinRecordAdvanceReceipt> finRecordAdvanceReceiptList =
                finRecordAdvanceReceiptMapper.selectFinRecordAdvanceReceiptList(o);
        if (CollectionUtils.isNotEmpty(finRecordAdvanceReceiptList)) {
            finRecordAdvanceReceiptList.forEach(item->{
                LambdaUpdateWrapper<FinRecordAdvanceReceipt> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(FinRecordAdvanceReceipt::getRecordAdvanceReceiptSid,item.getRecordAdvanceReceiptSid())
                        .set(FinRecordAdvanceReceipt::getDocumentDate, new Date()).set(FinRecordAdvanceReceipt::getProductSeasonSid,salSaleContract.getProductSeasonSid())
                        .set(FinRecordAdvanceReceipt::getSalePerson,salSaleContract.getSalePerson()).set(FinRecordAdvanceReceipt::getPaymentYear,salSaleContract.getYear())
                        .set(FinRecordAdvanceReceipt::getRemark,salSaleContract.getRemark());
                int row = finRecordAdvanceReceiptMapper.update(null, updateWrapper);
                LambdaUpdateWrapper<FinRecordAdvanceReceiptItem> updateWrapper2 = new LambdaUpdateWrapper<>();
                updateWrapper2.eq(FinRecordAdvanceReceiptItem::getRecordAdvanceReceiptSid,item.getRecordAdvanceReceiptSid())
                        .set(FinRecordAdvanceReceiptItem::getTaxRate, salSaleContract.getTaxRate());
                finRecordAdvanceReceiptItemMapper.update(null, updateWrapper2);
            });
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(salSaleContract.getHandleStatus())) {
            //预收款方式组合
            ConAccountMethodGroup accountMethodGroup = conAccountMethodGroupMapper.selectConAccountMethodGroupById(salSaleContract.getAccountsMethodGroup());
            //1.预收款结算方式是否选择：按合同   2.预收款比例是否大于0   3.合同类型是否选择：标准合同，如是则合同金额需大于0
            if (ConstantsEms.ADVANCE_SETTLE_MODE_HT.equals(salSaleContract.getAdvanceSettleMode()) &&
                    Double.parseDouble(accountMethodGroup.getAdvanceRate()) > 0) {
                if (salSaleContract.getCurrencyAmountTax() == null || salSaleContract.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BaseException("此操作合同金额合同金额为必填且必须大于 0 ！");
                }
                //凭证日期
                FinRecordAdvanceReceipt finRecordAdvanceReceipt = new FinRecordAdvanceReceipt();
                BeanCopyUtils.copyProperties(salSaleContract, finRecordAdvanceReceipt);
                finRecordAdvanceReceipt.setDocumentDate(new Date());
                finRecordAdvanceReceipt.setBookType(ConstantsFinance.BOOK_TYPE_YUS);
                finRecordAdvanceReceipt.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_SC);
                finRecordAdvanceReceipt.setSaleContractSid(salSaleContract.getSaleContractSid());
                finRecordAdvanceReceipt.setSaleContractCode(salSaleContract.getSaleContractCode());
                finRecordAdvanceReceipt.setPaymentYear(salSaleContract.getYear());
                finRecordAdvanceReceipt.setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                finRecordAdvanceReceipt.setSettleMode(salSaleContract.getAdvanceSettleMode());
                finRecordAdvanceReceipt.setAdvanceRate(new BigDecimal(accountMethodGroup.getAdvanceRate()));
                finRecordAdvanceReceipt.setCurrencyAmountTaxContract(salSaleContract.getCurrencyAmountTax());
                finRecordAdvanceReceiptMapper.insert(finRecordAdvanceReceipt);

                //应收金额
                FinRecordAdvanceReceiptItem finRecordAdvanceReceiptItem = new FinRecordAdvanceReceiptItem();
                finRecordAdvanceReceiptItem.setCurrencyAmountTaxYings(salSaleContract.getCurrencyAmountTax().multiply(new BigDecimal(accountMethodGroup.getAdvanceRate())));
                finRecordAdvanceReceiptItem.setRecordAdvanceReceiptSid(finRecordAdvanceReceipt.getRecordAdvanceReceiptSid());
                finRecordAdvanceReceiptItem.setCurrencyAmountTaxYhx(BigDecimal.ZERO)
                        .setCurrencyAmountTaxHxz(BigDecimal.ZERO).setCreateDate(new Date())
                        .setTaxRate(salSaleContract.getTaxRate()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                        .setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                if (salSaleContract.getYsAccountValidDays() != null){
                    Date dateValid = new Date();
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(dateValid);
                    calendar.add(calendar.DATE,salSaleContract.getYsAccountValidDays()); //把日期往后增加i天,整数  往后推,负数往前移动
                    dateValid = calendar.getTime(); //这个时间就是日期往后推i天的结果
                    finRecordAdvanceReceiptItem.setAccountValidDays(new Long(salSaleContract.getYsAccountValidDays()));
                    finRecordAdvanceReceiptItem.setAccountValidDate(dateValid);
                }
                else {
                    finRecordAdvanceReceiptItem.setAccountValidDays(new Long(0));
                    finRecordAdvanceReceiptItem.setAccountValidDate(new Date());
                }
                finRecordAdvanceReceiptItemMapper.insert(finRecordAdvanceReceiptItem);
            }
        }
    }

    /**
     * 销售合同号校验不同公司下
     */
    @Override
    public void checkCode(SalSaleContract salSaleContract) {
        List<SalSaleContract> checkCodeResust = new ArrayList<>();
        String msg = "";
        String methodMsg = "";
        //新建时
        if (salSaleContract.getSaleContractSid() == null) {
            checkCodeResust = salSaleContractMapper.selectSalSaleContractList(new SalSaleContract().setContractCode(salSaleContract.getSaleContractCode()));
            checkCodeResust = checkCodeResust.stream().filter(o->!o.getCompanySid().toString().equals(salSaleContract.getCompanySid().toString())).collect(Collectors.toList());
            msg = salSaleContract.getSaleContractCode() + "，是否确认创建？";
        } else {
            //提交时
            SalSaleContract contract = salSaleContractMapper.selectById(salSaleContract.getSaleContractSid());
            checkCodeResust = salSaleContractMapper.selectSalSaleContractList(new SalSaleContract().setContractCode(contract.getSaleContractCode()));
            checkCodeResust = checkCodeResust.stream().filter(o->!o.getCompanySid().toString().equals(contract.getCompanySid().toString())).collect(Collectors.toList());
            // 支付方式占比不能不等于1
            methodMsg = salSaleContractPayMethodService.submitVerifyById(salSaleContract.getSaleContractSid());
            if (StrUtil.isNotBlank(methodMsg)){
                msg = contract.getSaleContractCode() + "，且" + methodMsg +"，是否确认提交？";
            }else {
                msg = contract.getSaleContractCode() + "，是否确认提交？";
            }
        }
        if (CollectionUtil.isNotEmpty(checkCodeResust)) {
            String shortName = "";
            for (SalSaleContract contract : checkCodeResust) {
                if (StrUtil.isNotBlank(contract.getCompanyShortName())){
                    shortName = shortName + contract.getCompanyShortName() + ",";
                }else {
                    shortName = shortName + contract.getCompanyName() + ",";
                }
            }
            if (shortName.endsWith(",")) {
                shortName = shortName.substring(0,shortName.length() - 1);
            }
            throw new CustomException("公司:" + shortName + "下已存在销售合同号 " + msg);
        }else {
            if (StrUtil.isNotBlank(methodMsg)){
                throw new CustomException(methodMsg + "，是否确认提交？");
            }
        }
    }

    /**
     * 销售合同号校验
     */
    private void checkCodeUnique(SalSaleContract salSaleContract) {
        QueryWrapper<SalSaleContract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sale_contract_code", salSaleContract.getSaleContractCode())
                .eq("company_sid", salSaleContract.getCompanySid());
        SalSaleContract checkCodeResust = salSaleContractMapper.selectOne(queryWrapper);
        if (checkCodeResust != null) {
            throw new BaseException("该公司下已存在相同销售合同号");
        }
    }

    /**
     * 销售合同名称校验
     */
    private void checkNameUnique(SalSaleContract salSaleContract) {
        QueryWrapper<SalSaleContract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contract_name", salSaleContract.getContractName())
                .eq("company_sid", salSaleContract.getCompanySid());
        SalSaleContract checkNameResust = salSaleContractMapper.selectOne(queryWrapper);
        if (checkNameResust != null) {
            throw new BaseException("该公司下已存在相同销售合同名称");
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(SalSaleContract salSaleContract) {
        if (salSaleContract == null) {
            return;
        }
        if (salSaleContract.getCurrencyAmountTax() != null && BigDecimal.ZERO.compareTo(salSaleContract.getCurrencyAmountTax()) >= 0) {
            throw new CustomException("合同金额只能填写正数，请检查！");
        }
        if (ConstantsEms.CONTRACT_TYPE_BZ.equals(salSaleContract.getContractType())) {
            if (salSaleContract.getCurrencyAmountTax() == null || salSaleContract.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0) {
                throw new CustomException("标准合同，合同金额为必填且必须大于 0 ！");
            }
        }
        if (ConstantsEms.CONTRACT_TYPE_BZHTKJS.equals(salSaleContract.getContractType()) && salSaleContract.getOutlineAgreementSid() == null){
            throw new CustomException("合同类型为“标准合同（框架式）”，框架协议号不能为空");
        }
        if (!ConstantsEms.CONTRACT_TYPE_KJ.equals(salSaleContract.getContractType()) && StrUtil.isBlank(salSaleContract.getSaleMode())){
            throw new CustomException("合同类型不是“框架协议”时，销售模式不能为空");
        }
        //预收款方式组合
        ConAccountMethodGroup accountMethodGroup = conAccountMethodGroupMapper.selectConAccountMethodGroupById(salSaleContract.getAccountsMethodGroup());
        //预收款结算方式
        if (!ConstantsEms.CONTRACT_TYPE_KJ.equals(salSaleContract.getContractType())){
            if (ConstantsEms.ADVANCE_SETTLE_MODE_HT.equals(salSaleContract.getAdvanceSettleMode()) && Double.parseDouble(accountMethodGroup.getAdvanceRate()) > 0) {
                if (salSaleContract.getCurrencyAmountTax() == null || salSaleContract.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new CustomException("此操作合同金额为必填且必须大于 0 ，请检查！");
                }
            }
        }
        if (ConstantsEms.CHECK_STATUS.equals(salSaleContract.getHandleStatus()) ||
                ConstantsEms.SUBMIT_STATUS.equals(salSaleContract.getHandleStatus())) {
            if (ConstantsEms.CONTRACT_TYPE_BC.equals(salSaleContract.getContractType()) && salSaleContract.getOriginalSaleContractSid() == null){
                throw new CustomException("补充协议，原合同号为必填，请填写后再操作！");
            }
            if (StrUtil.isBlank(salSaleContract.getContractSigner())){
                throw new CustomException("此操作合同签约人不能为空");
            }
            if (salSaleContract.getContractSignDate() == null){
                throw new CustomException("此操作合同签约日期不能为空");
            }
            if (ConstantsEms.CHECK_STATUS.equals(salSaleContract.getHandleStatus())){
                salSaleContract.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
                salSaleContract.setConfirmDate(new Date());
            }
        }
        if (CollectionUtils.isNotEmpty(salSaleContract.getAttachmentList())){
            int i = 0;
            for (SalSaleContractAttachment item : salSaleContract.getAttachmentList()) {
                if (ConstantsEms.FILE_TYPE_XSHT.equals(item.getFileType())){
                    i++;
                    if (i >= 2){
                        throw new CustomException("每份合同只允许上传一份类型为“销售合同(电子版)”的附件");
                    }
                    salSaleContract.setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS_Y);
                }
            }
        }
    }

    /**
     * 销售合同信息-附件对象
     */
    private void addSalSaleContractAttachment(SalSaleContract salSaleContract, List<SalSaleContractAttachment> salSaleContractAttachmentList) {
        salSaleContractAttachmentMapper.delete(
                new UpdateWrapper<SalSaleContractAttachment>()
                        .lambda()
                        .eq(SalSaleContractAttachment::getSaleContractSid, salSaleContract.getSaleContractSid())
        );
        salSaleContractAttachmentList.forEach(o -> {
            o.setSaleContractSid(salSaleContract.getSaleContractSid());
            salSaleContractAttachmentMapper.insert(o);
        });
    }

    /**
     * 修改销售合同信息
     *
     * @param salSaleContract 销售合同信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalSaleContract(SalSaleContract salSaleContract) {
        SalSaleContract response = salSaleContractMapper.selectSalSaleContractById(salSaleContract.getSaleContractSid());
        if (!salSaleContract.getSaleContractCode().equals(response.getSaleContractCode()) ||
                !salSaleContract.getCompanySid().equals(response.getCompanySid())) {
            //校验同公司下销售合同号是否重复
            checkCodeUnique(salSaleContract);
        }
        if (StrUtil.isNotBlank(salSaleContract.getContractName())){
            if (!salSaleContract.getContractName().equals(response.getContractName()) ||
                    !salSaleContract.getCompanySid().equals(response.getCompanySid())) {
                //校验同公司下销售合同名称是否重复
                checkNameUnique(salSaleContract);
            }
        }
        //设置账期
        setZhangqi(salSaleContract);
        //初始化合同上传状态
        salSaleContract.setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS);
        //设置确认信息
        setConfirmInfo(salSaleContract);
        // 预收付款方式组合的编码
        if (salSaleContract.getAccountsMethodGroup() != null){
            ConAccountMethodGroup methodGroup = conAccountMethodGroupMapper.selectById(salSaleContract.getAccountsMethodGroup());
            if (methodGroup != null){
                salSaleContract.setAccountsMethodGroupCode(methodGroup.getCode());
            }
        }
        int row = salSaleContractMapper.updateAllById(salSaleContract);
        //处理确认状态下没有流水的情况
        if (ConstantsEms.CHECK_STATUS.equals(salSaleContract.getHandleStatus())){
            if (!ConstantsEms.CONTRACT_TYPE_KJ.equals(salSaleContract.getContractType())){
                FinRecordAdvanceReceipt finRecordAdvanceReceipt = new FinRecordAdvanceReceipt();
                finRecordAdvanceReceipt.setSaleContractSid(salSaleContract.getSaleContractSid());
                List<FinRecordAdvanceReceipt> finRecordAdvanceReceiptList =
                        finRecordAdvanceReceiptMapper.selectFinRecordAdvanceReceiptList(finRecordAdvanceReceipt);
                if (CollectionUtils.isEmpty(finRecordAdvanceReceiptList)) {
                    advanceReceipt(salSaleContract);
                }
            }
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, salSaleContract.getSaleContractSid()));
        }
        if (row > 0) {
            // 支付方式
            List<SalSaleContractPayMethod> payMethodList = setMerge(salSaleContract);
            salSaleContractPayMethodService.updateSalSaleContractPayMethodList(salSaleContract.getSaleContractSid(),payMethodList);
            //销售合同信息-附件
            List<SalSaleContractAttachment> salSaleContractAttachmentList = salSaleContract.getAttachmentList();
            if (CollectionUtils.isNotEmpty(salSaleContractAttachmentList)) {
                salSaleContractAttachmentList.stream().forEach(o -> {
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addSalSaleContractAttachment(salSaleContract, salSaleContractAttachmentList);
            }
            else {
                salSaleContractAttachmentMapper.delete(
                        new UpdateWrapper<SalSaleContractAttachment>()
                                .lambda()
                                .eq(SalSaleContractAttachment::getSaleContractSid, salSaleContract.getSaleContractSid())
                );
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, salSaleContract);
            MongodbDeal.update(salSaleContract.getSaleContractSid(), response.getHandleStatus(),salSaleContract.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更销售合同信息
     *
     * @param salSaleContract 销售合同信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSalSaleContract(SalSaleContract salSaleContract) {
        if (ConstantsEms.CONTRACT_TYPE_BC.equals(salSaleContract.getContractType()) && salSaleContract.getOriginalSaleContractSid() == null){
            throw new BaseException("补充协议，原合同号为必填，请填写后确认！");
        }
        if (ConstantsEms.CONTRACT_TYPE_BZHTKJS.equals(salSaleContract.getContractType()) && salSaleContract.getOutlineAgreementSid() == null){
            throw new CustomException("合同类型为“标准合同（框架式）”，框架协议号不能为空");
        }
        if (!ConstantsEms.CONTRACT_TYPE_KJ.equals(salSaleContract.getContractType()) && StrUtil.isBlank(salSaleContract.getSaleMode())){
            throw new CustomException("合同类型不是“框架协议”时，销售模式不能为空");
        }
        if (salSaleContract.getCurrencyAmountTax() != null && BigDecimal.ZERO.compareTo(salSaleContract.getCurrencyAmountTax()) >= 0) {
            throw new BaseException("合同金额只能填写正数，请检查！");
        }
        if (ConstantsEms.CONTRACT_TYPE_BZ.equals(salSaleContract.getContractType())) {
            if (salSaleContract.getCurrencyAmountTax() == null || salSaleContract.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BaseException("标准合同，合同金额为必填且必须大于 0 ！");
            }
        }
        if (StrUtil.isBlank(salSaleContract.getContractSigner())){
            throw new BaseException("此操作合同签约人不能为空");
        }
        if (salSaleContract.getContractSignDate() == null){
            throw new BaseException("此操作合同签约日期不能为空");
        }
        //设置账期
        setZhangqi(salSaleContract);
        //预付款方式组合
        ConAccountMethodGroup accountMethodGroup = conAccountMethodGroupMapper.selectConAccountMethodGroupById(salSaleContract.getAccountsMethodGroup());
        //预付款结算方式
        if (!ConstantsEms.CONTRACT_TYPE_KJ.equals(salSaleContract.getContractType())){
            if (ConstantsEms.ADVANCE_SETTLE_MODE_HT.equals(salSaleContract.getAdvanceSettleMode()) && Double.parseDouble(accountMethodGroup.getAdvanceRate()) > 0) {
                if (salSaleContract.getCurrencyAmountTax() == null || salSaleContract.getCurrencyAmountTax().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new CustomException("此操作合同金额为必填且必须大于 0 ，请检查！");
                }
            }
        }
        SalSaleContract response = salSaleContractMapper.selectSalSaleContractById(salSaleContract.getSaleContractSid());
        if (!salSaleContract.getSaleContractCode().equals(response.getSaleContractCode()) ||
                !salSaleContract.getCompanySid().equals(response.getCompanySid())) {
            //校验同公司下销售合同号是否重复
            checkCodeUnique(salSaleContract);
        }
        if (StrUtil.isNotBlank(salSaleContract.getContractName())){
            if (!salSaleContract.getContractName().equals(response.getContractName()) ||
                    !salSaleContract.getCompanySid().equals(response.getCompanySid())) {
                //校验同公司下销售合同名称是否重复
                checkNameUnique(salSaleContract);
            }
        }
        //合同附件，和纸质合同上传状态
        salSaleContract.setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS);
        if (CollectionUtils.isNotEmpty(salSaleContract.getAttachmentList())){
            int i = 0;
            for (SalSaleContractAttachment item : salSaleContract.getAttachmentList()) {
                if (ConstantsEms.FILE_TYPE_XSHT.equals(item.getFileType())){
                    i++;
                    if (i >= 2){
                        throw new BaseException("每份合同只允许上传一份类型为“销售合同(电子版)”的附件");
                    }
                    salSaleContract.setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS_Y);
                }
            }
        }
        int row = salSaleContractMapper.updateAllById(salSaleContract);
        boolean warn = false, codeChange = false;
        if (row > 0) {
            // 支付方式
            List<SalSaleContractPayMethod> payMethodList = setMerge(salSaleContract);
            salSaleContractPayMethodService.updateSalSaleContractPayMethodList(salSaleContract.getSaleContractSid(), payMethodList);
            //销售合同信息-附件
            List<SalSaleContractAttachment> salSaleContractAttachmentList = salSaleContract.getAttachmentList();
            if (CollectionUtils.isNotEmpty(salSaleContractAttachmentList)) {
                salSaleContractAttachmentList.stream().forEach(o -> {
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addSalSaleContractAttachment(salSaleContract, salSaleContractAttachmentList);
            }
            else {
                salSaleContractAttachmentMapper.delete(
                        new UpdateWrapper<SalSaleContractAttachment>()
                                .lambda()
                                .eq(SalSaleContractAttachment::getSaleContractSid, salSaleContract.getSaleContractSid())
                );
            }
            //动态栏
            String title = response.getShortName() + "的销售合同 " + response.getSaleContractCode() + "，合同信息发生变更，请知悉！";
            SysBusinessBcst businessBcst = new SysBusinessBcst();
            businessBcst.setUserId(response.getCreatorUserId()).setDocumentSid(response.getSaleContractSid()).setDocumentCode(response.getSaleContractCode())
                    .setTitle(title).setNoticeDate(new Date());
            sysBusinessBcstMapper.insert(businessBcst);
            //审批
            if (ConstantsEms.SUBMIT_STATUS.equals(salSaleContract.getHandleStatus())){
                Submit submit = new Submit();
                submit.setStartUserId(ApiThreadLocalUtil.get().getUserid().toString());
                submit.setFormType(FormType.XSHT_BG.getCode());
                List<FormParameter> list = new ArrayList();
                FormParameter formParameter = new FormParameter();
                formParameter.setParentId(salSaleContract.getSaleContractSid().toString());
                formParameter.setFormId(salSaleContract.getSaleContractSid().toString());
                formParameter.setFormCode(salSaleContract.getSaleContractCode());
                list.add(formParameter);
                submit.setFormParameters(list);
                workflowService.change(submit);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, salSaleContract);
            // 变更时记录部分字段变更说明
            String remark = "";

            // 销售合同号
            if ((response.getSaleContractCode() != null && !response.getSaleContractCode().equals(salSaleContract.getSaleContractCode()))
                    || (StrUtil.isBlank(response.getSaleContractCode()) && StrUtil.isNotBlank(salSaleContract.getSaleContractCode()))) {
                String oldData = StrUtil.isBlank(response.getSaleContractCode()) ? "" : response.getSaleContractCode();
                String newData = StrUtil.isBlank(salSaleContract.getSaleContractCode()) ? "" : salSaleContract.getSaleContractCode();
                remark = remark + "销售合同号字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                warn = true;
                codeChange = true;
            }
            // 公司
            if ((response.getCompanySid() != null && !response.getCompanySid().equals(salSaleContract.getCompanySid()))
                    || (response.getCompanySid() == null && salSaleContract.getCompanySid() != null)) {
                List<BasCompany> companyList = basCompanyMapper.selectList(new QueryWrapper<BasCompany>().lambda()
                        .eq(BasCompany::getCompanySid, response.getCompanySid()).or().eq(BasCompany::getCompanySid, salSaleContract.getCompanySid()));
                Map<Long, BasCompany> companyMaps = companyList.stream().collect(Collectors.toMap(BasCompany::getCompanySid, Function.identity()));
                String oldData = response.getCompanySid() == null ? "" :
                        StrUtil.isBlank(companyMaps.get(response.getCompanySid()).getShortName()) ? "" : companyMaps.get(response.getCompanySid()).getShortName();
                String newData = salSaleContract.getCompanySid() == null ? "" :
                        StrUtil.isBlank(companyMaps.get(salSaleContract.getCompanySid()).getShortName()) ? "" : companyMaps.get(salSaleContract.getCompanySid()).getShortName();
                remark = remark + "公司字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                warn = true;
            }
            // 客户
            if ((response.getCustomerSid() != null && !response.getCustomerSid().equals(salSaleContract.getCustomerSid()))
                    || (response.getCustomerSid() == null && salSaleContract.getCustomerSid() != null)) {
                List<BasCustomer> customerList = basCustomerMapper.selectList(new QueryWrapper<BasCustomer>().lambda()
                        .eq(BasCustomer::getCustomerSid, response.getCustomerSid()).or().eq(BasCustomer::getCustomerSid, salSaleContract.getCustomerSid()));
                Map<Long, BasCustomer> customerMaps = customerList.stream().collect(Collectors.toMap(BasCustomer::getCustomerSid, Function.identity()));
                String oldData = response.getCustomerSid() == null ? "" :
                        StrUtil.isBlank(customerMaps.get(response.getCustomerSid()).getShortName()) ? "" : customerMaps.get(response.getCustomerSid()).getShortName();
                String newData = salSaleContract.getCustomerSid() == null ? "" :
                                StrUtil.isBlank(customerMaps.get(salSaleContract.getCustomerSid()).getShortName()) ? "" : customerMaps.get(salSaleContract.getCustomerSid()).getShortName();
                remark = remark + "客户字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                warn = true;
            }
            // 合同类型
            if ((response.getContractType() != null && !response.getContractType().equals(salSaleContract.getContractType()))
                    || (StrUtil.isBlank(response.getContractType()) && StrUtil.isNotBlank(salSaleContract.getContractType()))) {
                List<DictData> contractTypeList = sysDictDataService.selectDictData("s_contract_type");
                Map<String, String> contractTypeMaps = contractTypeList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                String oldData = StrUtil.isBlank(response.getContractType()) ? "" : contractTypeMaps.get(response.getContractType());
                String newData = StrUtil.isBlank(salSaleContract.getContractType()) ? "" : contractTypeMaps.get(salSaleContract.getContractType());
                remark = remark + "合同类型字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                warn = true;
            }
            // 合同金额(含税)
            if ((response.getCurrencyAmountTax() != null && response.getCurrencyAmountTax().compareTo(salSaleContract.getCurrencyAmountTax()) != 0)
                    || (response.getCurrencyAmountTax() == null && salSaleContract.getCurrencyAmountTax() != null)) {
                String oldData = response.getCurrencyAmountTax() == null ? "" : response.getCurrencyAmountTax().toString();
                String newData = salSaleContract.getCurrencyAmountTax() == null ? "" : salSaleContract.getCurrencyAmountTax().toString();
                remark = remark + "合同金额(含税)字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                warn = true;
            }
            // 收款方式组合
            if ((response.getAccountsMethodGroup() != null && !response.getAccountsMethodGroup().equals(salSaleContract.getAccountsMethodGroup()))
                    || (response.getAccountsMethodGroup() == null && salSaleContract.getAccountsMethodGroup() != null)) {
                List<ConAccountMethodGroup> methodList = conAccountMethodGroupMapper.selectList(new QueryWrapper<ConAccountMethodGroup>().lambda()
                        .eq(ConAccountMethodGroup::getSid, response.getAccountsMethodGroup())
                        .or().eq(ConAccountMethodGroup::getSid, salSaleContract.getAccountsMethodGroup()));
                Map<Long, String> methodMaps = methodList.stream().collect(Collectors.toMap(ConAccountMethodGroup::getSid, ConAccountMethodGroup::getName, (key1, key2) -> key2));
                String oldData = response.getAccountsMethodGroup() == null ? "" : methodMaps.get(response.getAccountsMethodGroup());
                String newData = salSaleContract.getAccountsMethodGroup() == null ? "" : methodMaps.get(salSaleContract.getAccountsMethodGroup());
                remark = remark + "收款方式组合字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                warn = true;
            }
            // 预收款结算方式
            if ((response.getAdvanceSettleMode() != null && !response.getAdvanceSettleMode().equals(salSaleContract.getAdvanceSettleMode()))
                    || (StrUtil.isBlank(response.getAdvanceSettleMode()) && StrUtil.isNotBlank(salSaleContract.getAdvanceSettleMode()))) {
                List<ConAdvanceSettleMode> advanceList = conAdvanceSettleModeMapper.selectList(new QueryWrapper<ConAdvanceSettleMode>().lambda()
                        .eq(ConAdvanceSettleMode::getCode, response.getAdvanceSettleMode())
                        .or().eq(ConAdvanceSettleMode::getCode, salSaleContract.getAdvanceSettleMode()));
                Map<String, String> advanceMaps = advanceList.stream().collect(Collectors.toMap(ConAdvanceSettleMode::getCode, ConAdvanceSettleMode::getName, (key1, key2) -> key2));
                String oldData = StrUtil.isBlank(response.getAdvanceSettleMode()) ? "" : advanceMaps.get(response.getAdvanceSettleMode());
                String newData = StrUtil.isBlank(salSaleContract.getAdvanceSettleMode()) ? "" : advanceMaps.get(salSaleContract.getAdvanceSettleMode());
                remark = remark + "预收款结算方式字段变更，更新前：" + oldData + "，更新后：" + newData + "\n";
                warn = true;
            }
            MongodbUtil.insertUserLog(salSaleContract.getSaleContractSid(), BusinessType.CHANGE.getValue(), msgList, TITLE, remark);
            try {
                if (codeChange) {
                    String newData = salSaleContract.getSaleContractCode() == null ? "" : salSaleContract.getSaleContractCode();
                    // 销售订单
                    List<SalSalesOrder> orderList = salSalesOrderMapper.selectList(new QueryWrapper<SalSalesOrder>()
                            .lambda().eq(SalSalesOrder::getSaleContractSid, salSaleContract.getSaleContractSid()));
                    if (CollectionUtil.isNotEmpty(orderList)) {
                        orderList.forEach(order->{
                            String oldData = order.getSaleContractCode() == null ? "" : order.getSaleContractCode();
                            MongodbUtil.insertUserLog(order.getSalesOrderSid(), BusinessType.QITA.getValue(), null, "销售订单",
                                    "更新合同号字段值（变更前：" + oldData + "，变更后：" + newData + "）");
                        });
                        List<Long> orderSidList = orderList.stream().map(SalSalesOrder::getSalesOrderSid).collect(toList());
                        UpdateWrapper<SalSalesOrder> orderUpdateWrapper = new UpdateWrapper<>();
                        orderUpdateWrapper.lambda().in(SalSalesOrder::getSalesOrderSid, orderSidList);
                        orderUpdateWrapper.lambda().set(SalSalesOrder::getSaleContractCode, salSaleContract.getSaleContractCode());
                        salSalesOrderMapper.update(null, orderUpdateWrapper);
                    }
                    // 销售意向单
                    List<SalSalesIntentOrder> intentOrderList = salSalesIntentOrderMapper.selectList(new QueryWrapper<SalSalesIntentOrder>()
                            .lambda().eq(SalSalesIntentOrder::getSaleIntentContractSid, salSaleContract.getSaleContractSid()));
                    if (CollectionUtil.isNotEmpty(intentOrderList)) {
                        intentOrderList.forEach(order->{
                            String oldData = order.getSaleIntentContractCode() == null ? "" : order.getSaleIntentContractCode();
                            MongodbUtil.insertUserLog(order.getSalesIntentOrderSid(), BusinessType.QITA.getValue(), null, "销售意向单",
                                    "更新合同号字段值（变更前：" + oldData + "，变更后：" + newData + "）");
                        });
                        List<Long> orderSidList = intentOrderList.stream().map(SalSalesIntentOrder::getSalesIntentOrderSid).collect(toList());
                        UpdateWrapper<SalSalesIntentOrder> intentOrderUpdateWrapper = new UpdateWrapper<>();
                        intentOrderUpdateWrapper.lambda().in(SalSalesIntentOrder::getSalesIntentOrderSid, orderSidList);
                        intentOrderUpdateWrapper.lambda().set(SalSalesIntentOrder::getSaleIntentContractCode, salSaleContract.getSaleContractCode());
                        salSalesIntentOrderMapper.update(null, intentOrderUpdateWrapper);
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
    private void setZhangqi(SalSaleContract contract){
        if (contract.getYsAccountValidDays() != null && contract.getYsAccountValidDays() <= 0){
            throw new BaseException("预收款账期(天)仅允许输入正整数");
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
     * 批量删除销售合同信息
     *
     * @param saleContractSids 需要删除的销售合同信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalSaleContractByIds(List<Long> saleContractSids) {
        SalSaleContract params = new SalSaleContract();
        params.setSaleContractSids(saleContractSids);
        params.setHandleStatusList(new String[]{HandleStatus.SAVE.getCode(),HandleStatus.RETURNED.getCode()});
        int count = salSaleContractMapper.countByDomain(params);
        if (count != saleContractSids.size()) {
            throw new BaseException("仅保存和已退回状态才允许删除");
        }
        List<SalSalesOrder> orderList = salSalesOrderMapper.selectSalSalesOrderList(new SalSalesOrder().setSaleContractSidList(saleContractSids));
        if (CollectionUtil.isNotEmpty(orderList)) {
            List<String> codeList = orderList.stream().map(SalSalesOrder::getSaleContractCode).distinct().collect(Collectors.toList());
            String codes = "";
            for (String code : codeList) {
                codes = codes + code + ";";
            }
            if (codes.endsWith(";")) {
                codes = codes.substring(0, codes.length() - 1);
                throw new BaseException("合同" + codes + "已被销售订单引用，无法删除！");
            }
        }
        //删除销售合同信息
        salSaleContractMapper.deleteBatchIds(saleContractSids);
        // 支付方式
        salSaleContractPayMethodService.deleteSalSaleContractPayMethodByContract(saleContractSids);
        //删除销售合同信息附件
        salSaleContractAttachmentMapper.deleteSalSaleContractAttachmentByIds(saleContractSids);
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, saleContractSids));
        //插入日志
        saleContractSids.forEach(id->{
            MongodbUtil.insertUserLog(Long.valueOf(id), BusinessType.DELETE.getValue(), null, TITLE);
        });
        return saleContractSids.size();
    }

    /**
     * 更改确认状态
     *
     * @param salSaleContract
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(SalSaleContract saleContract) {
        int row = 1;
        Long[] sids = saleContract.getSaleContractSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                SalSaleContract entity = salSaleContractMapper.selectSalSaleContractById(id);
                if (ConstantsEms.CHECK_STATUS.equals(entity.getHandleStatus())) {
                    throw new BaseException(entity.getSaleContractCode() + "请不要重复确认！");
                }
                entity.setHandleStatus(saleContract.getHandleStatus());
                setConfirmInfo(entity);
                saleContract.setSaleContractSid(id);
                if (!ConstantsEms.SUBMIT_STATUS.equals(saleContract.getHandleStatus())){
                    row = salSaleContractMapper.updateById(saleContract);
                }
                if (!ConstantsEms.CONTRACT_TYPE_KJ.equals(saleContract.getContractType()) && ConstantsEms.CHECK_STATUS.equals(entity.getHandleStatus())){
                    advanceReceipt(entity);
                }
            }
        }
        if (ConstantsEms.CHECK_STATUS.equals(saleContract.getHandleStatus())){
            //确认操作后删除待办
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, sids));
            //插入日志
            for (Long sid : sids) {
                MongodbUtil.insertUserLog(sid, BusinessType.CHECK.getValue(),null,TITLE);
            }
        }
        return row;
    }

    /**
     * 销售合同下拉框列表
     */
    @Override
    public List<SalSaleContract> getSalSaleContractList() {
        return salSaleContractMapper.getSalSaleContractList();
    }

    /**
     * 销售合同下拉框列表
     */
    @Override
    public List<SalSaleContract> getSaleContractList(SalSaleContract salSaleContract) {
        return salSaleContractMapper.getSaleContractList(salSaleContract);
    }

    /**
     * 原合同号下拉框接口
     */
    @Override
    public List<SalSaleContract> getOriginalContractList(SalSaleContract salSaleContractr) {
        return salSaleContractMapper.getOriginalContractList(salSaleContractr);
    }

    /**
     * 作废销售合同信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancellationSalSaleContractById(SalSaleContract request) {
        Long saleContractSid = request.getSaleContractSid();
        SalSaleContract salSaleContract = salSaleContractMapper.selectSalSaleContractById(saleContractSid);
        if (!ConstantsEms.CHECK_STATUS.equals(salSaleContract.getHandleStatus())) {
            throw new BaseException("所选数据非'已确认'状态，无法作废！");
        }
        List<SalSalesOrder> salesOrderList = salSalesOrderMapper.selectList(new QueryWrapper<SalSalesOrder>().lambda()
                .eq(SalSalesOrder::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .eq(SalSalesOrder::getSaleContractSid, saleContractSid));
        if (CollectionUtil.isNotEmpty(salesOrderList)){
            throw new BaseException("存在'已确认'状态的销售订单已引用该合同，无法作废！");
        }
        List<FinRecordAdvanceReceipt> recordAdvanceReceiptList =
                finRecordAdvanceReceiptMapper.selectList(new QueryWrapper<FinRecordAdvanceReceipt>().lambda()
                        .eq(FinRecordAdvanceReceipt::getSaleContractSid, saleContractSid));
        if (CollectionUtil.isNotEmpty(recordAdvanceReceiptList)){
            FinRecordAdvanceReceipt advanceReceipt = new FinRecordAdvanceReceipt();
            recordAdvanceReceiptList.forEach(finRecordAdvanceReceipt ->{
                List<FinRecordAdvanceReceiptItem> recordAdvanceReceiptItemList =
                        finRecordAdvanceReceiptItemMapper.selectList(new QueryWrapper<FinRecordAdvanceReceiptItem>().lambda()
                                .eq(FinRecordAdvanceReceiptItem::getRecordAdvanceReceiptSid, finRecordAdvanceReceipt.getRecordAdvanceReceiptSid()));
                if (CollectionUtil.isNotEmpty(recordAdvanceReceiptItemList)){
                    recordAdvanceReceiptItemList.forEach(recordAdvanceReceiptItem ->{
                        if (!ConstantsEms.CLEAR_STATUS_WHX.equals(recordAdvanceReceiptItem.getClearStatus())){
                            throw new BaseException("该合同对应的客户待收预收款流水非'未核销'状态，无法作废！");
                        }
                    });
                }
                advanceReceipt.setHandleStatus(HandleStatus.INVALID.getCode());
                advanceReceipt.setRecordAdvanceReceiptSid(finRecordAdvanceReceipt.getRecordAdvanceReceiptSid());
                finRecordAdvanceReceiptMapper.updateById(advanceReceipt);
            });
        }
        salSaleContract.setHandleStatus(HandleStatus.INVALID.getCode());
        salSaleContract.setCancelRemark(request.getCancelRemark());
        String code = salSaleContract.getSaleContractCode();
        String name = salSaleContract.getContractName();
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
        salSaleContract.setSaleContractCode(code);
        salSaleContract.setContractName(name);
        int row = salSaleContractMapper.updateById(salSaleContract);
        MongodbUtil.insertUserLog(saleContractSid,BusinessType.CANCEL.getValue(), null, TITLE, request.getCancelRemark());
        return row;
    }

    /**
     * 结案销售合同信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int closingSalSaleContractById(Long saleContractSid) {
        SalSaleContract salSaleContract = salSaleContractMapper.selectSalSaleContractById(saleContractSid);
        if (!ConstantsEms.CHECK_STATUS.equals(salSaleContract.getHandleStatus())) {
            throw new BaseException("所选数据非'已确认'状态，无法结案！");
        }
        salSalesOrderMapper.update(null, new UpdateWrapper<SalSalesOrder>().lambda()
                .set(SalSalesOrder::getHandleStatus, HandleStatus.CLOSED.getCode())
                .in(SalSalesOrder::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .in(SalSalesOrder::getSaleContractSid, saleContractSid));
        salSaleContract.setHandleStatus(HandleStatus.CONCLUDE.getCode());
        return salSaleContractMapper.updateById(salSaleContract);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setToexpireDays(SalSaleContract salSaleContract) {
        if (salSaleContract.getSaleContractSidList().length == 0){
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<SalSaleContract> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //即将到期天数
        updateWrapper.in(SalSaleContract::getSaleContractSid, salSaleContract.getSaleContractSidList())
                .set(SalSaleContract::getToexpireDays, salSaleContract.getToexpireDays());
        row = salSaleContractMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 纸质合同签收
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int signSalSaleContractById(SalSaleContract salSaleContractr) {
        LambdaUpdateWrapper<SalSaleContract> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(SalSaleContract::getSaleContractSid,salSaleContractr.getSaleContractSids()).set(SalSaleContract::getSignInStatus, salSaleContractr.getSignInStatus());
        int row = salSaleContractMapper.update(null, updateWrapper);
        return row;
    }

    @Override
    public List<SalSaleContractFormResponse> getCountForm(SalSaleContract salSaleContract) {
        List<SalSaleContractFormResponse> list = salSaleContractMapper.getCountForm(salSaleContract);
        return list;
    }

    @Override
    public List<SalSaleContractFormResponse> getCountFormItem(SalSaleContract salSaleContract) {
        List<SalSaleContractFormResponse> list = salSaleContractMapper.getCountFormItem(salSaleContract);
        return list;
    }

    /**
     * 合同审批通过后发起e签宝签署
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvalContractToEsign(SalSaleContract salSaleContract) {
        // 已确认状态
        SalSaleContract contract = salSaleContractMapper.selectOne(new QueryWrapper<SalSaleContract>().lambda()
                .eq(SalSaleContract::getSaleContractSid, salSaleContract.getSaleContractSid())
                .eq(SalSaleContract::getHandleStatus, ConstantsEms.CHECK_STATUS));
        if (contract == null) {
            return;
        }
        // 校验租户设置
        SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new UpdateWrapper<SysDefaultSettingClient>()
                .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
        if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsEnableEsign())) {
            return;
        }
        // 如果已经有了销售合同/协议(电子版)
        List<SalSaleContractAttachment> attachments = salSaleContractAttachmentMapper.selectList(new QueryWrapper<SalSaleContractAttachment>()
                .lambda().eq(SalSaleContractAttachment::getSaleContractSid, salSaleContract.getSaleContractSid())
                .eq(SalSaleContractAttachment::getFileType, ConstantsEms.FILE_TYPE_XSHT));
        if (CollectionUtil.isNotEmpty(attachments)) {
            return;
        }

        // 合同协议的附件
        SalSaleContractAttachment attachment = salSaleContractAttachmentMapper.selectOne(new QueryWrapper<SalSaleContractAttachment>()
                .lambda().eq(SalSaleContractAttachment::getSaleContractSid, salSaleContract.getSaleContractSid())
                .eq(SalSaleContractAttachment::getFileType, ConstantsEms.FILE_TYPE_XSHT_DQS));
        if (attachment == null || StrUtil.isNotBlank(attachment.getEsignFileId())) {
            return;
        }
        // 调用e签宝
        try {
            // 调用上传文件
            EsignFile esignFile = esignFileService.getFileUploadUrl(attachment.getFilePath(), attachment.getFileName());
            if (esignFile != null && esignFile.getFileId() != null) {
                esignFile = esignFileService.getFileStatus(esignFile.getFileId());
                salSaleContractAttachmentMapper.update(null, new UpdateWrapper<SalSaleContractAttachment>().lambda()
                        .eq(SalSaleContractAttachment::getSaleContractAttachmentSid, attachment.getSaleContractAttachmentSid())
                        .set(SalSaleContractAttachment::getEsignFileId, esignFile.getFileId())
                        .set(SalSaleContractAttachment::getEsignFileStatus, esignFile.getFileStatus()));
                // 如果文件上传到e签宝成功
                if ("2".equals(esignFile.getFileStatus())) {
                    salSaleContractMapper.update(null, new UpdateWrapper<SalSaleContract>().lambda()
                            .eq(SalSaleContract::getSaleContractSid, contract.getSaleContractSid())
                            .set(SalSaleContract::getEsignFlowStatus, "1"));
                }
            }
            // 通过已上传文件 自动发起 e签宝签署
            SignerPsnInfo signerPsnInfo1 = new SignerPsnInfo();
            signerPsnInfo1.setPsnAccount("13695963755").setPsnInfo(new SignerPsnInfo.PsnInfo().setPsnName("陈凯文"));

            SignerPsnInfo signerPsnInfo2 = new SignerPsnInfo();
            signerPsnInfo2.setPsnAccount("15750820323").setPsnInfo(new SignerPsnInfo.PsnInfo().setPsnName("吴慧欣"));

            SignerPsnInfo signerPsnInfo3 = new SignerPsnInfo();
            signerPsnInfo3.setPsnAccount("18750616916").setPsnInfo(new SignerPsnInfo.PsnInfo().setPsnName("庄逸煊"));

            List<SignerPsnInfo> signerPsnInfoList = new ArrayList<>();
            signerPsnInfoList.add(signerPsnInfo1);
            signerPsnInfoList.add(signerPsnInfo2);
            signerPsnInfoList.add(signerPsnInfo3);

            String signFlowId = esignFlowService.execSignFlowPsn(esignFile, new SignFlowConfig().setSignFlowTitle("销售协议文件电子版签署"), signerPsnInfoList);
            if (StrUtil.isNotBlank(signFlowId)) {
                // 绑定e签宝签署id
                salSaleContractMapper.update(null, new UpdateWrapper<SalSaleContract>().lambda()
                        .eq(SalSaleContract::getSaleContractSid, contract.getSaleContractSid())
                        .set(SalSaleContract::getEsignFlowId, signFlowId));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * e签宝签署流程完成后回调方法
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addContractAttachEsign(SignFlowCallback signFlowCallback) {
        int row = 0;
        // 签署完成流程结束回调
        Date date = null;
        if (signFlowCallback.getSignFlowFinishTime() != 0) {
            date = new Date(signFlowCallback.getSignFlowFinishTime());
        }

        SalSaleContract contract = salSaleContractMapper.selectOne(new QueryWrapper<SalSaleContract>().lambda()
                .eq(SalSaleContract::getEsignFlowId, signFlowCallback.getSignFlowId()));
        if (contract == null) {
            log.warn("SCM未找到对应合同协议信息");
            return row;
        }

        SalSaleContractAttachment attachment = salSaleContractAttachmentMapper.selectOne(new QueryWrapper<SalSaleContractAttachment>()
                .lambda().eq(SalSaleContractAttachment::getSaleContractSid, contract.getSaleContractSid()));

        row += salSaleContractMapper.update(null, new UpdateWrapper<SalSaleContract>().lambda()
                .eq(SalSaleContract::getEsignFlowId, signFlowCallback.getSignFlowId())
                .set(SalSaleContract::getEsignFlowStatus, signFlowCallback.getSignFlowStatus())
                .set(SalSaleContract::getEsignFlowDesc, signFlowCallback.getStatusDescription())
                .set(SalSaleContract::getEsignFlowTime, date));
        // 调用下载接口将签署后的文件上传到minio服务器上并挂在对应表的附件下
        ESignDownloadResponse fileDownloadUrl = null;
        try {
            fileDownloadUrl = jssdkService.selectDownloadUrl(signFlowCallback.getSignFlowId());
            if (fileDownloadUrl != null && fileDownloadUrl.getData() != null) {
                System.out.println("==== > fileDownloadUrl < ==== : " + fileDownloadUrl);
                if (CollectionUtil.isNotEmpty(fileDownloadUrl.getData().getFiles())) {
                    // 得到文件下载链接
                    ESignDownloadFileResponse file = fileDownloadUrl.getData().getFiles().get(0);
                    String downloadPath = file.getDownloadUrl();

                    byte[] fileBytes = SignFileUtil.getFileBytesFromUrl(downloadPath);

                    // Convert the byte array to an InputStream
                    InputStream inputStream = new ByteArrayInputStream(fileBytes);

                    // 返回去掉最后一个`.`及其后缀的文件名
                    String fileName = file.getFileName().substring(0, file.getFileName().lastIndexOf("."));
                    File tempFile = CommonUtil.asFileTemp(inputStream, fileName, ".pdf");

                    // try catch
                    byte[] bytes = FileUtils.readFileToByteArray(tempFile);
                    DiskFileItem fileItem = new DiskFileItem(tempFile.getName(), Files.probeContentType(tempFile.toPath()),
                            false, tempFile.getName(), (int) tempFile.length(), tempFile.getParentFile());
                    fileItem.getOutputStream().write(bytes);
                    MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

                    // 关闭 inputStream
                    inputStream.close();

                    SysFile sysFile = remoteFileService.upload(multipartFile).getData();

                    tempFile.deleteOnExit();
                    if (sysFile != null) {
                        System.out.println("==== > sysFile < ==== : " + sysFile);
                        // 写入对应合同的附件中并改名
                        if (attachment != null) {
                            attachment.setSaleContractAttachmentSid(null)
                                    .setFilePath(sysFile.getUrl()).setCreateDate(new Date())
                                    .setCreatorAccount("xtgly")
                                    .setUpdateDate(null).setUpdaterAccount(null);
                            int lastDotIndex = attachment.getFileName().lastIndexOf(".");
                            String name = attachment.getFileName().substring(0, lastDotIndex) + "(已签署)" + attachment.getFileName().substring(lastDotIndex);
                            attachment.setFileName(name).setFileType(ConstantsEms.FILE_TYPE_XSHT);
                        }
                        else {
                            attachment = new SalSaleContractAttachment();
                            attachment.setSaleContractSid(contract.getSaleContractSid());
                            attachment.setFilePath(sysFile.getUrl()).setCreateDate(new Date())
                                    .setCreatorAccount("xtgly")
                                    .setClientId(contract.getClientId()).setUpdateDate(null).setUpdaterAccount(null);
                            String name = sysFile.getName().replace(".pdf", "(已签署).pdf");
                            attachment.setFileName(name).setFileType(ConstantsEms.FILE_TYPE_XSHT);
                        }
                        row +=  salSaleContractAttachmentMapper.insert(attachment);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }

    /**
     * 根据模板和合同数据自动生成电子合同
     *
     * @param saleContractSid 销售合同信息ID
     * @return
     */
    @Override
    public MultipartFile autoGenContract(String filePAth, Long saleContractSid){
        SalSaleContractAttachment attachment = null;
        try {
            attachment = salSaleContractAttachmentMapper.selectOne(new QueryWrapper<SalSaleContractAttachment>()
                    .lambda()
                    .eq(SalSaleContractAttachment::getSaleContractSid,saleContractSid).eq(SalSaleContractAttachment::getFileType,ConstantsEms.FILE_TYPE_XSHT));
        }catch (Exception e){
            throw new BaseException("该合同附件中存在重复电子合同，请确认保留一份正确的电子合同后操作！");
        }
        String contractFileName = null;
        if (attachment != null){
            contractFileName = attachment.getFileName();
        }else {
            contractFileName = "销售合同电子版";
        }
        SalSaleContract salSaleContract = this.selectSalSaleContractById(saleContractSid);
        try {
            MultipartFile multipartFile = CommonUtil.createPDF(filePAth,salSaleContract,null,true,null,contractFileName);
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
            List<DictData> saleModeDict = sysDictDataService.selectDictData("s_price_type"); //销售模式
            saleModeDict = saleModeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> saleModeMaps = saleModeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> rawMaterialModeDict = sysDictDataService.selectDictData("s_raw_material_mode"); //供料方式
            rawMaterialModeDict = rawMaterialModeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> rawMaterialModeMaps = rawMaterialModeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> contractTagDict = sysDictDataService.selectDictData("s_contract_tag"); //供料方式
            contractTagDict = contractTagDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> contractTagDictMaps = contractTagDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //错误信息
            CommonErrMsgResponse errMsg = null;
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            //
            HashMap<String, String> codeMap = new HashMap<>();
            HashMap<String, String> nameMap = new HashMap<>();
            //
            SalSaleContract salSaleContract = null;
            List<SalSaleContract> salSaleContractList = new ArrayList<>();
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
                 * 销售合同号 必填
                 */
                String saleContractCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (StrUtil.isBlank(saleContractCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("销售合同号不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    if (saleContractCode.length() > 20){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("销售合同号长度不能超过20个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        saleContractCode = saleContractCode.replaceAll(" ","");
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
                 * 客户简称 必填
                 */
                String customerShortName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                Long customerSid = null;
                String customerCode = null;
                String customerName = null;
                if (StrUtil.isBlank(customerShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("客户简称不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    try {
                        BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>().lambda().eq(BasCustomer::getShortName, customerShortName));
                        if (basCustomer == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("简称为"+ customerShortName +"没有对应的客户，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(basCustomer.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的客户必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            customerSid = basCustomer.getCustomerSid();
                            customerName = basCustomer.getCustomerName();
                            customerCode = basCustomer. getCustomerCode();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(customerShortName + "客户档案存在重复，请先检查该客户，导入失败！");
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
                }
                else {
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
                String companyCode = null;
                Long companySid = null;
                String companyName = null;
                if (StrUtil.isBlank(companyShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("公司简称不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
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
                if (StrUtil.isNotBlank(saleContractCode) && companySid != null) {
                    if (codeMap.get(saleContractCode+companySid.toString()) == null){
                        codeMap.put(saleContractCode+companySid.toString(),String.valueOf(num));
                        QueryWrapper<SalSaleContract> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("sale_contract_code", saleContractCode)
                                .eq("company_sid", companySid);
                        SalSaleContract checkCodeResust = salSaleContractMapper.selectOne(queryWrapper);
                        if (checkCodeResust != null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("系统中，该公司下已存在相同销售合同号，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("表格中，该公司下已存在相同销售合同号，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                //校验合同名称
                if (StrUtil.isNotBlank(contractName) && companySid != null) {
                    if (nameMap.get(contractName+companySid.toString()) == null) {
                        nameMap.put(contractName + companySid.toString(), String.valueOf(num));
                        QueryWrapper<SalSaleContract> queryWrapper2 = new QueryWrapper<>();
                        queryWrapper2.eq("contract_name", contractName)
                                .eq("company_sid", companySid);
                        SalSaleContract checkNameResust = salSaleContractMapper.selectOne(queryWrapper2);
                        if (checkNameResust != null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("系统中，该公司下已存在相同销售合同名称，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("表格中，该公司下已存在相同销售合同名称，导入失败！");
                        errMsgList.add(errMsg);
                    }

                }
                /**
                 * 年份 必填
                 */
                String year = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                if (StrUtil.isBlank(year)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("年份不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    year = yearMaps.get(year); //通过数据字典标签获取数据字典的值
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
                if (ConstantsEms.CONTRACT_TYPE_BZ.equals(contractType)) {
                    if (currencyAmountTax == null || currencyAmountTax.compareTo(BigDecimal.ZERO) <= 0) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("合同类型为标准合同，合同金额为必填且必须大于 0 ，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 收款方式组合编码 ，预收款结算方式 ， 尾款结算方式 在合同类型为标准合同框架式的时候不能填
                 */
                String accountsMethodGroupCode = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                String advanceSettleModeName = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                String remainSettleModeName = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
                if (ConstantsEms.CONTRACT_TYPE_BZHTKJS.equals(contractType)){
                    if (StrUtil.isNotBlank(accountsMethodGroupCode) || StrUtil.isNotBlank(advanceSettleModeName) || StrUtil.isNotBlank(remainSettleModeName)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("合同类型为标准合同(框架式)时，收款方式组合/预收款结算方式/尾款结算方式必须为空，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 收款方式组合编码 必填
                 */
                ConAccountMethodGroup conAccountMethodGroup = null;
                Long accountsMethodGroup = null;
                String weikuanRemark = null, zhongqikuanRemark = null, yushoukuanRemark = null;
                /**
                 * 预收款结算方式 必填
                 */
                String advanceSettleMode = null;
                /**
                 * 尾款结算方式 必填
                 */
                String remainSettleMode = null;
                if (StrUtil.isNotBlank(accountsMethodGroupCode)) {
                    try {
                        conAccountMethodGroup = conAccountMethodGroupMapper.selectOne(new QueryWrapper<ConAccountMethodGroup>()
                                .lambda().eq(ConAccountMethodGroup::getCode,accountsMethodGroupCode));
                        if (conAccountMethodGroup == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("没有找到 " + accountsMethodGroupCode + " 收款方式组合，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (!ConstantsEms.SHOUFUKUAN_TYPE_SK.equals(conAccountMethodGroup.getShoufukuanType())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("收款方式组合的收付款类型应为收款，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            if (ConstantsEms.DISENABLE_STATUS.equals(conAccountMethodGroup.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(conAccountMethodGroup.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的收款方式组合必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            yushoukuanRemark = conAccountMethodGroup.getYushoufukuanRemark();
                            zhongqikuanRemark = conAccountMethodGroup.getZhongqikuanRemark();
                            weikuanRemark = conAccountMethodGroup.getWeikuanRemark();
                            accountsMethodGroup = conAccountMethodGroup.getSid();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(accountsMethodGroupCode + "收款方式组合存在重复，请先检查该收款方式组合，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 预收款结算方式 必填
                 */
                if (StrUtil.isNotBlank(advanceSettleModeName)) {
                    try {
                        ConAdvanceSettleMode conAdvanceSettleMode = conAdvanceSettleModeMapper.selectOne(new QueryWrapper<ConAdvanceSettleMode>()
                                .lambda().eq(ConAdvanceSettleMode::getName,advanceSettleModeName));
                        if (conAdvanceSettleMode == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("预收款结算方式配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(conAdvanceSettleMode.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(conAdvanceSettleMode.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的预收款结算方式必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            advanceSettleMode = conAdvanceSettleMode.getCode();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(advanceSettleModeName + "预收款结算方式存在重复，请先检查该预收款结算方式，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                //预收款结算方式
                if (contractType != null &&!ConstantsEms.CONTRACT_TYPE_KJ.equals(contractType) && conAccountMethodGroup != null){
                    if (ConstantsEms.ADVANCE_SETTLE_MODE_HT.equals(advanceSettleMode) && Double.parseDouble(conAccountMethodGroup.getAdvanceRate()) > 0) {
                        if (currencyAmountTax == null || currencyAmountTax.compareTo(BigDecimal.ZERO) <= 0) {
                            throw new BaseException("第"+ num +"行，按协议的预收款结算方式且预收款比例大于0的协议金额为必填且必须大于 0 ，导入失败");
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
                            errMsg.setMsg(remainSettleModeName + "尾款结算方式存在重复，请先检查该尾款结算方式，导入失败！");
                            errMsgList.add(errMsg);
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
                }else {
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
                }else {
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
                }
                else {
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
                 * 销售渠道 必填
                 */
                String businessChannelName = objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString();
                String businessChannel = null;
                if (StrUtil.isBlank(businessChannelName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("销售渠道不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        ConBusinessChannel conBusinessChannel = conBusinessChannelMapper.selectOne(new QueryWrapper<ConBusinessChannel>().lambda().eq(ConBusinessChannel::getName,businessChannelName));
                        if (conBusinessChannel == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("销售渠道配置错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(conBusinessChannel.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(conBusinessChannel.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的销售渠道必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            businessChannel = conBusinessChannel.getCode();
                        }

                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(businessChannelName + "销售渠道存在重复，请先检查该销售渠道，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 客方合同号 选填
                 */
                String customerContractCode = objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString();
                if (StrUtil.isNotBlank(customerContractCode)){
                    if (customerContractCode.length() > 20){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("客方合同号长度不能超过20个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 产品季 选填
                 */
                String productSeasonName = objects.get(15) == null || objects.get(15) == "" ? null : objects.get(15).toString();
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
                 * 销售员账号 选填
                 */
                String salePerson = objects.get(16) == null || objects.get(16) == "" ? null : objects.get(16).toString();
                if (StrUtil.isNotBlank(salePerson)){
                    try {
                        SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>()
                                .lambda().eq(SysUser::getUserName,salePerson));
                        if (sysUser == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("账号为"+ salePerson +"没有对应的销售员，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.SYS_COMMON_STATUS_Y.equals(sysUser.getStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的销售员必须是已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(salePerson + "销售员账号存在重复，请先检查该采购员，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 物料类型 选填
                 */
                String materialTypeName = objects.get(17) == null || objects.get(17) == "" ? null : objects.get(17).toString();
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
                String exemptionDay_s = objects.get(18) == null || objects.get(18) == "" ? null : objects.get(18).toString();
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
                String warranty_s = objects.get(19) == null || objects.get(19) == "" ? null : objects.get(19).toString();
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
                String allowableRatioShort_s = objects.get(20) == null || objects.get(20) == "" ? null : objects.get(20).toString();
                BigDecimal allowableRatioShort = null;
                if (StrUtil.isNotBlank(allowableRatioShort_s)){
                    try {
                        allowableRatioShort = new BigDecimal(allowableRatioShort_s);
                        if (new BigDecimal(100).compareTo(allowableRatioShort) < 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("短发允许比例(%)不能超过100%，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (allowableRatioShort.compareTo(BigDecimal.ZERO) < 0){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("短发允许比例不能小于0，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                allowableRatioShort = allowableRatioShort.divide(new BigDecimal(100));
                                allowableRatioShort = allowableRatioShort.setScale(4, BigDecimal.ROUND_HALF_DOWN);
                                if (!JudgeFormat.isValidDouble(String.valueOf(allowableRatioShort),1,4)){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("短发允许比例格式错误，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            }
                        }
                    } catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("短发允许比例(%)的数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 超发允许比例(%) 选填
                 */
                String allowableRatioMore_s = objects.get(21) == null || objects.get(21) == "" ? null : objects.get(21).toString();
                BigDecimal allowableRatioMore = null;
                if (StrUtil.isNotBlank(allowableRatioMore_s)){
                    try {
                        allowableRatioMore = new BigDecimal(allowableRatioMore_s);
                        if (new BigDecimal(100).compareTo(allowableRatioMore) < 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("超发允许比例(%)不能超过100%，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (allowableRatioMore.compareTo(BigDecimal.ZERO) < 0){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("超发允许比例不能小于0，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                allowableRatioMore = allowableRatioMore.divide(new BigDecimal(100));
                                allowableRatioMore = allowableRatioMore.setScale(4, BigDecimal.ROUND_HALF_DOWN);
                                if (!JudgeFormat.isValidDouble(String.valueOf(allowableRatioMore),1,4)){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("超发允许比例格式错误，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            }
                        }
                    } catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("超发允许比例(%)的数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 销售模式(数据字典) 非必填
                 */
                String saleMode = objects.get(22) == null || objects.get(22) == "" ? null : objects.get(22).toString();
                if (contractType != null && !ConstantsEms.CONTRACT_TYPE_KJ.equals(contractType) && StrUtil.isBlank(saleMode)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("合同类型为标准合同/标准合同(框架式)时，销售模式不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (StrUtil.isNotBlank(saleMode)) {
                    saleMode = saleModeMaps.get(saleMode); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(saleMode)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("销售模式配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 框架协议号 非必填
                 */
                String outlineAgreementCode = objects.get(23) == null || objects.get(23) == "" ? null : objects.get(23).toString();
                Long outlineAgreementSid = null;
                if (contractType != null && ConstantsEms.CONTRACT_TYPE_BZHTKJS.equals(contractType) && StrUtil.isBlank(outlineAgreementCode)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("标准合同(框架式)的框架协议号为必填，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (contractType != null && (ConstantsEms.CONTRACT_TYPE_KJ.equals(contractType) && StrUtil.isNotBlank(outlineAgreementCode)) ||
                        (ConstantsEms.CONTRACT_TYPE_BZ.equals(contractType) && StrUtil.isNotBlank(outlineAgreementCode))){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("合同类型为框架协议/标准合同时，框架协议号必须为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (StrUtil.isNotBlank(outlineAgreementCode)){
                    try {
                        SalSaleContract contract  = salSaleContractMapper.selectOne(new QueryWrapper<SalSaleContract>()
                                .lambda()
                                .eq(SalSaleContract::getCustomerSid,customerSid)
                                .eq(SalSaleContract::getCompanySid,companySid)
                                .eq(SalSaleContract::getSaleContractCode,outlineAgreementCode));
                        if (contract == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("该公司和客户组合下，不存在 "+outlineAgreementCode+" 的框架协议号，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
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
                                            errMsg.setMsg("框架协议号中收款方式组合的预收款比例大于0且预收款结算方式按合同，合同金额为必填，导入失败！");
                                            errMsgList.add(errMsg);
                                        }else {
                                            if (currencyAmountTax.compareTo(BigDecimal.ZERO) <= 0 ){
                                                errMsg = new CommonErrMsgResponse();
                                                errMsg.setItemNum(num);
                                                errMsg.setMsg("框架协议号中收款方式组合的预收款比例大于0且预收款结算方式按合同，合同金额不能小于等于0，导入失败！");
                                                errMsgList.add(errMsg);
                                            }
                                        }
                                    }
                                }
                            }
                            outlineAgreementSid = contract.getSaleContractSid();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(outlineAgreementCode + "框架协议存在重复，请先检查该框架协议，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 合同签约人 必填
                 */
                String contractSigner = objects.get(24) == null || objects.get(24) == "" ? null : objects.get(24).toString();
                String contractSignerName = null;
                if (StrUtil.isBlank(contractSigner)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("合同签约人不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
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
                }
                /**
                 * 合同签约日期 必填
                 */
                String contractSignDate_s = objects.get(25) == null || objects.get(25) == "" ? null : objects.get(25).toString();
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
                String rawMaterialMode = objects.get(26) == null || objects.get(26) == "" ? null : objects.get(26).toString();
                if (StrUtil.isNotBlank(rawMaterialMode)){
                    rawMaterialMode = rawMaterialModeMaps.get(rawMaterialMode); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(rawMaterialMode)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("客供料方式配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 合同标识 选填
                 */
                String contractTag = objects.get(27) == null || objects.get(27) == "" ? null : objects.get(27).toString();
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
                String performRemark = objects.get(28) == null || objects.get(28) == "" ? null : objects.get(28).toString();
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
                    salSaleContract = new SalSaleContract();
                    salSaleContract.setSaleContractCode(saleContractCode).setContractName(contractName).setSaleMode(saleMode).setAccountsMethodGroupCode(accountsMethodGroupCode)
                            .setCustomerSid(customerSid).setContractType(contractType).setCompanySid(companySid).setRawMaterialMode(rawMaterialMode)
                            .setStartDate(startDate).setEndDate(endDate).setProductSeasonSid(productSeasonSid).setDayType(ConstantsFinance.DAY_TYPE_ZRR)
                            .setCustomerContractCode(customerContractCode).setBusinessChannel(businessChannel).setTaxRate(taxRate)
                            .setYear(Integer.parseInt(year)).setCurrencyAmountTax(currencyAmountTax).setAccountsMethodGroup(accountsMethodGroup)
                            .setAdvanceSettleMode(advanceSettleMode).setRemainSettleMode(remainSettleMode).setSalePerson(salePerson)
                            .setMaterialType(materialType).setExemptionDay(exemptionDay).setWarranty(warranty).setWarrantyUnit(warrantyUnit)
                            .setAllowableRatioShort(allowableRatioShort).setAllowableRatioMore(allowableRatioMore).setRemark(remark);
                    salSaleContract.setContractSigner(contractSigner).setContractSignerName(contractSignerName).setContractSignDate(contractSignDate);
                    salSaleContract.setOutlineAgreementSid(outlineAgreementSid).setOutlineAgreementCode(outlineAgreementCode)
                            .setYushoukuanRemark(yushoukuanRemark).setZhongqikuanRemark(zhongqikuanRemark).setWeikuanRemark(weikuanRemark);
                    salSaleContract.setHandleStatus(ConstantsEms.SAVA_STATUS).setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date());
                    salSaleContract.setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS).setSignInStatus(ConstantsEms.CONTRACT_SIGNIN_STATUS)
                            .setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN)
                            .setContractTag(contractTag)
                            .setPerformRemark(performRemark);
                    salSaleContractList.add(salSaleContract);
                }
            }
            if (CollectionUtil.isNotEmpty(errMsgList)){
                return errMsgList;
            }
            if (CollectionUtil.isNotEmpty(salSaleContractList)){
                salSaleContractList.forEach(item->{
                    //插入数据
                    salSaleContractMapper.insert(item);
                    //待办通知
                    SysTodoTask sysTodoTask = new SysTodoTask();
                    sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName("s_sal_sale_contract")
                            .setDocumentSid(item.getSaleContractSid());
                    sysTodoTask.setDocumentCode(String.valueOf(item.getSaleContractCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTask.setTitle("销售合同 " + item.getSaleContractCode() + " 当前是保存状态，请及时处理！");
                    sysTodoTaskMapper.insert(sysTodoTask);
                    MongodbUtil.insertUserLog(item.getSaleContractSid(), BusinessType.IMPORT.getValue(),null,TITLE);
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
