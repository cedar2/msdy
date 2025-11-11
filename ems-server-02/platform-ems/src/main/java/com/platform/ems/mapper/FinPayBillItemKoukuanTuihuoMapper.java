package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinPayBillItemKoukuanTuihuo;

/**
 * 付款单-核销退货扣款明细表Mapper接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface FinPayBillItemKoukuanTuihuoMapper extends BaseMapper<FinPayBillItemKoukuanTuihuo> {

    /**
     * 查询详情
     *
     * @param payBillItemKoukuanTuihuoSid 单据sid
     * @return FinPayBillItemKoukuanTuihuo
     */
    FinPayBillItemKoukuanTuihuo selectFinPayBillItemKoukuanTuihuoById(Long payBillItemKoukuanTuihuoSid);

    /**
     * 查询列表
     *
     * @param finPayBillItemKoukuanTuihuo FinPayBillItemKoukuanTuihuo
     * @return List
     */
    List<FinPayBillItemKoukuanTuihuo> selectFinPayBillItemKoukuanTuihuoList(FinPayBillItemKoukuanTuihuo finPayBillItemKoukuanTuihuo);

    /**
     * 添加多个
     *
     * @param list List FinPayBillItemKoukuanTuihuo
     * @return int
     */
    int inserts(@Param("list") List<FinPayBillItemKoukuanTuihuo> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinPayBillItemKoukuanTuihuo
     * @return int
     */
    int updateAllById(FinPayBillItemKoukuanTuihuo entity);

    /**
     * 更新多个
     *
     * @param list List FinPayBillItemKoukuanTuihuo
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPayBillItemKoukuanTuihuo> list);

}
