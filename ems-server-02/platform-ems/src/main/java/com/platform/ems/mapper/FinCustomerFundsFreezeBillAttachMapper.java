package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinCustomerFundsFreezeBillAttach;

/**
 * 客户暂押款-附件Mapper接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface FinCustomerFundsFreezeBillAttachMapper extends BaseMapper<FinCustomerFundsFreezeBillAttach> {


    FinCustomerFundsFreezeBillAttach selectFinCustomerFundsFreezeBillAttachById(Long fundsFreezeBillAttachmentSid);

    List<FinCustomerFundsFreezeBillAttach> selectFinCustomerFundsFreezeBillAttachList(FinCustomerFundsFreezeBillAttach finCustomerFundsFreezeBillAttach);

    /**
     * 添加多个
     *
     * @param list List FinCustomerFundsFreezeBillAttach
     * @return int
     */
    int inserts(@Param("list") List<FinCustomerFundsFreezeBillAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinCustomerFundsFreezeBillAttach
     * @return int
     */
    int updateAllById(FinCustomerFundsFreezeBillAttach entity);

    /**
     * 更新多个
     *
     * @param list List FinCustomerFundsFreezeBillAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FinCustomerFundsFreezeBillAttach> list);


}
