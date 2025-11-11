package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinVendorInvoiceRecord;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 供应商发票台账表Service接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface IFinVendorInvoiceRecordService extends IService<FinVendorInvoiceRecord> {

    /**
     * 查询供应商发票台账表
     *
     * @param vendorInvoiceRecordSid 供应商发票台账表ID
     * @return 供应商发票台账表
     */
    public FinVendorInvoiceRecord selectFinVendorInvoiceRecordById(Long vendorInvoiceRecordSid);

    /**
     * 查询供应商发票台账表列表
     *
     * @param finVendorInvoiceRecord 供应商发票台账表
     * @return 供应商发票台账表集合
     */
    public List<FinVendorInvoiceRecord> selectFinVendorInvoiceRecordList(FinVendorInvoiceRecord finVendorInvoiceRecord);

    /**
     * 新增供应商发票台账表
     *
     * @param finVendorInvoiceRecord 供应商发票台账表
     * @return 结果
     */
    public int insertFinVendorInvoiceRecord(FinVendorInvoiceRecord finVendorInvoiceRecord);

    /**
     * 修改供应商发票台账表
     *
     * @param finVendorInvoiceRecord 供应商发票台账表
     * @return 结果
     */
    public int updateFinVendorInvoiceRecord(FinVendorInvoiceRecord finVendorInvoiceRecord);

    /**
     * 变更供应商发票台账表
     *
     * @param finVendorInvoiceRecord 供应商发票台账表
     * @return 结果
     */
    public int changeFinVendorInvoiceRecord(FinVendorInvoiceRecord finVendorInvoiceRecord);

    /**
     * 批量删除供应商发票台账表
     *
     * @param vendorInvoiceRecordSids 需要删除的供应商发票台账表ID
     * @return 结果
     */
    public int deleteFinVendorInvoiceRecordByIds(List<Long> vendorInvoiceRecordSids);

    /**
     * 更改确认状态 前校验
     *
     * @param finVendorInvoiceRecord 请求参数
     * @return
     */
    void checkJudge(FinVendorInvoiceRecord finVendorInvoiceRecord);

    /**
     * 更改确认状态
     *
     * @param finVendorInvoiceRecord 请求参数
     * @return
     */
    int check(FinVendorInvoiceRecord finVendorInvoiceRecord);

    /**
     * 更改发票签收状态
     */
    int updateSendFlag(FinVendorInvoiceRecord finVendorInvoiceRecord);

    /**
     * 更改对账账期
     */
    int updatePeriod(FinVendorInvoiceRecord finVendorInvoiceRecord);

    /**
     * 导入
     */
    EmsResultEntity importRecord(MultipartFile file);
}
