package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.dto.response.RepBusinessRemindRepResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.platform.ems.constant.ConstantsEms;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.RepBusinessRemindSoMapper;
import com.platform.ems.domain.RepBusinessRemindSo;
import com.platform.ems.service.IRepBusinessRemindSoService;

import static java.util.stream.Collectors.toList;

/**
 * 已逾期/即将到期-销售订单Service业务层处理
 *
 * @author linhongwei
 * @date 2022-02-24
 */
@Service
@SuppressWarnings("all")
public class RepBusinessRemindSoServiceImpl extends ServiceImpl<RepBusinessRemindSoMapper, RepBusinessRemindSo> implements IRepBusinessRemindSoService {
    @Autowired
    private RepBusinessRemindSoMapper repBusinessRemindSoMapper;

    /**
     * 查询已逾期/即将到期-销售订单
     *
     * @param dataRecordSid 已逾期/即将到期-销售订单ID
     * @return 已逾期/即将到期-销售订单
     */
    @Override
    public RepBusinessRemindSo selectRepBusinessRemindSoById(Long dataRecordSid) {
        RepBusinessRemindSo repBusinessRemindSo = repBusinessRemindSoMapper.selectRepBusinessRemindSoById(dataRecordSid);
        return repBusinessRemindSo;
    }

    /**
     * 查询已逾期/即将到期-销售订单列表
     *
     * @param repBusinessRemindSo 已逾期/即将到期-销售订单
     * @return 已逾期/即将到期-销售订单
     */
    @Override
    public List<RepBusinessRemindSo> selectRepBusinessRemindSoList(RepBusinessRemindSo repBusinessRemindSo) {
        return repBusinessRemindSoMapper.selectRepBusinessRemindSoList(repBusinessRemindSo);
    }

    /**
     * 查询已逾期-或即将到期 销售订单报表
     */
    @Override
    public List<RepBusinessRemindSo> yyqReport(RepBusinessRemindSo repBusinessRemindSo) {
        return repBusinessRemindSoMapper.getYyq(repBusinessRemindSo);
    }

    /**
     * 查询已逾期或即将到期报表明细
     */
    @Override
    public List<RepBusinessRemindSo> reportItem(RepBusinessRemindSo repBusinessRemindSo) {
        List<RepBusinessRemindSo> list = repBusinessRemindSoMapper.getYyqItem(repBusinessRemindSo);
        return list;
    }

    @Override
    public List<RepBusinessRemindSo> sort(List<RepBusinessRemindSo> salSalesOrderItemList){
        if(CollectionUtil.isNotEmpty(salSalesOrderItemList)){
            salSalesOrderItemList = salSalesOrderItemList.stream().sorted(
                    Comparator.comparing(RepBusinessRemindSo::getMaterialCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(RepBusinessRemindSo::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(RepBusinessRemindSo::getSku1Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(RepBusinessRemindSo::getSort2, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(RepBusinessRemindSo::getSku2Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
            ).collect(toList());
            return salSalesOrderItemList;
        }
        return new ArrayList<>();
    }
    @Override
    public List<RepBusinessRemindRepResponse> getReport() {
        List<RepBusinessRemindSo> repBusinessRemindSosY = repBusinessRemindSoMapper.selectList(new QueryWrapper<RepBusinessRemindSo>().lambda()
                .eq(RepBusinessRemindSo::getRemindType, ConstantsEms.YYQ)
        );
        List<RepBusinessRemindRepResponse> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(repBusinessRemindSosY)) {
            RepBusinessRemindRepResponse repBusinessRemindSo = new RepBusinessRemindRepResponse();
            repBusinessRemindSo.setRemindType(ConstantsEms.YYQ);
            BigDecimal sum = repBusinessRemindSosY.stream().map(li -> li.getQuantityWeifh()).reduce(BigDecimal.ZERO, BigDecimal::add);
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

        List<RepBusinessRemindSo> repBusinessRemindSosJ = repBusinessRemindSoMapper.selectList(new QueryWrapper<RepBusinessRemindSo>().lambda()
                .eq(RepBusinessRemindSo::getRemindType, ConstantsEms.JJDQ)
        );
        if (CollectionUtil.isNotEmpty(repBusinessRemindSosJ)) {
            RepBusinessRemindRepResponse repBusinessRemindSo = new RepBusinessRemindRepResponse();
            repBusinessRemindSo.setRemindType(ConstantsEms.JJDQ);
            BigDecimal sum = repBusinessRemindSosJ.stream().map(li -> li.getQuantityWeifh()).reduce(BigDecimal.ZERO, BigDecimal::add);
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

    public int getcount(List<RepBusinessRemindSo> list) {
        HashSet<Long> longs = new HashSet<>();
        list.forEach(li -> {
            longs.add(li.getSalesOrderSid());
        });
        return longs.size();
    }

    /**
     * 新增已逾期/即将到期-销售订单
     * 需要注意编码重复校验
     *
     * @param repBusinessRemindSo 已逾期/即将到期-销售订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepBusinessRemindSo(RepBusinessRemindSo repBusinessRemindSo) {
        int row = repBusinessRemindSoMapper.insert(repBusinessRemindSo);
        return row;
    }

    /**
     * 批量删除已逾期/即将到期-销售订单
     *
     * @param dataRecordSids 需要删除的已逾期/即将到期-销售订单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepBusinessRemindSoByIds(List<Long> dataRecordSids) {
        return repBusinessRemindSoMapper.deleteBatchIds(dataRecordSids);
    }

}
