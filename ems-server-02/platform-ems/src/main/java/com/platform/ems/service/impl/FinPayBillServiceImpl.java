package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.constant.*;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.*;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.data.BigDecimalSum;
import com.platform.ems.workflow.service.impl.WorkFlowServiceImpl;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
import com.platform.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.util.MongodbUtil;

/**
 * 付款单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-21
 */
@Service
@SuppressWarnings("all")
public class FinPayBillServiceImpl extends ServiceImpl<FinPayBillMapper, FinPayBill> implements IFinPayBillService {
    @Autowired
    private FinPayBillMapper finPayBillMapper;
    @Autowired
    private IFinPayBillItemService billItemService;
    @Autowired
    private IFinPayBillItemInvoiceService invoiceService;
    @Autowired
    private IFinPayBillItemOutsourceSettleService outsourceSettleService;
    @Autowired
    private IFinPayBillItemYufuService yufuService;
    @Autowired
    private IFinPayBillItemHuokuanService huokuanService;
    @Autowired
    private IFinPayBillItemKoukuanService koukuanService;
    @Autowired
    private IFinPayBillItemKoukuanJiagongliaoService jiagongliaoService;
    @Autowired
    private IFinPayBillItemKoukuanTuihuoService tuihuoService;
    @Autowired
    private FinPayBillAttachmentMapper attachmentMapper;
    @Autowired
    private BasVendorMapper vendorMapper;
    @Autowired
    private BasCompanyMapper companyMapper;

    @Autowired
    private IFinBookPaymentEstimationItemService bookPaymentEstimationItemService;
    @Autowired
    private FinBookPaymentEstimationItemMapper bookPaymentEstimationItemMapper;
    @Autowired
    private FinClearLogPaymentEstimationMapper logPaymentEstimationMapper;
    @Autowired
    private FinClearLogVendorDeductionMapper logVendorDeductionMapper;
    @Autowired
    private FinClearLogAdvancePaymentMapper logAdvancePaymentMapper;
    @Autowired
    private IFinBookVendorDeductionItemService bookVendorDeductionItemService;
    @Autowired
    private FinBookVendorDeductionItemMapper bookVendorDeductionItemMapper;
    @Autowired
    private IFinBookPaymentItemService bookPaymentItemService;
    @Autowired
    private FinBookPaymentItemMapper bookPaymentItemMapper;
    @Autowired
    private IFinBookPaymentService finBookPaymentService;
    @Autowired
    private FinBookPaymentMapper finBookPaymentMapper;
    @Autowired
    private FinVendorInvoiceRecordMapper finVendorInvoiceRecordMapper;
    @Autowired
    private SysDefaultSettingClientMapper defaultSettingClientMapper;
    @Autowired
    private ISysTodoTaskService sysTodoTaskService;
    @Autowired
    private ISysDictDataService sysDictDataService;
    @Autowired
    private WorkFlowServiceImpl workflowService;

    private static final String TITLE = "付款单";
    /**
     * 获取租户默认设置
     */
    @Override
    public SysDefaultSettingClient getClientSetting() {
        return defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
    }

