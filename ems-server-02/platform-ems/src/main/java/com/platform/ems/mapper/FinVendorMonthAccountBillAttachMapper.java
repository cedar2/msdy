package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinVendorMonthAccountBillAttach;

/**
 * 供应商月对账单-附件Mapper接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface FinVendorMonthAccountBillAttachMapper extends BaseMapper<FinVendorMonthAccountBillAttach> {


    FinVendorMonthAccountBillAttach selectFinVendorMonthAccountBillAttachById(Long monthAccountBillAttachmentSid);

    List<FinVendorMonthAccountBillAttach> selectFinVendorMonthAccountBillAttachList(FinVendorMonthAccountBillAttach finVendorMonthAccountBillAttach);

    /**
     * 添加多个
     *
     * @param list List FinVendorMonthAccountBillAttach
     * @return int
     */
    int inserts(@Param("list") List<FinVendorMonthAccountBillAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinVendorMonthAccountBillAttach
     * @return int
     */
    int updateAllById(FinVendorMonthAccountBillAttach entity);

    /**
     * 更新多个
     *
     * @param list List FinVendorMonthAccountBillAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FinVendorMonthAccountBillAttach> list);


}
