package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinPayBillItemHuokuan;

/**
 * 付款单-核销货款明细表Mapper接口
 */
public interface FinPayBillItemHuokuanMapper extends BaseMapper<FinPayBillItemHuokuan> {

    /**
     * 查询详情
     *
     * @param payBillItemHuokuanSid 单据sid
     * @return FinPayBillItemHuokuan
     */
    FinPayBillItemHuokuan selectFinPayBillItemHuokuanById(Long payBillItemHuokuanSid);

    /**
     * 查询列表
     *
     * @param finPayBillItemHuokuan FinPayBillItemHuokuan
     * @return List
     */
    List<FinPayBillItemHuokuan> selectFinPayBillItemHuokuanList(FinPayBillItemHuokuan finPayBillItemHuokuan);

    /**
     * 添加多个
     *
     * @param list List FinPayBillItemHuokuan
     * @return int
     */
    int inserts(@Param("list") List<FinPayBillItemHuokuan> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinPayBillItemHuokuan
     * @return int
     */
    int updateAllById(FinPayBillItemHuokuan entity);

    /**
     * 更新多个
     *
     * @param list List FinPayBillItemHuokuan
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPayBillItemHuokuan> list);

}
