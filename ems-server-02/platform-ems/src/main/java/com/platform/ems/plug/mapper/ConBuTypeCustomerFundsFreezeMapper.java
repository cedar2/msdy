package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConBuTypeCustomerFundsFreeze;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 业务类型_客户暂押款Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-27
 */
public interface ConBuTypeCustomerFundsFreezeMapper extends BaseMapper<ConBuTypeCustomerFundsFreeze> {


    ConBuTypeCustomerFundsFreeze selectConBuTypeCustomerFundsFreezeById(Long sid);

    List<ConBuTypeCustomerFundsFreeze> selectConBuTypeCustomerFundsFreezeList(ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze);

    /**
     * 添加多个
     *
     * @param list List ConBuTypeCustomerFundsFreeze
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeCustomerFundsFreeze> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConBuTypeCustomerFundsFreeze
     * @return int
     */
    int updateAllById(ConBuTypeCustomerFundsFreeze entity);

    /**
     * 更新多个
     *
     * @param list List ConBuTypeCustomerFundsFreeze
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeCustomerFundsFreeze> list);

    /**
     * 业务类型_客户暂押款下拉框列表
     */
    List<ConBuTypeCustomerFundsFreeze> getList(ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze);
}
