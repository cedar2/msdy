package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinSaleDeductionBillItem;

/**
 * 销售扣款单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-04-09
 */
public interface IFinSaleDeductionBillItemService extends IService<FinSaleDeductionBillItem>{
    /**
     * 查询销售扣款单-明细
     * 
     * @param saleDeductionItemSid 销售扣款单-明细ID
     * @return 销售扣款单-明细
     */
    public FinSaleDeductionBillItem selectFinSaleDeductionItemById(Long saleDeductionItemSid);

    /**
     * 查询销售扣款单-明细列表
     * 
     * @param FinSaleDeductionBillItem 销售扣款单-明细
     * @return 销售扣款单-明细集合
     */
    public List<FinSaleDeductionBillItem> selectFinSaleDeductionItemList(FinSaleDeductionBillItem FinSaleDeductionBillItem);

    /**
     * 新增销售扣款单-明细
     * 
     * @param FinSaleDeductionBillItem 销售扣款单-明细
     * @return 结果
     */
    public int insertFinSaleDeductionItem(FinSaleDeductionBillItem FinSaleDeductionBillItem);

    /**
     * 修改销售扣款单-明细
     * 
     * @param FinSaleDeductionBillItem 销售扣款单-明细
     * @return 结果
     */
    public int updateFinSaleDeductionItem(FinSaleDeductionBillItem FinSaleDeductionBillItem);

    /**
     * 批量删除销售扣款单-明细
     * 
     * @param saleDeductionItemSids 需要删除的销售扣款单-明细ID
     * @return 结果
     */
    public int deleteFinSaleDeductionItemByIds(List<Long> saleDeductionItemSids);

}
