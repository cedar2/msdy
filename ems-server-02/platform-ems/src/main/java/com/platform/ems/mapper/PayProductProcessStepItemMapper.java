package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PayProcessStepCompleteItem;
import com.platform.ems.domain.PayProductProcessStep;
import com.platform.ems.domain.PayProductProcessStepItem;
import com.platform.ems.domain.dto.response.PayProcessStepCompleteTableStepResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品道序-明细Mapper接口
 *
 * @author c
 * @date 2021-09-08
 */
public interface PayProductProcessStepItemMapper extends BaseMapper<PayProductProcessStepItem> {


    PayProductProcessStepItem selectPayProductProcessStepItemById(Long stepItemSid);

    List<PayProductProcessStepItem> selectPayProductProcessStepItemList(PayProductProcessStepItem payProductProcessStepItem);

    /**
     * 查询商品道序-明细报表
     *
     * @param payProductProcessStepItem 商品道序-明细
     * @return 商品道序-明细
     */
    @SqlParser(filter=true)
    List<PayProductProcessStepItem> selectPayProductProcessStepItemForm(PayProductProcessStepItem payProductProcessStepItem);

    /**
     * 查询商品道序-明细   (主要用于计薪量明细查询的接口)
     *
     * @param payProductProcessStepItem 商品道序-明细
     * @return 商品道序-明细
     */
    @SqlParser(filter=true)
    List<PayProductProcessStepItem> selectPayProductProcessStepItem(PayProductProcessStepItem payProductProcessStepItem);

    /**
     * 通过查询条件查商品道序明细
     *
     * @param payProductProcessStepItem PayProductProcessStepItem
     * @return PayProductProcessStepItem
     */
    PayProductProcessStepItem selectPayProductProcessStepItemBy(PayProductProcessStepItem payProductProcessStepItem);

    //共价模板 获取成本价
    List<PayProductProcessStepItem> getCostPrice(PayProductProcessStep payProductProcessStep);

    /**
     * 添加多个
     *
     * @param list List PayProductProcessStepItem
     * @return int
     */
    int inserts(@Param("list") List<PayProductProcessStepItem> list);

    int deleteByProcessStepSid(@Param("productProcessStepSid") Long productProcessStepSid);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PayProductProcessStepItem
     * @return int
     */
    int updateAllById(PayProductProcessStepItem entity);

    /**
     * 更新多个
     *
     * @param list List PayProductProcessStepItem
     * @return int
     */
    int updatesAllById(@Param("list") List<PayProductProcessStepItem> list);

    /**
     * 计薪量申报查询明细， 新
     * @param payProductProcessStepItem 主要用来分页
     */
    @SqlParser(filter=true)
    List<PayProductProcessStepItem> getManOrderItemList(PayProductProcessStepItem payProductProcessStepItem);

    /**
     * 计薪量申报查询明细获取价格和倍率
     * @param payProductProcessStepItem 主要用来分页
     */
    List<PayProductProcessStepItem> getPrice(PayProductProcessStepItem payProductProcessStepItem);

    /**
     * 计薪量申报明细按款录入 弹出窗 列数据 道序序号
     * @param payProductProcessStepItem
     */
    List<PayProcessStepCompleteItem> selectPayProductProcessStepItemSort(PayProductProcessStepItem payProductProcessStepItem);
}
