package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConBuTypeVendorCashPledge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 业务类型_供应商押金Mapper接口
 *
 * @author c
 * @date 2021-09-27
 */
public interface ConBuTypeVendorCashPledgeMapper extends BaseMapper<ConBuTypeVendorCashPledge> {


    ConBuTypeVendorCashPledge selectConBuTypeVendorCashPledgeById(Long sid);

    List<ConBuTypeVendorCashPledge> selectConBuTypeVendorCashPledgeList(ConBuTypeVendorCashPledge conBuTypeVendorCashPledge);

    /**
     * 添加多个
     *
     * @param list List ConBuTypeVendorCashPledge
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeVendorCashPledge> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConBuTypeVendorCashPledge
     * @return int
     */
    int updateAllById(ConBuTypeVendorCashPledge entity);

    /**
     * 更新多个
     *
     * @param list List ConBuTypeVendorCashPledge
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeVendorCashPledge> list);

    /**
     * 业务类型_供应商押金下拉框列表
     */
    List<ConBuTypeVendorCashPledge> getList(ConBuTypeVendorCashPledge conBuTypeVendorCashPledge);
}
