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
import com.platform.ems.mapper.FinVendorFundsFreezeBillItemMapper;
import com.platform.ems.domain.FinVendorFundsFreezeBillItem;
import com.platform.ems.service.IFinVendorFundsFreezeBillItemService;

/**
 * 供应商暂押款-明细Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-22
 */
@Service
@SuppressWarnings("all")
public class FinVendorFundsFreezeBillItemServiceImpl extends ServiceImpl<FinVendorFundsFreezeBillItemMapper, FinVendorFundsFreezeBillItem> implements IFinVendorFundsFreezeBillItemService {
    @Autowired
    private FinVendorFundsFreezeBillItemMapper finVendorFundsFreezeBillItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商暂押款-明细";

    /**
     * 查询供应商暂押款-明细
     *
     * @param fundsFreezeBillItemSid 供应商暂押款-明细ID
     * @return 供应商暂押款-明细
     */
    @Override
    public FinVendorFundsFreezeBillItem selectFinVendorFundsFreezeBillItemById(Long fundsFreezeBillItemSid) {
        FinVendorFundsFreezeBillItem finVendorFundsFreezeBillItem = finVendorFundsFreezeBillItemMapper.selectFinVendorFundsFreezeBillItemById(fundsFreezeBillItemSid);
        MongodbUtil.find(finVendorFundsFreezeBillItem);
        return finVendorFundsFreezeBillItem;
    }

    /**
     * 查询供应商暂押款-明细列表
     *
     * @param finVendorFundsFreezeBillItem 供应商暂押款-明细
     * @return 供应商暂押款-明细
     */
    @Override
    public List<FinVendorFundsFreezeBillItem> selectFinVendorFundsFreezeBillItemList(FinVendorFundsFreezeBillItem finVendorFundsFreezeBillItem) {
        return finVendorFundsFreezeBillItemMapper.selectFinVendorFundsFreezeBillItemList(finVendorFundsFreezeBillItem);
    }

    /**
     * 新增供应商暂押款-明细
     * 需要注意编码重复校验
     *
     * @param finVendorFundsFreezeBillItem 供应商暂押款-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinVendorFundsFreezeBillItem(FinVendorFundsFreezeBillItem finVendorFundsFreezeBillItem) {
        int row = finVendorFundsFreezeBillItemMapper.insert(finVendorFundsFreezeBillItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(finVendorFundsFreezeBillItem.getFundsFreezeBillItemSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商暂押款-明细
     *
     * @param finVendorFundsFreezeBillItem 供应商暂押款-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinVendorFundsFreezeBillItem(FinVendorFundsFreezeBillItem finVendorFundsFreezeBillItem) {
        FinVendorFundsFreezeBillItem response = finVendorFundsFreezeBillItemMapper.selectFinVendorFundsFreezeBillItemById(finVendorFundsFreezeBillItem.getFundsFreezeBillItemSid());
        int row = finVendorFundsFreezeBillItemMapper.updateById(finVendorFundsFreezeBillItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finVendorFundsFreezeBillItem.getFundsFreezeBillItemSid(), BusinessType.UPDATE.ordinal(), response, finVendorFundsFreezeBillItem, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商暂押款-明细
     *
     * @param finVendorFundsFreezeBillItem 供应商暂押款-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeFinVendorFundsFreezeBillItem(FinVendorFundsFreezeBillItem finVendorFundsFreezeBillItem) {
        FinVendorFundsFreezeBillItem response = finVendorFundsFreezeBillItemMapper.selectFinVendorFundsFreezeBillItemById(finVendorFundsFreezeBillItem.getFundsFreezeBillItemSid());
        int row = finVendorFundsFreezeBillItemMapper.updateAllById(finVendorFundsFreezeBillItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(finVendorFundsFreezeBillItem.getFundsFreezeBillItemSid(), BusinessType.CHANGE.ordinal(), response, finVendorFundsFreezeBillItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商暂押款-明细
     *
     * @param fundsFreezeBillItemSids 需要删除的供应商暂押款-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinVendorFundsFreezeBillItemByIds(List<Long> fundsFreezeBillItemSids) {
        return finVendorFundsFreezeBillItemMapper.deleteBatchIds(fundsFreezeBillItemSids);
    }


}
