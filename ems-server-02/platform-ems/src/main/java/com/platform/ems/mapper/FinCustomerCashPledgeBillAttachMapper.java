package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinCustomerCashPledgeBillAttach;

/**
 * 客户押金-附件Mapper接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface FinCustomerCashPledgeBillAttachMapper extends BaseMapper<FinCustomerCashPledgeBillAttach> {


    FinCustomerCashPledgeBillAttach selectFinCustomerCashPledgeBillAttachById(Long cashPledgeBillAttachmentSid);

    List<FinCustomerCashPledgeBillAttach> selectFinCustomerCashPledgeBillAttachList(FinCustomerCashPledgeBillAttach finCustomerCashPledgeBillAttach);

    /**
     * 添加多个
     *
     * @param list List FinCustomerCashPledgeBillAttach
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerCashPledgeBillAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinCustomerCashPledgeBillAttach
     * @return int
     */
    int updateAllById(FinCustomerCashPledgeBillAttach entity);

    /**
     * 更新多个
     *
     * @param list List FinCustomerCashPledgeBillAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FinCustomerCashPledgeBillAttach> list);


}
