package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SalSaleContractPayMethod;

/**
 * 销售合同信息-支付方式Mapper接口
 *
 * @author chenkw
 * @date 2022-05-17
 */
public interface SalSaleContractPayMethodMapper extends BaseMapper<SalSaleContractPayMethod> {

    SalSaleContractPayMethod selectSalSaleContractPayMethodById(Long contractPayMethodSid);

    List<SalSaleContractPayMethod> selectSalSaleContractPayMethodList(SalSaleContractPayMethod salSaleContractPayMethod);

    /**
     * 添加多个
     *
     * @param list List SalSaleContractPayMethod
     * @return int
     */
    int inserts(@Param("list") List<SalSaleContractPayMethod> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SalSaleContractPayMethod
     * @return int
     */
    int updateAllById(SalSaleContractPayMethod entity);

    /**
     * 更新多个
     *
     * @param list List SalSaleContractPayMethod
     * @return int
     */
    int updatesAllById(@Param("list") List<SalSaleContractPayMethod> list);


}
