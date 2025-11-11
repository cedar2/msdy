package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinPayBillPayMethod;

/**
 * 付款单-支付方式明细Mapper接口
 *
 * @author chenkw
 * @date 2022-06-23
 */
public interface FinPayBillPayMethodMapper extends BaseMapper<FinPayBillPayMethod> {

    FinPayBillPayMethod selectFinPayBillPayMethodById(Long payBillPayMethodSid);

    List<FinPayBillPayMethod> selectFinPayBillPayMethodList(FinPayBillPayMethod finPayBillPayMethod);

    /**
     * 添加多个
     *
     * @param list List FinPayBillPayMethod
     * @return int
     */
    int inserts(@Param("list") List<FinPayBillPayMethod> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinPayBillPayMethod
     * @return int
     */
    int updateAllById(FinPayBillPayMethod entity);

    /**
     * 更新多个
     *
     * @param list List FinPayBillPayMethod
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPayBillPayMethod> list);

}
