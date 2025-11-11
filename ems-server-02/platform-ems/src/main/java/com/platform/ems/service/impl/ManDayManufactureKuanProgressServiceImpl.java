package com.platform.ems.service.impl;

import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ManDayManufacturePlanItem;
import com.platform.ems.domain.ManDayManufactureProgress;
import com.platform.ems.domain.ManDayManufactureProgressItem;
import com.platform.ems.mapper.ManDayManufacturePlanItemMapper;
import com.platform.ems.mapper.ManDayManufactureProgressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.ManDayManufactureKuanProgressMapper;
import com.platform.ems.domain.ManDayManufactureKuanProgress;
import com.platform.ems.service.IManDayManufactureKuanProgressService;

import static java.util.stream.Collectors.toList;

/**
 * 生产进度日报-款生产进度Service业务层处理
 *
 * @author chenkw
 * @date 2022-08-03
 */
@Service
@SuppressWarnings("all")
public class ManDayManufactureKuanProgressServiceImpl extends ServiceImpl<ManDayManufactureKuanProgressMapper, ManDayManufactureKuanProgress> implements IManDayManufactureKuanProgressService {
    @Autowired
    private ManDayManufactureKuanProgressMapper manDayManufactureKuanProgressMapper;
    @Autowired
    private ManDayManufactureProgressMapper manDayManufactureProgressMapper;
    @Autowired
    private ManDayManufacturePlanItemMapper manDayManufacturePlanItemMapper;

    private static final String TITLE = "生产进度日报-款生产进度";

    /**
     * 查询生产进度日报-款生产进度
     *
     * @param dayManufactureKuanProgressSid 生产进度日报-款生产进度ID
     * @return 生产进度日报-款生产进度
     */
    @Override
    public ManDayManufactureKuanProgress selectManDayManufactureKuanProgressById(Long dayManufactureKuanProgressSid) {
        ManDayManufactureKuanProgress manDayManufactureKuanProgress = manDayManufactureKuanProgressMapper.selectManDayManufactureKuanProgressById(dayManufactureKuanProgressSid);
        MongodbUtil.find(manDayManufactureKuanProgress);
        return manDayManufactureKuanProgress;
    }

    /**
     * 查询生产进度日报-款生产进度列表
     *
     * @param manDayManufactureKuanProgress 生产进度日报-款生产进度
     * @return 生产进度日报-款生产进度
     */
    @Override
    public List<ManDayManufactureKuanProgress> selectManDayManufactureKuanProgressList(ManDayManufactureKuanProgress manDayManufactureKuanProgress) {
        return manDayManufactureKuanProgressMapper.selectManDayManufactureKuanProgressList(manDayManufactureKuanProgress);
    }

