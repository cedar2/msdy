package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConBuTypeCustomerCashPledge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 业务类型_客户押金Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-27
 */
public interface ConBuTypeCustomerCashPledgeMapper extends BaseMapper<ConBuTypeCustomerCashPledge> {


    ConBuTypeCustomerCashPledge selectConBuTypeCustomerCashPledgeById(Long sid);

    List<ConBuTypeCustomerCashPledge> selectConBuTypeCustomerCashPledgeList(ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge);

    /**
     * 添加多个
     *
     * @param list List ConBuTypeCustomerCashPledge
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeCustomerCashPledge> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConBuTypeCustomerCashPledge
     * @return int
     */
    int updateAllById(ConBuTypeCustomerCashPledge entity);

    /**
     * 更新多个
     *
     * @param list List ConBuTypeCustomerCashPledge
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeCustomerCashPledge> list);

    /**
     * 业务类型_客户押金下拉框列表
     */
    List<ConBuTypeCustomerCashPledge> getList(ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge);
}
