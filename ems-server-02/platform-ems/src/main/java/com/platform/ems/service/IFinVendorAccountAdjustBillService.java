package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinVendorAccountAdjustBill;

/**
 * 供应商调账单Service接口
 * 
 * @author qhq
 * @date 2021-05-26
 */
public interface IFinVendorAccountAdjustBillService extends IService<FinVendorAccountAdjustBill>{
    /**
     * 查询供应商调账单
     * 
     * @param adjustBillSid 供应商调账单ID
     * @return 供应商调账单
     */
    public FinVendorAccountAdjustBill selectFinVendorAccountAdjustBillById(Long adjustBillSid);

    /**
     * 查询供应商调账单列表
     * 
     * @param finVendorAccountAdjustBill 供应商调账单
     * @return 供应商调账单集合
     */
    public List<FinVendorAccountAdjustBill> selectFinVendorAccountAdjustBillList(FinVendorAccountAdjustBill finVendorAccountAdjustBill);

    /**
     * 新增供应商调账单
     * 
     * @param finVendorAccountAdjustBill 供应商调账单
     * @return 结果
     */
    public int insertFinVendorAccountAdjustBill(FinVendorAccountAdjustBill finVendorAccountAdjustBill);

    /**
     * 修改供应商调账单
     * 
     * @param finVendorAccountAdjustBill 供应商调账单
     * @return 结果
     */
    public int updateFinVendorAccountAdjustBill(FinVendorAccountAdjustBill finVendorAccountAdjustBill);

    /**
     * 变更供应商调账单
     *
     * @param finVendorAccountAdjustBill 供应商调账单
     * @return 结果
     */
    public int changeFinVendorAccountAdjustBill(FinVendorAccountAdjustBill finVendorAccountAdjustBill);

    /**
     * 批量删除供应商调账单
     * 
     * @param adjustBillSids 需要删除的供应商调账单ID
     * @return 结果
     */
    public int deleteFinVendorAccountAdjustBillByIds(List<Long>  adjustBillSids);

    /**
     * 更改确认状态
     * @param finVendorAccountAdjustBill
     * @return
     */
    int check(FinVendorAccountAdjustBill finVendorAccountAdjustBill);

    /**
     * 生成流水
     * @param entity
     * @return
     */
    void insertBookAccount(FinVendorAccountAdjustBill entity);

    /**
     * 作废单据
     * @param adjustBillSid
     * @return
     */
    int invalid(Long adjustBillSid);
}
