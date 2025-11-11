package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.DevCategoryPlanItem;
import com.platform.ems.domain.FinHuipiaoRecord;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.FinHuipiaoRecordUseRecordMapper;
import com.platform.ems.domain.FinHuipiaoRecordUseRecord;
import com.platform.ems.service.IFinHuipiaoRecordUseRecordService;

/**
 * 汇票台账-使用记录表Service业务层处理
 *
 * @author platform
 * @date 2024-03-12
 */
@Service
@SuppressWarnings("all")
public class FinHuipiaoRecordUseRecordServiceImpl extends ServiceImpl<FinHuipiaoRecordUseRecordMapper, FinHuipiaoRecordUseRecord> implements IFinHuipiaoRecordUseRecordService {
    @Autowired
    private FinHuipiaoRecordUseRecordMapper finHuipiaoRecordUseRecordMapper;

    private static final String TITLE = "汇票台账-使用记录表";

    /**
     * 查询汇票台账-使用记录表
     *
     * @param huipiaoRecordUseRecordSid 汇票台账-使用记录表ID
     * @return 汇票台账-使用记录表
     */
    @Override
    public FinHuipiaoRecordUseRecord selectFinHuipiaoRecordUseRecordById(Long huipiaoRecordUseRecordSid) {
        FinHuipiaoRecordUseRecord finHuipiaoRecordUseRecord = finHuipiaoRecordUseRecordMapper.selectFinHuipiaoRecordUseRecordById(huipiaoRecordUseRecordSid);
        MongodbUtil.find(finHuipiaoRecordUseRecord);
        return finHuipiaoRecordUseRecord;
    }

    /**
     * 查询汇票台账-使用记录表列表
     *
     * @param finHuipiaoRecordUseRecord 汇票台账-使用记录表
     * @return 汇票台账-使用记录表
     */
    @Override
    public List<FinHuipiaoRecordUseRecord> selectFinHuipiaoRecordUseRecordList(FinHuipiaoRecordUseRecord finHuipiaoRecordUseRecord) {
        return finHuipiaoRecordUseRecordMapper.selectFinHuipiaoRecordUseRecordList(finHuipiaoRecordUseRecord);
    }

