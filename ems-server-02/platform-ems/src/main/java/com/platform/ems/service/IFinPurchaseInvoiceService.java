package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinPurchaseInvoice;
import com.platform.ems.domain.FinPurchaseInvoiceAttachment;
import com.platform.ems.domain.FinPurchaseInvoiceDiscount;
import com.platform.ems.domain.FinPurchaseInvoiceItem;

/**
 * 采购发票Service接口
 *
 * @author linhongwei
 * @date 2021-04-20
 */
public interface IFinPurchaseInvoiceService extends IService<FinPurchaseInvoice> {
    /**
     * 查询采购发票
     *
     * @param purchaseInvoiceSid 采购发票ID
     * @return 采购发票
     */
    public FinPurchaseInvoice selectFinPurchaseInvoiceById(Long purchaseInvoiceSid);

    /**
     * 查询采购发票列表
     *
     * @param finPurchaseInvoice 采购发票
     * @return 采购发票集合
     */
    public List<FinPurchaseInvoice> selectFinPurchaseInvoiceList(FinPurchaseInvoice finPurchaseInvoice);

    /**
     * 新增采购发票
     *
     * @param finPurchaseInvoice 采购发票
     * @return 结果
     */
    public int insertFinPurchaseInvoice(FinPurchaseInvoice finPurchaseInvoice);

    /**
     * 修改采购发票
     *
     * @param finPurchaseInvoice 采购发票
     * @return 结果
     */
    public int updateFinPurchaseInvoice(FinPurchaseInvoice finPurchaseInvoice);

    /**
     * 批量删除采购发票
     *
     * @param purchaseInvoiceSids 需要删除的采购发票ID
     * @return 结果
     */
    public int deleteFinPurchaseInvoiceByIds(List<Long> purchaseInvoiceSids);

    /**
     * 提交前检验
     *
     * @param entity 发票详细信息
     * @return 结果
     */
    void setConfirmInfo(FinPurchaseInvoice entity);

    /**
     * 采购发票确认
     */
    int confirm(FinPurchaseInvoice finPurchaseInvoice);

    /**
     * 采购发票变更
     */
    int change(FinPurchaseInvoice finPurchaseInvoice);

    /**
     * 删除子表，回退明细流水，删除附件表
     *
     *  handleStatus：针对保存/确认两种不同状态去分别释放流水来源的核销中/已核销
     *
     * @author chenkw
     */
    void deleteItem(Long finPurchaseInvoiceSid, String handleStatus);

    /**
     * 新增子表，修改明细流水，新增附件表
     *
     * handleStatus：如果是红冲操作，就不对流失来源进行处理
     *
     * @author chenkw
     */
    void insertChild(List<FinPurchaseInvoiceItem> itemList, List<FinPurchaseInvoiceDiscount> discountList, List<FinPurchaseInvoiceAttachment> atmList, Long sid, String handleStatus);

    /**
     * 生成应付流水，修改明细流水信息
     *
     * handleStatus：如果是红冲操作，就不对流失来源进行处理
     *
     * @author chenkw
     */
    void insertBook(FinPurchaseInvoice finPurchaseInvoice, String handleStatus);

    /**
     * 更新折扣流水
     * @author chenkw
     */
    void updateBook(FinPurchaseInvoice finPurchaseInvoice);

    /**
     * 查询财务流水转为折扣信息
     * @author chenkw
     */
    List<FinPurchaseInvoiceDiscount> bookList(FinPurchaseInvoiceDiscount request);

    /**
     * 作废
     * @author chenkw
     */
    int invalidInvoice(Long purchaseInvoiceSid);

    /**
     * 红冲
     * @author chenkw
     */
    int redDashed(Long purchaseInvoiceSid);

    /**
     * 纸质发票签收
     * @author chenkw
     */
    int changeSignFlag(List<Long> purchaseInvoiceSids);

}
