package com.platform.ems.task.report;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.ems.util.CommonUtil;
import com.platform.system.mapper.SysClientMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 财务报表看板指标数据统计分析定时作业
 * @author chenkw
 */
@EnableScheduling
@Component
@SuppressWarnings("all")
@Slf4j
@Service
public class RepFinanceStatusTask {

    @Autowired
    private RepFinanceStatusDaiysMapper repFinanceStatusDaiysMapper;
    @Autowired
    private RepFinanceStatusYingsMapper repFinanceStatusYingsMapper;
    @Autowired
    private RepFinanceStatusDaixysMapper repFinanceStatusDaixysMapper;
    @Autowired
    private RepFinanceStatusDaiyfMapper repFinanceStatusDaiyfMapper;
    @Autowired
    private RepFinanceStatusYingfMapper repFinanceStatusYingfMapper;
    @Autowired
    private RepFinanceStatusDaixyfMapper repFinanceStatusDaixyfMapper;
    @Autowired
    private FunFundAccountMapper funFundAccountMapper;
    @Autowired
    private AssAssetRecordMapper assAssetRecordMapper;
    @Autowired
    private RepFinanceStatusMapper repFinanceStatusMapper;
    @Autowired
    private SysClientMapper clientMapper;

    @Transactional(rollbackFor = Exception.class)
    public void setRepFinanceStatus(){
        repFinanceStatusMapper.delete(new QueryWrapper<>());
        // 客户  收
        this.customer();
        // 供应商  付
        this.vendor();
        // 资金资产

    }

    /**
     *  开始统计客户数据
     *
     * @author chenkw
     */
    @Transactional(rollbackFor = Exception.class)
    public void customer(){
        log.info("=====>开始统计客户数据<=====");
        List<SysClient> clientList = clientMapper.selectList(new QueryWrapper<>());
        repFinanceStatusDaiysMapper.delete(new QueryWrapper<>());
        repFinanceStatusYingsMapper.delete(new QueryWrapper<>());
        repFinanceStatusDaixysMapper.delete(new QueryWrapper<>());
        for (SysClient client : clientList) {
            List<RepFinanceStatus> repFinanceStatusList = new ArrayList<>();
            //*****财务状况-客户-待预收
            List<RepFinanceStatusDaiys> daiysList = repFinanceStatusDaiysMapper.getRepFinanceStatusDaiysList(new RepFinanceStatusDaiys().setClientId(client.getClientId()));
            if (CollectionUtil.isNotEmpty(daiysList)){
                int daiys = repFinanceStatusDaiysMapper.inserts(daiysList);
                //求和并换算成万保留两位小数
                BigDecimal daiysMoney = daiysList.parallelStream().map(RepFinanceStatusDaiys::getMoneyAmountDaiys)
                        .reduce(BigDecimal.ZERO,BigDecimal::add);
                daiysMoney = daiysMoney.divide(new BigDecimal(10000),2,BigDecimal.ROUND_HALF_UP);
                //写入
                RepFinanceStatus financeStatusDaiys = new RepFinanceStatus();
                financeStatusDaiys.setClientId(client.getClientId());
                financeStatusDaiys.setStatisticType(ConstantsEms.STATISTIC_TYPE_YS).setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
                financeStatusDaiys.setFieldName(CommonUtil.getFieldName(new RepFinanceStatusDaiys(),"moneyAmountDaiys")).setFieldValue(daiysMoney);
                repFinanceStatusList.add(financeStatusDaiys);
            }
            //*****财务状况-客户-应收
            List<RepFinanceStatusYings> yingsList = repFinanceStatusYingsMapper.getRepFinanceStatusYingsList(new RepFinanceStatusYings().setClientId(client.getClientId()));
            if (CollectionUtil.isNotEmpty(yingsList)){
                int yings = repFinanceStatusYingsMapper.inserts(yingsList);
                //求和并换算成万保留两位小数
                BigDecimal yingsMoney = yingsList.parallelStream().map(RepFinanceStatusYings::getMoneyAmountYings)
                        .reduce(BigDecimal.ZERO,BigDecimal::add);
                yingsMoney = yingsMoney.divide(new BigDecimal(10000),2,BigDecimal.ROUND_HALF_UP);
                //写入
                RepFinanceStatus financeStatusYings = new RepFinanceStatus();
                financeStatusYings.setClientId(client.getClientId());
                financeStatusYings.setStatisticType(ConstantsEms.STATISTIC_TYPE_YS).setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
                financeStatusYings.setFieldName(CommonUtil.getFieldName(new RepFinanceStatusYings(),"moneyAmountYings")).setFieldValue(yingsMoney);
                repFinanceStatusList.add(financeStatusYings);
            }
            //*****财务状况-客户-待销已收
            List<RepFinanceStatusDaixys> daixysList = repFinanceStatusDaixysMapper.getRepFinanceStatusDaixysList(new RepFinanceStatusDaixys().setClientId(client.getClientId()));
            if (CollectionUtil.isNotEmpty(daixysList)){
                int daixys = repFinanceStatusDaixysMapper.inserts(daixysList);
                //求和并换算成万保留两位小数
                BigDecimal daixysMoney = yingsList.parallelStream().map(RepFinanceStatusYings::getMoneyAmountYings)
                        .reduce(BigDecimal.ZERO,BigDecimal::add);
                daixysMoney = daixysMoney.divide(new BigDecimal(10000),2,BigDecimal.ROUND_HALF_UP);
                //写入
                RepFinanceStatus financeStatusDaixys = new RepFinanceStatus();
                financeStatusDaixys.setClientId(client.getClientId());
                financeStatusDaixys.setStatisticType(ConstantsEms.STATISTIC_TYPE_YS).setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
                financeStatusDaixys.setFieldName(CommonUtil.getFieldName(new RepFinanceStatusDaixys(),"moneyAmountDaixys")).setFieldValue(daixysMoney);
                repFinanceStatusList.add(financeStatusDaixys);
            }
            //财务状况
            if (CollectionUtil.isNotEmpty(repFinanceStatusList)){
                repFinanceStatusMapper.inserts(repFinanceStatusList);
            }
        }
    }

