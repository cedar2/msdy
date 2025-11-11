package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinReceivableBillPayMethod;

/**
 * 收款单-支付方式明细Mapper接口
 *
 * @author chenkw
 * @date 2022-06-23
 */
public interface FinReceivableBillPayMethodMapper extends BaseMapper<FinReceivableBillPayMethod> {


    FinReceivableBillPayMethod selectFinReceivableBillPayMethodById(Long receivableBillPayMethodSid);

    List<FinReceivableBillPayMethod> selectFinReceivableBillPayMethodList(FinReceivableBillPayMethod finReceivableBillPayMethod);

    /**
     * 添加多个
     *
     * @param list List FinReceivableBillPayMethod
     * @return int
     */
    int inserts(@Param("list") List<FinReceivableBillPayMethod> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinReceivableBillPayMethod
     * @return int
     */
    int updateAllById(FinReceivableBillPayMethod entity);

    /**
     * 更新多个
     *
     * @param list List FinReceivableBillPayMethod
     * @return int
     */
    int updatesAllById(@Param("list") List<FinReceivableBillPayMethod> list);


}
