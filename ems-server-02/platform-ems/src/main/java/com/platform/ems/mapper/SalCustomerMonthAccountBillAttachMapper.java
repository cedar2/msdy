package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.SalCustomerMonthAccountBillAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供应商对账单-附件Mapper接口
 *
 */
public interface SalCustomerMonthAccountBillAttachMapper extends BaseMapper<SalCustomerMonthAccountBillAttach> {


    SalCustomerMonthAccountBillAttach selectSalCustomerMonthAccountBillAttachById(Long monthAccountBillAttachmentSid);

    List<SalCustomerMonthAccountBillAttach> selectSalCustomerMonthAccountBillAttachList(SalCustomerMonthAccountBillAttach salCustomerMonthAccountBillAttach);

    /**
     * 添加多个
     *
     * @param list List SalCustomerMonthAccountBillAttach
     * @return int
     */
    int inserts(@Param("list") List<SalCustomerMonthAccountBillAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SalCustomerMonthAccountBillAttach
     * @return int
     */
    int updateAllById(SalCustomerMonthAccountBillAttach entity);

    /**
     * 更新多个
     *
     * @param list List SalCustomerMonthAccountBillAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<SalCustomerMonthAccountBillAttach> list);


}