    /**
     *  开始统计供应商数据
     *
     * @author chenkw
     */
    @Transactional(rollbackFor = Exception.class)
    public void vendor(){
        log.info("=====>开始统计供应商数据<=====");
        List<SysClient> clientList = clientMapper.selectList(new QueryWrapper<>());
        repFinanceStatusDaiyfMapper.delete(new QueryWrapper<>());
        repFinanceStatusYingfMapper.delete(new QueryWrapper<>());
        repFinanceStatusDaixyfMapper.delete(new QueryWrapper<>());
        for (SysClient client : clientList) {
            List<RepFinanceStatus> repFinanceStatusList = new ArrayList<>();
            //*****财务状况-供应商-待预付
            List<RepFinanceStatusDaiyf> daiyfList = repFinanceStatusDaiyfMapper.getRepFinanceStatusDaiyfList(new RepFinanceStatusDaiyf().setClientId(client.getClientId()));
            if (CollectionUtil.isNotEmpty(daiyfList)){
                int daiys = repFinanceStatusDaiyfMapper.inserts(daiyfList);
                //求和并换算成万保留两位小数
                BigDecimal daiyfMoney = daiyfList.parallelStream().map(RepFinanceStatusDaiyf::getMoneyAmountDaiyf)
                        .reduce(BigDecimal.ZERO,BigDecimal::add);
                daiyfMoney = daiyfMoney.divide(new BigDecimal(10000),2,BigDecimal.ROUND_HALF_UP);
                //写入
                RepFinanceStatus financeStatusDaiyf = new RepFinanceStatus();
                financeStatusDaiyf.setClientId(client.getClientId());
                financeStatusDaiyf.setStatisticType(ConstantsEms.STATISTIC_TYPE_YF).setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
                financeStatusDaiyf.setFieldName(CommonUtil.getFieldName(new RepFinanceStatusDaiyf(),"moneyAmountDaiyf")).setFieldValue(daiyfMoney);
                repFinanceStatusList.add(financeStatusDaiyf);
            }
            //*****财务状况-供应商-应付
            List<RepFinanceStatusYingf> yingfList = repFinanceStatusYingfMapper.getRepFinanceStatusYingfList(new RepFinanceStatusYingf().setClientId(client.getClientId()));
            if (CollectionUtil.isNotEmpty(yingfList)){
                int yingf = repFinanceStatusYingfMapper.inserts(yingfList);
                //求和并换算成万保留两位小数
                BigDecimal yingfMoney = yingfList.parallelStream().map(RepFinanceStatusYingf::getMoneyAmountYingf)
                        .reduce(BigDecimal.ZERO,BigDecimal::add);
                yingfMoney = yingfMoney.divide(new BigDecimal(10000),2,BigDecimal.ROUND_HALF_UP);
                //写入
                RepFinanceStatus financeStatusYingf = new RepFinanceStatus();
                financeStatusYingf.setClientId(client.getClientId());
                financeStatusYingf.setStatisticType(ConstantsEms.STATISTIC_TYPE_YF).setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
                financeStatusYingf.setFieldName(CommonUtil.getFieldName(new RepFinanceStatusYingf(),"moneyAmountYingf")).setFieldValue(yingfMoney);
                repFinanceStatusList.add(financeStatusYingf);
            }
            //*****财务状况-供应商-待销已付
            List<RepFinanceStatusDaixyf> daixyfList = repFinanceStatusDaixyfMapper.getRepFinanceStatusDaixyfList(new RepFinanceStatusDaixyf().setClientId(client.getClientId()));
            if (CollectionUtil.isNotEmpty(daixyfList)){
                int daixyf = repFinanceStatusDaixyfMapper.inserts(daixyfList);
                //求和并换算成万保留两位小数
                BigDecimal daixyfMoney = yingfList.parallelStream().map(RepFinanceStatusYingf::getMoneyAmountYingf)
                        .reduce(BigDecimal.ZERO,BigDecimal::add);
                daixyfMoney = daixyfMoney.divide(new BigDecimal(10000),2,BigDecimal.ROUND_HALF_UP);
                //写入
                RepFinanceStatus financeStatusDaixyf = new RepFinanceStatus();
                financeStatusDaixyf.setClientId(client.getClientId());
                financeStatusDaixyf.setStatisticType(ConstantsEms.STATISTIC_TYPE_YF).setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
                financeStatusDaixyf.setFieldName(CommonUtil.getFieldName(new RepFinanceStatusDaixyf(),"moneyAmountDaixyf")).setFieldValue(daixyfMoney);
                repFinanceStatusList.add(financeStatusDaixyf);
            }
            //财务状况
            if (CollectionUtil.isNotEmpty(repFinanceStatusList)){
                repFinanceStatusMapper.inserts(repFinanceStatusList);
            }
        }
    }

