package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinSaleDeductionBill;

/**
 * 销售扣款单Service接口
 * 
 * @author linhongwei
 * @date 2021-04-09
 */
public interface IFinSaleDeductionBillService extends IService<FinSaleDeductionBill>{
    /**
     * 查询销售扣款单
     * 
     * @param saleDeductionSid 销售扣款单ID
     * @return 销售扣款单
     */
    public FinSaleDeductionBill selectFinSaleDeductionById(Long saleDeductionSid);

    /**
     * 查询销售扣款单列表
     * 
     * @param FinSaleDeductionBill 销售扣款单
     * @return 销售扣款单集合
     */
    public List<FinSaleDeductionBill> selectFinSaleDeductionList(FinSaleDeductionBill FinSaleDeductionBill);

    /**
     * 新增销售扣款单
     * 
     * @param FinSaleDeductionBill 销售扣款单
     * @return 结果
     */
    public int insertFinSaleDeduction(FinSaleDeductionBill FinSaleDeductionBill);

    /**
     * 修改销售扣款单
     * 
     * @param FinSaleDeductionBill 销售扣款单
     * @return 结果
     */
    public int updateFinSaleDeduction(FinSaleDeductionBill FinSaleDeductionBill);

    /**
     * 批量删除销售扣款单
     * 
     * @param saleDeductionSids 需要删除的销售扣款单ID
     * @return 结果
     */
    public int deleteFinSaleDeductionByIds(Long[] saleDeductionSids);

    /**
     * 销售扣款单确认
     */
    int confirm(FinSaleDeductionBill FinSaleDeductionBill);

    /**
     * 销售扣款单变更
     */
    int change(FinSaleDeductionBill FinSaleDeductionBill);
}
