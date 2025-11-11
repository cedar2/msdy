package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinBookPaymentEstimation;
import com.platform.ems.domain.FinBookPaymentEstimationItem;

/**
 * 财务流水账-明细-应付暂估Service接口
 *
 * @author linhongwei
 * @date 2021-05-31
 */
public interface IFinBookPaymentEstimationItemService extends IService<FinBookPaymentEstimationItem>{

    /**
     * 查询财务流水账-明细-应付暂估列表
     */
    List<FinBookPaymentEstimationItem> selectFinBookPaymentEstimationItemList(FinBookPaymentEstimationItem finBookPaymentEstimationItem);

    /**
     * 修改 核销中金额和核销中数量 && 已核销金额和已核销数量
     */
    int updateByAmountTax(FinBookPaymentEstimationItem finBookPaymentEstimationItem);

    /**
     * 设置是否业务对账
     */
    int setBusinessVerify(FinBookPaymentEstimationItem finBookPaymentEstimationItem);

    /**
     * 设置对账账期
     */
    int setBusinessVerifyPeriod(FinBookPaymentEstimationItem finBookPaymentEstimationItem);

    /**
     * 新增
     */
    public int insertFinBookPaymentEstimationItem(FinBookPaymentEstimationItem estimationItem);

    /**
     * 批量新增
     */
    public int insertByList(FinBookPaymentEstimation estimation);

    /**
     * 明细行行号为空的设置行号
     */
    void setItemNum(List<FinBookPaymentEstimationItem> list);

    /**
     * 批量修改
     */
    int updateByList(FinBookPaymentEstimation bill);

    /**
     * 批量删除
     */
    int deleteByList(List<FinBookPaymentEstimationItem> itemList);

}
