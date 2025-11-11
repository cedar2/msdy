package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeDeliveryNote;

/**
 * 业务类型_采购交货单/销售发货单Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeDeliveryNoteMapper extends BaseMapper<ConBuTypeDeliveryNote> {


    ConBuTypeDeliveryNote selectConBuTypeDeliveryNoteById(Long sid);

    List<ConBuTypeDeliveryNote> selectConBuTypeDeliveryNoteList(ConBuTypeDeliveryNote conBuTypeDeliveryNote);

    /**
     * 添加多个
     *
     * @param list List ConBuTypeDeliveryNote
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeDeliveryNote> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConBuTypeDeliveryNote
     * @return int
     */
    int updateAllById(ConBuTypeDeliveryNote entity);

    /**
     * 更新多个
     *
     * @param list List ConBuTypeDeliveryNote
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeDeliveryNote> list);

    /**
     * 业务类型_采购交货单/销售发货单下拉列表
     */
    List<ConBuTypeDeliveryNote> getList(ConBuTypeDeliveryNote conBuTypeDeliveryNote);
}
