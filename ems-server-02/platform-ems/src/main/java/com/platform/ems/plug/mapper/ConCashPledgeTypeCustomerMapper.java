package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConCashPledgeTypeCustomer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 押金类型_客户Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-25
 */
public interface ConCashPledgeTypeCustomerMapper extends BaseMapper<ConCashPledgeTypeCustomer> {


    ConCashPledgeTypeCustomer selectConCashPledgeTypeCustomerById(Long sid);

    List<ConCashPledgeTypeCustomer> selectConCashPledgeTypeCustomerList(ConCashPledgeTypeCustomer conCashPledgeTypeCustomer);

    /**
     * 添加多个
     *
     * @param list List ConCashPledgeTypeCustomer
     * @return int
     */
    int inserts(@Param("list") List<ConCashPledgeTypeCustomer> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConCashPledgeTypeCustomer
     * @return int
     */
    int updateAllById(ConCashPledgeTypeCustomer entity);

    /**
     * 更新多个
     *
     * @param list List ConCashPledgeTypeCustomer
     * @return int
     */
    int updatesAllById(@Param("list") List<ConCashPledgeTypeCustomer> list);

    /**
     * 押金类型_客户下拉框列表
     */
    List<ConCashPledgeTypeCustomer> getList(ConCashPledgeTypeCustomer conCashPledgeTypeCustomer);
}
