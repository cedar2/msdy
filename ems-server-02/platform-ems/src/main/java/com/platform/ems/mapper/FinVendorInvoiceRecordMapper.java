package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinVendorInvoiceRecord;

/**
 * 供应商发票台账表Mapper接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface FinVendorInvoiceRecordMapper extends BaseMapper<FinVendorInvoiceRecord> {

    /**
     * 查询详情
     *
     * @param vendorInvoiceRecordSid 单据sid
     * @return FinVendorInvoiceRecord
     */
    FinVendorInvoiceRecord selectFinVendorInvoiceRecordById(Long vendorInvoiceRecordSid);

    /**
     * 查询列表
     *
     * @param finVendorInvoiceRecord FinVendorInvoiceRecord
     * @return List
     */
    List<FinVendorInvoiceRecord> selectFinVendorInvoiceRecordList(FinVendorInvoiceRecord finVendorInvoiceRecord);

    /**
     * 添加多个
     *
     * @param list List FinVendorInvoiceRecord
     * @return int
     */
    int inserts(@Param("list") List<FinVendorInvoiceRecord> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinVendorInvoiceRecord
     * @return int
     */
    int updateAllById(FinVendorInvoiceRecord entity);

    /**
     * 更新多个
     *
     * @param list List FinVendorInvoiceRecord
     * @return int
     */
    int updatesAllById(@Param("list") List<FinVendorInvoiceRecord> list);

}
