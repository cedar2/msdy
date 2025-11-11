package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.FinCustomerCashPledgeBillItemMapper;
import com.platform.ems.domain.FinCustomerCashPledgeBillItem;
import com.platform.ems.service.IFinCustomerCashPledgeBillItemService;

/**
 * 客户押金-明细Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Service
@SuppressWarnings("all")
public class FinCustomerCashPledgeBillItemServiceImpl extends ServiceImpl<FinCustomerCashPledgeBillItemMapper, FinCustomerCashPledgeBillItem> implements IFinCustomerCashPledgeBillItemService {
    @Autowired
    private FinCustomerCashPledgeBillItemMapper finCustomerCashPledgeBillItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "客户押金-明细";

    /**
     * 查询客户押金-明细
     *
     * @param cashPledgeBillItemSid 客户押金-明细ID
     * @return 客户押金-明细
     */
    @Override
    public FinCustomerCashPledgeBillItem selectFinCustomerCashPledgeBillItemById(Long cashPledgeBillItemSid) {
        FinCustomerCashPledgeBillItem finCustomerCashPledgeBillItem = finCustomerCashPledgeBillItemMapper.selectFinCustomerCashPledgeBillItemById(cashPledgeBillItemSid);
        MongodbUtil.find(finCustomerCashPledgeBillItem);
        return finCustomerCashPledgeBillItem;
    }

    /**
     * 查询客户押金-明细列表
     *
     * @param finCustomerCashPledgeBillItem 客户押金-明细
     * @return 客户押金-明细
     */
    @Override
    public List<FinCustomerCashPledgeBillItem> selectFinCustomerCashPledgeBillItemList(FinCustomerCashPledgeBillItem finCustomerCashPledgeBillItem) {
        return finCustomerCashPledgeBillItemMapper.selectFinCustomerCashPledgeBillItemList(finCustomerCashPledgeBillItem);
    }

    /**
     * 新增客户押金-明细
     * 需要注意编码重复校验
     *
     * @param finCustomerCashPledgeBillItem 客户押金-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinCustomerCashPledgeBillItem(FinCustomerCashPledgeBillItem finCustomerCashPledgeBillItem) {
        int row = finCustomerCashPledgeBillItemMapper.insert(finCustomerCashPledgeBillItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(finCustomerCashPledgeBillItem.getCashPledgeBillItemSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改客户押金-明细
     *
     * @param finCustomerCashPledgeBillItem 客户押金-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinCustomerCashPledgeBillItem(FinCustomerCashPledgeBillItem finCustomerCashPledgeBillItem) {
        FinCustomerCashPledgeBillItem response = finCustomerCashPledgeBillItemMapper.selectFinCustomerCashPledgeBillItemById(finCustomerCashPledgeBillItem.getCashPledgeBillItemSid());
        int row = finCustomerCashPledgeBillItemMapper.updateById(finCustomerCashPledgeBillItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finCustomerCashPledgeBillItem.getCashPledgeBillItemSid(), BusinessType.UPDATE.ordinal(), response, finCustomerCashPledgeBillItem, TITLE);
        }
        return row;
    }

    /**
     * 变更客户押金-明细
     *
     * @param finCustomerCashPledgeBillItem 客户押金-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinCustomerCashPledgeBillItem(FinCustomerCashPledgeBillItem finCustomerCashPledgeBillItem) {
        FinCustomerCashPledgeBillItem response = finCustomerCashPledgeBillItemMapper.selectFinCustomerCashPledgeBillItemById(finCustomerCashPledgeBillItem.getCashPledgeBillItemSid());
        int row = finCustomerCashPledgeBillItemMapper.updateAllById(finCustomerCashPledgeBillItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finCustomerCashPledgeBillItem.getCashPledgeBillItemSid(), BusinessType.CHANGE.ordinal(), response, finCustomerCashPledgeBillItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除客户押金-明细
     *
     * @param cashPledgeBillItemSids 需要删除的客户押金-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinCustomerCashPledgeBillItemByIds(List<Long> cashPledgeBillItemSids) {
        return finCustomerCashPledgeBillItemMapper.deleteBatchIds(cashPledgeBillItemSids);
    }

}
