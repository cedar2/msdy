package com.platform.ems.mapper;
import java.util.List;

import com.platform.ems.domain.FinSaleInvoice;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinBookAccountReceivable;

/**
 * 财务流水账-应收Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-11
 */
public interface FinBookAccountReceivableMapper  extends BaseMapper<FinBookAccountReceivable> {


    FinBookAccountReceivable selectFinBookAccountReceivableById(Long bookAccountReceivableSid);

    List<FinBookAccountReceivable> selectFinBookAccountReceivableList(FinBookAccountReceivable finBookAccountReceivable);

    /**
     * 添加多个
     * @param list List FinBookAccountReceivable
     * @return int
     */
    int inserts(@Param("list") List<FinBookAccountReceivable> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookAccountReceivable
    * @return int
    */
    int updateAllById(FinBookAccountReceivable entity);

    /**
     * 更新多个
     * @param list List FinBookAccountReceivable
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookAccountReceivable> list);

    List<FinBookAccountReceivable> getReportForm(FinBookAccountReceivable entity);

    List<FinBookAccountReceivable> getRoutineItem(FinSaleInvoice finSaleInvoice);

}
