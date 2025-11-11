package com.platform.ems.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinPayBill;
import org.apache.ibatis.annotations.Select;

/**
 * 付款单Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-21
 */
public interface FinPayBillMapper extends BaseMapper<FinPayBill> {

    /**
     * 查询详情
     *
     * @param payBillSid 单据sid
     * @return FinPayBill
     */
    FinPayBill selectFinPayBillById(Long payBillSid);

    /**
     * 查询列表
     *
     * @param finPayBill FinPayBill
     * @return List
     */
    List<FinPayBill> selectFinPayBillList(FinPayBill finPayBill);

    /**
     * 添加多个
     *
     * @param list List FinPayBill
     * @return int
     */
    int inserts(@Param("list") List<FinPayBill> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinPayBill
     * @return int
     */
    int updateAllById(FinPayBill entity);

    /**
     * 找到”是否到票提醒“为”是“的付款单
     */
    @Select(" SELECT t.*, u5.user_id as agent_id " +
            " FROM s_fin_pay_bill t " +
            " LEFT JOIN sys_user u5 ON t.agent = u5.user_name" +
            " where t.is_youpiao = 'Y' AND NOT EXISTS (SELECT 1 FROM s_fin_pay_bill_item_invoice inv WHERE t.pay_bill_sid = inv.pay_bill_sid)" +
            " and u5.user_id is not null")
    List<FinPayBill> selectIsRemainDaopiao();
}
