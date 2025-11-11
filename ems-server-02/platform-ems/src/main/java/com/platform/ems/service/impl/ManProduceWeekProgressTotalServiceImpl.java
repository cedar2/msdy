package com.platform.ems.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.ManProduceWeekProgressTotalMapper;
import com.platform.ems.domain.ManProduceWeekProgressTotal;
import com.platform.ems.service.IManProduceWeekProgressTotalService;

/**
 * 生产周进度汇总Service业务层处理
 *
 * @author chenkw
 * @date 2022-08-26
 */
@Service
@SuppressWarnings("all")
public class ManProduceWeekProgressTotalServiceImpl extends ServiceImpl<ManProduceWeekProgressTotalMapper, ManProduceWeekProgressTotal> implements IManProduceWeekProgressTotalService {
    @Autowired
    private ManProduceWeekProgressTotalMapper manProduceWeekProgressTotalMapper;

    private static final String TITLE = "生产周进度汇总";

    /**
     * 查询生产周进度汇总
     *
     * @param weekProgressTotalSid 生产周进度汇总ID
     * @return 生产周进度汇总
     */
    @Override
    public ManProduceWeekProgressTotal selectManProduceWeekProgressTotalById(Long weekProgressTotalSid) {
        ManProduceWeekProgressTotal manProduceWeekProgressTotal = manProduceWeekProgressTotalMapper.selectManProduceWeekProgressTotalById(weekProgressTotalSid);
        MongodbUtil.find(manProduceWeekProgressTotal);
        return manProduceWeekProgressTotal;
    }

    /**
     * 查询生产周进度汇总列表
     *
     * @param manProduceWeekProgressTotal 生产周进度汇总
     * @return 生产周进度汇总
     */
    @Override
    public List<ManProduceWeekProgressTotal> selectManProduceWeekProgressTotalList(ManProduceWeekProgressTotal manProduceWeekProgressTotal) {
        if (manProduceWeekProgressTotal.getRecentWeeks() != null) {
            LocalDate now = manProduceWeekProgressTotal.getDateStart()!=null?manProduceWeekProgressTotal.getDateStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate():LocalDate.now();
            LocalDate todayOfLastWeekBegin = now.minusDays(7*manProduceWeekProgressTotal.getRecentWeeks());
            LocalDate todayOfLastWeekEnd = now.minusDays(7);
            LocalDate monday = todayOfLastWeekBegin.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).plusDays(1);
            LocalDate sunday = todayOfLastWeekEnd.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).minusDays(1);
            ZoneId zone = ZoneId.systemDefault();
            manProduceWeekProgressTotal.setDateStart(Date.from(monday.atStartOfDay().atZone(zone).toInstant()));
            manProduceWeekProgressTotal.setDateEnd(Date.from(sunday.atStartOfDay().atZone(zone).toInstant()));
        }
        return manProduceWeekProgressTotalMapper.selectManProduceWeekProgressTotalList(manProduceWeekProgressTotal);
    }

    /**
     * 新增生产周进度汇总
     * 需要注意编码重复校验
     *
     * @param manProduceWeekProgressTotal 生产周进度汇总
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManProduceWeekProgressTotal(ManProduceWeekProgressTotal manProduceWeekProgressTotal) {
        int row = manProduceWeekProgressTotalMapper.insert(manProduceWeekProgressTotal);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ManProduceWeekProgressTotal(), manProduceWeekProgressTotal);
            MongodbDeal.insert(manProduceWeekProgressTotal.getWeekProgressTotalSid(), ConstantsEms.SAVA_STATUS, msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改生产周进度汇总
     *
     * @param manProduceWeekProgressTotal 生产周进度汇总
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManProduceWeekProgressTotal(ManProduceWeekProgressTotal manProduceWeekProgressTotal) {
        ManProduceWeekProgressTotal original = manProduceWeekProgressTotalMapper.selectManProduceWeekProgressTotalById(manProduceWeekProgressTotal.getWeekProgressTotalSid());
        int row = manProduceWeekProgressTotalMapper.updateById(manProduceWeekProgressTotal);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, manProduceWeekProgressTotal);
            MongodbDeal.update(manProduceWeekProgressTotal.getWeekProgressTotalSid(), ConstantsEms.SAVA_STATUS, ConstantsEms.SAVA_STATUS, msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更生产周进度汇总
     *
     * @param manProduceWeekProgressTotal 生产周进度汇总
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManProduceWeekProgressTotal(ManProduceWeekProgressTotal manProduceWeekProgressTotal) {
        ManProduceWeekProgressTotal response = manProduceWeekProgressTotalMapper.selectManProduceWeekProgressTotalById(manProduceWeekProgressTotal.getWeekProgressTotalSid());
        int row = manProduceWeekProgressTotalMapper.updateAllById(manProduceWeekProgressTotal);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manProduceWeekProgressTotal.getWeekProgressTotalSid(), BusinessType.CHANGE.getValue(), response, manProduceWeekProgressTotal, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产周进度汇总
     *
     * @param weekProgressTotalSids 需要删除的生产周进度汇总ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManProduceWeekProgressTotalByIds(List<Long> weekProgressTotalSids) {
        List<ManProduceWeekProgressTotal> list = manProduceWeekProgressTotalMapper.selectList(new QueryWrapper<ManProduceWeekProgressTotal>()
                .lambda().in(ManProduceWeekProgressTotal::getWeekProgressTotalSid, weekProgressTotalSids));
        int row = manProduceWeekProgressTotalMapper.deleteBatchIds(weekProgressTotalSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ManProduceWeekProgressTotal());
                MongodbUtil.insertUserLog(o.getWeekProgressTotalSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 更新汇总生产周进度汇总
     *
     * @param weekProgressTotalSids 需要删除的生产周进度汇总ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int refreshManProduceWeekProgressTotalByIds(List<Long> weekProgressTotalSids) {
        int row = 0;
        List<ManProduceWeekProgressTotal> list = manProduceWeekProgressTotalMapper.selectList(new QueryWrapper<ManProduceWeekProgressTotal>()
                .lambda().in(ManProduceWeekProgressTotal::getWeekProgressTotalSid, weekProgressTotalSids));
        List<ManProduceWeekProgressTotal> updateList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            List<ManProduceWeekProgressTotal> newList = new ArrayList<>();
            for (ManProduceWeekProgressTotal total : list) {
                newList = manProduceWeekProgressTotalMapper.selectManWeekManufacturePlanList(new ManProduceWeekProgressTotal()
                        .setDateStart(total.getDateStart()).setDateEnd(total.getDateEnd()).setPlantSid(total.getPlantSid())
                        .setDepartmentSid(total.getDepartmentSid()));
                if (CollectionUtil.isNotEmpty(newList)) {
                    ManProduceWeekProgressTotal newOne = newList.get(0);
                    total.setQuantityJih(newOne.getQuantityJih()).setQuantityWanc(newOne.getQuantityWanc())
                            .setQuantityQian(newOne.getQuantityQian()).setRateWanc(newOne.getRateWanc())
                            .setRateQian(newOne.getRateQian());
                    updateList.add(total);
                }
            }
            if (CollectionUtil.isNotEmpty(updateList)) {
                manProduceWeekProgressTotalMapper.updatesAllById(updateList);
                row = updateList.size();
            }
        }
        return row;
    }

}
