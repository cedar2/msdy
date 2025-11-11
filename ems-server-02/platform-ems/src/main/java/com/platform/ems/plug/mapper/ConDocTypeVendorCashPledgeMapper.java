package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConDocTypeVendorCashPledge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 单据类型_供应商押金Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-25
 */
public interface ConDocTypeVendorCashPledgeMapper extends BaseMapper<ConDocTypeVendorCashPledge> {


    ConDocTypeVendorCashPledge selectConDocTypeVendorCashPledgeById(Long sid);

    List<ConDocTypeVendorCashPledge> selectConDocTypeVendorCashPledgeList(ConDocTypeVendorCashPledge conDocTypeVendorCashPledge);

    /**
     * 添加多个
     *
     * @param list List ConDocTypeVendorCashPledge
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeVendorCashPledge> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConDocTypeVendorCashPledge
     * @return int
     */
    int updateAllById(ConDocTypeVendorCashPledge entity);

    /**
     * 更新多个
     *
     * @param list List ConDocTypeVendorCashPledge
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeVendorCashPledge> list);

    /**
     * 单据类型_供应商押金下拉框列表
     */
    List<ConDocTypeVendorCashPledge> getList(ConDocTypeVendorCashPledge conDocTypeVendorCashPledge);
}
