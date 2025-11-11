package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinBookVendorAccountBalance;

/**
 * 财务流水账-供应商账互抵Mapper接口
 *
 * @author qhq
 * @date 2021-06-18
 */
public interface FinBookVendorAccountBalanceMapper  extends BaseMapper<FinBookVendorAccountBalance> {


    FinBookVendorAccountBalance selectFinBookVendorAccountBalanceById(Long bookAccountBalanceSid);

    List<FinBookVendorAccountBalance> selectFinBookVendorAccountBalanceList(FinBookVendorAccountBalance finBookVendorAccountBalance);

    /**
     * 添加多个
     * @param list List FinBookVendorAccountBalance
     * @return int
     */
    int inserts(@Param("list") List<FinBookVendorAccountBalance> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookVendorAccountBalance
    * @return int
    */
    int updateAllById(FinBookVendorAccountBalance entity);

    /**
     * 更新多个
     * @param list List FinBookVendorAccountBalance
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookVendorAccountBalance> list);

    /**
     * 查流水
     * @param entity
     * @return
     */
    List<FinBookVendorAccountBalance> getReportForm(FinBookVendorAccountBalance entity);
}