    /**
     * 查询付款单
     *
     * @param payBillSid 付款单ID
     * @return 付款单
     */
    @Override
    public FinPayBill selectFinPayBillById(Long payBillSid) {
        FinPayBill finPayBill = finPayBillMapper.selectFinPayBillById(payBillSid);
        if (finPayBill == null) {
            throw new BaseException("找不到该付款单");
        }
        // 明细列表
        finPayBill.setItemList(new ArrayList<>());
        List<FinPayBillItem> itemList = billItemService.selectFinPayBillItemList
                (new FinPayBillItem().setPayBillSid(payBillSid));
        if (CollectionUtil.isNotEmpty(itemList)) {
            finPayBill.setItemList(itemList);
        }
        // 外发加工费明细列表
        finPayBill.setOutsourceSettleList(new ArrayList<>());
        List<FinPayBillItemOutsourceSettle> outsourceSettleList = outsourceSettleService.selectFinPayBillItemOutsourceSettleList
                (new FinPayBillItemOutsourceSettle().setPayBillSid(payBillSid));
        if (CollectionUtil.isNotEmpty(outsourceSettleList)) {
            finPayBill.setOutsourceSettleList(outsourceSettleList);
        }
        // 发票台账明细列表
        finPayBill.setInvoiceList(new ArrayList<>());
        List<FinPayBillItemInvoice> invoiceList = invoiceService.selectFinPayBillItemInvoiceList
                (new FinPayBillItemInvoice().setPayBillSid(payBillSid));
        if (CollectionUtil.isNotEmpty(invoiceList)) {
            finPayBill.setInvoiceList(invoiceList);
        }
        // 货款页签 / 预付款页签
        if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finPayBill.getBusinessType())
                || ConstantsFinance.PAY_BILL_BUSINESS_TYPE_YFK.equals(finPayBill.getBusinessType())) {
            // 本次核销已预付款明细列表
            finPayBill.setYufuList(new ArrayList<>());
            List<FinPayBillItemYufu> yufuList = yufuService.selectFinPayBillItemYufuList
                    (new FinPayBillItemYufu().setPayBillSid(payBillSid));
            if (CollectionUtil.isNotEmpty(yufuList)) {
                finPayBill.setYufuList(yufuList);
            }
            // 货款
            if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finPayBill.getBusinessType())) {
                // 本次核销扣款明细列表
                finPayBill.setHuokuanList(new ArrayList<>());
                List<FinPayBillItemHuokuan> huokuanList = huokuanService.selectFinPayBillItemHuokuanList
                        (new FinPayBillItemHuokuan().setPayBillSid(payBillSid));
                if (CollectionUtil.isNotEmpty(huokuanList)) {
                    finPayBill.setHuokuanList(huokuanList);
                }
                // 本次核销扣款明细列表
                finPayBill.setKoukuanList(new ArrayList<>());
                List<FinPayBillItemKoukuan> koukuanList = koukuanService.selectFinPayBillItemKoukuanList
                        (new FinPayBillItemKoukuan().setPayBillSid(payBillSid));
                if (CollectionUtil.isNotEmpty(koukuanList)) {
                    finPayBill.setKoukuanList(koukuanList);
                }
                // 本次核销甲供料扣款明细列表
                finPayBill.setJiagongliaoList(new ArrayList<>());
                List<FinPayBillItemKoukuanJiagongliao> kegongliaoList = jiagongliaoService.selectFinPayBillItemKoukuanJiagongliaoList
                        (new FinPayBillItemKoukuanJiagongliao().setPayBillSid(payBillSid));
                if (CollectionUtil.isNotEmpty(kegongliaoList)) {
                    finPayBill.setJiagongliaoList(kegongliaoList);
                }
                // 本次核销退货扣款明细列表
                finPayBill.setTuihuoList(new ArrayList<>());
                List<FinPayBillItemKoukuanTuihuo> tuihuoList = tuihuoService.selectFinPayBillItemKoukuanTuihuoList
                        (new FinPayBillItemKoukuanTuihuo().setPayBillSid(payBillSid));
                if (CollectionUtil.isNotEmpty(tuihuoList)) {
                    finPayBill.setTuihuoList(tuihuoList);
                }
            }
        }
        // 附件清单
        finPayBill.setAttachmentList(new ArrayList<>());
        List<FinPayBillAttachment> attachmentList = attachmentMapper.selectFinPayBillAttachmentList
                (new FinPayBillAttachment().setPayBillSid(payBillSid));
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            finPayBill.setAttachmentList(attachmentList);
        }
        // 计算明细
        countItem(finPayBill);
        // 计算基本信息页签的待核销数量
        countBaseDai(finPayBill, getClientSetting());
        // 操作日志
        MongodbUtil.find(finPayBill);
        return finPayBill;
    }

    /**
     * 查询付款单列表
     *
     * @param finPayBill 付款单
     * @return 付款单
     */
    @Override
    public List<FinPayBill> selectFinPayBillList(FinPayBill finPayBill) {
        return finPayBillMapper.selectFinPayBillList(finPayBill);
    }

    /**
     * 计算明细本次金额
     */
    public void countItem(FinPayBill finPayBill) {
        // 本次实付金额    =本次实付明细的金额(含税)之和
        if (CollectionUtil.isNotEmpty(finPayBill.getItemList())) {
            BigDecimal currencyAmountTax = finPayBill.getItemList().stream().map(FinPayBillItem::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            if (finPayBill.getCurrencyAmountTax() != null &&
                    finPayBill.getCurrencyAmountTax().compareTo(currencyAmountTax) != 0) {
                log.warn("前端计算本次实付小计(含税)与后端计算本次实付小计(含税)不一致");
            }
            if (finPayBill.getCurrencyAmountTax() == null) {
                finPayBill.setCurrencyAmountTax(currencyAmountTax);
            }
        }
        // 本次核销扣款     =本次核销扣款明细的金额(含税)之和
        if (CollectionUtil.isNotEmpty(finPayBill.getKoukuanList())) {
            BigDecimal heXiaoKouKuan = finPayBill.getKoukuanList().stream().map(FinPayBillItemKoukuan::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            if (finPayBill.getHeXiaoKouKuan() != null &&
                    finPayBill.getHeXiaoKouKuan().compareTo(heXiaoKouKuan) != 0) {
                log.warn("前端计算本次核销扣款与后端计算本次本次核销扣款不一致");
            }
            if (finPayBill.getHeXiaoKouKuan() == null) {
                finPayBill.setHeXiaoKouKuan(heXiaoKouKuan);
            }
        }
        // 本次核销甲供料扣款金额      =本次核销甲供料扣款=本次核销甲供料扣款明细的金额(含税)之和
        if (CollectionUtil.isNotEmpty(finPayBill.getJiagongliaoList())) {
            BigDecimal heXiaoJiagongliao = finPayBill.getJiagongliaoList().stream().map(FinPayBillItemKoukuanJiagongliao::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            if (finPayBill.getHeXiaoJiagongliao() != null &&
                    finPayBill.getHeXiaoJiagongliao().compareTo(heXiaoJiagongliao) != 0) {
                log.warn("前端计算本次核销甲供料扣款金额与后端计算本次核销甲供料扣款金额不一致");
            }
            if (finPayBill.getHeXiaoJiagongliao() == null) {
                finPayBill.setHeXiaoJiagongliao(heXiaoJiagongliao);
            }
        }
        // 本次核销退货扣款         =本次核销退货扣款明细的金额(含税)之和
        if (CollectionUtil.isNotEmpty(finPayBill.getTuihuoList())) {
            BigDecimal heXiaoTuihuo = finPayBill.getTuihuoList().stream().map(FinPayBillItemKoukuanTuihuo::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            if (finPayBill.getHeXiaoTuihuo() != null &&
                    finPayBill.getHeXiaoTuihuo().compareTo(heXiaoTuihuo) != 0) {
                log.warn("前端计算本次核销退货扣款与后端计算本次核销退货扣款不一致");
            }
            if (finPayBill.getHeXiaoTuihuo() == null) {
                finPayBill.setHeXiaoTuihuo(heXiaoTuihuo);
            }
        }
        // 本次核销已预付款  =本次核销已预付款明细的金额(含税)之和
        if (CollectionUtil.isNotEmpty(finPayBill.getYufuList())) {
            BigDecimal heXiaoYiyufu = finPayBill.getYufuList().stream().map(FinPayBillItemYufu::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            if (finPayBill.getHeXiaoYiyufu() != null &&
                    finPayBill.getHeXiaoYiyufu().compareTo(heXiaoYiyufu) != 0) {
                log.warn("前端计算本次核销已预付款与后端计算本次核销已预付款不一致");
            }
            if (finPayBill.getHeXiaoYiyufu() == null) {
                finPayBill.setHeXiaoYiyufu(heXiaoYiyufu);
            }
        }
        // 本次核销货款=本次实付金额+本次核销扣款+本次核销甲供料扣款金额+本次核销退货扣款+本次核销已预付款
        // sumTotalHuokuan(finPayBill);
        // 本次核销货款
        if (CollectionUtil.isNotEmpty(finPayBill.getHuokuanList())) {
            BigDecimal heXiaoHuokuan = finPayBill.getHuokuanList().stream().map(FinPayBillItemHuokuan::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            if (finPayBill.getHeXiaoHuokuan() != null &&
                    finPayBill.getHeXiaoHuokuan().compareTo(heXiaoHuokuan) != 0) {
                log.warn("前端计算本次核销货款与后端计算本次核销货款不一致");
            }
            if (finPayBill.getHeXiaoHuokuan() == null) {
                finPayBill.setHeXiaoHuokuan(heXiaoHuokuan);
            }
        }
    }

    /**
     * 计算基本信息的待数量、基本信息页签，待付货款金额(元)、待核销已预付金额(元)、待核销扣款金额(元)、待核销退货扣款金额(元)数据未显示
     */
    @Override
    public void countBaseDai(FinPayBill finPayBill, SysDefaultSettingClient settingClient) {
        if (finPayBill.getVendorSid() == null || finPayBill.getCompanySid() == null) {
            log.warn("计算基本信息的待数据时获取到的供应商或公司为空");
            return;
        }
        BigDecimal daiFuHuoKuan = BigDecimal.ZERO;
        BigDecimal daiXiaoTuiHuoKouKuan = BigDecimal.ZERO;
        if (ConstantsFinance.SHOUKUAN_ACC_CLEAR_WAY_YFZG.equals(settingClient.getFukuanAccountClearWay())) {
            // 2、找到所有“生效状态”为”未生效 “，”是否退货“为”否“，且供应商与公司一致的核销应付暂估日志，并计算出核销金额的和
            // 待付货款金额 = 第一步找到的未核销金额和 – 第二步找到的核销金额和
            // 找到未被处理状态不是“已确认”的应付暂估调价量单 引用 的 应付暂估流水
            List<FinBookPaymentEstimationItem> bookItemList = bookPaymentEstimationItemMapper.paySubmitSelect(new FinBookPaymentEstimationItem()
                    .setVendorSid(finPayBill.getVendorSid()).setCompanySid(finPayBill.getCompanySid()));
            List<FinBookPaymentEstimationItem> noTuihuo = new ArrayList<>();
            List<FinBookPaymentEstimationItem> yesTuihuo = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(bookItemList)) {
                bookItemList = bookItemList.stream().filter(i -> ConstantsFinance.CLEAR_STATUS_BFHX.equals(i.getClearStatus())
                        || ConstantsFinance.CLEAR_STATUS_WHX.equals(i.getClearStatus())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(bookItemList)) {
                    noTuihuo = bookItemList.stream().filter(i->ConstantsEms.NO.equals(i.getIsTuihuo())).collect(Collectors.toList());
                    yesTuihuo = bookItemList.stream().filter(i->ConstantsEms.YES.equals(i.getIsTuihuo())).collect(Collectors.toList());
                }
            }
            if (CollectionUtil.isNotEmpty(noTuihuo)) {
                daiFuHuoKuan = noTuihuo.stream().map(FinBookPaymentEstimationItem::getCurrencyAmountTaxLeft)
                        .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            }
            if (CollectionUtil.isNotEmpty(yesTuihuo)) {
                daiXiaoTuiHuoKouKuan = yesTuihuo.stream().map(FinBookPaymentEstimationItem::getCurrencyAmountTaxLeft)
                        .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            }
        }
        /**
         * 待付货款金额(元) : 计算逻辑
         * 》若系统默认设置的“付款核销模式“为”应付暂估“，默认显示所有”核销状态“为”未核销“或”部分核销“，”是否退货“为”否“，
         * 且供应商与公司一致的应付暂估流水的未核销金额(未核销金额=出入库金额-已核销金额)的和
         * 》若系统默认设置的“付款核销模式“为”应付“，暂不实现
         * 》若系统默认设置的“付款核销模式“为”应付“，显示为空
         */
        finPayBill.setDaiFuHuoKuan(daiFuHuoKuan);
        /**
         * 待核销退货扣款金额(元) 计算逻辑
         * 》若系统默认设置的“付款核销模式“为”应付暂估“，默认显示所有”核销状态“为”未核销“或”部分核销“，”是否退货“为”是“，
         * 且供应商与公司一致的应付暂估流水的未核销金额(未核销金额=出入库金额-已核销金额)的和的绝对值
         * 注意点：这里得到的流水的未核销金额为负数，显示时要转化为绝对值
         * 》若系统默认设置的“付款核销模式“为”应付“，暂不实现
         * 》若系统默认设置的“付款核销模式“为”应付“，显示为空
         */
        finPayBill.setDaiXiaoTuiHuoKouKuan(daiXiaoTuiHuoKouKuan.abs());
        /**
         * 待核销扣款金额(元) 计算逻辑
         * 默认显示所有”核销状态“为”未核销“或”部分核销“，
         * 且供应商与公司一致的供应商扣款流水的未核销金额(未核销金额=扣款金额-已核销金额)的和
         */
        BigDecimal daiXiaoKouKuan = BigDecimal.ZERO;
/*        List<FinBookVendorDeductionItem> bookItemList = bookVendorDeductionItemMapper.selectFinBookVendorDeductionItemList
                (new FinBookVendorDeductionItem().setClearStatusList(new String[]{ConstantsFinance.CLEAR_STATUS_WHX, ConstantsFinance.CLEAR_STATUS_BFHX})
                        .setVendorSid(finPayBill.getVendorSid()).setCompanySid(finPayBill.getCompanySid()));
        if (CollectionUtil.isNotEmpty(bookItemList)) {
            daiXiaoKouKuan = bookItemList.stream().map(FinBookVendorDeductionItem::getCurrencyAmountTaxDhx)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
        }*/
        // 2、找到所有“生效状态”为”未生效 “，且供应商与公司一致的核销供应商扣款日志，并计算出核销金额的和
        //待核销扣款金额 = 第一步找到的未核销金额和 – 第二步找到的核销金额和
//        List<FinClearLogVendorDeduction> logs = logVendorDeductionMapper.selectList(new QueryWrapper<FinClearLogVendorDeduction>()
//                .lambda().eq(FinClearLogVendorDeduction::getVendorSid, finPayBill.getVendorSid())
//                .eq(FinClearLogVendorDeduction::getCompanySid, finPayBill.getCompanySid())
//                .eq(FinClearLogVendorDeduction::getShengxiaoStatus, ConstantsFinance.SHENGXIAO_STATUS_WSX));
//        if (CollectionUtil.isNotEmpty(logs)) {
//            daiXiaoKouKuan = daiXiaoKouKuan.subtract(logs.stream().map(FinClearLogVendorDeduction::getCurrencyAmountTax)
//                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum));
//        }
//        finPayBill.setDaiXiaoKouKuan(daiXiaoKouKuan);
        /**
         * 待核销已预付款金额(元) 计算逻辑
         * 默认显示所有”核销状态“为”未核销“或”部分核销“，”流水来源类别“为”预付款“，
         * 且供应商与公司一致的付款流水的未核销金额(未核销金额=付款金额-已核销金额)的和
         */
        BigDecimal daiXiaoYiYuFuKuan = BigDecimal.ZERO;
/*        List<FinBookPaymentItem> receiptbookList = bookPaymentItemMapper.selectFinBookPaymentItemList
                (new FinBookPaymentItem().setClearStatusList(new String[]{ConstantsFinance.CLEAR_STATUS_WHX, ConstantsFinance.CLEAR_STATUS_BFHX})
                        .setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_YFK)
                        .setVendorSid(finPayBill.getVendorSid()).setCompanySid(finPayBill.getCompanySid()));
        if (CollectionUtil.isNotEmpty(receiptbookList)) {
            daiXiaoYiYuFuKuan = receiptbookList.stream().map(FinBookPaymentItem::getCurrencyAmountTaxDhx)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
        }*/
        // 2、找到所有“生效状态”为”未生效 “，且供应商与公司一致的核销供应商已预付款日志，并计算出核销金额的和
        //待核销已预付款金额 = 第一步找到的未核销金额和 – 第二步找到的核销金额和
//        List<FinClearLogAdvancePayment> advanceLogs = logAdvancePaymentMapper.selectList(new QueryWrapper<FinClearLogAdvancePayment>()
//                .lambda().eq(FinClearLogAdvancePayment::getVendorSid, finPayBill.getVendorSid())
//                .eq(FinClearLogAdvancePayment::getCompanySid, finPayBill.getCompanySid())
//                .eq(FinClearLogAdvancePayment::getShengxiaoStatus, ConstantsFinance.SHENGXIAO_STATUS_WSX));
//        if (CollectionUtil.isNotEmpty(advanceLogs)) {
//            daiXiaoYiYuFuKuan = daiXiaoYiYuFuKuan.subtract(advanceLogs.stream().map(FinClearLogAdvancePayment::getCurrencyAmountTax)
//                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum));
//        }
//        finPayBill.setDaiXiaoYiYuFuKuan(daiXiaoYiYuFuKuan);
    }

    /**
     * 计算本次核销货款  =本次实付金额+本次核销扣款+本次核销甲供料扣款金额+本次核销退货扣款+本次核销已预付款
     */
    public void sumTotalHuokuan(FinPayBill finPayBill) {
        BigDecimal heXiaoHuokuan = BigDecimal.ZERO;
        if (finPayBill.getHeXiaoHuokuan() != null) {
            return;
        }
        if (finPayBill.getCurrencyAmountTax() != null) {
            heXiaoHuokuan = heXiaoHuokuan.add(finPayBill.getCurrencyAmountTax());
        }
        if (finPayBill.getHeXiaoKouKuan() != null) {
            heXiaoHuokuan = heXiaoHuokuan.add(finPayBill.getHeXiaoKouKuan());
        }
        if (finPayBill.getHeXiaoJiagongliao() != null) {
            heXiaoHuokuan = heXiaoHuokuan.add(finPayBill.getHeXiaoJiagongliao());
        }
        if (finPayBill.getHeXiaoTuihuo() != null) {
            heXiaoHuokuan = heXiaoHuokuan.add(finPayBill.getHeXiaoTuihuo());
        }
        if (finPayBill.getHeXiaoYiyufu() != null) {
            heXiaoHuokuan = heXiaoHuokuan.add(finPayBill.getHeXiaoYiyufu());
        }
        finPayBill.setHeXiaoHuokuan(heXiaoHuokuan);
    }

    /**
     * 校验逻辑
     */
    public void judge(FinPayBill finPayBill, SysDefaultSettingClient settingClient) {
        // 计算明细
        countItem(finPayBill);
        // 校验金额填写
        judgeItemAmount(finPayBill, settingClient);
        // 计算基本信息的待核销数据
        if (ConstantsEms.SUBMIT_STATUS.equals(finPayBill.getHandleStatus()) ||
                ConstantsEms.CHECK_STATUS.equals(finPayBill.getHandleStatus())) {
            if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finPayBill.getBusinessType())
                    && CollectionUtil.isNotEmpty(finPayBill.getItemList())) {
                // 计算基本信息的待核销数据
                countBaseDai(finPayBill, settingClient);
                // 校验金额不能大于待
                judgeDai(finPayBill, settingClient);
            }
            //
            if (ConstantsEms.YES.equals(settingClient.getIsAttachRequiredFukuan())) {
                if (CollectionUtil.isEmpty(finPayBill.getAttachmentList())) {
                    throw new BaseException("附件不能为空！");
                }
            }
        }
    }

    /**
     * 校验明细的金额
     */
    public void judgeItemAmount(FinPayBill finPayBill, SysDefaultSettingClient settingClient) {
        // 校验明细
        judgeItem(finPayBill);
        // 校验外发加工费结算单明细
        judgeSettle(finPayBill);
        // 校验部分明细金额不能为0
        judgeNotNull(finPayBill);
        // 校验发票明细
        judgeInvoice(finPayBill);
        // 校验预收
        judgeYu(finPayBill);
        // 校验扣款
        judgeKoukuan(finPayBill);
    }

    /**
     * 校验付款明细
     */
    public void judgeItem(FinPayBill finPayBill) {
        if (CollectionUtil.isEmpty(finPayBill.getItemList())) {
            // 货款
            if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finPayBill.getBusinessType())) {
                throw new BaseException("本次实付明细不能为空！");
            }
            else {
                throw new BaseException("付款明细不能为空！");
            }
        }
        else {
            if (finPayBill.getItemList().stream().anyMatch(i->i.getCurrencyAmountTax()==null
                    || BigDecimal.ZERO.compareTo(i.getCurrencyAmountTax()) == 0)) {
                if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finPayBill.getBusinessType())) {
                    throw new BaseException("本次实付明细金额不能为空且不能填0！");
                }
                else {
                    throw new BaseException("付款明细金额不能为空且不能填0！");
                }
            }
            if (ConstantsFinance.PAY_BILL_BUSINESS_TYPE_YFK.equals(finPayBill.getBusinessType())) {
                // 若“业务类型”为“预收款”，明细的下单季必须一致，否则报错：
                List<FinPayBillItem> productSeasonList = finPayBill.getItemList().stream()
                        .filter(o->o.getProductSeasonSid() != null).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(productSeasonList)) {
                    Map<Long, List<FinPayBillItem>> map = productSeasonList.stream()
                            .collect(Collectors.groupingBy(FinPayBillItem::getProductSeasonSid));
                    if (productSeasonList.size() != finPayBill.getItemList().size() || map.size() > 1) {
                        throw new BaseException("预付款明细的下单季必须一致！");
                    }
                }
            }
        }
    }

    /**
     * 校验外发加工费结算单明细
     */
    public void judgeSettle(FinPayBill finPayBill) {
        if (CollectionUtil.isNotEmpty(finPayBill.getOutsourceSettleList())) {
            if (finPayBill.getOutsourceSettleList().stream().anyMatch(i->i.getCurrencyAmountTax()==null
                    || BigDecimal.ZERO.compareTo(i.getCurrencyAmountTax()) == 0)) {
                throw new BaseException("本次核销外发加工费结算单明细的金额不能为空且不能填0！");
            }
            // 点击暂存或提交按钮时，若此页签存在明细行的金额大于未支付金额，提示：本次核销外发加工费结算单明细的金额不能大于未支付金额！
            for (FinPayBillItemOutsourceSettle settle : finPayBill.getOutsourceSettleList()) {
                if (settle.getCurrencyAmountTax() != null
                        && settle.getCurrencyAmountTax().compareTo(settle.getCurrencyAmountTaxWzf()) > 0) {
                    throw new BaseException("本次核销外发加工费结算单明细" + settle.getManufactureOutsourceSettleCode() + "的金额不能大于未支付金额");
                }
            }
        }
    }

    /**
     * 校验部分明细金额不能为0
     */
    public void judgeNotNull(FinPayBill finPayBill) {
        if (CollectionUtil.isNotEmpty(finPayBill.getHuokuanList())) {
            if (finPayBill.getHuokuanList().stream().anyMatch(i->i.getCurrencyAmountTax()==null
                    || BigDecimal.ZERO.compareTo(i.getCurrencyAmountTax()) == 0)) {
                throw new BaseException("本次核销货款明细金额不能为空且不能填0！");
            }
        }
        if (CollectionUtil.isNotEmpty(finPayBill.getYufuList())) {
            if (finPayBill.getYufuList().stream().anyMatch(i->i.getCurrencyAmountTax()==null
                    || BigDecimal.ZERO.compareTo(i.getCurrencyAmountTax()) == 0)) {
                throw new BaseException("本次核销已预付款金额不能为空且不能填0！");
            }
        }
        if (CollectionUtil.isNotEmpty(finPayBill.getTuihuoList())) {
            if (finPayBill.getTuihuoList().stream().anyMatch(i->i.getCurrencyAmountTax()==null
                    || BigDecimal.ZERO.compareTo(i.getCurrencyAmountTax()) == 0)) {
                throw new BaseException("本次核销退货扣款金额不能为空且不能填0！");
            }
        }
        if (CollectionUtil.isNotEmpty(finPayBill.getKoukuanList())) {
            if (finPayBill.getKoukuanList().stream().anyMatch(i->i.getCurrencyAmountTax()==null
                    || BigDecimal.ZERO.compareTo(i.getCurrencyAmountTax()) == 0)) {
                throw new BaseException("本次核销扣款金额不能为空且不能填0！");
            }
        }
        if (CollectionUtil.isNotEmpty(finPayBill.getJiagongliaoList())) {
            if (finPayBill.getJiagongliaoList().stream().anyMatch(i->i.getCurrencyAmountTax()==null
                    || BigDecimal.ZERO.compareTo(i.getCurrencyAmountTax()) == 0)) {
                throw new BaseException("本次核销甲供料扣款金额不能为空且不能填0！");
            }
        }

    }

    /**
     * 校验预收
     */
    public void judgeYu(FinPayBill finPayBill) {
        if (CollectionUtil.isEmpty(finPayBill.getYufuList())) {
            return;
        }
        if (ConstantsEms.SUBMIT_STATUS.equals(finPayBill.getHandleStatus()) ||
                ConstantsEms.CHECK_STATUS.equals(finPayBill.getHandleStatus())) {
            /**
             * 【仅提交时校验】若本次核销已预收款有明细，则明细的金额不能大于对应明细的待核销金额，
             * 提示：客户已预收款单号XXX的金额不能大于待核销金额！
             */
            for (FinPayBillItemYufu yushou : finPayBill.getYufuList()) {
                if (yushou.getCurrencyAmountTax() != null && yushou.getCurrencyAmountTax() != null
                        && yushou.getCurrencyAmountTax().compareTo(yushou.getCurrencyAmountTaxDhx()) > 0) {
                    throw new BaseException("供应商已预付款单号 " + yushou.getReferDocCode() + " 的金额不能大于待核销金额！");
                }
            }
        }
    }

    /**
     * 校验扣款
     */
    public void judgeKoukuan(FinPayBill finPayBill) {
        if (CollectionUtil.isEmpty(finPayBill.getKoukuanList())) {
            return;
        }
        if (ConstantsEms.SUBMIT_STATUS.equals(finPayBill.getHandleStatus()) ||
                ConstantsEms.CHECK_STATUS.equals(finPayBill.getHandleStatus())) {
            /**
             * 【仅提交时校验】若本次核销扣款有明细，则明细的金额不能大于对应明细的待核销金额，
             * 提示：客户扣款单号XXX的金额不能大于待核销金额！
             */
            for (FinPayBillItemKoukuan koukuan : finPayBill.getKoukuanList()) {
                if (koukuan.getCurrencyAmountTax() != null && koukuan.getCurrencyAmountTax() != null
                        && koukuan.getCurrencyAmountTax().compareTo(koukuan.getCurrencyAmountTaxDhx()) > 0) {
                    throw new BaseException("供应商扣款单号 " + koukuan.getReferDocCode() + " 的金额不能大于待核销金额！");
                }
            }
        }
    }

    /**
     * 校验发票明细
     */
    public void judgeInvoice(FinPayBill finPayBill) {
        if (CollectionUtil.isNotEmpty(finPayBill.getInvoiceList())) {
            if (finPayBill.getInvoiceList().stream().anyMatch(i -> i.getCurrencyAmountTax()==null
                    || BigDecimal.ZERO.compareTo(i.getCurrencyAmountTax()) == 0)) {
                throw new BaseException("本次核销发票台账的金额(含税)不能为空且不能填0！");
            }
        }
        if (ConstantsEms.YES.equals(finPayBill.getIsYoupiao())) {
//            if (CollectionUtil.isEmpty(finPayBill.getInvoiceList())) {
//                throw new BaseException("本次核销发票台账明细不能为空！");
//            }
            // 若是否有票为“是”，且本次核销发票台账明细不为空，进行如下校验：
            if (ConstantsEms.SUBMIT_STATUS.equals(finPayBill.getHandleStatus()) ||
                    ConstantsEms.CHECK_STATUS.equals(finPayBill.getHandleStatus())) {
                /**
                 * 若本次核销发票台账明细的金额(含税)大于此明细的待付款金额(元)，
                 * 提示：本次核销发票台账XXXXX不能大于待付款金额！其中，XXXX为供应商发票台账号
                 */
                for (FinPayBillItemInvoice invoice : finPayBill.getInvoiceList()) {
                    if (invoice.getCurrencyAmountTaxDai() != null && invoice.getCurrencyAmountTax() != null
                            && invoice.getCurrencyAmountTax().compareTo(invoice.getCurrencyAmountTaxDai()) > 0) {
                        throw new BaseException("本次核销发票台账 " + invoice.getVendorInvoiceRecordCode() + " 的金额不能大于待付款金额！");
                    }
                }
            }
        }
    }

    /**
     * 校验金额不能大于待
     */
    public void judgeDai(FinPayBill finPayBill, SysDefaultSettingClient settingClient) {
        // 》若业务类型为“货款“，且实付明细不为空，进行如下校验：
        if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finPayBill.getBusinessType())) {
            /*
             /**
             * 若本次核销扣款明细的金额(含税)之和大于待核销扣款金额(元)，提示：本次核销扣款明细和不能大于待核销扣款！
             *//*
            // 本次核销扣款明细的金额(含税) 有值再校验
            if (finPayBill.getHeXiaoKouKuan() != null) {
                *//**
             * 待核销扣款金额(元) 计算逻辑
             * 默认显示所有”核销状态“为”未核销“或”部分核销“，
             * 且供应商与公司一致的供应商扣款流水的未核销金额(未核销金额=扣款金额-核销中金额-已核销金额)的和
             *//*
                BigDecimal daiXiaoKouKuan = finPayBill.getDaiXiaoKouKuan();
                if (daiXiaoKouKuan != null && finPayBill.getHeXiaoKouKuan().compareTo(daiXiaoKouKuan) > 0) {
                    throw new BaseException("本次核销扣款明细和不能大于待核销扣款！");
                }
            }
            *//**
             * 若本次核销已预付款明细的金额(含税)之和大于待核销已预付款金额(元)，提示：本次核销已预付款明细和不能大于待核销已预付款！
             *//*
            if (finPayBill.getHeXiaoYiyufu() != null) {
                *//**
             * 待核销已预付款金额(元) 计算逻辑
             * 默认显示所有”核销状态“为”未核销“或”部分核销“，”流水来源类别“为”预付款“，
             * 且供应商与公司一致的付款流水的未核销金额(未核销金额=付款金额-核销中金额-已核销金额)的和
             *//*
                BigDecimal daiXiaoYiYuFuKuan = finPayBill.getDaiXiaoYiYuFuKuan();
                if (daiXiaoYiYuFuKuan != null && finPayBill.getHeXiaoYiyufu().compareTo(daiXiaoYiYuFuKuan) > 0) {
                    throw new BaseException("本次核销已预付款明细和不能大于待核销已预付款！");
                }
            }
            */
            /**
             * •若系统默认设置的“付款核销方式”为“应付暂估”或“应付”，若本次核销货款大于待付货款金额(元)，提示：本次核销货款不能大于待付货款！
             * •若系统默认设置的“付款核销方式”为“应付暂估”或“应付”，若本次核销退货扣款明细的金额(含税)之和大于待核销退货扣款金额(元) (该字段取绝对值数据)，
             *  提示：本次核销退货扣款明细和不能大于待核销退货扣款！
             */
            if (settingClient != null && (ConstantsFinance.SHOUKUAN_ACC_CLEAR_WAY_YFZG.equals(settingClient.getFukuanAccountClearWay())
                    || ConstantsFinance.SHOUKUAN_ACC_CLEAR_WAY_YF.equals(settingClient.getFukuanAccountClearWay()))) {
                // 本次核销退货扣款明细的金额(含税)有值再校验
                if (finPayBill.getHeXiaoTuihuo() != null) {
                    BigDecimal daiXiaoTuiHuoKouKuan = finPayBill.getDaiXiaoTuiHuoKouKuan();
                    if (daiXiaoTuiHuoKouKuan != null && finPayBill.getHeXiaoTuihuo().compareTo(daiXiaoTuiHuoKouKuan) > 0) {
                        throw new BaseException("本次核销退货扣款明细和不能大于待核销退货扣款！");
                    }
                }
                // 本次核销货款有值再校验
                if (finPayBill.getHeXiaoHuokuan() != null) {
                    BigDecimal daiFuHuoKuan = finPayBill.getDaiFuHuoKuan();
                    if (daiFuHuoKuan != null && finPayBill.getHeXiaoHuokuan().compareTo(daiFuHuoKuan) > 0) {
                        throw new BaseException("本次核销货款不能大于待付货款！");
                    }
                }
            }
        }
    }

    /**
     * 默认值
     */
    public void setData(FinPayBill finPayBill) {
        finPayBill.setIsFinanceVerify(ConstantsEms.NO);
        //
        finPayBill.setCompanyCode(null);
        if (finPayBill.getCompanySid() != null) {
            BasCompany company = companyMapper.selectById(finPayBill.getCompanySid());
            if (company != null) {
                finPayBill.setCompanyCode(company.getCompanyCode());
            }
        }
        finPayBill.setVendorCode(null);
        if (finPayBill.getVendorSid() != null) {
            BasVendor vendor = vendorMapper.selectById(finPayBill.getVendorSid());
            if (vendor != null) {
                finPayBill.setVendorCode(String.valueOf(vendor.getVendorCode()));
            }
        }
        if (ConstantsFinance.PAY_BILL_BUSINESS_TYPE_YFK.equals(finPayBill.getBusinessType())) {
            if (CollectionUtil.isNotEmpty(finPayBill.getItemList())) {
                finPayBill.setProductSeasonSid(finPayBill.getItemList().get(0).getProductSeasonSid());
            }
        }
    }

    /**
     * 新增付款单
     * 需要注意编码重复校验
     *
     * @param finPayBill 付款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinPayBill(FinPayBill finPayBill) {
        SysDefaultSettingClient settingClient = getClientSetting();
        // 校验
        judge(finPayBill, settingClient);
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finPayBill.getHandleStatus())) {
            if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowFk())) {
                // 不需要走审批，提交及确认
                finPayBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(finPayBill.getHandleStatus())) {
            finPayBill.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        setData(finPayBill);
        int row = finPayBillMapper.insert(finPayBill);
        if (row > 0) {
            // 回写编码
            FinPayBill bill = finPayBillMapper.selectById(finPayBill.getPayBillSid());
            finPayBill.setPayBillCode(bill.getPayBillCode());
            // 写入明细
            if (CollectionUtil.isNotEmpty(finPayBill.getItemList())) {
                billItemService.insertByList(finPayBill);
            }
            // 写入外发加工费结算明细
            if (CollectionUtil.isNotEmpty(finPayBill.getOutsourceSettleList())) {
                outsourceSettleService.insertByList(finPayBill);
            }
            // 写入发票明细
            if (CollectionUtil.isNotEmpty(finPayBill.getInvoiceList())) {
                invoiceService.insertByList(finPayBill);
            }
            // 货款页签 / 预付款页签
            if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finPayBill.getBusinessType())
                    || ConstantsFinance.PAY_BILL_BUSINESS_TYPE_YFK.equals(finPayBill.getBusinessType())) {
                // 写入本次核销已预付款明细列表
                if (CollectionUtil.isNotEmpty(finPayBill.getYufuList())) {
                    yufuService.insertByList(finPayBill);
                }
                // 货款
                if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finPayBill.getBusinessType())) {
                    // 写入本次核销货款明细列表
                    if (CollectionUtil.isNotEmpty(finPayBill.getHuokuanList())) {
                        huokuanService.insertByList(finPayBill);
                    }
                    // 写入本次核销扣款明细列表
                    if (CollectionUtil.isNotEmpty(finPayBill.getKoukuanList())) {
                        koukuanService.insertByList(finPayBill);
                    }
                    // 写入本次核销甲供料扣款明细列表
                    if (CollectionUtil.isNotEmpty(finPayBill.getJiagongliaoList())) {
                        jiagongliaoService.insertByList(finPayBill);
                    }
                    // 写入本次核销退货扣款明细列表
                    if (CollectionUtil.isNotEmpty(finPayBill.getTuihuoList())) {
                        tuihuoService.insertByList(finPayBill);
                    }
                }
            }
            // 附件清单
            if (CollectionUtil.isNotEmpty(finPayBill.getAttachmentList())) {
                finPayBill.getAttachmentList().forEach(item->{
                    item.setClientId(ApiThreadLocalUtil.get().getSysUser().getClientId())
                            .setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                            .setPayBillSid(finPayBill.getPayBillSid());
                });
                attachmentMapper.inserts(finPayBill.getAttachmentList());
            }
            // 待办
            if (ConstantsEms.SAVA_STATUS.equals(finPayBill.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_FIN_PAY_BILL)
                        .setDocumentSid(finPayBill.getPayBillSid());
                sysTodoTask.setTitle("付款单" + finPayBill.getPayBillCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(finPayBill.getPayBillCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskService.insertSysTodoTaskMenu(sysTodoTask, ConstantsWorkbench.TODO_FIN_PAY_BILL);
            }
            if (ConstantsEms.SUBMIT_STATUS.equals(finPayBill.getHandleStatus())
                    || ConstantsEms.CHECK_STATUS.equals(finPayBill.getHandleStatus())) {
                // 更新流水
                updateBookItem(finPayBill, settingClient);
                if (ConstantsEms.CHECK_STATUS.equals(finPayBill.getHandleStatus())) {
                    updateNewDateBookItem(finPayBill);
                }
                // 走提交审批，参数从查询页面提交按钮参考
                else if (ConstantsEms.SUBMIT_STATUS.equals(finPayBill.getHandleStatus())) {
                    this.submit(finPayBill);
                }
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinPayBill(), finPayBill);
            MongodbDeal.insert(finPayBill.getPayBillSid(), finPayBill.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 提交更新流水
     */
    private void updateBookItem(FinPayBill finPayBill, SysDefaultSettingClient settingClient) {
        if (finPayBill == null || settingClient == null) {
            return;
        }
        //若业务类型为“货款“，更新下述流水的数据如下：
        if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finPayBill.getBusinessType())) {
            if (ConstantsFinance.SHOUKUAN_ACC_CLEAR_WAY_YFZG.equals(settingClient.getFukuanAccountClearWay())) {

                // 若系统默认设置的“付款核销模式“为”应付暂估“，按照创建日期(从最早的开始)，找到”核销状态“为”未核销“或”部分核销“，
                // 供应商与公司一致 且 未被处理状态不是“已确认”的应付暂估调价量单 引用 的 应付暂估流水
                List<FinBookPaymentEstimationItem> bookItemList = bookPaymentEstimationItemMapper.paySubmitSelect(new FinBookPaymentEstimationItem()
                        .setVendorSid(finPayBill.getVendorSid()).setCompanySid(finPayBill.getCompanySid()));
                if (CollectionUtil.isNotEmpty(bookItemList)) {
                    bookItemList = bookItemList.stream().filter(i->i.getClearStatus().equals(ConstantsFinance.CLEAR_STATUS_WHX)
                            || i.getClearStatus().equals(ConstantsFinance.CLEAR_STATUS_BFHX)).collect(Collectors.toList());
                }
                if (CollectionUtil.isNotEmpty(bookItemList)) {
                    // 本次核销货款 必须要有值
                    // 更新对应待核销金额为核销中金额并根据核销情况更新核销状态
                    // 若系统默认设置的“付款核销模式“为”应付“，暂不实现
                    // 若系统默认设置的“付款核销模式“为“无”，无需更新数据
                    if (finPayBill.getHeXiaoHuokuan() != null && BigDecimal.ZERO.compareTo(finPayBill.getHeXiaoHuokuan()) != 0) {
                        BigDecimal heXiaoHuokuan = finPayBill.getHeXiaoHuokuan();
                        // ”是否退货“为”否“
                        List<FinBookPaymentEstimationItem> noTuihuo = bookItemList.stream().filter(i->ConstantsEms.NO.equals(i.getIsTuihuo())).collect(Collectors.toList());
                        for (FinBookPaymentEstimationItem bookItem : noTuihuo) {
                            if (bookItem.getCurrencyAmountTaxLeft() != null && BigDecimal.ZERO.compareTo(bookItem.getCurrencyAmountTaxLeft()) != 0) {
                                // 如果待核销的 比 本次核销货款 多 ，则将货款全部核销到该笔
                                if (bookItem.getCurrencyAmountTaxLeft().abs().compareTo(heXiaoHuokuan.abs()) >= 0) {
                                    // 记录到日志表里
                                    FinClearLogPaymentEstimation logBook = new FinClearLogPaymentEstimation();
                                    logBook.setPayBillSid(finPayBill.getPayBillSid())
                                            .setPayBillCode(finPayBill.getPayBillCode())
                                            .setVendorSid(finPayBill.getVendorSid()).setVendorCode(finPayBill.getVendorCode())
                                            .setCompanySid(finPayBill.getCompanySid()).setCompanyCode(finPayBill.getCompanyCode())
                                            .setBookPaymentEstimationSid(bookItem.getBookPaymentEstimationSid())
                                            .setBookPaymentEstimationCode(bookItem.getBookPaymentEstimationCode())
                                            .setBookPaymentEstimationItemSid(bookItem.getBookPaymentEstimationItemSid())
                                            .setIsTuihuo(bookItem.getIsTuihuo()).setShengxiaoStatus(ConstantsFinance.SHENGXIAO_STATUS_WSX)
                                            .setCurrencyAmountTax(heXiaoHuokuan);
                                    logPaymentEstimationMapper.insert(logBook);
                                    break;
                                }
                                else {
                                    // 如果待核销的 比 本次核销货款 少 ，则将该笔 待核销 全部 核销到 核销中
                                    // 记录到日志表里
                                    FinClearLogPaymentEstimation logBook = new FinClearLogPaymentEstimation();
                                    logBook.setPayBillSid(finPayBill.getPayBillSid())
                                            .setPayBillCode(finPayBill.getPayBillCode())
                                            .setVendorSid(finPayBill.getVendorSid()).setVendorCode(finPayBill.getVendorCode())
                                            .setCompanySid(finPayBill.getCompanySid()).setCompanyCode(finPayBill.getCompanyCode())
                                            .setBookPaymentEstimationSid(bookItem.getBookPaymentEstimationSid())
                                            .setBookPaymentEstimationCode(bookItem.getBookPaymentEstimationCode())
                                            .setBookPaymentEstimationItemSid(bookItem.getBookPaymentEstimationItemSid())
                                            .setIsTuihuo(bookItem.getIsTuihuo()).setShengxiaoStatus(ConstantsFinance.SHENGXIAO_STATUS_WSX)
                                            .setCurrencyAmountTax(bookItem.getCurrencyAmountTaxLeft());
                                    logPaymentEstimationMapper.insert(logBook);
                                    heXiaoHuokuan = heXiaoHuokuan.subtract(bookItem.getCurrencyAmountTaxLeft());
                                }
                            }
                        }
                    }

                    // 本次核销退货扣款 必须要有值
                    // 根据 绝对值 更新对应待核销金额为核销中金额并根据核销情况更新核销状态
                    // 若系统默认设置的“付款核销模式“为”应付“，暂不实现
                    // 若系统默认设置的“付款核销模式“为“无”，无需更新数据
                    if (finPayBill.getHeXiaoTuihuo() != null && BigDecimal.ZERO.compareTo(finPayBill.getHeXiaoTuihuo()) != 0) {
                        BigDecimal heXiaoTuihuo = finPayBill.getHeXiaoTuihuo();
                        // ”是否退货“为”是“，
                        List<FinBookPaymentEstimationItem> yesTuihuo = bookItemList.stream().filter(i->ConstantsEms.YES.equals(i.getIsTuihuo())).collect(Collectors.toList());
                        // 注意：本次核销退货扣款为正数，请转换为负数再进行计算
                        BigDecimal heXiaoTuihuoFushu = heXiaoTuihuo.add(BigDecimal.ZERO);
                        if (heXiaoTuihuoFushu.compareTo(BigDecimal.ZERO) > 0) {
                            heXiaoTuihuoFushu = heXiaoTuihuoFushu.multiply(new BigDecimal("-1"));
                        }
                        for (FinBookPaymentEstimationItem bookItem : yesTuihuo) {
                            if (bookItem.getCurrencyAmountTaxLeft() != null && BigDecimal.ZERO.compareTo(bookItem.getCurrencyAmountTaxLeft()) != 0) {
                                // 如果待核销的 比 本次核销货款 多 ，则将货款全部核销到该笔
                                if (bookItem.getCurrencyAmountTaxLeft().abs().compareTo(heXiaoTuihuoFushu.abs()) >= 0) {
                                    // 记录到日志表
                                    FinClearLogPaymentEstimation logBook = new FinClearLogPaymentEstimation();
                                    logBook.setPayBillSid(finPayBill.getPayBillSid())
                                            .setPayBillCode(finPayBill.getPayBillCode())
                                            .setVendorSid(finPayBill.getVendorSid()).setVendorCode(finPayBill.getVendorCode())
                                            .setCompanySid(finPayBill.getCompanySid()).setCompanyCode(finPayBill.getCompanyCode())
                                            .setBookPaymentEstimationSid(bookItem.getBookPaymentEstimationSid())
                                            .setBookPaymentEstimationCode(bookItem.getBookPaymentEstimationCode())
                                            .setBookPaymentEstimationItemSid(bookItem.getBookPaymentEstimationItemSid())
                                            .setIsTuihuo(bookItem.getIsTuihuo()).setShengxiaoStatus(ConstantsFinance.SHENGXIAO_STATUS_WSX)
                                            .setCurrencyAmountTax(heXiaoTuihuoFushu);
                                    logPaymentEstimationMapper.insert(logBook);
                                    break;
                                }
                                else {
                                    // 如果待核销的 比 本次核销货款 少 ，则将该笔 待核销 全部 核销到 核销中
                                    // 记录到日志表
                                    FinClearLogPaymentEstimation logBook = new FinClearLogPaymentEstimation();
                                    logBook.setPayBillSid(finPayBill.getPayBillSid())
                                            .setPayBillCode(finPayBill.getPayBillCode())
                                            .setVendorSid(finPayBill.getVendorSid()).setVendorCode(finPayBill.getVendorCode())
                                            .setCompanySid(finPayBill.getCompanySid()).setCompanyCode(finPayBill.getCompanyCode())
                                            .setBookPaymentEstimationSid(bookItem.getBookPaymentEstimationSid())
                                            .setBookPaymentEstimationCode(bookItem.getBookPaymentEstimationCode())
                                            .setBookPaymentEstimationItemSid(bookItem.getBookPaymentEstimationItemSid())
                                            .setIsTuihuo(bookItem.getIsTuihuo()).setShengxiaoStatus(ConstantsFinance.SHENGXIAO_STATUS_WSX)
                                            .setCurrencyAmountTax(bookItem.getCurrencyAmountTaxLeft());
                                    logPaymentEstimationMapper.insert(logBook);
                                    heXiaoTuihuoFushu = heXiaoTuihuoFushu.subtract(bookItem.getCurrencyAmountTaxLeft());
                                }
                            }
                        }
                    }
                }
            }

/*            // 本次核销扣款
            // 按照创建日期(从最早的开始)，找到”核销状态“为”未核销“或”部分核销“，且供应商与公司一致的供应商扣款流水，
            // 更新对应待核销金额为核销中金额并根据核销情况更新核销状态
            BigDecimal heXiaoKouKuan = finPayBill.getHeXiaoKouKuan();
            if (heXiaoKouKuan != null && BigDecimal.ZERO.compareTo(heXiaoKouKuan) != 0) {
                List<FinBookVendorDeductionItem> bookKoukuanList = bookVendorDeductionItemMapper.paySubmitSelect(new FinBookVendorDeductionItem()
                        .setVendorSid(finPayBill.getVendorSid()).setCompanySid(finPayBill.getCompanySid()));
                if (CollectionUtil.isNotEmpty(bookKoukuanList)) {
                    bookKoukuanList = bookKoukuanList.stream().filter(i->i.getClearStatus().equals(ConstantsFinance.CLEAR_STATUS_WHX)
                            || i.getClearStatus().equals(ConstantsFinance.CLEAR_STATUS_BFHX)).collect(Collectors.toList());
                }
                if (CollectionUtil.isNotEmpty(bookKoukuanList)) {
                    for (FinBookVendorDeductionItem bookItem : bookKoukuanList) {
                        if (bookItem.getCurrencyAmountTaxDhx() != null && BigDecimal.ZERO.compareTo(bookItem.getCurrencyAmountTaxDhx()) != 0) {
                            // 如果待核销的 比 本次核销货款 多 ，则将货款全部核销到该笔
                            if (bookItem.getCurrencyAmountTaxDhx().compareTo(heXiaoKouKuan) >= 0) {
                                FinClearLogVendorDeduction logBook = new FinClearLogVendorDeduction();
                                logBook.setPayBillSid(finPayBill.getPayBillSid())
                                        .setPayBillCode(finPayBill.getPayBillCode())
                                        .setVendorSid(finPayBill.getVendorSid()).setVendorCode(finPayBill.getVendorCode())
                                        .setCompanySid(finPayBill.getCompanySid()).setCompanyCode(finPayBill.getCompanyCode())
                                        .setBookDeductionSid(bookItem.getBookDeductionSid())
                                        .setBookDeductionCode(bookItem.getBookDeductionCode())
                                        .setBookDeductionItemSid(bookItem.getBookDeductionItemSid())
                                        .setShengxiaoStatus(ConstantsFinance.SHENGXIAO_STATUS_WSX)
                                        .setCurrencyAmountTax(heXiaoKouKuan);
                                logVendorDeductionMapper.insert(logBook);
                                break;
                            }
                            else {
                                // 如果待核销的 比 本次核销货款 少 ，则将该笔 待核销 全部 核销到 核销中
                                FinClearLogVendorDeduction logBook = new FinClearLogVendorDeduction();
                                logBook.setPayBillSid(finPayBill.getPayBillSid())
                                        .setPayBillCode(finPayBill.getPayBillCode())
                                        .setVendorSid(finPayBill.getVendorSid()).setVendorCode(finPayBill.getVendorCode())
                                        .setCompanySid(finPayBill.getCompanySid()).setCompanyCode(finPayBill.getCompanyCode())
                                        .setBookDeductionSid(bookItem.getBookDeductionSid())
                                        .setBookDeductionCode(bookItem.getBookDeductionCode())
                                        .setBookDeductionItemSid(bookItem.getBookDeductionItemSid())
                                        .setShengxiaoStatus(ConstantsFinance.SHENGXIAO_STATUS_WSX)
                                        .setCurrencyAmountTax(bookItem.getCurrencyAmountTaxDhx());
                                logVendorDeductionMapper.insert(logBook);
                                heXiaoKouKuan = heXiaoKouKuan.subtract(bookItem.getCurrencyAmountTaxDhx());
                            }
                        }
                    }
                }
            }

            // 本次核销已预付款
            // 按照创建日期(从最早的开始)，找到”核销状态“为”未核销“或”部分核销“，”流水来源类别“为”预付款“，
            // 且供应商与公司一致的付款流水，更新对应待核销金额为核销中金额并根据核销情况更新核销状态
            BigDecimal heXiaoYiyufu = finPayBill.getHeXiaoYiyufu();
            if (heXiaoYiyufu != null && BigDecimal.ZERO.compareTo(heXiaoYiyufu) != 0) {
                List<FinBookPaymentItem> bookItemList = bookPaymentItemMapper.paySubmitSelect(new FinBookPaymentItem()
                        .setVendorSid(finPayBill.getVendorSid()).setCompanySid(finPayBill.getCompanySid()));
                if (CollectionUtil.isNotEmpty(bookItemList)) {
                    bookItemList = bookItemList.stream().filter(i->i.getClearStatus().equals(ConstantsFinance.CLEAR_STATUS_WHX)
                            || i.getClearStatus().equals(ConstantsFinance.CLEAR_STATUS_BFHX)).collect(Collectors.toList());
                }
                if (CollectionUtil.isNotEmpty(bookItemList)) {
                    for (FinBookPaymentItem bookItem : bookItemList) {
                        if (bookItem.getCurrencyAmountTaxDhx() != null && BigDecimal.ZERO.compareTo(bookItem.getCurrencyAmountTaxDhx()) != 0) {
                            // 如果待核销的 比 本次核销货款 多 ，则将货款全部核销到该笔
                            if (bookItem.getCurrencyAmountTaxDhx().compareTo(heXiaoYiyufu) >= 0) {
                                FinClearLogAdvancePayment logBook = new FinClearLogAdvancePayment();
                                logBook.setPayBillSid(finPayBill.getPayBillSid())
                                        .setPayBillCode(finPayBill.getPayBillCode())
                                        .setVendorSid(finPayBill.getVendorSid()).setVendorCode(finPayBill.getVendorCode())
                                        .setCompanySid(finPayBill.getCompanySid()).setCompanyCode(finPayBill.getCompanyCode())
                                        .setBookPaymentSid(bookItem.getBookPaymentSid())
                                        .setBookPaymentCode(bookItem.getBookPaymentCode())
                                        .setBookPaymentItemSid(bookItem.getBookPaymentItemSid())
                                        .setShengxiaoStatus(ConstantsFinance.SHENGXIAO_STATUS_WSX)
                                        .setCurrencyAmountTax(heXiaoYiyufu);
                                logAdvancePaymentMapper.insert(logBook);
                                break;
                            }
                            else {
                                // 如果待核销的 比 本次核销货款 少 ，则将该笔 待核销 全部 核销到 核销中
                                FinClearLogAdvancePayment logBook = new FinClearLogAdvancePayment();
                                logBook.setPayBillSid(finPayBill.getPayBillSid())
                                        .setPayBillCode(finPayBill.getPayBillCode())
                                        .setVendorSid(finPayBill.getVendorSid()).setVendorCode(finPayBill.getVendorCode())
                                        .setCompanySid(finPayBill.getCompanySid()).setCompanyCode(finPayBill.getCompanyCode())
                                        .setBookPaymentSid(bookItem.getBookPaymentSid())
                                        .setBookPaymentCode(bookItem.getBookPaymentCode())
                                        .setBookPaymentItemSid(bookItem.getBookPaymentItemSid())
                                        .setShengxiaoStatus(ConstantsFinance.SHENGXIAO_STATUS_WSX)
                                        .setCurrencyAmountTax(bookItem.getCurrencyAmountTaxDhx());
                                logAdvancePaymentMapper.insert(logBook);
                                heXiaoYiyufu = heXiaoYiyufu.subtract(bookItem.getCurrencyAmountTaxDhx());
                            }
                        }
                    }
                }
            }*/
        }
    }

    /**
     * 释放已经更新的流水
     */
    private void returnBookItem(FinPayBill old, SysDefaultSettingClient settingClient) {
        if (old == null || settingClient == null) {
            return;
        }
        //若业务类型为“货款“，更新下述流水的数据如下：
        if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(old.getBusinessType())) {
            if (ConstantsFinance.SHOUKUAN_ACC_CLEAR_WAY_YFZG.equals(settingClient.getFukuanAccountClearWay())) {
                logPaymentEstimationMapper.delete(new QueryWrapper<FinClearLogPaymentEstimation>().lambda()
                        .eq(FinClearLogPaymentEstimation::getPayBillSid, old.getPayBillSid()));
/*                logVendorDeductionMapper.delete(new QueryWrapper<FinClearLogVendorDeduction>().lambda()
                        .eq(FinClearLogVendorDeduction::getPayBillSid, old.getPayBillSid()));
                logAdvancePaymentMapper.delete(new QueryWrapper<FinClearLogAdvancePayment>().lambda()
                        .eq(FinClearLogAdvancePayment::getPayBillSid, old.getPayBillSid()));*/
            }
        }
    }

    /**
     * 提交
     */
    private void submit(FinPayBill bill){
        Map<String, Object> variables = new HashMap<>();
        variables.put("formCode", bill.getPayBillCode());
        variables.put("formId", bill.getPayBillSid());
        variables.put("formType", FormType.PayBill.getCode());
        variables.put("startUserId", ApiThreadLocalUtil.get().getSysUser().getUserId());
        try {
            AjaxResult result = workflowService.submitOnly(variables);
        } catch (BaseException e) {
            throw e;
        }
    }

    /**
     * 批量修改附件信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAttach(FinPayBill bill) {
        // 先删后加
        attachmentMapper.delete(new QueryWrapper<FinPayBillAttachment>().lambda()
                .eq(FinPayBillAttachment::getPayBillSid, bill.getPayBillSid()));
        if (CollectionUtil.isNotEmpty(bill.getAttachmentList())) {
            bill.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getPayBillSid() == null) {
                    att.setClientId(ApiThreadLocalUtil.get().getSysUser().getClientId())
                            .setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                    att.setPayBillSid(bill.getPayBillSid());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            attachmentMapper.inserts(bill.getAttachmentList());
        }
    }

    /**
     * 修改付款单
     *
     * @param finPayBill 付款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinPayBill(FinPayBill finPayBill) {
        SysDefaultSettingClient settingClient = getClientSetting();
        // 校验
        judge(finPayBill, settingClient);
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finPayBill.getHandleStatus())) {
            if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowFk())) {
                // 不需要走审批，提交及确认
                finPayBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        FinPayBill original = this.selectFinPayBillById(finPayBill.getPayBillSid());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(finPayBill.getHandleStatus())) {
            finPayBill.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        setData(finPayBill);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, finPayBill);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finPayBill.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finPayBillMapper.updateAllById(finPayBill);
        if (row > 0) {
            // 写入明细
            billItemService.updateByList(finPayBill);
            // 写入外发加工费结算明细
            outsourceSettleService.updateByList(finPayBill);
            // 写入发票明细
            invoiceService.updateByList(finPayBill);
            // 货款页签 / 预付款页签
            if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finPayBill.getBusinessType())
                    || ConstantsFinance.PAY_BILL_BUSINESS_TYPE_YFK.equals(finPayBill.getBusinessType())) {
                // 写入本次核销已预付款明细列表
                yufuService.updateByList(finPayBill);
                // 货款
                if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finPayBill.getBusinessType())) {
                    // 写入本次核销货款明细列表
                    huokuanService.updateByList(finPayBill);
                    // 写入本次核销扣款明细列表
                    koukuanService.updateByList(finPayBill);
                    // 写入本次核销甲供料扣款明细列表
                    jiagongliaoService.updateByList(finPayBill);
                    // 写入本次核销退货扣款明细列表
                    tuihuoService.updateByList(finPayBill);
                }
            }
            // 附件清单
            updateAttach(finPayBill);
            // 删除待办
            Long[] sids = new Long[]{finPayBill.getPayBillSid()};
            if (!ConstantsEms.SAVA_STATUS.equals(finPayBill.getHandleStatus())) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, finPayBill.getHandleStatus(), null);
            }
            if (ConstantsEms.SUBMIT_STATUS.equals(finPayBill.getHandleStatus())
                    || ConstantsEms.CHECK_STATUS.equals(finPayBill.getHandleStatus())) {
                // 更新流水
                updateBookItem(finPayBill, settingClient);
                // 走提交审批，参数从查询页面提交按钮参考
                if (ConstantsEms.SUBMIT_STATUS.equals(finPayBill.getHandleStatus())) {
                    this.submit(finPayBill);
                }
                else if (ConstantsEms.CHECK_STATUS.equals(finPayBill.getHandleStatus())) {
                    updateNewDateBookItem(finPayBill);
                }
            }
            //插入日志
            MongodbDeal.update(finPayBill.getPayBillSid(), original.getHandleStatus(),
                    finPayBill.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更付款单
     *
     * @param finPayBill 付款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinPayBill(FinPayBill finPayBill) {
        SysDefaultSettingClient settingClient = getClientSetting();
        // 校验
        judge(finPayBill, settingClient);
        FinPayBill response = this.selectFinPayBillById(finPayBill.getPayBillSid());
        setData(finPayBill);
        // 释放旧的引用流水
        FinPayBill original = this.selectFinPayBillById(finPayBill.getPayBillSid());
        returnBookItem(original, settingClient);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, finPayBill);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finPayBill.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finPayBillMapper.updateAllById(finPayBill);
        if (row > 0) {
            // 写入明细
            billItemService.updateByList(finPayBill);
            // 写入外发加工费结算明细
            outsourceSettleService.updateByList(finPayBill);
            // 写入发票明细
            invoiceService.updateByList(finPayBill);
            // 货款页签 / 预付款页签
            if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finPayBill.getBusinessType())
                    || ConstantsFinance.PAY_BILL_BUSINESS_TYPE_YFK.equals(finPayBill.getBusinessType())) {
                // 写入本次核销已预付款明细列表
                yufuService.updateByList(finPayBill);
                // 货款
                if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finPayBill.getBusinessType())) {
                    // 写入本次核销货款明细列表
                    huokuanService.updateByList(finPayBill);
                    // 写入本次核销扣款明细列表
                    koukuanService.updateByList(finPayBill);
                    // 写入本次核销甲供料扣款明细列表
                    jiagongliaoService.updateByList(finPayBill);
                    // 写入本次核销退货扣款明细列表
                    tuihuoService.updateByList(finPayBill);
                }
            }
            // 附件清单
            updateAttach(finPayBill);
            // 更新流水
            updateBookItem(finPayBill, settingClient);
            //插入日志
            MongodbUtil.insertUserLog(finPayBill.getPayBillSid(), BusinessType.CHANGE.getValue(), response, finPayBill, TITLE);
        }
        return row;
    }

    /**
     * 批量删除付款单
     *
     * @param payBillSids 需要删除的付款单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinPayBillByIds(List<Long> payBillSids) {
        if (CollectionUtil.isEmpty(payBillSids)) {
            return 0;
        }
        List<FinPayBill> list = new ArrayList<>();
        for (Long sid : payBillSids) {
            list.add(this.selectFinPayBillById(sid));
        }
        int row = finPayBillMapper.deleteBatchIds(payBillSids);
        if (row > 0) {
            Long[] sids = payBillSids.toArray(new Long[payBillSids.size()]);
            // 明细列表
            List<FinPayBillItem> itemList = billItemService.selectFinPayBillItemList
                    (new FinPayBillItem().setPayBillSidList(sids));
            if (CollectionUtil.isNotEmpty(itemList)) {
                billItemService.deleteByList(itemList);
            }
            // 外发加工费结算明细列表
            List<FinPayBillItemOutsourceSettle> outsourceSettleList = outsourceSettleService.selectFinPayBillItemOutsourceSettleList
                    (new FinPayBillItemOutsourceSettle().setPayBillSidList(sids));
            if (CollectionUtil.isNotEmpty(outsourceSettleList)) {
                outsourceSettleService.deleteByList(outsourceSettleList);
            }
            // 发票台账明细列表
            List<FinPayBillItemInvoice> invoiceList = invoiceService.selectFinPayBillItemInvoiceList
                    (new FinPayBillItemInvoice().setPayBillSidList(sids));
            if (CollectionUtil.isNotEmpty(invoiceList)) {
                invoiceService.deleteByList(invoiceList);
            }
            // 本次核销已预付款明细列表
            List<FinPayBillItemYufu> yufuList = yufuService.selectFinPayBillItemYufuList
                    (new FinPayBillItemYufu().setPayBillSidList(sids));
            if (CollectionUtil.isNotEmpty(yufuList)) {
                yufuService.deleteByList(yufuList);
            }
            // 本次核销扣款明细列表
            List<FinPayBillItemKoukuan> koukuanList = koukuanService.selectFinPayBillItemKoukuanList
                    (new FinPayBillItemKoukuan().setPayBillSidList(sids));
            if (CollectionUtil.isNotEmpty(koukuanList)) {
                koukuanService.deleteByList(koukuanList);
            }
            // 本次核销甲供料扣款明细列表
            List<FinPayBillItemKoukuanJiagongliao> kegongliaoList = jiagongliaoService.selectFinPayBillItemKoukuanJiagongliaoList
                    (new FinPayBillItemKoukuanJiagongliao().setPayBillSidList(sids));
            if (CollectionUtil.isNotEmpty(kegongliaoList)) {
                jiagongliaoService.deleteByList(kegongliaoList);
            }
            // 本次核销退货扣款明细列表
            List<FinPayBillItemKoukuanTuihuo> tuihuoList = tuihuoService.selectFinPayBillItemKoukuanTuihuoList
                    (new FinPayBillItemKoukuanTuihuo().setPayBillSidList(sids));
            if (CollectionUtil.isNotEmpty(tuihuoList)) {
                tuihuoService.deleteByList(tuihuoList);
            }
            // 本次核销退货扣款明细列表
            List<FinPayBillItemHuokuan> huokuanList = huokuanService.selectFinPayBillItemHuokuanList
                    (new FinPayBillItemHuokuan().setPayBillSidList(sids));
            if (CollectionUtil.isNotEmpty(huokuanList)) {
                huokuanService.deleteByList(huokuanList);
            }
            // 删除附件
            attachmentMapper.delete(new QueryWrapper<FinPayBillAttachment>().lambda()
                    .in(FinPayBillAttachment::getPayBillSid, sids));
            // 删除待办
            sysTodoTaskService.deleteSysTodoTaskList(sids, null, null);
            // 操作日志
            //SysDefaultSettingClient settingClient = getClientSetting();
            list.forEach(o -> {
                // 操作日志
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FinPayBill());
                MongodbUtil.insertUserLog(o.getPayBillSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 更改处理状态
     */
    private int updateHandle(Long[] sids, String handleStatus) {
        LambdaUpdateWrapper<FinPayBill> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinPayBill::getPayBillSid, sids);
        updateWrapper.set(FinPayBill::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(FinPayBill::getConfirmDate, new Date());
            updateWrapper.set(FinPayBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return finPayBillMapper.update(null, updateWrapper);
    }

    /**
     * 工作流流程
     */
    public int workFlow(FinPayBill finPayBill) {
        int row = 1;
        Long[] sids = finPayBill.getPayBillSidList();
        // 处理状态
        String handleStatus = finPayBill.getHandleStatus();
        if (StrUtil.isNotBlank(finPayBill.getOperateType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(finPayBill.getOperateType());
        }
        if (sids != null && sids.length > 0) {
            // 获取数据
            List<FinPayBill> billList = finPayBillMapper.selectFinPayBillList
                    (new FinPayBill().setPayBillSidList(sids));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus,
                        ConstantsTable.TABLE_FIN_PAY_BILL);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(finPayBill.getOperateType())) {
                // 修改处理状态
                row = this.updateHandle(sids, ConstantsEms.SUBMIT_STATUS);
                // 开启工作流
                for (int i = 0; i < billList.size(); i++) {
                    this.submit(billList.get(i));
                    FinPayBill bill = finPayBillMapper.selectById(billList.get(i).getPayBillSid());
                    //插入日志
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(billList.get(i), bill);
                    MongodbUtil.insertUserLog(billList.get(i).getPayBillSid(),
                            BusinessType.SUBMIT.getValue(), msgList, TITLE, finPayBill.getComment());
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(finPayBill.getOperateType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                for (int i = 0; i < billList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(billList.get(i).getPayBillSid().toString());
                    taskVo.setFormId(billList.get(i).getPayBillSid());
                    taskVo.setFormCode(billList.get(i).getPayBillCode().toString());
                    taskVo.setFormType(FormType.PayBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finPayBill.getComment());
                    try {
                        SysFormProcess process = workflowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getPayBillSid()}, ConstantsEms.CHECK_STATUS);
                            // 确认成功 最近被收款单引用日期
                            updateNewDateBookItem(billList.get(i));
                        }
                        finPayBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    FinPayBill bill = finPayBillMapper.selectById(billList.get(i).getPayBillSid());
                    //插入日志
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(billList.get(i), bill);
                    MongodbUtil.insertUserLog(billList.get(i).getPayBillSid(),
                            BusinessType.APPROVAL.getValue(), msgList, TITLE, finPayBill.getComment());
                }
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(finPayBill.getOperateType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                // 审批意见
                String comment = "";
                for (int i = 0; i < billList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setTargetKey("2");
                    taskVo.setBusinessKey(billList.get(i).getPayBillSid().toString());
                    taskVo.setFormId(billList.get(i).getPayBillSid());
                    taskVo.setFormCode(billList.get(i).getPayBillCode().toString());
                    taskVo.setFormType(FormType.PayBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finPayBill.getComment());
                    try {
                        SysFormProcess process = workflowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getPayBillSid()}, HandleStatus.RETURNED.getCode());
                        }
                        finPayBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    FinPayBill bill = finPayBillMapper.selectById(billList.get(i).getPayBillSid());
                    //插入日志
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(billList.get(i), bill);
                    MongodbUtil.insertUserLog(billList.get(i).getPayBillSid(),
                            BusinessType.APPROVAL.getValue(), msgList, TITLE, finPayBill.getComment());
                }
            }
        }
        return row;
    }

    /**
     * 1、付款单，把处理状态更新为“已确认”时，更新引用供应商扣款流水、已付款流水的“最近被付款单引用日期”为当前操作日期
     */
    public void updateNewDateBookItem(FinPayBill bill) {
        List<FinPayBillItemKoukuan> koukuanList = bill.getKoukuanList();
        if (CollectionUtil.isEmpty(koukuanList)) {
            koukuanList = koukuanService.selectFinPayBillItemKoukuanList(new FinPayBillItemKoukuan().setPayBillSid(bill.getPayBillSid()));
        }
        if (CollectionUtil.isNotEmpty(koukuanList)) {
            Long[] bookSids = koukuanList.stream().map(FinPayBillItemKoukuan::getBookDeductionSid).toArray(Long[]::new);
            bookVendorDeductionItemMapper.update(null, new UpdateWrapper<FinBookVendorDeductionItem>().lambda()
                    .in(FinBookVendorDeductionItem::getBookDeductionSid, bookSids)
                    .set(FinBookVendorDeductionItem::getNewPaymentUseDate, new Date()));
        }
        //
        List<FinPayBillItemYufu> yushouList = bill.getYufuList();
        if (CollectionUtil.isEmpty(yushouList)) {
            yushouList = yufuService.selectFinPayBillItemYufuList(new FinPayBillItemYufu().setPayBillSid(bill.getPayBillSid()));
        }
        if (CollectionUtil.isNotEmpty(yushouList)) {
            Long[] bookSids = yushouList.stream().map(FinPayBillItemYufu::getBookPaymentSid).toArray(Long[]::new);
            bookPaymentItemMapper.update(null, new UpdateWrapper<FinBookPaymentItem>().lambda()
                    .in(FinBookPaymentItem::getBookPaymentSid, bookSids)
                    .set(FinBookPaymentItem::getNewPaymentUseDate, new Date()));
        }
        //
        List<FinPayBillItemInvoice> invoiceList = bill.getInvoiceList();
        if (CollectionUtil.isEmpty(invoiceList)) {
            invoiceList = invoiceService.selectFinPayBillItemInvoiceList(new FinPayBillItemInvoice().setPayBillSid(bill.getPayBillSid()));
        }
        if (CollectionUtil.isNotEmpty(invoiceList)) {
            Long[] bookSids = invoiceList.stream().map(FinPayBillItemInvoice::getVendorInvoiceRecordSid).toArray(Long[]::new);
            finVendorInvoiceRecordMapper.update(null, new UpdateWrapper<FinVendorInvoiceRecord>().lambda()
                    .in(FinVendorInvoiceRecord::getVendorInvoiceRecordSid, bookSids)
                    .set(FinVendorInvoiceRecord::getNewPaymentUseDate, new Date()));
        }
    }

    /**
     * 提交时校验
     */
    @Override
    public EmsResultEntity submitVerify(FinPayBill finPayBill) {
        SysDefaultSettingClient settingClient = getClientSetting();
        // 校验
        judge(finPayBill, settingClient);
        //
        if (CollectionUtil.isNotEmpty(finPayBill.getItemList())) {
            boolean season = finPayBill.getItemList().stream().anyMatch(o -> o.getProductSeasonSid() == null);
            boolean method = finPayBill.getItemList().stream().anyMatch(o -> StrUtil.isBlank(o.getPaymentMethod()));
            if (season || method) {
                return EmsResultEntity.warning(null, "存在本次实付明细的“下单季”或“付款方式”为空，是否继续操作？");
            }
        }
        return EmsResultEntity.success();
    }

    /**
     * 更改确认状态
     *
     * @param finPayBill
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FinPayBill finPayBill) {
        int row = 0;
        Long[] sids = finPayBill.getPayBillSidList();
        if (sids != null && sids.length > 0) {
            SysDefaultSettingClient settingClient = getClientSetting();
            // 判断是否要走审批
            if (BusinessType.SUBMIT.getValue().equals(finPayBill.getOperateType())) {
                Map<Long, FinPayBill> map = new HashMap<>();
                for (Long sid : sids) {
                    FinPayBill bill = this.selectFinPayBillById(sid);
                    // 校验金额填写
                    bill.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
                    judgeItemAmount(bill, settingClient);
                    judgeDai(bill, settingClient);
                    // 用来记录旧数据
                    map.put(sid, bill);
                }
                if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowFk())) {
                    // 不需要走审批，提交及确认
                    LambdaUpdateWrapper<FinPayBill> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.in(FinPayBill::getPayBillSid,sids)
                            .set(FinPayBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                            .set(FinPayBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                            .set(FinPayBill::getConfirmDate, new Date());
                    row = finPayBillMapper.update(null, updateWrapper);
                    // 删除待办
                    sysTodoTaskService.deleteSysTodoTaskList(sids, ConstantsEms.CHECK_STATUS, null);
                    for (Long id : sids) {
                        FinPayBill bill = this.selectFinPayBillById(id);
                        // 更新流水
                        updateBookItem(bill, settingClient);
                        // 最近被收款单引用日期
                        updateNewDateBookItem(bill);
                        //插入日志
                        List<OperMsg> msgList = new ArrayList<>();
                        msgList = BeanUtils.eq(map.get(id), bill);
                        MongodbDeal.check(id, ConstantsEms.CHECK_STATUS, msgList, TITLE, null);
                    }
                    return row;
                }
            }
            // 走工作流程
            row = workFlow(finPayBill);
            // 处理流水 后续这里如果还要 就放到 workFlow 里去
            if (BusinessType.SUBMIT.getValue().equals(finPayBill.getOperateType())) {
                for (Long id : sids) {
                    FinPayBill bill = this.selectFinPayBillById(id);
                    // 更新流水
                    updateBookItem(bill, settingClient);
                }
            }
            //
            else if (BusinessType.DISAPPROVED.getValue().equals(finPayBill.getOperateType())) {
                for (Long id : sids) {
                    FinPayBill bill = this.selectFinPayBillById(id);
                    // 更新流水
                    returnBookItem(bill, settingClient);
                }
            }
        }
        return row;
    }

    /**
     * 支付
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int receipt(FinPayBill finPayBill) {
        int row = 0;
        Long[] sids = finPayBill.getPayBillSidList();
        if (sids != null && sids.length > 0) {
            if (finPayBill.getPaymentDate() == null) {
                throw new BaseException("支付日期不能为空！");
            }
            List<FinPayBill> list = finPayBillMapper.selectList(new QueryWrapper<FinPayBill>().lambda()
                    .in(FinPayBill::getPayBillSid, sids)
                    .eq(FinPayBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .ne(FinPayBill::getPaymentStatus, ConstantsFinance.PAYMENT_STATUS_YZF));
            if (CollectionUtil.isEmpty(list) || sids.length != list.size()) {
                throw new BaseException("所选数据不符合支付操作！");
            }
            Map<Long, FinPayBill> map = list.stream().collect(Collectors.toMap(FinPayBill::getPayBillSid, Function.identity()));
            // 更新状态
            LambdaUpdateWrapper<FinPayBill> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(FinPayBill::getPayBillSid, sids)
                    .set(FinPayBill::getPaymentStatus, ConstantsFinance.PAYMENT_STATUS_YZF)
                    .set(FinPayBill::getPaymentDate, finPayBill.getPaymentDate());
            row = finPayBillMapper.update(null, updateWrapper);
            for (Long sid : sids) {
                FinPayBill bill = this.selectFinPayBillById(sid);
                // 生成付款流水
                insertBook(bill);
                // 确认支付后 处理更新流水
                updateBookConfirm(bill);
                // 操作日志
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(map.get(sid), bill);
                MongodbUtil.insertUserLog(sid, BusinessType.RECEIPT.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 撤回保存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int revocation(FinPayBill finPayBill) {
        int row = 0;
        Long[] sids = finPayBill.getPayBillSidList();
        if (sids != null && sids.length > 0) {
            if (finPayBill.getComment() == null) {
                throw new BaseException("撤回说明不能为空！");
            }
            List<FinPayBill> list = finPayBillMapper.selectList(new QueryWrapper<FinPayBill>().lambda()
                    .in(FinPayBill::getPayBillSid, sids)
                    .eq(FinPayBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .ne(FinPayBill::getPaymentStatus, ConstantsFinance.PAYMENT_STATUS_YZF));
            if (CollectionUtil.isEmpty(list) || sids.length != list.size()) {
                throw new BaseException("所选数据不符合撤回保存操作！");
            }
            Map<Long, FinPayBill> map = list.stream().collect(Collectors.toMap(FinPayBill::getPayBillSid, Function.identity()));
            // 更新状态
            LambdaUpdateWrapper<FinPayBill> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(FinPayBill::getPayBillSid, sids)
                    .set(FinPayBill::getHandleStatus, ConstantsEms.SAVA_STATUS);
            row = finPayBillMapper.update(null, updateWrapper);
            SysDefaultSettingClient settingClient = getClientSetting();
            String comment = finPayBill.getComment() == null ? "" : "撤回说明：" + finPayBill.getComment();
            for (Long sid : sids) {
                FinPayBill bill = this.selectFinPayBillById(sid);
                // 更新流水
                returnBookItem(bill, settingClient);
                // 最近被收款单引用日期
                updateNewDateBookItem(bill);
                // 操作日志
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(map.get(sid), bill);
                MongodbUtil.insertUserLog(sid, BusinessType.QITA.getValue(), msgList, TITLE, comment);
            }
        }
        return row;
    }

    /**
     * 设置是否有票
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setIsyoupiao(FinPayBill finPayBill) {
        int row = 0;
        Long[] sids = finPayBill.getPayBillSidList();
        if (sids != null && sids.length > 0) {
            if (StrUtil.isBlank(finPayBill.getIsYoupiao())) {
                throw new BaseException("是否有票不能为空！");
            }
            List<FinPayBill> billList = finPayBillMapper.selectList(new QueryWrapper<FinPayBill>().lambda()
                    .in(FinPayBill::getPayBillSid, sids).and(que ->
                            que.ne(FinPayBill::getIsYoupiao, finPayBill.getIsYoupiao())
                                    .or().isNull(FinPayBill::getIsYoupiao)));
            if (CollectionUtil.isEmpty(billList)) {
                return row;
            }
            sids = billList.stream().map(FinPayBill::getPayBillSid).toArray(Long[]::new);
            // 必填校验通过后，判断所选收款单是否存在本次核销发票台账明细，
            // 若存在，提示：收款单XXX存在核销发票台账明细，操作失败！其中，XXX为收款单号
            // 上述校验均通过，更新所选收款单的相关信息，并记录操作日志如下：
            // 操作类型：变更 详情：修改是否有票，更改前：XXX，更改后：XXX
            List<FinPayBillItemInvoice> invoiceList = invoiceService.selectFinPayBillItemInvoiceList(
                    new FinPayBillItemInvoice().setPayBillSidList(sids));
            if (CollectionUtil.isNotEmpty(invoiceList)) {
                throw new BaseException("付款单" + invoiceList.get(0).getPayBillCode() + "存在核销发票台账明细，操作失败！");
            }
            LambdaUpdateWrapper<FinPayBill> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(FinPayBill::getPayBillSid,sids)
                    .set(FinPayBill::getIsYoupiao, finPayBill.getIsYoupiao());
            row = finPayBillMapper.update(new FinPayBill(), updateWrapper);
            if (row > 0) {
                // 是否有票数据字典
                List<DictData> yesnoList = sysDictDataService.selectDictData("s_yesno_flag");
                Map<String, String> yesnoMaps = yesnoList.stream().collect(Collectors.toMap(DictData::getDictValue,
                        DictData::getDictLabel, (key1, key2) -> key2));

                for (FinPayBill bill : billList) {
                    String old = yesnoMaps.get(bill.getIsYoupiao()) == null ? "" : yesnoMaps.get(bill.getIsYoupiao());
                    String remark = "修改是否有票，更改前：" + old + "，更改后：" + yesnoMaps.get(finPayBill.getIsYoupiao());
                    // 操作日志
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.setDiff(bill, "isYoupiao", bill.getIsYoupiao(), finPayBill.getIsYoupiao(), msgList);
                    MongodbUtil.insertUserLog(bill.getPayBillSid(), BusinessType.CHANGE.getValue(), msgList, TITLE, remark);
                }
            }
        }
        return row;
    }

    /**
     * 生成付款流水
     */
    public void insertBook(FinPayBill bill) {
        if (CollectionUtil.isNotEmpty(bill.getItemList())) {
            BigDecimal currencyAmountTax = bill.getCurrencyAmountTax();

            FinBookPayment receiptpayment = new FinBookPayment();
            receiptpayment.setBookType(ConstantsFinance.BOOK_TYPE_FK)
                    .setVendorSid(bill.getVendorSid()).setVendorCode(bill.getVendorCode())
                    .setCompanySid(bill.getCompanySid()).setCompanyCode(bill.getCompanyCode())
                    .setCurrency(bill.getCurrency()).setCurrencyUnit(bill.getCurrencyUnit())
                    .setHandleStatus(bill.getHandleStatus()).setIsFinanceVerify(ConstantsEms.NO);
            Calendar cal = Calendar.getInstance();
            receiptpayment.setDocumentDate(new Date())
                    .setPaymentYear(Long.parseLong(String.valueOf(cal.get(Calendar.YEAR))))
                    .setPaymentMonth((long) cal.get(Calendar.MONTH) + 1);
            receiptpayment.setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                    .setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            // 明细
            FinBookPaymentItem receiptpaymentItem = new FinBookPaymentItem();
            receiptpaymentItem.setReferDocSid(bill.getPayBillSid())
                    .setReferDocCode(bill.getPayBillCode())
                    .setReferDocCategory(bill.getAccountCategory())
                    .setBookPaymentSid(receiptpayment.getBookPaymentSid())
                    .setItemNum((long)1).setIsFinanceVerify(ConstantsEms.NO)
                    .setCurrencyAmountTaxFk(currencyAmountTax)
                    .setCurrencyAmountTaxYhx(BigDecimal.ZERO)
                    .setCurrencyAmountTaxHxz(BigDecimal.ZERO);
            receiptpaymentItem.setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            //①若业务类型为“其它付款”
            //生成核销状态为“全部核销”，流水来源类别为“其它付款”的付款流水(汇总付款单明细的金额和，仅生成一笔流水明细)
            if (ConstantsFinance.BUSINESS_TYPE_QTFK.equals(bill.getBusinessType())) {
                receiptpayment.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_QTFK);
                receiptpaymentItem.setCurrencyAmountTaxYhx(currencyAmountTax);
                receiptpaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                receiptpayment.setItemList(new ArrayList<FinBookPaymentItem>(){{add(receiptpaymentItem);}});
                finBookPaymentService.insertFinBookPayment(receiptpayment);
            }
            //③若业务类型为“货款”
            //(1)生成核销状态为“全部核销”，流水来源类别为“货款”的付款流水(汇总付款单明细的金额和，仅生成一笔流水明细)
            else if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(bill.getBusinessType())) {
                receiptpayment.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_HK);
                receiptpaymentItem.setCurrencyAmountTaxYhx(currencyAmountTax);
                receiptpaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                receiptpayment.setItemList(new ArrayList<FinBookPaymentItem>(){{add(receiptpaymentItem);}});
                finBookPaymentService.insertFinBookPayment(receiptpayment);
            }
            //②若业务类型为“预付款”
            //生成核销状态为“未核销”，流水来源类别为“预付款”的付款流水(汇总付款单明细的金额和，仅生成一笔流水明细)
            else if (ConstantsFinance.BUSINESS_TYPE_YUFK.equals(bill.getBusinessType())) {
                receiptpayment.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_YFK);
                receiptpaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                receiptpayment.setItemList(new ArrayList<FinBookPaymentItem>(){{add(receiptpaymentItem);}});
                finBookPaymentService.insertFinBookPayment(receiptpayment);
            }
        }
    }

    /**
     * 确认支付后 处理更新流水
     * ## 请检查是否需要提前计算某些金额数据 ##
     */
    public void updateBookConfirm(FinPayBill bill) {
        SysDefaultSettingClient settingClient = getClientSetting();
        // ●应付暂估流水
        if (ConstantsFinance.SHOUKUAN_ACC_CLEAR_WAY_YFZG.equals(settingClient.getFukuanAccountClearWay())) {
            //根据付款单的核销应付暂估日志的核销金额，更新应付暂估流水的已核销金额，并根据核销情况更新核销状态
            List<FinClearLogPaymentEstimation> logs = logPaymentEstimationMapper.selectList(new QueryWrapper<FinClearLogPaymentEstimation>().lambda()
                    .eq(FinClearLogPaymentEstimation::getPayBillSid, bill.getPayBillSid())
                    .eq(FinClearLogPaymentEstimation::getShengxiaoStatus, ConstantsFinance.SHENGXIAO_STATUS_WSX));
            if (CollectionUtil.isNotEmpty(logs)) {
                for (FinClearLogPaymentEstimation log : logs) {
                    FinBookPaymentEstimationItem item = bookPaymentEstimationItemMapper.selectById(log.getBookPaymentEstimationItemSid());
                    // 流水写已核销
                    BigDecimal yhx = item.getCurrencyAmountTaxYhx().add(log.getCurrencyAmountTax());
                    item.setCurrencyAmountTaxYhx(yhx);
                    bookPaymentEstimationItemService.updateByAmountTax(item);
                }
                // 日志表改为已生效
                logPaymentEstimationMapper.update(null, new UpdateWrapper<FinClearLogPaymentEstimation>().lambda()
                        .set(FinClearLogPaymentEstimation::getShengxiaoStatus, ConstantsFinance.SHENGXIAO_STATUS_YSX)
                        .in(FinClearLogPaymentEstimation::getPayBillSid, bill.getPayBillSid()));
            }
        }
        /**
         // ●供应商扣款流水
         //根据付款单的核销供应商扣款日志的核销金额，更新供应商扣款流水的已核销金额，并根据核销情况更新核销状态
         List<FinClearLogVendorDeduction> logs2 = logVendorDeductionMapper.selectList(new QueryWrapper<FinClearLogVendorDeduction>().lambda()
         .eq(FinClearLogVendorDeduction::getPayBillSid, bill.getPayBillSid())
         .eq(FinClearLogVendorDeduction::getShengxiaoStatus, ConstantsFinance.SHENGXIAO_STATUS_WSX));
         if (CollectionUtil.isNotEmpty(logs2)) {
         for (FinClearLogVendorDeduction log : logs2) {
         FinBookVendorDeductionItem item = bookVendorDeductionItemMapper.selectById(log.getBookDeductionItemSid());
         // 流水写已核销
         BigDecimal yhx = item.getCurrencyAmountTaxYhx().add(log.getCurrencyAmountTax());
         item.setCurrencyAmountTaxYhx(yhx);
         bookVendorDeductionItemService.updateByAmountTax(item);
         }
         // 日志表改为已生效
         logVendorDeductionMapper.update(null, new UpdateWrapper<FinClearLogVendorDeduction>().lambda()
         .set(FinClearLogVendorDeduction::getShengxiaoStatus, ConstantsFinance.SHENGXIAO_STATUS_YSX)
         .in(FinClearLogVendorDeduction::getPayBillSid, bill.getPayBillSid()));
         }
         // ●供应商已预付款流水
         //根据付款单的核销供应商已预付款日志的核销金额，更新供应商已预付款流水的已核销金额，并根据核销情况更新核销状态
         List<FinClearLogAdvancePayment> logs3 = logAdvancePaymentMapper.selectList(new QueryWrapper<FinClearLogAdvancePayment>().lambda()
         .eq(FinClearLogAdvancePayment::getPayBillSid, bill.getPayBillSid())
         .eq(FinClearLogAdvancePayment::getShengxiaoStatus, ConstantsFinance.SHENGXIAO_STATUS_WSX));
         if (CollectionUtil.isNotEmpty(logs3)) {
         for (FinClearLogAdvancePayment log : logs3) {
         FinBookPaymentItem item = bookPaymentItemMapper.selectById(log.getBookPaymentItemSid());
         // 流水写已核销
         BigDecimal yhx = item.getCurrencyAmountTaxYhx().add(log.getCurrencyAmountTax());
         item.setCurrencyAmountTaxYhx(yhx);
         bookPaymentItemService.updateByAmountTax(item);
         }
         // 日志表改为已生效
         logAdvancePaymentMapper.update(null, new UpdateWrapper<FinClearLogAdvancePayment>().lambda()
         .set(FinClearLogAdvancePayment::getShengxiaoStatus, ConstantsFinance.SHENGXIAO_STATUS_YSX)
         .in(FinClearLogAdvancePayment::getPayBillSid, bill.getPayBillSid()));
         }
         **/
    }

    /**
     * 更新发票台账 查询
     */
    @Override
    public FinPayBill invoiceList(FinPayBill finPayBill) {
        if (finPayBill != null) {
            // 发票台账明细列表
            finPayBill.setInvoiceList(new ArrayList<>());
            if (finPayBill.getPayBillSid() != null) {
                List<FinPayBillItemInvoice> invoiceList = invoiceService.selectFinPayBillItemInvoiceList
                        (new FinPayBillItemInvoice().setPayBillSid(finPayBill.getPayBillSid()));
                if (CollectionUtil.isNotEmpty(invoiceList)) {
                    finPayBill.setInvoiceList(invoiceList);
                }
            }
        }
        return finPayBill;
    }

    /**
     * 更新发票台账 更新
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int invoiceUpdate(FinPayBill finPayBill) {
        int row = 0;
        if (finPayBill != null && finPayBill.getPayBillSid() != null) {
            FinPayBill bill = finPayBillMapper.selectFinPayBillById(finPayBill.getPayBillSid());
            bill.setInvoiceList(finPayBill.getInvoiceList());
            // 校验
            judgeInvoice(bill);
            //
            List<FinPayBillItemInvoice> updateDateList = new ArrayList<>();
            if (ConstantsEms.CHECK_STATUS.equals(bill.getHandleStatus())) {
                // 原本明细
                List<FinPayBillItemInvoice> oldList = invoiceService.selectFinPayBillItemInvoiceList(new FinPayBillItemInvoice()
                        .setPayBillSid(bill.getPayBillSid()));
                if (CollectionUtil.isNotEmpty(oldList)) {
                    updateDateList.addAll(oldList);
                }
                // 现在的明细
                if (CollectionUtil.isNotEmpty(finPayBill.getInvoiceList())) {
                    updateDateList.addAll(finPayBill.getInvoiceList());
                }
            }
            // 更新发票明细
            row = invoiceService.updateByList(bill);
            //
            if (CollectionUtil.isNotEmpty(updateDateList)) {
                Long[] bookSids = updateDateList.stream().map(FinPayBillItemInvoice::getVendorInvoiceRecordSid).distinct().toArray(Long[]::new);
                finVendorInvoiceRecordMapper.update(null, new UpdateWrapper<FinVendorInvoiceRecord>().lambda()
                        .in(FinVendorInvoiceRecord::getVendorInvoiceRecordSid, bookSids)
                        .set(FinVendorInvoiceRecord::getNewPaymentUseDate, new Date()));
            }
        }
        return row;
    }

}
