package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinReceivableBillItemHuokuan;

/**
 * 收款单-核销货款明细表Mapper接口
 */
public interface FinReceivableBillItemHuokuanMapper extends BaseMapper<FinReceivableBillItemHuokuan> {

    /**
     * 查询详情
     *
     * @param receivableBillItemHuokuanSid 单据sid
     * @return FinReceivableBillItemHuokuan
     */
    FinReceivableBillItemHuokuan selectFinReceivableBillItemHuokuanById(Long receivableBillItemHuokuanSid);

    /**
     * 查询列表
     *
     * @param finReceivableBillItemHuokuan FinReceivableBillItemHuokuan
     * @return List
     */
    List<FinReceivableBillItemHuokuan> selectFinReceivableBillItemHuokuanList(FinReceivableBillItemHuokuan finReceivableBillItemHuokuan);

    /**
     * 添加多个
     *
     * @param list List FinReceivableBillItemHuokuan
     * @return int
     */
    int inserts(@Param("list") List<FinReceivableBillItemHuokuan> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinReceivableBillItemHuokuan
     * @return int
     */
    int updateAllById(FinReceivableBillItemHuokuan entity);

    /**
     * 更新多个
     *
     * @param list List FinReceivableBillItemHuokuan
     * @return int
     */
    int updatesAllById(@Param("list") List<FinReceivableBillItemHuokuan> list);

}
