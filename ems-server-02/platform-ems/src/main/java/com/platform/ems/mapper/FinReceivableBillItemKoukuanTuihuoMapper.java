package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinReceivableBillItemKoukuanTuihuo;

/**
 * 收款单-核销退货扣款明细表Mapper接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface FinReceivableBillItemKoukuanTuihuoMapper extends BaseMapper<FinReceivableBillItemKoukuanTuihuo> {

    /**
     * 查询详情
     *
     * @param receivableBillItemKoukuanTuihuoSid 单据sid
     * @return FinReceivableBillItemKoukuanTuihuo
     */
    FinReceivableBillItemKoukuanTuihuo selectFinReceivableBillItemKoukuanTuihuoById(Long receivableBillItemKoukuanTuihuoSid);

    /**
     * 查询列表
     *
     * @param finReceivableBillItemKoukuanTuihuo FinReceivableBillItemKoukuanTuihuo
     * @return List
     */
    List<FinReceivableBillItemKoukuanTuihuo> selectFinReceivableBillItemKoukuanTuihuoList(FinReceivableBillItemKoukuanTuihuo finReceivableBillItemKoukuanTuihuo);

    /**
     * 添加多个
     *
     * @param list List FinReceivableBillItemKoukuanTuihuo
     * @return int
     */
    int inserts(@Param("list") List<FinReceivableBillItemKoukuanTuihuo> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinReceivableBillItemKoukuanTuihuo
     * @return int
     */
    int updateAllById(FinReceivableBillItemKoukuanTuihuo entity);

    /**
     * 更新多个
     *
     * @param list List FinReceivableBillItemKoukuanTuihuo
     * @return int
     */
    int updatesAllById(@Param("list") List<FinReceivableBillItemKoukuanTuihuo> list);

}
