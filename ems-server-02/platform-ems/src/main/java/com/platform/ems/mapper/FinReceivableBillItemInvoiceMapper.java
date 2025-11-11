package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinReceivableBillItemInvoice;

/**
 * 收款单-发票台账明细表Mapper接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface FinReceivableBillItemInvoiceMapper extends BaseMapper<FinReceivableBillItemInvoice> {

    /**
     * 查询详情
     *
     * @param receivableBillItemInvoiceSid 单据sid
     * @return FinReceivableBillItemInvoice
     */
    FinReceivableBillItemInvoice selectFinReceivableBillItemInvoiceById(Long receivableBillItemInvoiceSid);

    /**
     * 查询列表
     *
     * @param finReceivableBillItemInvoice FinReceivableBillItemInvoice
     * @return List
     */
    List<FinReceivableBillItemInvoice> selectFinReceivableBillItemInvoiceList(FinReceivableBillItemInvoice finReceivableBillItemInvoice);

    /**
     * 添加多个
     *
     * @param list List FinReceivableBillItemInvoice
     * @return int
     */
    int inserts(@Param("list") List<FinReceivableBillItemInvoice> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinReceivableBillItemInvoice
     * @return int
     */
    int updateAllById(FinReceivableBillItemInvoice entity);

    /**
     * 更新多个
     *
     * @param list List FinReceivableBillItemInvoice
     * @return int
     */
    int updatesAllById(@Param("list") List<FinReceivableBillItemInvoice> list);

}
