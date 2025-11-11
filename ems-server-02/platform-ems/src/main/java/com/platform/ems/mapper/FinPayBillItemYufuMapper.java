package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinPayBillItemYufu;

/**
 * 付款单-核销预付明细表Mapper接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface FinPayBillItemYufuMapper extends BaseMapper<FinPayBillItemYufu> {

    /**
     * 查询详情
     *
     * @param payBillItemYufuSid 单据sid
     * @return FinPayBillItemYufu
     */
    FinPayBillItemYufu selectFinPayBillItemYufuById(Long payBillItemYufuSid);

    /**
     * 查询列表
     *
     * @param finPayBillItemYufu FinPayBillItemYufu
     * @return List
     */
    List<FinPayBillItemYufu> selectFinPayBillItemYufuList(FinPayBillItemYufu finPayBillItemYufu);

    /**
     * 添加多个
     *
     * @param list List FinPayBillItemYufu
     * @return int
     */
    int inserts(@Param("list") List<FinPayBillItemYufu> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinPayBillItemYufu
     * @return int
     */
    int updateAllById(FinPayBillItemYufu entity);

    /**
     * 更新多个
     *
     * @param list List FinPayBillItemYufu
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPayBillItemYufu> list);

}
