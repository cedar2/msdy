package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinVendorFundsFreezeBill;

/**
 * 供应商暂押款Mapper接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface FinVendorFundsFreezeBillMapper extends BaseMapper<FinVendorFundsFreezeBill> {

    FinVendorFundsFreezeBill selectFinVendorFundsFreezeBillById(Long fundsFreezeBillSid);

    List<FinVendorFundsFreezeBill> selectFinVendorFundsFreezeBillList(FinVendorFundsFreezeBill finVendorFundsFreezeBill);

    /**
     * 添加多个
     *
     * @param list List FinVendorFundsFreezeBill
     * @return int
     */
    int inserts(@Param("list") List<FinVendorFundsFreezeBill> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinVendorFundsFreezeBill
     * @return int
     */
    int updateAllById(FinVendorFundsFreezeBill entity);

}
