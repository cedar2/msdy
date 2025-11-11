package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinReceivableBillItemKoukuanKegongliao;

/**
 * 收款单-核销客供料扣款明细表Mapper接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface FinReceivableBillItemKoukuanKegongliaoMapper extends BaseMapper<FinReceivableBillItemKoukuanKegongliao> {

    /**
     * 查询详情
     *
     * @param receivableBillItemKoukuanKegongliaoSid 单据sid
     * @return FinReceivableBillItemKoukuanKegongliao
     */
    FinReceivableBillItemKoukuanKegongliao selectFinReceivableBillItemKoukuanKegongliaoById(Long receivableBillItemKoukuanKegongliaoSid);

    /**
     * 查询列表
     *
     * @param finReceivableBillItemKoukuanKegongliao FinReceivableBillItemKoukuanKegongliao
     * @return List
     */
    List<FinReceivableBillItemKoukuanKegongliao> selectFinReceivableBillItemKoukuanKegongliaoList(FinReceivableBillItemKoukuanKegongliao finReceivableBillItemKoukuanKegongliao);

    /**
     * 添加多个
     *
     * @param list List FinReceivableBillItemKoukuanKegongliao
     * @return int
     */
    int inserts(@Param("list") List<FinReceivableBillItemKoukuanKegongliao> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinReceivableBillItemKoukuanKegongliao
     * @return int
     */
    int updateAllById(FinReceivableBillItemKoukuanKegongliao entity);

    /**
     * 更新多个
     *
     * @param list List FinReceivableBillItemKoukuanKegongliao
     * @return int
     */
    int updatesAllById(@Param("list") List<FinReceivableBillItemKoukuanKegongliao> list);

}
