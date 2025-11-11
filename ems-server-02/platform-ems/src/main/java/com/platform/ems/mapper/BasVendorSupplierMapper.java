package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorSupplier;

/**
 * 供应商的主要供应商Mapper接口
 *
 * @author chenkw
 * @date 2022-01-05
 */
public interface BasVendorSupplierMapper extends BaseMapper<BasVendorSupplier> {


    BasVendorSupplier selectBasVendorSupplierById(Long vendorSupplierSid);

    List<BasVendorSupplier> selectBasVendorSupplierList(BasVendorSupplier basVendorSupplier);

    /**
     * 添加多个
     *
     * @param list List BasVendorSupplier
     * @return int
     */
    int inserts(@Param("list") List<BasVendorSupplier> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasVendorSupplier
     * @return int
     */
    int updateAllById(BasVendorSupplier entity);

    /**
     * 更新多个
     *
     * @param list List BasVendorSupplier
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorSupplier> list);


}
