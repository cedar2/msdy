package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.SalCustomerMonthAccountBillZangu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供应商对账单-暂估明细Mapper接口
 *
 * @author xfzz
 */
public interface SalCustomerMonthAccountBillZanguMapper extends BaseMapper<SalCustomerMonthAccountBillZangu> {


    SalCustomerMonthAccountBillZangu selectSalCustomerMonthAccountBillZanguById(Long customerMonthAccountBillSid);

    List<SalCustomerMonthAccountBillZangu> selectSalCustomerMonthAccountBillZanguList(SalCustomerMonthAccountBillZangu salCustomerMonthAccountBillZangu);

    /**
     * 添加多个
     *
     * @param list List SalCustomerMonthAccountBillZangu
     * @return int
     */
    int inserts(@Param("list") List<SalCustomerMonthAccountBillZangu> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SalCustomerMonthAccountBillZangu
     * @return int
     */
    int updateAllById(SalCustomerMonthAccountBillZangu entity);

    /**
     * 更新多个
     *
     * @param list List SalCustomerMonthAccountBillZangu
     * @return int
     */
    int updatesAllById(@Param("list") List<SalCustomerMonthAccountBillZangu> list);

    List<SalCustomerMonthAccountBillZangu> getReportForm(SalCustomerMonthAccountBillZangu entity);

}
