package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorCustomer;

/**
 * 供应商的主要客户信息Mapper接口
 *
 * @author chenkw
 * @date 2022-01-05
 */
public interface BasVendorCustomerMapper extends BaseMapper<BasVendorCustomer> {


    BasVendorCustomer selectBasVendorCustomerById(Long vendorCustomerSid);

    List<BasVendorCustomer> selectBasVendorCustomerList(BasVendorCustomer basVendorCustomer);

    /**
     * 添加多个
     *
     * @param list List BasVendorCustomer
     * @return int
     */
    int inserts(@Param("list") List<BasVendorCustomer> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasVendorCustomer
     * @return int
     */
    int updateAllById(BasVendorCustomer entity);

    /**
     * 更新多个
     *
     * @param list List BasVendorCustomer
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorCustomer> list);


}