    /**
     * 新增生产进度日报-款生产进度
     * 需要注意编码重复校验
     *
     * @param manDayManufactureKuanProgress 生产进度日报-款生产进度
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManDayManufactureKuanProgress(ManDayManufactureKuanProgress manDayManufactureKuanProgress) {
        int row = manDayManufactureKuanProgressMapper.insert(manDayManufactureKuanProgress);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ManDayManufactureKuanProgress(), manDayManufactureKuanProgress);
            MongodbDeal.insert(manDayManufactureKuanProgress.getDayManufactureKuanProgressSid(), ConstantsEms.SAVA_STATUS, msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改生产进度日报-款生产进度
     *
     * @param manDayManufactureKuanProgress 生产进度日报-款生产进度
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManDayManufactureKuanProgress(ManDayManufactureKuanProgress manDayManufactureKuanProgress) {
        ManDayManufactureKuanProgress original = manDayManufactureKuanProgressMapper.selectManDayManufactureKuanProgressById(manDayManufactureKuanProgress.getDayManufactureKuanProgressSid());
        int row = manDayManufactureKuanProgressMapper.updateById(manDayManufactureKuanProgress);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, manDayManufactureKuanProgress);
            MongodbDeal.update(manDayManufactureKuanProgress.getDayManufactureKuanProgressSid(), ConstantsEms.SAVA_STATUS, ConstantsEms.SAVA_STATUS, msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更生产进度日报-款生产进度
     *
     * @param manDayManufactureKuanProgress 生产进度日报-款生产进度
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManDayManufactureKuanProgress(ManDayManufactureKuanProgress manDayManufactureKuanProgress) {
        ManDayManufactureKuanProgress response = manDayManufactureKuanProgressMapper.selectManDayManufactureKuanProgressById(manDayManufactureKuanProgress.getDayManufactureKuanProgressSid());
        int row = manDayManufactureKuanProgressMapper.updateAllById(manDayManufactureKuanProgress);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manDayManufactureKuanProgress.getDayManufactureKuanProgressSid(), BusinessType.CHANGE.getValue(), response, manDayManufactureKuanProgress, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产进度日报-款生产进度
     *
     * @param dayManufactureKuanProgressSids 需要删除的生产进度日报-款生产进度ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManDayManufactureKuanProgressByIds(List<Long> dayManufactureKuanProgressSids) {
        List<ManDayManufactureKuanProgress> list = manDayManufactureKuanProgressMapper.selectList(new QueryWrapper<ManDayManufactureKuanProgress>()
                .lambda().in(ManDayManufactureKuanProgress::getDayManufactureKuanProgressSid, dayManufactureKuanProgressSids));
        int row = manDayManufactureKuanProgressMapper.deleteBatchIds(dayManufactureKuanProgressSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ManDayManufactureKuanProgress());
                MongodbUtil.insertUserLog(o.getDayManufactureKuanProgressSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 根据生产进度日报明细查询生产进度日报-款生产进度列表
     *
     * @param manDayManufactureProgress 生产进度日报
     * @return 生产进度日报-款生产进度集合
     */
    @Override
    public List<ManDayManufactureKuanProgress> selectManDayManufactureProgressKuanList(ManDayManufactureProgress manDayManufactureProgress) {
        List<ManDayManufactureKuanProgress> list = new ArrayList<>();
        List<ManDayManufactureKuanProgress> newList = new ArrayList<>();
        // 分组
        Map<String, List<ManDayManufactureKuanProgress>> map = new HashMap<>();
        Map<String, List<ManDayManufactureProgressItem>> itemMap = new HashMap<>();
        // 页面中的明细
        if (CollectionUtil.isNotEmpty(manDayManufactureProgress.getDayManufactureProgressItemList())) {
            itemMap = manDayManufactureProgress.getDayManufactureProgressItemList().stream().collect(Collectors.groupingBy(o->
                    String.valueOf(o.getMaterialSid())+"_"+String.valueOf(o.getSku1Sid())+"_"+String.valueOf(o.getWorkCenterSid())+"_"+String.valueOf(o.getPaichanBatch())));
        }
        // 页面已存在
        if (CollectionUtil.isNotEmpty(manDayManufactureProgress.getKuanProcessList())) {
            list.addAll(manDayManufactureProgress.getKuanProcessList());
            map = list.stream().collect(Collectors.groupingBy(o->
                    String.valueOf(o.getProductSid())+"_"+String.valueOf(o.getSku1Sid())+"_"+String.valueOf(o.getWorkCenterSid())+"_"+String.valueOf(o.getPaichanBatch())));
        }
        // 数据库已存在
        if (manDayManufactureProgress.getDayManufactureProgressSid() != null) {
            List<ManDayManufactureKuanProgress> have = manDayManufactureKuanProgressMapper.selectManDayManufactureKuanProgressList(new ManDayManufactureKuanProgress()
                    .setDayManufactureProgressSid(manDayManufactureProgress.getDayManufactureProgressSid()));
            if (CollectionUtil.isNotEmpty(have)) {
                for (ManDayManufactureKuanProgress item : have) {
                    if (map.get(String.valueOf(item.getProductSid())+"_"+String.valueOf(item.getSku1Sid())+"_"+String.valueOf(item.getWorkCenterSid())
                            +"_"+String.valueOf(item.getPaichanBatch())) == null) {
                        newList.add(item);
                        map.put(String.valueOf(item.getProductSid())+"_"+String.valueOf(item.getSku1Sid())+"_"+String.valueOf(item.getWorkCenterSid())
                                +"_"+String.valueOf(item.getPaichanBatch()), new ArrayList<>());
                    }
                }
                list.addAll(newList);
            }
        }
        newList.clear();
        // 读取页面中的明细
        if (CollectionUtil.isNotEmpty(manDayManufactureProgress.getDayManufactureProgressItemList())) {
            for (ManDayManufactureProgressItem item : manDayManufactureProgress.getDayManufactureProgressItemList()) {
                if (map.get(String.valueOf(item.getMaterialSid())+"_"+String.valueOf(item.getSku1Sid())+"_"+String.valueOf(item.getWorkCenterSid())
                        +"_"+String.valueOf(item.getPaichanBatch())) == null) {
                    ManDayManufactureKuanProgress kuan = new ManDayManufactureKuanProgress();
                    kuan.setManufactureOrderSid(item.getManufactureOrderSid()).setManufactureOrderCode(item.getManufactureOrderCode());
                    kuan.setProductSid(item.getMaterialSid()).setProductCode(item.getMaterialCode()).setPaichanBatch(item.getPaichanBatch());
                    kuan.setSku1Sid(item.getSku1Sid()).setSku1Type(item.getSku1Type()).setSku1Code(item.getSku1Code()).setSku1Name(item.getSku1Name())
                            .setWorkCenterSid(item.getWorkCenterSid()).setWorkCenterName(item.getWorkCenterName()).setDepartmentName(item.getDepartmentName())
                            .setWorkCenterCode(item.getWorkCenterCode()).setDepartmentSid(item.getDepartmentSid()).setDepartmentCode(item.getDepartmentCode());
                    newList.add(kuan);
                    map.put(String.valueOf(item.getMaterialSid())+"_"+String.valueOf(item.getSku1Sid())+"_"+String.valueOf(item.getWorkCenterSid())
                            +"_"+String.valueOf(item.getPaichanBatch()), new ArrayList<>());
                }
            }
            if (list.size() > 0) {
                for (int i = list.size() -1; i >= 0; i--) {
                    if (itemMap.get(String.valueOf(list.get(i).getProductSid())+"_"+String.valueOf(list.get(i).getSku1Sid())+"_"+String.valueOf(list.get(i).getWorkCenterSid())
                            +"_"+String.valueOf(list.get(i).getPaichanBatch())) == null) {
                        list.remove(i);
                    }
                }
            }
            list.addAll(newList);
        }
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item->{
                ManDayManufacturePlanItem planItem = manDayManufacturePlanItemMapper.selectQuantityBy(new ManDayManufacturePlanItem()
                        .setPlanDate(manDayManufactureProgress.getDocumentDate())
                        .setWorkCenterSid(item.getWorkCenterSid()).setPaichanBatch(item.getPaichanBatch())
                        .setMaterialSid(item.getProductSid()).setSku1Sid(item.getSku1Sid()));
                if (planItem != null) {
                    item.setPlanQuantity(planItem.getPlanQuantity());
                }
                else {
                    item.setPlanQuantity(null);
                }
            });
            list = list.stream()
                    .sorted(Comparator.comparing(ManDayManufactureKuanProgress::getWorkCenterName,
                            Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(ManDayManufactureKuanProgress::getProductCode,
                                    Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(ManDayManufactureKuanProgress::getPaichanBatch, Comparator.nullsLast(Long::compareTo))).collect(toList());
        }
        return list;
    }

}
