package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IRepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 看板数据Service业务层处理
 *
 * @author chenkw
 * @date 2022-04-26
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class RepServiceImpl implements IRepService {

    @Autowired
    private RepBusinessRemindSoMapper businessRemindSoMapper;
    @Autowired
    private RepBusinessRemindPoMapper businessRemindPoMapper;
    @Autowired
    private RepBusinessRemindMoMapper businessRemindMoMapper;
    @Autowired
    private RepFinanceStatusMapper repFinanceStatusMapper;
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

    /**
     * 已逾期与即将逾期看板数据Service接口
     *
     * @author chenkw
     * @date 2022-04-26
     */
    public RepBusinessRemind getBusinessRemind(){
        LocalDateTime beginTime = LocalDateTime.now();
        // 返回实体
        RepBusinessRemind response = new RepBusinessRemind();
        /******************销售订单******************/
        // 销售订单：已逾期
        List<RepBusinessRemindSo> soyyqList = businessRemindSoMapper.selectList(new QueryWrapper<RepBusinessRemindSo>().lambda()
                .eq(RepBusinessRemindSo::getRemindType, ConstantsEms.YYQ));
        if (CollectionUtil.isNotEmpty(soyyqList)){
            List<RepBusinessRemindSo> yyqKuanSoList = new ArrayList<>();
            yyqKuanSoList.addAll(soyyqList); // 按款
            // 未发货量
            BigDecimal yyqSalOrderWfhl = soyyqList.stream().map(o -> null!=o.getQuantityWeifh()?o.getQuantityWeifh():BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 订单数
            soyyqList = soyyqList.stream().filter(distinctByKey(s->s.getSalesOrderSid())).collect(Collectors.toList());
            // 款数
            yyqKuanSoList = yyqKuanSoList.stream().filter(distinctByKey(s->s.getMaterialSid())).collect(Collectors.toList());
            response.setYyqSalOrderWfhl(yyqSalOrderWfhl.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString()).setYyqSalOrderDds(String.valueOf(soyyqList.size())).setYyqSalOrderKs(String.valueOf(yyqKuanSoList.size()));
        }
        // 销售订单：即将到期
        List<RepBusinessRemindSo> sojjdqList = businessRemindSoMapper.selectList(new QueryWrapper<RepBusinessRemindSo>().lambda()
                .eq(RepBusinessRemindSo::getRemindType, ConstantsEms.JJDQ));
        if (CollectionUtil.isNotEmpty(sojjdqList)){
            List<RepBusinessRemindSo> jjdqKuanSoList = new ArrayList<>();
            jjdqKuanSoList.addAll(sojjdqList); // 按款
            // 待发货量
            BigDecimal jjdqSalOrderDfhl = sojjdqList.stream().map(o -> null!=o.getQuantityWeifh()?o.getQuantityWeifh():BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 订单数
            sojjdqList = sojjdqList.stream().filter(distinctByKey(s->s.getSalesOrderSid())).collect(Collectors.toList());
            // 款数
            jjdqKuanSoList = jjdqKuanSoList.stream().filter(distinctByKey(s->s.getMaterialSid())).collect(Collectors.toList());
            response.setJjdqSalOrderDfhl(jjdqSalOrderDfhl.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString()).setJjdqSalOrderDds(String.valueOf(sojjdqList.size())).setJjdqSalOrderKs(String.valueOf(jjdqKuanSoList.size()));
        }
        /******************采购订单******************/
        // 采购订单已逾期
        List<RepBusinessRemindPo> poyyqList = businessRemindPoMapper.selectList(new QueryWrapper<RepBusinessRemindPo>().lambda()
                .eq(RepBusinessRemindPo::getRemindType, ConstantsEms.YYQ));
        if (CollectionUtil.isNotEmpty(poyyqList)){
            List<RepBusinessRemindPo> yyqKuanPoList = new ArrayList<>();
            yyqKuanPoList.addAll(poyyqList); // 按款
            // 未交货量
            BigDecimal yyqPurOrderWjhl = poyyqList.stream().map(o -> null!=o.getQuantityWeijh()?o.getQuantityWeijh():BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 订单数
            poyyqList = poyyqList.stream().filter(distinctByKey(s->s.getPurchaseOrderSid())).collect(Collectors.toList());
            // 款数
            yyqKuanPoList = yyqKuanPoList.stream().filter(distinctByKey(s->s.getMaterialSid())).collect(Collectors.toList());
            response.setYyqPurOrderWjhl(yyqPurOrderWjhl.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString()).setYyqPurOrderDds(String.valueOf(poyyqList.size())).setYyqPurOrderKs(String.valueOf(yyqKuanPoList.size()));
        }
        // 采购订单即将到期
        List<RepBusinessRemindPo> pojjdqList = businessRemindPoMapper.selectList(new QueryWrapper<RepBusinessRemindPo>().lambda()
                .eq(RepBusinessRemindPo::getRemindType, ConstantsEms.JJDQ));
        if (CollectionUtil.isNotEmpty(pojjdqList)){
            List<RepBusinessRemindPo> jjdqKuanPoList = new ArrayList<>();
            jjdqKuanPoList.addAll(pojjdqList); // 按款
            // 待交货量
            BigDecimal jjdqPurOrderWjhl = pojjdqList.stream().map(o -> null!=o.getQuantityWeijh()?o.getQuantityWeijh():BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 订单数
            poyyqList = poyyqList.stream().filter(distinctByKey(s->s.getPurchaseOrderSid())).collect(Collectors.toList());
            // 款数
            jjdqKuanPoList = jjdqKuanPoList.stream().filter(distinctByKey(s->s.getMaterialSid())).collect(Collectors.toList());
            response.setJjdqPurOrderDjhl(jjdqPurOrderWjhl.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString()).setJjdqPurOrderDds(String.valueOf(pojjdqList.size())).setJjdqPurOrderKs(String.valueOf(jjdqKuanPoList.size()));
        }
        /******************生产订单******************/
        // 生产订单已逾期
        List<RepBusinessRemindMo> moyyqList = businessRemindMoMapper.selectList(new QueryWrapper<RepBusinessRemindMo>().lambda()
                .eq(RepBusinessRemindMo::getRemindType, ConstantsEms.YYQ));
        if (CollectionUtil.isNotEmpty(moyyqList)){
            List<RepBusinessRemindMo> yyqKuanMoList = new ArrayList<>();
            yyqKuanMoList.addAll(moyyqList); // 按款
            // 未交货量
            BigDecimal yyqManOrderWwcl = moyyqList.stream().map(o -> null!=o.getQuantityWeiwc()?o.getQuantityWeiwc():BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 订单数
            moyyqList = moyyqList.stream().filter(distinctByKey(s->s.getManufactureOrderSid())).collect(Collectors.toList());
            // 款数
            yyqKuanMoList = yyqKuanMoList.stream().filter(distinctByKey(s->s.getMaterialSid())).collect(Collectors.toList());
            response.setYyqManOrderWwcl(yyqManOrderWwcl.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString()).setYyqManOrderDds(String.valueOf(moyyqList.size())).setYyqManOrderKs(String.valueOf(yyqKuanMoList.size()));
        }
        // 生产订单即将到期
        List<RepBusinessRemindMo> mojjdqList = businessRemindMoMapper.selectList(new QueryWrapper<RepBusinessRemindMo>().lambda()
                .eq(RepBusinessRemindMo::getRemindType, ConstantsEms.JJDQ));
        if (CollectionUtil.isNotEmpty(mojjdqList)){
            List<RepBusinessRemindMo> jjdqKuanMoList = new ArrayList<>();
            jjdqKuanMoList.addAll(mojjdqList); // 按款
            // 待交货量
            BigDecimal jjdqManOrderWwcl = mojjdqList.stream().map(o -> null!=o.getQuantityWeiwc()?o.getQuantityWeiwc():BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 订单数
            mojjdqList = mojjdqList.stream().filter(distinctByKey(s->s.getManufactureOrderSid())).collect(Collectors.toList());
            // 款数
            jjdqKuanMoList = jjdqKuanMoList.stream().filter(distinctByKey(s->s.getMaterialSid())).collect(Collectors.toList());
            response.setJjdqManOrderDwcl(jjdqManOrderWwcl.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString()).setJjdqManOrderDds(String.valueOf(mojjdqList.size())).setJjdqManOrderKs(String.valueOf(jjdqKuanMoList.size()));
        }
        log.info("=============> 已逾期与即将到期数据获取执行时间：" + Duration.between(beginTime,LocalDateTime.now()).toMillis() + " <=============");
        return response;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * 财务状况看板数据接口Service接口
     *
     * @author chenkw
     * @date 2022-05-07
     */
    public RepBusinessRemind getFinanceStatus(){
        LocalDateTime beginTime = LocalDateTime.now();
        // 返回实体
        RepBusinessRemind response = new RepBusinessRemind();
        List<RepFinanceStatus> statuseList = repFinanceStatusMapper.selectRepFinanceStatusList(new RepFinanceStatus());
        if (CollectionUtil.isNotEmpty(statuseList)){
            for (int i = 0; i < statuseList.size(); i++) {
                if ("待预收金额".equals(statuseList.get(i).getFieldName())){
                    response.setDaiys(statuseList.get(i).getFieldValue().setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
                    continue;
                }
                if ("应收金额".equals(statuseList.get(i).getFieldName())){
                    response.setYings(statuseList.get(i).getFieldValue().setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
                    continue;
                }
                if ("待销已收金额".equals(statuseList.get(i).getFieldName())){
                    response.setDaixys(statuseList.get(i).getFieldValue().setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
                    continue;
                }
                if ("待预付金额".equals(statuseList.get(i).getFieldName())){
                    response.setDaiyf(statuseList.get(i).getFieldValue().setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
                    continue;
                }
                if ("应付金额".equals(statuseList.get(i).getFieldName())){
                    response.setYingf(statuseList.get(i).getFieldValue().setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
                    continue;
                }
                if ("待销已付金额".equals(statuseList.get(i).getFieldName())){
                    response.setDaixyf(statuseList.get(i).getFieldValue().setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
                    continue;
                }
            }
        }
        log.info("=============> 财务状况数据获取执行时间：" + Duration.between(beginTime,LocalDateTime.now()).toMillis() + " <=============");
        return response;
    }
}
