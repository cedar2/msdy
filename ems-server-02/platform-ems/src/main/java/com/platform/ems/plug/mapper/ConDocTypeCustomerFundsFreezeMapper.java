package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConDocTypeCustomerFundsFreeze;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 单据类型_客户暂押款Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-25
 */
public interface ConDocTypeCustomerFundsFreezeMapper extends BaseMapper<ConDocTypeCustomerFundsFreeze> {


    ConDocTypeCustomerFundsFreeze selectConDocTypeCustomerFundsFreezeById(Long sid);

    List<ConDocTypeCustomerFundsFreeze> selectConDocTypeCustomerFundsFreezeList(ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze);

    /**
     * 添加多个
     *
     * @param list List ConDocTypeCustomerFundsFreeze
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeCustomerFundsFreeze> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConDocTypeCustomerFundsFreeze
     * @return int
     */
    int updateAllById(ConDocTypeCustomerFundsFreeze entity);

    /**
     * 更新多个
     *
     * @param list List ConDocTypeCustomerFundsFreeze
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeCustomerFundsFreeze> list);

    /**
     * 单据类型_客户暂押款下拉框列表
     */
    List<ConDocTypeCustomerFundsFreeze> getList(ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze);
}
