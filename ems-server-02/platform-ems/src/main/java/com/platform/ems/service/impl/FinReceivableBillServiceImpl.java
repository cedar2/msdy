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
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.constant.*;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.*;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.data.BigDecimalSum;
import com.platform.ems.workflow.service.impl.WorkFlowServiceImpl;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 收款单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-22
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class FinReceivableBillServiceImpl extends ServiceImpl<FinReceivableBillMapper, FinReceivableBill> implements IFinReceivableBillService {
    @Autowired
    private FinReceivableBillMapper finReceivableBillMapper;
    @Autowired
    private IFinReceivableBillItemService billItemService;
    @Autowired
    private IFinReceivableBillItemInvoiceService invoiceService;
    @Autowired
    private IFinReceivableBillItemYushouService yushouService;
    @Autowired
    private IFinReceivableBillItemKoukuanService koukuanService;
    @Autowired
    private IFinReceivableBillItemHuokuanService huokuanService;
    @Autowired
    private IFinReceivableBillItemKoukuanKegongliaoService kegongliaoService;
    @Autowired
    private IFinReceivableBillItemKoukuanTuihuoService tuihuoService;
    @Autowired
    private FinReceivableBillAttachmentMapper attachmentMapper;
    @Autowired
    private BasCustomerMapper customerMapper;
    @Autowired
    private BasCompanyMapper companyMapper;

    @Autowired
    private IFinBookReceiptEstimationItemService bookReceiptEstimationItemService;
    @Autowired
    private FinBookReceiptEstimationItemMapper bookReceiptEstimationItemMapper;
    @Autowired
    private FinClearLogReceiptEstimationMapper logReceiptEstimationMapper;
    @Autowired
    private FinClearLogCustomerDeductionMapper logCustomerDeductionMapper;
    @Autowired
    private FinClearLogAdvanceReceiptPaymentMapper logAdvanceReceiptPaymentMapper;
    @Autowired
    private IFinBookCustomerDeductionItemService bookCustomerDeductionItemService;
    @Autowired
    private FinBookCustomerDeductionItemMapper bookCustomerDeductionItemMapper;
    @Autowired
    private IFinBookReceiptPaymentItemService bookReceiptPaymentItemService;
    @Autowired
    private FinBookReceiptPaymentItemMapper bookReceiptPaymentItemMapper;
    @Autowired
    private IFinBookReceiptPaymentService finBookReceiptPaymentService;
    @Autowired
    private FinBookReceiptPaymentMapper finBookReceiptPaymentMapper;
    @Autowired
    private FinCustomerInvoiceRecordMapper finCustomerInvoiceRecordMapper;
    @Autowired
    private SysDefaultSettingClientMapper defaultSettingClientMapper;
    @Autowired
    private ISysTodoTaskService sysTodoTaskService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private WorkFlowServiceImpl workflowService;

    private static final String TITLE = "收款单";

    /**
     * 获取租户默认设置
     */
    @Override
    public SysDefaultSettingClient getClientSetting() {
        return defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId()));
    }

    /**
     * 查询收款单
     *
     * @param receivableBillSid 收款单ID
     * @return 收款单
     */
    @Override
    public FinReceivableBill selectFinReceivableBillById(Long receivableBillSid) {
        FinReceivableBill finReceivableBill = finReceivableBillMapper.selectFinReceivableBillById(receivableBillSid);
        if (finReceivableBill == null) {
            throw new BaseException("找不到该收款单");
        }
        // 明细列表
        finReceivableBill.setItemList(new ArrayList<>());
        List<FinReceivableBillItem> itemList = billItemService.selectFinReceivableBillItemList
                (new FinReceivableBillItem().setReceivableBillSid(receivableBillSid));
        if (CollectionUtil.isNotEmpty(itemList)) {
            finReceivableBill.setItemList(itemList);
        }
        // 发票台账明细列表
        finReceivableBill.setInvoiceList(new ArrayList<>());
        List<FinReceivableBillItemInvoice> invoiceList = invoiceService.selectFinReceivableBillItemInvoiceList
                (new FinReceivableBillItemInvoice().setReceivableBillSid(receivableBillSid));
        if (CollectionUtil.isNotEmpty(invoiceList)) {
            finReceivableBill.setInvoiceList(invoiceList);
        }
        // 货款页签 / 预收款页签
        if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finReceivableBill.getBusinessType())
                || ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_YSK.equals(finReceivableBill.getBusinessType())) {
            // 本次核销已预收款明细列表
            finReceivableBill.setYushouList(new ArrayList<>());
            List<FinReceivableBillItemYushou> yushouList = yushouService.selectFinReceivableBillItemYushouList
                    (new FinReceivableBillItemYushou().setReceivableBillSid(receivableBillSid));
            if (CollectionUtil.isNotEmpty(yushouList)) {
                finReceivableBill.setYushouList(yushouList);
            }
            // 货款
            if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finReceivableBill.getBusinessType())) {
                // 本次核销扣款明细列表
                finReceivableBill.setHuokuanList(new ArrayList<>());
                List<FinReceivableBillItemHuokuan> huokuanList = huokuanService.selectFinReceivableBillItemHuokuanList
                        (new FinReceivableBillItemHuokuan().setReceivableBillSid(receivableBillSid));
                if (CollectionUtil.isNotEmpty(huokuanList)) {
                    finReceivableBill.setHuokuanList(huokuanList);
                }
                // 本次核销扣款明细列表
                finReceivableBill.setKoukuanList(new ArrayList<>());
                List<FinReceivableBillItemKoukuan> koukuanList = koukuanService.selectFinReceivableBillItemKoukuanList
                        (new FinReceivableBillItemKoukuan().setReceivableBillSid(receivableBillSid));
                if (CollectionUtil.isNotEmpty(koukuanList)) {
                    finReceivableBill.setKoukuanList(koukuanList);
                }
                // 本次核销客供料扣款明细列表
                finReceivableBill.setKegongliaoList(new ArrayList<>());
                List<FinReceivableBillItemKoukuanKegongliao> kegongliaoList = kegongliaoService.selectFinReceivableBillItemKoukuanKegongliaoList
                        (new FinReceivableBillItemKoukuanKegongliao().setReceivableBillSid(receivableBillSid));
                if (CollectionUtil.isNotEmpty(kegongliaoList)) {
                    finReceivableBill.setKegongliaoList(kegongliaoList);
                }
                // 本次核销退货扣款明细列表
                finReceivableBill.setTuihuoList(new ArrayList<>());
                List<FinReceivableBillItemKoukuanTuihuo> tuihuoList = tuihuoService.selectFinReceivableBillItemKoukuanTuihuoList
                        (new FinReceivableBillItemKoukuanTuihuo().setReceivableBillSid(receivableBillSid));
                if (CollectionUtil.isNotEmpty(tuihuoList)) {
                    finReceivableBill.setTuihuoList(tuihuoList);
                }
            }
        }
        // 附件清单
        finReceivableBill.setAttachmentList(new ArrayList<>());
        List<FinReceivableBillAttachment> attachmentList = attachmentMapper.selectFinReceivableBillAttachmentList
                (new FinReceivableBillAttachment().setReceivableBillSid(receivableBillSid));
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            finReceivableBill.setAttachmentList(attachmentList);
        }
        // 计算明细
        countItem(finReceivableBill);
        // 计算基本信息页签的待核销数量
        countBaseDai(finReceivableBill, getClientSetting());
        // 操作日志
        MongodbUtil.find(finReceivableBill);
        return finReceivableBill;
    }

    /**
     * 查询收款单列表
     *
     * @param finReceivableBill 收款单
     * @return 收款单
     */
    @Override
    public List<FinReceivableBill> selectFinReceivableBillList(FinReceivableBill finReceivableBill) {
        return finReceivableBillMapper.selectFinReceivableBillList(finReceivableBill);
    }

    /**
     * 计算明细本次金额
     */
    public void countItem(FinReceivableBill finReceivableBill) {
        // 本次实收金额    =本次实收明细的金额(含税)之和
        if (CollectionUtil.isNotEmpty(finReceivableBill.getItemList())) {
            BigDecimal currencyAmountTax = finReceivableBill.getItemList().stream().map(FinReceivableBillItem::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            if (finReceivableBill.getCurrencyAmountTax() != null &&
                    finReceivableBill.getCurrencyAmountTax().compareTo(currencyAmountTax) != 0) {
                log.warn("前端计算本次实收小计(含税)与后端计算本次实收小计(含税)不一致");
            }
            if (finReceivableBill.getCurrencyAmountTax() == null) {
                finReceivableBill.setCurrencyAmountTax(currencyAmountTax);
            }
        }
        // 本次核销扣款     =本次核销扣款明细的金额(含税)之和
        if (CollectionUtil.isNotEmpty(finReceivableBill.getKoukuanList())) {
            BigDecimal heXiaoKouKuan = finReceivableBill.getKoukuanList().stream().map(FinReceivableBillItemKoukuan::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            if (finReceivableBill.getHeXiaoKouKuan() != null &&
                    finReceivableBill.getHeXiaoKouKuan().compareTo(heXiaoKouKuan) != 0) {
                log.warn("前端计算本次核销扣款与后端计算本次本次核销扣款不一致");
            }
            if (finReceivableBill.getHeXiaoKouKuan() == null) {
                finReceivableBill.setHeXiaoKouKuan(heXiaoKouKuan);
            }
        }
        // 本次核销客供料扣款金额      =本次核销客供料扣款=本次核销客供料扣款明细的金额(含税)之和
        if (CollectionUtil.isNotEmpty(finReceivableBill.getKegongliaoList())) {
            BigDecimal heXiaokegongliao = finReceivableBill.getKegongliaoList().stream().map(FinReceivableBillItemKoukuanKegongliao::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            if (finReceivableBill.getHeXiaokegongliao() != null &&
                    finReceivableBill.getHeXiaokegongliao().compareTo(heXiaokegongliao) != 0) {
                log.warn("前端计算本次核销客供料扣款金额与后端计算本次核销客供料扣款金额不一致");
            }
            if (finReceivableBill.getHeXiaokegongliao() == null) {
                finReceivableBill.setHeXiaokegongliao(heXiaokegongliao);
            }
        }
        // 本次核销退货扣款         =本次核销退货扣款明细的金额(含税)之和
        if (CollectionUtil.isNotEmpty(finReceivableBill.getTuihuoList())) {
            BigDecimal heXiaoTuihuo = finReceivableBill.getTuihuoList().stream().map(FinReceivableBillItemKoukuanTuihuo::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            if (finReceivableBill.getHeXiaoTuihuo() != null &&
                    finReceivableBill.getHeXiaoTuihuo().compareTo(heXiaoTuihuo) != 0) {
                log.warn("前端计算本次核销退货扣款与后端计算本次核销退货扣款不一致");
            }
            if (finReceivableBill.getHeXiaoTuihuo() == null) {
                finReceivableBill.setHeXiaoTuihuo(heXiaoTuihuo);
            }
        }
        // 本次核销已预收款  =本次核销已预收款明细的金额(含税)之和
        if (CollectionUtil.isNotEmpty(finReceivableBill.getYushouList())) {
            BigDecimal heXiaoYiyushou = finReceivableBill.getYushouList().stream().map(FinReceivableBillItemYushou::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            if (finReceivableBill.getHeXiaoYiyushou() != null &&
                    finReceivableBill.getHeXiaoYiyushou().compareTo(heXiaoYiyushou) != 0) {
                log.warn("前端计算本次核销退货扣款与后端计算本次核销退货扣款不一致");
            }
            if (finReceivableBill.getHeXiaoYiyushou() == null) {
                finReceivableBill.setHeXiaoYiyushou(heXiaoYiyushou);
            }
        }
        // 本次核销货款=本次实收金额+本次核销扣款+本次核销客供料扣款金额+本次核销退货扣款+本次核销已预收款
        // sumTotalHuokuan(finReceivableBill);
        // 本次核销货款
        if (CollectionUtil.isNotEmpty(finReceivableBill.getHuokuanList())) {
            BigDecimal heXiaoHuokuan = finReceivableBill.getHuokuanList().stream().map(FinReceivableBillItemHuokuan::getCurrencyAmountTax)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            if (finReceivableBill.getHeXiaoHuokuan() != null &&
                    finReceivableBill.getHeXiaoHuokuan().compareTo(heXiaoHuokuan) != 0) {
                log.warn("前端计算本次核销货款与后端计算本次核销货款不一致");
            }
            if (finReceivableBill.getHeXiaoHuokuan() == null) {
                finReceivableBill.setHeXiaoHuokuan(heXiaoHuokuan);
            }
        }
    }

    /**
     * 计算基本信息的待数量、基本信息页签，待收货款金额(元)、待核销已预收金额(元)、待核销扣款金额(元)、待核销退货扣款金额(元)数据未显示
     */
    @Override
    public void countBaseDai(FinReceivableBill finReceivableBill, SysDefaultSettingClient settingClient) {
        if (finReceivableBill.getCustomerSid() == null || finReceivableBill.getCompanySid() == null) {
            log.warn("计算基本信息的待数据时获取到的客户或公司为空");
            return;
        }
        BigDecimal daiShouHuoKuan = BigDecimal.ZERO;
        BigDecimal daiXiaoTuiHuoKouKuan = BigDecimal.ZERO;
        if (ConstantsFinance.SHOUKUAN_ACC_CLEAR_WAY_YSZG.equals(settingClient.getShoukuanAccountClearWay())) {
            // 2、找到所有“生效状态”为”未生效 “，”是否退货“为”否“，且客户与公司一致的核销应收暂估日志，并计算出核销金额的和
            // 待收货款金额 = 第一步找到的未核销金额和 – 第二步找到的核销金额和
            // 找到未被处理状态不是“已确认”的应收暂估调价量单 引用 的 应收暂估流水
            List<FinBookReceiptEstimationItem> bookItemList = bookReceiptEstimationItemMapper.receivableSubmitSelect(new FinBookReceiptEstimationItem()
                    .setCustomerSid(finReceivableBill.getCustomerSid()).setCompanySid(finReceivableBill.getCompanySid()));
            List<FinBookReceiptEstimationItem> noTuihuo = new ArrayList<>();
            List<FinBookReceiptEstimationItem> yesTuihuo = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(bookItemList)) {
                bookItemList = bookItemList.stream().filter(i -> ConstantsFinance.CLEAR_STATUS_BFHX.equals(i.getClearStatus())
                        || ConstantsFinance.CLEAR_STATUS_WHX.equals(i.getClearStatus())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(bookItemList)) {
                    noTuihuo = bookItemList.stream().filter(i->ConstantsEms.NO.equals(i.getIsTuihuo())).collect(Collectors.toList());
                    yesTuihuo = bookItemList.stream().filter(i->ConstantsEms.YES.equals(i.getIsTuihuo())).collect(Collectors.toList());
                }
            }
            if (CollectionUtil.isNotEmpty(noTuihuo)) {
                daiShouHuoKuan = noTuihuo.stream().map(FinBookReceiptEstimationItem::getCurrencyAmountTaxLeft)
                        .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            }
            if (CollectionUtil.isNotEmpty(yesTuihuo)) {
                daiXiaoTuiHuoKouKuan = yesTuihuo.stream().map(FinBookReceiptEstimationItem::getCurrencyAmountTaxLeft)
                        .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            }
        }
        /**
         * 待收货款金额(元) : 计算逻辑
         * 》若系统默认设置的“收款核销模式“为”应收暂估“，默认显示所有”核销状态“为”未核销“或”部分核销“，”是否退货“为”否“，
         * 且客户与公司一致的应收暂估流水的未核销金额(未核销金额=出入库金额-已核销金额)的和
         * 》若系统默认设置的“收款核销模式“为”应收“，暂不实现
         * 》若系统默认设置的“收款核销模式“为”应收“，显示为空
         */
        finReceivableBill.setDaiShouHuoKuan(daiShouHuoKuan);
        /**
         * 待核销退货扣款金额(元) 计算逻辑
         * 》若系统默认设置的“收款核销模式“为”应收暂估“，默认显示所有”核销状态“为”未核销“或”部分核销“，”是否退货“为”是“，
         * 且客户与公司一致的应收暂估流水的未核销金额(未核销金额=出入库金额-已核销金额)的和的绝对值
         * 注意点：这里得到的流水的未核销金额为负数，显示时要转化为绝对值
         * 》若系统默认设置的“收款核销模式“为”应收“，暂不实现
         * 》若系统默认设置的“收款核销模式“为”应收“，显示为空
         */
        finReceivableBill.setDaiXiaoTuiHuoKouKuan(daiXiaoTuiHuoKouKuan.abs());
        /**
         * 待核销扣款金额(元) 计算逻辑
         * 默认显示所有”核销状态“为”未核销“或”部分核销“，
         * 且客户与公司一致的客户扣款流水的未核销金额(未核销金额=扣款金额-已核销金额)的和
         */
        BigDecimal daiXiaoKouKuan = BigDecimal.ZERO;
/*        List<FinBookCustomerDeductionItem> bookItemList = bookCustomerDeductionItemMapper.selectFinBookCustomerDeductionItemList
                (new FinBookCustomerDeductionItem().setClearStatusList(new String[]{ConstantsFinance.CLEAR_STATUS_WHX, ConstantsFinance.CLEAR_STATUS_BFHX})
                        .setCustomerSid(finReceivableBill.getCustomerSid()).setCompanySid(finReceivableBill.getCompanySid()));
        if (CollectionUtil.isNotEmpty(bookItemList)) {
            daiXiaoKouKuan = bookItemList.stream().map(FinBookCustomerDeductionItem::getCurrencyAmountTaxDhx)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
        }*/
        // 2、找到所有“生效状态”为”未生效 “，且客户与公司一致的核销客户扣款日志，并计算出核销金额的和
        //待核销扣款金额 = 第一步找到的未核销金额和 – 第二步找到的核销金额和
//        List<FinClearLogCustomerDeduction> logs = logCustomerDeductionMapper.selectList(new QueryWrapper<FinClearLogCustomerDeduction>()
//                .lambda().eq(FinClearLogCustomerDeduction::getCustomerSid, finReceivableBill.getCustomerSid())
//                .eq(FinClearLogCustomerDeduction::getCompanySid, finReceivableBill.getCompanySid())
//                .eq(FinClearLogCustomerDeduction::getShengxiaoStatus, ConstantsFinance.SHENGXIAO_STATUS_WSX));
//        if (CollectionUtil.isNotEmpty(logs)) {
//            daiXiaoKouKuan = daiXiaoKouKuan.subtract(logs.stream().map(FinClearLogCustomerDeduction::getCurrencyAmountTax)
//                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum));
//        }
        finReceivableBill.setDaiXiaoKouKuan(daiXiaoKouKuan);
        /**
         * 待核销已预收款金额(元) 计算逻辑
         * 默认显示所有”核销状态“为”未核销“或”部分核销“，”流水来源类别“为”预收款“，
         * 且客户与公司一致的收款流水的未核销金额(未核销金额=收款金额-已核销金额)的和
         */
/*        BigDecimal daiXiaoYiYuShouKuan = BigDecimal.ZERO;
        List<FinBookReceiptPaymentItem> receiptbookList = bookReceiptPaymentItemMapper.selectFinBookReceiptPaymentItemList
                (new FinBookReceiptPaymentItem().setClearStatusList(new String[]{ConstantsFinance.CLEAR_STATUS_WHX, ConstantsFinance.CLEAR_STATUS_BFHX})
                        .setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_YSK)
                        .setCustomerSid(finReceivableBill.getCustomerSid()).setCompanySid(finReceivableBill.getCompanySid()));
        if (CollectionUtil.isNotEmpty(receiptbookList)) {
            daiXiaoYiYuShouKuan = receiptbookList.stream().map(FinBookReceiptPaymentItem::getCurrencyAmountTaxDhx)
                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
        }*/
        // 2、找到所有“生效状态”为”未生效 “，且客户与公司一致的核销客户已预收款日志，并计算出核销金额的和
        //待核销已预收款金额 = 第一步找到的未核销金额和 – 第二步找到的核销金额和
//        List<FinClearLogAdvanceReceiptPayment> advanceLogs = logAdvanceReceiptPaymentMapper.selectList(new QueryWrapper<FinClearLogAdvanceReceiptPayment>()
//                .lambda().eq(FinClearLogAdvanceReceiptPayment::getCustomerSid, finReceivableBill.getCustomerSid())
//                .eq(FinClearLogAdvanceReceiptPayment::getCompanySid, finReceivableBill.getCompanySid())
//                .eq(FinClearLogAdvanceReceiptPayment::getShengxiaoStatus, ConstantsFinance.SHENGXIAO_STATUS_WSX));
//        if (CollectionUtil.isNotEmpty(advanceLogs)) {
//            daiXiaoYiYuShouKuan = daiXiaoYiYuShouKuan.subtract(advanceLogs.stream().map(FinClearLogAdvanceReceiptPayment::getCurrencyAmountTax)
//                    .reduce(BigDecimal.ZERO, BigDecimalSum::sum));
//        }
//        finReceivableBill.setDaiXiaoYiYuShouKuan(daiXiaoYiYuShouKuan);
    }

    /**
     * 计算本次核销货款  =本次实收金额+本次核销扣款+本次核销客供料扣款金额+本次核销退货扣款+本次核销已预收款
     */
    public void sumTotalHuokuan(FinReceivableBill finReceivableBill) {
        BigDecimal heXiaoHuokuan = BigDecimal.ZERO;
        if (finReceivableBill.getHeXiaoHuokuan() != null) {
            return;
        }
        if (finReceivableBill.getCurrencyAmountTax() != null) {
            heXiaoHuokuan = heXiaoHuokuan.add(finReceivableBill.getCurrencyAmountTax());
        }
        if (finReceivableBill.getHeXiaoKouKuan() != null) {
            heXiaoHuokuan = heXiaoHuokuan.add(finReceivableBill.getHeXiaoKouKuan());
        }
        if (finReceivableBill.getHeXiaokegongliao() != null) {
            heXiaoHuokuan = heXiaoHuokuan.add(finReceivableBill.getHeXiaokegongliao());
        }
        if (finReceivableBill.getHeXiaoTuihuo() != null) {
            heXiaoHuokuan = heXiaoHuokuan.add(finReceivableBill.getHeXiaoTuihuo());
        }
        if (finReceivableBill.getHeXiaoYiyushou() != null) {
            heXiaoHuokuan = heXiaoHuokuan.add(finReceivableBill.getHeXiaoYiyushou());
        }
        finReceivableBill.setHeXiaoHuokuan(heXiaoHuokuan);
    }

    /**
     * 校验逻辑
     */
    public void judge(FinReceivableBill finReceivableBill, SysDefaultSettingClient settingClient) {
        // 计算明细
        countItem(finReceivableBill);
        // 校验金额填写
        judgeItemAmount(finReceivableBill, settingClient);
        // 计算基本信息的待核销数据
        if (ConstantsEms.SUBMIT_STATUS.equals(finReceivableBill.getHandleStatus()) ||
                ConstantsEms.CHECK_STATUS.equals(finReceivableBill.getHandleStatus())) {
            if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finReceivableBill.getBusinessType())
                    && CollectionUtil.isNotEmpty(finReceivableBill.getItemList())) {
                // 计算基本信息的待核销数据
                countBaseDai(finReceivableBill, settingClient);
                // 校验金额不能大于待
                judgeDai(finReceivableBill, settingClient);
            }
            //
            if (ConstantsEms.YES.equals(settingClient.getIsAttachRequiredShoukuan())) {
                if (CollectionUtil.isEmpty(finReceivableBill.getAttachmentList())) {
                    throw new BaseException("附件不能为空！");
                }
            }
        }
    }

    /**
     * 校验明细的金额
     */
    public void judgeItemAmount(FinReceivableBill finReceivableBill, SysDefaultSettingClient settingClient) {
        // 校验明细
        judgeItem(finReceivableBill);
        // 校验部分明细金额不能为0
        judgeNotNull(finReceivableBill);
        // 校验发票明细
        judgeInvoice(finReceivableBill);
        // 校验预收
        judgeYu(finReceivableBill);
        // 校验扣款
        judgeKoukuan(finReceivableBill);
    }

    /**
     * 校验收款明细
     */
    public void judgeItem(FinReceivableBill finReceivableBill) {
        if (CollectionUtil.isEmpty(finReceivableBill.getItemList())) {
            // 货款
            if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finReceivableBill.getBusinessType())) {
                throw new BaseException("本次实收明细不能为空！");
            }
            else {
                throw new BaseException("收款明细不能为空！");
            }
        }
        else {
            if (finReceivableBill.getItemList().stream().anyMatch(i->i.getCurrencyAmountTax()==null
                || BigDecimal.ZERO.compareTo(i.getCurrencyAmountTax()) == 0)) {
                if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finReceivableBill.getBusinessType())) {
                    throw new BaseException("本次实收明细金额不能为空且不能填0！");
                }
                else {
                    throw new BaseException("收款明细金额不能为空且不能填0！");
                }
            }
            if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_YSK.equals(finReceivableBill.getBusinessType())) {
                // 若“业务类型”为“预收款”，明细的下单季必须一致，否则报错：
                List<FinReceivableBillItem> productSeasonList = finReceivableBill.getItemList().stream()
                        .filter(o->o.getProductSeasonSid() != null).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(productSeasonList)) {
                    Map<Long, List<FinReceivableBillItem>> map = productSeasonList.stream()
                            .collect(Collectors.groupingBy(FinReceivableBillItem::getProductSeasonSid));
                    if (productSeasonList.size() != finReceivableBill.getItemList().size() || map.size() > 1) {
                        throw new BaseException("预收款明细的下单季必须一致！");
                    }
                }
            }
        }
    }

    /**
     * 校验部分明细金额不能为0
     */
    public void judgeNotNull(FinReceivableBill finReceivableBill) {
        if (CollectionUtil.isNotEmpty(finReceivableBill.getHuokuanList())) {
            if (finReceivableBill.getHuokuanList().stream().anyMatch(i->i.getCurrencyAmountTax()==null
                    || BigDecimal.ZERO.compareTo(i.getCurrencyAmountTax()) == 0)) {
                throw new BaseException("本次核销货款明细金额不能为空且不能填0！");
            }
        }
        if (CollectionUtil.isNotEmpty(finReceivableBill.getYushouList())) {
            if (finReceivableBill.getYushouList().stream().anyMatch(i->i.getCurrencyAmountTax()==null
                    || BigDecimal.ZERO.compareTo(i.getCurrencyAmountTax()) == 0)) {
                throw new BaseException("本次核销已预收款金额不能为空且不能填0！");
            }
        }
        if (CollectionUtil.isNotEmpty(finReceivableBill.getTuihuoList())) {
            if (finReceivableBill.getTuihuoList().stream().anyMatch(i->i.getCurrencyAmountTax()==null
                    || BigDecimal.ZERO.compareTo(i.getCurrencyAmountTax()) == 0)) {
                throw new BaseException("本次核销退货扣款金额不能为空且不能填0！");
            }
        }
        if (CollectionUtil.isNotEmpty(finReceivableBill.getKoukuanList())) {
            if (finReceivableBill.getKoukuanList().stream().anyMatch(i->i.getCurrencyAmountTax()==null
                    || BigDecimal.ZERO.compareTo(i.getCurrencyAmountTax()) == 0)) {
                throw new BaseException("本次核销扣款金额不能为空且不能填0！");
            }
        }
        if (CollectionUtil.isNotEmpty(finReceivableBill.getKegongliaoList())) {
            if (finReceivableBill.getKegongliaoList().stream().anyMatch(i->i.getCurrencyAmountTax()==null
                    || BigDecimal.ZERO.compareTo(i.getCurrencyAmountTax()) == 0)) {
                throw new BaseException("本次核销客供料扣款金额不能为空且不能填0！");
            }
        }
    }

    /**
     * 校验预收
     */
    public void judgeYu(FinReceivableBill finReceivableBill) {
        if (CollectionUtil.isEmpty(finReceivableBill.getYushouList())) {
            return;
        }
        if (ConstantsEms.SUBMIT_STATUS.equals(finReceivableBill.getHandleStatus()) ||
                ConstantsEms.CHECK_STATUS.equals(finReceivableBill.getHandleStatus())) {
            /**
             * 【仅提交时校验】若本次核销已预收款有明细，则明细的金额不能大于对应明细的待核销金额，
             * 提示：客户已预收款单号XXX的金额不能大于待核销金额！
             */
            for (FinReceivableBillItemYushou yushou : finReceivableBill.getYushouList()) {
                if (yushou.getCurrencyAmountTax() != null && yushou.getCurrencyAmountTax() != null
                        && yushou.getCurrencyAmountTax().compareTo(yushou.getCurrencyAmountTaxDhx()) > 0) {
                    throw new BaseException("客户已预收款单号 " + yushou.getReferDocCode() + " 的金额不能大于待核销金额！");
                }
            }
        }
    }

    /**
     * 校验扣款
     */
    public void judgeKoukuan(FinReceivableBill finReceivableBill) {
        if (CollectionUtil.isEmpty(finReceivableBill.getKoukuanList())) {
            return;
        }
        if (ConstantsEms.SUBMIT_STATUS.equals(finReceivableBill.getHandleStatus()) ||
                ConstantsEms.CHECK_STATUS.equals(finReceivableBill.getHandleStatus())) {
            /**
             * 【仅提交时校验】若本次核销扣款有明细，则明细的金额不能大于对应明细的待核销金额，
             * 提示：客户扣款单号XXX的金额不能大于待核销金额！
             */
            for (FinReceivableBillItemKoukuan koukuan : finReceivableBill.getKoukuanList()) {
                if (koukuan.getCurrencyAmountTax() != null && koukuan.getCurrencyAmountTax() != null
                        && koukuan.getCurrencyAmountTax().compareTo(koukuan.getCurrencyAmountTaxDhx()) > 0) {
                    throw new BaseException("客户扣款单号 " + koukuan.getReferDocCode() + " 的金额不能大于待核销金额！");
                }
            }
        }
    }

    /**
     * 校验发票明细
     */
    public void judgeInvoice(FinReceivableBill finReceivableBill) {
        if (CollectionUtil.isNotEmpty(finReceivableBill.getInvoiceList())) {
            if (finReceivableBill.getInvoiceList().stream().anyMatch(i -> i.getCurrencyAmountTax()==null
                    || BigDecimal.ZERO.compareTo(i.getCurrencyAmountTax()) == 0)) {
                throw new BaseException("本次核销发票台账的金额(含税)不能为空且不能填0！");
            }
        }
        if (ConstantsEms.YES.equals(finReceivableBill.getIsYoupiao())) {
            //if (CollectionUtil.isEmpty(finReceivableBill.getInvoiceList())) {
            //    throw new BaseException("本次核销发票台账明细不能为空！");
            //}
            // 若是否有票为“是”，且本次核销发票台账明细不为空，进行如下校验：
            if (ConstantsEms.SUBMIT_STATUS.equals(finReceivableBill.getHandleStatus()) ||
                    ConstantsEms.CHECK_STATUS.equals(finReceivableBill.getHandleStatus())) {
                /**
                 * 若本次核销发票台账明细的金额(含税)大于此明细的待收款金额(元)，
                 * 提示：本次核销发票台账XXXXX不能大于待收款金额！其中，XXXX为客户发票台账号
                 */
                for (FinReceivableBillItemInvoice invoice : finReceivableBill.getInvoiceList()) {
                    if (invoice.getCurrencyAmountTaxDai() != null && invoice.getCurrencyAmountTax() != null
                            && invoice.getCurrencyAmountTax().compareTo(invoice.getCurrencyAmountTaxDai()) > 0) {
                        throw new BaseException("本次核销发票台账 " + invoice.getCustomerInvoiceRecordCode() + " 的金额不能大于待收款金额！");
                    }
                }
            }
        }
    }

    /**
     * 校验金额不能大于待
     */
    public void judgeDai(FinReceivableBill finReceivableBill, SysDefaultSettingClient settingClient) {
        // 》若业务类型为“货款“，且实收明细不为空，进行如下校验：
        if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finReceivableBill.getBusinessType())) {

//            /**
//             * 若本次核销扣款明细的金额(含税)之和大于待核销扣款金额(元)，提示：本次核销扣款明细和不能大于待核销扣款！
//             */
//            // 本次核销扣款明细的金额(含税) 有值再校验
//            if (finReceivableBill.getHeXiaoKouKuan() != null) {
//                /**
//                 * 待核销扣款金额(元) 计算逻辑
//                 * 默认显示所有”核销状态“为”未核销“或”部分核销“，
//                 * 且客户与公司一致的客户扣款流水的未核销金额(未核销金额=扣款金额-核销中金额-已核销金额)的和
//                 */
//                BigDecimal daiXiaoKouKuan = finReceivableBill.getDaiXiaoKouKuan();
//                if (daiXiaoKouKuan != null && finReceivableBill.getHeXiaoKouKuan().compareTo(daiXiaoKouKuan) > 0) {
//                    throw new BaseException("本次核销扣款明细和不能大于待核销扣款！");
//                }
//            }
//            /**
//             * 若本次核销已预收款明细的金额(含税)之和大于待核销已预收款金额(元)，提示：本次核销已预收款明细和不能大于待核销已预收款！
//             */
//            if (finReceivableBill.getHeXiaoYiyushou() != null) {
//                /**
//                 * 待核销已预收款金额(元) 计算逻辑
//                 * 默认显示所有”核销状态“为”未核销“或”部分核销“，”流水来源类别“为”预收款“，
//                 * 且客户与公司一致的收款流水的未核销金额(未核销金额=收款金额-核销中金额-已核销金额)的和
//                 */
//                BigDecimal daiXiaoYiYuShouKuan = finReceivableBill.getDaiXiaoYiYuShouKuan();
//                if (daiXiaoYiYuShouKuan != null && finReceivableBill.getHeXiaoYiyushou().compareTo(daiXiaoYiYuShouKuan) > 0) {
//                    throw new BaseException("本次核销已预收款明细和不能大于待核销已预收款！");
//                }
//            }

            /**
             * •若系统默认设置的“收款核销方式”为“应收暂估”或“应收”，若本次核销货款大于待收货款金额(元)，提示：本次核销货款不能大于待收货款！
             * •若系统默认设置的“收款核销方式”为“应收暂估”或“应收”，若本次核销退货扣款明细的金额(含税)之和大于待核销退货扣款金额(元) (该字段取绝对值数据)，
             *  提示：本次核销退货扣款明细和不能大于待核销退货扣款！
             */
            if (settingClient != null && (ConstantsFinance.SHOUKUAN_ACC_CLEAR_WAY_YSZG.equals(settingClient.getShoukuanAccountClearWay())
                    || ConstantsFinance.SHOUKUAN_ACC_CLEAR_WAY_YS.equals(settingClient.getShoukuanAccountClearWay()))) {
                // 本次核销退货扣款明细的金额(含税)有值再校验
                if (finReceivableBill.getHeXiaoTuihuo() != null) {
                    BigDecimal daiXiaoTuiHuoKouKuan = finReceivableBill.getDaiXiaoTuiHuoKouKuan();
                    if (daiXiaoTuiHuoKouKuan != null && finReceivableBill.getHeXiaoTuihuo().compareTo(daiXiaoTuiHuoKouKuan) > 0) {
                        throw new BaseException("本次核销退货扣款明细和不能大于待核销退货扣款！");
                    }
                }
                // 本次核销货款有值再校验
                if (finReceivableBill.getHeXiaoHuokuan() != null) {
                    BigDecimal daiShouHuoKuan = finReceivableBill.getDaiShouHuoKuan();
                    if (daiShouHuoKuan != null && finReceivableBill.getHeXiaoHuokuan().compareTo(daiShouHuoKuan) > 0) {
                        throw new BaseException("本次核销货款不能大于待收货款！");
                    }
                }
            }
        }
    }

    /**
     * 默认值
     */
    public void setData(FinReceivableBill finReceivableBill) {
        finReceivableBill.setIsFinanceVerify(ConstantsEms.NO);
        //
        finReceivableBill.setCompanyCode(null);
        if (finReceivableBill.getCompanySid() != null) {
            BasCompany company = companyMapper.selectById(finReceivableBill.getCompanySid());
            if (company != null) {
                finReceivableBill.setCompanyCode(company.getCompanyCode());
            }
        }
        finReceivableBill.setCustomerCode(null);
        if (finReceivableBill.getCustomerSid() != null) {
            BasCustomer customer = customerMapper.selectById(finReceivableBill.getCustomerSid());
            if (customer != null) {
                finReceivableBill.setCustomerCode(customer.getCustomerCode());
            }
        }
        if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_YSK.equals(finReceivableBill.getBusinessType())) {
            if (CollectionUtil.isNotEmpty(finReceivableBill.getItemList())) {
                finReceivableBill.setProductSeasonSid(finReceivableBill.getItemList().get(0).getProductSeasonSid());
            }
        }
    }

    /**
     * 新增收款单
     * 需要注意编码重复校验
     *
     * @param finReceivableBill 收款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinReceivableBill(FinReceivableBill finReceivableBill) {
        SysDefaultSettingClient settingClient = getClientSetting();
        // 校验
        judge(finReceivableBill, settingClient);
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finReceivableBill.getHandleStatus())) {
            if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowSk())) {
                // 不需要走审批，提交及确认
                finReceivableBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(finReceivableBill.getHandleStatus())) {
            finReceivableBill.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        setData(finReceivableBill);
        int row = finReceivableBillMapper.insert(finReceivableBill);
        if (row > 0) {
            // 回写编码
            FinReceivableBill bill = finReceivableBillMapper.selectById(finReceivableBill.getReceivableBillSid());
            finReceivableBill.setReceivableBillCode(bill.getReceivableBillCode());
            // 写入明细
            if (CollectionUtil.isNotEmpty(finReceivableBill.getItemList())) {
                billItemService.insertByList(finReceivableBill);
            }
            // 写入发票明细
            if (CollectionUtil.isNotEmpty(finReceivableBill.getInvoiceList())) {
                invoiceService.insertByList(finReceivableBill);
            }
            // 货款页签 / 预收款页签
            if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finReceivableBill.getBusinessType())
                    || ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_YSK.equals(finReceivableBill.getBusinessType())) {
                // 写入本次核销已预收款明细列表
                if (CollectionUtil.isNotEmpty(finReceivableBill.getYushouList())) {
                    yushouService.insertByList(finReceivableBill);
                }
                // 货款
                if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finReceivableBill.getBusinessType())) {
                    // 写入本次核销货款明细列表
                    if (CollectionUtil.isNotEmpty(finReceivableBill.getHuokuanList())) {
                        huokuanService.insertByList(finReceivableBill);
                    }
                    // 写入本次核销扣款明细列表
                    if (CollectionUtil.isNotEmpty(finReceivableBill.getKoukuanList())) {
                        koukuanService.insertByList(finReceivableBill);
                    }
                    // 写入本次核销客供料扣款明细列表
                    if (CollectionUtil.isNotEmpty(finReceivableBill.getKegongliaoList())) {
                        kegongliaoService.insertByList(finReceivableBill);
                    }
                    // 写入本次核销退货扣款明细列表
                    if (CollectionUtil.isNotEmpty(finReceivableBill.getTuihuoList())) {
                        tuihuoService.insertByList(finReceivableBill);
                    }
                }
            }
            // 附件清单
            if (CollectionUtil.isNotEmpty(finReceivableBill.getAttachmentList())) {
                finReceivableBill.getAttachmentList().forEach(item->{
                    item.setClientId(ApiThreadLocalUtil.get().getSysUser().getClientId())
                            .setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                            .setReceivableBillSid(finReceivableBill.getReceivableBillSid());
                });
                attachmentMapper.inserts(finReceivableBill.getAttachmentList());
            }
            // 待办
            if (ConstantsEms.SAVA_STATUS.equals(finReceivableBill.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_FIN_RECEIVABLE_BILL)
                        .setDocumentSid(finReceivableBill.getReceivableBillSid());
                sysTodoTask.setTitle("收款单" + finReceivableBill.getReceivableBillCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(finReceivableBill.getReceivableBillCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskService.insertSysTodoTaskMenu(sysTodoTask, ConstantsWorkbench.TODO_FIN_RECEIVALE_BILL);
            }

            if (ConstantsEms.SUBMIT_STATUS.equals(finReceivableBill.getHandleStatus())
                    || ConstantsEms.CHECK_STATUS.equals(finReceivableBill.getHandleStatus())) {
                // 更新流水
                updateBookItem(finReceivableBill, settingClient);
                if (ConstantsEms.CHECK_STATUS.equals(finReceivableBill.getHandleStatus())) {
                    updateNewDateBookItem(finReceivableBill);
                }
                // 走提交审批，参数从查询页面提交按钮参考
                else if (ConstantsEms.SUBMIT_STATUS.equals(finReceivableBill.getHandleStatus())) {
                    this.submit(finReceivableBill);
                }
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinReceivableBill(), finReceivableBill);
            MongodbDeal.insert(finReceivableBill.getReceivableBillSid(), finReceivableBill.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 提交更新流水
     */
    private void updateBookItem(FinReceivableBill finReceivableBill, SysDefaultSettingClient settingClient) {
        if (finReceivableBill == null || settingClient == null) {
            return;
        }
        //若业务类型为“货款“，更新下述流水的数据如下：
        if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finReceivableBill.getBusinessType())) {
            if (ConstantsFinance.SHOUKUAN_ACC_CLEAR_WAY_YSZG.equals(settingClient.getShoukuanAccountClearWay())) {

                // 若系统默认设置的“收款核销模式“为”应收暂估“，按照创建日期(从最早的开始)，找到”核销状态“为”未核销“或”部分核销“，
                // 客户与公司一致 且 未被处理状态不是“已确认”的应收暂估调价量单 引用 的 应收暂估流水
                List<FinBookReceiptEstimationItem> bookItemList = bookReceiptEstimationItemMapper.receivableSubmitSelect(new FinBookReceiptEstimationItem()
                        .setCustomerSid(finReceivableBill.getCustomerSid()).setCompanySid(finReceivableBill.getCompanySid()));
                if (CollectionUtil.isNotEmpty(bookItemList)) {
                    bookItemList = bookItemList.stream().filter(i->i.getClearStatus().equals(ConstantsFinance.CLEAR_STATUS_WHX)
                            || i.getClearStatus().equals(ConstantsFinance.CLEAR_STATUS_BFHX)).collect(Collectors.toList());
                }
                if (CollectionUtil.isNotEmpty(bookItemList)) {
                    // 本次核销货款 必须要有值
                    // 更新对应待核销金额为核销中金额并根据核销情况更新核销状态
                    // 若系统默认设置的“收款核销模式“为”应收“，暂不实现
                    // 若系统默认设置的“收款核销模式“为“无”，无需更新数据
                    if (finReceivableBill.getHeXiaoHuokuan() != null && BigDecimal.ZERO.compareTo(finReceivableBill.getHeXiaoHuokuan()) != 0) {
                        BigDecimal heXiaoHuokuan = finReceivableBill.getHeXiaoHuokuan();
                        // ”是否退货“为”否“
                        List<FinBookReceiptEstimationItem> noTuihuo = bookItemList.stream().filter(i->ConstantsEms.NO.equals(i.getIsTuihuo())).collect(Collectors.toList());
                        for (FinBookReceiptEstimationItem bookItem : noTuihuo) {
                            if (bookItem.getCurrencyAmountTaxLeft() != null && BigDecimal.ZERO.compareTo(bookItem.getCurrencyAmountTaxLeft()) != 0) {
                                // 如果待核销的 比 本次核销货款 多 ，则将货款全部核销到该笔
                                if (bookItem.getCurrencyAmountTaxLeft().abs().compareTo(heXiaoHuokuan.abs()) >= 0) {
                                    // 记录到日志表里
                                    FinClearLogReceiptEstimation logBook = new FinClearLogReceiptEstimation();
                                    logBook.setReceivableBillSid(finReceivableBill.getReceivableBillSid())
                                            .setReceivableBillCode(finReceivableBill.getReceivableBillCode())
                                            .setCustomerSid(finReceivableBill.getCustomerSid()).setCustomerCode(finReceivableBill.getCustomerCode())
                                            .setCompanySid(finReceivableBill.getCompanySid()).setCompanyCode(finReceivableBill.getCompanyCode())
                                            .setBookReceiptEstimationSid(bookItem.getBookReceiptEstimationSid())
                                            .setBookReceiptEstimationCode(bookItem.getBookReceiptEstimationCode())
                                            .setBookReceiptEstimationItemSid(bookItem.getBookReceiptEstimationItemSid())
                                            .setIsTuihuo(bookItem.getIsTuihuo()).setShengxiaoStatus(ConstantsFinance.SHENGXIAO_STATUS_WSX)
                                            .setCurrencyAmountTax(heXiaoHuokuan);
                                    logReceiptEstimationMapper.insert(logBook);
                                    break;
                                }
                                else {
                                    // 如果待核销的 比 本次核销货款 少 ，则将该笔 待核销 全部 核销到 核销中
                                    // 记录到日志表里
                                    FinClearLogReceiptEstimation logBook = new FinClearLogReceiptEstimation();
                                    logBook.setReceivableBillSid(finReceivableBill.getReceivableBillSid())
                                            .setReceivableBillCode(finReceivableBill.getReceivableBillCode())
                                            .setCustomerSid(finReceivableBill.getCustomerSid()).setCustomerCode(finReceivableBill.getCustomerCode())
                                            .setCompanySid(finReceivableBill.getCompanySid()).setCompanyCode(finReceivableBill.getCompanyCode())
                                            .setBookReceiptEstimationSid(bookItem.getBookReceiptEstimationSid())
                                            .setBookReceiptEstimationCode(bookItem.getBookReceiptEstimationCode())
                                            .setBookReceiptEstimationItemSid(bookItem.getBookReceiptEstimationItemSid())
                                            .setIsTuihuo(bookItem.getIsTuihuo()).setShengxiaoStatus(ConstantsFinance.SHENGXIAO_STATUS_WSX)
                                            .setCurrencyAmountTax(bookItem.getCurrencyAmountTaxLeft());
                                    logReceiptEstimationMapper.insert(logBook);
                                    heXiaoHuokuan = heXiaoHuokuan.subtract(bookItem.getCurrencyAmountTaxLeft());
                                }
                            }
                        }
                    }

                    // 本次核销退货扣款 必须要有值
                    // 根据 绝对值 更新对应待核销金额为核销中金额并根据核销情况更新核销状态
                    // 若系统默认设置的“收款核销模式“为”应收“，暂不实现
                    // 若系统默认设置的“收款核销模式“为“无”，无需更新数据
                    if (finReceivableBill.getHeXiaoTuihuo() != null && BigDecimal.ZERO.compareTo(finReceivableBill.getHeXiaoTuihuo()) != 0) {
                        BigDecimal heXiaoTuihuo = finReceivableBill.getHeXiaoTuihuo();
                        // ”是否退货“为”是“，
                        List<FinBookReceiptEstimationItem> yesTuihuo = bookItemList.stream().filter(i->ConstantsEms.YES.equals(i.getIsTuihuo())).collect(Collectors.toList());
                        // 注意：本次核销退货扣款为正数，请转换为负数再进行计算
                        BigDecimal heXiaoTuihuoFushu = heXiaoTuihuo.add(BigDecimal.ZERO);
                        if (heXiaoTuihuoFushu.compareTo(BigDecimal.ZERO) > 0) {
                            heXiaoTuihuoFushu = heXiaoTuihuoFushu.multiply(new BigDecimal("-1"));
                        }
                        for (FinBookReceiptEstimationItem bookItem : yesTuihuo) {
                            if (bookItem.getCurrencyAmountTaxLeft() != null && BigDecimal.ZERO.compareTo(bookItem.getCurrencyAmountTaxLeft()) != 0) {
                                // 如果待核销的 比 本次核销货款 多 ，则将货款全部核销到该笔
                                if (bookItem.getCurrencyAmountTaxLeft().abs().compareTo(heXiaoTuihuoFushu.abs()) >= 0) {
                                    // 记录到日志表
                                    FinClearLogReceiptEstimation logBook = new FinClearLogReceiptEstimation();
                                    logBook.setReceivableBillSid(finReceivableBill.getReceivableBillSid())
                                            .setReceivableBillCode(finReceivableBill.getReceivableBillCode())
                                            .setCustomerSid(finReceivableBill.getCustomerSid()).setCustomerCode(finReceivableBill.getCustomerCode())
                                            .setCompanySid(finReceivableBill.getCompanySid()).setCompanyCode(finReceivableBill.getCompanyCode())
                                            .setBookReceiptEstimationSid(bookItem.getBookReceiptEstimationSid())
                                            .setBookReceiptEstimationCode(bookItem.getBookReceiptEstimationCode())
                                            .setBookReceiptEstimationItemSid(bookItem.getBookReceiptEstimationItemSid())
                                            .setIsTuihuo(bookItem.getIsTuihuo()).setShengxiaoStatus(ConstantsFinance.SHENGXIAO_STATUS_WSX)
                                            .setCurrencyAmountTax(heXiaoTuihuoFushu);
                                    logReceiptEstimationMapper.insert(logBook);
                                    break;
                                }
                                else {
                                    // 如果待核销的 比 本次核销货款 少 ，则将该笔 待核销 全部 核销到 核销中
                                    // 记录到日志表
                                    FinClearLogReceiptEstimation logBook = new FinClearLogReceiptEstimation();
                                    logBook.setReceivableBillSid(finReceivableBill.getReceivableBillSid())
                                            .setReceivableBillCode(finReceivableBill.getReceivableBillCode())
                                            .setCustomerSid(finReceivableBill.getCustomerSid()).setCustomerCode(finReceivableBill.getCustomerCode())
                                            .setCompanySid(finReceivableBill.getCompanySid()).setCompanyCode(finReceivableBill.getCompanyCode())
                                            .setBookReceiptEstimationSid(bookItem.getBookReceiptEstimationSid())
                                            .setBookReceiptEstimationCode(bookItem.getBookReceiptEstimationCode())
                                            .setBookReceiptEstimationItemSid(bookItem.getBookReceiptEstimationItemSid())
                                            .setIsTuihuo(bookItem.getIsTuihuo()).setShengxiaoStatus(ConstantsFinance.SHENGXIAO_STATUS_WSX)
                                            .setCurrencyAmountTax(bookItem.getCurrencyAmountTaxLeft());
                                    logReceiptEstimationMapper.insert(logBook);
                                    heXiaoTuihuoFushu = heXiaoTuihuoFushu.subtract(bookItem.getCurrencyAmountTaxLeft());
                                }
                            }
                        }
                    }
                }
            }

//            // 本次核销扣款
//            // 按照创建日期(从最早的开始)，找到”核销状态“为”未核销“或”部分核销“，且客户与公司一致的客户扣款流水，
//            // 更新对应待核销金额为核销中金额并根据核销情况更新核销状态
//            BigDecimal heXiaoKouKuan = finReceivableBill.getHeXiaoKouKuan();
//            if (heXiaoKouKuan != null && BigDecimal.ZERO.compareTo(heXiaoKouKuan) != 0) {
//                List<FinBookCustomerDeductionItem> bookKoukuanList = bookCustomerDeductionItemMapper.receivableSubmitSelect(new FinBookCustomerDeductionItem()
//                        .setCustomerSid(finReceivableBill.getCustomerSid()).setCompanySid(finReceivableBill.getCompanySid()));
//                if (CollectionUtil.isNotEmpty(bookKoukuanList)) {
//                    bookKoukuanList = bookKoukuanList.stream().filter(i->i.getClearStatus().equals(ConstantsFinance.CLEAR_STATUS_WHX)
//                            || i.getClearStatus().equals(ConstantsFinance.CLEAR_STATUS_BFHX)).collect(Collectors.toList());
//                }
//                if (CollectionUtil.isNotEmpty(bookKoukuanList)) {
//                    for (FinBookCustomerDeductionItem bookItem : bookKoukuanList) {
//                        if (bookItem.getCurrencyAmountTaxDhx() != null && BigDecimal.ZERO.compareTo(bookItem.getCurrencyAmountTaxDhx()) != 0) {
//                            // 如果待核销的 比 本次核销货款 多 ，则将货款全部核销到该笔
//                            if (bookItem.getCurrencyAmountTaxDhx().compareTo(heXiaoKouKuan) >= 0) {
//                                FinClearLogCustomerDeduction logBook = new FinClearLogCustomerDeduction();
//                                logBook.setReceivableBillSid(finReceivableBill.getReceivableBillSid())
//                                        .setReceivableBillCode(finReceivableBill.getReceivableBillCode())
//                                        .setCustomerSid(finReceivableBill.getCustomerSid()).setCustomerCode(finReceivableBill.getCustomerCode())
//                                        .setCompanySid(finReceivableBill.getCompanySid()).setCompanyCode(finReceivableBill.getCompanyCode())
//                                        .setBookDeductionSid(bookItem.getBookDeductionSid())
//                                        .setBookDeductionCode(bookItem.getBookDeductionCode())
//                                        .setBookDeductionItemSid(bookItem.getBookDeductionItemSid())
//                                        .setShengxiaoStatus(ConstantsFinance.SHENGXIAO_STATUS_WSX)
//                                        .setCurrencyAmountTax(heXiaoKouKuan);
//                                logCustomerDeductionMapper.insert(logBook);
//                                break;
//                            }
//                            else {
//                                // 如果待核销的 比 本次核销货款 少 ，则将该笔 待核销 全部 核销到 核销中
//                                FinClearLogCustomerDeduction logBook = new FinClearLogCustomerDeduction();
//                                logBook.setReceivableBillSid(finReceivableBill.getReceivableBillSid())
//                                        .setReceivableBillCode(finReceivableBill.getReceivableBillCode())
//                                        .setCustomerSid(finReceivableBill.getCustomerSid()).setCustomerCode(finReceivableBill.getCustomerCode())
//                                        .setCompanySid(finReceivableBill.getCompanySid()).setCompanyCode(finReceivableBill.getCompanyCode())
//                                        .setBookDeductionSid(bookItem.getBookDeductionSid())
//                                        .setBookDeductionCode(bookItem.getBookDeductionCode())
//                                        .setBookDeductionItemSid(bookItem.getBookDeductionItemSid())
//                                        .setShengxiaoStatus(ConstantsFinance.SHENGXIAO_STATUS_WSX)
//                                        .setCurrencyAmountTax(bookItem.getCurrencyAmountTaxDhx());
//                                logCustomerDeductionMapper.insert(logBook);
//                                heXiaoKouKuan = heXiaoKouKuan.subtract(bookItem.getCurrencyAmountTaxDhx());
//                            }
//                        }
//                    }
//                }
//            }
//
//            // 本次核销已预收款
//            // 按照创建日期(从最早的开始)，找到”核销状态“为”未核销“或”部分核销“，”流水来源类别“为”预收款“，
//            // 且客户与公司一致的收款流水，更新对应待核销金额为核销中金额并根据核销情况更新核销状态
//            BigDecimal heXiaoYiyushou = finReceivableBill.getHeXiaoYiyushou();
//            if (heXiaoYiyushou != null && BigDecimal.ZERO.compareTo(heXiaoYiyushou) != 0) {
//                List<FinBookReceiptPaymentItem> bookItemList = bookReceiptPaymentItemMapper.receivableSubmitSelect(new FinBookReceiptPaymentItem()
//                        .setCustomerSid(finReceivableBill.getCustomerSid()).setCompanySid(finReceivableBill.getCompanySid()));
//                if (CollectionUtil.isNotEmpty(bookItemList)) {
//                    bookItemList = bookItemList.stream().filter(i->i.getClearStatus().equals(ConstantsFinance.CLEAR_STATUS_WHX)
//                            || i.getClearStatus().equals(ConstantsFinance.CLEAR_STATUS_BFHX)).collect(Collectors.toList());
//                }
//
//                if (CollectionUtil.isNotEmpty(bookItemList)) {
//                    for (FinBookReceiptPaymentItem bookItem : bookItemList) {
//                        if (bookItem.getCurrencyAmountTaxDhx() != null && BigDecimal.ZERO.compareTo(bookItem.getCurrencyAmountTaxDhx()) != 0) {
//                            // 如果待核销的 比 本次核销货款 多 ，则将货款全部核销到该笔
//                            if (bookItem.getCurrencyAmountTaxDhx().compareTo(heXiaoYiyushou) >= 0) {
//                                FinClearLogAdvanceReceiptPayment logBook = new FinClearLogAdvanceReceiptPayment();
//                                logBook.setReceivableBillSid(finReceivableBill.getReceivableBillSid())
//                                        .setReceivableBillCode(finReceivableBill.getReceivableBillCode())
//                                        .setCustomerSid(finReceivableBill.getCustomerSid()).setCustomerCode(finReceivableBill.getCustomerCode())
//                                        .setCompanySid(finReceivableBill.getCompanySid()).setCompanyCode(finReceivableBill.getCompanyCode())
//                                        .setBookReceiptPaymentSid(bookItem.getBookReceiptPaymentSid())
//                                        .setBookReceiptPaymentCode(bookItem.getBookReceiptPaymentCode())
//                                        .setBookReceiptPaymentItemSid(bookItem.getBookReceiptPaymentItemSid())
//                                        .setShengxiaoStatus(ConstantsFinance.SHENGXIAO_STATUS_WSX)
//                                        .setCurrencyAmountTax(heXiaoYiyushou);
//                                logAdvanceReceiptPaymentMapper.insert(logBook);
//                                break;
//                            }
//                            else {
//                                // 如果待核销的 比 本次核销货款 少 ，则将该笔 待核销 全部 核销到 核销中
//                                FinClearLogAdvanceReceiptPayment logBook = new FinClearLogAdvanceReceiptPayment();
//                                logBook.setReceivableBillSid(finReceivableBill.getReceivableBillSid())
//                                        .setReceivableBillCode(finReceivableBill.getReceivableBillCode())
//                                        .setCustomerSid(finReceivableBill.getCustomerSid()).setCustomerCode(finReceivableBill.getCustomerCode())
//                                        .setCompanySid(finReceivableBill.getCompanySid()).setCompanyCode(finReceivableBill.getCompanyCode())
//                                        .setBookReceiptPaymentSid(bookItem.getBookReceiptPaymentSid())
//                                        .setBookReceiptPaymentCode(bookItem.getBookReceiptPaymentCode())
//                                        .setBookReceiptPaymentItemSid(bookItem.getBookReceiptPaymentItemSid())
//                                        .setShengxiaoStatus(ConstantsFinance.SHENGXIAO_STATUS_WSX)
//                                        .setCurrencyAmountTax(bookItem.getCurrencyAmountTaxDhx());
//                                logAdvanceReceiptPaymentMapper.insert(logBook);
//                                heXiaoYiyushou = heXiaoYiyushou.subtract(bookItem.getCurrencyAmountTaxDhx());
//                            }
//                        }
//                    }
//                }
//            }
        }

    }

    /**
     * 释放已经更新的流水
     */
    private void returnBookItem(FinReceivableBill old, SysDefaultSettingClient settingClient) {
        if (old == null || settingClient == null) {
            return;
        }
        //若业务类型为“货款“，更新下述流水的数据如下：
        if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(old.getBusinessType())) {
            if (ConstantsFinance.SHOUKUAN_ACC_CLEAR_WAY_YSZG.equals(settingClient.getShoukuanAccountClearWay())) {
                logReceiptEstimationMapper.delete(new QueryWrapper<FinClearLogReceiptEstimation>().lambda()
                        .eq(FinClearLogReceiptEstimation::getReceivableBillSid, old.getReceivableBillSid()));
//                logCustomerDeductionMapper.delete(new QueryWrapper<FinClearLogCustomerDeduction>().lambda()
//                        .eq(FinClearLogCustomerDeduction::getReceivableBillSid, old.getReceivableBillSid()));
//                logAdvanceReceiptPaymentMapper.delete(new QueryWrapper<FinClearLogAdvanceReceiptPayment>().lambda()
//                        .eq(FinClearLogAdvanceReceiptPayment::getReceivableBillSid, old.getReceivableBillSid()));
            }
        }
    }

    /**
     * 提交
     */
    private void submit(FinReceivableBill bill){
        Map<String, Object> variables = new HashMap<>();
        variables.put("formCode", bill.getReceivableBillCode());
        variables.put("formId", bill.getReceivableBillSid());
        variables.put("formType", FormType.ReceivableBill.getCode());
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
    public void updateAttach(FinReceivableBill bill) {
        // 先删后加
        attachmentMapper.delete(new QueryWrapper<FinReceivableBillAttachment>().lambda()
                .eq(FinReceivableBillAttachment::getReceivableBillSid, bill.getReceivableBillSid()));
        if (CollectionUtil.isNotEmpty(bill.getAttachmentList())) {
            bill.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getReceivableBillSid() == null) {
                    att.setClientId(ApiThreadLocalUtil.get().getSysUser().getClientId())
                            .setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                    att.setReceivableBillSid(bill.getReceivableBillSid());
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
     * 修改收款单
     *
     * @param finReceivableBill 收款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinReceivableBill(FinReceivableBill finReceivableBill) {
        SysDefaultSettingClient settingClient = getClientSetting();
        // 校验
        judge(finReceivableBill, settingClient);
        // 判断是否要走审批
        if (ConstantsEms.SUBMIT_STATUS.equals(finReceivableBill.getHandleStatus())) {
            if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowSk())) {
                // 不需要走审批，提交及确认
                finReceivableBill.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        FinReceivableBill original = this.selectFinReceivableBillById(finReceivableBill.getReceivableBillSid());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(finReceivableBill.getHandleStatus())) {
            finReceivableBill.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        setData(finReceivableBill);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, finReceivableBill);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finReceivableBill.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finReceivableBillMapper.updateAllById(finReceivableBill);
        if (row > 0) {
            // 写入明细
            billItemService.updateByList(finReceivableBill);
            // 写入发票明细
            invoiceService.updateByList(finReceivableBill);
            // 货款页签 / 预收款页签
            if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finReceivableBill.getBusinessType())
                    || ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_YSK.equals(finReceivableBill.getBusinessType())) {
                // 写入本次核销已预收款明细列表
                yushouService.updateByList(finReceivableBill);
                // 货款
                if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finReceivableBill.getBusinessType())) {
                    // 写入本次核销货款明细列表
                    huokuanService.updateByList(finReceivableBill);
                    // 写入本次核销扣款明细列表
                    koukuanService.updateByList(finReceivableBill);
                    // 写入本次核销客供料扣款明细列表
                    kegongliaoService.updateByList(finReceivableBill);
                    // 写入本次核销退货扣款明细列表
                    tuihuoService.updateByList(finReceivableBill);
                }
            }
            // 附件清单
            updateAttach(finReceivableBill);
            // 删除待办
            Long[] sids = new Long[]{finReceivableBill.getReceivableBillSid()};
            if (!ConstantsEms.SAVA_STATUS.equals(finReceivableBill.getHandleStatus())) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, finReceivableBill.getHandleStatus(), null);
            }
            if (ConstantsEms.SUBMIT_STATUS.equals(finReceivableBill.getHandleStatus())
                    || ConstantsEms.CHECK_STATUS.equals(finReceivableBill.getHandleStatus())) {
                // 更新流水
                updateBookItem(finReceivableBill, settingClient);
                // 走提交审批，参数从查询页面提交按钮参考
                if (ConstantsEms.SUBMIT_STATUS.equals(finReceivableBill.getHandleStatus())) {
                    this.submit(finReceivableBill);
                }
                else if (ConstantsEms.CHECK_STATUS.equals(finReceivableBill.getHandleStatus())) {
                    updateNewDateBookItem(finReceivableBill);
                }
            }
            //插入日志
            MongodbDeal.update(finReceivableBill.getReceivableBillSid(), original.getHandleStatus(),
                    finReceivableBill.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更收款单
     *
     * @param finReceivableBill 收款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinReceivableBill(FinReceivableBill finReceivableBill) {
        SysDefaultSettingClient settingClient = getClientSetting();
        // 校验
        judge(finReceivableBill, settingClient);
        FinReceivableBill response = this.selectFinReceivableBillById(finReceivableBill.getReceivableBillSid());
        setData(finReceivableBill);
        // 释放旧的引用流水
        FinReceivableBill original = this.selectFinReceivableBillById(finReceivableBill.getReceivableBillSid());
        returnBookItem(original, settingClient);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, finReceivableBill);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finReceivableBill.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finReceivableBillMapper.updateAllById(finReceivableBill);
        if (row > 0) {
            // 写入明细
            billItemService.updateByList(finReceivableBill);
            // 写入发票明细
            invoiceService.updateByList(finReceivableBill);
            // 货款页签 / 预收款页签
            if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finReceivableBill.getBusinessType())
                    || ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_YSK.equals(finReceivableBill.getBusinessType())) {
                // 写入本次核销已预收款明细列表
                yushouService.updateByList(finReceivableBill);
                // 货款
                if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(finReceivableBill.getBusinessType())) {
                    // 写入本次核销货款明细列表
                    huokuanService.updateByList(finReceivableBill);
                    // 写入本次核销扣款明细列表
                    koukuanService.updateByList(finReceivableBill);
                    // 写入本次核销客供料扣款明细列表
                    kegongliaoService.updateByList(finReceivableBill);
                    // 写入本次核销退货扣款明细列表
                    tuihuoService.updateByList(finReceivableBill);
                }
            }
            // 附件清单
            updateAttach(finReceivableBill);
            // 更新流水
            updateBookItem(finReceivableBill, settingClient);
            //插入日志
            MongodbUtil.insertUserLog(finReceivableBill.getReceivableBillSid(), BusinessType.CHANGE.getValue(), response, finReceivableBill, TITLE);
        }
        return row;
    }

    /**
     * 批量删除收款单
     *
     * @param receivableBillSids 需要删除的收款单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinReceivableBillByIds(List<Long> receivableBillSids) {
        if (CollectionUtil.isEmpty(receivableBillSids)) {
            return 0;
        }
        List<FinReceivableBill> list = new ArrayList<>();
        for (Long sid : receivableBillSids) {
            list.add(this.selectFinReceivableBillById(sid));
        }
        int row = finReceivableBillMapper.deleteBatchIds(receivableBillSids);
        if (row > 0) {
            Long[] sids = receivableBillSids.toArray(new Long[receivableBillSids.size()]);
            // 明细列表
            List<FinReceivableBillItem> itemList = billItemService.selectFinReceivableBillItemList
                    (new FinReceivableBillItem().setReceivableBillSidList(sids));
            if (CollectionUtil.isNotEmpty(itemList)) {
                billItemService.deleteByList(itemList);
            }
            // 发票台账明细列表
            List<FinReceivableBillItemInvoice> invoiceList = invoiceService.selectFinReceivableBillItemInvoiceList
                    (new FinReceivableBillItemInvoice().setReceivableBillSidList(sids));
            if (CollectionUtil.isNotEmpty(invoiceList)) {
                invoiceService.deleteByList(invoiceList);
            }
            // 本次核销已预收款明细列表
            List<FinReceivableBillItemYushou> yushouList = yushouService.selectFinReceivableBillItemYushouList
                    (new FinReceivableBillItemYushou().setReceivableBillSidList(sids));
            if (CollectionUtil.isNotEmpty(yushouList)) {
                yushouService.deleteByList(yushouList);
            }
            // 本次核销扣款明细列表
            List<FinReceivableBillItemKoukuan> koukuanList = koukuanService.selectFinReceivableBillItemKoukuanList
                    (new FinReceivableBillItemKoukuan().setReceivableBillSidList(sids));
            if (CollectionUtil.isNotEmpty(koukuanList)) {
                koukuanService.deleteByList(koukuanList);
            }
            // 本次核销客供料扣款明细列表
            List<FinReceivableBillItemKoukuanKegongliao> kegongliaoList = kegongliaoService.selectFinReceivableBillItemKoukuanKegongliaoList
                    (new FinReceivableBillItemKoukuanKegongliao().setReceivableBillSidList(sids));
            if (CollectionUtil.isNotEmpty(kegongliaoList)) {
                kegongliaoService.deleteByList(kegongliaoList);
            }
            // 本次核销退货扣款明细列表
            List<FinReceivableBillItemKoukuanTuihuo> tuihuoList = tuihuoService.selectFinReceivableBillItemKoukuanTuihuoList
                    (new FinReceivableBillItemKoukuanTuihuo().setReceivableBillSidList(sids));
            if (CollectionUtil.isNotEmpty(tuihuoList)) {
                tuihuoService.deleteByList(tuihuoList);
            }
            // 本次核销退货扣款明细列表
            List<FinReceivableBillItemHuokuan> huokuanList = huokuanService.selectFinReceivableBillItemHuokuanList
                    (new FinReceivableBillItemHuokuan().setReceivableBillSidList(sids));
            if (CollectionUtil.isNotEmpty(huokuanList)) {
                huokuanService.deleteByList(huokuanList);
            }
            // 删除附件
            attachmentMapper.delete(new QueryWrapper<FinReceivableBillAttachment>().lambda()
                    .in(FinReceivableBillAttachment::getReceivableBillSid, sids));
            // 删除待办
            sysTodoTaskService.deleteSysTodoTaskList(sids, null, null);
            // 操作日志
            //SysDefaultSettingClient settingClient = getClientSetting();
            list.forEach(o -> {
                // 操作日志
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FinReceivableBill());
                MongodbUtil.insertUserLog(o.getReceivableBillSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 更改处理状态
     */
    private int updateHandle(Long[] sids, String handleStatus) {
        LambdaUpdateWrapper<FinReceivableBill> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FinReceivableBill::getReceivableBillSid, sids);
        updateWrapper.set(FinReceivableBill::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(FinReceivableBill::getConfirmDate, new Date());
            updateWrapper.set(FinReceivableBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return finReceivableBillMapper.update(null, updateWrapper);
    }

    /**
     * 工作流流程
     */
    public int workFlow(FinReceivableBill finReceivableBill) {
        int row = 1;
        Long[] sids = finReceivableBill.getReceivableBillSidList();
        // 处理状态
        String handleStatus = finReceivableBill.getHandleStatus();
        if (StrUtil.isNotBlank(finReceivableBill.getOperateType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(finReceivableBill.getOperateType());
        }
        if (sids != null && sids.length > 0) {
            // 获取数据
            List<FinReceivableBill> billList = finReceivableBillMapper.selectFinReceivableBillList
                    (new FinReceivableBill().setReceivableBillSidList(sids));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus,
                        ConstantsTable.TABLE_FIN_RECEIVABLE_BILL);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(finReceivableBill.getOperateType())) {
                // 修改处理状态
                row = this.updateHandle(sids, ConstantsEms.SUBMIT_STATUS);
                // 开启工作流
                for (int i = 0; i < billList.size(); i++) {
                    this.submit(billList.get(i));
                    FinReceivableBill bill = finReceivableBillMapper.selectById(billList.get(i).getReceivableBillSid());
                    //插入日志
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(billList.get(i), bill);
                    MongodbUtil.insertUserLog(billList.get(i).getReceivableBillSid(),
                            BusinessType.SUBMIT.getValue(), msgList, TITLE, finReceivableBill.getComment());
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(finReceivableBill.getOperateType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                for (int i = 0; i < billList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(billList.get(i).getReceivableBillSid().toString());
                    taskVo.setFormId(Long.valueOf(billList.get(i).getReceivableBillSid().toString()));
                    taskVo.setFormCode(billList.get(i).getReceivableBillCode().toString());
                    taskVo.setFormType(FormType.ReceivableBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finReceivableBill.getComment());
                    try {
                        SysFormProcess process = workflowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getReceivableBillSid()}, ConstantsEms.CHECK_STATUS);
                            // 确认成功 最近被收款单引用日期
                            updateNewDateBookItem(billList.get(i));
                        }
                        finReceivableBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    FinReceivableBill bill = finReceivableBillMapper.selectById(billList.get(i).getReceivableBillSid());
                    //插入日志
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(billList.get(i), bill);
                    MongodbUtil.insertUserLog(billList.get(i).getReceivableBillSid(),
                            BusinessType.APPROVAL.getValue(), msgList, TITLE, finReceivableBill.getComment());
                }
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(finReceivableBill.getOperateType())) {
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                // 审批意见
                String comment = "";
                for (int i = 0; i < billList.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setTargetKey("2");
                    taskVo.setBusinessKey(billList.get(i).getReceivableBillSid().toString());
                    taskVo.setFormId(Long.valueOf(billList.get(i).getReceivableBillSid().toString()));
                    taskVo.setFormCode(billList.get(i).getReceivableBillCode().toString());
                    taskVo.setFormType(FormType.ReceivableBill.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(finReceivableBill.getComment());
                    try {
                        SysFormProcess process = workflowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 修改处理状态
                            row = this.updateHandle(new Long[]{billList.get(i).getReceivableBillSid()}, HandleStatus.RETURNED.getCode());
                        }
                        finReceivableBill.setComment(process.getRemark());
                    } catch (BaseException e) {
                        throw e;
                    }
                    FinReceivableBill bill = finReceivableBillMapper.selectById(billList.get(i).getReceivableBillSid());
                    //插入日志
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(billList.get(i), bill);
                    MongodbUtil.insertUserLog(billList.get(i).getReceivableBillSid(),
                            BusinessType.APPROVAL.getValue(), msgList, TITLE, finReceivableBill.getComment());
                }
            }
        }
        return row;
    }

    /**
     * 提交时校验
     */
    @Override
    public EmsResultEntity submitVerify(FinReceivableBill finReceivableBill) {
        SysDefaultSettingClient settingClient = getClientSetting();
        // 校验
        judge(finReceivableBill, settingClient);
        //
        if (CollectionUtil.isNotEmpty(finReceivableBill.getItemList())) {
            boolean season = finReceivableBill.getItemList().stream().anyMatch(o -> o.getProductSeasonSid() == null);
            boolean method = finReceivableBill.getItemList().stream().anyMatch(o -> StrUtil.isBlank(o.getPaymentMethod()));
            if (season || method) {
                return EmsResultEntity.warning(null, "存在本次实收明细的“下单季”或“收款方式”为空，是否继续操作？");
            }
        }
        return EmsResultEntity.success();
    }

    /**
     * 更改确认状态
     *
     * @param finReceivableBill
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(FinReceivableBill finReceivableBill) {
        int row = 0;
        Long[] sids = finReceivableBill.getReceivableBillSidList();
        if (sids != null && sids.length > 0) {
            SysDefaultSettingClient settingClient = getClientSetting();
            // 判断是否要走审批
            if (BusinessType.SUBMIT.getValue().equals(finReceivableBill.getOperateType())) {
                Map<Long, FinReceivableBill> map = new HashMap<>();
                for (Long sid : sids) {
                    FinReceivableBill bill = this.selectFinReceivableBillById(sid);
                    // 校验金额填写
                    bill.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
                    judgeItemAmount(bill, settingClient);
                    judgeDai(bill, settingClient);
                    // 用来记录旧数据
                    map.put(sid, bill);
                }
                if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowSk())) {
                    // 不需要走审批，提交及确认
                    LambdaUpdateWrapper<FinReceivableBill> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.in(FinReceivableBill::getReceivableBillSid,sids)
                            .set(FinReceivableBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                            .set(FinReceivableBill::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                            .set(FinReceivableBill::getConfirmDate, new Date());
                    row = finReceivableBillMapper.update(null, updateWrapper);
                    // 删除待办
                    sysTodoTaskService.deleteSysTodoTaskList(sids, ConstantsEms.CHECK_STATUS, null);
                    for (Long id : sids) {
                        FinReceivableBill bill = this.selectFinReceivableBillById(id);
                        // 更新流水
                        updateBookItem(bill, settingClient);
                        // 最近被收款单引用日期
                        updateNewDateBookItem(bill);
                        //插入日志
                        List<OperMsg> msgList = new ArrayList<>();
                        msgList = BeanUtils.eq(map.get(id), bill);
                        MongodbDeal.check(id, ConstantsEms.CHECK_STATUS, null, TITLE, null);
                    }
                    return row;
                }
            }
            // 走工作流程
            row = workFlow(finReceivableBill);
            // 处理流水  后续这里如果还要 就放到 workFlow 里去
            if (BusinessType.SUBMIT.getValue().equals(finReceivableBill.getOperateType())) {
                for (Long id : sids) {
                    FinReceivableBill bill = this.selectFinReceivableBillById(id);
                    // 更新流水
                    updateBookItem(bill, settingClient);
                }
            }
            //
            else if (BusinessType.DISAPPROVED.getValue().equals(finReceivableBill.getOperateType())) {
                for (Long id : sids) {
                    FinReceivableBill bill = this.selectFinReceivableBillById(id);
                    // 更新流水
                    returnBookItem(bill, settingClient);
                }
            }
        }
        return row;
    }

    /**
     * 2、收款单，把处理状态更新为“已确认”时，更新引用客户扣款流水、已预收流水的“最近被收款单引用日期”为当前操作日期到账
     */
    public void updateNewDateBookItem(FinReceivableBill bill) {
        List<FinReceivableBillItemKoukuan> koukuanList = bill.getKoukuanList();
        if (CollectionUtil.isEmpty(koukuanList)) {
            koukuanList = koukuanService.selectFinReceivableBillItemKoukuanList(new FinReceivableBillItemKoukuan().setReceivableBillSid(bill.getReceivableBillSid()));
        }
        if (CollectionUtil.isNotEmpty(koukuanList)) {
            Long[] bookSids = koukuanList.stream().map(FinReceivableBillItemKoukuan::getBookDeductionSid).toArray(Long[]::new);
            bookCustomerDeductionItemMapper.update(null, new UpdateWrapper<FinBookCustomerDeductionItem>().lambda()
                    .in(FinBookCustomerDeductionItem::getBookDeductionSid, bookSids)
                    .set(FinBookCustomerDeductionItem::getNewReceivableUseDate, new Date()));
        }
        //
        List<FinReceivableBillItemYushou> yushouList = bill.getYushouList();
        if (CollectionUtil.isEmpty(yushouList)) {
            yushouList = yushouService.selectFinReceivableBillItemYushouList(new FinReceivableBillItemYushou().setReceivableBillSid(bill.getReceivableBillSid()));
        }
        if (CollectionUtil.isNotEmpty(yushouList)) {
            Long[] bookSids = yushouList.stream().map(FinReceivableBillItemYushou::getBookReceiptPaymentSid).toArray(Long[]::new);
            bookReceiptPaymentItemMapper.update(null, new UpdateWrapper<FinBookReceiptPaymentItem>().lambda()
                    .in(FinBookReceiptPaymentItem::getBookReceiptPaymentSid, bookSids)
                    .set(FinBookReceiptPaymentItem::getNewReceivableUseDate, new Date()));
        }
        //
        List<FinReceivableBillItemInvoice> invoiceList = bill.getInvoiceList();
        if (CollectionUtil.isEmpty(invoiceList)) {
            invoiceList = invoiceService.selectFinReceivableBillItemInvoiceList(new FinReceivableBillItemInvoice().setReceivableBillSid(bill.getReceivableBillSid()));
        }
        if (CollectionUtil.isNotEmpty(invoiceList)) {
            Long[] bookSids = invoiceList.stream().map(FinReceivableBillItemInvoice::getCustomerInvoiceRecordSid).toArray(Long[]::new);
            finCustomerInvoiceRecordMapper.update(null, new UpdateWrapper<FinCustomerInvoiceRecord>().lambda()
                    .in(FinCustomerInvoiceRecord::getCustomerInvoiceRecordSid, bookSids)
                    .set(FinCustomerInvoiceRecord::getNewReceivableUseDate, new Date()));
        }
    }

    /**
     * 到账
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int receipt(FinReceivableBill finReceivableBill) {
        int row = 0;
        Long[] sids = finReceivableBill.getReceivableBillSidList();
        if (sids != null && sids.length > 0) {
            if (finReceivableBill.getReceivableDate() == null) {
                throw new BaseException("到账日期不能为空！");
            }
            List<FinReceivableBill> list = finReceivableBillMapper.selectList(new QueryWrapper<FinReceivableBill>().lambda()
                    .in(FinReceivableBill::getReceivableBillSid, sids)
                    .eq(FinReceivableBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .ne(FinReceivableBill::getReceiptPaymentStatus, ConstantsFinance.RECEIPT_PAYMENT_STATUS_YDZ));
            if (CollectionUtil.isEmpty(list) || sids.length != list.size()) {
                throw new BaseException("所选数据不符合到账操作！");
            }
            Map<Long, FinReceivableBill> map = list.stream().collect(Collectors.toMap(FinReceivableBill::getReceivableBillSid, Function.identity()));
            // 更新状态
            LambdaUpdateWrapper<FinReceivableBill> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(FinReceivableBill::getReceivableBillSid, sids)
                    .set(FinReceivableBill::getReceiptPaymentStatus, ConstantsFinance.RECEIPT_PAYMENT_STATUS_YDZ)
                    .set(FinReceivableBill::getReceivableDate, finReceivableBill.getReceivableDate());
            row = finReceivableBillMapper.update(null, updateWrapper);
            for (Long sid : sids) {
                FinReceivableBill bill = this.selectFinReceivableBillById(sid);
                // 生成收款流水
                insertBook(bill);
                // 确认到账后 处理更新流水
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
    public int revocation(FinReceivableBill finReceivableBill) {
        int row = 0;
        Long[] sids = finReceivableBill.getReceivableBillSidList();
        if (sids != null && sids.length > 0) {
            if (finReceivableBill.getComment() == null) {
                throw new BaseException("撤回说明不能为空！");
            }
            List<FinReceivableBill> list = finReceivableBillMapper.selectList(new QueryWrapper<FinReceivableBill>().lambda()
                    .in(FinReceivableBill::getReceivableBillSid, sids)
                    .eq(FinReceivableBill::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .ne(FinReceivableBill::getReceiptPaymentStatus, ConstantsFinance.PAYMENT_STATUS_YZF));
            if (CollectionUtil.isEmpty(list) || sids.length != list.size()) {
                throw new BaseException("所选数据不符合撤回保存操作！");
            }
            Map<Long, FinReceivableBill> map = list.stream().collect(Collectors.toMap(FinReceivableBill::getReceivableBillSid, Function.identity()));
            // 更新状态
            LambdaUpdateWrapper<FinReceivableBill> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(FinReceivableBill::getReceivableBillSid, sids)
                    .set(FinReceivableBill::getHandleStatus, ConstantsEms.SAVA_STATUS);
            row = finReceivableBillMapper.update(null, updateWrapper);
            SysDefaultSettingClient settingClient = getClientSetting();
            String comment = finReceivableBill.getComment() == null ? "" : "撤回说明：" + finReceivableBill.getComment();
            for (Long sid : sids) {
                FinReceivableBill bill = this.selectFinReceivableBillById(sid);
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
    public int setIsyoupiao(FinReceivableBill finReceivableBill) {
        int row = 0;
        Long[] sids = finReceivableBill.getReceivableBillSidList();
        if (sids != null && sids.length > 0) {
            if (StrUtil.isBlank(finReceivableBill.getIsYoupiao())) {
                throw new BaseException("是否有票不能为空！");
            }
            List<FinReceivableBill> billList = finReceivableBillMapper.selectList(new QueryWrapper<FinReceivableBill>().lambda()
                    .in(FinReceivableBill::getReceivableBillSid, sids).and(que ->
                            que.ne(FinReceivableBill::getIsYoupiao, finReceivableBill.getIsYoupiao())
                                    .or().isNull(FinReceivableBill::getIsYoupiao)));
            if (CollectionUtil.isEmpty(billList)) {
                return row;
            }
            sids = billList.stream().map(FinReceivableBill::getReceivableBillSid).toArray(Long[]::new);
            // 必填校验通过后，判断所选收款单是否存在本次核销发票台账明细，
            // 若存在，提示：收款单XXX存在核销发票台账明细，操作失败！其中，XXX为收款单号
            // 上述校验均通过，更新所选收款单的相关信息，并记录操作日志如下：
            // 操作类型：变更 详情：修改是否有票，更改前：XXX，更改后：XXX
            List<FinReceivableBillItemInvoice> invoiceList = invoiceService.selectFinReceivableBillItemInvoiceList
                    (new FinReceivableBillItemInvoice().setReceivableBillSidList(sids));
            if (CollectionUtil.isNotEmpty(invoiceList)) {
                throw new BaseException("收款单" + invoiceList.get(0).getReceivableBillCode() + "存在核销发票台账明细，操作失败！");
            }
            LambdaUpdateWrapper<FinReceivableBill> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(FinReceivableBill::getReceivableBillSid,sids)
                    .set(FinReceivableBill::getIsYoupiao, finReceivableBill.getIsYoupiao());
            row = finReceivableBillMapper.update(new FinReceivableBill(), updateWrapper);
            if (row > 0) {
                // 是否有票数据字典
                List<DictData> yesnoList = sysDictDataService.selectDictData("s_yesno_flag");
                Map<String, String> yesnoMaps = yesnoList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));

                for (FinReceivableBill bill : billList) {
                    String old = yesnoMaps.get(bill.getIsYoupiao()) == null ? "" : yesnoMaps.get(bill.getIsYoupiao());
                    String remark = "修改是否有票，更改前：" + old + "，更改后：" + yesnoMaps.get(finReceivableBill.getIsYoupiao());
                    // 操作日志
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.setDiff(bill, "isYoupiao", bill.getIsYoupiao(), finReceivableBill.getIsYoupiao(), msgList);
                    MongodbUtil.insertUserLog(bill.getReceivableBillSid(), BusinessType.CHANGE.getValue(), msgList, TITLE, remark);
                }
            }
        }
        return row;
    }

    /**
     * 生成收款流水
     */
    public void insertBook(FinReceivableBill bill) {
        if (CollectionUtil.isNotEmpty(bill.getItemList())) {
            BigDecimal currencyAmountTax = bill.getCurrencyAmountTax();

            FinBookReceiptPayment receiptpayment = new FinBookReceiptPayment();
            receiptpayment.setBookType(ConstantsFinance.BOOK_TYPE_SK)
                    .setCustomerSid(bill.getCustomerSid()).setCustomerCode(bill.getCustomerCode())
                    .setCompanySid(bill.getCompanySid()).setCompanyCode(bill.getCompanyCode())
                    .setCurrency(bill.getCurrency()).setCurrencyUnit(bill.getCurrencyUnit())
                    .setHandleStatus(bill.getHandleStatus()).setIsFinanceVerify(ConstantsEms.NO);
            Calendar cal = Calendar.getInstance();
            receiptpayment.setDocumentDate(new Date())
                    .setPaymentYear(Long.parseLong(String.valueOf(cal.get(Calendar.YEAR))))
                    .setPaymentMonth(cal.get(Calendar.MONTH) + 1);
            receiptpayment.setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                    .setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            // 明细
            FinBookReceiptPaymentItem receiptpaymentItem = new FinBookReceiptPaymentItem();
            receiptpaymentItem.setReferDocSid(bill.getReceivableBillSid())
                    .setReferDocCode(bill.getReceivableBillCode())
                    .setReferDocCategory(bill.getAccountCategory())
                    .setBookReceiptPaymentSid(receiptpayment.getBookReceiptPaymentSid())
                    .setItemNum((long)1).setIsFinanceVerify(ConstantsEms.NO)
                    .setCurrencyAmountTaxSk(currencyAmountTax)
                    .setCurrencyAmountTaxYhx(BigDecimal.ZERO)
                    .setCurrencyAmountTaxHxz(BigDecimal.ZERO);
            receiptpaymentItem.setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            //①若业务类型为“其它收款”
            //生成核销状态为“全部核销”，流水来源类别为“其它收款”的收款流水(汇总收款单明细的金额和，仅生成一笔流水明细)
            if (ConstantsFinance.BUSINESS_TYPE_QTSK.equals(bill.getBusinessType())) {
                receiptpayment.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_QTSK);
                receiptpaymentItem.setCurrencyAmountTaxYhx(currencyAmountTax);
                receiptpaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                receiptpayment.setItemList(new ArrayList<FinBookReceiptPaymentItem>(){{add(receiptpaymentItem);}});
                finBookReceiptPaymentService.insertFinBookReceiptPayment(receiptpayment);
            }
            //③若业务类型为“货款”
            //(1)生成核销状态为“全部核销”，流水来源类别为“货款”的收款流水(汇总收款单明细的金额和，仅生成一笔流水明细)
            else if (ConstantsFinance.RECEIVABLE_BILL_BUSINESS_TYPE_HK.equals(bill.getBusinessType())) {
                receiptpayment.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_HK);
                receiptpaymentItem.setCurrencyAmountTaxYhx(currencyAmountTax);
                receiptpaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_QHX);
                receiptpayment.setItemList(new ArrayList<FinBookReceiptPaymentItem>(){{add(receiptpaymentItem);}});
                finBookReceiptPaymentService.insertFinBookReceiptPayment(receiptpayment);
            }
            //②若业务类型为“预收款”
            //生成核销状态为“未核销”，流水来源类别为“预收款”的收款流水(汇总收款单明细的金额和，仅生成一笔流水明细)
            else if (ConstantsFinance.BUSINESS_TYPE_YUSK.equals(bill.getBusinessType())) {
                receiptpayment.setBookSourceCategory(ConstantsFinance.BOOK_SOURCE_CAT_YSK);
                receiptpaymentItem.setClearStatus(ConstantsFinance.CLEAR_STATUS_WHX);
                receiptpayment.setItemList(new ArrayList<FinBookReceiptPaymentItem>(){{add(receiptpaymentItem);}});
                finBookReceiptPaymentService.insertFinBookReceiptPayment(receiptpayment);
            }
        }
    }

    /**
     * 确认到账后 处理更新流水
     * ## 请检查是否需要提前计算某些金额数据 ##
     */
    public void updateBookConfirm(FinReceivableBill bill) {
        SysDefaultSettingClient settingClient = getClientSetting();
        // ●应收暂估流水
        if (ConstantsFinance.SHOUKUAN_ACC_CLEAR_WAY_YSZG.equals(settingClient.getShoukuanAccountClearWay())) {
            //根据收款单的核销应收暂估日志的核销金额，更新应收暂估流水的已核销金额，并根据核销情况更新核销状态
            List<FinClearLogReceiptEstimation> logs = logReceiptEstimationMapper.selectList(new QueryWrapper<FinClearLogReceiptEstimation>().lambda()
                    .eq(FinClearLogReceiptEstimation::getReceivableBillSid, bill.getReceivableBillSid())
                    .eq(FinClearLogReceiptEstimation::getShengxiaoStatus, ConstantsFinance.SHENGXIAO_STATUS_WSX));
            if (CollectionUtil.isNotEmpty(logs)) {
                for (FinClearLogReceiptEstimation log : logs) {
                    FinBookReceiptEstimationItem item = bookReceiptEstimationItemMapper.selectById(log.getBookReceiptEstimationItemSid());
                    // 流水写已核销
                    BigDecimal yhx = item.getCurrencyAmountTaxYhx().add(log.getCurrencyAmountTax());
                    item.setCurrencyAmountTaxYhx(yhx);
                    bookReceiptEstimationItemService.updateByAmountTax(item);
                }
                // 日志表改为已生效
                logReceiptEstimationMapper.update(null, new UpdateWrapper<FinClearLogReceiptEstimation>().lambda()
                        .set(FinClearLogReceiptEstimation::getShengxiaoStatus, ConstantsFinance.SHENGXIAO_STATUS_YSX)
                        .in(FinClearLogReceiptEstimation::getReceivableBillSid, bill.getReceivableBillSid()));
            }
        }
//        // ●客户扣款流水
//        //根据收款单的核销客户扣款日志的核销金额，更新客户扣款流水的已核销金额，并根据核销情况更新核销状态
//        List<FinClearLogCustomerDeduction> logs2 = logCustomerDeductionMapper.selectList(new QueryWrapper<FinClearLogCustomerDeduction>().lambda()
//                .eq(FinClearLogCustomerDeduction::getReceivableBillSid, bill.getReceivableBillSid())
//                .eq(FinClearLogCustomerDeduction::getShengxiaoStatus, ConstantsFinance.SHENGXIAO_STATUS_WSX));
//        if (CollectionUtil.isNotEmpty(logs2)) {
//            for (FinClearLogCustomerDeduction log : logs2) {
//                FinBookCustomerDeductionItem item = bookCustomerDeductionItemMapper.selectById(log.getBookDeductionItemSid());
//                // 流水写已核销
//                BigDecimal yhx = item.getCurrencyAmountTaxYhx().add(log.getCurrencyAmountTax());
//                item.setCurrencyAmountTaxYhx(yhx);
//                bookCustomerDeductionItemService.updateByAmountTax(item);
//            }
//            // 日志表改为已生效
//            logCustomerDeductionMapper.update(null, new UpdateWrapper<FinClearLogCustomerDeduction>().lambda()
//                    .set(FinClearLogCustomerDeduction::getShengxiaoStatus, ConstantsFinance.SHENGXIAO_STATUS_YSX)
//                    .in(FinClearLogCustomerDeduction::getReceivableBillSid, bill.getReceivableBillSid()));
//        }
//        // ●客户已预收款流水
//        //根据收款单的核销客户已预收款日志的核销金额，更新客户已预收款流水的已核销金额，并根据核销情况更新核销状态
//        List<FinClearLogAdvanceReceiptPayment> logs3 = logAdvanceReceiptPaymentMapper.selectList(new QueryWrapper<FinClearLogAdvanceReceiptPayment>().lambda()
//                .eq(FinClearLogAdvanceReceiptPayment::getReceivableBillSid, bill.getReceivableBillSid())
//                .eq(FinClearLogAdvanceReceiptPayment::getShengxiaoStatus, ConstantsFinance.SHENGXIAO_STATUS_WSX));
//        if (CollectionUtil.isNotEmpty(logs3)) {
//            for (FinClearLogAdvanceReceiptPayment log : logs3) {
//                FinBookReceiptPaymentItem item = bookReceiptPaymentItemMapper.selectById(log.getBookReceiptPaymentItemSid());
//                // 流水写已核销
//                BigDecimal yhx = item.getCurrencyAmountTaxYhx().add(log.getCurrencyAmountTax());
//                item.setCurrencyAmountTaxYhx(yhx);
//                bookReceiptPaymentItemService.updateByAmountTax(item);
//            }
//            // 日志表改为已生效
//            logAdvanceReceiptPaymentMapper.update(null, new UpdateWrapper<FinClearLogAdvanceReceiptPayment>().lambda()
//                    .set(FinClearLogAdvanceReceiptPayment::getShengxiaoStatus, ConstantsFinance.SHENGXIAO_STATUS_YSX)
//                    .in(FinClearLogAdvanceReceiptPayment::getReceivableBillSid, bill.getReceivableBillSid()));
//        }

    }

    /**
     * 更新发票台账 查询
     */
    @Override
    public FinReceivableBill invoiceList(FinReceivableBill finReceivableBill) {
        if (finReceivableBill != null) {
            // 发票台账明细列表
            finReceivableBill.setInvoiceList(new ArrayList<>());
            if (finReceivableBill.getReceivableBillSid() != null) {
                List<FinReceivableBillItemInvoice> invoiceList = invoiceService.selectFinReceivableBillItemInvoiceList
                        (new FinReceivableBillItemInvoice().setReceivableBillSid(finReceivableBill.getReceivableBillSid()));
                if (CollectionUtil.isNotEmpty(invoiceList)) {
                    finReceivableBill.setInvoiceList(invoiceList);
                }
            }
        }
        return finReceivableBill;
    }

    /**
     * 更新发票台账 更新
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int invoiceUpdate(FinReceivableBill finReceivableBill) {
        int row = 0;
        if (finReceivableBill != null && finReceivableBill.getReceivableBillSid() != null) {
            FinReceivableBill bill = finReceivableBillMapper.selectFinReceivableBillById(finReceivableBill.getReceivableBillSid());
            bill.setInvoiceList(finReceivableBill.getInvoiceList());
            // 校验
            judgeInvoice(bill);
            //
            List<FinReceivableBillItemInvoice> updateDateList = new ArrayList<>();
            if (ConstantsEms.CHECK_STATUS.equals(bill.getHandleStatus())) {
                // 原本明细
                List<FinReceivableBillItemInvoice> oldList = invoiceService.selectFinReceivableBillItemInvoiceList(new FinReceivableBillItemInvoice()
                        .setReceivableBillSid(bill.getReceivableBillSid()));
                if (CollectionUtil.isNotEmpty(oldList)) {
                    updateDateList.addAll(oldList);
                }
                // 现在的明细
                if (CollectionUtil.isNotEmpty(finReceivableBill.getInvoiceList())) {
                    updateDateList.addAll(finReceivableBill.getInvoiceList());
                }
            }
            // 更新发票明细
            row = invoiceService.updateByList(bill);
            //
            if (CollectionUtil.isNotEmpty(updateDateList)) {
                Long[] bookSids = updateDateList.stream().map(FinReceivableBillItemInvoice::getCustomerInvoiceRecordSid).distinct().toArray(Long[]::new);
                finCustomerInvoiceRecordMapper.update(null, new UpdateWrapper<FinCustomerInvoiceRecord>().lambda()
                        .in(FinCustomerInvoiceRecord::getCustomerInvoiceRecordSid, bookSids)
                        .set(FinCustomerInvoiceRecord::getNewReceivableUseDate, new Date()));
            }
        }
        return row;
    }
}

