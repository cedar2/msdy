package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinPayBillItem;

/**
 * 付款单-明细Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-21
 */
public interface FinPayBillItemMapper extends BaseMapper<FinPayBillItem> {

    /**
     * 查询详情
     *
     * @param payBillItemSid 单据sid
     * @return FinPayBillItem
     */
    FinPayBillItem selectFinPayBillItemById(Long payBillItemSid);

    /**
     * 查询列表
     *
     * @param finPayBillItem FinPayBillItem
     * @return List
     */
    List<FinPayBillItem> selectFinPayBillItemList(FinPayBillItem finPayBillItem);

    /**
     * 添加多个
     *
     * @param list List FinPayBillItem
     * @return int
     */
    int inserts(@Param("list") List<FinPayBillItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinPayBillItem
     * @return int
     */
    int updateAllById(FinPayBillItem entity);

    /**
     * 更新多个
     *
     * @param list List FinPayBillItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPayBillItem> list);
}
