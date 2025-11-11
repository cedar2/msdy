package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinPurchaseDeductionBillItem;
import com.platform.ems.mapper.FinPurchaseDeductionBillItemMapper;
import com.platform.ems.service.IFinPurchaseDeductionItemService;

/**
 * 采购扣款单-明细Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-10
 */
@Service
@SuppressWarnings("all")
public class FinPurchaseDeductionItemServiceImpl extends ServiceImpl<FinPurchaseDeductionBillItemMapper,FinPurchaseDeductionBillItem>  implements IFinPurchaseDeductionItemService {
    @Autowired
    private FinPurchaseDeductionBillItemMapper FinPurchaseDeductionBillItemMapper;

    /**
     * 查询采购扣款单-明细
     * 
     * @param purchaseDeductionItemSid 采购扣款单-明细ID
     * @return 采购扣款单-明细
     */
    @Override
    public FinPurchaseDeductionBillItem selectFinPurchaseDeductionItemById(Long purchaseDeductionItemSid) {
        return FinPurchaseDeductionBillItemMapper.selectFinPurchaseDeductionItemById(purchaseDeductionItemSid);
    }

    /**
     * 查询采购扣款单-明细列表
     * 
     * @param FinPurchaseDeductionBillItem 采购扣款单-明细
     * @return 采购扣款单-明细
     */
    @Override
    public List<FinPurchaseDeductionBillItem> selectFinPurchaseDeductionItemList(FinPurchaseDeductionBillItem FinPurchaseDeductionBillItem) {
        return FinPurchaseDeductionBillItemMapper.selectFinPurchaseDeductionItemList(FinPurchaseDeductionBillItem);
    }

    /**
     * 新增采购扣款单-明细
     * 需要注意编码重复校验
     * @param FinPurchaseDeductionBillItem 采购扣款单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinPurchaseDeductionItem(FinPurchaseDeductionBillItem FinPurchaseDeductionBillItem) {
        return FinPurchaseDeductionBillItemMapper.insert(FinPurchaseDeductionBillItem);
    }

    /**
     * 修改采购扣款单-明细
     * 
     * @param FinPurchaseDeductionBillItem 采购扣款单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinPurchaseDeductionItem(FinPurchaseDeductionBillItem FinPurchaseDeductionBillItem) {
        return FinPurchaseDeductionBillItemMapper.updateById(FinPurchaseDeductionBillItem);
    }

    /**
     * 批量删除采购扣款单-明细
     * 
     * @param purchaseDeductionItemSids 需要删除的采购扣款单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinPurchaseDeductionItemByIds(List<Long> purchaseDeductionItemSids) {
        return FinPurchaseDeductionBillItemMapper.deleteBatchIds(purchaseDeductionItemSids);
    }


}
