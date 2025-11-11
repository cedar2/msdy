package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.dto.response.RepBusinessRemindRepResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.platform.ems.constant.ConstantsEms;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.RepBusinessRemindPoMapper;
import com.platform.ems.domain.RepBusinessRemindPo;
import com.platform.ems.service.IRepBusinessRemindPoService;

import static java.util.stream.Collectors.toList;

/**
 * 已逾期/即将到期-采购订单Service业务层处理
 *
 * @author linhongwei
 * @date 2022-02-24
 */
@Service
@SuppressWarnings("all")
public class RepBusinessRemindPoServiceImpl extends ServiceImpl<RepBusinessRemindPoMapper, RepBusinessRemindPo> implements IRepBusinessRemindPoService {
    @Autowired
    private RepBusinessRemindPoMapper repBusinessRemindPoMapper;

    /**
     * 查询已逾期/即将到期-采购订单
     *
     * @param dataRecordSid 已逾期/即将到期-采购订单ID
     * @return 已逾期/即将到期-采购订单
     */
    @Override
    public RepBusinessRemindPo selectRepBusinessRemindPoById(Long dataRecordSid) {
        RepBusinessRemindPo repBusinessRemindPo = repBusinessRemindPoMapper.selectRepBusinessRemindPoById(dataRecordSid);
        return repBusinessRemindPo;
    }

    /**
     * 查询已逾期/即将到期-采购订单列表
     *
     * @param repBusinessRemindPo 已逾期/即将到期-采购订单
     * @return 已逾期/即将到期-采购订单
     */
    @Override
    public List<RepBusinessRemindPo> selectRepBusinessRemindPoList(RepBusinessRemindPo repBusinessRemindPo) {
        return repBusinessRemindPoMapper.selectRepBusinessRemindPoList(repBusinessRemindPo);
    }

    /**
     * 查询已逾期/即将到期-采购订单列表
     *
     * @param repBusinessRemindPo 已逾期/即将到期-报表 主
     * @return 已逾期/即将到期-采购订单
     */
    @Override
    public List<RepBusinessRemindPo> getYYQHead(RepBusinessRemindPo repBusinessRemindPo) {
        return repBusinessRemindPoMapper.getYyq(repBusinessRemindPo);
    }

    /**
     * 查询已逾期/即将到期-采购订单列表
     *
     * @param repBusinessRemindPo 已逾期/即将到期-报表 明细
     * @return 已逾期/即将到期-采购订单
     */
    @Override
    public List<RepBusinessRemindPo> getYYQItem(RepBusinessRemindPo repBusinessRemindPo) {
        List<RepBusinessRemindPo> list = repBusinessRemindPoMapper.getYyqItem(repBusinessRemindPo);
        return list;
    }
    @Override
    public List<RepBusinessRemindPo> sort(List<RepBusinessRemindPo> salSalesOrderItemList){
        if(CollectionUtil.isNotEmpty(salSalesOrderItemList)){
            salSalesOrderItemList = salSalesOrderItemList.stream().sorted(
                    Comparator.comparing(RepBusinessRemindPo::getMaterialCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(RepBusinessRemindPo::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(RepBusinessRemindPo::getSku1Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(RepBusinessRemindPo::getSort2, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(RepBusinessRemindPo::getSku2Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
            ).collect(toList());
            return salSalesOrderItemList;
        }
        return new ArrayList<>();
    }


    @Override
    public List<RepBusinessRemindRepResponse> getReport() {
        List<RepBusinessRemindPo> repBusinessRemindSosY = repBusinessRemindPoMapper.selectList(new QueryWrapper<RepBusinessRemindPo>().lambda()
                .eq(RepBusinessRemindPo::getRemindType, ConstantsEms.YYQ)
        );
        List<RepBusinessRemindRepResponse> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(repBusinessRemindSosY)) {
            RepBusinessRemindRepResponse repBusinessRemindSo = new RepBusinessRemindRepResponse();
            repBusinessRemindSo.setRemindType(ConstantsEms.YYQ);
            BigDecimal sum = repBusinessRemindSosY.stream().map(li -> li.getQuantityWeijh()).reduce(BigDecimal.ZERO, BigDecimal::add);
            repBusinessRemindSo.setQuantityDaiFh(sum);
            int count = getcount(repBusinessRemindSosY);
            repBusinessRemindSo.setQuantity(count);
            list.add(repBusinessRemindSo);
        } else {
            RepBusinessRemindRepResponse repBusinessRemindSo = new RepBusinessRemindRepResponse();
            repBusinessRemindSo.setRemindType(ConstantsEms.YYQ);
            repBusinessRemindSo.setQuantityDaiFh(BigDecimal.ZERO);
            repBusinessRemindSo.setQuantity(0);
            list.add(repBusinessRemindSo);
        }

        List<RepBusinessRemindPo> repBusinessRemindSosJ = repBusinessRemindPoMapper.selectList(new QueryWrapper<RepBusinessRemindPo>().lambda()
                .eq(RepBusinessRemindPo::getRemindType, ConstantsEms.JJDQ)
        );
        if (CollectionUtil.isNotEmpty(repBusinessRemindSosJ)) {
            RepBusinessRemindRepResponse repBusinessRemindSo = new RepBusinessRemindRepResponse();
            repBusinessRemindSo.setRemindType(ConstantsEms.JJDQ);
            BigDecimal sum = repBusinessRemindSosJ.stream().map(li -> li.getQuantityWeijh()).reduce(BigDecimal.ZERO, BigDecimal::add);
            repBusinessRemindSo.setQuantityDaiFh(sum);
            int count = getcount(repBusinessRemindSosJ);
            repBusinessRemindSo.setQuantity(count);
            list.add(repBusinessRemindSo);
        } else {
            RepBusinessRemindRepResponse repBusinessRemindSo = new RepBusinessRemindRepResponse();
            repBusinessRemindSo.setRemindType(ConstantsEms.JJDQ);
            repBusinessRemindSo.setQuantityDaiFh(BigDecimal.ZERO);
            repBusinessRemindSo.setQuantity(0);
            list.add(repBusinessRemindSo);
        }

        return list;
    }

    public int getcount(List<RepBusinessRemindPo> list) {
        HashSet<Long> longs = new HashSet<>();
        list.forEach(li -> {
            longs.add(li.getPurchaseOrderSid());
        });
        return longs.size();
    }

    /**
     * 新增已逾期/即将到期-采购订单
     * 需要注意编码重复校验
     *
     * @param repBusinessRemindPo 已逾期/即将到期-采购订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepBusinessRemindPo(RepBusinessRemindPo repBusinessRemindPo) {
        int row = repBusinessRemindPoMapper.insert(repBusinessRemindPo);
        return row;
    }

    /**
     * 批量删除已逾期/即将到期-采购订单
     *
     * @param dataRecordSids 需要删除的已逾期/即将到期-采购订单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepBusinessRemindPoByIds(List<Long> dataRecordSids) {
        return repBusinessRemindPoMapper.deleteBatchIds(dataRecordSids);
    }

}
