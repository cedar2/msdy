package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinReceivableBillItem;

/**
 * 收款单-明细Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-22
 */
public interface FinReceivableBillItemMapper extends BaseMapper<FinReceivableBillItem> {

    /**
     * 查询详情
     *
     * @param receivableBillItemSid 单据sid
     * @return FinReceivableBillItem
     */
    FinReceivableBillItem selectFinReceivableBillItemById(Long receivableBillItemSid);

    /**
     * 查询列表
     *
     * @param finReceivableBillItem FinReceivableBillItem
     * @return List
     */
    List<FinReceivableBillItem> selectFinReceivableBillItemList(FinReceivableBillItem finReceivableBillItem);

    /**
     * 添加多个
     *
     * @param list List FinReceivableBillItem
     * @return int
     */
    int inserts(@Param("list") List<FinReceivableBillItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinReceivableBillItem
     * @return int
     */
    int updateAllById(FinReceivableBillItem entity);

    /**
     * 更新多个
     *
     * @param list List FinReceivableBillItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinReceivableBillItem> list);

}
