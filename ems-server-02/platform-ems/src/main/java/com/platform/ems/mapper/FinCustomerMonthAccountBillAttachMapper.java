package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinCustomerMonthAccountBillAttach;

/**
 * 客户月对账单-附件Mapper接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface FinCustomerMonthAccountBillAttachMapper extends BaseMapper<FinCustomerMonthAccountBillAttach> {


    FinCustomerMonthAccountBillAttach selectFinCustomerMonthAccountBillAttachById(Long monthAccountBillAttachmentSid);

    List<FinCustomerMonthAccountBillAttach> selectFinCustomerMonthAccountBillAttachList(FinCustomerMonthAccountBillAttach finCustomerMonthAccountBillAttach);

    /**
     * 添加多个
     *
     * @param list List FinCustomerMonthAccountBillAttach
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerMonthAccountBillAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinCustomerMonthAccountBillAttach
     * @return int
     */
    int updateAllById(FinCustomerMonthAccountBillAttach entity);

    /**
     * 更新多个
     *
     * @param list List FinCustomerMonthAccountBillAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FinCustomerMonthAccountBillAttach> list);


}
