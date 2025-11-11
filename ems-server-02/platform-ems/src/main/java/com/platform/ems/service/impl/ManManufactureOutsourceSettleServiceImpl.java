package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.constant.ConstantsTask;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IManManufactureOutsourceSettleService;
import com.platform.ems.service.IManOutsourceSettleExtraDeductionItemService;
import com.platform.ems.service.ISysTodoTaskService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.CommonUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.service.ISysUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 外发加工费结算单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-10
 */
@Service
@SuppressWarnings("all")
public class ManManufactureOutsourceSettleServiceImpl extends ServiceImpl<ManManufactureOutsourceSettleMapper, ManManufactureOutsourceSettle> implements IManManufactureOutsourceSettleService {
    @Autowired
    private ManManufactureOutsourceSettleMapper manManufactureOutsourceSettleMapper;
    @Autowired
    private ManManufactureOutsourceSettleItemMapper manManufactureOutsourceSettleItemMapper;
    @Autowired
    private IManOutsourceSettleExtraDeductionItemService extraDeductionItemService;
    @Autowired
    private ManManufactureOutsourceSettleAttachMapper manManufactureOutsourceSettleAttachMapper;
    @Autowired
    private FinBookPaymentEstimationMapper finBookPaymentEstimationMapper;
    @Autowired
    private FinBookPaymentEstimationItemMapper finBookPaymentEstimationItemMapper;
    @Autowired
    private FinBookPaymentEstimationServiceImpl finBookPaymentEstimationServiceimpl;
    @Autowired
    private PurOutsourcePurchasePriceItemMapper outsourcePurchasePriceItemMapper;
    @Autowired
    private BasVendorMapper vendorMapper;
    @Autowired
    private BasPlantMapper plantMapper;
    @Autowired
    private BasCompanyMapper companyMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysTodoTaskService sysTodoTaskService;
    @Autowired
    private IWorkFlowService workFlowService;

    private static final String TITLE = "外发加工费结算单";

    /**
     * 查询外发加工费结算单
     *
     * @param manufactureOutsourceSettleSid 外发加工费结算单ID
     * @return 外发加工费结算单
     */
    @Override
    public ManManufactureOutsourceSettle selectManManufactureOutsourceSettleById(Long manufactureOutsourceSettleSid) {
        ManManufactureOutsourceSettle manManufactureOutsourceSettle = manManufactureOutsourceSettleMapper.selectManManufactureOutsourceSettleById(manufactureOutsourceSettleSid);
        if (manManufactureOutsourceSettle == null) {
            return null;
        }
        //外发加工费结算单-明细
        ManManufactureOutsourceSettleItem manManufactureOutsourceSettleItem = new ManManufactureOutsourceSettleItem();
        manManufactureOutsourceSettleItem.setManufactureOutsourceSettleSid(manufactureOutsourceSettleSid);
        List<ManManufactureOutsourceSettleItem> manManufactureOutsourceSettleItemList =
                manManufactureOutsourceSettleItemMapper.selectManManufactureOutsourceSettleItemList(manManufactureOutsourceSettleItem);
        //外发加工费结算单-附件
        ManManufactureOutsourceSettleAttach manManufactureOutsourceSettleAttach = new ManManufactureOutsourceSettleAttach();
        manManufactureOutsourceSettleAttach.setManufactureOutsourceSettleSid(manufactureOutsourceSettleSid);
        List<ManManufactureOutsourceSettleAttach> manManufactureOutsourceSettleAttachList =
                manManufactureOutsourceSettleAttachMapper.selectManManufactureOutsourceSettleAttachList(manManufactureOutsourceSettleAttach);
        // 额外扣款
        List<ManOutsourceSettleExtraDeductionItem> extraDeductionItemList = extraDeductionItemService.selectManOutsourceSettleExtraDeductionItemList(manufactureOutsourceSettleSid);
        manManufactureOutsourceSettle.setManManufactureOutsourceSettleItemList(manManufactureOutsourceSettleItemList);
        manManufactureOutsourceSettle.setAttachmentList(manManufactureOutsourceSettleAttachList);
        manManufactureOutsourceSettle.setExtraDeductionItemList(extraDeductionItemList);
        MongodbUtil.find(manManufactureOutsourceSettle);
        // 获取最新加工采购价价格
        try {
            itemGetPrice(manManufactureOutsourceSettle, false);
        } catch (Exception e) {
            log.error("加工费结算单明细获取最新加工采购价报错");
        }
        return manManufactureOutsourceSettle;
    }

    /**
     * 查询外发加工费结算单列表
     *
     * @param manManufactureOutsourceSettle 外发加工费结算单
     * @return 外发加工费结算单
     */
    @Override
    public List<ManManufactureOutsourceSettle> selectManManufactureOutsourceSettleList(ManManufactureOutsourceSettle manManufactureOutsourceSettle) {
        return manManufactureOutsourceSettleMapper.selectManManufactureOutsourceSettleList(manManufactureOutsourceSettle);
    }

