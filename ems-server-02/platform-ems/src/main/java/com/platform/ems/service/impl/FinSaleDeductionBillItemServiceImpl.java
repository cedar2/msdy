package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinSaleDeductionBillItem;
import com.platform.ems.mapper.FinSaleDeductionBillItemMapper;
import com.platform.ems.service.IFinSaleDeductionBillItemService;

/**
 * 销售扣款单-明细Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-09
 */
@Service
@SuppressWarnings("all")
public class FinSaleDeductionBillItemServiceImpl extends ServiceImpl<FinSaleDeductionBillItemMapper,FinSaleDeductionBillItem>  implements IFinSaleDeductionBillItemService {
    @Autowired
    private FinSaleDeductionBillItemMapper finSaleDeductionItemMapper;

    /**
     * 查询销售扣款单-明细
     * 
     * @param saleDeductionItemSid 销售扣款单-明细ID
     * @return 销售扣款单-明细
     */
    @Override
    public FinSaleDeductionBillItem selectFinSaleDeductionItemById(Long saleDeductionItemSid) {
        return finSaleDeductionItemMapper.selectFinSaleDeductionItemById(saleDeductionItemSid);
    }

    /**
     * 查询销售扣款单-明细列表
     * 
     * @param FinSaleDeductionBillItem 销售扣款单-明细
     * @return 销售扣款单-明细
     */
    @Override
    public List<FinSaleDeductionBillItem> selectFinSaleDeductionItemList(FinSaleDeductionBillItem FinSaleDeductionBillItem) {
        return finSaleDeductionItemMapper.selectFinSaleDeductionItemList(FinSaleDeductionBillItem);
    }

    /**
     * 新增销售扣款单-明细
     * 需要注意编码重复校验
     * @param FinSaleDeductionBillItem 销售扣款单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinSaleDeductionItem(FinSaleDeductionBillItem FinSaleDeductionBillItem) {
        return finSaleDeductionItemMapper.insert(FinSaleDeductionBillItem);
    }

    /**
     * 修改销售扣款单-明细
     * 
     * @param FinSaleDeductionBillItem 销售扣款单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinSaleDeductionItem(FinSaleDeductionBillItem FinSaleDeductionBillItem) {
        return finSaleDeductionItemMapper.updateById(FinSaleDeductionBillItem);
    }

    /**
     * 批量删除销售扣款单-明细
     * 
     * @param saleDeductionItemSids 需要删除的销售扣款单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinSaleDeductionItemByIds(List<Long> saleDeductionItemSids) {
        return finSaleDeductionItemMapper.deleteBatchIds(saleDeductionItemSids);
    }


}
