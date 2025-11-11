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
import com.platform.ems.mapper.FinCustomerFundsFreezeBillItemMapper;
import com.platform.ems.domain.FinCustomerFundsFreezeBillItem;
import com.platform.ems.service.IFinCustomerFundsFreezeBillItemService;

/**
 * 客户暂押款-明细Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Service
@SuppressWarnings("all")
public class FinCustomerFundsFreezeBillItemServiceImpl extends ServiceImpl<FinCustomerFundsFreezeBillItemMapper, FinCustomerFundsFreezeBillItem> implements IFinCustomerFundsFreezeBillItemService {
    @Autowired
    private FinCustomerFundsFreezeBillItemMapper finCustomerFundsFreezeBillItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "客户暂押款-明细";

    /**
     * 查询客户暂押款-明细
     *
     * @param fundsFreezeBillItemSid 客户暂押款-明细ID
     * @return 客户暂押款-明细
     */
    @Override
    public FinCustomerFundsFreezeBillItem selectFinCustomerFundsFreezeBillItemById(Long fundsFreezeBillItemSid) {
        FinCustomerFundsFreezeBillItem finCustomerFundsFreezeBillItem = finCustomerFundsFreezeBillItemMapper.selectFinCustomerFundsFreezeBillItemById(fundsFreezeBillItemSid);
        MongodbUtil.find(finCustomerFundsFreezeBillItem);
        return finCustomerFundsFreezeBillItem;
    }

    /**
     * 查询客户暂押款-明细列表
     *
     * @param finCustomerFundsFreezeBillItem 客户暂押款-明细
     * @return 客户暂押款-明细
     */
    @Override
    public List<FinCustomerFundsFreezeBillItem> selectFinCustomerFundsFreezeBillItemList(FinCustomerFundsFreezeBillItem finCustomerFundsFreezeBillItem) {
        return finCustomerFundsFreezeBillItemMapper.selectFinCustomerFundsFreezeBillItemList(finCustomerFundsFreezeBillItem);
    }

    /**
     * 新增客户暂押款-明细
     * 需要注意编码重复校验
     *
     * @param finCustomerFundsFreezeBillItem 客户暂押款-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinCustomerFundsFreezeBillItem(FinCustomerFundsFreezeBillItem finCustomerFundsFreezeBillItem) {
        int row = finCustomerFundsFreezeBillItemMapper.insert(finCustomerFundsFreezeBillItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(finCustomerFundsFreezeBillItem.getFundsFreezeBillItemSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改客户暂押款-明细
     *
     * @param finCustomerFundsFreezeBillItem 客户暂押款-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinCustomerFundsFreezeBillItem(FinCustomerFundsFreezeBillItem finCustomerFundsFreezeBillItem) {
        FinCustomerFundsFreezeBillItem response = finCustomerFundsFreezeBillItemMapper.selectFinCustomerFundsFreezeBillItemById(finCustomerFundsFreezeBillItem.getFundsFreezeBillItemSid());
        int row = finCustomerFundsFreezeBillItemMapper.updateById(finCustomerFundsFreezeBillItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finCustomerFundsFreezeBillItem.getFundsFreezeBillItemSid(), BusinessType.UPDATE.ordinal(), response, finCustomerFundsFreezeBillItem, TITLE);
        }
        return row;
    }

    /**
     * 变更客户暂押款-明细
     *
     * @param finCustomerFundsFreezeBillItem 客户暂押款-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinCustomerFundsFreezeBillItem(FinCustomerFundsFreezeBillItem finCustomerFundsFreezeBillItem) {
        FinCustomerFundsFreezeBillItem response = finCustomerFundsFreezeBillItemMapper.selectFinCustomerFundsFreezeBillItemById(finCustomerFundsFreezeBillItem.getFundsFreezeBillItemSid());
        int row = finCustomerFundsFreezeBillItemMapper.updateAllById(finCustomerFundsFreezeBillItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finCustomerFundsFreezeBillItem.getFundsFreezeBillItemSid(), BusinessType.CHANGE.ordinal(), response, finCustomerFundsFreezeBillItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除客户暂押款-明细
     *
     * @param fundsFreezeBillItemSids 需要删除的客户暂押款-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinCustomerFundsFreezeBillItemByIds(List<Long> fundsFreezeBillItemSids) {
        return finCustomerFundsFreezeBillItemMapper.deleteBatchIds(fundsFreezeBillItemSids);
    }

}
