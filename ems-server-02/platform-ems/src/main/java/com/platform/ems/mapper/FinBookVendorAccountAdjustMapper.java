package com.platform.ems.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinBookVendorAccountAdjust;

/**
 * 财务流水账-供应商调账Mapper接口
 *
 * @author qhq
 * @date 2021-06-02
 */
public interface FinBookVendorAccountAdjustMapper extends BaseMapper<FinBookVendorAccountAdjust> {


    FinBookVendorAccountAdjust selectFinBookVendorAccountAdjustById(Long bookAccountAdjustSid);

    List<FinBookVendorAccountAdjust> selectFinBookVendorAccountAdjustList(FinBookVendorAccountAdjust finBookVendorAccountAdjust);

    /**
     * 添加多个
     *
     * @param list List FinBookVendorAccountAdjust
     * @return int
     */
    int inserts(@Param("list") List<FinBookVendorAccountAdjust> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinBookVendorAccountAdjust
     * @return int
     */
    int updateAllById(FinBookVendorAccountAdjust entity);

    /**
     * 更新多个
     *
     * @param list List FinBookVendorAccountAdjust
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookVendorAccountAdjust> list);

    /**
     * 查报表
     *
     * @param entity
     * @return
     */
    List<FinBookVendorAccountAdjust> getReportForm(FinBookVendorAccountAdjust entity);
}
