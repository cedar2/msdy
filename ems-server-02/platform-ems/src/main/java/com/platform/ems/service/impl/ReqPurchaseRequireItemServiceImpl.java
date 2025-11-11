package com.platform.ems.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.ReqPurchaseRequireItemMapper;
import com.platform.ems.domain.ReqPurchaseRequireItem;
import com.platform.ems.service.IReqPurchaseRequireItemService;

/**
 * 申请单-明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-06
 */
@Service
@SuppressWarnings("all")
public class ReqPurchaseRequireItemServiceImpl extends ServiceImpl<ReqPurchaseRequireItemMapper, ReqPurchaseRequireItem> implements IReqPurchaseRequireItemService {
    @Autowired
    private ReqPurchaseRequireItemMapper reqPurchaseRequireItemMapper;

    /**
     * 查询申请单-明细
     *
     * @param purchaseRequireItemSid 申请单-明细ID
     * @return 申请单-明细
     */
    @Override
    public ReqPurchaseRequireItem selectReqPurchaseRequireItemById(Long purchaseRequireItemSid) {
        return reqPurchaseRequireItemMapper.selectReqPurchaseRequireItemById(purchaseRequireItemSid);
    }

    /**
     * 查询申请单-明细列表
     *
     * @param reqPurchaseRequireItem 申请单-明细
     * @return 申请单-明细
     */
    @Override
    public List<ReqPurchaseRequireItem> selectReqPurchaseRequireItemList(ReqPurchaseRequireItem reqPurchaseRequireItem) {
        return reqPurchaseRequireItemMapper.selectReqPurchaseRequireItemList(reqPurchaseRequireItem);
    }

    /**
     * 新增申请单-明细
     * 需要注意编码重复校验
     *
     * @param reqPurchaseRequireItem 申请单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertReqPurchaseRequireItem(ReqPurchaseRequireItem reqPurchaseRequireItem) {
        return reqPurchaseRequireItemMapper.insert(reqPurchaseRequireItem);
    }

    /**
     * 修改申请单-明细
     *
     * @param reqPurchaseRequireItem 申请单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateReqPurchaseRequireItem(ReqPurchaseRequireItem reqPurchaseRequireItem) {
        return reqPurchaseRequireItemMapper.updateById(reqPurchaseRequireItem);
    }

    /**
     * 批量删除申请单-明细
     *
     * @param purchaseRequireItemSids 需要删除的申请单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteReqPurchaseRequireItemByIds(List<Long> purchaseRequireItemSids) {
        return reqPurchaseRequireItemMapper.deleteBatchIds(purchaseRequireItemSids);
    }

    /**
     * 查询申请单-明细列表
     *
     * @param reqPurchaseRequireItem 申请单-明细
     * @return 申请单-明细
     */
    @Override
    public List<ReqPurchaseRequireItem> getItemList(ReqPurchaseRequireItem reqPurchaseRequireItem) {
        return reqPurchaseRequireItemMapper.getItemList(reqPurchaseRequireItem);
    }
}
