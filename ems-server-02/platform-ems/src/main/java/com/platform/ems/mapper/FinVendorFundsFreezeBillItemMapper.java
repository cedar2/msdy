package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinVendorFundsFreezeBillItem;

/**
 * 供应商暂押款-明细Mapper接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface FinVendorFundsFreezeBillItemMapper extends BaseMapper<FinVendorFundsFreezeBillItem> {


    FinVendorFundsFreezeBillItem selectFinVendorFundsFreezeBillItemById(Long fundsFreezeBillItemSid);

    List<FinVendorFundsFreezeBillItem> selectFinVendorFundsFreezeBillItemList(FinVendorFundsFreezeBillItem finVendorFundsFreezeBillItem);

    /**
     * 添加多个
     *
     * @param list List FinVendorFundsFreezeBillItem
     * @return int
     */
    int inserts(@Param("list") List<FinVendorFundsFreezeBillItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinVendorFundsFreezeBillItem
     * @return int
     */
    int updateAllById(FinVendorFundsFreezeBillItem entity);

    /**
     * 更新多个
     *
     * @param list List FinVendorFundsFreezeBillItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinVendorFundsFreezeBillItem> list);


}
