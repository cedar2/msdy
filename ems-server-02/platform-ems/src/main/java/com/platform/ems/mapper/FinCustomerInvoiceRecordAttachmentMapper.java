package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinCustomerInvoiceRecordAttachment;

/**
 * 客户发票台账-附件表Mapper接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface FinCustomerInvoiceRecordAttachmentMapper extends BaseMapper<FinCustomerInvoiceRecordAttachment> {

    /**
     * 查询详情
     *
     * @param customerInvoiceRecordAttachmentSid 单据sid
     * @return FinCustomerInvoiceRecordAttachment
     */
    FinCustomerInvoiceRecordAttachment selectFinCustomerInvoiceRecordAttachmentById(Long customerInvoiceRecordAttachmentSid);

    /**
     * 查询列表
     *
     * @param finCustomerInvoiceRecordAttachment FinCustomerInvoiceRecordAttachment
     * @return List
     */
    List<FinCustomerInvoiceRecordAttachment> selectFinCustomerInvoiceRecordAttachmentList(FinCustomerInvoiceRecordAttachment finCustomerInvoiceRecordAttachment);

    /**
     * 添加多个
     *
     * @param list List FinCustomerInvoiceRecordAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerInvoiceRecordAttachment> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinCustomerInvoiceRecordAttachment
     * @return int
     */
    int updateAllById(FinCustomerInvoiceRecordAttachment entity);

    /**
     * 更新多个
     *
     * @param list List FinCustomerInvoiceRecordAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinCustomerInvoiceRecordAttachment> list);

}
