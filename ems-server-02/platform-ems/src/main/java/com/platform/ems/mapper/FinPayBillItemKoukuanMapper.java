package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinPayBillItemKoukuan;

/**
 * 付款单-核销扣款明细表Mapper接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface FinPayBillItemKoukuanMapper extends BaseMapper<FinPayBillItemKoukuan> {

    /**
     * 查询详情
     *
     * @param payBillItemKoukuanSid 单据sid
     * @return FinPayBillItemKoukuan
     */
    FinPayBillItemKoukuan selectFinPayBillItemKoukuanById(Long payBillItemKoukuanSid);

    /**
     * 查询列表
     *
     * @param finPayBillItemKoukuan FinPayBillItemKoukuan
     * @return List
     */
    List<FinPayBillItemKoukuan> selectFinPayBillItemKoukuanList(FinPayBillItemKoukuan finPayBillItemKoukuan);

    /**
     * 添加多个
     *
     * @param list List FinPayBillItemKoukuan
     * @return int
     */
    int inserts(@Param("list") List<FinPayBillItemKoukuan> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinPayBillItemKoukuan
     * @return int
     */
    int updateAllById(FinPayBillItemKoukuan entity);

    /**
     * 更新多个
     *
     * @param list List FinPayBillItemKoukuan
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPayBillItemKoukuan> list);

}
