package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinCustomerInvoiceRecord;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 客户发票台账表Service接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface IFinCustomerInvoiceRecordService extends IService<FinCustomerInvoiceRecord> {

    /**
     * 查询客户发票台账表
     *
     * @param customerInvoiceRecordSid 客户发票台账表ID
     * @return 客户发票台账表
     */
    public FinCustomerInvoiceRecord selectFinCustomerInvoiceRecordById(Long customerInvoiceRecordSid);

    /**
     * 查询客户发票台账表列表
     *
     * @param finCustomerInvoiceRecord 客户发票台账表
     * @return 客户发票台账表集合
     */
    public List<FinCustomerInvoiceRecord> selectFinCustomerInvoiceRecordList(FinCustomerInvoiceRecord finCustomerInvoiceRecord);

    /**
     * 新增客户发票台账表
     *
     * @param finCustomerInvoiceRecord 客户发票台账表
     * @return 结果
     */
    public int insertFinCustomerInvoiceRecord(FinCustomerInvoiceRecord finCustomerInvoiceRecord);

    /**
     * 修改客户发票台账表
     *
     * @param finCustomerInvoiceRecord 客户发票台账表
     * @return 结果
     */
    public int updateFinCustomerInvoiceRecord(FinCustomerInvoiceRecord finCustomerInvoiceRecord);

    /**
     * 变更客户发票台账表
     *
     * @param finCustomerInvoiceRecord 客户发票台账表
     * @return 结果
     */
    public int changeFinCustomerInvoiceRecord(FinCustomerInvoiceRecord finCustomerInvoiceRecord);

    /**
     * 批量删除客户发票台账表
     *
     * @param customerInvoiceRecordSids 需要删除的客户发票台账表ID
     * @return 结果
     */
    public int deleteFinCustomerInvoiceRecordByIds(List<Long> customerInvoiceRecordSids);

    /**
     * 更改确认状态 前校验
     *
     * @param finCustomerInvoiceRecord 请求参数
     * @return
     */
    void checkJudge(FinCustomerInvoiceRecord finCustomerInvoiceRecord);

    /**
     * 更改确认状态
     *
     * @param finCustomerInvoiceRecord 请求参数
     * @return
     */
    int check(FinCustomerInvoiceRecord finCustomerInvoiceRecord);

    /**
     * 更改发票寄出状态
     */
    int updateSendFlag(FinCustomerInvoiceRecord finCustomerInvoiceRecord);

    /**
     * 更改对账账期
     */
    int updatePeriod(FinCustomerInvoiceRecord finCustomerInvoiceRecord);

    /**
     * 导入
     */
    EmsResultEntity importRecord(MultipartFile file);
}
