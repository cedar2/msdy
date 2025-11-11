package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinReceivableBillItemKoukuan;

/**
 * 收款单-核销扣款明细表Mapper接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface FinReceivableBillItemKoukuanMapper extends BaseMapper<FinReceivableBillItemKoukuan> {

    /**
     * 查询详情
     *
     * @param receivableBillItemKoukuanSid 单据sid
     * @return FinReceivableBillItemKoukuan
     */
    FinReceivableBillItemKoukuan selectFinReceivableBillItemKoukuanById(Long receivableBillItemKoukuanSid);

    /**
     * 查询列表
     *
     * @param finReceivableBillItemKoukuan FinReceivableBillItemKoukuan
     * @return List
     */
    List<FinReceivableBillItemKoukuan> selectFinReceivableBillItemKoukuanList(FinReceivableBillItemKoukuan finReceivableBillItemKoukuan);

    /**
     * 添加多个
     *
     * @param list List FinReceivableBillItemKoukuan
     * @return int
     */
    int inserts(@Param("list") List<FinReceivableBillItemKoukuan> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinReceivableBillItemKoukuan
     * @return int
     */
    int updateAllById(FinReceivableBillItemKoukuan entity);

    /**
     * 更新多个
     *
     * @param list List FinReceivableBillItemKoukuan
     * @return int
     */
    int updatesAllById(@Param("list") List<FinReceivableBillItemKoukuan> list);

}
