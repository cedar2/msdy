package com.platform.ems.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Month;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.RepSalesStatusOrderTotal;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.mapper.RepSalesStatusOrderTotalMapper;
import com.platform.ems.service.IRepSalesStatusOrderTotalService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 销售状况-销售占比/销售趋势/销售同比Service业务层处理
 *
 * @author linhongwei
 * @date 2022-02-25
 */
@Service
@SuppressWarnings("all")
public class RepSalesStatusOrderTotalServiceImpl extends ServiceImpl<RepSalesStatusOrderTotalMapper, RepSalesStatusOrderTotal> implements IRepSalesStatusOrderTotalService {
    @Autowired
    private RepSalesStatusOrderTotalMapper repSalesStatusOrderTotalMapper;

    /**
     * 查询销售状况-销售占比/销售趋势/销售同比
     *
     * @param dataRecordSid 销售状况-销售占比/销售趋势/销售同比ID
     * @return 销售状况-销售占比/销售趋势/销售同比
     */
    @Override
    public RepSalesStatusOrderTotal selectRepSalesStatusOrderTotalById(Long dataRecordSid) {
        RepSalesStatusOrderTotal repSalesStatusOrderTotal = repSalesStatusOrderTotalMapper.selectRepSalesStatusOrderTotalById(dataRecordSid);
        MongodbUtil.find(repSalesStatusOrderTotal);
        return repSalesStatusOrderTotal;
    }

