package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinCustomerAccountAdjustBill;

/**
 * 客户调账单Service接口
 * 
 * @author qhq
 * @date 2021-05-26
 */
public interface IFinCustomerAccountAdjustBillService extends IService<FinCustomerAccountAdjustBill>{
    /**
     * 查询客户调账单
     * 
     * @param adjustBillSid 客户调账单ID
     * @return 客户调账单
     */
    public FinCustomerAccountAdjustBill selectFinCustomerAccountAdjustBillById(Long adjustBillSid);

    /**
     * 查询客户调账单列表
     * 
     * @param finCustomerAccountAdjustBill 客户调账单
     * @return 客户调账单集合
     */
    public List<FinCustomerAccountAdjustBill> selectFinCustomerAccountAdjustBillList(FinCustomerAccountAdjustBill finCustomerAccountAdjustBill);

    /**
     * 新增客户调账单
     * 
     * @param finCustomerAccountAdjustBill 客户调账单
     * @return 结果
     */
    public int insertFinCustomerAccountAdjustBill(FinCustomerAccountAdjustBill finCustomerAccountAdjustBill);

    /**
     * 修改客户调账单
     * 
     * @param finCustomerAccountAdjustBill 客户调账单
     * @return 结果
     */
    public int updateFinCustomerAccountAdjustBill(FinCustomerAccountAdjustBill finCustomerAccountAdjustBill);

    /**
     * 变更客户调账单
     *
     * @param finCustomerAccountAdjustBill 客户调账单
     * @return 结果
     */
    public int changeFinCustomerAccountAdjustBill(FinCustomerAccountAdjustBill finCustomerAccountAdjustBill);

    /**
     * 批量删除客户调账单
     * 
     * @param adjustBillSids 需要删除的客户调账单ID
     * @return 结果
     */
    public int deleteFinCustomerAccountAdjustBillByIds(List<Long>  adjustBillSids);

    /**
     * 更改确认状态
     * @param finCustomerAccountAdjustBill
     * @return
     */
    int check(FinCustomerAccountAdjustBill finCustomerAccountAdjustBill);

    /**
     * 生成流水
     * @param entity
     * @return
     */
    void insertBookAccount(FinCustomerAccountAdjustBill entity);

    /**
     * 作废单据
     * @param adjustBillSid
     * @return
     */
    int invalid(Long adjustBillSid);
}
