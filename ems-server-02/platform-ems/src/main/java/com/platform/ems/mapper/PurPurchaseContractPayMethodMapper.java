package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurPurchaseContractPayMethod;

/**
 * 采购合同信息-支付方式Mapper接口
 *
 * @author chenkw
 * @date 2022-05-17
 */
public interface PurPurchaseContractPayMethodMapper extends BaseMapper<PurPurchaseContractPayMethod> {

    PurPurchaseContractPayMethod selectPurPurchaseContractPayMethodById(Long contractPayMethodSid);

    List<PurPurchaseContractPayMethod> selectPurPurchaseContractPayMethodList(PurPurchaseContractPayMethod purPurchaseContractPayMethod);

    /**
     * 添加多个
     *
     * @param list List PurPurchaseContractPayMethod
     * @return int
     */
    int inserts(@Param("list") List<PurPurchaseContractPayMethod> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PurPurchaseContractPayMethod
     * @return int
     */
    int updateAllById(PurPurchaseContractPayMethod entity);

    /**
     * 更新多个
     *
     * @param list List PurPurchaseContractPayMethod
     * @return int
     */
    int updatesAllById(@Param("list") List<PurPurchaseContractPayMethod> list);


}
