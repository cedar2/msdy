package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinReceivableBill;
import org.apache.ibatis.annotations.Select;

/**
 * 收款单Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-22
 */
public interface FinReceivableBillMapper extends BaseMapper<FinReceivableBill> {

    /**
     * 查询详情
     *
     * @param receivableBillSid 单据sid
     * @return FinReceivableBill
     */
    FinReceivableBill selectFinReceivableBillById(Long receivableBillSid);

    /**
     * 查询列表
     *
     * @param finReceivableBill FinReceivableBill
     * @return List
     */
    List<FinReceivableBill> selectFinReceivableBillList(FinReceivableBill finReceivableBill);

    /**
     * 添加多个
     *
     * @param list List FinReceivableBill
     * @return int
     */
    int inserts(@Param("list") List<FinReceivableBill> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinReceivableBill
     * @return int
     */
    int updateAllById(FinReceivableBill entity);

    /**
     * 找到”是否到票提醒“为”是“的收款单
     */
    @Select(" SELECT t.*, u5.user_id as agent_id " +
            " FROM s_fin_receivable_bill t " +
            " LEFT JOIN sys_user u5 ON t.agent = u5.user_name" +
            " where t.is_youpiao = 'Y' AND NOT EXISTS (SELECT 1 FROM s_fin_receivable_bill_item_invoice inv WHERE t.receivable_bill_sid = inv.receivable_bill_sid)" +
            " and u5.user_id is not null")
    List<FinReceivableBill> selectIsRemainDaopiao();
}
