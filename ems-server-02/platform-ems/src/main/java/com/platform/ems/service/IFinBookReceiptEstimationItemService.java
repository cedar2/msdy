package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinBookReceiptEstimation;
import com.platform.ems.domain.FinBookReceiptEstimationItem;

/**
 * 财务流水账-明细-应收暂估Service接口
 *
 * @author linhongwei
 * @date 2021-06-08
 */
public interface IFinBookReceiptEstimationItemService extends IService<FinBookReceiptEstimationItem>{

    /**
     * 查询财务流水账-明细-应收暂估列表
     */
    public List<FinBookReceiptEstimationItem> selectFinBookReceiptEstimationItemList(FinBookReceiptEstimationItem finBookReceiptEstimationItem);

    /**
     * 修改 核销中金额和核销中数量 && 已核销金额和已核销数量
     */
    int updateByAmountTax(FinBookReceiptEstimationItem finBookReceiptEstimationItem);

    /**
     * 设置是否业务对账
     */
    int setBusinessVerify(FinBookReceiptEstimationItem finBookReceiptEstimationItem);

    /**
     * 设置对账账期
     */
    int setBusinessVerifyPeriod(FinBookReceiptEstimationItem finBookReceiptEstimationItem);

    /**
     * 新增
     */
    public int insertFinBookReceiptEstimationItem(FinBookReceiptEstimationItem estimationItem);

    /**
     * 批量新增
     */
    public int insertByList(FinBookReceiptEstimation estimation);

    /**
     * 明细行行号为空的设置行号
     */
    void setItemNum(List<FinBookReceiptEstimationItem> list);

    /**
     * 批量修改
     */
    int updateByList(FinBookReceiptEstimation bill);

    /**
     * 批量删除
     */
    int deleteByList(List<FinBookReceiptEstimationItem> itemList);
}