    /**
     *  开始统计供应商数据
     *
     * @author chenkw
     */
    @Transactional(rollbackFor = Exception.class)
    public void fundAndAsset(){
        log.info("=====>开始统计资金资产数据<=====");
        List<SysClient> clientList = clientMapper.selectList(new QueryWrapper<>());
        for (SysClient client : clientList) {
            List<RepFinanceStatus> repFinanceStatusList = new ArrayList<>();
            // 资金
            List<FunFundAccount> accountList = funFundAccountMapper.selectList(new QueryWrapper<FunFundAccount>().lambda()
                    .eq(FunFundAccount::getClientId,client.getClientId()));
            if (CollectionUtil.isNotEmpty(accountList)){

                //写入
                RepFinanceStatus financeStatusZj = new RepFinanceStatus();
                financeStatusZj.setClientId(client.getClientId());
                financeStatusZj.setStatisticType(ConstantsEms.STATISTIC_TYPE_ZJ).setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
                repFinanceStatusList.add(financeStatusZj);
            }
            //资产
            List<AssAssetRecord> assetList = assAssetRecordMapper.selectList(new QueryWrapper<AssAssetRecord>().lambda()
                    .eq(AssAssetRecord::getClientId,client.getClientId()));
            if (CollectionUtil.isNotEmpty(assetList)){

                //写入
                RepFinanceStatus financeStatusZc = new RepFinanceStatus();
                financeStatusZc.setClientId(client.getClientId());
                financeStatusZc.setStatisticType(ConstantsEms.STATISTIC_TYPE_ZC).setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
                repFinanceStatusList.add(financeStatusZc);
            }
            //财务状况
            if (CollectionUtil.isNotEmpty(repFinanceStatusList)){
                repFinanceStatusMapper.inserts(repFinanceStatusList);
            }
        }
    }

}
