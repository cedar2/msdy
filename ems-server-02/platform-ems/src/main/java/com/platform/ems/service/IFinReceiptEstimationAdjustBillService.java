package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinReceiptEstimationAdjustBill;

/**
 * 应收暂估调价量单Service接口
 *
 * @author chenkw
 * @date 2022-01-10
 */
public interface IFinReceiptEstimationAdjustBillService extends IService<FinReceiptEstimationAdjustBill> {
    /**
     * 查询应收暂估调价量单
     *
     * @param receiptEstimationAdjustBillSid 应收暂估调价量单ID
     * @return 应收暂估调价量单
     */
    public FinReceiptEstimationAdjustBill selectFinReceiptEstimationAdjustBillById(Long receiptEstimationAdjustBillSid);

    /**
     * 查询应收暂估调价量单列表
     *
     * @param finReceiptEstimationAdjustBill 应收暂估调价量单
     * @return 应收暂估调价量单集合
     */
    public List<FinReceiptEstimationAdjustBill> selectFinReceiptEstimationAdjustBillList(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill);

    /**
     * 新增应收暂估调价量单
     *
     * @param finReceiptEstimationAdjustBill 应收暂估调价量单
     * @return 结果
     */
    public int insertFinReceiptEstimationAdjustBill(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill);

    /**
     * 修改应收暂估调价量单
     *
     * @param finReceiptEstimationAdjustBill 应收暂估调价量单
     * @return 结果
     */
    public int updateFinReceiptEstimationAdjustBill(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill);

    /**
     * 变更应收暂估调价量单
     *
     * @param finReceiptEstimationAdjustBill 应收暂估调价量单
     * @return 结果
     */
    public int changeFinReceiptEstimationAdjustBill(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill);

    /**
     * 批量删除应收暂估调价量单
     *
     * @param receiptEstimationAdjustBillSids 需要删除的应收暂估调价量单ID
     * @return 结果
     */
    public int deleteFinReceiptEstimationAdjustBillByIds(List<Long> receiptEstimationAdjustBillSids);

    /**
     * 更改确认状态
     *
     * @param finReceiptEstimationAdjustBill
     * @return
     */
    int check(FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill);

}
