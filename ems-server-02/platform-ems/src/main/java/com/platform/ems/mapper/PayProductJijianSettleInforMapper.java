package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PayProductJijianSettleInfor;
import org.apache.ibatis.annotations.Select;

/**
 * 商品计件结算信息Mapper接口
 *
 * @author chenkw
 * @date 2022-07-14
 */
public interface PayProductJijianSettleInforMapper extends BaseMapper<PayProductJijianSettleInfor> {

    PayProductJijianSettleInfor selectPayProductJijianSettleInforById(Long jijianSettleInforSid);

    @SqlParser(filter=true)
    List<PayProductJijianSettleInfor> selectPayProductJijianSettleInforList(PayProductJijianSettleInfor payProductJijianSettleInfor);

    /**
     * 精确查询
     *
     * @param
     * @return int
     */
    @SqlParser(filter=true)
    List<PayProductJijianSettleInfor> selectPayProductJijianSettleInforListPrecision(PayProductJijianSettleInfor payProductJijianSettleInfor);

    /**
     * 添加多个
     *
     * @param list List PayProductJijianSettleInfor
     * @return int
     */
    int inserts(@Param("list" ) List<PayProductJijianSettleInfor> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PayProductJijianSettleInfor
     * @return int
     */
    int updateAllById(PayProductJijianSettleInfor entity);

    /**
     * 更新多个
     *
     * @param list List PayProductJijianSettleInfor
     * @return int
     */
    int updatesAllById(@Param("list" ) List<PayProductJijianSettleInfor> list);

    /**
     * 根据所选择计薪量申报单的“工厂、商品工价类型、计薪完工类型、操作部门、款号、排产批次号”，
     * 获取“商品计件结算信息表”中的“已确认”状态的结算数，并对获取的结算数进行小计；
     */
    Double countSettleQuantity(PayProductJijianSettleInfor productJijianSettleInfor);

    /**
     点击此按钮，从“商品计件结算数”数据库表中获取满足查询条件的数据，并按以下逻辑对“结算数”进行小计：
     》结算数累计(已确认)
     将查询出来的数据，按“工厂、商品编码(款号)、排产批次号、操作部门、商品工价类型、计薪完工类型”且“处理状态是已确认”的结算数进行小计

     》结算数累计(含保存)
     将查询出来的数据，按“工厂、商品编码(款号)、排产批次号、操作部门、商品工价类型、计薪完工类型”的结算数进行小计，即“处理状态”是“保存”和“已确认”都进行小计。
     */
    @SqlParser(filter=true)
    List<PayProductJijianSettleInfor> collectPayProductJijianSettleInforList(PayProductJijianSettleInfor payProductJijianSettleInfor);
}