    // 销售状况-销售占比
    @Override
    public RepSalesStatusOrderTotalResponse getReport(String productSeasonCode) {
        List<RepSalesStatusOrderTotal> repSalesStatusOrderTotals = repSalesStatusOrderTotalMapper.selectList(new QueryWrapper<RepSalesStatusOrderTotal>().lambda()
                .eq(RepSalesStatusOrderTotal::getProductSeasonCode, productSeasonCode)
        );
        BigDecimal sum = repSalesStatusOrderTotals.stream().map(li -> li.getMoneyAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
        RepSalesStatusOrderTotalResponse repSalesStatusOrderTotal = new RepSalesStatusOrderTotalResponse();
        repSalesStatusOrderTotal.setSumMoneyAmount(sum);
        repSalesStatusOrderTotal.setProductSeasonCode(productSeasonCode);
        ArrayList<RepSalesStatusOrderTotalDivResponse> itemList = new ArrayList<>();
        //产品季和客户分组
        Map<String, List<RepSalesStatusOrderTotal>> list = repSalesStatusOrderTotals.stream().collect(Collectors.groupingBy(v -> v.getCustomerShortName()));
        list.keySet().stream().forEach(li -> {
            List<RepSalesStatusOrderTotal> totals = list.get(li);
            RepSalesStatusOrderTotalDivResponse item = new RepSalesStatusOrderTotalDivResponse();
            BigDecimal sumCu = totals.stream().map(m -> m.getMoneyAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
            item.setSumMoneyAmount(sumCu);
            item.setProportion(sumCu.divide(sum, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")));
            item.setCustomerName(li);
            itemList.add(item);
        });
        repSalesStatusOrderTotal.setItemList(itemList);
        return repSalesStatusOrderTotal;
    }

    /**
     * 查询销售趋势报表
     *
     */
    @Override
    public RepSalesStatusOrderTotalTrend geTrendt(String code) {
        RepSalesStatusOrderTotalTrend repSalesStatusOrderTotalTrend = new RepSalesStatusOrderTotalTrend();
        List<String> xList = new ArrayList<>();
        List<String> yList = new ArrayList<>();
        List<RepSalesStatusOrderItemTrendResponse> itemList = new ArrayList<>();
        Date date = new Date();
        //近三个月
        if("month".equals(code)){
            //当前月份最开始时间
            DateTime dateTimeMonthStart = DateUtil.beginOfMonth(date);
            DateTime offsetMonth = DateUtil.offsetMonth(dateTimeMonthStart, -3);
            List<RepSalesStatusOrderTotal> repSalesStatusOrderTotals=null;
            //当前月份
            int month = DateUtil.month(date)+1;
            int year = DateUtil.year(date);
            //近三月份
            if(month<3){
                int reduceMonth =12-(3- month);
                repSalesStatusOrderTotals = repSalesStatusOrderTotalMapper.selectList(new QueryWrapper<RepSalesStatusOrderTotal>().lambda()
                        .gt(RepSalesStatusOrderTotal::getMoth, reduceMonth)
                        .eq(RepSalesStatusOrderTotal::getYear,year-1)
                        .or()
                        .eq(RepSalesStatusOrderTotal::getYear,year)
                );
                for (int i = 1; i <= month; i++) {
                    xList.add(month+"月");
                }
                for (int i = 1; i <= 3- month; i++) {
                    int monthBe=12-(3- month);
                    xList.add(monthBe+"月");
                }
                if(CollectionUtils.isNotEmpty(repSalesStatusOrderTotals)){
                    Map<String, List<RepSalesStatusOrderTotal>> listMap = repSalesStatusOrderTotals.stream().collect((Collectors.groupingBy(v -> v.getBusinessChannelName())));
                    listMap.keySet().forEach(key->{
                        yList.add(key);
                        RepSalesStatusOrderItemTrendResponse item = new RepSalesStatusOrderItemTrendResponse();
                        List<BigDecimal> dataList=new ArrayList<>();
                        //当前年
                        for (int i = 1; i <= month; i++) {
                            List<RepSalesStatusOrderTotal> totals = listMap.get(key).stream().filter(li -> li.getYear() == year&& li.getMoth() == month).collect(Collectors.toList());
                            if(CollectionUtils.isNotEmpty(totals)){
                                BigDecimal total = totals.stream().map(li -> li.getMoneyAmount().multiply(li.getQuantity())).reduce(BigDecimal.ZERO, BigDecimal::add);
                                dataList.add(total);
                            }else{
                                dataList.add(BigDecimal.ZERO);
                            }
                        }
                        //去年
                        for (int i = 1; i <= 3- month; i++) {
                            int monthBe=12-(3- month);
                            List<RepSalesStatusOrderTotal> totals = listMap.get(key).stream().filter(li -> li.getYear() == year-1&& li.getMoth() == monthBe).collect(Collectors.toList());
                            if(CollectionUtils.isNotEmpty(totals)){
                                BigDecimal total = totals.stream().map(li -> li.getMoneyAmount().multiply(li.getQuantity())).reduce(BigDecimal.ZERO, BigDecimal::add);
                                dataList.add(total);
                            }else{
                                dataList.add(BigDecimal.ZERO);
                            }
                        }
                        item.setBusinessChannelName(key);
                        item.setDataList(dataList);
                        itemList.add(item);
                    });
                }
            }else{
                repSalesStatusOrderTotals = repSalesStatusOrderTotalMapper.selectList(new QueryWrapper<RepSalesStatusOrderTotal>().lambda()
                        .gt(RepSalesStatusOrderTotal::getMoth, month - 3)
                        .eq(RepSalesStatusOrderTotal::getYear,year)
                );
                for (int i = 2; i >=0 ; i--) {
                    int monthThird=month-i;
                    xList.add(monthThird+"月");
                }
                if(CollectionUtils.isNotEmpty(repSalesStatusOrderTotals)) {
                    Map<String, List<RepSalesStatusOrderTotal>> listMap = repSalesStatusOrderTotals.stream().collect((Collectors.groupingBy(v -> v.getBusinessChannelName())));
                    listMap.keySet().stream().forEach(key -> {
                        yList.add(key);
                        RepSalesStatusOrderItemTrendResponse item = new RepSalesStatusOrderItemTrendResponse();
                        List<BigDecimal> dataList = new ArrayList<>();
                        for (int i = 2; i >= 0; i--) {
                            int monthThird = month - i;
                            List<RepSalesStatusOrderTotal> totals = listMap.get(key).stream().filter(li -> li.getYear() == year && li.getMoth() == monthThird).collect(Collectors.toList());
                            if (CollectionUtils.isNotEmpty(totals)) {
                                BigDecimal total = totals.stream().map(li -> li.getMoneyAmount().multiply(li.getQuantity())).reduce(BigDecimal.ZERO, BigDecimal::add);
                                dataList.add(total);
                            } else {
                                dataList.add(BigDecimal.ZERO);
                            }
                        }
                        item.setBusinessChannelName(key);
                        item.setDataList(dataList);
                        itemList.add(item);
                    });
                }
            }
        }
        //近四周
        if("week".equals(code)){

        }
        //近七天
        if("week".equals(code)){

        }
        repSalesStatusOrderTotalTrend.setDataList(itemList)
                .setXList(xList)
                .setYList(yList);
        return repSalesStatusOrderTotalTrend;
    }
    //销售同比
    @Override
    public RepSalesStatusOrderProResponse getPro(String productSeasonCode){
        RepSalesStatusOrderProResponse response = new RepSalesStatusOrderProResponse();
        List<RepSalesStatusOrderProItemResponse> dateItemList=new ArrayList<>();
        List<String> yList = new ArrayList<>();
        List<String> xList = new ArrayList<>();
        String year = productSeasonCode.substring(0, 4);
        Integer lastYear = Integer.valueOf(year)-1;
        String code = productSeasonCode.substring(4);
        //去年产品季的code
        String lastProductSeasonCode=lastYear+code;
        yList.add(productSeasonCode);
        yList.add(lastProductSeasonCode);
        List<RepSalesStatusOrderTotal> repSalesStatusOrderTotals = repSalesStatusOrderTotalMapper.selectList(new QueryWrapper<RepSalesStatusOrderTotal>().lambda()
                .in(RepSalesStatusOrderTotal::getProductSeasonCode, new String[]{productSeasonCode,lastProductSeasonCode})
        );
        if(CollectionUtils.isNotEmpty(repSalesStatusOrderTotals)){
            Map<String, List<RepSalesStatusOrderTotal>> listMap = repSalesStatusOrderTotals.stream().collect(Collectors.groupingBy(v -> v.getBusinessChannelName()));
            listMap.keySet().stream().forEach(key->{
                xList.add(key);
                List<RepSalesStatusOrderProItemCodeResponse>  dateProItemList=new ArrayList<>();
                //今年
                List<RepSalesStatusOrderTotal> totals = listMap.get(key).stream().filter(li -> li.getProductSeasonCode().equals(productSeasonCode)).collect(Collectors.toList());
                RepSalesStatusOrderProItemCodeResponse itemtotals = new RepSalesStatusOrderProItemCodeResponse();
                itemtotals.setProductSeasonCode(productSeasonCode);
                if(CollectionUtils.isNotEmpty(totals)){
                    BigDecimal tatal = totals.stream().map(li -> li.getMoneyAmount().multiply(li.getQuantity())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    itemtotals.setTotalItem(tatal);
                }else{
                    itemtotals.setTotalItem(BigDecimal.ZERO);
                }
                dateProItemList.add(itemtotals);
                //去年
                List<RepSalesStatusOrderTotal> totalsLast = listMap.get(key).stream().filter(li -> li.getProductSeasonCode().equals(lastProductSeasonCode)).collect(Collectors.toList());
                RepSalesStatusOrderProItemCodeResponse itemtotalsLast = new RepSalesStatusOrderProItemCodeResponse();
                itemtotalsLast.setProductSeasonCode(lastProductSeasonCode);
                if(CollectionUtils.isNotEmpty(totalsLast)){
                    BigDecimal tatal = totalsLast.stream().map(li -> li.getMoneyAmount().multiply(li.getQuantity())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    itemtotalsLast.setTotalItem(tatal);
                }else{
                    itemtotalsLast.setTotalItem(BigDecimal.ZERO);
                }
                dateProItemList.add(itemtotalsLast);
                RepSalesStatusOrderProItemResponse salesStatusOrderProItemResponse = new RepSalesStatusOrderProItemResponse();
                salesStatusOrderProItemResponse.setBusinessChannelName(key);
                salesStatusOrderProItemResponse.setDateItemList(dateProItemList);
                dateItemList.add(salesStatusOrderProItemResponse);

            });
        }
        response.setDateItemList(dateItemList)
                .setXList(xList)
                .setYList(yList);
        return response;
    }
    public static void main(String[] args) {
        Date date = new Date();
        DateTime dateTimeWeek = DateUtil.lastWeek();
        System.out.println("上周时间：" + dateTimeWeek);
        DateTime dateTime = DateUtil.lastMonth();
        System.out.println("上个月时间：" + dateTime);

         //获得月份，从0开始计数
        int month = DateUtil.month(date)+1;
        System.out.println("获得月份：" + month);
         //获得月份枚举
        Month monthEnum = DateUtil.monthEnum(date);
        System.out.println("获得月份枚举：" + DateUtil.monthEnum(date));
        DateTime offsetMonth = DateUtil.offsetMonth(date, -3);
        System.out.println("偏移月分：" + offsetMonth);

        DateTime dateTime1 = DateUtil.beginOfMonth(date);
        System.out.println("当前月最开始时间：" + dateTime1);
        int year = DateUtil.year(date);
        System.out.println("当前年份：" + year);
        String productSeasonCode="2022SF";
        String ye = productSeasonCode.substring(0, 4);
        Integer lastYear = Integer.valueOf(ye)-1;
        String code = productSeasonCode.substring(4);
        String afterProductSeasonCode=lastYear+code;
        System.out.println("产品季：" + afterProductSeasonCode);
    }
    /**
     * 新增销售状况-销售占比/销售趋势/销售同比
     * 需要注意编码重复校验
     *
     * @param repSalesStatusOrderTotal 销售状况-销售占比/销售趋势/销售同比
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepSalesStatusOrderTotal(RepSalesStatusOrderTotal repSalesStatusOrderTotal) {
        int row = repSalesStatusOrderTotalMapper.insert(repSalesStatusOrderTotal);
        return row;
    }

    /**
     * 批量删除销售状况-销售占比/销售趋势/销售同比
     *
     * @param dataRecordSids 需要删除的销售状况-销售占比/销售趋势/销售同比ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepSalesStatusOrderTotalByIds(List<Long> dataRecordSids) {
        return repSalesStatusOrderTotalMapper.deleteBatchIds(dataRecordSids);
    }

}
