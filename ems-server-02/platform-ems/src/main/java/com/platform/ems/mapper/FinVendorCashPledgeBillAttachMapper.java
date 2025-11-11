package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinVendorCashPledgeBillAttach;

/**
 * 供应商押金-附件Mapper接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface FinVendorCashPledgeBillAttachMapper extends BaseMapper<FinVendorCashPledgeBillAttach> {


    FinVendorCashPledgeBillAttach selectFinVendorCashPledgeBillAttachById(Long cashPledgeBillAttachmentSid);

    List<FinVendorCashPledgeBillAttach> selectFinVendorCashPledgeBillAttachList(FinVendorCashPledgeBillAttach finVendorCashPledgeBillAttach);

    /**
     * 添加多个
     *
     * @param list List FinVendorCashPledgeBillAttach
     * @return int
     */
    int inserts(@Param("list") List<FinVendorCashPledgeBillAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinVendorCashPledgeBillAttach
     * @return int
     */
    int updateAllById(FinVendorCashPledgeBillAttach entity);

    /**
     * 更新多个
     *
     * @param list List FinVendorCashPledgeBillAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FinVendorCashPledgeBillAttach> list);


}
