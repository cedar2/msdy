package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinPayBillItemInvoice;

/**
 * 付款单-发票台账明细表Mapper接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface FinPayBillItemInvoiceMapper  extends BaseMapper<FinPayBillItemInvoice> {

    /**
     * 查询详情
     * @param payBillItemInvoiceSid 单据sid
     * @return FinPayBillItemInvoice
     */
    FinPayBillItemInvoice selectFinPayBillItemInvoiceById(Long payBillItemInvoiceSid);

    /**
     * 查询列表
     * @param finPayBillItemInvoice FinPayBillItemInvoice
     * @return List
     */
    List<FinPayBillItemInvoice> selectFinPayBillItemInvoiceList(FinPayBillItemInvoice finPayBillItemInvoice);

    /**
     * 添加多个
     * @param list List FinPayBillItemInvoice
     * @return int
     */
    int inserts(@Param("list") List<FinPayBillItemInvoice> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity FinPayBillItemInvoice
     * @return int
     */
    int updateAllById(FinPayBillItemInvoice entity);

    /**
     * 更新多个
     * @param list List FinPayBillItemInvoice
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPayBillItemInvoice> list);

}