    /**
     * 批量新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertByList(FinHuipiaoRecord finHuipiaoRecord) {
        int row = 0;
        List<FinHuipiaoRecordUseRecord> list = finHuipiaoRecord.getUseRecordList();
        if (CollectionUtil.isNotEmpty(list)) {
            FinHuipiaoRecordUseRecord item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setHuipiaoRecordSid(finHuipiaoRecord.getHuipiaoRecordSid());
                row += insertFinHuipiaoRecordUseRecord(item);
            }
        }
        return row;
    }

    /**
     * 批量修改
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByList(FinHuipiaoRecord finHuipiaoRecord) {
        int row = 0;
        List<FinHuipiaoRecordUseRecord> list = finHuipiaoRecord.getUseRecordList();
        // 原本明细
        List<FinHuipiaoRecordUseRecord> oldList = finHuipiaoRecordUseRecordMapper.selectList(new QueryWrapper<FinHuipiaoRecordUseRecord>()
                .lambda().eq(FinHuipiaoRecordUseRecord::getHuipiaoRecordSid, finHuipiaoRecord.getHuipiaoRecordSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            for (FinHuipiaoRecordUseRecord iii : list) {
                if (iii.getUseAmount() != null && iii.getUseAmount().scale() > 2) {
                    throw new BaseException("汇票台账的使用记录的使用金额小数位上限为2位");
                }
            }
            // 新增行
            List<FinHuipiaoRecordUseRecord> newList = list.stream().filter(o -> o.getHuipiaoRecordUseRecordSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                finHuipiaoRecord.setUseRecordList(newList);
                row += insertByList(finHuipiaoRecord);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<FinHuipiaoRecordUseRecord> updateList = list.stream().filter(o -> o.getHuipiaoRecordUseRecordSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(FinHuipiaoRecordUseRecord::getHuipiaoRecordUseRecordSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, FinHuipiaoRecordUseRecord> map = oldList.stream().collect(Collectors.toMap(FinHuipiaoRecordUseRecord::getHuipiaoRecordUseRecordSid, Function.identity()));
                    for (FinHuipiaoRecordUseRecord item : updateList) {
                        if (map.containsKey(item.getHuipiaoRecordUseRecordSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getHuipiaoRecordUseRecordSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            row += finHuipiaoRecordUseRecordMapper.updateAllById(item); // 全量更新
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getHuipiaoRecordUseRecordSid(), finHuipiaoRecord.getHandleStatus(), msgList, TITLE);
                        }
                    }
                    // 删除行
                    List<FinHuipiaoRecordUseRecord> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getHuipiaoRecordUseRecordSid())).collect(Collectors.toList());
                    row += deleteByList(delList);
                }
            }
        }
        else {
            // 如果 请求明细 没有了，但是数据库有明细，则删除数据库的明细
            if (CollectionUtil.isNotEmpty(oldList)) {
                row += deleteByList(oldList);
            }
        }
        return row;
    }


    /**
     * 批量删除品类规划-明细
     *
     * @param itemList 需要删除的品类规划-明细列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByList(List<FinHuipiaoRecordUseRecord> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> itemSidList = itemList.stream().filter(o -> o.getHuipiaoRecordUseRecordSid() != null)
                .map(FinHuipiaoRecordUseRecord::getHuipiaoRecordUseRecordSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemSidList)) {
            row = finHuipiaoRecordUseRecordMapper.deleteBatchIds(itemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new DevCategoryPlanItem());
                    MongodbUtil.insertUserLog(o.getHuipiaoRecordUseRecordSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

    /**
     * 新增汇票台账-使用记录表
     * 需要注意编码重复校验
     *
     * @param finHuipiaoRecordUseRecord 汇票台账-使用记录表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinHuipiaoRecordUseRecord(FinHuipiaoRecordUseRecord finHuipiaoRecordUseRecord) {
        int row = finHuipiaoRecordUseRecordMapper.insert(finHuipiaoRecordUseRecord);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new FinHuipiaoRecordUseRecord(), finHuipiaoRecordUseRecord);
            MongodbUtil.insertUserLog(finHuipiaoRecordUseRecord.getHuipiaoRecordUseRecordSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改汇票台账-使用记录表
     *
     * @param finHuipiaoRecordUseRecord 汇票台账-使用记录表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinHuipiaoRecordUseRecord(FinHuipiaoRecordUseRecord finHuipiaoRecordUseRecord) {
        FinHuipiaoRecordUseRecord original = finHuipiaoRecordUseRecordMapper.selectFinHuipiaoRecordUseRecordById(finHuipiaoRecordUseRecord.getHuipiaoRecordUseRecordSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, finHuipiaoRecordUseRecord);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finHuipiaoRecordUseRecord.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finHuipiaoRecordUseRecordMapper.updateAllById(finHuipiaoRecordUseRecord);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finHuipiaoRecordUseRecord.getHuipiaoRecordUseRecordSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更汇票台账-使用记录表
     *
     * @param finHuipiaoRecordUseRecord 汇票台账-使用记录表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinHuipiaoRecordUseRecord(FinHuipiaoRecordUseRecord finHuipiaoRecordUseRecord) {
        FinHuipiaoRecordUseRecord response = finHuipiaoRecordUseRecordMapper.selectFinHuipiaoRecordUseRecordById(finHuipiaoRecordUseRecord.getHuipiaoRecordUseRecordSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, finHuipiaoRecordUseRecord);
        if (CollectionUtil.isNotEmpty(msgList)) {
            finHuipiaoRecordUseRecord.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = finHuipiaoRecordUseRecordMapper.updateAllById(finHuipiaoRecordUseRecord);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finHuipiaoRecordUseRecord.getHuipiaoRecordUseRecordSid(), BusinessType.CHANGE.getValue(), response, finHuipiaoRecordUseRecord, TITLE);
        }
        return row;
    }

    /**
     * 批量删除汇票台账-使用记录表
     *
     * @param huipiaoRecordUseRecordSids 需要删除的汇票台账-使用记录表ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinHuipiaoRecordUseRecordByIds(List<Long> huipiaoRecordUseRecordSids) {
        List<FinHuipiaoRecordUseRecord> list = finHuipiaoRecordUseRecordMapper.selectList(new QueryWrapper<FinHuipiaoRecordUseRecord>()
                .lambda().in(FinHuipiaoRecordUseRecord::getHuipiaoRecordUseRecordSid, huipiaoRecordUseRecordSids));
        int row = finHuipiaoRecordUseRecordMapper.deleteBatchIds(huipiaoRecordUseRecordSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new FinHuipiaoRecordUseRecord());
                MongodbUtil.insertUserLog(o.getHuipiaoRecordUseRecordSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

}
