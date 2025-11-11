package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PaySalaryBillItem;
import com.platform.ems.domain.dto.request.PaySalaryBillItemRequest;
import com.platform.ems.domain.dto.response.PaySalaryBillItemExResponse;
import com.platform.ems.domain.dto.response.form.PaySalaryWageFormResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工资单-明细Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-14
 */
public interface PaySalaryBillItemMapper extends BaseMapper<PaySalaryBillItem> {


    PaySalaryBillItem selectPaySalaryBillItemById(Long billItemSid);

    List<PaySalaryBillItem> selectPaySalaryBillItemList(PaySalaryBillItem paySalaryBillItem);

   List<PaySalaryBillItemExResponse> getReport(PaySalaryBillItemRequest paySalaryBillItemRequest);
    /**
     * 添加多个
     *
     * @param list List PaySalaryBillItem
     * @return int
     */
    int inserts(@Param("list") List<PaySalaryBillItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PaySalaryBillItem
     * @return int
     */
    int updateAllById(PaySalaryBillItem entity);

    /**
     * 更新多个
     *
     * @param list List PaySalaryBillItem
     * @return int
     */
    int updatesAllById(@Param("list") List<PaySalaryBillItem> list);

    /**
     * 获得员工的计件工资和返修费
     *
     * @param paySalaryBillItem PaySalaryBillItem
     * @return int
     */
    List<PaySalaryBillItem> getProcessStepCompleteWage(PaySalaryBillItem paySalaryBillItem);

}