    /**
     * 新增外发加工费结算单
     * 需要注意编码重复校验
     *
     * @param manManufactureOutsourceSettle 外发加工费结算单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManManufactureOutsourceSettle(ManManufactureOutsourceSettle manManufactureOutsourceSettle) {
        setConfirmInfo(manManufactureOutsourceSettle);
        // 设置 code
        setData(manManufactureOutsourceSettle, null);
        int row = manManufactureOutsourceSettleMapper.insert(manManufactureOutsourceSettle);
        if (row > 0) {
            ManManufactureOutsourceSettle outsourceSettle =
                    manManufactureOutsourceSettleMapper.selectManManufactureOutsourceSettleById(manManufactureOutsourceSettle.getManufactureOutsourceSettleSid());
            manManufactureOutsourceSettle.setManufactureOutsourceSettleCode(outsourceSettle.getManufactureOutsourceSettleCode());
            //外发加工费结算单-明细对象
            List<ManManufactureOutsourceSettleItem> manManufactureOutsourceSettleItemList = manManufactureOutsourceSettle.getManManufactureOutsourceSettleItemList();
            if (CollectionUtils.isNotEmpty(manManufactureOutsourceSettleItemList)) {
                if (HandleStatus.CONFIRMED.getCode().equals(manManufactureOutsourceSettle.getHandleStatus())) {
                    manManufactureOutsourceSettleItemList.forEach(item -> {
                        if (item.getPriceTax() == null) {
                            throw new BaseException("存在结算价未维护的明细行，请维护结算价后再确认！");
                        }
                        if (item.getSettleQuantity() == null || item.getSettleQuantity().compareTo(BigDecimal.ZERO) == -1) {
                            throw new BaseException("存在合格量为空或小于0的明细行，不允许确认");
                        }
                    });
                }
                addManManufactureOutsourceSettleItem(manManufactureOutsourceSettle, manManufactureOutsourceSettleItemList);
            }
            //外发加工费结算单-附件对象
            List<ManManufactureOutsourceSettleAttach> manManufactureOutsourceSettleAttachList = manManufactureOutsourceSettle.getAttachmentList();
            if (CollectionUtils.isNotEmpty(manManufactureOutsourceSettleAttachList)) {
                addManManufactureOutsourceSettleAttach(manManufactureOutsourceSettle, manManufactureOutsourceSettleAttachList);
            }
            // 额外扣款
            if (CollectionUtil.isNotEmpty(manManufactureOutsourceSettle.getExtraDeductionItemList())) {
                extraDeductionItemService.insertManOutsourceSettleExtraDeductionItemListBy(manManufactureOutsourceSettle);
            }
            //应付暂估流水
            // paymentEstimation(manManufactureOutsourceSettle);
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(manManufactureOutsourceSettle.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_MANUFACTURE_OUTSOURCE_SETTLE)
                        .setDocumentSid(manManufactureOutsourceSettle.getManufactureOutsourceSettleSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("外发加工费结算单" + outsourceSettle.getManufactureOutsourceSettleCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(outsourceSettle.getManufactureOutsourceSettleCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            }
            // 提交
            else if (ConstantsEms.SUBMIT_STATUS.equals(manManufactureOutsourceSettle.getHandleStatus())) {
                this.submit(manManufactureOutsourceSettle);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(manManufactureOutsourceSettle.getManufactureOutsourceSettleSid(), manManufactureOutsourceSettle.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 提交
     *
     * @param frmPhotoSampleGain
     * @return
     */
    private void submit(ManManufactureOutsourceSettle settle) {
        // 根据配置不同工厂走不同审批
        Map<String, Object> variables = new HashMap<>();
        variables.put("formId", settle.getManufactureOutsourceSettleSid());
        variables.put("formCode", settle.getManufactureOutsourceSettleCode());
        variables.put("formType", FormType.OutsourceBill.getCode());
        variables.put("startUserId", ApiThreadLocalUtil.get().getSysUser().getUserId());
        variables.put("entity", CommonUtil.transferEntityToMap(settle));
        try {
            AjaxResult result = workFlowService.submitOnly(variables);
        } catch (BaseException e) {
            throw e;
        }
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(ManManufactureOutsourceSettle manManufactureOutsourceSettle) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, manManufactureOutsourceSettle.getManufactureOutsourceSettleSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, manManufactureOutsourceSettle.getManufactureOutsourceSettleSid()));
        }
    }

    //应付暂估流水
    private void paymentEstimation(ManManufactureOutsourceSettle manManufactureOutsourceSettle) {
        if (!ConstantsEms.YES.equals(ApiThreadLocalUtil.get().getSysUser().getIsBusinessFinance())){
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(manManufactureOutsourceSettle.getHandleStatus())) {
            FinBookPaymentEstimation finBookPaymentEstimation = new FinBookPaymentEstimation();
            finBookPaymentEstimation.setDocumentDate(new Date());
            finBookPaymentEstimation.setCompanySid(manManufactureOutsourceSettle.getCompanySid());
            finBookPaymentEstimation.setBookType(ConstantsFinance.BOOK_TYPE_YFZG);
            finBookPaymentEstimation.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_POSV);
            BeanCopyUtils.copyProperties(manManufactureOutsourceSettle, finBookPaymentEstimation);
            List<ManManufactureOutsourceSettleItem> outsourceSettleItemList = new ArrayList<>();
            outsourceSettleItemList = manManufactureOutsourceSettle.getManManufactureOutsourceSettleItemList();
            if (CollectionUtils.isEmpty(outsourceSettleItemList)) {
                outsourceSettleItemList = manManufactureOutsourceSettleItemMapper.selectList(new QueryWrapper<ManManufactureOutsourceSettleItem>().lambda()
                        .eq(ManManufactureOutsourceSettleItem::getManufactureOutsourceSettleSid, manManufactureOutsourceSettle.getManufactureOutsourceSettleSid()));
            }
            List<FinBookPaymentEstimationItem> finBookPaymentEstimationItemList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(outsourceSettleItemList)) {
                for (ManManufactureOutsourceSettleItem outsourceSettleItem : outsourceSettleItemList) {
                    if (StrUtil.isNotEmpty(outsourceSettleItem.getFreeFlag()) && ConstantsEms.YES_OR_NO_N.equals(outsourceSettleItem.getFreeFlag())) {
                        FinBookPaymentEstimationItem finBookPaymentEstimationItem = new FinBookPaymentEstimationItem();
                        //本次结算量、结算价格（含税）大于0
                    /*if (outsourceSettleItem.getSettleQuantity() == null || (outsourceSettleItem.getSettleQuantity().compareTo(BigDecimal.ZERO)) == 1 ||
                            outsourceSettleItem.getPriceTax() == null || (outsourceSettleItem.getPriceTax().compareTo(BigDecimal.ZERO)) == 1) {
                        throw new BaseException("确认时合格量及结算价格不能为空，且大于0！");
                    }*/
                        finBookPaymentEstimationItem.setBookPaymentEstimationSid(finBookPaymentEstimation.getBookPaymentEstimationSid());
                        //关联单据号sid
                        finBookPaymentEstimationItem.setReferDocSid(manManufactureOutsourceSettle.getManufactureOutsourceSettleSid());
                        //关联单据号code
                        finBookPaymentEstimationItem.setReferDocCode(Long.parseLong(manManufactureOutsourceSettle.getManufactureOutsourceSettleCode()));
                        //关联单据行sid
                        finBookPaymentEstimationItem.setReferDocItemSid(outsourceSettleItem.getManufactureOutsourceSettleItemSid());
                        //关联单据行code
                        finBookPaymentEstimationItem.setItemNum(outsourceSettleItem.getItemNum());
                        //采购订单号
                        finBookPaymentEstimationItem.setPurchaseOrderSid(manManufactureOutsourceSettle.getManufactureOutsourceSettleSid());
                        //采购价单位
                        finBookPaymentEstimationItem.setUnitPrice(outsourceSettleItem.getUnitPrice());
                        //数量
                        finBookPaymentEstimationItem.setQuantity(outsourceSettleItem.getSettleQuantity());
                        //采购价（含税）
                        finBookPaymentEstimationItem.setPriceTax(outsourceSettleItem.getPriceTax());
                        if (outsourceSettleItem.getTaxRate() != null) {
                            //税率
                            finBookPaymentEstimationItem.setTaxRate(outsourceSettleItem.getTaxRate());
                            BigDecimal init = BigDecimal.ONE;
                            BigDecimal price = null;
                            //采购价（不含税）:采购价含税/(1+税率)
                            price = outsourceSettleItem.getPriceTax().divide(init.add(outsourceSettleItem.getTaxRate()), 4, RoundingMode.HALF_UP);
                            finBookPaymentEstimationItem.setPrice(price);
                        }
                        //采购金额（含税）：数量*采购价（含税）
                        finBookPaymentEstimationItem.setCurrencyAmountTax(finBookPaymentEstimationItem.getQuantity().multiply(finBookPaymentEstimationItem.getPriceTax()));
                        finBookPaymentEstimationItem.setMaterialSid(outsourceSettleItem.getMaterialSid());
//                        finBookPaymentEstimationItemMapper.insert(finBookPaymentEstimationItem);
                        finBookPaymentEstimationItemList.add(finBookPaymentEstimationItem);
                    }
                }
                if (CollectionUtil.isNotEmpty(finBookPaymentEstimationItemList)) {
                    finBookPaymentEstimation.setItemList(finBookPaymentEstimationItemList);
                    finBookPaymentEstimationServiceimpl.insertFinBookPaymentEstimation(finBookPaymentEstimation);
                }
            }
        }
    }

    /**
     * 设置档案的code
     * @param newOne 新值
     * @param old 旧值
     */
    private void setData(ManManufactureOutsourceSettle newOne, ManManufactureOutsourceSettle old) {
        // 供应商
        if (newOne.getVendorSid() == null) {
            newOne.setVendorCode(null);
        }
        else if (old == null || !newOne.getVendorSid().equals(old.getVendorSid())) {
            BasVendor vendor = vendorMapper.selectById(newOne.getVendorSid());
            if (vendor != null) {
                newOne.setVendorCode(String.valueOf(vendor.getVendorCode()));
            }
        }
        // 工厂
        if (newOne.getPlantSid() == null) {
            newOne.setPlantCode(null);
        }
        else if (old == null || !newOne.getPlantSid().equals(old.getPlantSid())) {
            BasPlant plant = plantMapper.selectById(newOne.getPlantSid());
            if (plant != null) {
                newOne.setPlantCode(String.valueOf(plant.getPlantCode()));
            }
        }
        // 公司
        if (newOne.getCompanySid() == null) {
            newOne.setCompanyCode(null);
        }
        else if (old == null || !newOne.getCompanySid().equals(old.getCompanySid())) {
            BasCompany company = companyMapper.selectById(newOne.getCompanySid());
            if (company != null) {
                newOne.setCompanyCode(String.valueOf(company.getCompanyCode()));
            }
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ManManufactureOutsourceSettle o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
//            if (o.getPurchaseContractCode() == null) {
//                throw new BaseException("确认时采购合同号不能为空，请查看！");
//            }
            List<ManManufactureOutsourceSettleItem> itemList = o.getManManufactureOutsourceSettleItemList();
            if (CollectionUtil.isEmpty(itemList)) {
                throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 外发加工费结算单-明细对象
     */
    private void addManManufactureOutsourceSettleItem(ManManufactureOutsourceSettle manManufactureOutsourceSettle, List<ManManufactureOutsourceSettleItem> manManufactureOutsourceSettleItemList) {
        long i = 1;
        Long maxItemNum = manManufactureOutsourceSettle.getMaxItemNum();
        if (maxItemNum != null) {
            i = maxItemNum + i;
        }
        for (ManManufactureOutsourceSettleItem settleItem : manManufactureOutsourceSettleItemList) {
            // 采购价单位默认等于基本计量单位且换位换算比例默认1
            settleItem.setUnitPrice(settleItem.getUnitBase()).setUnitConversionRate(BigDecimal.ONE);
            settleItem.setManufactureOutsourceSettleSid(manManufactureOutsourceSettle.getManufactureOutsourceSettleSid());
            settleItem.setManufactureOutsourceSettleCode(manManufactureOutsourceSettle.getManufactureOutsourceSettleCode());
            settleItem.setItemNum(i).setCreateDate(null).setCreatorAccount(null);
            if (settleItem.getDefectiveAllowableRate() != null) {
                BigDecimal decimal = new BigDecimal(0);
                decimal = decimal.add(new BigDecimal(settleItem.getDefectiveAllowableRate()).divide(BigDecimal.valueOf(100))).setScale(3, BigDecimal.ROUND_HALF_UP);
                settleItem.setDefectiveAllowableRate(decimal.toString());
            }
            i++;
        }
        manManufactureOutsourceSettleItemMapper.inserts(manManufactureOutsourceSettleItemList);
    }

    private void deleteItem(ManManufactureOutsourceSettle manManufactureOutsourceSettle) {
        manManufactureOutsourceSettleItemMapper.delete(
                new UpdateWrapper<ManManufactureOutsourceSettleItem>()
                        .lambda()
                        .eq(ManManufactureOutsourceSettleItem::getManufactureOutsourceSettleSid, manManufactureOutsourceSettle.getManufactureOutsourceSettleSid())
        );
    }

    /**
     * 外发加工费结算单-附件对象
     */
    private void addManManufactureOutsourceSettleAttach(ManManufactureOutsourceSettle manManufactureOutsourceSettle, List<ManManufactureOutsourceSettleAttach> manManufactureOutsourceSettleAttachList) {
//        deleteAttach(manManufactureOutsourceSettle);
        manManufactureOutsourceSettleAttachList.forEach(o -> {
            o.setManufactureOutsourceSettleSid(manManufactureOutsourceSettle.getManufactureOutsourceSettleSid());
            o.setManufactureOutsourceSettleCode(manManufactureOutsourceSettle.getManufactureOutsourceSettleCode());
            manManufactureOutsourceSettleAttachMapper.insert(o);
        });
    }

    private void deleteAttach(ManManufactureOutsourceSettle manManufactureOutsourceSettle) {
        manManufactureOutsourceSettleAttachMapper.delete(
                new UpdateWrapper<ManManufactureOutsourceSettleAttach>()
                        .lambda()
                        .eq(ManManufactureOutsourceSettleAttach::getManufactureOutsourceSettleSid, manManufactureOutsourceSettle.getManufactureOutsourceSettleSid())
        );
    }

    /**
     * 修改外发加工费结算单
     *
     * @param manManufactureOutsourceSettle 外发加工费结算单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManManufactureOutsourceSettle(ManManufactureOutsourceSettle manManufactureOutsourceSettle) {
        ManManufactureOutsourceSettle response = manManufactureOutsourceSettleMapper.selectManManufactureOutsourceSettleById(manManufactureOutsourceSettle.getManufactureOutsourceSettleSid());
        setConfirmInfo(manManufactureOutsourceSettle);
        // 设置 code
        setData(manManufactureOutsourceSettle, null);
        manManufactureOutsourceSettle.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = manManufactureOutsourceSettleMapper.updateAllById(manManufactureOutsourceSettle);
        if (row > 0) {
            //外发加工费结算单-明细对象
            List<ManManufactureOutsourceSettleItem> settleItemList = manManufactureOutsourceSettle.getManManufactureOutsourceSettleItemList();
            if (CollectionUtils.isNotEmpty(settleItemList)) {
                if (HandleStatus.CONFIRMED.getCode().equals(manManufactureOutsourceSettle.getHandleStatus())) {
                    settleItemList.forEach(item -> {
                        if (item.getPriceTax() == null) {
                            throw new BaseException("存在结算价未维护的明细行，请维护结算价后再确认！");
                        }
                        if (item.getSettleQuantity() == null || item.getSettleQuantity().compareTo(BigDecimal.ZERO) == -1) {
                            throw new BaseException("存在合格量为空或小于0的明细行，不允许确认");
                        }
                    });
                }
            }
            operateItem(manManufactureOutsourceSettle, settleItemList);
            //外发加工费结算单-附件对象
            List<ManManufactureOutsourceSettleAttach> settleAttachList = manManufactureOutsourceSettle.getAttachmentList();
            operateAttachment(manManufactureOutsourceSettle, settleAttachList);
            // 额外扣款
            extraDeductionItemService.updateManOutsourceSettleExtraDeductionItemListBy(manManufactureOutsourceSettle);
            //应付暂估流水
            // paymentEstimation(manManufactureOutsourceSettle);
            if (!ConstantsEms.SAVA_STATUS.equals(manManufactureOutsourceSettle.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(manManufactureOutsourceSettle);
            }
            // 提交
            if (ConstantsEms.SUBMIT_STATUS.equals(manManufactureOutsourceSettle.getHandleStatus())) {
                this.submit(manManufactureOutsourceSettle);
            }
            //插入日志
            List<OperMsg> msgList = BeanUtils.eq(response, manManufactureOutsourceSettle);
            MongodbDeal.update(manManufactureOutsourceSettle.getManufactureOutsourceSettleSid(), response.getHandleStatus(), manManufactureOutsourceSettle.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 外发加工费结算单-明细
     */
    private void operateItem(ManManufactureOutsourceSettle manManufactureOutsourceSettle, List<ManManufactureOutsourceSettleItem> settleItemList) {
        if (CollectionUtil.isNotEmpty(settleItemList)) {
            //最大行号
            List<Long> itemNums = settleItemList.stream().filter(o -> o.getItemNum() != null).map(ManManufactureOutsourceSettleItem::getItemNum).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(itemNums)) {
                Long maxItemNum = itemNums.stream().max(Comparator.comparingLong(Long::longValue)).get();
                manManufactureOutsourceSettle.setMaxItemNum(maxItemNum);
            }
            //新增
            List<ManManufactureOutsourceSettleItem> addList = settleItemList.stream().filter(o -> o.getManufactureOutsourceSettleItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addManManufactureOutsourceSettleItem(manManufactureOutsourceSettle, addList);
            }
            //编辑
            List<ManManufactureOutsourceSettleItem> editList = settleItemList.stream().filter(o -> o.getManufactureOutsourceSettleItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    if (o.getDefectiveAllowableRate() != null) {
                        BigDecimal decimal = new BigDecimal(0);
                        decimal = decimal.add(new BigDecimal(o.getDefectiveAllowableRate()).divide(BigDecimal.valueOf(100))).setScale(3, BigDecimal.ROUND_HALF_UP);
                        o.setDefectiveAllowableRate(decimal.toString());
                    }
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manManufactureOutsourceSettleItemMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ManManufactureOutsourceSettleItem> itemList = manManufactureOutsourceSettleItemMapper.selectList(new QueryWrapper<ManManufactureOutsourceSettleItem>().lambda()
                    .eq(ManManufactureOutsourceSettleItem::getManufactureOutsourceSettleSid, manManufactureOutsourceSettle.getManufactureOutsourceSettleSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManManufactureOutsourceSettleItem::getManufactureOutsourceSettleItemSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = settleItemList.stream().map(ManManufactureOutsourceSettleItem::getManufactureOutsourceSettleItemSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manManufactureOutsourceSettleItemMapper.deleteBatchIds(result);
            }
        } else {
            deleteItem(manManufactureOutsourceSettle);
        }
    }

    /**
     * 外发加工费结算单-附件
     */
    private void operateAttachment(ManManufactureOutsourceSettle manManufactureOutsourceSettle, List<ManManufactureOutsourceSettleAttach> settleAttachList) {
        if (CollectionUtil.isNotEmpty(settleAttachList)) {
            //新增
            List<ManManufactureOutsourceSettleAttach> addList = settleAttachList.stream().filter(o -> o.getManufactureOutsourceSettleAttachSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addManManufactureOutsourceSettleAttach(manManufactureOutsourceSettle, addList);
            }
            //编辑
            List<ManManufactureOutsourceSettleAttach> editList = settleAttachList.stream().filter(o -> o.getManufactureOutsourceSettleAttachSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manManufactureOutsourceSettleAttachMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ManManufactureOutsourceSettleAttach> itemList =
                    manManufactureOutsourceSettleAttachMapper.selectList(new QueryWrapper<ManManufactureOutsourceSettleAttach>().lambda()
                            .eq(ManManufactureOutsourceSettleAttach::getManufactureOutsourceSettleSid, manManufactureOutsourceSettle.getManufactureOutsourceSettleSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManManufactureOutsourceSettleAttach::getManufactureOutsourceSettleAttachSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = settleAttachList.stream().map(ManManufactureOutsourceSettleAttach::getManufactureOutsourceSettleAttachSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                manManufactureOutsourceSettleAttachMapper.deleteBatchIds(result);
            }
        } else {
            deleteAttach(manManufactureOutsourceSettle);
        }
    }

    /**
     * 变更外发加工费结算单
     *
     * @param manManufactureOutsourceSettle 外发加工费结算单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManManufactureOutsourceSettle(ManManufactureOutsourceSettle manManufactureOutsourceSettle) {
        setConfirmInfo(manManufactureOutsourceSettle);
        // 设置 code
        setData(manManufactureOutsourceSettle, null);
        manManufactureOutsourceSettle.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        ManManufactureOutsourceSettle response = manManufactureOutsourceSettleMapper.selectManManufactureOutsourceSettleById(manManufactureOutsourceSettle.getManufactureOutsourceSettleSid());
        int row = manManufactureOutsourceSettleMapper.updateAllById(manManufactureOutsourceSettle);
        if (row > 0) {
            //外发加工费结算单-明细对象
            List<ManManufactureOutsourceSettleItem> manManufactureOutsourceSettleItemList = manManufactureOutsourceSettle.getManManufactureOutsourceSettleItemList();
            if (CollectionUtils.isNotEmpty(manManufactureOutsourceSettleItemList)) {
                manManufactureOutsourceSettleItemList.stream().forEach(o -> {
                    if (o.getPriceTax() == null) {
                        throw new BaseException("存在结算价未维护的明细行，请维护结算价后再确认！");
                    }
                    if (o.getSettleQuantity() == null || o.getSettleQuantity().compareTo(BigDecimal.ZERO) == -1) {
                        throw new BaseException("存在合格量为空或小于0的明细行，不允许确认");
                    }
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
            }
            operateItem(manManufactureOutsourceSettle, manManufactureOutsourceSettleItemList);
            //外发加工费结算单-附件对象
            List<ManManufactureOutsourceSettleAttach> manManufactureOutsourceSettleAttachList = manManufactureOutsourceSettle.getAttachmentList();
            if (CollectionUtils.isNotEmpty(manManufactureOutsourceSettleAttachList)) {
                manManufactureOutsourceSettleAttachList.stream().forEach(o -> {
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addManManufactureOutsourceSettleAttach(manManufactureOutsourceSettle, manManufactureOutsourceSettleAttachList);
            } else {
                deleteAttach(manManufactureOutsourceSettle);
            }
            // 额外扣款
            extraDeductionItemService.updateManOutsourceSettleExtraDeductionItemListBy(manManufactureOutsourceSettle);
            //插入日志
            List<OperMsg> msgList = BeanUtils.eq(response, manManufactureOutsourceSettle);
            MongodbDeal.update(manManufactureOutsourceSettle.getManufactureOutsourceSettleSid(), response.getHandleStatus(), manManufactureOutsourceSettle.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量删除外发加工费结算单
     *
     * @param manufactureOutsourceSettleSids 需要删除的外发加工费结算单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManManufactureOutsourceSettleByIds(List<Long> manufactureOutsourceSettleSids) {
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(ConstantsEms.SAVA_STATUS);
        handleStatusList.add(ConstantsEms.BACK_STATUS);
        Integer count = manManufactureOutsourceSettleMapper.selectCount(new QueryWrapper<ManManufactureOutsourceSettle>().lambda()
                .in(ManManufactureOutsourceSettle::getHandleStatus, handleStatusList)
                .in(ManManufactureOutsourceSettle::getManufactureOutsourceSettleSid, manufactureOutsourceSettleSids));
        if (count != manufactureOutsourceSettleSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT_APPROVE);
        }
        //删除外发加工费结算单
        manManufactureOutsourceSettleMapper.deleteBatchIds(manufactureOutsourceSettleSids);
        //删除外发加工费结算单明细
        manManufactureOutsourceSettleItemMapper.delete(new UpdateWrapper<ManManufactureOutsourceSettleItem>().lambda()
                .in(ManManufactureOutsourceSettleItem::getManufactureOutsourceSettleSid, manufactureOutsourceSettleSids));
        //删除外发加工费结算单附件
        manManufactureOutsourceSettleAttachMapper.delete(new UpdateWrapper<ManManufactureOutsourceSettleAttach>().lambda()
                .in(ManManufactureOutsourceSettleAttach::getManufactureOutsourceSettleSid, manufactureOutsourceSettleSids));
        // 删除额外扣款
        extraDeductionItemService.deleteListBySids(manufactureOutsourceSettleSids);
        ManManufactureOutsourceSettle manManufactureOutsourceSettle = new ManManufactureOutsourceSettle();
        manufactureOutsourceSettleSids.forEach(manufactureOutsourceSettleSid -> {
            manManufactureOutsourceSettle.setManufactureOutsourceSettleSid(manufactureOutsourceSettleSid);
            //校验是否存在待办
            checkTodoExist(manManufactureOutsourceSettle);
        });
        return manufactureOutsourceSettleSids.size();
    }

    /**
     * 更改确认状态
     *
     * @param manManufactureOutsourceSettle
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ManManufactureOutsourceSettle manufactureOutsourceSettle) {
        Long[] sids = manufactureOutsourceSettle.getManufactureOutsourceSettleSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                manufactureOutsourceSettle.setManufactureOutsourceSettleSid(id);
                //校验是否存在待办
                checkTodoExist(manufactureOutsourceSettle);
                manManufactureOutsourceSettleMapper.updateById(manufactureOutsourceSettle);

                ManManufactureOutsourceSettle manManufactureOutsourceSettle =
                        manManufactureOutsourceSettleMapper.selectManManufactureOutsourceSettleById(id);
                //应付暂估流水
                // paymentEstimation(manManufactureOutsourceSettle);
                //插入日志
                /*List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.check(id, manufactureOutsourceSettle.getHandleStatus(), msgList, TITLE);*/
            }
        }
        return sids.length;
    }

    /**
     * 更改处理状态
     *
     * @param sids, handleStatus
     * @return
     */
    private int updateHandle(Long[] sids, String handleStatus) {
        LambdaUpdateWrapper<ManManufactureOutsourceSettle> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ManManufactureOutsourceSettle::getManufactureOutsourceSettleSid, sids);
        updateWrapper.set(ManManufactureOutsourceSettle::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(ManManufactureOutsourceSettle::getConfirmDate, new Date());
            updateWrapper.set(ManManufactureOutsourceSettle::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return manManufactureOutsourceSettleMapper.update(null, updateWrapper);
    }

    /**
     * 更改确认状态
     *
     * @param outsourceSettle
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approval(ManManufactureOutsourceSettle outsourceSettle) {
        int row = 0;
        Long[] sids = outsourceSettle.getManufactureOutsourceSettleSidList();
        // 处理状态
        String handleStatus = outsourceSettle.getHandleStatus();
        if (StrUtil.isNotBlank(outsourceSettle.getBusinessType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(outsourceSettle.getBusinessType());
        }
        if (sids != null && sids.length > 0) {
            // 获取数据
            List<ManManufactureOutsourceSettle> outsourceSettleList = manManufactureOutsourceSettleMapper.selectManManufactureOutsourceSettleList(
                    new ManManufactureOutsourceSettle().setManufactureOutsourceSettleSidList(sids));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus, null);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(outsourceSettle.getBusinessType())) {
                // 修改处理状态
                this.updateHandle(sids, ConstantsEms.SUBMIT_STATUS);
                // 开启工作流
                for (int i = 0; i < outsourceSettleList.size(); i++) {
                    // 提交
                    this.submit(outsourceSettleList.get(i));
                    //插入日志
                    MongodbUtil.insertUserLog(outsourceSettleList.get(i).getManufactureOutsourceSettleSid(),
                            BusinessType.SUBMIT.getValue(), null, TITLE, outsourceSettle.getComment());
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(outsourceSettle.getBusinessType())) {
                Map<String, List<ManManufactureOutsourceSettle>> map = outsourceSettleList.stream()
                        .collect(Collectors.groupingBy(o -> String.valueOf(o.getApprovalUserId())));
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                if (map != null) {
                    for (String key : map.keySet()) {
                        if (key == null || (!key.equals(userId.toString()) && !key.startsWith(userId.toString()+",")
                                && key.indexOf(","+userId.toString()) == -1 && key.indexOf(", "+userId.toString()) == -1)) {
                            throw new BaseException("您不是当前审批节点处理人，无法点击此按钮！");
                        }
                    }
                }
                // 审批意见
                String comment = "";
                for (int i = 0; i < outsourceSettleList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(outsourceSettleList.get(i).getManufactureOutsourceSettleSid().toString());
                    taskVo.setFormId(outsourceSettleList.get(i).getManufactureOutsourceSettleSid());
                    taskVo.setFormCode(outsourceSettleList.get(i).getManufactureOutsourceSettleCode().toString());
                    taskVo.setFormType(FormType.OutsourceBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(outsourceSettle.getComment());
                    Map<String, Object> variables = new HashMap<>();
                    variables.put("entity", CommonUtil.transferEntityToMap(outsourceSettleList.get(i)));
                    taskVo.setValues(variables);
                    try {
                        SysFormProcess process = workFlowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 修改处理状态
                            this.updateHandle(new Long[]{outsourceSettleList.get(i).getManufactureOutsourceSettleSid()}, ConstantsEms.CHECK_STATUS);
                        }
                        comment = process.getRemark();
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(outsourceSettleList.get(i).getManufactureOutsourceSettleSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, comment);
                }
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(outsourceSettle.getBusinessType())) {
                Map<String, List<ManManufactureOutsourceSettle>> map = outsourceSettleList.stream()
                        .collect(Collectors.groupingBy(o -> String.valueOf(o.getApprovalUserId())));
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                if (map != null) {
                    for (String key : map.keySet()) {
                        if (key == null || (!key.equals(userId.toString()) && !key.startsWith(userId.toString()+",")
                                && key.indexOf(","+userId.toString()) == -1 && key.indexOf(", "+userId.toString()) == -1)) {
                            throw new BaseException("您不是当前审批节点处理人，无法点击此按钮！");
                        }
                    }
                }
                // 审批意见
                String comment = "";
                for (int i = 0; i < outsourceSettleList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setTargetKey("2");
                    taskVo.setBusinessKey(outsourceSettleList.get(i).getManufactureOutsourceSettleSid().toString());
                    taskVo.setFormId(outsourceSettleList.get(i).getManufactureOutsourceSettleSid());
                    taskVo.setFormCode(outsourceSettleList.get(i).getManufactureOutsourceSettleCode().toString());
                    taskVo.setFormType(FormType.OutsourceBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(outsourceSettle.getComment());
                    try {
                        SysFormProcess process = workFlowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 修改处理状态
                            this.updateHandle(new Long[]{outsourceSettleList.get(i).getManufactureOutsourceSettleSid()}, HandleStatus.RETURNED.getCode());
                        }
                        comment = process.getRemark();
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(outsourceSettleList.get(i).getManufactureOutsourceSettleSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, comment);
                }
            }
        }
        return 1;
    }

    /**
     * 提交
     *
     * @param manManufactureOutsourceSettle
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity verify(ManManufactureOutsourceSettle outsourceSettle) {
        List<CommonErrMsgResponse> errorList = new ArrayList<>();
        Long code = null;
        if (StrUtil.isNotBlank(outsourceSettle.getManufactureOutsourceSettleCode())) {
            code = Long.parseLong(outsourceSettle.getManufactureOutsourceSettleCode());
        }
        if (outsourceSettle.getPurchaseContractCode() == null) {
//            CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
//            errMsg.setCode(code);
//            errMsg.setMsg("采购合同号不能为空！");
//            errorList.add(errMsg);
        }
        List<ManManufactureOutsourceSettleItem> list = outsourceSettle.getManManufactureOutsourceSettleItemList();
        if (CollectionUtils.isNotEmpty(list)) {
            for (ManManufactureOutsourceSettleItem item : list) {
                if (ConstantsEms.YES_OR_NO_N.equals(item.getFreeFlag()) && item.getPriceTax() == null) {
                    CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                    errMsg.setCode(code);
                    errMsg.setMsg("存在结算价未维护的明细行，请维护结算价后再提交！");
                    errorList.add(errMsg);
                }
                if (item.getSettleQuantity() == null || item.getSettleQuantity().compareTo(BigDecimal.ZERO) == -1) {
                    CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
                    errMsg.setCode(code);
                    errMsg.setMsg("存在合格量为空或小于0的明细行，不允许提交！");
                    errorList.add(errMsg);
                }
            };
        } else {
            CommonErrMsgResponse errMsg = new CommonErrMsgResponse();
            errMsg.setCode(code);
            errMsg.setMsg("“明细信息”页签的明细行数据不能为空！");
            errorList.add(errMsg);
        }
        if (CollectionUtil.isNotEmpty(errorList)) {
            return EmsResultEntity.error(errorList);
        }
        return EmsResultEntity.success();
    }

    /**
     * 作废外发加工费结算单
     */
    @Override
    public int cancellationManufactureOutsourceSettleById(Long manufactureOutsourceSettleSid) {
        ManManufactureOutsourceSettle manManufactureOutsourceSettle = manManufactureOutsourceSettleMapper.selectManManufactureOutsourceSettleById(manufactureOutsourceSettleSid);
        if (!ConstantsEms.CHECK_STATUS.equals(manManufactureOutsourceSettle.getHandleStatus())) {
            throw new BaseException(ConstantsEms.CONFIRM_CANCELLATION);
        }
        if (ConstantsEms.YES.equals(ApiThreadLocalUtil.get().getSysUser().getIsBusinessFinance())){
            List<FinBookPaymentEstimationItem> items =
                    finBookPaymentEstimationItemMapper.selectList(new QueryWrapper<FinBookPaymentEstimationItem>().lambda()
                            .eq(FinBookPaymentEstimationItem::getReferDocSid, manufactureOutsourceSettleSid));
            if (CollectionUtil.isNotEmpty(items)) {
                List<FinBookPaymentEstimationItem> statusList =
                        items.stream().filter(o -> ConstantsEms.CLEAR_STATUS_WHX.equals(o.getClearStatus())).collect(Collectors.toList());
                if (items.size() != statusList.size()) {
                    throw new BaseException("此外发加工费结算单，对应的应付暂估流水非'未核销'状态，不允许作废！");
                }
                FinBookPaymentEstimation finBookPaymentEstimation = new FinBookPaymentEstimation();
                items.forEach(o -> {
                    finBookPaymentEstimation.setBookPaymentEstimationSid(o.getBookPaymentEstimationSid());
                });
                finBookPaymentEstimation.setHandleStatus(ConstantsEms.HANDLE_IM);
                finBookPaymentEstimationMapper.updateById(finBookPaymentEstimation);
            }
        }
        //插入日志
        MongodbUtil.insertUserLog(manufactureOutsourceSettleSid, BusinessType.CANCEL.getValue(), manManufactureOutsourceSettle, manManufactureOutsourceSettle, TITLE);
        manManufactureOutsourceSettle.setHandleStatus(ConstantsEms.HANDLE_IM);
        manManufactureOutsourceSettle.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        manManufactureOutsourceSettle.setUpdateDate(new Date());
        return manManufactureOutsourceSettleMapper.updateById(manManufactureOutsourceSettle);
    }

    /**
     * 获取明细的加工采购价
     * @param settle
     * @return
     */
    @Override
    public ManManufactureOutsourceSettle itemGetPrice(ManManufactureOutsourceSettle settle, boolean flag) {
        if (settle == null || CollectionUtil.isEmpty(settle.getManManufactureOutsourceSettleItemList())) {
            return settle;
        }
        List<ManManufactureOutsourceSettleItem> settleItemList = settle.getManManufactureOutsourceSettleItemList();
        List<ManManufactureOutsourceSettleItem> processList = settleItemList.stream().filter(o->o.getProcessSid() != null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(processList) && settle.getVendorSid() != null) {
            // 根据加工商 和 各明细的商品sid，工序sid 查询加工采购价
            PurOutsourcePurchasePriceItem priceItem = new PurOutsourcePurchasePriceItem();
            Long[] processSidList = processList.stream().map(ManManufactureOutsourceSettleItem::getProcessSid).toArray(Long[]::new);
            Long[] materialSidList = settleItemList.stream().map(ManManufactureOutsourceSettleItem::getMaterialSid).toArray(Long[]::new);
            priceItem.setVendorSid(settle.getVendorSid()).setMaterialSidList(materialSidList).setProcessSidList(processSidList);
            DateTimeFormatter fmDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            priceItem.setNowDate(LocalDate.now().format(fmDate));
            priceItem.setHandleStatus(ConstantsEms.CHECK_STATUS);
            List<PurOutsourcePurchasePriceItem> priceItemList = outsourcePurchasePriceItemMapper.selectPurOutsourcePurchasePriceItemList(priceItem);
            if (CollectionUtil.isNotEmpty(priceItemList)) {
                Map<String, List<PurOutsourcePurchasePriceItem>> map = priceItemList.stream().collect(Collectors.groupingBy(e ->
                        String.valueOf(e.getMaterialSid()) + "-"+ String.valueOf(e.getProcessSid()) + "-"+ String.valueOf(e.getSku1Sid())));
                for (ManManufactureOutsourceSettleItem item : settleItemList) {
                    String key = String.valueOf(item.getMaterialSid()) +
                            "-"+ String.valueOf(item.getProcessSid()) + "-"+ String.valueOf(item.getSku1Sid());
                    BigDecimal priceTax = null;
                    if (map.containsKey(key)) {
                        priceTax = map.get(key).get(0).getPurchasePriceTax();
                        item.setNowPriceTax(priceTax);
                    }
                    // 按款色的找不到价格 就找按款的
                    if (item.getSku1Sid() != null && item.getNowPriceTax() == null) {
                        key = String.valueOf(item.getMaterialSid()) +
                                "-"+ String.valueOf(item.getProcessSid()) + "-"+ null;
                        if (map.containsKey(key)) {
                            priceTax = map.get(key).get(0).getPurchasePriceTax();
                            item.setNowPriceTax(priceTax);
                        }
                    }
                    // 详情的时候 flag = false 价格取本身，不是详情详情的  时候 判断 价格没填再写入
                    if (flag && item.getPriceTax() == null) {
                        item.setPriceTax(priceTax);
                    }
                }
            }
        }
        for (ManManufactureOutsourceSettleItem item : settleItemList) {
            // 若“次品扣款价(允许范围内)”的值为空，且系统默认设置(租户级)中的字段“次品扣款价(范围内)默认等于加工价”的值为“是”，
            // 则添加成功后，次品扣款价(允许范围内)=加工价(含税)
            String isInDefectivePriceTaxEqualToPriceTax = ApiThreadLocalUtil.get().getSysUser().getClient().getIsInDefectivePriceTaxEqualToPriceTax();
            if (item.getInDefectivePriceTax() == null && ConstantsEms.YES.equals(
                    ApiThreadLocalUtil.get().getSysUser().getClient().getIsInDefectivePriceTaxEqualToPriceTax())) {
                item.setInDefectivePriceTax(item.getPriceTax());
            }
        }
        return settle;
    }
}
