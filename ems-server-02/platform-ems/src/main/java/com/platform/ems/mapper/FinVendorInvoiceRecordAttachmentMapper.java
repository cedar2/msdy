package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinVendorInvoiceRecordAttachment;

/**
 * 供应商发票台账-附件表Mapper接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface FinVendorInvoiceRecordAttachmentMapper extends BaseMapper<FinVendorInvoiceRecordAttachment> {

    /**
     * 查询详情
     *
     * @param vendorInvoiceRecordAttachmentSid 单据sid
     * @return FinVendorInvoiceRecordAttachment
     */
    FinVendorInvoiceRecordAttachment selectFinVendorInvoiceRecordAttachmentById(Long vendorInvoiceRecordAttachmentSid);

    /**
     * 查询列表
     *
     * @param finVendorInvoiceRecordAttachment FinVendorInvoiceRecordAttachment
     * @return List
     */
    List<FinVendorInvoiceRecordAttachment> selectFinVendorInvoiceRecordAttachmentList(FinVendorInvoiceRecordAttachment finVendorInvoiceRecordAttachment);

    /**
     * 添加多个
     *
     * @param list List FinVendorInvoiceRecordAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinVendorInvoiceRecordAttachment> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinVendorInvoiceRecordAttachment
     * @return int
     */
    int updateAllById(FinVendorInvoiceRecordAttachment entity);

    /**
     * 更新多个
     *
     * @param list List FinVendorInvoiceRecordAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinVendorInvoiceRecordAttachment> list);

}
