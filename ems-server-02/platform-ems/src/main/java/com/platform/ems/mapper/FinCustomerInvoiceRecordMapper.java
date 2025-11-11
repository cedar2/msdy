package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinCustomerInvoiceRecord;

/**
 * 客户发票台账表Mapper接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface FinCustomerInvoiceRecordMapper extends BaseMapper<FinCustomerInvoiceRecord> {

    /**
     * 查询详情
     *
     * @param customerInvoiceRecordSid 单据sid
     * @return FinCustomerInvoiceRecord
     */
    FinCustomerInvoiceRecord selectFinCustomerInvoiceRecordById(Long customerInvoiceRecordSid);

    /**
     * 查询列表
     *
     * @param finCustomerInvoiceRecord FinCustomerInvoiceRecord
     * @return List
     */
    List<FinCustomerInvoiceRecord> selectFinCustomerInvoiceRecordList(FinCustomerInvoiceRecord finCustomerInvoiceRecord);

    /**
     * 添加多个
     *
     * @param list List FinCustomerInvoiceRecord
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerInvoiceRecord> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinCustomerInvoiceRecord
     * @return int
     */
    int updateAllById(FinCustomerInvoiceRecord entity);

    /**
     * 更新多个
     *
     * @param list List FinCustomerInvoiceRecord
     * @return int
     */
    int updatesAllById(@Param("list") List<FinCustomerInvoiceRecord> list);

}
