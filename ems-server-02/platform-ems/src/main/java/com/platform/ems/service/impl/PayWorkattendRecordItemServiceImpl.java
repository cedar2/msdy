package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.PayWorkattendRecordItem;
import com.platform.ems.mapper.PayWorkattendRecordItemMapper;
import com.platform.ems.service.IPayWorkattendRecordItemService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 考勤信息-明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-14
 */
@Service
@SuppressWarnings("all")
public class PayWorkattendRecordItemServiceImpl extends ServiceImpl<PayWorkattendRecordItemMapper, PayWorkattendRecordItem> implements IPayWorkattendRecordItemService {
    @Autowired
    private PayWorkattendRecordItemMapper payWorkattendRecordItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "考勤信息-明细";

    /**
     * 查询考勤信息-明细
     *
     * @param recordItemSid 考勤信息-明细ID
     * @return 考勤信息-明细
     */
    @Override
    public PayWorkattendRecordItem selectPayWorkattendRecordItemById(Long recordItemSid) {
        PayWorkattendRecordItem payWorkattendRecordItem = payWorkattendRecordItemMapper.selectPayWorkattendRecordItemById(recordItemSid);
        MongodbUtil.find(payWorkattendRecordItem);
        return payWorkattendRecordItem;
    }

    /**
     * 查询考勤信息-明细列表
     *
     * @param payWorkattendRecordItem 考勤信息-明细
     * @return 考勤信息-明细
     */
    @Override
    public List<PayWorkattendRecordItem> selectPayWorkattendRecordItemList(PayWorkattendRecordItem payWorkattendRecordItem) {
        return payWorkattendRecordItemMapper.selectPayWorkattendRecordItemList(payWorkattendRecordItem);
    }

    /**
     * 新增考勤信息-明细
     * 需要注意编码重复校验
     *
     * @param payWorkattendRecordItem 考勤信息-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPayWorkattendRecordItem(PayWorkattendRecordItem payWorkattendRecordItem) {
        int row = payWorkattendRecordItemMapper.insert(payWorkattendRecordItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(payWorkattendRecordItem.getRecordItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改考勤信息-明细
     *
     * @param payWorkattendRecordItem 考勤信息-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePayWorkattendRecordItem(PayWorkattendRecordItem payWorkattendRecordItem) {
        PayWorkattendRecordItem response = payWorkattendRecordItemMapper.selectPayWorkattendRecordItemById(payWorkattendRecordItem.getRecordItemSid());
        int row = payWorkattendRecordItemMapper.updateById(payWorkattendRecordItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(payWorkattendRecordItem.getRecordItemSid(), BusinessType.UPDATE.getValue(), response, payWorkattendRecordItem, TITLE);
        }
        return row;
    }

    /**
     * 变更考勤信息-明细
     *
     * @param payWorkattendRecordItem 考勤信息-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePayWorkattendRecordItem(PayWorkattendRecordItem payWorkattendRecordItem) {
        PayWorkattendRecordItem response = payWorkattendRecordItemMapper.selectPayWorkattendRecordItemById(payWorkattendRecordItem.getRecordItemSid());
        int row = payWorkattendRecordItemMapper.updateAllById(payWorkattendRecordItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(payWorkattendRecordItem.getRecordItemSid(), BusinessType.CHANGE.getValue(), response, payWorkattendRecordItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除考勤信息-明细
     *
     * @param recordItemSids 需要删除的考勤信息-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePayWorkattendRecordItemByIds(List<Long> recordItemSids) {
        return payWorkattendRecordItemMapper.deleteBatchIds(recordItemSids);
    }
}
