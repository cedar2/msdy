package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManOutsourceSettleExtraDeductionItem;

/**
 * 外发加工费结算单-额外扣款明细Mapper接口
 *
 * @author admin
 * @date 2023-08-10
 */
public interface ManOutsourceSettleExtraDeductionItemMapper extends BaseMapper<ManOutsourceSettleExtraDeductionItem> {

    ManOutsourceSettleExtraDeductionItem selectManOutsourceSettleExtraDeductionItemById(Long outsourceSettleExtraDeductionItemSid);

    List<ManOutsourceSettleExtraDeductionItem> selectManOutsourceSettleExtraDeductionItemList(ManOutsourceSettleExtraDeductionItem manOutsourceSettleExtraDeductionItem);

    /**
     * 添加多个
     *
     * @param list List ManOutsourceSettleExtraDeductionItem
     * @return int
     */
    int inserts(@Param("list") List<ManOutsourceSettleExtraDeductionItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManOutsourceSettleExtraDeductionItem
     * @return int
     */
    int updateAllById(ManOutsourceSettleExtraDeductionItem entity);

    /**
     * 更新多个
     *
     * @param list List ManOutsourceSettleExtraDeductionItem
     * @return int
     */
    int updatesAllById(@Param("list") List<ManOutsourceSettleExtraDeductionItem> list);

}
