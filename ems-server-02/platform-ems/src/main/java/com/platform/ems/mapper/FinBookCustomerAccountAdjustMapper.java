package com.platform.ems.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinBookCustomerAccountAdjust;

/**
 * 财务流水账-客户调账Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-08
 */
public interface FinBookCustomerAccountAdjustMapper extends BaseMapper<FinBookCustomerAccountAdjust> {


    FinBookCustomerAccountAdjust selectFinBookCustomerAccountAdjustById(Long bookAccountAdjustSid);

    List<FinBookCustomerAccountAdjust> selectFinBookCustomerAccountAdjustList(FinBookCustomerAccountAdjust finBookCustomerAccountAdjust);

    /**
     * 添加多个
     *
     * @param list List FinBookCustomerAccountAdjust
     * @return int
     */
    int inserts(@Param("list") List<FinBookCustomerAccountAdjust> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinBookCustomerAccountAdjust
     * @return int
     */
    int updateAllById(FinBookCustomerAccountAdjust entity);

    /**
     * 更新多个
     *
     * @param list List FinBookCustomerAccountAdjust
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookCustomerAccountAdjust> list);

    /**
     * 查报表
     *
     * @param entity
     * @return
     */
    List<FinBookCustomerAccountAdjust> getReportForm(FinBookCustomerAccountAdjust entity);

}
