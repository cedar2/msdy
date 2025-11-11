package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConFundsFreezeTypeCustomer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 暂押款类型_客户Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-25
 */
public interface ConFundsFreezeTypeCustomerMapper extends BaseMapper<ConFundsFreezeTypeCustomer> {


    ConFundsFreezeTypeCustomer selectConFundsFreezeTypeCustomerById(Long sid);

    List<ConFundsFreezeTypeCustomer> selectConFundsFreezeTypeCustomerList(ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer);

    /**
     * 添加多个
     *
     * @param list List ConFundsFreezeTypeCustomer
     * @return int
     */
    int inserts(@Param("list") List<ConFundsFreezeTypeCustomer> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConFundsFreezeTypeCustomer
     * @return int
     */
    int updateAllById(ConFundsFreezeTypeCustomer entity);

    /**
     * 更新多个
     *
     * @param list List ConFundsFreezeTypeCustomer
     * @return int
     */
    int updatesAllById(@Param("list") List<ConFundsFreezeTypeCustomer> list);

    /**
     * 暂押款类型_客户下拉框列表
     */
    List<ConFundsFreezeTypeCustomer> getList(ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer);
}
