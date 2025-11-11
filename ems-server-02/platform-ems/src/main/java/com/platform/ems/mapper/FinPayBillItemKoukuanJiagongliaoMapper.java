package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinPayBillItemKoukuanJiagongliao;

/**
 * 付款单-核销甲供料扣款明细表Mapper接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface FinPayBillItemKoukuanJiagongliaoMapper extends BaseMapper<FinPayBillItemKoukuanJiagongliao> {

    /**
     * 查询详情
     *
     * @param payBillItemKoukuanJiagongliaoSid 单据sid
     * @return FinPayBillItemKoukuanJiagongliao
     */
    FinPayBillItemKoukuanJiagongliao selectFinPayBillItemKoukuanJiagongliaoById(Long payBillItemKoukuanJiagongliaoSid);

    /**
     * 查询列表
     *
     * @param finPayBillItemKoukuanJiagongliao FinPayBillItemKoukuanJiagongliao
     * @return List
     */
    List<FinPayBillItemKoukuanJiagongliao> selectFinPayBillItemKoukuanJiagongliaoList(FinPayBillItemKoukuanJiagongliao finPayBillItemKoukuanJiagongliao);

    /**
     * 添加多个
     *
     * @param list List FinPayBillItemKoukuanJiagongliao
     * @return int
     */
    int inserts(@Param("list") List<FinPayBillItemKoukuanJiagongliao> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinPayBillItemKoukuanJiagongliao
     * @return int
     */
    int updateAllById(FinPayBillItemKoukuanJiagongliao entity);

    /**
     * 更新多个
     *
     * @param list List FinPayBillItemKoukuanJiagongliao
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPayBillItemKoukuanJiagongliao> list);

}
