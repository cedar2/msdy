package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SalSalesIntentOrderAttach;

/**
 * 销售意向单-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-10-17
 */
public interface SalSalesIntentOrderAttachMapper extends BaseMapper<SalSalesIntentOrderAttach> {

    SalSalesIntentOrderAttach selectSalSalesIntentOrderAttachById(Long salesIntentOrderAttachSid);

    List<SalSalesIntentOrderAttach> selectSalSalesIntentOrderAttachList(SalSalesIntentOrderAttach salSalesIntentOrderAttach);

    /**
     * 添加多个
     *
     * @param list List SalSalesIntentOrderAttach
     * @return int
     */
    int inserts(@Param("list") List<SalSalesIntentOrderAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SalSalesIntentOrderAttach
     * @return int
     */
    int updateAllById(SalSalesIntentOrderAttach entity);

    /**
     * 更新多个
     *
     * @param list List SalSalesIntentOrderAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<SalSalesIntentOrderAttach> list);


}
