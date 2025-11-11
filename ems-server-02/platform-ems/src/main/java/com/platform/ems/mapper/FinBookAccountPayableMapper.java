package com.platform.ems.mapper;
import java.util.List;

import com.platform.ems.domain.FinPurchaseInvoice;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinBookAccountPayable;

/**
 * 财务流水账-应付Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-03
 */
public interface FinBookAccountPayableMapper  extends BaseMapper<FinBookAccountPayable> {


    FinBookAccountPayable selectFinBookAccountPayableById(Long bookAccountPayableSid);

    List<FinBookAccountPayable> selectFinBookAccountPayableList(FinBookAccountPayable finBookAccountPayable);

    /**
     * 添加多个
     * @param list List FinBookAccountPayable
     * @return int
     */
    int inserts(@Param("list") List<FinBookAccountPayable> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookAccountPayable
    * @return int
    */
    int updateAllById(FinBookAccountPayable entity);

    /**
     * 更新多个
     * @param list List FinBookAccountPayable
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookAccountPayable> list);

    List<FinBookAccountPayable> getReportForm(FinBookAccountPayable entity);

    List<FinBookAccountPayable> getRoutineItem(FinPurchaseInvoice finPurchaseInvoice);
}
