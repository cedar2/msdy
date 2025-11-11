package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinVendorFundsFreezeBillAttach;

/**
 * 供应商暂押款-附件Mapper接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface FinVendorFundsFreezeBillAttachMapper extends BaseMapper<FinVendorFundsFreezeBillAttach> {


    FinVendorFundsFreezeBillAttach selectFinVendorFundsFreezeBillAttachById(Long fundsFreezeBillAttachmentSid);

    List<FinVendorFundsFreezeBillAttach> selectFinVendorFundsFreezeBillAttachList(FinVendorFundsFreezeBillAttach finVendorFundsFreezeBillAttach);

    /**
     * 添加多个
     *
     * @param list List FinVendorFundsFreezeBillAttach
     * @return int
     */
    int inserts(@Param("list") List<FinVendorFundsFreezeBillAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinVendorFundsFreezeBillAttach
     * @return int
     */
    int updateAllById(FinVendorFundsFreezeBillAttach entity);

    /**
     * 更新多个
     *
     * @param list List FinVendorFundsFreezeBillAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FinVendorFundsFreezeBillAttach> list);


}
