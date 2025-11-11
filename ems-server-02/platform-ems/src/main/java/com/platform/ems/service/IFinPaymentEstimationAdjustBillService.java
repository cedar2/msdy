package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinPaymentEstimationAdjustBill;

/**
 * 应付暂估调价量单Service接口
 *
 * @author chenkw
 * @date 2022-01-10
 */
public interface IFinPaymentEstimationAdjustBillService extends IService<FinPaymentEstimationAdjustBill> {
    /**
     * 查询应付暂估调价量单
     *
     * @param paymentEstimationAdjustBillSid 应付暂估调价量单ID
     * @return 应付暂估调价量单
     */
    public FinPaymentEstimationAdjustBill selectFinPaymentEstimationAdjustBillById(Long paymentEstimationAdjustBillSid);

    /**
     * 查询应付暂估调价量单列表
     *
     * @param finPaymentEstimationAdjustBill 应付暂估调价量单
     * @return 应付暂估调价量单集合
     */
    public List<FinPaymentEstimationAdjustBill> selectFinPaymentEstimationAdjustBillList(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill);

    /**
     * 新增应付暂估调价量单
     *
     * @param finPaymentEstimationAdjustBill 应付暂估调价量单
     * @return 结果
     */
    public int insertFinPaymentEstimationAdjustBill(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill);

    /**
     * 修改应付暂估调价量单
     *
     * @param finPaymentEstimationAdjustBill 应付暂估调价量单
     * @return 结果
     */
    public int updateFinPaymentEstimationAdjustBill(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill);

    /**
     * 变更应付暂估调价量单
     *
     * @param finPaymentEstimationAdjustBill 应付暂估调价量单
     * @return 结果
     */
    public int changeFinPaymentEstimationAdjustBill(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill);

    /**
     * 批量删除应付暂估调价量单
     *
     * @param paymentEstimationAdjustBillSids 需要删除的应付暂估调价量单ID
     * @return 结果
     */
    public int deleteFinPaymentEstimationAdjustBillByIds(List<Long> paymentEstimationAdjustBillSids);

    /**
     * 更改确认状态
     *
     * @param finPaymentEstimationAdjustBill
     * @return
     */
    int check(FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill);

}
