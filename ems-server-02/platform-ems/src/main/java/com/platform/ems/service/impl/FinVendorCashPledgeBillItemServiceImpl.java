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
import com.platform.ems.mapper.FinVendorCashPledgeBillItemMapper;
import com.platform.ems.domain.FinVendorCashPledgeBillItem;
import com.platform.ems.service.IFinVendorCashPledgeBillItemService;

/**
 * 供应商押金-明细Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Service
@SuppressWarnings("all")
public class FinVendorCashPledgeBillItemServiceImpl extends ServiceImpl<FinVendorCashPledgeBillItemMapper, FinVendorCashPledgeBillItem> implements IFinVendorCashPledgeBillItemService {

    @Autowired
    private FinVendorCashPledgeBillItemMapper finVendorCashPledgeBillItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商押金-明细";

    /**
     * 查询供应商押金-明细
     *
     * @param cashPledgeBillItemSid 供应商押金-明细ID
     * @return 供应商押金-明细
     */
    @Override
    public FinVendorCashPledgeBillItem selectFinVendorCashPledgeBillItemById(Long cashPledgeBillItemSid) {
        FinVendorCashPledgeBillItem finVendorCashPledgeBillItem = finVendorCashPledgeBillItemMapper.selectFinVendorCashPledgeBillItemById(cashPledgeBillItemSid);
        MongodbUtil.find(finVendorCashPledgeBillItem);
        return finVendorCashPledgeBillItem;
    }

    /**
     * 查询供应商押金-明细列表
     *
     * @param finVendorCashPledgeBillItem 供应商押金-明细
     * @return 供应商押金-明细
     */
    @Override
    public List<FinVendorCashPledgeBillItem> selectFinVendorCashPledgeBillItemList(FinVendorCashPledgeBillItem finVendorCashPledgeBillItem) {
        return finVendorCashPledgeBillItemMapper.selectFinVendorCashPledgeBillItemList(finVendorCashPledgeBillItem);
    }

    /**
     * 新增供应商押金-明细
     * 需要注意编码重复校验
     *
     * @param finVendorCashPledgeBillItem 供应商押金-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinVendorCashPledgeBillItem(FinVendorCashPledgeBillItem finVendorCashPledgeBillItem) {
        int row = finVendorCashPledgeBillItemMapper.insert(finVendorCashPledgeBillItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(finVendorCashPledgeBillItem.getCashPledgeBillItemSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商押金-明细
     *
     * @param finVendorCashPledgeBillItem 供应商押金-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinVendorCashPledgeBillItem(FinVendorCashPledgeBillItem finVendorCashPledgeBillItem) {
        FinVendorCashPledgeBillItem response = finVendorCashPledgeBillItemMapper.selectFinVendorCashPledgeBillItemById(finVendorCashPledgeBillItem.getCashPledgeBillItemSid());
        int row = finVendorCashPledgeBillItemMapper.updateById(finVendorCashPledgeBillItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finVendorCashPledgeBillItem.getCashPledgeBillItemSid(), BusinessType.UPDATE.ordinal(), response, finVendorCashPledgeBillItem, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商押金-明细
     *
     * @param finVendorCashPledgeBillItem 供应商押金-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinVendorCashPledgeBillItem(FinVendorCashPledgeBillItem finVendorCashPledgeBillItem) {
        FinVendorCashPledgeBillItem response = finVendorCashPledgeBillItemMapper.selectFinVendorCashPledgeBillItemById(finVendorCashPledgeBillItem.getCashPledgeBillItemSid());
        int row = finVendorCashPledgeBillItemMapper.updateAllById(finVendorCashPledgeBillItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finVendorCashPledgeBillItem.getCashPledgeBillItemSid(), BusinessType.CHANGE.ordinal(), response, finVendorCashPledgeBillItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商押金-明细
     *
     * @param cashPledgeBillItemSids 需要删除的供应商押金-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinVendorCashPledgeBillItemByIds(List<Long> cashPledgeBillItemSids) {
        return finVendorCashPledgeBillItemMapper.deleteBatchIds(cashPledgeBillItemSids);
    }

}
