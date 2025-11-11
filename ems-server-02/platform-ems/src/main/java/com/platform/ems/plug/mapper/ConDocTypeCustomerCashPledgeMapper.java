package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConDocTypeCustomerCashPledge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 单据类型_客户押金Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-25
 */
public interface ConDocTypeCustomerCashPledgeMapper extends BaseMapper<ConDocTypeCustomerCashPledge> {


    ConDocTypeCustomerCashPledge selectConDocTypeCustomerCashPledgeById(Long sid);

    List<ConDocTypeCustomerCashPledge> selectConDocTypeCustomerCashPledgeList(ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge);

    /**
     * 添加多个
     *
     * @param list List ConDocTypeCustomerCashPledge
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeCustomerCashPledge> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConDocTypeCustomerCashPledge
     * @return int
     */
    int updateAllById(ConDocTypeCustomerCashPledge entity);

    /**
     * 更新多个
     *
     * @param list List ConDocTypeCustomerCashPledge
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeCustomerCashPledge> list);

    /**
     * 单据类型_客户押金下拉框列表
     */
    List<ConDocTypeCustomerCashPledge> getList(ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge);
}
