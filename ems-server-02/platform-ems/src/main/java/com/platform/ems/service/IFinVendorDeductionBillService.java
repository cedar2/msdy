package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinVendorDeductionBill;

/**
 * 供应商扣款单Service接口
 * 
 * @author qhq
 * @date 2021-05-31
 */
public interface IFinVendorDeductionBillService extends IService<FinVendorDeductionBill>{
    /**
     * 查询供应商扣款单
     * 
     * @param deductionBillSid 供应商扣款单ID
     * @return 供应商扣款单
     */
    public FinVendorDeductionBill selectFinVendorDeductionBillById(Long deductionBillSid);

    /**
     * 查询供应商扣款单列表
     * 
     * @param finVendorDeductionBill 供应商扣款单
     * @return 供应商扣款单集合
     */
    public List<FinVendorDeductionBill> selectFinVendorDeductionBillList(FinVendorDeductionBill finVendorDeductionBill);

    /**
     * 新增供应商扣款单
     * 
     * @param finVendorDeductionBill 供应商扣款单
     * @return 结果
     */
    public int insertFinVendorDeductionBill(FinVendorDeductionBill finVendorDeductionBill);

    /**
     * 修改供应商扣款单
     * 
     * @param finVendorDeductionBill 供应商扣款单
     * @return 结果
     */
    public int updateFinVendorDeductionBill(FinVendorDeductionBill finVendorDeductionBill);

    /**
     * 变更供应商扣款单
     *
     * @param finVendorDeductionBill 供应商扣款单
     * @return 结果
     */
    public int changeFinVendorDeductionBill(FinVendorDeductionBill finVendorDeductionBill);

    /**
     * 批量删除供应商扣款单
     * 
     * @param deductionBillSids 需要删除的供应商扣款单ID
     * @return 结果
     */
    public int deleteFinVendorDeductionBillByIds(List<Long>  deductionBillSids);

    /**
     * 更改确认状态
     * @param finVendorDeductionBill
     * @return
     */
    int check(FinVendorDeductionBill finVendorDeductionBill);

    /**
     * 生成流水
     * @param entity
     * @return
     */
    void insertBookAccount(FinVendorDeductionBill entity);

    /**
     * 作废单据
     * @param deductionBillSid
     * @return
     */
    int invalid(Long deductionBillSid);
}
