package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.*;

/**
 * 销售发票Service接口
 *
 * @author linhongwei
 * @date 2021-04-20
 */
public interface IFinSaleInvoiceService extends IService<FinSaleInvoice> {
    /**
     * 查询销售发票
     *
     * @param saleInvoiceSid 销售发票ID
     * @return 销售发票
     */
    public FinSaleInvoice selectFinSaleInvoiceById(Long saleInvoiceSid);

    /**
     * 查询销售发票列表
     *
     * @param finSaleInvoice 销售发票
     * @return 销售发票集合
     */
    public List<FinSaleInvoice> selectFinSaleInvoiceList(FinSaleInvoice finSaleInvoice);

    /**
     * 新增销售发票
     *
     * @param finSaleInvoice 销售发票
     * @return 结果
     */
    public int insertFinSaleInvoice(FinSaleInvoice finSaleInvoice);

    /**
     * 修改销售发票
     *
     * @param finSaleInvoice 销售发票
     * @return 结果
     */
    public int updateFinSaleInvoice(FinSaleInvoice finSaleInvoice);

    /**
     * 批量删除销售发票
     *
     * @param saleInvoiceSids 需要删除的销售发票ID
     * @return 结果
     */
    public int deleteFinSaleInvoiceByIds(List<Long> saleInvoiceSids);


    /**
     * 提交前检验
     *
     * @param entity 发票详细信息
     * @return 结果
     */
    void setConfirmInfo(FinSaleInvoice entity);


    /**
     * 销售发票确认
     */
    int confirm(FinSaleInvoice finSaleInvoice);

    /**
     * 销售发票变更
     */
    int change(FinSaleInvoice finSaleInvoice);

    /**
     * 删除子表，回退明细流水，删除附件表
     *
     * handleStatus：针对保存/确认两种不同状态去分别释放流水来源的核销中/已核销
     *
     * @author chenkw
     */
    void deleteItem(Long finSaleInvoiceSid, String handleStatus);

    /**
     * 新增子表，修改明细流水，新增附件表
     *
     * handleStatus：如果是红冲操作，就不对流失来源进行处理
     *
     * @author chenkw
     */
    void insertChild(List<FinSaleInvoiceItem> itemList, List<FinSaleInvoiceDiscount> discountList, List<FinSaleInvoiceAttachment> atmList, Long sid, String handleStatus);

    /**
     * 生成应收流水，修改明细流水信息
     *
     * handleStatus：如果是红冲操作，就不对流失来源进行处理
     *
     * @author chenkw
     */
    void insertBook(FinSaleInvoice finSaleInvoice, String handleStatus);

    /**
     * 更新折扣流水
     * @author chenkw
     */
    void updateBook(FinSaleInvoice finSaleInvoice);

    /**
     * 查询财务流水转为折扣信息
     * @author chenkw
     */
    List<FinSaleInvoiceDiscount> bookList(FinSaleInvoiceDiscount request);

    /**
     * 作废
     * @author chenkw
     */
    int invalidInvoice(Long saleInvoiceSid);

    /**
     * 红冲
     * @author chenkw
     */
    int redDashed(Long saleInvoiceSid);

    /**
     * 纸质发票签收
     * @author chenkw
     */
    int changeSignFlag(List<Long> saleInvoiceSids);
}
