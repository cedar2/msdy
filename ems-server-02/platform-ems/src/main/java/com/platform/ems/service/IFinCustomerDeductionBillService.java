package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinCustomerDeductionBill;
import com.platform.ems.domain.FinVendorDeductionBill;

/**
 * 客户扣款单Service接口
 * 
 * @author linhongwei
 * @date 2021-06-08
 */
public interface IFinCustomerDeductionBillService extends IService<FinCustomerDeductionBill>{
    /**
     * 查询客户扣款单
     * 
     * @param deductionBillSid 客户扣款单ID
     * @return 客户扣款单
     */
    public FinCustomerDeductionBill selectFinCustomerDeductionBillById(Long deductionBillSid);

    /**
     * 查询客户扣款单列表
     * 
     * @param finCustomerDeductionBill 客户扣款单
     * @return 客户扣款单集合
     */
    public List<FinCustomerDeductionBill> selectFinCustomerDeductionBillList(FinCustomerDeductionBill finCustomerDeductionBill);

    /**
     * 新增客户扣款单
     * 
     * @param finCustomerDeductionBill 客户扣款单
     * @return 结果
     */
    public int insertFinCustomerDeductionBill(FinCustomerDeductionBill finCustomerDeductionBill);

    /**
     * 修改客户扣款单
     * 
     * @param finCustomerDeductionBill 客户扣款单
     * @return 结果
     */
    public int updateFinCustomerDeductionBill(FinCustomerDeductionBill finCustomerDeductionBill);

    /**
     * 变更客户扣款单
     *
     * @param finCustomerDeductionBill 客户扣款单
     * @return 结果
     */
    public int changeFinCustomerDeductionBill(FinCustomerDeductionBill finCustomerDeductionBill);

    /**
     * 批量删除客户扣款单
     * 
     * @param deductionBillSids 需要删除的客户扣款单ID
     * @return 结果
     */
    public int deleteFinCustomerDeductionBillByIds(List<Long>  deductionBillSids);

    /**
     * 更改确认状态
     * @param finCustomerDeductionBill
     * @return
     */
    int check(FinCustomerDeductionBill finCustomerDeductionBill);

    /**
     * 生成流水
     * @param entity
     * @return
     */
    void insertBookAccount(FinCustomerDeductionBill entity);

    /**
     * 作废单据
     * @param deductionBillSid
     * @return
     */
    int invalid(Long deductionBillSid);
}
